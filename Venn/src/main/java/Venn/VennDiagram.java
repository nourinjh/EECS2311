package Venn;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class VennDiagram {

	// Backend Framework for a VennDiagram
	// Nabi Khalid, 216441677
	
	String name;
	public static int counter;
	public Set<String> insideValues; // set of values in VennDiagram
	public List<String> allValues; // list of values out of the VennDiagram
	public List<Circle> circles; // list of circles 
	
	public VennDiagram() {
		VennDiagram.counter++;
		this.name = "Venn" + VennDiagram.counter;
		this.insideValues = new HashSet<String>();
		this.allValues = new ArrayList<String>();
		this.circles = new ArrayList<Circle>();
	}
	
	public VennDiagram(String name) {
		VennDiagram.counter++;
		this.name = name;
		this.insideValues = new HashSet<String>();
		this.allValues = new ArrayList<String>();
		this.circles = new ArrayList<Circle>();
	}
	
	public void addValue(String value) {
		this.allValues.add(value);
		// does not add to insideValues
	}
	
	public void addValueCircle(Circle c, String value) {
		// Checks for intersection
		// Add to circle list of values
		this.insideValues.add(value);
		this.allValues.add(value);
	}
	
	
	
	
	
	
}
