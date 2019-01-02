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
import superfdiagrams.FXMLDocumentController;
import superfdiagrams.model.drawer.ElipseDrawer;
import superfdiagrams.model.drawer.LineDrawer;
import superfdiagrams.model.drawer.PolygonDrawer;
import superfdiagrams.model.primitive.Heritage;
import superfdiagrams.model.primitive.Primitive;
import superfdiagrams.model.primitive.Type;
import static superfdiagrams.model.primitive.Type.*;

/**
 *
 * @author sebca
 */
public class ElementBuilder {
    private static final double DEFAULT_SIZE = 25;
    private static final double DEF_ATT_SIZE = 25;
    private String name;
    private Vertex center;
    private double size;
    
    public ElementBuilder(){
        this.size = DEFAULT_SIZE;
    }
    
    public static double getDefaultSize(){
        return DEFAULT_SIZE;
    }
    
    public void setLabel(String name) {
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
        entity.setLabel(name);
        
        element.setPrimitive(entity);
        double xSizeMultiplier = GeometricUtilities.getSizeMultiplier(name);
        element.setVertexes(VertexGenerator.generateRectangle(size * xSizeMultiplier, size, center));
        element.setCenterVertex(center);
        
        PolygonDrawer drawer = new PolygonDrawer();
        drawer.setType(type);
        drawer.setCenter(center);
        element.setDrawer(drawer);
        return element;
    }
    
    public Element generateRelationship(List<Element> related, Type type){
        Element element = new Element();
        Relationship relation = new Relationship();
        
        double xSizeMultiplier = GeometricUtilities.getSizeMultiplier(name);
        element.setVertexes(VertexGenerator.generateVertexes(related.size(), size * xSizeMultiplier, size, center));
        element.setCenterVertex(center);
        
        relation.setLabel(name);
        relation.setType(type);
        List<Element> unions = new ArrayList<>();
        
        element.setPrimitive(relation);
        if(related.size() > 1){
            for(Element el: related){
                String c = "0";
                do {
                    c = FXMLDocumentController.askCardinality(el.getPrimitive().getLabel());
                } while (c.equals("0"));
                unions.add(generateLine(element, el, c));
            }
        } else if (related.size() == 1){
            unions.add(generateLine(element, related.get(0)));
            unions.add(generateLine(element, related.get(0)));
        }
         
        
        relation.setChildren(unions);
        PolygonDrawer drawer = new PolygonDrawer();
        drawer.setType(type);
        drawer.setCenter(center);
        element.setDrawer(drawer);
        return element;       
    }
    
    public Element generateAttribute(List<Element> related, Type type){
        Attribute attribute = new Attribute();
        attribute.setChildren(related);
        attribute.setType(type);
        attribute.setLabel(name);
        
        Element element = new Element();
        
        element.setPrimitive(attribute);
        element.setCenterVertex(center);
        double xSizeMultiplier = GeometricUtilities.getSizeMultiplier(name);
        element.setVertexes(VertexGenerator.generateEllipse(49, DEF_ATT_SIZE * xSizeMultiplier, DEF_ATT_SIZE, center));
        
        List<Element> unions = new ArrayList<>();
        
        for(Element el: attribute.getChildren())
            unions.add(generateLine(element, el));
        
        attribute.setChildren(unions);
        
        ElipseDrawer drawer = new ElipseDrawer();
        drawer.setCenter(center);
        drawer.setType(type);
        element.setDrawer(drawer);
        
        //related siempre tiene un único elemento al ser llamado este método.
        //Verifica que el child no pertenesca a una agregacion, de ser el caso
        // lo agrega a esa.
        Element child = related.get(0);
        ComplexElement superParent = Finder.findComplexContained(child);
        if (superParent != null){
            List<Element> added = new ArrayList<>();
            added.add(element);
            added.addAll(unions);
        }
            
        
        return element;
    }
    
    public Element generateHeritage(List<Element> related, Type type){
        Heritage heritage = new Heritage();
        heritage.setChildren(related);
        heritage.setLabel(name); 
        
        Element element = new Element();
        
        element.setPrimitive(heritage);
        element.setCenterVertex(center);
        element.setVertexes(VertexGenerator.generateVertexes(49, 15, center));
        
        List<Element> unions = new ArrayList<>();
        
        for(Element el: heritage.getChildren()){
                    Element line = generateLine(element, el);
                    ((LineDrawer)line.getDrawer()).setType(UNION_HERITAGE);
                    line.getPrimitive().setType(UNION_HERITAGE);
                    unions.add(line);
                }
        ((LineDrawer)unions.get(0).getDrawer()).setType(ROLE_STRONG);
        unions.get(0).getPrimitive().setType(ROLE_STRONG);
        heritage.setChildren(unions);
        
        PolygonDrawer drawer = new PolygonDrawer();
        drawer.setCenter(center);
        drawer.setType(heritage.getType());
        element.setDrawer(drawer);
        return element;
        
    }

    public Element generateAgregation(List<Element> related, Type type){
        Entity agregation = new Entity();
        agregation.setChildren(related);
        agregation.setLabel(name);
        
        for(Element r : related){
            r.addPriority(2);
            System.out.println(r);
        }
        Element element = new ComplexElement();
        element.setPrimitive(agregation);
        List<Vertex> polygon = VertexGenerator.getAgregationVertexes(related);
        Vertex thisCenter = GeometricUtilities.getCenterOfMass(polygon);
        element.setVertexes(polygon);
        element.setCenterVertex(thisCenter);
        
        PolygonDrawer drawer = new PolygonDrawer();
        drawer.setCenter(thisCenter);
        drawer.setType(type);
        element.setDrawer(drawer);
        return element;
    }
    
    public Element generateLine(Element relation, Element entity){
        return this.generateLine(relation, entity, "");
    }
    
    public Element generateLine(Element relation, Element entity, String cardinality){
        Element line = new Element();
        
        List<Vertex> vertexes = GeometricUtilities.nearestVertexes(
                relation.getVertexes(), entity.getCenterVertex());
        
        Union union = new Union();
        union.setParent(relation);
        union.setChild(entity);
        union.setCardinality(cardinality);
        
        LineDrawer drawer = new LineDrawer();
        drawer.setCardinality(cardinality);
        
        if (relation.getPrimitive().getType() == ROLE_WEAK 
         && entity.getPrimitive().getType() == ROLE_WEAK)
        {
            union.setType(ROLE_WEAK);
            drawer.setType(ROLE_WEAK);
        }else{
            union.setType(ROLE_STRONG);
            drawer.setType(ROLE_STRONG);
        }
        
        line.setVertexes(vertexes);
        line.setCenterVertex(GeometricUtilities.getCenterOfMass(vertexes));
        line.setDrawer(drawer);
        line.setPrimitive(union);
        return line;
    }
    
    public Element cloneElement(Element element){
        Element clone = new Element();
        
        List<Vertex> vertexes = VertexGenerator.cloneVertex(element.getVertexes());
        
        clone.setVertexes(vertexes);
        clone.setPrimitive(element.getPrimitive());
        clone.setDrawer(element.getDrawer());
        
        return clone;
    }
    
    public Element cloneUnion(Element union){
        Element clone = new Element();
        
        Element parent = ((Union) union.getPrimitive()).getParent();
        Element child = ((Union) union.getPrimitive()).getChild();
        
        List<Vertex> vertexes = GeometricUtilities.nearestVertexes(parent.getVertexes(), child.getVertexes());
        
        Union primitive = new Union();
        primitive.setParent(parent);
        primitive.setChild(child);
        primitive.setType(union.getPrimitive().getType());
        
        clone.setVertexes(vertexes);
        clone.setDrawer(new LineDrawer());
        clone.getDrawer().setType(union.getDrawer().getType());
        clone.setPrimitive(primitive);
        
        return clone;
    }
    
    /**
     * Cambia el tamaño de un elemento cuando su label es cambiado. La función genera
     * un nuevo set de Vertex.
     * @author Sebastián Cancino
     * @param element Element a cambiar de forma
     */
    public void resize(Element element){
        Primitive primitive = element.getPrimitive();
        double multiplier = GeometricUtilities.getSizeMultiplier(primitive.getLabel());
        Vertex elementCenter = element.getCenterVertex();
        List<Vertex> newVertexSet = null;
        
        if(primitive instanceof Entity){
            newVertexSet = VertexGenerator.generateRectangle(size * multiplier, size, elementCenter);
        }
        
        if(primitive instanceof Relationship){
            newVertexSet = VertexGenerator.generateVertexes(primitive.getChildren().size(), size * multiplier, size, elementCenter);
        } 
        
        if(primitive instanceof Attribute){
            newVertexSet = VertexGenerator.generateEllipse(50, DEF_ATT_SIZE * multiplier, DEF_ATT_SIZE, elementCenter);
        }
        
        if (newVertexSet == null)
            return;
        
        List<Vertex> oldVertex = element.getVertexes();
        for(int i = 0 ; i < newVertexSet.size() && i < oldVertex.size(); i++){
            Vertex n = newVertexSet.get(i);
            Vertex o = oldVertex.get(i);
            
            o.setxPos(n.getxPos());
            o.setyPos(o.getyPos());
        }
    }
}
