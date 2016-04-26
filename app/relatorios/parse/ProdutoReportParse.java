/**
 * 
 */
package relatorios.parse;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

import business.estoque.EstoqueControl;

import play.Logger;

import models.Produto;

/**
 * @author guerrafe
 *
 */
public class ProdutoReportParse implements Serializable {

	private static final long serialVersionUID = -9097864534368651L;
	
	public enum LineSeparator {
		VIRGULA(",", "VIRGULA"),
		PONTO_VIRGULA(";", "PONTO_VIRGULA");
		
		private String simbolo;
		private String descricao;
		
		private LineSeparator(String simbolo, String descricao) {
			this.simbolo = simbolo;
			this.descricao = descricao;
		}
		
		public String getDescricao() {
			return descricao;
		}
		
		public String getSimbolo() {
			return simbolo;
		}
		
		public void setDescricao(String descricao) {
			this.descricao = descricao;
		}
		
		public void setSimbolo(String simbolo) {
			this.simbolo = simbolo;
		}
	}
	
	private List<Produto> produtos;
	
	public ProdutoReportParse(List<Produto> produtos) {
		this.produtos = produtos;
	}

	public InputStream parse(final String columnSeparator) {
		InputStream result = null;
		StringBuilder line = new StringBuilder();
		
		try {
			if(produtos!=null && !produtos.isEmpty()) {
				for(Produto produto : produtos) {
					line.append(produto.id).append(columnSeparator);
					line.append(produto.getNome().replaceAll(",", ".")).append(columnSeparator);
					line.append(produto.getDescricao().replaceAll(",", ".")).append(columnSeparator);
					line.append(produto.getFornecedor().getNome()).append(columnSeparator);
					line.append(produto.getSecao()==null ? "" : produto.getSecao().getDescricao()).append(columnSeparator);
					line.append(produto.getCodigoProduto()).append(columnSeparator);
					line.append(produto.getAtivo() ? "ATIVO" : "INATIVO").append(columnSeparator);
					line.append(produto.getValorPago()).append(columnSeparator);
					line.append(produto.getValorVenda()).append(columnSeparator);
					line.append(produto.getDataCadastro()).append(columnSeparator);
					line.append((EstoqueControl.loadEstoque(null, produto.id)!=null) 
								? "Quantidade em estoque: " + String.valueOf(EstoqueControl.loadEstoque(null, produto.id).getQuantidade()) 
								: " - ").append(columnSeparator);
					line.append("\r\n");
					
				}
				result = new ByteArrayInputStream(line.toString().getBytes("ISO-8859-1"));
			}
			
		}catch(Exception e) {
			Logger.error(e, "Ocorreu um erro na tentativa de gerar o Relatório de Produtos no formato CSV.");
			throw new RuntimeException(e);
		}
		return result;
	}
	
}
