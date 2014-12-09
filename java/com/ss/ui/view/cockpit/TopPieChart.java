package com.ss.ui.view.cockpit;

import java.util.Map;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.ChartType;
import com.vaadin.addon.charts.model.Credits;
import com.vaadin.addon.charts.model.DataSeries;
import com.vaadin.addon.charts.model.DataSeriesItem;
import com.vaadin.addon.charts.model.PlotOptionsPie;
import com.vaadin.addon.charts.model.style.Color;
import com.vaadin.addon.charts.model.style.SolidColor;

@SuppressWarnings("serial")
public class TopPieChart extends Chart {

    public TopPieChart(Map<String, Integer> threats, String caption) {
        super(ChartType.PIE);

        setCaption(caption);
        getConfiguration().setTitle("");
        getConfiguration().getChart().setType(ChartType.PIE);
        getConfiguration().getChart().setAnimation(false);
        setWidth("100%");
        setHeight("90%");

        DataSeries series = new DataSeries();

        int i = 0;
        for(Map.Entry<String, Integer> entry: threats.entrySet()){
        	DataSeriesItem item = new DataSeriesItem(entry.getKey(),
        			entry.getValue());
            series.add(item);
            item.setColor(chartColors[9 - i]);
            i++;
        }
        
        getConfiguration().setSeries(series);

        PlotOptionsPie opts = new PlotOptionsPie();
        opts.setBorderWidth(0);
        opts.setShadow(false);
        opts.setAnimation(false);
        getConfiguration().setPlotOptions(opts);

        Credits c = new Credits("Social Shepherd");
        getConfiguration().setCredits(c);
    }
    
    public static Color[] chartColors = new Color[] {
        new SolidColor("#3090F0"), new SolidColor("#18DDBB"),
        new SolidColor("#98DF58"), new SolidColor("#F9DD51"),
        new SolidColor("#F09042"), new SolidColor("#EC6464"),
        new SolidColor("#F0F030"), new SolidColor("#DD183A"),
        new SolidColor("#DD5918"), new SolidColor("#6464EC"),
        new SolidColor("#64EC64"), new SolidColor("#DCDF58")};

}
