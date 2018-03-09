
public class Wedge {
	
	private String title;
	private double time;
	
	public Wedge(String initialTitle, double totalTime){
		title = initialTitle;
		time = totalTime;
	}
	
	public Wedge(){
		title = "Default Title";
		time = 0;
	}
	
	public Wedge(String initialTitle){
		title = initialTitle;
		time = 0;
	}
	
	public void setTitle(String newTitle){
		title = newTitle;
	}
	
	public void setTime(int newTime){
		time = newTime;
	}
	
	public String getTitle(){
		return title;
	}
	
	public int getTime(){
		return time;
	}
}
