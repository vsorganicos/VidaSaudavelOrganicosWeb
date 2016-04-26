/**
 * 
 */
package controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.apache.commons.lang.StringUtils;

import models.CestaPronta;
import models.Fornecedor;
import models.Pedido;
import models.Produto;
import models.Secao;
import play.Logger;
import play.cache.Cache;
import play.data.binding.As;
import play.data.validation.Valid;
import play.db.jpa.Transactional;
import play.i18n.Messages;
import play.libs.Images;
import play.modules.paginate.ValuePaginator;
import play.mvc.Before;
import types.ListaProduto;
import util.AmazonS3Util;
import util.ProdutoComparator;
import business.produto.ProdutoControl;

/**
 * @author guerrafe
 *
 */
public class Produtos extends BaseController {
	
	private static final String ARQUIVO_PEDIDOS = "pedidos.xml";
	
	@Before(unless={"view", "findBySecao", "detail", "getProdutosAtivos"})
	static void estaAutorizado() {
		Logger.debug("####### Verificar se o usuário autenticado é admin... ########");
		
		if( (StringUtils.isEmpty(session.get("isAdmin")) || Boolean.FALSE.equals(Boolean.valueOf(session.get("isAdmin")))) 
				&& (StringUtils.isEmpty(session.get("isEmployee")) && Boolean.FALSE.equals(Boolean.valueOf(session.get("isEmployee")))) ) 
			{
			Logger.debug("####### Usuário não autorizado a acessar essa funcionalidade...%s ########", session.get("usuarioAutenticado"));
			
			Home.index("Usuário não autorizado a acessar essa funcionalidade.");
		}
	}
	
	public static void cadastrar(Produto produto) {
		Logger.debug("###### Início - Cadastro de Produtos... ######", "");
		Secao secao = null;
		List<Secao> secoes = Secao.find("ativo = ?", Boolean.TRUE).fetch();
		List<Fornecedor> fornecedores = Fornecedor.findAll();
		
		if(produto==null) {
			produto = new Produto();
			secao = new Secao();
			
		}else {
			secao = produto.getSecao();
		}
		render(produto, secao, secoes, fornecedores);
	}
	
	@Transactional(readOnly=false)
	public static void salvar(@Valid Produto produto, @As("dd/MM/yyyy") Date dataValidade) {
		Logger.debug("######### Vai cadastrar o produto: %s #########", produto.getNome());

		if(produto.getImagem()==null)
			validation.addError("Imagem", "validation.required", "");
		
		if(produto.getFornecedor()==null)
			validation.addError("Fornecedor", "validation.required", "");
		
		if(validation.hasErrors()) {
			Logger.debug("######### Não foi possível salvar o produto: %s #########", validation.errors());
			
			params.flash();
			validation.keep();
			
			Produtos.cadastrar(produto);
		}
		produto.setDataCadastro(new Date());
		produto.setAtivo(true);
		
		moveImage(produto);
		produto.save();

		Produtos.show(null);
	}
	
	@Transactional(readOnly=false)
	public static void atualizar(@Valid Produto produto, @As("dd/MM/yyyy") Date dataValidade) {
		Logger.debug("######### Vai cadastrar o produto: %s #########", produto.getNome());
		
		if(produto.getImagem()!=null) {
			removeImage(produto);
			moveImage(produto);
		}else
			getImage(produto);
		
		if(validation.hasErrors()) {
			params.flash();
			validation.keep();
			
			Produtos.edit(produto.id, null);
		}

		produto.setDataAlteracao(new Date());

		produto.save();
		
		Produtos.show(null);
	}
	
	/**
	 * Para requisições via AJAX
	 * @param id
	 */
	public static void view(Long id) {
		Logger.debug("######### Início - Visualização de produto %s... #########", id);
		
		Produto produto = Produto.findById(id);
		render(produto);
	}
	
	public static void edit(Long id, String message) {
		Logger.info("######### O produto.id %s vai ser consultado... #########", id);
		Produto produto = Produto.findById(id);
		List<Fornecedor> fornecedores = Fornecedor.findAll();
		
		List<Secao> secoes = Secao.find("ativo = ?", Boolean.TRUE).fetch();
		
		getImage(produto);
		
		render(produto, secoes, message, fornecedores);
	}
	
	@Transactional(readOnly=false)
	public static void changeStatus(Long id) {
		Produto _prod = Produto.findById(id);
		
		_prod.setAtivo(!_prod.getAtivo());
		
		_prod.save();
		
		show(null);
	}
	
	@Transactional(readOnly=false)
	public static void changeProductsStatus(Boolean status) {
		Logger.debug("#### Vai ativar/inativar [%s] todos os produtos ####", status);
		
		Query query = Produto.em().createQuery("update Produto set ativo =:status").setParameter("status", status);
		
		Logger.debug("#### Fim ativar/inativar [%s] todos os produtos ####", query.executeUpdate());
		
		show(null);
	}
	
	@Transactional(readOnly=false)
	public static void adicionaStatusPromocao(Long id) {
		Produto _prod = Produto.findById(id);
		
		_prod.setEhPromocao(!_prod.getEhPromocao());
		
		_prod.save();
		
		show(null);
	}
	
	public static void show(List<Produto> prods) {
		Logger.debug("####### Vai consultar os produtos... ########");
		
		if(prods==null)
			prods = Produto.all().fetch();
		
		ValuePaginator<Produto> produtos = new ValuePaginator<Produto>(prods);
		render(produtos);
		
		Logger.debug("####### Total consultado...%s ########", prods.size());
	}
	
	@SuppressWarnings("all")
	public static void order(String order, Boolean asc) {
		StringBuffer params = new StringBuffer();
		params.append("order by ").append(order).append(" ").append(asc ? "ASC" : "DESC");
		
		List<Produto> prods = Produto.find(params.toString(), null).fetch();
		
		ValuePaginator<Produto> produtos = new ValuePaginator<Produto>(prods);
		
		renderTemplate("Produtos/show.html", produtos);
	}
	
	private static void removeImage(Produto produto) {
		if(produto!=null) {
			Logger.info("######## A imagem %s foi excluída? %s #########", 
					produto.getImagemProduto(), 
					AmazonS3Util.deleteFileInS3(null, "images/" +produto.getImagemProduto() ));
		}
	}
	
	private static void getImage(Produto produto) {
		if(produto!=null) {
			File imagem = new File(Messages.get("application.path.upload.images", "") + produto.getImagemProduto() );
			
			if(imagem.exists())
				produto.setImagem(imagem);
		
		}
	}
	
	private static void moveImage(Produto produto) {
		File path = null;
		File image = null;
		String nameFile = null;
		
		if(produto!=null) {
				path = new File(System.getProperty("java.io.tmpdir") + File.separatorChar );
				nameFile = String.valueOf(new Date().getTime())+".jpg";
				image = new File(path, nameFile);
				
				Images.resize(produto.getImagem(), image, 
						Integer.parseInt(Messages.get("application.path.images.width", "")), 
						Integer.parseInt(Messages.get("application.path.images.height", "")));
				
				produto.setImagemProduto(nameFile);
				
				String result = AmazonS3Util.sendFileToS3(null, image, "images/" + 
															nameFile);
				
				Logger.info("########## Arquivo movido. Caminho: %s ###########", result);
		}
	}
	
	public static void findBySecao(Long id, String nome) {
		Secao secao = Secao.findById(id);
		
		flash.success(buildProdutosSecao(secao));
		
		if(nome.toLowerCase().trim().contains("cestas prontas")) {
			List<CestaPronta> cestas = Cache.get("cestasAtivas", List.class);
			
			if(cestas==null) {
				cestas = CestaPronta.find("ativo = ?", Boolean.TRUE).fetch();
				Cache.add("cestasAtivas", cestas, "24h");
			}
			renderTemplate("Cestas/cestaProdutos.html", cestas);
			
		}else {
			List<Produto> prods = Produto.find("secao.id = ? AND ativo = ?", id, Boolean.TRUE).fetch();
			
			Logger.debug("######## Fim - Pesquisa da seção %s, foram econtrado(s) %s produto(s).########", id, prods.size());
			
			Collections.sort(prods, new ProdutoComparator(true));
			
			ValuePaginator<Produto> produtos = new ValuePaginator<Produto>(prods);
			produtos.setPageSize(50);
			
			renderTemplate("Home/search.html", produtos, nome);
		}
	}
	
	/**
	 * Pesquisar os produtos
	 */
	public static void findByParams(String produtoParametro, String param) {
		Logger.debug("######## Início - Pesquisar produtos pelo parâmetro: %s########", param); 
		
		StringBuilder query = new StringBuilder();
		String parametro = "";
		
		if("descricao".equalsIgnoreCase(param)) {
			parametro = "%"+produtoParametro.toUpperCase()+"%";
			query.append("UPPER(descricao) LIKE ? ");
			
		}else if("codigo".equalsIgnoreCase(param)) {
			parametro = produtoParametro.toUpperCase();
			query.append("UPPER(codigoProduto) = ? ");
			
		}else if("secao".equalsIgnoreCase(param)) {
			parametro = produtoParametro.toUpperCase();
			query.append("UPPER(secao.descricao) LIKE ? ");
		}
		
		List<Produto> produtos = Produto.find(query.toString(), parametro).fetch();
		
		Logger.debug("######## Fim - Pesquisar produtos pelo parâmetro: %s########", param);
		
		render("Produtos/show.html", produtos);
	}
	
	public static void detail(Long id, String nome) {
		Produto produto = Produto.find("id = ? AND ativo = ?", id, Boolean.TRUE).first();
		
		if(produto!=null) {
			flash.success(buildProdutosSecao(produto.getSecao()));
		
			render(produto);
		}else {
			Home.index(Messages.get("message.notfound.product", ""));
		}
	}
	
	@SuppressWarnings("all")
	public static void atualizarProdutosFornecedores() {
		Logger.debug("######### Início - Atualizar todos os produtos do site com as tabelas enviadas. ##########");
		ProdutoControl control = new ProdutoControl();
		/*
		Integer updates = 0;
		List<Long> fornecedores = Fornecedor.find("select id from Fornecedor order by id ASC", null).fetch();
		
		
		for(Long idFornecedor : fornecedores) {
			control.atualizarProdutos(idFornecedor);
			
			updates++;
		}
		*/
		//Rebuild dos índices
		control.generateLuceneIndex();
		
		Logger.debug("######### Fim - Atualizar todos os produtos do site com as tabelas enviadas. ##########");
		
		Home.index("Os produtos foram atualizados para o mecanismo de busca do site.");
	}
	
	@Transactional(readOnly=false)
	public static void mudarStatusPromocao(Boolean ehPromocao) {
		Logger.debug("#### Vai mudar o status de promoção [%s] dos produtos. ####", ehPromocao);
		
		Query query = Produto.em().createQuery("update Produto set ehPromocao =:ehPromocao");
		query.setParameter("ehPromocao", ehPromocao);
		
		Logger.debug("#### %s produtos com status de promoção alterado(s). ####", query.executeUpdate());
		
		show(null);
	}
	
	@Transactional(readOnly=false)
	public static void excluir(Long id) {
		Produto produto = Produto.findById(id);
		
		removeImage(produto);
		
		produto.delete();
		
		Logger.info("Produto removido: %s ", id);
		
		show(null);
	}
	
	public static void consultarPreco(Long id) {
		Pedido pedido = Produto.find("select valorPago from Pedido where id = ?", id).first();
		
		renderJSON(pedido.getValorPago());
	}
	
	public static void getProdutosAtivos() {
		List<Produto> produtosAtivos = null;
		Marshaller marshaller = null;
		File arquivoProdutos = null;
		ListaProduto produtos = Cache.get(ARQUIVO_PEDIDOS, ListaProduto.class);
		
		try {
			Logger.info("#### Início - Invocação Serviço de Produtos Ativos ####", "");
			
			if(produtos==null) {
				produtosAtivos = Produto.find("ativo = ?", Boolean.TRUE).fetch();
			
				produtos = new ListaProduto(produtosAtivos);
				
				Cache.add(ARQUIVO_PEDIDOS, produtos, "24h");
			}
			
			JAXBContext jaxbContext = JAXBContext.newInstance(ListaProduto.class);
			marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			
			arquivoProdutos = new File(System.getProperty("java.io.tmpdir")+File.separatorChar + ARQUIVO_PEDIDOS);
			
			if(!arquivoProdutos.exists())
				arquivoProdutos.createNewFile();
			
			InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(arquivoProdutos));

			marshaller.marshal(produtos, arquivoProdutos);
			
			char[] buffer = new char[new FileInputStream(arquivoProdutos).available()];
			
			Logger.info("#### Arquivo XML Serializado: %s; tamanho: %s ####", arquivoProdutos.getAbsolutePath(), buffer.length);
			
			inputStreamReader.read(buffer);
			
			inputStreamReader.close();
			
			renderXml(new String(buffer));
			
		}catch(Exception e) {
			Logger.error(e, "Erro ao tentar recuperar os produtos ativos via WS.");
			renderXml(e);
			
		}finally {
			Logger.info("#### Fim - Invocação Serviço de Produtos Ativos ####", "");
		}
	}
	
	private static String buildProdutosSecao(Secao secao) {
		StringBuilder path = null;
		
		if(Cache.get("secao:" + secao.id)==null) {
			List<Secao> secoes = new ArrayList<Secao>();
			path = new StringBuilder();
			boolean test = true;
			
			while(test) {
				if(secao.getSecaoPai()==null) {
					secoes.add(secao);
					test = !test;
				}else {
					secoes.add(secao);
					
					secao = Secao.findById(secao.getSecaoPai().id);
				}
			}
			
			for(int i=secoes.size()-1; i>=0; i--) {
				Secao _secao = secoes.get(i);
				path.append("<a class=\"a1\" href=\"/produtos/secao/").append(_secao.id).append("/").append(_secao.getDescricao()).append("\">");
				path.append(_secao.getDescricao());
				path.append("</a>");
				
				if(i>0)
					path.append(" > ");
			}
			
			Cache.add("secao:" + secao.id, path, "24h");
			
		}else {
			path = (StringBuilder) Cache.get("secao:" + secao.id);
			
		}
		return path.toString();
	}
	
}
