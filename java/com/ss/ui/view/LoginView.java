package com.ss.ui.view;

import com.ss.services.DBService;
import com.ss.ui.domain.User;
import com.ss.ui.event.SocialShepherdUIEventBus;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.Position;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import com.ss.ui.event.SocialShepherdUIEvent.UserLoginRequestedEvent;

@SuppressWarnings("serial")
public class LoginView extends VerticalLayout {


	public LoginView() {

        setSizeFull();

        //addComponent(buildLabels());
        
        Component loginForm = buildLoginForm();
        addComponent(loginForm);
        setComponentAlignment(loginForm, Alignment.MIDDLE_CENTER);


    }

    private Component buildLoginForm() {
       
    	final VerticalLayout loginPanel = new VerticalLayout();
        loginPanel.setSizeUndefined();
        loginPanel.setSpacing(true);
        Responsive.makeResponsive(loginPanel);
        loginPanel.addStyleName("login-panel");

        loginPanel.addComponent(buildFields());
        return loginPanel;
    }

    private Component buildFields() {
        VerticalLayout fields = new VerticalLayout();
        fields.setSpacing(true);
        fields.addStyleName("fields");

        Label title = new Label("Welcome to Social Shepherd");
        title.setSizeUndefined();
        title.addStyleName(ValoTheme.LABEL_H2);
        title.addStyleName(ValoTheme.LABEL_LIGHT);
        
        final TextField username = new TextField("Username");
        username.setIcon(FontAwesome.USER);
        username.addStyleName(ValoTheme.TEXTFIELD_INLINE_ICON);

        final PasswordField password = new PasswordField("Password");
        password.setIcon(FontAwesome.LOCK);
        password.addStyleName(ValoTheme.TEXTFIELD_INLINE_ICON);

        final Button signin = new Button("Sign In");
        signin.addStyleName(ValoTheme.BUTTON_PRIMARY);
        signin.setClickShortcut(KeyCode.ENTER);
        signin.focus();

        fields.addComponents(title, username, password, signin);
        fields.setComponentAlignment(signin, Alignment.BOTTOM_LEFT);

        signin.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
            	
            	if(!DBService.getInstance().getUserDAO().isAuthorizedUser(username.getValue(), password.getValue()))  {
            			Notification notification = new Notification("Welcome to Social Shepherd");
            			notification.setDescription("<span>Login Failed, enter valid credentials. For first time users please contact system administrator</span>");
            			notification.setHtmlContentAllowed(true);
            			notification.setStyleName("tray dark small closable login-help");
            			notification.setPosition(Position.BOTTOM_CENTER);
            			notification.show(Page.getCurrent());
            			notification.setDelayMsec(-1);
            	}else{
            		
            		User user = DBService.getInstance().getUserDAO().getUserDetails(username.getValue());
            		VaadinSession.getCurrent().setAttribute(User.class.getName(), user);
            		
            		SocialShepherdUIEventBus.post(new UserLoginRequestedEvent(username.getValue(), password.getValue()));
            	}
   
            }
        });
        return fields;
    }
  
}
