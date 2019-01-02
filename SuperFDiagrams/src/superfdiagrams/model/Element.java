/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package superfdiagrams.model;

import superfdiagrams.model.primitive.Primitive;
import java.util.List;
import javafx.scene.canvas.GraphicsContext;
import static superfdiagrams.model.ElementState.NORMAL;
import superfdiagrams.model.drawer.Drawable;
import superfdiagrams.model.drawer.Drawer;

/**
 *
 * @author sebca
 */
public class Element implements Drawable, Comparable<Element>{
    private Primitive primitive;
    private Drawer drawer;
    private List<Vertex> vertexes;
    private Vertex center;
    private ElementState state = NORMAL;
    private int priority = 0;

    @Override
    public void draw(GraphicsContext gc) {
        drawer.doDraw(gc,vertexes,primitive.getLabel(), state);
    }

    @Override
    public void drawVertex(GraphicsContext gc)
    {
        drawer.doDrawVertex(gc,vertexes);
    }

    public Primitive getPrimitive() {
        return primitive;
    }

    public void setPrimitive(Primitive element) {
        this.primitive = element;
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
    
    public void setElementState(ElementState value){
        this.state = value;
    }

    public ElementState getElementState(){return this.state;}

    public void setCenterVertex(Vertex center){
        this.center = center;
    }
    
    public Vertex getCenterVertex(){
        return center;
    }

    public void addPriority(int n){
        this.priority+=n;
    }
    
    public int getPriority(){
        return primitive.getPriority() + this.priority;
    }
    
    @Override
    public int compareTo(Element that) {
        return this.getPriority() - that.getPriority();
    }
    
    @Override
    public String toString(){
        return this.primitive.getLabel() + " " + getPriority();
    }
}
