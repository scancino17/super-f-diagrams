/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package superfdiagrams.model;

/**
 *
 * @author sebca
 */
public class Entity implements Element{
    private String name;
    
    @Override
    public String getName() {
        return name;
    }
    
    public void setName(String name){
        this.name = name;
    }
}
