/**
 * 
 */
package exception;

/**
 * @author guerrafe
 *
 */
public class SystemException extends Exception {
	
	public SystemException() {
		super();
	}
	
	public SystemException(String msg, Throwable t) {
		super(msg, t);
	}
	
	public SystemException(Throwable t) {
		super(t);
	}

}
