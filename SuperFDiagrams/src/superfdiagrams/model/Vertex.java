/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package superfdiagrams.model;

/**
 *
 * @author sebca
 */
public class Vertex {
    private double xPos;
    private double yPos;

    //Acá tengo los campos separados.
    //No sé si sea la opción definitiva, igual podria manejarse todos los
    //parámetros de los métodos con un array primitivo.
    // -Seba
    
    public Vertex(double xPos, double yPos) {
        this.xPos = xPos;
        this.yPos = yPos;
    }
    
    //Constructor que usa array primitivo.
    public Vertex(double[] pos){
        this.xPos = pos[0];
        this.yPos = pos[1];
    }

    public double getxPos() {
        return xPos;
    }

    public void setxPos(double xPos) {
        this.xPos = xPos;
    }

    public double getyPos() {
        return yPos;
    }

    public void setyPos(double yPos) {
        this.yPos = yPos;
    }
    
    public double[] getCoordinates(){
        return new double[]{xPos, yPos};
    }
    
    public void displace(double[] units){
       this.xPos += units[0];
       this.yPos += units[1];
    }
    
    @Override
    public String toString(){
        return "Vertice en (" + xPos + ", " + yPos + ")";
    }

}
