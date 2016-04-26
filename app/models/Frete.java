/**
 * 
 */
package models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import play.data.validation.Required;
import play.db.jpa.Model;

/**
 * @author Felipe G. de Oliveira
 *
 */
@Entity
@Table(name="FRETE")
public class Frete extends Model {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 12345467335425311L;
	
	@OneToMany(fetch=FetchType.LAZY, mappedBy="frete", cascade=CascadeType.ALL)
	private List<Pedido> pedidos;

	public Frete() {}
	
	public Frete(Double value) {
		setValor(BigDecimal.valueOf(value));
	}
	
	public Frete(BigDecimal valor) {
		this.valor = valor;
	}
	
	@Required(message="validation.required")
	@Column(name="VALOR", nullable=false, scale=2, precision=8)
	private BigDecimal valor = BigDecimal.ZERO;

	/**
	 * @return the valor
	 */
	public BigDecimal getValor() {
		if(this.valor==null)
			this.valor = BigDecimal.ZERO;
		
		return valor;
	}

	/**
	 * @param valor the valor to set
	 */
	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	/**
	 * @return the pedidos
	 */
	public List<Pedido> getPedidos() {
		if(this.pedidos==null)
			this.pedidos = new ArrayList<Pedido>();
			
		return pedidos;
	}
	
	public boolean addPedido(Pedido pedido) {
		if(pedido==null)
			return false;
		
		pedido.setFrete(this);
		
		return this.getPedidos().add(pedido);
	}

	/**
	 * @param pedidos the pedidos to set
	 */
	public void setPedidos(List<Pedido> pedidos) {
		this.pedidos = pedidos;
	}
	
}
