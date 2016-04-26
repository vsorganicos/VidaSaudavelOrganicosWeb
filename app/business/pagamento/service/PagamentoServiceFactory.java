/**
 * 
 */
package business.pagamento.service;

import java.io.Serializable;

import play.i18n.Messages;

import models.FormaPagamento;

import business.pagamento.service.interfaces.GatewayService;

/**
 * @author Felipe Guerra
 *
 */
public class PagamentoServiceFactory implements Serializable {

	private static final long serialVersionUID = 19314613841386481L;
	
	private static PagamentoServiceFactory INSTANCE = new PagamentoServiceFactory();
	
	private PagamentoServiceFactory(){}
	
	public static PagamentoServiceFactory getInstance() {
		return INSTANCE;
	}
	
	public GatewayService getGatewayServiceImpl(FormaPagamento formaPagamento) {
		switch(formaPagamento) {
			case PAYPAL:
				return PayPalService.newInstance(Messages.get("application.service.url.paypal", ""), 
						Messages.get("application.username.url.paypal", ""), 
						Messages.get("application.password.url.paypal", ""), 
						Messages.get("application.signature.url.paypal", ""));
			
			case PAGSEGURO: 
				return PagSeguroService.newInstance(Messages.get("application.url.pagseguro", ""), 
													Messages.get("application.username.pagseguro", ""), 
													Messages.get("application.token.pagseguro", ""));
			
			default: throw new IllegalStateException("Método de pagamento não encontrado");
			
		}
	}
	
}
