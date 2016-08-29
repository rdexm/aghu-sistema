package br.gov.mec.aghu.exames.patologia.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelNomenclaturaApJnDAO;
import br.gov.mec.aghu.exames.dao.AelNomenclaturaApsDAO;
import br.gov.mec.aghu.model.AelNomenclaturaApJn;
import br.gov.mec.aghu.model.AelNomenclaturaAps;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class AelNomenclaturaApsRN extends BaseBusiness {

	@EJB
	private LaudoUnicoRN laudoUnicoRN;
	
	private static final Log LOG = LogFactory.getLog(AelNomenclaturaApsRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelNomenclaturaApsDAO aelNomenclaturaApsDAO;
	
	@Inject
	private AelNomenclaturaApJnDAO aelNomenclaturaApJnDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 4532904490783765513L;

	public void persistir(final AelNomenclaturaAps nomenclaturaAps) throws BaseException {
		if (nomenclaturaAps.getSeq() == null) {
			this.inserir(nomenclaturaAps);
		} else {
			AelNomenclaturaAps nomenclaturaApsOld = getAelNomenclaturaApsDAO().obterOriginal(nomenclaturaAps);
			this.atualizar(nomenclaturaAps, nomenclaturaApsOld);
		}
	}

	public void atualizar(final AelNomenclaturaAps nomenclaturaAps, final AelNomenclaturaAps nomenclaturaApsOld) throws BaseException {
		this.executarAntesAtualizar(nomenclaturaAps);
		this.executarAposAtualizar(nomenclaturaAps, nomenclaturaApsOld);
	}

	public void inserir(final AelNomenclaturaAps nomenclaturaAps) throws BaseException {
		this.executarAntesInserir(nomenclaturaAps);
		getAelNomenclaturaApsDAO().persistir(nomenclaturaAps);
	}
	
	public void excluir(final AelNomenclaturaAps nomenclaturaAps) throws BaseException {
		this.getAelNomenclaturaApsDAO().remover(nomenclaturaAps);
		this.executarAposExcluir(nomenclaturaAps);
	}
	
	// @ORABD AELT_LO6_BRI
	private void executarAntesInserir(final AelNomenclaturaAps nomenclaturaAps) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		nomenclaturaAps.setCriadoEm(new Date());

		// aelk_ael_rn.rn_aelp_atu_servidor | Código equivalente
		nomenclaturaAps.setServidor(servidorLogado);

		getLaudoUnicoRN().rnL06VerEtapLau(nomenclaturaAps.getAelExameAp().getSeq());
		
		// O servidor deve estar na tab Patologistas com ind_sit = 'S'
		getLaudoUnicoRN().rnL06VerServidor(nomenclaturaAps.getServidor());
	}

	// @ORABD AELT_LO6_BRU
	private void executarAntesAtualizar(final AelNomenclaturaAps nomenclaturaAps) throws BaseException {
		// Não permite inclusão DO registro qdo etapas_laudo = 'LA'
		getLaudoUnicoRN().rnL06VerEtapLau(nomenclaturaAps.getAelExameAp().getSeq());
		
		// O servidor deve estar na tab Patologistas com ind_sit = 'S'
		getLaudoUnicoRN().rnL06VerServidor(nomenclaturaAps.getServidor());
	}

	// @ORABD AELT_LO6_ARU
	private void executarAposAtualizar(final AelNomenclaturaAps nomenclaturaAps, final AelNomenclaturaAps nomenclaturaApsOld) throws BaseException {
		if(CoreUtil.modificados(nomenclaturaAps.getSeq(), nomenclaturaApsOld.getSeq())
		||	CoreUtil.modificados(nomenclaturaAps.getCriadoEm(), nomenclaturaApsOld.getCriadoEm())
		||	CoreUtil.modificados(nomenclaturaAps.getAelExameAp(), nomenclaturaApsOld.getAelExameAp())
		||	CoreUtil.modificados(nomenclaturaAps.getAelNomenclaturaEspecs(), nomenclaturaApsOld.getAelNomenclaturaEspecs())
		||	CoreUtil.modificados(nomenclaturaAps.getServidor(), nomenclaturaApsOld.getServidor())	
		) {
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			
			AelNomenclaturaApJn jn = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.UPD, AelNomenclaturaApJn.class, servidorLogado.getUsuario());
			jn.setSeq(nomenclaturaApsOld.getSeq());
			jn.setCriadoEm(nomenclaturaApsOld.getCriadoEm());
			jn.setLuxSeq(nomenclaturaApsOld.getAelExameAp().getSeq());
			jn.setLueLugSeq((nomenclaturaApsOld.getAelNomenclaturaEspecs()!=null)?nomenclaturaApsOld.getAelNomenclaturaEspecs().getId().getLugSeq():null);
			jn.setLueSeqp((nomenclaturaApsOld.getAelNomenclaturaEspecs()!=null)?nomenclaturaApsOld.getAelNomenclaturaEspecs().getId().getSeqp():null);
			jn.setSerMatricula(nomenclaturaApsOld.getServidor().getId().getMatricula());
			jn.setSerVinCodigo(nomenclaturaApsOld.getServidor().getId().getVinCodigo());
			getAelNomenclaturaApJnDAO().persistir(jn);
		}
	}

	// @ORABD AELT_LO6_ARD
	private void executarAposExcluir(final AelNomenclaturaAps nomenclaturaApsOld) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		AelNomenclaturaApJn jn = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.DEL, AelNomenclaturaApJn.class, servidorLogado.getUsuario());
		jn.setSeq(nomenclaturaApsOld.getSeq());
		jn.setCriadoEm(nomenclaturaApsOld.getCriadoEm());
		jn.setLuxSeq(nomenclaturaApsOld.getAelExameAp().getSeq());
		jn.setLueLugSeq((nomenclaturaApsOld.getAelNomenclaturaEspecs()!=null)?nomenclaturaApsOld.getAelNomenclaturaEspecs().getId().getLugSeq():null);
		jn.setLueSeqp((nomenclaturaApsOld.getAelNomenclaturaEspecs()!=null)?nomenclaturaApsOld.getAelNomenclaturaEspecs().getId().getSeqp():null);
		jn.setSerMatricula(nomenclaturaApsOld.getServidor().getId().getMatricula());
		jn.setSerVinCodigo(nomenclaturaApsOld.getServidor().getId().getVinCodigo());
		getAelNomenclaturaApJnDAO().persistir(jn);
	}

	private LaudoUnicoRN getLaudoUnicoRN() {
		return laudoUnicoRN;
	}

	private AelNomenclaturaApsDAO getAelNomenclaturaApsDAO() {
		return aelNomenclaturaApsDAO;
	}

	private AelNomenclaturaApJnDAO getAelNomenclaturaApJnDAO() {
		return aelNomenclaturaApJnDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
