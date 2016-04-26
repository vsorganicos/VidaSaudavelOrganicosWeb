/**
 * 
 */
package models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Query;
import javax.persistence.QueryHint;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.JPA;
import play.db.jpa.Model;
import play.i18n.Messages;

/**
 * @author Felipe Guerra
 *
 */
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Cacheable
@Entity
@Table(name="PEDIDO")
@NamedQueries(
value={
		@NamedQuery(name="findAllOrderByDataPedidoAndCodigoEstado", 
				query="select pedido "
						+ "from Pedido as pedido "
						+ "where pedido.arquivado =:arquivado order by pedido.dataPedido desc, pedido.codigoEstadoPedido desc", 
				hints={@QueryHint(value="true", name="org.hibernate.cacheable")}),
		@NamedQuery(name="findUltimoPedidoCliente", 
				query="from Pedido where cliente.id =:idCliente AND codigoEstadoPedido IN (:status) order by dataPedido desc"),
		@NamedQuery(name="getQuantidadePedidosFinalizados", 
				query="select count(id) as quantidade, sum(valorPedido) as total from Pedido " +
						"where cliente.id =:idCliente AND codigoEstadoPedido IN (:status)",
				hints={@QueryHint(value="true", name="org.hibernate.cacheable")})		
	}
)
public class Pedido extends Model {

	private static final long serialVersionUID = -69049043535585581L;

	public enum PedidoEstado {
		ENTREGUE(1, "Entregue"),
		CANCELADO(2, "Cancelado"),
		AGUARDANDO_PAGAMENTO(3, "Aguardando Pagamento"),
		FINALIZADO(4, "Finalizado"),
		ARQUIVADO(5, "Arquivado"),
		PAGO(6, "Pago"),
		AGUARDANDO_ENTREGA(7, "Aguardando Entrega"),
		EM_ANALISE(8, "Em Analise");
		
		private Integer codigo;
		private String estado;
		
		private PedidoEstado(Integer codigo, String estado) {
			this.codigo = codigo;
			this.estado = estado;
		}

		/**
		 * @return the codigo
		 */
		public Integer getCodigo() {
			return codigo;
		}

		/**
		 * @param codigo the codigo to set
		 */
		public void setCodigo(Integer codigo) {
			this.codigo = codigo;
		}

		/**
		 * @return the estado
		 */
		public String getEstado() {
			return estado;
		}

		/**
		 * @param estado the estado to set
		 */
		public void setEstado(String estado) {
			this.estado = estado;
		}
		
		public static PedidoEstado[] getPedidoEstado(List<Integer> status) {
			List<PedidoEstado> result = null;
			
			if(status!=null && !status.isEmpty()) {
				result = new ArrayList<Pedido.PedidoEstado>();
				
				for(Integer codigo : status) {
					laco:
					for(PedidoEstado estado : PedidoEstado.values())
						if(estado.codigo.equals(codigo)) {
							result.add(estado);
							break laco;
						}
				}
			}
			return (PedidoEstado[]) result.toArray(new PedidoEstado[result.size()]);
		}
	}
	
	@Required
	@Column(name="CODIGO_ESTADO_PEDIDO", length=1, nullable=false)
	private PedidoEstado codigoEstadoPedido;
	
	@Column(name="ULTIMO_ESTADO_PEDIDO", length=1, nullable=true)
	private PedidoEstado ultimoStatusEstadoPedido;
	
	@Required
	@Column(name="VALOR_PEDIDO", nullable=false, scale=2, precision=8)
	private BigDecimal valorPedido;
	
	@Column(name="VALOR_PAGO", nullable=true, scale=2, precision=8)
	private BigDecimal valorPago;
	
	@Column(name="VALOR_PEDIDO_FINALIZADO", nullable=true, scale=2, precision=8)
	private BigDecimal valorPedidoFinalizado;
	
	/**
	 * À partir de 29/05/2012, o valor do código do pedido será o id do carrinho, 
	 * para questões de rastreabilidade. 
	 */
	@MaxSize(value=80)
	@Column(name="CODIGO_PEDIDO", nullable=true, length=80)
	private String codigoPedido;

	@Required
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATA_PEDIDO", nullable=false)
	private Date dataPedido;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATA_ENTREGA")
	private Date dataEntrega;
	
	@Column(name="OBSERVACAO", nullable=true, length=880)
	private String observacao;
	
	@Required
	@ManyToOne(fetch=FetchType.EAGER, cascade=CascadeType.REFRESH)
	private Cliente cliente;

	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="pedido")
	private List<PedidoItem> itens = null;
	
	@OneToOne(cascade=CascadeType.ALL, fetch=FetchType.LAZY, orphanRemoval=true)
	private Pagamento pagamento = null;
	
	@Required
	@ManyToOne(fetch=FetchType.EAGER, cascade=CascadeType.PERSIST)
	private Desconto desconto = null;
	
	@Column(name="USUARIO_ALTERACAO", nullable=true, length=80)
	private String usuarioAlteracao;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATA_ALTERACAO", nullable=true)
	private Date dataAlteracao;
	
	@Column(name="FLAG_PEDIDO_CESTA_ASSINATURA", nullable=true)
	private Boolean ehPedidoDeCesta;
	
	@Required(message="validation.required")
	@ManyToOne(fetch=FetchType.EAGER, cascade=CascadeType.ALL)
	private Frete frete;
	
	@Column(name="PEDIDO_ARQUIVADO", nullable=true)
	private Boolean arquivado = Boolean.FALSE;
	
	@Column(name="OUTRAS_DESPESAS", nullable=true, scale=2, precision=8)
	private BigDecimal outrasDespesas = BigDecimal.ZERO;
	
	/**
	 * @return the outrasDespesas
	 */
	public BigDecimal getOutrasDespesas() {
		return outrasDespesas;
	}

	/**
	 * @param outrasDespesas the outrasDespesas to set
	 */
	public void setOutrasDespesas(BigDecimal outrasDespesas) {
		this.outrasDespesas = outrasDespesas;
	}

	/**
	 * @return the codigoEstadoPedido
	 */
	public PedidoEstado getCodigoEstadoPedido() {
		return codigoEstadoPedido;
	}

	/**
	 * @param codigoEstadoPedido the codigoEstadoPedido to set
	 */
	public void setCodigoEstadoPedido(PedidoEstado codigoEstadoPedido) {
		this.codigoEstadoPedido = codigoEstadoPedido;
	}

	/**
	 * @return the valorPedido
	 */
	public BigDecimal getValorPedido() {
		return valorPedido;
	}

	/**
	 * @param valorPedido the valorPedido to set
	 */
	public void setValorPedido(BigDecimal valorPedido) {
		this.valorPedido = valorPedido;
	}

	/**
	 * @return the codigoPedido
	 */
	public String getCodigoPedido() {
		return codigoPedido;
	}

	/**
	 * @param codigoPedido the codigoPedido to set
	 */
	public void setCodigoPedido(String codigoPedido) {
		this.codigoPedido = codigoPedido;
	}

	/**
	 * @return the dataPedido
	 */
	public Date getDataPedido() {
		return dataPedido;
	}

	/**
	 * @param dataPedido the dataPedido to set
	 */
	public void setDataPedido(Date dataPedido) {
		this.dataPedido = dataPedido;
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
	 * @return the dataEntrega
	 */
	public Date getDataEntrega() {
		return dataEntrega;
	}

	/**
	 * @param dataEntrega the dataEntrega to set
	 */
	public void setDataEntrega(Date dataEntrega) {
		this.dataEntrega = dataEntrega;
	}

	/**
	 * @return the itens
	 */
	public List<PedidoItem> getItens() {
		if(this.itens==null)
			this.itens = new ArrayList<PedidoItem>();
		
		return itens;
	}

	/**
	 * @param itens the itens to set
	 */
	public void setItens(List<PedidoItem> itens) {
		this.itens = itens;
	}

	/**
	 * @return the pagamento
	 */
	public Pagamento getPagamento() {
		return pagamento;
	}

	/**
	 * @param pagamento the pagamento to set
	 */
	public void setPagamento(Pagamento pagamento) {
		this.pagamento = pagamento;
	}
	
	/**
	 * Adiciona os ítens com os respectivos produtos
	 * @param itens
	 */
	public void addPedidoItem(List<CarrinhoItem> itens) {
		if(itens!=null) {
			PedidoItem pedidoItem = null;
			
			for(CarrinhoItem item : itens) {
				pedidoItem = new PedidoItem(this, item.getQuantidade(), item.getProdutos());
				
				getItens().add(pedidoItem);
			}
		}
	}
	
	public void addCesta(List<CestaPronta> cestas) {
		if(cestas!=null && !cestas.isEmpty()) {
			for(CestaPronta cesta : cestas) {
				this.getItens().addAll( PedidoItem.buildListPedidoItem(cesta.getProdutosAtivos(), this) );
			}
		}
	}
	
	/**
	 * @return the desconto
	 */
	public Desconto getDesconto() {
		if(desconto==null)
			this.desconto = new Desconto();
		
		return desconto;
	}

	/**
	 * @param desconto the desconto to set
	 */
	public void setDesconto(Desconto desconto) {
		this.desconto = desconto;
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
	 * Calcula o valor do desconto. 
	 * Se o valor for preenchido, o mesmo terá prioridade sobre a porcentagem, caso contrário, é calculado através da porcentagem.
	 */
	@Transient
	public void calcularDesconto() {
		if(getDesconto().getValorDesconto().doubleValue()>0) {
			this.getDesconto().setPorcentagem( new BigDecimal(getDesconto().getValorDesconto().doubleValue() / this.getValorPedido().doubleValue()).multiply(Desconto.CEM_PORCENTO).setScale(2, BigDecimal.ROUND_HALF_UP));
			
		}else if(this.getDesconto().getPorcentagem().doubleValue()>0) {
			this.getDesconto().setValorDesconto( this.desconto.getPorcentagem().divide(Desconto.CEM_PORCENTO).multiply(this.valorPedido).setScale(2, BigDecimal.ROUND_HALF_UP) );
		}
	}
	
	public BigDecimal getValorDesconto() {
		if(this.desconto==null)
			return BigDecimal.ZERO;
		
		return getDesconto().getPorcentagem().divide(Desconto.CEM_PORCENTO).multiply(getValorPedido()).setScale(2, BigDecimal.ROUND_HALF_DOWN);
	}
	
	/**
	 * Calcula o valor do pedido aplicando um desconto. 
	 */
	@Transient
	public BigDecimal calcularDesconto(BigDecimal porcentagem) {
		return this.valorPedido.subtract( porcentagem.divide(Desconto.CEM_PORCENTO).multiply(this.valorPedido) ).setScale(2, BigDecimal.ROUND_HALF_DOWN);
	}
	
	@Transient
	public static BigDecimal calcularDesconto(BigDecimal valorPedido, BigDecimal porcentagem) {
		return valorPedido.subtract( porcentagem.divide(Desconto.CEM_PORCENTO).multiply(valorPedido) ).setScale(2, BigDecimal.ROUND_HALF_DOWN);
	}
	
	@Transient
	public BigDecimal getValorComDesconto() {
		if(getValorDesconto().doubleValue()>0)
			return this.valorPedido.subtract( getValorDesconto() ).setScale(2, BigDecimal.ROUND_HALF_DOWN);
		else
			return this.valorPedido.subtract( getDesconto().getPorcentagem().divide(Desconto.CEM_PORCENTO).multiply(this.valorPedido) ).setScale(2, BigDecimal.ROUND_HALF_DOWN);
	}
	
	@Transient
	public static BigDecimal calcularValorTotalPedidos(List<Pedido> pedidos) {
		BigDecimal result = null;
		
		if(pedidos!=null && !pedidos.isEmpty()) {
			result = BigDecimal.ZERO;
			
			for(Pedido pedido : pedidos)
				result = result.add(pedido.getValorPedido());
		}
		return result;
	}
	
	@Transient
	public static BigDecimal getDebitosCreditosTodosPedidosCliente(Long idCliente) {
		BigDecimal result = BigDecimal.ZERO;
		List<Pedido> pedidos = Pedido.find("cliente.id = ? AND valorPago IS NOT NULL", idCliente).fetch();
		
		for(Pedido pedido : pedidos) {
			result = result.add(pedido.getValorPago().subtract(pedido.getValorTotal()));
		}
		return result;
	}
	
	@Transient
	public static Pedido findUltimoPedidoCliente(Long idCliente) {
		Query query = JPA.em().createNamedQuery("findUltimoPedidoCliente");
		query.setParameter("idCliente", idCliente);
		query.setParameter("status", Pedido.PedidoEstado.FINALIZADO);
		
		List<Pedido> pedidos = query.getResultList();
		
		return (pedidos==null || pedidos.isEmpty()) ? null : pedidos.get(0);
	}

	@Transient
	public Map<Long, BigDecimal> getQuantidadePedidosFinalizados() {
		Map<Long, BigDecimal> result = null;
		
		Query query = JPA.em().createNamedQuery("getQuantidadePedidosFinalizados");
		query.setParameter("idCliente", cliente.id);
		query.setParameter("status", Pedido.PedidoEstado.FINALIZADO);
		
		Object[] pedidos = (Object[])query.getSingleResult();
		
		if(pedidos!=null) {
			result = new HashMap<Long, BigDecimal>();
			result.put((Long)pedidos[0], (BigDecimal) pedidos[1]);
		}
		return result;
	}
	
	/**
	 * @return the dataAlteracao
	 */
	public Date getDataAlteracao() {
		return dataAlteracao;
	}

	/**
	 * @param dataAlteracao the dataAlteracao to set
	 */
	public void setDataAlteracao(Date dataAlteracao) {
		this.dataAlteracao = dataAlteracao;
	}

	/**
	 * @return the ultimoStatusEstadoPedido
	 */
	public PedidoEstado getUltimoStatusEstadoPedido() {
		return ultimoStatusEstadoPedido;
	}

	/**
	 * @param ultimoStatusEstadoPedido the ultimoStatusEstadoPedido to set
	 */
	public void setUltimoStatusEstadoPedido(PedidoEstado ultimoStatusEstadoPedido) {
		this.ultimoStatusEstadoPedido = ultimoStatusEstadoPedido;
	}
	
	@Transient
	public Boolean podeAlterarMetodoPagamento() {
		Boolean result = Boolean.FALSE;
		
		if(!this.codigoEstadoPedido.equals(PedidoEstado.ENTREGUE) 
				&& !this.codigoEstadoPedido.equals(PedidoEstado.FINALIZADO)
				&& !this.codigoEstadoPedido.equals(PedidoEstado.CANCELADO)) {
			result = Boolean.TRUE;
		}
		return result;
	}
	
	public static BigDecimal calcularFrete(BigDecimal valorTotalCompra, Boolean estaNaCapital) {
		BigDecimal result = new BigDecimal(0);
		
		Double valorCompraSemFrete = Double.valueOf(Messages.get("application.frete.valor", ""));
		
		if(valorCompraSemFrete!=null && valorTotalCompra.doubleValue()<valorCompraSemFrete) {
			if(estaNaCapital) {
				Frete frete = Frete.findById(1L);
				result = frete.getValor();
			}else {
				result = new BigDecimal(Double.valueOf(Messages.get("application.frete.interior.valor", "")));
			}
		}
		return result;
	}
	
	public void addFrete() {
		this.valorPedido = this.getValorPedido().add(calcularFrete(this.getValorPedido(), this.cliente.estaNaCapital())).setScale(2, BigDecimal.ROUND_HALF_UP);
	}
	
	/**
	 * @return the ehPedidoDeCesta
	 */
	public Boolean getEhPedidoDeCesta() {
		if(ehPedidoDeCesta==null)
			ehPedidoDeCesta = Boolean.FALSE;
		
		return ehPedidoDeCesta;
	}

	/**
	 * @param ehPedidoDeCesta the ehPedidoDeCesta to set
	 */
	public void setEhPedidoDeCesta(Boolean ehPedidoDeCesta) {
		this.ehPedidoDeCesta = ehPedidoDeCesta;
	}

	/**
	 * @return the frete
	 */
	public Frete getFrete() {
		return frete;
	}

	/**
	 * @param frete the frete to set
	 */
	public void setFrete(Frete frete) {
		this.frete = frete;
	}
	
	public BigDecimal getValorTotal() {
		BigDecimal valorFrete = (this.getFrete()==null || this.getFrete().getValor()==null) ? BigDecimal.ZERO : this.getFrete().getValor();
		
		return valorFrete.add(getValorComDesconto());
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime
				* result
				+ ((codigoEstadoPedido == null) ? 0 : codigoEstadoPedido
						.hashCode());
		result = prime * result
				+ ((codigoPedido == null) ? 0 : codigoPedido.hashCode());
		result = prime * result
				+ ((dataPedido == null) ? 0 : dataPedido.hashCode());
		result = prime * result
				+ ((observacao == null) ? 0 : observacao.hashCode());
		result = prime * result
				+ ((valorPedido == null) ? 0 : valorPedido.hashCode());
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
		if (!(obj instanceof Pedido)) {
			return false;
		}
		Pedido other = (Pedido) obj;
		
		if (!this.id.equals(other.id)) {
			return false;
		}
		if (codigoEstadoPedido != other.codigoEstadoPedido) {
			return false;
		}
		if (codigoPedido == null) {
			if (other.codigoPedido != null) {
				return false;
			}
		} else if (!codigoPedido.equals(other.codigoPedido)) {
			return false;
		}
		if (dataPedido == null) {
			if (other.dataPedido != null) {
				return false;
			}
		} else if (!dataPedido.equals(other.dataPedido)) {
			return false;
		}
		if (observacao == null) {
			if (other.observacao != null) {
				return false;
			}
		} else if (!observacao.equals(other.observacao)) {
			return false;
		}
		if (valorPedido == null) {
			if (other.valorPedido != null) {
				return false;
			}
		} else if (!valorPedido.equals(other.valorPedido)) {
			return false;
		}
		return true;
	}

	/**
	 * @return the arquivado
	 */
	public Boolean getArquivado() {
		return arquivado;
	}

	/**
	 * @param arquivado the arquivado to set
	 */
	public void setArquivado(Boolean arquivado) {
		this.arquivado = arquivado;
	}
	
	public BigDecimal getValorCustoPedido() {
		BigDecimal result = BigDecimal.ZERO;
		
		for(PedidoItem item : this.itens) {
			if(!item.getExcluido())
				result = result.add(Produto.getValorCustoProdutos(item.getProdutos()));
		}
		return result;
	}

	/**
	 * @return the usuarioAlteracao
	 */
	public String getUsuarioAlteracao() {
		return usuarioAlteracao;
	}

	/**
	 * @param usuarioAlteracao the usuarioAlteracao to set
	 */
	public void setUsuarioAlteracao(String usuarioAlteracao) {
		this.usuarioAlteracao = usuarioAlteracao;
	}

	/**
	 * @return the valorPago
	 */
	public BigDecimal getValorPago() {
		return valorPago;
	}

	/**
	 * @param valorPago the valorPago to set
	 */
	public void setValorPago(BigDecimal valorPago) {
		this.valorPago = valorPago;
	}
	
	public BigDecimal getSaldoPedido() {
		BigDecimal saldo = BigDecimal.ZERO;
		
		if(this.getValorPago()!=null) {
			saldo = this.getValorPago().subtract(this.getValorTotal());
		}
			
		return saldo;
	}
	
	/**
	 * <p>
	 * 	M�todo respons�vel por calcular o dia aproximado da entrega.
	 *  A regra atual �: pedidos fechados at� Domingo, 23hs, ser�o entregues na pr�xima Ter�a-feira.
	 *  Para pedidos fechados entre Domingo, 23hs, e Te�a-feira, 22hs, a entrega ser� realizada na Quinta-feira seguinte.
	 * </p>
	 * @return
	 */
	public Date getDataAproximadaEntrega() {
		Calendar dataAproximadaEntrega = null;
		
		dataAproximadaEntrega = Calendar.getInstance();
		
		switch(dataAproximadaEntrega.get(Calendar.DAY_OF_WEEK)) {
			case Calendar.MONDAY:
				dataAproximadaEntrega.add(Calendar.DAY_OF_MONTH, 3);
				break;
			case Calendar.WEDNESDAY:
				dataAproximadaEntrega.add(Calendar.DAY_OF_MONTH, 6);
				break;
			case Calendar.THURSDAY:
				dataAproximadaEntrega.add(Calendar.DAY_OF_MONTH, 5);
				break;
			case Calendar.FRIDAY:
				dataAproximadaEntrega.add(Calendar.DAY_OF_MONTH, 4);
				break;
			case Calendar.SATURDAY:
				dataAproximadaEntrega.add(Calendar.DAY_OF_MONTH, 3);
				break;
			case Calendar.TUESDAY:
				if(dataAproximadaEntrega.get(Calendar.HOUR_OF_DAY)>=22 && dataAproximadaEntrega.get(Calendar.MINUTE)>0) {
					dataAproximadaEntrega.add(Calendar.DAY_OF_MONTH, 7);
				}else {
					dataAproximadaEntrega.add(Calendar.DAY_OF_MONTH, 2);
				}
				break;
			case Calendar.SUNDAY:
				if(dataAproximadaEntrega.get(Calendar.HOUR_OF_DAY)>=23 && dataAproximadaEntrega.get(Calendar.MINUTE)>0) {
					dataAproximadaEntrega.add(Calendar.DAY_OF_MONTH, 4);
				}else {
					dataAproximadaEntrega.add(Calendar.DAY_OF_MONTH, 2);
				}
				break;
		}
		return dataAproximadaEntrega.getTime();
	}
	
	public static PedidoEstado setPedidoEstado() {
		Calendar dataEntrega = Calendar.getInstance();
		PedidoEstado result = PedidoEstado.AGUARDANDO_ENTREGA;
		
		switch(dataEntrega.get(Calendar.DAY_OF_WEEK)) {
			case Calendar.TUESDAY:
				if(dataEntrega.get(Calendar.HOUR_OF_DAY)==22 && dataEntrega.get(Calendar.MINUTE)>0) {
					result = PedidoEstado.EM_ANALISE;
				}
				break;
				
			case Calendar.SUNDAY:
				if(dataEntrega.get(Calendar.HOUR_OF_DAY)==23 && dataEntrega.get(Calendar.MINUTE)>0) {
					result = PedidoEstado.EM_ANALISE;
				}
				break;
				
			case Calendar.MONDAY:
				result = PedidoEstado.EM_ANALISE;
				break;	
			
			case Calendar.THURSDAY:
				result = PedidoEstado.EM_ANALISE;
				break;
		}
			
		return result;
	}

	/**
	 * @return the valorPagoFinalizado
	 */
	public BigDecimal getValorPedidoFinalizado() {
		return valorPedidoFinalizado;
	}

	/**
	 * @param valorPagoFinalizado the valorPagoFinalizado to set
	 */
	public void setValorPedidoFinalizado(BigDecimal valorPedidoFinalizado) {
		this.valorPedidoFinalizado = valorPedidoFinalizado;
	}
	
}
