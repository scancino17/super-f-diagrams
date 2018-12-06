/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package superfdiagrams.model.drawer;

import java.util.List;
import javafx.scene.canvas.GraphicsContext;
import superfdiagrams.model.ElementState;
import superfdiagrams.model.Vertex;
import superfdiagrams.model.primitive.Type;

/**
 *
 * @author sebca
 */
public interface Drawer {
    public void doDraw(GraphicsContext gc, List<Vertex> vertexes, String name, ElementState highlighted);
    public void doDrawVertex(GraphicsContext gc, List<Vertex> vextexes);
    public void setType(Type type);
    public Type getType();
}
