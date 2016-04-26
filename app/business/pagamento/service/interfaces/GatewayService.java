/**
 * 
 */
package business.pagamento.service.interfaces;

import java.io.Serializable;
import java.math.BigDecimal;

import exception.GatewayServiceException;

import models.CarrinhoProduto;
import models.Cliente;

/**
 * <p>Interface que representa o Gateway de Pagamento</p>
 * @author Felipe Guerra
 * @version 1.0
 * @since 08/11/2013
 */
public interface GatewayService extends Serializable {

	public String checkout(Cliente cliente, CarrinhoProduto carrinho, BigDecimal valorFrete, Double valorPedido) throws GatewayServiceException;
	
}
