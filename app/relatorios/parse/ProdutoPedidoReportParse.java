/**
 * 
 */
package relatorios.parse;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

import play.Logger;
import vo.ProdutoPedidoReportVO;

/**
 * @author guerrafe
 *
 */
public class ProdutoPedidoReportParse implements Serializable {

	private static final long serialVersionUID = -6392129970196986560L;
	
	private List<ProdutoPedidoReportVO> produtos = null;
	
	public ProdutoPedidoReportParse(List<ProdutoPedidoReportVO> produtosReport) {
		this.produtos = produtosReport;
	}
	
	public InputStream generateProdutoPedidoReport() {
		InputStream result = null;
		StringBuilder line = new StringBuilder();
		
		try {
			if(this.produtos!=null && !this.produtos.isEmpty()) {
				line.append("Código").append(";");
				line.append("Descrição").append(";");
				line.append("Quantidade").append(";");
				line.append("Fornecedor").append(";");
				line.append("\r\n");
				
				for(ProdutoPedidoReportVO produtoPedidoReportVO : produtos) {
					line.append(produtoPedidoReportVO.getCodigoProduto()).append(";");
					line.append(produtoPedidoReportVO.getDescricao()).append(";");
					line.append(produtoPedidoReportVO.getQuantidade()).append(";");
					line.append(produtoPedidoReportVO.getFornecedor()).append(";");
					line.append("\r\n");
				}
				
				result = new ByteArrayInputStream(line.toString().getBytes("ISO-8859-1"));
			}
			
		}catch(Exception e) {
			Logger.error(e, "Ocorreu um erro na tentativa de gerar o Relatório de Produtos por Fornecedor no formato CSV.");
			throw new RuntimeException(e);
		}
		return result;
	}
	
}
