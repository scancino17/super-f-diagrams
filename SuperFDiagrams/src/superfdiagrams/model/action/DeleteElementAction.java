/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package superfdiagrams.model.action;

import java.util.List;
import superfdiagrams.model.Attribute;
import superfdiagrams.model.ConnectsWrappers;
import superfdiagrams.model.ElementWrapper;
import superfdiagrams.model.Entity;
import superfdiagrams.model.MainController;
import superfdiagrams.model.Relationship;
import superfdiagrams.model.Union;


/**
 *
 * @author sebca
 */
public class DeleteElementAction implements Action{
    private ElementWrapper deleted;
    private List<ElementWrapper> related;
    private MainController mainC;
    
    public DeleteElementAction(ElementWrapper deleted, List<ElementWrapper> related){
        this.deleted = deleted;
        this.related = related;
        this.mainC = MainController.getController();
    }
    
    @Override
    public void redo() {
       execute();
    }

    @Override
    public void undo() {
        for(ElementWrapper r : related){
            if(deleted.getElement() instanceof Entity){
                addUnion(r);
            } else
                mainC.addElement(r);
            }
            mainC.addElement(deleted);
    }
     
    public void execute(){
        for(ElementWrapper r : related){
            if(deleted.getElement() instanceof Entity)
                removeUnion(r);
            else
                mainC.removeElement(r);
            }
            mainC.removeElement(deleted);
    }
    
    private void removeUnion(ElementWrapper union){
        ElementWrapper parent = ((ConnectsWrappers)union.getElement()).getParent();
        List<ElementWrapper> parentContained = parent.getElement().getContained();
        
        if(parent.getElement() instanceof Relationship && parentContained.size() == 2){
            ElementWrapper child1 = ((Union)parentContained.get(0).getElement()).getChild();
            ElementWrapper child2 = ((Union)parentContained.get(1).getElement()).getChild();
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
    
    private void addUnion(ElementWrapper union){
        ElementWrapper parent = ((ConnectsWrappers)union.getElement()).getParent();
        List<ElementWrapper> parentContained = parent.getElement().getContained();
        
        if(parent.getElement() instanceof Relationship && parentContained.size() == 2){
            ElementWrapper child1 = ((Union)parentContained.get(0).getElement()).getChild();
            ElementWrapper child2 = ((Union)parentContained.get(1).getElement()).getChild();
            if (child1 == child2){
                mainC.removeElement(parentContained.get(0));
                parentContained.remove(0);
            }
        }
        
        parent.getElement().getContained().add(union);    
            
        if(!mainC.fetchElements().contains(parent))
            mainC.addElement(parent);
        
        mainC.addElement(union);
        mainC.morphElement(parent);
    }
}
