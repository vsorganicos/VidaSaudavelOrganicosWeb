/**
 * 
 */
package exception;

/**
 * Classe que representa os erros ocasionados com a manipulação do estoque
 * @author Felipe G. de Oliveira
 *
 */
public class ProdutoEstoqueException extends Exception {

	/**
	 * 
	 */
	public ProdutoEstoqueException() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public ProdutoEstoqueException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public ProdutoEstoqueException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ProdutoEstoqueException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

}
