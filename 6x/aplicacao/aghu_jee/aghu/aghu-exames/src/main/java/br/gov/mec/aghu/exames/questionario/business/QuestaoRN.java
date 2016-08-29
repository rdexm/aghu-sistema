package br.gov.mec.aghu.exames.questionario.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.exames.agendamento.business.IAgendamentoExamesFacade;
import br.gov.mec.aghu.exames.dao.AelQuestaoDAO;
import br.gov.mec.aghu.exames.dao.AelValorValidoQuestaoDAO;
import br.gov.mec.aghu.model.AelQuestao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.ApplicationBusinessExceptionCode;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class QuestaoRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(QuestaoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IAgendamentoExamesFacade agendamentoExamesFacade;
	
	@Inject
	private AelQuestaoDAO aelQuestaoDAO;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject
	private AelValorValidoQuestaoDAO aelValorValidoQuestaoDAO;

	private static final long serialVersionUID = 4547421033969073780L;

	public enum QuestaoRNExceptionCode implements BusinessExceptionCode {
		AEL_00346, MENSAGEM_ERRO_REMOVER_DEPENDENCIAS;
	}

	public void persistir(final AelQuestao aelQuestao) throws ApplicationBusinessException {
		if (aelQuestao.getSeq() == null) {
			this.executarAntesInserir(aelQuestao);
			this.getAelQuestaoDAO().persistir(aelQuestao);
		} else {

			this.executarAntesAtualizar(aelQuestao);
			this.getAelQuestaoDAO().merge(aelQuestao);
		}
	}

	/**
	 * @ORADB AELT_QAO_BRU
	 */
	private void executarAntesAtualizar(final AelQuestao aelQuestao) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		final AelQuestao aelQuestaoOld = this.getAelQuestaoDAO().obterOriginal(aelQuestao.getSeq());
		if (CoreUtil.modificados(aelQuestao.getDescricao(), aelQuestaoOld.getDescricao())) {
			throw new ApplicationBusinessException(QuestaoRNExceptionCode.AEL_00346);
		}
		aelQuestao.setRapServidoresAlterado(servidorLogado);
		aelQuestao.setAlteradoEm(new Date());

	}

	/**
	 * @ORADB AELT_QAO_BRI
	 */
	private void executarAntesInserir(final AelQuestao aelQuestao) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		final Date date = new Date();
		aelQuestao.setRapServidores(servidorLogado);
		aelQuestao.setRapServidoresAlterado(servidorLogado);
		aelQuestao.setCriadoEm(date);
		aelQuestao.setAlteradoEm(date);
	}

	public void remover(final Integer questaoSeq) throws ApplicationBusinessException {
		final AelQuestao aelQuestao = this.getAelQuestaoDAO().obterPorChavePrimaria(questaoSeq);
		
		if (aelQuestao == null) {
			throw new BaseRuntimeException(ApplicationBusinessExceptionCode.REGISTRO_NULO_EXCLUSAO);
		}
		
		this.executarAntesExcluir(aelQuestao);
		this.getAelQuestaoDAO().remover(aelQuestao);
	}

	/**
	 * @ORADB AELT_QAO_BRD
	 */
	private void executarAntesExcluir(AelQuestao aelQuestao) throws ApplicationBusinessException {
		this.verificarReferencias(aelQuestao);
		final AelQuestao aelQuestaoOld = this.getAelQuestaoDAO().obterOriginal(aelQuestao.getSeq());
		getAgendamentoExamesFacade().verificarDelecaoTipoMarcacaoExame(aelQuestaoOld.getCriadoEm());
	}

	private void verificarReferencias(AelQuestao aelQuestao) throws ApplicationBusinessException {
		if (getAelValorValidoQuestaoDAO().contarValoresValidosQuestaoPorQuestao(aelQuestao.getSeq()) > 0) {
			throw new ApplicationBusinessException(QuestaoRNExceptionCode.MENSAGEM_ERRO_REMOVER_DEPENDENCIAS, "AEL_VALORES_VALIDOS_QUESTAO");
		}
		if (getAelQuestaoDAO().contarReferenciaQuestionario(aelQuestao) > 0) {
			throw new ApplicationBusinessException(QuestaoRNExceptionCode.MENSAGEM_ERRO_REMOVER_DEPENDENCIAS, "AEL_QUESTOES_QUESTIONARIO");
		}
	}

	protected AelQuestaoDAO getAelQuestaoDAO() {
		return aelQuestaoDAO;
	}

	protected AelValorValidoQuestaoDAO getAelValorValidoQuestaoDAO() {
		return aelValorValidoQuestaoDAO;
	}

	protected IAgendamentoExamesFacade getAgendamentoExamesFacade() {
		return this.agendamentoExamesFacade;
	}

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
