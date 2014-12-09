package com.ss.ui.view.forecast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

import com.ss.ml.forecast.ClassifierEvaluatorBean;
import com.ss.ml.forecast.PredictionInputBean;
import com.ss.ml.forecast.PredictionOutputBean;
import com.ss.ml.forecast.Predictor;
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
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings({ "serial", "unchecked","deprecation","unused" })
public final class ForeCastView extends VerticalLayout implements View {

    private Table resultTable = null;
    private Table evalTable = null;
	private final Label evalLabel = null;
    private Panel dataPanel = null;
    private Panel evalPanel = null;
    private Panel resultPanel = null;
    
    private File uploadFile = null;
 
    private static String[] RESULT_HEADER = { "ID", "Property1", "Property2", "Property3", "Property4", "Property5", "Property6", "Property7", "Property8", "Property9", "Property10", "ActualValue", "PredictedVlaue"};
    private static String[] EVALUATION_HEADERS = { "Model Build Time(s)", "F Measure", "Precision", "Recall", "True Positive", "False Postive", "True Negative", "False Negative", "Error Rate", "RMSE"};
    
    
    public ForeCastView() {
        
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
    
    	Label titleLabel = new Label("Social Shepherd - Prediction Experimenter");
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
		dataPanel.setCaption("Prediction Data Selection");
		dataPanel.setImmediate(false);
		dataPanel.setWidth("402px");
		dataPanel.setHeight("160px");
		
		AbsoluteLayout dataPanelLayout = buildDataPanelLayout();
		dataPanel.setContent(dataPanelLayout);
		
		return dataPanel;
	}


	private AbsoluteLayout buildDataPanelLayout() {
		
		AbsoluteLayout dataPanelLayout = new AbsoluteLayout();
		dataPanelLayout.setCaption("Prediction Data Selection");
		dataPanelLayout.setImmediate(false);
		dataPanelLayout.setWidth("100.0%");
		dataPanelLayout.setHeight("100.0%");
		
		Label fileSelectionLabel = new Label();
		fileSelectionLabel.setImmediate(false);
		fileSelectionLabel.setWidth("-1px");
		fileSelectionLabel.setHeight("-1px");
		fileSelectionLabel.setValue("Select File for Processing");
		dataPanelLayout.addComponent(fileSelectionLabel,"top:10.0px;left:10.0px;");
		
		Upload upload = new Upload(null, new Upload.Receiver() {
		      @Override
		      public OutputStream receiveUpload(String filename, String mimeType) {
		        try {
		        	
		        	if("".equalsIgnoreCase(filename)){
		        		Notification error = new Notification("Invalid File Name", "Select file before processing",Notification.TYPE_ERROR_MESSAGE);
		        		error.show(Page.getCurrent());
		        		
		        	}else if (!filename.toLowerCase().contains("arff")){
		        		Notification warning = new Notification("Invalid File Format", "Select ARFF formatted file Only",Notification.TYPE_WARNING_MESSAGE);
		        		warning.show(Page.getCurrent());
		        	}
		        	
		        	uploadFile = new File(filename);
		        	return new FileOutputStream(uploadFile);
		        	
		        } catch (IOException e) {
		          e.printStackTrace();
		          return null;
		        }
		      }
		    });
		    upload.addListener(new Upload.FinishedListener() {
		      @Override
		      public void uploadFinished(Upload.FinishedEvent finishedEvent) {
		        try {
		         
		        	DecimalFormat df = new DecimalFormat("#.###");
		        	
		        	Notification message = new Notification("SS Prediction Engine in Action", Notification.Type.HUMANIZED_MESSAGE);
			        message.setCaption("SS Prediction Engine in Action");
			        message.setPosition(Position.MIDDLE_CENTER);
		        	message.show(Page.getCurrent());
		        	
		         long classificatinStart = System.currentTimeMillis(); 
	
		         Predictor predictor = MLService.getInstance().getPredictor();
		         predictor.setInputDataSetAbsolutePath(uploadFile.getAbsolutePath());
		         PredictionOutputBean outputBean = predictor.getPredictions();
		         
		         long classificatinEnd = System.currentTimeMillis();
		          
		         long RenderingStart = System.currentTimeMillis(); 
		         
		         IndexedContainer indexedContainer = buildResultTableFromPredictionOutput(outputBean);
		          
		          /* Finally, let's update the table with the container */
		          //resulTable.setCaption(finishedEvent.getFilename());
		          resultTable.setContainerDataSource(indexedContainer);
		          resultPanel.setVisible(true);
		          
		          indexedContainer = buildEvalTableFromPredictionOutput(outputBean.getEvaluatorBean());
		          
		          /* Finally, let's update the table with the container */
		          //evalTable.setCaption(finishedEvent.getFilename());
		          evalTable.setContainerDataSource(indexedContainer);
		          evalPanel.setVisible(true);
		          long RenderingEnd = System.currentTimeMillis(); 
		          
		          message.setCaption("SS Prediction Engine Action Finished "+ Double.valueOf(df.format((classificatinEnd - classificatinStart)/1000)) + " (s) and Rendering took " + Double.valueOf(df.format((RenderingEnd - RenderingStart)/1000)) + " (s)");
		          message.setDelayMsec(-1);
		          
		        } catch (Exception e) {
		          e.printStackTrace();
		        }finally{
		        	uploadFile.delete();
		        }
		      }
		    });
		
		   upload.setButtonCaption("Predict !!!");
		   upload.setWidth("375px");
		   upload.setHeight("25px");
		   
		   dataPanelLayout.addComponent(upload, "top:50.0px;left:10.0px;");
			
		return dataPanelLayout;
	}

	
	private Panel buildEvalPanel() {
		
		Panel evalPanel = new Panel();
		evalPanel.setCaption("Prediction Evaluation Metrics");
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
		resultPanel.setCaption("Opinion Miner Results (Fast Random Forest [WEKA] --> Prediction)");
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
	
	/**
	   * Uses http://opencsv.sourceforge.net/ to read the entire contents of a CSV
	   * file, and creates an IndexedContainer from it
	   *
	   * @param reader
	   * @return
	   * @throws IOException
	   */
	  protected IndexedContainer buildResultTableFromPredictionOutput(PredictionOutputBean outputBean) throws IOException {
	    
		String[] propertyIds = null; String[] fields = null;  
		IndexedContainer container = new IndexedContainer();
	    
	    addColumHeaders(container, outputBean.getColumnHeaders());
	    
	    propertyIds = new String[outputBean.getColumnHeaders().size()];
	    fields = new String [propertyIds.length];
	    
	    for(PredictionInputBean inputBean: outputBean.getInputBeans()){
	    	int i = 0;
	    	
	    	propertyIds[i] = outputBean.getColumnHeaders().get(i);
	    	fields[i] = inputBean.getProperty1();
	    	i++;
	    	
	    	propertyIds[i] = outputBean.getColumnHeaders().get(i);
	    	fields[i] = inputBean.getProperty2();
	    	i++;
	    	
	    	propertyIds[i] = outputBean.getColumnHeaders().get(i);
	    	fields[i] = inputBean.getProperty3();
	    	i++;
	    	
	    	propertyIds[i] = outputBean.getColumnHeaders().get(i);
	    	fields[i] = inputBean.getProperty4();
	    	i++;
	    	
	    	propertyIds[i] = outputBean.getColumnHeaders().get(i);
	    	fields[i] = inputBean.getProperty5();
	    	i++;
	    	
	    	propertyIds[i] = outputBean.getColumnHeaders().get(i);
	    	fields[i] = inputBean.getProperty6();
	    	i++;
	    	
	    	propertyIds[i] = outputBean.getColumnHeaders().get(i);
	    	fields[i] = inputBean.getProperty7();
	    	i++;
	    	
	    	propertyIds[i] = outputBean.getColumnHeaders().get(i);
	    	fields[i] = inputBean.getProperty8();
	    	i++;
	    	
	    	propertyIds[i] = outputBean.getColumnHeaders().get(i);
	    	fields[i] = inputBean.getProperty9();
	    	i++;
	    	
	    	propertyIds[i] = outputBean.getColumnHeaders().get(i);
	    	fields[i] = inputBean.getProperty10();
	    	i++;
	    	
	    	propertyIds[i] = outputBean.getColumnHeaders().get(i);
	    	fields[i] = inputBean.getActualValue();
	    	i++;
	    	
	    	propertyIds[i] = outputBean.getColumnHeaders().get(i);
	    	fields[i] = inputBean.getPredictedValue();
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
	  
	  /**
	   * Uses http://opencsv.sourceforge.net/ to read the entire contents of a CSV
	   * file, and creates an IndexedContainer from it
	   *
	   * @param reader
	   * @return
	   * @throws IOException
	   */
	  protected IndexedContainer buildEvalTableFromPredictionOutput(ClassifierEvaluatorBean evaluatorBean) throws IOException {
	    
		String[] propertyIds = null; String[] fields = null;  
		IndexedContainer container = new IndexedContainer();
	    
	    addColumHeaders(container, Arrays.asList(EVALUATION_HEADERS));
	    
	    propertyIds = new String[10];
	    fields = new String [propertyIds.length];
	   	
	    int i = 0;
	    	
	    propertyIds[i] = EVALUATION_HEADERS[i];
	    fields[i] = String.valueOf(evaluatorBean.getTimeToBuildModelInSecs());
	    i++;
	    
	    propertyIds[i] = EVALUATION_HEADERS[i];
	    fields[i] = String.valueOf(evaluatorBean.getfMeasure());
	    i++;
	    
	    propertyIds[i] = EVALUATION_HEADERS[i];
	    fields[i] = String.valueOf(evaluatorBean.getPrecision());
	    i++;
	    
	    propertyIds[i] = EVALUATION_HEADERS[i];
	    fields[i] = String.valueOf(evaluatorBean.getRecall());
	    i++;
	    
	    propertyIds[i] = EVALUATION_HEADERS[i];
	    fields[i] = String.valueOf(evaluatorBean.getTruePositive());
	    i++;
	    
	    propertyIds[i] = EVALUATION_HEADERS[i];
	    fields[i] = String.valueOf(evaluatorBean.getFalsePositive());
	    i++;
	    
	    propertyIds[i] = EVALUATION_HEADERS[i];
	    fields[i] = String.valueOf(evaluatorBean.getFalseNegative());
	    i++;
	    
	    propertyIds[i] = EVALUATION_HEADERS[i];
	    fields[i] = String.valueOf(evaluatorBean.getTrueNegative());
	    i++;
	    
	    propertyIds[i] = EVALUATION_HEADERS[i];
	    fields[i] = String.valueOf(evaluatorBean.getErrorRate());
	    i++;
	    
	    propertyIds[i] = EVALUATION_HEADERS[i];
	    fields[i] = String.valueOf(evaluatorBean.getRmse());
	    i++;
	    
	    
	    addItem(container, propertyIds, fields);
	   
	    return container;
	  }
	
  
}
