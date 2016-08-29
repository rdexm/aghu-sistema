package br.gov.mec.aghu.exames.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelConfigMapaExameJnDAO;
import br.gov.mec.aghu.exames.dao.AelConfigMapaExamesDAO;
import br.gov.mec.aghu.model.AelConfigMapaExameJn;
import br.gov.mec.aghu.model.AelConfigMapaExames;
import br.gov.mec.aghu.model.AelConfigMapaExamesId;
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
public class AelConfigMapaExamesRN extends BaseBusiness {
	
	private static final Log LOG = LogFactory.getLog(AelConfigMapaExamesRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelConfigMapaExameJnDAO aelConfigMapaExameJnDAO;
	
	@Inject
	private AelConfigMapaExamesDAO aelConfigMapaExamesDAO;

	private static final long serialVersionUID = 262685070250962147L;
	
	public enum AelConfigMapaExamesRNExceptionCode implements BusinessExceptionCode {
		AEL_CONFIG_MAPA_EXAMES_DUPLICADO
	}

	/**
	 * ORADB AELT_CGE_BRI (INSERT)
	 */
	private void preInserir(AelConfigMapaExames aelConfigMapaExames) throws BaseException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		final AelConfigMapaExamesDAO dao = this.getAelConfigMapaExamesDAO();
		
		AelConfigMapaExames registroExistente = dao.obterPorChavePrimaria(aelConfigMapaExames.getId());
		if(registroExistente != null){
			throw new ApplicationBusinessException(AelConfigMapaExamesRNExceptionCode.AEL_CONFIG_MAPA_EXAMES_DUPLICADO);
		}
		
		aelConfigMapaExames.setCriadoEm(new Date());//RN1
		
		// Substitui aelk_ael_rn.rn_aelp_atu_servidor (:new.ser_matricula,:new.ser_vin_codigo);
		aelConfigMapaExames.setRapServidores(servidorLogado);//RN2
	}
	
	public void inserir(AelConfigMapaExames aelConfigMapaExames) throws BaseException{
		preInserir(aelConfigMapaExames);		
		getAelConfigMapaExamesDAO().persistir(aelConfigMapaExames);
	}
	
	/**
	 * Atualiza aelConfigMapaExames
	 */
	public void atualizar(AelConfigMapaExames aelConfigMapaExames) throws BaseException{
		
		// AELT_CGE_BRU
		// this.preAtualizar(aelConfigMapaExames);	// Na trigger efetua o calculo do version, não sendo necessário migrar.
		final AelConfigMapaExamesDAO dao = this.getAelConfigMapaExamesDAO();
		dao.atualizar(aelConfigMapaExames);
		this.posAtualizar(aelConfigMapaExames);
	} 
	
	/**
	 * ORADB: AELT_CGE_ARU
	 */
	private void posAtualizar(AelConfigMapaExames aelConfigMapaExames) throws ApplicationBusinessException {
		final AelConfigMapaExames original = getAelConfigMapaExamesDAO().obterOriginal(aelConfigMapaExames);
		
		if(CoreUtil.modificados(aelConfigMapaExames.getRapServidores(), original.getRapServidores()) ||
				CoreUtil.modificados(aelConfigMapaExames.getCriadoEm(), original.getCriadoEm()) ||
				CoreUtil.modificados(aelConfigMapaExames.getIndSituacao(), original.getIndSituacao()) 
		){
			createJournal(aelConfigMapaExames, DominioOperacoesJournal.UPD);
		}
	}

	/**
	 * ORADB AELT_CGE_ARD
	 */
	private void posRemover(AelConfigMapaExames aelConfigMapaExames) throws BaseException{
		createJournal(aelConfigMapaExames, DominioOperacoesJournal.DEL);
	}
	
	/**
	 * Atualiza AelConfigMapa
	 */
	public void remover(AelConfigMapaExamesId id) throws BaseException{
		AelConfigMapaExames aelConfigMapaExames = getAelConfigMapaExamesDAO().obterPorChavePrimaria(id);
		getAelConfigMapaExamesDAO().remover(aelConfigMapaExames);
		this.posRemover(aelConfigMapaExames);		
	}

	private void createJournal(final AelConfigMapaExames reg, final DominioOperacoesJournal operacao) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		final AelConfigMapaExameJn journal = BaseJournalFactory.getBaseJournal(operacao, AelConfigMapaExameJn.class, servidorLogado.getUsuario());
		
		journal.setAelUnfExecutaExames(reg.getAelUnfExecutaExames());
		journal.setConfigMapa(reg.getConfigMapa());
		
		journal.setRapServidores(reg.getRapServidores());
		journal.setCriadoEm(reg.getCriadoEm());
		journal.setIndSituacao(reg.getIndSituacao());
		
		getAelConfigMapaExameJnDAO().persistir(journal);
	}
	
	
	private AelConfigMapaExameJnDAO getAelConfigMapaExameJnDAO() {
		return aelConfigMapaExameJnDAO;
	}

	protected AelConfigMapaExamesDAO getAelConfigMapaExamesDAO() {
		return aelConfigMapaExamesDAO;
	}
	

	public void persistir(AelConfigMapaExames aelConfigMapaExames) throws BaseException {
		if(aelConfigMapaExames.getCriadoEm() != null){
			this.atualizar(aelConfigMapaExames);
		} else {
			this.inserir(aelConfigMapaExames);
		}
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}