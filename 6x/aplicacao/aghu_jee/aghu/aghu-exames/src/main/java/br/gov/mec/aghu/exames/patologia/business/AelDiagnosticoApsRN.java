package br.gov.mec.aghu.exames.patologia.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelDiagnosticoApJnDAO;
import br.gov.mec.aghu.exames.dao.AelDiagnosticoApsDAO;
import br.gov.mec.aghu.model.AelDiagnosticoApJn;
import br.gov.mec.aghu.model.AelDiagnosticoAps;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class AelDiagnosticoApsRN extends BaseBusiness {

	@EJB
	private LaudoUnicoRN laudoUnicoRN;
	
	private static final Log LOG = LogFactory.getLog(AelDiagnosticoApsRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelDiagnosticoApJnDAO aelDiagnosticoApJnDAO;
	
	@Inject
	private AelDiagnosticoApsDAO aelDiagnosticoApsDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 4532904490783765513L;

	private enum AelDiagnosticoApsRNExceptionCode implements BusinessExceptionCode {
		  AEL_02647
		;
	}

	public void persistir(final AelDiagnosticoAps diagnosticoAps) throws BaseException {
		if (diagnosticoAps.getSeq() == null) {
			this.inserir(diagnosticoAps);
		} else {
			final AelDiagnosticoAps diagnosticoApsOld = getAelDiagnosticoApsDAO().obterOriginal(diagnosticoAps.getSeq()); 
			this.atualizar(diagnosticoAps, diagnosticoApsOld);
		}
	}

	public void atualizar(final AelDiagnosticoAps diagnosticoAps, final AelDiagnosticoAps diagnosticoApsOld) throws BaseException {
		this.executarAntesAtualizar(diagnosticoAps);
		this.getAelDiagnosticoApsDAO().merge(diagnosticoAps);
		this.executarAposAtualizar(diagnosticoAps, diagnosticoApsOld);
	}

	public void inserir(final AelDiagnosticoAps diagnosticoAps) throws BaseException {
		this.executarAntesInserir(diagnosticoAps);
		getAelDiagnosticoApsDAO().persistir(diagnosticoAps);
	}
	
	// @ORABD AELT_LUN_BRI
	private void executarAntesInserir(final AelDiagnosticoAps diagnosticoAps) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		diagnosticoAps.setCriadoEm(new Date());
		// aelk_ael_rn.rn_aelp_atu_servidor | Código equivalente
		diagnosticoAps.setServidor(servidorLogado);

		getLaudoUnicoRN().rnLunpVerEtapLau(diagnosticoAps.getAelExameAp().getSeq());

		// O servidor deve estar na tab Patologistas com ind_sit = 'S'
		getLaudoUnicoRN().rnLunpVerServidor(diagnosticoAps.getServidor());
	}

	// @ORABD AELT_LUN_BRU
	private void executarAntesAtualizar(final AelDiagnosticoAps diagnosticoAps) throws BaseException {

		getLaudoUnicoRN().rnLunpVerEtapLau(diagnosticoAps.getAelExameAp().getSeq());

		//Se neoplasia maligna = 'S' então a margem comprometida deve ser informada.
		if(diagnosticoAps.getNeoplasiaMaligna()!= null && diagnosticoAps.getNeoplasiaMaligna().isSim() && diagnosticoAps.getMargemComprometida() == null) {
			//Equivalente a @ORABD : AELK_LUN_RN.RN_LUNP_VER_MARGEM
			//Informe a Margem Comprometida para Neoplasia Maligna!
			throw new ApplicationBusinessException(AelDiagnosticoApsRNExceptionCode.AEL_02647);
		}
		
		// O servidor deve estar na tab Patologistas com ind_sit = 'S'
		getLaudoUnicoRN().rnLunpVerServidor(diagnosticoAps.getServidor());
	}

	// @ORABD AELT_LUN_ARU
	@SuppressWarnings("PMD.NPathComplexity")
	private void executarAposAtualizar(final AelDiagnosticoAps diagnosticoAps, final AelDiagnosticoAps diagnosticoApsOld) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if(CoreUtil.modificados(diagnosticoAps.getDiagnostico(), diagnosticoApsOld.getDiagnostico())) {
			AelDiagnosticoApJn jn = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.UPD, AelDiagnosticoApJn.class, servidorLogado.getUsuario());
			jn.setSeq(diagnosticoApsOld.getSeq());
			jn.setDiagnostico(diagnosticoApsOld.getDiagnostico());
			jn.setNeoplasiaMaligna(diagnosticoApsOld.getNeoplasiaMaligna() != null ? diagnosticoApsOld.getNeoplasiaMaligna().toString() : null);
			jn.setCriadoEm(diagnosticoApsOld.getCriadoEm());
			jn.setLuaLutSeq((diagnosticoApsOld.getAelTopografiaAparelhos()!=null)?diagnosticoApsOld.getAelTopografiaAparelhos().getId().getLutSeq():null);
			jn.setLuaSeqp((diagnosticoApsOld.getAelTopografiaAparelhos()!=null)?diagnosticoApsOld.getAelTopografiaAparelhos().getId().getSeqp():null);
			jn.setLueLugSeq((diagnosticoApsOld.getAelNomenclaturaEspecs()!=null)?diagnosticoApsOld.getAelNomenclaturaEspecs().getId().getLugSeq():null);
			jn.setLueSeqp((diagnosticoApsOld.getAelNomenclaturaEspecs()!=null)?diagnosticoApsOld.getAelNomenclaturaEspecs().getId().getSeqp():null);
			jn.setLuxSeq(diagnosticoApsOld.getAelExameAp().getSeq());
			jn.setSerMatricula(diagnosticoApsOld.getServidor().getId().getMatricula());
			jn.setSerVinCodigo(diagnosticoApsOld.getServidor().getId().getVinCodigo());
			jn.setBiopsia(diagnosticoApsOld.getBiopsia() != null ? diagnosticoApsOld.getBiopsia().toString() : null);
			getAelDiagnosticoApJnDAO().persistir(jn);
		}
	}

	private LaudoUnicoRN getLaudoUnicoRN() {
		return laudoUnicoRN;
	}

	private AelDiagnosticoApsDAO getAelDiagnosticoApsDAO() {
		return aelDiagnosticoApsDAO;
	}

	private AelDiagnosticoApJnDAO getAelDiagnosticoApJnDAO() {
		return aelDiagnosticoApJnDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
