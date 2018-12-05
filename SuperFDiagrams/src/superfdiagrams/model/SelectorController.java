/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package superfdiagrams.model;

import java.util.ArrayList;
import java.util.List;
import superfdiagrams.model.primitive.Attribute;
import superfdiagrams.model.primitive.Entity;
import superfdiagrams.model.primitive.Relationship;
import static superfdiagrams.model.primitive.Type.ATTRIBUTE_COMPOSITE;

/**
 *
 * @author sebca
 */
public class SelectorController {
    private static SelectorController sc;
    
    private List<Element> selectedElements;
    private StateController stateC;
    
    private SelectorController(){
        this.selectedElements = new ArrayList<>();
        this.stateC = StateController.getController();
    }
    
    public static SelectorController getController(){
        if (sc == null)
            sc = new SelectorController();
        return sc;
    }
    
    public void add(Element element){
        switch(stateC.getState()){
            case SELECTING_ENTITIES:
                if (!selectedElements.contains(element) 
                 &&  selectedElements.size() < 6)
                    this.addToList(element);
                break;
            case SELECTING_CHILDREN:
                if (!selectedElements.contains(element))
                    this.addToList(element);
                break;
            case CHOSING_ENTITY:
                if( (element.getElement() instanceof Relationship) 
                  ||(element.getElement() instanceof Entity 
                  || ((Attribute)element.getElement()).getType() == ATTRIBUTE_COMPOSITE) 
                  && this.selectionSize() < 1)
                    this.addToList(element);
                
        }
    }
    
    private void addToList(Element element){
        System.out.println(element);
        element.setHighlighted(true);
        selectedElements.add(element);
    }
    
    public List<Element> getSelected(){
        List<Element> toReturn = selectedElements;
        emptySelection();
        return toReturn;
    }
    
    private void deselectElements(){
        if(!selectedElements.isEmpty())
            for(Element e: selectedElements)
                e.setHighlighted(false);
    }
    
    public void emptySelection(){
        deselectElements();
        selectedElements = new ArrayList<>();
    }
    
    public int selectionSize(){
        return selectedElements.size();
    }
    
    public boolean isEmpty(){
        return selectedElements.isEmpty();
    }
}
