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
 * MDB usado para consumir mensagens da fila de pendâncias de assinatura no
 * terceiro nível. Esta fila está configurada para re-tentar processar suas
 * mensagens a cada 20 min, no máximo 3 vezes. Toda vez que o processamento de
 * uma mensagem falha, um email é enviado a administração do sistema notificando
 * o problema.
 * 
 * @author Geraldo Maciel
 * 
 */
//@MessageDriven(activationConfig = {
//		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
//		@ActivationConfigProperty(propertyName = "destination", propertyValue = "queue/pendenciaAssinaturaNivel3Queue") })
public class GeradorPendenciasAssinaturaTerceiroNivel implements
		MessageListener {

	private static final Log LOG = LogFactory.getLog(GeradorPendenciasAssinaturaTerceiroNivel.class);

//	@In(create = true)
//	protected EmailUtil emailUtil;

	@Inject
	private ICertificacaoDigitalFacade certificacaoDigitalFacade;

//	@In(create = true, value = "EMAIL_ERRO_GERACAO_PENDENCIA_ASSINATURA")
//	private String emailErroGeracaoPendenciaAssinatura;
//
//	@In(create = true, value = "EMAIL_ENVIO.VLR_TEXTO")
//	private String emailEnvioErroGeracaoPendenciaAssinatura;
	
//	@In(create = true, value = "hostRemotoCache")
//	private HostRemotoCache hostRemotoCache;

	@Override
	public void onMessage(Message message) {

		LOG.info("Processando mensagem de criação de pendencia de assinatura em nivel 3");

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

		} catch (RuntimeException e) {
			this.enviarEmailErroCriarMensagem(e.getMessage());
			throw e;
		} catch (ApplicationBusinessException e) {
			LOG.error("Erro ao obter mensagem: " + e.getMessage());
			throw new IllegalArgumentException(e);
		}

	}

	private void enviarEmailErroCriarMensagem(String mensagemErro) {

		// Implementar lógica de envio de mail quando a nova api de email estiver pronta.
		
//		ContatoEmail remetente = new ContatoEmail("sistema AGHU",
//				emailEnvioErroGeracaoPendenciaAssinatura);
//		ContatoEmail destinatario = new ContatoEmail("Grupo de Arquitetura",
//				emailErroGeracaoPendenciaAssinatura);
//		
//		String nomeComputador = null;
//		try {
//			nomeComputador =  hostRemotoCache.getEnderecoRedeHostRemoto();
//		} catch (UnknownHostException e) {
//			LOG.error(e.getMessage(), e);
//		}
//
//		emailUtil.enviaEmail(remetente, destinatario, null,
//				"Erro ao processar Pendência de assinatura",
//				"Aconteceu um problema ao processar pendência de assinatura no computador "+ nomeComputador +": "
//						+ mensagemErro);

	}	
}
