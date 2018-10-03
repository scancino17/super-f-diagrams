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
public class Diagram {
    private ArrayList<Entity> entitys = new ArrayList();
    private ArrayList<Relationship> relations = new ArrayList();
    
    public Diagram() {

    }
    
    public void addEntity(Entity entity){
        entitys.add(entity);
    }
    
    public void addRelation(Relationship relation){
        relations.add(relation);
    }
    
    public Entity getEntity(int index){
        return entitys.get(index);
    }
    
    public Relationship getRelation(int index){
        return relations.get(index);
    }
}
