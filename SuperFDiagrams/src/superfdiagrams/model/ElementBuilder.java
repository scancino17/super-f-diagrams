/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package superfdiagrams.model;

import superfdiagrams.model.primitive.Relationship;
import superfdiagrams.model.primitive.Entity;
import superfdiagrams.model.primitive.Union;
import superfdiagrams.model.primitive.Attribute;
import java.util.ArrayList;
import java.util.List;
import superfdiagrams.model.drawer.ElipseDrawer;
import superfdiagrams.model.drawer.LineDrawer;
import superfdiagrams.model.drawer.PolygonDrawer;
import superfdiagrams.model.primitive.Heritage;
import superfdiagrams.model.primitive.Type;
import static superfdiagrams.model.primitive.Type.*;

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
    
    public Element generateEntity(Type type){
        Element element = new Element();
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
    
    public Element generateRelationship(int vertexes, List<Element> relations){
        Element element = new Element();
        Relationship relation = new Relationship();
        relation.setLabel(name);
        relation.setChildren(relations);
        
        element.setElement(relation);

  
        element.setVertexes(VertexGenerator.generateVertexes(vertexes, size, center));
        
        element.setDrawer(new PolygonDrawer());
        return element;
    }
    
    public Element generateRelationship(List<Element> entities, Type type){
        Element element = new Element();
        Relationship relation = new Relationship();
        
        element.setVertexes(VertexGenerator.generateVertexes(entities.size(), size, center));
        
        relation.setLabel(name);
        relation.setType(type);
        List<Element> unions = new ArrayList<>();
        
        element.setElement(relation);
        if(entities.size() > 1){
            for(Element el: entities){
                unions.add(generateLine(element, el));
            }
        } else if (entities.size() == 1){
            unions.add(generateLine(element, entities.get(0)));
            unions.add(generateLine(element, entities.get(0)));
        }
         
        
        relation.setChildren(unions);
        PolygonDrawer drawer = new PolygonDrawer();
        drawer.setType(type);
        drawer.setCenter(center);
        element.setDrawer(drawer);
        return element;
        
    }
    
    public Element generateAttribute(Attribute attribute){
        Element element = new Element();
        
        element.setElement(attribute);
        element.getElement().setLabel(name);
        
        element.setVertexes(VertexGenerator.generateEllipse(50, 55, center));
        
        List<Element> unions = new ArrayList<>();
        
        for(Element el: attribute.getChildren()){
                    unions.add(generateLine(element, el));
                }
        
        attribute.setChildren(unions);
        
        ElipseDrawer drawer = new ElipseDrawer();
        drawer.setCenter(center);
        drawer.setType(attribute.getType());
        element.setDrawer(drawer);
        return element;
    }
    
    public Element generateHeritage(Heritage heritage){
        Element element = new Element();
        
        element.setElement(heritage);
 
        element.getElement().setLabel(name);
        
        element.setVertexes(VertexGenerator.generateVertexes(50, 15, center));
        
        List<Element> unions = new ArrayList<>();
        
        for(Element el: heritage.getChildren()){
                    Element line = generateLine(element, el);
                    ((LineDrawer)line.getDrawer()).setType(UNION_HERITAGE);
                    line.getElement().setType(UNION_HERITAGE);
                    unions.add(line);
                }
        ((LineDrawer)unions.get(0).getDrawer()).setType(ROLE_STRONG);
        unions.get(0).getElement().setType(ROLE_STRONG);
        heritage.setChildren(unions);
        
        PolygonDrawer drawer = new PolygonDrawer();
        drawer.setCenter(center);
        drawer.setType(heritage.getType());
        element.setDrawer(drawer);
        return element;
        
    }

    
    public Element generateLine(Element relation, Element entity){
        Element line = new Element();
        
        List<Vertex> vertexes = GeometricUtilities.nearestVertexes(
                relation.getVertexes(), entity.getVertexes());
        
        Union union = new Union();
        union.setParent(relation);
        union.setChild(entity);
        LineDrawer drawer = new LineDrawer();
        
        if (relation.getElement().getType() == ROLE_WEAK && entity.getElement().getType() == ROLE_WEAK){
            union.setType(ROLE_WEAK);
            drawer.setType(ROLE_WEAK);
        }else{
            union.setType(ROLE_STRONG);
            drawer.setType(ROLE_STRONG);
        }
        
        line.setVertexes(vertexes);
        line.setDrawer(drawer);
        line.setElement(union);
        return line;
    }
    
    public Element cloneElement(Element element){
        Element clone = new Element();
        
        List<Vertex> vertexes = VertexGenerator.cloneVertex(element.getVertexes());
        
        clone.setVertexes(vertexes);
        clone.setElement(element.getElement());
        clone.setDrawer(element.getDrawer());
        
        return clone;
    }
    
    public Element cloneUnion(Element union){
        Element clone = new Element();
        
        Element parent = ((Union) union.getElement()).getParent();
        Element child = ((Union) union.getElement()).getChild();
        
        List<Vertex> vertexes = GeometricUtilities.nearestVertexes(parent.getVertexes(), child.getVertexes());
        
        Union primitive = new Union();
        primitive.setParent(parent);
        primitive.setChild(child);
        primitive.setType(union.getElement().getType());
        
        clone.setVertexes(vertexes);
        clone.setDrawer(new LineDrawer());
        clone.getDrawer().setType(union.getDrawer().getType());
        clone.setElement(primitive);
        
        
        return clone;
    }
}
