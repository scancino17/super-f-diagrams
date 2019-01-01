/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package superfdiagrams.model;

import java.util.List;

/**
 *
 * @author Sebastian Cancino
 */
public class ComplexElement extends Element{
    private static int num = 0;
    private int n;
    
    public ComplexElement(){
        this.n = ComplexElement.num;
        ComplexElement.num+=1;
    }
    
    public List<Element> getComposite() {
        return this.getPrimitive().getChildren();
    }

    public void setComposite(List<Element> composite) {
        this.getPrimitive().setChildren(composite);
    }

    /*@Override
    public void draw(GraphicsContext gc){
    super.draw(gc);
    
    for(Element e : this.getComposite())
    e.draw(gc);
    }*/
    
    /*@Override
    public void drawVertex(GraphicsContext gc){
    super.drawVertex(gc);
    
    for(Element e : this.getComposite())
    e.drawVertex(gc);
    }*/
    
    public Element getComposite(int i){
        if (this.getComposite() == null) return null;
        return getComposite().get(i);
    }
    
    public void addComposite(Element element){
        if (getComposite() != null) 
            this.getComposite().add(element);
    }
    
    public boolean contains(Element element){
        return this.getComposite().contains(element);
    }
    
    @Override
    public int compareTo(Element that){
        return this.getPrimitive().getPriority() - n - that.getPrimitive().getPriority();
    }
}
