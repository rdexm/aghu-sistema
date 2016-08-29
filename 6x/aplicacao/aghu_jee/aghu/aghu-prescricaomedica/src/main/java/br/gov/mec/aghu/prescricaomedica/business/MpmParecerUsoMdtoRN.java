package br.gov.mec.aghu.prescricaomedica.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MpmParecerUsoMdto;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.dao.MpmItemPrescParecerMdtoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmParecerUsoMdtosDAO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class MpmParecerUsoMdtoRN extends BaseBusiness {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 87806587180284710L;
	
	private static final Log LOG = LogFactory.getLog(MpmParecerUsoMdtoRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private MpmParecerUsoMdtosDAO mpmParecerUsoMdtosDAO;
	
	@Inject
	private MpmItemPrescParecerMdtoDAO mpmItemPrescParecerMdtoDAO;
	
	public enum MpmParecerUsoMdtosRNExceptionCode implements BusinessExceptionCode {
		RAP_00175, MPM_01006, MPM_01008, MPM_01009, MPM_01011, AVALIACAO_NAO_PERMITIDO_EXCLUIR, MPM_01010;
	}

	public void persistirParecerUsoMdto(MpmParecerUsoMdto parecer) throws ApplicationBusinessException {
		this.preInserir(parecer);
		this.mpmParecerUsoMdtosDAO.persistir(parecer);
		this.mpmParecerUsoMdtosDAO.flush();
	}
	
	/**
	 * Trigger de Before Insert MPMT_PUM_BRI de MPM_PARECER_USO_MDTOS
	 * 
	 * @ORADB MPMT_PUM_BRI
	 */
	public void preInserir(MpmParecerUsoMdto parecer) throws ApplicationBusinessException {
		parecer.setDthrParecer(new Date());
		this.verificarTipoParecer(parecer);
		parecer.setServidorMatricula(this.servidorLogadoFacade.obterServidorLogado());
		this.validarServidor(parecer.getServidorMatricula());
	}
	
	/**
	 * @ORADB MPMK_PUM_RN.RN_PUMP_ATU_PCER_NAO 
	 */
	public void atualizarParecerNaoVerificado(MpmParecerUsoMdto parecer) {
		parecer.setIndParecerVerificado(DominioSimNao.N);
		parecer.setServidorMatriculaVisualizado(null);
	}
	
	/**
	 * @ORADB MPMK_PUM_RN.RN_PUMP_ATU_PCER_SIM 
	 */
	public void atualizarParecerVerificado(MpmParecerUsoMdto parecer) throws ApplicationBusinessException {
		parecer.setServidorMatriculaVisualizado(servidorLogadoFacade.obterServidorLogado());
		validarServidor(parecer.getServidorMatriculaVisualizado());
	}
	
	/**
	 * fk de JUSTIFICATIVA_USO_MDTO não pode ser alterada
	 * 
	 * @ORADB MPMK_PUM_RN.RN_PUMP_VER_ALTERA 
	 */
	public void verificarParecerAlterado(MpmParecerUsoMdto parecer) throws ApplicationBusinessException {
		throw new ApplicationBusinessException(MpmParecerUsoMdtosRNExceptionCode.MPM_01006);
	}
	
	/**
	 * Não permite deleção
	 * 
	 * @ORADB MPMK_PUM_RN.RN_PUMP_VER_DELECAO 
	 */
	public void verificarParecerDeletado(MpmParecerUsoMdto parecer) throws ApplicationBusinessException {
		throw new ApplicationBusinessException(MpmParecerUsoMdtosRNExceptionCode.AVALIACAO_NAO_PERMITIDO_EXCLUIR);
	}
	
	/**
	 * Deve haver pelo menos um ITEM_PRESCRICAO_PARECER_MDTO
	 * 
	 * @ORADB MPMK_PUM_RN.RN_PUMP_VER_IT_PCER 
	 */
	public void verificarItemPrescricao(MpmParecerUsoMdto parecer) throws ApplicationBusinessException {
		Long count = this.mpmItemPrescParecerMdtoDAO.obterCountItemPrescParecerPorParecerUso(parecer.getSeq());
		if (count == null || count == 0l) {
			throw new ApplicationBusinessException(MpmParecerUsoMdtosRNExceptionCode.MPM_01008);
		}
	}
	
	/**
	 * Verifica se tipo de parecer de uso está ativo
	 * 
	 * @ORADB MPMK_PUM_RN.RN_PUMP_VER_TP_PCER
	 */
	public void verificarTipoParecer(MpmParecerUsoMdto parecer) throws ApplicationBusinessException {
		if (parecer.getMpmTipoParecerUsoMdtos() == null) {
			throw new ApplicationBusinessException(MpmParecerUsoMdtosRNExceptionCode.MPM_01009);
		} else {
			if (!DominioSituacao.A.toString().equals(parecer.getMpmTipoParecerUsoMdtos().getIndSituacao())) {
				throw new ApplicationBusinessException(MpmParecerUsoMdtosRNExceptionCode.MPM_01010);
			}
			if (DominioSimNao.S.toString().equals(parecer.getMpmTipoParecerUsoMdtos().getIndDigitaObservacao()) && 
					parecer.getObservacao() == null) {
				throw new ApplicationBusinessException(MpmParecerUsoMdtosRNExceptionCode.MPM_01011);
			}
		}
	}
	
	public void validarServidor(RapServidores servidor) throws ApplicationBusinessException {
		if (servidor == null || servidor.getId() == null || servidor.getId().getMatricula() == null) {
			throw new ApplicationBusinessException(MpmParecerUsoMdtosRNExceptionCode.RAP_00175);
		}
	}
}
