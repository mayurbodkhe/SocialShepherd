package com.ss.ui.view.recommendation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.ss.ml.suggest.RecommendationTableBean;
import com.ss.ml.suggest.SuggestDataBean;
import com.ss.ml.suggest.SuggestOutputBean;
import com.ss.ml.suggest.Suggestor;
import com.ss.services.DBService;
import com.ss.services.MLService;
import com.ss.ui.event.SocialShepherdUIEventBus;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Page;
import com.vaadin.shared.Position;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("unused")
public final class RecommendationView extends VerticalLayout implements View {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Table resultTable = null;
    private Panel resultPanel = null;
    
    private static String[] RESULT_HEADER = { "User Request Source", "Suggested Action"};
    
    
    public RecommendationView() {
     
    	setSizeFull();
        addStyleName("dashboard-view");
        SocialShepherdUIEventBus.register(this);
        
        addComponent(buildDetailedView());
        updateResultTableWithDBDetails();
         
    }

    @Override
    public void detach() {
        super.detach();
        SocialShepherdUIEventBus.unregister(this);
    }
    
    private Component buildDetailedView(){
    	
    	AbsoluteLayout mainLayout = new AbsoluteLayout();
    
    	resultPanel = buildResultPanel();
		resultPanel.setVisible(false);
		mainLayout.addComponent(resultPanel, "left:15.0px;");
		
	   return mainLayout;
    }
    

	private Panel buildResultPanel() {
		
		Panel resultPanel = new Panel();
		resultPanel.setCaption("Social Shepherd - Recommendation View for Data Source (Powered by Apache Mahout API)");
		resultPanel.setImmediate(false);
		resultPanel.setSizeFull();
		resultPanel.setWidth("90%");
		resultPanel.setHeight("90%");
		
		AbsoluteLayout resultLayout = buildResultLayout();
		resultPanel.setContent(resultLayout);
		
		return resultPanel;
	}

	
	private AbsoluteLayout buildResultLayout() {
		
		AbsoluteLayout resultLayout = new AbsoluteLayout();
		resultLayout.setImmediate(false);
		resultLayout.setSizeFull();
		resultLayout.setWidth("100.0%");
		resultLayout.setHeight("100.0%");
			
		resultTable = new Table();
		resultTable.setImmediate(false);
		resultTable.setSizeFull();
		resultTable.setWidth("100.0%");
		resultTable.setHeight("100.0%");
		
		
		
		resultLayout.addComponent(resultTable, "top:15.0px;left:15.0px;bottom:15.0px;right:15.0px;");
		
		return resultLayout;
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub
		
	}
	
	private void updateResultTableWithDBDetails(){
		  
		  BufferedWriter writer = null;
		  File tempFile = null;
		  List<String> lines = null;
		  DecimalFormat df = null;
		  List<RecommendationTableBean> beans = null;
		  Suggestor suggestor;
		  Notification message;
		  IndexedContainer indexedContainer;
		  long classificatinStart, classificatinEnd, renderingStart, renderingEnd;
		  SuggestOutputBean outputBean;
		  
			try {
		         
	        	df = new DecimalFormat("#.###");
	
	            message = new Notification("SS Recommendation Engine in Action", Notification.Type.HUMANIZED_MESSAGE);
		        message.setCaption("SS Recommendation Engine in Action");
		        message.setPosition(Position.MIDDLE_CENTER);
	        	message.show(Page.getCurrent());
	        	
	        	classificatinStart = System.currentTimeMillis(); 
	       
	        	beans = DBService.getInstance().getDaoFactory().getRecommendationDAO().getRecommendationInputByCount(100);
	        	suggestor = MLService.getInstance().getSuggestor();
	        	
	        	lines = new LinkedList<String>();
	        	tempFile = File.createTempFile("recommender", ".csv");
	        	writer = Files.newBufferedWriter(tempFile.toPath(), Charset.defaultCharset());
	        	writer.append("DataSource,SWOTText,Count");
	        	writer.newLine();
	        	
	        	for(RecommendationTableBean bean:beans){
	        		writer.append(bean.getDataSource() + "," + bean.getSwotText() + "," + bean.getDataSource());
	        		writer.newLine();
	        	}
	        	
	        	writer.flush();		
	        	
	        	suggestor.runDefaultSuggestor(tempFile.getAbsolutePath());
	        	outputBean = suggestor.getSuggestions();
	         
	        	classificatinEnd = System.currentTimeMillis();
	          
	        	renderingStart = System.currentTimeMillis(); 
	         
	        	indexedContainer = buildResultTableFromSuggestorOutput(outputBean.getDataBeans());
	          
	        	/* Finally, let's update the table with the container */
	        	//resulTable.setCaption(finishedEvent.getFilename());
	        	resultTable.setContainerDataSource(indexedContainer);
	        	resultPanel.setVisible(true);
	          
	        	renderingEnd = System.currentTimeMillis(); 
	          
	        	message.setCaption("SS Recommendation Engine Action Finished "+ Double.valueOf(df.format((classificatinEnd - classificatinStart)/1000)) + " (s) and Rendering took " + Double.valueOf(df.format((renderingEnd - renderingStart)/1000)) + " (s)");
	        	message.setDelayMsec(-1);
	          
	        	} catch (Exception e) {
	        		e.printStackTrace();
	        	}finally{
	        		if(writer != null){
						try {
							writer.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
	        		}
	        		
	        		if(tempFile != null){
						try {
							tempFile.delete();
						} catch (Exception e) {
							e.printStackTrace();
						}
	        		}
	        	}
	  }
	  
	  
	
	  
	  /**
	   * Uses http://opencsv.sourceforge.net/ to read the entire contents of a CSV
	   * file, and creates an IndexedContainer from it
	   *
	   * @param reader
	   * @return
	   * @throws IOException
	   */
	  protected IndexedContainer buildResultTableFromSuggestorOutput(List<SuggestDataBean> beans) throws IOException {
	    
		String[] propertyIds = null; String[] fields = null;  
		IndexedContainer container = new IndexedContainer();
	    
	    addColumHeaders(container, Arrays.asList(RESULT_HEADER));
	    
	    propertyIds = new String[RESULT_HEADER.length];
	    fields = new String [RESULT_HEADER.length];
	   	
	    for(SuggestDataBean bean: beans){
		    int i = 0;
		    	
		    propertyIds[i] = RESULT_HEADER[i];
		    fields[i] = bean.getUserName();
		    i++;
		    
		    propertyIds[i] = RESULT_HEADER[i];
		    fields[i] = bean.getSuggestedItem();
		    
		    addItem(container, propertyIds, fields);
	    }
	    
	    return container;
	  }
	  
	  /**
	   * Set's up the item property ids for the container. Each is a String (of course,
	   * you can create whatever data type you like, but I guess you need to parse the whole file
	   * to work it out)
	   *
	   * @param container The container to set
	   * @param columnHeaders The column headers, i.e. the first row from the CSV file
	   */
	  private static void addColumHeaders(IndexedContainer container, List<String> columnHeaders) {
	    for (String propertyName : columnHeaders) {
	      container.addContainerProperty(propertyName, String.class, null);
	    }
	  }

	  /**
	   * Adds an item to the given container, assuming each field maps to it's corresponding property id.
	   * Again, note that I am assuming that the field is a string.
	   *
	   * @param container
	   * @param propertyIds
	   * @param fields
	   */
	  @SuppressWarnings("unchecked")
	  private static void addItem(IndexedContainer container, String[] propertyIds, String[] fields) {
	    if (propertyIds.length != fields.length) {
	      throw new IllegalArgumentException("Hmmm - Different number of columns to fields in the record");
	    }
	    Object itemId = container.addItem();
	    Item item = container.getItem(itemId);
	    for (int i = 0; i < fields.length; i++) {
	      String propertyId = propertyIds[i];
	      String field = fields[i];
	      item.getItemProperty(propertyId).setValue(field);
	    }
	  }
	  
	
  
}
