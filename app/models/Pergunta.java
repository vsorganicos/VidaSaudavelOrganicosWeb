/**
 * 
 */
package models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import play.db.jpa.Model;

/**
 * @author hpadmin
 *
 */

@Entity
@Table(name="PERGUNTA")
public class Pergunta extends Model implements Serializable {
	
	private static final long serialVersionUID = -7234761116705884365L;

	public Pergunta() {
	}
	
	public Pergunta(String questao) {
		this.questao = questao;
		this.ativo = Boolean.TRUE;
	}
	
	@Column(name="QUESTAO", length=180, nullable=false)
	private String questao;
	
	@Column(name="FLAG_ATIV0", nullable=false)
	private Boolean ativo;
	
	@ManyToMany(mappedBy="perguntas")
	private List<Questionario> questionarios;
	
	@OneToMany(fetch=FetchType.LAZY, mappedBy="pergunta")
	private List<PerguntaResposta> respostas;

	/**
	 * @return the questao
	 */
	public String getQuestao() {
		return questao;
	}

	/**
	 * @param questao the questao to set
	 */
	public void setQuestao(String questao) {
		this.questao = questao;
	}

	/**
	 * @return the ativo
	 */
	public Boolean getAtivo() {
		return ativo;
	}

	/**
	 * @param ativo the ativo to set
	 */
	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	/**
	 * @return the questionarios
	 */
	public List<Questionario> getQuestionarios() {
		if(this.questionarios==null)
			this.questionarios = new ArrayList<Questionario>();
		
		return questionarios;
	}

	/**
	 * @param questionarios the questionarios to set
	 */
	public void setQuestionarios(List<Questionario> questionarios) {
		this.questionarios = questionarios;
	}

	/**
	 * @return the respostas
	 */
	public List<PerguntaResposta> getRespostas() {
		if(this.respostas==null)
			this.respostas = new ArrayList<PerguntaResposta>();
		
		return respostas;
	}

	/**
	 * @param respostas the respostas to set
	 */
	public void setRespostas(List<PerguntaResposta> respostas) {
		this.respostas = respostas;
	}
	
	public boolean addResposta(PerguntaResposta perguntaResposta) {
		if(perguntaResposta!=null) {
			perguntaResposta.setPergunta(this);
			
			return getRespostas().add(perguntaResposta);
		}
		return false;
	}

	@Override
	public String toString() {
		return this.getQuestao();
	}
	
}
