/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package superfdiagrams.model.action;


import superfdiagrams.model.Element;
import superfdiagrams.model.ElementBuilder;


/**
 *
 * @author 19807862
 */
public class RenameElementAction implements Action{
    private String oldName;
    private String name;
    private Element contained;
    
    public RenameElementAction(Element contained, String name){
        this.contained = contained;
        this.name = name;
        this.oldName = contained.getElement().getLabel();
    }
    
    @Override
    public void redo() {
        execute();
    }

    @Override
    public void undo() {
        this.contained.getElement().setLabel(oldName);
        new ElementBuilder().resize(contained);
    }
    
    public void execute(){
        this.contained.getElement().setLabel(name);
        new ElementBuilder().resize(contained);
    }
}
