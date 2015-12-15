
package data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ContinuousDataSet extends DataSet {
	private List<Double> indVars = Collections.synchronizedList(new ArrayList<>());;

	/**
	 * Math functions performed by the user of the frameowrk
	 */
	private List<Function<Double, Double>> functions = new ArrayList<>();

	/**
	 * Auxiliary list to store the indexes of functions, so upon reapplying
	 * functions to the independent dataset we know which dependent data sets to
	 * ignore and which ones to update
	 */
	private List<Integer> functionIndexes = new ArrayList<>();

	public ContinuousDataSet() {
		super();
	}

	/**
	 * Functional interface for performing maths functions
	 * 
	 * @param <E>
	 *            Independent data type
	 * @param <T>
	 *            Dependent data type
	 */
	public interface Function<E, T> {
		T apply(E x);
	}

	/**
	 * Add a function to this data set, causes the result to be computed
	 * straight away on the current thread, and causes any graphs watching this
	 * dataset to redraw themselves
	 * 
	 * @param f
	 *            New function to be performed
	 */
	public synchronized void addFunction(Function<Double, Double> f) {
		Objects.requireNonNull(f);
		functionIndexes.add(functions.size());
		functions.add(f);
		ArrayList<Double> y = new ArrayList<>();
		for (Double a : indVars) {
			y.add(f.apply(a).doubleValue());
		}
		depVars.add(y);
		update();
	}

	/**
	 * Return the independent variables list
	 * 
	 * @return Returns the list of independent variables
	 */
	public synchronized List<Double> getIndependent() {
		return indVars;
	}

	public synchronized void setIndependent(Collection<?> list) {
		Objects.requireNonNull(list);
		ArrayList<Double> newVars = new ArrayList<>();
		for (Object o : list) {
			if (!(o instanceof Number)) {
				throw new IllegalArgumentException("Only numbers are allowed in the independent variables");
			}
			newVars.add(((Number) o).doubleValue());
		}
		indVars = Collections.synchronizedList(newVars);
		reApplyFunctions();
	}

	/**
	 * Add a variable to the independent dataset
	 * 
	 * @param o
	 *            Value to be added
	 */
	public synchronized void addToInd(Double o) {
		Objects.requireNonNull(o);
		indVars.add(o);
		reApplyFunctions();
	}

	/**
	 * Re-applies functions, used when dataset modified
	 */
	private void reApplyFunctions() {
		if (functionIndexes.isEmpty()) {
			return;
		}
		Collections.sort(functionIndexes);
		for (int i = functionIndexes.get(functionIndexes.size() - 1); i >= 0; i--) {
			depVars.remove((int) functionIndexes.get(i));
		}
		functionIndexes.clear();
		for (Function<Double, Double> f : functions) {
			ArrayList<Double> newList = new ArrayList<>();
			for (Double e : indVars) {
				newList.add(f.apply(e).doubleValue());
			}
			functionIndexes.add(depVars.size());
			depVars.add(newList);
		}
		update();
	}

	/**
	 * Removes the independent dataset, causes the graphs watching this dataset
	 * to redraw themselves with no values
	 */
	public synchronized void removeIndependent() {
		indVars.clear();
		update();
	}
}
