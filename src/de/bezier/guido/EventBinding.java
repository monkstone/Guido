package de.bezier.guido;

import java.lang.reflect.Method;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EventBinding
{
	String eventName;
	WeakReference<Object> targetWeakReference;
	Method targetMethod;

	public static String getIdFor ( Object object, String event )
	{
		if ( object == null ) return "<null>" + "." + event;
		return object.getClass().getName() + "@" + object.hashCode() + "." + event;
	}

	public EventBinding ( String eventName, Object targetObject, Method targetMethod )
	{
		this.eventName = eventName;
		targetWeakReference = new WeakReference<>(targetObject);
		this.targetMethod = targetMethod;
	}

	public void send ( Object[] values )
	{
		if ( targetWeakReference != null )
		{
			Object target = targetWeakReference.get();
			if ( target != null )
			{
				if ( !targetMethod.isAccessible() )
				{
					try {
						targetMethod.setAccessible( true );
					} catch ( SecurityException se ) {
						Logger.getLogger(EventBinding.class.getName()).log(Level.SEVERE, null, se);
					}
				}
				try {
					targetMethod.invoke( target, values );
				} catch ( IllegalAccessException | IllegalArgumentException | InvocationTargetException e ) {
					Logger.getLogger(EventBinding.class.getName()).log(Level.SEVERE, null, e);

					System.err.println(String.format("EventBinding.send():\n\t-> %s\n\t() %s\n\t.. %s",
						target.toString(),
						targetMethod.toString(),
						Arrays.toString(values)
					));
				}	
			}
		}
	}
}