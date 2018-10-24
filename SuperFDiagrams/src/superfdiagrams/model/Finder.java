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
}
