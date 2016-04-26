/**
 * 
 */
package types.pagseguro;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Felipe Guerra
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="shipping", propOrder={
		"type"
})
@XmlRootElement(name="shipping")
public class ShippingPagSeguro implements Serializable {

	private static final long serialVersionUID = -2312004263599300378L;
	
	@XmlElement(name="type", required=true)
	private Integer type;

	/**
	 * @return the type
	 */
	public Integer getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(Integer type) {
		this.type = type;
	} 

	
}
