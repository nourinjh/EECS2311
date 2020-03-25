package backend_framework_nabi;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class venn_diagram {

	public List<venn_set> sets;
	
	public String name;
	
	public venn_diagram() {
		this.name = "Venn Diagram";
		this.sets = new ArrayList<venn_set>();
		
	}

	// Name manipulation
	
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	// Set manipulation
	
	public void addSet(venn_set v) {
		this.sets.add(v);
	}
	
	public void removeSet(venn_set v) {
		this.sets.remove(v);
	}
	
	public static venn_set intersection(venn_set s1, venn_set s2){
		
		// perform set intersection
		
		// retainAll();
		
		venn_set result = new venn_set("Intersection");
		
		for(String text : s1.getValues) {
			
			if(s2.getValues.contains(text)) {
				result.addValue(text);
			}
			
		}
		
		return result;
		
	}
	
	
	
}
