/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package superfdiagrams.model.action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import superfdiagrams.model.Element;
import superfdiagrams.model.primitive.Entity;
import superfdiagrams.model.MainController;
import superfdiagrams.model.primitive.Attribute;
import superfdiagrams.model.primitive.Heritage;
import superfdiagrams.model.primitive.Relationship;
import static superfdiagrams.model.primitive.Type.ROLE_STRONG;
import static superfdiagrams.model.primitive.Type.UNION_HERITAGE;
import superfdiagrams.model.primitive.Union;


/**
 *
 * @author sebca
 */
public class DeleteElementAction implements Action{
    private Element deleted;
    private List<Element> related;
    private MainController mainC;
    private List <DeleteAttributeAction> attributes;
    private List <Element> heritageRelated;
    
    
    public DeleteElementAction(Element deleted, List<Element> related){
        this.deleted = deleted;
        this.related = related;
        this.mainC = MainController.getController();
        System.out.println(related.size());
    }
    
    @Override
    public void redo() {
       //no es lo más eficiente, pero debería impedir que se vaya a las pailas
       //en caso de sobrar tiempo, rediseñare para ver como no tener que hacer
       //todo denuevo.
       if (attributes != null)
           attributes = new ArrayList<>();
       execute();
    }

    @Override
    public void undo() {
        for(Element r : related){
            if(deleted.getElement() instanceof Entity){
                addUnion(r);
            } else mainC.addElement(r);
        }
        
        mainC.addElement(deleted);
        
        if (attributes != null)
            for(DeleteAttributeAction attribute: attributes)
               attribute.undo();
    }
     
    public void execute(){
        for (Element r : related) {
            if(deleted.getElement() instanceof Entity)
                removeUnion(r);
            else
                mainC.removeElement(r);
        }
            mainC.removeElement(deleted);
    }
    
    private void removeUnion(Element union){
        Element parent = ((Union)union.getElement()).getParent();
        
        if (parent.getElement() instanceof Attribute){       
            removeAttribute(parent);
            return;
        }
        
        if (parent.getElement() instanceof Heritage){
            mainC.removeElement(union);
            parent.getElement().getChildren().remove(union);
            
            if (shouldRemoveHeritage(parent)){
                System.out.println(parent.getElement().getLabel());
                for(Element un : parent.getElement().getChildren()){
                    addToHeritageRelated(un);
                    mainC.removeElement(un);
                }
                mainC.removeElement(parent);
            }
            
            return;
        }
        
        List<Element> parentContained = parent.getElement().getChildren();
        
        if(parent.getElement() instanceof Relationship && parentContained.size() == 2){
            Element child1 = ((Union)parentContained.get(0).getElement()).getChild();
            Element child2 = ((Union)parentContained.get(1).getElement()).getChild();
            
            if (child1 == child2){
                mainC.removeElement(parentContained.get(0));
                mainC.removeElement(parentContained.get(1));
                parentContained.remove(0);
                parentContained.remove(0);
            } else {
                parentContained.remove(union);
                mainC.removeElement(union);
            }
        } else {
            parentContained.remove(union);
            mainC.removeElement(union);
        }
        
        if(parentContained.isEmpty())
            mainC.removeElement(parent);
        else if(parent.getElement() instanceof Attribute)
            for (Element u: parentContained)
                removeUnion(u);
        
        mainC.removeElement(union); 
        mainC.morphElement(parent);
    }
    
    private void addUnion(Element union){
        Element parent = ((Union)union.getElement()).getParent();
        
        if(parent.getElement() instanceof Attribute)
            return;
        
        if(heritageRelated != null){
            for (Element u: heritageRelated){
                parent.getElement().getChildren().add(u);
                mainC.addElement(u);
            }
        }
        
        List<Element> parentContained = parent.getElement().getChildren();
        
        if(parent.getElement() instanceof Relationship && parentContained.size() == 2){
            Element child1 = ((Union)parentContained.get(0).getElement()).getChild();
            Element child2 = ((Union)parentContained.get(1).getElement()).getChild();
            
            if (child1 == child2){
                mainC.removeElement(parentContained.get(0));
                parentContained.remove(0);
            }
        }  
        
        parent.getElement().getChildren().add(union);    
        
        if(!mainC.fetchElements().contains(parent))
            mainC.addElement(parent);    
        mainC.addElement(union);
        mainC.morphElement(parent);
    }
    
    private void removeAttribute(Element deleted){
        if (attributes == null)
            attributes = new ArrayList<>();
        
        DeleteAttributeAction action = new DeleteAttributeAction(deleted);
        action.execute();
        attributes.add(action);
    }
    
    private boolean shouldRemoveHeritage(Element heritage){
        if (!(heritage.getElement() instanceof Heritage))
            return false;
        
        boolean hasChild = false;
        boolean hasParent = false;
        
        List<Element> unions = heritage.getElement().getChildren();
        for (Element union: unions){
            System.out.println(union.getElement().getType());
            if (union.getElement().getType() == ROLE_STRONG){
                hasParent = true;
            }
            
            if (union.getElement().getType() == UNION_HERITAGE){
                hasChild = true;
            }
            
            if (hasChild && hasParent)
                return false;
        }
        
        return true;
    }
    
    private void addToHeritageRelated(Element element){
        if (this.heritageRelated == null)
            heritageRelated = new ArrayList<>();
        heritageRelated.add(element);
    }
}
