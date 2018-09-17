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
public class Relationship implements Element{
    private String name;
    List<Union> relations;

    @Override
    public String getName() {
        return name;
    }

    public List<Union> getRelations() {
        return relations;
    }

    public void setRelations(List<Union> relations) {
        this.relations = relations;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    
    
}
