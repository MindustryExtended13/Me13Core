package me13.core.annotations.util;

import arc.struct.Seq;
import com.squareup.javapoet.TypeName;
import me13.core.annotations.BaseProcessor;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.TypeMirror;

public class MethodBox extends ElementBox<ExecutableElement> {
    public static MethodBox of(Element element) {
        return new MethodBox((ExecutableElement) element);
    }

    public MethodBox(ExecutableElement executableElement) {
        super(executableElement);
    }

    public boolean isAny(Modifier... mod){
        for(Modifier m : mod){
            if(is(m)) return true;
        }
        return false;
    }

    public String descString(String packageName) {
        return up().asType().toString() + "#" + super.toString().replace(packageName + ".", "");
    }

    public boolean is(Modifier mod) {
        return e.getModifiers().contains(mod);
    }

    public TypeBox type() {
        return TypeBox.of(up());
    }

    public Seq<TypeMirror> thrown(){
        return Seq.with(e.getThrownTypes()).as();
    }

    public Seq<TypeName> thrownt() {
        return Seq.with(e.getThrownTypes()).map(TypeName::get);
    }

    public Seq<TypeParameterElement> typeVariables() {
        return Seq.with(e.getTypeParameters()).as();
    }

    public Seq<VarBox> params(){
        return Seq.with(e.getParameters()).map(VarBox::new);
    }

    public boolean isVoid() {
        return ret().toString().equals("void");
    }

    public TypeMirror ret(){
        return e.getReturnType();
    }

    public TypeName retn() {
        return TypeName.get(ret());
    }

    public String simpleString(){
        return name() + "(" + params().toString(", ", p -> BaseProcessor.simpleName(p.mirror().toString())) + ")";
    }
}