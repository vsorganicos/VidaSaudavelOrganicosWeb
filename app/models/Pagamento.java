/**
 * 
 */
package models;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import play.data.validation.Required;
import play.db.jpa.Model;

/**
 * @author guerrafe
 *
 */
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Cacheable
@Entity
@Table(name="PAGAMENTO")
public class Pagamento extends Model {

	private static final long serialVersionUID = 5357681761469474333L;

	@Required
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATA_PAGAMENTO", nullable=true)
	private Date dataPagamento;
	
	@Required
	@Column(name="VALOR_PAGAMENTO", nullable=true, scale=2, precision=8, unique=false)
	private BigDecimal valorPagamento;
	
	@Column(name="FORMA_PAGAMENTO", nullable=true)
	private FormaPagamento formaPagamento;
	
	@Column(name="INFORMACOES", nullable=true, length=300)
	private String informacoes;
	
	@Column(name="ERRORS", nullable=true, length=600)
	private String errors;
	
	@OneToOne(mappedBy="pagamento")
	private Pedido pedido = null;
	
	public Pagamento() {
	}
	
	public Pagamento(BigDecimal valorPagamento) {
		this.valorPagamento = valorPagamento;
	}
	
	/**
	 * @return the dataPagamento
	 */
	public Date getDataPagamento() {
		return dataPagamento;
	}

	/**
	 * @param dataPagamento the dataPagamento to set
	 */
	public void setDataPagamento(Date dataPagamento) {
		this.dataPagamento = dataPagamento;
	}

	/**
	 * @return the valorPagamento
	 */
	public BigDecimal getValorPagamento() {
		return valorPagamento;
	}

	/**
	 * @param valorPagamento the valorPagamento to set
	 */
	public void setValorPagamento(BigDecimal valorPagamento) {
		this.valorPagamento = valorPagamento;
	}

	/**
	 * @return the pedido
	 */
	public Pedido getPedido() {
		return pedido;
	}

	/**
	 * @param pedido the pedido to set
	 */
	public void setPedido(Pedido pedido) {
		this.pedido = pedido;
	}

	/**
	 * @return the formaPagamento
	 */
	public FormaPagamento getFormaPagamento() {
		return formaPagamento;
	}

	/**
	 * @param formaPagamento the formaPagamento to set
	 */
	public void setFormaPagamento(FormaPagamento formaPagamento) {
		this.formaPagamento = formaPagamento;
	}

	/**
	 * @return the informacoes
	 */
	public String getInformacoes() {
		return informacoes;
	}

	/**
	 * @param informacoes the informacoes to set
	 */
	public void setInformacoes(String informacoes) {
		this.informacoes = informacoes;
	}

	/**
	 * @return the errors
	 */
	public String getErrors() {
		return errors;
	}

	/**
	 * @param errors the errors to set
	 */
	public void setErrors(String errors) {
		this.errors = errors;
	}

}

