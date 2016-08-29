package br.gov.mec.aghu.papelaria.business;

import javax.ejb.Stateless;

import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.papelaria.IPapelariaService;
import br.gov.mec.aghu.sig.custos.vo.PedidosPapelariaVO;

/**
 * Fachada para o modulo de integração do AGHU com sistema de pedidos de papelaria.
 * 
 * @author alisson.salin
 * 
 */

@Stateless
public class PapelariaService extends BaseFacade implements IPapelariaService {

	private static final long serialVersionUID = 392083271408969106L;
	
	@Override
	public PedidosPapelariaVO buscarPedidosPapelaria(String autorizacao, String papelariaServiceAddress) throws ApplicationBusinessException {
		return getPapelariaIntegracaoON().buscarPedidosPapelaria(autorizacao, papelariaServiceAddress);
	}
	
	private PapelariaIntegracaoON getPapelariaIntegracaoON() {
		return new PapelariaIntegracaoON();
	}

}
