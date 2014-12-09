package com.ss.ui.view;

import com.ss.ui.view.churner.ChurnerView;
import com.ss.ui.view.cockpit.CockPitView;
import com.ss.ui.view.entity.explorer.EntityExplorerView;
import com.ss.ui.view.feature.explorer.FeatureExplorerView;
import com.ss.ui.view.forecast.ForeCastView;
import com.ss.ui.view.opinion.explorer.OpinionExplorerView;
import com.ss.ui.view.opinion.miner.OpinionMinerView;
import com.ss.ui.view.recommendation.RecommendationView;
import com.ss.ui.view.sentiment.analysis.SentimentAnalysisView;
import com.ss.ui.view.source.explorer.SourceExplorerView;
import com.ss.ui.view.suggest.SuggestView;
import com.vaadin.navigator.View;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;

public enum SocialShepherdUIViewType {
    COCKPIT("HOME - Cockpit", CockPitView.class, FontAwesome.HOME, true),
    CHURNER("VIEW - Churn", ChurnerView.class, FontAwesome.CALENDAR_O, false),
    SENTIMENTANALYSIS("VIEW - Sentiment Analysis", SentimentAnalysisView.class, FontAwesome.CALENDAR_O, false),   
    RECOMMENDATION("VIEW - Recommendation", RecommendationView.class, FontAwesome.CALENDAR_O, false),
    ENTITYEXPLORER("VIEW - Entity Explorer", EntityExplorerView.class, FontAwesome.CALENDAR_O, false), 
    FEATUREEXPLORER("VIEW - Feature Explorer", FeatureExplorerView.class, FontAwesome.CALENDAR_O, false),
    OPINIONEXPLORER("VIEW - Opinion Explorer", OpinionExplorerView.class, FontAwesome.CALENDAR_O, false),
    SOURCEEXPLORER("VIEW - Source Explorer", SourceExplorerView.class, FontAwesome.CALENDAR_O, false),
    FORECAST("EXPERIMENTER - Forecast", ForeCastView.class, FontAwesome.BAR_CHART_O, false),
    OPINIONMINER("EXPERIMENTER - Opinion Miner",OpinionMinerView.class, FontAwesome.BAR_CHART_O, false), 
    SUGGEST("EXPERIMENTER - Suggest", SuggestView.class, FontAwesome.BAR_CHART_O, true)
    ;

    private final String viewName;
    private final Class<? extends View> viewClass;
    private final Resource icon;
    private final boolean stateful;

    private SocialShepherdUIViewType(final String viewName,
            final Class<? extends View> viewClass, final Resource icon,
            final boolean stateful) {
        this.viewName = viewName;
        this.viewClass = viewClass;
        this.icon = icon;
        this.stateful = stateful;
    }

    public boolean isStateful() {
        return stateful;
    }

    public String getViewName() {
        return viewName;
    }

    public Class<? extends View> getViewClass() {
        return viewClass;
    }

    public Resource getIcon() {
        return icon;
    }

    public static SocialShepherdUIViewType getByViewName(final String viewName) {
    	SocialShepherdUIViewType result = null;
        for (SocialShepherdUIViewType viewType : values()) {
            if (viewType.getViewName().equals(viewName)) {
                result = viewType;
                break;
            }
        }
        return result;
    }

}
