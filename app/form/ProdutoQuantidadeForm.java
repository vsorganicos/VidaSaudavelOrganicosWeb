/**
 * 
 */
package form;

import java.io.Serializable;
import java.util.List;

/**
 * Classe para o parse dos parâmetros da página de visualização do carrinho.
 * @author Felipe G. de Oliveira
 *
 */
public class ProdutoQuantidadeForm implements Serializable {
	
	private Long id;
	
	private Integer quantidade;
	
	private Boolean excluir = Boolean.FALSE;

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
	 * @return the quantidade
	 */
	public Integer getQuantidade() {
		return quantidade;
	}

	/**
	 * @param quantidade the quantidade to set
	 */
	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}

	
	public static Integer findQuantidade(List<ProdutoQuantidadeForm> produtos, Long id) {
		Integer result = null;
		
		if(produtos!=null) {
			for(ProdutoQuantidadeForm prod : produtos) {
				if(prod.getId().equals(id)) {
					result = prod.getQuantidade();
					break;
				}
			}
		}
		return result;
	}

	/**
	 * @return the excluir
	 */
	public Boolean getExcluir() {
		return excluir;
	}

	/**
	 * @param excluir the excluir to set
	 */
	public void setExcluir(Boolean excluir) {
		this.excluir = excluir;
	}
}
