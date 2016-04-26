/**
 * 
 */
package controllers;

import java.util.ArrayList;
import java.util.List;

import models.Cliente;
import models.Telefone;
import models.Usuario;

import play.Logger;
import play.data.binding.As;
import play.data.validation.Required;
import exception.SystemException;

/**
 * 
 * @author Felipe G. de Oliveira
 *
 */
public class FaleConosco extends BaseController {

	public static void index(String nome, String assunto, String email, String mensagem, String result) {
		render(nome, assunto, email, mensagem, result);
	}
	
	public static void enviarFaleConosco(@Required String nome, @Required String assunto,
									@Required String email, @Required String mensagem) throws SystemException {
		Logger.debug("#### Início - Fale Conosco: %s ####", nome);
		
		validation.valid(nome);
		validation.valid(assunto);
		validation.valid(email);
		validation.valid(mensagem);
		validation.email(email);
		
		if(validation.hasErrors()) {
			params.flash();
			validation.keep();
			
			index(nome, assunto, email, mensagem, null);
		}else {
			Logger.debug("#### Vai enviar o Fale Conosco: %s ####", nome);
			
			Mail.sendFaleConosco(assunto, "Contato<contato@vidasaudavelorganicos.com.br>", nome, email, mensagem);
			Mail.newFaleConosco(assunto, "Contato<contato@vidasaudavelorganicos.com.br>", nome, email, mensagem);
			
			Logger.debug("#### Fim enviar o Fale Conosco: %s ####", nome);
		}
		index(null,null,null,null, "message_success");
	}
	
	public static void mensagemTexto(Long idUsuario, List<String> clientes, String message) {
		List<Telefone> telefones = new ArrayList<Telefone>();
		
		if(clientes!=null) {
			for(String idCliente : clientes) {
				telefones.add(Cliente.getTelefoneCelular(Long.parseLong(idCliente)));
			}
		}
		render(telefones, clientes, message);
	}
	
	/**
	 * Enviar mensagem aos clientes com pedido aguardando entrega
	 */
	public static void enviarMensagemTexto(@As(",") List<String> clientes, String canalMensagem, String mensagem) {
		Logger.info("########## [User: %s] Enviar mensagem %s para os clientes %s ##########", session.get("usuarioAutenticado"), canalMensagem, clientes);
		Usuario cliente = null;
		String result = "";
		Usuario usuarioLogado = Usuario.find("cliente.id = ?", Long.parseLong(session.get("clienteId"))).first();
		
		if("email".equalsIgnoreCase(canalMensagem)) {
			for(String idCliente : clientes) {
				cliente = Usuario.find("cliente.id = ?", Long.parseLong(idCliente)).first();
				
				try {
					Mail.emailContatoCliente("Contato", Mail.EMAIL_CONTACT, mensagem, cliente.getEmail(), cliente.getCliente(), usuarioLogado);
					
				}catch(SystemException sex) {
					sex.printStackTrace();
				}
			}
			result = "Mensagem enviada com sucesso!";
		}
		mensagemTexto(Long.parseLong(session.get("clienteId")), clientes, result);
	}
	
}
