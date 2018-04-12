package uk.ac.bath.csedgroup2.systemx;

import uk.ac.bath.csedgroup2.systemx.models.Category;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.util.Rotation;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

import javax.swing.*;
import java.util.List;


public class JFrameGraphTest extends JFrame {

	private static final long serialVersionUID = 1L;
	private transient DBF db;

	JFrame JFrame1 = new JFrame();

	public ChartPanel redraw() {
		return createPieChart("Your analytics since 4eva");
	}

	public ChartPanel createPieChart(String chartTitle){
		PieDataset dataset = createDataset();
		JFreeChart chart = createChart(dataset, chartTitle);
		ChartPanel chartPanel = new ChartPanel(chart);
		return chartPanel;
	}
	
	private PieDataset createDataset(){
		DefaultPieDataset data = new DefaultPieDataset();

		List<Category> categoryArrayList = this.db.getGroupedCategoriesSinceTime(0);
		for (Category category : categoryArrayList) {
			data.setValue(category.getTitle(), category.getDuration());
		}

		return data;
	}
	

	private JFreeChart createChart(PieDataset dataset, String title){
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