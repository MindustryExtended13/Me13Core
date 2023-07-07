package me13.core.annotations.util;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;

public class VarBox extends ElementBox<VariableElement> {
    public static VarBox of(Element element) {
        return new VarBox((VariableElement) element);
    }

    public VarBox(VariableElement variableElement) {
        super(variableElement);
    }

    public String descString(String packageName) {
        return up().asType().toString() + "#" + super.toString().replace(packageName + ".", "");
    }

    public TypeBox enclosingType() {
        return TypeBox.of(up());
    }

    public boolean isAny(Modifier... mods){
        for(Modifier m : mods) {
            if(is(m)) return true;
        }
        return false;
    }

    public boolean is(Modifier mod){
        return e.getModifiers().contains(mod);
    }
}