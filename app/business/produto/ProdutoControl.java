/**
 * 
 */
package business.produto;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import models.Fornecedor;
import models.Produto;

import org.hibernate.CacheMode;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;

import play.Logger;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.i18n.Messages;
import business.produto.layout.LayoutArquivo;
import business.produto.layout.parse.ILayoutParse;
import business.produto.layout.parse.factory.LayoutFactory;
import controllers.Mail;

/**
 * @author Felipe G. de Oliveira
 * @version 1.0
 * @since 30/07/2012
 */
public class ProdutoControl implements Serializable {

	private static final long serialVersionUID = 17867564352456789L;
	
	private ILayoutParse layoutParse;
	
	public ProdutoControl() {
		layoutParse = LayoutFactory.getLayout(LayoutArquivo.CSV);
	}
	
	public ProdutoControl(ILayoutParse layout) {
		layoutParse = layout;
	}
	
	public void atualizarProdutos() {
		Logger.debug("##### Início - Atualizar as informações dos produtos #####", "");
		File diretorio = null;
		String caminhoArquivos = null;
		List<Produto> produtosAtivos = null;
		
		try {
			caminhoArquivos = System.getProperty("java.io.tmpdir") + 
					File.separatorChar +
					Messages.get("application.path.upload.archives", "");
			
			diretorio = new File(caminhoArquivos);
			
			if(diretorio!=null && diretorio.exists()) {
				for(File _arq : diretorio.listFiles())
					if(!_arq.isDirectory()) {
						produtosAtivos = layoutParse.parse(null, _arq);
						break;
					}
				
				Logger.info("##### Vai tentar atualizar %s produto(s). #####", produtosAtivos.size());
				salvarProdutosNaoEncontrados(updateBatch(produtosAtivos), null);
			}
			
		}catch(Exception e) {
			Logger.error(e, "Erro ao tentar atualizar os produtos.");
			throw new RuntimeException(e);
		}
		Logger.debug("##### Fim - Atualizar as informações dos produtos  #####", "");
	}
	
	public void atualizarProdutos(Long idFornecedor) {
		Logger.debug("##### Início - Atualizar os produtos para o fornecedor [%s] #####", idFornecedor);
		File arquivo = null;
		String caminhoArquivos = null;
		List<Produto> produtosAtivos = null;
		
		try {
			caminhoArquivos = System.getProperty("java.io.tmpdir");
			arquivo = new File(caminhoArquivos + File.separatorChar + idFornecedor);
			
			if(arquivo.exists() && arquivo.listFiles().length==1) {
				produtosAtivos = layoutParse.parse(idFornecedor, arquivo.listFiles()[0]);
				
				Logger.info("##### Vai tentar atualizar %s produto(s). #####", produtosAtivos.size());
				salvarProdutosNaoEncontrados(updateBatch(produtosAtivos, idFornecedor), idFornecedor);
			}
			
		}catch(Exception e) {
			Logger.error(e, "Erro ao tentar atualizar os produtos.");
			throw new RuntimeException(e);
		}
		Logger.debug("##### Fim - Atualizar os produtos para o fornecedor [%s] #####", idFornecedor);
	}
	
	@Transactional(readOnly=false)
	private List<Produto> updateBatch(List<Produto> produtos, Long idFornecedor) {
		List<Produto> naoAtualizados = new ArrayList<Produto>();
		Integer count = 0;
		Query query = Produto.em().createQuery("update Produto set ativo = 0 where fornecedor.id =:id");
		query.setParameter("id", idFornecedor);
		
		Logger.info("###### Foram inativados %s produto(s) - fornecedor: %s #######",  query.executeUpdate(), idFornecedor);
		
		for(Produto prod : produtos) {
			count++;
			Produto produto = Produto.find("codigoProduto = ? AND fornecedor.id = ?", prod.getCodigoProduto().trim(), idFornecedor).first();
			
			if(produto!=null) {
				produto.setAtivo(Boolean.TRUE);
				
				if(count%5==0)
					produto.setEhPromocao(Boolean.TRUE);
				
				produto.save();
			}else {
				naoAtualizados.add(prod);
			}
		}
		return naoAtualizados;
	}
	
	@Transactional(readOnly=false)
	private List<Produto> updateBatch(List<Produto> produtos) {
		Logger.debug("##### Atualizar %s produto(s) na base... #####", produtos.size());
		List<Produto> naoAtualizados = new ArrayList<Produto>();
		
		for(Produto prod : produtos) {
			Produto produto = Produto.findById(prod.id);
			if(produto!=null) {
				produto.setCodigoProduto(prod.getCodigoProduto());
				produto.setValorPago(prod.getValorPago());
				produto.setValorVenda(prod.getValorVenda());
				produto.setAtivo(prod.getAtivo());
				produto.setDescricao(prod.getDescricao());
				produto.setDataAlteracao(new Date());
				produto.setNome(prod.getNome());
				
				produto.save();
				Logger.info("#### Produto código: %s atualizado. ####", produto.getCodigoProduto());
			}else {
				naoAtualizados.add(prod);
			}
		}
		return naoAtualizados;
	}
	
	private void salvarProdutosNaoEncontrados(List<Produto> produtos, Long idFornecedor) {
		FileOutputStream outputStream = null;
		File report = null;
		StringBuffer buffer = null;
		Fornecedor fornecedor = null;
		String caminhoArquivos = System.getProperty("java.io.tmpdir") + File.separatorChar;
		String message = "Todos os produtos foram encontrados!";
		
		try {
			if(produtos!=null && !produtos.isEmpty()) {
				if(idFornecedor!=null) {
					fornecedor = Fornecedor.findById(idFornecedor);
					
					caminhoArquivos = Messages.get("application.path.upload.archives", "") + File.separatorChar + String.valueOf(idFornecedor)  
											+ File.separatorChar + fornecedor.getNome().toUpperCase() + "-PRODUTOS_NAO_ENCONTRADOS.csv";
				}else {
					caminhoArquivos = Messages.get("application.path.upload.archives", "") + File.separatorChar + "PRODUTOS_NAO_ENCONTRADOS.csv";
					
				}
				outputStream = new FileOutputStream(caminhoArquivos);
				
				for(Produto prod : produtos) {
					buffer = new StringBuffer();
					
					buffer.append(prod.getCodigoProduto());
					buffer.append(";");
					buffer.append(prod.getAtivo());
					buffer.append(";");
					buffer.append(prod.getDescricao().replace(';', ','));
					buffer.append("\r\n");
					
					outputStream.write(buffer.toString().getBytes("ISO-8859-1"), 0, buffer.length());
					
					outputStream.flush();
				}
				report = new File(caminhoArquivos);
				
				Logger.info("#### Arquivo Gerado %s ####", report.getAbsolutePath());
				
				message = "Segue em anexo o relatório para os produtos não encontrados [Total: "+produtos.size()+"]";
				
				if(fornecedor!=null)
					message += " do fornecedor: " + fornecedor.getNome().toUpperCase();
				
				Mail.sendProdutosNaoEncontrados("Relatório de Produtos não Encontrados", Mail.EMAIL_ADMIN, 
												report.getAbsolutePath(), 
												message, 
												Messages.get("application.email.admin", "").split(","));
			}else {
				Mail.sendProdutosNaoEncontrados("Relatório de Produtos não Encontrados", Mail.EMAIL_ADMIN, 
						null, 
						message, 
						Messages.get("application.email.admin", "").split(","));
			}
			
		}catch(Exception e) {
			Logger.error(e, "Erro ao tentar enviar os produtos não encontrados por e-mail");
			throw new RuntimeException(e);
			
		}finally {
			try {
				if(outputStream!=null)
					outputStream.close();
				
			}catch(IOException ex) {}
		}
	}
	
	public List<Produto> findProdutosByNomeOuDetalhe(String parametro) {
		List<Produto> result = new ArrayList<Produto>();
		EntityManager em = null;
		FullTextEntityManager fullTextEntityManager = null;
		QueryBuilder queryBuilder = null;
		
		try {
			em = JPA.em();
			
			fullTextEntityManager = Search.getFullTextEntityManager(em);
			
			queryBuilder = fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(Produto.class).get();
			
			org.apache.lucene.search.Query query = queryBuilder.bool()
																.must(queryBuilder.keyword()
																.fuzzy()
																.withPrefixLength(3)
																.onField("nome").andField("detalhe").matching(parametro)
																.createQuery()
																)
																.must(queryBuilder
																.keyword()
																.onField("ativo").matching("false")
																.createQuery()
																).not()
																.createQuery();
			
			Logger.debug("Query: %s", query.toString());
			
			Query jpaQuery = fullTextEntityManager.createFullTextQuery(query, Produto.class);
			
			result = jpaQuery.getResultList();
			
		}catch(Exception e) {
			Logger.error(e, "Erro ao tentar pesquisar os produtos pelo parâmetro: %s", parametro);
			throw new RuntimeException(e);
		}
		return result;
	}
	
	public void generateLuceneIndex() {
		EntityManager em = null;
		FullTextEntityManager fullTextEntityManager = null;
		
		try {
			Logger.info("###### Início - Gerar índices do Lucene ######", "");
			
			em = JPA.em();
			fullTextEntityManager = Search.getFullTextEntityManager(em);
			
			fullTextEntityManager.createIndexer()
			 					.batchSizeToLoadObjects( 50 )
			 					.cacheMode( CacheMode.NORMAL )
			 					.threadsToLoadObjects( 5 )
			 					.threadsForSubsequentFetching( 20 )
			 					.startAndWait();
			
			Logger.info("###### Fim - Gerar índices do Lucene ######", "");
			
		}catch(Exception e) {
			Logger.error(e, "Ocorreu um erro ao tentar criar os índices para o Lucene.", "");
		}
	}

	/**
	 * @param layoutParse the layoutParse to set
	 */
	public void setLayoutParse(ILayoutParse layoutParse) {
		this.layoutParse = layoutParse;
	}
	
}
