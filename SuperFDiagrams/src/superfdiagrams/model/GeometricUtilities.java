/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package superfdiagrams.model;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.input.MouseEvent;

/**
 *
 * @author sebca
 */
public class GeometricUtilities {
    /**
     *
     * @param p - Lista de Vertex que conforman el poligono
     * @param q - Vertex a verificar si está contenido
     * @return true o false en caso de que el Vertex esté contenido o no (no verifica si está en los limites del polígono)
     */
    public static boolean PointInPolygon(List<Vertex> p, Vertex q)
    {
        boolean c = false;
        for (int i = 0; i < p.size(); i++)
        {
            int j = (i+1)%p.size();
            if ((p.get(i).getyPos() <= q.getyPos() && q.getyPos() < p.get(j).getyPos() ||
                    p.get(j).getyPos() <= q.getyPos() && q.getyPos() < p.get(i).getyPos()) &&
                    q.getxPos() < p.get(i).getxPos() + (p.get(j).getxPos() - p.get(i).getxPos()) *
                            (q.getyPos() - p.get(i).getyPos()) / (p.get(j).getyPos() - p.get(i).getyPos()))
                c = !c;
        }
        return c;
    }
    
    // solo para probar... recorre todos los elementos y ve si el Vertex p está pertenece a alguno
    public static ElementWrapper checkColition(Vertex p)
    {
        for(ElementWrapper element: DiagramController.getController().fetchElements())
            if(PointInPolygon(element.getVertexes(), p))
                return element;

        return null;
    }
    
    public static ElementWrapper checkColition(double x, double y){
        return checkColition(VertexGenerator.generateVertex(x  , y ));
    }
    
    public static Vertex getCenterOfMass(List<Vertex> polygon){
        float x = 0;
        float y = 0;
        
        for(Vertex v: polygon){
            x+= v.getxPos();
            y+= v.getyPos();
        }
        
        x = x / polygon.size();
        y = y / polygon.size();
        
        return new Vertex(Math.round(x), Math.round(y));
    }
    
    public static double vertexDistance(Vertex v1, Vertex v2){
        double xDistance = v1.getxPos() - v2.getxPos();
        double yDistance = v1.getyPos() - v2.getyPos();
        return Math.sqrt(Math.pow(xDistance, 2) + Math.pow(yDistance, 2));
    }
    
    public static List<Vertex> nearestVertexes(List<Vertex> polygon1,List<Vertex> polygon2){
        Double lesserDistance = null;
        List<Vertex> nearestVertexes = null;
        double distance;
        
        for(Vertex v1: polygon1){
            for(Vertex v2: polygon2){
                distance = vertexDistance(v1, v2);
                if ((lesserDistance == null || distance < lesserDistance) && !v1.isUsed()){
                    lesserDistance = distance;
                    nearestVertexes = new ArrayList<>();
                    nearestVertexes.add(v1);
                    nearestVertexes.add(v2);
                }
            }
        }
        
        if (nearestVertexes != null && !nearestVertexes.isEmpty())
            for(Vertex v: nearestVertexes)
                v.setUsed(true);
        
        return nearestVertexes;
    }
    
    public static Vertex midPoint(Vertex a, Vertex b){
        return new Vertex((a.getxPos() + b.getxPos()) / 2, (a.getyPos() + b.getyPos()) /2);
    }
}
