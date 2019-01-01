/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package superfdiagrams.model.drawer;

import java.util.List;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import superfdiagrams.model.ElementState;
import superfdiagrams.model.Vertex;
import superfdiagrams.model.primitive.Type;

/**
 *
 * @author sebca
 */
public interface Drawer {
    public void doDraw(GraphicsContext gc, List<Vertex> vertexes, String name, ElementState highlighted);
    public void doDrawVertex(GraphicsContext gc, List<Vertex> vextexes);
    public void setType(Type type);
    public Type getType();
    /**
     * Método encargado de pintar el fondo. Basado en algoritmo point-in-polygon.
     * Dado un polígono concavo, el algoritmo traza lineas horizontales
     * y registra todos los puntos en que la línea atraviesa el polígono.
     * Las líneas son trazadas en un cuadrado que va desde el menor x hasta el
     * mayor x, y del menor y hasta el mayor y del polígono.
     * Luego de haber registrado todos los puntos, el algoritmo rellena el
     * polígono dibujando líneas entre pares de puntos por medio, siguiendo el
     * mismo principio que permite determinar si un punto se encuentra dentro
     * del polígono o no.
     * @author Sebastian Cancino, Ignacio Martinez.
     * @param gc GraphicsContext sobre el que es dibujado
     * @param vertexes Lista que continene los vertex que forman el polígono
     * @param zoom Constante de zoom
     */
    default public void doDrawBackground(GraphicsContext gc,
                                         List<Vertex> vertexes,
                                         double zoom)
    {
        //Determinar mínimos y máximos del polígono
        double top, bot, left, right;
        top = bot = left = right = 0;
        if (vertexes == null || vertexes.isEmpty()) return;
        for(Vertex v : vertexes){
            top   = Math.min(top,   v.getyPos());
            bot   = Math.max(bot,   v.getyPos());
            left  = Math.min(left,  v.getxPos());
            right = Math.max(right, v.getxPos());
        }
        
        int nodes, i, j;
        double pixelY;
        double nodeX[] = new double[vertexes.size()];
        double vix, viy, vjx, vjy;
        //Por cada "pixel", en realidad este pixel se ve influenciado por la
        //constante del zoom en forma inversamente proporcional.
        //pixelY =  pixelY + (1 * (1/zoom)) => pixelY+=(1/zoom)
        for (pixelY = top; pixelY<bot; pixelY+=(1/zoom)){
            nodes = 0;
            j = vertexes.size() - 1;
            
            //determina los puntos al trazar una linea horizontal. 
            for(i = 0; i < vertexes.size(); i++){
                vix = vertexes.get(i).getxPos();
                viy = vertexes.get(i).getyPos();
                vjx = vertexes.get(j).getxPos();
                vjy = vertexes.get(j).getyPos();
                
                if ( viy < pixelY && vjy >= pixelY
                  || vjy < pixelY && viy >= pixelY )
                    nodeX[nodes++] = ( vix + (pixelY - viy) / ( vjy - viy) * (vjx - vix));
                
                j = i;
            }
            
            //Se dibuja una línea entre cada par de puntos
            for(i = 0; i < nodes; i+=2){
                if (nodeX[i  ] >= right) break;
                if (nodeX[i+1] > left){
                    if (nodeX[i  ] < left ) nodeX[i]   = left;
                    if (nodeX[i+1] > right) nodeX[i+1] = right;
                    gc.setStroke(Color.WHITE);
                    gc.setLineWidth(2);
                    gc.strokeLine(nodeX[i]  * zoom,
                                  pixelY    * zoom,
                                  nodeX[i+1]* zoom,
                                  pixelY    * zoom);
                }
            }
        }
    }
}
