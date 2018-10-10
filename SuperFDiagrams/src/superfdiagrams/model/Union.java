/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package superfdiagrams.model;

import java.util.ArrayList;

/**
 *
 * @author sebca
 */
public class Union implements Element{
    public ElementWrapper entity;
    public Relationship relationship;

    
    
    @Override
    public String getName() {
        return null;
    }

    @Override
    public void setName(String name) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    

    public ElementWrapper getEntity() {
        return entity;
    }

    public void setEntity(ElementWrapper entity) {
        this.entity = entity;
    }

    public Relationship getRelationship() {
        return relationship;
    }

    public void setRelationship(Relationship relationship) {
        this.relationship = relationship;
    }

    @Override
    public ArrayList<ElementWrapper> getRelations() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setRelations(ArrayList<ElementWrapper> relations) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
