package br.gov.mec.aghu.exames.questionario.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioTipoDadoQuestionario;
import br.gov.mec.aghu.exames.dao.AelQuestoesQuestionarioDAO;
import br.gov.mec.aghu.exames.dao.AelRespostaQuestaoDAO;
import br.gov.mec.aghu.exames.solicitacao.vo.ItemSolicitacaoExameVO;
import br.gov.mec.aghu.model.AelQuestoesQuestionario;
import br.gov.mec.aghu.model.AelQuestoesQuestionarioId;
import br.gov.mec.aghu.model.AelRespostaQuestao;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.ApplicationBusinessExceptionCode;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class QuestaoQuestionarioON extends BaseBusiness {
	
	
	@EJB
	private QuestaoQuestionarioRN questaoQuestionarioRN;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	private static final Log LOG = LogFactory.getLog(QuestaoQuestionarioON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@Inject
	private AelQuestoesQuestionarioDAO aelQuestoesQuestionarioDAO;
	
	@Inject
	private AelRespostaQuestaoDAO aelRespostaQuestaoDAO;

	private boolean habilitouSuggestionCidsQuestionario = false;

	private static final long	serialVersionUID	= -3371603363951389896L;
	
	private static final String PARAMETRO_VERIFICA_MOSTRA_SUGGESTION_AGH = "AELC_VER_PHI_CID10";

	public enum QuestaoQuestionarioONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_ERRO_REMOVER_DEPENDENCIAS;
	}

	public void excluir(final AelQuestoesQuestionarioId id) throws ApplicationBusinessException {
		AelQuestoesQuestionario elemento = aelQuestoesQuestionarioDAO.obterPorChavePrimaria(id);
		
		if (elemento == null) {
			throw new BaseRuntimeException(ApplicationBusinessExceptionCode.REGISTRO_NULO_EXCLUSAO);
		}
		
		this.verificarResposta(id);
		this.getQuestaoQuestionarioRN().excluir(id);
	}

	private void verificarResposta(AelQuestoesQuestionarioId id) throws ApplicationBusinessException {
		if(this.getAelRespostaQuestaoDAO().contarRespostaQuestaoPorQuestaoQuestionario(id) > 0) {
			throw new ApplicationBusinessException(QuestaoQuestionarioONExceptionCode.MENSAGEM_ERRO_REMOVER_DEPENDENCIAS, "AEL_RESPOSTAS_QUESTOES");
		}
	}
	
	public boolean verificarSuggestionCidSeraExibidaNoQuestionarioExame(
			ItemSolicitacaoExameVO itemSolicitacaoExameVo)
			throws ApplicationBusinessException {

		for (AelRespostaQuestao respostasQuestoes : itemSolicitacaoExameVo
				.getRespostasQuestoes()) {
			
         if(respostasQuestoes.getQuestaoQuestionario().getValidacao() != null) {
			
        	 if (respostasQuestoes.getQuestaoQuestionario().getValidacao()
					.equals(PARAMETRO_VERIFICA_MOSTRA_SUGGESTION_AGH)) {
				
				AghParametros buscaParametro = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_PARAMETRO_HU);
				
				if (buscaParametro.getVlrTexto() != null){
						
					if(buscaParametro.getVlrTexto().equals("HCPA")) {
						
						respostasQuestoes.getQuestaoQuestionario().getQuestao().setTipoDados(DominioTipoDadoQuestionario.C);
					}				
				}
				habilitouSuggestionCidsQuestionario = true;
			}
          }	
		}
		return habilitouSuggestionCidsQuestionario;
	}
	
	public void persistir(final AelQuestoesQuestionario aelQuestoesQuestionario) throws BaseException {
		this.getQuestaoQuestionarioRN().persistir(aelQuestoesQuestionario);
	}

	protected AelRespostaQuestaoDAO getAelRespostaQuestaoDAO() {
		return aelRespostaQuestaoDAO;
	}
	
	protected QuestaoQuestionarioRN getQuestaoQuestionarioRN() {
		return questaoQuestionarioRN;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

}
