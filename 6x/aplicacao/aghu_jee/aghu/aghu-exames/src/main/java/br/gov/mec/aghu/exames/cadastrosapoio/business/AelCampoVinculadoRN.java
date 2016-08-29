package br.gov.mec.aghu.exames.cadastrosapoio.business;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoCampoCampoLaudo;
import br.gov.mec.aghu.exames.dao.AelCampoLaudoDAO;
import br.gov.mec.aghu.exames.dao.AelCampoVinculadoDAO;
import br.gov.mec.aghu.model.AelCampoLaudo;
import br.gov.mec.aghu.model.AelCampoVinculado;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;


@Stateless
public class AelCampoVinculadoRN extends BaseBusiness{

private static final Log LOG = LogFactory.getLog(AelCampoVinculadoRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AelCampoVinculadoDAO aelCampoVinculadoDAO;

@Inject
private AelCampoLaudoDAO aelCampoLaudoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 124280383325527243L;

	public enum AelCampoLaudoRNExceptionCode implements BusinessExceptionCode {
		AEL_01314,AEL_00790,AEL_01315;
	}

	/**
	 * ORADB AELT_CVC_BRI (INSERT) – ANTES DE INSERIR O OBJETO
	 * @param campoLaudoRelacionado
	 * @throws BaseException
	 */
	private void preInserir(AelCampoVinculado campoVinculado) throws BaseException{
		
		this.validarCampoLaudo(campoVinculado.getAelParametroCamposLaudoByAelCvcPclFk1().getId().getCalSeq());
		
	}

	/**
	 * ORADB aelk_cvc_rn.rn_cvcp_ver_tp_campo
	 * @param campoLaudoRelacionado
	 * @throws ApplicationBusinessException
	 */
	private void validarCampoLaudo(Integer seq)	throws ApplicationBusinessException {
		AelCampoLaudo campoLaudo = null;
		
		if(seq != null) {
			campoLaudo = this.getAelCampoLaudoDAO().obterCampoLaudoPorSeq(seq);
		}

		if(campoLaudo == null){
			/*Campo laudo não encontrado*/
			throw new ApplicationBusinessException(AelCampoLaudoRNExceptionCode.AEL_01314);
		}else{


			if(!campoLaudo.getSituacao().equals(DominioSituacao.A)){
				/*Campo laudo está inativo*/
				throw new ApplicationBusinessException(AelCampoLaudoRNExceptionCode.AEL_01315);
			}
			
			if(campoLaudo.getTipoCampo().equals(DominioTipoCampoCampoLaudo.T)) {
				//Campo do tipo texto não deve ser relacionado
				throw new ApplicationBusinessException(AelCampoLaudoRNExceptionCode.AEL_00790);
			}
		
		}
	}

	/**
	 * Inserir AEL_CAMPOS_LAUDO_RELACIONADOS
	 * @param campoLaudo
	 * @throws BaseException
	 */
	public void inserir(AelCampoVinculado campoVinculado) throws IllegalArgumentException, BaseException {

		this.preInserir(campoVinculado);		
		this.getAelCampoVinculadoDAO().persistir(campoVinculado);
	}

	public void excluir(AelCampoVinculado campoVinculado) throws BaseException{
		this.getAelCampoVinculadoDAO().remover(campoVinculado);	
	}

	/**
	 * Getters para RNs e DAOs
	 */

	protected AelCampoLaudoDAO getAelCampoLaudoDAO() {
		return aelCampoLaudoDAO;
	}

	protected AelCampoVinculadoDAO getAelCampoVinculadoDAO() {
		return aelCampoVinculadoDAO;
	}

}
