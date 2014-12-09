package com.ss.ui.view.cockpit;

import java.util.Iterator;

import com.ss.db.bean.OverallSentimentBean;
import com.ss.ml.opinion.miner.stanford.SWOTIndentifierService.SWOT_TYPE;
import com.ss.services.DBService;
import com.ss.ui.component.SparklineChart;
import com.ss.ui.event.SocialShepherdUIEvent.CloseOpenWindowsEvent;
import com.ss.ui.event.SocialShepherdUIEventBus;
import com.ss.ui.view.cockpit.CockPitEdit.CockPitEditListener;
import com.vaadin.addon.charts.model.style.Color;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
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

public final class CockPitView extends Panel implements View,
        CockPitEditListener {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String EDIT_ID = "dashboard-edit";
    public static final String TITLE_ID = "dashboard-title";

    private Label titleLabel;
    private CssLayout dashboardPanels;
    private final VerticalLayout root;
    
    public CockPitView() {
    	
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
        root.addComponent(buildSparklines());

        Component content = buildContent();
        root.addComponent(content);
        root.setExpandRatio(content, 1);
        
        // All the open sub-windows should be closed whenever the root layout
        // gets clicked.
        root.addLayoutClickListener(new LayoutClickListener() {
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
            public void layoutClick(final LayoutClickEvent event) {
            	SocialShepherdUIEventBus.post(new CloseOpenWindowsEvent());
            }
        });
    }

    private Component buildSparklines() {
        CssLayout sparks = new CssLayout();
        sparks.addStyleName("sparks");
        sparks.setWidth("100%");
        Responsive.makeResponsive(sparks);

        OverallSentimentBean bean = new OverallSentimentBean(DBService.getInstance().getSentimentAnalysisDAO().getOverallSentiment());
        
        SparklineChart s = new SparklineChart("Positive Sentiment", "%", "",
                chartColors[2], 8, 89, 150, bean.getOverallPostiveCount(), bean.getOverallPositivePercentage());//0
        sparks.addComponent(s);

        s = new SparklineChart("Neutral Sentiment", "%", "",
                chartColors[3], 8, 89, 150, bean.getNeutralCount(), bean.getNeutralPercentage());//2
        sparks.addComponent(s);

        s = new SparklineChart("Negative Sentiment", "%", "",
                chartColors[5], 8, 89, 150, bean.getOverallNegativeCount(), bean.getOverallNegativePercentage());//3
        sparks.addComponent(s);

        s = new SparklineChart("Total Samples", "", "",
                chartColors[0], 8, 89, 150,bean.getTotalCount(),(int)bean.getTotalCount());//5
        sparks.addComponent(s);

        return sparks;
    }

    private Component buildHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.addStyleName("viewheader");
        header.setSpacing(true);

        titleLabel = new Label("Social Shepherd - Cockpit");
        titleLabel.setId(TITLE_ID);
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

        dashboardPanels.addComponent(buildTopStrengths());
        dashboardPanels.addComponent(buildTopWeeknesses());
        dashboardPanels.addComponent(buildTopOpportunities());
        dashboardPanels.addComponent(buildTopThreats());

        return dashboardPanels;
    }

    private Component buildTopStrengths() {
    	return createContentWrapper( new TopPieChart(DBService.getInstance().getSentimentAnalysisDAO().getTopKSWOTFeatureBySWOTType(10, SWOT_TYPE.STRENGTH.getType()), "Top 10 Strengths"));
    }

    private Component buildTopWeeknesses() {
    	return createContentWrapper( new TopPieChart(DBService.getInstance().getSentimentAnalysisDAO().getTopKSWOTFeatureBySWOTType(10, SWOT_TYPE.STRENGTH.getType()),"Top 10 Weeknesses"));
    }

    private Component buildTopOpportunities() {
    	return createContentWrapper( new TopPieChart(DBService.getInstance().getSentimentAnalysisDAO().getTopKSWOTFeatureBySWOTType(10, SWOT_TYPE.STRENGTH.getType()),"Top 10 Opportunities"));
    }

    private Component buildTopThreats() {
        return createContentWrapper(new TopPieChart(DBService.getInstance().getSentimentAnalysisDAO().getTopKSWOTFeatureBySWOTType(10, SWOT_TYPE.STRENGTH.getType()),"Top 10 Threats"));
    }

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

            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

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
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
            public void menuSelected(final MenuItem selectedItem) {
                Notification.show("Not implemented in this demo");
            }
        });
        root.addSeparator();
        root.addItem("Close", new Command() {
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

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


    @Override
    public void dashboardNameEdited(final String name) {
        titleLabel.setValue(name);
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

    public static Color[] chartColors = new Color[] {
        new SolidColor("#3090F0"), new SolidColor("#18DDBB"),
        new SolidColor("#008000"), new SolidColor("#F9DD51"),
        new SolidColor("#F09042"), new SolidColor("#EC6464") };

	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub
		
	}

}
