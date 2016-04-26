/**
 * 
 */
package controllers;

import java.util.List;

import models.Usuario;
import play.Logger;
import play.cache.Cache;
import play.data.validation.Valid;
import play.mvc.Before;
import play.mvc.Http.Cookie;

/**
 * @author guerrafe
 *
 */
public class Login extends BaseController {
	
	public static void cadastrar(@Valid String email) {
		Logger.debug("####### Início - Forward cadastrar cliente #######", email);
		validation.email(email);
		
		if(validation.hasErrors()) {
			params.flash();
			validation.keep();
			index(null, email, null);
		}
		
		Clientes.index(null, null, null, email, null, null, null);
	}
	
	public static void index(Usuario usuario, String email, String message) {
		if(usuario==null)
			usuario = new Usuario();
		
		if(email==null)
			email = null;
		
		render(usuario, email, message);
		
	}
	
	public static void logon(@Valid Usuario usuario, String sessionId) {
		Logger.debug("##### Vai tentar autenticar o usuário: %s ######", usuario.getEmail());
		
		usuario.encryptPassword();
		
		if(!validation.hasErrors()) {
			List<Usuario> list = Usuario.find("email = ? AND senha = ?", usuario.getEmail(), usuario.getSenha()).fetch();
			 
			if(list.isEmpty()) { 
				validation.addError("usuario.senha", "form.login.logon.failed", "");
				
				Logger.debug("####### Usuário ou senha não encontrado. %s #######", usuario.getEmail());
			}else {
				usuario = list.get(0);
			}
		}
		
		if(usuario.getSenhaExpirada())
			redirect("/cliente/alterar/senha/" + usuario.encryptEmail(usuario.getEmail()));
		
		if(validation.hasErrors()) {
			Logger.debug("##### Não foi possível autenticar o usuário %s #####", validation.errors());
			
			usuario.setSenha(null);
			
			params.flash();
			validation.keep();
			
			index(usuario, null, null);
			
		}else {
			usuario.decryptPassword();
			
			session.put("estaNaCapital", usuario.getCliente().getEnderecos().get(0));
			session.put("usuarioAutenticado", usuario);
			session.put("clienteId", usuario.getCliente().getId());
			session.put("isAdmin", usuario.isAdmin());
			session.put("isEmployee", usuario.isEmployee());
			session.put("email", usuario.getEmail());
			
			if(session.get("carrinho")!=null)
				redirect("/pagamento/"+ ((Cookie)request.cookies.get("vidasaudavel")).value);
			
			Home.index(null);
		}
	}
	
	public static void logout(String sessionId) {
		Logger.debug("##### Vai finalizar a sessão ######");
		
		session.remove("usuarioAutenticado");
		session.remove("clienteId");
		session.remove("isAdmin");
		session.remove("cupom");
		session.remove("email");
		
		session.clear();
		
		Cache.safeDelete("valorTotal."+sessionId);
		Cache.safeDelete(sessionId);
		
		Home.index(null);
		Logger.debug("##### Sessão destroída ######");
	}

}
