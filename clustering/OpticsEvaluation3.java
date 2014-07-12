package clustering;


import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.ws.Endpoint;

import data.Cluster;
import data.MinHeap;
import data.ReachabilityPoint;
import data.SteepArea;
import fileIO.OpticsOrderingReader;
import graph.OpticsPlot;

public class OpticsEvaluation3 {
	
	ReachabilityPoint currentpoint;					//for iterations
	ReachabilityPoint nextPoint = null;				//for iterations

	
	
		//estimate lang
	//double xi= 0.02;			//difference threshold
	
	public static void main(String[] args) {
		OpticsEvaluation3 opticsEval = new OpticsEvaluation3();
//		opticsEval.evaluate(32, 67);
		opticsEval.evaluate(1, 2);
//		opticsEval.evaluate(0, 1);
		System.out.println("Done OPTICSEVAL 3");
		System.out.println(Double.POSITIVE_INFINITY + " - " + Double.MAX_VALUE);
	}
	
	public void evaluate(int trainItem, int testItem){
		
		String DataDirectory = "RandomPieces_10000";
		String opticsFilename = "test.optics";
		
		ArrayList<ReachabilityPoint> ordering = OpticsOrderingReader.readFile(DataDirectory + File.separatorChar+opticsFilename);
		//reconfigure
		for (ReachabilityPoint reachabilityPoint : ordering) {
			if(reachabilityPoint.reachability <0)
				reachabilityPoint.reachability = Double.POSITIVE_INFINITY;
		}
		
		
		
		// steepness sensitivity: 0 (sensitive) -> 1 (insensitve)
		double end_xi = 0.1; 			//higher
		double starting_xi = 0;			//low
		double reset_xi = 0.1;			//lower
		
		int minPts = 3;				//minimum size of area
		int nonConseqLimit = 3;		//keep it low
		
		
		ArrayList<SteepArea> areas =  findSteepAreas(ordering, starting_xi, reset_xi, end_xi, minPts,nonConseqLimit);
		System.out.println("Areas Found: " +areas.size());
		
		System.out.println("------------------------------");
		System.out.println("EXTRACTING CLUSTERS");
		System.out.println("------------------------------");
		
		
		ArrayList<Cluster> clusters = extractClusters(ordering, areas,starting_xi,minPts);
		System.out.println("Clusters Found: " +clusters.size());
		
		for (Cluster steepArea : clusters) {
			System.out.println("Cluster:" + steepArea.startIndex +"  " + steepArea.endIndex + "\tSize:" + (steepArea.endIndex - steepArea.startIndex +1) );
		}
		
		
		for (ReachabilityPoint reachabilityPoint : ordering) {
			if(reachabilityPoint.reachability == Double.POSITIVE_INFINITY)
				reachabilityPoint.reachability = -1000;
		}
		
//		System.out.println("Areas " + areas.size());
		OpticsPlot.plotGraphAreas("Areas", ordering, areas);
		OpticsPlot.plotGraphClusters("Clusters", ordering, clusters);
		OpticsPlot.plotGraph("Attacks", ordering, OpticsPlot.BY_ATTACK_CATEGORY);
//		OpticsPlot.plotGraph("Test Vs Train", ordering, OpticsPlot.BY_TRAIN_VS_TEST);
		
	}
	
	/**
	 * Extract clusters 
	 * @param points Ordering of points
	 * @param areas Steep areas
	 * @return Clusters formed
	 */
	public ArrayList<Cluster> extractClusters(ArrayList<ReachabilityPoint> points, ArrayList<SteepArea> areas, double xi, int minpts){
		ArrayList<SteepArea> SetOfSteepDownAreas = new ArrayList<SteepArea>();
		
		ArrayList<Cluster> SetOfClusters = new ArrayList<Cluster>();
		
		
		
		int index = 0;
		double mib = 0;
		
		int areaCounter = 0;
		SteepArea currentArea = areas.get(areaCounter);
		ReachabilityPoint currenpoint;
		
		//if start of steepdown at index
		while(index < points.size()){
			currenpoint = points.get(index);
			mib = Math.max(mib, currenpoint.reachability);
			
			System.out.print("i:"+index);
			
			//update currentarea
			if(index > currentArea.endIndex){
				areaCounter++;
				if(areaCounter < areas.size()) currentArea = areas.get(areaCounter);
				System.out.print("\tupdate");
			}else{
				System.out.print("\tstay");
			}
			
			String direction = (currentArea.isSteepUp? "UP" : "DOWN");
			
			System.out.print("\tArea : "+currentArea.startIndex+"\t" + currentArea.endIndex + "\t" + direction  );
			
			if(currentArea.startIndex == index && currentArea.isSteepUp == false){		//if index is start of SDA
				System.out.print("\tAt SDA Start");
				
				
				//update mib values
				updateMibValuesOfSDAs(SetOfSteepDownAreas, currenpoint.reachability,index);				
				//filter SDAs -- remove SDA if SDAmib > SDAstart x (1-xi)
				filterSDAs(points,SetOfSteepDownAreas,  xi);
				
				currentArea.mib = 0;
				SetOfSteepDownAreas.add(currentArea);			
				index = currentArea.endIndex +1;
				
				
			}else if(currentArea.startIndex == index && currentArea.isSteepUp == true){	//if index is start of SUA
				System.out.print("\tAt SUA Start");
				
				//update mib values
				updateMibValuesOfSDAs(SetOfSteepDownAreas, currenpoint.reachability,index);				
				//filter SDAs -- remove SDA if SDAmib > SDAstart x (1-xi)
				filterSDAs(points,SetOfSteepDownAreas,  xi);
				
				
				
				index = currentArea.endIndex +1;
				
				//for all SDAs, check if combination with current area satisfies cluster conditions
				for (SteepArea steepDownArea : SetOfSteepDownAreas) {
					
					//check if SDAmib > SUAend x (1-xi)
					double SUAEndReach = points.get(currentArea.endIndex + 1).reachability; // or index + 1??
					if(steepDownArea.mib > SUAEndReach * (1-xi) && SUAEndReach > 0){	
						System.out.println("SteepUpArea not compatible.");
						continue;						
					}
					
					Cluster candidate = findStartandEndPoints(points, steepDownArea, currentArea, xi, minpts);
					if(candidate != null){
						SetOfClusters.add(candidate);
					}
					System.out.println("\n\tChecking SDAs  :" + steepDownArea.startIndex +"\t"+steepDownArea.endIndex + "\tStatus:" + (candidate!= null) );
				}			
				
				
				
			}else{
				index++;
			}
			
			System.out.println();
		}
		
		
		return SetOfClusters;
	}
	
	
	// TODO fix for SDA, SUA
	/**
	 * Removes a steep down area if the mib value is greater than SD(Reach start) x 1-xi
	 * @param SetOfSteepDownAreas
	 * @param reachability
	 * @param xi
	 */
	private void filterSDAs(ArrayList<ReachabilityPoint> points, ArrayList<SteepArea> SetOfSteepDownAreas, double xi){
		//consider negative starts of SDAs
		int size = SetOfSteepDownAreas.size();
		for (int i = 0; i< size ;i++) {
			SteepArea steepArea = SetOfSteepDownAreas.get(i);
			ReachabilityPoint SDAStart = points.get(steepArea.startIndex);
			
			if(steepArea.mib > SDAStart.reachability * (1-xi) && SDAStart.reachability > 0){
				System.out.print("\n\t Removed SDA: "+ steepArea.startIndex+"\t"+ steepArea.endIndex +"\tSDA mib:"+ steepArea.mib +"\t VS "+SDAStart.reachability +"\t at mibindex:"+ steepArea.mibIndex + " mult "+ (1-xi));
				SetOfSteepDownAreas.remove(i);
				size--;
			}
		}
		
	}
		
	
	
	/**
	 * Updates the mib value if it is greater than the current mib value
	 * @param areas
	 * @param update
	 */
	private void updateMibValuesOfSDAs( ArrayList<SteepArea> areas, double reachability, int index){
		for (SteepArea steepArea : areas) {
			if(reachability < 0 ){
				reachability = Double.POSITIVE_INFINITY;			//update to positive infinity if negative
			}	
			
			steepArea.updateMIB(reachability,index);
		}
	}
	
	
	private Cluster findStartandEndPoints(ArrayList<ReachabilityPoint> points, SteepArea sda,SteepArea sua,double xi, int minpts){
		
		//mib of SDA must be less than end of SUA x (1-xi) ------------- or SUA+1 x (1-xi)
		double suaEnd = points.get(sua.endIndex).reachability;
		if(sda.mib > suaEnd*(1-xi))
			return null;
		
		//find start and end areas
		double reachStart =  points.get(sda.startIndex).reachability ; 	//fix for negative values
		double reachEnd;
		if(sua.endIndex+1 >= points.size()){
			reachEnd = points.get(sua.endIndex).reachability;			//what to do when there is no SUA +1?
			reachEnd = Double.MAX_VALUE;
		}else
			reachEnd =  ((points.get(sua.endIndex + 1).reachability < 0)? Double.MAX_VALUE  : points.get(sua.endIndex +1).reachability) ; 	//fix for negative values
		
		
		
		int startIndex = -1 ,endIndex = -1;
		
		
		if(reachStart * (1-xi) >= reachEnd ){			//case B	
			endIndex = sua.endIndex;
			
			//find the MAX val of r(x) , x is in SDA  st. r(x) <= reachEnd --search in SDA 
			double maxval = Double.MIN_VALUE;
			
			for (int i = sda.startIndex; i <= sda.endIndex; i++) {
				if(points.get(i).reachability > maxval && points.get(i).reachability <= reachEnd){
					startIndex = i;
					maxval = points.get(i).reachability;
				}
			}
			
			
		
		}else if(reachEnd * (1-xi) >= reachStart ){		//case C
			startIndex = sda.startIndex;
			
			//find the MIN val of r(x) , x is in SUA  st. r(x) <= reachEnd reachStart --search in SUA
			double minval = Double.MAX_VALUE;
			
			for (int i = sua.startIndex; i <= sua.endIndex; i++) {
				if(points.get(i).reachability < minval && points.get(i).reachability >= reachStart){
					endIndex = i;
					minval = points.get(i).reachability;
				}
			}	
			
		}else{											//case A
			startIndex = sda.startIndex;
			endIndex = sua.endIndex;
		}
		
		//SPECIAL CASES----------- START BOUNDARIES / END BOUNDARIES WHEN NEGATIVE, ZEROES

		if(reachStart < 0 && reachEnd > 0){
			startIndex = sda.startIndex;
			endIndex = sua.endIndex;
			// ends with all SUAs? -except with inf(-1) mib value
		}
		
		if(reachEnd < 0 && reachStart > 0){
			endIndex = sua.endIndex;
			startIndex = sda.startIndex;
		}
		
		
		
		
		
		
		//check boundaries
		if(startIndex <0) {
			System.out.println("No start boundaries found");
			return null;
		}
		
		if(endIndex < 0){
			System.out.println("No end boundaries found");
			return null;
		}
		
		//check cluster size
		if(endIndex - startIndex+1 < minpts){
			System.out.println("Cluster not big enough. Size: " + (endIndex - startIndex+1 )  + "\tMinpts:"+minpts);
			return null;
		}
		
		return new Cluster(startIndex, endIndex);
	}
	
	
	
	/**
	 * Searches Steepdown and steep up areas
	 * @param points
	 * @param start_xi
	 * @param reset_xi
	 * @param end_xi
	 * @param minPts
	 * @param nonConseqLimit
	 * @return
	 */
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
		//System.out.print("\tending "+currentArea.startIndex+" - " + currentArea.endIndex);
		if(currentArea.size() >= minPts){
			areas.add(currentArea);
			
		}else if(points.get(currentArea.startIndex).reachability <0 ){	//if starting from negative
			areas.add(currentArea);
		}else if(points.get(currentArea.startIndex).reachability == 0 && currentArea.isSteepUp ){	//if starting from zero and steep up
			areas.add(currentArea);
		}
	}
			
	private boolean isSteepUpPoint(double reachability, double reachabilityNext, double xi){
		if(reachabilityNext <0)	// next is positive inf
			return (reachability >= 0 );
		if(reachability < 0)
			return false;
//		if(reachability == 0 && reachabilityNext >0)
//			return true;
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
