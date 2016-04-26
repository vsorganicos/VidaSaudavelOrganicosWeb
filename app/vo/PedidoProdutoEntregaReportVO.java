package vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import util.PedidoFornecedorVORefrigeracaoComparator;

import models.Pedido;
import controllers.Pedidos;
import controllers.Relatorios;

public class PedidoProdutoEntregaReportVO implements Serializable, Comparable<PedidoProdutoEntregaReportVO> {

	private static final long serialVersionUID = -2916677616049482213L;
	
	private Long id;
	private String nome;
	private String logradouro;
	private String complemento;
	private String bairro;
	private String cidade;
	private String cep;
	private String uf;
	private Integer numero;
	private Date dataPedido;
	private String prefixoTelefone;
	private String telefone;
	private String prefixoCelular;
	private String telefoneCelular;
	private BigDecimal valorPedidoHistorico = BigDecimal.ZERO;
	private BigDecimal valorPedidoHistoricoPago = BigDecimal.ZERO;
	private BigDecimal valorPago = BigDecimal.ZERO;
	private String statusPedido;
	private String formaPagamento;
	private String observacao;
	private Date dataCadastroCliente;
	
	private List<ProdutoPedidoReportVO> produtos;
	
	private List<PedidoProdutoEntregaReportVO> ultimosPedidos;
	
	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}



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
	 * @return the logradouro
	 */
	public String getLogradouro() {
		return logradouro;
	}



	/**
	 * @param logradouro the logradouro to set
	 */
	public void setLogradouro(String logradouro) {
		this.logradouro = logradouro;
	}



	/**
	 * @return the complemento
	 */
	public String getComplemento() {
		return complemento;
	}



	/**
	 * @param complemento the complemento to set
	 */
	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}



	/**
	 * @return the bairro
	 */
	public String getBairro() {
		return bairro;
	}



	/**
	 * @param bairro the bairro to set
	 */
	public void setBairro(String bairro) {
		this.bairro = bairro;
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
	 * @return the cep
	 */
	public String getCep() {
		return cep;
	}



	/**
	 * @param cep the cep to set
	 */
	public void setCep(String cep) {
		this.cep = cep;
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
	 * @return the numero
	 */
	public Integer getNumero() {
		return numero;
	}



	/**
	 * @param numero the numero to set
	 */
	public void setNumero(Integer numero) {
		this.numero = numero;
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
	 * @return the pedidos
	 */
	public List<ProdutoPedidoReportVO> getProdutos() {
		if(this.produtos==null)
			this.produtos = new ArrayList<ProdutoPedidoReportVO>();
		
		return produtos;
	}

	/**
	 * @param pedidos the pedidos to set
	 */
	public void setProdutos(List<ProdutoPedidoReportVO> produtos) {
		this.produtos = produtos;
	}

	@Override
	public int compareTo(PedidoProdutoEntregaReportVO o) {
		if(o.id==null)
			return 0;
		else
			return this.id.compareTo(o.id);
	}
	
	public static List<PedidoProdutoEntregaReportVO> fillListReport(List<Pedido> pedidos) {
		List<PedidoProdutoEntregaReportVO> result = new ArrayList<PedidoProdutoEntregaReportVO>();
		PedidoProdutoEntregaReportVO entity = null;
		
		if(pedidos!=null && !pedidos.isEmpty()) {
			for(Pedido pedido : pedidos) {
				entity = new PedidoProdutoEntregaReportVO();
				
				entity.setId(pedido.id);
				entity.setDataPedido(pedido.getDataPedido());
				entity.setNome(pedido.getCliente().getNome());
				entity.setLogradouro(pedido.getCliente().getEnderecos().get(0).getLogradouro());
				entity.setNumero(pedido.getCliente().getEnderecos().get(0).getNumero());
				entity.setComplemento(pedido.getCliente().getEnderecos().get(0).getComplemento());
				entity.setBairro(pedido.getCliente().getEnderecos().get(0).getBairro());
				entity.setCidade(pedido.getCliente().getEnderecos().get(0).getCidade());
				entity.setUf(pedido.getCliente().getEnderecos().get(0).getUf());
				entity.setCep(pedido.getCliente().getEnderecos().get(0).getCep());
				entity.setPrefixoTelefone(pedido.getCliente().getTelefones().get(0).getPrefixo());
				entity.setTelefone(pedido.getCliente().getTelefones().get(0).getNumero());
				entity.setFormaPagamento(pedido.getPagamento().getFormaPagamento().getDescricao());
				entity.setDataCadastroCliente(pedido.getCliente().getDataCadastro());
				
				//2º telefone
				if(pedido.getCliente().getTelefones().size()>1) {
					entity.setPrefixoCelular(pedido.getCliente().getTelefones().get(1).getPrefixo());
					entity.setTelefoneCelular(pedido.getCliente().getTelefones().get(1).getNumero());
					
				}else {
					entity.setPrefixoCelular("");
					entity.setTelefoneCelular("");
				}

				entity.setObservacao(pedido.getObservacao());
				entity.setProdutos( ProdutoPedidoReportVO.fillProdutos(Relatorios.findProdutosAguardandoEntrega(pedido.id), 
																	pedido.getValorPedido(), 
																	pedido.getValorDesconto(),
																	pedido.getObservacao(),
																	pedido.getFrete()==null ? BigDecimal.ZERO : pedido.getFrete().getValor(),
																	pedido.getOutrasDespesas()) );
				
				Collections.sort(entity.getProdutos(), new PedidoFornecedorVORefrigeracaoComparator());
				
				entity.setUltimosPedidos(fillPedidosHistorico(Pedidos.getPedidosAbertosEFinalizados(pedido.getCliente().id, 4)));

				result.add(entity);
			}
		}
		return result;
	}
	
	public static List<PedidoProdutoEntregaReportVO> fillPedidosHistorico(List<Pedido> pedidosHistorico) {
		List<PedidoProdutoEntregaReportVO> result = new ArrayList<PedidoProdutoEntregaReportVO>();
		
		if(pedidosHistorico!=null && !pedidosHistorico.isEmpty()) {
			PedidoProdutoEntregaReportVO pedidoVO = null;
			
			for(Pedido _pedido : pedidosHistorico) {
				pedidoVO = new PedidoProdutoEntregaReportVO();
				
				pedidoVO.setId(_pedido.id);
				pedidoVO.setDataPedido(_pedido.getDataPedido());
				pedidoVO.setValorPedidoHistorico(_pedido.getValorTotal());
				pedidoVO.setStatusPedido(_pedido.getCodigoEstadoPedido().getEstado());
				pedidoVO.setValorPago(_pedido.getValorPago()==null ? _pedido.getValorTotal() : _pedido.getValorPago());
				
				switch (_pedido.getCodigoEstadoPedido()) {
					case AGUARDANDO_PAGAMENTO:
						pedidoVO.setValorPedidoHistoricoPago(_pedido.getValorTotal().negate());
						break;
					case FINALIZADO:
						pedidoVO.setValorPedidoHistoricoPago(_pedido.getValorPago()!=null ? _pedido.getValorPago().subtract(_pedido.getValorTotal()) : BigDecimal.ZERO);
						break;
					default:
						break;
				}
				result.add(pedidoVO);
			}
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof PedidoProdutoEntregaReportVO))
			return false;
		final PedidoProdutoEntregaReportVO other = (PedidoProdutoEntregaReportVO) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	/**
	 * @return the prefixoTelefone
	 */
	public String getPrefixoTelefone() {
		return prefixoTelefone;
	}

	/**
	 * @param prefixoTelefone the prefixoTelefone to set
	 */
	public void setPrefixoTelefone(String prefixoTelefone) {
		this.prefixoTelefone = prefixoTelefone;
	}

	/**
	 * @return the telefone
	 */
	public String getTelefone() {
		return telefone;
	}

	/**
	 * @param telefone the telefone to set
	 */
	public void setTelefone(String telefone) {
		this.telefone = telefone;
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
	 * @return the telefoneCelular
	 */
	public String getTelefoneCelular() {
		return telefoneCelular;
	}

	/**
	 * @param telefoneCelular the telefoneCelular to set
	 */
	public void setTelefoneCelular(String telefoneCelular) {
		this.telefoneCelular = telefoneCelular;
	}

	/**
	 * @return the prefixoCelular
	 */
	public String getPrefixoCelular() {
		return prefixoCelular;
	}

	/**
	 * @param prefixoCelular the prefixoCelular to set
	 */
	public void setPrefixoCelular(String prefixoCelular) {
		this.prefixoCelular = prefixoCelular;
	}

	/**
	 * @return the ultimosPedidos
	 */
	public List<PedidoProdutoEntregaReportVO> getUltimosPedidos() {
		if(ultimosPedidos==null)
			ultimosPedidos = new ArrayList<PedidoProdutoEntregaReportVO>();
		
		return ultimosPedidos;
	}

	/**
	 * @param ultimosPedidos the ultimosPedidos to set
	 */
	public void setUltimosPedidos(List<PedidoProdutoEntregaReportVO> ultimosPedidos) {
		this.ultimosPedidos = ultimosPedidos;
	}

	/**
	 * @return the valorPedidoHistorico
	 */
	public BigDecimal getValorPedidoHistorico() {
		return valorPedidoHistorico.setScale(2, BigDecimal.ROUND_HALF_UP);
	}

	/**
	 * @param valorPedidoHistorico the valorPedidoHistorico to set
	 */
	public void setValorPedidoHistorico(BigDecimal valorPedidoHistorico) {
		this.valorPedidoHistorico = valorPedidoHistorico;
	}

	/**
	 * @return the valorPedidoHistoricoPago
	 */
	public BigDecimal getValorPedidoHistoricoPago() {
		return valorPedidoHistoricoPago.setScale(2, BigDecimal.ROUND_HALF_UP);
	}

	/**
	 * @param valorPedidoHistoricoPago the valorPedidoHistoricoPago to set
	 */
	public void setValorPedidoHistoricoPago(BigDecimal valorPedidoHistoricoPago) {
		this.valorPedidoHistoricoPago = valorPedidoHistoricoPago;
	}

	/**
	 * @return the statusPedido
	 */
	public String getStatusPedido() {
		return statusPedido;
	}

	/**
	 * @param statusPedido the statusPedido to set
	 */
	public void setStatusPedido(String statusPedido) {
		this.statusPedido = statusPedido;
	}

	/**
	 * @return the formaPagamento
	 */
	public String getFormaPagamento() {
		return formaPagamento;
	}

	/**
	 * @param formaPagamento the formaPagamento to set
	 */
	public void setFormaPagamento(String formaPagamento) {
		this.formaPagamento = formaPagamento;
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

	/**
	 * @return the dataCadastroCliente
	 */
	public Date getDataCadastroCliente() {
		return dataCadastroCliente;
	}

	/**
	 * @param dataCadastroCliente the dataCadastroCliente to set
	 */
	public void setDataCadastroCliente(Date dataCadastroCliente) {
		this.dataCadastroCliente = dataCadastroCliente;
	}
}
