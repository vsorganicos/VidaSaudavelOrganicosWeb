/**
 * 
 */
package util;

import java.io.Serializable;
import java.util.Comparator;

import vo.ProdutoPedidoReportVO;

/**
 * Classe que realiza a comparação dos objetos analisando a flag que indica se o produto exige refrigeração ou não.
 * @author Felipe Guerra
 * @version 1.0
 */
public class PedidoFornecedorVORefrigeracaoComparator implements Comparator<ProdutoPedidoReportVO>, Serializable {
	
	private static final long serialVersionUID = -7117161863809989800L;
	
	public PedidoFornecedorVORefrigeracaoComparator() {
		// TODO Auto-generated constructor stub
	}
	
	
	@Override
	public int compare(ProdutoPedidoReportVO o1, ProdutoPedidoReportVO o2) {
		if((o1==null || o2==null)) 
			return 0;
		else
			return o1.getEhRefrigerado().compareTo(o2.getEhRefrigerado());
	}
}
