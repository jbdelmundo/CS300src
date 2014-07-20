package data;

import java.io.PrintWriter;
import java.util.ArrayList;

public class ClusterPerformance {

	
	public String Directory;
	public int testfiles;
	public int trainfiles[];
	
	public int traindata;
	public int testdata;
	
	public int correct;
	public int incorrect;
	
	public int truepositive;
	public int truenegative;
	public int falsepositive;
	public int falsenegative;
	
	public int assigned, unassigned;
	public int certain;
	public int guesses;
	public int correctguesses;
	
	public int clustersFormed;
	
	
	public double accuracy(){
		return ((double)correct/testdata);
	}
	
	public int dataSize(){
		return traindata+testdata;
	}
	
	public double testVStrainRatio(){
		return (double)testdata/traindata;
	}
	
	public void showstats(){
		
	
		System.out.println("Stats:");
		System.out.println("TrainData \t" + traindata);
		System.out.println("TestData \t" + testdata);
		System.out.println("DataSize \t" + (traindata+testdata));
		System.out.println();
		System.out.println("Correct " + correct  + "\tout of "+ testdata + "\t" + (correct*1.0/testdata));
		System.out.println("InCorrect " + incorrect  + "\tout of "+ testdata + "\t" + (incorrect*1.0/testdata));
		System.out.println("\tFalse positive " + falsenegative  + "\tout of "+ testdata + "\t" + (falsenegative*1.0/testdata));
		System.out.println("\tFalse negative " + falsepositive  + "\tout of "+ testdata + "\t" + (falsepositive*1.0/testdata));
		System.out.println("Certain " + certain  + "\tout of "+ testdata + "\t" + (certain*1.0/testdata));
		System.out.println();
		System.out.println("Assigned " + assigned  + "\tout of "+ testdata + "\t" + (assigned*1.0/testdata));
		System.out.println("Unassigned " + unassigned  + "\tout of "+ testdata + "\t" + (unassigned*1.0/testdata));
		System.out.println("Guesses " + guesses  + "\tout of "+ testdata + "\t" + (guesses*1.0/testdata));
		
	}
	
	public void printToFile( PrintWriter pw){
		pw.println("Stats:");
		pw.println("TrainData \t" + traindata);
		pw.println("TestData \t" + testdata);
		pw.println("DataSize \t" + (traindata+testdata));
		pw.println();
		pw.println("Correct " + correct  + "\tout of "+ testdata + "\t" + (correct*1.0/testdata));
		pw.println("InCorrect " + incorrect  + "\tout of "+ testdata + "\t" + (incorrect*1.0/testdata));
		pw.println("\tFalse positive " + falsenegative  + "\tout of "+ testdata + "\t" + (falsenegative*1.0/testdata));
		pw.println("\tFalse negative " + falsepositive  + "\tout of "+ testdata + "\t" + (falsepositive*1.0/testdata));
		pw.println("Certain " + certain  + "\tout of "+ testdata + "\t" + (certain*1.0/testdata));
		pw.println();
		pw.println("Assigned " + assigned  + "\tout of "+ testdata + "\t" + (assigned*1.0/testdata));
		pw.println("Unassigned " + unassigned  + "\tout of "+ testdata + "\t" + (unassigned*1.0/testdata));
		pw.println("Guesses " + guesses  + "\tout of "+ testdata + "\t" + (guesses*1.0/testdata));
		pw.println("Clusters Formed\t" + clustersFormed );
	}
	
	
	
	public static void computeAverageStats(ArrayList<ClusterPerformance> list, PrintWriter pw){
		
		
		
		int traindata = 0;
		int testdata = 0;
		
		int correct = 0;
		int incorrect = 0;
		
		
		int falsepositive = 0;
		int falsenegative = 0;
		
		
		int assigned = 0, unassigned = 0;
		int certain = 0;
		int guesses = 0;
		int correctguesses = 0;
		
		int clustersFormed = 0;
		
		traindata = list.get(0).traindata;
		testdata = list.get(0).testdata;
		
		double size = list.size();
		for (int i = 0; i < list.size(); i++) {
			
			correct += list.get(i).correct;
			incorrect += list.get(i).incorrect;
			
			falsepositive += list.get(i).falsepositive;
			falsenegative  += list.get(i).falsenegative;
			
			certain += list.get(i).certain;
			
			assigned += list.get(i).assigned;
			unassigned += list.get(i).unassigned;
			
			correctguesses += list.get(i).correctguesses;
			clustersFormed += list.get(i).clustersFormed;
		}
		
		
		
		
		pw.println("=============AVERAGE STATS ======================");
		pw.println("Stats:");
		pw.println("TrainData " + traindata);
		pw.println("TestData " + testdata);
		pw.println("DataSize " + (traindata+testdata));
		pw.println("TrainTestRatio " + ((double)traindata/testdata));
		pw.println();
		pw.println("Correct "  + (correct*1.0/(testdata*size)));
		pw.println("InCorrect " +  (incorrect*1.0/(testdata*size)));
		pw.println("\tFalse positive " + (falsenegative*1.0/(testdata*size)));
		pw.println("\tFalse negative " + (falsepositive*1.0/(testdata*size)));
		pw.println("Certain " + (certain*1.0/(testdata*size)));
		pw.println();
		pw.println("Assigned " + assigned  + "\tout of "+ testdata + "\t" + (assigned*1.0/(testdata*size)));
		pw.println("Unassigned " + unassigned  + "\tout of "+ testdata + "\t" + (unassigned*1.0/(testdata*size)));
		pw.println("Correct Guesses " +(correctguesses*1.0/(testdata*size)));
		pw.println("ClustersFormed " + (clustersFormed*1.0/(testdata*size)));
		
	}
}
