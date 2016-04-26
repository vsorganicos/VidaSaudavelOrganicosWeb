/**
 * 
 */
package types;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>Classe utilizada para WS.</p>
 * @author Felipe G. de Oliveira
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="EstoqueService", propOrder={
		"error",
		"message"
})
@XmlRootElement(name="EstoqueService")
public class EstoqueService {
	
	@XmlTransient
	private static Map<Long, String> errors = new HashMap<Long, String>();
	
	@XmlElement(name="Error", required=false)
	private Long error;
	
	@XmlElement(name="Message", required=false)
	private String message;
	
	static {
		errors.put(1000L, "Produto não encontrado");
		errors.put(1001L, "Produto não disponível para a quantidade requisitada");
	}
	
	public EstoqueService() {
	}

	/**
	 * @return the error
	 */
	public Long getError() {
		return error;
	}

	/**
	 * @param error the error to set
	 */
	public void setError(Long error) {
		this.error = error;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	
	public static String getErrorMessage(Long idError) {
		return errors.get(idError);
	}
}
