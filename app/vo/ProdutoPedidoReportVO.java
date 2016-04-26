/**
 * 
 */
package vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import models.PedidoItem;
import models.Produto;
import business.estoque.EstoqueControl;

/**
 * @author Felipe Guerra
 * @version 1.0
 */
public class ProdutoPedidoReportVO implements Serializable, Comparable<ProdutoPedidoReportVO> {

	private static final long serialVersionUID = -3980579539977698958L;
	
	private Long id;
	private String descricao;
	private BigDecimal valor;
	private Integer quantidade;
	private BigDecimal valorPedido;
	private String fornecedor;
	private String codigoProduto;
	private BigDecimal desconto = BigDecimal.ZERO;
	private BigDecimal frete = BigDecimal.ZERO;
	private BigDecimal creditoDebito = BigDecimal.ZERO;
	
	//produtos refrigerados
	private Boolean ehRefrigerado = Boolean.FALSE;
	
	private String observacao;

	public ProdutoPedidoReportVO() {
	}
	
	public ProdutoPedidoReportVO(Long id) {
		this.id = id;
	}
	
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
	 * @return the descricao
	 */
	public String getDescricao() {
		return descricao;
	}



	/**
	 * @param descricao the descricao to set
	 */
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}



	/**
	 * @return the valor
	 */
	public BigDecimal getValor() {
		return valor.setScale(2, BigDecimal.ROUND_HALF_UP);
	}



	/**
	 * @param valor the valor to set
	 */
	public void setValor(BigDecimal valor) {
		this.valor = valor;	
	}



	/**
	 * @return the quantidade
	 */
	public Integer getQuantidade() {
		return quantidade;
	}


	/**
	 * @return the valorPedido
	 */
	public BigDecimal getValorPedido() {
		return valorPedido.subtract(this.desconto).setScale(2, BigDecimal.ROUND_HALF_UP);
	}

	/**
	 * @param valorPedido the valorPedido to set
	 */
	public void setValorPedido(BigDecimal valorPedido) {
		this.valorPedido = valorPedido;
	}

	/**
	 * @param quantidade the quantidade to set
	 */
	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}
	
	public String getFornecedor() {
		return fornecedor;
	}
	
	public void setFornecedor(String fornecedor) {
		this.fornecedor = fornecedor;
	}
	
	public BigDecimal getSubTotal() {
		if(this.valor!=null && this.quantidade!=null)
			return this.valor.multiply(new BigDecimal(this.quantidade)).setScale(2, BigDecimal.ROUND_HALF_UP);
		else
			return new BigDecimal(0).setScale(2, BigDecimal.ROUND_HALF_UP);
	}
	
	public void addQuantidade(Integer quantidade) {
		if(this.quantidade!=null && quantidade!=null)
			this.quantidade += quantidade;
	}
	
	/**
	 * @return the codigoProduto
	 */
	public String getCodigoProduto() {
		return codigoProduto;
	}

	/**
	 * @param codigoProduto the codigoProduto to set
	 */
	public void setCodigoProduto(String codigoProduto) {
		this.codigoProduto = codigoProduto;
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
	 * @param pedidos
	 * @return valor total dos pedidos
	 */
	public static BigDecimal calcularValorTotalPedidos(List<ProdutoPedidoReportVO> pedidos) {
		BigDecimal result = null;
		
		if(pedidos!=null && !pedidos.isEmpty()) {
			result = new BigDecimal(0.0d);
			
			for(ProdutoPedidoReportVO pedido : pedidos)
				result = result.add( pedido.getValorPedido() );
			
		}
		return result;
	}

	/**
	 * Método utilitário para pesquisar um objeto numa determinada lista
	 * @param list
	 * @param idProduto
	 * @return ProdutoPedidoReportVO
	 */
	public static ProdutoPedidoReportVO findProdutoPedidoReportByIdProduto(List<ProdutoPedidoReportVO> list, List<Produto> produtos) {
		ProdutoPedidoReportVO result = null;
		
		if(list!=null && !list.isEmpty() && produtos!=null) {
			laco_raiz:
			for(ProdutoPedidoReportVO entity : list) {
				for(Produto _produto : produtos) {
					if(entity.getId().equals(_produto.id)) {
						result = entity;
						break laco_raiz;
					}
				}
			}
		}
		return result;
	}
	
	public static List<ProdutoPedidoReportVO> fillProdutos(Collection<Produto> produtos, BigDecimal valorPedido, 
														BigDecimal desconto, 
														String observacao,
														BigDecimal frete,
														BigDecimal creditoDebito) {
		List<ProdutoPedidoReportVO> result = new ArrayList<ProdutoPedidoReportVO>();
		ProdutoPedidoReportVO entity = null;
		Long idAnterior = null;
		
		if(produtos!=null && !produtos.isEmpty()) {
			for(Produto produto : produtos) {
				if(produto.id.equals(idAnterior)) {
					entity.addQuantidade(1);
					
				}else {
					entity = new ProdutoPedidoReportVO();
				
					entity.setCodigoProduto(produto.getCodigoProduto());
					entity.setValorPedido(valorPedido.add(frete));
					entity.setId(produto.id);
					entity.setQuantidade(1);
					entity.setDescricao(produto.getDescricao());
					entity.setFornecedor(produto.getFornecedor().getNome());
					entity.setValor(new BigDecimal(produto.getValorVenda()));
					entity.setDesconto(desconto);
					entity.setCreditoDebito(creditoDebito);
					entity.setFrete(frete);
					
					//produto refrigera��o
					entity.setEhRefrigerado(produto.getEhRefrigerado());
					
					//Observação
					entity.setObservacao(observacao);
					
					result.add(entity);
				}
				idAnterior = produto.id;
			}
		}		
		return result;
	}
	
	public static List<ProdutoPedidoReportVO> fillProdutos(List<PedidoItem> produtos, BigDecimal valorPedido, BigDecimal desconto) {
		List<ProdutoPedidoReportVO> result = new ArrayList<ProdutoPedidoReportVO>();
		ProdutoPedidoReportVO entity = null;
		
		if(produtos!=null && !produtos.isEmpty()) {
			for(PedidoItem produto : produtos) {
				entity = new ProdutoPedidoReportVO();
				
				entity.setCodigoProduto(produto.getProdutos().get(0).getCodigoProduto());
				entity.setValorPedido(valorPedido);
				entity.setId(produto.getProdutos().get(0).id);
				entity.setQuantidade(produto.getQuantidade());
				entity.setDescricao(produto.getProdutos().get(0).getDescricao());
				
				if(produto.getProdutos().get(0).getFornecedor()!=null)
					entity.setFornecedor(produto.getProdutos().get(0).getFornecedor().getNome());
				
				entity.setValor(new BigDecimal(produto.getProdutos().get(0).getValorVenda()));
				entity.setDesconto(desconto);
				
				//Observação
				entity.setObservacao(produto.getPedido().getObservacao());
				
				result.add(entity);
			}
		}		
		return result;
	}
	
	public static List<ProdutoPedidoReportVO> fillProdutos(List<Produto> produtos) {
		List<ProdutoPedidoReportVO> result = new ArrayList<ProdutoPedidoReportVO>();
		ProdutoPedidoReportVO entity = null;
		Long idAnterior = null;
		
		if(produtos!=null && !produtos.isEmpty()) {
			for(Produto produto : produtos) {
				if(produto.id.equals(idAnterior)) {
					entity.addQuantidade(1);
					
				}else {
					entity = new ProdutoPedidoReportVO();
					
					entity.setCodigoProduto(produto.getCodigoProduto());
					entity.setId(produto.id);
					entity.setQuantidade(1);
					entity.setDescricao(produto.getDescricao());
					
					//produto refrigera��o
					entity.setEhRefrigerado(produto.getEhRefrigerado());
					
					if(produto.getFornecedor()!=null)
						entity.setFornecedor(produto.getFornecedor().getNome());
					
					result.add(entity);
				}
				idAnterior = produto.id;
			}
		}		
		return result;
	}
	
	public static List<ProdutoPedidoReportVO> fillProdutos(PedidoItem item, BigDecimal valorPedido) {
		List<ProdutoPedidoReportVO> result = new ArrayList<ProdutoPedidoReportVO>();
		ProdutoPedidoReportVO entity = null;
		
		if(item!=null && !item.getProdutos().isEmpty()) {
			entity = new ProdutoPedidoReportVO();
			
			entity.setCodigoProduto(item.getProdutos().get(0).getCodigoProduto());
			entity.setValorPedido(valorPedido);
			entity.setId(item.getProdutos().get(0).id);
			entity.setQuantidade(item.getQuantidade());
			entity.setDescricao(item.getProdutos().get(0).getDescricao());
			
			if(item.getProdutos().get(0).getFornecedor()!=null)
				entity.setFornecedor(item.getProdutos().get(0).getFornecedor().getNome());
			
			entity.setValor(new BigDecimal(item.getProdutos().get(0).getValorVenda()));
			entity.setDesconto(item.getPedido().getValorDesconto());
			
			//Observação
			entity.setObservacao(item.getPedido().getObservacao());
			
			result.add(entity);
		}		
		return result;
	}
	
	/**
	 * Overload para atender aos produtos existentes no estoque...
	 * @param item
	 * @param valorPedido
	 * @param estoque
	 * @return
	 */
	public static List<ProdutoPedidoReportVO> fillProdutos(PedidoItem item, BigDecimal valorPedido, Boolean estoque) {
		List<ProdutoPedidoReportVO> result = new ArrayList<ProdutoPedidoReportVO>();
		ProdutoPedidoReportVO entity = null;
		Produto produto = null;
		
		if(item!=null && !item.getProdutos().isEmpty()) {
			produto = item.getProdutos().get(0);
			
			//Tem que ser oriundo do estoque e o produto precisa estar associado ao estoque...
			if(estoque.equals(EstoqueControl.loadEstoque(null, produto.id) !=null)) {
				entity = new ProdutoPedidoReportVO();
				
				entity.setCodigoProduto(produto.getCodigoProduto());
				entity.setValorPedido(valorPedido);
				entity.setId(produto.id);
				entity.setQuantidade(item.getQuantidade());
				entity.setDescricao(produto.getDescricao());
				
				if(produto.getFornecedor()!=null)
					entity.setFornecedor(produto.getFornecedor().getNome());
				
				entity.setValor(new BigDecimal(produto.getValorVenda()));
				entity.setDesconto(item.getPedido().getValorDesconto());
				
				//Observação
				entity.setObservacao(item.getPedido().getObservacao());
				
				result.add(entity);
			}
		}		
		return result;
	}

	@Override
	public int compareTo(ProdutoPedidoReportVO o) {
		if(o==null)
			return 0;
		else
			return this.id.compareTo(o.id);
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
		if (!(obj instanceof ProdutoPedidoReportVO))
			return false;
		final ProdutoPedidoReportVO other = (ProdutoPedidoReportVO) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	/**
	 * @return the desconto
	 */
	public BigDecimal getDesconto() {
		return desconto.setScale(2, BigDecimal.ROUND_HALF_UP);
	}

	/**
	 * @param desconto the desconto to set
	 */
	public void setDesconto(BigDecimal desconto) {
		this.desconto = desconto;
	}

	/**
	 * @return the frete
	 */
	public BigDecimal getFrete() {
		return frete.setScale(2, BigDecimal.ROUND_HALF_UP);
	}

	/**
	 * @param frete the frete to set
	 */
	public void setFrete(BigDecimal frete) {
		this.frete = frete;
	}
	
	public BigDecimal getCreditoDebito() {
		if(creditoDebito==null)
			creditoDebito = BigDecimal.ZERO;
		
		return creditoDebito;
	}

	public void setCreditoDebito(BigDecimal creditoDebito) {
		this.creditoDebito = creditoDebito;
	}

	/**
	 * @return the ehRefrigerado
	 */
	public Boolean getEhRefrigerado() {
		return ehRefrigerado;
	}

	/**
	 * @param ehRefrigerado the ehRefrigerado to set
	 */
	public void setEhRefrigerado(Boolean ehRefrigerado) {
		this.ehRefrigerado = ehRefrigerado;
	}

}
