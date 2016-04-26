/**
 * 
 */
package controllers;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import models.CarrinhoItem;
import models.CarrinhoProduto;
import models.CestaPronta;
import models.Cliente;
import models.CupomDesconto;
import models.Desconto;
import models.Endereco;
import models.FormaPagamento;
import models.Frete;
import models.Pagamento;
import models.Pedido;
import models.Pedido.PedidoEstado;
import models.Produto;
import play.Logger;
import play.cache.Cache;
import play.db.jpa.Transactional;
import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.Catch;
import play.mvc.Controller;
import business.estoque.EstoqueControl;
import business.pagamento.service.PagamentoServiceFactory;
import business.pagamento.service.PayPalService;
import business.pagamento.service.interfaces.GatewayService;
import ebay.api.paypalapi.SetExpressCheckoutResponseType;
import exception.ProdutoEstoqueException;
import exception.SystemException;
import form.ProdutoQuantidadeForm;

/**
 * @author Felipe Guerra
 * @version 1.0
 */
public class Carrinho extends Controller {
	
	@Before(only={"pagamento", "finalizar", "loading"}) 
	static void isAuthenticated(){
		if(session.get("usuarioAutenticado")==null) {
			session.put("carrinho", request.path);
			
			Login.index(null, null, Messages.get("application.message.notAuthenticated", ""));
		}
	}
	
	@Catch(value=ProdutoEstoqueException.class, priority=0)
	public static void erroCarrinho(Exception e) {
		Logger.error(e, "Houve um erro na posição do estoque.");

		Mail.sendError("Falta no Estoque", Mail.EMAIL_ADMIN, Mail.EMAIL_ADMIN, e);
		
		Home.index(Messages.get("application.error.estoque", e.getMessage()));
	}
	
	private static void addProduto(String sessionId, Long idProduto, Integer quantidade, Double valorProduto) {
		Logger.debug("######## Vai adicionar produtos ao Cache... #########");
		CarrinhoProduto carrinho = Cache.get(sessionId, CarrinhoProduto.class);
		Produto produto = null;

		List<Produto> produtos = new ArrayList<Produto>();
		List<Produto> tempProdutos = new ArrayList<Produto>();
		int i = 0;
		
		if(carrinho==null) {
			carrinho = new CarrinhoProduto();
		}
		else
			for(CarrinhoItem item : carrinho.getItens())
				produtos.addAll(item.getProdutos());
		
		while(i<quantidade) {
			produto = Produto.findById(idProduto);
			
			tempProdutos.add(produto);
			
			i++;
		}
		produtos.addAll(tempProdutos);
		carrinho.getItens().clear();
		carrinho.createItens(produtos);
		
		Cache.add(sessionId, carrinho, "40mn");
		Logger.debug("######## Produtos adicionados ao Cache... #########");
	}
	
	private static BigDecimal adicionarProdutoCarrinho(String sessionId, Long idProduto, Integer quantidade, Double valorProduto) {
		Logger.debug("########## Adicionar sessionId: %s, idProduto: %s, quantidade: %s, valorProduto: %s ##########", sessionId, idProduto, quantidade, valorProduto);
		BigDecimal valorTotalCache = null;
		
		if(quantidade>0) {
			BigDecimal valorTotal = new BigDecimal( quantidade*valorProduto ).setScale(2, BigDecimal.ROUND_HALF_UP);
			
			/*
			 * Adicionar os dados no Cache do Play! para posterior recuperação...
			 */
			addProduto(sessionId, idProduto, quantidade, valorProduto);
			
			valorTotalCache = (Cache.get("valorTotal."+sessionId, BigDecimal.class)==null) ? new BigDecimal(0): Cache.get("valorTotal."+sessionId, BigDecimal.class);
			
			valorTotalCache = valorTotalCache.add( valorTotal );
			
			Cache.set("valorTotal."+sessionId, valorTotalCache, "40mn");
		}
		
		Logger.debug("########## Produtos adicionados para a sessão: valor: %s ##########", valorTotalCache);
		
		return valorTotalCache;
	}
	
	/**
	 * @param sessionId
	 * @param idProduto
	 * @param quantidade
	 * @param valorProduto
	 * @return valor total carrinho
	 */
	private static BigDecimal atualizarProdutoCarrinho(String sessionId, Long idProduto, Integer quantidade, Double valorProduto) {
		Logger.debug("########## Adicionar sessionId: %s, idProduto: %s, quantidade: %s, valorProduto: %s ##########", sessionId, idProduto, quantidade, valorProduto);
		BigDecimal valorTotal = null;
		
		if(quantidade>0) {
			valorTotal = new BigDecimal( quantidade*valorProduto ).setScale(2, BigDecimal.ROUND_HALF_UP);
			
			atualizarProduto(sessionId, idProduto, quantidade, valorProduto);
		}
		
		Logger.debug("########## Produtos adicionados para a sessão: valor: %s ##########", valorTotal);
		
		return valorTotal;
	}
	
	private static void atualizarProduto(String sessionId, Long idProduto, Integer quantidade, Double valorProduto) {
		Logger.debug("######## Vai adicionar produtos ao Cache... #########");
		CarrinhoProduto carrinho = Cache.get(sessionId, CarrinhoProduto.class);
		Produto produto = null;
		int i = 0;
		
		CarrinhoItem item = carrinho.getItemListProduto(idProduto);
		item.getProdutos().clear();
		item.setQuantidade(quantidade);
		
		while(i<quantidade) {
			produto = Produto.findById(idProduto);
			
			item.getProdutos().add(produto);
			
			i++;
		}
		Cache.add(sessionId, carrinho, "40mn");
		Logger.debug("######## Produtos adicionados ao Cache... #########");
	}
	
	/**
	 * Trabalhando com o Cache do Play! 
	 * @param idProduto
	 * @param quantidade
	 * @param valorProduto
	 */
	public static void adicionarProduto(String sessionId, Long idProduto, Integer quantidade, Double valorProduto) {
		renderJSON(adicionarProdutoCarrinho(sessionId, idProduto, quantidade, valorProduto));
	}
	
	/**
	 * <p>Metodo para inserir a cesta no carrinho.</p>
	 * @param sessionId
	 * @param idProduto
	 * @param quantidade
	 * @param valorProduto
	 */
	public static void adicionarCesta(String sessionId, Long idProduto, Integer quantidade, Double valorProduto) {
		CarrinhoProduto carrinho = Cache.get(sessionId, CarrinhoProduto.class);
		CestaPronta cesta = CestaPronta.findById(idProduto);
		BigDecimal valorTotalCache = BigDecimal.ZERO;
		
		if(carrinho==null)
			carrinho = new CarrinhoProduto();
		
		if(!carrinho.contains(cesta)) {
			if(Cache.get("valorTotal."+sessionId, BigDecimal.class)!=null)
				valorTotalCache = Cache.get("valorTotal."+sessionId, BigDecimal.class);
			
			valorTotalCache = valorTotalCache.add( BigDecimal.valueOf(valorProduto*quantidade).setScale(2, BigDecimal.ROUND_HALF_UP) );
			
			Cache.set("valorTotal."+sessionId, valorTotalCache, "40mn");
			
			carrinho.addCestaPronta(cesta);
			
			Cache.set(sessionId, carrinho, "40mn");
		}
		renderJSON(valorTotalCache);
	}
	
	public static void valorTotalCompra(String sessionId) {
		BigDecimal result = null;
		
		result = Cache.get("valorTotal."+sessionId, BigDecimal.class);
		
		renderJSON(result);
	}
	
	public static void view(String sessionId, String message) {
		Logger.debug("######## Início - Ver Produtos do Carrinho... ########");
		CarrinhoProduto carrinho = Cache.get(sessionId, CarrinhoProduto.class);
		
		if(carrinho!=null && (!carrinho.getItens().isEmpty() || !carrinho.getCestas().isEmpty())) {
			BigDecimal valorTotalCompra = Cache.get("valorTotal."+sessionId, BigDecimal.class);
			
			carrinho.setDataCompra(new Date());
			
			carrinho.setValorTotalCompra(valorTotalCompra);
			
			render(carrinho, sessionId, message);
			
		}else {
			Home.index(Messages.get("application.message.basket.empty", ""));
			Cache.clear();
		}
		Logger.debug("######## Fim - Ver Produtos do Carrinho... ########");
	}
	
	public static void limparCarrinho(String sessionId) {
		Logger.debug("####### Vai limpar o carrinho para a sessão %s ########", sessionId);
		
		Cache.delete("valorTotal."+sessionId);
		Cache.delete(sessionId);
		
		request.cookies.clear();
		session.remove("sessionId");
		
		redirect("/index");
	}
	
	@Transactional(readOnly=false)
	public static void finalizar(String sessionId) {
		CarrinhoProduto carrinho = Cache.get(sessionId, CarrinhoProduto.class);
		Boolean isAssessor = null;
		Boolean responderQuestionario = Boolean.FALSE;
		String formaPagamento = null;
		SetExpressCheckoutResponseType resultPayPalService = null;
		String urlCheckout = null;
		PedidoEstado statusPedido = null;
		BigDecimal credito = BigDecimal.ZERO;
		BigDecimal valorDesconto = BigDecimal.ZERO;
		Pedido pedido = null;
		
		try {
			if(carrinho!=null && session.get("clienteId")!=null) {
				validarValorCompra(carrinho.getValorTotalCompra());
				
				if(Boolean.parseBoolean(session.get("isAdmin")) && "-1".equalsIgnoreCase(params.get("cliente"))) {
					validation.addError("cliente", "message.basket.order.assessor.required", "");
					isAssessor = Boolean.TRUE;
				}
				
				formaPagamento = params.get("formaPagamento");
				
				if(validation.hasErrors()) {
					params.flash();
					validation.keep();
					
					Logger.debug("###### Não foi possível finalizar o pedido: %s ######", validation.errors());
					
					pagamento(sessionId, isAssessor);
					
				}else {
					//Validar o Estoque	
					EstoqueControl.atualizarEstoque(carrinho.getItens());

					Long idCliente = params.get("cliente")==null ? Long.parseLong(session.get("clienteId")) : Long.parseLong(params.get("cliente"));
					
					Cliente cliente = Cliente.findById(idCliente);
					
					Frete frete = new Frete(Pedido.calcularFrete(carrinho.getValorTotalCompra(), cliente.estaNaCapital()));
					
					Pagamento pagamento = new Pagamento();

					carrinho.setCliente(cliente);
					
					credito = Pedido.getDebitosCreditosTodosPedidosCliente(cliente.id);
					
					//Adiciona o crédito se, e somente se, for positivo...valores negativos precisam ser vistos no processo (a entrega)
					if(credito.doubleValue()>0) {
						carrinho.setValorTotalCompra(carrinho.getValorTotalCompra().subtract(credito));
						Pedidos.zerarDebitosCreditos(null, cliente.id);
						Logger.info("####### Cr�dito R$ %s utilizado para o cliente: %s #######", credito, cliente.getNome());
					}
					
					if(carrinho.id==null)
						carrinho.save();
					else
						carrinho.merge();
					
					pedido = new Pedido();
					
					Desconto desconto = CupomDesconto.getDescontoDoCupom(session.get("cupom"), cliente);
					
					//Tem que adicionar o desconto (cupom) no valor do carrinho
					if(desconto!=null) {
						desconto.calcularDesconto(carrinho.getValorTotalCompra());
						carrinho.setValorTotalCompra( carrinho.getValorTotalCompra() );
						valorDesconto = valorDesconto.add(desconto.getValorDesconto());
					}
					
					if(FormaPagamento.PAGSEGURO.equals(FormaPagamento.getFormaPagamento(formaPagamento))) {
						GatewayService pagSeguroService = PagamentoServiceFactory.getInstance().getGatewayServiceImpl(FormaPagamento.PAGSEGURO);
						
						String token = pagSeguroService.checkout(cliente, carrinho, frete.getValor(), carrinho.getValorTotalCompra().subtract(valorDesconto).setScale(2, BigDecimal.ROUND_HALF_DOWN).doubleValue());
						
						urlCheckout = Messages.get("application.redirectUrl.pagseguro", token);
						
						statusPedido = PedidoEstado.AGUARDANDO_PAGAMENTO;
						
						pagamento.setInformacoes(String.valueOf(carrinho.id));
						pagamento.setFormaPagamento(FormaPagamento.PAGSEGURO);
					
					}else if(FormaPagamento.PAYPAL.equals(FormaPagamento.getFormaPagamento(formaPagamento))) {
						StringBuffer infosPedido = new StringBuffer();
						pagamento.setFormaPagamento(FormaPagamento.PAYPAL);
						
						PayPalService payPalService = PayPalService.newInstance(Messages.get("application.service.url.paypal", ""), 
																				Messages.get("application.username.url.paypal", ""), 
																				Messages.get("application.password.url.paypal", ""), 
																				Messages.get("application.signature.url.paypal", ""));
						infosPedido.append("Pedido gerado em: ").append(new Date()).append(".");
						infosPedido.append("Cliente: ").append(cliente.getNome()).append(".");
						infosPedido.append("Código Pedido: ").append(carrinho.id);
						infosPedido.append("Total Pedido: ").append(carrinho.getValorTotalCompra().add(frete.getValor()));
						
						resultPayPalService = payPalService.solicitarPagamento(cliente.getNome(), carrinho.getValorTotalCompra().add(frete.getValor()).subtract(valorDesconto).setScale(2, BigDecimal.ROUND_HALF_DOWN).doubleValue(), 
																				carrinho.id, infosPedido.toString());
						
						if(payPalService.foiExecutadoComSucesso(resultPayPalService.getAck(), resultPayPalService.getErrors())) {
							pagamento.setInformacoes(resultPayPalService.getToken());
							
							urlCheckout = Messages.get("application.checkout.url.paypal", "");
							urlCheckout += resultPayPalService.getToken();
							
							statusPedido = PedidoEstado.AGUARDANDO_PAGAMENTO;
							
						}else {
							Logger.info("#### O pedido de pagamento no Paypal não foi aprovado: %s ####", PayPalService.getInformacoesErro(resultPayPalService.getErrors()));
						}
						
					}else if(FormaPagamento.DINHEIRO.equals(FormaPagamento.getFormaPagamento(formaPagamento))){
						BigDecimal valorPedidoComDesconto = new BigDecimal(Messages.get("application.valor.pedido.desconto", ""));
						
						pagamento.setFormaPagamento(FormaPagamento.DINHEIRO);
						
						if(desconto==null && carrinho.getValorTotalCompra().doubleValue()>valorPedidoComDesconto.doubleValue()) {
							desconto = new Desconto(new BigDecimal(Messages.get("application.pedido.paypal.desconto", "")));
							desconto.setDataDesconto(new Date());
							desconto.getPedidos().add(pedido);
						}
						
						statusPedido = Pedido.setPedidoEstado();
					}
					//D� baixa no cupom utilizado
					CupomDesconto.atualizarCupomDesconto(session.get("cupom"), cliente);
					
					// Gerar Pedido
					pedido.addCesta(carrinho.getCestas());
					
					if(!carrinho.getCestas().isEmpty()) {
						pedido.setObservacao(carrinho.getCestas().get(0).getTitulo());
						//Indica se o pedido foi gerado a partir de uma cesta pronta
						pedido.setEhPedidoDeCesta(Boolean.TRUE);
					}
					pedido.setCodigoPedido(String.valueOf(carrinho.getId()));
					pedido.addPedidoItem(carrinho.getItens());
					pedido.setCliente(cliente);
					pedido.setDataPedido(new Date());
					pedido.setValorPedido(carrinho.getValorTotalCompra());
					pedido.setCodigoEstadoPedido(statusPedido);
					pedido.setArquivado(Boolean.FALSE);
					pagamento.setPedido(pedido);
					pagamento.setValorPagamento(pedido.getValorPedido());
					
					pedido.setValorPedidoFinalizado(carrinho.getValorTotalCompra());
					pedido.setPagamento(pagamento);
					pedido.setOutrasDespesas(credito);
					
					if(desconto!=null) {
						pedido.setDesconto(new Desconto(desconto.getPorcentagem(), cliente.getUsuario(), pedido));
						pedido.calcularDesconto();
					}
					
					frete.addPedido(pedido);
					frete.save();
					pedido.save();
					
					gerarNotaPedidoEnviarPorEmail(pedido, cliente);
					
					Cache.safeDelete(sessionId);
					Cache.safeDelete("valorTotal." + sessionId);
					session.remove("cupom");
					Logger.debug("######## Cache limpo e Pedido gerado: %s ########", pedido.id);
	
					if(urlCheckout==null) {
						String pedidoFinalizado = String.valueOf(pedido.id);
	
						responderQuestionario = Questionarios.haQuestionarioPendente(cliente.getUsuario().getId());
						
						render(pedidoFinalizado, cliente, responderQuestionario, pedido);
					}
					redirect(urlCheckout);
				}
				
			}else {
				Home.index(Messages.get("application.message.basket.empty", ""));
			}
			
		}catch(Exception ex) {
			Logger.error(ex, "Ocorreu um erro ao tentar finalizar o pedido.");
			EstoqueControl.reporEstoque(carrinho.getItens());
			
			validation.addError("cliente", ex.getMessage(), "");
			params.flash();
			validation.keep();
			
			pagamento(sessionId, isAssessor);
		}
	}
	
	private static void gerarNotaPedidoEnviarPorEmail(Pedido pedido, Cliente cliente) {
		String caminhoArquivo = null;
		FileOutputStream fis = null;
		
		try {
			StringBuffer email = new StringBuffer();
			email.append("Vida Saudável Orgânicos");
			email.append("<").append("contato@vidasaudavelorganicos.com.br").append(">");
			
//			tempFile = File.createTempFile(Relatorios.REPORT_TITLE, ".pdf");
//			tempFile.deleteOnExit();
//			fis = new FileOutputStream(tempFile);
//			
//			IOUtils.copy(Relatorios.gerarNotaFiscalPedido(pedido.id), fis);
//			caminhoArquivo = tempFile.getAbsolutePath();
			
			Logger.info("Valor Desconto %s", pedido.getValorDesconto());
			Logger.info("###### E-mail de confirmação do pedido para: %s #######", cliente.getUsuario().getEmail());
			
			Mail.pedidoFinalizado(
					"Pedido Finalizado",
					email.toString(), 
					pedido,
					caminhoArquivo,
					cliente.getUsuario().getEmail(), Mail.EMAIL_CONTACT
					);

			Logger.info("###### E-mail de confirmação do pedido para: %s enviado. #######", cliente.getUsuario().getEmail());
			
//		}catch(IOException ioex) {
//			Logger.error(ioex, "Erro ao gerar a nota para o cliente %s", cliente.getUsuario().getEmail());
								
		}catch(SystemException ex) {
			Logger.error(ex, "Erro ao enviar o e-mail de confirmação para %s", cliente.getUsuario().getEmail());
			
		}finally {
			if(fis!=null)
				try {
					fis.close();
				}catch(IOException ex) {
					//ignore
				}
		}
	}
	
	public static void excluirProdutos(String sessionId, List<ProdutoQuantidadeForm> produtoQuantidade, List<CestaPronta> cestas) {
		Logger.debug("##### Início - Excluir produtos do carrinho. #####");
		
		CarrinhoProduto carrinho = Cache.get(sessionId, CarrinhoProduto.class);
		String message = null;
		
		if( (produtoQuantidade==null || produtoQuantidade.isEmpty()) && (cestas==null || cestas.isEmpty()) ) {
			validation.addError("", Messages.get("message.product.required", ""));
			
			validation.keep();
			
		}else {
			if(produtoQuantidade!=null) {
				for(ProdutoQuantidadeForm prod : produtoQuantidade) {
					Produto tempProduto = new Produto(prod.getId());
					
					laco_carrinho:
					for(CarrinhoItem item : carrinho.getItens()) {
						if(item.getProdutos().get(0).id.equals(tempProduto.id)) {
							//Subtrair o valor dos produtos
							BigDecimal result = Cache.get("valorTotal."+sessionId, BigDecimal.class);
							BigDecimal newValue = result.subtract(new BigDecimal( item.getQuantidade() * item.getProdutos().get(0).getValorVenda() ).setScale(2, BigDecimal.ROUND_HALF_UP));
							
							carrinho.setValorTotalCompra(newValue);
							Logger.debug("#### Novo valor do carrinho: %s ####", newValue);
							
							carrinho.getItens().remove(item);
							
							Cache.set("valorTotal."+sessionId, newValue, "40mn");
							break laco_carrinho;
						}
					}
				}
			}
			if(cestas!=null) {
				BigDecimal result = Cache.get("valorTotal."+sessionId, BigDecimal.class);
				
				for(CestaPronta cesta : cestas) {
					BigDecimal valorCesta = CestaPronta.find("select valorVenda from CestaPronta where id = ?", cesta.id).first();
					
					BigDecimal newValue = result.subtract(valorCesta).setScale(2, BigDecimal.ROUND_HALF_UP);
					
					if(carrinho.getCestas().remove(cesta)) {
						carrinho.setValorTotalCompra(newValue);
						
						Cache.set("valorTotal."+sessionId, newValue, "40mn");					
					}
				}
			}
			message = Messages.get("validation.data.success", "");
		
			if(carrinho.getCestas().isEmpty() && carrinho.getItens().isEmpty())
				limparCarrinho(sessionId);
		}
		
		Logger.debug("##### Fim - Excluir produtos do carrinho. #####");
		
		view(sessionId, message);
	}
	
	public static void pagamento(String sessionId, Boolean isAssessor) {
		Logger.debug("###### Início - Selecionar forma de pagamento... ######");
		Endereco endereco = null;
		CarrinhoProduto carrinho = Cache.get(sessionId, CarrinhoProduto.class);
		Frete frete = new Frete(0.0d);
		List<Cliente> clientes = null;
		Boolean pedidoAssessor = Boolean.TRUE.equals(isAssessor) && Boolean.parseBoolean(session.get("isAdmin"));
		BigDecimal valorMinPagPayPal = null;
		BigDecimal valorComDesconto = null;
		BigDecimal valorPagamentoComDesconto = null;
		BigDecimal credito = BigDecimal.ZERO;
		CupomDesconto cupom = null;
		
		if(carrinho!=null && session.get("clienteId")!=null) {
			valorPagamentoComDesconto = new BigDecimal(Messages.get("application.valor.pedido.desconto", ""));
			valorMinPagPayPal = new BigDecimal(Messages.get("application.minValue.gateways", ""));
			
			if(pedidoAssessor) {
				clientes = Cliente.find("ativo = ? order by nome ASC", Boolean.TRUE).fetch();
			}
			validarValorCompra(carrinho.getValorTotalCompra());
			
			if(validation.hasErrors()) {
				validation.keep();
				
				try {
					Mail.sendPedidoNaoFinalizado("Pedido Não Fechado", 
							Mail.EMAIL_CONTACT, 
							Long.parseLong(session.get("clienteId")), 
							"marcos.onofre@vidasaudavelorganicos.com.br");

				} catch (SystemException e) {
					// TODO Auto-generated catch block
				}
				
				Logger.info("###### Não foi possível finalizar o pedido: %s ######", validation.errors());
			}else {
				carrinho.setDataCompra(new Date());
				
				if(!pedidoAssessor) {
					Cliente cliente = Cliente.findById( Long.parseLong(session.get("clienteId")) );
					
					frete = new Frete(Pedido.calcularFrete(carrinho.getValorTotalCompra(), cliente.estaNaCapital()));
					
					endereco = cliente.getEnderecos().get(0);
					
					carrinho.setCliente(cliente);
					
					cupom = CupomDesconto.pesquisarPorCodigoCupom(session.get("cupom"), cliente);
					
					credito = Pedido.getDebitosCreditosTodosPedidosCliente(cliente.id);
				}
				
				if(cupom!=null)
					valorComDesconto = CupomDesconto.calcularDescontoCarrinhoComCupom(cupom, carrinho);
				else
					valorComDesconto = Pedido.calcularDesconto(carrinho.getValorTotalCompra(), new BigDecimal(Messages.get("application.pedido.paypal.desconto", "")));
			}
			
		}else {
			Home.index(Messages.get("application.message.basket.empty", ""));
		}
		
		Logger.debug("###### Fim - Selecionar forma de pagamento... ######");
		
		FormaPagamento pagamento = FormaPagamento.DINHEIRO; 
		
		render(carrinho, sessionId, frete, endereco, clientes, isAssessor, pagamento, valorMinPagPayPal, 
				valorComDesconto, valorPagamentoComDesconto, cupom, credito);
	}
	
	public static void atualizar(String sessionId, List<ProdutoQuantidadeForm> produtoQuantidade, List<CestaPronta> cestas) {
		Logger.debug("###### Início - Atualizar Produtos...%s ######", sessionId);
		BigDecimal valorTotalCache = Cache.get("valorTotal."+sessionId, BigDecimal.class);
		String message = null;

		if(produtoQuantidade==null || produtoQuantidade.isEmpty()) {
			validation.addError("", Messages.get("message.product.required", ""));
			
			validation.keep();
			
		}else {
			CarrinhoProduto carrinho = Cache.get(sessionId, CarrinhoProduto.class);
			
			for(ProdutoQuantidadeForm prod : produtoQuantidade) {
				Produto tempProduto = new Produto(prod.getId());
				
				laco_1:
				for(CarrinhoItem item : carrinho.getItens()) {
					if(item.getProdutos().get(0).id.equals(tempProduto.id)) {
						if(ProdutoQuantidadeForm.findQuantidade(produtoQuantidade, tempProduto.id)!=null) {
							//Retirar o valor anterior do Total guardado no Cache
							BigDecimal valorAnterior = new BigDecimal( item.getProdutos().get(0).getValorVenda() * item.getQuantidade() ).setScale(2, BigDecimal.ROUND_HALF_UP); 
							valorTotalCache = valorTotalCache.subtract(valorAnterior); 
							valorTotalCache = valorTotalCache.add(atualizarProdutoCarrinho(sessionId, tempProduto.id, 
												ProdutoQuantidadeForm.findQuantidade(produtoQuantidade, tempProduto.id), item.getProdutos().get(0).getValorVenda())
												);	
						}
						break laco_1;
					}
				}
			}
			Cache.set("valorTotal."+sessionId, valorTotalCache, "40mn");
			
			message = Messages.get("validation.data.success", "");
		}
		Logger.debug("###### Fim - Atualizar Produtos...%s ######", sessionId);
		
		view(sessionId, message);
	}
	
	public static void loading(String sessionId, String pagamentoSolicitado) {
		String formaPagamento = FormaPagamento.getFormaPagamento(pagamentoSolicitado).getDescricao();
		
		render(sessionId, formaPagamento);
	}
	
	private static void validarValorCompra(BigDecimal valorCompra) {
		if(Double.parseDouble(Messages.get("application.order.minValue", ""))>valorCompra.doubleValue())
			validation.addError("Valor Compra", Messages.get("message.validation.order.minValue", ""), "");
	}
	
	public static Boolean validarValorCompra(Double valorCompra) {
		if(Double.parseDouble(Messages.get("application.order.minValue", ""))>valorCompra.doubleValue())
			return Boolean.FALSE;
		
		return Boolean.TRUE;
	}
}
