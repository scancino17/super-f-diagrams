/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package superfdiagrams.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author sebca
 */
public class Finder {
    public List<Element> findRelatedUnions(List<Element> list, Element element){
        List<Element> relatedUnions = new ArrayList<>();
        
        for(Element onList: list){
            Primitive union = onList.getElement();
            if(union instanceof Union){
                Element parent = ((Union) union).getParent();
                Element child = ((Union) union).getChild();
                
                if (parent == element || child == element)
                    relatedUnions.add(onList);
            }
        }
        
        return relatedUnions;
    }
    
    public boolean isParentPresent(List<Element> list, Element element){
        boolean isPresent = false;
        Primitive union = element.getElement();
        if (!(union instanceof Union))
            return false;
        else
            if(list.contains(((Union)union).getParent()))
                return true;
        
        return isPresent;
    }
    
    public List<Element> obtainChildren(Element element){
        List<Element> childrenList = new ArrayList<>();
        
        return null;
    }
    
    private void insertChildren(List<Element> list, Element element){
        if(element.getElement() instanceof Union){
            ((Union)element.getElement()).getChild();
        }
    }
}
