
package data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class CategoricDataSet extends DataSet {
	private List<String> indVars = Collections.synchronizedList(new ArrayList<>());
	
	public CategoricDataSet() {
		super();
	}

	/**
	 * Return the independent variables list
	 * 
	 * @return Returns the list of independent variables
	 */
	public synchronized List<String> getIndependent() {
		return indVars;
	}

	/**
	 * Add a variable to the independent dataset
	 * 
	 * @param o
	 *            Value to be added
	 */
	public synchronized void addToIndependent(String o) {
		Objects.requireNonNull(o);
		indVars.add(o);
	}

	@Override
	public synchronized void setIndependent(Collection<?> list) {
		Objects.requireNonNull(list);
		ArrayList<String> newVars = new ArrayList<>();
		for (Object o : list) {
			if (!(o instanceof String)) {
				throw new IllegalArgumentException("Independent data collection must only contain strings.");
			}
			newVars.add((String) o);
		}
		indVars = Collections.synchronizedList(newVars);
	}

}