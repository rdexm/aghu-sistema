package br.gov.mec.aghu.exames.cadastrosapoio.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelAgrpPesquisaJnDAO;
import br.gov.mec.aghu.exames.dao.AelAgrpPesquisasDAO;
import br.gov.mec.aghu.model.AelAgrpPesquisaJn;
import br.gov.mec.aghu.model.AelAgrpPesquisas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class AelAgrpPesquisasRN extends BaseBusiness{

	private static final Log LOG = LogFactory.getLog(AelAgrpPesquisasRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelAgrpPesquisasDAO aelAgrpPesquisasDAO;
	
	@Inject
	private AelAgrpPesquisaJnDAO aelAgrpPesquisaJnDAO; 

	private static final long serialVersionUID = -5266023404807211750L;

	public void persistirAelAgrpPesquisas(final AelAgrpPesquisas aelAgrpPesquisas) throws BaseException{
		if (aelAgrpPesquisas.getSeq() == null) {
			this.inserir(aelAgrpPesquisas);
		} else {
			this.atualizar(aelAgrpPesquisas);
		}
	}
	
	/**
	 * ORADB TRIGGER AELT_AXE_BRI (INSERT)
	 */
	private void preInserir(final AelAgrpPesquisas aelAgrpPesquisas) throws BaseException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		aelAgrpPesquisas.setCriadoEm(new Date());

		// Substitui aelk_ael_rn.rn_aelp_atu_servidor (:new.ser_matricula,:new.ser_vin_codigo);
		aelAgrpPesquisas.setRapServidor(servidorLogado);
	}

	private void inserir(final AelAgrpPesquisas aelAgrpPesquisas) throws BaseException{
		this.preInserir(aelAgrpPesquisas);
		this.getAelAgrpPesquisasDAO().persistir(aelAgrpPesquisas);
	}
	
	private void atualizar(final AelAgrpPesquisas aelAgrpPesquisas) throws BaseException{

		final AelAgrpPesquisas original = getAelAgrpPesquisasDAO().obterOriginal(aelAgrpPesquisas);
		this.getAelAgrpPesquisasDAO().merge(aelAgrpPesquisas);
		this.posAtualizar(original, aelAgrpPesquisas);
	}
	
	/**
	 * ORADB: AELT_AXE_ARU
	 * @throws ApplicationBusinessException 
	 */
	private void posAtualizar(final AelAgrpPesquisas original, final AelAgrpPesquisas alterado) throws ApplicationBusinessException{
		if(!CoreUtil.igual(original.getDescricao(), alterado.getDescricao()) ||
				!CoreUtil.igual(original.getIndSituacao(), alterado.getIndSituacao()) 
		){
			createJournal(alterado, DominioOperacoesJournal.UPD);
		}
	}
	
	private void createJournal(final AelAgrpPesquisas reg, final DominioOperacoesJournal operacao) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		final AelAgrpPesquisaJn journal = BaseJournalFactory.getBaseJournal(operacao, AelAgrpPesquisaJn.class, servidorLogado.getUsuario());
		
		journal.setDescricao(reg.getDescricao());
		journal.setIndSituacao(reg.getIndSituacao());
		journal.setSeq(reg.getSeq());
		journal.setRapServidor(reg.getRapServidor()); 
		journal.setCriadoEm(reg.getCriadoEm());
		
		getAelAgrpPesquisaJnDAO().persistir(journal);
	}

	private AelAgrpPesquisaJnDAO getAelAgrpPesquisaJnDAO(){
		return aelAgrpPesquisaJnDAO;
	}
	
	private AelAgrpPesquisasDAO getAelAgrpPesquisasDAO() {
		return aelAgrpPesquisasDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
