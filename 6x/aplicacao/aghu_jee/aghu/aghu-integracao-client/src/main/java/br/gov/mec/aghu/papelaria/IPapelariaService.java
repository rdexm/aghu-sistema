package br.gov.mec.aghu.papelaria;

import br.gov.mec.aghu.sig.custos.vo.PedidosPapelariaVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * Interface da Fachada para o modulo de integração do AGHU com sistema de pedidos de papelaria.
 * 
 * @author alisson.salin
 * 
 */
@SuppressWarnings("ucd")
public interface IPapelariaService {
	PedidosPapelariaVO buscarPedidosPapelaria(String autorizacao, String papelariaServiceAddress) throws ApplicationBusinessException;
}
