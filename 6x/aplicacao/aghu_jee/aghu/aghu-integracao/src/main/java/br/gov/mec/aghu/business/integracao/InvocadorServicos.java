package br.gov.mec.aghu.business.integracao;

import java.util.Map;

import javax.ejb.Local;

import br.gov.mec.aghu.business.integracao.exception.AGHUIntegracaoException;

/**
 * Interface local do EJB stateless usado para invocar serviços do ESB.
 * 
 * @author aghu
 * 
 */
@Local
public interface InvocadorServicos {

	Object invocarServicoSincrono(ServiceEnum servico,
			Map<String, Object> parametros, Integer tempoEspera)
			throws  AGHUIntegracaoException;

	void invocarServicoAssincrono(ServiceEnum servico,
			Map<String, Object> parametros)
			throws AGHUIntegracaoException;

}
