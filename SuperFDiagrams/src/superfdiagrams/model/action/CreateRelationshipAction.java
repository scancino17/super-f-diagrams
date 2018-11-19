/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package superfdiagrams.model.action;

import java.util.List;
import superfdiagrams.model.Element;
import superfdiagrams.model.MainController;

/**
 *
 * @author sebca
 */
public class CreateRelationshipAction implements Action {
    private Element relationship;
    private List<Element> unions;
    
    public CreateRelationshipAction(Element relationship){
        this.relationship = relationship;
        this.unions = relationship.getElement().getChildren();
    }
    
    @Override
    public void redo() {
        MainController mainC = MainController.getController();
        for(Element union: unions)
            mainC.addElement(union);
        mainC.addElement(relationship);
    }

    @Override
    public void undo() {
        MainController mainC = MainController.getController();
        for(Element union: unions)
            mainC.removeElement(union);
        mainC.removeElement(relationship);
    }
    
}
