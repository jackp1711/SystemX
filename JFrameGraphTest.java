import java.awt.FlowLayout;
import java.util.ArrayList;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.util.Rotation;




public class JFrameGraphTest extends JFrame{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	JFrame JFrame1 = new JFrame();
	
	ArrayList<Wedge> wedges = new ArrayList<Wedge>();
	
	private ChartPanel createPieChart(String chartTitle){
		System.out.println("PieChart");
		fillData();
		PieDataset dataset = createDataset();
		JFreeChart chart = createChart(dataset, chartTitle);
		ChartPanel chartPanel = new ChartPanel(chart);
		return chartPanel;
		}
	
	private PieDataset createDataset(){
		System.out.println("PieDataset");
		DefaultPieDataset data = new DefaultPieDataset();
		
		for(Wedge thisWedge: wedges){
			data.setValue(thisWedge.getTitle(), thisWedge.getTime());
		}

		data.setValue("Site 1", 29);
		data.setValue("Site 2", 5);
		data.setValue("Site 3", 57);
		return data;
	}
	

	private JFreeChart createChart(PieDataset dataset, String title){
		System.out.println("Create chart");
		JFreeChart chart = ChartFactory.createPieChart(title, dataset, true, true, false);
		PiePlot plot = (PiePlot) chart.getPlot();
		plot.setStartAngle(290);
		plot.setDirection(Rotation.CLOCKWISE);
		plot.setForegroundAlpha(0.5f);
		return chart;
	}

	public void fillData(){
		ArrayList<String> stubTitles; //stubTitles to be populated by a database call
		for (String title: stubTitles){
			double totalTime = 0;
			//call database with parameter title
			ArrayList<Double> stubTimes; //stubTime to be populated by a database call
			for (Double time: stubTimes){
				totalTime += time;
			}
			wedges.add(new Wedge(title, totalTime));
		}
		
	}
	
	
	public JFrameGraphTest(){

		String title = "System X";
		JFrame1.setLayout(new FlowLayout());
		JFrame1.setSize(1000,1000);
		JFrame1.setLocationRelativeTo(null);
		JFrame1.add(createPieChart(title));
		JFrame1.setDefaultCloseOperation(EXIT_ON_CLOSE);
		JFrame1.pack();
		
	}
	
	public static void main(String[] args){
		java.awt.EventQueue.invokeLater(new Runnable(){
			public void run(){
				new JFrameGraphTest().JFrame1.setVisible(true);
			}
		});
	}
}