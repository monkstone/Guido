// not in a package so it can access other classes
// in the Java "default package".

import java.lang.reflect.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is a .. well, a hack to be able to read / write fields of objects that
 * are not inside a package. Normally this is not permitted by the Java security
 * manager.
 */
public class DeBezierGuidoReflectionHelper {

    /**
     * Set an objects fields from matching fields in another object.
     * @param obj
     * @param args
     */
    static public void set(Object obj, Object args) {
        if (obj == null || args == null) {
            return;
        }

        Class klass = args.getClass();
        Class clazz = obj.getClass();

        while (clazz != null) {
			//System.out.println(clazz);

            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                Field argField = null;
                try {
                    argField = klass.getDeclaredField(field.getName());
                } catch (NoSuchFieldException nsfe) {
                    // ignore
                    //nsfLogger.getLogger(DeBezierGuidoReflectionHelper.class.getName()).log(Level.SEVERE, null, e);
                } catch (SecurityException e) {
                    Logger.getLogger(DeBezierGuidoReflectionHelper.class.getName()).log(Level.SEVERE, null, e);
                }
                if (argField != null) {
                    try {
                        field.set(obj, argField.get(args));
                    } catch (IllegalAccessException iae) {
                        // ignore
                        //iaLogger.getLogger(DeBezierGuidoReflectionHelper.class.getName()).log(Level.SEVERE, null, e);
                    } catch (IllegalArgumentException e) {
                        Logger.getLogger(DeBezierGuidoReflectionHelper.class.getName()).log(Level.SEVERE, null, e);
                    }
                }
            }
            clazz = clazz.getSuperclass();
        }
    }

    /**
     * Set an objects fields from an array of objects by treating subsequent
     * elements as key-value pairs.
     *
     * set( receiver, ["field1", value1, "field2", value2] )
     * @param obj
     * @param args
     */
    static public void set(Object obj, Object[] args) {
        if (obj == null || args == null || (args.length % 2) != 0) {
            return;
        }

        Class clazz; // = null;

        for (int i = 0; i < args.length; i += 2) {
            Object fieldNameObj = Array.get(args, i);
            if (fieldNameObj.getClass() != String.class) {
                continue;
            }

            String fieldName = (String) fieldNameObj;
            Object arg = Array.get(args, i + 1);

            Class klass = arg.getClass();
            clazz = obj.getClass();

            while (clazz != null) {
				//System.out.println(clazz);

                Field[] fields = clazz.getDeclaredFields();
                for (Field field : fields) {
                    field.setAccessible(true);

                    if (field.getName().equals(fieldName)
                        && field.getType().isAssignableFrom(klass)) {
                        try {
                            field.set(obj, arg);
                        } catch (IllegalAccessException iae) {
	                        // ignore
                            //Logger.getLogger(DeBezierGuidoReflectionHelper.class.getName()).log(Level.SEVERE, null, iae);
                        } catch (IllegalArgumentException e) {
                            Logger.getLogger(DeBezierGuidoReflectionHelper.class.getName()).log(Level.SEVERE, null, e);
                        }
                    }
                }
                clazz = clazz.getSuperclass();
            }
        }
    }

    /**
     * Get value of a given field from an object.
     * @param obj
     * @param field
     * @return 
     * @throws java.lang.IllegalAccessException
     */
    static public Object get(Object obj, Field field) throws java.lang.IllegalAccessException {
        if (obj == null || field == null) {
            return null;
        }

        field.setAccessible(true);

        return field.get(obj);
    }
}
