package Venn;

import java.util.ArrayList;
import java.util.List;

public class Circle {

	// VennDiagram is composed of Circles
	// Nabi Khalid, 216441677
	
	public String name;
	public List<String> values;
	public static int counter;
	
	public Circle() {
		Circle.counter++;
		this.name = "Circle" + Circle.counter;
		this.values = new ArrayList<String>();
	}
	
	public void addValueCircle (String value) {
		this.values.add(value);
	}
	
	public void removeValue (String value) {
		this.values.remove(value);
	}
	
}
