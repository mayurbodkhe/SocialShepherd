package com.ss.ml.forecast;

import hr.irb.fastRandomForest.FastRandomForest;

import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;

//import com.ss.services.DBService;

import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.*;
import weka.classifiers.Classifier;
import weka.classifiers.CostMatrix;
import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.meta.MetaCost;

/**
* <h1>Predictor</h1>
* The class behaves like predictor by implementing 
* Maching Learning Classification Algorithms
*
* @author  Mayur Bodakhe
* @version 1.0
* @since   2014-11-09
*/

public class Predictor {
	
	/** Classfication Algorithm to use */
	private Classifier classifier;
	
	/** Input Data Fully Qualified Path */
	private String inputDataSetAbsolutePath;
	
	/** Percentage data should be used for training model */
	private double percentTrain;
	
	/** Options used while building classifier model */
	private String[] classfierOptions;
	
	/** Train Data Fully Qualified Path */
	private String trainDataSetAbsolutePath;
	
	public String getTrainDataSetAbsolutePath() {
		return trainDataSetAbsolutePath;
	}


	public void setTrainDataSetAbsolutePath(String trainDataSetAbsolutePath) {
		this.trainDataSetAbsolutePath = trainDataSetAbsolutePath;
	}


	/**
     * Returns Classfier Algorithm to be used for Prediction
     * @return The classifier algorithm
     */
	public Classifier getClassifier() {
		return classifier;
	}

	
	/**
     * Sets the Classfier Algorithm to be used for Prediction
     * @param classifier The classifier algorithm
     */
	public void setClassifier(Classifier classifier) {
		this.classifier = classifier;
	}

	
	/**
     * Returns Input Data Fully Qualified Path
     * @return The Input Data Fully Qualified Path
     */
	public String getInputDataSetAbsolutePath() {
		return inputDataSetAbsolutePath;
	}

	/**
     * Sets the Input Data Fully Qualified Path
     * @param inputDataSetAbsolutePath Input Data Fully Qualified Path
     */
	public void setInputDataSetAbsolutePath(String inputDataSetAbsolutePath) {
		this.inputDataSetAbsolutePath = inputDataSetAbsolutePath;
	}


	/**
     * Returns Percentage data should be used for training model
     * @return The Percentage data should be used for training model
     */
	public double getPercentTrain() {
		return percentTrain;
	}

	/**
     * Sets the Percentage data should be used for training model
     * @param percentTrain Percentage data should be used for training model
     */
	public void setPercentTrain(double percentTrain) {
		this.percentTrain = percentTrain;
	}


	/**
     * Returns Options used while building classifier model
     * @return The Options used while building classifier model
     */
	public String[] getClassfierOptions() {
		return classfierOptions;
	}

	/**
     * Sets the Options used while building classifier model
     * @param classfierOptions Options used while building classifier model
     */
	public void setClassfierOptions(String[] classfierOptions) {
		this.classfierOptions = classfierOptions;
	}


	private double timeToBuildModelInSecs = 0.0;
	
	public Predictor(){
		this.classfierOptions = new String [] {"-I", "100", "-threads", "2"};
		this.classifier = new FastRandomForest();
		this.percentTrain = 80.0;
	}
	
	
	
	public PredictionOutputBean getPredictions(){
		
		Instances inputDataSet = null, trainDataSet = null, testDataSet = null;
		Instances split [] = null;
		Classifier model = null;
		PredictionOutputBean outputBean = null;
	    List<PredictionInputBean> inputBeans = null;
	    List<String> headers;
			
		try{
		
			//1. Read Input File ARFF
			inputDataSet = new Instances(new java.io.FileReader(this.inputDataSetAbsolutePath));
			
			//2. Split DataSet into Training and Test
			split = crossValidationSplit(inputDataSet, this.percentTrain);
			trainDataSet = split[0];
			testDataSet = split[1];
			
			//3. get Properties to display/insert
			headers = getInputBeanImpPropertiesHeader(testDataSet);
			inputBeans = getInputBeanImpProperties(testDataSet);
			
			//4. get ColumnHeader
			outputBean = new PredictionOutputBean();
			outputBean.setColumnHeaders(headers);
			
			//5. PreProcess DataSet
			trainDataSet = preProcessing(trainDataSet);
			testDataSet = preProcessing(testDataSet);
			
		    //6. Build Classifier
			model = buildClassifier(trainDataSet);
			
			//7. Evaluate Classifier
			outputBean.setEvaluatorBean(evaluateClassifier(model, trainDataSet, testDataSet));
			
			//8. Write Prediction Status
			inputBeans = updatePredictionStatus(model, testDataSet, inputBeans);
			outputBean.setInputBeans(inputBeans);
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		return outputBean;
	}
	
	private Instances[] crossValidationSplit(Instances inputDataSet, double percentTrain) {
		
		Instances[] split = new Instances[2];
 
		int trainSize = (int) Math.round(inputDataSet.numInstances() * percentTrain / 100); 
		int testSize = inputDataSet.numInstances() - trainSize;
		
		split[0] = new Instances(inputDataSet, 0, trainSize); 
		split[1] = new Instances(inputDataSet, trainSize, testSize);
		
		return split;
	}

	private List<PredictionInputBean> getInputBeanImpProperties(Instances inputDataSet){
		
		List<PredictionInputBean> inputBeans = null;
		PredictionInputBean inputBean = null;
		Instance currInst = null;
		
		try{
			
			inputBeans = new LinkedList<PredictionInputBean>();
			
			for (int i = 0; i < inputDataSet.numInstances(); i++) {
				inputBean = new PredictionInputBean();
				
				currInst = inputDataSet.instance(i);
				
				if(currInst.attribute(0).type() == 0)
					inputBean.setProperty1(String.valueOf(currInst.value(0)));
				else
					inputBean.setProperty1(currInst.stringValue(0));
			
				
				if(currInst.attribute(1).type() == 0)
					inputBean.setProperty2(String.valueOf(currInst.value(1)));
				else
					inputBean.setProperty2(currInst.stringValue(1));
				
				if(currInst.attribute(2).type() == 0)
					inputBean.setProperty3(String.valueOf(currInst.value(2)));
				else
					inputBean.setProperty3(currInst.stringValue(2));
				
				
				if(currInst.attribute(3).type() == 0)
					inputBean.setProperty4(String.valueOf(currInst.value(3)));
				else
					inputBean.setProperty4(currInst.stringValue(3));
				
				if(currInst.attribute(4).type() == 0)
					inputBean.setProperty5(String.valueOf(currInst.value(4)));
				else
					inputBean.setProperty5(currInst.stringValue(4));
				
				if(currInst.attribute(5).type() == 0)
					inputBean.setProperty6(String.valueOf(currInst.value(5)));
				else
					inputBean.setProperty6(currInst.stringValue(5));
				
				if(currInst.attribute(6).type() == 0)
					inputBean.setProperty7(String.valueOf(currInst.value(6)));
				else
					inputBean.setProperty7(currInst.stringValue(6));
				
				if(currInst.attribute(7).type() == 0)
					inputBean.setProperty8(String.valueOf(currInst.value(7)));
				else
					inputBean.setProperty8(currInst.stringValue(7));
				
				if(currInst.attribute(8).type() == 0)
					inputBean.setProperty9(String.valueOf(currInst.value(8)));
				else
					inputBean.setProperty9(currInst.stringValue(8));
				
				if(currInst.attribute(9).type() == 0)
					inputBean.setProperty10(String.valueOf(currInst.value(8)));
				else
					inputBean.setProperty10(currInst.stringValue(9));
				
				inputBean.setActualValue(String.valueOf(currInst.stringValue(currInst.numAttributes()-1)));
				
				inputBeans.add(inputBean);
			}
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		return inputBeans;
	}
	
	private List<String> getInputBeanImpPropertiesHeader(Instances inputDataSet){
		
		List<String> headers = null;
			
		try{
			
			headers = new LinkedList<String>();
			
			headers.add(inputDataSet.attribute(0).name());
			headers.add(inputDataSet.attribute(1).name());
			headers.add(inputDataSet.attribute(2).name());
			headers.add(inputDataSet.attribute(3).name());
			headers.add(inputDataSet.attribute(4).name());
			headers.add(inputDataSet.attribute(5).name());
			headers.add(inputDataSet.attribute(6).name());
			headers.add(inputDataSet.attribute(7).name());
			headers.add(inputDataSet.attribute(8).name());
			headers.add(inputDataSet.attribute(9).name());
			
			headers.add("ActualValue");
			headers.add("PredictedValue");
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		return headers;
	}

	
	private Instances preProcessing(Instances inputDataSet){
	
		ReplaceMissingValues replaceMissingFilter = null;
		Remove removeFilter = null;
		Normalize normalizeFilter = null;
		Instances replaceMissingDataSet = null, removeDataSet = null, normalizeDataSet = null;
		
		try{
			
			//Clean up training data
			replaceMissingFilter = new ReplaceMissingValues();
			replaceMissingFilter.setInputFormat(inputDataSet);
			replaceMissingDataSet = Filter.useFilter(inputDataSet, replaceMissingFilter);
		    
		    //Ignore ID column
			removeFilter = new Remove();
			removeFilter.setAttributeIndices("1");
			removeFilter.setInputFormat(replaceMissingDataSet);
			removeDataSet = Filter.useFilter(replaceMissingDataSet, removeFilter);
		
		    //Normalize training data
			normalizeFilter = new Normalize();
			normalizeFilter.setInputFormat(removeDataSet);
			normalizeDataSet = Filter.useFilter(removeDataSet, normalizeFilter);
		
		    //Set class attribute for pre-processed training data
			normalizeDataSet.setClassIndex(normalizeDataSet.numAttributes() - 1);
			
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
	    
	    return normalizeDataSet;
	    
	}
	

	private Classifier buildClassifier(Instances trainingSet) throws Exception {
		
		long start = 0;
		FastRandomForest classifier = null;
		MetaCost metaCost = null;
		CostMatrix costMatrix = null;
		 
		try{
			
			start = System.currentTimeMillis();
		    
			classifier = (FastRandomForest) this.classifier;
		    classifier.setOptions(this.classfierOptions);
		    
		    metaCost = new MetaCost();
		    metaCost.setClassifier(classifier);
		    costMatrix = new CostMatrix(2);
		    metaCost.setCostMatrix(costMatrix);
		    metaCost.buildClassifier(trainingSet);
		    
			timeToBuildModelInSecs =  (double) (System.currentTimeMillis() - start)/1000.0;
			
		}catch (Exception ex){
			ex.printStackTrace();
		}
			    
	    return metaCost;
	}
	
	private ClassifierEvaluatorBean evaluateClassifier(Classifier model, Instances trainDataSet, Instances testDataSet){
		
		Evaluation eval = null;
		ClassifierEvaluatorBean evaluatorBean = null;
		double[][] confusionMatrix = null;
		DecimalFormat df = null;
		
		try{
			eval = new Evaluation(trainDataSet);
			eval.evaluateModel(model, testDataSet);
	    
			evaluatorBean = new ClassifierEvaluatorBean();
			
			df = new DecimalFormat("#.##");
			
			evaluatorBean.setTimeToBuildModelInSecs(Double.valueOf(df.format(timeToBuildModelInSecs)));
			
			evaluatorBean.setfMeasure(Double.valueOf(df.format(eval.weightedFMeasure())));
			evaluatorBean.setPrecision(Double.valueOf(df.format(eval.weightedPrecision())));
			evaluatorBean.setRecall(Double.valueOf(df.format(eval.weightedRecall())));
			
			confusionMatrix = eval.confusionMatrix();
			evaluatorBean.setTruePositive(Double.valueOf(df.format(confusionMatrix[0][0])));
			evaluatorBean.setFalseNegative(Double.valueOf(df.format(confusionMatrix[0][1])));
			evaluatorBean.setFalsePositive(Double.valueOf(df.format(confusionMatrix[1][0])));
			evaluatorBean.setTrueNegative(Double.valueOf(df.format(confusionMatrix[1][1])));
		
			evaluatorBean.setErrorRate(Double.valueOf(df.format(eval.errorRate())));
			evaluatorBean.setRmse(Double.valueOf(df.format(eval.rootMeanSquaredError())));
			
		}catch (Exception ex){
			ex.printStackTrace();
		}
		
		return evaluatorBean;
	}
	
	private List<PredictionInputBean> updatePredictionStatus(Classifier model, Instances testDataSet, List<PredictionInputBean> inputBeans){
	
		Instance currentInst = null;
		int predictedClass = 0;
		PredictionInputBean inputBean = null;
		try{
			for (int i = 0; i < testDataSet.numInstances(); i++) {
		        currentInst = testDataSet.instance(i);
		        predictedClass = (int) model.classifyInstance(currentInst);
		        inputBean = inputBeans.get(i);
		        inputBean.setPredictedValue(predictedClass == 0 ? "nonchurner" : "churner");
		        inputBeans.set(i, inputBean);
		        
		    }
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		return inputBeans;
	}
	
	
	public PredictionOutputBean determineChurners(){
		
		Instances trainDataSet = null, testDataSet = null;
		Classifier model = null;
		PredictionOutputBean outputBean = null;
	    List<PredictionInputBean> inputBeans = null;
	    List<String> headers;
			
		try{
		
			//1. Read Model Building Dataset File ARFF
			trainDataSet = new Instances(new java.io.FileReader(this.trainDataSetAbsolutePath));
			
			//2. Read Input File ARFF
			testDataSet = new Instances(new java.io.FileReader(this.inputDataSetAbsolutePath));
					
			//3. get Properties to display/insert
			headers = getInputBeanImpPropertiesHeader(testDataSet);
			inputBeans = getInputBeanImpProperties(testDataSet);
			
			//4. get ColumnHeader
			outputBean = new PredictionOutputBean();
			outputBean.setColumnHeaders(headers);
			
			//5. PreProcess DataSet
			trainDataSet = preProcessing(trainDataSet);
			testDataSet = preProcessing(testDataSet);
			
		    //6. Build Classifier
			model = buildClassifier(trainDataSet);
			
			//7. Write Prediction Status
			inputBeans = updatePredictionStatus(model, testDataSet, inputBeans);
			outputBean.setInputBeans(inputBeans);
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		return outputBean;
	}
	
	public static void main(String[] args) {
		
		Predictor predictor = new Predictor();
		predictor.setInputDataSetAbsolutePath("C:\\Users\\Mayur\\Downloads\\telekom-www\\telekom-www\\TelecomChurnTestData_400.arff");
		PredictionOutputBean outputBean = predictor.getPredictions();
		System.out.println(outputBean.getColumnHeaders().size());
		
//		for(PredictionInputBean inputBean:outputBean.getInputBeans()){
//			System.out.println(inputBean.getPredictedValue() + "-->"+inputBean.getProperty1());
//		}
//		
//		DBService.getInstance().getDaoFactory().getPredictionDAO().insertPredictionCustomerDetails(outputBean);
	}
	
	
	
	
}
