package br.gov.mec.aghu.exames.patologia.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelMacroscopiaApJnDAO;
import br.gov.mec.aghu.exames.dao.AelMacroscopiaApsDAO;
import br.gov.mec.aghu.model.AelMacroscopiaApJn;
import br.gov.mec.aghu.model.AelMacroscopiaAps;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class AelMacroscopiaApsRN extends BaseBusiness {

	@EJB
	private LaudoUnicoRN laudoUnicoRN;
	
	private static final Log LOG = LogFactory.getLog(AelMacroscopiaApsRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelMacroscopiaApJnDAO aelMacroscopiaApJnDAO;
	
	@Inject
	private AelMacroscopiaApsDAO aelMacroscopiaApsDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 4532904490783765513L;

	public void persistir(final AelMacroscopiaAps macroscopiaAps) throws BaseException {
		if (macroscopiaAps.getSeq() == null) {
			this.inserir(macroscopiaAps);
			
		} else {
			final AelMacroscopiaAps macroscopiaApsOld = getAelMacroscopiaApsDAO().obterOriginal(macroscopiaAps.getSeq());
			this.atualizar(macroscopiaAps, macroscopiaApsOld);
		}
	}

	public void atualizar(final AelMacroscopiaAps macroscopiaAps, final AelMacroscopiaAps macroscopiaApsOld) throws BaseException {
		this.executarAntesAtualizar(macroscopiaAps);
		this.getAelMacroscopiaApsDAO().merge(macroscopiaAps);
		this.executarAposAtualizar(macroscopiaAps, macroscopiaApsOld);
	}

	public void inserir(final AelMacroscopiaAps macroscopiaAps) throws BaseException {
		this.executarAntesInserir(macroscopiaAps);
		getAelMacroscopiaApsDAO().persistir(macroscopiaAps);
	}
	
	// @ORABD AELT_LUO_BRI
	private void executarAntesInserir(final AelMacroscopiaAps macroscopiaAps) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		macroscopiaAps.setCriadoEm(new Date());
		// aelk_ael_rn.rn_aelp_atu_servidor | Código equivalente
		if(macroscopiaAps.getServidor() == null) {
			macroscopiaAps.setServidor(servidorLogado);
		}
		getLaudoUnicoRN().rnLuopVerEtapLau(macroscopiaAps.getAelExameAps().getSeq());
		// O servidor deve estar na tab Patologistas com ind_sit = 'S'
		getLaudoUnicoRN().rnLuopVerServidor(macroscopiaAps.getServidor());
	}

	// @ORABD AELT_LUO_BRU
	private void executarAntesAtualizar(final AelMacroscopiaAps macroscopiaAps) throws BaseException {
		// Não permite inclusão DO registro qdo etapas_laudo = 'LA'
		getLaudoUnicoRN().rnLuopVerEtapLau(macroscopiaAps.getAelExameAps().getSeq());
		// O servidor deve estar na tab Patologistas com ind_sit = 'S'
		getLaudoUnicoRN().rnLuopVerServidor(macroscopiaAps.getServidor());
	}
	
	// @ORABD AELT_LUO_ARU
	private void executarAposAtualizar(final AelMacroscopiaAps macroscopiaAps, final AelMacroscopiaAps macroscopiaApsOld) throws BaseException {
		if(CoreUtil.modificados(macroscopiaAps.getMacroscopia(), macroscopiaApsOld.getMacroscopia())) {
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			
			AelMacroscopiaApJn jn = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.UPD, AelMacroscopiaApJn.class, servidorLogado.getUsuario());
			jn.setSeq(macroscopiaApsOld.getSeq());
			jn.setMacroscopia(macroscopiaApsOld.getMacroscopia());
			jn.setCriadoEm(macroscopiaApsOld.getCriadoEm());
			if (macroscopiaApsOld.getAelExameAps() != null) {
				jn.setLuxSeq(macroscopiaApsOld.getAelExameAps().getSeq());
			}
			jn.setSerMatricula(macroscopiaApsOld.getServidor().getId().getMatricula());
			jn.setSerVinCodigo(macroscopiaApsOld.getServidor().getId().getVinCodigo());
			getAelMacroscopiaApJnDAO().persistir(jn);
		}
	}

	private AelMacroscopiaApsDAO getAelMacroscopiaApsDAO() {
		return aelMacroscopiaApsDAO;
	}
	
	private LaudoUnicoRN getLaudoUnicoRN() {
		return laudoUnicoRN;
	}
	
	private AelMacroscopiaApJnDAO getAelMacroscopiaApJnDAO() {
		return aelMacroscopiaApJnDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
