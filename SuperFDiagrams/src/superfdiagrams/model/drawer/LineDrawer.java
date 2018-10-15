/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package superfdiagrams.model.drawer;

import static java.util.Collections.list;
import java.util.List;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import superfdiagrams.model.Vertex;

/**
 *
 * @author sebca
 */
public class LineDrawer implements Drawer{
    @Override
    public void doDraw(GraphicsContext gc, List<Vertex> vertexes, String name, boolean highlighted) {
        //gc.setStroke(Color.WHITE);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);
        gc.strokeLine(vertexes.get(0).getxPos(), vertexes.get(0).getyPos(),
                vertexes.get(1).getxPos(),vertexes.get(1).getyPos());
    }
    
    public void doDraw(GraphicsContext gc, List<Vertex> vertexes, String name){
        this.doDraw(gc, vertexes, name, false);
    }

    @Override
    public void doDrawVertex(GraphicsContext gc, List<Vertex> vextexes)
    {
       
    }

}
