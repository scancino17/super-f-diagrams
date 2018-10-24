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
public class Relationship implements Element{
    private String name;
    private List<ElementWrapper> relations;


    
    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<ElementWrapper> getRelations() {
        return relations;
    }

    @Override
    public void setRelations(List<ElementWrapper> relations) {
        this.relations = relations;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int getType() {
        return 0;
    }

 
    
    
    
}
