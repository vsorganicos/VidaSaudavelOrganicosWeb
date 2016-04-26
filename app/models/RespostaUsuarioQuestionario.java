/**
 * 
 */
package models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import play.db.jpa.Model;

/**
 * @author hpadmin
 *
 */
@Entity
@Table(name="RESPOSTA_USUARIO_QUESTIONARIO")
public class RespostaUsuarioQuestionario extends Model implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 98765406879568341L;

	@ManyToOne(fetch=FetchType.EAGER)
	private Usuario usuario;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATA_RESPOSTA", nullable=false)
	private Date dataResposta;
	
	@ManyToOne(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
	private Questionario questionario;
	
	@ManyToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	private List<Resposta> respostas;

	/**
	 * @return the usuario
	 */
	public Usuario getUsuario() {
		return usuario;
	}

	/**
	 * @param usuario the usuario to set
	 */
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	/**
	 * @return the dataResposta
	 */
	public Date getDataResposta() {
		return dataResposta;
	}

	/**
	 * @param dataResposta the dataResposta to set
	 */
	public void setDataResposta(Date dataResposta) {
		this.dataResposta = dataResposta;
	}

	/**
	 * @return the questionario
	 */
	public Questionario getQuestionario() {
		return questionario;
	}

	/**
	 * @param questionario the questionario to set
	 */
	public void setQuestionario(Questionario questionario) {
		this.questionario = questionario;
	}

	/**
	 * @return the respostas
	 */
	public List<Resposta> getRespostas() {
		if(this.respostas==null)
			this.respostas = new ArrayList<Resposta>();
			
		return respostas;
	}
	
	public boolean addResposta(Resposta resposta) {
		boolean result = false;
		
		if(resposta!=null) {
			resposta.getRespostaUsuarioQuestionario().add(this);
			result = getRespostas().add(resposta);
		}
		return result;
	}

	/**
	 * @param respostas the respostas to set
	 */
	public void setRespostas(List<Resposta> respostas) {
		this.respostas = respostas;
	}
		
}
