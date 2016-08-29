package br.gov.mec.aghu.exames.questionario.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.exames.dao.AelQuestionariosDAO;
import br.gov.mec.aghu.model.AelQuestionarios;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class QuestionarioRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(QuestionarioRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelQuestionariosDAO aelQuestionariosDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	private static final long serialVersionUID = 4324454727151306419L;

	public enum QuestionarioRNExceptionCode implements BusinessExceptionCode {
		AEL_00343, AEL_00344, AEL_00353, AEL_00346;

	}

	/**
	 * Trigger
	 * 
	 * ORADB: AELT_QTN_BRD
	 */
	public void executarBeforeDeleteQuestionario(Date criadoEm) throws ApplicationBusinessException {
		
		AghParametros paramDiasPermDelAel;
		try {
			paramDiasPermDelAel = getParametroFacade().obterAghParametro(AghuParametrosEnum.P_DIAS_PERM_DEL_AEL);
		} catch (ApplicationBusinessException e) {
			throw new ApplicationBusinessException( QuestionarioRNExceptionCode.AEL_00344);
		}
		
		Integer numDias = DateUtil.calcularDiasEntreDatas(criadoEm, DateUtil.truncaData(new Date()));
		
		if (numDias > paramDiasPermDelAel.getVlrNumerico().intValue()) {
			throw new ApplicationBusinessException(QuestionarioRNExceptionCode.AEL_00343);
		}
	}
	
	/**
	 * Trigger
	 * 
	 * ORADB: AELT_QTN_BRI
	 */
	public void executarBeforeInsertQuestionario(AelQuestionarios questionarioNew) throws ApplicationBusinessException {

		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if (servidorLogado == null) {
			throw new ApplicationBusinessException( QuestionarioRNExceptionCode.AEL_00353);
		}

		questionarioNew.setServidor(servidorLogado);
		questionarioNew.setServidorAlterado(servidorLogado);
		
		questionarioNew.setCriadoEm(new Date());
		questionarioNew.setAlteradoEm(new Date());
		getAelQuestionariosDAO().persistir(questionarioNew);
	}
	
	/**
	 * Trigger
	 * 
	 * ORADB: AELT_QTN_BRU
	 */
	public void executarBeforeUpdateQuestionario(AelQuestionarios questionarioNew) throws ApplicationBusinessException {
		AelQuestionarios questionarioOld = getAelQuestionariosDAO().obterOriginal(questionarioNew);
		
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if (servidorLogado == null) {
			throw new ApplicationBusinessException( QuestionarioRNExceptionCode.AEL_00353);
		}
		
		questionarioNew.setServidorAlterado(servidorLogado);
		
		if(!questionarioOld.getDescricao().equals(questionarioNew.getDescricao())) {
			throw new ApplicationBusinessException( QuestionarioRNExceptionCode.AEL_00346);
		}
		
		questionarioNew.setAlteradoEm(new Date());
		getAelQuestionariosDAO().merge(questionarioNew);
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	protected AelQuestionariosDAO getAelQuestionariosDAO() {
		return aelQuestionariosDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}	
}
