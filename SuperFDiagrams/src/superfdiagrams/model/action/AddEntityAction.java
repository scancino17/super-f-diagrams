/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package superfdiagrams.model.action;

import java.util.ArrayList;
import java.util.List;
import superfdiagrams.FXMLDocumentController;
import superfdiagrams.model.Element;
import superfdiagrams.model.ElementBuilder;
import superfdiagrams.model.MainController;
import superfdiagrams.model.primitive.Heritage;
import superfdiagrams.model.primitive.Primitive;
import superfdiagrams.model.primitive.Relationship;
import static superfdiagrams.model.primitive.Type.UNION_HERITAGE;
import superfdiagrams.model.primitive.Union;

/**
 *
 * @author Sebastian Cancino, Diego Vargas
 */
public class AddEntityAction implements Action{
    private MainController mainC;
    private Element target;
    private List<Element> entities;
    private List<Element> related;
    private boolean wasUnary;
    private Element unary;

    public AddEntityAction(Element target, List<Element> added){
        this.mainC = MainController.getController();
        this.target = target;
        this.entities = added;
        this.related = new ArrayList<>();
        checkUnary();
    }
    
    @Override
    public void redo() {
        execute();
    }

    @Override
    public void undo() {
        removeFromTarget();
        if(wasUnary && unary != null){
            target.getPrimitive().getChildren().add(unary);
            mainC.addElement(unary);
        }
        if(target.getPrimitive() instanceof Relationship)
            mainC.morphElement(target);
    }
    
    public void execute(){
        if(related.isEmpty())
            generateUnions();
        addUnionsToTarget();
        
        if(wasUnary){
            unary = target.getPrimitive().getChildren().remove(0);
            mainC.removeElement(unary);
        }
        if(target.getPrimitive() instanceof Relationship)
            mainC.morphElement(target);
    }
    
    private void generateUnions(){
        if(target.getPrimitive() instanceof Relationship){
            relationshipUnion();
        }else if (target.getPrimitive() instanceof Heritage)
            heritageUnion();
    }
    
    private void relationshipUnion(){
        ElementBuilder builder = new ElementBuilder();
        for(Element added : entities){
            String type = FXMLDocumentController.askCardinality(added.getPrimitive().getLabel());
            related.add(builder.generateLine(target, added, type));
        }
    }
    
    private void heritageUnion(){
        ElementBuilder builder = new ElementBuilder();
        for(Element added : entities){
            Element union = builder.generateLine(target, added);
            union.getPrimitive().setType(UNION_HERITAGE);
            union.getDrawer().setType(UNION_HERITAGE);
        }
    }
    
    private void addUnionsToTarget(){
        for(Element union : related){
            target.getPrimitive().getChildren().add(union);
            mainC.fetchElements().add(union);
        }
    }
    
    private  void removeFromTarget(){
        for(Element union : related){
            target.getPrimitive().getChildren().remove(union);
            mainC.removeElement(union);
        }
    }
    
    private void checkUnary(){
        if ( target.getPrimitive() instanceof Relationship
          && target.getPrimitive().getChildren().size() == 2){
            Primitive relation = target.getPrimitive();
            Element child1 = ((Union)relation.getChildren().get(0).getPrimitive()).getChild();
            Element child2 = ((Union)relation.getChildren().get(1).getPrimitive()).getChild();
            wasUnary = child1 == child2;
        }
    }
}
