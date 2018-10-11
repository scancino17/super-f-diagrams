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
    private List<ElementWrapper> elements;
    
    public Diagram() {
        this.elements = new ArrayList<>();
    }
    
    public List<ElementWrapper> getElements() {
    return elements;
    }
    
    public void setElements(List<ElementWrapper> elements) {
    this.elements = elements;
    }
    
    public void addElement(ElementWrapper element){
    this.elements.add(element);
    }
}