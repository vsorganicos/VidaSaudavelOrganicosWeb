/**
 * 
 */
package jobs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import play.Logger;
import play.jobs.Job;
import controllers.Mail;
import controllers.Relatorios;

/**
 * <p>Relatório contendo os produtos que estão no estoque que serão entregues na semana.</p>
 * @author Felipe Guerra
 *
 */
//@On("0 19 23 ? * Sun")
public class RelatorioProdutoEstoque extends Job {
	
	@Override
	public void doJob() throws Exception {
		InputStream csv = null;
		String pathReport = null;
		OutputStream outputStream = null;
		File report = null;

		Logger.info("### Início do processo de envio de e-mail automático com o Relatório de Produtos do Estoque Aguardando Entrega. ###", "");
		csv = Relatorios.generateRelatorioProdutoEstoqueFornecedorCSV();
		
		Logger.info("### Relatório gerado? [%s - %s] ###", this.getClass().getName(), csv);
		
		if(csv!=null) {
			pathReport = System.getProperty("java.io.tmpdir") + File.separatorChar + Relatorios.RELATORIO_PRODUTO_ESTOQUE + "_" + new Date().getTime()+ ".csv";
			outputStream = new FileOutputStream(pathReport);

			int read;
			
			byte[] bytes = new byte[1024];

			while((read = csv.read(bytes))>-1) {
				outputStream.write(bytes, 0, read);
			}
			
			report = new File(pathReport);
			
			Logger.info("### Gerou o Relatório CSV? %s | path: %s ###", report.exists(), report.getAbsolutePath());

			csv.close();
			outputStream.flush();
			outputStream.close();
			
			Mail.sendRelatorioPedidosAguardandoEntrega("Relatório de Produtos em Estoque Aguardando Entregas", 
														"Vida Saudável Orgânicos<administrador@vidasaudavelorganicos.com.br>", 
														report.getAbsolutePath(), report.getName(),
														"felipe@vidasaudavelorganicos.com.br");
		}
	}
	
	@Override
	public void onException(Throwable e) {
		Logger.error(e, "Erro no processo de envio de e-mail automático com o Relatório de Produtos do Estoque Aguardando Entrega.");
	}
	
	@Override
	public void onSuccess() throws Exception {
		Logger.info("### Fim do processo de envio de e-mail automático com o Relatório de Produtos do Estoque Aguardando Entrega. ###", "");
	}
	
}
