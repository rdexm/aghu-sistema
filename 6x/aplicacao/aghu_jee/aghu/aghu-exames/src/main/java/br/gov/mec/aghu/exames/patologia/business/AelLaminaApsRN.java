package br.gov.mec.aghu.exames.patologia.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelLaminaApJnDAO;
import br.gov.mec.aghu.exames.dao.AelLaminaApsDAO;
import br.gov.mec.aghu.model.AelLaminaApJn;
import br.gov.mec.aghu.model.AelLaminaAps;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class AelLaminaApsRN extends BaseBusiness {
	
	@EJB
	private LaudoUnicoRN laudoUnicoRN;
	
	private static final Log LOG = LogFactory.getLog(AelLaminaApsRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelLaminaApJnDAO aelLaminaApJnDAO;
	
	@Inject
	private AelLaminaApsDAO aelLaminaApsDAO;

	private static final long serialVersionUID = 4879378548867391256L;
	
	public enum AelLaminaApsRNExceptionCode implements BusinessExceptionCode {
		AEL_00353
	}
	
	public void inserirAelLaminaAps(final AelLaminaAps aelLaminaAps) throws BaseException {
		final AelLaminaApsDAO dao = getAelLaminaApsDAO();
		this.executarAntesInserirAelLaminaAps(aelLaminaAps);
		dao.persistir(aelLaminaAps);
	}

	public void atulizarAelLaminaAps(final AelLaminaAps aelLaminaApsNew, final AelLaminaAps aelLaminaApsOld) throws BaseException {
		final AelLaminaApsDAO dao = getAelLaminaApsDAO();
		this.executarAntesAtualizarAelLaminaAps(aelLaminaApsNew, aelLaminaApsOld);
		dao.merge(aelLaminaApsNew);
		this.executarDepoisAtualizarAelLaminaAps(aelLaminaApsNew, aelLaminaApsOld);
	}

	public void excluirAelLaminaAps(AelLaminaAps aelLaminaAps) throws BaseException {
		final AelLaminaApsDAO dao = getAelLaminaApsDAO();
		this.executarAntesExcluirAelLaminaAps(aelLaminaAps);
		aelLaminaAps = dao.merge(aelLaminaAps);
		dao.remover(aelLaminaAps);
		this.executarAposExcluirAelLaminaAps(aelLaminaAps);
	}
	
	/**
	 * ORADB Trigger AELT_LUZ_ARD
	 * 
	 * @param aelLaminaAps
	 * @throws ApplicationBusinessException 
	 */
	protected void executarAposExcluirAelLaminaAps(final AelLaminaAps aelLaminaAps) throws ApplicationBusinessException {
		insereJournal(aelLaminaAps, DominioOperacoesJournal.DEL);		
	}

	/**
	 * ORADB Trigger AELT_LUZ_BRD
	 * 
	 * @param aelLaminaAps
	 * @throws ApplicationBusinessException 
	 */
	protected void executarAntesExcluirAelLaminaAps(final AelLaminaAps aelLaminaAps) throws ApplicationBusinessException {
		final LaudoUnicoRN laudoUnicoRN = getLaudoUnicoRN();
		
		laudoUnicoRN.rnLuzpVerEtapLau(aelLaminaAps.getAelExameAp().getSeq());

		laudoUnicoRN.rnLuzpVerEtapLa2(aelLaminaAps.getAelExameAp().getSeq());
	}

	
	/**
	 * ORADB Trigger AELT_LUZ_BRI
	 * 
	 * @param aelLaminaAps
	 * @throws ApplicationBusinessException  
	 */
	protected void executarAntesInserirAelLaminaAps(final AelLaminaAps aelLaminaAps) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		final LaudoUnicoRN laudoUnicoRN = getLaudoUnicoRN();
		
		aelLaminaAps.setCriadoEm(new Date());

		if (servidorLogado == null) {
			throw new ApplicationBusinessException(AelLaminaApsRNExceptionCode.AEL_00353);
		}
		
		aelLaminaAps.setServidor(servidorLogado);
		
		laudoUnicoRN.rnLuzpVerEtapLau(aelLaminaAps.getAelExameAp().getSeq());

		laudoUnicoRN.rnLuzpVerServidor(aelLaminaAps.getServidor());

		laudoUnicoRN.rnLuzpVerEtapLa2(aelLaminaAps.getAelExameAp().getSeq());
	}
	
	/**
	 * ORADB Trigger AELT_LUZ_ARU
	 * 
	 * @param aelLaminaApsNew
	 * @param aelLaminaApsOld
	 * @throws ApplicationBusinessException 
	 */
	protected void executarDepoisAtualizarAelLaminaAps(final AelLaminaAps aelLaminaApsNew,
			final AelLaminaAps aelLaminaApsOld) throws ApplicationBusinessException {
		
		if (CoreUtil.modificados(aelLaminaApsNew.getSeq(), aelLaminaApsOld.getSeq())
		||  CoreUtil.modificados(aelLaminaApsNew.getDthrLamina(), aelLaminaApsOld.getDthrLamina())
		||  CoreUtil.modificados(aelLaminaApsNew.getCesto(), aelLaminaApsOld.getCesto())
		||  CoreUtil.modificados(aelLaminaApsNew.getNumeroCapsula(), aelLaminaApsOld.getNumeroCapsula())
		||  CoreUtil.modificados(aelLaminaApsNew.getNumeroFragmentos(), aelLaminaApsOld.getNumeroFragmentos())
		||  CoreUtil.modificados(aelLaminaApsNew.getColoracao(), aelLaminaApsOld.getColoracao())
		||  CoreUtil.modificados(aelLaminaApsNew.getDescricao(), aelLaminaApsOld.getDescricao())
		||  CoreUtil.modificados(aelLaminaApsNew.getCriadoEm(), aelLaminaApsOld.getCriadoEm())
		||  CoreUtil.modificados(aelLaminaApsNew.getAelExameAp(), aelLaminaApsOld.getAelExameAp())
		||  CoreUtil.modificados(aelLaminaApsNew.getAelMaterialAp(), aelLaminaApsOld.getAelMaterialAp())
		||  CoreUtil.modificados(aelLaminaApsNew.getAelTextoPadraoColoracs(), aelLaminaApsOld.getAelTextoPadraoColoracs())
		||  CoreUtil.modificados(aelLaminaApsNew.getIndRecorte(), aelLaminaApsOld.getIndRecorte())
		||  CoreUtil.modificados(aelLaminaApsNew.getServidor(), aelLaminaApsOld.getServidor())) {
			
			insereJournal(aelLaminaApsOld, DominioOperacoesJournal.UPD);
		}
		
	}

	private void insereJournal(final AelLaminaAps aelLaminaApsOld,
			final DominioOperacoesJournal operacao) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		final AelLaminaApJn jn = new  AelLaminaApJn();
		
		jn.setNomeUsuario(servidorLogado.getUsuario());
		jn.setOperacao(operacao);
		jn.setSeq(aelLaminaApsOld.getSeq());
		jn.setDthrLamina(aelLaminaApsOld.getDthrLamina());
		jn.setCesto(aelLaminaApsOld.getCesto());
		jn.setNumeroCapsula(aelLaminaApsOld.getNumeroCapsula());
		jn.setNumeroFragmentos(aelLaminaApsOld.getNumeroFragmentos());
		jn.setColoracao(aelLaminaApsOld.getColoracao());
		jn.setDescricao(aelLaminaApsOld.getDescricao());
		jn.setCriadoEm(aelLaminaApsOld.getCriadoEm());
		jn.setAelExameAp(aelLaminaApsOld.getAelExameAp());
		jn.setServidor(aelLaminaApsOld.getServidor());
		
		final AelLaminaApJnDAO dao = getAelLaminaApJnDAO();
		dao.persistir(jn);
	}

	/**
	 * ORADB Trigger AELT_LUZ_BRU
	 * 
	 * @param aelLaminaApsNew
	 * @param aelLaminaApsOld
	 * @throws ApplicationBusinessException 
	 */
	protected void executarAntesAtualizarAelLaminaAps(final AelLaminaAps aelLaminaApsNew,
			final AelLaminaAps aelLaminaApsOld) throws ApplicationBusinessException {
		
		final LaudoUnicoRN laudoUnicoRN = getLaudoUnicoRN();
		
		
		laudoUnicoRN.rnLuzpVerEtapLau(aelLaminaApsOld.getAelExameAp().getSeq());

		laudoUnicoRN.rnLuzpVerServidor(aelLaminaApsOld.getServidor());

		laudoUnicoRN.rnLuzpVerEtapLa2(aelLaminaApsOld.getAelExameAp().getSeq());
	}

	protected AelLaminaApsDAO getAelLaminaApsDAO() {
		return aelLaminaApsDAO;
	}

	protected LaudoUnicoRN getLaudoUnicoRN() {
		return laudoUnicoRN;
	}
	
	protected AelLaminaApJnDAO getAelLaminaApJnDAO() {
		return aelLaminaApJnDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}
