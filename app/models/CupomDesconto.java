/**
 * 
 */
package models;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.joda.time.DateTime;
import org.joda.time.Period;

import play.db.jpa.Model;
import play.db.jpa.Transactional;
import play.i18n.Messages;
import play.libs.Crypto;

/**
 * <p>
 * 	Essa classe tem por objetivo abstrair as propriedades e as operações ligadas ao Cupom de Desconto.
 *  Tudo pertinente ao Cupom, deve ser mapeado aqui, desde regras de negócio, persistência e relacionamentos.
 * </p>
 * @author Felipe Guerra
 * @version 1.0
 * @since 29/11/2013
 */
@Entity
@Table(name="CUPOM_DESCONTO")
public class CupomDesconto extends Model implements Serializable {

	private static final long serialVersionUID = -2326101260146636340L;
	
	public CupomDesconto() {}
	
	public CupomDesconto(Cliente cliente, Desconto desconto, String codigo, String criadoPorUsuario, Integer diasValidadeCupom, 
								Integer qtdMaxVezesUsoCupom) {
		this.cliente = cliente; 
		this.dataCadastroCupom = new Date();
		this.desconto = desconto;
		setCodigo(codigo);
		this.usuarioCriadorCupom = criadoPorUsuario;
		this.diasValidadeCupom = diasValidadeCupom;
		this.ativo = Boolean.TRUE;
		this.qtdMaxVezesUsoCupom = qtdMaxVezesUsoCupom;
		this.qtdVezesUsoCupom = Integer.valueOf(0);
		this.qtdVezesUsoCupomCliente = Integer.valueOf(0);
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATA_CADASTRO", nullable=false)
	private Date dataCadastroCupom;
	
	@ManyToOne(fetch=FetchType.EAGER)
	private Cliente cliente;

	@Column(name="CODIGO", nullable=false)
	private String codigo;

	@ManyToOne(fetch=FetchType.EAGER, cascade=CascadeType.PERSIST)
	private Desconto desconto;
	
	@Column(name="FLAG_ATIVO", nullable=false)
	private Boolean ativo;
	
	@Column(name="USUARIO_CRIADOR_CUPOM", nullable=true)
	private String usuarioCriadorCupom;
	
	@Column(name="QTD_DIAS_VALIDADE", nullable=false)
	private Integer diasValidadeCupom;
	
	@Column(name="QTD_MAXIMA_USO_CUPOM", nullable=false)
	private Integer qtdMaxVezesUsoCupom = null;
	
	@Column(name="QTD_UTILIZADA_CUPOM", nullable=false)
	private Integer qtdVezesUsoCupom = null;
	
	@Column(name="QTD_UTILIZADA_CUPOM_CLIENTE", nullable=false)
	private Integer qtdVezesUsoCupomCliente = null;
	
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
	 * @return the codigo
	 */
	public String getCodigo() {
		return codigo;
	}

	/**
	 * @param codigo the codigo to set
	 */
	public void setCodigo(String codigo) {
		StringBuffer key = new StringBuffer(Usuario.CRYPTO_KEY);
		
		this.codigo = Crypto.encryptAES(codigo, key.toString());
	}

	/**
	 * @return the desconto
	 */
	public Desconto getDesconto() {
		return desconto;
	}

	/**
	 * @param desconto the desconto to set
	 */
	public void setDesconto(Desconto desconto) {
		this.desconto = desconto;
	}
	
	@Transactional(readOnly=false)
	public static CupomDesconto gravarCodigoDescontoParaCliente(Cliente cliente, Desconto desconto, String codigo, 
																String criadoPorUsuario, Integer diasValidadeCupom, 
																Integer qtdMaxVezesUsoCupom) {
		CupomDesconto cupomDesconto = new CupomDesconto(cliente, desconto, codigo, criadoPorUsuario, diasValidadeCupom, qtdMaxVezesUsoCupom);
		desconto.getCupons().add(cupomDesconto);
		
		return cupomDesconto.save();
	}
	
	@Transactional(readOnly=false)
	public static void gravarListaDescontoClientes(List<CupomDesconto> cupons) {
		for(CupomDesconto cupomDesconto : cupons) {
			cupomDesconto.save();
		}
	}

	/**
	 * @return the dataCadastroCupom
	 */
	public Date getDataCadastroCupom() {
		return dataCadastroCupom;
	}

	/**
	 * @param dataCadastroCupom the dataCadastroCupom to set
	 */
	public void setDataCadastroCupom(Date dataCadastroCupom) {
		this.dataCadastroCupom = dataCadastroCupom;
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
	 * @return the usuarioCriadorCupom
	 */
	public String getUsuarioCriadorCupom() {
		return usuarioCriadorCupom;
	}

	/**
	 * @param usuarioCriadorCupom the usuarioCriadorCupom to set
	 */
	public void setUsuarioCriadorCupom(String usuarioCriadorCupom) {
		this.usuarioCriadorCupom = usuarioCriadorCupom;
	}

	/**
	 * @return the diasValidadeCupom
	 */
	public Integer getDiasValidadeCupom() {
		if(this.diasValidadeCupom==null)
			this.diasValidadeCupom = Integer.valueOf(0);
			
		return diasValidadeCupom;
	}

	/**
	 * @param diasValidadeCupom the diasValidadeCupom to set
	 */
	public void setDiasValidadeCupom(Integer diasValidadeCupom) {
		this.diasValidadeCupom = diasValidadeCupom;
	}

	/**
	 * @return the qtdMaxVezesUsoCupom
	 */
	public Integer getQtdMaxVezesUsoCupom() {
		if(this.qtdMaxVezesUsoCupom==null)
			this.qtdMaxVezesUsoCupom = Integer.valueOf(Messages.get("application.crm.cupom.desconto.max.time", ""));
		
		return qtdMaxVezesUsoCupom;
	}

	/**
	 * @param qtdMaxVezesUsoCupom the qtdMaxVezesUsoCupom to set
	 */
	public void setQtdMaxVezesUsoCupom(Integer qtdMaxVezesUsoCupom) {
		this.qtdMaxVezesUsoCupom = qtdMaxVezesUsoCupom;
	}
	
	private static CupomDesconto pesquisarPorCliente(Cliente cliente) {
		CupomDesconto result = null;
		
		if(cliente!=null) {
			result = CupomDesconto.find("cliente.id = ? AND ativo = ? AND qtdVezesUsoCupom > 0 " +
					"AND (qtdVezesUsoCupom < qtdMaxVezesUsoCupom AND qtdVezesUsoCupomCliente < qtdMaxVezesUsoCupom)", cliente.id, Boolean.TRUE).first();
		}
		return result;
	}
	
	public static CupomDesconto pesquisarPorCodigoCupom(String codigoCupom, Cliente cliente) {
		CupomDesconto result = null;
		CupomDesconto cupom = null;
		
		if(cliente!=null)
			cupom = pesquisarPorCliente(cliente);
			
		if(cupom==null && codigoCupom!=null)
			cupom = CupomDesconto.find("codigo = ? AND ativo = ? AND cliente.id <> ? AND (qtdVezesUsoCupomCliente < qtdMaxVezesUsoCupom AND qtdVezesUsoCupom < qtdMaxVezesUsoCupom)", codigoCupom, Boolean.TRUE,
																		cliente!=null ? cliente.id : -1).first();
		
		if(cupom!=null) {
			DateTime dataAtual = new DateTime(System.currentTimeMillis());
			DateTime dataCadastroCupom = new DateTime(cupom.getDataCadastroCupom().getTime());
			Period intervalo = new Period(dataCadastroCupom, dataAtual);
			
			if(intervalo.getDays()<=cupom.getDiasValidadeCupom())
				result = cupom;
		}
		return result;
	}
	
	public static BigDecimal calcularDescontoCarrinhoComCupom(CupomDesconto cupom, CarrinhoProduto carrinho) {
		BigDecimal result = BigDecimal.ZERO;
		
		if(cupom!=null && carrinho!=null)
			result = Pedido.calcularDesconto(carrinho.getValorTotalCompra(), cupom.getDesconto().getPorcentagem());
		
		return result;
	}
	
	@Transactional(readOnly=false)
	public static void atualizarCupomDesconto(String cupom, Cliente cliente) {
		CupomDesconto cupomDesconto = pesquisarPorCodigoCupom(cupom, cliente);
		
		if(cupomDesconto!=null) {
			if(cupomDesconto.getCliente().id.equals(cliente.id))
				cupomDesconto.setQtdVezesUsoCupomCliente(cupomDesconto.getQtdVezesUsoCupomCliente()+1);
			else
				cupomDesconto.setQtdVezesUsoCupom(cupomDesconto.getQtdVezesUsoCupom()+1);
				
			cupomDesconto.save();
		}		
		
	}
	
	public static Desconto getDescontoDoCupom(String cupom, Cliente cliente) {
		Desconto result = null;
		
		CupomDesconto cupomDesconto = pesquisarPorCodigoCupom(cupom, cliente);
			
		result = (cupomDesconto!=null && cupomDesconto.getDesconto()!=null) ? cupomDesconto.getDesconto() : null;

		return result;
	}

	/**
	 * @return the qtdVezesUsoCupom
	 */
	public Integer getQtdVezesUsoCupom() {
		if(this.qtdVezesUsoCupom==null)
			this.qtdVezesUsoCupom = Integer.valueOf(0);
			
		return qtdVezesUsoCupom;
	}

	/**
	 * @param qtdVezesUsoCupom the qtdVezesUsoCupom to set
	 */
	public void setQtdVezesUsoCupom(Integer qtdVezesUsoCupom) {
		this.qtdVezesUsoCupom = qtdVezesUsoCupom;
	}

	/**
	 * @return the qtdVezesUsoCupomCliente
	 */
	public Integer getQtdVezesUsoCupomCliente() {
		if(this.qtdVezesUsoCupomCliente==null)
			this.qtdVezesUsoCupomCliente = Integer.valueOf(0);
		
		return qtdVezesUsoCupomCliente;
	}

	/**
	 * @param qtdVezesUsoCupomCliente the qtdVezesUsoCupomCliente to set
	 */
	public void setQtdVezesUsoCupomCliente(Integer qtdVezesUsoCupomCliente) {
		this.qtdVezesUsoCupomCliente = qtdVezesUsoCupomCliente;
	}
	
}
