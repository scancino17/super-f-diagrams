/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package superfdiagrams.model.primitive;

import java.util.List;
import superfdiagrams.model.Element;

/**
 *
 * @author Diego
 */
public class Attribute implements Primitive {
    private String label = "";
    private Type type;
    private List<Element> children;
    
    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public void setLabel(String name) {
        this.label = name;
    }

    @Override
    public List<Element> getChildren() {
        return children;
    }

    @Override
    public void setChildren(List<Element> relations) {
        this.children = relations;
    }
    
    @Override
    public Type getType(){
        return type;
    }

    @Override
    public void setType(Type type) {
        this.type =  type;
    }
}
