/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package superfdiagrams.model.action;

import java.util.List;
import superfdiagrams.model.ElementWrapper;
import superfdiagrams.model.MainController;

/**
 *
 * @author sebca
 */
public class DeleteElementAction implements Action{
    private ElementWrapper deleted;
    private List<ElementWrapper> related;
    private List<ElementWrapper> toMorph;
    private MainController mainC;

    public DeleteElementAction(ElementWrapper deleted, List<ElementWrapper> related, List<ElementWrapper> toMorph) {
        this.deleted = deleted;
        this.related = related;
        this.toMorph = toMorph;
        this.mainC = MainController.getController();
    }
    
    @Override
    public void redo() {
        mainC.removeElement(deleted);
        for(ElementWrapper element:related){
            mainC.removeElement(element);
        }
        mainC.morphElements(toMorph);
    }

    @Override
    public void undo() {
        mainC.addElement(deleted);
        for(ElementWrapper element: related){
            mainC.addElement(element);
        }
        
        mainC.morphElements(toMorph);
    }
    
}
