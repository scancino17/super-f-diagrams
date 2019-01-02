/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package superfdiagrams.model.drawer;

import java.util.List;
import javafx.scene.canvas.GraphicsContext;
import superfdiagrams.model.Element;

/**
 *
 * @author sebca
 */
public class DrawController {
    private static DrawController dc;
    private GraphicsContext gc;
    private boolean shouldDrawVertex;
    
    private DrawController(){
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
    
    public void doDrawLoop(List<Element> elements){
        if(gc == null){
            System.err.println("Error. GraphicsContext no entregado.");
            return;
        }
        
        for (Element toDraw: elements){
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
