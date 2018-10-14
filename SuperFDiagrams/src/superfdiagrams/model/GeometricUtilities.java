/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package superfdiagrams.model;

import java.util.List;

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
        return checkColition(VertexGenerator.generateVertex((int) x, (int) y));
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
}
