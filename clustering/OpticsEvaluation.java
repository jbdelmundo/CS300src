package clustering;


import java.io.File;
import java.util.ArrayList;

import data.MinHeap;
import data.ReachabilityPoint;
import data.SteepArea;
import fileIO.OpticsOrderingReader;
import graph.OpticsPlot;

public class OpticsEvaluation {
	
	ReachabilityPoint currentpoint;					//for iterations
	ReachabilityPoint nextPoint = null;				//for iterations

	
	
	int nonConseqLimit = 10;	//estimate lang
	//double xi= 0.02;			//difference threshold
	
	public static void main(String[] args) {
		OpticsEvaluation opticsEval = new OpticsEvaluation();
		opticsEval.evaluate(32, 67);
//		opticsEval.evaluate(34, 125);
//		opticsEval.evaluate(0, 1);
		System.out.println("Done");
	}
	
	public void evaluate(int trainItem, int testItem){
		
		String DataDirectory = "RandomPieces_200";
		String opticsFilename = "ids200_" + trainItem + "-" + testItem + ".optics";
		
		ArrayList<ReachabilityPoint> ordering = OpticsOrderingReader.readFile(DataDirectory + File.separatorChar+opticsFilename);
		
		
		findSteepAreas2(ordering, 0.2);
		
		if(true)  return;
		
		MinHeap SteepAreaList = findSteepAreas(ordering, 0.1);		//assigns value to SteepAreaList
		
		
		
		//write to file 		
		ArrayList<SteepArea> areas = getAreas(SteepAreaList);		//areas including flat areas
		
		/**PRINT AREAS**/
		for (int i = 0; i < areas.size(); i++) {
			SteepArea steeparea = areas.get(i);
			
			System.out.print("SteepArea: \t"+steeparea.startIndex+" \t" + steeparea.endIndex + "\t");
			if(steeparea.isFlat){
				System.out.println("FLAT");
			}else if(steeparea.isSteepUp && !steeparea.isFlat){
				System.out.println("UP");
			}else if(!steeparea.isSteepUp && !steeparea.isFlat){
				System.out.println("DOWN");
			}
		}
		
		
		OpticsPlot.plotGraph("Eval", ordering, OpticsPlot.BY_TRAIN_VS_TEST, areas);
		
//		ordering = assignToCluster(ordering,areas);				//fill values of assignedLabel Field
//		
//		
//		getAccuracy(ordering);
//		
//		System.out.println("Accuracy");
//		
		//write to file 
		
		
		//evaluate accuracy
		

	}
	
	public void findSteepAreas2(ArrayList<ReachabilityPoint> buffer, double xi){
		
		int size = buffer.size();
		
		
		boolean isSteepUpPoint[] = new boolean[size];
		boolean isSteepDownPoint[] = new boolean[size];
		boolean isAsHigh[] = new boolean[size];
		boolean isAsLow[] = new boolean[size];
		
		isAsHigh[0] = false;
		isAsLow[0] = true;
		
		
		int mintpts = 6;
		int start = -1; 
		int end = -1;
		int nonConseq = 0;
		boolean isSteepUp = false;
		boolean isInSteepArea = false;
		
		for (int i = 0; i < size; i++) {
			
			currentpoint = buffer.get(i);
			nextPoint = (i == buffer.size() - 1) ? currentpoint : buffer.get(i + 1);
			
			
			
			isSteepUpPoint[i] = isSteepUpPoint(currentpoint.reachability, nextPoint.reachability, xi);		
			isSteepDownPoint[i] = isSteepDownPoint(currentpoint.reachability, nextPoint.reachability, xi);
			
			if(i < size-1){
				isAsHigh[i+1] = isAsHigh(currentpoint.reachability, nextPoint.reachability);
				isAsLow[i+1] = isAsLow(currentpoint.reachability, nextPoint.reachability);
			}	
			
			
			
			
			
			
			
			if(!isInSteepArea){
				if(isSteepUpPoint[i]){
					isSteepUp = true;
					isInSteepArea = true;
					start = i;
				}else if(isSteepDownPoint[i]){
					isSteepUp = false;
					isInSteepArea = true;
					start = i;
					
				}
				nonConseq=0;
			
			
			}else{
				
				//update end if steep point
				if(isSteepUp && isSteepUpPoint[i] && isAsHigh[i]){					
					end = i;
				}else if(!isSteepUp && isSteepDownPoint[i] && isAsLow[i] ){					
					end = i;
					
					
					
				}else if(isSteepUp && isAsHigh[i]){					
					nonConseq++;				//increment limit
					if(nonConseq > mintpts){
						isInSteepArea=false;	//stop 
					}
				}else if(!isSteepUp && isAsLow[i]){					
					nonConseq++;				//increment limit
					if(nonConseq > mintpts){
						isInSteepArea=false;	//stop 
					}
				}else if (isSteepUp && !isAsHigh[i]){
					isInSteepArea=false;
				}else if (!isSteepUp && !isAsLow[i]){
					isInSteepArea=false;
				}
				
				
				
			}
			
			
			System.out.print(i);
			
			System.out.print("\t" + isSteepUpPoint[i]+ "\t"  + isSteepDownPoint[i]+"\t"+isAsHigh[i]  +"\t"+isAsLow[i]);
//			System.out.print("\t|" + (isSteepUpPoint[i]^ isSteepDownPoint[i])+"\t"+(isAsHigh[i] ^ isAsLow[i]));
			
			
			System.out.print("\t|"+isInSteepArea );
			if (isInSteepArea) {
				System.out.print("\t"+(isSteepUp? "UP":"DWN"));
				System.out.print("\t"+start+"|\t|"+end);
			}else{
				System.out.print("\t---\t\t");
			}
			System.out.print("\t"+nonConseq);
			
			
			System.out.println("\t" + currentpoint.reachability+"\t" + nextPoint.reachability);
			
			
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
			return true;
		if(reachability <0)
			return false;
		return reachability <= reachabilityNext;
	}

	private boolean isAsLow(double reachability, double reachabilityNext){
		if(reachability <0)
			return true;
		if(reachabilityNext<0)
			return false;
		return reachability >= reachabilityNext;
	}
	
	
	/**
	 * identifies steep up and steep down areas and places them in SteepAreaList
	 * 
	 * if nega
	 * 		Start steepUP area
	 * else if  --- next reachability is "xi" percent higher
	 * 		start steepUP
	 * 
	 * 
	 * if next is lower
	 * 		end steepUP
	 * 
	 * 
	 * @param buffer
	 * @param xi
	 */
	public MinHeap findSteepAreas(ArrayList<ReachabilityPoint> buffer, double xi) {

		MinHeap SteepAreaList = new MinHeap();	
		SteepArea currentSteepArea = null; // current steepArea, valid if
											// boolean var is true;
		int steepNonConseq = 0;
		boolean isInSteepArea = false;

		// Scan for SteepAreas
		for (int i = 0; i < buffer.size(); i++) {

			currentpoint = buffer.get(i);
			nextPoint = (i == buffer.size() - 1) ? currentpoint : buffer
					.get(i + 1);

			double currentReachabilty = currentpoint.reachability;
			double nextReachability = nextPoint.reachability;
			
			
			
			// No SteepArea
			if (!isInSteepArea) {

				if (currentReachabilty < 0) { // start a new steepdown if nega
					isInSteepArea = true;
					currentSteepArea = new SteepArea();
					currentSteepArea.isSteepUp = false;
					currentSteepArea.startIndex = i;
					currentSteepArea.endIndex = i;
					steepNonConseq = 0;
					// System.out.println("Steepdown start at "+i
					// +" from negative reachabilty");

					// if steepupward, start a steep upward
				} else if (Math.abs(currentReachabilty - nextReachability) > currentReachabilty
						* xi
						&& currentReachabilty < nextReachability) {
					isInSteepArea = true;
					;
					currentSteepArea = new SteepArea();
					currentSteepArea.isSteepUp = true;
					currentSteepArea.startIndex = i;
					currentSteepArea.endIndex = i;
					steepNonConseq = 0;

					// System.out.println("Steep UP start at "+i);
				}

			}

			// Inside SteepUp
			else if (isInSteepArea && currentSteepArea.isSteepUp) {

				double endReachability = buffer.get(currentSteepArea.endIndex).reachability;

				if (currentReachabilty >= endReachability) {
					steepNonConseq = 0; // reset
					currentSteepArea.endIndex = i; // update EndIndex
				} else if (currentReachabilty >= 0) {
					steepNonConseq++; // count

					if (steepNonConseq >= nonConseqLimit) { // end exceed the
															// limit
						isInSteepArea = false;
						i = currentSteepArea.endIndex;
						// System.out.println("Limit full at "+
						// i+" Ending steepUP");
					}

				} else {
					// end steepup if negative at its own end index and return
					// to endIndex+1
					isInSteepArea = false;
					i = currentSteepArea.endIndex;
					// System.out.println("NEGATIVE at "+ i+" Ending steepUP " +
					// currentReachabilty+ "   " + nextReachability);
				}

				if (!isInSteepArea) { // if currentsteeparea ends, add to list
					SteepAreaList.push(currentSteepArea);
				}

			}

			// Inside SteepDown
			else if (isInSteepArea && !currentSteepArea.isSteepUp) {

				double endReachability = buffer.get(currentSteepArea.endIndex).reachability;

				if (endReachability < 0 && currentReachabilty >= 0) { // negative
																		// start
					steepNonConseq = 0; // reset
					currentSteepArea.endIndex = i;

				} else if (currentReachabilty < endReachability) {
					steepNonConseq = 0; // reset
					currentSteepArea.endIndex = i; // update EndIndex

				} else if (currentReachabilty >= 0) {
					steepNonConseq++; // count

					if (steepNonConseq >= nonConseqLimit) { // end exceed the
															// limit
						isInSteepArea = false;
						i = currentSteepArea.endIndex;
						// System.out.println("Limit full at "+
						// i+" Ending steepdown "+ steepNonConseq);
					}

				} else {
					// end steepdown if negative at its own end index and return
					// to endIndex+1
					isInSteepArea = false;
					i = currentSteepArea.endIndex;
					// System.out.println("NEGATIVE at "+
					// i+" Ending steepdown");
				}

				if (!isInSteepArea) { // if currentsteeparea ends, add to list
					SteepAreaList.push(currentSteepArea);
				}

			}
			
			System.out.println(i+"\t"+currentReachabilty+"\t"+ nextReachability + "\t" + isInSteepArea+ " "+((currentSteepArea.isSteepUp)? "up":"down"));

			// if(i == 30000) break;

		}
		return SteepAreaList;
	}

	/* Returns a list of steep down, flat and steep up areas*/
	private ArrayList<SteepArea>  getAreas(MinHeap SteepAreaList){
			
		//generate 'clusters' for every area
		ArrayList<SteepArea> areas = new ArrayList<SteepArea>();
		
		SteepArea steeparea = null;
		int i = -1;
		do{
			steeparea = (SteepArea) SteepAreaList.pop();
			int start = steeparea.startIndex, end = steeparea.endIndex;
			
			
			if(i+1 == start){
				areas.add(steeparea);
			}else{
				SteepArea flat = new SteepArea();
				flat.startIndex = i+1;
				flat.endIndex = start -1;
				flat.isFlat = true;
				areas.add(flat);
				areas.add(steeparea);
			}
			i = end;
			
			
			
			
		}while(SteepAreaList.size() !=0);
		
		
		return areas;
			
	}
	
	public ArrayList<ReachabilityPoint> assignToCluster(ArrayList<ReachabilityPoint> buffer, ArrayList<SteepArea> areas){

		 
		for (SteepArea area : areas) {
			int start = area.startIndex;
			int end = area.endIndex;			
			
			int assignedLabel = -1; //undef
			int traceback = start;
			
			
			//area traverse
			for (int i = start; i <=end ; i++) {
				ReachabilityPoint rp = buffer.get(i);
				
				if(rp.hasLabel){					//if labeled
					
					if(assignedLabel == -1){		//if will change the assignment, follow up from the traceback
						for(int j = traceback; j <= i; j++){
							ReachabilityPoint rp2 = buffer.get(j);
							rp2.assignedlabel = rp.label;
						}
						
					}
					assignedLabel = rp.label;
					rp.assignedlabel = assignedLabel;
					
				}else{								//if unlabeled
					if(assignedLabel == -1){		//mark if unknown label to place
						traceback = i;
					}
					
					rp.assignedlabel = assignedLabel;
				}
				
				
			}//end of area traverse
			
		}
		
		return buffer;
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
