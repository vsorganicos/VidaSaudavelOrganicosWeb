/**
 * 
 */
package business.pagamento.service;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import models.CarrinhoProduto;
import models.Cliente;

import org.w3c.dom.Document;

import play.Logger;
import play.i18n.Messages;
import play.libs.WS;
import play.libs.WS.HttpResponse;
import types.pagseguro.CheckoutPagSeguro;
import types.pagseguro.ItemPagSeguro;
import types.pagseguro.SenderPagSeguro;
import types.pagseguro.SenderPagSeguro.Phone;
import types.pagseguro.ShippingPagSeguro;
import business.pagamento.service.interfaces.GatewayService;
import exception.GatewayServiceException;

/**
 * @author Felipe Guerra
 * @version 1.0
 * @since 13/11/2013
 */
public class PagSeguroService implements GatewayService {

	private static final long serialVersionUID = 12089786753641L;
	
	private StringBuffer url = new StringBuffer();
	private String user = null;
	private String token = null;

	private PagSeguroService(String urlEndPoint, String userService, String token) {
		this.url.append(urlEndPoint);
		this.user = userService;
		this.token = token;
	}
	
	public static PagSeguroService newInstance(String urlEndPoint, String userService, String token) {
		return new PagSeguroService(urlEndPoint, userService, token);
	}

	/* (non-Javadoc)
	 * @see business.pagamento.service.interfaces.GatewayService#checkout()
	 */
	@Override
	public String checkout(Cliente cliente, CarrinhoProduto carrinho, BigDecimal valorFrete, Double valorPedido) throws GatewayServiceException {
		CheckoutPagSeguro checkout = null;
		SenderPagSeguro sender = null;
		ItemPagSeguro item = null;
		Phone phone = null;
		ShippingPagSeguro shipping = null;
		String token = null;
		
		try {
			checkout = new CheckoutPagSeguro();
			checkout.setCurrency(CheckoutPagSeguro.CURRENCY_BRL);
			
			sender = new SenderPagSeguro();
			sender.setEmail(cliente.getUsuario().getEmail());
			sender.setName(cliente.getNome());
			
			if(!cliente.getTelefones().isEmpty()) {
				phone = new Phone();
				phone.setAreaCode(cliente.getTelefones().get(0).getPrefixo());
				phone.setNumber(cliente.getTelefones().get(0).getNumero());
				sender.setPhone(phone);
			}
			checkout.setSender(sender);
			
			item = new ItemPagSeguro();
			item.setId(1L);
			item.setQuantity(1);
			item.setDescription("Carrinho: " + carrinho.getId());
			item.setAmount(valorFrete.add(BigDecimal.valueOf(valorPedido)));
			item.setWeight(0L);
			
			checkout.getItems().add(item);
			
			shipping = new ShippingPagSeguro();
			shipping.setType(3);
			
			checkout.setReference(carrinho.getId().toString());
			checkout.setShipping(shipping);
			checkout.setRedirectURL(Messages.get("application.return.url.pagseguro", carrinho.getId()));
			//addProxySettings();
			
			this.url.append("?");
			this.url.append("email");
			this.url.append("=");
			this.url.append(this.user);
			this.url.append("&");
			this.url.append("token");
			this.url.append("=");
			this.url.append(this.token);
			
			Logger.info("Vai tentar realizar um checkout no Gateway PagSeguro: %s", cliente.getNome());
			
			HttpResponse response = WS.url(this.url.toString()).setHeader("Content-Type", "application/xml").body(logPayload(checkout)).post();

			if(response.getStatus()!=200) {
				Logger.error("Não foi possível finalizar o pagamento no Gateway PagSeguro: %s", response.getString());
				throw new GatewayServiceException("Não foi possível finalizar o pagamento no Gateway PagSeguro: " + response.getString());
			}
			Document result = response.getXml();
			
			token = result.getElementsByTagName("code").item(0).getTextContent();
			
			Logger.info("Fim checkout no Gateway PagSeguro: %s", cliente.getNome());
			
		}catch(Exception e) {
			Logger.error(e, "Ocorreu um erro ao tentar realizar um checkout no Gateway PagSeguro");
			throw new GatewayServiceException("Ocorreu um erro ao tentar realizar um checkout no Gateway PagSeguro");
		}
		return token;
	}
	
	private OutputStream logPayload(Object object) {
		JAXBContext jaxbContext = null;
		OutputStream result = new ByteArrayOutputStream();
		
		try {
			jaxbContext = JAXBContext.newInstance(object.getClass());
			
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			marshaller.setProperty(Marshaller.JAXB_ENCODING, "ISO-8859-1");
			marshaller.marshal(object, result);
			
			Logger.info("### Request a ser enviado: %s", result);
			
		}catch(Exception e) {
			Logger.error(e, "Erro ao tentar registrar o log do serviço.");
		}
		return result;
	}
	
	private void addProxySettings() {
		System.setProperty("http.proxyHost", "proxy.houston.hp.com");
        System.setProperty("http.proxyPort", "8080");
        System.setProperty("https.proxyHost", "proxy.houston.hp.com");
        System.setProperty("https.proxyPort", "8080");
	}

}
