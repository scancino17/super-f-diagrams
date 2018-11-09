/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package superfdiagrams.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author sebca
 */
public class Diagram {
    private List<Element> elements;
    
    public Diagram() {
        this.elements = new ArrayList<>();
    }
    
    public List<Element> getElements() {
        return elements;
    }
    
    public void setElements(List<Element> elements) {
        this.elements = elements;
    }
    
    public void addElement(Element element){
        this.elements.add(element);
    }
    
    public void removeElement(Element element){
        if (elements.contains(element))
            elements.remove(element);
    }
}