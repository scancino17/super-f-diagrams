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
    public List<ElementWrapper> findRelatedUnions(List<ElementWrapper> list, ElementWrapper element){
        List<ElementWrapper> relatedUnions = new ArrayList<>();
        
        for(ElementWrapper onList: list){
            Element union = onList.getElement();
            if(union instanceof Union){
                ElementWrapper parent = ((Union) union).getParent();
                ElementWrapper child = ((Union) union).getChild();
                
                if (parent == element || child == element)
                    relatedUnions.add(onList);
            }
        }
        
        return relatedUnions;
    }
    
    public boolean isParentPresent(List<ElementWrapper> list, ElementWrapper element){
        boolean isPresent = false;
        Element union = element.getElement();
        if (!(union instanceof Union))
            return false;
        else
            if(list.contains(((Union)union).getParent()))
                return true;
        
        return isPresent;
    }
    
    public List<ElementWrapper> obtainChildren(ElementWrapper element){
        List<ElementWrapper> childrenList = new ArrayList<>();
        
        return null;
    }
    
    private void insertChildren(List<ElementWrapper> list, ElementWrapper element){
        if(element.getElement() instanceof Union){
            ((ConnectsWrappers)element.getElement()).getChild();
        }
    }
}
