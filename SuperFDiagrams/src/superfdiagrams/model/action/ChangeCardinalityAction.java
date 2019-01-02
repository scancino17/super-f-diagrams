/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package superfdiagrams.model.action;

import superfdiagrams.model.Element;
import superfdiagrams.model.drawer.LineDrawer;
import superfdiagrams.model.primitive.Union;

/**
 *
 * @author sebca
 */
public class ChangeCardinalityAction implements Action{
    private Element union;
    
    public ChangeCardinalityAction(Element union){
        this.union = union;
    }

    @Override
    public void redo() {
        execute();
    }

    @Override
    public void undo() {
        execute();
    }
    /**
     * Al ser solo 2 tipos de cardinalidad hace un swap en la union y su drawer.
     * @author Diego Vargas
     */    
    public void execute(){
        Union primitive = (Union) union.getPrimitive();
        LineDrawer drawer = (LineDrawer) union.getDrawer();
        if("N".equals(primitive.getCardinality())){
            primitive.setCardinality("1");
            drawer.setCardinality("1");
        }else{
            primitive.setCardinality("N");
            drawer.setCardinality("N");
        }
    
    }
}
