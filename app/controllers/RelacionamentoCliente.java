/**
 * 
 */
package controllers;

import java.io.File;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import models.Cliente;
import models.CupomDesconto;
import models.Desconto;
import models.Pedido;
import models.Usuario;

import org.apache.commons.lang.StringUtils;

import play.Logger;
import play.data.binding.As;
import play.data.validation.Required;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.i18n.Messages;
import play.mvc.Before;
import exception.SystemException;

/**
 * <p>Controller para todas as funcionalidades de CRM</p>
 * @author Felipe Guerra
 * @version 1.0
 */
public class RelacionamentoCliente extends BaseController {
	
	@Before
	static void estaAutorizado() {
		Logger.debug("####### Verificar se o usuário autenticado é admin... ########");
		
		if( (StringUtils.isEmpty(session.get("isAdmin")) || Boolean.FALSE.equals(Boolean.valueOf(session.get("isAdmin")))) 
			&& (StringUtils.isEmpty(session.get("isEmployee")) && Boolean.FALSE.equals(Boolean.valueOf(session.get("isEmployee")))) ) 
		{
			Logger.debug("####### Usuário não autorizado a acessar essa funcionalidade...%s ########", session.get("usuarioAutenticado"));
			
			Home.index("Usuário não autorizado a acessar essa funcionalidade.");
		}
	}
	
	public static void index(String message, Desconto desconto) {
		if(desconto==null)
			desconto = new Desconto(BigDecimal.ZERO); 
		
		List<Cliente> clientes = Cliente.find("ativo = ? order by nome ASC", Boolean.TRUE).fetch();
		
		render(desconto, message, clientes);
	}
	
	@Transactional(readOnly=false)
	public static void gerarCuponsCliente(@Required(message="Por favor, especifique o desconto") Desconto desconto, 
										@Required(message="Por favor, preencha a validade do cupom") Integer validadeCupomEmDias,
										@Required(message="Por favor, preencha a qtde máxima de uso do cupom") Integer qtdMaxUsoCupom,
										Boolean todosClientes,
										List<Long> clientesSelecionados) {
		CupomDesconto cupom = null;
		String result = null;
		boolean hasSent = false;
		Query query = null;
		List<Cliente> clientes = null;
		String subject = null;
		int contador = 0;
		Usuario usuarioLogado = null;
		StringBuilder cupomDesconto = null;
		
		if(validation.hasErrors()) {
			params.flash();
			validation.keep();
			
			index(result, desconto);
			
		}else {
			if(clientesSelecionados==null) {
				query = JPA.em().createNamedQuery("pesquisarClienteComAlgumPedidoValido");
				query.setParameter("parameters", Pedido.PedidoEstado.FINALIZADO);
				
			}else {
				query = JPA.em().createNamedQuery("pesquisarClientesCupomDesconto");
				query.setParameter("parameters", clientesSelecionados);
				
			}
			clientes = query.getResultList();
			usuarioLogado = Usuario.find("cliente.id = ?", Long.parseLong(session.get("clienteId"))).first();
			result = Messages.get("application.crm.cupom.desconto.success", "");
			subject = Messages.get("application.crm.cupom.desconto.email.subject", "");

			desconto.setDataDesconto(new Date());
			desconto.setUsuario(usuarioLogado);
			
			for(Cliente cliente : clientes) {
				contador++;
				cupom = CupomDesconto.gravarCodigoDescontoParaCliente(cliente, desconto, cliente.getCpf(), session.get("usuarioAutenticado"), validadeCupomEmDias, qtdMaxUsoCupom);
				
				File tmp = Relatorios.gerarCupomDescontoCliente(cupom.getDesconto().getPorcentagem(), cupom.getDiasValidadeCupom(), cupom.getCodigo());
				
				cupomDesconto = new StringBuilder();
				cupomDesconto.append(Messages.get("application.static.content", "")).append(Messages.get("application.path.public.images", ""));
				cupomDesconto.append(cupom.getCodigo()).append("/");
				cupomDesconto.append(Relatorios.CUPOM_DESCONTO_FILE).append(".bmp");
				
				try {
					Mail.enviarCupomDescontoCliente(subject, Mail.EMAIL_CONTACT, cupom, cupomDesconto.toString(), cliente);
					hasSent = true;
					
				}catch(SystemException sysex) {
					Logger.error(sysex, "Erro ao enviar o e-mail com o cupom para o cliente: ", cliente.getNome());
				}
				if(contador%50==0) {
					JPA.em().flush();
					JPA.em().getTransaction().commit();
					JPA.em().getTransaction().begin();
				}
			}

			if(!hasSent)
				result = Messages.get("application.crm.cupom.desconto.fail", "");
			
			index(result, null);
		}
	}

}
