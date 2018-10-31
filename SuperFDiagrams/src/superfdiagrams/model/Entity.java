/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package superfdiagrams.model;

import java.util.List;

/**
 *
 * @author sebca
 */
public class Entity implements Element{
    private String name = "";
    private int type;
    
    @Override
    public String getLabel() {
        return name;
    }
    
    
    public void setLabel(String name){
        this.name = name;
    }

    @Override
    public List<ElementWrapper> getContained() {
        return null;
    }

    @Override
    public void setContained(List<ElementWrapper> relations) {}
    
    @Override
    public int getType(){
        return type;
    }
    
    @Override
    public void setType(int type){
        this.type = type;
    }
}
