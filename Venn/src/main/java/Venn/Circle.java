package Venn;

import java.util.HashSet;
import java.util.Set;

public class Circle {
	
	// yessssir 
	
	public String name;
	public String description;
	public Set<String> values; // Each circle is a Set 
	public static int counter;
	
	public Circle() {
		Circle.counter++;
		this.name = "Circle" + Circle.counter;
		this.description = "";
		this.values = new HashSet<String>();
	}
	
	public Circle(String name, String description) {
		Circle.counter++;
		this.name = name;
		this.description = description;
		this.values = new HashSet<String>();
	}
	
	public void addValue (String value) {
		this.values.add(value);
	}
	
	public void removeValue (String value) {
		this.values.remove(value);
	}
	
	public static Set<String> performIntersection(Circle c1, Circle c2) { 
		c1.values.retainAll(c2.values);
		return new HashSet<String>(c1.values); 
	}
	
}
