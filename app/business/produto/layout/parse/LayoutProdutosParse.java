/**
 * 
 */
package business.produto.layout.parse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import models.Produto;

import org.apache.commons.lang.StringUtils;

import play.Logger;

/**
 * @author hpadmin
 *
 */
public class LayoutProdutosParse implements ILayoutParse<Produto> {

	private static final long serialVersionUID = -8084078675448834438L;

	/* (non-Javadoc)
	 * @see business.produto.layout.parse.ILayoutParse#parse(java.lang.Long, java.io.File)
	 */
	@Override
	public List<Produto> parse(Long idFornecedor, File archive) {
		Logger.info("##### Início - Parse do arquivo %s  #####", archive.getName());
		List<Produto> result = null;
		BufferedReader reader = null;
		Produto produtoEncontrado = null;
		String line = null;
		
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(archive), "ISO-8859-1"));
			
			result = new ArrayList<Produto>();
			
			while((line = reader.readLine())!=null) {
				String[] campos = line.split(";");
				
				if(campos!=null && campos.length>=3) {
					Long id = Long.parseLong(campos[0].trim());
					String codigo = campos[1].trim();
					Double valorPago = (campos[2]==null || StringUtils.isEmpty(campos[2]) ? null : Double.parseDouble(campos[2].trim()));
					Double valorVenda = (campos[3]==null || StringUtils.isEmpty(campos[3]) ? null : Double.parseDouble(campos[3].trim()));
					Boolean ativo = Integer.valueOf(1).equals(Integer.parseInt(campos[4].trim()));
					String nome = campos[5].trim();
					String descricao = campos[6].trim();
					
					produtoEncontrado = new Produto();
					produtoEncontrado.id = id;
					produtoEncontrado.setCodigoProduto(codigo);
					produtoEncontrado.setDescricao(descricao);
					produtoEncontrado.setValorPago(valorPago);
					produtoEncontrado.setValorVenda(valorVenda);
					produtoEncontrado.setNome(nome);
					produtoEncontrado.setAtivo(ativo);
					
					result.add(produtoEncontrado);
				}
			}
			
		}catch(Exception e) {
			Logger.error(e, "Erro ao tentar realizar o parse do arquivo: " + archive.getName());
			throw new RuntimeException(e);
			
		}finally {
			Logger.info("##### Fim - Parse do arquivo %s  #####", archive.getName());

			try {
				if(reader!=null)
					reader.close();
				
			}catch(IOException e) {
				//ignore
			}
		}
		return result;
	}

}
