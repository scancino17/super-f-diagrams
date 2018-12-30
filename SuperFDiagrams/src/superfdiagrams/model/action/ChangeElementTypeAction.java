/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package superfdiagrams.model.action;

import superfdiagrams.model.Element;
import superfdiagrams.model.primitive.Type;

/**
 *
 * @author sebca
 */
public class ChangeElementTypeAction implements Action{
    private Element target;
    private Type oldType;
    private Type newType;
    
    public ChangeElementTypeAction(Element target, Type newType){
        this.target = target;
        this.newType = newType;
        this.oldType = target.getPrimitive().getType();
    }

    @Override
    public void redo() {
        execute();
    }

    @Override
    public void undo() {
        target.getDrawer().setType(oldType);
        target.getPrimitive().setType(oldType);
    }
    
    public void execute(){
        target.getDrawer().setType(newType);
        target.getPrimitive().setType(newType);
    }
    
}
