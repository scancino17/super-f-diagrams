/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package superfdiagrams.model;

import java.util.List;
import javafx.scene.canvas.GraphicsContext;
import superfdiagrams.model.drawer.Drawable;
import superfdiagrams.model.drawer.Drawer;

/**
 *
 * @author sebca
 */
public class ElementWrapper implements Drawable{
    private Element element;
    private Drawer drawer;
    private List<Vertex> vertexes;
    private boolean highlighted = false;

    @Override
    public void draw(GraphicsContext gc) {
        drawer.doDraw(gc,vertexes,element.getLabel(), highlighted);
    }

    public void drawVertex(GraphicsContext gc)
    {
        drawer.doDrawVertex(gc,vertexes);
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
    
    public void setHighlighted(boolean value){
        this.highlighted = value;
    }
}
