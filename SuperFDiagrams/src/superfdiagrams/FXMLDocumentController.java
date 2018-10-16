/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package superfdiagrams;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.function.Consumer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import superfdiagrams.model.*;

import static superfdiagrams.model.GeometricUtilities.*;
import static superfdiagrams.model.State.ENTITY;
import static superfdiagrams.model.State.MOVING_ELEMENT;
import static superfdiagrams.model.State.RELATIONSHIP;
import static superfdiagrams.model.State.SELECTING_ENTITIES;
import superfdiagrams.model.drawer.DrawController;
import static superfdiagrams.model.State.VIEW;

/**
 *
 * @author sebca
 */
public class FXMLDocumentController implements Initializable{
    @FXML public Canvas canvas;
    @FXML public Button entityButton;
    @FXML public Button relationButton;
    @FXML public Button eraseButton;
    @FXML public Button btnExport;
    @FXML public Button btnShowVertex;
    @FXML public TextArea textArea;
    @FXML public Button finishRelationship;
    
    private StateController stateC;
    private DrawController drawC;
    private DiagramController diagramC;
    
    private ElementWrapper selected;
    private double mouseXPos;
    private double mouseYPos;
    
    private VertexGenerator vGen = new VertexGenerator();
    public ArrayList<ElementWrapper> elementsToRelation = new ArrayList();

    /**
     * Estado inicial del canvas y del controlador
     * @param location
     * @param resources 
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        stateC = StateController.getController();
        diagramC = DiagramController.getController();
        diagramC.newDiagram();
        
        GraphicsContext gc = canvas.getGraphicsContext2D();
        drawC = DrawController.getDrawController();
        drawC.setGraphicsContext(gc);
        
        canvas.setOnMouseMoved(elementOnMouseDragged);
        deactivateTextArea();
        deactivateFinishButton();
        
        Timeline tl = new Timeline(
                new KeyFrame(Duration.millis(30), e -> run(gc)));
        tl.setCycleCount(Timeline.INDEFINITE);
        tl.play();
    }
    
    /**
     * Funcion que ejecuta el loop principal del canvas.
     * Aqui va todo lo relacionado con los dibujos y lo que ocurre cuando se
     * hace click.
     * @param gc 
     */
    public void run(GraphicsContext gc)
    {   
        if(selected != null
        && stateC.getState() == MOVING_ELEMENT )
            vGen.recalculateVertexes(selected.getVertexes(),
                                     new Vertex((int)mouseXPos, (int)mouseYPos)
                                    );
        
        gc.clearRect(0,0, canvas.getWidth(),canvas.getHeight());
        drawElements(gc);
    }

    @FXML public void btnShowVertex()
    {
        drawC.toggleDrawVertex();
    }

    EventHandler<MouseEvent> elementOnMouseDragged =
        (MouseEvent event) -> {
            mouseXPos = event.getX();
            mouseYPos = event.getY();
    };

    
    @FXML public void CanvasClickEvent(MouseEvent mouseEvent)
    {
        switch(stateC.getState()){
            case ENTITY:
                if(checkColition(mouseEvent.getX(), mouseEvent.getY()) == null)
                {
                    String name = textArea.getText();
                    createNewEntity((int) Math.round(mouseEvent.getX()), (int) Math.round(mouseEvent.getY()), name);
                    stateC.setState(State.VIEW);
                    deactivateTextArea();
                }
                break;
            case SELECTING_ENTITIES:   
                if(checkColition(mouseEvent.getX(), mouseEvent.getY()) != null)
                {
                    activateFinishButton();
                    ElementWrapper entity = checkColition(mouseEvent.getX(), mouseEvent.getY());
                    if (entity.getElement() instanceof Entity) {
                        entity.toggleHighlighted();
                        if (!elementsToRelation.contains(entity))
                            this.elementsToRelation.add(entity);
                    }
                }
                break;
            case RELATIONSHIP:
                if(checkColition(mouseEvent.getX(), mouseEvent.getY()) == null)
                {
                    String name = textArea.getText();
                    createNewRelation((int) Math.round(mouseEvent.getX()), (int) Math.round(mouseEvent.getY()), name);
                    stateC.setState(VIEW);
                    deactivateTextArea();
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
            && mouseEvent.getClickCount() == 2)
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

    }
    

    /**
     * Funcion que cambia el estado para dibujar entidades, se activa cuando 
     * se apreta el boton correspondiente.
     */
    @FXML
    public void changeStatusEntity(){
        stateC.setState(ENTITY);
        activateTextArea();
    }
    
    /**
     * Funcion que cambia el estado para dibujar entidades, se activa cuando 
     * apreta el boton correspondiente.
     */
    @FXML
    public void changeStatusRelation(){
        stateC.setState(SELECTING_ENTITIES);
        activateTextArea();
    }
    
    /**
     * Funcion que cambia el estado para que no se haga nada cuando se hace click
     * Por ahora no se usa.
     */
    @FXML
    public void changeStatusView(){
        stateC.setState(VIEW);
    }
    
    /**
     * Funcion que crea una nueva entidad y la guarda en la lista
     * @param posX
     * @param posY
     * @param name
     */
    public void createNewEntity(int posX, int posY, String name){
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
    public void createNewRelation(int posX, int posY, String name){
        System.out.println("el tamaño:"+ elementsToRelation.size());
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
    public void drawElements(GraphicsContext gc){
        if (!drawC.isBufferEmpty()){
            drawC.doDrawLoop();
        }else{
            gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        }
    }
    
    /**
     * Funcion que limpia los elementos de la lista
     * Se activa cuando se pulsa el boton de reiniciar.
     */
    @FXML
    public void erase(){
        diagramC.newDiagram();
        elementsToRelation.clear();
        drawC.eraseBuffer();
        selected = null;
        stateC.setState(VIEW);
    }

    /**
     * Exporta el canvas como una imagen a png
     */
    @FXML public void Export(ActionEvent e)
    {
        Exporter.toExport(e, canvas);
    }
    
    @FXML
    public void acceptRelation(){
        deactivateFinishButton();
        if (elementsToRelation.isEmpty())
            stateC.setState(VIEW);
        else
            stateC.setState(RELATIONSHIP);
    }
    
    @FXML
    public void close(){
        Platform.exit();
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
                element = elementConstructor.generateLine(relation, 0);
                diagramC.addElement(element);
                drawC.addToBuffer(element);
            }  
        }else{
            for(ElementWrapper entity: relation.getElement().getRelations()){
                element = elementConstructor.generateLine(relation, entity);
                diagramC.addElement(element);
                drawC.addToBuffer(element);
            }
            
            /*for (int i = 0; i < relation.getElement().getRelations().size(); i++) {
            element = elementConstructor.generateLine(relation, i);
            diagramC.addElement(element);
            drawC.addToBuffer(element);
            }*/
        }
    }
    
    public void activateTextArea(){
        textArea.setDisable(false);
        textArea.setVisible(true);
        textArea.requestFocus();
    }
    
    public void deactivateTextArea(){
        textArea.setText("");
        textArea.setDisable(true);
        textArea.setVisible(false);
    }
    
    public void activateFinishButton(){
        finishRelationship.setDisable(false);
        finishRelationship.setVisible(true);
    }
    
    public void deactivateFinishButton(){
        finishRelationship.setDisable(true);
        finishRelationship.setVisible(false);
    }
}
