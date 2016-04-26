/**
 * 
 */
package models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import play.data.validation.Min;
import play.data.validation.Required;
import play.db.jpa.Model;

/**
 * @author guerrafe
 *
 */
@Entity
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Cacheable
@Table(name="DESCONTO")
public class Desconto extends Model {

	private static final long serialVersionUID = 2230720664447243872L;

	public final static BigDecimal CEM_PORCENTO = new BigDecimal("100.0");
	
	@Required
	@Min(value=0, message="message.minsize.value.desconto")
	@Column(name="PORCENTAGEM", nullable=false, scale=2, precision=4)
	private BigDecimal porcentagem;
	
	@Required
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATA_DESCONTO", nullable=true)
	private Date dataDesconto;
	
	@Required
	@Min(value=0, message="message.minsize.value.desconto")
	@Column(name="VALOR_DESCONTO", nullable=false, scale=2, precision=8)
	private BigDecimal valorDesconto;
	
	@Required
	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.REFRESH)
	private Usuario usuario;
	
	@OneToMany(mappedBy="desconto", fetch=FetchType.LAZY)
	private List<Pedido> pedidos;
	
	@OneToMany(mappedBy="desconto",fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	private List<CupomDesconto> cupons;
	
	public Desconto() {
		this.porcentagem = BigDecimal.ZERO;
		this.valorDesconto = BigDecimal.ZERO;
	}
	
	public Desconto(BigDecimal porcentagem) {
		this.porcentagem = porcentagem;
		this.valorDesconto = BigDecimal.ZERO;
	}
	
	public Desconto(BigDecimal porcentagem, Usuario usuario, Pedido pedido) {
		this.porcentagem = porcentagem;
		this.valorDesconto = new BigDecimal(0);
		this.usuario = usuario;
		getPedidos().add(pedido);
		this.dataDesconto = new Date();
	}

	/**
	 * @return the porcentagem
	 */
	public BigDecimal getPorcentagem() {
		if(this.porcentagem==null)
			this.porcentagem = BigDecimal.ZERO;
		
		return porcentagem;
	}

	/**
	 * @param porcentagem the porcentagem to set
	 */
	public void setPorcentagem(BigDecimal porcentagem) {
		if(porcentagem.equals(BigDecimal.ZERO))
			this.valorDesconto = BigDecimal.ZERO;
		
		this.porcentagem = porcentagem;
	}

	/**
	 * @return the dataDesconto
	 */
	public Date getDataDesconto() {
		return dataDesconto;
	}

	/**
	 * @param dataDesconto the dataDesconto to set
	 */
	public void setDataDesconto(Date dataDesconto) {
		this.dataDesconto = dataDesconto;
	}

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
	 * @param valorDesconto the valorDesconto to set
	 */
	public void setValorDesconto(BigDecimal valorDesconto) {
		if(valorDesconto.equals(BigDecimal.ZERO))
			this.porcentagem = BigDecimal.ZERO;
		
		this.valorDesconto = valorDesconto;
	}

	/**
	 * @return the cupom
	 */
	public List<CupomDesconto> getCupons() {
		if(this.cupons==null)
			this.cupons = new ArrayList<CupomDesconto>();
			
		return cupons;
	}

	/**
	 * @param cupom the cupom to set
	 */
	public void setCupons(List<CupomDesconto> cupons) {
		this.cupons = cupons;
	}

	/**
	 * @return the pedidos
	 */
	public List<Pedido> getPedidos() {
		if(pedidos==null)
			this.pedidos = new ArrayList<Pedido>();
			
		return pedidos;
	}

	/**
	 * @param pedidos the pedidos to set
	 */
	public void setPedidos(List<Pedido> pedidos) {
		this.pedidos = pedidos;
	}

	/**
	 * @return the valorDesconto
	 */
	public BigDecimal getValorDesconto() {
		return valorDesconto;
	}
	
	@Transient
	public void calcularDesconto(BigDecimal valorPedido) {
		if(getValorDesconto().doubleValue()>0) {
			this.setPorcentagem( new BigDecimal(getValorDesconto().doubleValue() / valorPedido.doubleValue()).multiply(Desconto.CEM_PORCENTO).setScale(2, BigDecimal.ROUND_HALF_UP));
			
		}else if(getPorcentagem().doubleValue()>0) {
			this.setValorDesconto( getPorcentagem().divide(Desconto.CEM_PORCENTO).multiply(valorPedido).setScale(2, BigDecimal.ROUND_HALF_UP) );
		}
	}
}
