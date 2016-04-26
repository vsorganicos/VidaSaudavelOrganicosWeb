/**
 * 
 */
package business.estoque;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import models.CarrinhoItem;
import models.PedidoItem;
import models.Produto;
import models.ProdutoLoteEstoque;
import play.Logger;
import exception.ProdutoEstoqueException;

/**
 * @author Felipe G. de Oliveira
 * @version 1.0
 * @since 28/05/2012
 */
public class EstoqueControl implements Serializable {
	
	private static final long serialVersionUID = 7262464347061954320L;

	public EstoqueControl() {
		super();
	}
	
	public static synchronized void atualizarEstoque(ProdutoLoteEstoque produtoEstoque, Integer quantidade, String usuario) {
		if(produtoEstoque!=null) {
			Logger.info("######### Início - Atualizar Estoque [estoque id: %s; quantidade: %s] #########", produtoEstoque.id, quantidade);
			
			produtoEstoque.getLote().setDataAlteracao(new Date());
			produtoEstoque.getLote().setUsuarioAlteracao(usuario);
			produtoEstoque.save();

			Logger.info("######### Fim - Atualizar Estoque [estoque id: %s;] #########", produtoEstoque.id);
		}
	}

	public static synchronized void atualizarEstoque(ProdutoLoteEstoque produtoEstoque) {
		if(produtoEstoque!=null) {
			Logger.info("######### Início - Atualizar Estoque [estoque id: %s;] #########", produtoEstoque.id);
			
			produtoEstoque.getLote().setDataAlteracao(new Date());
			
			produtoEstoque.save();

			Logger.info("######### Fim - Atualizar Estoque [estoque id: %s;] #########", produtoEstoque.id);
		}
	}
	
	public static synchronized ProdutoLoteEstoque loadEstoque(Long id, Long idProduto) {
		if(id==null)
			return findByIdProduto(idProduto);
		else 
			if(idProduto==null)
				return load(id);
			else 
				return null;
	}
	
	private static ProdutoLoteEstoque load(Long id) {
		return ProdutoLoteEstoque.findById(id);
	}

	private static ProdutoLoteEstoque findByIdProduto(Long id) {
		ProdutoLoteEstoque result = ProdutoLoteEstoque.find("produto.id = ?", id).first();
		
		return result;
	}
	
	public static synchronized Integer findEstoque(Long id) {
		Integer quantidade = ProdutoLoteEstoque.find("select quantidade from ProdutoLoteEstoque where produto.id = ?", id).first();
		
		return quantidade;
	}
	
	/**
	 * @param idCarrinho
	 * @param itens
	 * @throws ProdutoEstoqueException
	 */
	public static synchronized void atualizarEstoque(List<CarrinhoItem> itens) throws ProdutoEstoqueException {
		Integer quantidade = 0;
		Produto produto = null;
		ProdutoLoteEstoque estoque = null;
		
		if(itens!=null && !itens.isEmpty()) {
			Logger.debug("#### Atualizar estoque para produtos do carrinho. ####", "");
			for(CarrinhoItem item : itens) {
				quantidade = item.getQuantidade();
				produto = item.getProdutos().get(0);
				estoque = loadEstoque(null, produto.id);
				
				if(estoque!=null) {				
					if(estoque.getQuantidade()<quantidade) {
						StringBuilder exception = new StringBuilder();
						exception.append("O estoque do produto '").append(estoque.getProduto().getNome());
						exception.append("' - Quantidade em estoque: ").append(estoque.getQuantidade());
						exception.append(", não é suficiente para atender ao pedido");
						
						throw new ProdutoEstoqueException(exception.toString());
					}
					
					estoque.setQuantidade(estoque.getQuantidade()-quantidade);
					atualizarEstoque(estoque);
				}
			}
			Logger.debug("#### Fim atualizar estoque para produtos do carrinho. ####", "");
		}
	}
	
	/**
	 * Através de uma lista de ítem de pedidos, atualiza a posição do estoque adicionando a quantidade de cada pedido ao estoque já existente.
	 * @param itens
	 * @param usuarioAlteracao
	 */
	public static synchronized void reporEstoque(List<PedidoItem> itens, String usuarioAlteracao) {
		Logger.debug("#### Atualizar o estoque...usuário: %s####", usuarioAlteracao);
		Produto produto = null;
		ProdutoLoteEstoque estoque = null;
		Integer quantidade = null;
		
		if(itens!=null && !itens.isEmpty()) {
			for(PedidoItem item : itens) {
				quantidade = 0;
				
				produto = item.getProdutos().get(0);
				estoque = loadEstoque(null, produto.id);
				
				if(estoque!=null) {
					quantidade = estoque.getQuantidade() + item.getQuantidade();
					
					estoque.setQuantidade(quantidade);
					estoque.getLote().setDataAlteracao(new Date());
					estoque.getLote().setUsuarioAlteracao(usuarioAlteracao);
					
					estoque.save();
					Logger.info("#### Estoque para o produto %s atualizado. Qtde atualizada: %s ####", produto.getNome(), quantidade);
				}
			}
		}
		Logger.debug("#### Fim atualizar o estoque...usuário: %s####", usuarioAlteracao);
	}
	
	public static synchronized void reporEstoque(List<CarrinhoItem> itens) {
		Produto produto = null;
		ProdutoLoteEstoque estoque = null;
		Integer quantidade = null;
		
		if(itens!=null && !itens.isEmpty()) {
			for(CarrinhoItem item : itens) {
				quantidade = 0;
				
				produto = item.getProdutos().get(0);
				estoque = loadEstoque(null, produto.id);
				
				if(estoque!=null) {
					quantidade = estoque.getQuantidade() + item.getQuantidade();
					
					estoque.setQuantidade(quantidade);
					estoque.getLote().setDataAlteracao(new Date());
					
					estoque.save();
					Logger.info("#### Estoque para o produto %s atualizado. Qtde atualizada: %s ####", produto.getNome(), quantidade);
				}
			}
		}
	}
	
	/**
	 * Consulta todos os produtos que se encontram com uma quantidade menor ou igual a especificada. 
	 * @param qtdMinimaEstoque
	 * @return lista dos produtos em estoque
	 */
	public static synchronized List<ProdutoLoteEstoque> findProdutoEstoque(Integer qtdMinimaEstoque) {
		Logger.debug("### Início - Consultar estoque com quantidade mínima de %s. ###", qtdMinimaEstoque);
		List<ProdutoLoteEstoque> result = null;
		
		result = ProdutoLoteEstoque.find("quantidade <= ? order by produto.nome ASC", qtdMinimaEstoque).fetch();
		
		Logger.debug("### Fim - Consultar estoque com quantidade mínima de %s. Resultado: %s ###", qtdMinimaEstoque, result.size());
		
		return result;
	}
	
}
