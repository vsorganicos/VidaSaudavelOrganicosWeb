/**
 * 
 */
package util;

import java.io.Serializable;
import java.util.Comparator;

import vo.ProdutoPedidoReportVO;

/**
 * Classe utilitária que compara uma lista de <code>ProdutoPedidoReportVO</code> através do nome do fornecedor. 
 * @author Felipe G. de Oliveira
 *
 */
public class PedidoFornecedorComparator implements Comparator<ProdutoPedidoReportVO>, Serializable {
	
	private static final long serialVersionUID = -7009117161863826745L;
	
	private boolean decrescente = false;
	
	public PedidoFornecedorComparator(boolean decrescente) {
		this.decrescente = decrescente; 
	}
	
	public PedidoFornecedorComparator() {
	}
	
	@Override
	public int compare(ProdutoPedidoReportVO o1, ProdutoPedidoReportVO o2) {
		if((o1==null || o2==null) || (o1.getFornecedor()==null || o2.getFornecedor()==null))
			return 0;
		else
			if(!decrescente)
				return o1.getFornecedor().compareTo(o2.getFornecedor());
			else
				return o2.getFornecedor().compareTo(o1.getFornecedor());
	}

}
