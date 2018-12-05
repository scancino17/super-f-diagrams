/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package superfdiagrams.model.drawer;

import java.util.List;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import superfdiagrams.model.GeometricUtilities;
import superfdiagrams.model.MainController;
import superfdiagrams.model.Vertex;
import superfdiagrams.model.primitive.Type;

/**
 *
 * @author sebca
 */
public class LineDrawer implements Drawer{
    private Type type;
    private double zoom;

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public void setType(Type type) {
        this.type = type;
    }
    
    @Override
    public void doDraw(GraphicsContext gc, List<Vertex> vertexes, String name, boolean highlighted) {
        zoom =  MainController.getController().getZoomFactor();
        switch(type){
            case ROLE_WEAK:
                weakDraw(gc, vertexes, name, highlighted);
                break;
            case UNION_HERITAGE:
                heritageDraw(gc, vertexes, name, highlighted);
            default:
                normalDraw(gc, vertexes, name, highlighted);
                break;
        }
    }
    
    public void doDraw(GraphicsContext gc, List<Vertex> vertexes, String name){
        this.doDraw(gc, vertexes, name, false);
    }

    @Override
    public void doDrawVertex(GraphicsContext gc, List<Vertex> vertexes)
    {
       for (Vertex v : vertexes)
        {
            gc.setStroke(Color.RED);
            gc.setLineWidth(5);
            gc.strokeLine(v.getxPos() * zoom,
                    v.getyPos() * zoom,
                    v.getxPos() * zoom,
                    v.getyPos() * zoom);
        }
    }
    
    public void normalDraw(GraphicsContext gc, List<Vertex> vertexes, String name, boolean highlighted){
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);
        gc.strokeLine(vertexes.get(0).getxPos() * zoom, vertexes.get(0).getyPos() * zoom,
                vertexes.get(1).getxPos() * zoom,vertexes.get(1).getyPos() * zoom);
    }
    
    public void weakDraw(GraphicsContext gc, List<Vertex> vertexes, String name, boolean highlighted){
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(3);
        gc.strokeLine(vertexes.get(0).getxPos() * zoom, vertexes.get(0).getyPos() * zoom,
                vertexes.get(1).getxPos() * zoom,vertexes.get(1).getyPos() * zoom);
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(1);
        gc.strokeLine(vertexes.get(0).getxPos() * zoom, vertexes.get(0).getyPos() * zoom,
                vertexes.get(1).getxPos() * zoom,vertexes.get(1).getyPos() * zoom);
    }
    
    public void heritageDraw(GraphicsContext gc, List<Vertex> vertexes, String name, boolean highlighted){
        this.normalDraw(gc, vertexes, name, highlighted);
        this.drawSemicircle(gc, vertexes);
    }
    
    public void drawSemicircle(GraphicsContext gc, List<Vertex> vertexes){
        Vertex a = vertexes.get(0);
        Vertex b = vertexes.get(1);
        Vertex mid = GeometricUtilities.midPoint(a, b);
        double y = (a.getyPos() - b.getyPos()) ;
        double x = (a.getxPos() - b.getxPos());
        double angle = x / y;
        angle = Math.toDegrees(Math.atan(angle));
        
        gc.strokeArc((mid.getxPos() - 25) * zoom,
                     (mid.getyPos() - 25) * zoom,
                     50 * zoom,
                     50 * zoom,
                     (y < 0) ? angle + 180 : angle,
                     180, ArcType.OPEN);
    }
}
