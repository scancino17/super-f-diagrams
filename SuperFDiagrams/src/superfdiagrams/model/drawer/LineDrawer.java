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
    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
    
    @Override
    public void doDraw(GraphicsContext gc, List<Vertex> vertexes, String name, boolean highlighted) {
        switch(type){
            case 1:
                normalDraw(gc, vertexes, name, highlighted);
                break;
            case 2:
                weakDraw(gc, vertexes, name, highlighted);
                break;
        }
    }
    
    public void doDraw(GraphicsContext gc, List<Vertex> vertexes, String name){
        this.doDraw(gc, vertexes, name, false);
    }

    @Override
    public void doDrawVertex(GraphicsContext gc, List<Vertex> vextexes)
    {
       
    }
    
    public void normalDraw(GraphicsContext gc, List<Vertex> vertexes, String name, boolean highlighted){
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);
        gc.strokeLine(vertexes.get(0).getxPos(), vertexes.get(0).getyPos(),
                vertexes.get(1).getxPos(),vertexes.get(1).getyPos());
    }
    
    public void weakDraw(GraphicsContext gc, List<Vertex> vertexes, String name, boolean highlighted){
        System.out.println("xd");
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(3);
        gc.strokeLine(vertexes.get(0).getxPos(), vertexes.get(0).getyPos(),
                vertexes.get(1).getxPos(),vertexes.get(1).getyPos());
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(1);
        gc.strokeLine(vertexes.get(0).getxPos(), vertexes.get(0).getyPos(),
                vertexes.get(1).getxPos(),vertexes.get(1).getyPos());
    }
}