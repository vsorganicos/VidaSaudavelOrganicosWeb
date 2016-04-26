/**
 * 
 */
package controllers;

import java.util.List;

import models.Produto;

import org.apache.commons.lang.StringUtils;

import play.Logger;
import play.Play;
import play.i18n.Messages;
import play.modules.paginate.ValuePaginator;
import business.produto.ProdutoControl;

/**
 * @author guerrafe
 *
 */
public class Home extends BaseController {
	
	@play.mvc.Before(only={"index"})
	static void getParameters(){
		String codigoCupomDesconto = params.get("cp_code", String.class);
		
		if(codigoCupomDesconto!=null) {
			session.put("cupom", codigoCupomDesconto);
		}
	}
	
	public static void index(String message) {
		Logger.debug("###### Início - Home Vida Saudável...%s #######", "");
		Boolean home = Boolean.TRUE;

		List<Produto> produtos = Produto.find("ativo = ? AND (fornecedor.nome = ? OR ehPromocao = ?)", Boolean.TRUE, "Estoque", Boolean.TRUE).fetch(12);

		Logger.debug("###### Fim - Home Vida Saudável...%s #######", message);
		render(produtos, message, SecaoProdutos.loadAll(), home);
	}
	
	public static void search() {
		String param = params.get("search", String.class);
		
		Logger.debug("###### Início - Pesquisa de produto...%s #######", param);
		
		if(!StringUtils.isEmpty(param.trim())) {
			String nome = param;
			List<Produto> prods = new ProdutoControl().findProdutosByNomeOuDetalhe(param);
			
			Logger.debug("Foram encontrados %s produtos para o parâmetro: %s", prods.size(), param);
			
			Logger.debug("###### Fim - Pesquisa de produto...%s #######", param);
			
			ValuePaginator<Produto> produtos = new ValuePaginator<Produto>(prods);
			produtos.setPageSize(50);
			
			render(produtos, nome);
			
		}else {
			index(Messages.get("validation.required", "texto"));
		}
	}
	
}
