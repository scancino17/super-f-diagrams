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
    private int xPos;
    private int yPos;

    //Acá tengo los campos separados.
    //No sé si sea la opción definitiva, igual podria manejarse todos los
    //parámetros de los métodos con un array primitivo.
    // -Seba
    
    public Vertex(int xPos, int yPos) {
        this.xPos = xPos;
        this.yPos = yPos;
    }
    
    //Constructor que usa array primitivo.
    public Vertex(int[] pos){
        this.xPos = pos[0];
        this.yPos = pos[1];
    }

    public int getxPos() {
        return xPos;
    }

    public void setxPos(int xPos) {
        this.xPos = xPos;
    }

    public int getyPos() {
        return yPos;
    }

    public void setyPos(int yPos) {
        this.yPos = yPos;
    }
    
    public int[] getCoordinates(){
        return new int[]{xPos, yPos};
    }
    
    public void displace(int[] units){
       this.xPos += units[0];
       this.yPos += units[1];
    }
    
    @Override
    public String toString(){
        return "Vertice en (" + xPos + ", " + yPos + ")";
    }

}
