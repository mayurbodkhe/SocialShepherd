package com.ss.ui.event;

import java.io.Serializable;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.SubscriberExceptionContext;
import com.google.common.eventbus.SubscriberExceptionHandler;
import com.ss.ui.SocialShepherdUI;

/**
 * A simple wrapper for Guava event bus. Defines static convenience methods for
 * relevant actions.
 */
public class SocialShepherdUIEventBus implements SubscriberExceptionHandler, Serializable {

	
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final EventBus eventBus = new EventBus(this);

    public static void post(final Object event) {
    	SocialShepherdUI.getDashboardEventbus().eventBus.post(event);
    }

    public static void register(final Object object) {
    	SocialShepherdUI.getDashboardEventbus().eventBus.register(object);
    }

    public static void unregister(final Object object) {
    	SocialShepherdUI.getDashboardEventbus().eventBus.unregister(object);
    }

    @Override
    public final void handleException(final Throwable exception,
            final SubscriberExceptionContext context) {
        exception.printStackTrace();
    }
}
