/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package superfdiagrams.model;

import java.util.List;

/**
 *
 * @author sebca
 */
public interface Element {
    public String getLabel();
    public void setLabel(String name);
    public List<ElementWrapper> getContained();
    public void setContained(List<ElementWrapper> contained);
}
