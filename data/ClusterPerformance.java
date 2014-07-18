package data;

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
		System.out.println("TrainData " + traindata);
		System.out.println("TestData " + testdata);
		System.out.println("DataSize " + (traindata+testdata));
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
}
