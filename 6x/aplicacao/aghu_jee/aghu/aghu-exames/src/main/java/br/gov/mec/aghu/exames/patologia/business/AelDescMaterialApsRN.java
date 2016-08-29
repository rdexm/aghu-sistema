package br.gov.mec.aghu.exames.patologia.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelDescMaterialApsDAO;
import br.gov.mec.aghu.exames.dao.AelDescMaterialApsJnDAO;
import br.gov.mec.aghu.model.AelDescMaterialAps;
import br.gov.mec.aghu.model.AelDescMaterialApsJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class AelDescMaterialApsRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(AelDescMaterialApsRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
	return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private LaudoUnicoRN laudoUnicoRN;
	
	@Inject
	private AelDescMaterialApsDAO aelDescMaterialApsDAO;
	
	@Inject
	private AelDescMaterialApsJnDAO aelDescMaterialApsJnDAO;
	
	private static final long serialVersionUID = 4879378548867391256L;
	
	
	public void persistir(final AelDescMaterialAps aelDescMaterialAps) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		if (aelDescMaterialAps.getSeq() == null) {
			this.inserir(aelDescMaterialAps, servidorLogado);
			
		} else {
			final AelDescMaterialAps aelDescMaterialApsOld = getAelDescMaterialApsDAO().obterOriginal(aelDescMaterialAps.getSeq());
			this.atualizar(aelDescMaterialAps, aelDescMaterialApsOld, servidorLogado != null ? servidorLogado.getUsuario() : null);
		}
	}

	public void atualizar(final AelDescMaterialAps aelDescMaterialAps, final AelDescMaterialAps aelDescMaterialApsOld, String usuarioLogado) throws BaseException {
		this.executarAntesAtualizar(aelDescMaterialAps);
		this.aelDescMaterialApsDAO.merge(aelDescMaterialAps);
		this.executarAposAtualizar(aelDescMaterialAps, aelDescMaterialApsOld, usuarioLogado);
	}

	public void inserir(final AelDescMaterialAps aelDescMaterialAps, RapServidores servidorLogado) throws BaseException {
		this.executarAntesInserir(aelDescMaterialAps, servidorLogado);
		getAelDescMaterialApsDAO().persistir(aelDescMaterialAps);
	}
	
	private void executarAntesInserir(final AelDescMaterialAps aelDescMaterialAps, RapServidores servidorLogado) throws BaseException {
		aelDescMaterialAps.setCriadoEm(new Date());
		if(aelDescMaterialAps.getServidor() == null) {
			aelDescMaterialAps.setServidor(servidorLogado);
		}
		// Não permite alteração do registro qdo etapas_laudo = 'LA'
		getLaudoUnicoRN().rnLuopVerEtapLau(aelDescMaterialAps.getAelExameAps().getSeq());
		// O servidor deve estar na tab Patologistas com ind_sit = 'S'
		getLaudoUnicoRN().rnLuopVerServidor(aelDescMaterialAps.getServidor());
	}

	private void executarAntesAtualizar(final AelDescMaterialAps aelDescMaterialAps) throws BaseException {
		// Não permite inclusão do registro qdo etapas_laudo = 'LA'
		getLaudoUnicoRN().rnLuopVerEtapLau(aelDescMaterialAps.getAelExameAps().getSeq());
		// O servidor deve estar na tab Patologistas com ind_sit = 'S'
		getLaudoUnicoRN().rnLuopVerServidor(aelDescMaterialAps.getServidor());
	}
	
	private void executarAposAtualizar(final AelDescMaterialAps aelDescMaterialAps, final AelDescMaterialAps aelDescMaterialApsOld, String usuarioLogado) throws BaseException {
		if(CoreUtil.modificados(aelDescMaterialAps.getDescrMaterial(), aelDescMaterialApsOld.getDescrMaterial())) {
			AelDescMaterialApsJn jn = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.UPD, AelDescMaterialApsJn.class, usuarioLogado);
			jn.setSeq(aelDescMaterialApsOld.getSeq());
			jn.setDescrMaterial(aelDescMaterialApsOld.getDescrMaterial());
			jn.setCriadoEm(aelDescMaterialApsOld.getCriadoEm());
			if (aelDescMaterialApsOld.getAelExameAps() != null) {
				jn.setLuxSeq(aelDescMaterialApsOld.getAelExameAps().getSeq());
			}
			jn.setServidor(aelDescMaterialApsOld.getServidor());
			getAelDescMaterialApsJnDAO().persistir(jn);
		}
	}
	
	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}

	protected AelDescMaterialApsDAO getAelDescMaterialApsDAO() {
		return this.aelDescMaterialApsDAO;
	}
	
	protected LaudoUnicoRN getLaudoUnicoRN() {
		return this.laudoUnicoRN;
	}
	
	protected AelDescMaterialApsJnDAO getAelDescMaterialApsJnDAO() {
		return this.aelDescMaterialApsJnDAO;
	}
	
}
