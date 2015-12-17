
package interfaces;

import java.util.List;

public interface DataModel<I, D> {

	public void addObserver(SwingObserver<I, D> o);

	public void removeObserver(SwingObserver<I, D> o);

	public void removeAllObservers();

	public List<List<D>> getDependent();
	
	public List<I> getIndependent();
}
