/**
 * 
 */
package business.produto.layout;

import java.io.Serializable;

/**
 * @author hpadmin
 *
 */
public enum LayoutArquivo implements Serializable {
	CSV("CSV", "Arquivo usando o delimitador ';' e na sequência <código>;<ativo>;<descrição>"),
	PRODUTO_CSV("PRODUTO_CSV", "Arquivo usando o delimitador ';' e na sequência <id>;<código>;<valorPago>;<valorVenda>;<ativo>;<nome>;<descrição>");
	
	private String codigo;
	private String descricao;
	
	private LayoutArquivo(String codigo, String descricao) {
		this.codigo = codigo;
		this.descricao = descricao;
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
		this.codigo = codigo;
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
	
}
