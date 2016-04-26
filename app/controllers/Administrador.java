/**
 * 
 */
package controllers;

import java.io.File;
import java.util.List;

import models.BairroEntrega;
import models.Frete;
import play.Logger;
import play.cache.Cache;
import play.data.validation.Valid;
import play.db.jpa.Transactional;
import play.i18n.Messages;
import play.mvc.Before;

/**
 * @author guerrafe
 *
 */
public class Administrador extends BaseController {
	
	@Before(unless={"oQueSaoOrganicos", "politicaEntrega", "quemSomos", "fornecedores", "parceiros", "naMidia", "servicos"})
	static void isAuthenticated() {
		Logger.debug("####### Validar se usuário está autenticado...#######");
		
		if(session.get("usuarioAutenticado")==null)
			Login.index(null, null, Messages.get("application.message.notAuthenticated", ""));
	}
	
	public static void clearCache() {
		Logger.debug("######## Início - Limpar o Cache... #########");
		
		Cache.clear();
		
		Logger.debug("######## Fim - Limpar o Cache... #########");
		
		Home.index(null);
	}
	
	public static void editFrete(String message) {
		Frete frete = Frete.findById(1L);
		
		Logger.debug("######## Início - Editar o Frete...%s #########", frete);
		
		render(message, frete);
	}
	
	@Transactional(readOnly=false)
	public static void alterarFrete(@Valid Frete frete) {
		Logger.debug("######## Início - Alterar o Frete...%s #########", frete);
		
		if(validation.hasErrors()) {
			params.flash();
			flash.keep();
			
			editFrete(null);
			
		}else {
			frete.save();
			
			editFrete(Messages.get("validation.data.success"));
		}
		Logger.debug("######## Fim - Alterar o Frete...%s #########", frete);
	}
		
	public static void showBairrosEntrega() {
		Logger.debug("#### Início - Ver todos os bairros para entrega... ####", "");
		List<BairroEntrega> bairrosAtendidos = BairroEntrega.find("order by nome ASC").fetch();
		
		render(bairrosAtendidos);
	}
	
	public static void cadastrarBairroEntrega(BairroEntrega bairro, Long id) {
		Logger.debug("#### Início - Cadastrar novo bairro para entrega... ####", "");
		
		List<String> ufs = getListaUF();

		if(id==null) { 
			bairro.setUf("SP");
			bairro.setCidade("São Paulo");
		}
		bairro.id = id;
		
		render(bairro, ufs);
	}
	
	@Transactional(readOnly=false)
	public static void inserirBairroEntrega(@Valid BairroEntrega bairro) {
		Logger.debug("#### Início - Inserir novo bairro para entrega... ####", bairro);

		if(validation.hasErrors()) {
			validation.keep();
			flash.keep();
			
			Logger.debug("#### Erro - Inserir novo bairro para entrega... ####", bairro);
			cadastrarBairroEntrega(bairro, null);
			
		}else {
			bairro.setAtivo(Boolean.TRUE);
			
			bairro.save();
		}
		Logger.debug("#### Fim - Inserir novo bairro para entrega... ####", bairro);
		showBairrosEntrega();
		
	}
	
	public static void editarBairroEntrega(Long id) {
		Logger.debug("#### Início - Consultar bairro para entrega... ####", id);
		
		BairroEntrega bairroEntrega = BairroEntrega.findById(id);
		
		Logger.debug("#### Fim - Consultar bairro para entrega... ####", bairroEntrega.getNome());
		cadastrarBairroEntrega(bairroEntrega, bairroEntrega.id);
	}

	public static void politicaEntrega() {
		List<BairroEntrega> bairrosAtendidos = BairroEntrega.find("ativo = ? order by nome ASC", Boolean.TRUE).fetch();
		
		render(bairrosAtendidos);
	}
	
	@Transactional(readOnly=false)
	public static void changeStatus(Long id) {
		BairroEntrega bairroEntrega = BairroEntrega.findById(id);
		bairroEntrega.setAtivo(!bairroEntrega.getAtivo());
		
		bairroEntrega.save();
		
		showBairrosEntrega();
	}
	
	public static void quemSomos() {
		render();
	}
	
	public static void oQueSaoOrganicos() {
		render();
	}
	
	public static void fornecedores() {
		render();
	}
	
	public static void parceiros() {
		render();
	}
	
	public static void naMidia() {
		render();
	}
	
	public static void servicos() {
		render();
	}
}
