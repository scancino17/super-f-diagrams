/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package superfdiagrams.controller;

import java.util.List;

/**
 *
 * @author sebca
 */
public class DrawController {
    private static DrawController dc;
    private List<Drawable> buffer;
    
    private DrawController(){}
    
    public static DrawController getDrawController(){
        if(dc == null)
            dc = new DrawController();
        return dc;
    }
}
