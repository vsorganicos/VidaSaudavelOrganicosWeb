/**
 * 
 */
package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import play.data.validation.Required;
import play.db.jpa.Model;

/**
 * @author guerrafe
 *
 */
@Entity
@Table(name="BAIRRO_ENTREGA")
public class BairroEntrega extends Model {
	
	private static final long serialVersionUID = -7756730216617962360L;

	@Required(message="message.required.endereco.bairro")
	@Column(name="NOME", length=150, nullable=false)
	private String nome;
	
	@Required(message="message.required.endereco.cidade")
	@Column(name="CIDADE", length=150, nullable=false)
	private String cidade;
	
	@Required(message="message.required.endereco.uf")
	@Column(name="UF", length=2, nullable=false)
	private String uf;
	
	@Column(name="URL", length=200, nullable=true)
	private String url;
	
	@Column(name="FLAG_ATIVO", nullable=false)
	private Boolean ativo;
	
	/**
	 * @return the nome
	 */
	public String getNome() {
		return nome;
	}
	/**
	 * @param nome the nome to set
	 */
	public void setNome(String nome) {
		this.nome = nome;
	}
	/**
	 * @return the cidade
	 */
	public String getCidade() {
		return cidade;
	}
	/**
	 * @param cidade the cidade to set
	 */
	public void setCidade(String cidade) {
		this.cidade = cidade;
	}
	/**
	 * @return the uf
	 */
	public String getUf() {
		return uf;
	}
	/**
	 * @param uf the uf to set
	 */
	public void setUf(String uf) {
		this.uf = uf;
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
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}
	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}
	
}
