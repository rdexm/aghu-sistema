package br.gov.mec.aghu.exames.solicitacao.business;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.solicitacao.vo.MensagemLaudoExameVO;
import br.gov.mec.aghu.core.business.BaseBusiness;

/**
 * Classe responsável por enviar os exames para a fila
 * 
 * @author dcastro
 * 
 */
@Stateless
public class ProcessarFilaLaudoExames extends BaseBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6232161861854775702L;

	private static final Log LOG = LogFactory.getLog(ProcessarFilaLaudoExames.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@Resource(mappedName = "java:/ConnectionFactory")
	private ConnectionFactory connectionFactory;

	@Resource(mappedName = "java:/queue/laudoExamesQueue")
	private Destination destination;
	
	public void enviar(Integer soeSeq, Integer grupoSeq, String localizador, byte[] arquivoLaudo, String xmlEnvio){

		MensagemLaudoExameVO mensagemVO = new MensagemLaudoExameVO();
		mensagemVO.setSeqSolicitacaoExame(soeSeq);
		mensagemVO.setSeqExameInternetGrupo(grupoSeq);
		mensagemVO.setArquivoLaudo(arquivoLaudo);
		mensagemVO.setXmlEnvio(xmlEnvio);
		mensagemVO.setLocalizador(localizador);
				
		Connection connection = null;
		try {
			connection = connectionFactory.createConnection();
			Session session = connection.createSession(false,
					Session.AUTO_ACKNOWLEDGE);
			MessageProducer messageProducer = session
					.createProducer(destination);
			Message mensagem = session.createObjectMessage(mensagemVO);
			messageProducer.send(mensagem);	
			LOG.info("Mensagem JMS inserção na fila LaudoExames realizada com sucesso.");
		} catch (JMSException e) {
			LOG.error("Erro ao inserir mensagem na fila LaudoExames: " + e.getMessage());
			throw new IllegalArgumentException(e);			
		}

	}	
	
}