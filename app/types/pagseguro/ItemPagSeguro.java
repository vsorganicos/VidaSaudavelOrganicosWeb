/**
 * 
 */
package types.pagseguro;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>Elemento que representa a tag item do Gateway de Pagamento PagSeguro</p>
 * @author Felipe Guerra
 * @version 1.0
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="item", propOrder={
		"id",
		"description",
		"quantity",
		"amount",
		"weight"
})
@XmlRootElement(name="item")
public class ItemPagSeguro {
	
	@XmlElement(name="id", required=true)
	private Long id;
	
	@XmlElement(name="description", required=true)
	private String description;
	
	@XmlElement(name="quantity", required=true)
	private Integer quantity;
	
	@XmlElement(name="amount", required=true)
	private BigDecimal amount;
	
	@XmlElement(name="weight", required=false)
	private Long weight;

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the quantity
	 */
	public Integer getQuantity() {
		return quantity;
	}

	/**
	 * @param quantity the quantity to set
	 */
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	/**
	 * @return the amount
	 */
	public BigDecimal getAmount() {
		return amount;
	}

	/**
	 * @param amount the amount to set
	 */
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	/**
	 * @return the weight
	 */
	public Long getWeight() {
		return weight;
	}

	/**
	 * @param weight the weight to set
	 */
	public void setWeight(Long weight) {
		this.weight = weight;
	}

}
