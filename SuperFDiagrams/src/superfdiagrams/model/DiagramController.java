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
public class DiagramController {
    private static DiagramController dc;
    private Diagram diagram;
    
    private DiagramController(){}
    
    public static DiagramController getController(){
        if(dc == null)
            dc = new DiagramController();
        return dc;
    }

    public Diagram getDiagram() {
        return diagram;
    }

    public void setDiagram(Diagram diagram) {
        this.diagram = diagram;
    }
    
    public void newDiagram(){
        this.diagram = new Diagram();
    }
    
    
    //Este codigo era lo que inicialmente planeaba hacer
    //En vista de que hay que reescribir gran parte del código para poder
    //realizarlo, he decido mantener la compatibilidad con el código existente
    //En el futuro, cuando podamos refactorizar el código de las uniones
    //sólo esto bastará.
    // -- Seba
    /*public void addElement(ElementWrapper element){
    diagram.addElement(element);
    }
    
    public List<ElementWrapper> fetchElements(){
    return diagram.getElements();
    }*/
    
    public void addEntity(ElementWrapper element){
        diagram.addEntity(element);
    }
    
    public void addRelationship(ElementWrapper element){
        diagram.addRelationship(element);
    }
    
    public List<ElementWrapper> fetchEntities(){
        return diagram.getEntities();
    }
    
    public List<ElementWrapper> fetchRelationships(){
        return diagram.getRelationships();
    }
}
