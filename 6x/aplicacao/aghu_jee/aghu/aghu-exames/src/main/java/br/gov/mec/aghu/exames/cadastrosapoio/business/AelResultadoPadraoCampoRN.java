package br.gov.mec.aghu.exames.cadastrosapoio.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.OptimisticLockException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoCampoCampoLaudo;
import br.gov.mec.aghu.exames.dao.AelResultadoCaracteristicaDAO;
import br.gov.mec.aghu.exames.dao.AelResultadoCodificadoDAO;
import br.gov.mec.aghu.exames.dao.AelResultadoPadraoCampoDAO;
import br.gov.mec.aghu.exames.dao.AelResultadosPadraoDAO;
import br.gov.mec.aghu.model.AelCampoLaudo;
import br.gov.mec.aghu.model.AelResultadoCaracteristica;
import br.gov.mec.aghu.model.AelResultadoCodificado;
import br.gov.mec.aghu.model.AelResultadoPadraoCampo;
import br.gov.mec.aghu.model.AelResultadosPadrao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@SuppressWarnings("PMD.CyclomaticComplexity")
@Stateless
public class AelResultadoPadraoCampoRN extends BaseBusiness {
	
	private static final Log LOG = LogFactory.getLog(AelResultadoPadraoCampoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelResultadoPadraoCampoDAO aelResultadoPadraoCampoDAO;
	
	@Inject
	private AelResultadosPadraoDAO aelResultadosPadraoDAO;
	
	@Inject
	private AelResultadoCodificadoDAO aelResultadoCodificadoDAO;
	
	@Inject
	private AelResultadoCaracteristicaDAO aelResultadoCaracteristicaDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -4775759519996492100L;

	public enum AelResultadoPadraoCampoRNExceptionCode implements BusinessExceptionCode {

		AEL_00669, AEL_00670, AEL_00814, AEL_00804, AEL_00803, AEL_00806,// 
		AEL_00868, AEL_00805, AEL_00867, AEL_00801, AEL_00802, AEL_00869, AEL_01072;//

	}
	
	public void atualizar(AelResultadoPadraoCampo resultadoPadraoCampo) throws BaseException {
		try {
			this.preAtualizarResultadoPadraoCampo(resultadoPadraoCampo);
			this.getAelResultadoPadraoCampoDAO().atualizar(resultadoPadraoCampo);
			this.posAtualizarResultadoPadraoCampo(resultadoPadraoCampo);
			this.getAelResultadoPadraoCampoDAO().flush();
		
		}  catch (OptimisticLockException e) {
			logError(e.getMessage(), e);
			throw e;
		}
	}
	
	public void preAtualizarResultadoPadraoCampo(AelResultadoPadraoCampo resultadoPadraoCampo) throws BaseException {
		
		this.executarRestricoes(resultadoPadraoCampo);
		this.obterServidorLogado(resultadoPadraoCampo);
		
		final AelResultadoPadraoCampo original = this.getAelResultadoPadraoCampoDAO().obterOriginal(resultadoPadraoCampo);
		
		if(!original.getId().getRpaSeq().equals(resultadoPadraoCampo.getId().getRpaSeq()) 
				|| (original.getCampoLaudo() != null && !original.getCampoLaudo().equals(resultadoPadraoCampo.getCampoLaudo()))){
			this.verificarRNRpcpVerArcExm(resultadoPadraoCampo);
		}
		
		if(!original.getId().getRpaSeq().equals(resultadoPadraoCampo.getId().getRpaSeq()) 
				|| (original.getCampoLaudo() != null && !original.getCampoLaudo().equals(resultadoPadraoCampo.getCampoLaudo()))
				|| (original.getParametroCampoLaudo() !=null && !original.getParametroCampoLaudo().equals(resultadoPadraoCampo.getParametroCampoLaudo()))
				|| (original.getResultadoCaracteristica() != null && !original.getResultadoCaracteristica().equals(resultadoPadraoCampo.getResultadoCaracteristica()))
				|| (original.getResultadoCodificado() != null && !original.getResultadoCodificado().equals(resultadoPadraoCampo.getResultadoCodificado()))
				|| (original.getValor() != null && !original.getValor().equals(resultadoPadraoCampo.getValor()))){
			this.verificarRNRpcpVerTipCamp(resultadoPadraoCampo);
		}
		
		
	}
	
	
	public void posAtualizarResultadoPadraoCampo(AelResultadoPadraoCampo resultadoPadraoCampo) throws ApplicationBusinessException {
		this.verificarRNRpcpVerDuplic(resultadoPadraoCampo);
	}
	
	
	public void persistir(AelResultadoPadraoCampo resultadoPadraoCampo) throws BaseException {
		
		if(resultadoPadraoCampo.getId() == null){
			this.inserir(resultadoPadraoCampo);
		} else{
			this.atualizar(resultadoPadraoCampo);
		}
		
		this.getAelResultadoPadraoCampoDAO().flush();
	}
	
	/**
	 * Insere AelResultadoPadraoCampo
	 * 
	 * @param resultadoPadraoCampo
	 * @throws BaseException
	 */
	public void inserir(AelResultadoPadraoCampo resultadoPadraoCampo) throws BaseException {
		this.preInserirResultaPadraoCampo(resultadoPadraoCampo);
		this.getAelResultadoPadraoCampoDAO().persistir(resultadoPadraoCampo);
		this.posInserirResultadoPadraoCampo(resultadoPadraoCampo);
	}
	
		
	/**
	 * ORADB TRIGGER AELT_RPC_BRI
	 * 
	 * @param resultadoPadraoCampo
	 * @throws BaseException
	 */
	public void preInserirResultaPadraoCampo(AelResultadoPadraoCampo resultadoPadraoCampo) throws BaseException {
		this.executarRestricoes(resultadoPadraoCampo);
		this.obterServidorLogado(resultadoPadraoCampo);
		this.verificarRNRpcpVerArcExm(resultadoPadraoCampo);
		this.verificarRNRpcpVerTipCamp(resultadoPadraoCampo);
	}
	
	
	public void posInserirResultadoPadraoCampo(AelResultadoPadraoCampo resultadoPadraoCampo) throws BaseException {
		this.verificarRNRpcpVerDuplic(resultadoPadraoCampo);
	}
	
	
	/**
	 * ORADB CONSTRAINTS (INSERT/UPDATE)
	 * 
	 * @param resultadoPadraoCampo
	 * @throws BaseException
	 */
	public void executarRestricoes(AelResultadoPadraoCampo resultadoPadraoCampo) throws BaseException {
		//AEL_RPC_CK1
		if(resultadoPadraoCampo.getParametroCampoLaudo() != null
					&& resultadoPadraoCampo.getCampoLaudo() != null) {
			throw new ApplicationBusinessException(AelResultadoPadraoCampoRNExceptionCode.AEL_00669);
		}
		
		//AEL_RPC_CK2
		if(resultadoPadraoCampo.getResultadoCodificado() != null 
				&& resultadoPadraoCampo.getResultadoCaracteristica() != null) {
			throw new ApplicationBusinessException(AelResultadoPadraoCampoRNExceptionCode.AEL_00670);
		}
	}
	
	
	public void obterServidorLogado(AelResultadoPadraoCampo resultadoPadraoCampo) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		resultadoPadraoCampo.setServidor(servidorLogado);
	}
	
	
	/**
	 * ORADB PROCEDURE AELK_RPC_RN.RN_RPCP_VER_ARC_EXM
	 * 
	 * @param resultadoPadraoCampo
	 * @throws ApplicationBusinessException 
	 */
	public void verificarRNRpcpVerArcExm(AelResultadoPadraoCampo resultadoPadraoCampo) throws ApplicationBusinessException {
		final AelResultadosPadrao resultadoPadrao = this.getAelResultadosPadraoDAO().obterOriginal(
				resultadoPadraoCampo.getResultadoPadrao().getSeq());
		
		if((resultadoPadrao != null 
				&& resultadoPadrao.getExameMaterialAnalise() != null)
				&& resultadoPadraoCampo.getCampoLaudo() != null) {
			throw new ApplicationBusinessException(AelResultadoPadraoCampoRNExceptionCode.AEL_00814);
		}
	}
	
	
	/**
	 * ORADB PROCEDURE AELK_RPC_RN.RN_RPCP_VER_TIP_CAMP
	 * 
	 * @param resultadoPadraoCampo
	 * @throws ApplicationBusinessException 
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	public void verificarRNRpcpVerTipCamp(AelResultadoPadraoCampo resultadoPadraoCampo) throws ApplicationBusinessException {

		AelCampoLaudo campoLaudo = resultadoPadraoCampo.getCampoLaudo();

		// Quando o campo laudo do resultado padrão for nulo utiliza o campo laudo do parâmetro campo laudo
		if (campoLaudo == null) {
			campoLaudo = resultadoPadraoCampo.getParametroCampoLaudo().getCampoLaudo();
		}

		// RN3.8
		if (campoLaudo != null) {

			// RN3.1
			if ((resultadoPadraoCampo.getResultadoCaracteristica() != null 
					|| resultadoPadraoCampo.getResultadoCodificado() != null) 
					&& !DominioTipoCampoCampoLaudo.C.equals(campoLaudo.getTipoCampo())) {
				throw new ApplicationBusinessException(AelResultadoPadraoCampoRNExceptionCode.AEL_00804);
			} else {

				if (DominioTipoCampoCampoLaudo.C.equals(campoLaudo.getTipoCampo())) {

					// RN3.2
					if (resultadoPadraoCampo.getResultadoCaracteristica() == null && resultadoPadraoCampo.getResultadoCodificado() == null) {
						throw new ApplicationBusinessException(AelResultadoPadraoCampoRNExceptionCode.AEL_00803);
					}

					// RN3.3
					if (campoLaudo.getGrupoResultadoCaracteristica() != null) {
						String velEmaExaSigla = null;
						Integer velEmaManSeq = null;
						
						if(resultadoPadraoCampo.getParametroCampoLaudo() != null && resultadoPadraoCampo.getParametroCampoLaudo().getId() != null) {
							velEmaExaSigla = resultadoPadraoCampo.getParametroCampoLaudo().getId().getVelEmaExaSigla();
							velEmaManSeq = resultadoPadraoCampo.getParametroCampoLaudo().getId().getVelEmaManSeq();
						}
						
						AelResultadoCaracteristica resultadoCaracteristica = this.getAelResultadoCaracteristicaDAO().obterResultadoCaracteristicaPorTipoCampo(
								resultadoPadraoCampo.getCampoLaudo().getSeq(), velEmaExaSigla, velEmaManSeq);

						if (resultadoCaracteristica == null) {
							throw new ApplicationBusinessException(AelResultadoPadraoCampoRNExceptionCode.AEL_00806);
						} else if (DominioSituacao.I.equals(resultadoCaracteristica.getIndSituacao())) {
							throw new ApplicationBusinessException(AelResultadoPadraoCampoRNExceptionCode.AEL_00868);
						}
					} else{
						
						// RN3.4
						if (campoLaudo.getGrupoResultadoCodificado() != null
								&& resultadoPadraoCampo.getResultadoCodificado() != null
								&& !campoLaudo.getGrupoResultadoCodificado().getSeq().equals(resultadoPadraoCampo.getResultadoCodificado().getId().getGtcSeq())) {
							throw new ApplicationBusinessException(AelResultadoPadraoCampoRNExceptionCode.AEL_00805);
						} else{
							
							// RN3.5
							if (campoLaudo.getGrupoResultadoCodificado() != null && campoLaudo.getGrupoResultadoCodificado().getSeq() != null
									&& resultadoPadraoCampo.getResultadoCodificado() != null) {

								AelResultadoCodificado resultadoCodificado = this.getAelResultadoCodificadoDAO().obterPorChavePrimaria(
										resultadoPadraoCampo.getResultadoCodificado().getId());

								if (resultadoCodificado != null && DominioSituacao.I.equals(resultadoCodificado.getSituacao())) {
									throw new ApplicationBusinessException(AelResultadoPadraoCampoRNExceptionCode.AEL_00867);
								}
							}
						}

					}

				} else {

					// RN3.6
					if (DominioTipoCampoCampoLaudo.N.equals(campoLaudo.getTipoCampo()) && resultadoPadraoCampo.getValor() == null) {
						throw new ApplicationBusinessException(AelResultadoPadraoCampoRNExceptionCode.AEL_00801);
					} else {
						// RN3.7
						if (DominioTipoCampoCampoLaudo.E.equals(campoLaudo.getTipoCampo()) || DominioTipoCampoCampoLaudo.T.equals(campoLaudo.getTipoCampo())) {
							throw new ApplicationBusinessException(AelResultadoPadraoCampoRNExceptionCode.AEL_00802);
						}
					}

				}

			}

		} else{
			throw new ApplicationBusinessException(AelResultadoPadraoCampoRNExceptionCode.AEL_00869);
		}
	}
	
	
	/**
	 * ORADB PROCEDURE AELK_RPC_RN.RN_RPCP_VER_DUPLIC
	 * 
	 * @param resultadoPadraoCampo
	 * @throws ApplicationBusinessException
	 */
	public void verificarRNRpcpVerDuplic(AelResultadoPadraoCampo resultadoPadraoCampo) throws ApplicationBusinessException {
		
		AelCampoLaudo campoLaudo = resultadoPadraoCampo.getCampoLaudo();
		
		// Quando o campo laudo do resultado padrão for nulo utiliza o campo laudo do parâmetro campo laudo
		if(campoLaudo == null){
			campoLaudo = resultadoPadraoCampo.getParametroCampoLaudo().getCampoLaudo();
		}
		
		AelResultadoPadraoCampo vResultadoPadraoCampo = 
			this.getAelResultadoPadraoCampoDAO().obterResultadoPadraoCampoPorParametro(
					campoLaudo.getSeq(), 
					resultadoPadraoCampo.getId().getSeqp(), 
					resultadoPadraoCampo.getId().getRpaSeq());
		
		if(vResultadoPadraoCampo != null) {
			throw new ApplicationBusinessException(AelResultadoPadraoCampoRNExceptionCode.AEL_01072);
		}
	}
	
	
	public void remover(AelResultadoPadraoCampo resultadoPadraoCampo) throws BaseException {
		resultadoPadraoCampo = this.getAelResultadoPadraoCampoDAO().obterPorChavePrimaria(resultadoPadraoCampo.getId());
		this.getAelResultadoPadraoCampoDAO().remover(resultadoPadraoCampo);
	}
	
	
	/**GET**/
	protected AelResultadoPadraoCampoDAO getAelResultadoPadraoCampoDAO() {
		return aelResultadoPadraoCampoDAO;
	}
	
	protected AelResultadosPadraoDAO getAelResultadosPadraoDAO() {
		return aelResultadosPadraoDAO;
	}
	
	protected AelResultadoCaracteristicaDAO getAelResultadoCaracteristicaDAO() {
		return aelResultadoCaracteristicaDAO;
	}
	
	protected AelResultadoCodificadoDAO getAelResultadoCodificadoDAO() {
		return aelResultadoCodificadoDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
