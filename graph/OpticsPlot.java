package graph;

import java.awt.Dimension;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYBarDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RefineryUtilities;

import data.DataPacket;
import data.ReachabilityPoint;
import data.SteepArea;
import fileIO.OpticsOrderingReader;



public class OpticsPlot extends JFrame{
	
	XYSeriesCollection dataset;
	XYSeries normal;
	XYSeries attack;
	
	public static final int BY_ATTACK_VS_NORMAL = 1;
	public static final int BY_ATTACK_CATEGORY = 2;
	public static final int BY_TRAIN_VS_TEST = 3;
	
	
	public OpticsPlot(String title) {
		super(title);
		dataset = new XYSeriesCollection();
		
		String chartTitle = title;
		String xAxisLabel = "Point number";
		String yAxisLabel = "Reachability";

		JFreeChart chart = ChartFactory.createXYBarChart(
				chartTitle,	//title
				xAxisLabel,			//domain label
				false,					//display date?
				yAxisLabel,			//range label
				dataset, 				//data
				PlotOrientation.VERTICAL, // orientation
				true, // include legend
				false, // tooltips?
				false // URLs?
				);
				
		
		
		
		//hart.getPlot().
		ChartPanel chartPanel = new ChartPanel(chart, false);
		chartPanel.setPreferredSize(new Dimension(1024, 768));
		setContentPane(chartPanel);
		
		//sets the base to -50
		XYBarRenderer chartrenderer = (XYBarRenderer)(chart.getXYPlot().getRenderer());
		chartrenderer.setBase(-50);
		
		
		
	}
	
	public void addData(ArrayList<ReachabilityPoint> points, int mode){
		switch (mode) {
		case BY_ATTACK_CATEGORY:
			addDataByCategory(points);
			break;
			
		case BY_ATTACK_VS_NORMAL:
			addDataByNormalAttack(points);
			break;

		case BY_TRAIN_VS_TEST:
			addDataByTrainTest(points);
			break;
			
		default:
			break;
		}
	}
	
	
	
	public void addDataBySteepAreas(ArrayList<ReachabilityPoint> points,ArrayList<SteepArea> areas){
		
		XYSeries steepUp = new XYSeries("SteepUp");
		XYSeries steepDown = new XYSeries("SteepDown");
		XYSeries flat = new XYSeries("Flat");
		
		steepDown.setNotify(false);
		steepUp.setNotify(false);
		flat.setNotify(false);
		
		
		int counter = 0;
		int areaCounter = 0;
		SteepArea currentArea = areas.get(areaCounter);
		boolean isIn = false;		
		
		boolean isSteepUp = false, isFlat = false;
		for (int i = 0; i < points.size(); i++) {
			
			
			ReachabilityPoint point = points.get(i);
			System.out.print("i:"+i + "\t" + currentArea.startIndex + " " + currentArea.endIndex);
			
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
			
			
			System.out.println("\t" + isFlat);
			
			counter++;
			double reachability = point.reachability ;
			if(isFlat){
				flat.add(counter, reachability);
			}else if(isSteepUp && !isFlat){
				steepUp.add(counter, reachability);
			}else if(!isSteepUp && !isFlat){
				steepDown.add(counter, reachability);
			}
			
			
		}
		dataset.addSeries(flat);
		dataset.addSeries(steepDown);
		dataset.addSeries(steepUp);
	}
	
	public void addDataByNormalAttack(ArrayList<ReachabilityPoint> points){
		
		normal = new XYSeries("Normal");
		attack = new XYSeries("Attack");
		normal.setNotify(false);
		attack.setNotify(false);
		
		int counter = 0;
		for (int i = 0; i < points.size(); i++) {
			ReachabilityPoint point = points.get(i);
			
			int label = point.label;
			double reachability = point.reachability ;
			if(label != 0){
				attack.add(counter, reachability);
			}else{
				normal.add(counter, reachability);
			}
			
			counter++;
		}
		
		dataset.addSeries(attack);
		dataset.addSeries(normal);
	}
	
	public void addDataByTrainTest(ArrayList<ReachabilityPoint> points){
		int test = 0,train = 0;
		
		XYSeries data[] = new XYSeries[2];
		data[0] = new XYSeries("Train");
		data[1] = new XYSeries("Test");
		
		for (int i = 0; i < data.length; i++) {
			data[i].setNotify(false);
		}
		
		for (int i = 0; i < points.size(); i++) {
			ReachabilityPoint point = points.get(i);
			
			if(point.hasLabel){
				data[0].add(i,point.reachability);
				train++;
			}else{
				data[1].add(i,point.reachability);
				test++;
			}			
		}
		
		for (int i = 0; i < data.length; i++) {
			dataset.addSeries(data[i]);
		}
		
		System.out.println("------------------VERIFICATION "+"PLOT"+"---------------");
		System.out.println("Train: " + train + "\t Test: "+ test);
	}
	
	public void addDataByCategory(ArrayList<ReachabilityPoint> points){
		
		XYSeries data[] = new  XYSeries[5];
		data[0] = new XYSeries("Normal");
		data[1] = new XYSeries("DOS");
		data[2] = new XYSeries("U2R");
		data[3] = new XYSeries("R2L");
		data[4] = new XYSeries("PROBE");
		
		for (int i = 0; i < data.length; i++) {
			data[i].setNotify(false);
		}
		
		for (int i = 0; i < points.size(); i++) {
			ReachabilityPoint point = points.get(i);
			
			int label = point.label;
			int labelCategory = DataPacket.getLabelCategory(label);			
			
			data[labelCategory].add(i,point.reachability);		
						
		}
		
		for (int i = 0; i < data.length; i++) {
			dataset.addSeries(data[i]);
		}
	}

	
	public static void plotGraph(String title, ArrayList<ReachabilityPoint> points, int mode){
		OpticsPlot demo = new OpticsPlot(title);
		demo.pack();
		RefineryUtilities.centerFrameOnScreen(demo);
		demo.setVisible(true);
		demo.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		demo.addData(points, mode);
	}
	
	public static void plotGraph(String title, String path, int mode){
		ArrayList<ReachabilityPoint> points  = OpticsOrderingReader.readFile(path);		
		plotGraph(title, points, mode);
	}
	
	public static void plotGraphAreas(String title, ArrayList<ReachabilityPoint> points, ArrayList<SteepArea> areas ){
		OpticsPlot demo = new OpticsPlot(title);
		demo.pack();
		RefineryUtilities.centerFrameOnScreen(demo);
		demo.setVisible(true);
		demo.setDefaultCloseOperation(EXIT_ON_CLOSE);		
		
		demo.addDataBySteepAreas(points, areas);
	}
	
	public static void plotGraphAreas(String title, String path, ArrayList<SteepArea> areas){
		ArrayList<ReachabilityPoint> points  = OpticsOrderingReader.readFile(path);		
		plotGraphAreas(title, points, areas);
	}
	
	public static void main(String[] args) {
//		 String path = "RandomPieces_200" + File.separatorChar+ "ids200_34-125.optics";
//		 String path = "RandomPieces_200" + File.separatorChar+ "ids200_32-67.optics";
//		 String path = "RandomPieces_200" + File.separatorChar+ "ids200_10-100.optics";
		 String path = "RandomPieces_10000" + File.separatorChar+ "test.optics";
		 plotGraph("OpticsPlot",path, OpticsPlot.BY_ATTACK_CATEGORY);	
//		 plotGraph("OpticsPlot",path, OpticsPlot.BY_TRAIN_VS_TEST);	
	}
}
