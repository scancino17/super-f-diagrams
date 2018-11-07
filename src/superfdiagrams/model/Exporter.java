/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package superfdiagrams.model;

import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

/**
 *
 * @author sebca
 */
public class Exporter {
    public static void toExport(ActionEvent e, Canvas canvas){
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
                    PDPage page = new PDPage(new PDRectangle((float)canvas.getWidth(),(float)canvas.getHeight()));
                    //agrego la pagina al pdf
                    doc.addPage(page);
                    //creo un ByteArrayOutputStream y guardo la imagen ahi
                    ByteArrayOutputStream f = new ByteArrayOutputStream();
                    ImageIO.write( renderedImage, "png", f);
                    f.flush(); //un  flush para forzar por si las moscas...
                    //Creo una imagen que acepta el pdf
                    PDImageXObject image =  PDImageXObject.createFromByteArray(doc, f.toByteArray(), "img");
                    PDPageContentStream content = new PDPageContentStream(doc, page);
                    //content.drawImage(image, 0, 0,page.getMediaBox().getWidth(), page.getMediaBox().getHeight()); //falta corregir la redimencion para que se vea bien...
                    content.drawImage(image, 0, 0, (float) canvas.getWidth(), (float) canvas.getHeight());
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
}
