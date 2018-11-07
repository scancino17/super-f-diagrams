/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package superfdiagrams.model.drawer;

import java.util.List;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import superfdiagrams.model.GeometricUtilities;
import superfdiagrams.model.Vertex;
import superfdiagrams.model.VertexGenerator;

/**
 *
 * @author Diego
 */
public class ElipseDrawer implements Drawer{
    private int type;
    private Vertex center;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
    
    public void setCenter(Vertex center){
        this.center = center;
    }

    @Override
    public void doDraw(GraphicsContext gc, List<Vertex> vertexes, String name, boolean highlighted) {
        this.doDraw(gc, vertexes, name, highlighted, type);
    }

    public void doDraw(GraphicsContext gc, List<Vertex> vertexes, String name, boolean highlighted, int type) {
        switch (type){
            case 1:
                derivateDraw(gc, vertexes, name, highlighted);
                break;
            case 2:
                genericDraw(gc, vertexes, name, highlighted);
                break;
            case 3:
                keyDraw(gc, vertexes, name, highlighted);
                break;
            case 4:
                genericDraw(gc, vertexes, name, highlighted);
                break;
            case 5:
                multivaluateDraw(gc, vertexes, name, highlighted);
                break;
            case 6:
                keyDraw(gc, vertexes, name, highlighted);
                break;       
        }
    }

    @Override
    public void doDrawVertex(GraphicsContext gc, List<Vertex> vextexes) {
        for (Vertex v : vextexes)
        {
            gc.setStroke(Color.RED);
            gc.setLineWidth(5);
            gc.strokeLine(v.getxPos(), v.getyPos() , v.getxPos(), v.getyPos());
        }
    }
    
    /**
     * Funcion que crea un atributo derivado, deja un espacio vacio despues 
     * de dibujar 5 veces las lineas.
     * @param gc
     * @param vertexes
     * @param name
     * @param highlighted 
     */
    private void derivateDraw(GraphicsContext gc, List<Vertex> vertexes, String name, boolean highlighted){
        if(!highlighted){
            gc.setStroke(Color.BLACK);
        } else{
            gc.setStroke(Color.CORNFLOWERBLUE);
        }
        int j = 0;
        int size = vertexes.size();
        for (int i = 0; i < vertexes.size(); i++) {
            gc.strokeLine(vertexes.get(i % size).getxPos(), vertexes.get(i % size).getyPos(),
                    vertexes.get((i + 1) % size).getxPos(), vertexes.get(( i +1 )% size).getyPos());
            if (j == 2){
                i = i+1;
                j = 0;
            }
            j++;
        }
        Vertex center = GeometricUtilities.getCenterOfMass(vertexes);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.strokeText(name, center.getxPos(), center.getyPos());
    }
    
    /**
     * Funcion que dibuja una elipse comun y corriente.
     * @param gc
     * @param vertexes
     * @param name
     * @param highlighted 
     */
    private void genericDraw(GraphicsContext gc, List<Vertex> vertexes, String name, boolean highlighted){
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
    
    /**
     * Funcion que dibuja una elipse y una linea debajo del texto.
     * @param gc
     * @param vertexes
     * @param name
     * @param highlighted 
     */
    private void keyDraw(GraphicsContext gc, List<Vertex> vertexes, String name, boolean highlighted){
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
        gc.strokeLine(center.getxPos()-50, center.getyPos()+5, center.getxPos()+50, center.getyPos()+5);
    }
    
    private void multivaluateDraw(GraphicsContext gc, List<Vertex> vertexes, String name, boolean highlighted){
      
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
        
        Vertex center = GeometricUtilities.getCenterOfMass(vertexes);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.strokeText(name, center.getxPos(), center.getyPos());
        
    }

}