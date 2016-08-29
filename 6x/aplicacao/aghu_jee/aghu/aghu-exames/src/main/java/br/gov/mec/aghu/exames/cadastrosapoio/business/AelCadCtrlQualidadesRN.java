package br.gov.mec.aghu.exames.cadastrosapoio.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelCadCtrlQualidadesDAO;
import br.gov.mec.aghu.exames.dao.AelCadCtrlQualidadesJnDAO;
import br.gov.mec.aghu.model.AelCadCtrlQualidades;
import br.gov.mec.aghu.model.AelCadCtrlQualidadesJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class AelCadCtrlQualidadesRN extends BaseBusiness{

	private static final Log LOG = LogFactory.getLog(AelCadCtrlQualidadesRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelCadCtrlQualidadesJnDAO aelCadCtrlQualidadesJnDAO;
	
	@Inject
	private AelCadCtrlQualidadesDAO aelCadCtrlQualidadesDAO; 

	private static final long serialVersionUID = 3925694796757944477L;

	public void persistirAelCadCtrlQualidades(final AelCadCtrlQualidades AelCadCtrlQualidades) throws BaseException{
		if (AelCadCtrlQualidades.getSeq() == null) {
			this.inserir(AelCadCtrlQualidades);
		} else {
			this.atualizar(AelCadCtrlQualidades);
		}
	}
	
	/**
	 * ORADB TRIGGER AELT_CCQ_BRI (INSERT)
	 */
	private void preInserir(final AelCadCtrlQualidades aelCadCtrlQualidades) throws BaseException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		aelCadCtrlQualidades.setCriadoEm(new Date());

		// Substitui aelk_ael_rn.rn_aelp_atu_servidor (:new.ser_matricula,:new.ser_vin_codigo);
		aelCadCtrlQualidades.setServidor(servidorLogado);
	}

	private void inserir(final AelCadCtrlQualidades aelCadCtrlQualidades) throws BaseException{
		this.preInserir(aelCadCtrlQualidades);
		this.getAelCadCtrlQualidadesDAO().persistir(aelCadCtrlQualidades);
	}
	
	private void atualizar(final AelCadCtrlQualidades aelCadCtrlQualidades) throws BaseException{
		// AELT_CCQ_BRU Foi suprimida, pois não efetua nenhuma alteração
		final AelCadCtrlQualidades original = getAelCadCtrlQualidadesDAO().obterOriginal(aelCadCtrlQualidades);
		this.getAelCadCtrlQualidadesDAO().merge(aelCadCtrlQualidades);
		this.posAtualizar(original, aelCadCtrlQualidades);
	}
	
	/**
	 * ORADB: AELT_CCQ_ARU
	 */
	private void posAtualizar(final AelCadCtrlQualidades original, final AelCadCtrlQualidades alterado) throws ApplicationBusinessException{
		if(!CoreUtil.igual(original.getSeq(), alterado.getSeq()) ||
				!CoreUtil.igual(original.getMaterial(), alterado.getMaterial()) || 
				!CoreUtil.igual(original.getCriadoEm(), alterado.getCriadoEm()) ||
				!CoreUtil.igual(original.getConvenioSaudePlano(), alterado.getConvenioSaudePlano()) ||
				!CoreUtil.igual(original.getServidor(), alterado.getServidor())   
		){
			createJournal(alterado, DominioOperacoesJournal.UPD);
		}
	}
	
	public void remover(final Integer seq) throws BaseException{
		final AelCadCtrlQualidades aelCadCtrlQualidades = aelCadCtrlQualidadesDAO.obterPorChavePrimaria(seq);
		getAelCadCtrlQualidadesDAO().remover(aelCadCtrlQualidades);
		createJournal(aelCadCtrlQualidades, DominioOperacoesJournal.DEL);
	}
	
	private void createJournal(final AelCadCtrlQualidades reg, final DominioOperacoesJournal operacao) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		final AelCadCtrlQualidadesJn journal = BaseJournalFactory.getBaseJournal(operacao, AelCadCtrlQualidadesJn.class, servidorLogado.getUsuario());
		
		journal.setConvenioSaudePlano(reg.getConvenioSaudePlano());
		journal.setCriadoEm(reg.getCriadoEm());
		journal.setSeq(reg.getSeq());
		journal.setServidor(reg.getServidor());
		journal.setMaterial(reg.getMaterial());
		
		getAelCadCtrlQualidadesJnDAO().persistir(journal);
	}

	private AelCadCtrlQualidadesJnDAO getAelCadCtrlQualidadesJnDAO(){
		return aelCadCtrlQualidadesJnDAO;
	}
	
	private AelCadCtrlQualidadesDAO getAelCadCtrlQualidadesDAO() {
		return aelCadCtrlQualidadesDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}