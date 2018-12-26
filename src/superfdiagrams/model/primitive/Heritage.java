/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package superfdiagrams.model.primitive;

import java.util.List;
import superfdiagrams.model.Element;
import static superfdiagrams.model.primitive.Type.*;

/**
 *
 * @author sebca
 */
public class Heritage implements Primitive{
    private List<Element> children;
    private Type type;

    @Override
    public String getLabel() {
        switch(type){
            case HERITAGE_D:
                return "D";
            case HERITAGE_S:
                return "S";
            default:
                return null;
        }
    }
    
    @Override
    public void setLabel(String name) {
        if (name.equals("D"))
            type = HERITAGE_D;
        if (name.equals("S"))
            type = HERITAGE_S;
        else
            System.err.println("No se ha ingresado una etiqueta válida.");
    }

    @Override
    public Type getType(){
        return type;
    }
    
    @Override
    public void setType(Type type){
        this.type = type;
    }

    @Override
    public List<Element> getChildren() {
        return children;
    }

    @Override
    public void setChildren(List<Element> children) {
        this.children = children;
    }

    @Override
    public int getPriority() {
        return 2;
    }
}
