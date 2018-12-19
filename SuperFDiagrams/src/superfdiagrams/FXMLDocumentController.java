/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package superfdiagrams;

import java.net.URL;
import java.util.Arrays;
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
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import superfdiagrams.model.*;

import static superfdiagrams.model.State.*;
import superfdiagrams.model.primitive.Relationship;
import static superfdiagrams.model.primitive.Type.*;
import superfdiagrams.model.primitive.Type;
import superfdiagrams.model.primitive.Union;

/**
 *
 * @author sebca
 */
public class FXMLDocumentController implements Initializable{
    @FXML private Canvas canvas;
    @FXML private ScrollPane canvasContainer;
    @FXML private Button finishRelationship;
    @FXML private Button entityButton;
    @FXML private Button relationButton;
    @FXML private Button btnExport;
    @FXML private Button eraseButton;
    @FXML private Button btnClose;
    @FXML private Button undoButton;
    @FXML private Button redoButton;
    @FXML private Button attributeBtn;
    @FXML private Button deleteBtn;
    @FXML private Text statusText;
    @FXML private TextField currentElementText;
    @FXML private Button applyChanges;
    @FXML private TitledPane editElementPane;
    @FXML private TextArea errorText;
    
    private MainController mainC;
    private NameCounter nameC;

    /**
     * Estado inicial del canvas y del controlador
     * @param location
     * @param resources 
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mainC = MainController.getController();
        nameC = NameCounter.getCounter();
        
        GraphicsContext gc = canvas.getGraphicsContext2D();
        mainC.setContext(gc);
        mainC.newDiagram();
        mainC.setUiController(this);
        
        
        canvas.setOnMouseMoved(elementOnMouseDragged);
        deactivateFinishButton();
        deactivateButton(undoButton);
        deactivateButton(redoButton);
        hideElementPane();

        errorText.setStyle("-fx-text-fill: RED;");

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
        //autosize canvas for fit
        canvas.setHeight(Math.max(mainC.getMaxHeight(), canvasContainer.getHeight()));
        canvas.setWidth(Math.max(mainC.getMaxWith(), canvasContainer.getWidth()));
        //end autosize

        mainC.runMainLoop();
        gc.clearRect(0,0, canvas.getWidth(),canvas.getHeight());
        mainC.drawElements();
        checkButtonStatus();

        errorText.setText(mainC.checkSemantics());

    }

    @FXML public void btnShowVertex()
    {
        mainC.toggleDrawVertex();
    }

    private final EventHandler<MouseEvent> elementOnMouseDragged =
        (MouseEvent event) -> {
            mainC.setMousePos(event.getX(), event.getY());
    };

    
    @FXML public void CanvasClickEvent(MouseEvent mouseEvent)
    {
        mainC.setDoubleClick(mouseEvent.getButton().equals(MouseButton.PRIMARY) 
                             && mouseEvent.getClickCount() == 2 );
        mainC.doClickAction();

        //aquí cargaría toda la info del elemento para que se pueda modificar...
        if(mainC.getCurrentElement() != null && mainC.getState() == VIEW)
        {
            showElementPane();
            currentElementText.setText(mainC.getCurrentElement().getPrimitive().getLabel());
            mainC.getCurrentElement().setElementState(ElementState.NORMAL);
        }
        else
        {
            currentElementText.clear();
            hideElementPane();
        }
    }


    /**
     * Funcion que cambia el estado para dibujar entidades, se activa cuando 
     * se apreta el boton correspondiente.
     */
    @FXML
    public void changeStatusEntity(){
        mainC.setState(ENTITY);
        mainC.cancelSelection();
    }
    
    /**
     * Funcion que cambia el estado para dibujar entidades, se activa cuando 
     * apreta el boton correspondiente.
     */
    @FXML
    public void changeStatusRelation(){
        mainC.setState(SELECTING_ENTITIES);
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
        //normaliza...
        double temp = mainC.getZoomFactor();
        mainC.setZoomFactor(1);
        canvas.setWidth(mainC.getMaxWith());
        canvas.setHeight(mainC.getMaxHeight());
        //exporta
        Exporter.toExport(e, canvas);
        //vuelve al anterior antes de normalizar
        mainC.setZoomFactor(temp);
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
    
    @FXML
    public void undo(){
        mainC.undo();
    }
    
    @FXML void redo(){
        mainC.redo();
    }
    
    public void activateFinishButton(){
        activateButton(finishRelationship);
    }
    
    public void deactivateFinishButton(){
        deactivateButton(finishRelationship);
    }
    
    public void checkButtonStatus(){
        if(undoButton.isDisabled() && !mainC.isUndoEmpty())
            activateButton(undoButton);
        if(!undoButton.isDisabled() && mainC.isUndoEmpty())
            deactivateButton(undoButton);
        if(redoButton.isDisabled() && !mainC.isRedoEmpty())
            activateButton(redoButton);
        if(!redoButton.isDisabled() && mainC.isRedoEmpty())
            deactivateButton(redoButton);
    }
    
    private void activateButton(Button button){
        button.setDisable(false);
        button.setVisible(true);
    }
    
    private void deactivateButton(Button button){
        button.setDisable(true);
        //button.setVisible(false);
    }
    
    public String getElementLabel(String display){
        TextInputDialog dialog = new TextInputDialog(nameC.generateLabel(display));
        dialog.setTitle("Ingrese nombre de la " + display + ".");
        dialog.setHeaderText("Ingrese nombre: ");
        dialog.setContentText("Nombre:");
        Optional<String> result = dialog.showAndWait();
        String newName = null;
        if (result.isPresent())
            newName = result.get();
        //else 
            //aqui iria quizas un mensaje de error en pantalla en caso de que le den al cancelar
        
        return newName;
    }
    
    public void setStatusText(String text){
        //statusText.setText(text);
        ((Stage)(btnExport.getScene().getWindow())).setTitle("Super F Diagrams " + text);
    }
    
    @FXML public void canvasZoom(ScrollEvent scroll){
        double zoomFactor = mainC.getZoomFactor();
        if (zoomFactor >= 0.3 && zoomFactor <= 2.7){
            double zoom = 1.1;
            if (scroll.getDeltaY() < 0)
                zoom = 2.0 - zoom;
            mainC.setZoomFactor(mainC.getZoomFactor() * zoom);
        } else if (zoomFactor < 0.3){
            mainC.setZoomFactor(0.3);
        } else if (zoomFactor > 2.7){
            mainC.setZoomFactor(2.7);
        }
        System.out.println(mainC.getZoomFactor());
    }
    
    @FXML
    public void changeStatusAtrribute(){
        mainC.setState(CHOSING_ENTITY);
    }
    
    @FXML
    public void changeStatusHeritage(){
        mainC.setState(State.SELECTING_CHILDREN);
    }
    
    @FXML
    public void changeStatusAgregation(){
        mainC.setState(State.CREATING_AGREGATION);
    }
    
    public Type askAttributeType(){
        String[] choices =  new String[]{"1 - Derivado",
                                         "2 - Genérico",
                                         "3 - Clave",
                                         "4 - Compuesto",
                                         "5 - Multivaluado",
                                         "6 - Clave Parcial"};
        ChoiceDialog dialog = new ChoiceDialog(choices[0], Arrays.asList(choices));
        Optional<String> result = dialog.showAndWait();
        String selected = "0";
        
        if (result.isPresent()){
            selected = result.get();
            selected = selected.substring(0, 1);
        }
        
        switch(Integer.parseInt(selected)){
            case 0:
                return null;
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
    
    public Type askRoleType(){
        String[] choices =  new String[]{"1 - Normal",
                                         "2 - Débil",};
        ChoiceDialog dialog = new ChoiceDialog(choices[0], Arrays.asList(choices));
        Optional<String> result = dialog.showAndWait();
        String selected = "0";
        
        if (result.isPresent()){
            selected = result.get();
            selected = selected.substring(0, 1);
        }
        switch(Integer.parseInt(selected)){
            case 0:
                return null;
            case 2:
                return ROLE_WEAK;
            default:
                return ROLE_STRONG;
        }
    }
    
    public Type askHeritageType(){
        String[] choices =  new String[]{"1 - Disyunción",
                                         "2 - Solapamiento",};
        ChoiceDialog dialog = new ChoiceDialog(choices[0], Arrays.asList(choices));
        Optional<String> result = dialog.showAndWait();
        String selected = "0";
        
        if (result.isPresent()){
            selected = result.get();
            selected = selected.substring(0, 1);
        }
        
        switch(Integer.parseInt(selected)){
            case 1:
                return HERITAGE_D;
            case 2:
                return HERITAGE_S;
            default:
                return null;
        }
    }
    
    @FXML
    private void changeStatusDelete(){
        if(mainC.getCurrentElement() != null)
            mainC.deleteElement(mainC.getCurrentElement());
        //mainC.setState(DELETING_ELEMENT);
    }

    @FXML private void applyChanges()
    {
        if(mainC.getCurrentElement() == null)
            return;

        String oldName = mainC.getCurrentElement().getPrimitive().getLabel();
        mainC.renameCurrentElement(currentElementText.getText());
        
        EntityCheck temp = mainC.weakEntityCheck.get(mainC.getCurrentElement().getPrimitive().hashCode());
        
        if (temp == null)
            return;
        
        temp.name = currentElementText.getText();
        mainC.weakEntityCheck.replace(mainC.getCurrentElement().getPrimitive().hashCode(), temp);
        mainC.entityNanes.put(currentElementText.getText(), mainC.entityNanes.remove(oldName));
    }
    
    public void showElementPane(){
        editElementPane.setDisable(false);
        editElementPane.setVisible(true);
    }
    
    public void hideElementPane(){
        editElementPane.setDisable(true);
        editElementPane.setVisible(false);
    }
    
        public void changeDependency(){
        int size = mainC.getCurrentElement().getPrimitive().getChildren().size();
        Relationship relation = (Relationship)mainC.getCurrentElement().getPrimitive();
        String[] choices =  new String[size];
        for (int i = 0; i < size; i++) {
            Union union = (Union)relation.getChildren().get(i).getPrimitive();
            choices[i] = i+1+" .-"+ union.getChild().getPrimitive().getLabel();
        }

        ChoiceDialog dialog = new ChoiceDialog(choices[0], Arrays.asList(choices));
        dialog.setHeaderText("Cambiando dependencia: ");
        Optional<String> result = dialog.showAndWait();
        String selected = "0";
        
        if (result.isPresent()){
            selected = result.get();
            selected = selected.substring(0, 1);
            int n = Integer.parseInt(selected.substring(0, 1))-1;
            Union union = (Union)mainC.getCurrentElement().getPrimitive().getChildren().get(n).getPrimitive();
            if (union.getType() == ROLE_STRONG){
                changeType(DEPENDENCY, n);
            }else if(union.getType() == DEPENDENCY){
                changeType(ROLE_STRONG, n);
            }
        }
    }
    
    public void changeType(Type type, int n){
        mainC.getCurrentElement().getPrimitive().getChildren().get(n).getDrawer().setType(type);
        mainC.getCurrentElement().getPrimitive().getChildren().get(n).getPrimitive().setType(type);
    }
    
    public static String askCardinality(String eName){
        String[] choices =  new String[]{"1 - n",
                                         "2 - 1",};
        ChoiceDialog dialog = new ChoiceDialog(choices[0], Arrays.asList(choices));
        dialog.setHeaderText("Cardinalidad de " + eName + ": ");
        Optional<String> result = dialog.showAndWait();
        String selected = "0";
        selected = result.get();
        
        return Character.toString(selected.charAt(4));
        
    }
}
