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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;

import play.data.validation.Email;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.db.jpa.Model;
import play.libs.Crypto;

/**
 * @author guerrafe
 *
 */
@Entity
@Table(name="USUARIO")
public class Usuario extends Model {
	
	private static final long serialVersionUID = 8296928212368352947L;

	public Usuario() {}
	
	public Usuario(Long id, String senha, String email, Cliente cliente, Grupo grupo) {
		this.id = id;
		this.senha = senha;
		this.email = email;
		this.cliente = cliente;
		this.grupo = grupo;
	}
	
	@Transient
	public static final String CRYPTO_KEY = "GFsdjhgyu56s09-1";

	@MinSize(message="message.user.password.minsize", value=6)
	@Required(message="message.required.login.password")
	@Column(name="SENHA", nullable=false, length=300)
	private String senha;
	
	@Required(message="message.required.cliente.email")
	@Column(name="EMAIL", nullable=false, length=350)
	@Email(message="E-mail inválido!")
	private String email;
	
	@OneToOne(fetch=FetchType.EAGER, mappedBy="usuario")
	private Cliente cliente = null;
	
	@ManyToOne(fetch=FetchType.EAGER, cascade=CascadeType.ALL)
	private Grupo grupo = null;
	
	@Column(name="FLAG_EMAILMARKETING", nullable=true)
	private Boolean recebeMail = Boolean.FALSE;
	
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="usuario")
	private List<RespostaUsuarioQuestionario> respostasQuestionarios;
	
	@Column(name="FLAG_SENHAEXPIRADA", nullable=true)
	private Boolean senhaExpirada;
	
	@Column(name="DATA_SENHAEXPIRADA", nullable=true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataSenhaExpirada;

	/**
	 * @return the senha
	 */
	public String getSenha() {
		return senha;
	}

	/**
	 * @param senha the senha to set
	 */
	public void setSenha(String senha) {
		this.senha = senha;
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
	 * @return the grupo
	 */
	public Grupo getGrupo() {
		return grupo;
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
	 * @param grupo the grupo to set
	 */
	public void setGrupo(Grupo grupo) {
		this.grupo = grupo;
	}
		
	/**
	 * @return the recebeMail
	 */
	public Boolean getRecebeMail() {
		return recebeMail;
	}

	/**
	 * @param recebeMail the recebeMail to set
	 */
	public void setRecebeMail(Boolean recebeMail) {
		this.recebeMail = recebeMail;
	}

	public boolean isAdmin() {
		if(this.grupo==null || this.grupo.getNome()==null)
			return false;
			
		return this.grupo.getNome().equalsIgnoreCase(Grupo.ROLE_ADMIN);
	}
	
	public boolean isPartner() {
		if(this.grupo==null || this.grupo.getNome()==null)
			return false;
			
		return this.grupo.getNome().equalsIgnoreCase(Grupo.ROLE_PARTNER);
	}
	
	public boolean isEmployee() {
		if(this.grupo==null || this.grupo.getNome()==null)
			return false;
			
		return this.grupo.getNome().equalsIgnoreCase(Grupo.ROLE_EMPLOYEE);
	}
	
	@Transient
	public void encryptPassword() {
		if(!StringUtils.isEmpty(this.senha)) {
			this.setSenha(Crypto.encryptAES(this.senha, CRYPTO_KEY));
		}
	}
	
	@Transient
	public void decryptPassword() {
		if(!StringUtils.isEmpty(this.senha)) {
			this.setSenha(Crypto.decryptAES(this.senha, CRYPTO_KEY));
		}
	}
	
	@Transient
	public String encryptEmail(String email) {
		return Crypto.encryptAES(email, CRYPTO_KEY);
	}
	
	@Transient
	public static String decryptEmail(String email) {
		String result = Crypto.decryptAES(email, CRYPTO_KEY);
		
		return result;
	}
	
	@Override
	public String toString() {
		return this.cliente.getNome();
	}

	/**
	 * @return the respostasQuestionarios
	 */
	public List<RespostaUsuarioQuestionario> getRespostasQuestionarios() {
		if(this.respostasQuestionarios==null)
			this.respostasQuestionarios = new ArrayList<RespostaUsuarioQuestionario>();
		
		return respostasQuestionarios;
	}

	/**
	 * @param respostasQuestionarios the respostasQuestionarios to set
	 */
	public void setRespostasQuestionarios(
			List<RespostaUsuarioQuestionario> respostasQuestionarios) {
		this.respostasQuestionarios = respostasQuestionarios;
	}
	
	
	public Boolean verificaNecessidadeResponderQuestionario(Questionario questionario) {
		Boolean result = Boolean.FALSE;
		
		if(getRespostasQuestionarios().isEmpty() || questionario==null)
			result = Boolean.TRUE;
		else
			result = Boolean.TRUE;
			
			for(RespostaUsuarioQuestionario respostaUsuarioQuestionario : getRespostasQuestionarios()) {
				if(respostaUsuarioQuestionario.getQuestionario().equals(questionario)) {
					result = Boolean.FALSE;
					break;
				}
			}
		return result;
	}

	/**
	 * @return the senhaExpirada
	 */
	public Boolean getSenhaExpirada() {
		if(senhaExpirada==null)
			senhaExpirada = Boolean.FALSE;
		
		return senhaExpirada;
	}

	/**
	 * @param senhaExpirada the senhaExpirada to set
	 */
	public void setSenhaExpirada(Boolean senhaExpirada) {
		this.senhaExpirada = senhaExpirada;
	}

	/**
	 * @return the dataSenhaExpirada
	 */
	public Date getDataSenhaExpirada() {
		return dataSenhaExpirada;
	}

	/**
	 * @param dataSenhaExpirada the dataSenhaExpirada to set
	 */
	public void setDataSenhaExpirada(Date dataSenhaExpirada) {
		this.dataSenhaExpirada = dataSenhaExpirada;
	}

}
