package uk.ac.bath.csedgroup2.focusmonster;

import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import uk.ac.bath.csedgroup2.focusmonster.models.Category;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

import javax.swing.*;
import java.util.List;

public class JFrameGraph extends JFrame {

	public JFrameGraph(){
	}

	private static final long serialVersionUID = 1L;
	private transient DBF db;


	public ChartPanel createPieChart(){
		PieDataset dataset = createPieDataset();
		JFreeChart chart = ChartFactory.createPieChart("Your browsing habits", dataset, true, true, false);
		return new ChartPanel(chart);
	}

	public ChartPanel createBarChart(int time){
		CategoryDataset dataset = createBarDataset(time);
		JFreeChart chart = ChartFactory.createBarChart("Your goals", "Category", "Productive time (Hours)", dataset);
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

	public CategoryDataset createBarDataset(int time){
		DefaultCategoryDataset data = new DefaultCategoryDataset();
		//this.db.generateSampleData();
		List<Category> categoryArrayList = this.db.getGroupedCategoriesSinceTime(Timer.getCurrentTimestamp() - 86400*time);	//past week of data
		for (Category category : categoryArrayList) {
			data.addValue(category.getDuration()/3600, "Current" , category.getTitle());
			data.addValue(category.getGoal()/3600, "Goal", category.getTitle());

		}
		return data;
	}

	public JFrameGraph(DBF db){
		this.db = db;
	}
}