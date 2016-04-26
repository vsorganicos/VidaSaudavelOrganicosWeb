/**
 * 
 */
package models;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import play.db.jpa.Model;

/**
 * @author Felipe G. de Oliveira
 * @version 1.2
 */
@Entity
@Table(name="CESTA_PRODUTO")
public class CestaProduto extends Model implements Comparable<CestaProduto> {

	private static final long serialVersionUID = -9198765234567889L;
	
	@ManyToOne(fetch=FetchType.EAGER)
	private CestaPronta cesta;
	
	@ManyToOne(fetch=FetchType.EAGER)
	private Produto produto;
	
	public CestaPronta getCesta() {
		return cesta;
	}

	public void setCesta(CestaPronta cesta) {
		this.cesta = cesta;
	}

	public Produto getProduto() {
		return produto;
	}

	public void setProduto(Produto produto) {
		this.produto = produto;
	}

	@Override
	public int compareTo(CestaProduto o) {
		if(o==null)
			return 0;
		
		return this.produto.id.compareTo(o.getProduto().id);	
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((cesta == null) ? 0 : cesta.hashCode());
		result = prime * result + ((produto == null) ? 0 : produto.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		CestaProduto other = (CestaProduto) obj;
		if (cesta == null) {
			if (other.cesta != null)
				return false;
		} else if (!cesta.equals(other.cesta))
			return false;
		if (produto == null) {
			if (other.produto != null)
				return false;
		} else if (!produto.equals(other.produto))
			return false;
		return true;
	}
	
	
}
