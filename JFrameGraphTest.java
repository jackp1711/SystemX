import java.awt.FlowLayout;
import java.util.ArrayList;

import javax.swing.JFrame;

import Models.Category;
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

	public ChartPanel redraw() {
		return createPieChart("You suck");
	}

	public ChartPanel createPieChart(String chartTitle){
		System.out.println("PieChart");
		PieDataset dataset = createDataset();
		JFreeChart chart = createChart(dataset, chartTitle);
		ChartPanel chartPanel = new ChartPanel(chart);
		return chartPanel;
	}
	
	private PieDataset createDataset(){
		System.out.println("PieDataset");
		DefaultPieDataset data = new DefaultPieDataset();

		ArrayList<Category> categoryArrayList = this.db.getGroupedCategoriesSinceTime(0);
		for (Category category : categoryArrayList) {
			data.setValue(category.getTitle(), category.getDuration());
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

	public JFrameGraphTest(DBF db){
		this.db = db;
	}
}