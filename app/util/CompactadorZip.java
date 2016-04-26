package util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipOutputStream;

/**
 * @author Felipe Guerra
 *
 */
public class CompactadorZip implements Serializable {

	private static final long serialVersionUID = -5684541680928626291L;

	private static final int BUFFER_SIZE = 2048;
	
	public List<ZipEntry> criarZip( File arquivoZip, File[] arquivos ) throws ZipException, IOException {  
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		
		try {  
			//adiciona a extensão .zip no arquivo, caso não exista
			if( !arquivoZip.getName().toLowerCase().endsWith(".zip") ) {
				arquivoZip = new File( arquivoZip.getAbsolutePath()+".zip" );
			}
			
			fos = new FileOutputStream( arquivoZip );
			bos = new BufferedOutputStream( fos, BUFFER_SIZE );
			List<ZipEntry> listaEntradasZip = criarZip( bos, arquivos ); 
			
			return listaEntradasZip;  
		}  
		finally {  
			if( bos != null ) {  
				try {  
					bos.close();  
				} catch( Exception e ) {}  
			}  
			if( fos != null ) {  
				try {  
					fos.close();  
				} catch( Exception e ) {}  
			}  
		}  
	}
	
	public List<ZipEntry> criarZip( OutputStream os, File[] arquivos ) throws ZipException, IOException {  
		if( arquivos == null || arquivos.length < 1 ) {  
			throw new ZipException("Adicione ao menos um arquivo ou diretório");  
		}
		
		List<ZipEntry> listaEntradasZip = new ArrayList<ZipEntry>();  
		ZipOutputStream zos = null;  
		try {  
			zos = new ZipOutputStream( os );
			
			for( int i=0; i<arquivos.length; i++ ) {  
				String caminhoInicial = arquivos[i].getParent();  
				List<ZipEntry> novasEntradas = adicionarArquivoNoZip( zos, arquivos[i], caminhoInicial );
				
				if( novasEntradas != null ) {  
					listaEntradasZip.addAll( novasEntradas );  
				}  
			}  
		}  
		finally {  
			if( zos != null ) {  
				try {  
					zos.close();  
				} catch( Exception e ) {}  
			}  
		}  
		return listaEntradasZip;  
	} 

	private List<ZipEntry> adicionarArquivoNoZip( ZipOutputStream zos, File arquivo, String caminhoInicial ) throws IOException {  
		List<ZipEntry> listaEntradasZip = new ArrayList<ZipEntry>();
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		byte buffer[] = new byte[BUFFER_SIZE];

		try {  
			//diretórios não são adicionados  
			if( arquivo.isDirectory() ) {  
				//recursivamente adiciona os arquivos dos diretórios abaixo  
				File[] arquivos = arquivo.listFiles();  

				for( int i=0; i<arquivos.length; i++ ) {  
					List<ZipEntry> novasEntradas = adicionarArquivoNoZip( zos, arquivos[i], caminhoInicial );

					if( novasEntradas != null )
						listaEntradasZip.addAll(novasEntradas);  

				}
				return listaEntradasZip;  
			}
			
			String caminhoEntradaZip = null;  
			int idx = arquivo.getAbsolutePath().indexOf(caminhoInicial);  

			if( idx >= 0 ) {  
				//calcula os diretórios a partir do diretório inicial  
				//isso serve para não colocar uma entrada com o caminho completo  
				caminhoEntradaZip = arquivo.getAbsolutePath().substring( idx+caminhoInicial.length()+1 );  
			}  

			ZipEntry entrada = new ZipEntry( caminhoEntradaZip );  
			zos.putNextEntry( entrada );  
			zos.setMethod( ZipOutputStream.DEFLATED );  

			fis = new FileInputStream( arquivo );  
			bis = new BufferedInputStream( fis, BUFFER_SIZE );  

			int bytesLidos = 0;  

			while((bytesLidos = bis.read(buffer, 0, BUFFER_SIZE)) != -1) {  
				zos.write( buffer, 0, bytesLidos );  
			}

			listaEntradasZip.add( entrada );  
		}
		finally {  
			if( bis != null ) {  
				try {  
					bis.close();  
				} catch( Exception e ) {}  
			}  
			if( fis != null ) {  
				try {  
					fis.close();  
				} catch( Exception e ) {}  
			}  
		}  
		return listaEntradasZip;  
	}
	
}
