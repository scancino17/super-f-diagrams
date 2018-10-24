/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package superfdiagrams.model;

import java.util.List;

/**
 *
 * @author Diego
 */
public class Attribute implements Element{
    private String name = "";
   // private ElementWrapper entity;
   /* public enum AttType{GENERICO, CLAVE, MULTI, COMPUESTO, DERIVADO};
    private AttType type;*/
    private int type;
    private List<ElementWrapper> relations;
    
    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public List<ElementWrapper> getRelations() {
        return relations;
    }

    @Override
    public void setRelations(List<ElementWrapper> relations) {
        this.relations = relations;
    }
    
   /* public void setEntity(ElementWrapper entity){
        this.entity = entity;
    }
    
    public ElementWrapper getEntity(){
        return this.entity;
    }*/
    
    /*public void setType(AttType type){
        this.type = type;
    }
    
    public AttType getType(){
        return this.type;
    }*/
    
    @Override
    public int getType(){
        return type;
    }
    
    public void setType(int type){
        this.type = type;
    }
}
