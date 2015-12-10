
package graphs;

public class DataNotSetException extends RuntimeException {
	private static final long serialVersionUID = 8823712681676295951L;

	public DataNotSetException() {
		super();
	}

	public DataNotSetException(String message) {
		super(message);
	}

	public DataNotSetException(String message, Throwable cause) {
		super(message, cause);
	}

	public DataNotSetException(Throwable cause) {
		super(cause);
	}

}
