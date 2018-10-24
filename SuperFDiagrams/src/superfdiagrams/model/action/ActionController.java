/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package superfdiagrams.model.action;

import java.util.ArrayDeque;

/**
 *
 * @author sebca
 */
public class ActionController {
    private static ActionController ac;
    private ArrayDeque<Action> undoStack;
    private ArrayDeque<Action> redoStack;
    
    
    private ActionController(){
        undoStack = new ArrayDeque<>();
        redoStack = new ArrayDeque<>();
    }
    
    public static ActionController getController(){
        if (ac == null)
            ac = new ActionController();
        return ac;
    }
    
    public void restart(){
        undoStack = new ArrayDeque<>();
        redoStack = new ArrayDeque<>();
    }
    
    public void undo(){     
        if (!undoStack.isEmpty()){
            Action undoAction = undoStack.pop();
            undoAction.undo();
            //addToStack(new UndoAction(undoAction));
            redoStack.push(undoAction);
        }
    }
    
    public void redo(){
        if(!redoStack.isEmpty()){
            Action redoAction = redoStack.pop();
            redoAction.redo();
            undoStack.push(redoAction);
        }
    }
    
    public void addToStack(Action added){
        if (added != null)
            undoStack.add(added);
    }
    
    public boolean isUndoEmpty(){
        return undoStack.isEmpty();
    }
    
    public boolean isRedoEmpty(){
        return redoStack.isEmpty();
    }
}
