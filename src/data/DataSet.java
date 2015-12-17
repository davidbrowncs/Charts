
package data;

import interfaces.DataModel;
import interfaces.SwingObserver;

import java.util.ArrayList;
import java.util.Collection;
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
 * Represents the data model for swing observers to get data from. Dependent
 * variables must be numbers in this implementation, Doubles in particular. This
 * class accepts any type of number as an argument, but the number is always
 * converted to its double value, without checking for rounding or truncation,
 * so for extremely precise or long numbers this will not be accurate.
 * 
 * Adding data to this dataset causes any swing observers observing the dataset
 * to recalculate and redraw themselves automatically.
 * 
 * @param <I>
 *            Type of data in the independent data set.
 */
public abstract class DataSet<I> implements DataModel<I, Double> {

	/**
	 * Keeps track of how many datasets are created.
	 */
	private static int dataSetCounter = 0;

	/**
	 * ID for a dataset. ID is used to compare datasets via the {@code equals}
	 * method. ID is the current {@code dataSetCounter}, and after being
	 * assigned, the {@dataSetCounter} is incremented.
	 */
	private int id = dataSetCounter++;

	/**
	 * List of independent variables. Is only one dimensional, so only one set
	 * of independents can be stored in one dataset at a time.
	 */
	protected List<I> indVars = new ArrayList<>();

	/**
	 * Dependent variables for the data set, Is a list of lists, so multiple
	 * dependent datasets can be represented by a dataset.
	 */
	protected List<List<Double>> depVars = new ArrayList<List<Double>>();

	/**
	 * List of swing observers observing this dataset.
	 */
	protected List<SwingObserver<I, Double>> observers = new ArrayList<>();

	/**
	 * Set the independent dataset to the one provided.
	 * 
	 * @param c
	 *            The collection to replace the current independent variables
	 *            with.
	 */
	public synchronized void setIndependent(Collection<I> c) {
		Objects.requireNonNull(c);
		indVars.clear();
		indVars.addAll(c);
	}

	/**
	 * Add a single variable to the independent dataset.
	 * 
	 * @param t
	 *            The value to be added to the independent dataset.
	 */
	public synchronized void addToIndependent(I t) {
		Objects.requireNonNull(t);
		indVars.add(t);
	}

	/**
	 * Returns the independent dataset.
	 * 
	 * @return Returns the independent dataset as a list.
	 */
	public synchronized List<I> getIndependent() {
		return indVars;
	}

	/**
	 * Get the list of dependent variables
	 * 
	 * @return Returns the list of dependent data sets
	 */
	public synchronized List<List<Double>> getDependent() {
		return depVars;
	}

	/**
	 * Add a dependent dataset to the model.
	 * 
	 * @param l
	 *            Collection of values to be added.
	 */
	public synchronized void addDependentSet(Collection<Double> l) {
		Objects.requireNonNull(l);
		depVars.add(new ArrayList<>(l));
		update();
	}

	/**
	 * Removes the data set at the given index (index specified by the order in
	 * which dependent datasets are added).
	 */
	public synchronized void removeDependent(int dataSet) {
		if (dataSet < 0 || dataSet >= depVars.size()) {
			throw new IndexOutOfBoundsException();
		}
		depVars.remove(dataSet);
		update();
	}

	/**
	 * Adds a data observer to receive a callback whenever the data model is
	 * updated.
	 * 
	 * @param observer
	 *            The observer to receive callbacks upon the data model being
	 *            updated.
	 */
	public synchronized void addObserver(SwingObserver<I, Double> observer) {
		Objects.requireNonNull(observer);
		if (!observers.contains(observer)) {
			Runnable r = () -> {
				observer.setDataModel(this);
				observer.updateDisplay();
			};
			observers.add(observer);
			if (!SwingUtilities.isEventDispatchThread()) {
				SwingUtilities.invokeLater(() -> {
					r.run();
				});
			} else {
				r.run();
			}
		}
	}

	/**
	 * Removes the given swing from this datamodel and is made to stop observing
	 * the datamodel.
	 * 
	 * @param o
	 *            Swing observer to be removed.
	 */
	public synchronized void removeObserver(SwingObserver<I, Double> o) {
		Objects.requireNonNull(o);
		observers.remove(o);
		if (!SwingUtilities.isEventDispatchThread()) {
			SwingUtilities.invokeLater(() -> {
				if (o.getDataModel().equals(this)) {
					o.removeDataModel();
				}
			});
		} else {
			if (o.getDataModel().equals(this)) {
				o.removeDataModel();
			}
		}
	}

	/**
	 * Stop all swing observers currently watching this dataset from watching
	 */
	public synchronized void removeAllObservers() {
		for (int i = observers.size() - 1; i >= 0; i--) {
			removeObserver(observers.get(i));
		}
	}

	/**
	 * Used whenever the swing observers need to be updated (when the data model
	 * changes)
	 */
	protected void update() {
		for (SwingObserver<I, Double> o : observers) {
			if (!SwingUtilities.isEventDispatchThread()) {
				SwingUtilities.invokeLater(() -> {
					o.updateDisplay();
				});
			} else {
				o.updateDisplay();
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
