/**
 * 
 */
package models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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
import javax.persistence.Transient;

import play.data.validation.Required;
import play.db.jpa.Model;

/**
 * <p>Alterada para suportar o novo modelo de cestas prontas. </p>
 * @author Felipe G. de Oliveira
 * @version 1.2
 */
@Entity
@Table(name="CESTA_PRONTA")
public class CestaPronta extends Model implements ProdutoCarrinho {

	private static final long serialVersionUID = 198765234567889L;
	
	@Required(message="Por favor, preencha o título")
	@Column(name="TITULO_CESTA", nullable=false, length=300)
	private String titulo;
	
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER, mappedBy="cesta", orphanRemoval=true)
	private List<CestaProduto> produtos;
	
	@Required(message="Por favor, preencha a data de entrega da cesta")
	@Column(name="DATA_PROXIMA_ENTREGA", nullable=false)
	@Temporal(TemporalType.DATE)
	private Date dataProximaEntrega;
	
	@Column(name="DATA_ALTERACAO", nullable=true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataAlteracao;

	@Column(name="FLAG_ATIVO", nullable=false)
	private Boolean ativo;
	
	@Required(message="message.required.product.imagem")
	@Column(name="CAMINHO_IMAGEM", nullable=true)
	private String caminhoImagem = "";
	
	@Column(name="OBSERVACAO", length=800, nullable=true)
	private String observacao;
	
	@Required(message="message.required.product.valorVenda")
	@Column(name="VALOR_VENDA", nullable=true, scale=2, precision=8)
	private BigDecimal valorVenda;
	
	@ManyToMany(fetch=FetchType.LAZY)
	private List<CarrinhoProduto> carrinhos;
	
	@Transient
	private BigDecimal valorCustoCesta = BigDecimal.ZERO;
	
	/**
	 * @return the valorCustoCesta
	 */
	public BigDecimal getValorCustoCesta() {
		List<Produto> prods = null; 
		
		for(CestaProduto cestaProduto : this.getProdutosAtivos()) {
			prods = new ArrayList<Produto>(1);
			prods.add(cestaProduto.getProduto());
			valorCustoCesta = valorCustoCesta.add(Produto.getValorCustoProdutos( prods ));
		}
		
		return valorCustoCesta;
	}

	/**
	 * @param valorCustoCesta the valorCustoCesta to set
	 */
	public void setValorCustoCesta(BigDecimal valorCustoCesta) {
		this.valorCustoCesta = valorCustoCesta;
	}

	public Date getDataProximaEntrega() {
		return dataProximaEntrega;
	}
	
	public Integer getSemanaEntrega() {
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(dataProximaEntrega);
		
		return calendar.get(Calendar.WEEK_OF_YEAR);
	}

	public void setDataProximaEntrega(Date dataProximaEntrega) {
		this.dataProximaEntrega = dataProximaEntrega;
	}

	public List<CestaProduto> getProdutosAtivos() {
		if(this.produtos==null)
			this.produtos = new ArrayList<CestaProduto>();
			
		return produtos;
	}
	

	public void setProdutos(List<CestaProduto> produtosAtivos) {
		this.produtos = produtosAtivos;
	}

	public Date getDataAlteracao() {
		return dataAlteracao;
	}

	public void setDataAlteracao(Date dataAlteracao) {
		this.dataAlteracao = dataAlteracao;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	/**
	 * @return the caminhoImagem
	 */
	public String getCaminhoImagem() {
		return caminhoImagem;
	}

	/**
	 * @param caminhoImagem the caminhoImagem to set
	 */
	public void setCaminhoImagem(String caminhoImagem) {
		this.caminhoImagem = caminhoImagem;
	}

	/**
	 * @return the observacao
	 */
	public String getObservacao() {
		return observacao;
	}

	/**
	 * @param observacao the observacao to set
	 */
	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	/**
	 * @return the valorVenda
	 */
	public BigDecimal getValorVenda() {
		return valorVenda;
	}

	/**
	 * @param valorVenda the valorVenda to set
	 */
	public void setValorVenda(BigDecimal valorVenda) {
		this.valorVenda = valorVenda;
	}

	/**
	 * @return the carrinhos
	 */
	public List<CarrinhoProduto> getCarrinhos() {
		if(carrinhos==null)
			this.carrinhos = new ArrayList<CarrinhoProduto>();
		
		return carrinhos;
	}

	/**
	 * @param carrinhos the carrinhos to set
	 */
	public void setCarrinhos(List<CarrinhoProduto> carrinhos) {
		this.carrinhos = carrinhos;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof CestaPronta)) {
			return false;
		}
		CestaPronta other = (CestaPronta) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}
	
}
