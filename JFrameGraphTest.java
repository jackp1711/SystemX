import java.awt.FlowLayout;
import java.util.ArrayList;

import javax.swing.JFrame;

import Models.TimerEntry;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.util.Rotation;
import org.jfree.chart.ChartPanel;




public class JFrameGraphTest extends JFrame{
	

	private static final long serialVersionUID = 1L;
	private DBF db;

	JFrame JFrame1 = new JFrame();
	
	ArrayList<Wedge> wedges = new ArrayList<Wedge>();
	
	public ChartPanel createPieChart(String chartTitle){
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
		ArrayList<TimerEntry> timerEntries = this.db.getEntriesSinceTime(0); //stubTitles to be populated by a database call
		for (TimerEntry timerEntry : timerEntries){
			wedges.add(new Wedge(timerEntry.getUrl().getTitle(), timerEntry.getDuration()));
		}
	}
	
	
	public JFrameGraphTest(DBF db){
		this.db = db;
		/*String title = "System X";
		JFrame1.setLayout(new FlowLayout());
		JFrame1.setSize(1000,1000);
		JFrame1.setLocationRelativeTo(null);
		JFrame1.add(createPieChart("TestChart"));
		JFrame1.setDefaultCloseOperation(EXIT_ON_CLOSE);
		JFrame1.pack();
		*/
	}

}