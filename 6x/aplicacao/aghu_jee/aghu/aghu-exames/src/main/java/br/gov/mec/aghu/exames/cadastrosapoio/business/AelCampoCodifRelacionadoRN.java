package br.gov.mec.aghu.exames.cadastrosapoio.business;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoCampoCampoLaudo;
import br.gov.mec.aghu.exames.dao.AelCampoCodifRelacionadoDAO;
import br.gov.mec.aghu.exames.dao.AelCampoLaudoDAO;
import br.gov.mec.aghu.model.AelCampoCodifRelacionado;
import br.gov.mec.aghu.model.AelCampoLaudo;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class AelCampoCodifRelacionadoRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(AelCampoCodifRelacionadoRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AelCampoLaudoDAO aelCampoLaudoDAO;

@Inject
private AelCampoCodifRelacionadoDAO aelCampoCodifRelacionadoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -2498212281558996452L;

	public enum AelCampoCodifRelacionadoRNExceptionCode implements
			BusinessExceptionCode {
		AEL_01314, //Campo laudo nao encontrado
		AEL_01315, //Campo laudo esta inativo
		AEL_01316, //Tipo do campo laudo deve ser codificado
		;
	}

	/**
	 * Insere um registro na <br>
	 * tabela AEL_CAMPOS_CODIF_RELACIONADOS
	 * 
	 * @param elemento
	 * @throws BaseException
	 */
	public void inserir(AelCampoCodifRelacionado elemento) throws BaseException {
		this.preInserir(elemento);
		this.getAelCampoCodifRelacionadoDAO().persistir(elemento);
	}
	
	/**
	 * Remover AEL_CAMPOS_CODIF_RELACIONADOS
	 * @param campoLaudoCodifRelacionado
	 * @throws BaseException
	 */
	public void excluir(AelCampoCodifRelacionado campoLaudoCodifRelacionado) throws BaseException{
		this.getAelCampoCodifRelacionadoDAO().remover(campoLaudoCodifRelacionado);
	}
	
	
	/**
	 * ORADB TRIGGER AELT_CCR_BRU
	 * 
	 * @param elemento
	 * @throws BaseException
	 */
	@SuppressWarnings("ucd")
	public void preAtualizar(AelCampoCodifRelacionado elemento) throws BaseException {
		AelCampoCodifRelacionado oldElemento = this.getAelCampoCodifRelacionadoDAO()
			.obterOriginal(elemento);
		//RN1
		if(oldElemento != null && CoreUtil.modificados(elemento.getId().getPclCalSeq(), 
				oldElemento.getId().getPclCalSeq())) {
			this.verificarTipoCampo(elemento.getId().getPclCalSeq());
		}
		
		//RN2
		if(oldElemento != null && CoreUtil.modificados(elemento.getId().getPclCalSeqVinculado(), 
				oldElemento.getId().getPclCalSeqVinculado())) {
			this.verificarTipoCampo(elemento.getId().getPclCalSeqVinculado());
		}
	}
	
	
	
	/**
	 * ORADB TRIGGER AELT_CCR_BRI
	 * 
	 * @param elemento
	 * @throws BaseException
	 */
	public void preInserir(AelCampoCodifRelacionado elemento) throws BaseException {
		//RN1 (pcl_cal_seq)
		this.verificarTipoCampo(elemento.getAelParametroCamposLaudoByAelCcrPclFk1().getId().getCalSeq());
		
		//RN2 (pcl_cal_seq_vinculado)
		this.verificarTipoCampo(elemento.getAelParametroCamposLaudoByAelCcrPclFk2().getId().getCalSeq());
	}
	
	
	
	/**
	 * ORADB aelk_ccr_rn.rn_ccrp_ver_tp_campo
	 * 
	 * @param calSeq
	 * @throws BaseException
	 */
	public void verificarTipoCampo(Integer calSeq) throws BaseException {
		final AelCampoLaudo campoLaudo = this.getAelCampoLaudoDAO()
			.obterCampoLaudoPorSeq(calSeq);
		
		if(campoLaudo == null) {
			throw new ApplicationBusinessException(AelCampoCodifRelacionadoRNExceptionCode.AEL_01314);
		} else if (DominioSituacao.A != campoLaudo.getSituacao()) {
			throw new ApplicationBusinessException(AelCampoCodifRelacionadoRNExceptionCode.AEL_01315);
		} else if (DominioTipoCampoCampoLaudo.C != campoLaudo.getTipoCampo()) {
			throw new ApplicationBusinessException(AelCampoCodifRelacionadoRNExceptionCode.AEL_01316);
		}
	}
	
	
	/** GET **/
	protected AelCampoCodifRelacionadoDAO getAelCampoCodifRelacionadoDAO() {
		return aelCampoCodifRelacionadoDAO;
	}
	
	protected AelCampoLaudoDAO getAelCampoLaudoDAO() {
		return aelCampoLaudoDAO;
	}
}
