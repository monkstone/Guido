package de.bezier.guido;

import processing.core.*;
import java.util.*;
import java.awt.event.*;
import java.lang.reflect.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <h1>This is the main element for you to use.</h1>
 *
 * <p>
 * The normal setup would be to create a class implementing some of the
 * callbacks provided by Guido and then register it with the manager.
 * </p>
 *
 * <p>
 * See <a href="ReflectiveActiveElement.html">ReflectiveActiveElement</a> for a
 * list of all available callbacks and their forms.
 * </p>
 */
public class Interactive
    implements MouseWheelListener {

    private boolean enabled = true;

    ArrayList<AbstractActiveElement> interActiveElements;
    private ArrayList<AbstractActiveElement> interActiveElementsList;

    static Interactive manager;
    PApplet papplet;

    static HashMap<String, ArrayList<EventBinding>> eventBindings;
    static Method emitMethod;

    private Interactive(PApplet papplet) {
        this.papplet = papplet;
        registerMethods(true);

        addMouseWheelListener();

    }

    final void registerMethods(boolean register) {
        if (register) {
            papplet.registerMethod("mouseEvent", this);
            papplet.registerMethod("draw", this);
            papplet.registerMethod("dispose", this);
        } else {
            papplet.unregisterMethod("mouseEvent", this);
            papplet.unregisterMethod("draw", this);
        }
    }

    /**
     * Add java.awt.event.MouseWheel listener to PApplet
     */
    private void addMouseWheelListener() {
        new Thread() {
            int runs = 0;

            @Override
            public void run() {
                runs++;
                if (manager.papplet.frame != null) {
                    manager.papplet.frame.addMouseWheelListener(manager);
                    return;
                }

                try {
                    Thread.sleep(1000);
                    if (runs < 10) {
                        start();
                    }
                } catch (Exception e) {
                    System.err.println(e);
                }
            }
        }.start();
    }

    /**
     * Main entry point for PApplet (from your sketch).
     *
     * @param papplet
     * @return
     */
    public static Interactive make(PApplet papplet) {
        if (manager == null) {
            manager = new Interactive(papplet);
        }

        return manager;
    }

    /**
     * Alternative entry point allows to force to renew the Interactive
     * instance. This solves a problem with Python mode
     *
     * @param papplet Your sketch
     * @param force Force a new instance
     * @return
     * @see #make(PApplet)
     */
    public static Interactive make(PApplet papplet, boolean force) {
        if (manager == null || force) {
            manager = new Interactive(papplet);
        }

        return manager;
    }

    /**
     * Get the actual manager instance.
     *
     * @return the Interactive instance which is the actual manager, or null
     */
    public static Interactive get() {
        if (manager == null) {
            System.err.println("You need to initialize me first with ...\n\n\tInteractive.make(this);\n");
        }
        return manager;
    }

    // ------------------------------------------
    //	static methods, states, utils
    // ------------------------------------------
    /**
     * Activate or deactivate and interface element.
     *
     * @param element the interface element
     * @param state	the state: true for active, false to deactivate
     */
    public static void setActive(Object element, boolean state) {
        if (manager != null) {
            manager.interActiveElements.stream().forEach((interActiveElement) -> {
                if (interActiveElement.getClass().equals(ReflectiveActiveElement.class)) {
                    if (((ReflectiveActiveElement) interActiveElement).listener == element) {
                        interActiveElement.setActive(state);
                    }
                } else if (interActiveElement == element) {
                    interActiveElement.setActive(state);
                }
            });
        }
    }

    /**
     * A utility function to do a simple box test.
     *
     * @param	x left coordinate
     * @param	y	top coordinate
     * @param	width width of the rectangle
     * @param	height height of the rectangle
     * @param	mx	x of point to test
     * @param	my	y of point to test
     * @return true if the point is inside the rectangle
     */
    public static boolean insideRect(float x, float y, float width, float height, float mx, float my) {
        return mx >= x && mx <= x + width && my >= y && my <= y + height;
    }

    /**
     * Add an element to the manager to be managed.
     *
     * <p>
     * The easiest way to use this is inside your elements constructor:      <code>
     *	public class YourElement {
     *		YourElement () {
     *			Interactive.add( this );
     *		}
     *	}
     * </code></p>
     *
     * @param element the element to be managed
     * @return AbstractActiveElement the internal listener
     */
    public static AbstractActiveElement add(Object element) {
        if (manager == null) {
            System.err.println("You need to call Interactive.make() first.");
            return null;
        }

        Class klass = element.getClass();
        while (klass != null) {
            if (klass == AbstractActiveElement.class) {
                return null;
            }
            klass = klass.getSuperclass();
        }

        // this adds itself to Interactive in constructor
        ReflectiveActiveElement rae = new ReflectiveActiveElement(element);
        return rae;
    }

    /**
     *
     * @param elements
     */
    public static void add(Object[] elements) {
        for (Object e : elements) {
            Interactive.add(e);
        }
    }

    /**
     * Remove an element from the manager.
     *
     * @param element the element to stop managing
     */
    public static void remove(Object element) {
        if (Interactive.get().interActiveElements != null) {
            for (Object e : Interactive.get().interActiveElements) {
                if (((ReflectiveActiveElement) e).getListener() == element) {
                    Interactive.get().interActiveElements.remove((ReflectiveActiveElement)e);
                    break;
                }
            }
        }
    }

    /**
     *
     * @param elements
     */
    public static void remove(Object[] elements) {
        for (Object e : elements) {
            Interactive.remove(e);
        }
    }

    /**
     * Trying to set a field in an object to a given value.
     *
     * @param obj
     * @param fieldName
     * @param value
     */
    public static void set(Object obj, String fieldName, Object value) {
        if (obj == null || fieldName == null || value == null) {
            System.err.println("Interactive.set() ... a value is null!");
        }
        Field field = Interactive.getField(obj, fieldName, value);
        if (field != null) {
            try {
                field.set(obj, value);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                Logger.getLogger(Interactive.class.getName()).log(Level.SEVERE, null, e);
            }
        }
    }

    /**
     * Get a field from an object by looking at the signature
     *
     * @param obj fields name and value
     * @param fieldName
     * @param value
     * @return
     */
    public static Field getField(Object obj, String fieldName, Object value) {
        Class valueClass = value.getClass();

        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field f : fields) {
            if (f.getType().isAssignableFrom(valueClass) && f.getName().equals(fieldName)) {
                return f;
            }
        }

        // what to do here?
        System.err.println("Interactive.set() ... unable to find a field with that name in given target");
        return null;
    }

    /**
     *
     * @param eventName
     * @param targetObject
     * @param targetMethodName
     */
    public static void on(String eventName, Object targetObject, String targetMethodName) {
        Interactive.onImpl(null, eventName, targetObject, targetMethodName);
    }

    /**
     *
     * @param emitterObjects
     * @param eventName
     * @param targetObject
     * @param targetMethodName
     */
    public static void on(Object[] emitterObjects, String eventName, Object targetObject, String targetMethodName) {
        if (emitterObjects != null) {
            for (Object e : emitterObjects) {
                Interactive.onImpl(e, eventName, targetObject, targetMethodName);
            }
        } else {
            System.err.println("Err: no emitter events available");
        }
    }

    /**
     *
     * @param emitterObject
     * @param eventName
     * @param targetObject
     * @param targetMethodName
     */
    public static void on(Object emitterObject, String eventName, Object targetObject, String targetMethodName) {
        Interactive.onImpl(emitterObject, eventName, targetObject, targetMethodName);
    }

    private static void onImpl(Object emitterObject, String eventName, Object targetObject, String targetMethodName) {
        Method targetMethod = null;
        try {
            Method[] meths = targetObject.getClass().getDeclaredMethods();
            for (Method m : meths) {
                if (m.getName().equals(targetMethodName)) {
                    targetMethod = m;
                }
            }
        } catch (Exception e) {
            Logger.getLogger(Interactive.class.getName()).log(Level.SEVERE, null, e);
        }
        if (targetMethod != null) {
            String bindingId = EventBinding.getIdFor(emitterObject, eventName);
            EventBinding binding = new EventBinding(eventName, targetObject, targetMethod);
            if (eventBindings == null) {
                eventBindings = new HashMap<>();
            }
            ArrayList<EventBinding> list = eventBindings.get(bindingId);
            if (list == null) {
                list = new ArrayList<>();
                eventBindings.put(bindingId, list);
            }
            list.add(binding);
        } else {
            System.err.println("Sorry, that method named " + targetMethodName + " could not be found ... ");
        }
    }

    /**
     *
     * @param eventName
     * @param eventValues
     */
    public static void send(String eventName, Object... eventValues) {
        if (emitMethod == null) {
            try {
                emitMethod = Interactive.class.getDeclaredMethod("send", new Class[]{
                    Object.class, String.class, Object[].class
                });
            } catch (NoSuchMethodException | SecurityException e) {
                Logger.getLogger(Interactive.class.getName()).log(Level.SEVERE, null, e);
            }
        }
        try {
            emitMethod.invoke(null, new Object[]{null, eventName, eventValues});
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            Logger.getLogger(Interactive.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    /**
     *
     * @param emitterObject
     * @param eventName
     * @param eventValues
     */
    public static void send(Object emitterObject, String eventName, Object... eventValues) {
        if (eventBindings == null) {
            System.err.println("No bindings exist at the moment");
            return;
        }
        String bindingId = EventBinding.getIdFor(emitterObject, eventName);
        ArrayList<EventBinding> list = eventBindings.get(bindingId);
        if (list != null) {
            list.stream().filter((binding) -> (binding != null)).forEach((binding) -> {
                binding.send(eventValues);
            });
        } else {
            System.out.println("No bindings found for: " + bindingId);
        }
    }

    /**
     *
     * @return
     */
    public static boolean isActive() {
        if (manager != null) {
            return manager.isEnabled();
        }
        return false;
    }

    /**
     *
     */
    public static void toggle() {
        if (manager != null) {
            manager.setEnabled(!manager.isEnabled());
        }
    }

    /**
     *
     */
    public static void deactivate() {
        if (manager != null) {
            manager.setEnabled(false);
        }
    }

    /**
     *
     */
    public static void activate() {
        if (manager != null) {
            manager.setEnabled(true);
        }
    }

    /**
     *
     * @param tf
     */
    public static void setActive(boolean tf) {
        if (manager != null) {
            manager.setEnabled(tf);
        }
    }

    // ------------------------------------------
    //	instance methods
    // ------------------------------------------

    /**
     *
     * @return
     */
        public boolean isEnabled() {
        return enabled;
    }

    /**
     *
     * @param tf
     */
    public void setEnabled(boolean tf) {
        enabled = tf;
    }

    /**
     * Callback for Component.addMouseWheelListener()
     *
     * @param e the mouse wheel (scroll) event
     * @see
     * java.awt.Component#addMouseWheelListener(java.awt.event.MouseWheelListener)
     */
    @Override
    public void mouseWheelMoved(java.awt.event.MouseWheelEvent e) {
        if (!enabled) {
            return;
        }
        if (interActiveElements == null) {
            return;
        }

        mouseWheelMovedImpl(e.getWheelRotation());
    }

    private void mouseWheelMovedImpl(float amount) {
        if (!enabled) {
            return;
        }
        if (interActiveElements == null) {
            return;
        }

        updateListenerList();

        interActiveElementsList.stream().filter((interActiveElement) -> !(!interActiveElement.isActive())).map((interActiveElement) -> {
            interActiveElement.mouseScrolled(amount);
            return interActiveElement;
        }).forEach((interActiveElement) -> {
            float mx = papplet.mouseX;
            float my = papplet.mouseY;
            boolean wasHover = interActiveElement.hover;
            interActiveElement.hover = interActiveElement.isInside(mx, my);
            if (interActiveElement.hover && !wasHover) {
                interActiveElement.mouseEntered(mx, my);
            } else if (!interActiveElement.hover && wasHover) {
                interActiveElement.mouseExited(mx, my);
            }
        });

        clearListenerList();
    }

    private void updateListenerList() {
        if (interActiveElements == null) {
            System.err.println("Trying to build event-list but source list is empty");
            return;
        }
        if (interActiveElementsList == null) {
            interActiveElementsList = new ArrayList<>(interActiveElements.size());
            interActiveElementsList.addAll(interActiveElements);
        }
    }

    private void clearListenerList() {
        interActiveElementsList = null;
    }

    /**
     * Add an element to the manager, mainly used internally.
     *
     * @param activeElement the element to manage
     */
    public void addElement(AbstractActiveElement activeElement) {
        if (interActiveElements == null) {
            interActiveElements = new ArrayList<>();
        }
        if (!interActiveElements.contains(activeElement)) {
            interActiveElements.add(activeElement);
        }
    }

    /**
     * Callback for PApplet.registerMethod("draw")
     *
     * @see
     * <a href="http://processing.googlecode.com/svn/trunk/processing/build/javadoc/core/processing/core/PApplet.html#registerDraw(java.lang.Object)">PApplet.registerDraw(
     * Object obj )</a>
     */
    public void draw() {
        if (!enabled) {
            return;
        }

        if (interActiveElements == null) {
            return;
        }

        updateListenerList();

        interActiveElementsList.stream().filter((interActiveElement) -> !(!interActiveElement.isActive())).forEach((interActiveElement) -> {
            interActiveElement.draw();
        });

        clearListenerList();
    }

    /**
     * Callback for PApplet.registerMethod("mouseEvent")
     *
     * @param evt
     * @see
     * <a href="http://processing.googlecode.com/svn/trunk/processing/build/javadoc/core/processing/core/PApplet.html#registerMouseEvent(java.lang.Object)">PApplet.registerMouseEvent(Object
     * obj)</a>
     */
    public void mouseEvent(processing.event.MouseEvent evt) {
        if (!enabled) {
            return;
        }

        if (interActiveElements == null) {
            return;
        }

        updateListenerList();

        switch (evt.getAction()) {
            case processing.event.MouseEvent.ENTER:
                mouseEntered(evt);
                break;
            case processing.event.MouseEvent.MOVE:
                mouseMoved(evt);
                break;
            case processing.event.MouseEvent.PRESS:
                mousePressed(evt);
                break;
            case processing.event.MouseEvent.DRAG:
                mouseDragged(evt);
                break;
            case processing.event.MouseEvent.RELEASE:
                mouseReleased(evt);
                break;
            case processing.event.MouseEvent.CLICK:
                //mousePressed( evt );
                break;
            case processing.event.MouseEvent.EXIT:
                mouseExited(evt);
                break;
            case processing.event.MouseEvent.WHEEL:
                mouseWheelMovedImpl(evt.getCount());
                break;
        }

        clearListenerList();
    }

    private void mouseEntered(processing.event.MouseEvent evt) {
    }

    private void mouseMoved(processing.event.MouseEvent evt) {
        int mx = evt.getX();
        int my = evt.getY();

        interActiveElementsList.stream().filter((interActiveElement) -> !(!interActiveElement.isActive())).forEach((interActiveElement) -> {
            boolean wasHover = interActiveElement.hover;
            interActiveElement.hover = interActiveElement.isInside(mx, my);
            if (interActiveElement.hover && !wasHover) {
                interActiveElement.mouseEntered(mx, my);
            } else if (!interActiveElement.hover && wasHover) {
                interActiveElement.mouseExited(mx, my);
            } else {
                interActiveElement.mouseMoved(mx, my);
            }
        });
    }

    private void mousePressed(processing.event.MouseEvent evt) {
        int mx = evt.getX();
        int my = evt.getY();

        interActiveElementsList.stream().filter((interActiveElement) -> !(!interActiveElement.isActive())).filter((interActiveElement) -> (interActiveElement.hover)).forEach((interActiveElement) -> {
            interActiveElement.mousePressedPre(mx, my);
        });
    }

    private void mouseDragged(processing.event.MouseEvent evt) {
        int mx = evt.getX();
        int my = evt.getY();

        interActiveElementsList.stream().filter((interActiveElement) -> !(!interActiveElement.isActive())).filter((interActiveElement) -> (interActiveElement.hover)).forEach((interActiveElement) -> {
            interActiveElement.mouseDraggedPre(mx, my);
        });
    }

    private void mouseReleased(processing.event.MouseEvent evt) {
        int mx = evt.getX();
        int my = evt.getY();

        interActiveElementsList.stream().filter((interActiveElement) -> !(!interActiveElement.isActive())).filter((interActiveElement) -> !(!interActiveElement.hover)).map((interActiveElement) -> {
            interActiveElement.mouseReleasedPre(mx, my);
            return interActiveElement;
        }).forEach((interActiveElement) -> {
            interActiveElement.mouseReleasedPost(mx, my);
        });
    }

    private void mouseExited(processing.event.MouseEvent evt) {
    }

    /**
     *
     */
    public void dispose() {
        registerMethods(false);
    }
}
