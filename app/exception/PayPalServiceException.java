/**
 * 
 */
package exception;

/**
 * Classe de Exceção para encapsular as chamadas ao gateway de pagamento PayPal
 * @author Felipe G. de Oliveira
 *
 */
public class PayPalServiceException extends Exception {

	/**
	 * 
	 */
	public PayPalServiceException() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public PayPalServiceException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public PayPalServiceException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public PayPalServiceException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

}
