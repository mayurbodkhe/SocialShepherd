package com.ss.ui;

import java.util.Locale;

import com.google.common.eventbus.Subscribe;
import com.ss.services.DBService;
import com.ss.ui.domain.User;
import com.ss.ui.event.SocialShepherdUIEvent.CloseOpenWindowsEvent;
import com.ss.ui.event.SocialShepherdUIEvent.UserLoggedOutEvent;
import com.ss.ui.event.SocialShepherdUIEvent.UserLoginRequestedEvent;
import com.ss.ui.event.SocialShepherdUIEvent.BrowserResizeEvent;
import com.ss.ui.event.SocialShepherdUIEventBus;
import com.ss.ui.view.LoginView;
import com.ss.ui.view.MainView;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.Page;
import com.vaadin.server.Page.BrowserWindowResizeEvent;
import com.vaadin.server.Page.BrowserWindowResizeListener;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

@Theme("dashboard")
@Widgetset("com.ss.ui.SocialShepherdWidgetSet")
@Title("Social Shepherd")
@SuppressWarnings("serial")
public final class SocialShepherdUI extends UI {

    /*
     * This field stores an access to the dummy backend layer. In real
     * applications you most likely gain access to your beans trough lookup or
     * injection; and not in the UI but somewhere closer to where they're
     * actually accessed.
     */
     private final SocialShepherdUIEventBus dashboardEventbus = new SocialShepherdUIEventBus();

     @Override
    protected void init(final VaadinRequest request) {
    	setLocale(Locale.US);

        SocialShepherdUIEventBus.register(this);
        Responsive.makeResponsive(this);

        updateContent();

        // Some views need to be aware of browser resize events so a
        // BrowserResizeEvent gets fired to the event but on every occasion.
        Page.getCurrent().addBrowserWindowResizeListener(
                new BrowserWindowResizeListener() {
                    @Override
                    public void browserWindowResized(
                            final BrowserWindowResizeEvent event) {
                    	SocialShepherdUIEventBus.post(new BrowserResizeEvent());
                    }
                });
    }

    /**
     * Updates the correct content for this UI based on the current user status.
     * If the user is logged in with appropriate privileges, main view is shown.
     * Otherwise login view is shown.
     */
    private void updateContent() {
        User user = (User) VaadinSession.getCurrent().getAttribute(
                User.class.getName());
        if (user != null //&& "1".equals(user.getRole())) {
            // Authenticated user
        		) {
            setContent(new MainView());
            removeStyleName("loginview");
            getNavigator().navigateTo(getNavigator().getState());
        } else {
            setContent(new LoginView());
            addStyleName("loginview");
        }
    }

    @Subscribe
    public void userLoginRequested(final UserLoginRequestedEvent event) {
        User user = DBService.getInstance().getUserDAO().getUserDetails(event.getUserName());
        VaadinSession.getCurrent().setAttribute(User.class.getName(), user);
        updateContent();
    }

    @Subscribe
    public void userLoggedOut(final UserLoggedOutEvent event) {
        // When the user logs out, current VaadinSession gets closed and the
        // page gets reloaded on the login screen. Do notice the this doesn't
        // invalidate the current HttpSession.
        VaadinSession.getCurrent().close();
        Page.getCurrent().reload();
    }

    @Subscribe
    public void closeOpenWindows(final CloseOpenWindowsEvent event) {
        for (Window window : getWindows()) {
            window.close();
        }
    }

     public static SocialShepherdUIEventBus getDashboardEventbus() {
        return ((SocialShepherdUI) getCurrent()).dashboardEventbus;
    }
}
