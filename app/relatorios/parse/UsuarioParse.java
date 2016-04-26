/**
 * 
 */
package relatorios.parse;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

import play.Logger;
import vo.ProdutoPedidoReportVO;

/**
 * @author hpadmin
 *
 */
public class UsuarioParse implements Serializable {
	
	public static final String TXT = ".txt";
	public static final String CSV = ".csv";
	
	public InputStream parseEmails(List<String> emails, String extensao) {
		InputStream result = null;
		StringBuilder line = new StringBuilder();
		
		try {
			if(emails!=null && !emails.isEmpty()) {
				line.append("E-mail");
				line.append("\r\n");
				line.append("\r\n");
				
				for(String mail : emails) {
					line.append(mail);
					
					if(CSV.equalsIgnoreCase(extensao))
						line.append(";");
					
					line.append("\r\n");
				}
				result = new ByteArrayInputStream(line.toString().getBytes("ISO-8859-1"));
			}
			
		}catch(Exception e) {
			Logger.error(e, "Ocorreu um erro na tentativa de exportar os e-mails dos clientes.");
			throw new RuntimeException(e);
		}
		return result;
		
	}

}
