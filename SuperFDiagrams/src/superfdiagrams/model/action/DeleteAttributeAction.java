/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package superfdiagrams.model.action;

import java.util.ArrayList;
import java.util.List;
import superfdiagrams.model.Element;
import superfdiagrams.model.Finder;
import superfdiagrams.model.MainController;
import superfdiagrams.model.primitive.Union;

/**
 *
 * @author sebca
 */
public class DeleteAttributeAction implements Action{
    private MainController mainC;
    private Element deleted;
    private List<DeleteAttributeAction> additional; 
    
    public DeleteAttributeAction(Element deleted){
        this.mainC = MainController.getController();
        this.deleted = deleted;
    }
    
    @Override
    public void redo() {
        execute();
    }

    @Override
    public void undo() {
        mainC.addElement(deleted);
        for(Element union : deleted.getPrimitive().getChildren())
            mainC.addElement(union);
        if (additional != null){
            for (DeleteAttributeAction action : additional)
                action.undo();
        }
    }
    
    public void execute(){
        for(Element union: deleted.getPrimitive().getChildren())
            mainC.removeElement(union);
        
        mainC.removeElement(deleted);
        
        List<Element> parents = new Finder().findRelatedParentUnions(mainC.fetchElements(), deleted);
        
        if (parents.isEmpty())
            return;
        
        if(additional != null){
            for(DeleteAttributeAction action: additional){
                action.execute();
            }
        } else
            for(Element union : parents){
                addAdditionalRemove(((Union)union.getPrimitive()).getParent());
            }
    }
    
    public void addAdditionalRemove(Element element){
        if (additional == null)
            additional = new ArrayList<>();
        
        DeleteAttributeAction action = new DeleteAttributeAction(element);
        action.execute();
        additional.add(action);
    }
}
