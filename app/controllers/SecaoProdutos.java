/**
 * 
 */
package controllers;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import models.Secao;
import play.Logger;
import play.cache.Cache;
import play.data.validation.Error;
import play.data.validation.Required;
import play.data.validation.Valid;
import play.db.jpa.Transactional;
import play.i18n.Messages;
import play.mvc.Before;

/**
 * @author guerrafe
 *
 */
public class SecaoProdutos extends BaseController {
	
	@Before(unless={"loadAll"})
	static void estaAutorizado() {
		Logger.debug("####### Verificar se o usuário autenticado é admin... ########");
		
		if( (StringUtils.isEmpty(session.get("isAdmin")) || Boolean.FALSE.equals(Boolean.valueOf(session.get("isAdmin")))) 
				&& (StringUtils.isEmpty(session.get("isEmployee")) && Boolean.FALSE.equals(Boolean.valueOf(session.get("isEmployee")))) ) 
			{
			Logger.debug("####### Usuário não autorizado a acessar essa funcionalidade...%s ########", session.get("usuarioAutenticado"));
			
			Home.index("Usuário não autorizado a acessar essa funcionalidade.");
		}
	}
	
	public static void index(Secao secao, String message) {
		if(secao==null)
			secao = new Secao();
		
		List<Secao> secoes = Secao.findAll();
		
		render(secao, secoes, message);
	}
	
	@Transactional(readOnly=false)
	public static void salvar(@Valid Secao secao) {
		Logger.debug("########## Início - Cadastrar Seção %s ##########", secao);
		
		if(validation.hasErrors()) {
			Logger.debug("######### Não foi possível salvar a seção: %s #########", validation.errors());
			
			params.flash();
			validation.keep();
			
			index(secao, null);
		}
		
		Cache.safeDelete("menu");
		
		secao.save();
		
		show();
	}
	
	@Transactional(readOnly=false)
	public static void atualizar(@Required Long id, @Required String descricao, 
								@Required Boolean ativo, Long idSecaoPai) {
		Logger.debug("########## Início - Atualizar Seção %s ##########", descricao);
		Secao secao = null;
		
		validation.valid(id);
		validation.valid(descricao);
		validation.valid(ativo);
		
		if(validation.hasErrors()) {
			Logger.debug("######### Não foi possível atualizar a seção: %s #########", validation.errors());
			
			StringBuffer erros = new StringBuffer();
			
			for(Error erro : validation.errors())
				erros.append(erro.message()).append("\n");
			
			renderText(erros.toString());
			
		}else {
			secao = Secao.findById(id);
			
			secao.setDescricao(descricao);
			secao.setAtivo(ativo);
			
			if(idSecaoPai!=null) {
				Secao secaoPai = Secao.findById(idSecaoPai);
				secao.setSecaoPai(secaoPai);
			}else {
				secao.setSecaoPai(null);
			}
			secao.save();
			
			Cache.safeDelete("menu");
			
			Logger.debug("########## Fim - Atualizar Seção %s ##########", descricao);
		}
		
		renderText(Messages.get("form.secao.success", "atualizada"));
	}
	
	public static void show() {
		Logger.debug("####### Vai consultar todas as seções #######");
		
		List<Secao> secoes = Secao.findAll();
		
		render(secoes);
		
		Logger.debug("####### Fim consulta todas as seções #######");
	}
	
	public static void edit(Long id) {
		Logger.debug("####### Vai editar a seção: %s #######", id);
		
		Secao secao = Secao.findById(id);
		
		List<Secao> secoes = Secao.findAll(); 
		
		render(secao, secoes);
	}

	public static List<Secao> loadAll() {
		Logger.debug("######### Vai carregar todos os menus ativos no banco de dados... ##########", "");
		List<Secao> secoes = Secao.find("ativo = ?", Boolean.TRUE).fetch();
		
		return secoes;
	}
	
}
