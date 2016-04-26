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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.Model;

/**
 * @author hpadmin
 *
 */
@Entity
@Table(name="QUESTIONARIO")
public class Questionario extends Model implements Serializable {
	
	private static final long serialVersionUID = -1677508748616836930L;

	@Required
	@MaxSize(value=100)
	@Column(name="TITULO", length=100, nullable=false)
	private String titulo;
	
	@Column(name="FLAG_ATIV0", nullable=false)
	private Boolean ativo;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATA_CADASTRO", nullable=false)
	private Date dataCadastro;
	
	@ManyToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY)
	private List<Pergunta> perguntas;
	
	@OneToMany(fetch=FetchType.LAZY, mappedBy="questionario")
	private List<RespostaUsuarioQuestionario> respostasQuestionarios;

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
	 * @return the questPergunta
	 */
	public List<Pergunta> getPerguntas() {
		if(this.perguntas==null)
			this.perguntas = new ArrayList<Pergunta>();
		
		return this.perguntas;
	}

	/**
	 * @param questPergunta the questPergunta to set
	 */
	public void setPerguntas(List<Pergunta> questPergunta) {
		if(this.perguntas==null)
			this.perguntas = new ArrayList<Pergunta>();
		
		this.perguntas = questPergunta;
	}
	
	public boolean addPergunta(Pergunta pergunta) {
		if(pergunta==null)
			return false;
		
		pergunta.getQuestionarios().add(this);
		
		return getPerguntas().add(pergunta);
	}

	/**
	 * @return the respostasQuestionarios
	 */
	public List<RespostaUsuarioQuestionario> getRespostasQuestionarios() {
		return respostasQuestionarios;
	}

	/**
	 * @param respostasQuestionarios the respostasQuestionarios to set
	 */
	public void setRespostasQuestionarios(
			List<RespostaUsuarioQuestionario> respostasQuestionarios) {
		this.respostasQuestionarios = respostasQuestionarios;
	}

	/**
	 * @return the titulo
	 */
	public String getTitulo() {
		return titulo;
	}

	/**
	 * @param titulo the titulo to set
	 */
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((titulo == null) ? 0 : titulo.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof Questionario))
			return false;
		final Questionario other = (Questionario) obj;
		if (titulo == null) {
			if (other.titulo != null)
				return false;
		} else if (!titulo.equals(other.titulo) && !super.id.equals(other.id))
			return false;
		return true;
	}
}
