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
public class Entity implements Primitive {
    private String name = "";
    private Type type;
    private List<Element> children;
    
    @Override
    public String getLabel() {
        return name;
    }
    
    public void addChildren(List<Element> children){
        if (this.children != null)
            this.children.addAll(children);
        else
            this.children = children;
    }
    
    public void addChildren(Element element){
        if (this.children != null)
            this.children.add(element);
        else{
            List<Element> children = new ArrayList<>();
            children.add(element);
            this.children = children;
        }
            
    }
    
    public void setLabel(String name){
        this.name = name;
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
    public Type getType(){
        return type;
    }
    
    @Override
    public void setType(Type type){
        this.type = type;
    }

    @Override
    public int getPriority() {
        if(children == null || children.isEmpty())
            return 2;
        else return 0;
    }
}
