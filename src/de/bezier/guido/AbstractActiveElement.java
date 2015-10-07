package de.bezier.guido;

/**
 *
 * @author Florian Jenett
 */
public abstract class AbstractActiveElement extends Basic2DElement {

    protected float clickedMouseX,

    /**
     *
     */
    clickedMouseY;
    protected float clickedPositionX,

    /**
     *
     */
    clickedPositionY;
    protected float draggedDistX,

    /**
     *
     */
    draggedDistY;

    public boolean pressed,

    /**
     *
     */
    dragged,

    /**
     *
     */
    hover,

    /**
     *
     */
    activated = true;

    /**
     *
     */
    protected boolean debug = false;

    long lastPressed = 0;

    /**
     *
     */
    public AbstractActiveElement() {
        super();
        Interactive.get().addElement(this);
    }

    /**
     *
     * @param x
     * @param y
     * @param width
     * @param height
     */
    public AbstractActiveElement(float x, float y, float width, float height) {
        super(x, y, width, height);
        Interactive.get().addElement(this);
    }

    /**
     *
     * @param tf
     */
    public void setDebug(boolean tf) {
        debug = tf;
    }

    /**
     *
     * @param yesNo
     */
    public void setActive(boolean yesNo) {
        activated = yesNo;
    }

    /**
     *
     * @return
     */
    public boolean isActive() {
        return activated;
    }

    /**
     *
     * @param mx
     * @param my
     */
    public void mouseEntered(float mx, float my) {
    }

    /**
     *
     * @param mx
     * @param my
     */
    public void mouseMoved(float mx, float my) {
    }

    /**
     *
     * @param mx
     * @param my
     */
    public void mouseExited(float mx, float my) {
    }

    /**
     *
     * @param mx
     * @param my
     */
    public void mousePressedPre(float mx, float my) {
        if (!isActive()) {
            return;
        }

        pressed = isInside(mx, my);
        if (pressed) {
            clickedPositionX = x;
            clickedPositionY = y;
            clickedMouseX = mx;
            clickedMouseY = my;
            long now = System.currentTimeMillis();
            long lp = lastPressed;
            lastPressed = now;
            if (now - lp < 200) // click-speed should go into a config, or be read from system props
            {
                mouseDoubleClicked(mx, my);
            } else {
                mousePressed(mx, my);
            }
        }
    }

	// deprecated since 0.1.0
    //public void mouseEntered ( ) { }
    //public void mouseMoved ( ) { }
    //public void mouseExited ( ) { }
    //abstract public void mousePressed ( );
    //abstract public void mouseDoubleClicked ( );
    //abstract public void mouseDragged ( float mx, float my );
    //abstract public void mouseReleased ( );

    /**
     *
     * @param mx
     * @param my
     */
        abstract public void mousePressed(float mx, float my);

    /**
     *
     * @param mx
     * @param my
     */
    abstract public void mouseDoubleClicked(float mx, float my);

    /**
     *
     * @param mx
     * @param my
     */
    public void mouseDraggedPre(float mx, float my) {
        if (!isActive()) {
            return;
        }

        dragged = pressed;
        if (dragged) {
            draggedDistX = clickedMouseX - mx;
            draggedDistY = clickedMouseY - my;
            mouseDragged(mx, my,
                clickedPositionX - draggedDistX,
                clickedPositionY - draggedDistY);
        }
    }

    /**
     *
     * @param mx
     * @param my
     * @param dx
     * @param dy
     */
    abstract public void mouseDragged(float mx, float my, float dx, float dy);

    /**
     *
     * @param mx
     * @param my
     */
    public void mouseReleasedPre(float mx, float my) {
        if (!isActive()) {
            return;
        }

        if (dragged) {
            draggedDistX = clickedMouseX - mx;
            draggedDistY = clickedMouseY - my;
        }

        if (pressed) {
            mouseReleased(mx, my);
        }
    }

    /**
     *
     * @param mx
     * @param my
     */
    abstract public void mouseReleased(float mx, float my);

    /**
     *
     * @param mx
     * @param my
     */
    public void mouseReleasedPost(float mx, float my) {
        pressed = false;
        dragged = false;
    }

    /**
     *
     * @param step
     */
    public void mouseScrolled(float step) {
    }

    /**
     *
     */
    public void draw() {
    }
}
