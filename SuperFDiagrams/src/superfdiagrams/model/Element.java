/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package superfdiagrams.model;

import superfdiagrams.model.primitive.Primitive;
import java.util.List;
import javafx.scene.canvas.GraphicsContext;
import superfdiagrams.model.drawer.Drawable;
import superfdiagrams.model.drawer.Drawer;

/**
 *
 * @author sebca
 */
public class Element implements Drawable{
    private Primitive element;
    private Drawer drawer;
    private List<Vertex> vertexes;
    private Vertex center;
    private boolean highlighted = false;

    @Override
    public void draw(GraphicsContext gc) {
        drawer.doDraw(gc,vertexes,element.getLabel(), highlighted);
    }

    public void drawVertex(GraphicsContext gc)
    {
        drawer.doDrawVertex(gc,vertexes);
    }

    public Primitive getElement() {
        return element;
    }

    public void setElement(Primitive element) {
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
    
    public void setCenterVertex(Vertex center){
        this.center = center;
    }
    
    public Vertex getCenterVertex(){
        return center;
    }
}
