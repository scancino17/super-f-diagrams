/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package superfdiagrams.model.drawer;
import java.util.List;
import javafx.scene.canvas.GraphicsContext;
import superfdiagrams.model.Diagram;
import superfdiagrams.model.Vertex;

/**
 *
 * @author sebca
 */
public class PolygonDrawer implements Drawer{
    //implementar
    /**
     * Funcion que dibuja los poligonos, tan solo usa los vertices creados en las
     * otras funciones y los une, al final se une el ultimo vertice de la lista 
     * con el primero
     * @param gc
     * @param vertexes
     * @param name 
     */
    @Override
    public void doDraw(GraphicsContext gc, List<Vertex> vertexes, String name) {
        int i;
        for (i = 0; i<vertexes.size()-1; i++){
            gc.strokeLine(vertexes.get(i).getxPos(), vertexes.get(i).getyPos()
                    , vertexes.get(i+1).getxPos(), vertexes.get(i+1).getyPos());
        }
        gc.strokeLine(vertexes.get(i).getxPos(), vertexes.get(i).getyPos(), 
                vertexes.get(0).getxPos(), vertexes.get(0).getyPos());
    }
    
}
