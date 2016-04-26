/**
 * 
 */
package models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import play.data.validation.Required;
import play.db.jpa.Model;

/**
 * @author guerrafe
 *
 */
@Entity
@Table(name="CARRINHO_PRODUTO")
public class CarrinhoProduto extends Model {
	
	private static final long serialVersionUID = 81763876502891L;

	@Required
	@Column(name="VALOR_TOTAL_COMPRA", nullable=true, precision=8, scale=2)
	private BigDecimal valorTotalCompra;
	
	@Required
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATA_COMPRA", nullable=false)
	private Date dataCompra;
	
	@Required
	@ManyToOne(fetch=FetchType.EAGER, cascade=CascadeType.REFRESH)
	private Cliente cliente;
	
	@Required
	@OneToMany(fetch=FetchType.EAGER, mappedBy="carrinho")
	private List<CarrinhoItem> itens = null;
	
	@ManyToMany(mappedBy="carrinhos")
	private List<CestaPronta> cestas = null;
	
	/**
	 * @return the cestas
	 */
	public List<CestaPronta> getCestas() {
		if(cestas==null)
			this.cestas = new ArrayList<CestaPronta>();
			
		return cestas;
	}
	/**
	 * @param cestas the cestas to set
	 */
	public void setCestas(List<CestaPronta> cestas) {
		this.cestas = cestas;
	}
	
	public boolean addCestaPronta(CestaPronta cestaPronta) {
		if(cestaPronta==null)
			return false;
		
		cestaPronta.getCarrinhos().add(this);
		
		return this.getCestas().add(cestaPronta);
	}
	
	public boolean contains(CestaPronta cestaPronta) {
		if(cestaPronta==null)
			return false;
		
		return getCestas().contains(cestaPronta);
	}
	
	/**
	 * @return the valorTotalCompra
	 */
	public BigDecimal getValorTotalCompra() {
		return valorTotalCompra.setScale(2, BigDecimal.ROUND_HALF_DOWN);
	}
	/**
	 * @param valorTotalCompra the valorTotalCompra to set
	 */
	public void setValorTotalCompra(BigDecimal valorTotalCompra) {
		this.valorTotalCompra = valorTotalCompra;
	}
	/**
	 * @return the dataCompra
	 */
	public Date getDataCompra() {
		return dataCompra;
	}
	/**
	 * @param dataCompra the dataCompra to set
	 */
	public void setDataCompra(Date dataCompra) {
		this.dataCompra = dataCompra;
	}
	
	/**
	 * @return the cliente
	 */
	public Cliente getCliente() {
		return cliente;
	}
	/**
	 * @param cliente the cliente to set
	 */
	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}
	/**
	 * @return the itens
	 */
	public List<CarrinhoItem> getItens() {
		if(itens==null)
			itens = new ArrayList<CarrinhoItem>();
		
		return itens;
	}
	/**
	 * @param itens the itens to set
	 */
	public void setItens(List<CarrinhoItem> itens) {
		this.itens = itens;
	}
	
	public void createItens(List<Produto> produtos) {
		if(!produtos.isEmpty()) {
			Long idProduto = -1L;
			CarrinhoItem item = new CarrinhoItem();
			
			Collections.sort(produtos);
			
			this.getItens().add(item);
			
			for(Produto p : produtos) {
				if(!idProduto.equals(-1L) && !idProduto.equals(p.getId())) {
					idProduto = p.getId();
					
					item = new CarrinhoItem();
					this.getItens().add(item);
				}
				this.getItens().get(this.getItens().size()-1).getProdutos().add(p);
				this.getItens().get(this.getItens().size()-1).addQuantidade();
				idProduto = p.getId();
			}
		}
	}
	
	/**
	 * Buscar a lista de produtos para um determinado Id
	 * @param idProduto
	 * @return produtos
	 */
	public CarrinhoItem getItemListProduto(Long idProduto) {
		CarrinhoItem result = null;
		
		for(CarrinhoItem item : this.getItens()) {
			List<Produto> tempProdutos = item.getProdutos();
			
			if(!tempProdutos.isEmpty() && tempProdutos.get(0).id.equals(idProduto)) {
				result = item;
				break;
			}
		}
		return result;
	}

}
