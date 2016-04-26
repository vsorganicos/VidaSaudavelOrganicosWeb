/**
 * 
 */
package business.produto.layout.parse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import models.Produto;
import play.Logger;

/**
 * @author Felipe G. de Oliveira
 * @version 1.0
 * @since 30/07/2012
 */
public class LayoutProdutoParse implements ILayoutParse<Produto> {

	private static final long serialVersionUID = 987656453456781L;

	/* (non-Javadoc)
	 * @see business.produto.layout.parse.ILayoutParse#parse(java.lang.Long)
	 */
	@Override
	public List<Produto> parse(Long idFornecedor, File archive) {
		Logger.info("##### Início - Parse do arquivo %s  #####", archive.getName());
		List<Produto> result = null;
		BufferedReader reader = null;
		Produto produtoEncontrado = null;
		FileReader fileReader = null;
		String line = null;
		
		try {
			fileReader = new FileReader(archive);
			reader = new BufferedReader( fileReader );

			result = new ArrayList<Produto>();
			
			while((line = reader.readLine())!=null) {
				String[] campos = line.split(";");
				
				if(campos!=null && campos.length>=3) {
					String codigo = campos[0].trim();
					Boolean ativo = Boolean.parseBoolean(campos[1].trim());
					String descricao = campos[2].trim();
					
					produtoEncontrado = new Produto(descricao, descricao, codigo, ativo);
					
					result.add(produtoEncontrado);
				}
			}
			
		}catch(Exception e) {
			Logger.error(e, "Erro ao tentar realizar o parse do arquivo: " + archive.getName());
			throw new RuntimeException(e);
			
		}finally {
			Logger.info("##### Fim - Parse do arquivo %s  #####", archive.getName());
			
			try {
				if(fileReader!=null)
					fileReader.close();
			
				if(reader!=null)
					reader.close();
				
			}catch(IOException e) {
				//ignore
			}
		}
		return result;
	}

}
