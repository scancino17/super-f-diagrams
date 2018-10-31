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
    private String label = "";
   // private ElementWrapper entity;
   /* public enum AttType{GENERICO, CLAVE, MULTI, COMPUESTO, DERIVADO};
    private AttType type;*/
    private int type;
    private List<ElementWrapper> contained;
    
    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public void setLabel(String name) {
        this.label = name;
    }

    @Override
    public List<ElementWrapper> getContained() {
        return contained;
    }

    @Override
    public void setContained(List<ElementWrapper> relations) {
        this.contained = relations;
    }
    
    public int getType(){
        return type;
    }
    
    public void setType(int type){
        this.type = type;
    }
}
