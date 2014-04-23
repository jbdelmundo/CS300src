package clustering;


import java.io.File;
import java.util.ArrayList;

import data.MinHeap;
import data.ReachabilityPoint;
import data.SteepArea;
import fileIO.OpticsOrderingReader;

public class OpticsEvaluation {
	
	ReachabilityPoint currentpoint;					//for iterations
	ReachabilityPoint nextPoint = null;				//for iterations
	MinHeap SteepAreaList;
	
	
	int nonConseqLimit = 50000;	//estimate lang
	//double xi= 0.02;			//difference threshold
	
	public static void main(String[] args) {
		OpticsEvaluation opticsEval = new OpticsEvaluation();
		opticsEval.evaluate(32, 67);
		System.out.println("Done");
	}
	
	public void evaluate(int trainItem, int testItem){
		
		String DataDirectory = "RandomPieces_200";
		String opticsFilename = "ids200_" + trainItem + "-" + testItem + ".optics";
		
		ArrayList<ReachabilityPoint> ordering = OpticsOrderingReader.readFile(DataDirectory + File.separatorChar+opticsFilename);
		
		SteepAreaList = new MinHeap();	
		findSteepAreas(ordering, 0.2);		//assigns value to SteepAreaList
		
		//write to file 
		
		ArrayList<SteepArea> areas = getAreas();		//areas including flat areas
		assignToCluster(ordering,areas);
		
		//write to file 
		
		
		//evaluate accuracy
		

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
	public void findSteepAreas(ArrayList<ReachabilityPoint> buffer, double xi) {

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

			// if(i == 30000) break;

		}

	}

	/* Returns a list of steep down, flat and steep up areas*/
	private ArrayList<SteepArea>  getAreas(){
			
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
				
				areas.add(flat);
				areas.add(steeparea);
			}
			i = end;
			
		}while(SteepAreaList.size() !=0);
		
		
		return areas;
			
	}
	
	public void assignToCluster(ArrayList<ReachabilityPoint> buffer, ArrayList<SteepArea> areas){

		 
	
		
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
		
		
		
		
	}
}
