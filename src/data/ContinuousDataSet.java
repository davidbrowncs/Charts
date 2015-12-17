
package data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Class representing a Double vs Double dataset. Adds the ability to add a 2D
 * mathematical function and to have the function automatically applied to all
 * data in the independent dataset.
 * 
 * All operations in this class are thread safe, and update any swing observers
 * in a thread safe manner.
 */
public class ContinuousDataSet extends DataSet<Double> {

	/**
	 * Maths functions, mapping the independent dataset to a dependent dataset
	 */
	private List<Function<Double, Double>> functions = new ArrayList<>();

	/**
	 * Auxiliary list to store the indexes of functions, so upon reapplying
	 * functions to the independent dataset we know which dependent data sets to
	 * ignore and which ones to update
	 */
	private List<Integer> functionIndexes = new ArrayList<>();

	/**
	 * Functional interface for performing maths functions
	 * 
	 * @param <E>
	 *            Independent data type
	 * @param <T>
	 *            Dependent data type (the type returned by the function)
	 */
	public interface Function<E, T> {
		T apply(E x);
	}

	/**
	 * Add a function to this data set, causes the result to be computed
	 * straight away on the current thread, and causes any observers watching
	 * this dataset to be updated.
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
	 * Set the independent dataset as well as reapplying all the functions to
	 * this new independent dataset.
	 */
	public synchronized void setIndependent(Collection<Double> list) {
		super.setIndependent(list);
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
	 * Removes the independent dataset, causes the swing observers to update
	 * themselves with an empty independent dataset.
	 */
	public synchronized void removeIndependent() {
		indVars.clear();
		update();
	}
}
