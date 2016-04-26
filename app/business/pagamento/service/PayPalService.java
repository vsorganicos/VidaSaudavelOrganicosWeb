/**
 * 
 */
package business.pagamento.service;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;

import models.CarrinhoProduto;
import models.Cliente;

import play.Logger;
import play.cache.Cache;
import play.i18n.Messages;
import business.pagamento.service.interfaces.GatewayService;

import com.sun.xml.internal.bind.api.JAXBRIContext;
import com.sun.xml.internal.ws.developer.WSBindingProvider;

import ebay.api.paypalapi.DoExpressCheckoutPaymentReq;
import ebay.api.paypalapi.DoExpressCheckoutPaymentRequestType;
import ebay.api.paypalapi.DoExpressCheckoutPaymentResponseType;
import ebay.api.paypalapi.GetExpressCheckoutDetailsReq;
import ebay.api.paypalapi.GetExpressCheckoutDetailsRequestType;
import ebay.api.paypalapi.GetExpressCheckoutDetailsResponseType;
import ebay.api.paypalapi.ObjectFactory;
import ebay.api.paypalapi.PayPalAPIAAInterface;
import ebay.api.paypalapi.PayPalAPIInterfaceService;
import ebay.api.paypalapi.SetExpressCheckoutReq;
import ebay.api.paypalapi.SetExpressCheckoutRequestType;
import ebay.api.paypalapi.SetExpressCheckoutResponseType;
import ebay.apis.corecomponenttypes.BasicAmountType;
import ebay.apis.eblbasecomponents.AckCodeType;
import ebay.apis.eblbasecomponents.CurrencyCodeType;
import ebay.apis.eblbasecomponents.CustomSecurityHeaderType;
import ebay.apis.eblbasecomponents.DoExpressCheckoutPaymentRequestDetailsType;
import ebay.apis.eblbasecomponents.ErrorType;
import ebay.apis.eblbasecomponents.PaymentActionCodeType;
import ebay.apis.eblbasecomponents.PaymentDetailsItemType;
import ebay.apis.eblbasecomponents.PaymentDetailsType;
import ebay.apis.eblbasecomponents.SetExpressCheckoutRequestDetailsType;
import ebay.apis.eblbasecomponents.UserIdPasswordType;
import exception.GatewayServiceException;

/**
 * @author Felipe Guerra
 *
 */
public class PayPalService implements GatewayService{

	private static final long serialVersionUID = 3485223699084668433L;

	private String urlEndPoint = null;
	
	private String userService = null;

	private String passwordService = null;
	
	private String signatureService = null;
	
	private PayPalAPIAAInterface payPalService = null;
	
	private PayPalService(String urlEndPoint, String userService, String passwordService, String signatureService) {
		this.urlEndPoint = urlEndPoint;
		this.userService = userService;
		this.passwordService = passwordService;
		this.signatureService = signatureService;
		
		try {
			this.payPalService = Cache.get("payPalService", PayPalAPIAAInterface.class);
			
			if(this.payPalService==null) {
				//addProxySettings();
				
				PayPalAPIInterfaceService service = new PayPalAPIInterfaceService(new URL("https://www.paypalobjects.com/wsdl/PayPalSvc.wsdl"), 
																				new QName("urn:ebay:api:PayPalAPI", "PayPalAPIInterfaceService"));
				
				this.payPalService = service.getPayPalAPIAA();
				((BindingProvider) this.payPalService).getRequestContext().put("com.sun.xml.ws.request.timeout", Integer.valueOf(30000));
				((BindingProvider) this.payPalService).getRequestContext().put("com.sun.xml.ws.connect.timeout", Integer.valueOf(30000));
				((BindingProvider) this.payPalService).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, this.urlEndPoint);
				
				Cache.add("payPalService", this.payPalService, "24h");
			}
			
		}catch(Exception e) {
			Logger.error(e, "Não foi possível se conectar o WS do PayPal.");
			throw new IllegalStateException(e);
		}
		
	}
	
	public static PayPalService newInstance(String urlEndPoint, String userService, String passwordService, String signatureService) {
		return new PayPalService(urlEndPoint, userService, passwordService, signatureService);
	}
	
	/**
	 * Método responsável por requisitar o pagamento no PayPal
	 * @param valorPedido
	 * @param idCarrinho
	 * @return
	 */
	public SetExpressCheckoutResponseType solicitarPagamento(String cliente, Double valorPedido, 
															Long idCarrinho, String infosPedido) throws GatewayServiceException {
		Logger.info("#### Iniciar pedido de pagamento para o cliente: %s no gateway PayPal ####", cliente);
		
		SetExpressCheckoutReq setExpressCheckoutReq = null;
		SetExpressCheckoutRequestType setExpressCheckoutRequestType = null;
		SetExpressCheckoutRequestDetailsType checkoutRequestDetailsType = null;
		BasicAmountType basicAmountType = new BasicAmountType();
		SetExpressCheckoutResponseType result = null;
		PaymentDetailsType paymentDetails = null;
		PaymentDetailsItemType itemType = null;
		
		try {
			checkoutRequestDetailsType = new SetExpressCheckoutRequestDetailsType();
			checkoutRequestDetailsType.setLocaleCode("BR");
			checkoutRequestDetailsType.setReqConfirmShipping("0");
			checkoutRequestDetailsType.setNoShipping("1");
			checkoutRequestDetailsType.setCallbackVersion("94.0");
			checkoutRequestDetailsType.setReturnURL(Messages.get("application.return.url.paypal", ""));
			checkoutRequestDetailsType.setCancelURL(Messages.get("application.cancel.url.paypal", ""));
			checkoutRequestDetailsType.setCallbackTimeout("20");

			basicAmountType.setCurrencyID(CurrencyCodeType.BRL);
			basicAmountType.setValue(valorPedido.toString());
			
			paymentDetails = new PaymentDetailsType();
			paymentDetails.setOrderTotal(basicAmountType);
			paymentDetails.setItemTotal(basicAmountType);
			paymentDetails.setPaymentAction(PaymentActionCodeType.SALE);
			
			itemType = new PaymentDetailsItemType();
			itemType.setAmount(basicAmountType);
			itemType.setQuantity(new BigInteger("1"));
			itemType.setDescription("Pedido Vida Saudável Orgânicos | Data: " + new Date());
			itemType.setName("Código: " + idCarrinho);
			
			paymentDetails.getPaymentDetailsItem().add(itemType);
			checkoutRequestDetailsType.getPaymentDetails().add(paymentDetails);
			
			setExpressCheckoutRequestType = new SetExpressCheckoutRequestType();
			setExpressCheckoutRequestType.setVersion("94.0");
			setExpressCheckoutRequestType.setSetExpressCheckoutRequestDetails(checkoutRequestDetailsType);
			
			setExpressCheckoutReq = new SetExpressCheckoutReq();
			setExpressCheckoutReq.setSetExpressCheckoutRequest(setExpressCheckoutRequestType);
			
			logPayload(setExpressCheckoutReq);
						
			setHeaderRequest();
			
			result = this.payPalService.setExpressCheckout(setExpressCheckoutReq);
			
		}catch(SOAPFaultException soapex) {
			Logger.error(soapex, "Erro ao tentar aprovar uma compra no PayPal.");
			throw new GatewayServiceException(soapex);
			
		}catch(WebServiceException wex) {
			Logger.error(wex, "Erro ao tentar aprovar uma compra no PayPal.");
			throw new GatewayServiceException(wex);
			
		}finally {
			Logger.info("#### Fim do pedido de pagamento para o cliente: %s no gateway PayPal ####", cliente);
		}
		return result;
	}
	
	public GetExpressCheckoutDetailsResponseType confirmarPagamento(String token) throws GatewayServiceException {
		Logger.info("#### Iniciar confirmação de pagamento - token: %s no gateway PayPal ####", token);
		GetExpressCheckoutDetailsReq getExpressCheckoutDetailsReq = null;
		GetExpressCheckoutDetailsRequestType getExpressCheckoutDetailsRequest = null;
		GetExpressCheckoutDetailsResponseType result = null;
		
		try {
			getExpressCheckoutDetailsReq = new GetExpressCheckoutDetailsReq();
			getExpressCheckoutDetailsRequest = new GetExpressCheckoutDetailsRequestType();
			getExpressCheckoutDetailsRequest.setToken(token);
			getExpressCheckoutDetailsRequest.setVersion("94.0");
			
			getExpressCheckoutDetailsReq.setGetExpressCheckoutDetailsRequest(getExpressCheckoutDetailsRequest);

			logPayload(getExpressCheckoutDetailsReq);
			
			setHeaderRequest();
			
			result = this.payPalService.getExpressCheckoutDetails(getExpressCheckoutDetailsReq);
			
			if(result.getErrors()!=null && !result.getErrors().isEmpty()) {
				ErrorType error = result.getErrors().get(0);
				throw new GatewayServiceException(error.getErrorCode() + " - " + error.getLongMessage());
			}
			
		}catch(SOAPFaultException soapex) {
			Logger.error(soapex, "Erro ao tentar confirmar uma compra no PayPal.");
			throw new GatewayServiceException(soapex);
			
		}catch(WebServiceException wex) {
			Logger.error(wex, "Erro ao tentar confirmar uma compra no PayPal.");
			throw new GatewayServiceException(wex);
			
		}finally {
			Logger.info("#### Fim confirmação de pagamento - token: %s no gateway PayPal ####", token);
		}
		return result;
	}
	
	public DoExpressCheckoutPaymentResponseType efetivarPagamento(String token, String payerID, Double valorPedido) throws GatewayServiceException {
		Logger.info("#### Vai efetivar o pagamento - token: %s no gateway PayPal ####", token);
		
		DoExpressCheckoutPaymentResponseType result = null;
		DoExpressCheckoutPaymentReq checkoutPaymentReq = null;
		DoExpressCheckoutPaymentRequestType checkoutPaymentRequestType = null; 
		DoExpressCheckoutPaymentRequestDetailsType expressCheckoutPaymentRequestDetailsType = null;
		PaymentDetailsType paymentDetails = null;
		BasicAmountType basicAmountType = null;
		
		try {
			checkoutPaymentReq = new DoExpressCheckoutPaymentReq();
			checkoutPaymentRequestType = new DoExpressCheckoutPaymentRequestType();
			checkoutPaymentRequestType.setVersion("94.0");
			
			expressCheckoutPaymentRequestDetailsType = new DoExpressCheckoutPaymentRequestDetailsType();
			expressCheckoutPaymentRequestDetailsType.setPayerID(payerID);
			expressCheckoutPaymentRequestDetailsType.setToken(token);
			
			basicAmountType = new BasicAmountType();
			basicAmountType.setCurrencyID(CurrencyCodeType.BRL);
			basicAmountType.setValue(valorPedido.toString());
			
			paymentDetails = new PaymentDetailsType();
			paymentDetails.setOrderTotal(basicAmountType);
			expressCheckoutPaymentRequestDetailsType.getPaymentDetails().add(paymentDetails);
			
			checkoutPaymentRequestType.setDoExpressCheckoutPaymentRequestDetails(expressCheckoutPaymentRequestDetailsType);

			checkoutPaymentReq.setDoExpressCheckoutPaymentRequest(checkoutPaymentRequestType);
			
			logPayload(checkoutPaymentReq);
			
			setHeaderRequest();
			
			result = this.payPalService.doExpressCheckoutPayment(checkoutPaymentReq);
			
		}catch(SOAPFaultException soapex) {
			Logger.error(soapex, "Erro ao tentar efetivar uma compra no PayPal.");
			throw new GatewayServiceException(soapex);
			
		}catch(WebServiceException wex) {
			Logger.error(wex, "Erro ao tentar efetivar uma compra no PayPal.");
			throw new GatewayServiceException(wex);
			
		}finally {
			Logger.info("#### Fim efetivar o pagamento - token:%s no gateway PayPal ####", token);
		}
		return result;
	}
	
	public String getUrlConfirmacaoPagamento() {
		String result = Messages.get("application.url.paypal", "");
		
		return result; 
	}
	
	/**
	 * Adicionar os parâmetros de autenticação no Header da mensagem SOAP.
	 */
	protected void setHeaderRequest() {
		CustomSecurityHeaderType customSecurityHeaderType = null;
		UserIdPasswordType userIdPasswordType = null;
		JAXBContext jaxbContext = null;
		
		try {
			userIdPasswordType = new UserIdPasswordType();
			userIdPasswordType.setPassword(this.passwordService);
			userIdPasswordType.setUsername(this.userService);
			userIdPasswordType.setSignature(this.signatureService);
			
			customSecurityHeaderType = new CustomSecurityHeaderType();
			customSecurityHeaderType.setCredentials(userIdPasswordType);
			
			ObjectFactory objectFactory = new ObjectFactory();
			
			JAXBElement<CustomSecurityHeaderType> credentials = objectFactory.createRequesterCredentials(customSecurityHeaderType);
			jaxbContext = JAXBContext.newInstance(CustomSecurityHeaderType.class);
			
			WSBindingProvider provider = (WSBindingProvider) this.payPalService;

			provider.setOutboundHeaders(
				com.sun.xml.internal.ws.api.message.Headers.create((JAXBRIContext) jaxbContext, credentials)
			);
			
		}catch(Exception e) {
			Logger.error(e, "Erro ao tentar adicionar o header de autenticação do serviço.");
			throw new IllegalStateException(e);
		}
		
	}
	
	private void logPayload(Object object) {
		JAXBContext jaxbContext = null;
		StringWriter stringWriter = new StringWriter();
		
		try {
			jaxbContext = JAXBContext.newInstance(object.getClass());
			
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			
			marshaller.marshal(object, stringWriter);
			stringWriter.flush();
			
			Logger.info("### Request a ser enviado: %s", stringWriter);
			
		}catch(Exception e) {
			Logger.error(e, "Erro ao tentar registrar o log do serviço.");
		}
	}
	
	private void addProxySettings() {
		System.setProperty("http.proxyHost", "proxy.houston.hp.com");
        System.setProperty("http.proxyPort", "8080");
        System.setProperty("https.proxyHost", "proxy.houston.hp.com");
        System.setProperty("https.proxyPort", "8080");
	}
	
	public static Boolean foiExecutadoComSucesso(AckCodeType ackCodeType, List<ErrorType> errors) {
		Boolean result = Boolean.TRUE;
		
		if(!ackCodeType.equals(AckCodeType.SUCCESS) || (errors!=null && !errors.isEmpty()))
			result = Boolean.FALSE;
		
		return result;
	}
	
	public static String getInformacoesErro(List<ErrorType> errors) {
		StringBuffer result = new StringBuffer();
		result.append("");
		
		if(errors!=null) {
			for(ErrorType error : errors) {
				result.append("Código:").append(" ");
				result.append(error.getErrorCode()).append("\r\n");
				result.append("Descrição:").append(" ");
				result.append(error.getLongMessage()).append("\r\n").append("\r\n");
			}
		}
		return result.toString();
	}
	
	@Override
	public String checkout(Cliente cliente, CarrinhoProduto carrinho, BigDecimal valorFrete, Double valorPedido)
			throws GatewayServiceException {
		// TODO Auto-generated method stub
		return null;
	}
	
}
