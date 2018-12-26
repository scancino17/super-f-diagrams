/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package superfdiagrams.model.primitive;

import java.util.ArrayList;
import java.util.List;
import superfdiagrams.model.Element;

/**
 *
 * @author sebca
 */
public class Union implements Primitive{
    private String label;
    private Type type;
    private Element child;
    private Element parent;
    private String cardinality = "n";
    // 0 es hijo
    // 1 es padre
    
    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public void setLabel(String name) {
        this.label = name;
    }   
    
    public String getCardinality() {
        return cardinality;
    }

    public void setCardinality(String cardinality) {
        this.cardinality = cardinality;
    }
    
    public Element getParent() {
       return parent;
    }

    public Element getChild() {
        return child;
    }

    public void setParent(Element element) {
        this.parent = element;
    }

    public void setChild(Element element) {
        this.child = element;
    }

    @Override
    public Type getType(){
        return type;
    }
    
    @Override
    public void setType(Type type){
        this.type = type;
    }

    @Override
    public List<Element> getChildren() {
        List<Element> children = new ArrayList<>();
        children.add(child);
        children.add(parent);
        return children;
    }

    /**
     * Se recomienda encarecidamente que prefiera los métodos setParent() 
     * y setChild().
     * 
     * <p>Método que define los elementos hijos de este elemento. Tomar en
     * consideración que el primer elemento de la union se consiedra el "hijo" 
     * de esta unión, y el segundo elementos de la lista se considera el padre.
     * Elementos adicionales serán ignorados.
     * @param children Lista de Elementos a ser asignada como los "hijos" de
     * esta instancia.
     */
    @Override
    public void setChildren(List<Element> children) {
        this.child = children.get(0);
        this.parent = children.get(1);      
    }
    
    @Override
    public int getPriority(){
        return 1;
    }
}
