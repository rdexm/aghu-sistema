package br.gov.mec.aghu.exames.questionario.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioTipoTransporteQuestionario;
import br.gov.mec.aghu.exames.dao.AelExQuestionarioOrigensDAO;
import br.gov.mec.aghu.model.AelExQuestionarioOrigens;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * 
 * @author aghu
 *
 */
@Stateless
public class AssociaOrigensON extends BaseBusiness {


@EJB
private AssociaOrigensRN associaOrigensRN;

private static final Log LOG = LogFactory.getLog(AssociaOrigensON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AelExQuestionarioOrigensDAO aelExQuestionarioOrigensDAO;
	
	private static final long serialVersionUID = 6126322883048042572L;
	
	public enum AssociaOrigensONExceptionCode implements BusinessExceptionCode {
		AEL_01374,
		AEL_01371;
	}
	
	
	/**
	 * Grava novo registro ou atualiza registro já existente
	 */
	public void gravar(AelExQuestionarioOrigens questionarioOrigem, boolean edicao) throws ApplicationBusinessException {		
		if(questionarioOrigem != null) {
			if(edicao) {
				getAssociaOrigensRN().inserirDadosUsuarioLogadoDataAlteracao(questionarioOrigem);
				getAelExQuestionarioOrigensDAO().merge(questionarioOrigem);
			} else {
				verificarSeJaExisteAssociacao(questionarioOrigem);
				validarOrigemQuestionario(questionarioOrigem);
				verificarSeExisteTipoTransporte(questionarioOrigem);
				getAssociaOrigensRN().inserirDadosUsuarioLogadoDataAlteracao(questionarioOrigem);
				getAelExQuestionarioOrigensDAO().persistir(questionarioOrigem);
			}
		}
	}


	private void verificarSeExisteTipoTransporte(AelExQuestionarioOrigens questionarioOrigem) {
		if(questionarioOrigem.getTipoTransporte() == null){
			questionarioOrigem.setTipoTransporte(DominioTipoTransporteQuestionario.T);
		}
	}


	/**
	 * Verifica se a origemAtendimento informada é igual a 'T'. 
	 * Caso contrário, busca o questionario origem informando os mesmos parâmetros e origemQuestionario = 'T'. Se encontrar, exibe msg de erro
	 * 
	 * @param questionarioOrigem
	 * @throws ApplicationBusinessException
	 */
	private void validarOrigemQuestionario(AelExQuestionarioOrigens questionarioOrigem) throws ApplicationBusinessException {
		if(questionarioOrigem != null && questionarioOrigem.getId() != null) {
			boolean isOrigemIgualTodasOrigens = getAssociaOrigensRN().verificarOrigemQuestionario(questionarioOrigem.getId().getEqeEmaExaSigla(), 
																					   			   questionarioOrigem.getId().getEqeEmaManSeq(), 
																					   			   questionarioOrigem.getId().getEqeQtnSeq(),
																					   			   questionarioOrigem.getId().getOrigemQuestionario());
			if(isOrigemIgualTodasOrigens) {
				throw new ApplicationBusinessException(AssociaOrigensONExceptionCode.AEL_01374);
			}
		}
	}
	
	/**
	 * Verifica se ja existe uma associacao entre o questionario e a origem informados
	 * @param questionarioOrigem
	 * @throws ApplicationBusinessException 
	 */
	private void verificarSeJaExisteAssociacao(AelExQuestionarioOrigens questionarioOrigem) throws ApplicationBusinessException {
		if(questionarioOrigem != null) {
			if(questionarioOrigem.getId() != null) {
				AelExQuestionarioOrigens obj = getAelExQuestionarioOrigensDAO().obterPorChavePrimaria(questionarioOrigem.getId());
				if(obj != null) {
					throw new ApplicationBusinessException(AssociaOrigensONExceptionCode.AEL_01371);
				}
			}
		}
	}
	
	/**
	 * Retorna AelExQuestionarioOrigensDAO
	 * @return
	 */
	private AelExQuestionarioOrigensDAO getAelExQuestionarioOrigensDAO() {
		return aelExQuestionarioOrigensDAO;
	}
	
	/**
	 * Retorna AssociaOrigensRN
	 * @return
	 */
	private AssociaOrigensRN getAssociaOrigensRN() {
		return associaOrigensRN;
	}
}