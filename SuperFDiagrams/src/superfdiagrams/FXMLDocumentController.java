/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package superfdiagrams;



import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Scanner;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import superfdiagrams.model.*;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import javax.imageio.ImageIO;

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
    public Diagram diagram = new Diagram();
    public enum Estado{VISTA,ENTIDAD,RELACION, SELECCIONARENTIDAD};
    public Estado estado;
    public ArrayList<ElementWrapper> elements = new ArrayList();
    private ArrayList<ElementWrapper> relationships = new ArrayList();
    public Scanner reader = new Scanner(System.in).useDelimiter("\n");
    public ArrayList<ElementWrapper> elementsToRelation = new ArrayList();
    boolean showVertex = false;

    /**
     * Estado inicial del canvas y del controlador
     * @param location
     * @param resources 
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        estado = Estado.VISTA; //estado inicial
        GraphicsContext gc = canvas.getGraphicsContext2D();
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
        gc.clearRect(0,0, canvas.getWidth(),canvas.getHeight());
        drawElements(gc);
        drawLine(gc);
    }

    @FXML public void btnShowVertex()
    {
        if(!showVertex)
            showVertex = true;
        else
            showVertex = false;
    }

    @FXML public void CanvasClickEvent(MouseEvent mouseEvent)
    {

        

        if(mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2) //Evento que verifica si es doble click
        {
            System.out.printf("doble click");

        }

        if(estado == Estado.ENTIDAD)
        {
            if(checkColition(new Vertex((int)mouseEvent.getX(), (int)mouseEvent.getY())) == null)
            {
                String name = reader.next();
                createNewEntity((int) Math.round(mouseEvent.getX()), (int) Math.round(mouseEvent.getY()), name);
                estado = Estado.VISTA;
            }
        }
        else if(estado== Estado.SELECCIONARENTIDAD)
        {
            if(checkColition(new Vertex((int)mouseEvent.getX(),(int)mouseEvent.getY())) != null){
              //  Union union = new Union();
                ElementWrapper entity = checkColition(new Vertex((int)mouseEvent.getX(),(int)mouseEvent.getY()));
            //    union.setEntity(entity);
                this.elementsToRelation.add(entity);
            }
        }
        else if(estado == Estado.RELACION){
            if(checkColition(new Vertex((int)mouseEvent.getX(), (int)mouseEvent.getY())) == null)
            {
                String name = "";
                createNewRelation((int) Math.round(mouseEvent.getX()), (int) Math.round(mouseEvent.getY()), name);
                estado = Estado.VISTA;
                //elementsToRelation.clear();
            }

        }
    }
    

    /**
     * Funcion que cambia el estado para dibujar entidades, se activa cuando 
     * se apreta el boton correspondiente.
     */
    @FXML
    public void changeStatusEntity(){
        estado = Estado.ENTIDAD;
    }
    
    /**
     * Funcion que cambia el estado para dibujar entidades, se activa cuando 
     * apreta el boton correspondiente.
     */
    @FXML
    public void changeStatusRelation()
    {
        estado = Estado.SELECCIONARENTIDAD;
    }
    
    /**
     * Funcion que cambia el estado para que no se haga nada cuando se hace click
     * Por ahora no se usa.
     */
    @FXML
    public void changeStatusView(){
        estado = Estado.VISTA;
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
        elements.add(elementConstructor.generateEntity());
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
        elementsToRelation = new ArrayList<>();
      //  element.getElement().setRelations(elementsToRelation);
        elements.add(element);
        relationships.add(element);
        //diagram.addRelation(relation);
        
    }
    
    /**
     * Funcion que dibuja los elementos de la lista.
     * Esta funcion va dibujando constantemente, cuando la lista se encuentra
     * vacia estara limpiando la pantalla.
     * @param gc 
     */
    public void drawElements(GraphicsContext gc){
        if (!elements.isEmpty()){
            for (int i = 0; i<elements.size() ; i++){
                elements.get(i).draw(gc);
                if(showVertex)
                    elements.get(i).drawVertex(gc);
            }
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
        elements.clear();
        relationships.clear();
        elementsToRelation.clear();

    }

    /**
     * Exporta el canvas como una imagen a png
     */
    @FXML public void Export(ActionEvent e)
    {
        FileChooser saveDialog = new FileChooser(); // crea un FileChooser
        //agrego los filtros para guardar.. (png o pdf)
        saveDialog .getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG FILE *.png", "*.png"));
        saveDialog .getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF FILE *.pdf", "*.pdf"));
        //muestro el saveDialog en pantalla y guardo creo un archivo de la direccion que se selecciona
        File file = saveDialog.showSaveDialog((Stage)((Button)e.getSource()).getScene().getWindow());
        if(file != null) // si file es distinto de null ya que si le dan a cancelar...
        {
            try
            {
                String extension = saveDialog.getSelectedExtensionFilter().getDescription(); //obtengo la descripcion de la extension

                //aqui creo la image del canvas..
                WritableImage writableImage = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
                canvas.snapshot(null, writableImage);
                RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);
                //fin de la creacion de imagen

                //si la extension termina en png solo hay que guardar la imagen asi que se escribe al archivo file
                if(extension.endsWith("png"))
                    ImageIO.write(renderedImage, "png", file);

                else
                {
                    //Creo un pdf...
                    PDDocument doc = new PDDocument();
                    //creo una pagina
                    PDPage page = new PDPage();
                    //agrego la pagina al pdf
                    doc.addPage(page);

                    //creo un ByteArrayOutputStream y guardo la imagen ahi
                    ByteArrayOutputStream f = new ByteArrayOutputStream();
                    ImageIO.write( renderedImage, "png", f);
                    f.flush(); //un  flush para forzar por si las moscas...
                    //Creo una imagen que acepta el pdf
                    PDImageXObject image =  PDImageXObject.createFromByteArray(doc, f.toByteArray(), "img");
                    PDPageContentStream content = new PDPageContentStream(doc, page);
                    content.drawImage(image, 0, 0,page.getMediaBox().getWidth(), page.getMediaBox().getHeight()); //falta corregir la redimencion para que se vea bien...
                    content.close();
                    doc.save(file);
                    doc.close();

                }
            }
            catch (IOException ex)
            {
                System.out.println("Error");
            }
        }
    }

    /**
     *
     * @param p - Lista de Vertex que conforman el poligono
     * @param q - Vertex a verificar si está contenido
     * @return true o false en caso de que el Vertex esté contenido o no (no verifica si está en los limites del polígono)
     */
    boolean PointInPolygon(List<Vertex> p, Vertex q)
    {
        boolean c = false;
        for (int i = 0; i < p.size(); i++)
        {
            int j = (i+1)%p.size();
            if ((p.get(i).getyPos() <= q.getyPos() && q.getyPos() < p.get(j).getyPos() ||
                    p.get(j).getyPos() <= q.getyPos() && q.getyPos() < p.get(i).getyPos()) &&
                    q.getxPos() < p.get(i).getxPos() + (p.get(j).getxPos() - p.get(i).getxPos()) *
                            (q.getyPos() - p.get(i).getyPos()) / (p.get(j).getyPos() - p.get(i).getyPos()))
                c = !c;
        }
        return c;
    }

    // solo para probar... recorre todos los elementos y ve si el Vertex p está pertenece a alguno
    public  ElementWrapper checkColition(Vertex p)
    {
        for(int i = 0; i< elements.size(); i++){
            if(PointInPolygon(elements.get(i).getVertexes(), p)){
                return elements.get(i);
            }
        }
        return null;
    }
    
    @FXML
    public void acceptRelation(){
        if (elementsToRelation.isEmpty())
            estado = Estado.VISTA;
        else
            estado = Estado.RELACION;
    }

    public void drawLine(GraphicsContext gc){
        if (!relationships.isEmpty()){
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(1);
            for (int i = 0; i<relationships.size(); i++){
                for (int j = 0; j < relationships.get(i).getVertexes().size(); j++){
                    Vertex vertexR = relationships.get(i).getVertexes().get(j);
                    //System.out.println(relationships.get(i).getElement().getRelations().size());
                    Vertex vertexE = relationships.get(i).getElement().getRelations().get(j).getVertexes().get(0);
                    gc.strokeLine(vertexR.getxPos(), vertexR.getyPos(), vertexE.getxPos(), vertexE.getyPos());
                }
            }
        }
    }

   /* public ArrayList<Vertex> createArray(int i){
        relationships
    }*/
}
