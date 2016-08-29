package br.gov.mec.aghu.certificacaodigital.business;

import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.vo.MensagemPendenciaAssinaturaVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * MDB usado para consumir mensagens da fila de pendência de assinatura no
 * segundo nível. Esta fila está configurada para relizar 2 tentativas com
 * intervalo de 10 minutos entre elas. Depois disso, a mensagem é encaminhada
 * para afila de nível 3.
 * 
 * @author Geraldo Maciel
 * 
 */
//@MessageDriven(activationConfig = {
//		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
//		@ActivationConfigProperty(propertyName = "destination", propertyValue = "queue/pendenciaAssinaturaNivel2Queue") })
public class GeradorPendenciasAssinaturaSegundoNivel implements MessageListener {

	private static final Log LOG = LogFactory.getLog(GeradorPendenciasAssinaturaSegundoNivel.class);

	@Inject
	private ICertificacaoDigitalFacade certificacaoDigitalFacade;

	@Override
	public void onMessage(Message message) {

		LOG.info("Processando mensagem de criação de pendencia de assinatura em nivel 2");

		try {
			MensagemPendenciaAssinaturaVO mensagem = (MensagemPendenciaAssinaturaVO) ((ObjectMessage) message)
					.getObject();
			certificacaoDigitalFacade.gerarPendenciaAssinatura(
					mensagem.getArquivoGerado(), mensagem.getEntidadePai(),
					mensagem.getDocumentoCertificado(),
					mensagem.getServidorLogado());

		} catch (JMSException e) {
			LOG.error("Erro ao obter mensagem: " + e.getMessage());
			throw new IllegalArgumentException(e);

		} catch (ApplicationBusinessException e) {
			LOG.error("Erro ao obter mensagem: " + e.getMessage());
			throw new IllegalArgumentException(e);
		}

	}

}
