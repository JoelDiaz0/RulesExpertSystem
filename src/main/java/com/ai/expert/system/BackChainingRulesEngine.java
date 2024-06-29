package com.ai.expert.system;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.projog.api.QueryResult;
import org.projog.core.term.Term;

public class BackChainingRulesEngine extends RulesEngine {

	private static final System.Logger LOGGER = System.getLogger(BackChainingRulesEngine.class.getName());
	
	private static final String TYPE_OF_INFERENCE = "/com/ai/expert/system/back_channing_inference.pl";
		
	public BackChainingRulesEngine() throws IOException, URISyntaxException {
		super();
		
		var inferenceTypeInput = getClass().getResourceAsStream(TYPE_OF_INFERENCE);
		var inferenceTypeReader = new InputStreamReader(inferenceTypeInput);  
		computerProblems.consultReader(inferenceTypeReader);
	}
	
	@Override
	public Optional<ArrayList<String>> identifyProblem(List<String> indicators) {
		
		   clearIndicators();

		   LOGGER.log(System.Logger.Level.INFO, "Indicator Values: " + indicators);
		   
	       addAllIndicator(indicators);
	              
	       QueryResult queryResult = computerProblems.executeQuery("diagnostic(X)."); 
	       Optional<ArrayList<String>> result = Optional.empty();

	       var possiblesResponse = new ArrayList<String>();
	       while(queryResult.next()) {
		       Term term = queryResult.getTerm("X");
		       String problemName = term.getName();
		       possiblesResponse.add(problemName);	      		     		      
		       LOGGER.log(System.Logger.Level.INFO, "Response: " + problemName);	
	       }
	       
	       if (!possiblesResponse.isEmpty()) {
		       result = Optional.of(possiblesResponse);
	       }
	         
	       return result;
	}
}