/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package superfdiagrams;

import java.net.URL;
import java.util.ArrayList;
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
import superfdiagrams.model.drawer.LineDrawer;
import superfdiagrams.model.primitive.Attribute;
import superfdiagrams.model.primitive.Entity;
import superfdiagrams.model.primitive.Heritage;
import superfdiagrams.model.primitive.Primitive;
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
    @FXML private Button applyChanges;
    @FXML private Button roleBtn;
    @FXML private Button dependencyBtn;
    @FXML private Button cardinalityBtn;
    @FXML private Button addBtn;
    @FXML private Button cancelBtn;
    
    @FXML private Text statusText;
    @FXML private TextField currentElementText;
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
            checkButtons(mainC.getCurrentElement());
            currentElementText.setText(mainC.getCurrentElement().getPrimitive().getLabel());
            mainC.getCurrentElement().setElementState(ElementState.HIGHLIGHTED);
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
        if(mainC.getState() != VIEW)
            activateButton(cancelBtn);
        else
            deactivateButton(cancelBtn);
        if(mainC.getState() == ADDING_ENTITY)
            deactivateButton(addBtn);
    }
    
    private void activateButton(Button button){
        button.setDisable(false);
        button.setVisible(true);
    }
    
    private void deactivateButton(Button button){
        button.setDisable(true);
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
    
    public static Type askHeritageType(){
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
    }

    @FXML private void applyChanges()
    {
        if(mainC.getCurrentElement() == null)
            return;
        mainC.renameCurrentElement(currentElementText.getText());
    }
    
    public void showElementPane(){
        editElementPane.setDisable(false);
        editElementPane.setVisible(true);
    }
    
    public void hideElementPane(){
        editElementPane.setDisable(true);
        editElementPane.setVisible(false);
    }
    
    /**
     * @author Diego Vargas
     */
    public void changeDependency()
    {
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
                    mainC.changeType(DEPENDENCY, n);
                }else if(union.getType() == DEPENDENCY){
                    mainC.changeType(ROLE_STRONG, n);
                }
            }
            mainC.clearMaps();
    }
    
    
    public static String askCardinality(String eName){
        String[] choices =  new String[]{"N",
                                         "1",};
        ChoiceDialog dialog = new ChoiceDialog(choices[0], Arrays.asList(choices));
        dialog.setHeaderText("Cardinalidad de " + eName + ": ");
        Optional<String> result = dialog.showAndWait();
        String selected = "0";
        if(result.isPresent())
            selected = result.get();
        
        return Character.toString(selected.charAt(0));
        
    }
    
    /**
     * Cambia el atributo de ciertos elementos, en este caso no añadi la relacion
     * para evitar problemas con el tema de las entidades debiles
     * @author Diego Vargas, refactorizado por Sebastian Cancino
     */
    public void editAttributeType(){
        Element target = mainC.getCurrentElement();
        Primitive primitive = target.getPrimitive();
        Type type = null;
        
        if(primitive instanceof Attribute)
            type = askAttributeType();
        else if(primitive instanceof Entity)
            type = askRoleType();
        else if (primitive instanceof Heritage)
            type = askHeritageType();
        else if (primitive instanceof Relationship)
            if(Finder.hasWeakEntity(target))
                type = askRoleType();
            else
                alert("Debe haber por lo menos una entidad débil.");
        else
            alert("Elemento inválido.");
        
        if(type != null)
            mainC.changeType(target, type);
        mainC.clearMaps();
    }
    
    /**
     * Esta funcion edita la cardinalidad, obviamente hay que asegurarse que sea
     * si y solo si una relacion
     * @author Diego Vargas
     */
    public void editCardinality(){
        if(mainC.getCurrentElement().getPrimitive() instanceof Relationship){
            int size = mainC.getCurrentElement().getPrimitive().getChildren().size();
            Relationship relation = (Relationship)mainC.getCurrentElement().getPrimitive();
            String[] choices =  new String[size];
            for (int i = 0; i < size; i++) {
                Union union = (Union)relation.getChildren().get(i).getPrimitive();
                choices[i] = i+1+" .-"+ union.getChild().getPrimitive().getLabel();
            }
            ChoiceDialog dialog = new ChoiceDialog(choices[0], Arrays.asList(choices));
            dialog.setHeaderText("Editar cardinalidad");
            Optional<String> result = dialog.showAndWait();
            String selected = "0";
            
            if(result.isPresent())
                selected = result.get();
            
            if (selected.equals("0"))
                return;
            
            int number = Integer.parseInt(Character.toString(selected.charAt(0)));
            mainC.swapCardinality(number);
        }else{
            alert("Elemento Inválido");
        }
    }
    
    /**
     * Por si acaso, para mostrar cuando hay algo malo.
     * @param s 
     */
    public void alert(String s){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(s);
        alert.showAndWait();
    }
    
    public void addEntity(){
        Element current = mainC.getCurrentElement();
        if ( current.getPrimitive() instanceof Relationship
          || current.getPrimitive() instanceof Heritage){
            SelectorController.getController().setToAdd(current);
            mainC.setState(ADDING_ENTITY);
        } else
            alert("Elemento inválido.");
    }
    
    /**
     * Muestra el cuadro y devuelve la entidad que se eligio
     * @param entities
     * @return 
     */
    public Entity showDialog(ArrayList<Entity> entities){
        int size = mainC.getDiagram().getElements().size();
            String[] choices =  new String[size];
            int j = 0;
  
            for (int i = 0; i < size; i++) {
                if (mainC.getDiagram().getElements().get(i).getPrimitive() instanceof Entity){
                    entities.add((Entity)mainC.getDiagram().getElements().get(i).getPrimitive());
                    choices[j] = j+1+" .-"+ mainC.getDiagram().getElements().get(i).getPrimitive().getLabel();
                    j++;
                }
            }
            ChoiceDialog dialog = new ChoiceDialog(choices[0], Arrays.asList(choices));
            dialog.setHeaderText("Añadir Entidad");
            Optional<String> result = dialog.showAndWait();
            String selected = "0";
            selected = result.get();
            return entities.get(Integer.parseInt(Character.toString(selected.charAt(0)))-1);
    }
    
    private void checkButtons(Element element){
        Primitive primitive = element.getPrimitive();
        
        if (primitive instanceof Heritage){
            this.deactivateButton(applyChanges);
            this.activateButton(addBtn);
        } else { 
            this.activateButton(applyChanges);
            this.deactivateButton(addBtn);
        }
        
        if (primitive instanceof Entity
          ||primitive instanceof Relationship
          ||primitive instanceof Heritage
          ||checkValidAttribute(element))
            this.activateButton(roleBtn);
        else
            this.deactivateButton(roleBtn);
        
        if(element instanceof ComplexElement)
            this.deactivateButton(roleBtn);
        
        if (primitive instanceof Relationship){
            this.activateButton(dependencyBtn);
            this.activateButton(cardinalityBtn);
            this.activateButton(addBtn);
        } else {
            this.deactivateButton(dependencyBtn);
            this.deactivateButton(cardinalityBtn);
            this.deactivateButton(addBtn);
        }
    }
    
    @FXML
    private void cancelButton(){
        mainC.finishAction();
    }
    
    private boolean checkValidAttribute(Element attr){
        Primitive p = attr.getPrimitive();
        if(!(p instanceof Attribute)){
            return false;
        }
        if(p.getType() != ATTRIBUTE_COMPOSITE &&
           p.getType() != ATTRIBUTE_GENERIC){
            return true;
        }
        else if (p.getType() == ATTRIBUTE_COMPOSITE){
            if(!Finder.findRelatedAttributes(attr).isEmpty()){
                return false;
            } else {
                return true;
            }
        }
        
        else if (p.getType() == ATTRIBUTE_GENERIC){
            Primitive child = ((Union)p.getChildren().get(0).getPrimitive()).getChild().getPrimitive();
            if(child instanceof Attribute){
                return false;
            } else {
                return true;
            }
        }
        return false;
    }
}
