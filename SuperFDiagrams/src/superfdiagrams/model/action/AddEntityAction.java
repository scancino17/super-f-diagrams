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
    
    public boolean execute(){
        if(target.getPrimitive() instanceof Relationship){
            if(wasUnary){
                unary = target.getPrimitive().getChildren().remove(0);
                mainC.removeElement(unary);
            }
            mainC.morphElement(target,
                    target.getPrimitive().getChildren().size() + entities.size());
        }
        
        if(related.isEmpty())
            if(!generateUnions()){
                undo();
                return false;
            }

        addUnionsToTarget();
        
        return true;
    }
    
    private boolean generateUnions(){
        if(target.getPrimitive() instanceof Relationship){
            if(!relationshipUnion())
                return false;
        }else if (target.getPrimitive() instanceof Heritage)
            heritageUnion();
        
        return true;
    }
    
    private boolean relationshipUnion(){
        ElementBuilder builder = new ElementBuilder();
        for(Element added : entities){
            String type = FXMLDocumentController.askCardinality(added.getPrimitive().getLabel());
            if (type.equalsIgnoreCase("0"))
                return false;
            related.add(builder.generateLine(target, added, type));
        }
        return true;
    }
    
    private void heritageUnion(){
        ElementBuilder builder = new ElementBuilder();
        for(Element added : entities){
            Element union = builder.generateLine(target, added);
            union.getPrimitive().setType(UNION_HERITAGE);
            union.getDrawer().setType(UNION_HERITAGE);
            related.add(union);
        }
    }
    
    private void addUnionsToTarget(){
        for(Element union : related){
            target.getPrimitive().getChildren().add(union);
            mainC.addElement(union);
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
