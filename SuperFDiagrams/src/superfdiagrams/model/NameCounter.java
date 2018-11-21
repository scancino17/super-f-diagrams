/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package superfdiagrams.model;

/**
 *
 * @author 19807862
 */
public class NameCounter {
    private static NameCounter counter;
    private int e;
    private int r;
    private int h;
    private int a;
    
    private NameCounter(){
        startValues();
    }
    
    public String generateLabel(String type){
        return type.substring(0, 1) + parseRequest(type);
    }
    
    private int parseRequest(String label){
        String type = label.substring(0, 1);
        if (type.equalsIgnoreCase("E"))
            return ++e;
        else if (type.equalsIgnoreCase("R"))
            return ++r;
        else if (type.equalsIgnoreCase("A"))
            return ++a;
        else if (type.equalsIgnoreCase("H"))
            return ++h;
        return -1;
    }
    
    public static NameCounter getCounter(){
        if (counter == null)
            counter = new NameCounter();
        return counter;
    }
    
    public static void restartCounter(){
        counter.startValues();
    }
    
    private void startValues(){
        this.a = 0;
        this.e = 0;
        this.r = 0;
        this.h = 0;
    }
}
