package br.gov.mec.aghu.exames.cadastrosapoio.business;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoCampoCampoLaudo;
import br.gov.mec.aghu.exames.dao.AelCampoLaudoDAO;
import br.gov.mec.aghu.exames.dao.AelDescricoesResulPadraoDAO;
import br.gov.mec.aghu.model.AelCampoLaudo;
import br.gov.mec.aghu.model.AelDescricoesResulPadrao;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class AelDescricoesResulPadraoRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(AelDescricoesResulPadraoRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AelDescricoesResulPadraoDAO aelDescricoesResulPadraoDAO;

@Inject
private AelCampoLaudoDAO aelCampoLaudoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 4545133467003606342L;

	
	public enum AelDescricoesResulPadraoRNExceptionCode implements BusinessExceptionCode {

		AEL_00780, AEL_00782, AEL_00934;//

	}
	
	
	public void atualizar(AelDescricoesResulPadrao descResultaPadrao) throws BaseException {
		this.getAelDescricoesResulPadraoDAO().atualizar(descResultaPadrao);
		this.getAelDescricoesResulPadraoDAO().flush();
	}
	
	
	/**
	 * Insere AelDescricoesResulPadrao
	 * 
	 * @param descResultaPadrao
	 * @throws BaseException
	 */
	protected void inserir(AelDescricoesResulPadrao descResultaPadrao) throws BaseException {
		this.preInserir(descResultaPadrao);
		this.getAelDescricoesResulPadraoDAO().persistir(descResultaPadrao);
	}
	
	
	/**
	 * Persiste AelDescricoesResulPadrao
	 * 
	 * @param descResultaPadrao
	 * @throws BaseException
	 */
	public void persistir(AelDescricoesResulPadrao descResultaPadrao) throws BaseException {
		this.inserir(descResultaPadrao);
		//Caso nenhum erro ocorra faz o flush das alterações
		this.getAelDescricoesResulPadraoDAO().flush();
	}
	
	
	/**
	 * ORADB TRIGGER AELT_DRP_BRI (INSERT)
	 * 
	 * @param descResultaPadrao
	 * @throws BaseException
	 */
	protected void preInserir(AelDescricoesResulPadrao descResultaPadrao) throws BaseException {
		this.verificarRNDrppVerTpCampo(descResultaPadrao);
	}
	
	
	/**
	 * ORADB PROCEDURE AELK_DRP_RN.RN_DRPP_VER_TP_CAMPO
	 * 
	 * @param descResultaPadrao
	 * @throws ApplicationBusinessException
	 */
	protected void verificarRNDrppVerTpCampo(AelDescricoesResulPadrao descResultaPadrao) throws ApplicationBusinessException {
		
		if(descResultaPadrao != null && descResultaPadrao.getResultadoPadraoCampo() != null){
			AelCampoLaudo campoLaudo = this.getAelCampoLaudoDAO().
			obterResultadoPadraoTipoCampoESituacao(
					descResultaPadrao.getResultadoPadraoCampo().getId().getRpaSeq(),descResultaPadrao.getResultadoPadraoCampo().getId().getSeqp());
			
			if(campoLaudo == null) {
				throw new ApplicationBusinessException(AelDescricoesResulPadraoRNExceptionCode.AEL_00780);
			} else if(!DominioSituacao.A.equals(campoLaudo.getSituacao())) {
				throw new ApplicationBusinessException(AelDescricoesResulPadraoRNExceptionCode.AEL_00782);
			} else if(!DominioTipoCampoCampoLaudo.A.equals(campoLaudo.getTipoCampo())) {
				throw new ApplicationBusinessException(AelDescricoesResulPadraoRNExceptionCode.AEL_00934);
			}
		}

	}
	
	public void remover(AelDescricoesResulPadrao descResultaPadrao) throws BaseException {
		descResultaPadrao = getAelDescricoesResulPadraoDAO().obterPorChavePrimaria(descResultaPadrao.getId());
		this.getAelDescricoesResulPadraoDAO().remover(descResultaPadrao);
	}
	
	
	
	/** GET **/
	protected AelCampoLaudoDAO getAelCampoLaudoDAO() {
		return aelCampoLaudoDAO;
	}
	
	protected AelDescricoesResulPadraoDAO getAelDescricoesResulPadraoDAO() {
		return aelDescricoesResulPadraoDAO;
	}
}
