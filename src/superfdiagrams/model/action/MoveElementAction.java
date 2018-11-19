/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package superfdiagrams.model.action;

import java.util.ArrayList;
import java.util.List;
import superfdiagrams.model.ElementWrapper;
import superfdiagrams.model.Vertex;
import superfdiagrams.model.VertexGenerator;

/**
 *
 * @author sebca
 */
public class MoveElementAction implements Action{
    private List<Vertex> mainPositionBefore;
    private List<Vertex> mainPositionAfter;
    private ElementWrapper mainElement;
    private List<ElementWrapper> elementRelated;

    public MoveElementAction(ElementWrapper element, List<ElementWrapper> related){
        this.mainElement = element;
        this.elementRelated = related;
        mainPositionBefore = new ArrayList<>();
        for (Vertex v : element.getVertexes())
            mainPositionBefore.add(v.obtainCopy());
    }
    
    public void getNewPosition(){
        mainPositionAfter = new ArrayList<>();
        for(Vertex v: mainElement.getVertexes())
            mainPositionAfter.add(v.obtainCopy());
    }
    
    @Override
    public void redo() {
        if (mainPositionAfter != null){
            mainElement.setVertexes(mainPositionAfter);
            VertexGenerator.recalculateNearestVertexes(elementRelated);
        }else
            System.out.println("Posición posterior a movimiento no capturada. Ignorando llamada.");
    }

    @Override
    public void undo() {
        if (mainPositionAfter != null){
            mainElement.setVertexes(mainPositionBefore);
            VertexGenerator.recalculateNearestVertexes(elementRelated);
        }else
            System.out.println("Posición posterior a movimiento no capturada. Ignorando llamada.");
    }
}
