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

	
	
		//estimate lang
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
		
		// steepness sensitivity: 0 (sensitive) -> 1 (insensitve)
		double end_xi = 0.1; 			//higher
		double starting_xi = 0;			//low
		double reset_xi = 0.1;			//lower
		
		int minPts = 2;				//minimum size of area
		int nonConseqLimit = 2;		//keep it low
		
		
		ArrayList<SteepArea> areas =  findSteepAreas(ordering, starting_xi, reset_xi, end_xi, minPts,nonConseqLimit);
		
		for (SteepArea steepArea : areas) {
			System.out.println("Area:" + steepArea.startIndex +"  " + steepArea.endIndex + " " + steepArea.isSteepUp);
		}
		
		
		
		System.out.println("Areas " + areas.size());
		OpticsPlot.plotGraphAreas("Clusters", ordering, areas);
		OpticsPlot.plotGraph("WEw", ordering, OpticsPlot.BY_ATTACK_CATEGORY);
//		OpticsPlot.plotGraph("Test Vs Train", ordering, OpticsPlot.BY_TRAIN_VS_TEST);
		
	}
	
	/**
	 * 
	 * @param points
	 * @param areas
	 */
	public void extractClusters(ArrayList<ReachabilityPoint> points, ArrayList<SteepArea> areas){
		ArrayList<SteepArea> SetOfSteepDownAreas = new ArrayList<SteepArea>();
		ArrayList<SteepArea> SetOfClusters = new ArrayList<SteepArea>();
		
		int index = 0;
		double mib = 0;
		
		int areaCounter = 0;
		SteepArea currentArea = areas.get(areaCounter);
		
		//if start of steepdown at index
		while(index < points.size()){
			mib = Math.max(mib, points.get(index).reachability);
			
			//update currentarea
			if(index > currentArea.endIndex){
				areaCounter++;
				if(areaCounter < areas.size()) currentArea = areas.get(areaCounter);
			}
			
			if(currentArea.startIndex == index && currentArea.isSteepUp == false){
				//update mib values
				
				
			}else if(currentArea.startIndex == index && currentArea.isSteepUp == true){
				
			}else{
				index++;
			}
		}
	}
	
	private void updateMibValues( ArrayList<SteepArea> areas, double update){
		for (SteepArea steepArea : areas) {
			steepArea.updateMIB(update);
		}
	}
	
	
	public void assignLabels(ArrayList<ReachabilityPoint> points, ArrayList<SteepArea> areas){
		int areaCounter = 0;
		SteepArea currentArea = areas.get(areaCounter);
		boolean isIn = false;		
		
		boolean isSteepUp = false, isFlat = false;
		
		for (int i = 0; i < points.size(); i++) {
			
			
			ReachabilityPoint point = points.get(i);
			
			
			if(i > currentArea.endIndex){
				areaCounter++;
				if(areaCounter < areas.size()) currentArea = areas.get(areaCounter);
			}
			
			if(i >= currentArea.startIndex && i <= currentArea.endIndex){
				isFlat = false;
				isSteepUp = currentArea.isSteepUp;				
			}else{
				isFlat = true;
			}
			
			if(point.hasLabel){
				point.assignedlabel = point.label;
				continue;
			}
		}
	}
	
	public ArrayList<SteepArea>  findSteepAreas(ArrayList<ReachabilityPoint> points, double start_xi, double reset_xi,
			double end_xi, int minPts, int nonConseqLimit){
		
		ArrayList<SteepArea> areas = new ArrayList<>();
		
		//Preliminaries
		
		
		SteepArea currentArea = null;
		
		int nonConseq = 0;
		
		for (int i = 0; i < points.size()-1; i++) {
			ReachabilityPoint currentPoint = points.get(i);
			ReachabilityPoint nextPoint = points.get(i+1);
			
			System.out.print("PT:" + i );
//			System.out.print("\t" +currentPoint.reachability +" " + nextPoint.reachability );
			
			boolean nonConseqFailure = false;
			int resetTo = i;
			
			if(currentArea != null){						//in steepArea
				
				System.out.print("\tin");
				
				
				if(nextPoint.reachability < 0){
					endArea(currentArea, i, minPts, areas,points);
					currentArea = null;
					System.out.print("\tEnd nega");
				}else
				//if in steepUP
				if(currentArea.isSteepUp){
					
					
					//end if steepDown
					if(isSteepDownPoint(currentPoint.reachability, nextPoint.reachability,end_xi)){
						endArea(currentArea, i-1, minPts, areas,points);
						currentArea = null;
						System.out.print("\tEnd OPP");
						
					}else if(isAsHigh(currentPoint.reachability, nextPoint.reachability)){
						System.out.print("\tContinue");
						nonConseq = 0;
					}else if(isSteepUpPoint(currentPoint.reachability, nextPoint.reachability,reset_xi)){
						System.out.print("\tReset");
						currentArea.endIndex = i;
						nonConseq = 0;
					}else{
						nonConseq++;
						if(nonConseq > nonConseqLimit){
							endArea(currentArea, currentArea.endIndex, minPts, areas,points);
							nonConseqFailure = true;
							resetTo = currentArea.endIndex + 1;
							currentArea = null;
						}
						System.out.print("\tNonConseq"+nonConseq);
						
					}
					

					
				} else
				
				//if in steepDown
				if(!currentArea.isSteepUp){
					
					//end if steepUP
					if(isSteepUpPoint(currentPoint.reachability, nextPoint.reachability,end_xi)){
						endArea(currentArea, i-1, minPts, areas,points);
						currentArea = null;
						System.out.print("\tEnd OPP");
						
					}else if(isAsLow(currentPoint.reachability, nextPoint.reachability)){
						System.out.print("\tContinue");
						nonConseq = 0;
					}else if(isSteepDownPoint(currentPoint.reachability, nextPoint.reachability,reset_xi)){
						System.out.print("\tReset");
						currentArea.endIndex = i;
						nonConseq = 0;
					}else{
						nonConseq++;
						if(nonConseq > nonConseqLimit){
							endArea(currentArea, currentArea.endIndex, minPts, areas,points);
							nonConseqFailure = true;
							resetTo = currentArea.endIndex + 1;
							currentArea = null;
						}
						System.out.print("\tNonconseq"+nonConseq);
						
					}
				}
				
				
			}
			
			if(currentArea == null && !nonConseqFailure){	// not in steeparea
				
				System.out.print("\tout");
								
				if(isSteepUpPoint(currentPoint.reachability, nextPoint.reachability,start_xi) ){
					currentArea =  new SteepArea(i,i,SteepArea.UP);			//start steepUp
					nonConseq = 0;
					System.out.print("\tStart UP");
					
				} else
				
				if(isSteepDownPoint(currentPoint.reachability, nextPoint.reachability,start_xi)){
					currentArea =  new SteepArea(i,i,SteepArea.DOWN);		//start steepDown
					nonConseq = 0;
					System.out.print("\tStart Down");
					
				}
				
				
			}
			
			if(nonConseqFailure)
				i = resetTo-1;
			
			System.out.print("\t end \t"+ (currentArea == null));
			System.out.print("\n");
			
		}
		
		return areas;
	}
	
	private void endArea(SteepArea currentArea,int end, int minPts, ArrayList<SteepArea> areas,ArrayList<ReachabilityPoint> points){
		currentArea.endIndex = end;
		if(currentArea.size() >= minPts){
			areas.add(currentArea);
		}
		if(points.get(currentArea.startIndex).reachability <0 ){
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
		return reachability <= reachabilityNext;
	}

	private boolean isAsLow(double reachability, double reachabilityNext){
		if(reachability <0)
			return !(reachabilityNext <0);
		if(reachabilityNext<0)
			return false;
		return reachability >= reachabilityNext;
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
