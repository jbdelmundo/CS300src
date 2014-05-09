package config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;

public class Config {

	static String ConfigFolder = "data";
	
	//Clustering
	public static boolean isBinaryClassification;
	public static boolean clusterByCategory;
	public static int MinPts;

	//Data
	public static boolean includeAllAttributes;
	public static String InputDataDirectory;
	public static String OutputDataDirectory;


	//Optimizations
	public static boolean useDB;
	public static boolean useParallelNNSearch;
	
		
	//Attributes	
	public static boolean ISINCLUDED[];
	public static int INCLUDED_ATTRIBUTES[];
	public static int symbiolicAttributeCount = 0, continuousAttibuteCount = 0 ;
	
	

	
	
	public static void init(){
		System.out.println("Storing Data");
		Properties prop = new Properties();
		
		
		
		
    		//set the properties value
			prop.setProperty("isBinaryClassification", "true");
			prop.setProperty("clusterByCategory", "true");
			prop.setProperty("MinPts", "5");
			
			prop.setProperty("includeAllAttributes", "true");
			prop.setProperty("InputDataDirectory", "KDD DATASET"+File.separatorChar);
			prop.setProperty("OutputDataDirectory", "KDD DATASET"+File.separatorChar);
			
			prop.setProperty("useDB", "false");
			prop.setProperty("useParallelNNSearch", "false");			
			
				
						

    		//save properties to project root folder
    		try {
				prop.store(new FileOutputStream(ConfigFolder+File.separatorChar+"properties.config"), null);				
				
				
				
				
			} catch (Exception e) {
				e.printStackTrace();
			}
 
    		
		
	}
	
	public static double getIntegerRangeNormalizationValue(){
		return Math.sqrt(Integer.MAX_VALUE);
	}
	
	
	public static int[] getIncludedAttributes(){
		if(INCLUDED_ATTRIBUTES != null){
			return INCLUDED_ATTRIBUTES;
		}
		
		INCLUDED_ATTRIBUTES = new int[7+34];
		
		try {
			Scanner scan = new Scanner(new File(ConfigFolder+File.separatorChar+"included_attr.config"));			
			for(int i = 0; scan.hasNext(); i++){
				INCLUDED_ATTRIBUTES[i] = Integer.parseInt( (scan.nextLine().split(" "))[0] );
				
				if(INCLUDED_ATTRIBUTES[i]== 1)
					if(i <= 6){
						symbiolicAttributeCount++;
					}else{
						continuousAttibuteCount++;
					}
				
			}
			scan.close();
			return INCLUDED_ATTRIBUTES;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}	
		
	public static int readConfig(){
		System.out.println("Reading config file");
		Properties prop = new Properties();
		Properties attr = new Properties();
		 
    	try {
            //load a properties file
    		prop.load(new FileInputStream("config.properties"));
    		attr.load(new FileInputStream("attributes.properties"));
    		
    	} catch (IOException ex) {
    		ex.printStackTrace();
        }
    	
    	//get the property value and print it out
		
		isBinaryClassification = Boolean.parseBoolean(prop.getProperty("isBinaryClassification"));
		clusterByCategory = Boolean.parseBoolean(prop.getProperty("clusterByCategory"));
		
		MinPts = Integer.parseInt(prop.getProperty("MinPts"));
		includeAllAttributes = Boolean.parseBoolean(prop.getProperty("includeAllAttributes"));
		
		InputDataDirectory = prop.getProperty("InputDataDirectory");
		OutputDataDirectory = prop.getProperty("OutputDataDirectory");
    	
		
		return 1;
    	
	}
	
	
	
	
	
	public static void printConfig(){
		System.out.println("isBinaryClassification:"+isBinaryClassification);
		System.out.println("clusterByCategory:"+clusterByCategory);
		System.out.println("MinPts:"+MinPts);
		System.out.println("includeAllAttributes:"+includeAllAttributes);
		System.out.println("inputDataDirectory:"+InputDataDirectory);
		System.out.println("outputDataDirectory:"+OutputDataDirectory);
		System.out.println("useDB:"+useDB);
		System.out.println("useParallelNNSearch:"+useParallelNNSearch);
		
		
		
	}
	
	public static void main(String[] args) {
		
//		Config.readConfig();
		Config.init();
//		Config.printConfig();
	}

}
