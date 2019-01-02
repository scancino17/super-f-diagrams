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
import superfdiagrams.model.primitive.Union;

/**
 *
 * @author sebca
 */
public class SelectorController {
    private static SelectorController sc;
    
    private List<Element> selectedElements;
    private Element addTo;
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
                if (!selectedElements.contains(element) &&
                element.getPrimitive() instanceof Entity)
                    this.addToList(element);
                break;
            case CHOSING_ENTITY:
                if( ((element.getPrimitive() instanceof Relationship) 
                  ||(element.getPrimitive() instanceof Entity 
                  || ((Attribute)element.getPrimitive()).getType() == ATTRIBUTE_COMPOSITE)) 
                  && this.selectionSize() < 1)
                    this.addToList(element);
                break;
            case CREATING_AGREGATION:
                if (  !selectedElements.contains(element)
                  && (element.getPrimitive() instanceof Relationship) 
                  && (element.getPrimitive().getChildren().size() == 2 )
                  && (Finder.findParentAggregation(element).isEmpty())){
                    
                    if(!selectedElements.isEmpty())
                        this.emptySelection();
                    this.addToList(element);
                }
                break;
            case ADDING_ENTITY:
                List<Element> children = addTo.getPrimitive().getChildren();
                if ( !selectedElements.contains(element)
                  && !checkContains(children, element)
                  && element.getPrimitive() instanceof Entity
                  && !(element instanceof ComplexElement)){
                    if(addTo.getPrimitive() instanceof Relationship){
                        if(children.size() + selectionSize() < 6)
                            this.addToList(element);
                    } else
                        this.addToList(element);
                }
                break;
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
    
    public void setToAdd(Element element){
        this.addTo = element;
    }
    
    public Element getToAdd(){
        return addTo;
    }
    
    private boolean checkContains(List<Element> unions, Element entity){
        for(Element u : unions){
            Union p = (Union) u.getPrimitive();
            if(p.getChild() == entity)
                return true;
        }
        return false;
    }
}
