/**
 * 
 */
package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import play.data.validation.Required;
import play.db.jpa.Model;

/**
 * <p>Classe que representa um Lote de Produtos</p>
 * @author Felipe Guerra
 * @version 1.0
 */
@Entity
@Table(name="LOTE")
public class Lote extends Model {

	private static final long serialVersionUID = -1613171995951814854L;
	
	@Required(message="O Lote é obrigatório")
	@Column(name="CODIGO", nullable=false)
	private String codigo;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATA_CADASTRO", nullable=false)
	private Date dataCadastro;
	
	@Column(name="DATA_ALTERACAO", nullable=true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataAlteracao;
	
	@Column(name="USUARIO_ALTERACAO", nullable=true, length=80)
	private String usuarioAlteracao;
	
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="lote")
	private List<ProdutoLoteEstoque> itens = null;
	
	/**
	 * @return the dataCadastro
	 */
	public Date getDataCadastro() {
		return dataCadastro;
	}

	/**
	 * @param dataCadastro the dataCadastro to set
	 */
	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	/**
	 * @return the dataAlteracao
	 */
	public Date getDataAlteracao() {
		return dataAlteracao;
	}

	/**
	 * @param dataAlteracao the dataAlteracao to set
	 */
	public void setDataAlteracao(Date dataAlteracao) {
		this.dataAlteracao = dataAlteracao;
	}

	/**
	 * @return the usuarioAlteracao
	 */
	public String getUsuarioAlteracao() {
		return usuarioAlteracao;
	}

	/**
	 * @param usuarioAlteracao the usuarioAlteracao to set
	 */
	public void setUsuarioAlteracao(String usuarioAlteracao) {
		this.usuarioAlteracao = usuarioAlteracao;
	}

	public List<ProdutoLoteEstoque> getItens() {
		if(this.itens==null)
			this.itens = new ArrayList<ProdutoLoteEstoque>();
		
		return itens;
	}
	
	public boolean addProdutoLoteEstoque(ProdutoLoteEstoque produtoLoteEstoque) {
		produtoLoteEstoque.setLote(this);
		
		return this.getItens().add(produtoLoteEstoque);
	}

	public void setItens(List<ProdutoLoteEstoque> itens) {
		this.itens = itens;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
}
