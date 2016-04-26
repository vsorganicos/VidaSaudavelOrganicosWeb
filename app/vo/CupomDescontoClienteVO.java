/**
 * 
 */
package vo;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Felipe Guerra
 * @version 1.0
 */
public class CupomDescontoClienteVO implements Serializable, Comparable<CupomDescontoClienteVO> {
	
	private static final long serialVersionUID = 3451723516037665369L;

	private Long id;
	private BigDecimal desconto;
	private Integer diasValidadeCupom;
	private String codigo;
	
	@Override
	public int compareTo(CupomDescontoClienteVO other) {
		if(other.getId()==null)
			return 0;
		else
			return this.id.compareTo(other.id);
	}

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
	 * @return the desconto
	 */
	public BigDecimal getDesconto() {
		return desconto;
	}

	/**
	 * @param desconto the desconto to set
	 */
	public void setDesconto(BigDecimal desconto) {
		this.desconto = desconto;
	}

	/**
	 * @return the validadeCupom
	 */
	public Integer getDiasValidadeCupom() {
		return diasValidadeCupom;
	}

	/**
	 * @param validadeCupom the validadeCupom to set
	 */
	public void setDiasValidadeCupom(Integer validadeCupom) {
		this.diasValidadeCupom = validadeCupom;
	}

	/**
	 * @return the codigo
	 */
	public String getCodigo() {
		return codigo;
	}

	/**
	 * @param codigo the codigo to set
	 */
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	
}
