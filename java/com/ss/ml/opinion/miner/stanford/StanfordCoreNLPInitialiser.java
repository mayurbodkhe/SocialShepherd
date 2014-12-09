package com.ss.ml.opinion.miner.stanford;

import java.util.Properties;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

public class StanfordCoreNLPInitialiser {
	
	StanfordCoreNLP pipeline;
	
	private void defaultInit(){
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, pos, lemma, parse, sentiment, ner");
		props.setProperty("ner.model", "edu/stanford/nlp/models/ner/english.all.3class.distsim.crf.ser.gz");
	
		pipeline = new StanfordCoreNLP(props);	
	}
	
	public StanfordCoreNLPInitialiser(){
		if(pipeline == null)
			defaultInit();
	}

	public StanfordCoreNLP getPipeline() {
		return pipeline;
	}

	public void setPipeline(StanfordCoreNLP pipeline) {
		this.pipeline = pipeline;
	}

}
