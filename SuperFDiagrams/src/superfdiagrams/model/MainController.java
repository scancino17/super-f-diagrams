/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package superfdiagrams.model;

import superfdiagrams.WeakEntityCheck;
import superfdiagrams.model.primitive.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.scene.canvas.GraphicsContext;
import superfdiagrams.FXMLDocumentController;
import static superfdiagrams.model.GeometricUtilities.checkColition;
import static superfdiagrams.model.State.*;
import superfdiagrams.model.action.ActionController;
import superfdiagrams.model.action.CreateElementAction;
import superfdiagrams.model.action.DeleteAttributeAction;
import superfdiagrams.model.action.DeleteElementAction;
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
    public HashMap<Integer, WeakEntityCheck> map;

    private MoveElementAction selectedAction;
    private boolean doubleClick;
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
        map = new HashMap<Integer, WeakEntityCheck>();

        currentElement = null;
        this.zoomFactor = 1;
        this.doubleClick = false;
        maxWith = 800; //defualt minimum with posible
        maxHeight = 700; //default minimum height posible

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
        String label = uiController.getElementName("entidad");

        if (label == null)
        {
            stateC.setState(VIEW);
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
        String label = uiController.getElementName("relación");

        if (label == null)
        {
            finishAction();
            return;
        }

        Type type = ROLE_STRONG;
        
        List<Element> selectedElements = selectorC.getSelected();
        for(Element element: selectedElements){
            if(element.getElement().getType() == ROLE_WEAK){
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
        String label = uiController.getElementName("atributo");
        
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
    
    /**
     * Funcion que dibuja los elementos de la lista.
     * Esta funcion va dibujando constantemente, cuando la lista se encuentra
     * vacia estara limpiando la pantalla.
     * @return true si dibujó algo, false caso contrario.
     */
    public boolean drawElements()
    {
        if (!drawC.isBufferEmpty())
        {
            drawC.doDrawLoop();
            return true;
        }
        return false;
    }

    public void restart()
    {
        actionC.restart();
        diagramC.newDiagram();
        drawC.eraseBuffer();
        selectorC.emptySelection();
        selected = null;
        currentElement = null;
        stateC.setState(VIEW);
        zoomFactor = 1;
        NameCounter.restartCounter();
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
        else
            stateC.setState(VIEW);
    }

    public void runMainLoop()
    {
        if (selected != null && stateC.getState() == MOVING_ELEMENT)
        {
            VertexGenerator.recalculateVertexes(selected.getVertexes(),
                                                new Vertex(mouseXPos,
                                                mouseYPos));
            VertexGenerator.recalculateNearestVertexes(selectedRelated);
        }

        this.setStatusText();
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
        }
    }

    public void doClickAction()
    {
        if (currentElement != null && stateC.getState() == VIEW)
            currentElement.setHighlighted(false);

        currentElement = checkColition(mouseXPos, mouseYPos);
        
        if(currentElement != null){
            switch(stateC.getState()){
                case MOVING_ELEMENT:
                    selected.setHighlighted(false);
                    selectedAction.getNewPosition();
                    selected = null;
                    selectedRelated = null;
                    selectedAction = null;
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
                    selectedRelated = new Finder().findRelatedUnions(diagramC.fetchElements(), selected);
                    selectedAction = new MoveElementAction(selected, selectedRelated);
                    actionC.addToStack(selectedAction);

                    if (!(selected.getElement() instanceof Union))
                    {
                        selected.setHighlighted(true);
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
            currentElement.setHighlighted(false);
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
        drawC.addToBuffer(element);

        if (element.getElement() instanceof Entity && element.getElement().getType() == Type.ROLE_WEAK)
            map.put(element.getElement().hashCode(), new WeakEntityCheck(element.getElement().getLabel()));
    }

    public void deleteElement(Element deleted)
    {
        List<Element> related = null;
        
        if (deleted.getElement() instanceof Attribute){
            DeleteAttributeAction action = new DeleteAttributeAction(deleted);
            action.execute();
            actionC.addToStack(action);
            return;
        }
        
        if (deleted.getElement() instanceof Entity){
            related = new Finder().findRelatedUnions(diagramC.fetchElements(), deleted); 
        } else if (deleted.getElement() instanceof Relationship
                || deleted.getElement() instanceof Heritage){
            related = deleted.getElement().getChildren();
        }
        else if (deleted.getElement() instanceof Relationship
                || deleted.getElement() instanceof Heritage)
        {
            related = deleted.getElement().getChildren();
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
        List<Element> contained = element.getElement().getChildren();

        if (contained.isEmpty())
        {
            removeElement(element);
            return;
        }

        if (element.getElement() instanceof Relationship)
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
            if(element.getElement().getType() == ROLE_WEAK){
                for (Element u : element.getElement().getChildren()){
                    if(u.getElement().getType() == ROLE_WEAK){
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
        drawC.removeFromBuffer(element);
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
        if (currentElement.getElement() instanceof Heritage)
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


    public String checkSemantics()
    {
        String message = "";
        List<Element> elements = diagramC.getDiagram().getElements();
        for (Element e : elements)
        {
            Primitive element = e.getElement();
            if (element.getType() == Type.ATTRIBUTE_PARTIAL_KEY)
            {
                if (((Union) (element.getChildren().get(0).getElement())).getChild().getElement().getType() == Type.ROLE_WEAK) //verifica tiene una entidad débil
                {
                    int key = ((Union) (element.getChildren().get(0).getElement())).getChild().getElement().hashCode();
                    WeakEntityCheck temp = map.get(key);
                    temp.partialKey = true;
                    map.replace(key, temp);
                }
            }
            else if(element instanceof Relationship)
            {
                boolean strong = false;
                int key = 0;
                for (int i = 0; i < element.getChildren().size(); ++i)
                {
                    if (((Union) (element.getChildren().get(i).getElement())).getChild().getElement().getType() == Type.ROLE_WEAK) //verifica tiene una entidad débil
                        key = ((Union) (element.getChildren().get(i).getElement())).getChild().getElement().hashCode();

                    else if (((Union) (element.getChildren().get(i).getElement())).getChild().getElement().getType() == Type.ROLE_STRONG) //verifica tiene una entidad fuerte
                        strong = true;
                }

                if(map.containsKey(key))
                {
                    WeakEntityCheck temp = map.get(key);
                    temp.strongEntity = strong;
                    map.replace(key, temp);
                }
            }
            else if(element instanceof Heritage)
            {
                String fatherName = ((Union)(element.getChildren().get(0).getElement())).getChild().getElement().getLabel();
                for(int i = 1; i< element.getChildren().size(); ++i)
                    if (((Union) (element.getChildren().get(i).getElement())).getChild().getElement().getLabel().equals(fatherName)) //verifica si entidades hijas (herencia) tiene el mismo nombre que el padre
                        message += "\n" + fatherName + " Tiene herencia con el mismo nombre";
            }
        }

        for (Map.Entry<Integer, WeakEntityCheck> entry : map.entrySet())
        {
            WeakEntityCheck temp = entry.getValue();
            if(temp.strongEntity == false || temp.partialKey == false)
                message += temp.name + temp.toString();
        }

        return message + "\n";
    }
}
