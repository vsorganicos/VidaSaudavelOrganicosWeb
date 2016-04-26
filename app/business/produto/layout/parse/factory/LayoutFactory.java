/**
 * 
 */
package business.produto.layout.parse.factory;

import business.produto.layout.LayoutArquivo;
import business.produto.layout.parse.ILayoutParse;
import business.produto.layout.parse.LayoutProdutoParse;
import business.produto.layout.parse.LayoutProdutosParse;

/**
 * @author hpadmin
 *
 */
public abstract class LayoutFactory {
	
	public static ILayoutParse<?> getLayout(LayoutArquivo layoutArquivo) {
		if(layoutArquivo.equals(LayoutArquivo.CSV))
			return new LayoutProdutoParse();
		else if(layoutArquivo.equals(LayoutArquivo.PRODUTO_CSV))
			return new LayoutProdutosParse();
		else
			return null;
	}

}
