/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package superfdiagrams.model.drawer;

import javafx.scene.canvas.GraphicsContext;
import superfdiagrams.model.Element;

/**
 *
 * @author sebca
 */
public interface Drawable extends Comparable<Element> {
    public void draw(GraphicsContext gc);
    public void drawVertex(GraphicsContext gc);
}
