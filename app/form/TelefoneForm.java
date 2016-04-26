/**
 * 
 */
package form;

import java.io.Serializable;

/**
 * @author hpadmin
 *
 */
public class TelefoneForm implements Serializable {

	private static final long serialVersionUID = 190879656409876L;

	private Long id;
	
	private Integer tipo;
	
	private String prefixo;
	
	private String numero;

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the tipoTelefone
	 */
	public Integer getTipo() {
		return tipo;
	}

	/**
	 * @param tipoTelefone the tipoTelefone to set
	 */
	public void setTipo(Integer tipo) {
		this.tipo = tipo;
	}

	/**
	 * @return the prefixo
	 */
	public String getPrefixo() {
		return prefixo;
	}

	/**
	 * @param prefixo the prefixo to set
	 */
	public void setPrefixo(String prefixo) {
		this.prefixo = prefixo;
	}

	/**
	 * @return the numero
	 */
	public String getNumero() {
		return numero;
	}

	/**
	 * @param numero the numero to set
	 */
	public void setNumero(String numero) {
		this.numero = numero;
	}

}
