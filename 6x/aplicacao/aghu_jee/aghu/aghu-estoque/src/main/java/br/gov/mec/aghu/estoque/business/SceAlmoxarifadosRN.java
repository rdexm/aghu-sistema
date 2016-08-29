package br.gov.mec.aghu.estoque.business;

import java.util.Calendar;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.estoque.dao.SceAlmoxarifadoDAO;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceAlmoxarifado;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class SceAlmoxarifadosRN extends BaseBusiness{
	
	private static final Log LOG = LogFactory.getLog(SceAlmoxarifadosRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private SceAlmoxarifadoDAO sceAlmoxarifadoDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -115049769079848697L;

	public enum SceAlmoxarifadosRNExceptionCode implements BusinessExceptionCode {
		SCE_00298,SCE_00329,ALMOXARIFADO_POSSUI_ITEM_TR_BLOQUEADO;
		
		public void throwException(Object... params)
				throws ApplicationBusinessException {
			throw new ApplicationBusinessException(this, params);
		}

	}
	
	/**
	 * ORADB SCEK_RMS_RN.RN_RMSP_VER_ALM_ATIV
	 * Verifica se o SceAlmoxarifados recebido não está ativo, gerando erro.
	 * @param seq
	 * @throws ApplicationBusinessException
	 */
	public SceAlmoxarifado  verificarAlmoxariadoAtivoPorId(Short seq) throws ApplicationBusinessException{
		SceAlmoxarifado sceAlmoxarifados = getSceAlmoxarifadoDAO().obterSceAlmoxarifadosAtivoPorSeq(seq);
		if(sceAlmoxarifados == null){
			throw new ApplicationBusinessException(SceAlmoxarifadosRNExceptionCode.SCE_00298);
		}
		return sceAlmoxarifados;
	}
	
	protected SceAlmoxarifadoDAO getSceAlmoxarifadoDAO() {
		return sceAlmoxarifadoDAO;
	}
	
	/**
	 * @ORADB scek_alm_rn.rn_almp_ver_alm_cent
	 * @param seq
	 * @return
	 */
	public void verificarExistenciaAlmoxarifadoCentral(SceAlmoxarifado almoxarifado) throws ApplicationBusinessException{
		if (almoxarifado.getIndCentral()
				&&	getSceAlmoxarifadoDAO().existeAlmoxarifadoCentralDiferenteSeq(almoxarifado.getSeq())) {
			SceAlmoxarifadosRNExceptionCode.SCE_00329.throwException();
		}		
	}
	
	/**
	 * @ORADB RN_ALMP_ATU_SITUACAO
	 * @param alterado
	 * @param original
	 * @return
	 *  
	 */
	public void verificarSituacao(SceAlmoxarifado alterado, SceAlmoxarifado original) throws ApplicationBusinessException {
		if (alterado.getSeq() != null
				&& !alterado.getIndSituacao().equals(original.getIndSituacao())) {
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			
			alterado.setServidor(servidorLogado);
			alterado.setDtAlteraSituacao(Calendar.getInstance().getTime());
		}
	}
	
	/**
	 * @ORABD RN_ALMP_VER_TR_BLOQ
	 * @param alterado
	 * @param original
	 * @throws ApplicationBusinessException
	 */
	public void verificarBloqueioTransferencia(SceAlmoxarifado alterado, SceAlmoxarifado original) throws ApplicationBusinessException{
		if (alterado.getSeq() != null
				&& !alterado.getIndBloqEntrTransf().equals(original.getIndBloqEntrTransf())
				&& !alterado.getIndBloqEntrTransf()
				&& getSceAlmoxarifadoDAO().isAlmoxarifadoPossuiItemTrBloqueado(alterado.getSeq())) {
			SceAlmoxarifadosRNExceptionCode.ALMOXARIFADO_POSSUI_ITEM_TR_BLOQUEADO.throwException();
		}
	}
	
	/**
	 * @ORABD Trigger SCET_ALM_BRU
	 * @param alterado
	 * @param original
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	public void executarAntesPersistencia(SceAlmoxarifado alterado, SceAlmoxarifado original) throws ApplicationBusinessException {
		verificarExistenciaAlmoxarifadoCentral(alterado);
		verificarSituacao(alterado, original);
		verificarBloqueioTransferencia(alterado, original);
	}

	@SuppressWarnings("PMD.NPathComplexity")
	public Boolean isAlmoxarifadoAlterado(SceAlmoxarifado alterado) {
		
		SceAlmoxarifado original = getSceAlmoxarifadoDAO().obterOriginal(alterado.getSeq());
		
		Boolean retorno = Boolean.FALSE;
		if (alterado != null && original != null) {
			if (CoreUtil.modificados(alterado.getCentroCusto(), original.getCentroCusto())) {
				retorno = Boolean.TRUE;				
			}
			if (CoreUtil.modificados(alterado.getDescricao(), original.getDescricao())) {
				retorno = Boolean.TRUE;
			}
			if (CoreUtil.modificados(alterado.getDiasEstqMinimo(), original.getDiasEstqMinimo())) {
				retorno = Boolean.TRUE;
			}
			if (CoreUtil.modificados(alterado.getIndBloqEntrTransf(), original.getIndBloqEntrTransf())) {
				retorno = Boolean.TRUE;
			}
			if (CoreUtil.modificados(alterado.getIndCalculaMediaPonderada(), original.getIndCalculaMediaPonderada())) {
				retorno = Boolean.TRUE;
			}
			if (CoreUtil.modificados(alterado.getIndCentral(), original.getIndCentral())) {
				retorno = Boolean.TRUE;
			}
			if (CoreUtil.modificados(alterado.getIndSituacao(), original.getIndSituacao())) {
				retorno = Boolean.TRUE;
			}
			if (CoreUtil.modificados(alterado.getTempoReposicaoClassA(), original.getTempoReposicaoClassA())) {
				retorno = Boolean.TRUE;
			}
			if (CoreUtil.modificados(alterado.getTempoReposicaoClassB(), original.getTempoReposicaoClassB())) {
				retorno = Boolean.TRUE;
			}
			if (CoreUtil.modificados(alterado.getTempoReposicaoClassC(), original.getTempoReposicaoClassC())) {
				retorno = Boolean.TRUE;
			}
			if (CoreUtil.modificados(alterado.getTempoReposicaoContrClassA(), original.getTempoReposicaoContrClassA())) {
				retorno = Boolean.TRUE;
			}
			if (CoreUtil.modificados(alterado.getTempoReposicaoContrClassB(), original.getTempoReposicaoContrClassB())) {
				retorno = Boolean.TRUE;
			}
			if (CoreUtil.modificados(alterado.getTempoReposicaoContrClassC(), original.getTempoReposicaoContrClassC())) {
				retorno = Boolean.TRUE;
			}
			if (CoreUtil.modificados(alterado.getDiasParcelaClassA(), original.getDiasParcelaClassA())) {
				retorno = Boolean.TRUE;
			}
			if (CoreUtil.modificados(alterado.getDiasParcelaClassB(), original.getDiasParcelaClassB())) {
				retorno = Boolean.TRUE;
			}
			if (CoreUtil.modificados(alterado.getDiasParcelaClassC(), original.getDiasParcelaClassC())) {
				retorno = Boolean.TRUE;
			}
			if (CoreUtil.modificados(alterado.getDiasSaldoClassA(), original.getDiasSaldoClassA())) {
				retorno = Boolean.TRUE;
			}
			if (CoreUtil.modificados(alterado.getDiasSaldoClassB(), original.getDiasSaldoClassB())) {
				retorno = Boolean.TRUE;
			}
			if (CoreUtil.modificados(alterado.getDiasSaldoClassC(), original.getDiasSaldoClassC())) {
				retorno = Boolean.TRUE;
			}
		}
		return retorno;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
