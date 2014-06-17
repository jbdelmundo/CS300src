package clustering;


import java.io.File;
import java.util.ArrayList;

import javax.xml.ws.Endpoint;

import data.MinHeap;
import data.ReachabilityPoint;
import data.SteepArea;
import fileIO.OpticsOrderingReader;
import graph.OpticsPlot;

public class OpticsEvaluation2 {
	
	ReachabilityPoint currentpoint;					//for iterations
	ReachabilityPoint nextPoint = null;				//for iterations

	
	
	int nonConseqLimit = 10;	//estimate lang
	//double xi= 0.02;			//difference threshold
	
	public static void main(String[] args) {
		OpticsEvaluation2 opticsEval = new OpticsEvaluation2();
//		opticsEval.evaluate(32, 67);
		opticsEval.evaluate(1, 2);
//		opticsEval.evaluate(0, 1);
		System.out.println("Done");
	}
	
	public void evaluate(int trainItem, int testItem){
		
		String DataDirectory = "RandomPieces_10000";
		String opticsFilename = "test.optics";
		
		ArrayList<ReachabilityPoint> ordering = OpticsOrderingReader.readFile(DataDirectory + File.separatorChar+opticsFilename);
		
		double xi = 0.3;
		int minPts = 5;
		
		
		ArrayList<SteepArea> areas =  findSteepAreas(ordering, xi, minPts);
		
		for (SteepArea steepArea : areas) {
			System.out.println("Area:" + steepArea.startIndex +"  " + steepArea.endIndex + " " + steepArea.isSteepUp);
		}
		
		System.out.println("Areas " + areas.size());
		OpticsPlot.plotGraphAreas("Clusters", ordering, areas);
		OpticsPlot.plotGraph("WEw", ordering, OpticsPlot.BY_ATTACK_CATEGORY);
		
	}
	
	/**
	 * Algo:
	 *  If SteepUp at point p,
	 *  	start steepUP if not in an area
	 *  	continue steepUP 
	 *  	end steepDOWN at p-1 if in steppdown
	 * @param points
	 */
	public ArrayList<SteepArea>  findSteepAreas(ArrayList<ReachabilityPoint> points, double xi, int minPts){
		
		ArrayList<SteepArea> areas = new ArrayList<>();
		
		//Preliminaries
		ReachabilityPoint prevPoint = points.get(0);
		
		SteepArea currentArea = null;
		
		int nonConseq = 0;
		
		for (int i = 0; i < points.size()-1; i++) {
			ReachabilityPoint currentPoint = points.get(i);
			ReachabilityPoint nextPoint = points.get(i+1);
			
			System.out.print("PT:" + i );
//			System.out.print("\t" +currentPoint.reachability +" " + nextPoint.reachability );
			
			
			
			if(currentArea != null){						//in steepArea
				
				System.out.print("\tin");
				
				//if in steepUP
				if(currentArea.isSteepUp){
					
					
					//end if steepDown
					if(isSteepDownPoint(currentPoint.reachability, nextPoint.reachability,xi)){
						endArea(currentArea, i-1, minPts, areas);
						currentArea = null;
						System.out.print("\tEnd OPP");
						
					}else if(isAsHigh(currentPoint.reachability, nextPoint.reachability)){
						System.out.print("\tContinue");
					}else{
						nonConseq++;
						if(nonConseq > nonConseqLimit){
							endArea(currentArea, i-1, minPts, areas);
							currentArea = null;
						}
						System.out.print("\tNonConseq"+nonConseq);
						
					}
					

					
				} else
				
				//if in steepDown
				if(!currentArea.isSteepUp){
					
					//end if steepUP
					if(isSteepUpPoint(currentPoint.reachability, nextPoint.reachability,xi)){
						endArea(currentArea, i-1, minPts, areas);
						currentArea = null;
						System.out.print("\tEnd OPP");
						
					}else if(isAsLow(currentPoint.reachability, nextPoint.reachability)){
						System.out.print("\tContinue");
					}else{
						nonConseq++;
						if(nonConseq > nonConseqLimit){
							endArea(currentArea, i-1, minPts, areas);
							currentArea = null;
						}
						System.out.print("\tNonconseq"+nonConseq);
						
					}
				}
				
				
			}
			
			if(currentArea == null){	// not in steeparea
				
				System.out.print("\tout");
				
				if(isSteepUpPoint(currentPoint.reachability, nextPoint.reachability,xi) ){
					currentArea =  new SteepArea(i,i,SteepArea.UP);			//start steepUp
					nonConseq = 0;
					System.out.print("\tStart UP");
					
				} else
				
				if(isSteepDownPoint(currentPoint.reachability, nextPoint.reachability,xi)){
					currentArea =  new SteepArea(i,i,SteepArea.DOWN);		//start steepDown
					nonConseq = 0;
					System.out.print("\tStart Down");
					
				}
				
				
			}
			
			System.out.print("\t end \t"+ (currentArea == null));
			System.out.print("\n");
			
		}
		
		return areas;
	}
	
	private void endArea(SteepArea currentArea,int end, int minPts, ArrayList<SteepArea> areas){
		currentArea.endIndex = end;
		if(currentArea.size() >= minPts){
			areas.add(currentArea);
		}		
	}
			
	
	
	private boolean isSteepUpPoint(double reachability, double reachabilityNext, double xi){
		if(reachabilityNext <0)	// next is positive inf
			return (reachability >= 0 );
		if(reachability < 0)
			return false;
		return reachability <= reachabilityNext * (1 - xi) && reachabilityNext > 0;
	}
	
	private boolean isSteepDownPoint(double reachability, double reachabilityNext, double xi){
		if(reachability < 0  )
			return (reachabilityNext >= 0);
		
		return reachability * (1 - xi) >= reachabilityNext  && reachability > 0;
	}
	
	private boolean isAsHigh(double reachability, double reachabilityNext){
		if(reachabilityNext<0)
			return !(reachability <0);
		if(reachability <0)
			return false;
		return reachability < reachabilityNext;
	}

	private boolean isAsLow(double reachability, double reachabilityNext){
		if(reachability <0)
			return !(reachabilityNext <0);
		if(reachabilityNext<0)
			return false;
		return reachability > reachabilityNext;
	}
	
	
	public void getAccuracy(ArrayList<ReachabilityPoint> points){
		
		ReachabilityPoint rp;
		int true_positive = 0;
		int false_positive = 0;
		int true_negative = 0;
		int false_negative = 0;
		
		int total_testdata = 0;
		
		ArrayList<ReachabilityPoint> testData = new ArrayList<ReachabilityPoint>();
		
		
		for (int i = 0; i < points.size(); i++) {
			rp = points.get(i);
			
			if(rp.hasLabel){
				continue;
			}
			
			testData.add(rp);
			total_testdata++;
			
			
			if(rp.label == 0 && rp.assignedlabel == 0){			//true negative
				
				true_negative++;						
				
			}else if (rp.label != 0 && rp.assignedlabel != 0){	//true positive
				
				true_positive++;
				
			}else if (rp.label != 0 && rp.assignedlabel == 0){	//false negative
			
				false_negative++;
				
			}else if (rp.label == 0 && rp.assignedlabel != 0){	//false positive
				
				false_positive++;
				
			}
			System.out.println("\t"+rp.label+ "\t" + rp.assignedlabel);
			
			
		}// end of all  points
		
		

		System.out.println("True Positive: " + true_positive);
		System.out.println("True Negative: " + true_negative);
		System.out.println("False Positive: " + false_positive);
		System.out.println("False Negative: " + false_negative);
		System.out.println("Total Data: " + total_testdata);
		System.out.println("Accuracy: " + (true_positive+true_negative)*1.0/total_testdata );
		
	}
}
