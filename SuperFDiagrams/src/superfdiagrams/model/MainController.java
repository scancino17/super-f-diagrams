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
import superfdiagrams.FXMLDocumentController;
import static superfdiagrams.model.GeometricUtilities.checkColition;
import static superfdiagrams.model.State.ATTRIBUTE;
import static superfdiagrams.model.State.MOVING_ELEMENT;
import static superfdiagrams.model.State.RELATIONSHIP;
import static superfdiagrams.model.State.SELECTING_ENTITIES;
import static superfdiagrams.model.State.VIEW;
import superfdiagrams.model.action.ActionController;
import superfdiagrams.model.action.CreateElementAction;
import superfdiagrams.model.action.CreateRelationshipAction;
import superfdiagrams.model.action.DeleteElementAction;
import superfdiagrams.model.action.MoveElementAction;
import superfdiagrams.model.action.RenameElementAction;
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
    private boolean doubleClick;
    private double mouseXPos;
    private double mouseYPos;
    private double zoomFactor;
    private boolean choosed;
    
    private ElementWrapper currentElement;
    
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
        currentElement = null;
        this.zoomFactor = 1;
        this.doubleClick = false;
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
        this.mouseXPos = x / zoomFactor;
        this.mouseYPos = y / zoomFactor;
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
        int type = Integer.parseInt(uiController.askType());
        if(type != 0)
        {
            Vertex vertex = new Vertex(posX, posY);
            ElementBuilder elementConstructor = new ElementBuilder();
            elementConstructor.setCenter(vertex);
            elementConstructor.setName(name);        
            ElementWrapper element = elementConstructor.generateEntity(type);
            actionC.addToStack(new CreateElementAction(element));
            this.addElement(element);
        }
    }
    
    /**
     * Funcion que crea una nueva relacion
     * @param posX
     * @param posY
     * @param name 
     */
    public void createNewRelation(double posX, double posY, String name){
        Vertex vertex = new Vertex (posX, posY);
        
        int type = 1;
        
        ElementBuilder elementConstructor = new ElementBuilder();
        elementConstructor.setCenter(vertex);
        elementConstructor.setName(name);
        
        for (int i = 0; i < elementsToRelation.size(); i++) {
            if (elementsToRelation.get(i).getElement().getType() == 2){
                type = 3;
                i = elementsToRelation.size();
            }
        }
        
        ElementWrapper element = elementConstructor.generateRelationship(elementsToRelation, type);
                
        for(ElementWrapper e : elementsToRelation)
            e.setHighlighted(false);
        
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
        currentElement = null;
        stateC.setState(VIEW);
        zoomFactor = 1;
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
                                    new Vertex(mouseXPos,
                                               mouseYPos));
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
    
    public void doClickAction(){
        if (currentElement != null && stateC.getState() == VIEW)
            currentElement.setHighlighted(false);
        
        currentElement = checkColition(mouseXPos, mouseYPos);
        switch(stateC.getState()){
            case ENTITY:
                if(currentElement == null)
                {
                    String name = uiController.getElementName("entidad");
                    if (name != null) {
                        createNewEntity(mouseXPos, mouseYPos, name);
                        stateC.setState(State.VIEW);
                    }
                }
                break;
            case SELECTING_ENTITIES:   
                if(currentElement != null)
                {                   
                    ElementWrapper entity = checkColition(mouseXPos, mouseYPos);
                    if (entity.getElement() instanceof Entity) {
                        uiController.activateFinishButton();
                        entity.setHighlighted(true);
                        if (!elementsToRelation.contains(entity)
                            &&  elementsToRelation.size() < 6)
                            this.elementsToRelation.add(entity);
                        else
                            entity.setHighlighted(false);
                    }
                }
                break;
            case RELATIONSHIP:
                if(currentElement == null)
                {
                    String name = uiController.getElementName("relación");
                    if (name != null){
                        createNewRelation(mouseXPos, mouseYPos, name);
                        stateC.setState(VIEW);
                    } else {
                        for(ElementWrapper element: elementsToRelation)
                            element.setHighlighted(false);
                    }
                }
                break;
            case CHOSING_ENTITY:
                uiController.activateFinishButton();
                if(currentElement != null){
                    ElementWrapper entity = checkColition(mouseXPos, mouseYPos);
                    if((entity.getElement() instanceof Entity || ((Attribute)entity.getElement()).getType() == 4) 
                            && !choosed){
                        elementsToRelation.add(entity);
                        entity.setHighlighted(true);
                        choosed = true;
                    }
                }
                break;
            case ATTRIBUTE:
                if(currentElement == null)
                {
                    String name = uiController.getElementName("Atributo");
                    if (name != null){
                        createNewAttribute(mouseXPos, mouseYPos, name);
                        stateC.setState(VIEW);
                        choosed = false;
                    } else {
                        for(ElementWrapper element: elementsToRelation)
                            element.setHighlighted(false);
                    }
                }
                break;
            case MOVING_ELEMENT:
                if(currentElement != null)
                {
                    selected.setHighlighted(false);
                    selectedAction.getNewPosition();
                    selected = null;
                    selectedRelated = null;
                    selectedAction = null;
                    stateC.setState(VIEW);
                }
                break;
        }
        
        if (currentElement != null){
            if(doubleClick && stateC.getState() == VIEW)
            {
                selected = checkColition(mouseXPos, mouseYPos);
                selectedRelated = new Finder().findRelatedUnions(diagramC.fetchElements(), selected);
                selectedAction = new MoveElementAction(selected, selectedRelated);
                actionC.addToStack(selectedAction);
                
                if (!(selected.getElement() instanceof Union)){
                    selected.setHighlighted(true);
                    stateC.setState(MOVING_ELEMENT);
                } else {
                    selected = null;
                }
            }
        }
    }
    
    public void cancelEntitySelection(){
        if (elementsToRelation != null && !elementsToRelation.isEmpty())
            for (ElementWrapper element: elementsToRelation)
                element.setHighlighted(false);
        
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
        List<ElementWrapper> related = null;
        
        if (deleted.getElement() instanceof Entity){
            related = new Finder().findRelatedUnions(diagramC.fetchElements(), deleted); 
        } else if (deleted.getElement() instanceof Relationship){
            related = deleted.getElement().getContained();
        } else if (deleted.getElement() instanceof Attribute){
            related = deleted.getElement().getContained();
        }
        if (related != null){
            DeleteElementAction deleteAction = new DeleteElementAction(deleted, related);
            deleteAction.execute();
            actionC.addToStack(deleteAction);
        }
    }
    
    public void morphElement(List<ElementWrapper> elementList){
        for(ElementWrapper element : elementList)
            morphElement(element);
    }
    
    public void morphElement(ElementWrapper element){
        List<ElementWrapper> contained = element.getElement().getContained();
        
        if(contained.isEmpty()){
            removeElement(element);
            return;
        }
        
        if(element.getElement() instanceof Relationship){
            if(contained.size() == 1){
                ElementWrapper union = new ElementBuilder().cloneUnion(contained.get(0));
                contained.add(union);
                this.addElement(union);
            }
            
            if(contained.get(0).equals(contained.get(1))){
                contained.remove(0);
            }
            
            element.setVertexes(VertexGenerator.generateVertexes(
                    contained.size(), 
                    ElementBuilder.getDefaultSize(), 
                    GeometricUtilities.getCenterOfMass(element.getVertexes())));
        }
        
        VertexGenerator.recalculateNearestVertexes(contained);
           
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
            e.setHighlighted(false);
        
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

    public int askType(){
        Scanner leer = new Scanner(System.in);
        System.out.println("1.- Normal");
        System.out.println("2.- Debil");
        return leer.nextInt();
    }

    public List<ElementWrapper> fetchElements() {
        return diagramC.fetchElements();
    }

    public ElementWrapper getCurrentElement() {return currentElement;}

    public void renameCurrentElement(String label){
        RenameElementAction action = new RenameElementAction(currentElement, label);
        action.execute();
        actionC.addToStack(action);
    }

    public double getZoomFactor(){ return  zoomFactor;}
    public void setZoomFactor(double _zoomFactor) {zoomFactor = _zoomFactor;}
    
    public void setDoubleClick(boolean value){
        this.doubleClick = value;
    }
}
