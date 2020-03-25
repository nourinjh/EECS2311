package backend_framework_nabi;

import java.util.HashSet;
import java.util.Set;

// Nabi Khalid, 216441677

public class venn_set {

	public Set<String> values;
	
	public String name;
	
	public static int counter;
	
	public venn_set() {
		venn_set.counter++;
		this.values = new HashSet<String>();
		this.name = "Set " + venn_set.counter;
	}
	
	public venn_set(String name) {
		venn_set.counter++;
		this.values = new HashSet<String>();
		this.name = name;
	}

	// Value manipulation
	
	public Set<String> getValues() {
		return this.values;
	}

	public void addValue(String value) {
		this.values.add(value);
	}
	
	public void removeVaue(String value) {
		this.values.remove(value);
	}
	
	public void hasValue(String value) {
		this.values.contains(value);
	}

	// Name manipulation
	
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
