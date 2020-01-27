package Venn;

import java.util.ArrayList;
import java.util.List;

public class Circle {
	//hi
	// VennDiagram is composed of Circles
	// Nabi Khalid, 216441677
	
	public String name;
	public String description;
	public List<String> values;
	public static int counter;
	
	public Circle() {
		Circle.counter++;
		this.name = "Circle" + Circle.counter;
		this.description = "";
		this.values = new ArrayList<String>();
	}
	
	public Circle(String name, String description) {
		Circle.counter++;
		this.name = name;
		this.description = description;
		this.values = new ArrayList<String>();
	}
	
	public void addValue (String value) {
		this.values.add(value);
	}
	
	public void removeValue (String value) {
		this.values.remove(value);
	}
	
}
