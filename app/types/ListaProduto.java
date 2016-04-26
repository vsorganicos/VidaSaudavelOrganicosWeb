/**
 * 
 */
package types;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import models.Produto;

/**
 * <p>Classe utilizada para WS.</p>
 * @author Felipe G. de Oliveira
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="ListaProduto", propOrder={
		"produtos"
})
@XmlRootElement(name="ListaProduto")
public class ListaProduto extends ArrayList<Produto> {

	private static final long serialVersionUID = 4169724716010279939L;
	
	@XmlElement(name="Produto", required=true, nillable=true)
	private List<Produto> produtos;
	
	public ListaProduto() {
	}
	
	public ListaProduto(List<Produto> prods) {
		this.produtos = prods;
	}

	/**
	 * @return the produtos
	 */
	public List<Produto> getProdutos() {
		return produtos;
	}

	/**
	 * @param produtos the produtos to set
	 */
	public void setProdutos(List<Produto> produtos) {
		if(this.produtos==null)
			this.produtos = new ArrayList<Produto>();
		
		this.produtos = produtos;
	}

}
