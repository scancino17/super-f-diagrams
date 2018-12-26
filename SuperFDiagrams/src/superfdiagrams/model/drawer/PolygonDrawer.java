/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package superfdiagrams.model.drawer;
import java.util.List;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import superfdiagrams.model.*;
import superfdiagrams.model.primitive.Type;

/**
 *
 * @author sebca
 */
public class PolygonDrawer implements Drawer{
    private Type type;
    public Vertex center;
    private double zoom = 1;
    
    @Override
    public Type getType() {
        return type;
    }

    @Override
    public void setType(Type type) {
        this.type = type;
    }
    
    public void setCenter(Vertex center){
        this.center = center;
    }
    
    /**
     * Funcion que dibuja los poligonos, tan solo usa los vertices creados en las
     * otras funciones y los une, al final se une el ultimo vertice de la lista
     * con el primero
     * @param gc
     * @param vertexes
     * @param name
     * @param elementState
     */
    @Override
    public void doDraw(GraphicsContext gc, List<Vertex> vertexes, String name, ElementState elementState) {
        zoom =  MainController.getController().getZoomFactor();
        switch (type){
            case ROLE_WEAK:
                weakDraw(gc, vertexes, name, elementState);
                break;
            case AGREGATION:
                this.drawAgregation(gc, vertexes, name, elementState);
                break;
            default:
                normalDraw(gc, vertexes, name, elementState);
                break; 
        }
    }

    /**Marca los puntos de los vertices
     * @param gc
     * @param vertexes
     */
    @Override
    public void doDrawVertex(GraphicsContext gc, List<Vertex> vertexes)
    {
        for (Vertex v : vertexes)
        {
            gc.setStroke(Color.RED);
            gc.setLineWidth(5);
            gc.strokeLine(v.getxPos() * zoom,
                    v.getyPos() * zoom,
                    v.getxPos() * zoom,
                    v.getyPos() * zoom);
        }
        gc.setLineWidth(1);
    }
    
    public void normalDraw(GraphicsContext gc, List<Vertex> vertexes, String name, ElementState elementState){
        gc.setStroke(setColor(elementState));

        gc.setLineWidth(1);
        
        int size = vertexes.size();
        for(int i = 0; i < size; i++){
            gc.strokeLine(vertexes.get(i % size).getxPos() * zoom,
                    vertexes.get(i % size).getyPos() * zoom,
                    vertexes.get((i + 1) % size).getxPos() * zoom,
                    vertexes.get(( i +1 )% size).getyPos() * zoom);
        }
        
        gc.setStroke(Color.BLACK);
        
        //this.paint(gc, vertexes);
        Vertex center = GeometricUtilities.getCenterOfMass(vertexes);
        this.drawText(gc, name, center);
    }
    
    public void weakDraw(GraphicsContext gc, List<Vertex> vertexes, String name, ElementState elementState){
        Color color = setColor(elementState);

        gc.setStroke(color);
        gc.setLineWidth(3);
        
        int size = vertexes.size();
        for(int i = 0; i < size; i++){
            gc.strokeLine(vertexes.get(i % size).getxPos() * zoom,
                    vertexes.get(i % size).getyPos() * zoom,
                    vertexes.get((i + 1) % size).getxPos() * zoom,
                    vertexes.get((i + 1)% size).getyPos()  * zoom);
        }
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(1);
        for(int i = 0; i < vertexes.size(); i++){
            gc.strokeLine(vertexes.get(i % size).getxPos() * zoom,
                    vertexes.get(i % size).getyPos() * zoom,
                    vertexes.get((i + 1) % size).getxPos() * zoom,
                    vertexes.get(( i +1 )% size).getyPos() * zoom);
        }
        
        gc.setStroke(color);
        gc.setLineWidth(1);
        gc.setStroke(color);
        //this.paint(gc, vertexes);
        Vertex center = GeometricUtilities.getCenterOfMass(vertexes);
        this.drawText(gc, name, center);
    }
    
    private void drawText(GraphicsContext gc, String label, Vertex center){
        this.drawText(gc, label, center, TextAlignment.CENTER);
    }
    
    private void drawText(GraphicsContext gc, String label, Vertex center, TextAlignment align){
        gc.setFill(Color.BLACK);
        gc.setFont(new Font(Font.getDefault().getSize() * zoom));
        gc.setTextAlign(align);
        gc.fillText(label, center.getxPos() * zoom, (center.getyPos() + 4)* zoom);
    }
    
    private Color setColor(ElementState state){
        switch(state){
            case HIGHLIGHTED:
                return Color.CORNFLOWERBLUE;
            case INVALID:
                return Color.CRIMSON;
            default:
                return Color.BLACK;
        }
    }
    
    private void drawAgregation(GraphicsContext gc, List<Vertex> vertexes, String label, ElementState elementState){
        gc.setStroke(this.setColor(elementState));
        
        //40: aprox 10 rayas por lado, 4 lados.
        double size = getLineSize(vertexes, vertexes.size() * 40);
        
        for(int i = 0; i < vertexes.size(); i++)
            this.drawAgregationLine(gc,
                                    vertexes.get(i),
                                    vertexes.get((i + 1) % vertexes.size()),
                                    size);
        
        gc.setStroke(Color.BLACK);
        //this.paint(gc, vertexes);
        Vertex upL = vertexes.get(0);
        Vertex centerLabel = new Vertex(upL.getxPos() + 5, upL.getyPos() + 10);
        this.drawText(gc, label, centerLabel, TextAlignment.LEFT);
    }
    
    private void drawAgregationLine(GraphicsContext gc, Vertex v1, Vertex v2, double size){
        Vertex beg;
        Vertex end;

        if ( v1.getxPos() <= v2.getxPos() && v1.getyPos() <= v2.getyPos()){
            beg = v1;
            end = v2;
        } else {
            beg = v2;
            end = v1;
        }
        
        double x = (beg.getxPos() - end.getxPos());
        double y = (beg.getyPos() - end.getyPos());
        double angle = Math.atan(x / y);
        
        Double x1, y1, x2 = null, y2 = null;
        boolean draw = true;
        
        /*System.out.println("v1x: " + v1.getxPos() + " v1y: " + v1.getyPos());
        System.out.println("v2x: " + v2.getxPos() + " v2y: " + v2.getyPos());
        System.out.println("size: " + size);
        System.out.println("Angle: " + angle);*/
        
        for(x1 = beg.getxPos(), y1 = beg.getyPos(); x1 <= end.getxPos() && y1 <= end.getyPos() ; draw = !draw, x1 = x2, y1 = y2){
            x2 = x1 - size * Math.sin(angle);
            y2 = y1 + size * Math.cos(angle);
            
            /*System.out.println("dibujar? : " + draw);
            System.out.println("x1: " + x1 + " y1: " + y1);
            System.out.println("x2: " + x2 + " y2: " + y2);
            System.out.println();*/
            
            if (!draw) continue;
            
            gc.strokeLine(x1 * zoom,
                          y1 * zoom,
                         ((end.getxPos() < x2) ? end.getxPos() : x2) * zoom,
                         ((end.getyPos() < y2) ? end.getyPos() : y2) * zoom);
        }
        /*System.out.println("\n-lado finalizado-\n");*/
    }
    
    private double getLineSize(List<Vertex> vertexes, int parts){
        double perimeter = 0;
        for (int i = 0; i < vertexes.size(); i++){
            perimeter += GeometricUtilities.vertexDistance(vertexes.get(i), vertexes.get( (i + 1) % 4 ));
        }
        return perimeter / parts;
    }
    
    //Esta funcion pintar es ilegal, usado para realizar pruebas.
    /*private void paint(GraphicsContext gc, List<Vertex> vertexes){
    gc.setFill(Color.WHITE);
    gc.beginPath();
    for(Vertex v: vertexes)
    gc.lineTo(v.getxPos() * zoom, v.getyPos() * zoom);
    gc.fill();
    gc.closePath();
    }*/
}
