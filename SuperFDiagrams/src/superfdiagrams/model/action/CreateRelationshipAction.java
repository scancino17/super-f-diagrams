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
public class CreateRelationshipAction implements Action {
    private ElementWrapper relationship;
    private List<ElementWrapper> unions;
    
    public CreateRelationshipAction(ElementWrapper relationship){
        this.relationship = relationship;
        this.unions = relationship.getElement().getContained();
    }
    
    @Override
    public void redo() {
        MainController mainC = MainController.getController();
        for(ElementWrapper union: unions)
            mainC.addElement(union);
        mainC.addElement(relationship);
    }

    @Override
    public void undo() {
        MainController mainC = MainController.getController();
        for(ElementWrapper union: unions)
            mainC.removeElement(union);
        mainC.removeElement(relationship);
    }
    
}
