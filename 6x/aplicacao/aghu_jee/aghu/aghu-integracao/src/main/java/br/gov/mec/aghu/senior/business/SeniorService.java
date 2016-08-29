package br.gov.mec.aghu.senior.business;

import javax.ejb.Stateless;

import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.exames.vo.ClienteNfeVO;
import br.gov.mec.aghu.senior.ISeniorService;

/**
 * Fachada para o modulo de integração do AGHU com sistema de pedidos de papelaria.
 * 
 * @author alisson.salin
 * 
 */

@Stateless
public class SeniorService extends BaseFacade implements ISeniorService {

	private static final long serialVersionUID = 392083271408969106L;
	
	@Override
	public Long gravarClienteNota(ClienteNfeVO clienteNfeVo) throws ApplicationBusinessException {
		return getSeniorIntegracaoON().gravarClienteNota(clienteNfeVo);
	}
	
	private SeniorIntegracaoON getSeniorIntegracaoON() {
		return new SeniorIntegracaoON();
	}

}
