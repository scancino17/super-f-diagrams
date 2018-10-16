/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package superfdiagrams;

import java.net.URL;
import java.util.ResourceBundle;
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
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import superfdiagrams.model.*;

import static superfdiagrams.model.State.ENTITY;
import static superfdiagrams.model.State.SELECTING_ENTITIES;
import static superfdiagrams.model.State.VIEW;

/**
 *
 * @author sebca
 */
public class FXMLDocumentController implements Initializable{
    @FXML private Canvas canvas;
    @FXML private Button entityButton;
    @FXML private Button relationButton;
    @FXML private Button eraseButton;
    @FXML private Button btnExport;
    @FXML private Button btnShowVertex;
    @FXML private TextArea textArea;
    @FXML private Button finishRelationship;
    
    private MainController mainC;

    /**
     * Estado inicial del canvas y del controlador
     * @param location
     * @param resources 
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mainC = MainController.getController();
        
        GraphicsContext gc = canvas.getGraphicsContext2D();
        mainC.setContext(gc);
        mainC.newDiagram();
        mainC.setUiController(this);
        
        
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
        mainC.runMainLoop();
        gc.clearRect(0,0, canvas.getWidth(),canvas.getHeight());
        mainC.drawElements();
    }

    @FXML public void btnShowVertex()
    {
        mainC.toggleDrawVertex();
    }

    EventHandler<MouseEvent> elementOnMouseDragged =
        (MouseEvent event) -> {
            mainC.setMousePos(event.getX(), event.getY());
    };

    
    @FXML public void CanvasClickEvent(MouseEvent mouseEvent)
    {
        mainC.doClickAction(mouseEvent);
    }
    

    /**
     * Funcion que cambia el estado para dibujar entidades, se activa cuando 
     * se apreta el boton correspondiente.
     */
    @FXML
    public void changeStatusEntity(){
        mainC.setState(ENTITY);
        activateTextArea();
    }
    
    /**
     * Funcion que cambia el estado para dibujar entidades, se activa cuando 
     * apreta el boton correspondiente.
     */
    @FXML
    public void changeStatusRelation(){
        mainC.setState(SELECTING_ENTITIES);
        activateTextArea();
    }
    
    /**
     * Funcion que cambia el estado para que no se haga nada cuando se hace click
     * Por ahora no se usa.
     */
    @FXML
    public void changeStatusView(){
        mainC.setState(VIEW);
    }
    
    /**
     * Funcion que limpia los elementos de la lista
     * Se activa cuando se pulsa el boton de reiniciar.
     */
    @FXML
    public void erase(){
        mainC.restart();
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
        mainC.finishEntitySelection();
    }
    
    @FXML
    public void close(){
        Platform.exit();
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
    
    public String getTextArea(){
        return textArea.getText();
    }
}
