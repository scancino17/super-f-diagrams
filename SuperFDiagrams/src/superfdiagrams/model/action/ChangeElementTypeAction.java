/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package superfdiagrams.model.action;

import java.util.ArrayList;
import java.util.List;
import superfdiagrams.model.Element;
import superfdiagrams.model.primitive.Primitive;
import superfdiagrams.model.primitive.Relationship;
import superfdiagrams.model.primitive.Type;
import static superfdiagrams.model.primitive.Type.*;
import superfdiagrams.model.primitive.Union;

/**
 *
 * @author sebca
 */
public class ChangeElementTypeAction implements Action{
    private Element target;
    private List<Element> related;
    private List<Type> relatedType;
    private boolean shouldCheckWeak;
    private Type oldType;
    private Type newType;
    
    public ChangeElementTypeAction(Element target, Type newType){
        this.target = target;
        this.newType = newType;
        this.oldType = target.getPrimitive().getType();
        this.shouldCheckWeak =(newType == ROLE_WEAK &&
                               target.getPrimitive() instanceof Relationship);
            
    }

    @Override
    public void redo() {
        execute();
    }

    @Override
    public void undo() {
        target.getDrawer().setType(oldType);
        target.getPrimitive().setType(oldType);
        if(shouldCheckWeak)
            revertRelated();
    }
    
    public void execute(){
        target.getDrawer().setType(newType);
        target.getPrimitive().setType(newType);
        if(shouldCheckWeak)
            doChangeRelated();
    }
    
    public void doChangeRelated(){
        this.related = new ArrayList<>();
        this.relatedType = new ArrayList<>();
        
        Primitive parent = target.getPrimitive();
        for(Element union : parent.getChildren()){
            Union u = (Union) union.getPrimitive();
            if(u.getChild().getPrimitive().getType() == ROLE_WEAK){
                related.add(union);
                relatedType.add(u.getType());
                u.setType(ROLE_WEAK);
                union.getDrawer().setType(ROLE_WEAK);
            }
        }
    }
    
    public void revertRelated(){
        for(int i = 0; i < related.size(); i++){
            Element u = related.get(i);
            Type t = relatedType.get(i);
            
            u.getPrimitive().setType(t);
            u.getDrawer().setType(t);
        }
    }
}
