/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package superfdiagrams.model;

import java.util.ArrayList;

/**
 *
 * @author sebca
 */
public interface Element {
    public String getName();

    public void setName(String name);
    
    public ArrayList<ElementWrapper> getRelations();
    public void setRelations(ArrayList<ElementWrapper> relations);
}
