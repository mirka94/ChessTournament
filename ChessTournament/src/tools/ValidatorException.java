package tools;

/**
 * Wyjątek dla niedozwolonej akcji przy próbie edycji danych zawodnika
 */
public class ValidatorException extends Exception {
	private static final long serialVersionUID = -3647346670224043963L;

	public ValidatorException(String message) {
		super(message);
	}
}
