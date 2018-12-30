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
import superfdiagrams.model.primitive.Entity;
import superfdiagrams.model.MainController;
import superfdiagrams.model.primitive.Attribute;
import superfdiagrams.model.primitive.Heritage;
import superfdiagrams.model.primitive.Relationship;
import static superfdiagrams.model.primitive.Type.ROLE_STRONG;
import static superfdiagrams.model.primitive.Type.ROLE_WEAK;
import static superfdiagrams.model.primitive.Type.UNION_HERITAGE;
import superfdiagrams.model.primitive.Union;


/**
 *
 * @author sebca
 */
public class DeleteElementAction implements Action{
    private List<DeleteElementAction> aggregations;
    private Element deleted;
    private List<Element> related;
    private MainController mainC;
    private List <DeleteAttributeAction> attributes;
    private List <Element> heritageRelated;
    
    
    public DeleteElementAction(Element deleted, List<Element> related){
        this.deleted = deleted;
        this.related = related;
        this.mainC = MainController.getController();
        this.aggregations = new ArrayList<>();
    }
    
    @Override
    public void redo() {
       //no es lo más eficiente, pero debería impedir que se vaya a las pailas
       //en caso de sobrar tiempo, rediseñare para ver como no tener que hacer
       //todo denuevo.
       if(heritageRelated != null)
           heritageRelated = new ArrayList<>();
           
       if (attributes != null)
           attributes = new ArrayList<>();
       execute();
    }

    @Override
    public void undo() {
        for(Element r : related){
            if(deleted.getPrimitive() instanceof Entity){
                addUnion(r);
            } else mainC.addElement(r);
        }
        
        mainC.addElement(deleted);
        
        if (attributes != null)
            for(DeleteAttributeAction attribute: attributes)
               attribute.undo();
        
        if(!aggregations.isEmpty()){
            for(DeleteElementAction action : aggregations)
                action.undo();
                
            aggregations = new ArrayList<>();
        }
    }
     
    public void execute(){
        for (Element r : related) {
            if(deleted.getPrimitive() instanceof Entity)
                removeUnion(r);
            else if (deleted.getPrimitive() instanceof Relationship){
                checkAggregation(r);
                mainC.removeElement(r);
            }else
                mainC.removeElement(r);
        }
        
        if(deleted.getPrimitive() instanceof Relationship){
            List<Element> relatedParents = Finder.findRelatedParentUnions(mainC.fetchElements(), deleted);
            for(Element union : relatedParents)
                removeAttribute(union);
        }
            mainC.removeElement(deleted);
    }
    
    private void removeUnion(Element union){
        Element parent = ((Union)union.getPrimitive()).getParent();
        
        if (parent.getPrimitive() instanceof Attribute){       
            removeAttribute(parent);
            return;
        }
        
        if (parent.getPrimitive() instanceof Heritage){
            mainC.removeElement(union);
            parent.getPrimitive().getChildren().remove(union);
            
            List<Element> heritageChildren = new ArrayList<>();
            for(Element un: parent.getPrimitive().getChildren()){
                heritageChildren.add(un);
            }
            
            if (shouldRemoveHeritage(parent)){
                for(Element un : heritageChildren){
                    addToHeritageRelated(un);
                    mainC.removeElement(un);
                    parent.getPrimitive().getChildren().remove(un);
                }
                mainC.removeElement(parent);
            }
            
            return;
        }
        
        List<Element> parentContained = parent.getPrimitive().getChildren();
        
        if(parent.getPrimitive() instanceof Relationship && parentContained.size() == 2){
            Element child1 = ((Union)parentContained.get(0).getPrimitive()).getChild();
            Element child2 = ((Union)parentContained.get(1).getPrimitive()).getChild();
            
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
        
        if(parentContained.isEmpty()){
            checkAggregation(parent);
        }
        else if(parent.getPrimitive() instanceof Attribute)
            for (Element u: parentContained)
                removeUnion(u);
        
        mainC.removeElement(union);
        mainC.morphElement(parent);
    }
    
    private void addUnion(Element union){
        Element parent = ((Union)union.getPrimitive()).getParent();
        
        if(parent.getPrimitive() instanceof Attribute)
            return;
        
        if(heritageRelated != null && !heritageRelated.isEmpty()){
            for (Element u: heritageRelated){
                Element heritageParent = ((Union)u.getPrimitive()).getParent();
                
                if (!heritageParent.getPrimitive().getChildren().contains(u)){
                    heritageParent.getPrimitive().getChildren().add(u);
                    mainC.addElement(u);
                }
            }
        }
        
        List<Element> parentContained = parent.getPrimitive().getChildren();
        
        if(parent.getPrimitive() instanceof Relationship && parentContained.size() == 2){
            Element child1 = ((Union)parentContained.get(0).getPrimitive()).getChild();
            Element child2 = ((Union)parentContained.get(1).getPrimitive()).getChild();
            
            if (child1 == child2){
                mainC.removeElement(parentContained.get(0));
                parentContained.remove(0);
            }
        }  
        
        
        parent.getPrimitive().getChildren().add(union);
        
        if(!mainC.fetchElements().contains(parent)){
            mainC.addElement(parent);
        }
        
        if(!mainC.fetchElements().contains(union)){
            mainC.addElement(union);
        }
        
        mainC.morphElement(parent);
        
        for (Element u : parent.getPrimitive().getChildren()){
            if (u.getPrimitive().getType() == ROLE_WEAK){
                parent.getDrawer().setType(parent.getPrimitive().getType());
                break;
            }
        }
    }
    
    private void removeAttribute(Element deleted){
        if (attributes == null)
            attributes = new ArrayList<>();
        
        DeleteAttributeAction action = new DeleteAttributeAction(deleted);
        action.execute();
        attributes.add(action);
    }
    
    private boolean shouldRemoveHeritage(Element heritage){
        if (!(heritage.getPrimitive() instanceof Heritage))
            return false;
        
        boolean hasChild = false;
        boolean hasParent = false;
        
        List<Element> unions = heritage.getPrimitive().getChildren();
        for (Element union: unions){
            if (union.getPrimitive().getType() == ROLE_STRONG){
                hasParent = true;
            }
            
            if (union.getPrimitive().getType() == UNION_HERITAGE){
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

    private void checkAggregation(Element r) {
        List<ComplexElement> aggregationContained = Finder.findParentAggregation(r);
        
        if (aggregationContained == null)
            return;
        
        for(ComplexElement e : aggregationContained){
            DeleteElementAction action = new DeleteElementAction(e, Finder.findRelatedUnions(mainC.fetchElements(), e));
            action.execute();
            this.aggregations.add(action);    
        }
    }
}
