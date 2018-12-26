/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package superfdiagrams.model.primitive;

import java.util.List;
import superfdiagrams.model.Element;

/**
 *
 * @author sebca
 */
public interface Primitive {
    public String getLabel();
    public void setLabel(String name);
    public void setType(Type type);
    public Type getType();
    public List<Element> getChildren();
    public void setChildren(List<Element> children);
    /**
     * Prioridad:
     * 0 - Agregacion
     * 1 - Union
     * 2 - Todo lo demas
     */
    public int getPriority();
}
