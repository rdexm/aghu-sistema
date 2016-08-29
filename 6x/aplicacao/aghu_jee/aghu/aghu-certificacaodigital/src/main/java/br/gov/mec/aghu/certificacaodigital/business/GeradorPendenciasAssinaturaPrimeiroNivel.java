package br.gov.mec.aghu.certificacaodigital.business;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
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
 * primeiro nível. Esta fila está configurada para relizar 2 tentativas com
 * intervalo de 5 minutos entre elas. Depois disso, a mensagem é encaminhada
 * para afila de nível 2.
 * 
 * @author Geraldo Maciel
 * 
 */
@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "java:/queue/pendenciaAssinaturaNivel1Queue") })
public class GeradorPendenciasAssinaturaPrimeiroNivel implements
		MessageListener {

	private static final Log LOG = LogFactory.getLog(GeradorPendenciasAssinaturaPrimeiroNivel.class);

	@EJB
	private ICertificacaoDigitalFacade certificacaoDigitalFacade;

	@Override
	public void onMessage(Message message) {

		LOG.info("Processando mensagem de criação de pendencia de assinatura em nivel 1");

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

		}catch (ApplicationBusinessException e) {
			LOG.error("Erro ao obter mensagem: " + e.getMessage());
			throw new IllegalArgumentException(e);
		}

	}
}
