package me13.core.annotations.util;

import arc.struct.Seq;
import me13.core.annotations.BaseProcessor;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

public class TypeBox extends ElementBox<TypeElement> {
    public static TypeBox of(TypeMirror mirror) {
        return new TypeBox((TypeElement) BaseProcessor.types.asElement(mirror));
    }

    public static TypeBox of(Element element) {
        return new TypeBox((TypeElement) element);
    }

    public TypeBox(TypeElement typeElement) {
        super(typeElement);
    }

    public String fullName() {
        return mirror().toString();
    }

    public Seq<TypeBox> interfaces() {
        return Seq.with(e.getInterfaces()).map(TypeBox::of);
    }

    public Seq<TypeBox> allInterfaces() {
        return interfaces().flatMap(s -> s.allInterfaces().add(s)).distinct();
    }

    public Seq<TypeBox> superclasses() {
        return Seq.with(BaseProcessor.types.directSupertypes(mirror())).map(TypeBox::of);
    }

    public Seq<TypeBox> allSuperclasses() {
        return superclasses().flatMap(s -> s.allSuperclasses().add(s)).distinct();
    }

    public TypeBox superclass(){
        return new TypeBox((TypeElement)BaseProcessor.types.asElement(BaseProcessor.
                types.directSupertypes(mirror()).get(0)));
    }

    public Seq<VarBox> fields() {
        return Seq.with(e.getEnclosedElements()).select(e -> e instanceof VariableElement).map(VarBox::of);
    }

    public Seq<MethodBox> methods(){
        return Seq.with(e.getEnclosedElements()).select(e -> e instanceof ExecutableElement
                && !e.getSimpleName().toString().contains("<")).map(MethodBox::of);
    }

    public Seq<MethodBox> constructors(){
        return Seq.with(e.getEnclosedElements()).select(e -> e instanceof ExecutableElement
                && e.getSimpleName().toString().contains("<")).map(MethodBox::of);
    }

    @Override
    public TypeMirror mirror() {
        return e.asType();
    }
}
