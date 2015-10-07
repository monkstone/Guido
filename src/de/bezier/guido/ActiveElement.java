package de.bezier.guido;

/**
 *
 * @author Florian Jenett
 */
public class ActiveElement extends AbstractActiveElement {

    /**
     *
     * @param x
     * @param y
     * @param width
     * @param height
     */
    public ActiveElement(float x, float y, float width, float height) {
        super(x, y, width, height);
    }

    // mouseEntered, mouseMoved, mouseExited missing?

    /**
     *
     * @param mx
     * @param my
     */
        @Override
    public void mousePressed(float mx, float my) {
    }

    /**
     *
     * @param mx
     * @param my
     */
    @Override
    public void mouseDoubleClicked(float mx, float my) {
    }

    /**
     *
     * @param mx
     * @param my
     * @param dx
     * @param dy
     */
    @Override
    public void mouseDragged(float mx, float my, float dx, float dy) {
    }

    /**
     *
     * @param mx
     * @param my
     */
    @Override
    public void mouseReleased(float mx, float my) {
    }

	// deprecated since 0.1.0
//public void mousePressed ( ){}
//public void mouseDoubleClicked ( ){}
//public void mouseDragged ( float mx, float my ){}
//public void mouseReleased ( ){}
}
