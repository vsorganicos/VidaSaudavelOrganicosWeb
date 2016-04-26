/**
 * 
 */
package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import play.data.validation.Required;
import play.db.jpa.Model;

/**
 * @author guerrafe
 *
 */
@Entity
@Table(name="SECAO")
public class Secao extends Model implements Comparable<Secao> {
	
	private static final long serialVersionUID = -5950668593471354114L;

	@Required(message="message.required.secao.descricao")
	@Column(name="DESCRICAO", length=120, nullable=false)
	private String descricao;
	
	@Required(message="message.required.secao.ativo")
	@Column(name="FLAG_ATIVO", nullable=false)
	private Boolean ativo;
	
	@OneToOne(fetch=FetchType.EAGER, optional=true, cascade=CascadeType.ALL)
	private Secao secaoPai;
	
	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.REFRESH, mappedBy="secao")
	private List<Produto> produtos = null;

	public Secao() {
		// TODO Auto-generated constructor stub
	}
	
	public Secao(Long id) {
		this.id = id;
	}
	
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
	 * @return the secaoPai
	 */
	public Secao getSecaoPai() {
		return secaoPai;
	}

	/**
	 * @param secaoPai the secaoPai to set
	 */
	public void setSecaoPai(Secao secaoPai) {
		this.secaoPai = secaoPai;
	}

	/**
	 * @return the ativo
	 */
	public Boolean isAtivo() {
		return ativo;
	}

	/**
	 * @param ativo the ativo to set
	 */
	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	/**
	 * @return the produtos
	 */
	public List<Produto> getProdutos() {
		if(this.produtos==null)
			this.produtos = new ArrayList<Produto>();
		
		return produtos;
	}

	/**
	 * @param produtos the produtos to set
	 */
	public void setProdutos(List<Produto> produtos) {
		this.produtos = produtos;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((descricao == null) ? 0 : descricao.hashCode()) + ((id==null) ? 0 : this.id.hashCode());
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
		if (!(obj instanceof Secao))
			return false;
		final Secao other = (Secao) obj;
		
		if(this.id==null) {
			if(other.id!=null)
				return false;
		}
		if (descricao == null) {
			if (other.descricao != null)
				return false;
		} else if (!descricao.equals(other.descricao) && !this.id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public int compareTo(Secao o) {
		if(o==null)
			return 0;

		return this.id.compareTo(o.id);
	}
	
}
