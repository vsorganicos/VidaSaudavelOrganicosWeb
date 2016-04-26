/**
 * 
 */
package types.pagseguro;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>Elemento que representar a tag checkout do Gateway de Pagamento PagSeguro</p>
 * @author Felipe Guerra
 * @version 1.0
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="checkout", propOrder={
		"currency",
		"reference",
		"items",
		"sender",
		"shipping",
		"redirectURL"
})
@XmlRootElement(name="checkout")
public class CheckoutPagSeguro implements Serializable {
	
	private static final long serialVersionUID = -3111936776352181366L;

	public static final String CURRENCY_BRL = "BRL"; 
	
	@XmlElement(name="currency", required=true)
	private String currency = null;
	
	@XmlElement(name="reference", required=true)
	private String reference = null;
	
	@XmlElementWrapper(name="items")
	@XmlElement(name="item")
	private List<ItemPagSeguro> items = new ArrayList<ItemPagSeguro>(0);
	
	@XmlElement(name="sender", required=true, nillable=true)
	private SenderPagSeguro sender;
	
	@XmlElement(name="shipping", required=true, nillable=true)
	private ShippingPagSeguro shipping;
	
	@XmlElement(name="redirectURL", required=true, nillable=true)
	private String redirectURL;

	/**
	 * @return the currency
	 */
	public String getCurrency() {
		return currency;
	}

	/**
	 * @param currency the currency to set
	 */
	public void setCurrency(String currency) {
		this.currency = currency;
	}

	/**
	 * @return the reference
	 */
	public String getReference() {
		return reference;
	}

	/**
	 * @param reference the reference to set
	 */
	public void setReference(String reference) {
		this.reference = reference;
	}

	/**
	 * @return the items
	 */
	public List<ItemPagSeguro> getItems() {
		return items;
	}

	/**
	 * @param items the items to set
	 */
	public void setItems(List<ItemPagSeguro> items) {
		this.items = items;
	}

	/**
	 * @return the sender
	 */
	public SenderPagSeguro getSender() {
		return sender;
	}

	/**
	 * @param sender the sender to set
	 */
	public void setSender(SenderPagSeguro sender) {
		this.sender = sender;
	}

	/**
	 * @return the shipping
	 */
	public ShippingPagSeguro getShipping() {
		return shipping;
	}

	/**
	 * @param shipping the shipping to set
	 */
	public void setShipping(ShippingPagSeguro shipping) {
		this.shipping = shipping;
	}

	/**
	 * @return the redirectURL
	 */
	public String getRedirectURL() {
		return redirectURL;
	}

	/**
	 * @param redirectURL the redirectURL to set
	 */
	public void setRedirectURL(String redirectURL) {
		this.redirectURL = redirectURL;
	}
	
}
