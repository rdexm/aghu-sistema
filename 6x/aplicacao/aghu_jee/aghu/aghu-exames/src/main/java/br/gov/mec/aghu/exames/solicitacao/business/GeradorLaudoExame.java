package br.gov.mec.aghu.exames.solicitacao.business;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacaoExameInternet;
import br.gov.mec.aghu.dominio.DominioSituacaoItemSolicitacaoExame;
import br.gov.mec.aghu.dominio.DominioStatusExameInternet;
import br.gov.mec.aghu.exames.solicitacao.vo.MensagemSolicitacaoExameGrupoVO;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.BaseException;

@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "queue/examesLiberadosQueue") })
public class GeradorLaudoExame extends BaseBusiness implements MessageListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3487412895819393L;

	private static final String ITEM_SOLICITACAO_NAO_ENCONTRADO = "Item da solicitação de exame não localizado";
	private static final Log LOG = LogFactory.getLog(GeradorLaudoExame.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@Inject
	private ISolicitacaoExameFacade solicitacaoExameFacade;

	@Override
	public void onMessage(Message message) {

		LOG.info("Processando mensagem de exames liberados");
		List<AelItemSolicitacaoExames> itensSolicitacao = new ArrayList<AelItemSolicitacaoExames>();

		MensagemSolicitacaoExameGrupoVO mensagem = null;
		
		try {
			mensagem = (MensagemSolicitacaoExameGrupoVO) ((ObjectMessage) message)
					.getObject();
			LOG.info("Mensagem capturada com sucesso da fila examesLiberadosQueue");
						
			if (mensagem.getItemSolicitacaoExames() != null && DominioSituacaoItemSolicitacaoExame.LI.toString().equals(mensagem.getItemSolicitacaoExames().getSituacaoItemSolicitacao().getCodigo())) {
				itensSolicitacao.add(solicitacaoExameFacade.obterItemSolicitacaoExamePorId(mensagem.getItemSolicitacaoExames().getId().getSoeSeq(), mensagem.getItemSolicitacaoExames().getId().getSeqp()));
			}
			else {
				itensSolicitacao = this.solicitacaoExameFacade
							.buscarItemExamesLiberadosPorGrupo(
									mensagem.getSeqSolicitacaoExame(),
									mensagem.getSeqExameInternetGrupo());
			}
			
			if(itensSolicitacao != null && !itensSolicitacao.isEmpty()){
				String token = this.solicitacaoExameFacade.gerarToken();
				String xmlEnvio = this.solicitacaoExameFacade.gerarXmlEnvioExameInternet(itensSolicitacao, mensagem.getSeqExameInternetGrupo());
				ByteArrayOutputStream arquivoLaudo = this.solicitacaoExameFacade.buildResultadoPDF(itensSolicitacao.get(0).getSolicitacaoExame(),
						mensagem.getSeqExameInternetGrupo(), token);
				
				this.solicitacaoExameFacade.atualizarStatusInternet(mensagem.getSeqSolicitacaoExame(), mensagem.getSeqExameInternetGrupo(), DominioStatusExameInternet.FG, DominioSituacaoExameInternet.R, null, null);
				
				this.solicitacaoExameFacade.inserirFilaLaudoExames(itensSolicitacao.get(0).getSolicitacaoExame(), itensSolicitacao, mensagem.getSeqExameInternetGrupo(), 
							arquivoLaudo.toByteArray(), xmlEnvio);
			} else{
				throw new JMSException(ITEM_SOLICITACAO_NAO_ENCONTRADO);
				/*
				this.solicitacaoExameFacade.atualizarStatusInternet(
						mensagem.getSeqSolicitacaoExame(),
						mensagem.getSeqExameInternetGrupo(),
						DominioStatusExameInternet.FG,
						DominioSituacaoExameInternet.E, null,
						ITEM_SOLICITACAO_NAO_ENCONTRADO);
				*/
			}
			
			this.flush();
		} catch (JMSException e) {
			LOG.error("Erro ao obter mensagem: ", e);
			throw new IllegalArgumentException(e);
		} catch (IOException e) {
			LOG.error("Erro ao obter mensagem: ", e);
			this.solicitacaoExameFacade.atualizarStatusInternet(mensagem.getSeqSolicitacaoExame(), mensagem.getSeqExameInternetGrupo(), DominioStatusExameInternet.FG, DominioSituacaoExameInternet.E, null, e.getMessage() + " causa " + e.getCause());
		} catch (BaseException e) {			
			LOG.error("Erro ao gerar arquivos para fila: ", e);
			this.solicitacaoExameFacade.atualizarStatusInternet(mensagem.getSeqSolicitacaoExame(), mensagem.getSeqExameInternetGrupo(), DominioStatusExameInternet.FG, DominioSituacaoExameInternet.E, null, e.getMessage() + " causa " + e.getCause());
		}

	}	
		
}
