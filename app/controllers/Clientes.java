/**
 * 
 */
package controllers;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.grepcepws.entity.xsd.Cep;

import business.cliente.service.EnderecoService;
import exception.SystemException;
import form.TelefoneForm;
import models.Cliente;
import models.Endereco;
import models.Grupo;
import models.Telefone;
import models.Telefone.TelefoneTipo;
import models.Usuario;
import play.Logger;
import play.cache.Cache;
import play.data.binding.As;
import play.data.validation.Error;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.data.validation.Valid;
import play.db.jpa.Transactional;
import play.i18n.Messages;
import play.libs.Codec;
import play.modules.paginate.ValuePaginator;
import play.mvc.Before;
import relatorios.parse.UsuarioParse;

/**
 * @author guerrafe
 *
 */
public class Clientes extends BaseController {

	@Before(unless={"index", "cadastrar", "enviarSenha", "lembrarSenha", "consultarCep", "alterarSenha", "cadastrarNovaSenha"})
	static void isAuthenticated() {
		Logger.debug("####### Validar se usuário está autenticado...#######");

		if(session.get("usuarioAutenticado")==null)
			Login.index(null, null, Messages.get("application.message.notAuthenticated", ""));
	}
	
	public static void findByParams(String clienteParam, String param) {
		Logger.debug("######## Início - Pesquisar clientes pelo parâmetro: %s########", param); 
		
		StringBuilder query = new StringBuilder();
		String parametro = "";
		
		if("cpf".equalsIgnoreCase(param)) {
			parametro = clienteParam.trim();
			query.append("UPPER(cpf) = ? ");
			
		}else if("nome".equalsIgnoreCase(param)) {
			parametro = "%"+clienteParam.toUpperCase()+"%";
			query.append("UPPER(nome) LIKE ? ");
			
		}else if("email".equalsIgnoreCase(param)) {
			parametro = clienteParam.toLowerCase();
			query.append("LOWER(usuario.email) = ? ");
		}
		
		List<Cliente> clientes = Cliente.find(query.toString(), parametro).fetch();
		
		Logger.debug("######## Fim - Pesquisar clientes pelo parâmetro: %s########", param);
		
		render("Clientes/showAll.html", clientes);
	}
	
	public static void exportEmails() {
		List<String> emails = Usuario.find("select email from Usuario where recebeMail = ?", Boolean.TRUE).fetch();
		
		renderBinary(new UsuarioParse().parseEmails(emails, UsuarioParse.CSV), "emails"+UsuarioParse.CSV);
	}
	
	public static void index(Cliente cliente, Usuario usuario, Endereco endereco, String email, 
							String message, Telefone telefone, Telefone otherTelefone) {
		Logger.debug("####### Clientes Início...#######");
		
		usuario = (usuario==null) ? new Usuario() : usuario;
		endereco = (endereco==null) ? new Endereco() : endereco;
		telefone = (telefone==null) ? new Telefone() : telefone;
		otherTelefone = (otherTelefone==null) ? new Telefone() : otherTelefone;
		
		TelefoneTipo[] telefones = TelefoneTipo.values();
		
		validarEmailCadastrado(email);

		usuario.setEmail(email);

		List<String> ufs = getListaUF();
		
		Logger.debug("####### Fim index... #######");
		render(cliente, endereco, usuario, ufs, telefones, message, telefone, otherTelefone);
	}
	
	public static void consultarCep(String cep) {
		Logger.debug("###### Consultar o cep: %s #######", cep);
		Endereco endereco = null;
		Cep _cep = null;
		StringBuffer urlConsultaCep = new StringBuffer();
		
		try {
			urlConsultaCep.append("http://api.wscep.com/cep?key=free&val=");
			urlConsultaCep.append(cep);
			
			_cep = EnderecoService.newInstance(urlConsultaCep.toString()).consultarEnderecoWSCep();
			
			if(_cep!=null) {
				endereco = new Endereco();
				
				endereco.setBairro(_cep.getBairro().getValue());
				endereco.setLogradouro(_cep.getLogradouro().getValue());
				endereco.setCidade(_cep.getCidade().getValue());
				endereco.setUf(_cep.getEstado().getValue().trim());
				
				Logger.debug("###### Endereço encontrado! [%s] #######", _cep.getLogradouro().getValue());
			}
			
		}catch(Exception e) {
			Logger.error(e, "Erro ao tentar consultar o CEP: %s", cep);
		}
		Logger.debug("###### Fim consultar o cep: %s #######", cep);
		renderJSON(endereco);
	}
	
	@Transactional(readOnly=false)
	public static void cadastrar(@Valid Cliente cliente, @Valid Usuario usuario, @Valid Endereco endereco, 
								@Valid Telefone telefone, 
								@Required(message="Selecione o tipo do Telefone.") Integer tipoTelefone,
								Telefone otherTelefone,
								Integer tipoOtherTelefone) throws SystemException {
		Logger.debug("Vai persistir o cliente! %s", cliente);
		
		validarSenhas(usuario.getSenha(), params.get("senha"));
		
		validarEmailCadastrado(usuario.getEmail());
		
		validarCpf(cliente.getCpf());
		
		if(validation.hasErrors()) {
			params.flash();
			validation.keep();
			
			Logger.debug("##### Erro na validação do cadastro. %s ######", validation.errors());
			
			index(cliente, usuario, endereco, usuario.getEmail(), null, telefone, otherTelefone);
		
		}else {
			Logger.debug("#####  Criptografar senha do usuário: %s #####", "");
			usuario.encryptPassword();
			
			cliente.setDataCadastro(new Date());
			cliente.setAtivo(true);
			
			Grupo grupo = Grupo.find("nome = ?", "CUSTOMER").first();
			usuario.setGrupo(grupo);
			
			//Adicionando o tipo do Telefone
			telefone.setTipo( Telefone.findById(tipoTelefone) );
			cliente.addTelefone(telefone);
			
			if(otherTelefone!=null) {
				otherTelefone.setTipo(Telefone.findById(tipoOtherTelefone));
				cliente.addTelefone(otherTelefone);
			}
			
			cliente.addEndereco(endereco);
			cliente.setUsuario(usuario);
			
			cliente.save();
			
			Logger.info("cliente %s cadastrado [IP: %s]...", cliente.getNome(), request.remoteAddress);
			
			Mail.sendCadastroAprovado("Bem Vindo", Mail.EMAIL_ADMIN, usuario.getEmail(), cliente);
				
		}
		Logger.debug("#### Fim do cadastro de cliente ####");
		Login.logon(new Usuario(null, usuario.getSenha(), usuario.getEmail(), null, null), "");
		
	}
	
	public static void edit(Long id, String message) {
		Logger.debug("######### Início - Editar Usuário: id %s ##########", id);
		
		Cliente cliente = Cliente.findById(id);
		cliente.getUsuario().decryptPassword();
		
		List<Grupo> grupos = Grupo.findAll();
		
		render(cliente, message, grupos);
	}
	
	@Transactional(readOnly=false)
	public static void atualizarDadosCliente(@Required Long id, @Required String nome, @Required String cpf, 
											@Required String rg, 
											@Required String sexo, @Required String estadoCivil, 
											@As(format="dd/MM/yyyy") Date dataNascimento,
											Boolean status,
											@Required Boolean recebeMail) {
		Logger.debug("####### Atualizar os dados do Cliente pelo id %s ########", id);
		Cliente cliente = null;
		
		validation.valid(id);
		validation.valid(nome);
		validation.valid(cpf);
		validation.valid(rg);
		validation.valid(sexo);
		validation.valid(estadoCivil);
		validation.valid(dataNascimento);
		
		if(validation.hasErrors()) {
			Logger.debug("####### Erro na validação dos dados do Cliente  #########", validation.errors());
			
			StringBuffer erros = new StringBuffer();
			
			for(Error erro : validation.errors())
				erros.append(erro.message()).append("\n");
			
			renderText(erros.toString());
			
		}else {
			cliente = Cliente.findById(id);
			
			cliente.setNome(nome);
			cliente.setDataNascimento(dataNascimento);
			cliente.setSexo(sexo);
			cliente.setEstadoCivil(estadoCivil);
			cliente.setCpf(cpf);
			cliente.setRg(rg);
			cliente.getUsuario().setRecebeMail(recebeMail);

			if(status!=null)
				cliente.setAtivo(status);
			
			cliente.save();
			
			Logger.debug("####### Cliente %s atualizado com sucesso #########", cliente.getNome());
		}
		
		renderText(Messages.get("validation.data.success", ""));
	}
	
	@Transactional(readOnly=false)
	public static void atualizarDadosUsuario(@Required Long idUsuario,
											@Required @MinSize(message="message.user.password.minsize", value=6) String senha, 
											@Required String confirmaSenha,
											Long idGrupo) {
		Usuario usuario = null;
		
		validation.valid(idUsuario);
		validation.valid(senha);
		validation.valid(confirmaSenha);
		
		validarSenhas(senha, confirmaSenha);
		
		if(validation.hasErrors()) {
			Logger.debug("####### Erro na validação dos dados do Usuário #########", validation.errors());
			
			StringBuffer erros = new StringBuffer();
			
			for(Error erro : validation.errors())
				erros.append(erro.message()).append("\n");
			
			renderText(erros.toString());
			
		}else {
			usuario = Usuario.findById(idUsuario);
			
			usuario.setSenha(senha);
			usuario.encryptPassword();
			
			if(idGrupo!=null) {
				Grupo grupo = Grupo.findById(idGrupo);
				usuario.setGrupo(grupo);
			}
			
			usuario.save();
		}
		renderText(Messages.get("validation.data.success", ""));
	}
	
	private static void validarEmailCadastrado(String email) {
		if(email!=null && Usuario.find("email = ?", email).first()!=null)
			validation.addError("usuario.email", "form.validation.email.exists", "");
	}
	
	public static void lembrarSenha(String message) {
		Logger.debug("####### Início - Lembrar Senha...");
		
		String randomID = Codec.UUID();
		
		render(message, randomID);
	}
	
	public static void enviarSenha(@Valid String email, 
								String code,	
								String randomID) {
		String message = null;
		
		try {
			validation.required(email);
			validation.email(email);
			
			validation.equals(code, Cache.get(randomID)).message("application.captcha.error");
			
			if(validation.hasErrors()) {
				params.flash();
				validation.keep();
				
			}else {
				Cliente cliente = Cliente.find("usuario.email = ?", email).first();
				
				if(cliente!=null) {
					cliente.getUsuario().decryptPassword();
					Mail.sendEmail("Recuperação de Senha", Mail.EMAIL_ADMIN, cliente.getUsuario().getEmail(), cliente);
					message = "sucesso";
				}else {
					validation.addError("email", "message.error.email.notfound", "");
				
					params.flash();
					validation.keep();
				}
			}
			lembrarSenha(message);
			
		}catch(SystemException e) {
			Logger.error("Erro ao tentar enviar a senha para o e-mail: " + email, e);
			throw new RuntimeException(e);
		}
	}
	
	public static void mypage() {
		render();
	}
	
	public static void showAll() {
		Logger.debug("######### Vai consultar todos os clientes... ###########", "");
		
		if(session.get("isAdmin")==null)
			Home.index("Usuário não autorizado a acessar essa funcionalidade.");
		
		List<Cliente> customers = Cliente.findAll();
		
		ValuePaginator<Cliente> clientes = new ValuePaginator<Cliente>(customers);
		clientes.setPageSize(30);
		
		render(clientes);
	}
	
	private static void validarSenhas(String _senha, String senha) {
			if(_senha==null || !_senha.equals(senha))
				validation.addError("usuario.senha", "form.validation.password.notEquals", "");
	}
	
	private static void validarCpf(String cpf) {
		if(Cliente.find("cpf = ?", cpf).first()!=null)
			validation.addError("usuario.senha", "message.cpf.existente", "");
	}
	
	public static void viewEndereco(Long id) {
		Logger.debug("#### Início - Visualizar o Endereço: %s #####", id);
		Endereco endereco = Cache.get("endereco." + id, Endereco.class);
		
		if(endereco==null) { 
			endereco = Endereco.findById(id);
			Cache.add("endereco."+id, endereco, "1mn");
		}
		
		List<String> ufs = getListaUF();
		
		List<Telefone> allPhones = Telefone.find("cliente.id = ?", id).fetch();
		TelefoneTipo[] telefones = TelefoneTipo.values();
		
		Logger.debug("#### Início - Visualizar o Endereço: %s #####", id);
		render(endereco, ufs, allPhones, telefones);
	}
	
	@Transactional(readOnly=false)
	public static void atualizarEndereco(@Required Long id, @Required Long idCliente, 
										@Required String logradouro, @Required Integer numero, 
										String complemento, @Required String bairro, @Required String cidade,
										@Required String uf, @Required String cep, @Required String tipo,
										@Required List<TelefoneForm> telefones) {
		Logger.debug("#### Início - Atualizar o Endereço: %s #####", id);

		validation.valid(id);
		validation.valid(logradouro);
		validation.valid(numero);
		validation.valid(bairro);
		validation.valid(cidade);
		validation.valid(uf);
		validation.valid(cep);
		validation.valid(tipo);
		
		if(validation.hasErrors()) {
			Logger.debug("#### Erro - Atualizar o Endereço: %s #####", id);
			StringBuffer erros = new StringBuffer();
			
			for(Error erro : validation.errors())
				erros.append(erro.message()).append("\n");
			
			renderText(erros.toString());
			
		}else {
			Endereco endereco = Endereco.findById(id);
			
			endereco.setComplemento(complemento);
			endereco.setLogradouro(logradouro);
			endereco.setNumero(numero);
			endereco.setCidade(cidade);
			endereco.setBairro(bairro);
			endereco.setUf(uf);
			endereco.setCep(cep);
			endereco.setTipoEndereco(tipo);
			
			endereco.save();
			
			for(TelefoneForm telefone : telefones) {
				Telefone tel = null;
				
				if(telefone.getId()!=null)
					tel = Telefone.findById(telefone.getId());
				else {
					Cliente cliente = Cliente.findById(idCliente);
					tel = new Telefone(cliente);
				}
				
				if(!StringUtils.isEmpty(telefone.getNumero())) {
					tel.setNumero(telefone.getNumero());
					tel.setPrefixo(telefone.getPrefixo());
					tel.setTipo( Telefone.findById(telefone.getTipo()) );
					
					tel.save();
				}
			}
			Endereco.cleanEnderecoCache();
			Logger.debug("#### Fim - Atualizar o Endereço: %s #####", id);
		}
		renderText(Messages.get("validation.data.success", ""));
	}
	
	public static void consultarEnderecoPeloIdCliente(Long idCliente) {
		Gson gsonBuilder = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		
		renderJSON(gsonBuilder.toJson(Endereco.getEndereco(idCliente)));
	}
	
	public static void alterarSenha(String uuid) {
		Usuario usuario =  Usuario.find("email = ?", Usuario.decryptEmail(uuid)).first();
		
		if(usuario==null)
			validation.addError("senha", "form.customer.notfound", "");
		else if(usuario.getSenhaExpirada())
			validation.addError("senha", "form.customer.password.expired", "");
		
		render(usuario);
	}
	
	@Transactional(readOnly=false)
	public static void cadastrarNovaSenha(Usuario usuario,
										@Required @MinSize(message="message.user.password.minsize", value=6) String novaSenha, 
										@Required String confirmaSenha) {
		if(!novaSenha.equals(confirmaSenha))
			validation.addError("senha", "form.validation.password.notEquals", "");
		
		if(validation.hasErrors()) {
			params.flash();
			validation.keep();
		
			alterarSenha(usuario.encryptEmail(usuario.getEmail()));
			
		}else {
			Usuario user = Usuario.findById(usuario.id);
		
			user.setSenha(novaSenha);
			user.encryptPassword();
			user.setSenhaExpirada(Boolean.FALSE);
			
			user.save();
			
			render("Login/index.html", usuario, usuario.getEmail(), Messages.get("form.customer.password.change", ""));
		}
	}
}
