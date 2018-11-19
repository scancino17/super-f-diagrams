/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package superfdiagrams.model.primitive;

import java.util.ArrayList;
import java.util.List;
import superfdiagrams.model.Element;

/**
 *
 * @author sebca
 */
public class Relationship implements Primitive{
    private String name;
    private List<Element> children;
    private Type type;

    @Override
    public String getLabel() {
        return name;
    }
    @Override
    public void setLabel(String name) {
        this.name = name;
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
  
}
