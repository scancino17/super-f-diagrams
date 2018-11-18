/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package superfdiagrams.model.action;

import java.util.List;
import superfdiagrams.model.Element;
import superfdiagrams.model.primitive.Entity;
import superfdiagrams.model.MainController;
import superfdiagrams.model.primitive.Relationship;
import superfdiagrams.model.primitive.Union;


/**
 *
 * @author sebca
 */
public class DeleteElementAction implements Action{
    private Element deleted;
    private List<Element> related;
    private MainController mainC;
    
    public DeleteElementAction(Element deleted, List<Element> related){
        this.deleted = deleted;
        this.related = related;
        this.mainC = MainController.getController();
        System.out.println(related.size());
    }
    
    @Override
    public void redo() {
       execute();
    }

    @Override
    public void undo() {
        for(Element r : related){
            if(deleted.getElement() instanceof Entity){
                addUnion(r);
            } else
                mainC.addElement(r);
            }
            mainC.addElement(deleted);
    }
     
    public void execute(){
        for(Element r : related){
            if(deleted.getElement() instanceof Entity)
                removeUnion(r);
            else
                mainC.removeElement(r);
            }
            mainC.removeElement(deleted);
    }
    
    private void removeUnion(Element union){
        Element parent = ((Union)union.getElement()).getParent();
        List<Element> parentContained = parent.getElement().getChildren();
        
        if(parent.getElement() instanceof Relationship && parentContained.size() == 2){
            Element child1 = ((Union)parentContained.get(0).getElement()).getChild();
            Element child2 = ((Union)parentContained.get(1).getElement()).getChild();
            
            if (child1 == child2){
                mainC.removeElement(parentContained.get(0));
                mainC.removeElement(parentContained.get(1));
                parentContained.remove(0);
                parentContained.remove(0);
            } else {
                parentContained.remove(union);
                mainC.removeElement(union);
            }
        } else {
            parentContained.remove(union);
            mainC.removeElement(union);
        }
        
        if(parentContained.isEmpty())
            mainC.removeElement(parent);
        
        mainC.removeElement(union); 
        mainC.morphElement(parent);
    }
    
    private void addUnion(Element union){
        Element parent = ((Union)union.getElement()).getParent();
        List<Element> parentContained = parent.getElement().getChildren();
        
        if(parent.getElement() instanceof Relationship && parentContained.size() == 2){
            Element child1 = ((Union)parentContained.get(0).getElement()).getChild();
            Element child2 = ((Union)parentContained.get(1).getElement()).getChild();
            
            if (child1 == child2){
                mainC.removeElement(parentContained.get(0));
                parentContained.remove(0);
            }
        }
        
        parent.getElement().getChildren().add(union);    
            
        if(!mainC.fetchElements().contains(parent))
            mainC.addElement(parent);
        
        mainC.addElement(union);
        mainC.morphElement(parent);
    }
}
