/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package superfdiagrams.model;

import superfdiagrams.model.primitive.Relationship;
import superfdiagrams.model.primitive.Entity;
import superfdiagrams.model.primitive.Union;
import superfdiagrams.model.primitive.Attribute;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.canvas.GraphicsContext;
import superfdiagrams.FXMLDocumentController;
import static superfdiagrams.model.GeometricUtilities.checkColition;
import static superfdiagrams.model.State.ATTRIBUTE;
import static superfdiagrams.model.State.HERITAGE;
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
import superfdiagrams.model.primitive.Heritage;
import superfdiagrams.model.primitive.Type;
import static superfdiagrams.model.primitive.Type.*;

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
    private List<Element> elementsToRelation;
    private Element selected;
    private List<Element> selectedRelated;
    
    private MoveElementAction selectedAction;
    private boolean doubleClick;
    private double mouseXPos;
    private double mouseYPos;
    private double zoomFactor;
    private boolean choosed;
    
    private Element currentElement;
    
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
        Type type = parseRoleType(Integer.parseInt(uiController.askType()));
        if(type != null)
        {
            Vertex vertex = new Vertex(posX, posY);
            ElementBuilder elementConstructor = new ElementBuilder();
            elementConstructor.setCenter(vertex);
            elementConstructor.setName(name);        
            Element element = elementConstructor.generateEntity(type);
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
        Type type = ROLE_STRONG;
        
        for(Element element: elementsToRelation){
            if(element.getElement().getType() == ROLE_WEAK){
                type = parseRoleType(Integer.parseInt(uiController.askType()));
            }
        }
        
        ElementBuilder elementConstructor = new ElementBuilder();
        elementConstructor.setCenter(vertex);
        elementConstructor.setName(name);
        
        /*if(type == 2)
        type++;*/
        
        Element element = elementConstructor.generateRelationship(elementsToRelation, type);
                
        for(Element e : elementsToRelation)
            e.setHighlighted(false);
        
        actionC.addToStack(new CreateRelationshipAction(element));
        
        elementsToRelation = new ArrayList<>();
        this.addElement(element);
        
        for(Element union: element.getElement().getChildren()){
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
        else if(stateC.getState() == State.SELECTING_CHILDREN && elementsToRelation.size() != 1)
            stateC.setState(HERITAGE);
        else if (stateC.getState() == State.SELECTING_ENTITIES)
            stateC.setState(RELATIONSHIP);
        else
            stateC.setState(VIEW);
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
                break;
            case DELETING_ELEMENT:
                uiController.setStatusText("Eliminando elemento...");
                break;
            case HERITAGE:
                uiController.setStatusText("Creando herencia...");
                break;
            case SELECTING_CHILDREN:
                uiController.setStatusText("Seleccionando hijos...");
                break;
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
                    Element entity = checkColition(mouseXPos, mouseYPos);
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
                        for(Element element: elementsToRelation)
                            element.setHighlighted(false);
                    }
                }
                break;
            case SELECTING_CHILDREN:
                if(currentElement != null)
                {                   
                    Element entity = checkColition(mouseXPos, mouseYPos);
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
            case HERITAGE:
                if(currentElement == null)
                {
                    createNewHeritage(mouseXPos, mouseYPos);
                    stateC.setState(VIEW);
                    choosed = false;
                }
                break;  
            case CHOSING_ENTITY:
                uiController.activateFinishButton();
                if(currentElement != null){
                    Element entity = checkColition(mouseXPos, mouseYPos);
                    if((entity.getElement() instanceof Relationship) ||(entity.getElement() instanceof Entity || ((Attribute)entity.getElement()).getType() == ATTRIBUTE_COMPOSITE) 
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
                        for(Element element: elementsToRelation)
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
            for (Element element: elementsToRelation)
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
    
    public void addElement(Element element){
        diagramC.addElement(element);
        drawC.addToBuffer(element);
    }
    
    public void deleteElement(Element deleted){
        List<Element> related = null;
        
        if (deleted.getElement() instanceof Entity){
            related = new Finder().findRelatedUnions(diagramC.fetchElements(), deleted); 
        } else if (deleted.getElement() instanceof Relationship){
            related = deleted.getElement().getChildren();
        } else if (deleted.getElement() instanceof Attribute){
            related = deleted.getElement().getChildren();
        }
        if (related != null){
            DeleteElementAction deleteAction = new DeleteElementAction(deleted, related);
            deleteAction.execute();
            actionC.addToStack(deleteAction);
        }
    }
    
    public void morphElement(List<Element> elementList){
        for(Element element : elementList)
            morphElement(element);
    }
    
    public void morphElement(Element element){
        List<Element> contained = element.getElement().getChildren();
        
        if(contained.isEmpty()){
            removeElement(element);
            return;
        }
        
        if(element.getElement() instanceof Relationship){
            if(contained.size() == 1){
                Element union = new ElementBuilder().cloneUnion(contained.get(0));
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
    
    public void removeElement(Element element){
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
        
        for(Element e : elementsToRelation)
            e.setHighlighted(false);
        
        Attribute attribute = new Attribute();
        attribute.setChildren(elementsToRelation);
        int type = Integer.parseInt(uiController.getType());
        if (type != 0){
            attribute.setType(parseAttributeType(type));

            Element element = elementCostructor.generateAttribute(attribute);

            actionC.addToStack(new CreateRelationshipAction(element));

            elementsToRelation = new ArrayList<>();
            this.addElement(element);

            for(Element union: element.getElement().getChildren()){
                this.addElement(union);
            }
        } else {
            cancelEntitySelection();
        }
    }
    
    public void createNewHeritage(double posX, double posY){
        String name = null;
        Vertex vertex = new Vertex(posX, posY);
        ElementBuilder elementConstructor = new ElementBuilder();
        elementConstructor.setCenter(vertex);
        if (Integer.parseInt(uiController.askHeritage()) == 1){
            name = "D";
        }else{
            name = "S";
        }
        elementConstructor.setName(name);
        
        Heritage heritage = new Heritage();
        heritage.setChildren(elementsToRelation);
        heritage.setLabel(name);
        
        for(Element e : elementsToRelation)
            e.setHighlighted(false);
        
        Element element = elementConstructor.generateHeritage(heritage);

        actionC.addToStack(new CreateRelationshipAction(element));
        
        elementsToRelation = new ArrayList<>();
        this.addElement(element);
        
        for(Element union: element.getElement().getChildren()){
            this.addElement(union);
        }
        
    }

    public List<Element> fetchElements() {
        return diagramC.fetchElements();
    }

    public Element getCurrentElement(){
        if (currentElement == null || currentElement.getElement() instanceof Heritage)
            return null;
        
        return currentElement;
    }

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
    
    private Type parseAttributeType(int type){
        switch(type){
            case 1:
                return ATTRIBUTE_DERIVATE;
            case 3:
                return ATTRIBUTE_KEY;
            case 4:
                return ATTRIBUTE_COMPOSITE;
            case 5:
                return ATTRIBUTE_MULTIVALUATED;
            case 6:
                return ATTRIBUTE_PARTIAL_KEY;
            default:
                return ATTRIBUTE_GENERIC;
        }
    }
    
    private Type parseRoleType(int type){
        switch(type){
            case 2:
                return ROLE_WEAK;
            default:
                return ROLE_STRONG;
        }
    }
}
