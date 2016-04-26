/**
 * 
 */
package exception;

/**
 * Classe de Exceção para encapsular as chamadas ao gateway de pagamento PayPal
 * @author Felipe G. de Oliveira
 *
 */
public class GatewayServiceException extends Exception {

	private static final long serialVersionUID = -837923860425399560L;

	/**
	 * 
	 */
	public GatewayServiceException() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public GatewayServiceException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public GatewayServiceException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public GatewayServiceException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

}
