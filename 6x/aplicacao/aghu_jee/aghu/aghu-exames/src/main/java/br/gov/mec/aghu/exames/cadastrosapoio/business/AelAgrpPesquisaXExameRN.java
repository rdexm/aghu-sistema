package br.gov.mec.aghu.exames.cadastrosapoio.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelAgrpPesquisaXExameDAO;
import br.gov.mec.aghu.exames.dao.AelAgrpPesquisaXExameJnDAO;
import br.gov.mec.aghu.model.AelAgrpPesquisaXExame;
import br.gov.mec.aghu.model.AelAgrpPesquisaXExameJn;
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
public class AelAgrpPesquisaXExameRN extends BaseBusiness{

	private static final Log LOG = LogFactory.getLog(AelAgrpPesquisaXExameRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelAgrpPesquisaXExameJnDAO aelAgrpPesquisaXExameJnDAO;
	
	@Inject
	private AelAgrpPesquisaXExameDAO aelAgrpPesquisaXExameDAO; 

	private static final long serialVersionUID = 7811623261611687590L;

	public enum AelAgrpPesquisaXExameRNExceptionCode implements BusinessExceptionCode {
		AEL_AGRP_PESQUISA_X_EXAME_DUPLICADO ;
	}

	public void persistirAelAgrpPesquisaXExame(final AelAgrpPesquisaXExame aelAgrpPesquisaXExame) throws BaseException{
		if (aelAgrpPesquisaXExame.getSeq() == null) {
			this.inserir(aelAgrpPesquisaXExame);
		} else {
			this.atualizar(aelAgrpPesquisaXExame);
		}
	}
	
	/**
	 * ORADB TRIGGER AELT_AXE_BRI (INSERT)
	 */
	private void preInserir(final AelAgrpPesquisaXExame aelAgrpPesquisaXExame) throws BaseException{
		
		final AelAgrpPesquisaXExame reg = getAelAgrpPesquisaXExameDAO().
		 buscarAtivoPorUnfExecutaExameEAelAgrpPesquisas( aelAgrpPesquisaXExame.getAgrpPesquisa(),
														 aelAgrpPesquisaXExame.getUnfExecutaExame());
		
		if(reg != null){
			throw new ApplicationBusinessException(AelAgrpPesquisaXExameRNExceptionCode.AEL_AGRP_PESQUISA_X_EXAME_DUPLICADO);
		}
		
		aelAgrpPesquisaXExame.setCriadoEm(new Date());
		aelAgrpPesquisaXExame.setServidor(getServidorLogadoFacade().obterServidorLogado());
	}

	private void inserir(final AelAgrpPesquisaXExame aelAgrpPesquisaXExame) throws BaseException{
		this.preInserir(aelAgrpPesquisaXExame);
		this.getAelAgrpPesquisaXExameDAO().persistir(aelAgrpPesquisaXExame);
	}
	
	private void atualizar(final AelAgrpPesquisaXExame aelAgrpPesquisaXExame) throws BaseException{

		final AelAgrpPesquisaXExame original = getAelAgrpPesquisaXExameDAO().obterOriginal(aelAgrpPesquisaXExame);
		this.getAelAgrpPesquisaXExameDAO().merge(aelAgrpPesquisaXExame);
		this.posAtualizar(original, aelAgrpPesquisaXExame);
	}
	
	/**
	 * ORADB: AELT_AXE_ARU
	 */
	private void posAtualizar(final AelAgrpPesquisaXExame original, final AelAgrpPesquisaXExame alterado) throws ApplicationBusinessException {
		if(!CoreUtil.igual(original.getIndSituacao(), alterado.getIndSituacao()) ||
				!CoreUtil.igual(original.getUnfExecutaExame(), alterado.getUnfExecutaExame()) 
		){
			createJournal(alterado, DominioOperacoesJournal.UPD);
		}
	}
	
	private void createJournal(final AelAgrpPesquisaXExame reg, final DominioOperacoesJournal operacao) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		final AelAgrpPesquisaXExameJn journal = BaseJournalFactory.getBaseJournal(operacao, AelAgrpPesquisaXExameJn.class, servidorLogado.getUsuario());
		
		journal.setAgrpPesquisa(reg.getAgrpPesquisa());
		journal.setIndSituacao(reg.getIndSituacao());
		journal.setSeq(reg.getSeq());
		journal.setServidor(reg.getServidor()); 
		journal.setCriadoEm(reg.getCriadoEm()); 
		journal.setUnfExecutaExame(reg.getUnfExecutaExame());
		
		getAelAgrpPesquisaXExameJnDAO().persistir(journal);
	}

	private AelAgrpPesquisaXExameJnDAO getAelAgrpPesquisaXExameJnDAO(){
		return aelAgrpPesquisaXExameJnDAO;
	}
	
	private AelAgrpPesquisaXExameDAO getAelAgrpPesquisaXExameDAO() {
		return aelAgrpPesquisaXExameDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
