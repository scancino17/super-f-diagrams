/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package superfdiagrams.model;

import java.util.List;
import java.lang.Math;

/**
 *
 * @author sebca
 */
public class GeometricUtilities {


    //algunas variavles
    private static final double INF = 1e100;
    private static final double EPS = 1e-12;

    /**
     * Suma dos vertex
     * @param p
     * @param q
     * @return un nuevo Vertex con  la suma de los dos Vertex
     */
    public static Vertex sum(Vertex p, Vertex q) { return  new Vertex(p.getxPos() + q.getxPos() , p.getyPos() + q.getyPos());}

    /**
     * Resta dos vertex
     * @param p
     * @param q
     * @return un nuevo Vertex con la resta de dos Vertex
     */
    public static Vertex minus(Vertex p, Vertex q) { return  new Vertex(p.getxPos() - q.getxPos() , p.getyPos() - q.getyPos());}

    /**
     * Multiplica un vertex por una constante
     * @param p
     * @param c
     * @return un nuevo Vertex multiplicado por una constante
     */
    public static Vertex mul(Vertex p, int c) { return new Vertex(p.getxPos() * c , p.getyPos()*c);}

    /**
     * Divide un vertex por una constante
     * @param p
     * @param c
     * @return un nuevo Vertex dividido por una constante
     */
    public static Vertex div(Vertex p, int c) { return new Vertex(p.getxPos() / c , p.getyPos()/c);}


    /**
     * Pseudo producto punto
     * @param p
     * @param q
     * @return int - p.x * q.x + p.y*q.y
     */
    public static int dot(Vertex p, Vertex q)     { return p.getxPos()*q.getxPos()+p.getyPos()*q.getyPos(); }


    /**
     * Pseudo producto cruz
     * @param p
     * @param q
     * @return int - p.x * q.x - p.y*q.y
     */
    public static int cross(Vertex p, Vertex q)   { return p.getxPos()*q.getyPos()-p.getyPos()*q.getxPos(); }

    /**
     * @param p
     * @param q
     * @return Distancia al cuadrado entre dos Vertex
     */
    public static int dist2(Vertex p, Vertex q)   { return dot(minus(p,q),minus(p,q)); }

    /**
     * @param p
     * @param q
     * @return Distancia entre dos puntos
     */
    public static double dist(Vertex p, Vertex q)   { return Math.sqrt(dist2(p,q)); }

    // poryecta punto c sobre linea atraves de a -> b
    // asumiendo a != b
    public static Vertex ProjectPointLine(Vertex a, Vertex b, Vertex c)
    {
        return sum(a , mul(minus(b,a) ,dot(minus(c,a), minus(b,a))/dot(minus(b,a), minus(b,a))));
    }

    // proyecta punto c sobre la linea a -> b
    public static Vertex ProjectPointSegment(Vertex a, Vertex b, Vertex c) {
        int r = dot(minus(b,a),minus(b,a));
        if (Math.abs(r) < EPS) return a;
        r = dot(minus(c,a), div(minus(b,a),r));
        if (r < 0) return a;
        if (r > 1) return b;
        return sum(a , mul(minus(b,a),r));
    }


    //distancia entre el punto c a la linea a -> b
    public static double distancePointSegment(Vertex a, Vertex b, Vertex c)
    {
        return Math.sqrt(dist2(c, ProjectPointSegment(a, b, c)));
    }

    /**
     * @param a
     * @param b
     * @param c
     * @param d
     * @return Si las lineas a -> b y c -> d son paralelas
     */
    public static boolean LinesParallel(Vertex a, Vertex b, Vertex c, Vertex d)
    {
        return Math.abs(cross(minus(b,a), minus(c,d))) < EPS;
    }

    /**
     * @param a
     * @param b
     * @param c
     * @param d
     * @return Si las lineas a -> b y c -> d son colineales
     */
    public static boolean LinesCollinear(Vertex a, Vertex b, Vertex c, Vertex d)
    {
        return LinesParallel(a, b, c, d)
                && Math.abs(cross(minus(a,b), minus(a,c))) < EPS
                && Math.abs(cross(minus(c,d), minus(c,a))) < EPS;
    }


    /**
     * Verifica si dos lineas se intersecta
     * @param a
     * @param b
     * @param c
     * @param d
     * @return si la linea a -> b se intersecta con la linea c -> d
     */
    public static boolean SegmentsIntersect(Vertex a, Vertex b, Vertex c, Vertex d)
    {
        if (LinesCollinear(a, b, c, d))
        {
            if (dist2(a, c) < EPS || dist2(a, d) < EPS ||
                    dist2(b, c) < EPS || dist2(b, d) < EPS) return true;
            if (dot(minus(c,a), minus(c,b)) > 0 && dot(minus(d,a), minus(d,b)) > 0 && dot(minus(c,b),minus(d,b)) > 0)
                return false;
            return true;
        }
        if (cross(minus(d,a), minus(b,a)) * cross(minus(c,a), minus(b,a)) > 0) return false;
        if (cross(minus(a,c), minus(d,c)) * cross(minus(b,c), minus(d,c)) > 0) return false;
        return true;
    }

    /**
     * Calcula el Vertex de la intersección entre dos lineas
     * @param a
     * @param b
     * @param c
     * @param d
     * @return Vertex de la interseccion de las lineas formadas por a -> b y c -> d
     */
    public static Vertex ComputeLineIntersection(Vertex a, Vertex b, Vertex c, Vertex d)
    {
        b=minus(b,a); d=minus(c,d); c=minus(c,a);
        assert(dot(b, b) > EPS && dot(d, d) > EPS);
        return sum(a , mul(b,cross(c, d)/cross(b, d)));
    }


    // Determina si el punto está en el la linea formada por el poligono
    public static boolean PointOnPolygon(List<Vertex> p, Vertex q) {
        for (int i = 0; i < p.size(); i++)
            if (dist2(ProjectPointSegment(p.get(i), p.get((i+1)%p.size()), q), q) < EPS)
                return true;
        return false;
    }


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

    public static Vertex RotateCCW90(Vertex p)   { return new Vertex(-1*p.getyPos(),p.getxPos()); }
    public static Vertex RotateCW90(Vertex p)    { return new Vertex(p.getyPos(),-1*p.getxPos()); }
    public static Vertex RotateCCW(Vertex p, double t)
    {
        return new Vertex((int)Math.round(p.getxPos()*Math.cos(t)-p.getyPos()*Math.sin(t)),
                          (int)Math.round(p.getxPos()*Math.sin(t)+p.getyPos()*Math.cos(t)));
    }


    private static double ComputeSignedArea(List<Vertex> p)
    {
        double area = 0;
        for(int i = 0; i < p.size(); i++)
        {
            int j = (i+1) % p.size();
            area += p.get(i).getxPos()*p.get(j).getyPos() - p.get(j).getxPos()*p.get(i).getyPos();
        }
        return (area / 2.0);
    }

    /**
     * @param p
     * @return Area de un polígono
     */
    public static double ComputeArea(List<Vertex> p)
    {
        return Math.abs(ComputeSignedArea(p));
    }


    //creo que es equivalente al método de getCenterOfMass habría que verificar
    /**
     * @param p
     * @return Punto de central de un polígono
     */
    public static Vertex ComputeCentroid(List<Vertex> p)
    {
        Vertex c = new Vertex(0,0);
        double scale = 6.0 * ComputeSignedArea(p);
        for (int i = 0; i < p.size(); i++){
            int j = (i+1) % p.size();
            c = sum(c , mul(sum(p.get(i), p.get(j)),p.get(i).getxPos()*p.get(j).getyPos() - p.get(j).getxPos()*p.get(i).getyPos()));
        }
        return div(c , (int)scale);
    }

    /**
     * @param p
     * @return Verifica si un poligono está en orden CW o CCW
     */
    public static boolean IsSimple(List<Vertex> p) {
        for (int i = 0; i < p.size(); i++) {
            for (int k = i+1; k < p.size(); k++) {
                int j = (i+1) % p.size();
                int l = (k+1) % p.size();
                if (i == l || j == k) continue;
                if (SegmentsIntersect(p.get(i), p.get(j), p.get(k), p.get(l)))
                    return false;
            }
        }
        return true;
    }
}
