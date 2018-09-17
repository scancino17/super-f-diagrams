/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package superfdiagrams.model;

import java.util.List;
import superfdiagrams.controller.Drawable;
import superfdiagrams.controller.drawer.Drawer;

/**
 *
 * @author sebca
 */
public class ElementWrapper implements Drawable{
    private Element element;
    private Drawer drawer;
    private List<Vertex> vertexes;

    @Override
    public void draw() {
        drawer.doDraw();
    }

    public Element getElement() {
        return element;
    }

    public void setElement(Element element) {
        this.element = element;
    }

    public Drawer getDrawer() {
        return drawer;
    }

    public void setDrawer(Drawer drawer) {
        this.drawer = drawer;
    }

    public List<Vertex> getVertexes() {
        return vertexes;
    }

    public void setVertexes(List<Vertex> vertexes) {
        this.vertexes = vertexes;
    }
    
    
    
}
