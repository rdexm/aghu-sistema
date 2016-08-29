package br.gov.mec.aghu.farmacia.cadastroapoio.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.farmacia.dao.AfaDispensacaoMdtosDAO;
import br.gov.mec.aghu.farmacia.dao.AfaTipoOcorDispensacaoDAO;
import br.gov.mec.aghu.model.AfaTipoOcorDispensacao;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class OcorrenciaCRUD extends BaseBusiness {


@EJB
private OcorrenciaRN ocorrenciaRN;

private static final Log LOG = LogFactory.getLog(OcorrenciaCRUD.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AfaDispensacaoMdtosDAO afaDispensacaoMdtosDAO;

@Inject
private AfaTipoOcorDispensacaoDAO afaTipoOcorDispensacaoDAO;
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 2969601492841136788L;

	private enum OcorrenciaCRUDExceptionCode implements BusinessExceptionCode {
		AFA_DISPENCACAO_MDTOS_DEPENDENTES
	}
	
	/**
	 * Método que persiste uma ocorrência
	 * @param ocorrencia
	 * @throws ApplicationBusinessException 
	 */
	public void persistirOcorrencia(AfaTipoOcorDispensacao ocorrencia) throws ApplicationBusinessException{
		if (ocorrencia.getSeq() == null){
			ocorrenciaRN.prePersistirOcorrencia(ocorrencia);
			//Faz a inserção
			getAfaTipoOcorDispensacaoDAO().persistir(ocorrencia);
		}
		else{
			getOcorrenciaRN().preUpdateOcorrencia(ocorrencia);
			//Faz a atualização
			getAfaTipoOcorDispensacaoDAO().atualizar(ocorrencia);
			getAfaTipoOcorDispensacaoDAO().flush();
		}
	}
	
	/**
	 * Método que remove uma ocorrência
	 * @param ocorrencia
	 *  
	 * @throws ApplicationBusinessException 
	 */
	public void removerOcorrencia(AfaTipoOcorDispensacao ocorrencia) throws ApplicationBusinessException{
		ocorrencia = getAfaTipoOcorDispensacaoDAO().reatacharOcorrencia(ocorrencia);
		this.verificarDependencias(ocorrencia);
		getOcorrenciaRN().preDeleteOcorrencia(ocorrencia);
		getAfaTipoOcorDispensacaoDAO().remover(ocorrencia);
	}

	/**
	 * @ORADB Form AFAF_CAD_TP_OCORREN CGRI$CHK_AFA_TIPO_OCOR_DISPENS
	 * @param ocorrencia
	 * @throws ApplicationBusinessException 
	 */
	private void verificarDependencias(AfaTipoOcorDispensacao ocorrencia) throws ApplicationBusinessException {
		if (getAfaDispensacaoMdtosDAO().verificarExisteDispensacaoPorOcorrencia(ocorrencia)){
			throw new ApplicationBusinessException(OcorrenciaCRUDExceptionCode.AFA_DISPENCACAO_MDTOS_DEPENDENTES);
		}	
	}

	protected OcorrenciaRN getOcorrenciaRN() {
		return ocorrenciaRN;
	}
	
	protected AfaTipoOcorDispensacaoDAO getAfaTipoOcorDispensacaoDAO() {
		return afaTipoOcorDispensacaoDAO;
	}
	
	protected AfaDispensacaoMdtosDAO getAfaDispensacaoMdtosDAO() {
		return afaDispensacaoMdtosDAO;
	}

}
