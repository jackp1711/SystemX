package uk.ac.bath.csedgroup2.focusmonster;

import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import uk.ac.bath.csedgroup2.focusmonster.models.Category;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.util.Rotation;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

import javax.swing.*;
import java.util.List;


public class JFrameGraph extends JFrame {

	private static final long serialVersionUID = 1L;
	private transient DBF db;

	public ChartPanel redraw() {
		return createPieChart("Your analytics since 4eva");
	}

	/*
	public ChartPanel redraw(){
		return createBarChart("Weekly productivity goals");
	}
	*/

	public ChartPanel createPieChart(String chartTitle){
		PieDataset dataset = createPieDataset();
		JFreeChart chart = createChart(dataset, chartTitle);
		return new ChartPanel(chart);
	}

	public ChartPanel createBarChart(String chartTitle){
		CategoryDataset dataset = createBarDataset();
		JFreeChart chart = createChart(dataset, chartTitle);
		return new ChartPanel(chart);
	}
	
	private PieDataset createPieDataset(){
		DefaultPieDataset data = new DefaultPieDataset();

		List<Category> categoryArrayList = this.db.getGroupedCategoriesSinceTime(0);
		for (Category category : categoryArrayList) {
			data.setValue(category.getTitle(), category.getDuration());
		}

		return data;
	}

	private CategoryDataset createBarDataset(){
		DefaultCategoryDataset data = new DefaultCategoryDataset();

		List<Category> categoryArrayList = this.db.getGroupedCategoriesSinceTime(0);
		for (Category category : categoryArrayList){
			data.addValue(category.getDuration(),"Current", category.getTitle());
		}
		/*
		data.addValue(1.0, "Goal", "Monday");
		data.addValue(2.0, "Actual", "Monday");

		data.addValue(3.0, "Goal", "Tuesday");
		data.addValue(2.0, "Actual", "Tuesday");

		data.addValue(1.5, "Goal","Wednesday");
		data.addValue(1.5, "Actual", "Wednesday");

		data.addValue(2.0, "Goal", "Thursday");
		data.addValue(0.5,"Actual", "Thursday");
		*/
		return data;
	}

	private JFreeChart createChart(CategoryDataset dataset, String title){
		JFreeChart chart = ChartFactory.createBarChart(title, "Day", "Productive time (secs)", dataset);
		return chart;
	}
	

	private JFreeChart createChart(PieDataset dataset, String title){
		JFreeChart chart = ChartFactory.createPieChart(title, dataset, true, true, false);
		PiePlot plot = (PiePlot) chart.getPlot();
		plot.setStartAngle(290);
		plot.setDirection(Rotation.CLOCKWISE);
		plot.setForegroundAlpha(0.5f);
		return chart;
	}

	public JFrameGraph(DBF db){
		this.db = db;
	}
}