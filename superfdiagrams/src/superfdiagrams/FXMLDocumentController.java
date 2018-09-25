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
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import superfdiagrams.model.Diagram;
import superfdiagrams.model.ElementBuilder;
import superfdiagrams.model.Vertex;

/**
 *
 * @author sebca
 */
public class FXMLDocumentController implements Initializable{
    @FXML
    public Canvas canvas;
    @FXML
    public AnchorPane root;
    public Diagram diagrama;
    public enum Estado{VISTA,ENTIDAD,RELACION};
    public Estado estado;
    //public ElementBuilder elementConstructor;
    //public Vertex vertex;

   
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        estado = Estado.VISTA; //estado inicial
        GraphicsContext gc = canvas.getGraphicsContext2D();
        Timeline tl = new Timeline(
                new KeyFrame(Duration.millis(5), e -> run(gc)));
        tl.setCycleCount(Timeline.INDEFINITE);
        tl.play();
    }
    

    private void run(GraphicsContext gc){
        canvas.setOnMousePressed((MouseEvent mouseEvent) -> {
            Vertex vertex = new Vertex((int) Math.round(mouseEvent.getX())
                    ,(int) Math.round(mouseEvent.getY()));
            ElementBuilder elementConstructor = new ElementBuilder();
            elementConstructor.setCenter(vertex);
            elementConstructor.generateRelationship().draw(gc);
            System.out.println("Mouse pressed X : Y - " +
                    (int)mouseEvent.getX() + " : " + mouseEvent.getY());
        });
    }
}
