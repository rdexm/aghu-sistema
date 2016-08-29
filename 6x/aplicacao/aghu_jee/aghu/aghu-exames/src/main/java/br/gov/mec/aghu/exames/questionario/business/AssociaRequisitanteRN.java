package br.gov.mec.aghu.exames.questionario.business;

import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.questionario.business.AssociaRequisitanteON.AssociarRequisitanteONExceptionCode;
import br.gov.mec.aghu.model.AelQuestionarios;
import br.gov.mec.aghu.model.AelQuestionariosConvUnid;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * Utilizada pela estoria #2235
 * @author aghu
 *
 */
@Stateless
public class AssociaRequisitanteRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(AssociaRequisitanteRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


	private static final long serialVersionUID = 6126467883048042599L;
	
	/**
	 * @ORADB: RN_QCUP_VER_CONVENIO
	 * @param convenioSaude
	 * @return
	 */
	public boolean verificarSeConvenioSaudeEstaAtivo(FatConvenioSaude convenioSaude) {
		if(convenioSaude != null) {
			if(DominioSituacao.A.equals(convenioSaude.getSituacao())) {
				return Boolean.TRUE;
			}
		}
		return Boolean.FALSE;
	}
	
	/**
	 * @ORADB: RN_QCUP_VER_QUESTION
	 * @param questionario
	 * @return
	 */
	public boolean verificarSeQuestionarioEstaAtivo(AelQuestionarios questionario) {
		if(questionario != null) {
			if(DominioSituacao.A.equals(questionario.getSituacao())) {
				return Boolean.TRUE;
			}
		}
		return Boolean.FALSE;
	}
	
	/**
	 * @ORADB: AELT_QCU_BRI
	 * @param questionarioConvenioSaude
	 * @throws ApplicationBusinessException
	 */
	public void validarAntesDeInserir(AelQuestionariosConvUnid questionarioConvenioSaude) throws ApplicationBusinessException {
		if(questionarioConvenioSaude.getConvenioSaude() != null) {
			validarConvenioSaude(questionarioConvenioSaude.getConvenioSaude());
		}
		if(questionarioConvenioSaude.getAelQuestionarios() != null) {
			validarQuestionario(questionarioConvenioSaude.getAelQuestionarios());
		}
	}
	
	/**
	 * @ORADB: AELT_QCU_BRU
	 * @param questionarioConvenioSaude
	 * @throws ApplicationBusinessException
	 */
	public void validarAntesDeAtualizar(AelQuestionariosConvUnid questionarioConvenioSaude) throws ApplicationBusinessException {
		if(questionarioConvenioSaude.getConvenioSaude() != null) {
			validarConvenioSaude(questionarioConvenioSaude.getConvenioSaude());				
		} else if (questionarioConvenioSaude.getAelQuestionarios() != null) {				
			validarQuestionario(questionarioConvenioSaude.getAelQuestionarios());
		}
	}

	/**
	 * Verifica se o convênio saúde associado ao questionário convênio saúde (ael_questionarios_conv_unid.cnv_codigo) está ativo
	 * @param convenioSaude
	 * @throws ApplicationBusinessException 
	 */
	private void validarConvenioSaude(FatConvenioSaude convenioSaude) throws ApplicationBusinessException {
		if(verificarSeConvenioSaudeEstaAtivo(convenioSaude) == false) {
			throw new ApplicationBusinessException(AssociarRequisitanteONExceptionCode.AEL_00365);
		}
	}
	
	/**
	 * Verifica se o questionário associado ao questionário convênio saúde (ael_questionarios_conv_unid.qtn_seq) está ativo
	 * @param questionario
	 * @throws ApplicationBusinessException
	 */
	private void validarQuestionario(AelQuestionarios questionario) throws ApplicationBusinessException {
		if(verificarSeQuestionarioEstaAtivo(questionario) == false) {
			throw new ApplicationBusinessException(AssociarRequisitanteONExceptionCode.AEL_00366);
		}
	}
	
}
