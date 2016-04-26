/**
 * 
 */
package util;

import java.io.Serializable;
import java.util.Comparator;

import models.Produto;

/**
 * @author guerrafe
 *
 */
public class ProdutoComparator implements Comparator<Produto>, Serializable {
	
	private static final long serialVersionUID = 1503152973023315092L;
	
	private boolean crescente = true;
	
	public ProdutoComparator(boolean decrescente) {
		this.crescente = decrescente;
	}

	@Override
	public int compare(Produto o1, Produto o2) {
		if(o1==null || o1.getDescricao()==null || o2==null || o2.getDescricao()==null)
			return 0;
		else
			if(crescente)
				return o1.getDescricao().compareTo(o2.getDescricao());
			else
				return o2.getDescricao().compareTo(o1.getDescricao());
	}

}
