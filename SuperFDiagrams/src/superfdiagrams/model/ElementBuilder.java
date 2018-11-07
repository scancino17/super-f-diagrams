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
    
    public static int getDefaultSize(){
        return DEFAULT_SIZE;
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
    
    public ElementWrapper generateEntity(int type){
        ElementWrapper element = new ElementWrapper();
        Entity entity = new Entity();
        entity.setType(type);
        element.setElement(entity);
        element.getElement().setLabel(name);
        
        element.setVertexes(VertexGenerator.generateRectangle(size, center));
        
        PolygonDrawer drawer = new PolygonDrawer();
        drawer.setType(type);
        drawer.setCenter(center);
        element.setDrawer(drawer);
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
    
    public ElementWrapper generateRelationship(List<ElementWrapper> entities, int type){
        ElementWrapper element = new ElementWrapper();
        Relationship relation = new Relationship();
        
        element.setVertexes(VertexGenerator.generateVertexes(entities.size(), size, center));
        
        relation.setLabel(name);
        relation.setType(type);
        List<ElementWrapper> unions = new ArrayList<>();
        
        element.setElement(relation);
        if(entities.size() > 1){
            for(ElementWrapper el: entities){
                unions.add(generateLine(element, el));
            }
        } else if (entities.size() == 1){
            unions.add(generateLine(element, entities.get(0)));
            unions.add(generateLine(element, entities.get(0)));
        }
         
        
        relation.setContained(unions);
        PolygonDrawer drawer = new PolygonDrawer();
        drawer.setType(type);
        drawer.setCenter(center);
        element.setDrawer(drawer);
        return element;
        
    }
    
    public ElementWrapper generateAttribute(Attribute attribute){
        ElementWrapper element = new ElementWrapper();
        
        element.setElement(attribute);
        element.getElement().setLabel(name);
        
        element.setVertexes(VertexGenerator.generateEllipse(50, 55, center));
        
        List<ElementWrapper> unions = new ArrayList<>();
        
        for(ElementWrapper el: attribute.getContained()){
                    unions.add(generateLine(element, el));
                }
        
        attribute.setContained(unions);
        
        ElipseDrawer drawer = new ElipseDrawer();
        drawer.setCenter(center);
        drawer.setType(attribute.getType());
        element.setDrawer(drawer);
        return element;
    }
    
    public ElementWrapper generateHeritage(Relationship heritage){
        ElementWrapper element = new ElementWrapper();
        
        element.setElement(heritage);
 
        element.getElement().setLabel(name);
        
        element.setVertexes(VertexGenerator.generateVertexes(50, 15, center));
        
        List<ElementWrapper> unions = new ArrayList<>();
        
        for(ElementWrapper el: heritage.getContained()){
                    unions.add(generateLine(element, el));
                }
        
        heritage.setContained(unions);
        
        PolygonDrawer drawer = new PolygonDrawer();
        drawer.setCenter(center);
        drawer.setType(heritage.getType());
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
        LineDrawer drawer = new LineDrawer();
        
        if (relation.getElement().getType() == 3 && entity.getElement().getType() == 2){
            drawer.setType(2);
        }else{
            drawer.setType(1);
        }
        
        line.setVertexes(vertexes);
        line.setDrawer(drawer);
        line.setElement(union);
        return line;
    }
    
    public ElementWrapper cloneElement(ElementWrapper element){
        ElementWrapper clone = new ElementWrapper();
        
        List<Vertex> vertexes = VertexGenerator.cloneVertex(element.getVertexes());
        
        clone.setVertexes(vertexes);
        clone.setElement(element.getElement());
        clone.setDrawer(element.getDrawer());
        
        return clone;
    }
    
    public ElementWrapper cloneUnion(ElementWrapper union){
        ElementWrapper clone = new ElementWrapper();
        
        ElementWrapper parent = ((Union) union.getElement()).getParent();
        ElementWrapper child = ((Union) union.getElement()).getChild();
        
        List<Vertex> vertexes = GeometricUtilities.nearestVertexes(parent.getVertexes(), child.getVertexes());
        
        Union primitive = new Union();
        primitive.setParent(parent);
        primitive.setChild(child);
        
        clone.setVertexes(vertexes);
        clone.setDrawer(new LineDrawer());
        clone.setElement(primitive);
        
        return clone;
    }
}
