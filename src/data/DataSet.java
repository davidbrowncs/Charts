
package data;

import graphs.Graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import javax.swing.SwingUtilities;

/**
 * Copyright (C) 2015 David Brown. Permission is granted to copy, distribute
 * and/or modify this document under the terms of the GNU Free Documentation
 * License, Version 1.3 or any later version published by the Free Software
 * Foundation; with no Invariant Sections, no Front-Cover Texts, and no
 * Back-Cover Texts. A copy of the license is included in the section entitled
 * "GNU Free Documentation License".
 *
 * Class which is used to store data to be drawn by graphs. Interacting with
 * this class is thread safe, and updating a dataset will automatically cause a
 * graph with this dataset set as its dataset to redraw itself with the new data
 *
 * @param <E>
 *            Type of Data in the independent data set
 * @param <T>
 *            Type of data in the independent data set, must be a type of number
 */
public final class DataSet<E, T extends Number> {

	private static int dataSetCounter = 0;
	private int id = dataSetCounter++;

	/**
	 * Indepedent variables for the data set
	 */
	private List<E> indVars;

	/**
	 * Dependent variables for the data set, Is a list of lists so multiple
	 * dependent datasets can be represented on one graph
	 */
	private List<ArrayList<T>> depVars;

	/**
	 * List of Graphs with this dataset as its dataset
	 */
	private List<Graph<E, T>> observers = new ArrayList<>();

	/**
	 * Math functions performed by the user of the frameowrk
	 */
	private List<Function<E, T>> functions = new ArrayList<>();

	/**
	 * Auxiliary list to store the indexes of functions, so upon reapplying
	 * functions to the independent dataset we know which dependent data sets to
	 * ignore and which ones to update
	 */
	private List<Integer> functionIndexes = new ArrayList<>();

	/**
	 * Init with thread safe lists
	 */
	public DataSet() {
		indVars = Collections.synchronizedList(new ArrayList<E>());
		depVars = Collections.synchronizedList(new ArrayList<ArrayList<T>>());
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
	public synchronized void addFunction(Function<E, T> f) {
		functionIndexes.add(functions.size());
		functions.add(f);
		ArrayList<T> y = new ArrayList<>();
		for (E a : indVars) {
			y.add(f.apply(a));
		}
		depVars.add(y);
		update();
	}

	/**
	 * Return the independent variables list
	 * 
	 * @return Returns the list of independent variables
	 */
	public synchronized List<E> getIndependent() {
		return indVars;
	}

	/**
	 * Get the list of dependent variables
	 * 
	 * @return Returns the list of dependent data sets
	 */
	public synchronized List<ArrayList<T>> getDependent() {
		return depVars;
	}

	/**
	 * Sets the Independent data set to the ones given by the list a. Clones the
	 * list, so the one given as a parameter can still be modified and won't be
	 * changed by this class
	 * 
	 * @param a
	 *            List to set as the independent dataset
	 */
	@SuppressWarnings("unchecked")
	public synchronized void setInd(ArrayList<E> a) {
		indVars = (ArrayList<E>) a.clone();
		reApplyFunctions();
	}

	/**
	 * Add a variable to the independent dataset
	 * 
	 * @param a
	 *            Value to be added
	 */
	public synchronized void addToInd(E a) {
		Objects.requireNonNull(a);
		indVars.add(a);
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
		for (Function<E, T> f : functions) {
			ArrayList<T> newList = new ArrayList<>();
			for (E e : indVars) {
				newList.add(f.apply(e));
			}
			functionIndexes.add(depVars.size());
			depVars.add(newList);
		}
		update();
	}

	/**
	 * Add a dependent dataset
	 * 
	 * @param l
	 *            List of values to be added
	 */
	public synchronized void addDep(ArrayList<T> l) {
		Objects.requireNonNull(l);
		depVars.add(l);
		update();
	}

	/**
	 * Removes the independent dataset, causes the graphs watching this dataset
	 * to redraw themselves with no values
	 */
	public void removeIndependent() {
		indVars.clear();
		update();
	}

	/**
	 * Removes the data set at the given index (index specified by the order in
	 * which dependent datasets are added
	 */
	public synchronized void removeDependent(int dataSet) {
		depVars.remove(dataSet);
		update();
	}

	/**
	 * Adds a graph to watch this dataset
	 * 
	 * @param o
	 *            The graph to begin watching this dataset
	 */
	public synchronized void addObserver(Graph<E, T> o) {
		Objects.requireNonNull(o);
		if (!observers.contains(o)) {
			observers.add(o);
			if (!SwingUtilities.isEventDispatchThread()) {
				SwingUtilities.invokeLater(() -> {
					o.setDataSet(this);
					o.updated();
				});
			} else {
				o.setDataSet(this);
				o.updated();
			}
		}
	}

	/**
	 * Stops the given chart from watching this dataset
	 * 
	 * @param o
	 *            Graph to update
	 */
	public synchronized void removeObserver(Graph<E, T> o) {
		Objects.requireNonNull(o);
		observers.remove(o);
		if (!SwingUtilities.isEventDispatchThread()) {
			SwingUtilities.invokeLater(() -> {
				if (o.getDataSet().equals(this)) {
					o.removeDataSet();
				}
			});
		} else {
			if (o.getDataSet().equals(this)) {
				o.removeDataSet();
			}
		}
	}

	/**
	 * Stop all graphs currently watching this dataset from watching
	 */
	public synchronized void removeAllObservers() {
		for (Iterator<Graph<E, T>> it = observers.iterator(); it.hasNext(); it.remove()) {
			removeObserver(it.next());
		}
	}

	/**
	 * Used whenever the graphs should be updated
	 */
	private void update() {
		for (Graph<E, T> o : observers) {
			if (!SwingUtilities.isEventDispatchThread()) {
				SwingUtilities.invokeLater(() -> {
					o.updated();
					o.repaint();
				});
			} else {
				o.updated();
				o.repaint();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof DataSet))
			return false;
		@SuppressWarnings("rawtypes")
		DataSet other = (DataSet) obj;
		if (id != other.id)
			return false;
		return true;
	}

}
