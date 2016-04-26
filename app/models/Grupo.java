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
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import play.data.validation.Required;
import play.db.jpa.Model;

/**
 * @author guerrafe
 *
 */
@Cache(usage=CacheConcurrencyStrategy.READ_ONLY)
@Entity
@Table(name="GRUPO")
public class Grupo extends Model {

	private static final long serialVersionUID = -3555867852698899928L;

	public static final String ROLE_ADMIN = "ADMIN";
	
	public static final String ROLE_PARTNER = "PARCEIRO";
	
	public static final String ROLE_EMPLOYEE = "COLABORADOR";
	
	@Required
	@Column(name="NOME", length=80, nullable=false)
	private String nome;
	
	@OneToMany(cascade=CascadeType.REFRESH, fetch=FetchType.LAZY, mappedBy="grupo")
	private List<Usuario> usuarios = null;

	public Grupo(String nome) {
		this.nome = nome;
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
	 * @return the usuarios
	 */
	public List<Usuario> getUsuarios() {
		if(usuarios==null)
			usuarios = new ArrayList<Usuario>();
		
		return usuarios;
	}
	
	public void addUsuario(Usuario usuario) {
		if(usuario!=null) {
			this.getUsuarios().add(usuario);
			usuario.setGrupo(this);
		}
	}

	/**
	 * @param usuarios the usuarios to set
	 */
	public void setUsuarios(List<Usuario> usuarios) {
		this.usuarios = usuarios;
	}

}
