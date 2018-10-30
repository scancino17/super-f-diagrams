/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package superfdiagrams.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import superfdiagrams.FXMLDocumentController;
import static superfdiagrams.model.GeometricUtilities.checkColition;
import static superfdiagrams.model.State.ATTRIBUTE;
import static superfdiagrams.model.State.MOVING_ELEMENT;
import static superfdiagrams.model.State.RELATIONSHIP;
import static superfdiagrams.model.State.VIEW;
import superfdiagrams.model.action.ActionController;
import superfdiagrams.model.action.CreateElementAction;
import superfdiagrams.model.action.CreateRelationshipAction;
import superfdiagrams.model.action.DeleteElementAction;
import superfdiagrams.model.action.MoveElementAction;
import superfdiagrams.model.drawer.DrawController;

/**
 *
 * @author sebca
 */
public class MainController {
    private static MainController mc;
    private StateController stateC;
    private DrawController drawC;
    private DiagramController diagramC;
    private FXMLDocumentController uiController;
    private ActionController actionC;
    private List<ElementWrapper> elementsToRelation;
    private ElementWrapper selected;
    private List<ElementWrapper> selectedRelated;
    private MoveElementAction selectedAction;
    private double mouseXPos;
    private double mouseYPos;
    private boolean choosed;
    
    public static MainController getController(){
        if (mc == null)
            mc = new MainController();
        return mc;
    }
    
    private MainController(){
        stateC = StateController.getController();
        drawC = DrawController.getDrawController();
        diagramC = DiagramController.getController();
        actionC = ActionController.getController();
        elementsToRelation = new ArrayList<>();
        this.choosed = false;
    }
    
    public void setUiController(FXMLDocumentController dc){
        uiController = dc;
    }
    
    public void setContext(GraphicsContext gc){
        drawC.setGraphicsContext(gc);
    }
    
    public State getState(){
        return stateC.getState();
    }
    
    public void setState(State state){
        stateC.setState(state);
    }
    
    public void setMousePos(double x, double y){
        this.mouseXPos = x;
        this.mouseYPos = y;
    }
    
    public void toggleDrawVertex(){
        drawC.toggleDrawVertex();
    }
    
    public void newDiagram(){
        diagramC.newDiagram();
    }
    
    /**
     * Funcion que crea una nueva entidad y la guarda en la lista
     * @param posX
     * @param posY
     * @param name
     */
    public void createNewEntity(double posX, double posY, String name){
        Vertex vertex = new Vertex(posX, posY);
        ElementBuilder elementConstructor = new ElementBuilder();
        elementConstructor.setCenter(vertex);
        elementConstructor.setName(name);
        ElementWrapper element = elementConstructor.generateEntity();
        actionC.addToStack(new CreateElementAction(element));
        this.addElement(element);
    }
    
    /**
     * Funcion que crea una nueva relacion
     * @param posX
     * @param posY
     * @param name 
     */
    public void createNewRelation(double posX, double posY, String name){
        Vertex vertex = new Vertex (posX, posY);
        
        ElementBuilder elementConstructor = new ElementBuilder();
        elementConstructor.setCenter(vertex);
        elementConstructor.setName(name);
        ElementWrapper element = elementConstructor.generateRelationship(elementsToRelation);
                
        for(ElementWrapper e : elementsToRelation)
            e.toggleHighlighted();
        
        actionC.addToStack(new CreateRelationshipAction(element));
        
        elementsToRelation = new ArrayList<>();
        this.addElement(element);
        
        for(ElementWrapper union: element.getElement().getContained()){
            this.addElement(union);
        }
    }
    
    /**
     * Funcion que dibuja los elementos de la lista.
     * Esta funcion va dibujando constantemente, cuando la lista se encuentra
     * vacia estara limpiando la pantalla.
     * @param gc 
     */
    public boolean drawElements(){
        if (!drawC.isBufferEmpty()){
            drawC.doDrawLoop();
            return true;
        }
        return false;
    }
    
    public void restart(){
        actionC.restart();
        diagramC.newDiagram();
        elementsToRelation.clear();
        drawC.eraseBuffer();
        selected = null;
        stateC.setState(VIEW);
        
    }
    
    public void finishEntitySelection(){
        if (elementsToRelation.isEmpty())
            stateC.setState(VIEW);
        else if(stateC.getState() == State.CHOSING_ENTITY)
            stateC.setState(ATTRIBUTE);
        else
            stateC.setState(RELATIONSHIP);
    }
    
    public void runMainLoop(){
        if(selected != null && stateC.getState() == MOVING_ELEMENT ){
            VertexGenerator.recalculateVertexes(selected.getVertexes(),
                                    new Vertex(mouseXPos, mouseYPos));
            VertexGenerator.recalculateNearestVertexes(selectedRelated);
        }
        
        switch(stateC.getState()){
            case VIEW:
                uiController.setStatusText("");
                break;
            case SELECTING_ENTITIES:
                uiController.setStatusText("Seleccionando entidades...");
                break;
            case RELATIONSHIP:
                uiController.setStatusText("Creando relación...");
                break;
            case MOVING_ELEMENT:
                uiController.setStatusText("Moviendo elemento...");
                break;
            case ENTITY:
                uiController.setStatusText("Creando entidad...");
            case CHOSING_ENTITY:
                uiController.setStatusText("Escogiendo Entidad...");
                break;
            case ATTRIBUTE:
                uiController.setStatusText("Creando Atributo...");
            case DELETING_ELEMENT:
                uiController.setStatusText("Eliminando elemento...");
        }
    }
    
    public void doClickAction(MouseEvent mouseEvent){
        switch(stateC.getState()){
            case ENTITY:
                if(checkColition(mouseEvent.getX(), mouseEvent.getY()) == null)
                {
                    String name = uiController.getElementName("entidad");
                    if (name != null) {
                        createNewEntity( Math.round(mouseEvent.getX()),  Math.round(mouseEvent.getY()), name);
                        stateC.setState(State.VIEW);
                    }
                }
                break;
            case SELECTING_ENTITIES:   
                if(checkColition(mouseEvent.getX(), mouseEvent.getY()) != null)
                {                   
                    ElementWrapper entity = checkColition(mouseEvent.getX(), mouseEvent.getY());
                    if (entity.getElement() instanceof Entity) {
                        uiController.activateFinishButton();
                        entity.toggleHighlighted();
                        if (!elementsToRelation.contains(entity)
                            &&  elementsToRelation.size() < 6)
                            this.elementsToRelation.add(entity);
                        else
                            entity.toggleHighlighted();
                    }
                }
                break;
            case RELATIONSHIP:
                if(checkColition(mouseEvent.getX(), mouseEvent.getY()) == null)
                {
                    String name = uiController.getElementName("relación");
                    if (name != null){
                        createNewRelation( Math.round(mouseEvent.getX()), 
                                           Math.round(mouseEvent.getY()), name);
                        stateC.setState(VIEW);
                    } else {
                        for(ElementWrapper element: elementsToRelation)
                            element.toggleHighlighted();
                    }
                }
                break;
            case CHOSING_ENTITY:
                uiController.activateFinishButton();
                if(checkColition(mouseEvent.getX(), mouseEvent.getY()) != null){
                    ElementWrapper entity = checkColition(mouseEvent.getX(), mouseEvent.getY());
                    if((entity.getElement() instanceof Entity || ((Attribute)entity.getElement()).getType() == 4) 
                            && !choosed){
                        elementsToRelation.add(entity);
                        entity.toggleHighlighted();
                        choosed = true;
                    }
                }
                break;
            case ATTRIBUTE:
                if(checkColition(mouseEvent.getX(), mouseEvent.getY()) == null)
                {
                    String name = uiController.getElementName("Atributo");
                    if (name != null){
                        createNewAttribute( Math.round(mouseEvent.getX()), 
                                           Math.round(mouseEvent.getY()), name);
                        stateC.setState(VIEW);
                        choosed = false;
                    } else {
                        for(ElementWrapper element: elementsToRelation)
                            element.toggleHighlighted();
                    }
                }
                break;
            case MOVING_ELEMENT:
                if(checkColition(mouseEvent.getX(), mouseEvent.getY()) != null)
                {
                    selected.toggleHighlighted();
                    selectedAction.getNewPosition();
                    selected = null;
                    selectedRelated = null;
                    selectedAction = null;
                    stateC.setState(VIEW);
                }
                break;
            case DELETING_ELEMENT:
                ElementWrapper toDelete = checkColition
                                (mouseEvent.getX(), mouseEvent.getY());
                if (toDelete != null){
                    deleteElement(toDelete);
                    stateC.setState(VIEW);
                }
        }
        
        if (checkColition(mouseEvent.getX(), mouseEvent.getY()) != null){
            if(mouseEvent.getButton().equals(MouseButton.PRIMARY) 
            && mouseEvent.getClickCount() == 2 && stateC.getState() == VIEW)
            {
                selected = checkColition(mouseEvent.getX(), mouseEvent.getY());
                selectedRelated = new Finder().findRelatedUnions(diagramC.fetchElements(), selected);
                selectedAction = new MoveElementAction(selected, selectedRelated);
                actionC.addToStack(selectedAction);
                
                if (!(selected.getElement() instanceof Union)){
                    selected.toggleHighlighted();
                    stateC.setState(MOVING_ELEMENT);
                } else {
                    selected = null;
                }
            }
        }
        
        /*Método para probar caracteristica de vertex*/
        /*if(mouseEvent.getClickCount() == 3 && checkColition(mouseEvent.getX(), mouseEvent.getY()) != null){
        ElementWrapper element = checkColition(mouseEvent.getX(), mouseEvent.getY());
        for (Vertex v: element.getVertexes())
        System.out.println(v.isUsed());
        }*/
    }
    
    public void cancelEntitySelection(){
        if (elementsToRelation != null && !elementsToRelation.isEmpty())
            for (ElementWrapper element: elementsToRelation)
                element.toggleHighlighted();
        
        elementsToRelation = new ArrayList<>();
        choosed = false;
    }
    
    public void undo(){
        actionC.undo();
    }
    
    public void redo(){
        actionC.redo();
    }
    
    public boolean isUndoEmpty(){
        return actionC.isUndoEmpty();
    }
    
    public boolean isRedoEmpty(){
        return actionC.isRedoEmpty();
    }
    
    public void addElement(ElementWrapper element){
        diagramC.addElement(element);
        drawC.addToBuffer(element);
    }
    
    public void deleteElement(ElementWrapper deleted){
        List<ElementWrapper> related;
        List<ElementWrapper> toMorph = new ArrayList<>();
        related = new Finder().findRelatedUnions(diagramC.fetchElements(), deleted);
        
        actionC.addToStack(new DeleteElementAction(deleted, related, toMorph));
        
        for(ElementWrapper element: related){
            ElementWrapper parent = ((ConnectsWrappers)element.getElement()).getParent();
            parent.getElement().getContained().remove(element);
            if (!toMorph.contains(parent))
                toMorph.add(parent);
            removeElement(element);
        }
        removeElement(deleted);
        morphElements(toMorph);
    }
    
    public void morphElements(List<ElementWrapper> toMorph){
        for(ElementWrapper element: toMorph){
            
            if (element.getElement() instanceof Relationship
            &&  element.getElement().getContained().size() == 1)
            {
                ElementWrapper clone = new ElementBuilder().cloneElement
                                      (element.getElement().getContained().get(0));
                element.getElement().getContained().add(clone);
                addElement(clone);
            }
            
            if(element.getElement().getContained().isEmpty())
                deleteElement(element);
                
            element.setVertexes(VertexGenerator.generateVertexes
                (element.getElement().getContained().size(),
                 ElementBuilder.getDefaultSize(),
                 GeometricUtilities.getCenterOfMass(element.getVertexes())));
            VertexGenerator.recalculateNearestVertexes(element.getElement().getContained());
        }
    }
    
    public void removeElement(ElementWrapper element){
        diagramC.removeElement(element);
        drawC.removeFromBuffer(element);
    }

    /**
     * Funcion que crea un nuevo atributo, pide que tipo de atributo sera 
     * y se une con la entidad que se eligio.
     * @param posX
     * @param posY
     * @param name 
     */
    public void createNewAttribute(double posX, double posY, String name){
        Vertex vertex = new Vertex(posX, posY);
        
        ElementBuilder elementCostructor = new ElementBuilder();
        elementCostructor.setCenter(vertex);
        elementCostructor.setName(name);
        
        for(ElementWrapper e : elementsToRelation)
            e.toggleHighlighted();
        
        Attribute attribute = new Attribute();
        attribute.setContained(elementsToRelation);
        int type = Integer.parseInt(uiController.getType());
        if (type != 0){
            attribute.setType(type);

            ElementWrapper element = elementCostructor.generateAttribute(attribute);

            actionC.addToStack(new CreateRelationshipAction(element));

            elementsToRelation = new ArrayList<>();
            this.addElement(element);

            for(ElementWrapper union: element.getElement().getContained()){
                this.addElement(union);
            }
        } else {
            cancelEntitySelection();
        }
    }

}
