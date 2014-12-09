package com.ss.ui.view.opinion.miner;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.ss.ml.opinion.miner.stanford.SAResultTableBean;
import com.ss.ml.opinion.miner.stanford.SentimentAnalysisResponse;
import com.ss.ml.opinion.miner.stanford.StanfordCoreNLPSentimentProcessor;
import com.ss.services.DataPullerService;
import com.ss.services.MLService;
import com.ss.social.media.api.TweetInfo;
import com.ss.social.media.api.TwitterExtractor;
import com.ss.ui.event.SocialShepherdUIEventBus;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Page;
import com.vaadin.shared.Position;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings({ "serial", "unchecked" })
public final class OpinionMinerView extends VerticalLayout implements View {

    private Table resultTable = null;
    private Panel dataPanel = null;
    private Panel evalPanel = null;
    private Panel resultPanel = null;
    private Table evalTable = null;
    
    private static String[] RESULT_HEADER = { "Twitter User ID", "Overall Sentiment", "Tweet Content"};
    private static String[] EVALUATION_HEADERS = { "Positive", "Neutral", "Negative"};
  
    public OpinionMinerView() {
        
    	setSizeFull();
        addStyleName("dashboard-view");
        SocialShepherdUIEventBus.register(this);

        addComponent(buildDetailedView());
         
    }

    @Override
    public void detach() {
        super.detach();
        SocialShepherdUIEventBus.unregister(this);
    }

  
    private Component buildDetailedView(){
    	
    	AbsoluteLayout mainLayout = new AbsoluteLayout();
    
    	Label titleLabel = new Label("Social Shepherd - Opinion Miner Experimenter");
    	titleLabel.addStyleName(ValoTheme.LABEL_H2);
        titleLabel.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        mainLayout.addComponent(titleLabel, "top:0.0px;left:15.0px;");
        
    	dataPanel = buildDataPanel();
        mainLayout.addComponent(dataPanel, "top:45.0px;left:15.0px;");
        
        evalPanel = buildEvalPanel();
        evalPanel.setVisible(false);
		mainLayout.addComponent(evalPanel,"top:45.0px;right:26.0px;left:460.0px;");
		
		resultPanel = buildResultPanel();
		resultPanel.setVisible(false);
		mainLayout.addComponent(resultPanel, "top:225.0px;left:15.0px;");
		
	   return mainLayout;
    }
    
	private Panel buildDataPanel() {
		
		Panel dataPanel = new Panel();
		dataPanel.setCaption("Opinion Miner Data Selection");
		dataPanel.setImmediate(false);
		dataPanel.setWidth("402px");
		dataPanel.setHeight("160px");
		
		AbsoluteLayout dataPanelLayout = buildDataPanelLayout();
		dataPanel.setContent(dataPanelLayout);
		
		return dataPanel;
	}


	private AbsoluteLayout buildDataPanelLayout() {
		
		
		AbsoluteLayout dataPanelLayout = new AbsoluteLayout();
		dataPanelLayout.setCaption("Opinion Miner Data Selection");
		dataPanelLayout.setImmediate(false);
		dataPanelLayout.setWidth("100.0%");
		dataPanelLayout.setHeight("100.0%");
		
		Label fileSelectionLabel = new Label();
		fileSelectionLabel.setImmediate(false);
		fileSelectionLabel.setWidth("-1px");
		fileSelectionLabel.setHeight("-1px");
		fileSelectionLabel.setValue("Select Tag to Search from Twitter");
		dataPanelLayout.addComponent(fileSelectionLabel,"top:10.0px;left:10.0px;");
		
		final TextField hashTagSuffix = new TextField("#");
		hashTagSuffix.setValue("PMOIndia");
		dataPanelLayout.addComponent(hashTagSuffix,"top:50.0px;left:10.0px;");
		
		final Button processButton = new Button("Process",
				  new Button.ClickListener() {
					@Override
				    public void buttonClick(ClickEvent event) {
				        
				        SAResultTableBean bean;
						List<String> overallSentiments = null;
						List<TweetInfo> tweetInfos;
						List<SAResultTableBean> resultTableBeans;
						
				    	long classificatinStart = System.currentTimeMillis(); 
				    	 
				    	Notification message = new Notification("SS Opinion Miner in Action", Notification.Type.HUMANIZED_MESSAGE);
				        message.setCaption("SS Opinion Miner in Action");
				        message.setPosition(Position.MIDDLE_CENTER);
					    message.show(Page.getCurrent());
				    	
					    List<SentimentAnalysisResponse> list = new LinkedList<SentimentAnalysisResponse>();
				        TwitterExtractor extractor = DataPullerService.getInstance().getTwitterExtractor();
				        tweetInfos = extractor.getTweetsTaggedByCount(hashTagSuffix.getValue(), 50);
				        StanfordCoreNLPSentimentProcessor processor = MLService.getInstance().getSentimentProcessor();
				        
				        resultTableBeans = new LinkedList<SAResultTableBean>();
				   
				        overallSentiments = new LinkedList<String>();
				        
						for(TweetInfo tweetInfo: tweetInfos){
							
							try{
								list = processor.getSentiment(tweetInfo.getText());
								
								for(SentimentAnalysisResponse response: list){
						
									bean = new SAResultTableBean();
							        bean.setUserName(tweetInfo.getUser());
							        bean.setTweetText(response.getRawTex());
							        bean.setOverallSentimentScore(response.getAggregate().getSentiment());
							        
							        resultTableBeans.add(bean);
							        overallSentiments.add(response.getAggregate().getSentiment());
								}
						        
							}catch(Exception ex){
								ex.printStackTrace();
							}
						}
						
						
						
						long classificatinEnd = System.currentTimeMillis();
						
						long RenderingStart = System.currentTimeMillis(); 
						
						IndexedContainer indexedContainer = buildResultTableFromSaHPOutput(resultTableBeans);
						
				          
				          /* Finally, let's update the table with the container */
				          //resulTable.setCaption(finishedEvent.getFilename());
				          resultTable.setContainerDataSource(indexedContainer);
				          updateTableRowColors(resultTable);
				          resultPanel.setVisible(true);
				          
				          try {
							indexedContainer = buildEvalTableFromPredictionOutput(getAverageOverallSentiment(overallSentiments));
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				          
				          /* Finally, let's update the table with the container */
				          //evalTable.setCaption(finishedEvent.getFilename());
				          evalTable.setContainerDataSource(indexedContainer);
				          evalPanel.setVisible(true);
				          
				          long RenderingEnd = System.currentTimeMillis(); 
				          
				          DecimalFormat df = new DecimalFormat("#.###");
				          message.setCaption("SS Opinion Miner Action Finished in "+ Double.valueOf(df.format((classificatinEnd - classificatinStart)/1000)) + " (s) and Rendering took " + Double.valueOf(df.format((RenderingEnd - RenderingStart)/1000)) + " (s)");
				          message.setDelayMsec(-1);
				    	
				    }
				  });
		
		dataPanelLayout.addComponent(processButton,"top:50.0px;left:220.0px;");
		
		return dataPanelLayout;
	}

	
	private Panel buildEvalPanel() {
		
		Panel evalPanel = new Panel();
		evalPanel.setCaption("Opinion Miner Evaluation Metrics");
		evalPanel.setImmediate(false);
		evalPanel.setWidth("100.0%");
		evalPanel.setHeight("155px");
		
		AbsoluteLayout evalLayout = buildEvalLayout();
		evalPanel.setContent(evalLayout);
		
		return evalPanel;
	}


	private AbsoluteLayout buildEvalLayout() {
		
		AbsoluteLayout evalLayout = new AbsoluteLayout();
		evalLayout.setImmediate(false);
		evalLayout.setWidth("100.0%");
		evalLayout.setHeight("100.0%");
		
		evalTable = new Table();
		evalTable.setImmediate(false);
		evalTable.setWidth("100%");
		evalTable.setHeight("100%");
		evalLayout.addComponent(evalTable, "top:15.0px;left:15.0px;bottom:15.0px;right:15.0px;");
		
		return evalLayout;
	}
	
	private Panel buildResultPanel() {
		
		Panel resultPanel = new Panel();
		resultPanel.setCaption("Opinion Miner Results (50 Twitter Feeds --> Stanford Pipeline [tokenize, ssplit, pos, lemma, parse, sentiment, ner] --> Sentiment)");
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
	
	/**
	   * Uses http://opencsv.sourceforge.net/ to read the entire contents of a CSV
	   * file, and creates an IndexedContainer from it
	   *
	   * @param reader
	   * @return
	   * @throws IOException
	   */
	  protected IndexedContainer buildResultTableFromSaHPOutput(List<SAResultTableBean> resultTableBeans) {
	    
		String[] propertyIds = null; String[] fields = null;  
		IndexedContainer container = new IndexedContainer();
	    
	    addColumHeaders(container, RESULT_HEADER);
	    
	    propertyIds = new String[3];
	    fields = new String [propertyIds.length];
	    
	    for(SAResultTableBean inputBean: resultTableBeans){
	    	int i = 0;
	    	
	    	propertyIds[i] = RESULT_HEADER[i];
	    	fields[i] = inputBean.getUserName();
	    	i++;
	    	
	    	propertyIds[i] = RESULT_HEADER[i];
	    	fields[i] = String.valueOf(inputBean.getOverallSentimentScore());;
	    	i++;
	    	
	    	propertyIds[i] = RESULT_HEADER[i];
	    	fields[i] = inputBean.getTweetText();
	    	i++;
	    	
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
	
	  
	  private Map<String, Integer> getAverageOverallSentiment(List<String> sentiments){
		  
		  Map<String, Integer> map = new LinkedHashMap<String, Integer>();
		  
		  int negCount = 0, neuCount = 0, posCount = 0;
		  
		  for(String sentiment: sentiments){
			  
			  if(sentiment.equalsIgnoreCase(StanfordCoreNLPSentimentProcessor.sentimentScores[0]))
				  negCount++;
			  else if(sentiment.equalsIgnoreCase(StanfordCoreNLPSentimentProcessor.sentimentScores[1]))
				  negCount++;
			  else if(sentiment.equalsIgnoreCase(StanfordCoreNLPSentimentProcessor.sentimentScores[2]))
				  neuCount++;
			  else if(sentiment.equalsIgnoreCase(StanfordCoreNLPSentimentProcessor.sentimentScores[3]))
				  posCount++;
			  else if(sentiment.equalsIgnoreCase(StanfordCoreNLPSentimentProcessor.sentimentScores[4]))
				  posCount++;
		  }
		  
		  map.put(StanfordCoreNLPSentimentProcessor.sentimentScores[1], negCount);
		  map.put(StanfordCoreNLPSentimentProcessor.sentimentScores[2], neuCount);
		  map.put(StanfordCoreNLPSentimentProcessor.sentimentScores[3], posCount);
		  
		  return map;
	  }
	  
	  
	  /**
	   * Uses http://opencsv.sourceforge.net/ to read the entire contents of a CSV
	   * file, and creates an IndexedContainer from it
	   *
	   * @param reader
	   * @return
	   * @throws IOException
	   */
	  protected IndexedContainer buildEvalTableFromPredictionOutput(Map<String,Integer> sentiScores) throws IOException {
	    
		String[] propertyIds = null; String[] fields = null;  
		IndexedContainer container = new IndexedContainer();
	    
	    addColumHeaders(container, EVALUATION_HEADERS);
	    
	    propertyIds = new String[EVALUATION_HEADERS.length];
	    fields = new String [EVALUATION_HEADERS.length];
	   	
	    int i = 0;
	    	
	    propertyIds[i] = EVALUATION_HEADERS[i];
	    fields[i] = String.valueOf(sentiScores.get(EVALUATION_HEADERS[i]));
	    i++;
	    
	    propertyIds[i] = EVALUATION_HEADERS[i];
	    fields[i] = String.valueOf(sentiScores.get(EVALUATION_HEADERS[i]));
	    i++;
	    
	    propertyIds[i] = EVALUATION_HEADERS[i];
	    fields[i] = String.valueOf(sentiScores.get(EVALUATION_HEADERS[i]));
	    
	    addItem(container, propertyIds, fields);
	   
	    return container;
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
		                    if(propertyId.toString().equalsIgnoreCase(RESULT_HEADER[1])) {
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
