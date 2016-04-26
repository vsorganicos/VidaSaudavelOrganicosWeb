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

import org.apache.commons.lang.StringUtils;

/**
 * @author guerrafe
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="sender", propOrder={
		"name",
		"email",
		"phone"
})
@XmlRootElement(name="sender")
public class SenderPagSeguro implements Serializable {

	private static final long serialVersionUID = -395458633237785076L;

	@XmlElement(name="name", required=true)
	private String name;
	
	@XmlElement(name="email", required=true)
	private String email;
	
	@XmlElement(name="phone", required=false)
	private Phone phone;
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the phone
	 */
	public Phone getPhone() {
		return phone;
	}

	/**
	 * @param phone the phone to set
	 */
	public void setPhone(Phone phone) {
		this.phone = phone;
	}
	
public static class Phone {
		
	private String areaCode;
	
	private String number;

	/**
	 * @return the areaCode
	 */
	public String getAreaCode() {
		return areaCode;
	}

	/**
	 * @param areaCode the areaCode to set
	 */
	public void setAreaCode(String areaCode) {
		if(!StringUtils.isEmpty(areaCode)) {
			this.areaCode = String.valueOf(Integer.parseInt(areaCode));
		}else {
			this.areaCode = areaCode;
		}
	}

	/**
	 * @return the number
	 */
	public String getNumber() {
		return number.replaceAll("-", "");
	}

	/**
	 * @param number the number to set
	 */
	public void setNumber(String number) {
		this.number = number;
	}
	
}
}
