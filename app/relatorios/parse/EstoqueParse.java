/**
 * 
 */
package relatorios.parse;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import models.ProdutoLoteEstoque;
import play.Logger;

/**
 * @author hpadmin
 *
 */
public class EstoqueParse implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1089768574651L;

	public static String buildHtmlLayout(List<ProdutoLoteEstoque> estoque) {
		StringBuffer build = new StringBuffer();
		NumberFormat numberFormat = null;
		
		try {
			if(estoque!=null && !estoque.isEmpty()) {
				numberFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
				
				for(ProdutoLoteEstoque produtoEstoque : estoque) {
					build.append("<tr>");
					build.append("<td align='center'>");
					build.append(produtoEstoque.getProduto().getNome());
					build.append("</td>");
					build.append("<td align='center'>");
					build.append(produtoEstoque.getQuantidade());
					build.append("</td>");
					build.append("<td align='center'>");
					build.append(numberFormat.format(produtoEstoque.getProduto().getValorPago()));
					build.append("</td>");
					build.append("<td align='center'>");
					build.append(produtoEstoque.getDataValidadeLote()==null ? " - " : produtoEstoque.getDataValidadeLote());
					build.append("</td>");
					build.append("</tr>");
				}
			}
			
		}catch(Exception e) {
			Logger.error(e, "Erro ao tentar fazer o parse dos produtos em Estoque no formato HTML.", "");
			throw new RuntimeException(e);
		}
		return build.toString();
	}

}
