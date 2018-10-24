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
public interface ConnectsWrappers {
    public ElementWrapper getParent();
    public ElementWrapper getChild();
    public void setParent(ElementWrapper element);
    public void setChild(ElementWrapper element);
}
