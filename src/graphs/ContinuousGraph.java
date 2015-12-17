
package graphs;

/**
 * Class to represent a continuous vs. continuous chart. Requires a data model
 * with a collection of doubles for its independent values, and a collection of
 * collections of doubles for the dependent values.
 */
public abstract class ContinuousGraph extends XYGraph<Double> {
	private static final long serialVersionUID = 1805212588655879298L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see graphs.Graph#convertPoints()
	 */
	@Override
	protected void convertPoints() {
		xPlotPoints.clear();
		for (Series aSeries : series) {
			aSeries.getyPlotPoints().clear();
		}

		// Reset padding around the graph drawing
		hpad = null;
		vPadMin = null;
		vPadMax = null;

		processNumberData(dataModel.getIndependent(), xRangeAuto, xPlotPoints, true);
		processDependents();
	}
}
