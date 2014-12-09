package com.ss.ui.view.entity.explorer;

import java.util.Iterator;

import com.ss.db.dao.mysql.MySqlSentimentAnalysisDAO.SENTIMENT_TYPE;
import com.ss.services.DBService;
import com.ss.ui.event.SocialShepherdUIEventBus;
import com.ss.ui.view.cockpit.TopPieChart;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Responsive;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

public final class EntityExplorerView extends Panel implements View {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final VerticalLayout root;
    private CssLayout dashboardPanels;
    private int topK = 10;
    
    public EntityExplorerView() {
    	
    	addStyleName(ValoTheme.PANEL_BORDERLESS);
        setSizeFull();
        SocialShepherdUIEventBus.register(this);
        
        root = new VerticalLayout();
        root.setSizeFull();
        root.setMargin(true);
        root.addStyleName("dashboard-view");
        setContent(root);
        Responsive.makeResponsive(root);
        
        root.addComponent(buildHeader());
        
        Component content = buildContent();
        root.addComponent(content);
        root.setExpandRatio(content, 1);
                
    }

    @Override
    public void detach() {
        super.detach();
        SocialShepherdUIEventBus.unregister(this);
    }

  
    private Component buildHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.addStyleName("viewheader");
        header.setSpacing(true);

        Label titleLabel;titleLabel = new Label("Social Shepherd - Sentiments By Entity");
        titleLabel.setSizeUndefined();
        titleLabel.addStyleName(ValoTheme.LABEL_H1);
        titleLabel.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        header.addComponent(titleLabel);
        return header;
    }
    
    private Component buildContent() {
	        dashboardPanels = new CssLayout();
	        dashboardPanels.addStyleName("dashboard-panels");
	        Responsive.makeResponsive(dashboardPanels);

	        dashboardPanels.addComponent(buildTopKOverallEntity());
	        dashboardPanels.addComponent(buildTopKPositivelEntity());
	        dashboardPanels.addComponent(buildTopKNeutralEntity());
	        dashboardPanels.addComponent(buildTopKNegativeEntity());

	        return dashboardPanels;
	    }

	    private Component buildTopKOverallEntity() {
	    	return createContentWrapper( new TopPieChart(DBService.getInstance().getSentimentAnalysisDAO().getTopKEntityBySentimentType(topK, SENTIMENT_TYPE.OVERALL.getSentimentType()), "Top "+topK+" Entity - Overall"));
	    }

	    private Component buildTopKPositivelEntity() {
	    	return createContentWrapper( new TopPieChart(DBService.getInstance().getSentimentAnalysisDAO().getTopKEntityBySentimentType(topK, SENTIMENT_TYPE.POSITIVE.getSentimentType()), "Top "+topK+" Entity - Positive"));
	    }
	    
	    private Component buildTopKNeutralEntity() {
	    	return createContentWrapper( new TopPieChart(DBService.getInstance().getSentimentAnalysisDAO().getTopKEntityBySentimentType(topK, SENTIMENT_TYPE.NEUTRAL.getSentimentType()), "Top "+topK+" Entity - Neutral"));
	    }
	    
	    private Component buildTopKNegativeEntity() {
	    	return createContentWrapper( new TopPieChart(DBService.getInstance().getSentimentAnalysisDAO().getTopKEntityBySentimentType(topK, SENTIMENT_TYPE.NEGATIVE.getSentimentType()), "Top "+topK+" Entity - Negative"));
	    }

	    @SuppressWarnings("serial")
		private Component createContentWrapper(final Component content) {
	        final CssLayout slot = new CssLayout();
	        slot.setWidth("100%");
	        slot.addStyleName("dashboard-panel-slot");

	        CssLayout card = new CssLayout();
	        card.setWidth("100%");
	        card.addStyleName(ValoTheme.LAYOUT_CARD);

	        HorizontalLayout toolbar = new HorizontalLayout();
	        toolbar.addStyleName("dashboard-panel-toolbar");
	        toolbar.setWidth("100%");

	        Label caption = new Label(content.getCaption());
	        caption.addStyleName(ValoTheme.LABEL_H4);
	        caption.addStyleName(ValoTheme.LABEL_COLORED);
	        caption.addStyleName(ValoTheme.LABEL_NO_MARGIN);
	        content.setCaption(null);

	        MenuBar tools = new MenuBar();
	        tools.addStyleName(ValoTheme.MENUBAR_BORDERLESS);
	        MenuItem max = tools.addItem("", FontAwesome.EXPAND, new Command() {

	            @Override
	            public void menuSelected(final MenuItem selectedItem) {
	                if (!slot.getStyleName().contains("max")) {
	                    selectedItem.setIcon(FontAwesome.COMPRESS);
	                    toggleMaximized(slot, true);
	                } else {
	                    slot.removeStyleName("max");
	                    selectedItem.setIcon(FontAwesome.EXPAND);
	                    toggleMaximized(slot, false);
	                }
	            }
	        });
	        max.setStyleName("icon-only");
	        MenuItem root = tools.addItem("", FontAwesome.COG, null);
	        root.addItem("Configure", new Command() {
	            @Override
	            public void menuSelected(final MenuItem selectedItem) {
	                Notification.show("Not implemented in this demo");
	            }
	        });
	        root.addSeparator();
	        root.addItem("Close", new Command() {
	            @Override
	            public void menuSelected(final MenuItem selectedItem) {
	                Notification.show("Not implemented in this demo");
	            }
	        });

	        toolbar.addComponents(caption, tools);
	        toolbar.setExpandRatio(caption, 1);
	        toolbar.setComponentAlignment(caption, Alignment.MIDDLE_LEFT);

	        card.addComponents(toolbar, content);
	        slot.addComponent(card);
	        return slot;
	    }
	    
	    
	    private void toggleMaximized(final Component panel, final boolean maximized) {
	        for (Iterator<Component> it = root.iterator(); it.hasNext();) {
	            it.next().setVisible(!maximized);
	        }
	        dashboardPanels.setVisible(true);

	        for (Iterator<Component> it = dashboardPanels.iterator(); it.hasNext();) {
	            Component c = it.next();
	            c.setVisible(!maximized);
	        }

	        if (maximized) {
	            panel.setVisible(true);
	            panel.addStyleName("max");
	        } else {
	            panel.removeStyleName("max");
	        }
	    }

		@Override
		public void enter(ViewChangeEvent event) {
			// TODO Auto-generated method stub
			
		}
	
	
}
