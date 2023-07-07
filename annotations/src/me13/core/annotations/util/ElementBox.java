package me13.core.annotations.util;

import arc.struct.Seq;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import me13.core.annotations.BaseProcessor;
import org.jetbrains.annotations.Nullable;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import java.lang.annotation.Annotation;

public class ElementBox<E extends Element> {
    public final E e;

    public ElementBox(E e) {
        this.e = e;
    }

    @Nullable
    public String doc() {
        return BaseProcessor.elements.getDocComment(e);
    }

    public Seq<ElementBox<?>> enclosed() {
        return Seq.with(e.getEnclosedElements()).map(ElementBox::new);
    }

    public String fullName(){
        return e.toString();
    }

    public TypeBox asType() {
        return TypeBox.of(e);
    }

    public VarBox asVar() {
        return VarBox.of(e);
    }

    public MethodBox asMethod() {
        return MethodBox.of(e);
    }

    public boolean isVar() {
        return e instanceof VariableElement;
    }

    public boolean isType() {
        return e instanceof TypeElement;
    }

    public boolean isMethod() {
        return e instanceof ExecutableElement;
    }

    public Seq<? extends AnnotationMirror> annotations() {
        return Seq.with(e.getAnnotationMirrors());
    }

    public <A extends Annotation> A annotation(Class<A> annotation){
        return e.getAnnotation(annotation);
    }

    public <A extends Annotation> boolean has(Class<A> annotation) {
        return annotation(annotation) != null;
    }

    public Element up() {
        return e.getEnclosingElement();
    }

    public TypeMirror mirror() {
        return e.asType();
    }

    public TypeName tname() {
        return TypeName.get(mirror());
    }

    public ClassName cname() {
        return ClassName.get((TypeElement) BaseProcessor.types.asElement(mirror()));
    }

    public String name() {
        return e.getSimpleName().toString();
    }

    @Override
    public String toString() {
        return e.toString();
    }

    @Override
    public int hashCode() {
        return e.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return o != null && o.getClass() == getClass() && e.equals(((ElementBox<?>)o).e);
    }
}