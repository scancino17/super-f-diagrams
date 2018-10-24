/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package superfdiagrams.model;

import java.util.ArrayList;
import java.util.List;
import superfdiagrams.model.drawer.ElipseDrawer;
import superfdiagrams.model.drawer.LineDrawer;
import superfdiagrams.model.drawer.PolygonDrawer;

/**
 *
 * @author sebca
 */
public class ElementBuilder {
    private static final int DEFAULT_SIZE = 75;
    private String name;
    private Vertex center;
    private int size;
    
    public ElementBuilder(){
        this.size = DEFAULT_SIZE;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCenter(Vertex vertex){
        this.center = vertex;
    }
    
    public void setSize(int size) {
        this.size = size;
    }
    
    public ElementWrapper generateEntity(){
        ElementWrapper element = new ElementWrapper();
        
        element.setElement(new Entity());
        element.getElement().setLabel(name);
        
        element.setVertexes(VertexGenerator.generateRectangle(size, center));
        
        element.setDrawer(new PolygonDrawer());
        return element;
    }
    
    public ElementWrapper generateRelationship(int vertexes, List<ElementWrapper> relations){
        ElementWrapper element = new ElementWrapper();
        Relationship relation = new Relationship();
        relation.setLabel(name);
        relation.setContained(relations);
        
        element.setElement(relation);

  
        element.setVertexes(VertexGenerator.generateVertexes(vertexes, size, center));
        
        element.setDrawer(new PolygonDrawer());
        return element;
    }
    
    public ElementWrapper generateRelationship(List<ElementWrapper> entities){
        ElementWrapper element = new ElementWrapper();
        Relationship relation = new Relationship();
        
        element.setVertexes(VertexGenerator.generateVertexes(entities.size(), size, center));
        
        relation.setLabel(name);
        List<ElementWrapper> unions = new ArrayList<>();
        
        if(entities.size() > 1){
            for(ElementWrapper el: entities){
                unions.add(generateLine(element, el));
            }
        } else if (entities.size() == 1){
            unions.add(generateLine(element, entities.get(0)));
            unions.add(generateLine(element, entities.get(0)));
        }
         
        
        relation.setContained(unions);
        element.setElement(relation);
        element.setDrawer(new PolygonDrawer());
        return element;
        
    }
    
    public ElementWrapper generateAttribute(Attribute attribute){
        ElementWrapper element = new ElementWrapper();
        
        element.setElement(attribute);
        element.getElement().setLabel(name);
        
        element.setVertexes(VertexGenerator.generateVertexes(50, size, center));
        
        List<ElementWrapper> unions = new ArrayList<>();
        
        for(ElementWrapper el: attribute.getContained()){
                    unions.add(generateLine(element, el));
                }
        
        attribute.setContained(unions);
        
        ElipseDrawer drawer = new ElipseDrawer();
        drawer.setType(attribute.getType());
        element.setDrawer(drawer);
        return element;
    }
    
    public ElementWrapper generateLine(ElementWrapper relation, ElementWrapper entity){
        ElementWrapper line = new ElementWrapper();
        
        List<Vertex> vertexes = GeometricUtilities.nearestVertexes(
                relation.getVertexes(), entity.getVertexes());
        
        Union union = new Union();
        union.setParent(relation);
        union.setChild(entity);
        
        line.setVertexes(vertexes);
        line.setDrawer(new LineDrawer());
        line.setElement(union);
        return line;
    }
}
