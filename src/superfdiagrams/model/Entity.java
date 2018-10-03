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
public class Entity implements Element{
    private String name = "";
    
    @Override
    public String getName() {
        return name;
    }
    
    
    public void setName(String name){
        this.name = name;
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
