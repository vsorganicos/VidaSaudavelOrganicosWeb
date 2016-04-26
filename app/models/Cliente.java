/**
 * 
 */
package models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import play.data.validation.MaxSize;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.db.jpa.Model;

/**
 * @author Felipe G. de Oliveira
 * @version 1.0
 * @since 01/07/2011
 */
@NamedQueries(value={
	@NamedQuery(name="pesquisarClienteComAlgumPedidoValido", query="select DISTINCT(cli) from Cliente cli inner join cli.pedidos as ped " +
																	"inner join cli.usuario as user " +
																	"WHERE user.recebeMail = true " +
																	"AND cli.id NOT IN (SELECT cliente.id FROM CupomDesconto as cp WHERE cp.ativo = true) " +
																	"AND ped.codigoEstadoPedido IN (:parameters) " +
																	"ORDER BY cli.id DESC"
				),
	@NamedQuery(name="pesquisarClientesCupomDesconto", query="select DISTINCT(cli) from Cliente cli " +
																	"inner join cli.usuario as user " +
																	"WHERE " +
																	"cli.id IN (:parameters) " +
																	"ORDER BY cli.id DESC"
				)
	}
)
@Entity
@Table(name="CLIENTE")
public class Cliente extends Model implements Serializable {

	private static final long serialVersionUID = -1574180722632855582L;

	@Required(message="message.required.cliente.nome")
	@Column(name="NOME", nullable=false, length=300)
	private String nome;

	@Required(message="message.required.cliente.dataNascimento") 
	@Temporal(TemporalType.DATE)
	@Column(name="DATA_NASCIMENTO", nullable=false)
	private Date dataNascimento;
	
	@Required(message="message.required.cliente.rg")
	@Column(name="RG", nullable=false, length=10)
	private String rg;
	
	@Required(message="message.required.cliente.cpf")
	@Column(name="CPF", nullable=false, length=11, unique=true)
	@MinSize(11)
	@MaxSize(11)
	private String cpf;
	
	@Required(message="message.required.cliente.estadoCivil")
	@Column(name="ESTADO_CIVIL", nullable=false, length=30)
	private String estadoCivil;
	
	@Required(message="message.required.cliente.sexo")
	@Column(name="SEXO", nullable=false, length=1)
	private String sexo;
	
	@Column(name="DATA_CADASTRO", nullable=false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataCadastro;
	
	@Column(name="FLAG_ATIVO", nullable=false)
	private Boolean ativo;
	
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY)
	private List<Endereco> enderecos = null;
	
	@OneToOne(fetch=FetchType.EAGER, cascade=CascadeType.ALL)
	private Usuario usuario = null;
	
	@OneToMany(cascade=CascadeType.REFRESH, fetch=FetchType.LAZY, mappedBy="cliente")
	private List<CarrinhoProduto> compras = null;
	
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="cliente")
	private List<Pedido> pedidos = null;

	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="cliente")
	private List<Telefone> telefones;
	
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="cliente")
	private List<CupomDesconto> cupons = null;
	
	public Cliente(String nome, Date dataNascimento) {
		this.nome = nome;
		this.dataNascimento = dataNascimento;
	}
	
	public Cliente() {
		
	}

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
	 * @return the dataNascimento
	 */
	public Date getDataNascimento() {
		return dataNascimento;
	}

	/**
	 * @param dataNascimento the dataNascimento to set
	 */
	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	/**
	 * @return the rg
	 */
	public String getRg() {
		return rg;
	}

	/**
	 * @param rg the rg to set
	 */
	public void setRg(String rg) {
		this.rg = rg;
	}

	/**
	 * @return the cpf
	 */
	public String getCpf() {
		return cpf;
	}

	/**
	 * @param cpf the cpf to set
	 */
	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	/**
	 * @return the estadoCivil
	 */
	public String getEstadoCivil() {
		return estadoCivil;
	}

	/**
	 * @param estadoCivil the estadoCivil to set
	 */
	public void setEstadoCivil(String estadoCivil) {
		this.estadoCivil = estadoCivil;
	}

	/**
	 * @return the sexo
	 */
	public String getSexo() {
		return sexo;
	}

	/**
	 * @param sexo the sexo to set
	 */
	public void setSexo(String sexo) {
		this.sexo = sexo;
	}

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
	
	public List<Endereco> getEnderecos() {
		if(enderecos==null)
			this.enderecos = new ArrayList<Endereco>();
			
		return enderecos;
	}
	
	public void addEndereco(Endereco endereco) {
		if(endereco!=null) {
			getEnderecos().add(endereco);
			endereco.setCliente(this);
		}
	}
	
	@Override
	public String toString() {
		StringBuilder campos = new StringBuilder();
		campos.append(this.nome).append("\n");
		campos.append(this.dataNascimento).append("\n");
		campos.append(this.cpf).append("\n");
		campos.append(this.rg).append("\n");
		campos.append(this.sexo).append("\n");
		campos.append(this.ativo).append("\n");
		campos.append(this.estadoCivil).append("\n");
		campos.append(this.dataCadastro);
		
		return campos.toString();
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
	 * @return the usuario
	 */
	public Usuario getUsuario() {
		return usuario;
	}

	/**
	 * @param usuario the usuario to set
	 */
	public void setUsuario(Usuario usuario) {
		if(usuario!=null)
			usuario.setCliente(this);
		
		this.usuario = usuario;
	}

	/**
	 * @return the compras
	 */
	public List<CarrinhoProduto> getCompras() {
		if(compras==null)
			compras = new ArrayList<CarrinhoProduto>();
		
		return compras;
	}

	/**
	 * @param compras the compras to set
	 */
	public void setCompras(List<CarrinhoProduto> compras) {
		this.compras = compras;
	}

	/**
	 * @return the pedidos
	 */
	public List<Pedido> getPedidos() {
		return pedidos;
	}

	/**
	 * @param pedidos the pedidos to set
	 */
	public void setPedidos(List<Pedido> pedidos) {
		if(this.pedidos==null)
			this.pedidos = new ArrayList<Pedido>();
		
		this.pedidos = pedidos;
	}
	
	public void addTelefone(Telefone telefone) {
		if(telefone!=null) {
			telefone.setCliente(this);
			this.getTelefones().add(telefone);
		}
	}

	/**
	 * @return the telefones
	 */
	public List<Telefone> getTelefones() {
		if(this.telefones==null)
			this.telefones = new ArrayList<Telefone>();
		
		return telefones;
	}

	/**
	 * @param telefones the telefones to set
	 */
	public void setTelefones(List<Telefone> telefones) {
		this.telefones = telefones;
	}

	/**
	 * @return the cupons
	 */
	public List<CupomDesconto> getCupons() {
		if(cupons==null)
			this.cupons = new ArrayList<CupomDesconto>();
			
		return cupons;
	}

	/**
	 * @param cupons the cupons to set
	 */
	public void setCupons(List<CupomDesconto> cupons) {
		this.cupons = cupons;
	}

	/**
	 * @param enderecos the enderecos to set
	 */
	public void setEnderecos(List<Endereco> enderecos) {
		this.enderecos = enderecos;
	}

	public Boolean estaNaCapital() {
		Boolean result = null;
		
		if(!this.getEnderecos().isEmpty()) {
			result = "Sao Paulo".equalsIgnoreCase(this.getEnderecos().get(0).getCidade().trim()) || "São Paulo".equalsIgnoreCase(this.getEnderecos().get(0).getCidade().trim());
			
		}
		return result;
	}
	
	public String getPrimeiroNome() {
		String result = "";
		
		if(this.getNome()!=null) {
			String[] pieces = this.getNome().split(" ");
			result = pieces!=null ? pieces[0] : "";
		}
		return result;
	}
	
	public static Telefone getTelefoneCelular(Long idCliente) {
		Cliente cliente = Cliente.findById(idCliente);
		Telefone result = null;
		
		for(Telefone fone : cliente.getTelefones()) {
			if(fone.getTipo().equals(Telefone.TelefoneTipo.CELULAR)) {
				result = fone;
				break;
			}
		}
		return result;
	}
	
	@Transient
	public Date getUltimoPedido() {
		Date result = null;
		
		Pedido pedido =  Pedido.findUltimoPedidoCliente(id);
		
		if(pedido!=null)
			result = pedido.getDataPedido();
		
		return result;
	}
	
}
