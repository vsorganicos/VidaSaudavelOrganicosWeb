/**
 * 
 */
package form;

import java.io.Serializable;

/**
 * @author hpadmin
 *
 */
public class PerguntaRespostaForm implements Serializable {

	private static final long serialVersionUID = -3582867259594920764L;

	private Long id;
	
	private Long idPergunta;
	
	private Long idResposta;

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
	 * @return the idPergunta
	 */
	public Long getIdPergunta() {
		return idPergunta;
	}

	/**
	 * @param idPergunta the idPergunta to set
	 */
	public void setIdPergunta(Long idPergunta) {
		this.idPergunta = idPergunta;
	}

	/**
	 * @return the idResposta
	 */
	public Long getIdResposta() {
		return idResposta;
	}

	/**
	 * @param idResposta the idResposta to set
	 */
	public void setIdResposta(Long idResposta) {
		this.idResposta = idResposta;
	}
	
}
