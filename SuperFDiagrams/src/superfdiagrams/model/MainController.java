/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package superfdiagrams.model;

import java.util.ArrayList;
import superfdiagrams.EntityCheck;
import superfdiagrams.model.primitive.*;

import java.util.HashMap;
import java.util.List;

import javafx.scene.canvas.GraphicsContext;
import superfdiagrams.FXMLDocumentController;
import static superfdiagrams.model.ElementState.HIGHLIGHTED;
import static superfdiagrams.model.ElementState.INVALID;
import static superfdiagrams.model.ElementState.NORMAL;
import static superfdiagrams.model.GeometricUtilities.checkColition;
import static superfdiagrams.model.State.*;
import superfdiagrams.model.action.ActionController;
import superfdiagrams.model.action.CreateElementAction;
import superfdiagrams.model.action.DeleteAttributeAction;
import superfdiagrams.model.action.DeleteElementAction;
import superfdiagrams.model.action.MoveAction;
import superfdiagrams.model.action.MoveComplexElementAction;
import superfdiagrams.model.action.MoveElementAction;
import superfdiagrams.model.action.RenameElementAction;
import superfdiagrams.model.drawer.DrawController;

import static superfdiagrams.model.primitive.Type.*;

/**
 *
 * @author sebca
 */
public class MainController
{
    private static MainController mc;
    private final StateController stateC;
    private final DrawController drawC;
    private final DiagramController diagramC;
    private FXMLDocumentController uiController;
    private final ActionController actionC;
    private final SelectorController selectorC;

    private Element selected;
    private List<Element> selectedRelated;
    private ComplexElement morphingComplex;

    //maps para verificaciones semánticas
    public HashMap<Integer, EntityCheck> weakEntityCheck; //un mapa con un objeto que contiene las verificaciones de entidades débiles
    public HashMap<String, Entity> entityNanes; //un mapa para verificar que entidades no tengan mismo nombre.
    //public HashMap<Integer, Boolean> StrongEntityCheck strongEntity;

    private MoveAction selectedAction;
    private boolean doubleClick;
    private boolean shouldComplexMorph;
    
    
    private double mouseXPos;
    private double mouseYPos;
    private double zoomFactor;
    private double maxWith;
    private double maxHeight;

    private Element currentElement;

    public static MainController getController()
    {
        if (mc == null)
            mc = new MainController();
        return mc;
    }

    private MainController()
    {
        stateC = StateController.getController();
        drawC = DrawController.getDrawController();
        diagramC = DiagramController.getController();
        actionC = ActionController.getController();
        selectorC = SelectorController.getController();
        weakEntityCheck = new HashMap<Integer, EntityCheck>();
        entityNanes = new HashMap<String, Entity>();

        currentElement = null;
        this.zoomFactor = 1;
        this.doubleClick = false;
        maxWith = 800; //defualt minimum with posible
        maxHeight = 700; //default minimum height posible
        shouldComplexMorph = false;
        morphingComplex = null;

    }

    public void setUiController(FXMLDocumentController dc)
    {
        uiController = dc;
    }

    public void setContext(GraphicsContext gc)
    {
        drawC.setGraphicsContext(gc);
    }

    public State getState()
    {
        return stateC.getState();
    }

    public void setState(State state)
    {
        stateC.setState(state);
        this.cancelSelection();
    }

    public void setMousePos(double x, double y)
    {
        this.mouseXPos = x / zoomFactor;
        this.mouseYPos = y / zoomFactor;
    }

    public void toggleDrawVertex()
    {
        drawC.toggleDrawVertex();
    }

    public void newDiagram()
    {
        diagramC.newDiagram();
    }

    public void createNewEntity()
    {
        String label = uiController.getElementLabel("entidad");

        if (label == null)
        {
            stateC.setState(VIEW);
            return;
        }
        if (entityNanes.containsKey(label)) //si ya se encuentra un elemento con ese mismo nombre...
        {
            System.out.println("elemento contenido...   ");
            //volver a pedir name...
            return;
        }

        Type type = uiController.askRoleType();

        if (type == null)
        {
            stateC.setState(VIEW);
            return;
        }

        CreateElementAction create = new CreateElementAction();
        create.createEntity(mouseXPos, mouseYPos, label, type);
        actionC.addToStack(create);
        stateC.setState(VIEW);
    }

    public void createNewRelationship()
    {
        String label = uiController.getElementLabel("relación");

        if (label == null)
        {
            finishAction();
            return;
        }

        Type type = ROLE_STRONG;
        
        List<Element> selectedElements = selectorC.getSelected();
        for(Element element: selectedElements){
            if(element.getPrimitive().getType() == ROLE_WEAK){
                type = uiController.askRoleType();
                break;
            }
        }

        if (type == null)
        {
            finishAction();
            return;
        }

        CreateElementAction create = new CreateElementAction();
        create.createRelationship(mouseXPos, mouseYPos, label, type, selectedElements);

        actionC.addToStack(create);
        finishAction();
    }
    
    public void createNewAttribute(){
        String label = uiController.getElementLabel("atributo");
        
        if (label == null){
            finishAction();
            return;
        }
        
        Type type = uiController.askAttributeType();
        
        if(type == null){
            finishAction();
            return;
        }
        
        List<Element> selectedElements = selectorC.getSelected();
        CreateElementAction create = new CreateElementAction();
        create.createAttribute(mouseXPos, mouseYPos, label, type, selectedElements);
        
        actionC.addToStack(create);
        finishAction();
    }
    
    public void createNewHeritage(){
        String name;
        Type type = uiController.askHeritageType();
        
        if(type == null){
            finishAction();
            return;
        }
        
        switch(type){
            case HERITAGE_D:
                name = "D";
                break;
            case HERITAGE_S:
                name = "S";
                break;
            default:
                finishAction();
                return;
        }
        
        List<Element> selectedElements = selectorC.getSelected();
        CreateElementAction create = new CreateElementAction();
        create.createHeritage(mouseXPos, mouseYPos, name, type, selectedElements);
        
        actionC.addToStack(create);
        finishAction();
    }
    
    public void createNewAgregation(){
        String label = uiController.getElementLabel("Agregación");
        
        if(label == null){
            finishAction();
            return;
        }
        
        Element temp = selectorC.getSelected().get(0);
        Vertex center = temp.getCenterVertex();
        List<Element> selectedElements = new ArrayList<>();
        
        selectedElements.add(temp);
        selectedElements.addAll(Finder.findRelatedAttributes(temp));
        for(Element el : temp.getPrimitive().getChildren()){
            selectedElements.add(el);
            Element child = ((Union) el.getPrimitive()).getChild();
            selectedElements.add(child);
            selectedElements.addAll(Finder.findRelatedAttributes(child));
        }
        
        CreateElementAction create = new CreateElementAction();
        create.createAgregation(center.getxPos(), center.getyPos(), label, AGREGATION, selectedElements);
        
        actionC.addToStack(create);
        finishAction();
    }
    
    /**
     * Funcion que dibuja los elementos de la lista.
     * Esta funcion va dibujando constantemente, cuando la lista se encuentra
     * vacia estara limpiando la pantalla.
     * @return true si dibujó algo, false caso contrario.
     */
    public boolean drawElements()
    {
        if (/*!drawC.isBufferEmpty()*/!diagramC.fetchElements().isEmpty())
        {
            drawC.doDrawLoop(fetchElements());
            return true;
        }
        return false;
    }

    public void restart()
    {
        actionC.restart();
        diagramC.newDiagram();
        /*drawC.eraseBuffer();*/
        selectorC.emptySelection();
        selected = null;
        currentElement = null;
        stateC.setState(VIEW);
        zoomFactor = 1;
        NameCounter.restartCounter();
        weakEntityCheck = new HashMap<Integer, EntityCheck>();
        this.entityNanes = new HashMap<>();
        this.shouldComplexMorph = false;
        this.morphingComplex = null;
    }
    
    public void finishEntitySelection(){
        if (selectorC.isEmpty())
            stateC.setState(VIEW);
        else if (stateC.getState() == State.CHOSING_ENTITY)
            stateC.setState(ATTRIBUTE);
        else if(stateC.getState() == State.SELECTING_CHILDREN 
             && selectorC.selectionSize() != 1)
            stateC.setState(HERITAGE);
        else if (stateC.getState() == State.SELECTING_ENTITIES)
            stateC.setState(RELATIONSHIP);
        else if (stateC.getState() == State.CREATING_AGREGATION)
            createNewAgregation();
        else
            stateC.setState(VIEW);
    }

    public void runMainLoop()
    {
        if (selected != null && stateC.getState() == MOVING_ELEMENT)
        {
            moveElement();
            if (shouldComplexMorph)
                recursiveComplexMorph(morphingComplex);
        }

        this.setStatusText();
    }
    
    private ComplexElement recursiveComplexMorph(ComplexElement morph){
        VertexGenerator.morphComplex(morph);
        ComplexElement container = Finder.findComplexContained(morph, fetchElements());
        if (container != null)
            return recursiveComplexMorph(container);
        else{
            return morph;
        }
    } 
    
    private void moveElement(){
        if(selected instanceof ComplexElement){
            VertexGenerator.recalculateComplexElement
                                      (selected,
                                      ((ComplexElement)selected).getComposite(),
                                      mouseXPos,
                                      mouseYPos);
            recursiveComplexMorph((ComplexElement) selected);
        } else {
            VertexGenerator.recalculateVertexes(selected, mouseXPos, mouseYPos);
            if (selectedRelated != null && !selectedRelated.isEmpty())
                VertexGenerator.recalculateNearestVertexes(selectedRelated);
        }
    }

    public void setStatusText()
    {
        switch (stateC.getState())
        {
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
            case CREATING_AGREGATION:
                uiController.setStatusText("Creando agregación...");
        }
    }

    public void doClickAction()
    {
        if (currentElement != null && stateC.getState() == VIEW)
            currentElement.setElementState(NORMAL);

        currentElement = checkColition(mouseXPos, mouseYPos);
        
        if(currentElement != null){
            switch(stateC.getState()){
                case MOVING_ELEMENT:
                    selected.setElementState(NORMAL);
                    selectedAction.getNewPosition();
                    
                    if (selected instanceof ComplexElement){
                        VertexGenerator.morphContainedComplex((ComplexElement) selected);
                    }
                    
                    selected = null;
                    selectedRelated = null;
                    selectedAction = null;
                    this.morphingComplex =  null;
                    this.shouldComplexMorph = false;
                    stateC.setState(VIEW);
                    for (Vertex v : currentElement.getVertexes())
                    {
                        maxWith = Math.max(maxWith, v.getxPos());
                        maxHeight = Math.max(maxHeight, v.getyPos());
                    }
                    break;

                case VIEW:
                    if (!doubleClick)
                        break;

                    selected = currentElement;
                    morphingComplex = Finder.findComplexContained(selected, fetchElements());
                    if (morphingComplex != null)
                        this.shouldComplexMorph = true;
                    
                    if (selected instanceof ComplexElement){
                        VertexGenerator.morphContainedComplex((ComplexElement) selected);
                        selectedAction = new MoveComplexElementAction((ComplexElement) selected);
                    } else {
                        if (!shouldComplexMorph){
                            selectedRelated = Finder.findRelatedUnions(diagramC.fetchElements(), selected);
                            selectedAction = new MoveElementAction(selected, selectedRelated);
                        } else {
                            selectedAction = new MoveComplexElementAction(recursiveComplexMorph(morphingComplex));
                        }
                    }
                    
                    actionC.addToStack(selectedAction);
                    
                    if (!(selected.getPrimitive() instanceof Union))
                    {
                        selected.setElementState(HIGHLIGHTED);
                        stateC.setState(MOVING_ELEMENT);
                    }
                    else selected = null;

                    break;
                default:
                    selectorC.add(currentElement);
                    if (!selectorC.isEmpty())
                        uiController.activateFinishButton();
                    break;
            }
        }
        else
        {
            switch (stateC.getState())
            {
                case ENTITY:
                    createNewEntity();
                    break;
                case RELATIONSHIP:
                    createNewRelationship();
                    break;
                case HERITAGE:
                    createNewHeritage();
                    break;
                case ATTRIBUTE:
                    createNewAttribute();
                    break;
            }
        }
    }

    public void cancelSelection()
    {
        selectorC.emptySelection();
        if (currentElement != null)
            currentElement.setElementState(NORMAL);
        currentElement = null;
    }

    public void finishAction()
    {
        stateC.setState(VIEW);
        selectorC.emptySelection();
    }

    public void undo()
    {
        actionC.undo();
    }

    public void redo()
    {
        actionC.redo();
    }

    public boolean isUndoEmpty()
    {
        return actionC.isUndoEmpty();
    }

    public boolean isRedoEmpty()
    {
        return actionC.isRedoEmpty();
    }

    public void addElement(Element element)
    {

        for (Vertex v : element.getVertexes())
        {
            maxWith = Math.max(maxWith, v.getxPos());
            maxHeight = Math.max(maxHeight, v.getyPos());
        }
        diagramC.addElement(element);
        /*drawC.addToBuffer(element);*/
    }

    public void deleteElement(Element deleted)
    {
        List<Element> related = null;
        
        if (deleted.getPrimitive() instanceof Attribute){
            DeleteAttributeAction action = new DeleteAttributeAction(deleted);
            action.execute();
            actionC.addToStack(action);
            return;
        }
        
        if (deleted.getPrimitive() instanceof Entity){
            related = Finder.findRelatedUnions(diagramC.fetchElements(), deleted); 
        } else if (deleted.getPrimitive() instanceof Relationship
                || deleted.getPrimitive() instanceof Heritage){
            related = deleted.getPrimitive().getChildren();
        }
        else if (deleted.getPrimitive() instanceof Relationship
                || deleted.getPrimitive() instanceof Heritage)
        {
            related = deleted.getPrimitive().getChildren();
        }
        if (related != null)
        {
            DeleteElementAction deleteAction = new DeleteElementAction(deleted, related);
            deleteAction.execute();
            actionC.addToStack(deleteAction);
        }
    }

    public void morphElement(List<Element> elementList)
    {
        for (Element element : elementList)
            morphElement(element);
    }

    public void morphElement(Element element)
    {
        List<Element> contained = element.getPrimitive().getChildren();

        if (contained.isEmpty())
        {
            removeElement(element);
            return;
        }

        if (element.getPrimitive() instanceof Relationship)
        {
            if (contained.size() == 1)
            {
                Element union = new ElementBuilder().cloneUnion(contained.get(0));
                contained.add(union);
                this.addElement(union);
            }

            if (contained.get(0).equals(contained.get(1)))
            {
                contained.remove(0);
            }

            element.setVertexes(VertexGenerator.generateVertexes(
                    contained.size(),
                    ElementBuilder.getDefaultSize(),
                    GeometricUtilities.getCenterOfMass(element.getVertexes())));
            
            boolean shouldMorph = true;
            if(element.getPrimitive().getType() == ROLE_WEAK){
                for (Element u : element.getPrimitive().getChildren()){
                    if(u.getPrimitive().getType() == ROLE_WEAK){
                        shouldMorph = false;
                        break;
                    }
                }
            }
            
            if(shouldMorph){
                element.getDrawer().setType(ROLE_STRONG);
            }
        }

        VertexGenerator.recalculateNearestVertexes(contained);

    }

    public void removeElement(Element element)
    {
        diagramC.removeElement(element);
        /*drawC.removeFromBuffer(element);*/
        weakEntityCheck.remove(element.getPrimitive().hashCode());
    }

    public List<Element> fetchElements()
    {
        return diagramC.fetchElements();
    }

    public Element getCurrentElement()
    {
        return currentElement;
    }

    public void renameCurrentElement(String label){
        if (currentElement.getPrimitive() instanceof Heritage)
            return;
        
        RenameElementAction action = new RenameElementAction(currentElement, label);
        action.execute();
        actionC.addToStack(action);
    }

    public double getZoomFactor() { return zoomFactor;}

    public void setZoomFactor(double _zoomFactor) {zoomFactor = _zoomFactor;}

    public void setDoubleClick(boolean value)
    {
        this.doubleClick = value;
    }

    public double getMaxWith() {return Math.min(Math.max((maxWith * zoomFactor), 800), 2000);} //800 minimum with posible; 2000 maximum

    public double getMaxHeight() {return Math.min(Math.max((maxHeight * zoomFactor), 700), 7000);} // 700 maximum with posible


    public void normalizeDraw() {zoomFactor = 1;}


    /**
     * Refactorizado por Sebastian Cancino
     * @author Ignacio Martinez
     * @return 
     */
    public String checkSemantics()
    {
        String message = "";
        List<Element> elements = this.fetchElements();
        for (Element e : elements)
        {
            if (e.getPrimitive() instanceof Entity)
            {
                if (!weakEntityCheck.containsKey(e.getPrimitive().hashCode()))
                    weakEntityCheck.put(e.getPrimitive().hashCode(), new EntityCheck(e.getPrimitive().getLabel(), e.getPrimitive().getType()));
                else
                    weakEntityCheck.get(e.getPrimitive().hashCode()).setFalse();

                if(!entityNanes.containsKey(e.getPrimitive().getLabel()))
                    entityNanes.put(e.getPrimitive().getLabel(), (Entity)e.getPrimitive());

            }
            Primitive element = e.getPrimitive();
            if (element.getType() == ATTRIBUTE_PARTIAL_KEY)  //verifica que una entidad débil tenga atributo parcial
            {
                Primitive entity = ((Union) (element.getChildren().get(0).getPrimitive())).getChild().getPrimitive();
                if (entity.getType() == ROLE_WEAK)
                {
                    int key = entity.hashCode();
                    EntityCheck temp = weakEntityCheck.get(key);
                    temp.partialKey = true;
                    weakEntityCheck.replace(key, temp);
                }
            }
            else if(element.getType() == ATTRIBUTE_KEY)  // verifica que entidad fuerte tenga un atributo clave
            {
                Primitive entity = ((Union) (element.getChildren().get(0).getPrimitive())).getChild().getPrimitive();
                if (entity.getType() == ROLE_STRONG)
                {
                    int key = entity.hashCode();
                    EntityCheck temp = weakEntityCheck.get(key);
                    temp.keyAtribute = true;
                    weakEntityCheck.replace(key, temp);
                }
            }
            else if(element instanceof Relationship) //verifica que una entidad débil esté relacionada con una fuerte por relación débl
            {
                boolean strong = false;
                int key = 0;
                
                List<Element> elementChildren = element.getChildren();
                
                for(Element el : elementChildren)
                {
                    Union u = (Union) el.getPrimitive();
                    Primitive entity = u.getChild().getPrimitive();
                    if (entity.getType() == ROLE_WEAK && u.getType() == ROLE_WEAK) //verifica que tiene una entidad debil
                        key = entity.hashCode();
                    else
                        strong = true; //asume que tiene una entidad fuerte, respetando diseño original
                }

                if(weakEntityCheck.containsKey(key))
                {
                    EntityCheck temp = weakEntityCheck.get(key);
                    temp.strongEntity = strong;
                    weakEntityCheck.replace(key, temp);
                }
            }
            else if(element instanceof Heritage)
            {
                String fatherName = ((Union)(element.getChildren().get(0).getPrimitive())).getChild().getPrimitive().getLabel();
                for(int i = 1; i< element.getChildren().size(); ++i)
                    if (((Union) (element.getChildren().get(i).getPrimitive())).getChild().getPrimitive().getLabel().equals(fatherName)) //verifica si entidades hijas (herencia) tiene el mismo nombre que el padre
                        message += "\n" + fatherName + "\nTiene herencia con el mismo nombre";
            }

            if (e.getPrimitive() instanceof Entity)
                if (e.getPrimitive().getType() == ROLE_WEAK){
                    boolean isValid = weakEntityCheck.get(e.getPrimitive().hashCode()).isValid();
                    if(!isValid && e.getElementState() != HIGHLIGHTED) e.setElementState(INVALID);
                    else if (e.getElementState() != HIGHLIGHTED) e.setElementState(NORMAL);
                }
        }

        //recorre los hash obteniendo los mensajes...
        for (HashMap.Entry<Integer, EntityCheck> entry : weakEntityCheck.entrySet())
        {
            EntityCheck temp = entry.getValue();
            if(!temp.isValid())
                message += "\n" + temp.name + temp.toString();
        }

        return message + "\n";
    }
}
