package com.ai.expert.system;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.System.Logger.Level;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.json.JSONObject;
import org.projog.api.Projog;

public abstract class RulesEngine {

	protected Projog computerProblems;

	private Path prologFilePath;
	
	public static final String KNOWLEDGE_DATA_NAME = "computer_problems_databse";
	
	public static final String MAIN_DATABASE_NAME = "/com/ai/expert/system/database.json";
	
	private static final System.Logger LOGGER = System.getLogger(RulesEngine.class.getName());
   	
	protected RulesEngine() throws IOException, URISyntaxException {	     	   
		computerProblems = new Projog();	
		prologFilePath = createPrologFile(KNOWLEDGE_DATA_NAME);		
		computerProblems.consultReader(Files.newBufferedReader(prologFilePath));
					
		LOGGER.log(System.Logger.Level.INFO, prologFilePath.getFileName() + " loaded successfully");
   }
    
   public abstract Optional<ArrayList<String>> identifyProblem(List<String> indicators);
       
   public static String convertIndicatorValueToOriginalValue(String indicatorValue) {	     	   
	   var finalValue = indicatorValue.replace(" ", "_");  
	   LOGGER.log(System.Logger.Level.DEBUG, "Indicator Original Value: " + finalValue);	   
	   return finalValue;
   }
   
   public static String convertOriginalIndicatorToView(String indicatorValue) {	     	   
	   var finalValue = indicatorValue.replace("_", " ");  
	   LOGGER.log(System.Logger.Level.DEBUG, "Indicator Original Value: " + finalValue);	   
	   return finalValue;
   }
     
   public static List<String> convertIndicatorsListToView(List<String> indicatorsList){	   
	   List<String> newIndicatorsList = new ArrayList<>();	   
	   for (var value : indicatorsList) {
		   newIndicatorsList.add(convertOriginalIndicatorToView(value));
	   }	   
	   return newIndicatorsList;
   }
   
   public void clearIndicators() {
	   computerProblems.executeOnce("retractall(indicator(_)).");	
   }
	
   public void addIndicator(final String INDICATOR) {
	   computerProblems.executeOnce("assertz(indicator(" + INDICATOR + ")).");
   }
	
   public void addAllIndicator(List<String> indicators) {	
	   for (final var INDICATOR : indicators) {
		   computerProblems.executeOnce("assertz(indicator(" + INDICATOR + ")).");
	   }	   
   }
  
   public static double getPercentageOfSimilarity(List<String> list1, List<String> list2) {
	   
	   if (list1 == null || list2 == null) {
		   throw new IllegalArgumentException();
	   }
	    
       var set1 = new HashSet<>(list1);
       var set2 = new HashSet<>(list2);

       var intersection = new HashSet<>(set1);
       intersection.retainAll(set2);

       var union = new HashSet<>(set1);
       union.addAll(set2);

       double jaccardIndex = (double) intersection.size() / union.size();

       return jaccardIndex * 100.0;  
   }
   
   public static Map<String, ArrayList<String>> getProblemAndIndicators() throws IOException {
		
		Map<String, ArrayList<String>> map = new HashMap<>();
		var json = loadJSONFile();
		var jsonProblemsList = json.getJSONArray("problems");
		
		String problemName;
		ArrayList<String> indicatorsName;
		
		for (final var JSON_PROBLEM_VALUE : jsonProblemsList) {				
			if (JSON_PROBLEM_VALUE instanceof JSONObject problem) {	
				
				problemName = problem.getString("problem");
				var tempList = problem.getJSONArray("indicators").toList();
				indicatorsName = new ArrayList<>();
				
				for (final var VALUE : tempList) {
					if (VALUE instanceof String newValue) {
						indicatorsName.add(newValue);
					}
				}
				
				map.put(problemName, indicatorsName);
			}					
		}
		
		return map;
	}
	
	public static List<String> getAllIndicators() throws IOException {	
		var json = loadJSONFile();
		var list = json.getJSONArray("indicator").toList();
		List<String> indicatorsList = new ArrayList<>();
		
		for (final var VALUE : list) {
			if (VALUE instanceof String indicator) {
				indicatorsList.add(indicator);
			}		
		}
			
		return indicatorsList;
	}
	
	public static Optional<List<String>> getIndicatorsOfProblem(String problem) throws IOException {
		var json = loadJSONFile();		
		var jsonProblemsList = json.getJSONArray("problems");	
		Optional<List<Object>> result = Optional.empty();
		Optional<List<String>> indicatorsList;
		
		for (final var JSON_PROBLEM_VALUE : jsonProblemsList) {
			if (JSON_PROBLEM_VALUE instanceof JSONObject problemName && problemName.getString("problem").compareTo(problem) == 0) {				
					result = Optional.of(problemName.getJSONArray("indicators").toList());
					break;
			}					
		}
			
		if (result.isEmpty()) {
			return Optional.empty();
		} else {
			List<String> results = new ArrayList<>();
			for (final var VALUE : result.get()) {
				if (VALUE instanceof String indicatorName) {
					results.add(indicatorName);
				}
			}
			indicatorsList = Optional.of(results);
		}
		
		return indicatorsList;
	}
	
	public static JSONObject loadJSONFile() throws IOException {	
		var in = RulesEngine.class.getResourceAsStream(MAIN_DATABASE_NAME);	
		var reader = new BufferedReader(new InputStreamReader(in));
		var stringBuilder = new StringBuilder();
		String line;
		
		while ((line = reader.readLine()) != null) {
			stringBuilder.append(line);
		}	
		
		reader.close();

		return new JSONObject(stringBuilder.toString());
	}

	public static Path createPrologFile(String name) throws IOException, URISyntaxException {					
       
		CodeSource codeSource = RulesEngine.class.getProtectionDomain().getCodeSource();
		File jarFile = new File(codeSource.getLocation().toURI().getPath());
		Path jarDir = jarFile.getParentFile().toPath();
     
		Path tempFile = Files.createTempFile(jarDir, name, ".pl");
		
		var problemsAndIndicators = getProblemAndIndicators();
                     
		try (OutputStream outputStream = Files.newOutputStream(tempFile)) {       	
			var prologFile = new StringBuilder(":- dynamic indicator/1.\n");          
			for (final var DATA : problemsAndIndicators.entrySet()) {  	 	
				String predicate = String.format("problem(%s, %s).%s", DATA.getKey(), DATA.getValue(), "\n");
				prologFile.append(predicate);          
			}                         
			outputStream.write(prologFile.toString().getBytes());                                     
		} catch (IOException e) {
			e.printStackTrace();     
		}	
          
		return tempFile;
	}
	
	public void deletePrologFile() throws IOException {		
		var prologFileName = prologFilePath.getFileName();	
        Files.deleteIfExists(prologFilePath);
        LOGGER.log(Level.INFO, prologFileName + " deleted successfully");
	}
}