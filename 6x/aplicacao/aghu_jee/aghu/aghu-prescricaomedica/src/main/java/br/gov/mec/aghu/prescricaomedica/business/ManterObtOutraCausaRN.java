package br.gov.mec.aghu.prescricaomedica.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.model.MpmObtOutraCausa;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaSumarioDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmObtOutraCausaDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * 
 * @author lalegre
 *
 */
@Stateless
public class ManterObtOutraCausaRN extends BaseBusiness {


@EJB
private ManterObtCausaDiretaRN manterObtCausaDiretaRN;

@EJB
private ManterAltaSumarioRN manterAltaSumarioRN;

@Inject
private MpmAltaSumarioDAO mpmAltaSumarioDAO;

private static final Log LOG = LogFactory.getLog(ManterObtOutraCausaRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private MpmObtOutraCausaDAO mpmObtOutraCausaDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -3716927706129542713L;

	public enum ManterObtOutraCausaRNExceptionCode implements BusinessExceptionCode {

		MPM_02726, MPM_02727, MPM_02728, MPM_02729, MPM_02618;

		public void throwException(Object... params)
		throws ApplicationBusinessException {
			throw new ApplicationBusinessException(this, params);
		}

	}

	/**
	 * TRIGGER MPMT_OOC_BASE_BRI, MPMT_OOC_BRI
	 * @param obtOutraCausa
	 */
	public void inserirObtOutraCausa(MpmObtOutraCausa obtOutraCausa) throws ApplicationBusinessException {

		this.preInserirObtOutraCausa(obtOutraCausa);
		this.getMpmObtOutraCausaDAO().persistir(obtOutraCausa);
		this.getMpmObtOutraCausaDAO().flush();

	}

	/**
	 * validacoes da trigger
	 * @param obtOutraCausa
	 */
	protected void preInserirObtOutraCausa(MpmObtOutraCausa obtOutraCausa) throws ApplicationBusinessException {

		if (obtOutraCausa.getMpmCidAtendimentos() != null) {
			this.getAltaSumarioRN().verificarCidAtendimento(obtOutraCausa.getMpmCidAtendimentos().getSeq());
		}
		this.getAltaSumarioRN().verificarAltaSumarioAtivo(obtOutraCausa.getMpmAltaSumarios());
		this.getManterObtCausaDiretaRN().verificarTipoAltaSumario(obtOutraCausa.getMpmAltaSumarios());

	}

	/**
	 * TRIGGER MPMT_OOC_BRU, MPMT_OOC_BASE_BRU
	 * @param obtOutraCausa
	 */
	public void atualizarObtOutraCausa(MpmObtOutraCausa obtOutraCausa) throws ApplicationBusinessException {

		this.preAtualizarObtOutraCausa(obtOutraCausa);
		this.getMpmObtOutraCausaDAO().atualizar(obtOutraCausa);
		this.getMpmObtOutraCausaDAO().flush();

	}

	/**
	 * validacoes da trigger
	 * @param obtOutraCausa
	 */
	protected void preAtualizarObtOutraCausa(MpmObtOutraCausa obtOutraCausa) throws ApplicationBusinessException {

		MpmObtOutraCausa obtOutraCausaOriginal = this.getMpmObtOutraCausaDAO().obterPorChavePrimaria(obtOutraCausa.getId());
	
        MpmAltaSumario  mpmAltaSumario = this.getMpmAltaSumarioDAO().obterAltaSumarioPeloId(
    		   obtOutraCausa.getMpmAltaSumarios().getId().getApaAtdSeq(),
    		   obtOutraCausa.getMpmAltaSumarios().getId().getApaSeq(),
    		   obtOutraCausa.getMpmAltaSumarios().getId().getSeqp());
		
		this.getAltaSumarioRN().verificarCidAtendimento(obtOutraCausa.getCid().getSeq());
		this.getAltaSumarioRN().verificarAltaSumarioAtivo(mpmAltaSumario);
		this.getManterObtCausaDiretaRN().verificarTipoAltaSumario(mpmAltaSumario);
		this.verificarAlteracoes(obtOutraCausa, obtOutraCausaOriginal);
		this.verificarSituacao(obtOutraCausa.getIndSituacao(), obtOutraCausaOriginal.getIndSituacao());
		this.verificarIndicadorDeCarga(obtOutraCausa.getIndCarga(), obtOutraCausaOriginal.getIndCarga());
		
	}

	/**
	 * TRIGGER MPMT_OOC_BRD
	 * @param obtOutraCausa
	 */
	public void removerObtOutraCausa(MpmObtOutraCausa obtOutraCausa) throws ApplicationBusinessException {

		this.getAltaSumarioRN().verificarAltaSumarioAtivo(obtOutraCausa.getMpmAltaSumarios());
		this.getMpmObtOutraCausaDAO().remover(obtOutraCausa);
		this.getMpmObtOutraCausaDAO().flush();

	}

	/**
	 * ORADB PROCEDURE RN_OOCP_VER_ALTERA
	 * @param obtOutraCausa
	 */
	public void verificarAlteracoes(MpmObtOutraCausa obtOutraCausa, MpmObtOutraCausa obtOutraCausaOriginal) throws ApplicationBusinessException {

		if (obtOutraCausa.getIndCarga().equals(DominioSimNao.S)) {

			if (CoreUtil.modificados(obtOutraCausa.getDescCid(), obtOutraCausaOriginal.getDescCid()) || CoreUtil.modificados(obtOutraCausa.getComplCid(), obtOutraCausaOriginal.getComplCid()) || CoreUtil.modificados(obtOutraCausa.getCid().getSeq(), obtOutraCausaOriginal.getCid().getSeq())) {

				ManterObtOutraCausaRNExceptionCode.MPM_02726.throwException();

			}

		} else {

			if (CoreUtil.modificados(obtOutraCausa.getDescCid(), obtOutraCausaOriginal.getDescCid()) && !CoreUtil.modificados(obtOutraCausa.getComplCid(), obtOutraCausaOriginal.getComplCid()) && !CoreUtil.modificados(obtOutraCausa.getCid().getSeq(), obtOutraCausaOriginal.getCid().getSeq())) {

				ManterObtOutraCausaRNExceptionCode.MPM_02727.throwException();

			}

			if (!CoreUtil.modificados(obtOutraCausa.getDescCid(), obtOutraCausaOriginal.getDescCid()) && CoreUtil.modificados(obtOutraCausa.getComplCid(), obtOutraCausaOriginal.getComplCid()) && CoreUtil.modificados(obtOutraCausa.getCid().getSeq(), obtOutraCausaOriginal.getCid().getSeq())) {

				ManterObtOutraCausaRNExceptionCode.MPM_02728.throwException();

			}

		}

	}
	
	/**
	 * ORADB RN_OOCP_VER_SITUACAO
	 * @param novaSituacao
	 * @param antigaSituacao
	 *  
	 */
	public void verificarSituacao(DominioSituacao novaSituacao, DominioSituacao antigaSituacao) throws ApplicationBusinessException {
		
		if (antigaSituacao.equals(DominioSituacao.I) && novaSituacao.equals(DominioSituacao.A)) {
			
			ManterObtOutraCausaRNExceptionCode.MPM_02729.throwException();
			
		}
		
	}
	
	/**
	 * ORABD PROCEDURE RN_OOCP_VER_IND_CARG
	 * @param novoIndCarga
	 * @param antigoIndCarga
	 *  
	 */
	public void verificarIndicadorDeCarga(DominioSimNao novoIndCarga, DominioSimNao antigoIndCarga) throws ApplicationBusinessException {
		
		if (!novoIndCarga.equals(antigoIndCarga)) {
			
			ManterObtOutraCausaRNExceptionCode.MPM_02618.throwException();
			
		}
		
	}

	protected ManterObtCausaDiretaRN getManterObtCausaDiretaRN() {
		return manterObtCausaDiretaRN;
	}

	protected MpmObtOutraCausaDAO getMpmObtOutraCausaDAO() {
		return mpmObtOutraCausaDAO;
	}

	protected ManterAltaSumarioRN getAltaSumarioRN() {
		return manterAltaSumarioRN;
	}

	public MpmAltaSumarioDAO getMpmAltaSumarioDAO() {
		return mpmAltaSumarioDAO;
	}

	public void setMpmAltaSumarioDAO(MpmAltaSumarioDAO mpmAltaSumarioDAO) {
		this.mpmAltaSumarioDAO = mpmAltaSumarioDAO;
	}

}
