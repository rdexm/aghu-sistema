package br.gov.mec.aghu.exames.patologia.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelTopografiaApJnDAO;
import br.gov.mec.aghu.exames.dao.AelTopografiaApsDAO;
import br.gov.mec.aghu.model.AelTopografiaApJn;
import br.gov.mec.aghu.model.AelTopografiaAps;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class AelTopografiaApsRN extends BaseBusiness {

	@EJB
	private LaudoUnicoRN laudoUnicoRN;
	
	private static final Log LOG = LogFactory.getLog(AelTopografiaApsRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelTopografiaApJnDAO aelTopografiaApJnDAO;
	
	@Inject
	private AelTopografiaApsDAO aelTopografiaApsDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 4532904490783765513L;

	public void persistir(final AelTopografiaAps topografiaAps) throws BaseException {
		if (topografiaAps.getSeq() == null) {
			this.inserir(topografiaAps);
		} else {
			AelTopografiaAps topografiaApsOld = getAelTopografiaApsDAO().obterOriginal(topografiaAps);
			this.atualizar(topografiaAps, topografiaApsOld);
		}
	}

	public void atualizar(final AelTopografiaAps topografiaAps, final AelTopografiaAps topografiaApsOld) throws BaseException {
		this.executarAntesAtualizar(topografiaAps);
		this.executarAposAtualizar(topografiaAps, topografiaApsOld);
	}

	public void inserir(final AelTopografiaAps topografiaAps) throws BaseException {
		this.executarAntesInserir(topografiaAps);
		getAelTopografiaApsDAO().persistir(topografiaAps);
	}
	
	public void excluir(final AelTopografiaAps topografiaAps) throws BaseException {
		this.getAelTopografiaApsDAO().remover(topografiaAps);
		this.executarAposExcluir(topografiaAps);
	}

	
	// @ORABD AELT_LO7_BRI
	private void executarAntesInserir(final AelTopografiaAps topografiaAps) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		topografiaAps.setCriadoEm(new Date());
		// aelk_ael_rn.rn_aelp_atu_servidor | Código equivalente
		topografiaAps.setServidor(servidorLogado);

		getLaudoUnicoRN().rnL07VerEtapLau(topografiaAps.getAelExameAp().getSeq());
		
		// O servidor deve estar na tab Patologistas com ind_sit = 'S'
		getLaudoUnicoRN().rnL07VerServidor(topografiaAps.getServidor());
	}

	// @ORABD AELT_LO7_BRU
	private void executarAntesAtualizar(final AelTopografiaAps topografiaAps) throws BaseException {

		// Não permite inclusão DO registro qdo etapas_laudo = 'LA'
		getLaudoUnicoRN().rnL07VerEtapLau(topografiaAps.getAelExameAp().getSeq());
		
		// O servidor deve estar na tab Patologistas com ind_sit = 'S'
		getLaudoUnicoRN().rnL07VerServidor(topografiaAps.getServidor());
	}

	// @ORABD AELT_LO7_ARU
	private void executarAposAtualizar(final AelTopografiaAps topografiaAps, final AelTopografiaAps topografiaApsOld) throws BaseException {
		if(CoreUtil.modificados(topografiaAps.getSeq(), topografiaApsOld.getSeq())
		||	CoreUtil.modificados(topografiaAps.getCriadoEm(), topografiaApsOld.getCriadoEm())
		||	CoreUtil.modificados(topografiaAps.getAelExameAp(), topografiaApsOld.getAelExameAp())
		||	CoreUtil.modificados(topografiaAps.getAelTopografiaAparelhos(), topografiaApsOld.getAelTopografiaAparelhos())
		||	CoreUtil.modificados(topografiaAps.getServidor(), topografiaApsOld.getServidor())	
		) {
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			
			AelTopografiaApJn jn = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.UPD, AelTopografiaApJn.class, servidorLogado.getUsuario());
			jn.setSeq(topografiaApsOld.getSeq());
			jn.setCriadoEm(topografiaApsOld.getCriadoEm());
			jn.setLuaLutSeq((topografiaApsOld.getAelTopografiaAparelhos()!=null)?topografiaApsOld.getAelTopografiaAparelhos().getId().getLutSeq():null);
			jn.setLuaSeqp((topografiaApsOld.getAelTopografiaAparelhos()!=null)?topografiaApsOld.getAelTopografiaAparelhos().getId().getSeqp():null);
			jn.setLuxSeq(topografiaApsOld.getAelExameAp().getSeq());
			jn.setSerMatricula(topografiaApsOld.getServidor().getId().getMatricula());
			jn.setSerVinCodigo(topografiaApsOld.getServidor().getId().getVinCodigo());
			getAelTopografiaApJnDAO().persistir(jn);
		}
	}

	// @ORABD AELT_LO7_ARD
	private void executarAposExcluir(final AelTopografiaAps topografiaApsOld) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		AelTopografiaApJn jn = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.DEL, AelTopografiaApJn.class, servidorLogado.getUsuario());
		jn.setSeq(topografiaApsOld.getSeq());
		jn.setCriadoEm(topografiaApsOld.getCriadoEm());
		jn.setLuaLutSeq((topografiaApsOld.getAelTopografiaAparelhos()!=null)?topografiaApsOld.getAelTopografiaAparelhos().getId().getLutSeq():null);
		jn.setLuaSeqp((topografiaApsOld.getAelTopografiaAparelhos()!=null)?topografiaApsOld.getAelTopografiaAparelhos().getId().getSeqp():null);
		jn.setLuxSeq(topografiaApsOld.getAelExameAp().getSeq());
		jn.setSerMatricula(topografiaApsOld.getServidor().getId().getMatricula());
		jn.setSerVinCodigo(topografiaApsOld.getServidor().getId().getVinCodigo());
		getAelTopografiaApJnDAO().persistir(jn);
	}

	private LaudoUnicoRN getLaudoUnicoRN() {
		return laudoUnicoRN;
	}

	private AelTopografiaApsDAO getAelTopografiaApsDAO() {
		return aelTopografiaApsDAO;
	}

	private AelTopografiaApJnDAO getAelTopografiaApJnDAO() {
		return aelTopografiaApJnDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}
