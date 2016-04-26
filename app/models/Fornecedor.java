/**
 * 
 */
package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Query;
import javax.persistence.Table;

import play.data.validation.Email;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.db.jpa.JPA;
import play.db.jpa.Model;
import business.produto.layout.LayoutArquivo;

/**
 * @author guerrafe
 *
 */
@Table(name="FORNECEDOR")
@Entity
@NamedQuery(name="findByNome", 
		query="from Fornecedor where UPPER(nome) LIKE :nome")
public class Fornecedor extends Model {

	private static final long serialVersionUID = 343556748359547283L;

	@Required(message="message.required.fornecedor.nome")
	@Column(name="NOME", length=180, nullable=false)
	private String nome;
	
	@MinSize(value=11, message="message.minsize.fornecedor.cnpj")
	@Required(message="message.minsize.fornecedor.cnpj")
	@Column(name="CNPJ", length=14, nullable=true)
	private String cnpj;
	
	@Column(name="FLAG_ATIVO", nullable=false)
	private Boolean ativo;
	
	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.REFRESH, mappedBy="fornecedor")
	private List<Produto> produtos;
	
	@Column(name="LAYOUT_ARQUIVO_PRODUTO", nullable=true)
	private LayoutArquivo layoutArquivo;
	
	@Column(name="EMAIL_CONTATO", nullable=true, length=300)
	@Required(message="message.required.cliente.email")
	@Email(message="E-mail inválido!")
	private String emailContato;
	
	@Column(name="EMAIL_VENDAS", nullable=true, length=300)
	private String emailVendas;
	
	@Column(name="INFO_ADICIONAL", nullable=true, length=3000)
	private String informacoesAdicionais;
	
	@Column(name="CONTATO_COMERCIAL", nullable=true, length=60)
	private String contatoComercial;
	
	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL, mappedBy="fornecedor")
	private List<Telefone> telefones = new ArrayList<Telefone>();
	
	/**
	 * @return the nome
	 */
	public String getNome() {
		return nome;
	}

	/**
	 * @param nome the nome to set
	 */
	public void setNome(String nome) {
		this.nome = nome;
	}

	/**
	 * @return the cnpj
	 */
	public String getCnpj() {
		return cnpj;
	}

	/**
	 * @param cnpj the cnpj to set
	 */
	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}

	/**
	 * @return the produtos
	 */
	public List<Produto> getProdutos() {
		if(produtos==null)
			produtos = new ArrayList<Produto>();
		
		return produtos;
	}

	/**
	 * @param produtos the produtos to set
	 */
	public void setProdutos(List<Produto> produtos) {
		this.produtos = produtos;
	}
	
	public void addProduto(Produto produto) {
		if(produto!=null) {
			getProdutos().add(produto);
			
			produto.setFornecedor(this);
		}
	}

	/**
	 * @return the ativo
	 */
	public Boolean getAtivo() {
		return ativo;
	}

	/**
	 * @param ativo the ativo to set
	 */
	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	/**
	 * @return the layoutArquivo
	 */
	public LayoutArquivo getLayoutArquivo() {
		return layoutArquivo;
	}

	/**
	 * @param layoutArquivo the layoutArquivo to set
	 */
	public void setLayoutArquivo(LayoutArquivo layoutArquivo) {
		this.layoutArquivo = layoutArquivo;
	}

	/**
	 * @return the emailContato
	 */
	public String getEmailContato() {
		return emailContato;
	}

	/**
	 * @param emailContato the emailContato to set
	 */
	public void setEmailContato(String emailContato) {
		this.emailContato = emailContato;
	}

	/**
	 * @return the telefones
	 */
	public List<Telefone> getTelefones() {
		return telefones;
	}

	/**
	 * @param telefones the telefones to set
	 */
	public void setTelefones(List<Telefone> telefones) {
		this.telefones = telefones;
	}

	public boolean addTelefone(Telefone t) {
		if(t==null)
			return false;
		
		t.setFornecedor(this);
		
		return getTelefones().add(t);
	}

	/**
	 * @return the informacoesAdicionais
	 */
	public String getInformacoesAdicionais() {
		return informacoesAdicionais;
	}

	/**
	 * @param informacoesAdicionais the informacoesAdicionais to set
	 */
	public void setInformacoesAdicionais(String informacoesAdicionais) {
		this.informacoesAdicionais = informacoesAdicionais;
	}

	/**
	 * @return the contatoComercial
	 */
	public String getContatoComercial() {
		return contatoComercial;
	}

	/**
	 * @param contatoComercial the contatoComercial to set
	 */
	public void setContatoComercial(String contatoComercial) {
		this.contatoComercial = contatoComercial;
	}

	/**
	 * @return the emailVendas
	 */
	public String getEmailVendas() {
		return emailVendas;
	}

	/**
	 * @param emailVendas the emailVendas to set
	 */
	public void setEmailVendas(String emailVendas) {
		this.emailVendas = emailVendas;
	}

	public static Fornecedor getFornecedorPelaDescricao(String nome) {
		Fornecedor result = null; 
		
		Query query = JPA.em().createNamedQuery("findByNome");
		query.setParameter("nome", nome);
		
		result = (Fornecedor) query.getSingleResult();
		
		return result;
	}
}
