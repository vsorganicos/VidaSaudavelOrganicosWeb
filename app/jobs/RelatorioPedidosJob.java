/**
 * 
 */
package jobs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import models.Fornecedor;
import play.Logger;
import play.i18n.Messages;
import play.jobs.Job;
import play.jobs.On;
import vo.ProdutoPedidoReportVO;
import controllers.Mail;
import controllers.Relatorios;
import exception.SystemException;

/**
 * @author Felipe Guerra
 * @version 1.0
 */
@On("0 10 23 ? * Sun,Tue")
public class RelatorioPedidosJob extends Job {
	
	@Override
	public void onException(Throwable e) {
		Logger.error(e, "Erro no processo de envio de e-mail com o Relatorio de Pedidos por Fornecedor Aguardando Entrega.");
	}
	
	public void doJob() throws SystemException {
		enviarPedidosAguardandoEntrega();
		enviarRelatorioPedidos();
	}
	
	public void enviarPedidosPorFornecedorAguardandoEntrega(ProdutoPedidoReportVO vo, String caminhoArquivoFornecedor) throws SystemException {
		Fornecedor fornecedor = Fornecedor.getFornecedorPelaDescricao(vo.getFornecedor());
		
		Mail.sendPedidosPorFornecedorAguardandoEntrega("Pedido | Vida Saud�vel Org�nicos", Mail.EMAIL_CONTACT, 
														caminhoArquivoFornecedor, fornecedor, 
														Messages.get("application.email.admin", "").split(","));
	}
	
	public void enviarPedidosAguardandoEntrega() throws SystemException {
		Mail.sendRelatorioPedidosAguardandoEntrega("Pedidos p/ Fornecedores", 
													Mail.EMAIL_CONTACT, 
													Relatorios.generateRelatorioProdutoFornecedorExcel().getAbsolutePath(), 
													"Em anexo os pedidos para os fornecedores.", 
													Messages.get("application.email.admin", "").split(","));
	}
	
	private void enviarRelatorioPedidos() {
		String pathReport = null;
		InputStream pdfReader = null;
		OutputStream outputStream = null;
		File report = null;
		
		try {
			Logger.info("### Início do processo de envio de e-mail automático com o Relatório de Pedidos Aguardando Entrega. ###", "");
			pdfReader = Relatorios.exportarRelatorioPdf();
			pathReport = System.getProperty("java.io.tmpdir") + File.separatorChar + "RELATORIO_PEDIDOSPRODUTO_ENTREGA_" + new Date().getTime()+ ".pdf";
			outputStream = new FileOutputStream(pathReport);
			
			int read;
			
			byte[] bytes = new byte[1024];

			while((read = pdfReader.read(bytes))>-1) {
				outputStream.write(bytes, 0, read);
			}
			
			report = new File(pathReport);
			
			Logger.warn("### Gerou o PDF? %s ###", report.exists());

			pdfReader.close();
			outputStream.flush();
			outputStream.close();
			
			Mail.sendRelatorioPedidosAguardandoEntrega("Pedidos Aguardando Entrega", 
														Mail.EMAIL_CONTACT, 
														report.getAbsolutePath(), 
														report.getName(),
														Messages.get("application.email.admin", "").split(","));
			
		}catch(Throwable e) {
			Logger.error(e, "Erro no processo de envio de e-mail automático com o Relatório de Pedidos Aguardando Entrega.");
			
		}finally {
			Logger.info("### Fim do processo de envio de e-mail automático com o Relatório de Pedidos Aguardando Entrega. ###", "");
		}
	}
}
