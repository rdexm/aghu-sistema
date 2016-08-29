package br.gov.mec.aghu.business.integracao;

import java.util.Map;

import javax.ejb.EJBException;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.integracao.exception.AGHUIntegracaoException;
import br.gov.mec.aghu.core.business.BaseBusiness;

/**
 * Superclasse das ONs de integração, com métodos auxiliares para invocar os
 * serviços.
 * 
 * @author aghu
 * 
 */
@SuppressWarnings({ "PMD.PackagePrivateSeamContextsManager",
		"PMD.ClassesOnRnNaoDevemSerPublicas", "ucd" })
public class AGHUIntegracao extends BaseBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8796020152392177727L;

	private static final Log LOG = LogFactory.getLog(AGHUIntegracao.class);

	@Inject
	private InvocadorServicos invocadorServicos;

	/**
	 * 
	 * Invoca um serviço de forma síncrona, esperando uma resposta. A transação
	 * fica suspensa até o retorno do método.
	 * 
	 * @param servico
	 * @param parametros
	 * @param tempoEspera
	 *            defautl 1 min 60000, se passado paramêtro tempo igual ao
	 *            paramêtro.
	 * @return
	 */
	protected Object invocarServicoSincrono(ServiceEnum servico,
			Map<String, Object> parametros, Integer tempoEspera)
			throws AGHUIntegracaoException {
		try {
			return invocadorServicos.invocarServicoSincrono(servico,
					parametros, tempoEspera);
		} catch (EJBException e) {
			if (e.getCause() instanceof AGHUIntegracaoException) {
				throw (AGHUIntegracaoException) e.getCause();
			} else {
				throw e;
			}
		}
	}

	/**
	 * Invoca um serviço de forma assíncrona. Já que não é esperada uma
	 * resposta, a transação é propagada normalmente
	 * 
	 * @param servico
	 * @param parametros
	 * @throws AGHUIntegracaoException
	 */
	protected void invocarServicoAssincrono(ServiceEnum servico,
			Map<String, Object> parametros) throws AGHUIntegracaoException {

		invocadorServicos.invocarServicoAssincrono(servico, parametros);

	}

	@Override
	protected Log getLogger() {
		return LOG;
	}

	// /**
	// * Retorna a classe responsável por efetuar a chamada aos serviços.
	// *
	// * @return
	// */
	// protected InvocadorServicos obterInvocadorServicos() {
	// return (InvocadorServicos) Component
	// .getInstance("invocadorServicosBean");
	//
	// }

}
