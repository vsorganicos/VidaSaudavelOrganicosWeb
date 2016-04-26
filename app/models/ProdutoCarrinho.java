/**
 * 
 */
package models;

/**
 * <p>Interface para possibilitar trabalhar com o carrinho para produtos e cestas.</p> 
 * @author Felipe G. de Oliveira
 * @version 1.0
 */
public interface ProdutoCarrinho {

	void setAtivo(Boolean ativo);
	
	Boolean getAtivo();
}
