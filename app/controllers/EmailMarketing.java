/**
 * 
 */
package controllers;

import java.util.List;

import exception.SystemException;
import play.Logger;
import play.data.binding.As;
import play.data.validation.Required;
import play.i18n.Messages;
import play.mvc.Before;

/**
 * @author guerrafe
 *
 */
public class EmailMarketing extends BaseController {
	
	@Before
	static void isAuthenticated() {
		Logger.debug("####### Validar se usuário está autenticado...#######");
		
		if(session.get("usuarioAutenticado")==null)
			Login.index(null, null, Messages.get("application.message.notAuthenticated", ""));
	}

	public static void index(String assunto, String emailFrom, String nomeFrom, String mensagem) {
		render(assunto, emailFrom, nomeFrom, mensagem);
	}
	
	public static void send(@Required(message="Por favor, digite o assunto") String assunto, 
						@Required(message="Por favor, digite o seu nome que aparecerá no e-mail") String nomeFrom, 
						@Required(message="Por favor, digite o seu email") String emailFrom, 
						@Required(message="Digite a mensagem que deseja enviar no e-mail marketing") String mensagem, 
						@Required(message="Digite os destinatários do e-mail marketing") 
						@As(";") List<String> para,
						@Required(message="Digite o nome da imagem que deseja enviar junto por e-mail.") String nomeImagem) {
		StringBuffer from = new StringBuffer();
		
		validation.email(emailFrom);
		
		if(validation.hasErrors()) {
			params.flash();
			validation.keep();
			
		}else {
			String imgName = nomeImagem.trim();
			String pathImage = Messages.get("application.mail.mkt.path", imgName);
			
			from.append(nomeFrom);
			from.append("<");
			from.append(emailFrom);
			from.append(">");

			for(String to : para) {
				try {
					Logger.info("##### E-mail enviado para %s #####", to);
					
					Mail.emailMarketVidaSaudavelOrganicos(assunto, 
													from.toString(), 
													mensagem,
													to,
													pathImage);
				
					Thread.sleep(50);
					
				}catch(InterruptedException e) {
					//ignore
				}catch(SystemException ex) {
					Logger.error(ex, "##### E-mail não enviado para % #####", to);
				}
			}
			
			flash.clear();
			flash.success("E-mail enviado com sucesso!", "");
			
		}
		index(assunto, emailFrom, nomeFrom, mensagem);
	}
	
}
