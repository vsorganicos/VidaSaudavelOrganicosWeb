/**
 * 
 */
package relatorios.parse;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import models.Endereco;
import play.Logger;
import play.i18n.Messages;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author Felipe Guerra
 *
 */
public class EnderecoGMapsParse implements Serializable {

	private static final long serialVersionUID = -7270237873681660581L;
	
	private List<Endereco> enderecos = null;
	
	private Gson gsonBuilder = null;
	
	public EnderecoGMapsParse(List<Endereco> enderecos) {
		if(enderecos==null || enderecos.isEmpty())
			throw new IllegalStateException("Não é possível fazer o parse de endereços nulos.");
		
		this.enderecos = enderecos;
	}
	
	public InputStream buildEnderecoCSV() {
		InputStream result = null;
		StringBuilder line = new StringBuilder();
		
		try {
			if(!this.enderecos.isEmpty()) {
				line.append(Messages.get("form.admin.customer", "")).append(";");
				line.append(Messages.get("form.address.logradouro", "")).append(";");
				line.append(Messages.get("form.title.numero", "")).append(";");
				line.append(Messages.get("form.address.complemento", "")).append(";");
				line.append(Messages.get("form.address.bairro", "")).append(";");
				line.append(Messages.get("form.address.cidade", "")).append(";");
				line.append(Messages.get("form.address.uf", "")).append(";");
				line.append(Messages.get("form.address.cep", "")).append(";");
				line.append("\r\n");
				
				for(Endereco ender : enderecos) {
					line.append(ender.getCliente().getNome()).append(";");
					line.append(ender.getLogradouro()).append(";");
					line.append(ender.getNumero()).append(";");
					line.append(ender.getComplemento()).append(";");
					line.append(ender.getBairro()).append(";");
					line.append(ender.getCidade()).append(";");
					line.append(ender.getUf()).append(";");
					line.append(ender.getCepFormatado()).append(";");
					
					line.append("\r\n");
				}
				
				result = new ByteArrayInputStream(line.toString().getBytes("ISO-8859-1"));
			}
			
		}catch(Exception e) {
			Logger.error(e, "Erro ao tentar fazer o parse dos endereços para o formato CSV.");
			throw new RuntimeException(e);
		}
		return result;
	}
	
	public String buildEnderecosJson(String origin, String destination) {
		StringBuilder result = new StringBuilder();
		Integer totalEnderecos = Integer.valueOf(0);
		//Date dataSaida = new
		Calendar dataSaida = new GregorianCalendar();
		dataSaida.set(GregorianCalendar.HOUR, 7);
		dataSaida.set(GregorianCalendar.MINUTE, 0);
		
		try {
			totalEnderecos = enderecos.size();
			
			result.append("{");
			result.append("\n");
			result.append("origin:");
			result.append("'");
			result.append(buildOrigin(origin));
			result.append("',");
			result.append("\n");
			result.append("destination:");
			result.append("'");
			result.append(buildDestination(destination));
			
			result.append("', ");
			result.append("\n");
			result.append("waypoints:");
			result.append("\n");
			result.append("[");
			for(int i=1; i<totalEnderecos; i++) {
				result.append("\n");
				result.append("{");
				result.append("location:");
				result.append("'");
				result.append(buildWayPoint(enderecos.get(i)));
				result.append("'");
				result.append(",");
				result.append("stopover:true");
				result.append("}");
				
				//Separa os elementos do array até o último
				if(i<(totalEnderecos-1))
					result.append(",");
			}
			result.append("\n");
			result.append("]");
			result.append(",");
			result.append("\n");
			result.append("optimizeWaypoints:true");
			result.append(",");
			result.append("\n");
			result.append("transitOptions:{");
			result.append("\n");
			result.append("departureTime:").append("new Date(").append(dataSaida.getTimeInMillis()).append(")");
			result.append("\n");
			result.append("}");
			result.append(",");
			result.append("\n");
			result.append("travelMode:google.maps.DirectionsTravelMode.DRIVING");
			result.append(",");
			result.append("\n");
			result.append("unitSystem:google.maps.UnitSystem.METRIC");
			result.append("\n");
			result.append("}");
			
		}catch(Exception e) {
			Logger.error(e, "Erro ao tentar fazer o parse dos endereços para o formato do Google Route.");
			throw new RuntimeException(e);
		}
		return result.toString();
	}
	
	private String buildOrigin(String endereco) {
		StringBuffer result = new StringBuffer();
		result.append(endereco);
		
		return result.toString();
	}
	
	private String buildWayPoint(Endereco endereco) {
		StringBuffer result = new StringBuffer();
		result.append(endereco.getLogradouro().trim()).append(", ");
		result.append(endereco.getNumero()).append(", ");
		result.append(endereco.getCidade().trim()).append("-");
		result.append(endereco.getUf()).append(", ");
		result.append(endereco.getCepFormatado());
		
		return result.toString();
	}
	
	private String buildDestination(String endereco) {
		StringBuffer result = new StringBuffer();
		result.append(endereco);
		
		return result.toString();
	}
	
	protected String buildEnderecoJson(Endereco endereco) {
		gsonBuilder = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		
		return gsonBuilder.toJson(endereco);
	}

}
