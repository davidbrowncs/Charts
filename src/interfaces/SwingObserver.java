
package interfaces;

public interface SwingObserver<I, D> {

	public void setDataModel(DataModel<I, D> model);

	public DataModel<I, D> getDataModel();

	public void removeDataModel();

	public void updateDisplay();
}
