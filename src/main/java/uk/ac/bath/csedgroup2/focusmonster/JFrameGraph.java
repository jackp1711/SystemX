package uk.ac.bath.csedgroup2.focusmonster;

import org.jfree.chart.annotations.CategoryLineAnnotation;
import org.jfree.chart.plot.CategoryMarker;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import uk.ac.bath.csedgroup2.focusmonster.models.Category;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.util.Rotation;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

import javax.swing.*;
import java.awt.*;
import java.util.List;


public class JFrameGraph extends JFrame {

	private static final long serialVersionUID = 1L;
	private transient DBF db;


	public ChartPanel redrawPieChart() {
		return createPieChart("Total browsing analytics");
	}

	public ChartPanel redrawBarChart(){
		return createBarChart("Total productivity goals");
	}


	public ChartPanel createPieChart(String chartTitle){
		PieDataset dataset = createPieDataset();
		JFreeChart chart = createPieChart(dataset, chartTitle);
		return new ChartPanel(chart);
	}

	public ChartPanel createBarChart(String chartTitle){
		CategoryDataset dataset = createBarDataset();
		JFreeChart chart = createBarChart(dataset, chartTitle);
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
		this.db.generateSampleData();
		List<Category> categoryArrayList = this.db.getGroupedCategoriesSinceTime(Timer.getCurrentTimestamp() - 604800);	//past week of data
		for (Category category : categoryArrayList) {
			data.addValue(category.getDuration()/3600, "Current" , category.getTitle());
			data.addValue(category.getGoal()/3600, "Goal", category.getTitle());

		}
		return data;
	}

	private JFreeChart createBarChart(CategoryDataset dataset, String title){
		JFreeChart chart = ChartFactory.createBarChart(title, "Category", "Productive time (Hours)", dataset);
		return chart;
	}
	

	private JFreeChart createPieChart(PieDataset dataset, String title){
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