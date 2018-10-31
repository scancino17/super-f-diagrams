/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package superfdiagrams.model.action;

import superfdiagrams.model.ElementWrapper;
import superfdiagrams.model.MainController;

/**
 *
 * @author sebca
 */
public class CreateElementAction implements Action{
    private ElementWrapper container;
    
    public CreateElementAction(ElementWrapper element){
        container = element;
    }
    
    @Override
    public void redo() {
        MainController.getController().addElement(container);
    }

    @Override
    public void undo() {
        MainController.getController().removeElement(container);
    }
    
}