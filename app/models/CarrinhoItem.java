/**
 * 
 */
package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import play.data.validation.Required;
import play.db.jpa.Model;

/**
 * @author guerrafe
 *
 */
@Entity
@Table(name="CARRINHO_ITEM")
public class CarrinhoItem extends Model {

	private static final long serialVersionUID = 1086515523232L;

	@Required
	@Column(name="QUANTIDADE", nullable=false)
	private Integer quantidade;
	
	@ManyToMany(fetch=FetchType.LAZY)
	private List<Produto> produtos;
	
	@ManyToOne(fetch=FetchType.EAGER)
	private CarrinhoProduto carrinho;

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

	/**
	 * @return the produtos
	 */
	public List<Produto> getProdutos() {
		if(produtos==null)
			produtos = new ArrayList<Produto>();
		
		return produtos;
	}

	/**
	 * @param produtos the produtos to set
	 */
	public void setProdutos(List<Produto> produtos) {
		this.produtos = produtos;
	}

	/**
	 * @return the carrinho
	 */
	public CarrinhoProduto getCarrinho() {
		return carrinho;
	}

	/**
	 * @param carrinho the carrinho to set
	 */
	public void setCarrinho(CarrinhoProduto carrinho) {
		this.carrinho = carrinho;
	}

	public void addQuantidade() {
		if(getQuantidade()==null)
			setQuantidade(0);

		this.quantidade++;
	}
	
}
