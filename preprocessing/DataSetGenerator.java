package preprocessing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import java.util.Random;
import java.util.Scanner;
import java.util.Vector;

import fileIO.DirectoryFactory;

public class DataSetGenerator {
/**
 * Set 1: Divide into N pieces - Random
 * Set 2: Divide into N pieces - Sequential
 * Set 3: Discovery:
 * 			Remove an activity, Save as training( Max size of N%) - still sequential
 * 			Those removed, Save as test ( Max size of N%) - Still Sequential
 * 
 * input: k
 * 
 */
	
	
	Vector<String> buffer;
	
	public DataSetGenerator(File infile) {
		
		
		buffer = new Vector<String>();
		try {
			Scanner scan = new Scanner(infile);
			
			for(int i = 0; scan.hasNext(); i++){
				buffer.add( scan.nextLine() );
				if(i%100000 == 0) System.out.println( (i*100/4898431) + "\tLine " + i + "/" + 4898431);
			}
			System.out.println("Buffering done");
			scan.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	
	public void generateNRandomPieces(int pieces, String outFileName){
		int n = buffer.size()/pieces;
		
		int ordering[] = new int[buffer.size()];
		
		for (int j = 0; j < ordering.length; j++) {
			ordering[j] = j;
		}
		Random r = new Random();		
		for (int j = 0; j < ordering.length; j++) {
			int swap = r.nextInt(ordering.length);
			int val = ordering[j];
			ordering[j] = ordering[swap];
			ordering[swap] = val;
		}
		
		
		
		
		
		int piece = 0;
		int i = 0;
		
		try {
			
			PrintWriter pw = null;
			
			while(i < buffer.size()){
				
				
				
				int c = 0;
				pw  = new PrintWriter(outFileName + "_" +piece + ".data");
				for (int count = 0; count < n && i < buffer.size(); count++) {
					
					
					pw.println(buffer.elementAt(ordering[i]));
					c++;
					i++;
				}
				System.out.println("\tPiece: "+piece+ "\tCount:"+c + "  " + i);
				pw.close();
				
				piece++;
			
			}
			
			
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	public void generateNSequentialPieces(int pieces, String outFileName){
		
		int n = buffer.size()/pieces;
		int i = 0;
		int piece = 0;
		
		
		try {
			
			PrintWriter pw = null;
			
			while(i < buffer.size()){
				
				
				int c = 0;
				pw  = new PrintWriter(outFileName + "_" +piece + ".data");
				for (int count = 0; count < n && i < buffer.size(); count++) {
					
					
					pw.println(buffer.elementAt(i));
					c++;
					i++;
				}
				System.out.println("\tPiece: "+piece+ "\tCount:"+c + "  " + i);
				pw.close();
				
				piece++;
			
			}
			
			
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		
	}
	

	public static void main(String[] args) {
		DataSetGenerator dsg = new DataSetGenerator(new File("data"+File.separatorChar+"ids.data"));
//		String directory = "SequentialPieces_200";
//		dsg.createDirectory(directory);
//		dsg.generateNSequentialPieces(200, directory +File.separatorChar+ "ids200");
//		
//		System.out.println("Sequential Done");
		
		String directory2 = "RandomPieces_10";
		DirectoryFactory.createDirectory(directory2);
		dsg.generateNRandomPieces(10, directory2 +File.separatorChar+ "ids10");
		
		System.out.println("Random Done");
		
		directory2 = "SequentialPieces_10";
		DirectoryFactory.createDirectory(directory2);
		dsg.generateNSequentialPieces(10, directory2 +File.separatorChar+ "ids10");
		
		System.out.println("Sequential Done");
	}
}
