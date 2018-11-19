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
    
    public void addElement(Element element){
        diagram.addElement(element);
    }
    
    public void removeElement(Element element){
        diagram.removeElement(element);
    }
    
    public List<Element> fetchElements(){
        return diagram.getElements();
    }
}
