/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package superfdiagrams.model;

import java.util.ArrayList;
import java.util.List;
import static superfdiagrams.model.ElementState.HIGHLIGHTED;
import static superfdiagrams.model.ElementState.NORMAL;
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
    private final StateController stateC;
    
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
                 &&  selectedElements.size() < 6
                 && element.getPrimitive() instanceof Entity)
                    this.addToList(element);
                break;
            case SELECTING_CHILDREN:
                if (!selectedElements.contains(element))
                    this.addToList(element);
                break;
            case CHOSING_ENTITY:
                if( (element.getPrimitive() instanceof Relationship) 
                  ||(element.getPrimitive() instanceof Entity 
                  || ((Attribute)element.getPrimitive()).getType() == ATTRIBUTE_COMPOSITE) 
                  && this.selectionSize() < 1)
                    this.addToList(element);
                break;
            case CREATING_AGREGATION:
                if (  !selectedElements.contains(element)
                  && (element.getPrimitive() instanceof Relationship) 
                  && (element.getPrimitive().getChildren().size() == 2 )){
                    
                    if(!selectedElements.isEmpty())
                        this.emptySelection();
                    this.addToList(element);
                }
        }
    }
    
    private void addToList(Element element){
        element.setElementState(HIGHLIGHTED);
        selectedElements.add(element);
    }
    
    public List<Element> getSelected(){
        List<Element> toReturn = selectedElements;
        emptySelection();
        return toReturn;
    }
    
    public Element getSelected0(){
        return selectedElements.get(0);
    }
    
    private void deselectElements(){
        if(!selectedElements.isEmpty())
            for(Element e: selectedElements)
                e.setElementState(NORMAL);
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
