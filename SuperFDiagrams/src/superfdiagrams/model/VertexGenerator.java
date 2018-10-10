/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package superfdiagrams.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase encargada de generar una Lista que contenga las instancias de Vertex
 * necesarias para dibujar un polígono.
 * @author sebca
 */
public class VertexGenerator {
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
    public static List<Vertex> generateVertexes(int number, int size, Vertex center){
        List<Vertex> vertexes = new ArrayList<>();
        
        if (number < 3)
            number = 4;
        
        int[] denormalized = denormalize(calculateVertexes(number), number, size);
        Vertex vertex;
        
        for(int i =0; i < number*2; i+=2){
            vertex = generateVertex(denormalized[i], denormalized[i+1]);
            vertex.displace(center.getCoordinates());
            vertexes.add(vertex);
        }
        
        return vertexes;
    }
    /**
     * Método utilizado para obtner rectángulos.
     * @param size int que representa proporción de tamaño del rectángulo
     * @param center Vertex que representa el centro de la figura.
     * @return 
     */
    public static List<Vertex> generateRectangle(int size, Vertex center){
        List<Vertex> vertexes = new ArrayList<>();
        
        double[] normalized = new double[]{-1, -0.5, -1, 0.5, 1, 0.5, 1, -0.5};
        int[] denormalized = denormalize(normalized, 4, size);
        
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
            System.out.println("Hecho " + j + " veces!");
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
    private static int[] denormalize(double[] normalized, int number, int size){
        int[] denormalized = new int[number * 2];
        
        for(int i = 0; i < number * 2 - 1; i+=2){
            denormalized[i] = (int) Math.round(normalized[i] * size);
            denormalized[i+1] = (int) Math.round(normalized[i+1] * size);
        }
        
        return denormalized;
    }
    
    /**
     * Método privado. Dado un par de numeros, la funcion se encarga de instanciar
     * la clase Vertex y retornar el objeto creado.
     * Utilizado por el método generateVertexes().
     * @param xPos Posicion en x del vertice.
     * @param yPos Posciion en y del vertice.
     * @return Instancia de la clase Vertex
     */
    public static Vertex generateVertex(int xPos, int yPos){
        return new Vertex(new int[]{xPos, yPos});
    }
}
