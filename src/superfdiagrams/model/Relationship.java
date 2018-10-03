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
    private ArrayList<ElementWrapper> relations;


    
    @Override
    public String getName() {
        return name;
    }

    @Override
    public ArrayList<ElementWrapper> getRelations() {
        return relations;
    }

    @Override
    public void setRelations(ArrayList<ElementWrapper> relations) {
        this.relations = relations;
        System.out.println(relations.size());
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

 
    
    
    
}
