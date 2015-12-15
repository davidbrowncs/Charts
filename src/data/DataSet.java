
package data;

import graphs.Graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
public abstract class DataSet {
	private static int dataSetCounter = 0;
	private int id = dataSetCounter++;

	/**
	 * Dependent variables for the data set, Is a list of lists so multiple
	 * dependent datasets can be represented on one graph
	 */
	protected List<ArrayList<Double>> depVars;

	public abstract void setIndependent(Collection<?> list);

	public abstract List<?> getIndependent();

	/**
	 * List of Graphs with this dataset as its dataset
	 */
	protected List<Graph> observers = new ArrayList<>();

	/**
	 * Init with thread safe lists
	 */
	public DataSet() {
		depVars = Collections.synchronizedList(new ArrayList<ArrayList<Double>>());
	}

	/**
	 * Get the list of dependent variables
	 * 
	 * @return Returns the list of dependent data sets
	 */
	public synchronized List<ArrayList<Double>> getDependent() {
		return depVars;
	}

	/**
	 * Add a dependent dataset
	 * 
	 * @param l
	 *            List of values to be added
	 */
	public synchronized void addDependentSet(ArrayList<Double> l) {
		Objects.requireNonNull(l);
		depVars.add(l);
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
	public synchronized void addObserver(Graph o) {
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
	public synchronized void removeObserver(Graph o) {
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
		for (int i = observers.size() - 1; i >= 0; i--) {
			removeObserver(observers.get(i));
		}
	}

	/**
	 * Used whenever the graphs should be updated
	 */
	protected void update() {
		for (Graph o : observers) {
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
		DataSet other = (DataSet) obj;
		if (id != other.id)
			return false;
		return true;
	}

}
