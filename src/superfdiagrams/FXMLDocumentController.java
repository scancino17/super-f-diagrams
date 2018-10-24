/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package superfdiagrams;

import java.net.URL;
import java.util.Optional;
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
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.util.Duration;
import superfdiagrams.model.*;
import static superfdiagrams.model.State.*;



/**
 *
 * @author sebca
 */
public class FXMLDocumentController implements Initializable{
    @FXML private Canvas canvas;
    @FXML private Button finishRelationship;
    @FXML private Button entityButton;
    @FXML private Button relationButton;
    @FXML private Button btnExport;
    @FXML private Button eraseButton;
    @FXML private Button btnClose;
    @FXML private Text statusText;
    @FXML private Button attributeButton;
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
        mainC.cancelEntitySelection();
    }
    
    /**
     * Funcion que cambia el estado para dibujar entidades, se activa cuando 
     * apreta el boton correspondiente.
     */
    @FXML
    public void changeStatusRelation(){
        mainC.setState(SELECTING_ENTITIES);
    }
    
    @FXML
    public void changeStatusAtrribute(){
        mainC.setState(CHOSING_ENTITY);
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
    
    public void activateFinishButton(){
        finishRelationship.setDisable(false);
        finishRelationship.setVisible(true);
    }
    
    public void deactivateFinishButton(){
        finishRelationship.setDisable(true);
        finishRelationship.setVisible(false);
    }
    
    
    public String getElementName(String display){
        TextInputDialog dialog = new TextInputDialog("Nombre aqui...");
        dialog.setTitle("Ingrese nombre de la " + display + ".");
        dialog.setHeaderText("Ingrese nombre: ");
        dialog.setContentText("Nombre:");
        Optional<String> result = dialog.showAndWait();
        String newName = null;
        if (result.isPresent())
            newName = result.get();
        //else 
            //aqui iria quizas un mensaje de error en pantalla en caso de que le den al cancelar
        
        if (newName != null && newName.length() > 15)
            newName = newName.substring(0, 15);
        return newName;
    }
    
    public void setStatusText(String text){
        statusText.setText(text);
    }
}