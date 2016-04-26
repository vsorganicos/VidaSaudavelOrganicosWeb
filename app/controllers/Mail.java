/**
 * 
 */
package controllers;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import models.Cliente;
import models.CupomDesconto;
import models.Fornecedor;
import models.Pedido;
import models.Usuario;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.mail.EmailAttachment;

import play.Logger;
import play.mvc.Mailer;
import exception.SystemException;

/**
 * @author guerrafe
 *
 */
public class Mail extends Mailer {
	
	public static final String EMAIL_ADMIN = "Administrador<administrador@vidasaudavelorganicos.com.br>";
	
	public static final String EMAIL_CONTACT = "Vida Saudável Orgânicos<contato@vidasaudavelorganicos.com.br>";
	
	public static void sendEmail(String subject,
								String from,
								String to, 
								Cliente cliente) throws SystemException {
		try {
			setFrom(from);
			setSubject(subject);
			addRecipient(to);
			send(cliente);
		
		}catch(Exception e) {
			Logger.error("Erro no envio do e-mail.", e);
			throw new SystemException(e);
		}
	}
	
	public static void sendCadastroAprovado(String subject,
								String from,
								String to, 
								Cliente cliente) throws SystemException {
		try {
			setFrom(from);
			setSubject(subject);
			addRecipient(to);
			
			cliente.getUsuario().decryptPassword();
			
			send(cliente);
			
		}catch(Exception e) {
			Logger.error(e, "Erro no envio do e-mail de cadastro aprovado.");
			throw new SystemException(e);
		}
	}
	
	/**
	 * @param subject
	 * @param from
	 * @param pedido
	 * @param fileAbsolutePath - caminho absoluto da Nota Gerada
	 * @param to
	 * @throws SystemException
	 */
	public static void pedidoFinalizado(String subject,
										String from,
										Pedido pedido,
										String fileAbsolutePath,
										String... to) throws SystemException {
		try {
			setFrom(from);
			setSubject(subject);
			addRecipient(to[0]);
			addBcc(to[to.length-1]);
			setReplyTo(EMAIL_CONTACT);
			
			if(!StringUtils.isEmpty(fileAbsolutePath)) {
				EmailAttachment attachments = new EmailAttachment();
				attachments.setDescription("Nota Pedido");
				attachments.setPath(fileAbsolutePath);
				
				addAttachment(attachments);
			}
			
			send(pedido);
			
		}catch(Exception e) {
			Logger.error(e, "Erro no envio do e-mail de pedido finalizado.");
			throw new SystemException(e);
		}

	}
	
	public static void sendError(String subject, String from, String to, Exception e) {
		try {
			setFrom(from);
			setSubject(subject);
			addRecipient(to);
			
			send(e);
		
		}catch(Throwable ex) {
			Logger.error(ex, "Erro ao enviar e-mail com a exceção de sistema.");
		}
		
	}
	
	public static void sendFaleConosco(String subject, String from, String nome, String email, String message) throws SystemException {
		try {
			setFrom(from);
			setSubject(subject);
			setReplyTo(from);
			addRecipient(email);
			
			send(message, nome);
			
		}catch(Exception e) {
			Logger.error(e, "Erro ao enviar e-mail do Fale Conosco.");
			throw new SystemException(e);
		}
	}
	
	public static void newFaleConosco(String subject, String from, String nome, String email, String message) throws SystemException {
		try {
			setFrom(from);
			setSubject(subject);
			setReplyTo(from);
			addRecipient(from);
			
			send(message, nome, email);
			
		}catch(Exception e) {
			Logger.error(e, "Erro ao enviar e-mail do Fale Conosco.");
			throw new SystemException(e);
		}
	}
	
	public static void sendRelatorioPedidosAguardandoEntrega(String subject, String from, 
															String fileAbsolutePath,
															String message,
															String... email) throws SystemException {
		try {
			setFrom(from);
			
			EmailAttachment attachments = new EmailAttachment();
			attachments.setDescription("Relatório Pedidos Aguardanto Entrega.");
			attachments.setPath(fileAbsolutePath);
			
			addAttachment(attachments);
			
			setSubject(subject);
			addRecipient(email);
			
			send(message);
			
		}catch(Exception e) {
			Logger.error(e, "Erro ao enviar e-mail com o relatório de Pedidos Aguardando Entrega.");
			throw new SystemException(e);
		}
	}
	
	public static void emailMarketVidaSaudavelOrganicos(String subject, String from, 
														String message, String email,
														String urlImagem) throws SystemException {
		try {
			setFrom(from);
			setSubject(subject);
			addRecipient(email);
			setReplyTo(from);
			
			Future<Boolean> sent = send(message, urlImagem);
			
			Logger.debug("Atingiu o timeout? %s",  sent.get(60, TimeUnit.SECONDS) );
			
		}catch(Exception e) {
			Logger.error(e, "Erro ao enviar e-mail com o Marketing.");
			throw new SystemException(e);
		}
	}
	
	public static void emailContatoCliente(String subject, String from, 
											String message, String email, 
											Cliente cliente, Usuario usuarioLogado) throws SystemException {
		try {
			setFrom(from);
			setSubject(subject);
			//addBcc(usuarioLogado.getEmail());
			addRecipient(email);
			setReplyTo(from);
			
			Future<Boolean> sent = send(message, cliente);
			
			Logger.debug("Atingiu o timeout? %s",  sent.get(60, TimeUnit.SECONDS) );
		
		}catch(Exception e) {
			Logger.error(e, "Erro ao enviar e-mail de contato com o cliente.");
			throw new SystemException(e);
		}
	}
	
	@SuppressWarnings("all")
	public static void estoqueControl(String subject, 
									String from,  
									String staticContent,
									String content,
									String... email) throws SystemException {
		try {
			setFrom(from);
			setSubject(subject);
			addRecipient(email);
			setReplyTo(from);
			send(staticContent, content);
			
		}catch(Exception e) {
			Logger.error(e, "Erro ao tentar enviar e-mail com o Relatório de Posição de Estoque.");
			throw new SystemException(e);
		}
		
	}
	
	public static void enviarCupomDescontoCliente(String subject, 
													String from, 
													CupomDesconto cupomDesconto,
													String fileAbsolutePath,
													Cliente cliente) throws SystemException {
		try {
			setFrom(from);
			setSubject(subject);
			addRecipient(cliente.getUsuario().getEmail());
			
			Future<Boolean> sent = send(cupomDesconto, fileAbsolutePath, cliente);
			
			Logger.info("Atingiu timeout %s", sent.get(60, TimeUnit.SECONDS));
			
		}catch(Exception e) {
			Logger.error(e, "Erro ao enviar e-mail com o cupom de desconto %s.", cupomDesconto.getCodigo());
			throw new SystemException(e);
		}
	}
	
	public static void sendProdutosNaoEncontrados(String subject, String from, 
											String fileAbsolutePath,
											String message,
											String... email) throws SystemException {
		try {
			setFrom(from);
			
			if(!StringUtils.isEmpty(fileAbsolutePath)) {
				EmailAttachment attachments = new EmailAttachment();
				attachments.setDescription("Relatório Pedidos Aguardanto Entrega.");
				attachments.setPath(fileAbsolutePath);
				
				addAttachment(attachments);
			}
			setSubject(subject);
			
			/*if(email.length>1) {
				addCc(email[email.length-1]);
				addRecipient(email[email.length]);
			}else {
				addRecipient(email);
			}*/
			addRecipient(email);
			send(message);
			
		}catch(Exception e) {
			Logger.error(e, "Erro ao enviar e-mail com os produtos não encontrados.");
			throw new SystemException(e);
		}
	}

	/**
	 * @param subject
	 * @param from
	 * @param fileAbsolutePath
	 * @param fornecedor
	 * @param email
	 * @throws SystemException
	 */
	public static void sendPedidosPorFornecedorAguardandoEntrega(String subject, String from, 
																String fileAbsolutePath,
																Fornecedor fornecedor,
																String... email) throws SystemException {
		try {
			setFrom(from);
			setReplyTo(from);
			
			if(!StringUtils.isEmpty(fileAbsolutePath)) {
				EmailAttachment attachments = new EmailAttachment();
				attachments.setDescription(fornecedor.getNome());
				attachments.setPath(fileAbsolutePath);
				
				addAttachment(attachments);
			}
			setSubject(subject);
			
			/*if(email.length>1) {
				addCc(email[email.length-1]);
				addRecipient(email[email.length]);
			}else {
				addRecipient(email);
			}*/
			
			addRecipient(email);
			Future<Boolean> sent = send(fornecedor);
			
		}catch(Exception e) {
			Logger.error(e, "Erro ao enviar e-mail com os produtos não encontrados.");
			throw new SystemException(e);
		}
		
	}
	
	public static void sendPedidoNaoFinalizado(String subject,
												String from,
												Long idCliente,
												String... email) throws SystemException {
		Cliente cliente = null;
		
		try {
			cliente = Cliente.findById(idCliente);
			
			setFrom(from);
			setReplyTo(from);
			setSubject(subject);
			addRecipient(email);
			
			send(cliente);
			
		}catch(Exception ex) {
			Logger.error(ex, "Não foi possível enviar o e-mail de pedido não finalizado.");
			throw new SystemException(ex);
		}
	}
	
}
