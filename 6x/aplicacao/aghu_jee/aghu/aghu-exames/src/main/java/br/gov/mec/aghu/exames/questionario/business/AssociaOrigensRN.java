package br.gov.mec.aghu.exames.questionario.business;

import java.util.Calendar;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioOrigemQuestionario;
import br.gov.mec.aghu.exames.dao.AelExQuestionarioOrigensDAO;
import br.gov.mec.aghu.model.AelExQuestionarioOrigens;
import br.gov.mec.aghu.model.AelExQuestionarioOrigensId;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * Utilizada pela estoria #2234
 * @author aghu
 *
 */
@Stateless
public class AssociaOrigensRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(AssociaOrigensRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelExQuestionarioOrigensDAO aelExQuestionarioOrigensDAO;

	private static final long serialVersionUID = 6126755883048042765L;
	
	/**
	 * ORADB: RN_EQOP_VER_ORIGEM
	 * @param eqeEmaExaSigla
	 * @param eqeEmaManSeq
	 * @param eqeQtnSeq
	 * @param origemQuestionario
	 * @return	
	 */
	public boolean verificarOrigemQuestionario(String eqeEmaExaSigla, 
											   Integer eqeEmaManSeq, 
											   Integer eqeQtnSeq, 
											   DominioOrigemQuestionario origemQuestionario) {
		
		if(origemQuestionario != null) {
			
			if(!DominioOrigemAtendimento.T.equals(origemQuestionario)) {
				
				AelExQuestionarioOrigensId id = new AelExQuestionarioOrigensId();
				id.setEqeEmaExaSigla(eqeEmaExaSigla);
				id.setEqeEmaManSeq(eqeEmaManSeq);
				id.setEqeQtnSeq(eqeQtnSeq);
				id.setOrigemQuestionario(DominioOrigemQuestionario.T);
			
				AelExQuestionarioOrigens questionario = new AelExQuestionarioOrigens();
				questionario.setId(id);			
				AelExQuestionarioOrigens questionarioOrigem = getAelExQuestionarioOrigensDAO().obterPorChavePrimaria(id);
			
				if(questionarioOrigem != null) {
					return Boolean.TRUE;
				}
			}
		}
		return Boolean.FALSE;
	}
	
	
	/**
	 * @ORADB: AELT_EQO_BRI
	 * @ORADB: AELT_EQO_BRU
	 * @param questionarioOrigem
	 * @throws ApplicationBusinessException  
	 */
	public void inserirDadosUsuarioLogadoDataAlteracao(AelExQuestionarioOrigens questionarioOrigem) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if(questionarioOrigem != null) {
			questionarioOrigem.setServidor(servidorLogado);
			questionarioOrigem.setAlteradoEm(Calendar.getInstance().getTime());
		}
	}
	
	
	/**
	 * Retorna AelExQuestionarioOrigensDAO
	 * @return
	 */
	private AelExQuestionarioOrigensDAO getAelExQuestionarioOrigensDAO() {
		return aelExQuestionarioOrigensDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}