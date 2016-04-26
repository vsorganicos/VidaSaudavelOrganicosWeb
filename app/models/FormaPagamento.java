/**
 * 
 */
package models;

/**
 * @author guerrafe
 *
 */
public enum FormaPagamento {
	BOLETO(1, "Boleto"),
	CARTAO_CREDITO(2, "Cartão de Crédito"),
	DINHEIRO(3, "Dinheiro/Cheque"),
	CHEQUE(4, "Cheque"),
	PAYPAL(5, "PayPal"),
	PAGSEGURO(6, "PagSeguro");
	
	private FormaPagamento(Integer codigo, String descricao) {
		this.codigo = codigo;
		this.descricao = descricao;
	}
	
	private Integer codigo;
	private String descricao;
	/**
	 * @return the codigo
	 */
	public Integer getCodigo() {
		return codigo;
	}
	/**
	 * @param codigo the codigo to set
	 */
	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}
	/**
	 * @return the descricao
	 */
	public String getDescricao() {
		return descricao;
	}
	/**
	 * @param descricao the descricao to set
	 */
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	public static FormaPagamento getFormaPagamento(String descricao) {
		FormaPagamento result = null;
		
		for(FormaPagamento value : FormaPagamento.values()) {
			if(value.getDescricao().toLowerCase().contains(descricao.toLowerCase().trim())) {
				result = value;
				break;
			}
		}
		return result;
	}

	public static FormaPagamento getFormaPagamento(Integer codigo) {
		FormaPagamento result = null;
		
		for(FormaPagamento value : FormaPagamento.values()) {
			if(value.getCodigo().equals(codigo)) {
				result = value;
				break;
			}
		}
		return result;
	}
}
