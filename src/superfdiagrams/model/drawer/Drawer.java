/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package superfdiagrams.model.drawer;

import java.util.List;
import javafx.scene.canvas.GraphicsContext;
import superfdiagrams.model.Vertex;

/**
 *
 * @author sebca
 */
public interface Drawer {
    public void doDraw(GraphicsContext gc, List<Vertex> vertexes, String name, boolean highlighted);
    public void doDrawVertex(GraphicsContext gc, List<Vertex> vextexes);
}
