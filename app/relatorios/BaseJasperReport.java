package relatorios;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JExcelApiExporter;
import net.sf.jasperreports.engine.export.JRGraphics2DExporter;
import net.sf.jasperreports.engine.export.JRGraphics2DExporterParameter;
import play.Logger;
import vo.CupomDescontoClienteVO;
import vo.PedidoProdutoEntregaReportVO;

public class BaseJasperReport {
	
	public static InputStream generateExcelReport(String pathJasperReport, String reportDefFile, Map<?, ?> reportParams, List<PedidoProdutoEntregaReportVO> pedidos) {
		OutputStream stream = new ByteArrayOutputStream();
		
		try {
			JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(pedidos);
			
			JasperPrint print = JasperFillManager.fillReport(pathJasperReport, reportParams, dataSource);
			
			JRExporter exporter = new JExcelApiExporter();
			
			exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, stream);
			exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);
			
			exporter.exportReport();

		}catch(Exception e) {
			Logger.error(e, "Erro ao tentar gerar o relatório.");
			throw new RuntimeException(e);
		}
		return new ByteArrayInputStream(((ByteArrayOutputStream) stream).toByteArray());
	}

	public static InputStream generatePdfReport(String pathJasperReport, String reportDefFile, Map<?, ?> reportParams, List<PedidoProdutoEntregaReportVO> pedidos) {
		OutputStream stream = new ByteArrayOutputStream();
		
		try {
			JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(pedidos);
			
			JasperPrint print = JasperFillManager.fillReport(pathJasperReport, reportParams, dataSource);
			
			byte[] bytes = JasperExportManager.exportReportToPdf(print);
			
			stream.write(bytes, 0, bytes.length);

		}catch(Exception e) {
			Logger.error(e, "Erro ao tentar gerar o relatório.");
			throw new RuntimeException(e);
		}
		return new ByteArrayInputStream(((ByteArrayOutputStream) stream).toByteArray());
	}
	
	public static InputStream generatePdfCupom(String pathJasperReport, String reportDefFile, Map<?, ?> reportParams, List<CupomDescontoClienteVO> dados) {
		OutputStream stream = new ByteArrayOutputStream();
		
		try {	
			JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(dados);
			
			JasperPrint print = JasperFillManager.fillReport(pathJasperReport, reportParams, dataSource);
			
			byte[] bytes = JasperExportManager.exportReportToPdf(print);
			
			stream.write(bytes, 0, bytes.length);

		}catch(Exception e) {
			Logger.error(e, "Erro ao tentar gerar o relatório.");
			throw new RuntimeException(e);
		}
		return new ByteArrayInputStream(((ByteArrayOutputStream) stream).toByteArray());
		
	}
	
	public static InputStream generateCupomImage(String pathJasperReport, String reportDefFile, Map<?, ?> reportParams, List<CupomDescontoClienteVO> dados) {
		OutputStream stream = new ByteArrayOutputStream();
		BufferedImage bufferedImage = null;
		
		try {
			JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(dados);
			
			JasperPrint print = JasperFillManager.fillReport(pathJasperReport, reportParams, dataSource);
			
			bufferedImage = new BufferedImage(print.getPageWidth()+1, print.getPageHeight()+1, BufferedImage.TYPE_INT_RGB);
			
			JRGraphics2DExporter exporter = new JRGraphics2DExporter();
			exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);
			exporter.setParameter(JRGraphics2DExporterParameter.GRAPHICS_2D, bufferedImage.getGraphics());
			exporter.setParameter(JRExporterParameter.PAGE_INDEX, Integer.valueOf(0));
			
			exporter.exportReport();
			ImageIO.write(bufferedImage, "bmp", stream);

		}catch(Exception e) {
			Logger.error(e, "Erro ao tentar gerar o relatório.");
			throw new RuntimeException(e);
		}
		return new ByteArrayInputStream(((ByteArrayOutputStream) stream).toByteArray());
		
	}
	
}
