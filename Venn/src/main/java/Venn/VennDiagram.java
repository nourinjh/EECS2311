package Venn;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class VennDiagram {

	// Backend Framework for a VennDiagram
	// Nabi Khalid, 216441677
	// VennDiagram is composed of Circles and String values 
	// Values are either in a circle, an intersection, or outside the VennDiagram
	
	// change 
	
	public String name; // Each VennDiagram has a name
	public String description; // Each VennDiagram has a description
	public static int counter; // VennDiagram counter
	public List<String> allValues; // list of values in and out of the VennDiagram 
	public List<Circle> circles; // list of circles 
	
	public VennDiagram() {
		VennDiagram.counter++;
		this.name = "Venn" + VennDiagram.counter;
		this.description = "";
		this.circles = new ArrayList<Circle>();
		this.allValues = new ArrayList<String>();
	}
	
	public VennDiagram(String name, String description) {
		VennDiagram.counter++;
		this.name = name;
		this.description = description;
		this.allValues = new ArrayList<String>();
		this.circles = new ArrayList<Circle>();
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void addCircle(Circle c) {
		this.circles.add(c);
	}
	
}
