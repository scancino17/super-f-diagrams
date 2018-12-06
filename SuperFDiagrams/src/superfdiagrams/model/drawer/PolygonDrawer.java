/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package superfdiagrams.model.drawer;
import java.util.List;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
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
     * @param elementState
     */
    @Override
    public void doDraw(GraphicsContext gc, List<Vertex> vertexes, String name, ElementState elementState) {
        zoom =  MainController.getController().getZoomFactor();
        switch (type){
            case ROLE_WEAK:
                weakDraw(gc, vertexes, name, elementState);
                break;
            default:
                normalDraw(gc, vertexes, name, elementState);
                break; 
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
    
    public void normalDraw(GraphicsContext gc, List<Vertex> vertexes, String name, ElementState elementState){
        switch(elementState){
            case HIGHLIGHTED:
                gc.setStroke(Color.CORNFLOWERBLUE);
                break;
            case INVALID:
                gc.setStroke(Color.CRIMSON);
                break;
            default:
                gc.setStroke(Color.BLACK);
                break;
        }

        gc.setLineWidth(1);
        
        int size = vertexes.size();
        for(int i = 0; i < size; i++){
            gc.strokeLine(vertexes.get(i % size).getxPos() * zoom,
                    vertexes.get(i % size).getyPos() * zoom,
                    vertexes.get((i + 1) % size).getxPos() * zoom,
                    vertexes.get(( i +1 )% size).getyPos() * zoom);
        }
        
        gc.setStroke(Color.BLACK);
        
        Vertex center = GeometricUtilities.getCenterOfMass(vertexes);
        this.drawText(gc, name, center);
    }
    
    public void weakDraw(GraphicsContext gc, List<Vertex> vertexes, String name, ElementState elementState){
        Color color = Color.BLACK;
        switch(elementState){
            case HIGHLIGHTED:
                color = (Color.CORNFLOWERBLUE);
                break;
            case INVALID:
                color = (Color.CRIMSON);
                break;
            default:
                color = (Color.BLACK);
                break;
        }

        gc.setStroke(color);
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
        
        gc.setStroke(color);
        gc.setLineWidth(1);
        gc.setStroke(color);
        Vertex center = GeometricUtilities.getCenterOfMass(vertexes);
        this.drawText(gc, name, center);
    }
    
    private void drawText(GraphicsContext gc, String label, Vertex center){
        gc.setFont(new Font(Font.getDefault().getSize() * zoom));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText(label, center.getxPos() * zoom, (center.getyPos() + 4)* zoom);
    }
}
