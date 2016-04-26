/**
 * 
 */
package models;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import play.data.validation.Required;
import play.db.jpa.Model;

/**
 * @author Felipe Guerra
 * <p>
 * 	Classe que representa o modelo de um lote e os respectivos produtos.
 * </p>
 */
@Entity
@Table(name="PRODUTO_LOTE")
public class ProdutoLoteEstoque extends Model implements Serializable {

	private static final long serialVersionUID = -34567890987651L;
	
	@Temporal(TemporalType.DATE)
	@Column(name="DATA_VALIDADE", nullable=true)
	private Date dataValidadeLote;
	
	@Required(message="message.required.lote.quantidade")
	@Column(name="QUANTIDADE", nullable=false)
	private Integer quantidade;
	
	@ManyToOne(fetch=FetchType.EAGER, cascade=CascadeType.ALL)
	private Lote lote;
	
	@ManyToOne(fetch=FetchType.EAGER)
	private Produto produto;
	
	public Lote getLote() {
		return lote;
	}

	public void setLote(Lote lote) {
		this.lote = lote;
	}

	/**
	 * @return the dataValidadeLote
	 */
	public Date getDataValidadeLote() {
		return dataValidadeLote;
	}

	/**
	 * @param dataValidadeLote the dataValidadeLote to set
	 */
	public void setDataValidadeLote(Date dataValidadeLote) {
		this.dataValidadeLote = dataValidadeLote;
	}

	/**
	 * @return the produto
	 */
	public Produto getProduto() {
		return produto;
	}

	/**
	 * @param produto the produto to set
	 */
	public void setProduto(Produto produto) {
		produto.addLoteEstoque(this);
		
		this.produto = produto;
	}

	/**
	 * @return the quantidade
	 */
	public Integer getQuantidade() {
		return quantidade;
	}

	/**
	 * @param quantidade the quantidade to set
	 */
	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}
	
}
