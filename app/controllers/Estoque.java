/**
 * 
 */
package controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import models.Lote;
import models.Produto;
import models.ProdutoLoteEstoque;

import org.apache.commons.lang.StringUtils;

import play.Logger;
import play.data.validation.Valid;
import play.db.jpa.Transactional;
import play.modules.paginate.ValuePaginator;
import play.mvc.Before;
import types.EstoqueService;
import business.estoque.EstoqueControl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author hpadmin
 *
 */
public class Estoque extends BaseController {
	
	@Before(unless={"reservarProduto", "reporProdutoEstoque"})
	static void estaAutorizado() {
		Logger.debug("####### Verificar se o usuário autenticado é admin... ########");
		
		if( (StringUtils.isEmpty(session.get("isAdmin")) || Boolean.FALSE.equals(Boolean.valueOf(session.get("isAdmin")))) 
				&& (StringUtils.isEmpty(session.get("isEmployee")) && Boolean.FALSE.equals(Boolean.valueOf(session.get("isEmployee")))) ) 
			{
			Logger.debug("####### Usuário não autorizado a acessar essa funcionalidade...%s ########", session.get("usuarioAutenticado"));
			
			Home.index("Usuário não autorizado a acessar essa funcionalidade.");
		}
	}
		
	public static void show() {
		List<Lote> listaEstoque = Lote.findAll();
		
		ValuePaginator<Lote> posicaoEstoque = new ValuePaginator<Lote>(listaEstoque);
		posicaoEstoque.setPageSize(30);
		
		render(posicaoEstoque);
	}
	
	public static void lote(Lote lote, List<ProdutoLoteEstoque> itens) {
		if(lote==null) {
			lote = new Lote();
			itens = new ArrayList<ProdutoLoteEstoque>();
		}
		ProdutoLoteEstoque produtoLote = new ProdutoLoteEstoque();
		
		render(lote, produtoLote, itens);
	}
	
	public static void consultarLote(Long id) {
		Lote lote = Lote.findById(id);
		ProdutoLoteEstoque produtoLote = new ProdutoLoteEstoque();
		List<ProdutoLoteEstoque> itens = lote.getItens();
		
		render("Estoque/lote.html", lote, produtoLote, itens);
	}
	
	@Transactional(readOnly=false)
	public static void excluirLote(Long id) {
		ProdutoLoteEstoque.delete("lote.id = ?", id);
		Logger.info("####### O Lote [id: %s] foi exclu�do? %s #######", id, Lote.delete("id = ?", id));
		
		show();
	}
	
	@Transactional(readOnly=false)
	public static void excluirProdutoLote(Long id) {
		ProdutoLoteEstoque produtoLoteEstoque = ProdutoLoteEstoque.findById(id);
		Long idLote = produtoLoteEstoque.getLote().id;
		ProdutoLoteEstoque.delete("id = ?", id);
		
		consultarLote(idLote);
	}
	
	@Transactional(readOnly=false)
	public static void cadastarLote(@Valid Lote lote, 
								@Valid ProdutoLoteEstoque produtoLote, 
								@Valid(message="message.required.product.nome") String nomeProduto, 
								@Valid(message="message.required.product.codigo") String codigoProduto) {
		Produto produto = Produto.find("codigoProduto = ? AND nome = ?", codigoProduto, nomeProduto).first();
		List<ProdutoLoteEstoque> itens = null;
		ProdutoLoteEstoque estoque = ProdutoLoteEstoque.find("produto.id = ?", produto.id).first(); 
		
		if(estoque!=null && estoque.getLote().getCodigo().equalsIgnoreCase(lote.getCodigo()))
			validation.addError("lote.codigo", "message.error.produto.codigo", "");
		
		if(lote.id==null && Lote.find("codigo = ?", lote.getCodigo().trim()).first()!=null)
			validation.addError("lote.codigo", "message.error.lote.codigo", "");
		
		if(validation.hasErrors()) {
			validation.keep();
		
			lote(null, null);
			
		}else {
			if(produto!=null) {
				produtoLote.setProduto(produto);

				lote.setUsuarioAlteracao(session.get("usuarioAutenticado"));
				lote.addProdutoLoteEstoque(produtoLote);
				
				if(lote.id==null)
					lote.setDataCadastro(new Date());
				else
					lote.setDataAlteracao(new Date());
				
				lote.save();
				itens = lote.getItens();
				
				render("Estoque/lote.html", lote, null, itens);
			}
		}
	}
	
	public static void editarProdutoLoteEstoque(Long id) {
		ProdutoLoteEstoque produtoLoteEstoque = ProdutoLoteEstoque.findById(id);
		
		render("Estoque/edit.html", produtoLoteEstoque);
	}
	
	public static void pesquisarProdutoPeloNome() {
		Gson gsonBuilder = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		StringBuilder param = new StringBuilder();
		param.append("%").append(params.get("q")).append("%");
		
		List<Produto> produtos = Produto.find("ativo = ? AND descricao LIKE ?", Boolean.TRUE, param.toString()).fetch(20);

		renderJSON(gsonBuilder.toJson(produtos));
	}
	
	@Transactional(readOnly=false)
	public static void atualizar(@Valid ProdutoLoteEstoque produtoLoteEstoque) {
		Logger.debug("#### In�cio - Atualizar Estoque para o produto id: %s ####", produtoLoteEstoque.getProduto().getNome());
		
		if(validation.hasErrors()) {
			validation.keep();
			
		}else {
			produtoLoteEstoque.getLote().setDataAlteracao(new Date());
			produtoLoteEstoque.getLote().setUsuarioAlteracao(session.get("usuarioAutenticado"));
			
			produtoLoteEstoque.save();
		}
		Logger.debug("#### Fim - Atualizar Estoque para o produto id: %s ####", produtoLoteEstoque.getProduto().getNome());
		consultarLote(produtoLoteEstoque.getLote().id);
	}

	public static void findByParams(String produtoParametro, String param) {
		Logger.debug("######## Início - Pesquisar estoque pelo parâmetro: %s########", param); 
		List<ProdutoLoteEstoque> estoqueProdutos = new ArrayList<ProdutoLoteEstoque>();
		StringBuilder query = new StringBuilder();
		Object parametro = null;

		if("produto.dataValidade".equalsIgnoreCase(param)){
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			
			try {
				parametro = dateFormat.parse(produtoParametro);
				query.append(param);
				query.append(" <= ?");
				
			}catch(Exception e) {
				validation.addError("dataPedido", "Digite uma data válida.", "");
				Logger.error(e, "Não foi possível converter a data: %s", param);
			}
			
		}else {
			parametro = "%"+produtoParametro.toUpperCase()+"%";
			query.append("UPPER(");
			query.append(param);
			query.append(") LIKE ? ");
		}
		
		if(!validation.hasErrors())
			estoqueProdutos = ProdutoLoteEstoque.find(query.toString(), parametro).fetch();
		
		Logger.debug("######## Fim - Pesquisar estoque pelo parâmetro: %s########", param);
		
		render("Estoque/show.html", estoqueProdutos);
	}
	
	@Transactional(readOnly=false)
	public static void reservarProduto(Long idProduto, Integer qtd) {
		ProdutoLoteEstoque estoque = null;
		EstoqueService service = null;
		Marshaller marshaller = null;
		File xmlReservaEstoque = null;
		InputStreamReader reader = null;
		
		try{
			Logger.info("Início - Reserva de estoque para o produto: %s", idProduto);
			
			service = new EstoqueService();
			estoque = EstoqueControl.loadEstoque(null, idProduto);
			
			JAXBContext jaxbContext = JAXBContext.newInstance(EstoqueService.class);
			marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			
			xmlReservaEstoque = new File(System.getProperty("java.io.tmpdir")+File.separatorChar + "reservaEstoque_" + new Date().getTime() + ".xml");
			xmlReservaEstoque.createNewFile();
			
			reader = new InputStreamReader(new FileInputStream(xmlReservaEstoque));
			
			if(estoque==null) {
				service.setError(1000L);
				service.setMessage( service.getErrorMessage(1000L) );
				
			}else if(estoque.getQuantidade()<=0 || (qtd!=null && estoque.getQuantidade()<qtd)) {
				service.setError(1001L);
				service.setMessage( service.getErrorMessage(1001L) );
				
			}else {
				estoque.setQuantidade(estoque.getQuantidade() - (qtd==null ? 1 : qtd));
				
				EstoqueControl.atualizarEstoque(estoque);
				
				service.setMessage("Sucesso");
			}
			
			marshaller.marshal(service, xmlReservaEstoque);
			
			char[] buffer = new char[new FileInputStream(xmlReservaEstoque).available()];
			
			reader.read(buffer);
			
			renderXml(new String(buffer));
			
		}catch(Exception e) {
			Logger.error(e, "Erro ao tentar reservar o produto. id: " + idProduto);
			service.setError(9999L);
			service.setMessage("Ocorreu um erro ao tenatr reservar o produto: " + e.getMessage());
			renderXml(e);
			
		}finally {
			if(reader!=null)
				try {
					reader.close();
					
				}catch(IOException e) {}
			
			Logger.info("Fim - Reserva de estoque para o produto: %s", idProduto);
		}
	}
	
	@Transactional(readOnly=false)
	public static void reporProdutoEstoque(Long idProduto, Integer qtd) {
		ProdutoLoteEstoque estoque = null;
		EstoqueService service = null;
		Marshaller marshaller = null;
		File xmlReservaEstoque = null;
		InputStreamReader reader = null;
		
		try{
			Logger.info("Início - Repor estoque para o produto: %s", idProduto);
			
			service = new EstoqueService();
			estoque = EstoqueControl.loadEstoque(null, idProduto);
			
			JAXBContext jaxbContext = JAXBContext.newInstance(EstoqueService.class);
			marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			
			xmlReservaEstoque = new File(System.getProperty("java.io.tmpdir")+File.separatorChar + "reporEstoque_" + new Date().getTime() + ".xml");
			xmlReservaEstoque.createNewFile();
			
			reader = new InputStreamReader(new FileInputStream(xmlReservaEstoque));
			
			if(estoque==null) {
				service.setError(1000L);
				service.setMessage( service.getErrorMessage(1000L) );
				
			}else {
				estoque.setQuantidade(estoque.getQuantidade() + (qtd==null ? 1 : qtd));
				
				EstoqueControl.atualizarEstoque(estoque);
				
				service.setMessage("Sucesso");
			}
			
			marshaller.marshal(service, xmlReservaEstoque);
			
			char[] buffer = new char[new FileInputStream(xmlReservaEstoque).available()];
			
			reader.read(buffer);
			
			renderXml(new String(buffer));
			
		}catch(Exception e) {
			Logger.error(e, "Erro ao tentar repor o produto. id: " + idProduto);
			service.setError(9999L);
			service.setMessage("Ocorreu um erro ao tenatr reservar o produto: " + e.getMessage());
			renderXml(e);
			
		}finally {
			if(reader!=null)
				try {
					reader.close();
					
				}catch(IOException e) {}
			
			Logger.info("Fim - Repor estoque para o produto: %s", idProduto);
		}
	}
}
