/**
 * 
 */
package business.cliente.service;

import java.io.Serializable;
import java.net.URL;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceException;

import org.w3c.dom.Document;

import play.Logger;
import play.cache.Cache;
import play.libs.WS;

import com.grepcepws.entity.xsd.Cep;
import com.grepcepws.entity.xsd.ObterCepResponse;
import com.grepcepws.ws.GrepCep;
import com.grepcepws.ws.GrepCepPortType;

/**
 * @author Felipe Guerra
 *
 */
public class EnderecoService implements Serializable {

	private static final long serialVersionUID = -8089352392001330630L;

	public static final String TOKEN = "201208173556399O8EEZCMGIDEVCYOWVPKH";
	
	private String urlEndPoint;
	private String token;
	private GrepCepPortType portType;
	
	private EnderecoService() {}
	
	private EnderecoService(String url) {
		//addProxySettings();
		this.urlEndPoint = url;
	}
	
	public Cep consultarEnderecoWSCep() {
		Cep cep = null;
		
		try {
			Document document = WS.url(this.urlEndPoint).get().getXml();
			
			cep = new Cep();
			
			cep.setBairro(new JAXBElement<String>(new QName("http://entity.grepcepws.com/xsd"), String.class, document.getElementsByTagName("bairro").item(0).getTextContent()));
			cep.setLogradouro(new JAXBElement<String>(new QName("http://entity.grepcepws.com/xsd"), String.class, document.getElementsByTagName("logradouro").item(0).getTextContent()));
			cep.setCidade(new JAXBElement<String>(new QName("http://entity.grepcepws.com/xsd"), String.class, document.getElementsByTagName("cidade").item(0).getTextContent()));
			cep.setEstado(new JAXBElement<String>(new QName("http://entity.grepcepws.com/xsd"), String.class, document.getElementsByTagName("uf").item(0).getTextContent()));
			
		}catch(Exception e) {
			Logger.error(e, "Ocorreu um erro no serviço de consulta de CEP. [%s]", this.urlEndPoint);
			throw new RuntimeException("Ocorreu um erro no serviço de consulta de CEP. ["+this.urlEndPoint+"]");
		}
		return cep;
	}

	private EnderecoService(String url, String token) {
		try {
			this.urlEndPoint = url;
			this.token = token;
			
			this.portType = Cache.get("grepCep", GrepCepPortType.class);
			
			if(this.portType==null) {
				GrepCep grepCep = new GrepCep(new URL(urlEndPoint), new QName("http://ws.grepcepws.com", "GrepCep"));

				this.portType = grepCep.getGrepCepHttpSoap12Endpoint();
				((BindingProvider) this.portType).getRequestContext().put("com.sun.xml.ws.request.timeout", Integer.valueOf(20000));
				((BindingProvider) this.portType).getRequestContext().put("com.sun.xml.ws.connect.timeout", Integer.valueOf(20000));
				((BindingProvider) this.portType).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, url);
				
				Cache.add("grepCep", this.portType, "24h");
			}
			
		}catch(Exception e) {
			Logger.error(e, "Não foi possível se conectar o WS de Cep.");
			throw new IllegalStateException(e);
		}
	}
	
	public Cep consultarEndereco(String cep) throws WebServiceException {
		ObterCepResponse result = null;
		Cep _cep = null;
		
		try {
			Logger.info("####### Início - Consultar CEP %s - Endpoint: %s ########", cep, this.urlEndPoint);
			result = this.portType.obterEnderecoCep(cep, this.token);
			
			if(result!=null && !result.getCepList().isEmpty()) {
				_cep = result.getCepList().get(0);
			}
			
			Logger.debug("####### Fim - Consultar o CEP: %s ########", cep);
			
		}catch(WebServiceException e) {
			Logger.error(e, "Ocorreu um erro no serviço de consulta de CEP. Param: %s", cep);
			throw e;
			
		}catch(Exception ex) {
			Logger.error(ex, "Erro ao tentar consultar o CEP: %s", cep);
		}
		return _cep;
	}
	
	/**
	 * Factory
	 * @param endPoint
	 * @param token
	 * @return nova instância
	 */
	public static EnderecoService newInstance(String endPoint, String token) {
		return new EnderecoService(endPoint, token);
	}
	
	public static EnderecoService newInstance(String endPoint) {
		return new EnderecoService(endPoint);
	}
	
	private void addProxySettings() {
		System.setProperty("http.proxyHost", "proxy.houston.hp.com");
        System.setProperty("http.proxyPort", "8080");
        System.setProperty("https.proxyHost", "proxy.houston.hp.com");
        System.setProperty("https.proxyPort", "8080");
	}
	
}
