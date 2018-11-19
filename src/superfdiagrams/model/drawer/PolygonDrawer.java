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
import superfdiagrams.model.Diagram;
import superfdiagrams.model.GeometricUtilities;
import superfdiagrams.model.Vertex;
import superfdiagrams.model.VertexGenerator;

/**
 *
 * @author sebca
 */
public class PolygonDrawer implements Drawer{
    private int type;
    public Vertex center;
    
    public int getType() {
        return type;
    }

    public void setType(int type) {
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
        switch (type){
            case 1:
                normalDraw(gc, vertexes, name, highlighted);
                break;
            case 2:
                weakDraw(gc, vertexes, name, highlighted);
                break;
            case 3:
                weakRelationDraw(gc, vertexes, name, highlighted);
                break;
             
        }
    }

    /**Marca los puntos de los vertices
     * @param gc
     * @param vextexes
     */
    @Override
    public void doDrawVertex(GraphicsContext gc, List<Vertex> vextexes)
    {
        for (Vertex v : vextexes)
        {
            gc.setStroke(Color.RED);
            gc.setLineWidth(5);
            gc.strokeLine(v.getxPos(), v.getyPos() , v.getxPos(), v.getyPos());
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
            gc.strokeLine(vertexes.get(i % size).getxPos(), vertexes.get(i % size).getyPos(),
                    vertexes.get((i + 1) % size).getxPos(), vertexes.get(( i +1 )% size).getyPos());
        }
        
        if(highlighted)
            gc.setStroke(Color.BLACK);
        
        Vertex center = GeometricUtilities.getCenterOfMass(vertexes);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.strokeText(name, center.getxPos(), center.getyPos());
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
            gc.strokeLine(vertexes.get(i % size).getxPos(), vertexes.get(i % size).getyPos(),
                    vertexes.get((i + 1) % size).getxPos(), vertexes.get(( i +1 )% size).getyPos());
        }
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(1);
        for(int i = 0; i < vertexes.size(); i++){
            gc.strokeLine(vertexes.get(i % size).getxPos(), vertexes.get(i % size).getyPos(),
                    vertexes.get((i + 1) % size).getxPos(), vertexes.get(( i +1 )% size).getyPos());
        }
        
        if(highlighted)
            gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);
        gc.setStroke(Color.BLACK);
        Vertex center = GeometricUtilities.getCenterOfMass(vertexes);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.strokeText(name, center.getxPos(), center.getyPos());
    }
    
    public void weakRelationDraw(GraphicsContext gc, List<Vertex> vertexes, String name, boolean highlighted){
        if(!highlighted){
            gc.setStroke(Color.BLACK);
        } else{
            gc.setStroke(Color.CORNFLOWERBLUE);
        }

        gc.setLineWidth(3);
        
        int size = vertexes.size();
        for(int i = 0; i < size; i++){
            gc.strokeLine(vertexes.get(i % size).getxPos(), vertexes.get(i % size).getyPos(),
                    vertexes.get((i + 1) % size).getxPos(), vertexes.get(( i +1 )% size).getyPos());
        }
        gc.setLineWidth(1);
        gc.setStroke(Color.WHITE);
        for(int i = 0; i < vertexes.size(); i++){
            gc.strokeLine(vertexes.get(i % size).getxPos(), vertexes.get(i % size).getyPos(),
                    vertexes.get((i + 1) % size).getxPos(), vertexes.get(( i +1 )% size).getyPos());
        }
        
        if(highlighted)
            gc.setStroke(Color.BLACK);
        
        gc.setStroke(Color.BLACK);
        Vertex center = GeometricUtilities.getCenterOfMass(vertexes);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.strokeText(name, center.getxPos(), center.getyPos());
    }
}
