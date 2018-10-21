/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package superfdiagrams.model;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import superfdiagrams.FXMLDocumentController;
import static superfdiagrams.model.GeometricUtilities.checkColition;
import static superfdiagrams.model.State.MOVING_ELEMENT;
import static superfdiagrams.model.State.RELATIONSHIP;
import static superfdiagrams.model.State.VIEW;
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
    private List<ElementWrapper> elementsToRelation;
    private ElementWrapper selected;
    private double mouseXPos;
    private double mouseYPos;
    
    public static MainController getController(){
        if (mc == null)
            mc = new MainController();
        return mc;
    }
    
    private MainController(){
        stateC = StateController.getController();
        drawC = DrawController.getDrawController();
        diagramC = DiagramController.getController();
        elementsToRelation = new ArrayList<>();
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
        diagramC.addElement(element);
        drawC.addToBuffer(element);
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
        ElementWrapper element = elementConstructor.generateRelationship(elementsToRelation.size(),elementsToRelation);
        
        for(ElementWrapper e : elementsToRelation)
            e.toggleHighlighted();
        
        elementsToRelation = new ArrayList<>();
        diagramC.addElement(element);
        drawC.addToBuffer(element);
        createUnion(element);
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
    
    /**
     * Funcion que crea una linea, la cual contiene un arreglo de 2 vertices,
     * el primer vertice de la lista de la relacion y un vertice de alguna 
     * entidad que se determina con otras funciones
     * @param relation 
     */
    public void createUnion(ElementWrapper relation){  
        ElementBuilder elementConstructor = new ElementBuilder();
        ElementWrapper element;
        
        if (relation.getElement().getRelations().size() == 1){
            for (int i = 0; i < 2; i++) {                
                element = elementConstructor.generateLine(relation, relation.getElement().getRelations().get(0));
                diagramC.addElement(element);
                drawC.addToBuffer(element);
            }  
        }else{
            for(ElementWrapper entity: relation.getElement().getRelations()){
            element = elementConstructor.generateLine(relation, entity);
            diagramC.addElement(element);
            drawC.addToBuffer(element);
            }
        }
    }
    
    public void restart(){
        diagramC.newDiagram();
        elementsToRelation.clear();
        drawC.eraseBuffer();
        selected = null;
        stateC.setState(VIEW);
    }
    
    public void finishEntitySelection(){
        if (elementsToRelation.isEmpty())
            stateC.setState(VIEW);
        else
            stateC.setState(RELATIONSHIP);
    }
    
    public void runMainLoop(){
        if(selected != null && stateC.getState() == MOVING_ELEMENT )
            VertexGenerator.recalculateVertexes(selected.getVertexes(),
                                    new Vertex(mouseXPos, mouseYPos));
        
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
                    uiController.activateFinishButton();
                    ElementWrapper entity = checkColition(mouseEvent.getX(), mouseEvent.getY());
                    if (entity.getElement() instanceof Entity) {
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
            case MOVING_ELEMENT:
                if(checkColition(mouseEvent.getX(), mouseEvent.getY()) != null)
                {
                    selected.toggleHighlighted();
                    selected = null;
                    stateC.setState(VIEW);
                }
                break;
        }
        
        if (checkColition(mouseEvent.getX(), mouseEvent.getY()) != null){
            if(mouseEvent.getButton().equals(MouseButton.PRIMARY) 
            && mouseEvent.getClickCount() == 2 && stateC.getState() == VIEW)
            {
                selected = checkColition(mouseEvent.getX(), mouseEvent.getY());
                
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
    }
}
