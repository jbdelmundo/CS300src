package clustering;


import java.io.File;
import java.util.ArrayList;

import javax.xml.ws.Endpoint;

import data.MinHeap;
import data.ReachabilityPoint;
import data.SteepArea;
import fileIO.OpticsOrderingReader;
import graph.OpticsPlot;

public class OpticsEvaluation3 {
	
	ReachabilityPoint currentpoint;					//for iterations
	ReachabilityPoint nextPoint = null;				//for iterations

	
	
	int nonConseqLimit = 10;	//estimate lang
	//double xi= 0.02;			//difference threshold
	
	public static void main(String[] args) {
		OpticsEvaluation3 opticsEval = new OpticsEvaluation3();
//		opticsEval.evaluate(32, 67);
		opticsEval.evaluate(1, 2);
//		opticsEval.evaluate(0, 1);
		System.out.println("Done");
	}
	
	public void examineArea(ArrayList<SteepArea> areas){
		for (SteepArea steepArea : areas) {
			if(steepArea.isFlat){
				System.out.println(steepArea.startIndex + " - " + steepArea.endIndex + "\tFlat" );
			}else if(steepArea.isSteepUp){
				System.out.println(steepArea.startIndex + " - " + steepArea.endIndex + "\tUP" );
			}else{
				System.out.println(steepArea.startIndex + " - " + steepArea.endIndex + "\tDown" );
			}
		}		
	}
	
	public void evaluate(int trainItem, int testItem){
		
		String DataDirectory = "RandomPieces_10000";
		String opticsFilename = "test.optics";
		
		ArrayList<ReachabilityPoint> ordering = OpticsOrderingReader.readFile(DataDirectory + File.separatorChar+opticsFilename);
		
		int minpts = 200;
		double xi = 0.2;
		ArrayList<SteepArea> areas1 = findSteepAreas2(ordering, minpts ,xi);
		examineArea(areas1);
		ArrayList<SteepArea> clusters = extractClusters(areas1, ordering, minpts, xi);
		
		
		
		
		OpticsPlot.plotGraphAreas("Clusters", ordering, areas1);
		
		for (int i = 0; i < areas1.size(); i++) {
			System.out.println("Area "+i+ "\tS:" + areas1.get(i).startIndex + "\tE:"+ areas1.get(i).endIndex + "\tsize:  "+( areas1.get(i).endIndex- areas1.get(i).startIndex+1));
		}
		
		for (int i = 0; i < clusters.size(); i++) {
			System.out.println(i+"\t" + clusters.get(i).startIndex + "\t" + + clusters.get(i).endIndex);
		}
		
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
		
		
		OpticsPlot.plotGraphAreas("Eval", ordering, areas);
		
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
	
	
	
	
	public ArrayList<SteepArea> extractClusters(ArrayList<SteepArea> areas,ArrayList<ReachabilityPoint> points,int minpts, double xi){
		
		int size = points.size();
		int currentAreaIndex = 0;
		SteepArea currentArea = areas.get(currentAreaIndex);
		
		ArrayList<SteepArea> setOfSteepDownAreas = new ArrayList<>();
		ArrayList<SteepArea> setOfClusters = new ArrayList<>();
		
		ReachabilityPoint index;
		double mib = 0;
		for (int i = 0; i < size; ) {
			
			if(i > currentArea.endIndex && currentAreaIndex < areas.size()-1 ){						
				currentAreaIndex++;
				currentArea = areas.get(currentAreaIndex);
			}
			
			index = points.get(i);			
			
			mib = Math.max(mib, index.reachability);
			
			if(i == currentArea.startIndex && !currentArea.isSteepUp){				//if index is a start of a steep down
				
				//update mib values and filter
				for (int j = 0; j< setOfSteepDownAreas.size(); j++) {
					SteepArea D = setOfSteepDownAreas.get(j);
					
					D.mib = Math.max(D.mib, index.reachability);
					
					ReachabilityPoint start = points.get(D.startIndex);
					if(start.reachability *(1-xi) < mib ){
						setOfSteepDownAreas.remove(j);
						j--;
						
					}	
				}
				
				
				currentArea.mib = 0;
				setOfSteepDownAreas.add(currentArea);
				
				i = currentArea.endIndex + 1;
				index = points.get(i);
				mib = index.reachability;
				
			}else if(i == currentArea.startIndex && currentArea.isSteepUp){			//if index is a start of a steep up
				
				//update and filter
				for (int j = 0; j< setOfSteepDownAreas.size(); j++) {
					SteepArea D = setOfSteepDownAreas.get(j);
					
					D.mib = Math.max(D.mib, index.reachability);
					
					ReachabilityPoint start = points.get(D.startIndex);
					if(start.reachability *(1-xi) < mib ){
						setOfSteepDownAreas.remove(j);
						j--;
					}	
				}
				
				i = currentArea.endIndex + 1;
				index = points.get(i);
				mib = index.reachability;
				
				for (SteepArea D : setOfSteepDownAreas) {
					
					//check combination of D and currentSteepArea 						
					
					ReachabilityPoint end;
					if(currentArea.endIndex != points.size()){
						end = points.get(currentArea.endIndex+1);
						if(D.mib * (1-xi) >= end.reachability){
							continue;				//fail at condition 3b
						}
					
					
						
						//Startpoint is in steepdownarea, end point is in steepuparea, interval >=minpts, end
						
						int clusterstart = -1,clusterend = -1;
							
						ReachabilityPoint start = points.get(D.startIndex);
						
						if(start.reachability *  (1-xi) >= end.reachability || (start.reachability < 0 && end.reachability >=0 )){
							clusterend = currentArea.endIndex;
							
							for (int j = D.startIndex; j < D.endIndex; j++) {
								double startReachability = points.get(j).reachability;
								if(startReachability*(1-xi) < end.reachability){
									clusterstart = j;
									break;
								}
							}
							
						}else if(end.reachability * (1-xi) >= start.reachability || (end.reachability <0 && start.reachability >=0)){
							
							clusterstart = D.startIndex;
							
							for (int j = currentArea.endIndex; j >= currentArea.startIndex; j--) {
								double endReachability = points.get(j).reachability;
								if(endReachability*(1-xi) < start.reachability){
									clusterend = j;
									break;
								}
								
							}
							
						}else{
							clusterstart = D.startIndex;
							clusterend = currentArea.endIndex;
						}
					
					
					
						//check clustersize
						int clustersize =  currentArea.endIndex - D.startIndex + 1;
						if(clustersize < minpts)
							continue;
						
						SteepArea cluster = new SteepArea();
						cluster.startIndex = clusterstart;
						cluster.endIndex = clusterend;
						
						setOfClusters.add(cluster);
						
					}else{
						//if steepup area ends at the last point, dont compare anymore
						
						//check clustersize
						int clustersize =  currentArea.endIndex - D.startIndex ;
						if(clustersize < minpts)
							continue;
						
						SteepArea cluster = new SteepArea();
						cluster.startIndex = D.startIndex;
						cluster.endIndex = currentArea.endIndex;
						
						setOfClusters.add(cluster);
					}
					
					
					
					
					
					
					
				}//end of loop for steep down areas
				
			}else{
				i++;
			}
			
		}//end loop for all points
		
		return setOfClusters;
	}
	
	
	public ArrayList<SteepArea> findSteepAreas2(ArrayList<ReachabilityPoint> buffer, int minpts, double xi){
		
		int size = buffer.size();
		
		
		boolean isSteepUpPoint[] = new boolean[size];
		boolean isSteepDownPoint[] = new boolean[size];
		boolean isAsHigh[] = new boolean[size];
		boolean isAsLow[] = new boolean[size];
		
		isAsHigh[0] = false;
		isAsLow[0] = true;
		
		
		minpts = 6;
		int start = -1; 
		int end = -1;
		int nonConseq = 0;
		boolean isSteepUp = false;
		boolean isInSteepArea = false;
		
		ArrayList<SteepArea> steepAreas = new ArrayList<SteepArea>();
		SteepArea lastArea = null;
		int max = 0,smax = 0;
		
		for (int i = 0; i < size; i++) {
			
			currentpoint = buffer.get(i);
			nextPoint = (i == buffer.size() - 1) ? currentpoint : buffer.get(i + 1);
			
			
			
			isSteepUpPoint[i] = isSteepUpPoint(currentpoint.reachability, nextPoint.reachability, xi);		
			isSteepDownPoint[i] = isSteepDownPoint(currentpoint.reachability, nextPoint.reachability, xi);
			
			if(i < size-1){
				isAsHigh[i+1] = isAsHigh(currentpoint.reachability, nextPoint.reachability);
				isAsLow[i+1] = isAsLow(currentpoint.reachability, nextPoint.reachability);
			}	
			
			
			
			
			if(isInSteepArea){				
				
				if(isSteepUp){
					if(isSteepDownPoint[i]){
						isInSteepArea=false;
					}else if(isSteepUpPoint[i] && isAsHigh[i]){
						end = i;								//update end
					}else if(isAsHigh[i]){						//if not steep point but as high
						nonConseq++;				
						if(nonConseq > minpts){
							isInSteepArea=false;	//stop 
						}
					}else if( !isAsHigh[i] ){						
						isInSteepArea=false;
					}
				
					
					
					
				}else{
					if(isSteepUpPoint[i]){
						isInSteepArea=false;
					}else if(isSteepDownPoint[i] && isAsLow[i]){
						end = i;								//update end
					}else if(isAsLow[i]){						//if not steep point but as low
						nonConseq++;				
						if(nonConseq > minpts){
							isInSteepArea=false;	//stop 
						}
					}else if( !isAsLow[i]){
						isInSteepArea=false;
					}
				}
				
				if(isInSteepArea == false){					//if terminated
					if(lastArea == null || lastArea.endIndex != start){
						lastArea = new SteepArea();
						lastArea.startIndex = start;
						lastArea.endIndex = end;
						lastArea.isSteepUp = isSteepUp;
						steepAreas.add(lastArea);	
						if(max < end-start+1){
							max = end-start+1;
							smax = start;
						}
					}
				}

			}
			
			
			if(!isInSteepArea){
				if(isSteepUpPoint[i]){
					isSteepUp = true;
					isInSteepArea = true;
					start = i;
					end = i;
				}else if(isSteepDownPoint[i]){
					isSteepUp = false;
					isInSteepArea = true;
					start = i;
					end = i;
					
				}
				nonConseq=0;
			
			
			}
			
			boolean showCalculations = false;	//show how steepareas computed
			if(showCalculations){
				System.out.print(i);				
				System.out.print("\t" + isSteepUpPoint[i]+ "\t"  + isSteepDownPoint[i]+"\t"+isAsHigh[i]  +"\t"+isAsLow[i]);					
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
		
		System.out.println("Max " + max+ " smax"  + smax);
		return steepAreas;
	
		
		
		
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
