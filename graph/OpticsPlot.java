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

import data.ReachabilityPoint;
import fileIO.OpticsOrderingReader;



public class OpticsPlot extends JFrame{
	
	XYSeriesCollection dataset;
	XYSeries normal;
	XYSeries attack;
	
	
	//1393380799912
	
	public OpticsPlot(String title) {
		super(title);
		dataset = new XYSeriesCollection();
		
		

		JFreeChart chart = ChartFactory.createXYBarChart(
				"XYBAR",	//title
				"Xaxis Label",			//domain label
				false,					//display date?
				"Yaxis Label",			//range label
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
	
	public void addData(String pathname){
		ArrayList<ReachabilityPoint> points = OpticsOrderingReader.readFile(pathname);
		addData(points);
	}
	
	public void addData(ArrayList<ReachabilityPoint> points){
		
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

	 public static void main(String[] args) {
		 OpticsPlot demo = new OpticsPlot("Bar Demo 1");
		 demo.pack();
		 RefineryUtilities.centerFrameOnScreen(demo);
		 demo.setVisible(true);
		 demo.setDefaultCloseOperation(EXIT_ON_CLOSE);
		 
		 String path = "RandomPieces_200" + File.separatorChar+ "ids200_32-67.optics";
		 demo.addData(path);
			
	}
}
