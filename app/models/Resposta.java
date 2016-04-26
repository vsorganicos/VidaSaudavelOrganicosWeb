/**
 * 
 */
package models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import play.data.validation.Required;
import play.db.jpa.Model;

/**
 * @author hpadmin
 *
 */
@Entity
@Table(name="RESPOSTA")
public class Resposta extends Model implements Serializable {
	
	private static final long serialVersionUID = 6446220009339691411L;

	public Resposta() {
	}
	
	public Resposta(String resposta) {
		this.descricao = resposta;
		this.dataCadastro = new Date();
	}
	
	@Required
	@Column(name="DESCRICAO", length=120, nullable=false)
	private String descricao;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATA_CADASTRO")
	private Date dataCadastro;
	
	@OneToMany(fetch=FetchType.LAZY, mappedBy="resposta")
	private List<PerguntaResposta> perguntas;
	
	@ManyToMany(fetch=FetchType.LAZY, mappedBy="respostas")
	private List<RespostaUsuarioQuestionario> respostaUsuarioQuestionario;

	/**
	 * @return the descricao
	 */
	public String getDescricao() {
		return descricao;
	}

	/**
	 * @param descricao the descricao to set
	 */
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	/**
	 * @return the dataCadastro
	 */
	public Date getDataCadastro() {
		return dataCadastro;
	}

	/**
	 * @param dataCadastro the dataCadastro to set
	 */
	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	/**
	 * @return the perguntas
	 */
	public List<PerguntaResposta> getPerguntas() {
		return perguntas;
	}

	/**
	 * @param perguntas the perguntas to set
	 */
	public void setPerguntas(List<PerguntaResposta> perguntas) {
		this.perguntas = perguntas;
	}

	/**
	 * @return the respostaUsuarioQuestionario
	 */
	public List<RespostaUsuarioQuestionario> getRespostaUsuarioQuestionario() {
		if(respostaUsuarioQuestionario==null)
			this.respostaUsuarioQuestionario = new ArrayList<RespostaUsuarioQuestionario>();
		
		return respostaUsuarioQuestionario;
	}

	/**
	 * @param respostaUsuarioQuestionario the respostaUsuarioQuestionario to set
	 */
	public void setRespostaUsuarioQuestionario(
			List<RespostaUsuarioQuestionario> respostaUsuarioQuestionario) {
		this.respostaUsuarioQuestionario = respostaUsuarioQuestionario;
	}

}
