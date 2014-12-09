package com.ss.ui;

import org.vaadin.googleanalytics.tracking.GoogleAnalyticsTracker;
import com.ss.ui.event.SocialShepherdUIEventBus;
import com.ss.ui.view.SocialShepherdUIViewType;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.navigator.ViewProvider;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.UI;
import com.ss.ui.event.SocialShepherdUIEvent.PostViewChangeEvent;
import com.ss.ui.event.SocialShepherdUIEvent.BrowserResizeEvent;
import com.ss.ui.event.SocialShepherdUIEvent.CloseOpenWindowsEvent;

@SuppressWarnings("serial")
public class SocialShepherdUINavigator extends Navigator {

    // Provide a Google Analytics tracker id here
    private static final String TRACKER_ID = null;// "UA-658457-6";
    private GoogleAnalyticsTracker tracker;

    private static final SocialShepherdUIViewType ERROR_VIEW = SocialShepherdUIViewType.COCKPIT;
    private ViewProvider errorViewProvider;

    public SocialShepherdUINavigator(final ComponentContainer container) {
        super(UI.getCurrent(), container);

        if (TRACKER_ID != null) {
            initGATracker(TRACKER_ID);
        }
        initViewChangeListener();
        initViewProviders();

    }

    private void initGATracker(final String trackerId) {
        tracker = new GoogleAnalyticsTracker(trackerId, "none");

        // GoogleAnalyticsTracker is an extension add-on for UI so it is
        // initialized by calling .extend(UI)
        tracker.extend(UI.getCurrent());
    }

    private void initViewChangeListener() {
        addViewChangeListener(new ViewChangeListener() {

            @Override
            public boolean beforeViewChange(final ViewChangeEvent event) {
                // Since there's no conditions in switching between the views
                // we can always return true.
                return true;
            }

            @Override
            public void afterViewChange(final ViewChangeEvent event) {
            	SocialShepherdUIViewType view = SocialShepherdUIViewType.getByViewName(event
                        .getViewName());
                // Appropriate events get fired after the view is changed.
            	SocialShepherdUIEventBus.post(new PostViewChangeEvent(view));
            	SocialShepherdUIEventBus.post(new BrowserResizeEvent());
            	SocialShepherdUIEventBus.post(new CloseOpenWindowsEvent());

                if (tracker != null) {
                    // The view change is submitted as a pageview for GA tracker
                    tracker.trackPageview("/dashboard/" + event.getViewName());
                }
            }
        });
    }

    private void initViewProviders() {
        // A dedicated view provider is added for each separate view type
        for (final SocialShepherdUIViewType viewType : SocialShepherdUIViewType.values()) {
            ViewProvider viewProvider = new ClassBasedViewProvider(
                    viewType.getViewName(), viewType.getViewClass()) {

                // This field caches an already initialized view instance if the
                // view should be cached (stateful views).
                private View cachedInstance;

                @Override
                public View getView(final String viewName) {
                    View result = null;
                    if (viewType.getViewName().equals(viewName)) {
                        if (viewType.isStateful()) {
                            // Stateful views get lazily instantiated
                            if (cachedInstance == null) {
                                cachedInstance = super.getView(viewType
                                        .getViewName());
                            }
                            result = cachedInstance;
                        } else {
                            // Non-stateful views get instantiated every time
                            // they're navigated to
                            result = super.getView(viewType.getViewName());
                        }
                    }
                    return result;
                }
            };

            if (viewType == ERROR_VIEW) {
                errorViewProvider = viewProvider;
            }

            addProvider(viewProvider);
        }

        setErrorProvider(new ViewProvider() {
            @Override
            public String getViewName(final String viewAndParameters) {
                return ERROR_VIEW.getViewName();
            }

            @Override
            public View getView(final String viewName) {
                return errorViewProvider.getView(ERROR_VIEW.getViewName());
            }
        });
    }
}
