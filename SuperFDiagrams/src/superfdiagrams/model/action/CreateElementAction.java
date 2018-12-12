/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package superfdiagrams.model.action;

import java.util.List;
import superfdiagrams.model.Element;
import superfdiagrams.model.ElementBuilder;
import superfdiagrams.model.MainController;
import superfdiagrams.model.Vertex;
import superfdiagrams.model.primitive.Type;

/**
 *
 * @author sebca
 */
public class CreateElementAction implements Action{
    private Element contained;
    private List<Element> related;

    @Override
    public void redo() {
        if(contained != null)
            this.execute();
        else
            System.err.println("Contenedor de acción vacío. Cree el elemento antes.");
    }

    @Override
    public void undo() {
        MainController mainC = MainController.getController();
        
        if(related != null)
            for (Element e: related)
                mainC.removeElement(e);
        
        mainC.removeElement(contained);
    }
    
    
    public void execute(){
        MainController mainC = MainController.getController();
        
        mainC.addElement(contained);
        
        if (related != null)
            for(Element e: related)
                mainC.addElement(e);
    }
    
    public void createEntity(double x,
                             double y,
                             String label,
                             Type type)
    {
        ElementBuilder generator = this.setBuilder(x, y, label);
        Element entity = generator.generateEntity(type);
        this.setAction(entity);
        this.execute();
    }
    
    public void createRelationship(double x,
                                   double y,
                                   String label,
                                   Type type,
                                   List<Element> related)
    {
        ElementBuilder generator = this.setBuilder(x, y, label);
        Element relationship = generator.generateRelationship(related, type);
        this.setAction(relationship);
        this.execute();
    }

    public void createAttribute(double x,
                                double y,
                                String label,
                                Type type,
                                List<Element> related)
    {
        
        ElementBuilder generator = this.setBuilder(x, y, label);
        Element attribute = generator.generateAttribute(related, type);
        this.setAction(attribute);
        execute();
    }
    
    public void createHeritage(double x,
                               double y,
                               String label,
                               Type type,
                               List<Element> related)
    {
        
        ElementBuilder generator = this.setBuilder(x, y, label);
        Element heritage = generator.generateHeritage(related, type);
        this.setAction(heritage);
        this.execute();
    }
    
    public void createAgregation(double x,
                                 double y,
                                 String label,
                                 Type type,
                                 List<Element> related)
    {
        ElementBuilder generator = this.setBuilder(x, y, label);
        Element agregation = generator.generateAgregation(related, type);
        this.contained = agregation;
        this.execute();
    }
    
    private ElementBuilder setBuilder(double x, double y, String label){
        Vertex center = new Vertex(x, y);
        ElementBuilder generator = new ElementBuilder();
        
        generator.setCenter(center);
        generator.setLabel(label);
        
        return generator;
    }
    
    private void setAction(Element contained){
        this.contained = contained;
        this.related = contained.getPrimitive().getChildren();  
    }
}
