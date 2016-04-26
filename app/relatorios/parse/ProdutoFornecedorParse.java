/**
 * 
 */
package relatorios.parse;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import play.Logger;
import util.CompactadorZip;
import util.PedidoNomeComparator;
import vo.ProdutoPedidoReportVO;

/**
 * @author Felipe Guerra
 * @version 1.0
 * @since 13/11/2013
 */
public class ProdutoFornecedorParse implements Serializable {

	private static final long serialVersionUID = 89786754632456871L;
	
	private List<ProdutoPedidoReportVO> produtosPorPedido = null;
	
	private StringBuffer tempfilePath = new StringBuffer(System.getProperty("java.io.tmpdir")).
											append(File.separatorChar);

	private StringBuffer caminhoCompletoRelatorio = null;
	private WritableSheet sheet = null;
	private WritableWorkbook workbook = null;
	private File relatorioExcel = null;
	private File arquivosCompactados = null;
	private int coluna = 0;
	private int linha = 0;
	
	/**
	 * @param dados
	 * @param tempfilePath arquivo onde será gravado os arquivos
	 */
	public ProdutoFornecedorParse(List<ProdutoPedidoReportVO> dados, String tempfilePath) {
		this.produtosPorPedido = dados;
		
		if(tempfilePath!=null)
			this.tempfilePath = new StringBuffer(tempfilePath);
	}
	
	public File createReport() {
		ProdutoPedidoReportVO fornecedor = null;
		List<ProdutoPedidoReportVO> produtosPedidoVO = new ArrayList<ProdutoPedidoReportVO>();
		//Calendar dataAtual = Calendar.getInstance();
		DateTime dataAtual = new DateTime();
		DateTimeFormatter format = DateTimeFormat.forPattern("ddMMYYYY");
		
		try {
			caminhoCompletoRelatorio = new StringBuffer();
			this.tempfilePath.append("PEDIDOS_FORNECEDOR-");
			this.tempfilePath.append(format.print(dataAtual));
			
			arquivosCompactados = new File(this.tempfilePath.toString());
			
			if(arquivosCompactados.exists())
				FileUtils.deleteDirectory(arquivosCompactados);

			arquivosCompactados.mkdir();
			
			initInstances(this.produtosPorPedido.get(0));
			
			fornecedor = this.produtosPorPedido.get(0);
			
			for(ProdutoPedidoReportVO vo : this.produtosPorPedido) {
				if(!fornecedor.getFornecedor().equalsIgnoreCase(vo.getFornecedor())) {
					addContent(produtosPedidoVO);
					
					initInstances(vo);
					
					linha = 0;
					
					produtosPedidoVO.clear();
				}
				fornecedor = vo;
				produtosPedidoVO.add(vo);
			}
			addContent(produtosPedidoVO);
						
			new CompactadorZip().criarZip(arquivosCompactados, arquivosCompactados.listFiles());
			
		}catch(Exception e) {
			Logger.error(e, "Ocorreu um erro na tentativa de gerar o Relatório de Produtos por Fornecedor no formato Excel.");
			throw new RuntimeException(e);
		}
		return new File(arquivosCompactados.getAbsolutePath()+".zip");
	}
		
	private void initInstances( 
								ProdutoPedidoReportVO vo
								) throws Exception {
		this.caminhoCompletoRelatorio = new StringBuffer();
		this.caminhoCompletoRelatorio.append(this.tempfilePath);
		this.caminhoCompletoRelatorio.append(File.separatorChar);
		this.caminhoCompletoRelatorio.append(vo.getFornecedor());
		this.caminhoCompletoRelatorio.append(".xls");
		
		this.relatorioExcel = new File(caminhoCompletoRelatorio.toString());
		
		this.relatorioExcel.createNewFile();
		
		this.workbook = Workbook.createWorkbook(this.relatorioExcel);
		
		this.sheet = workbook.createSheet(vo.getFornecedor(), 0);
	}
	
	private void closeWorkbook() throws Exception {
		workbook.write();
		workbook.close();
	}
	
	private void addContent(List<ProdutoPedidoReportVO> produtos) throws Exception {
		Collections.sort(produtos, new PedidoNomeComparator());
		
		for(ProdutoPedidoReportVO vo : produtos) {
			this.coluna = 0;
			
			Label descricao = new Label(coluna, linha, vo.getDescricao());
			sheet.addCell(descricao);
			coluna++;
			
			Number quantidade = new Number(coluna, linha, vo.getQuantidade());
			sheet.addCell(quantidade);
			
			this.linha++;
		}
		closeWorkbook();
	}
	
}
