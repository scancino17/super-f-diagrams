/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package superfdiagrams.model;

import superfdiagrams.model.primitive.Union;
import static superfdiagrams.model.GeometricUtilities.getCenterOfMass;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase encargada de generar una Lista que contenga las instancias de Vertex
 * necesarias para dibujar un polígono.
 * @author sebca
 */
public class VertexGenerator {
    /**
     * Constructor de clase privado para evitar la instanceación.
     */
    private VertexGenerator(){}
    
    /**
     * Método principal de la clase. Este método se encarga de recibir una serie
     * de parámetros: cantidad de vertices a ser generados, tamaño de la figura y
     * centro de la figura, para regresar una lista de instancias Vertex a ser
     * asginadas a un ElementWrapper.
     * <p>
     * Cabe destacar que para los casos que sean menores a tres, el método
     * retornará los vértices necesarios para dibujar un rombo.
     * 
     * @param number Cantidad de Vertices a ser generado.
     * @param size Tamaño proporcional de la figura.
     * @param center Centro de la figura.
     * @return Lista que contiene vertexes para a ser utilizados.
     */
    public static List<Vertex> generateVertexes(int number, double size, Vertex center){
        return generateVertexes(number, size, size, center);
    }
    
    /**
     * @author Sebastián Cancino.
     * @param sides Lados de la figura
     * @param xSize Tamaño en x
     * @param ySize Tamaño en Y
     * @param center Centro de la figura
     * @return Poligono creado por lista de Vertex
     */
    public static List<Vertex> generateVertexes(int sides,
                                                double xSize,
                                                double ySize,
                                                Vertex center)
    {
        List<Vertex> vertexes = new ArrayList<>();
        
        if (sides < 3)
            sides = 4;
        
        double[] denormalized = denormalize(calculateVertexes(sides), sides, xSize, ySize);
        Vertex vertex;
        
        for(int i =0; i < sides*2; i+=2){
            vertex = generateVertex(denormalized[i], denormalized[i+1]);
            vertex.displace(center.getCoordinates());
            vertexes.add(vertex);
        }
        
        return vertexes;
    }
    
    public static List<Vertex> generateEllipse(int number, double size, Vertex center){
        return generateEllipse(number, size, size / 1.75, center);
    }
    
    public static List<Vertex> generateEllipse(int sides,
                                               double xSize,
                                               double ySize,
                                               Vertex center)
    {
        List<Vertex> vertexes = new ArrayList<>();
        
        if (sides < 3)
            sides = 4;
        
        double[] denormalized = denormalize(calculateEllipseVertexes(sides), sides, xSize, ySize);
        Vertex vertex;
        
        for(int i =0; i < sides*2; i+=2){
            vertex = generateVertex(denormalized[i], denormalized[i+1]);
            vertex.displace(center.getCoordinates());
            vertexes.add(vertex);
        }
        
        return vertexes;
    }
    
    /**
     * Método utilizado para obtener rectángulos.
     * @param size int que representa proporción de tamaño del rectángulo
     * @param center Vertex que representa el centro de la figura.
     * @return 
     */
    public static List<Vertex> generateRectangle(double size, Vertex center){
        return generateRectangle(size, size * 0.5, center);
    }
    
    public static List<Vertex> generateRectangle(double xSize,
                                                 double ySize,
                                                 Vertex center)
    {
        List<Vertex> vertexes = new ArrayList<>();
        
        double[] normalized = new double[]{-1, -1,
                                           -1,  1,
                                            1,  1,
                                            1, -1};
        
        double[] denormalized = denormalize(normalized, 4, xSize, ySize);
        
        Vertex vertex;
        for (int i = 0; i<8; i+=2){
            vertex = generateVertex(denormalized[i], denormalized[i+1]);
            vertex.displace(center.getCoordinates());
            vertexes.add(vertex);
        }
        
        return vertexes;
    }
    
    /**
     * Método privado utilizado para generar el array primitivo con los valores 
     * a normalizar. El array entregado es de tamaño igual al doble del numero
     * entregado como parámetro al método. Estos valores representan x, y para
     * ser utilizados en la generación de vértices. 
     * @param number int que representa el numero de vértices a entregar.
     * @return Array que contiene valores usados en la creación de vértices.
     */
    private static double[] calculateVertexes(int number){
        double[] points = new double[number*2];
        
        double constant = 2*Math.PI/number;
        
        for(int i = 0, j=0; i<number*2; i+=2, j++){
            points[i]   =   Math.sin(constant*j);
            points[i+1] = - Math.cos(constant*j);
        }
        
        return points;
    }
    
    private static double[] calculateEllipseVertexes(int number){
        double[] points = new double[number*2];
        
        double constant = 2*Math.PI/number;
        
        for(int i = 0, j=0; i<number*2; i+=2, j++){
            points[i]   =   Math.sin(constant*j);
            points[i+1] =   Math.cos(constant*j);
        }
        
        return points;
    }
    
    /**
     * Método privado. Se encarga de crear un Array que contine los puntos
     * necesarios para generar los Vertices. Este utiliza los Arrays estaticos
     * de la clase como base, multiplicando estos por el tamaño dado.
     * @param normalized Array con valores a ser usados como base.
     * @param number Cantidad de puntos a ser creados.
     * @param size Multiplicador usado para generar los puntos.
     * @return Array que continene valores para generar instancias Vertex.
     */
    private static double[] denormalize(double[] normalized, int number, double size){
        return denormalize(normalized, number, size, size);
    }
    
    public static double[] denormalize(double[] normalized, int sides, double xSize, double ySize)
    {
        double[] denormalized = new double[sides * 2];
        
        for(int i = 0; i < sides * 2 - 1; i+=2){
            denormalized[i] = normalized[i] * xSize;
            denormalized[i+1] = normalized[i+1] * ySize;
        }
        
        return denormalized;
    }
    /**
     * Dado un par de numeros, la funcion se encarga de instanciar
     * la clase Vertex y retornar el objeto creado.
     * Utilizado por el método generateVertexes().
     * @param xPos Posicion en x del vertice.
     * @param yPos Posciion en y del vertice.
     * @return Instancia de la clase Vertex
     */
    public static Vertex generateVertex(double xPos, double yPos){
        return new Vertex(new double[]{xPos, yPos});
    }
    
    /**
     * Funcion que crea un arreglo de distancia entre los vertices de la entidad
     * con la de la relacion.
     * @param relation
     * @param index
     * @return retorna el vertice de la entidad mas cercano al de la relacion
     */
    public static Vertex determinateVertex(Element relation, int index){
        ArrayList<Double> distances = new ArrayList();
        for (int j = 0; j < 4; j++) {
            distances.add(twoPointsDistance(relation.getVertexes().get(index).getxPos()
                    , relation.getVertexes().get(index).getyPos(), 
                    relation.getPrimitive().getChildren().get(index).getVertexes().get(j).getxPos()
                    , relation.getPrimitive().getChildren().get(index).getVertexes().get(j).getyPos()));
        }
        return relation.getPrimitive().getChildren().get(index).getVertexes().get(minorIndex(distances));
    }
    
    public Vertex determinateVertex(Element relation, int index, boolean xd){
        return relation.getPrimitive().getChildren().get(index).getVertexes().get(index+2);
    }
    
    /**
     * @author Sebastian Cancino
     * @param related
     * @return 
     */
    public static List<Vertex> getAgregationVertexes(List<Element> related) {
        Double minX = null,
               minY = null,
               maxX = null,
               maxY = null;
        
        for(Element el : related)
            for(Vertex v : el.getVertexes()){
                double x = v.getxPos();
                double y = v.getyPos();
                
                if(minX == null || x < minX)
                    minX = x;
                if(maxX == null || x > maxX)
                    maxX = x;
                
                if(minY == null || y < minY)
                    minY = y;
                if(maxY == null || y > maxY)
                    maxY = y;
            }
        
        minX -= 10;
        maxX += 10;
        minY -= 30;
        maxY += 10;
        
        List<Vertex> vertexes = new ArrayList<>();
        vertexes.add( new Vertex(minX, minY) );
        vertexes.add( new Vertex(minX, maxY) );
        vertexes.add( new Vertex(maxX, maxY) );
        vertexes.add( new Vertex(maxX, minY) );
        
        return vertexes;
    }
    
    /**
     * Funcion que solo devuelve la distancia entre 2 puntos
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return 
     */
    
    public static double twoPointsDistance(double x1, double y1, double x2,double y2){
        return Math.hypot(x2-x1, y2-y1);
    }
    
    /**
     * Funcion que revisa cual de las distancias fue la menor para saber que 
     * vertice es el mas cercano al de la relacion.
     * @param distances
     * @return 
     */
    public static int minorIndex(ArrayList<Double> distances){
        double minor = distances.get(0);
        int j= 0;
        for (int i = 0; i < 4; i++) {
            if (distances.get(i) < minor){
                minor = distances.get(i);   
                j = i;
            }
        }
        return j;
    }
    
    public static void recalculateVertexes(Element element, double x, double y){
        recalculateVertexes(element.getVertexes(), new Vertex(x, y));
        element.getCenterVertex().moveTo(x, y);
    }
    
    public static void recalculateVertexes(Element element, Vertex newCenter){
        recalculateVertexes(element, newCenter.getxPos(), newCenter.getyPos());
        element.getCenterVertex().moveTo(newCenter.getxPos(), newCenter.getyPos());
    }
    
    private static void recalculateVertexes(List<Vertex> vertexes, 
                                    Vertex newCenter)
    {
        Vertex center = getCenterOfMass(vertexes);
        
        for(Vertex v: vertexes){
            v.moveTo(v.getxPos() - center.getxPos() + newCenter.getxPos()
                    ,v.getyPos() - center.getyPos() + newCenter.getyPos());
        }
    }
    
    public static void recalculateNearestVertexes(List<Element> unions){
        Element parent = null;
        for(Element union : unions){
            Element selected = ((Union)union.getPrimitive()).getParent();
            if (selected != parent){
                parent = selected;
                
                for(Vertex v:parent.getVertexes())
                    v.setUsed(false);
                
                
                for(Element unionInParent: parent.getPrimitive().getChildren()){
                    if (unionInParent.getPrimitive() instanceof Union){
                        unionInParent.setVertexes(GeometricUtilities.nearestVertexes
                            (parent.getVertexes(), ((Union)unionInParent.getPrimitive()).getChild().getCenterVertex()));
                    }
                }
            }
        }
    }
    
    public static List<Vertex> cloneVertex(List<Vertex> toClone){
        List<Vertex> newVertexes = new ArrayList<>();
        for (Vertex original: toClone){
            newVertexes.add(new Vertex(original.getCoordinates()));
        }
        return newVertexes;
    }
    
    public static List<Vertex> gatherCenterDif(Element central, List<Element> related){
        List<Vertex> centers = new ArrayList<>();
        
        double cx = central.getCenterVertex().getxPos();
        double cy = central.getCenterVertex().getyPos();
        double rx, ry; 
        
        for(Element r: related){ 
            /*if(r instanceof ComplexElement)
            r.setCenterVertex(getCenterOfMass(r.getVertexes()));*/
            rx = r.getCenterVertex().getxPos();
            ry = r.getCenterVertex().getyPos();
            
            centers.add(new Vertex(rx - cx, ry - cy));
        }
        
        return centers;
    }
    
    public static void recalculateComplexElement(Element complex, List<Element> related, double x, double y){
        List<Vertex> centers = gatherCenterDif(complex, related);
        double cx, cy;
        Vertex rCenter;
        
        for(int i = 0; i < centers.size(); i++){
            Element e = related.get(i);
            if (e.getPrimitive() instanceof Union)
                continue;
            
            rCenter = centers.get(i);
            cx = rCenter.getxPos();
            cy = rCenter.getyPos();
            
            if(e instanceof ComplexElement)
                recalculateComplexElement(e, ((ComplexElement) e).getComposite(), x + cx, y + cy);
            else
                recalculateVertexes(e, x + cx , y + cy);
        }
        recalculateVertexes(complex, x, y);
    }
    
    public static void morphComplex(ComplexElement element){
        List<Vertex> newVertex = getAgregationVertexes(element.getComposite());
        List<Vertex> cmpVertex = element.getVertexes();
        
        for(int i = 0 ; i < cmpVertex.size() ; i++){
            Vertex v0 = cmpVertex.get(i);
            Vertex v1 = newVertex.get(i);
            v0.moveTo(v1);
        }
        
        element.getCenterVertex().moveTo(getCenterOfMass(element.getVertexes()));
    }
    
    public static void morphContainedComplex(ComplexElement element){
        
        morphComplex(element);
        for(Element e : element.getComposite())
            if(e instanceof ComplexElement)
                morphContainedComplex((ComplexElement) e);
    }
    
    /**
     * Método que entregado un complexElement, busca si existe otro
     * ComplexElement que lo contenga. De ser el caso, repite la operación,
     * de no ser el caso, llama al método morphContainedComplex para transformar
     * los ComplexElements
     * @author Sebastian Cancino
     * @param element ComplexElement a ser transformado.
     */
    public static void backwardsComplexMorphing(ComplexElement element){
        ComplexElement parent = Finder.findComplexContained(element);
        if(parent != null)
            backwardsComplexMorphing(parent);
        else
            morphContainedComplex(element);
    }
}
