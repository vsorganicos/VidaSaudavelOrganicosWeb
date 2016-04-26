/**
 * 
 */
package models;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import play.db.jpa.Model;

/**
 * @author hpadmin
 *
 */
@Entity
@Table(name="PERGUNTA_RESPOSTA")
public class PerguntaResposta extends Model implements Serializable {
	
	private static final long serialVersionUID = 98768574615471L;

	public PerguntaResposta() {
	}
	
	public PerguntaResposta(Pergunta pergunta, Resposta resposta) {
		this.pergunta = pergunta;
		this.resposta = resposta;
	}
	
	@ManyToOne(fetch=FetchType.EAGER)
	private Pergunta pergunta;
	
	@ManyToOne(fetch=FetchType.EAGER)
	private Resposta resposta;

	/**
	 * @return the pergunta
	 */
	public Pergunta getPergunta() {
		return pergunta;
	}

	/**
	 * @param pergunta the pergunta to set
	 */
	public void setPergunta(Pergunta pergunta) {
		this.pergunta = pergunta;
	}

	/**
	 * @return the resposta
	 */
	public Resposta getResposta() {
		return resposta;
	}

	/**
	 * @param resposta the resposta to set
	 */
	public void setResposta(Resposta resposta) {
		this.resposta = resposta;
	}
	
}
