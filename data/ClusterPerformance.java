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
	
	public int addedCorrect;
	public int addedIncorrect;
	
	
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
		pw.println("Added  Correct\t" + addedCorrect + "\t out of " + (addedCorrect+addedIncorrect) + "\t" + (double)addedCorrect/(addedCorrect+addedIncorrect));
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
	
	public static void trackImprovement(ArrayList<ClusterPerformance> list, PrintWriter pw){
		
		
		//Header
		pw.print("Run,");
		pw.print("TrainData,");
		pw.print("TestData,");
		pw.print("Correct,");
		pw.print("Incorrect,");
		pw.print("False Positive,");
		pw.print("False Negative,");
		pw.print("Assigned,");
		pw.print("Unassigned,");
		pw.print("Certain,");
		pw.print("Clusters");
		pw.print("AddedCorrect");
		
		
		pw.print("\n");

		
		int ctr = 1;
	
		for (ClusterPerformance cp : list) {
			
			pw.print(ctr + ",");
			pw.print(cp.traindata + ",");
			pw.print(cp.testdata + ",");
			
			double testdata = cp.testdata;
			
			pw.print(cp.correct/testdata + ",");
			pw.print(cp.incorrect/testdata + ",");
			pw.print(cp.falsepositive/testdata + ",");
			pw.print(cp.falsenegative/testdata + ",");
			
			pw.print(cp.assigned/testdata + ",");			
			pw.print(cp.unassigned/testdata + ",");
			pw.print(cp.certain/testdata + ",");
			pw.print(cp.clustersFormed + ",");
			pw.print((double)cp.addedCorrect/(cp.addedCorrect + cp.addedIncorrect) + "");
			
			
			pw.print("\n");
			
			ctr++;			
		}
	}
	
	public static void trackAverageImprovement(ArrayList<ClusterPerformance> list[], PrintWriter pw){
		
		
		//Header
		pw.print("Run,");
		pw.print("TrainData,");
		pw.print("TestData,");
		pw.print("Correct,");
		pw.print("Incorrect,");
		pw.print("False Positive,");
		pw.print("False Negative,");
		pw.print("Assigned,");
		pw.print("Unassigned,");
		pw.print("Certain,");
		pw.print("Clusters");
		pw.print("AddedCorrect");
		
		pw.print("\n");

		
		
		
		for (int i = 0; i < list[0].size(); i++) {				// loop over the retraining
			
			int traindata = 0;
			int testdata = 0;
			
			double correct = 0;
			double incorrect = 0;		
			
			double falsepositive = 0;
			double falsenegative = 0;
					
			double assigned = 0, unassigned = 0;
			double certain = 0;
			
			double clustersFormed = 0;
			double addedCorrect = 0;
			
			
			
			for (int j = 0; j < list.length; j++) {						//compute the average for each retraining , loop over the array
				ClusterPerformance cp = list[j].get(i);					// all the ith run/retraining, jth iteration
				
				traindata = cp.traindata;
				testdata = cp.testdata;
			
				correct +=  (double)cp.correct / testdata;
				incorrect +=  (double)cp.incorrect / testdata;
				falsepositive +=  (double)cp.falsepositive / testdata;
				falsenegative +=  (double)cp.falsenegative / testdata;
				assigned +=  (double)cp.assigned / testdata;
				unassigned +=  (double)cp.unassigned / testdata;
				certain +=  (double)cp.certain / testdata;
				clustersFormed +=  clustersFormed;
				addedCorrect += (double)cp.addedCorrect/(cp.addedCorrect + cp.addedIncorrect);
							
			}	
			double iterations = list.length;
		
			pw.print((i+1) + ",");
			pw.print(traindata + ",");
			pw.print(testdata + ",");
			
			
			pw.print(correct/iterations + ",");
			pw.print(incorrect/iterations + ",");
			pw.print(falsepositive/iterations + ",");
			pw.print(falsenegative/iterations + ",");
			
			pw.print(assigned/iterations + ",");			
			pw.print(unassigned/iterations + ",");
			pw.print(certain/iterations + ",");
			pw.print(clustersFormed/iterations + ",");
			pw.print(addedCorrect/iterations + "");
			
			pw.print("\n");
			
		}
		
	
		
		
	}
	
	
	

}
