package br.gov.mec.aghu.exames.patologia.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelGrpMacroLacunaJnDAO;
import br.gov.mec.aghu.exames.dao.AelGrpTxtPadraoMacroJnDAO;
import br.gov.mec.aghu.exames.dao.AelTextoPadraoMacroJnDAO;
import br.gov.mec.aghu.exames.dao.AelTxtMacroLacunaJnDAO;
import br.gov.mec.aghu.model.AelGrpMacroLacuna;
import br.gov.mec.aghu.model.AelGrpMacroLacunaJn;
import br.gov.mec.aghu.model.AelGrpTxtPadraoMacro;
import br.gov.mec.aghu.model.AelGrpTxtPadraoMacroJn;
import br.gov.mec.aghu.model.AelTextoPadraoMacro;
import br.gov.mec.aghu.model.AelTextoPadraoMacroJn;
import br.gov.mec.aghu.model.AelTxtMacroLacuna;
import br.gov.mec.aghu.model.AelTxtMacroLacunaJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class TextoPadraoMacroscopiaRN extends BaseBusiness {
	
	private static final Log LOG = LogFactory.getLog(TextoPadraoMacroscopiaRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelGrpMacroLacunaJnDAO aelGrpMacroLacunaJnDAO;
	
	@Inject
	private AelGrpTxtPadraoMacroJnDAO aelGrpTxtPadraoMacroJnDAO;
	
	@Inject
	private AelTxtMacroLacunaJnDAO aelTxtMacroLacunaJnDAO;
	
	@Inject
	private AelTextoPadraoMacroJnDAO aelTextoPadraoMacroJnDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 717111286572838887L;

	/**
	 * ORADB Trigger AELT_LUB_BRI
	 * 
	 * @param aelGrpTxtPadraoMacroNew
	 * @throws ApplicationBusinessException  
	 */
	public void executarAntesInserirAelGrpTxtPadraoMacro(
			AelGrpTxtPadraoMacro aelGrpTxtPadraoMacroNew) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		aelGrpTxtPadraoMacroNew.setCriadoEm(new Date());
		aelGrpTxtPadraoMacroNew.setServidor(servidorLogado);
		
	}
	
	/**
	 * ORADB Trigger AELT_LUB_BRU
	 * 
	 * @param aelGrpTxtPadraoMacroNew
	 * @param aelGrpTxtPadraoMacroOld
	 */
	public void executarAntesAtualizarAelGrpTxtPadraoMacro(
			AelGrpTxtPadraoMacro aelGrpTxtPadraoMacroNew, AelGrpTxtPadraoMacro aelGrpTxtPadraoMacroOld) {//NOPMD
		
		// Não foi migrado pq chama a procedure AELK_LUB_RN.RN_LUBP_VER_DESC que tem um return na primeira linha
		
	}	
	
	/**
	 * ORADB Trigger AELT_LUB_ARU
	 * 
	 * @param aelGrpTxtPadraoMacroNew
	 * @param aelGrpTxtPadraoMacroOld
	 * @throws ApplicationBusinessException 
	 */
	public void executarAposAtualizarAelGrpTxtPadraoMacro(
			AelGrpTxtPadraoMacro aelGrpTxtPadraoMacroNew, AelGrpTxtPadraoMacro aelGrpTxtPadraoMacroOld) throws ApplicationBusinessException {

		if (CoreUtil.modificados(aelGrpTxtPadraoMacroNew.getSeq(), aelGrpTxtPadraoMacroOld.getSeq())
				|| CoreUtil.modificados(aelGrpTxtPadraoMacroNew.getDescricao(), aelGrpTxtPadraoMacroOld.getDescricao())
				|| CoreUtil.modificados(aelGrpTxtPadraoMacroNew.getIndSituacao(), aelGrpTxtPadraoMacroOld.getIndSituacao())
				|| CoreUtil.modificados(aelGrpTxtPadraoMacroNew.getCriadoEm(), aelGrpTxtPadraoMacroOld.getCriadoEm())
				|| CoreUtil.modificados(aelGrpTxtPadraoMacroNew.getServidor(), aelGrpTxtPadraoMacroOld.getServidor())) {
			
			createJournal(aelGrpTxtPadraoMacroOld, DominioOperacoesJournal.UPD);
			
		}
	}

	protected void createJournal(AelGrpTxtPadraoMacro reg,
			DominioOperacoesJournal operacao) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		final AelGrpTxtPadraoMacroJn journal = BaseJournalFactory.getBaseJournal(operacao, AelGrpTxtPadraoMacroJn.class, servidorLogado.getUsuario());
		
		journal.setSeq(reg.getSeq());
		journal.setDescricao(reg.getDescricao());
		journal.setIndSituacao(reg.getIndSituacao());
		journal.setCriadoEm(reg.getCriadoEm());
		journal.setServidor(reg.getServidor());

		this.getAelGrpTxtPadraoMacroJnDAO().persistir(journal);
		
	}

	/**
	 * ORADB Trigger AELT_LUB_ARD
	 * 
	 * @param aelGrpTxtPadraoMacro
	 * @throws ApplicationBusinessException 
	 */
	public void executarAposExcluirAelGrpTxtPadraoMacro(
			AelGrpTxtPadraoMacro aelGrpTxtPadraoMacro) throws ApplicationBusinessException {

		createJournal(aelGrpTxtPadraoMacro, DominioOperacoesJournal.DEL);
		
	}		
	
	
	//-------------------------------------------------
	
	
	/**
	 * ORADB Trigger AELT_LO3_BRI
	 * 
	 * @param AelGrpMacroLacunaNew
	 * @throws ApplicationBusinessException  
	 */
	public void executarAntesInserirAelGrpMacroLacuna(
			AelGrpMacroLacuna aelGrpMacroLacunaNew) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		aelGrpMacroLacunaNew.setCriadoEm(new Date());
		aelGrpMacroLacunaNew.setServidor(servidorLogado);
		
	}
	
	/**
	 * ORADB Trigger AELT_LO3_BRU
	 * 
	 * @param AelGrpMacroLacunaNew
	 * @param AelGrpMacroLacunaOld
	 */
	public void executarAntesAtualizarAelGrpMacroLacuna(
			AelGrpMacroLacuna aelGrpMacroLacunaNew, AelGrpMacroLacuna aelGrpMacroLacunaOld) {//NOPMD

		// Não foi migrado pq chama a procedure AELK_LO3_RN.RN_LO3P_VER_DESC que tem um return na primeira linha
		
	}	
	
	/**
	 * ORADB Trigger AELT_LO3_ARU
	 */
	public void executarAposAtualizarAelGrpMacroLacuna(
			AelGrpMacroLacuna aelGrpMacroLacunaNew, AelGrpMacroLacuna aelGrpMacroLacunaOld) throws ApplicationBusinessException {

		if (CoreUtil.modificados(aelGrpMacroLacunaNew.getId().getLufLubSeq(), aelGrpMacroLacunaOld.getId().getLufLubSeq())
				|| CoreUtil.modificados(aelGrpMacroLacunaNew.getId().getLufSeqp(), aelGrpMacroLacunaOld.getId().getLufSeqp())
				|| CoreUtil.modificados(aelGrpMacroLacunaNew.getLacuna(), aelGrpMacroLacunaOld.getLacuna())
				|| CoreUtil.modificados(aelGrpMacroLacunaNew.getIndSituacao(), aelGrpMacroLacunaOld.getIndSituacao())
				|| CoreUtil.modificados(aelGrpMacroLacunaNew.getCriadoEm(), aelGrpMacroLacunaOld.getCriadoEm())
				|| CoreUtil.modificados(aelGrpMacroLacunaNew.getServidor(), aelGrpMacroLacunaOld.getServidor())) {
			
			createJournal(aelGrpMacroLacunaOld, DominioOperacoesJournal.UPD);
		}
			
	}

	protected void createJournal(AelGrpMacroLacuna reg,
			DominioOperacoesJournal operacao) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		final AelGrpMacroLacunaJn journal = BaseJournalFactory.getBaseJournal(operacao, AelGrpMacroLacunaJn.class, servidorLogado.getUsuario());
		
		journal.setLufLubSeq(reg.getId().getLufLubSeq());
		journal.setLufSeqp(reg.getId().getLufSeqp());
		journal.setSeqp(reg.getId().getSeqp());
		journal.setLacuna(reg.getLacuna());
		journal.setIndSituacao(reg.getIndSituacao());
		journal.setCriadoEm(reg.getCriadoEm());
		journal.setServidor(reg.getServidor());

		this.getAelGrpMacroLacunaJnDAO().persistir(journal);		
	}

	/**
	 * ORADB Trigger AELT_LO3_ARD
	 * 
	 * @param AelGrpMacroLacuna
	 * @throws ApplicationBusinessException 
	 */
	public void executarAposExcluirAelGrpMacroLacuna(
			AelGrpMacroLacuna aelGrpMacroLacuna) throws ApplicationBusinessException {

		createJournal(aelGrpMacroLacuna, DominioOperacoesJournal.DEL);
		
	}	
	

	
	//-------------------------------------------------
	
	
	/**
	 * ORADB Trigger AELT_LUF_BRI
	 * 
	 * @param AelTextoPadraoMacroNew
	 * @throws ApplicationBusinessException  
	 */
	public void executarAntesInserirAelTextoPadraoMacro(
			AelTextoPadraoMacro aelTextoPadraoMacroNew) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		aelTextoPadraoMacroNew.setCriadoEm(new Date());
		aelTextoPadraoMacroNew.setServidor(servidorLogado);
		
	}
	
	/**
	 * ORADB Trigger AELT_LUF_BRU
	 * 
	 * @param AelTextoPadraoMacroNew
	 * @param AelTextoPadraoMacroOld
	 */
	public void executarAntesAtualizarAelTextoPadraoMacro(
			AelTextoPadraoMacro aelTextoPadraoMacroNew, AelTextoPadraoMacro aelTextoPadraoMacroOld) {//NOPMD

		// Não foi migrado pq chama a procedure AELK_LUF_RN.RN_LUFP_VER_DESC que tem um return na primeira linha
		
	}	
	
	/**
	 * ORADB Trigger AELT_LUF_ARU
	 * 
	 * @param AelTextoPadraoMacroNew
	 * @param AelTextoPadraoMacroOld
	 * @throws ApplicationBusinessException 
	 */
	public void executarAposAtualizarAelTextoPadraoMacro(
			AelTextoPadraoMacro aelTextoPadraoMacroNew, AelTextoPadraoMacro aelTextoPadraoMacroOld) throws ApplicationBusinessException {

		if (CoreUtil.modificados(aelTextoPadraoMacroNew.getId().getLubSeq(), aelTextoPadraoMacroOld.getId().getLubSeq())
				|| CoreUtil.modificados(aelTextoPadraoMacroNew.getDescricao(), aelTextoPadraoMacroOld.getDescricao())
				|| CoreUtil.modificados(aelTextoPadraoMacroNew.getIndSituacao(), aelTextoPadraoMacroOld.getIndSituacao())
				|| CoreUtil.modificados(aelTextoPadraoMacroNew.getCriadoEm(), aelTextoPadraoMacroOld.getCriadoEm())
				|| CoreUtil.modificados(aelTextoPadraoMacroNew.getServidor(), aelTextoPadraoMacroOld.getServidor())) {
			
			createJournal(aelTextoPadraoMacroOld, DominioOperacoesJournal.UPD);
		}
	}

	protected void createJournal(AelTextoPadraoMacro reg,
			DominioOperacoesJournal operacao) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		final AelTextoPadraoMacroJn journal = BaseJournalFactory.getBaseJournal(operacao, AelTextoPadraoMacroJn.class, servidorLogado.getUsuario());
		
		journal.setLubSeq(reg.getId().getLubSeq());
		journal.setSeqp(reg.getId().getSeqp());
		journal.setDescricao(reg.getDescricao());
		journal.setIndSituacao(reg.getIndSituacao());
		journal.setCriadoEm(reg.getCriadoEm());
		journal.setServidor(reg.getServidor());
		journal.setApelido(reg.getApelido());

		this.getAelTextoPadraoMacroJnDAO().persistir(journal);
		
	}

	/**
	 * ORADB Trigger AELT_LUF_ARD
	 * 
	 * @param AelTextoPadraoMacro
	 * @throws ApplicationBusinessException 
	 */
	public void executarAposExcluirAelTextoPadraoMacro(
			AelTextoPadraoMacro aelTextoPadraoMacro) throws ApplicationBusinessException {

		createJournal(aelTextoPadraoMacro, DominioOperacoesJournal.DEL);
		
	}		
	

	//-------------------------------------------------
	
	
	/**
	 * ORADB Trigger AELT_LO4_BRI
	 * 
	 * @param AelTxtMacroLacunaNew
	 * @throws ApplicationBusinessException  
	 */
	public void executarAntesInserirAelTxtMacroLacuna(
			AelTxtMacroLacuna aelTxtMacroLacunaNew) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		aelTxtMacroLacunaNew.setCriadoEm(new Date());
		aelTxtMacroLacunaNew.setServidor(servidorLogado);
		
	}
	
	/**
	 * ORADB Trigger AELT_LO4_BRU
	 * 
	 * @param AelTxtMacroLacunaNew
	 * @param AelTxtMacroLacunaOld
	 */
	public void executarAntesAtualizarAelTxtMacroLacuna(
			AelTxtMacroLacuna aelTxtMacroLacunaNew, AelTxtMacroLacuna aelTxtMacroLacunaOld) {//NOPMD

		// Não foi migrado pq chama a procedure AELK_LO4_RN.RN_LO4P_VER_DESC que tem um return na primeira linha
		
	}	
	
	/**
	 * ORADB Trigger AELT_LO4_ARU
	 * 
	 * @param AelTxtMacroLacunaNew
	 * @param AelTxtMacroLacunaOld
	 * @throws ApplicationBusinessException 
	 */
	public void executarAposAtualizarAelTxtMacroLacuna(
			AelTxtMacroLacuna aelTxtMacroLacunaNew, AelTxtMacroLacuna aelTxtMacroLacunaOld) throws ApplicationBusinessException {

		if (CoreUtil.modificados(aelTxtMacroLacunaNew.getId().getLo3LufLubSeq(), aelTxtMacroLacunaOld.getId().getLo3LufLubSeq())
				|| CoreUtil.modificados(aelTxtMacroLacunaNew.getId().getLo3LufSeqp(), aelTxtMacroLacunaOld.getId().getLo3LufSeqp())
				|| CoreUtil.modificados(aelTxtMacroLacunaNew.getId().getSeqp(), aelTxtMacroLacunaOld.getId().getSeqp())
				|| CoreUtil.modificados(aelTxtMacroLacunaNew.getTextoLacuna(), aelTxtMacroLacunaOld.getTextoLacuna())
				|| CoreUtil.modificados(aelTxtMacroLacunaNew.getIndSituacao(), aelTxtMacroLacunaOld.getIndSituacao())
				|| CoreUtil.modificados(aelTxtMacroLacunaNew.getCriadoEm(), aelTxtMacroLacunaOld.getCriadoEm())
				|| CoreUtil.modificados(aelTxtMacroLacunaNew.getServidor(), aelTxtMacroLacunaOld.getServidor())) {
			
			createJournal(aelTxtMacroLacunaOld, DominioOperacoesJournal.UPD);
		}
			
	}

	protected void createJournal(AelTxtMacroLacuna reg,
			DominioOperacoesJournal operacao) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		final AelTxtMacroLacunaJn journal = BaseJournalFactory.getBaseJournal(operacao, AelTxtMacroLacunaJn.class, servidorLogado.getUsuario());
		
		journal.setLo3LufLubSeq(reg.getId().getLo3LufLubSeq());
		journal.setLo3LufSeqp(reg.getId().getLo3LufSeqp());
		journal.setLo3Seqp(reg.getId().getLo3Seqp());
		journal.setSeqp(reg.getId().getSeqp());
		journal.setTextoLacuna(reg.getTextoLacuna());
		journal.setIndSituacao(reg.getIndSituacao());
		journal.setCriadoEm(reg.getCriadoEm());
		journal.setServidor(reg.getServidor());

		this.getAelTxtMacroLacunaJnDAO().persistir(journal);
		
	}

	/**
	 * ORADB Trigger AELT_LO4_ARD
	 * 
	 * @param AelTxtMacroLacuna
	 * @throws ApplicationBusinessException 
	 */
	public void executarAposExcluirAelTxtMacroLacuna(
			AelTxtMacroLacuna aelTxtMacroLacuna) throws ApplicationBusinessException {
		createJournal(aelTxtMacroLacuna, DominioOperacoesJournal.DEL);
	}

	protected AelGrpTxtPadraoMacroJnDAO getAelGrpTxtPadraoMacroJnDAO() {
		return aelGrpTxtPadraoMacroJnDAO;
	}

	protected AelGrpMacroLacunaJnDAO getAelGrpMacroLacunaJnDAO() {
		return aelGrpMacroLacunaJnDAO;
	}

	protected AelTextoPadraoMacroJnDAO getAelTextoPadraoMacroJnDAO() {
		return aelTextoPadraoMacroJnDAO;
	}
	
	protected AelTxtMacroLacunaJnDAO getAelTxtMacroLacunaJnDAO() {
		return aelTxtMacroLacunaJnDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
