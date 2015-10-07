package de.bezier.guido;

/**
 *
 * @author Florian Jenett
 */
public class Basic2DElement {

    public float x,

    /**
     *
     */
    y;
    public float width,

    /**
     *
     */
    height;

    /**
     *
     */
    public Basic2DElement() {
    }

    /**
     *
     * @param x
     * @param y
     * @param width
     * @param height
     */
    public Basic2DElement(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     *
     * @param tx
     * @param ty
     * @return
     */
    public boolean isInside(float tx, float ty) {
        return (tx >= x && tx <= x + width && ty >= y && ty <= y + height);
    }
}
