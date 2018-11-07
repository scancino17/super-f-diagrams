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
public class StateController {
    private static StateController sc;
    private State state;
    
    private StateController(){
        this.state = State.VIEW;
    };
    
    public static StateController getController(){
        if (sc == null)
            sc = new StateController();
        return sc;
    }
    
    public void setState(State state){
        this.state = state;
    }
    
    public State getState(){
        return state;
    }
}
