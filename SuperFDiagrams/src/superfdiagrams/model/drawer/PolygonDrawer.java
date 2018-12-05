/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package superfdiagrams.model.drawer;
import java.util.List;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import superfdiagrams.model.*;
import superfdiagrams.model.primitive.Type;

/**
 *
 * @author sebca
 */
public class PolygonDrawer implements Drawer{
    private Type type;
    public Vertex center;
    private double zoom = 1;
    
    @Override
    public Type getType() {
        return type;
    }

    @Override
    public void setType(Type type) {
        this.type = type;
    }
    
    public void setCenter(Vertex center){
        this.center = center;
    }
    
    /**
     * Funcion que dibuja los poligonos, tan solo usa los vertices creados en las
     * otras funciones y los une, al final se une el ultimo vertice de la lista
     * con el primero
     * @param gc
     * @param vertexes
     * @param name
     * @param highlighted
     */
    @Override
    public void doDraw(GraphicsContext gc, List<Vertex> vertexes, String name, boolean highlighted) {
        zoom =  MainController.getController().getZoomFactor();
        switch (type){
            case ROLE_WEAK:
                weakDraw(gc, vertexes, name, highlighted);
                break;
            default:
                normalDraw(gc, vertexes, name, highlighted);
                break;/*
                case 3:
                weakRelationDraw(gc, vertexes, name, highlighted);
                break;*/
             
        }
    }

    /**Marca los puntos de los vertices
     * @param gc
     * @param vertexes
     */
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
        if(!highlighted){
            gc.setStroke(Color.BLACK);
        } else{
            gc.setStroke(Color.CORNFLOWERBLUE);
        }

        gc.setLineWidth(1);
        
        int size = vertexes.size();
        for(int i = 0; i < size; i++){
            gc.strokeLine(vertexes.get(i % size).getxPos() * zoom,
                    vertexes.get(i % size).getyPos() * zoom,
                    vertexes.get((i + 1) % size).getxPos() * zoom,
                    vertexes.get(( i +1 )% size).getyPos() * zoom);
        }
        
        if(highlighted)
            gc.setStroke(Color.BLACK);
        
        Vertex center = GeometricUtilities.getCenterOfMass(vertexes);
        paint(gc, vertexes);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.strokeText(name, center.getxPos() * zoom, center.getyPos() * zoom);
    }
    
    public void weakDraw(GraphicsContext gc, List<Vertex> vertexes, String name, boolean highlighted){
        if(!highlighted){
            gc.setStroke(Color.BLACK);
        } else{
            gc.setStroke(Color.CORNFLOWERBLUE);
        }

        gc.setLineWidth(3);
        
        int size = vertexes.size();
        for(int i = 0; i < size; i++){
            gc.strokeLine(vertexes.get(i % size).getxPos() * zoom,
                    vertexes.get(i % size).getyPos() * zoom,
                    vertexes.get((i + 1) % size).getxPos() * zoom,
                    vertexes.get((i + 1)% size).getyPos()  * zoom);
        }
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(1);
        for(int i = 0; i < vertexes.size(); i++){
            gc.strokeLine(vertexes.get(i % size).getxPos() * zoom,
                    vertexes.get(i % size).getyPos() * zoom,
                    vertexes.get((i + 1) % size).getxPos() * zoom,
                    vertexes.get(( i +1 )% size).getyPos() * zoom);
        }
        
        if(highlighted)
            gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);
        gc.setStroke(Color.BLACK);
        Vertex center = GeometricUtilities.getCenterOfMass(vertexes);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.strokeText(name, center.getxPos() * zoom, center.getyPos() * zoom);
    }
    
    private void paint(GraphicsContext gc, List<Vertex> vertexes){
        gc.setFill(Color.WHITE);
        gc.beginPath();
        
        for(Vertex v: vertexes)
            gc.lineTo(v.getxPos(), v.getyPos());
        
        Vertex v1 = vertexes.get(0);
        gc.lineTo(v1.getxPos(), v1.getyPos());
        gc.fill();
        gc.closePath();
    }
}
