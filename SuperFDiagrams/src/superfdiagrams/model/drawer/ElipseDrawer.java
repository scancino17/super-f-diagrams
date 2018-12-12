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
import superfdiagrams.model.ElementState;
import static superfdiagrams.model.ElementState.NORMAL;
import superfdiagrams.model.GeometricUtilities;
import superfdiagrams.model.MainController;
import superfdiagrams.model.Vertex;
import superfdiagrams.model.primitive.Type;

/**
 *
 * @author Diego
 */
public class ElipseDrawer implements Drawer{
    private Type type;
    private Vertex center;
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

    @Override
    public void doDraw(GraphicsContext gc, List<Vertex> vertexes, String name, ElementState elementState
   ) {
        zoom = MainController.getController().getZoomFactor();
        this.doDraw(gc, vertexes, name, elementState
               , type);
    }

    public void doDraw(GraphicsContext gc, List<Vertex> vertexes, String name, ElementState elementState
           , Type type) {
        switch (type){
            case ATTRIBUTE_DERIVATE:
                derivateDraw(gc, vertexes, name, elementState
               );
                break;
            case ATTRIBUTE_KEY:
                keyDraw(gc, vertexes, name, elementState
               );
                break;
            case ATTRIBUTE_MULTIVALUATED:
                multivaluateDraw(gc, vertexes, name, elementState
               );
                break;
            case ATTRIBUTE_PARTIAL_KEY:
                partialKeyDraw(gc, vertexes, name, elementState
               );
                break;
            default:
                genericDraw(gc, vertexes, name, elementState
               );
                break;
        }
    }

    @Override
    public void doDrawVertex(GraphicsContext gc, List<Vertex> vextexes) {
        for (Vertex v : vextexes)
        {
            gc.setStroke(Color.RED);
            gc.setLineWidth(5);
            gc.strokeLine(v.getxPos() * zoom, v.getyPos()* zoom , v.getxPos() * zoom, v.getyPos() * zoom);
        }
    }
    
    /**
     * Funcion que crea un atributo derivado, deja un espacio vacio despues 
     * de dibujar 5 veces las lineas.
     * @param gc
     * @param vertexes
     * @param name
     * @param elementState
     *
     */
    private void derivateDraw(GraphicsContext gc, List<Vertex> vertexes, String name, ElementState elementState){
        gc.setStroke(setColor(elementState));
        
        int j = 0;
        int size = vertexes.size();
        for (int i = 0; i < vertexes.size(); i++) {
            gc.strokeLine(vertexes.get(i % size).getxPos() * zoom, vertexes.get(i % size).getyPos() * zoom,
                    vertexes.get((i + 1) % size).getxPos() * zoom, vertexes.get(( i +1 )% size).getyPos() * zoom);
            if (j == 2){
                i = i+1;
                j = 0;
            }
            j++;
        }
        Vertex center = GeometricUtilities.getCenterOfMass(vertexes);
        this.drawText(gc, name, center);
    }
    
    /**
     * Funcion que dibuja una elipse comun y corriente.
     * @param gc
     * @param vertexes
     * @param name
     * @param elementState
     *
     */
    private void genericDraw(GraphicsContext gc, List<Vertex> vertexes, String name, ElementState elementState){
        gc.setStroke(setColor(elementState));

        gc.setLineWidth(1);
        
        int size = vertexes.size();
        for(int i = 0; i < size; i++){
            gc.strokeLine(vertexes.get(i % size).getxPos() * zoom, vertexes.get(i % size).getyPos() * zoom,
                    vertexes.get((i + 1) % size).getxPos() * zoom, vertexes.get(( i +1 )% size).getyPos() * zoom);
        }
        
        gc.setStroke(Color.BLACK);
        
        Vertex center = GeometricUtilities.getCenterOfMass(vertexes);
        this.drawText(gc, name, center);
    }
    
    /**
     * Funcion que dibuja una elipse y una linea debajo del texto.
     * @param gc
     * @param vertexes
     * @param name
     * @param elementState
     *
     */
    private void keyDraw(GraphicsContext gc, List<Vertex> vertexes, String name, ElementState elementState){
        gc.setStroke(setColor(elementState));

        gc.setLineWidth(1);
        
        int size = vertexes.size();
        for(int i = 0; i < size; i++){
            gc.strokeLine(vertexes.get(i % size).getxPos() * zoom, vertexes.get(i % size).getyPos() * zoom,
                    vertexes.get((i + 1) % size).getxPos() * zoom, vertexes.get(( i +1 )% size).getyPos() * zoom);
        }
        
        gc.setStroke(Color.BLACK);
        
        Vertex center = GeometricUtilities.getCenterOfMass(vertexes);
        gc.setTextAlign(TextAlignment.CENTER);
        this.drawText(gc, name, center);
        gc.strokeLine(center.getxPos() * zoom - name.length() * 5, center.getyPos() * zoom + 5,
                center.getxPos() * zoom + name.length() * 5, center.getyPos() * zoom + 5);
    }
    
    private void multivaluateDraw(GraphicsContext gc, List<Vertex> vertexes, String name, ElementState elementState){
      
        gc.setStroke(setColor(elementState));

        gc.setLineWidth(3);
        
        int size = vertexes.size();
        for(int i = 0; i < size; i++){
            gc.strokeLine(vertexes.get(i % size).getxPos() * zoom, vertexes.get(i % size).getyPos() * zoom,
                    vertexes.get((i + 1) % size).getxPos() * zoom, vertexes.get(( i +1 )% size).getyPos() * zoom);
        }
        gc.setLineWidth(1);
        gc.setStroke(Color.WHITE);
        for(int i = 0; i < vertexes.size(); i++){
            gc.strokeLine(vertexes.get(i % size).getxPos() * zoom, vertexes.get(i % size).getyPos() * zoom,
                    vertexes.get((i + 1) % size).getxPos() * zoom, vertexes.get(( i +1 )% size).getyPos() * zoom);
        }
        
        gc.setStroke(Color.BLACK);
        
        gc.setStroke(Color.BLACK);
        Vertex center = GeometricUtilities.getCenterOfMass(vertexes);
        this.drawText(gc, name, center);
        
    }
    
    public void partialKeyDraw(GraphicsContext gc, List<Vertex> vertexes, String name, ElementState elementState){
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
            gc.strokeLine(vertexes.get(i % size).getxPos() * zoom, vertexes.get(i % size).getyPos() * zoom,
                    vertexes.get((i + 1) % size).getxPos() * zoom, vertexes.get(( i +1 )% size).getyPos() * zoom);
        }
        
        gc.setStroke(Color.BLACK);
        
        Vertex center = GeometricUtilities.getCenterOfMass(vertexes);
        this.drawText(gc, name, center);
        double inicio = center.getxPos() * zoom - name.length() * 5;
        
        for (int i = 0; i < name.length(); i++) {
            double fin = inicio + 5;
            gc.strokeLine(inicio, center.getyPos() * zoom + 5,
                fin, center.getyPos() * zoom + 5);
            inicio = inicio + 10;
        }
    }
    
    private void drawText(GraphicsContext gc, String label, Vertex center){
        gc.setFont(new Font(Font.getDefault().getSize() * zoom));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText(label, center.getxPos() * zoom, (center.getyPos() + 4)* zoom);
    }
    
    private Color setColor(ElementState state){
        switch(state){
            case HIGHLIGHTED:
                return Color.CORNFLOWERBLUE;
            case INVALID:
                return Color.CRIMSON;
            default:
                return Color.BLACK;
        }
    }
}
