package com.ss.ui.view.sentiment.analysis;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

import com.ss.ml.opinion.miner.stanford.SentimentTableBean;
import com.ss.services.DBService;
import com.ss.ui.event.SocialShepherdUIEventBus;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Page;
import com.vaadin.shared.Position;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Component;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;


public final class SentimentAnalysisView extends VerticalLayout implements View {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Table resultTable = null;
    private Panel resultPanel = null;
    
    private static String[] RESULT_HEADER = { "Sentence Id", "Data Source Name", "Overall Sentiment", "Short Text"};
    
    public SentimentAnalysisView() {
        
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
		resultPanel.setCaption("Social Shepherd - Sentiment Analysis View (Powered by Stanford CoreNLP API)");
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
		resultTable.addStyleName("sentiment-analysis");
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
		  
		  DecimalFormat df;
		  Notification message;
		  long classificatinStart, classificatinEnd, renderingStart, renderingEnd;
		  List<SentimentTableBean> beans;
		  IndexedContainer indexedContainer;
		  
		  try {
		         
	        df = new DecimalFormat("#.###");
	        	
	        message = new Notification("SS Sentiment Analysis Data Fetch in Progress", Notification.Type.HUMANIZED_MESSAGE);
			message.setCaption("SS Sentiment Analysis Data Fetch in Progress");
			message.setPosition(Position.MIDDLE_CENTER);
	        message.show(Page.getCurrent());
	        	
	        classificatinStart = System.currentTimeMillis(); 
	       
	        beans = DBService.getInstance().getDaoFactory().getSentimentAnalysisDAO().getSentimentByCount(500);
	        	
	        classificatinEnd = System.currentTimeMillis();
	          
	        renderingStart = System.currentTimeMillis(); 
	         
	        indexedContainer = buildResultTableFromDB(beans);
	          
	        /* Finally, let's update the table with the container */
	        //resulTable.setCaption(finishedEvent.getFilename());
	        resultTable.setContainerDataSource(indexedContainer);
	        updateTableRowColors(resultTable);
	        resultPanel.setVisible(true);
	          
	        renderingEnd = System.currentTimeMillis(); 
	          
	        message.setCaption("SS Sentiment Analysis Data Fetch finished "+ Double.valueOf(df.format((classificatinEnd - classificatinStart)/1000)) + " (s) and Rendering took " + Double.valueOf(df.format((renderingEnd - renderingStart)/1000)) + " (s)");
	        message.setDelayMsec(-1);
	          
	        } catch (Exception e) {
	        	e.printStackTrace();
	        }finally{
	        		
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
	  protected IndexedContainer buildResultTableFromDB(List<SentimentTableBean> beans) throws IOException {
	    
		String[] propertyIds = null; String[] fields = null;  
		IndexedContainer container = new IndexedContainer();
	    
	    addColumHeaders(container, RESULT_HEADER);
	    
	    propertyIds = new String[RESULT_HEADER.length];
	    fields = new String [propertyIds.length];
	    
	    
	    for(SentimentTableBean inputBean: beans){   	
	    	
	    	int i = 0;
	    	
	    	propertyIds[i] = RESULT_HEADER[i];
	    	fields[i] = String.valueOf(inputBean.getSentenceId());
	    	i++;
	    	
	    	propertyIds[i] = RESULT_HEADER[i];
	    	fields[i] = inputBean.getDataSourceName();
	    	i++;
	    	
	    	propertyIds[i] = RESULT_HEADER[i];
	    	fields[i] = inputBean.getOverallSentiment(); 
	    	i++;
	    	
	    	propertyIds[i] = RESULT_HEADER[i];
	    	fields[i] = inputBean.getShortText();   	
	    	
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
	  private static void addColumHeaders(IndexedContainer container, String[] columnHeaders) {
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
	
	private void updateTableRowColors(Table table){
		
		 table.setCellStyleGenerator(new Table.CellStyleGenerator() {                
	            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

				@Override
	            public String getStyle(Table source, Object itemId, Object propertyId) {
	                if(propertyId != null ) {
	                    Item item = source.getItem(itemId);
	                    if(propertyId.toString().equalsIgnoreCase(RESULT_HEADER[2])) {
	                        String cellValue = (String)item.getItemProperty(propertyId).getValue();
	                        if( cellValue.equals("Very Positive") ) {
	                            return "ssverypositive";
	                        } else if( cellValue.equals("Positive") ) {
	                            return "sspositive";
	                        } else if( cellValue.equals("Neutral") ) {
	                            return "ssneutral";
	                        } else if( cellValue.equals("Negative") ) {
	                            return "ssnegative";
	                        } else if( cellValue.equals("Very Negative") ) {
	                            return "ssverynegative";    
	                        } else {
	                            return "white";
	                        }
	                    } else {
	                        return "white";
	                    }
	                } else {
	                    return null;
	                }
	            }
	          });
	}
	
}
