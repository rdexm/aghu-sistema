package br.gov.mec.aghu.exames.questionario.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelQuestionariosConvUnidDAO;
import br.gov.mec.aghu.model.AelQuestionariosConvUnid;
import br.gov.mec.aghu.model.AelQuestionariosConvUnidId;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Utilizada pela estoria #2235
 * @author aghu
 *
 */
@Stateless
public class AssociaRequisitanteON extends BaseBusiness {


@EJB
private AssociaRequisitanteRN associaRequisitanteRN;

private static final Log LOG = LogFactory.getLog(AssociaRequisitanteON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AelQuestionariosConvUnidDAO aelQuestionariosConvUnidDAO;

	
	private static final long serialVersionUID = 6216467883048042955L;

	
	public enum AssociarRequisitanteONExceptionCode implements BusinessExceptionCode {
		ERRO_PREENCHIMENTO_CONVENIO_UNIDADE_FUNCIONAL,
		AEL_00365, // convenio saude nao esta ativo
		AEL_00366; // questionario nao esta ativo
	}
	
	
	/**
	 * ON1 - Caso sejam informados convenio e unidade funcional, apresenta mensagem de erro
	 * @param questionarioConvenioSaude
	 * @throws ApplicationBusinessException
	 */
	private void validarPreenchimentoConvenioSaudeEUnidadeFuncional(AelQuestionariosConvUnid questionarioConvenioSaude) throws ApplicationBusinessException {
		if(questionarioConvenioSaude != null) {
			if(questionarioConvenioSaude.getConvenioSaude() != null && questionarioConvenioSaude.getUnidadeFuncional() != null) {
				throw new ApplicationBusinessException(AssociarRequisitanteONExceptionCode.ERRO_PREENCHIMENTO_CONVENIO_UNIDADE_FUNCIONAL);
			}
		}
	}
	
	/**
	 * ON2 - Retorna proximo sequencial(seqp) da tabela AEL_QUESTIONARIOS_CONV_UNID
	 * @return
	 */
	public Integer obterProximoSequencialAelQuestionariosConvUnid() {
		return getAelQuestionariosConvUnidDAO().obterProximoSequencial();
	}
	
	
	/**
	 * Grava novo registro ou atualiza registro já existente
	 * @param questionarioConvenioSaude
	 * @throws ApplicationBusinessException
	 */
	public void gravar(AelQuestionariosConvUnid questionarioConvenioSaude) throws ApplicationBusinessException {
		if(questionarioConvenioSaude != null) {
			
			validarPreenchimentoConvenioSaudeEUnidadeFuncional(questionarioConvenioSaude);
			
			if(questionarioConvenioSaude.getId() == null || questionarioConvenioSaude.getId().getSeqp() == null) {
				// insert
				getAssociarRequisitanteRN().validarAntesDeInserir(questionarioConvenioSaude);
				getAelQuestionariosConvUnidDAO().persistir(questionarioConvenioSaude);
			} else {
				// update
				getAssociarRequisitanteRN().validarAntesDeAtualizar(questionarioConvenioSaude);
				getAelQuestionariosConvUnidDAO().merge(questionarioConvenioSaude);
			}
		}
	}

	/**
	 * Retorna AssociarRequisitanteRN
	 * @return
	 */
	private AssociaRequisitanteRN getAssociarRequisitanteRN() {
		return associaRequisitanteRN;
	}
	
	/**
	 * Retorna AelQuestionariosConvUnidDAO
	 * @return
	 */
	private AelQuestionariosConvUnidDAO getAelQuestionariosConvUnidDAO() {
		return aelQuestionariosConvUnidDAO;
	}

	public AelQuestionariosConvUnid obterPorChavePrimaria(AelQuestionariosConvUnidId id){
		return getAelQuestionariosConvUnidDAO().obterPorChavePrimaria(id);
	}
}
