/**
 * 
 */
package controllers;

import java.util.Date;
import java.util.List;

import models.Cliente;
import models.Pergunta;
import models.PerguntaResposta;
import models.Questionario;
import models.Resposta;
import models.RespostaUsuarioQuestionario;
import models.Usuario;

import org.apache.commons.lang.StringUtils;

import play.Logger;
import play.data.binding.As;
import play.data.validation.Required;
import play.data.validation.Valid;
import play.db.jpa.Transactional;
import play.i18n.Messages;
import play.modules.paginate.ValuePaginator;
import play.mvc.Before;
import form.PerguntaRespostaForm;

/**
 * @author hpadmin
 *
 */
public class Questionarios extends BaseController {
	
	@Before
	static void isAuthenticated() {
		Logger.debug("####### Validar se usuário está autenticado...#######");

		if(session.get("usuarioAutenticado")==null)
			Login.index(null, null, Messages.get("application.message.notAuthenticated", ""));
	}
	
	public static void cadastrar() {
		Questionario questionario = new Questionario();
		
		render(questionario);
	}
	
	public static void perguntas() {
		render();
	}
	
	public static void respostas() {
		render();
	}
	
	public static void changeStatus(Long id) {
		Questionario questionario = Questionario.findById(id);
		
		questionario.setAtivo(!questionario.getAtivo());
		questionario.save();
		
		show();
	}
	
	public static void show() {
		List<Questionario> questionarios = Questionario.findAll();
		ValuePaginator<Questionario> vQuestionarios = new ValuePaginator<Questionario>(questionarios);
		vQuestionarios.setPageSize(20);
		
		render(vQuestionarios);
	}
	
	@Transactional(readOnly=false)
	public static void salvarPerguntas(@As(";") List<String> perguntas) {
		Logger.debug("##### Início - Inserir Perguntas #####", "");
		
		if(perguntas==null || perguntas.isEmpty()) {
			validation.addError("perguntas", "message.questionario.error", "Pergunta");
		}
		
		if(validation.hasErrors()) {
			params.flash();
			validation.keep();
			
		}else {
			Pergunta tempPergunta = null;
			
			for(String pergunta : perguntas) {
				if(pergunta!=null && !StringUtils.isEmpty(pergunta.trim())) {
					tempPergunta = new Pergunta(pergunta);
					
					tempPergunta.save();
				}
			}
			flash.success("Perguntas cadastradas com sucesso!", "success");
			Logger.debug("##### Perguntas inseridas com sucesso! #####", "");
		}
		perguntas();
	}

	@Transactional(readOnly=false)
	public static void salvarRespostas(@As(";") List<String> respostas) {
		Logger.debug("##### Início - Inserir Respostas #####", "");
		
		if(respostas==null || respostas.isEmpty()) {
			validation.addError("perguntas", "message.questionario.error", "Resposta");
		}
		
		if(validation.hasErrors()) {
			params.flash();
			validation.keep();
			
		}else {
			for(String resp : respostas) {
				if(resp!=null && !StringUtils.isEmpty(resp.trim())) {
					Resposta resposta = new Resposta(resp);
					
					resposta.save();
				}
			}
			
			Logger.debug("##### Respostas inseridas com sucesso! #####", "");
			flash.success("Respostas cadastradas com sucesso!", "success");
		}
		respostas();
	}
	
	public static void associar() {
		Questionario questionario = new Questionario();
		List<Pergunta> perguntas = Pergunta.find("ativo = ?", Boolean.TRUE).fetch();
		List<Resposta> respostas = Resposta.findAll();
		
		render(questionario, perguntas, respostas);
	}
	
	@Transactional(readOnly=false)
	public static void salvarAssociacao(@Valid Questionario questionario, 
										Pergunta pergunta,
										@Required(message="Selecione ao menos uma resposta") List<Long> respostas) {
		Logger.debug("##### Início - Cadastrar Questionário %s  #####", questionario.getTitulo());
		
		if(validation.hasErrors()) {
			params.flash();
			validation.keep();
			
			associar();
			
		}else {
			Resposta resposta = null;
			
			for(Long idResposta : respostas) {
				if(PerguntaResposta.find("pergunta.id = ? AND resposta.id = ?", pergunta.id, idResposta).first()==null) {
					resposta = Resposta.findById(idResposta);
					
					PerguntaResposta perguntaResposta = new PerguntaResposta(pergunta, resposta);
					pergunta.addResposta(perguntaResposta);
					
					perguntaResposta.save();
				}
			}
			
			questionario.addPergunta(pergunta);
			questionario.setDataCadastro(new Date());
			questionario.setAtivo(Boolean.TRUE);
			
			questionario.save();
			
			Logger.debug("##### Fim - Cadastrar Questionário %s  #####", questionario.getTitulo());
			
			show();
		}
	}
	
	public static void carregarPerguntaResposta(Long id) {
		Logger.debug("##### Carregar questionário: %s #####", id);
		
		Questionario questionario = Questionario.findById(id);
		
		render(questionario);
	}
	
	public static Boolean haQuestionarioPendente(Long idUsuario) {
		Logger.debug("######### Início - há Questionário ativo...usuário id: %s #########", idUsuario);
		
		Usuario usuario = Usuario.findById(idUsuario);
		
		Questionario questionario = Questionario.find("ativo = ? order by id DESC", Boolean.TRUE).first();
		
		return (questionario!=null && !questionario.getPerguntas().isEmpty()) && usuario.verificaNecessidadeResponderQuestionario(questionario);
	}
	
	public static void questionario() {
		Logger.debug("######### Início Lógica para resposta do Questionário #########", "");
		String clienteId = session.get("clienteId");
		Long usuarioId = null;
		Boolean responder = Boolean.FALSE;
		
		if(!StringUtils.isEmpty(clienteId)) {
			Cliente cliente = Cliente.findById(Long.parseLong(clienteId));
			Usuario usuario = cliente.getUsuario();
			usuarioId = usuario.id;
			Questionario questionario = Questionario.find("ativo = ? order by id DESC", Boolean.TRUE).first();
			
			if(usuario.verificaNecessidadeResponderQuestionario(questionario)) {
				responder = Boolean.TRUE;
				Logger.info("######### Fim Lógica para resposta do Questionário - usuário: %s #########", usuario.getEmail());
			}
			Logger.debug("######### Fim Lógica para resposta do Questionário %s - Cliente %s #########", questionario.id, clienteId);
			render(questionario, usuarioId, responder);
		}
	}
	
	@Transactional(readOnly=false)
	public static void responderQuestionario(@Required Long usuarioId, 
											@Required Long questionarioId,
											List<PerguntaRespostaForm> respostas) {
		Logger.info("######### Usuário: %s que vai responder questionário: %s. #########", usuarioId, questionarioId);
		Usuario usuario = null;
		Questionario questionario = null;

		if(usuarioId!=null && questionarioId!=null) {
			usuario = Usuario.findById(usuarioId);
			questionario = Questionario.findById(questionarioId);
			
			if(usuario!=null && questionario!=null) {
				RespostaUsuarioQuestionario respostaQuestionario = new RespostaUsuarioQuestionario();
				respostaQuestionario.setDataResposta(new Date());
				respostaQuestionario.setQuestionario(questionario);
				respostaQuestionario.setUsuario(usuario);
				
				for(PerguntaRespostaForm resposta : respostas) {
					Resposta resp = Resposta.findById(resposta.getIdResposta());
					
					respostaQuestionario.addResposta(resp);
				}
				
				usuario.getRespostasQuestionarios().add(respostaQuestionario);

				respostaQuestionario.save();
			}
			renderText("Obrigado pela resposta!");
		}
	}
	
	public static void showRespostas(Long id) {
		Logger.debug("##### Início - Carregar respostas do questionário: %s ######", id);
		
		Questionario questionario = Questionario.findById(id);
		
		Logger.debug("##### Fim - Carregar respostas do questionário: %s ######", id);
		render(questionario);
	}
}
