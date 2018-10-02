/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package superfdiagrams.model;

import superfdiagrams.model.drawer.PolygonDrawer;

/**
 *
 * @author sebca
 */
public class ElementBuilder {
    private static final int DEFAULT_SIZE = 100;
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
        element.getElement().setName(name);
        
        element.setVertexes(VertexGenerator.generateRectangle(size, center));
        
        element.setDrawer(new PolygonDrawer());
        return element;
    }
    
    public ElementWrapper generateRelationship(int vertexes){
        ElementWrapper element = new ElementWrapper();
        
        element.setElement(new Relationship());
        element.getElement().setName(name);
        
        element.setVertexes(VertexGenerator.generateVertexes(vertexes, size, center));
        
        element.setDrawer(new PolygonDrawer());
        return element;
    }
}
