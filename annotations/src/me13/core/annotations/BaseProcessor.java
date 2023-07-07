package me13.core.annotations;

import arc.files.Fi;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.OS;
import arc.util.Structs;
import com.squareup.javapoet.*;
import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import me13.core.annotations.util.*;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
public abstract class BaseProcessor extends AbstractProcessor {
    protected Set<? extends TypeElement> annotations;
    protected RoundEnvironment env;
    protected Fi rootDirectory;
    protected int rounds = 1;
    protected int round;
    protected int childs = 2;

    public abstract void process();

    public static Types types;
    public static Elements elements;
    public static Filer filer;
    public static Messager messager;

    public Seq<ElementBox<?>> elements(Class<? extends Annotation> type){
        return Seq.with(env.getElementsAnnotatedWith(type)).map(ElementBox::new);
    }

    public Seq<TypeBox> types(Class<? extends Annotation> type) {
        return Seq.with(env.getElementsAnnotatedWith(type))
                .select(e -> e instanceof TypeElement)
                .map(TypeBox::of);
    }

    public Seq<VarBox> fields(Class<? extends Annotation> type) {
        return Seq.with(env.getElementsAnnotatedWith(type))
                .select(e -> e instanceof VariableElement)
                .map(VarBox::of);
    }

    public Seq<MethodBox> methods(Class<? extends Annotation> type){
        return Seq.with(env.getElementsAnnotatedWith(type))
                .select(e -> e instanceof ExecutableElement)
                .map(MethodBox::of);
    }

    @Override
    public synchronized void init(ProcessingEnvironment env){
        super.init(env);

        types = env.getTypeUtils();
        elements = env.getElementUtils();
        filer = env.getFiler();
        messager = env.getMessager();

        Log.level = Log.LogLevel.info;
        if(System.getProperty("debug") != null){
            Log.level = Log.LogLevel.debug;
        }
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if(round++ >= rounds) return false; //only process 1 round
        if(rootDirectory == null){
            try{
                Fi path = Fi.get(filer.getResource(StandardLocation.CLASS_OUTPUT, "no", "no")
                                .toUri().toURL().toString().substring(OS.isWindows ? 6 : "file:".length()));
                for(int i = 0; i < childs; i++) {
                    path = path.parent();
                }
                rootDirectory = Fi.get(path.toString().replace("%20", " ")).parent();
            }catch(IOException e){
                throw new RuntimeException(e);
            }
        }

        this.env = roundEnv;
        this.annotations = annotations;
        try{
            process();
        }catch(Throwable e){
            Log.err(e);
            throw new RuntimeException(e);
        }
        return true;
    }

    public static String getMethodName(Element element){
        return ((TypeElement)element.getEnclosingElement()).getQualifiedName()
                .toString() + "." + element.getSimpleName();
    }

    public static boolean isPrimitive(String type){
        return type.equals("boolean") || type.equals("byte") || type.equals("short") || type.equals("int")
                || type.equals("long") || type.equals("float") || type.equals("double") || type.equals("char");
    }

    public static boolean instanceOf(String type, String other){
        TypeElement a = elements.getTypeElement(type);
        TypeElement b = elements.getTypeElement(other);
        return a != null && b != null && types.isSubtype(a.asType(), b.asType());
    }

    public static String getDefault(String value){
        switch(value){
            case "float":
            case "double":
            case "int":
            case "long":
            case "short":
            case "char":
            case "byte":
                return "0";
            case "boolean":
                return "false";
            default:
                return "null";
        }
    }

    //in bytes
    public static int typeSize(String kind){
        switch(kind){
            case "boolean":
            case "byte":
                return 1;
            case "short":
                return 2;
            case "float":
            case "char":
            case "int":
                return 4;
            case "long":
                return 8;
            default:
                throw new IllegalArgumentException("Invalid primitive type: " + kind + "");
        }
    }

    public static String simpleName(String str){
        return str.contains(".") ? str.substring(str.lastIndexOf('.') + 1) : str;
    }

    public static TypeName tnameSimple(String pack, String simple){
        return ClassName.get(pack, simple);
    }

    public static TypeName tname(String packageName, String name){
        if(!name.contains(".")) return ClassName.get(packageName, name);

        String pack = name.substring(0, name.lastIndexOf("."));
        String simple = name.substring(name.lastIndexOf(".") + 1);
        return ClassName.get(pack, simple);
    }

    public static TypeName tname(Class<?> c){
        return ClassName.get(c).box();
    }

    public static TypeVariableName getTVN(TypeParameterElement element){
        String name = element.getSimpleName().toString();
        List<? extends TypeMirror> boundsMirrors = element.getBounds();

        List<TypeName> boundsTypeNames = new ArrayList<>();
        for(TypeMirror typeMirror : boundsMirrors){
            boundsTypeNames.add(TypeName.get(typeMirror));
        }

        return TypeVariableName.get(name, boundsTypeNames.toArray(new TypeName[0]));
    }

    public static void write(TypeSpec.Builder builder, String packageName) throws IOException {
        write(builder, null, packageName);
    }

    public static void write(TypeSpec.Builder builder, Seq<String> imports, String packageName) throws IOException {
        builder.superinterfaces.sort(Structs.comparing(TypeName::toString));
        builder.methodSpecs.sort(Structs.comparing(MethodSpec::toString));
        builder.fieldSpecs.sort(Structs.comparing(f -> f.name));

        JavaFile file = JavaFile.builder(packageName, builder.build()).skipJavaLangImports(true).build();
        String writeString;

        if(imports != null){
            imports = imports.map(m -> Seq.with(m.split("\n")).sort().toString("\n"));
            imports.sort();
            String rawSource = file.toString();
            Seq<String> result = new Seq<>();
            for(String s : rawSource.split("\n", -1)){
                result.add(s);
                if(s.startsWith("package ")){
                    result.add("");
                    for (String i : imports){
                        result.add(i);
                    }
                }
            }

            writeString = result.toString("\n");
        }else{
            writeString = file.toString();
        }

        JavaFileObject object = filer.createSourceFile(file.packageName + "." + file.typeSpec.name, file.typeSpec.originatingElements.toArray(new Element[0]));
        Writer stream = object.openWriter();
        stream.write(writeString);
        stream.close();
    }

    public static void err(String message){
        messager.printMessage(Diagnostic.Kind.ERROR, message);
        Log.err("[CODEGEN ERROR] " +message);
    }

    public static void err(String message, Element elem){
        messager.printMessage(Diagnostic.Kind.ERROR, message, elem);
        Log.err("[CODEGEN ERROR] " + message + ": " + elem);
    }

    public static void err(String message, ElementBox<?> elem) {
        err(message, elem.e);
    }

    @Override
    public SourceVersion getSupportedSourceVersion(){
        return SourceVersion.RELEASE_8;
    }
}