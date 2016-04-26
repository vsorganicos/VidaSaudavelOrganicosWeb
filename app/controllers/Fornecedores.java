/**
 * 
 */
package controllers;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.persistence.Query;

import models.Fornecedor;
import models.Telefone;
import models.Telefone.TelefoneTipo;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import play.Logger;
import play.data.validation.Required;
import play.data.validation.Valid;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.i18n.Messages;
import play.modules.paginate.ValuePaginator;
import play.mvc.Before;
import business.produto.ProdutoControl;
import business.produto.layout.LayoutArquivo;
import business.produto.layout.parse.factory.LayoutFactory;

/**
 * @author Felipe Guerra
 *
 */
public class Fornecedores extends BaseController {
	
	@Before(unless={"upload", "sendArchive"})
	static void estaAutorizado() {
		Logger.debug("####### Verificar se o usuário autenticado é admin... ########");
		
		if( (StringUtils.isEmpty(session.get("isAdmin")) || Boolean.FALSE.equals(Boolean.valueOf(session.get("isAdmin")))) 
				&& (StringUtils.isEmpty(session.get("isEmployee")) && Boolean.FALSE.equals(Boolean.valueOf(session.get("isEmployee")))) ) 
			{
			Logger.debug("####### Usuário não autorizado a acessar essa funcionalidade...%s ########", session.get("usuarioAutenticado"));
			
			Home.index("Usuário não autorizado a acessar essa funcionalidade.");
		}
	}
	
	public static void edit(Long id) {
		Fornecedor fornecedor = Fornecedor.findById(id);
		TelefoneTipo[] telefones = TelefoneTipo.values();
		
		renderTemplate("Fornecedores/index.html", fornecedor, telefones);
	}
	
	public static void index(Fornecedor fornecedor, String success) {
		TelefoneTipo[] telefones = TelefoneTipo.values();
		Telefone telefone = null;
		
		if(fornecedor==null) {
			fornecedor = new Fornecedor();
		
			telefone = new Telefone();
		}
		render(fornecedor, telefones, success, telefone);
	}
	
	@Transactional(readOnly=false)
	public static void cadastrar(@Valid Fornecedor fornecedor) {
		Logger.debug("###### Início - Cadastrar Fornecedor: %s ######", fornecedor.getNome());
		Integer telefoneTipo = null;
		
		if(validation.hasErrors()) {
			Logger.error("###### Erro - Cadastrar Fornecedor: %s ######", validation.errors());
			
			validation.keep();
			params.flash();
			
			index(fornecedor, validation.errors().iterator().next().message());
			
		}else {
			telefoneTipo = Integer.valueOf(params.get("telefoneTipo"));
			
			fornecedor.getTelefones().get(0).setTipo(Telefone.findById(telefoneTipo));
			fornecedor.getTelefones().get(0).setFornecedor(fornecedor);
			
			fornecedor.save();
			
			show();
		}
	}
	
	public static void show() {
		Logger.debug("###### Início - Consultar todos os Fornecedores... ######", "");
		
		List<Fornecedor> fornecedores = Fornecedor.findAll();
		ValuePaginator<Fornecedor> fornecs = new ValuePaginator<Fornecedor>(fornecedores);
		fornecs.setPageSize(20);
		
		render(fornecs);
	}
	
	public static void upload(String message) {
		List<Fornecedor> fornecedores = Fornecedor.find("ativo = ?", Boolean.TRUE).fetch();
		
		render(fornecedores, message);
	}
	
	public static void sendArchive(@Required(message="Por favor, selecione algum arquivo") File arquivo, 
								Long idFornecedor,
								Integer atualizarProdutos) throws IOException {
		Logger.info("##### Início - Enviar arquivo de parceiro [%s] #####", session.get("usuarioAutenticado"));
		File path = null;
		String caminhoArquivos = null;
		String message = null;
		Long maxLengthArchive = null;
		ProdutoControl control = null;
		
		if(validation.hasErrors()) {
			validation.keep();
			params.flash();
			
		}else {
			Logger.info("##### Vai mover o arquivo %s - tamanho: %s Kb #####", arquivo.getName(), arquivo.length()/1024);
			
			maxLengthArchive = Long.parseLong(Messages.get("form.parceiros.upload.archive.maxLength", ""));
			
			if(arquivo.length()>maxLengthArchive) {
				validation.addError("imagem", "form.parceiros.error.archive.maxLength", "");
				validation.keep();
				params.flash();
				
			}if(atualizarProdutos.equals(1) && idFornecedor==null) {
				validation.addError("imagem", "message.required.product.fornecedor", "");
				validation.keep();
				params.flash();
				
			}else {
				caminhoArquivos = Messages.get("application.path.upload.archives", "");
				
				if(!StringUtils.isEmpty(caminhoArquivos)) {
					//Refactory do parsing de arquivos de atualização de produtos
					path = new File(System.getProperty("java.io.tmpdir") + 
							File.separatorChar + 
								(atualizarProdutos.equals(2) ?
									Messages.get("application.path.upload.archives", "") : 
									String.valueOf(idFornecedor)
								)
							);
					
					if(path.exists()) {
						FileUtils.deleteDirectory(path);
					}
					path.mkdir();
					
					FileUtils.copyFileToDirectory(arquivo, path);
					
					control = new ProdutoControl();
					
					if(atualizarProdutos.equals(1)) {
						control.atualizarProdutos(idFornecedor);
						message = "Arquivo enviado e produtos do site atualizados.";
						
					}else if(atualizarProdutos.equals(2)) {
						control = new ProdutoControl(LayoutFactory.getLayout(LayoutArquivo.PRODUTO_CSV));
						control.atualizarProdutos();
						
						message = "Arquivo enviado e dados dos produtos atualizados.";
					}
					control.generateLuceneIndex();
					
				}else {
					validation.addError("imagem", "form.parceiros.path.notConfigured", "");
				}
			}
		}
		Logger.info("##### Fim - Enviar arquivo de parceiro [%s] #####", session.get("usuarioAutenticado"));
		
		upload(message);
	}
	
	/**
	 * M�todo para ativar/inativar produtos de um determinado fornecedor
	 * @param status
	 */
	@Transactional(readOnly=false)
	public static void inativarProdutosPorFornecedor(Long idFornecedor) {
		Query query = JPA.em().createQuery("update Produto set ativo =:ativo where ativo = 1 AND fornecedor.id =:idFornecedor");
		query.setParameter("ativo", Boolean.FALSE);
		query.setParameter("idFornecedor", idFornecedor);
		
		Logger.info("############ [user: %s] Atualizou %s produtos(s) do Fornecedor %s ############", session.get("usuarioAutenticado"), query.executeUpdate(), idFornecedor);
		
		show();
	}

}
