package br.gov.mec.aghu.exames.cadastrosapoio.business;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoCampoCampoLaudo;
import br.gov.mec.aghu.exames.dao.AelCampoLaudoDAO;
import br.gov.mec.aghu.exames.dao.AelCampoLaudoRelacionadoDAO;
import br.gov.mec.aghu.model.AelCampoLaudo;
import br.gov.mec.aghu.model.AelCampoLaudoRelacionado;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;


@Stateless
public class AelCampoLaudoRelacionadoRN extends BaseBusiness{

private static final Log LOG = LogFactory.getLog(AelCampoLaudoRelacionadoRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AelCampoLaudoRelacionadoDAO aelCampoLaudoRelacionadoDAO;

@Inject
private AelCampoLaudoDAO aelCampoLaudoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 124280383325527243L;

	public enum AelCampoLaudoRNExceptionCode implements BusinessExceptionCode {
		AEL_01314,//Campo laudo não encontrado
		AEL_00790,//Campo do tipo texto não deve ser relacionado
		AEL_01315;//Campo laudo está inativo
	}

	/**
	 * ORADB AELT_CLV_BRI (INSERT) – ANTES DE INSERIR O OBJETO
	 * @param campoLaudoRelacionado
	 * @throws BaseException
	 */
	private void preInserir(AelCampoLaudoRelacionado campoLaudoRelacionado) throws BaseException{
		this.validarCampoLaudo(campoLaudoRelacionado.getAelParametroCamposLaudoByAelClvPclFk1().getId().getCalSeq()); //rn1
		this.validarCampoLaudo(campoLaudoRelacionado.getAelParametroCamposLaudoByAelClvPclFk2().getId().getCalSeq()); //rn2
	}

	/**
	 * ORADB aelk_clv_rn.rn_clvp_ver_tp_campo 
	 * @param campoLaudoRelacionado
	 * @throws ApplicationBusinessException
	 */
	private void validarCampoLaudo(
			Integer seq)
	throws ApplicationBusinessException {
		AelCampoLaudo campoLaudo = this.getAelCampoLaudoDAO().obterCampoLaudoPorSeq(seq);

		if(campoLaudo == null){
			/*Campo laudo não encontrado*/
			throw new ApplicationBusinessException(AelCampoLaudoRNExceptionCode.AEL_01314);
		}else{


			if(!campoLaudo.getSituacao().equals(DominioSituacao.A)){
				/*Campo laudo está inativo*/
				throw new ApplicationBusinessException(AelCampoLaudoRNExceptionCode.AEL_01315);
			}


			if(campoLaudo.getTipoCampo().equals(DominioTipoCampoCampoLaudo.T)){
				/*Campo do tipo texto não deve ser relacionado*/
				throw new ApplicationBusinessException(AelCampoLaudoRNExceptionCode.AEL_00790);
			}


		}
	}

	/**
	 * Inserir AEL_CAMPOS_LAUDO_RELACIONADOS
	 * @param campoLaudo
	 * @throws BaseException
	 */
	public void inserir(AelCampoLaudoRelacionado campoLaudoRelacionado) throws BaseException{

		this.preInserir(campoLaudoRelacionado);		
		this.getAelCampoLaudoRelacionadoDAO().persistir(campoLaudoRelacionado);
	}
	
	/**
	 * Remover AEL_CAMPOS_LAUDO_RELACIONADOS
	 * @param campoLaudo
	 * @throws BaseException
	 */
	public void excluir(AelCampoLaudoRelacionado campoLaudoRelacionado) throws BaseException{
		this.getAelCampoLaudoRelacionadoDAO().remover(campoLaudoRelacionado);
	}

	/**
	 * Getters para RNs e DAOs
	 */

	protected AelCampoLaudoDAO getAelCampoLaudoDAO() {
		return aelCampoLaudoDAO;
	}

	protected AelCampoLaudoRelacionadoDAO getAelCampoLaudoRelacionadoDAO() {
		return aelCampoLaudoRelacionadoDAO;
	}

}
