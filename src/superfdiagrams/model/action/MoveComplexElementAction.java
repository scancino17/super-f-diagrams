/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package superfdiagrams.model.action;

import java.util.ArrayList;
import java.util.List;
import superfdiagrams.model.ComplexElement;
import superfdiagrams.model.Element;
import superfdiagrams.model.Finder;
import superfdiagrams.model.MainController;
import superfdiagrams.model.primitive.Union;

/**
 *
 * @author sebca
 */
public class MoveComplexElementAction implements MoveAction{
    private final List<MoveAction> moveActions;

    public MoveComplexElementAction(ComplexElement cmpElement){
        Finder f = new Finder();
        List<Element> fetch = MainController.getController().fetchElements();
        
        this.moveActions = new ArrayList<>();
        moveActions.add(new MoveElementAction(cmpElement, f.findRelatedUnions(fetch, cmpElement)));
        for(Element e : cmpElement.getComposite()){
            if (e.getPrimitive() instanceof Union)
                continue;
            if (e instanceof ComplexElement)
                moveActions.add(new MoveComplexElementAction((ComplexElement) e));
            else
                moveActions.add(new MoveElementAction(e, f.findRelatedUnions(fetch, e)));
        }
    }
    
    @Override
    public void redo() {
        for(MoveAction action : moveActions)
            action.redo();
    }

    @Override
    public void undo() {
        for (MoveAction action : moveActions)
            action.undo();
    }
    
    @Override
    public void getNewPosition(){
        for(MoveAction action : moveActions)
            action.getNewPosition();
    }
}
