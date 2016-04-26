/**
 * 
 */
package controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import models.CestaProduto;
import models.CestaPronta;
import models.Produto;
import play.Logger;
import play.cache.Cache;
import play.data.validation.Valid;
import play.db.jpa.Transactional;
import play.i18n.Messages;
import play.mvc.Before;


/**
 * @author Felipe G. de Oliveira
 * @version 1.2
 */
public class Cestas extends BaseController {
	
	@Before(unless={"detail"})
	static void estaAutorizado() {
		Logger.debug("####### Verificar se o usuário autenticado é admin... ########");
		
		if( (StringUtils.isEmpty(session.get("isAdmin")) || Boolean.FALSE.equals(Boolean.valueOf(session.get("isAdmin")))) 
				&& (StringUtils.isEmpty(session.get("isEmployee")) && Boolean.FALSE.equals(Boolean.valueOf(session.get("isEmployee")))) ) 
			{
			Logger.debug("####### Usuário não autorizado a acessar essa funcionalidade...%s ########", session.get("usuarioAutenticado"));
			
			Home.index("Usuário não autorizado a acessar essa funcionalidade.");
		}
	}
	
	public static void cadastrar(CestaPronta cesta) {
		if(cesta==null)
			cesta = new CestaPronta();
		
		List<Produto> produtosAtivos = Produto.find("ativo = ? order by secao.descricao ASC", Boolean.TRUE).fetch();
		List<Produto> produtos = new ArrayList<Produto>();
		
		render(cesta, produtosAtivos, produtos);
	}
	
	@Transactional(readOnly=false)
	public static void salvar(@Valid CestaPronta cesta, List<Long> produtosDisponiveis) {
		Logger.debug("##### Vai cadastrar uma nova cesta...Título: %s ######", cesta.getTitulo());
		StringBuffer caminhoImagem = null;
		
		if(produtosDisponiveis==null || produtosDisponiveis.isEmpty())
			validation.addError("produtosDisponiveis", "Selecione ao menos 1 produto", "");
		
		if(validation.hasErrors()) {
			params.flash();
			validation.keep();
			
			cadastrar(cesta);
			
		}else {
			CestaProduto cestaProduto = null;
			
			for(Long id : produtosDisponiveis) {
				Produto produto = Produto.findById(id);
				cestaProduto = new CestaProduto();
				
				cestaProduto.setProduto(produto);
				cestaProduto.setCesta(cesta);

				Logger.debug("#### Produto %s adicionado à cesta? %s ####", produto.getNome(), cesta.getProdutosAtivos().add(cestaProduto)) ;
			}
			caminhoImagem = new StringBuffer();
			caminhoImagem.append(Messages.get("application.static.content", ""));
			caminhoImagem.append(Messages.get("application.path.public.images", ""));
			caminhoImagem.append(cesta.getCaminhoImagem());
			
			cesta.setCaminhoImagem(caminhoImagem.toString());
			cesta.setAtivo(Boolean.TRUE);
			cesta.save();
			
			Cache.safeDelete("cestas");
			Cache.safeDelete("cestasAtivas");
			
			Logger.info("##### Cesta adicionada com sucesso...Título: %s ######", cesta.getTitulo());
		
			show();
		}
	}
	
	public static void show() {
		List<CestaPronta> cestas = getCestas();
		
		render(cestas);
	}
	
	@Transactional(readOnly=false)
	public static void changeStatusCesta(Long id) {
		CestaPronta cestaPronta = CestaPronta.findById(id);
		cestaPronta.setAtivo(!cestaPronta.getAtivo());

		cestaPronta.save();

		Cache.safeDelete("cestasAtivas");
		Cache.safeDelete("cestas");
		
		show();
	}
	
	public static void edit(Long id) {
		CestaPronta cesta = CestaPronta.findById(id);
		List<CestaProduto> produtosCesta = cesta.getProdutosAtivos();
		List<Produto> produtosDisponiveis = new ArrayList<Produto>();
		List<Produto> produtosAtivos = Produto.find("ativo = ? order by secao.descricao ASC", Boolean.TRUE).fetch();
		
		for(CestaProduto produto : produtosCesta)
			produtosDisponiveis.add(produto.getProduto());
		
		render(cesta, produtosDisponiveis, produtosAtivos);
	}

	@Transactional(readOnly=false)
	public static void atualizar(@Valid CestaPronta cesta, List<Long> produtosDisponiveis) {
		Logger.debug("##### Vai atualizar uma cesta...Título: %s ######", cesta.getTitulo());
		StringBuffer caminhoImagem = null;
		
		if(produtosDisponiveis==null || produtosDisponiveis.isEmpty())
			validation.addError("produtosDisponiveis", "Selecione ao menos 1 produto", "");
		
		if(validation.hasErrors()) {
			params.flash();
			validation.keep();
			
			edit(cesta.id);
			
		}else {
			cesta.getProdutosAtivos().clear();
			
			CestaProduto cestaProduto = null;
			
			for(Long id : produtosDisponiveis) {
				Produto produto = Produto.findById(id);
				cestaProduto = new CestaProduto();
				
				cestaProduto.setProduto(produto);
				cestaProduto.setCesta(cesta);

				Logger.debug("#### Produto %s adicionado à cesta? %s ####", produto.getNome(), cesta.getProdutosAtivos().add(cestaProduto)) ;
			}
			cesta.setDataAlteracao(new Date());
			cesta.save();
			
			Cache.safeDelete("cestasAtivas");
			Cache.safeDelete("cestas");
			
			Logger.info("##### Cesta atualizada com sucesso...Título: %s ######", cesta.getTitulo());
		
			show();
		}
	}
	
	public static void detail(Long id, String nome) {
		CestaPronta cesta = CestaPronta.findById(id);
		List<Produto> produtos = new ArrayList<Produto>();
		
		for(CestaProduto produto : cesta.getProdutosAtivos())
			produtos.add(produto.getProduto());
		
		render(cesta, produtos);
	}
	
	/**
	 * @return todas as cestas
	 */
	private static List<CestaPronta> getCestas() {
		List<CestaPronta> cestas = Cache.get("cestas", List.class);
		
		if(cestas==null) {
			cestas = CestaPronta.findAll();
			Cache.add("cestas", cestas, "24h");
		}
		return cestas;
	}
	
}
