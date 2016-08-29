package br.gov.mec.aghu.business.integracao;

import java.util.Map;
import java.util.Map.Entry;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.soa.esb.client.ServiceInvoker;
import org.jboss.soa.esb.couriers.FaultMessageException;
import org.jboss.soa.esb.listeners.message.MessageDeliverException;
import org.jboss.soa.esb.listeners.message.MissingServiceException;
import org.jboss.soa.esb.message.Message;
import org.jboss.soa.esb.message.format.MessageFactory;
import org.jboss.soa.esb.services.registry.RegistryException;

import br.gov.mec.aghu.business.integracao.exception.AGHUIntegracaoException;
import br.gov.mec.aghu.business.integracao.exception.ESBInacessivelException;
import br.gov.mec.aghu.business.integracao.exception.ExecucaoServicoException;
import br.gov.mec.aghu.business.integracao.exception.ExecucaoServicoNegocioException;
import br.gov.mec.aghu.business.integracao.exception.ServicoIndisponivelException;
import br.gov.mec.aghu.business.integracao.exception.ServicoSemRespostaException;

/**
 * Classe usada para Inovacar serviços do ESB de forma síncrona e assíncrona,
 * incluindo o controlhe de transação necessário.
 * 
 * @author aghu
 * 
 */
@Stateless

@SuppressWarnings("PMD.SuspiciousConstantFieldName")
public class InvocadorServicosBean implements InvocadorServicos {
	
	private static final Log LOG = LogFactory.getLog(InvocadorServicosBean.class);

	// Timeout padrão: 1 minuto
	private static Integer TEMPO_PADRAO = 60000;

	/**
	 * Invoca um serviço de forma síncrona, esperando uma resposta. A transação,
	 * se houver, fica suspensa até o retorno do método.
	 */
	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public Object invocarServicoSincrono(ServiceEnum servico,
			Map<String, Object> parametros, Integer tempoEspera ) throws AGHUIntegracaoException {

		Object resposta;

		Message esbMessage = MessageFactory.getInstance().getMessage();

		if (parametros != null) {
			for (Entry<String, Object> parametro : parametros.entrySet()) {
				esbMessage.getBody().add(parametro.getKey(),
						parametro.getValue());
			}
		}
		try {
			if(tempoEspera != null) {
				TEMPO_PADRAO = tempoEspera;
			}
			Message mensagemRetorno = obterServiceInvoker(servico).deliverSync(
					esbMessage, TEMPO_PADRAO);

			if (mensagemRetorno.getBody().get("aghu.integracao.erro") != null) {
				throw new ExecucaoServicoNegocioException(
						(String) mensagemRetorno.getBody().get(
								"aghu.integracao.erro"));
			} else if (mensagemRetorno.getBody()
					.get("aghu.integracao.resposta") != null) {
				resposta = mensagemRetorno.getBody().get(
						"aghu.integracao.resposta");
			} else {
				throw new ServicoSemRespostaException();
			}
			return resposta;
		} catch (FaultMessageException e) {
			LOG.error(e.getMessage());
			if (e.getCause() == null) {
				throw new ExecucaoServicoException(e);
			} else {
				throw new ExecucaoServicoException(e.getCause());
			}
		} catch (MissingServiceException e) {
			LOG.error(e.getMessage());
			throw new ServicoIndisponivelException(e);
		} catch (MessageDeliverException e) {
			LOG.error(e.getMessage());
			if (e.getCause() == null) {
				throw new ESBInacessivelException(e);
			} else {
				throw new ESBInacessivelException(e.getCause());
			}
		} catch (RegistryException e) {
			LOG.error(e.getMessage());
			throw new ServicoIndisponivelException(e);
		}

	}

	/**
	 * Invoca um serviço de forma assíncrona. Já que não é esperada uma
	 * resposta, a transação é propagada normalmente
	 */
	@Override
	public void invocarServicoAssincrono(ServiceEnum servico,
			Map<String, Object> parametros) throws AGHUIntegracaoException {

		Message esbMessage = MessageFactory.getInstance().getMessage();

		if (parametros != null) {
			for (Entry<String, Object> parametro : parametros.entrySet()) {
				esbMessage.getBody().add(parametro.getKey(),
						parametro.getValue());
			}
		}

		try {
			obterServiceInvoker(servico).deliverAsync(esbMessage);
		} catch (MissingServiceException e) {
			LOG.error(e.getMessage());
			throw new ServicoIndisponivelException(e);
		} catch (MessageDeliverException e) {
			LOG.error(e.getMessage());
			if (e.getCause() == null) {
				throw new ESBInacessivelException(e);
			} else {
				throw new ESBInacessivelException(e.getCause());
			}
		}

	}

	/**
	 * abstrai a criação de um service invoker para determinado serviço,
	 * utilizando cache a nível de request para evitar consultas desnecessárias
	 * ao registro de serviços.
	 * 
	 * @param servico
	 * @return
	 * @throws MessageDeliverException
	 */
	private ServiceInvoker obterServiceInvoker(ServiceEnum servico)
			throws MessageDeliverException {
		//ServiceInvoker retorno = null;
		//String nomeInvoker = "service_invoker_" + servico.getNome();

	//	retorno = (ServiceInvoker) Contexts.getEventContext().get(nomeInvoker);
	//	if (retorno == null) {
			// Categoria padrão: AGHU
	//		retorno = new ServiceInvoker("AGHU", servico.getNome());
		//	Contexts.getEventContext().set(nomeInvoker, retorno);
	//	}
		return new ServiceInvoker("AGHU", servico.getNome());

	}

}
