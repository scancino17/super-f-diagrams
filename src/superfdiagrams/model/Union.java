/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package superfdiagrams.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author sebca
 */
public class Union implements Element, ConnectsWrappers{
    private ElementWrapper entity;
    private ElementWrapper relationship;
    private int type;
    
    @Override
    public String getLabel() {
        return null;
    }

    @Override
    public void setLabel(String name) {}   
    
    @Override
    public List<ElementWrapper> getContained() {
        return null;
    }

    @Override
    public void setContained(List<ElementWrapper> relations) {}

    @Override
    public ElementWrapper getParent() {
       return relationship;
    }

    @Override
    public ElementWrapper getChild() {
        return entity;
    }

    @Override
    public void setParent(ElementWrapper element) {
        this.relationship = element;
    }

    @Override
    public void setChild(ElementWrapper element) {
        this.entity = element;
    }

    @Override
    public int getType(){
        return type;
    }
    
    @Override
    public void setType(int type){
        this.type = type;
    }
}
