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

import br.gov.mec.aghu.exames.solicitacao.vo.MensagemSolicitacaoExameGrupoVO;
import br.gov.mec.aghu.core.business.BaseBusiness;

/**
 * Classe responsável por enviar os exames para a fila
 * 
 * @author dcastro
 * 
 */
@Stateless
public class ProcessarFilaExamesLiberados extends BaseBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2989429596752821578L;

	private static final Log LOG = LogFactory
			.getLog(ProcessarFilaExamesLiberados.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@Resource(mappedName = "java:/ConnectionFactory")
	private ConnectionFactory connectionFactory;

	@Resource(mappedName = "java:/queue/examesLiberadosQueue")
	private Destination destination;

	public void enviar(MensagemSolicitacaoExameGrupoVO mensagemVO) {
		Connection connection = null;
		try {
			connection = connectionFactory.createConnection();
			Session session = connection.createSession(false,
					Session.AUTO_ACKNOWLEDGE);
			MessageProducer messageProducer = session
					.createProducer(destination);
			Message mensagem = session.createObjectMessage(mensagemVO);
			messageProducer.send(mensagem);
			LOG.info("Mensagem JMS inserção na fila ExamesLiberados realizada com sucesso.");

		} catch (JMSException e) {
			LOG.error("Erro ao inserir mensagem na fila ExamesLiberados: "
					+ e.getMessage());
			throw new IllegalArgumentException(e);
		}

	}

}