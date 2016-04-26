/**
 * 
 */
package util;

import java.io.Serializable;
import java.util.Comparator;

import vo.ProdutoPedidoReportVO;

/**
 * <p>
 * 	Classe utilitária responsável pelo ordenamento através da descrição do produto 
 * </p>
 * @author Felipe Guerra
 * @version 1.0
 */
public class PedidoNomeComparator implements Comparator<ProdutoPedidoReportVO>, Serializable {

	private static final long serialVersionUID = -117161880976638267L;
	
	private boolean decrescente = false;
	
	public PedidoNomeComparator(boolean decrescente) {
		this.decrescente = decrescente; 
	}
	
	public PedidoNomeComparator() {
	}
	
	@Override
	public int compare(ProdutoPedidoReportVO o1, ProdutoPedidoReportVO o2) {
		if((o1==null || o2==null) || (o1.getDescricao()==null || o2.getDescricao()==null))
			return 0;
		else
			if(!decrescente)
				return o1.getDescricao().compareTo(o2.getDescricao());
			else
				return o2.getDescricao().compareTo(o1.getDescricao());
	}


}
