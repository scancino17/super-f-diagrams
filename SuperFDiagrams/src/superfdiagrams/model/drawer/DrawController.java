/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package superfdiagrams.model.drawer;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.canvas.GraphicsContext;

/**
 *
 * @author sebca
 */
public class DrawController {
    private static DrawController dc;
    private List<Drawable> buffer;
    private GraphicsContext gc;
    private boolean shouldDrawVertex;
    
    private DrawController(){
        this.buffer = new ArrayList<>();
        this.shouldDrawVertex = false;
    }
    
    public static DrawController getDrawController(){
        if(dc == null)
            dc = new DrawController();
        return dc;
    }
    
    public void setGraphicsContext(GraphicsContext gc){
        this.gc = gc;
    }
    
    public void toggleDrawVertex(){
        this.shouldDrawVertex = !shouldDrawVertex;
    }  
    
    public void addToBuffer(Drawable toDraw){
        buffer.add(toDraw);
    }
    
    public void removeFromBuffer(Drawable toRemove){
        buffer.remove(toRemove);
    }
    
    public boolean isBufferEmpty(){
        return this.buffer.isEmpty();
    }
    
    public void eraseBuffer(){
        this.buffer = new ArrayList<>();
    }
    
    public void doDrawLoop(){
        if (gc == null){
            System.err.println("Error. GraphicsContext no entregado.");
            return;
        }
        
        for (Drawable toDraw: buffer){
            try {
                toDraw.draw(gc);
                if(shouldDrawVertex) 
                    toDraw.drawVertex(gc);
            } catch (Exception e) {
                System.err.println("Error en la rutina de dibujo. Entregando error: ");
                System.err.println(e.toString());
                e.printStackTrace();
            }
        }
    }
}
