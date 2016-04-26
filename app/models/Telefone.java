/**
 * 
 */
package models;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import play.data.validation.Required;
import play.db.jpa.Model;

/**
 * @author Felipe G. de Oliveira
 * @since 12/09/2011
 */
@Entity
@Table(name="TELEFONE")
public class Telefone extends Model implements Serializable {

	private static final long serialVersionUID = -9354565624562451L;

	public enum TelefoneTipo {
		RESIDENCIAL(1, "Residencial"),
		CELULAR(2, "Celular"),
		COMERCIAL(3, "Comercial");
		
		private TelefoneTipo(Integer cod, String descr) {
			this.codigo = cod;
			this.descricao = descr;
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
		
	}
	
	public Telefone(Cliente cliente) {
		this.cliente = cliente;
	}
	
	public Telefone() {
		// TODO Auto-generated constructor stub
	}
	
	@Required(message="message.required.phone.prefixo")
	@Column(name="PREFIXO", nullable=false, length=3)
	private String prefixo;
	
	@Required(message="message.required.phone.numero")
	@Column(name="NUMERO", nullable=false, length=9)
	private String numero;

	@Column(name="TIPO", nullable=false, length=1)
	private TelefoneTipo tipo;

	@ManyToOne(cascade=CascadeType.REFRESH, fetch=FetchType.EAGER)
	private Cliente cliente;
	
	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	private Fornecedor fornecedor;
	
	/**
	 * @return the prefixo
	 */
	public String getPrefixo() {
		return prefixo;
	}

	/**
	 * @param prefixo the prefixo to set
	 */
	public void setPrefixo(String prefixo) {
		this.prefixo = prefixo;
	}

	/**
	 * @return the numero
	 */
	public String getNumero() {
		return numero;
	}

	/**
	 * @param numero the numero to set
	 */
	public void setNumero(String numero) {
		this.numero = numero;
	}

	/**
	 * @return the tipo
	 */
	public TelefoneTipo getTipo() {
		return tipo;
	}

	/**
	 * @param tipo the tipo to set
	 */
	public void setTipo(TelefoneTipo tipo) {
		this.tipo = tipo;
	}

	/**
	 * @return the cliente
	 */
	public Cliente getCliente() {
		return cliente;
	}

	/**
	 * @param cliente the cliente to set
	 */
	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}
	
	/**
	 * @return the fornecedor
	 */
	public Fornecedor getFornecedor() {
		return fornecedor;
	}

	/**
	 * @param fornecedor the fornecedor to set
	 */
	public void setFornecedor(Fornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}
	
	public String toString() {
		return this.prefixo+this.numero;
	}

	@Transient
	public static TelefoneTipo findById(Integer id) {
		TelefoneTipo result = null;
		
		if(id!=null) {
			for(TelefoneTipo _telefoneTipo : TelefoneTipo.values())
				if(_telefoneTipo.getCodigo().equals(id)) {
					result = _telefoneTipo;
					break;
				}
		}
		return result;
	}

}
