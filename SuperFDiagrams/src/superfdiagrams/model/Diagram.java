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
    /*private List<ElementWrapper> elements;*/
    private List<ElementWrapper> entities;
    private List<ElementWrapper> relationships;
    
    
    public Diagram() {
        this.entities = new ArrayList<>();
        this.relationships = new ArrayList<>();
    }
    
    //Este codigo era lo que inicialmente planeaba hacer
    //En vista de que hay que reescribir gran parte del código para poder
    //realizarlo, he decido mantener la compatibilidad con el código existente
    //En el futuro, cuando podamos refactorizar el código de las uniones
    //sólo esto bastará.
    // -- Seba
    /*public List<ElementWrapper> getElements() {
    return elements;
    }
    
    public void setElements(List<ElementWrapper> elements) {
    this.elements = elements;
    }
    
    public void addElement(ElementWrapper element){
    this.elements.add(element);
    }*/

    public List<ElementWrapper> getEntities() {
        return entities;
    }

    public List<ElementWrapper> getRelationships() {
        return relationships;
    }
    
    public void addEntity(ElementWrapper element){
        this.entities.add(element);
    }
    
    public void addRelationship(ElementWrapper element){
        this.relationships.add(element);
    }
}