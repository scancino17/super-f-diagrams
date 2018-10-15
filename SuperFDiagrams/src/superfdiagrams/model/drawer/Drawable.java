/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package superfdiagrams.model.drawer;

import javafx.scene.canvas.GraphicsContext;

/**
 *
 * @author sebca
 */
public interface Drawable {
    public void draw(GraphicsContext gc);
    public void drawVertex(GraphicsContext gc);
}
