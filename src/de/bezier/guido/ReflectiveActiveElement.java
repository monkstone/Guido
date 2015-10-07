package de.bezier.guido;

import java.lang.reflect.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReflectiveActiveElement extends AbstractActiveElement {

    Object listener;
    Method setter, getter;
    Method mouseEntered2,
        mouseMoved2,
        mouseExited2,
        mousePressed2,
        mouseReleased2,
        mouseDragged4,
        mouseDoubleClicked2,
        mouseScrolled1,
        isInside2,
        isActive0, setActive1,
        draw0;
    Field fieldX, fieldY, fieldWidth, fieldHeight;

    ReflectiveActiveElement(Object l) {
        super();
        init(l);
    }

    ReflectiveActiveElement(Object l, float x, float y, float width, float height) {
        super(x, y, width, height);
        init(l);
    }

    /**
     * TODO: - check and warn about unreachable functions, wrong parameters, ...
     */
    private void init(Object l) {
        try {
            Class sClass = Class.forName("DeBezierGuidoReflectionHelper");
            setter = sClass.getMethod("set", Object.class, Object.class);
            if (debug) {
                System.out.println(setter);
            }

        } catch (ClassNotFoundException | NoSuchMethodException | SecurityException e) {
            if (debug) {
                Logger.getLogger(ReflectiveActiveElement.class.getName()).log(Level.SEVERE, null, e);
            }
        }

        try {
            Class sClass = Class.forName("DeBezierGuidoReflectionHelper");
            getter = sClass.getMethod("get", Object.class, Field.class);
            if (debug) {
                System.out.println(getter);
            }

        } catch (ClassNotFoundException | NoSuchMethodException | SecurityException e) {
            if (debug) {
                Logger.getLogger(ReflectiveActiveElement.class.getName()).log(Level.SEVERE, null, e);
            }
        }

        listener = l;

        Method[] meths = getClass().getDeclaredMethods();
        for (Method m : meths) {
            try {

                Method mo = listener.getClass().getDeclaredMethod(m.getName(), m.getParameterTypes());
                if (mo != null) {
                    if (m.getName().startsWith("mouse")) {
                        int paramsLength = m.getParameterTypes().length;
                        if (m.getName().equals("mouseDragged") && paramsLength == 2) {
                            System.err.println(String.format(
                                "Callback method \"%s\" with 2 arguments is no longer supported!\n"
                                + "mouseDragged(float mx, float my) became mouseDragged(float mx, float my, float dx, float dy)",
                                m.getName(),
                                paramsLength
                            ));
                            continue;
                        } else if (paramsLength == 0) {
                            System.err.println(String.format(
                                "Callback method \"%s\" without arguments is no longer supported!\n"
                                + "%s() became %s(float mx, float my)",
                                m.getName(),
                                paramsLength,
                                m.getName()
                            ));
                            continue;
                        }
                    }

                    Field mField = getClass().getDeclaredField(m.getName() + m.getParameterTypes().length);
                    mField.set(this, mo);

                    if (debug) {
                        System.out.println(mo.getName());
                    }
                }
                // else
                // {
                // 	mo = listener.getClass().getMethod( m.getName(), m.getParameterTypes() );
                // 	if ( mo != null )
                // 	{
                // 		getClass().getDeclaredField( m.getName()+m.getParameterTypes().length ).set( this, mo );
                // 		if (debug) System.out.println( mo.getName() );
                // 	}
                // }
            } catch (NoSuchMethodException | SecurityException | NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
                if (debug) {
                    Logger.getLogger(ReflectiveActiveElement.class.getName()).log(Level.SEVERE, null, e);
                }
            }
        }

        Field[] fields = listener.getClass().getDeclaredFields();
        for (Field f : fields) {
            if (f.getType() == float.class) {
                String fName = f.getName();

                switch (fName) {
                    case "x":
                        fieldX = f;
                        if (debug) {
                            System.out.println("x");
                        }
                        break;
                    case "y":
                        fieldY = f;
                        if (debug) {
                            System.out.println("y");
                        }
                        break;
                    case "width":
                        fieldWidth = f;
                        if (debug) {
                            System.out.println("width");
                        }
                        break;
                    case "height":
                        fieldHeight = f;
                        if (debug) {
                            System.out.println("height");
                        }
                        break;
                }
            }
        }
    }

    private void updateXY() {
		// if ( fieldX != null && fieldY != null ) {
        // 	try {
        // 		x = fieldX.getFloat( listener );
        // 		y = fieldY.getFloat( listener );
        // 	} catch ( Exception e ) {
        // 		if (debug) Logger.getLogger(ReflectiveActiveElement.class.getName()).log(Level.SEVERE, null, e);
        // 	}
        // }

        // try {
        // 	setter.invoke( null, this, listener );
        // } catch ( Exception e ) {
        // 	if (debug) Logger.getLogger(ReflectiveActiveElement.class.getName()).log(Level.SEVERE, null, e);
        // }
    }

    /**
     * Activate or deactivate callback.
     *
     * Implement this in your class to be notified when Interactive.setActive()
     * changes your element.
     *
     * @param activeState the boolean state: active or not
     */
    @Override
    public void setActive(boolean activeState) {
        if (setActive1 != null) {
            try {
                setActive1.invoke(listener, activeState);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                if (debug) {
                    Logger.getLogger(ReflectiveActiveElement.class.getName()).log(Level.SEVERE, null, e);
                }
            }
        } else {
            super.setActive(activeState);
        }
    }

    /**
     * Getter callback for active state.
     *
     * Add this to your class to be notified when another element tries to read
     * your elements active state.
     *
     * @return true or false reflecting active state of element
     */
    @Override
    public boolean isActive() {
        if (isActive0 != null) {
            try {
                return (Boolean) isActive0.invoke(listener);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                if (debug) {
                    Logger.getLogger(ReflectiveActiveElement.class.getName()).log(Level.SEVERE, null, e);
                }
            }
        } else {
            return super.isActive();
        }

        return false;
    }

    /**
     * Getter for listener object
     *
     *
     * @return true or false reflecting active state of element
     */
    public Object getListener() {
        return listener;
    }

    /**
     * Deprecated since 0.1.0, use mouseEntered ( float mx, float my )
     *
     * @see de.bezier.guido.ReflectiveActiveElement#isInside(float mx,float my)
     * @deprecated Since 0.1.0, use {@link #mouseEntered(float,float)}
     */
    public void mouseEntered() {
    }

    /**
     * Callback for mouse entered with mouse position x and y.
     *
     * Implement this to be notified once the mouse pointer (cursor) enters your
     * element. See isInside() for how entering is determined.
     *
     * @param mx mouse pointer x coordinate
     * @param my mouse pointer y coordinate
     * @see de.bezier.guido.ReflectiveActiveElement#isInside(float mx,float my)
     */
    @Override
    public void mouseEntered(float mx, float my) {
        updateXY();
        try {
            if (mouseEntered2 != null) {
                mouseEntered2.invoke(listener, mx, my);
            }
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            if (debug) {
                Logger.getLogger(ReflectiveActiveElement.class.getName()).log(Level.SEVERE, null, e);
            }
        }
    }

    /**
     * Deprecated since 0.1.0, use mouseMoved( float mx, float my ) instead
     *
     * @see de.bezier.guido.ReflectiveActiveElement#isInside(float mx,float my)
     * @deprecated Since 0.1.0, use {@link #mouseMoved(float,float)}
     */
    public void mouseMoved() {
    }

    /**
     * Callback for mouse entered with mouse position x and y.
     *
     * Implement this to be notified when the mouse pointer (cursor) moves over
     * your element. See isInside() for how "over" is determined.
     *
     * @param mx mouse pointer x coordinate
     * @param my mouse pointer y coordinate
     * @see de.bezier.guido.ReflectiveActiveElement#isInside(float mx,float my)
     */
    @Override
    public void mouseMoved(float mx, float my) {
        updateXY();
        try {
            if (mouseMoved2 != null) {
                mouseMoved2.invoke(listener, mx, my);
            }
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            if (debug) {
                Logger.getLogger(ReflectiveActiveElement.class.getName()).log(Level.SEVERE, null, e);
            }
        }
    }

    /**
     * Deprecated since 0.1.0, use mouseExited( float mx, float my ) instead
     *
     * @see de.bezier.guido.ReflectiveActiveElement#isInside(float mx,float my)
     * @deprecated Since 0.1.0, use {@link #mouseExited(float,float)} instead
     */
    public void mouseExited() {
    }

    /**
     * Callback for mouse leave/exited witho mouse position x and y.
     *
     * Implement this to be notified once the mouse pointer (cursor) leaves your
     * element. See isInside() for how leaving is determined.
     *
     * @param mx mouse pointer x coordinate
     * @param my mouse pointer y coordinate
     * @see de.bezier.guido.ReflectiveActiveElement#isInside(float mx,float my)
     */
    @Override
    public void mouseExited(float mx, float my) {
        updateXY();
        try {
            if (mouseExited2 != null) {
                mouseExited2.invoke(listener, mx, my);
            }
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            if (debug) {
                Logger.getLogger(ReflectiveActiveElement.class.getName()).log(Level.SEVERE, null, e);
            }
        }
    }

    /**
     * Deprecated since 0.1.0, use mousePressed( float mx, float my ) instead
     *
     * @deprecated Since 0.1.0, use {@link #mousePressed(float,float)} instead
     */
    public void mousePressed() {
    }

    /**
     * Callback for mouse pressed with mouse position x and y.
     *
     * Implement this to be notified once the mouse is pressed on your element.
     * See isInside() for how on is determined.
     *
     * @param mx mouse pointer x coordinate
     * @param my mouse pointer y coordinate
     * @see de.bezier.guido.ReflectiveActiveElement#isInside(float mx,float my)
     */
    @Override
    public void mousePressed(float mx, float my) {
        updateXY();
        try {
            if (mousePressed2 != null) {
                mousePressed2.invoke(listener, mx, my);
            }
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            if (debug) {
                Logger.getLogger(ReflectiveActiveElement.class.getName()).log(Level.SEVERE, null, e);
            }
        }
    }

    /**
     * Deprecated since 0.1.0, use mouseDoubleClicked( float mx, float my )
     * instead
     *
     * @deprecated Since 0.1.0, use {@link #mouseDoubleClicked(float,float)}
     * instead
     */
    public void mouseDoubleClicked() {
    }

    /**
     * Callback for mouse double pressed with mouse position x and y.
     *
     * Implement this to be notified once your element has been double clicked
     * (pressed twice in a short time frame). See isInside() for how on is
     * determined.
     *
     * @param mx mouse pointer x coordinate
     * @param my mouse pointer y coordinate
     * @see de.bezier.guido.ReflectiveActiveElement#isInside(float mx,float my)
     */
    @Override
    public void mouseDoubleClicked(float mx, float my) {
        updateXY();
        try {
            if (mouseDoubleClicked2 != null) {
                mouseDoubleClicked2.invoke(listener, mx, my);
            }
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            if (debug) {
                Logger.getLogger(ReflectiveActiveElement.class.getName()).log(Level.SEVERE, null, e);
            }
        }
    }

    /**
     * Deprecated since 0.1.0, use mouseDragged( fl
     *
     * @param mx
     * @param my float dy ) instead
     *
     * @deprecated Since 0.1.0, use
     * {@link #mouseDragged(float,float,float,float)} instead
     */
    public void mouseDragged(float mx, float my) {
    }

    /**
     * Callback for mouse dragged with mouse position x/y and drag distance xd
     * and yd (difference between mouse position and click position).
     *
     * Implement this to be notified the mouse drags (moves pressed) on your
     * element. See isInside() for how on is determined.
     *
     * @param mx mouse pointer x coordinate
     * @param my mouse pointer y coordinate
     * @param dx vertical drag distance: difference between mouse pressed x and
     * current x
     * @param dy horizontal drag distance: difference between mouse pressed y
     * and current y
     * @see de.bezier.guido.ReflectiveActiveElement#isInside(float mx,float my)
     */
    @Override
    public void mouseDragged(float mx, float my, float dx, float dy) {
        updateXY();
        try {
            if (mouseDragged4 != null) {
                mouseDragged4.invoke(listener, mx, my, dx, dy);
            }
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            if (debug) {
                Logger.getLogger(ReflectiveActiveElement.class.getName()).log(Level.SEVERE, null, e);
            }
        }
    }

    /**
     * Deprecated since 0.1.0, use mouseReleased( float mx, float my ) instead
     *
     * @deprecated Since 0.1.0, use {@link #mouseReleased(float,float)} instead
     */
    public void mouseReleased() {
    }

    /**
     * Callback for mouse released with mouse position x and y.
     *
     * Implement this to be notified the mouse was released from pressing your
     * element. See isInside() for how on is determined.
     *
     * @param mx mouse pointer x coordinate
     * @param my mouse pointer y coordinate
     * @see de.bezier.guido.ReflectiveActiveElement#isInside(float mx,float my)
     */
    @Override
    public void mouseReleased(float mx, float my) {
        updateXY();
        try {
            if (mouseReleased2 != null) {
                mouseReleased2.invoke(listener, mx, my);
            }
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            if (debug) {
                Logger.getLogger(ReflectiveActiveElement.class.getName()).log(Level.SEVERE, null, e);
            }
        }
    }

    /**
     * Callback for mouse wheel / scroll with (normalized) scroll step.
     *
     * Implement this to be notified the mouse wheel was used on your element.
     * See isInside() for how on is determined.
     *
     * @param step mouse wheel step value
     * @see de.bezier.guido.ReflectiveActiveElement#isInside(float mx,float my)
     */
    @Override
    public void mouseScrolled(float step) {
        updateXY();
        try {
            if (mouseScrolled1 != null) {
                mouseScrolled1.invoke(listener, step);
            }
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            if (debug) {
                Logger.getLogger(ReflectiveActiveElement.class.getName()).log(Level.SEVERE, null, e);
            }
        }
    }

    /**
     * Callback for drawing your element.
     *
     * This is being called <em>after</em> PApplet.draw to be able to draw
     * elements on top.
     *
     * @see de.bezier.guido.ReflectiveActiveElement#isInside(float mx,float my)
     */
    @Override
    public void draw() {
        if (draw0 != null) {
            try {
                draw0.invoke(listener);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                if (debug) {
                    Logger.getLogger(ReflectiveActiveElement.class.getName()).log(Level.SEVERE, null, e);
                }
            }
        }
    }

    /**
     * Callback to determine if mouse pointer is over element.
     *
     * A simple rectangle test is automatically done by reading x,y,width,height
     * from your element if available.
     *
     * By implementing this in your class you can alter the test to work with
     * almost any shape and probably even in 3D space.
     *
     * @param mx
     * @return use pointer x coordinate
     * @param my mouse pointer y coordinate
     * @see de.bezier.guido.Interactive#insideRect(float x,float y,float
     * width,float height,float mx,float my)
     */
    @Override
    public boolean isInside(float mx, float my) {
        updateXY();
        try {
            if (isInside2 != null) {
                return ((Boolean) (isInside2.invoke(listener, mx, my)));
            }
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            if (debug) {
                Logger.getLogger(ReflectiveActiveElement.class.getName()).log(Level.SEVERE, null, e);
            }
        }
        return isInsideFromFields(mx, my);
    }

    private boolean isInsideFromFields(float mx, float my) {
        if (fieldX == null || fieldY == null || fieldWidth == null || fieldHeight == null) {
            return super.isInside(mx, my);
        } else if (getter != null) {
            try {

                float xx = (Float) (getter.invoke(null, this.listener, fieldX));
                float yy = (Float) (getter.invoke(null, this.listener, fieldY));
                float wide = (Float) (getter.invoke(null, this.listener, fieldWidth));
                float high = (Float) (getter.invoke(null, this.listener, fieldHeight));

                return Interactive.insideRect(xx, yy, wide, high, mx, my);

            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                if (debug) {
                    Logger.getLogger(ReflectiveActiveElement.class.getName()).log(Level.SEVERE, null, e);
                }
            }
        }
        System.err.println("Unable to set from fields.");
        return false;
    }
}
