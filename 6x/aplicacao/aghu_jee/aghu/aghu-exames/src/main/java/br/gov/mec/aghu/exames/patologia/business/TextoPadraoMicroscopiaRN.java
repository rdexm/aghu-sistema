package br.gov.mec.aghu.exames.patologia.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelGrpMicroLacunaJnDAO;
import br.gov.mec.aghu.exames.dao.AelGrpTxtPadraoMicroJnDAO;
import br.gov.mec.aghu.exames.dao.AelTextoPadraoMicroJnDAO;
import br.gov.mec.aghu.exames.dao.AelTxtMicroLacunaJnDAO;
import br.gov.mec.aghu.model.AelGrpMicroLacuna;
import br.gov.mec.aghu.model.AelGrpMicroLacunaJn;
import br.gov.mec.aghu.model.AelGrpTxtPadraoMicro;
import br.gov.mec.aghu.model.AelGrpTxtPadraoMicroJn;
import br.gov.mec.aghu.model.AelTextoPadraoMicro;
import br.gov.mec.aghu.model.AelTextoPadraoMicroJn;
import br.gov.mec.aghu.model.AelTxtMicroLacuna;
import br.gov.mec.aghu.model.AelTxtMicroLacunaJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class TextoPadraoMicroscopiaRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(TextoPadraoMicroscopiaRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelTxtMicroLacunaJnDAO aelTxtMicroLacunaJnDAO;
	
	@Inject
	private AelGrpMicroLacunaJnDAO aelGrpMicroLacunaJnDAO;
	
	@Inject
	private AelGrpTxtPadraoMicroJnDAO aelGrpTxtPadraoMicroJnDAO;
	
	@Inject
	private AelTextoPadraoMicroJnDAO aelTextoPadraoMicroJnDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 717111286572838887L;

	/**
	 * ORADB Trigger AELT_LUU_BRI
	 * 
	 * @param aelGrpTxtPadraoMicroNew
	 * @throws ApplicationBusinessException  
	 */
	public void executarAntesInserirAelGrpTxtPadraoMicro(
			AelGrpTxtPadraoMicro aelGrpTxtPadraoMicroNew) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		aelGrpTxtPadraoMicroNew.setCriadoEm(new Date());
		aelGrpTxtPadraoMicroNew.setServidor(servidorLogado);
		
	}
	
	/**
	 * ORADB Trigger AELT_LUU_BRU
	 * 
	 * @param aelGrpTxtPadraoMicroNew
	 * @param aelGrpTxtPadraoMicroOld
	 */
	public void executarAntesAtualizarAelGrpTxtPadraoMicro(
			AelGrpTxtPadraoMicro aelGrpTxtPadraoMicroNew, AelGrpTxtPadraoMicro aelGrpTxtPadraoMicroOld) {//NOPMD
		
		// Não foi migrado pq chama a procedure AELK_LUU_RN.RN_LUUP_VER_DESC que tem um return na primeira linha
		
	}	
	
	/**
	 * ORADB Trigger AELT_LUU_ARU
	 * 
	 * @param aelGrpTxtPadraoMicroNew
	 * @param aelGrpTxtPadraoMicroOld
	 * @throws ApplicationBusinessException 
	 */
	public void executarAposAtualizarAelGrpTxtPadraoMicro(
			AelGrpTxtPadraoMicro aelGrpTxtPadraoMicroNew, AelGrpTxtPadraoMicro aelGrpTxtPadraoMicroOld) throws ApplicationBusinessException {

		if (CoreUtil.modificados(aelGrpTxtPadraoMicroNew.getSeq(), aelGrpTxtPadraoMicroOld.getSeq())
				|| CoreUtil.modificados(aelGrpTxtPadraoMicroNew.getDescricao(), aelGrpTxtPadraoMicroOld.getDescricao())
				|| CoreUtil.modificados(aelGrpTxtPadraoMicroNew.getIndSituacao(), aelGrpTxtPadraoMicroOld.getIndSituacao())
				|| CoreUtil.modificados(aelGrpTxtPadraoMicroNew.getCriadoEm(), aelGrpTxtPadraoMicroOld.getCriadoEm())
				|| CoreUtil.modificados(aelGrpTxtPadraoMicroNew.getServidor(), aelGrpTxtPadraoMicroOld.getServidor())) {
			
			createJournal(aelGrpTxtPadraoMicroOld, DominioOperacoesJournal.UPD);
			
		}
	}

	protected void createJournal(AelGrpTxtPadraoMicro reg,
			DominioOperacoesJournal operacao) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		final AelGrpTxtPadraoMicroJn journal = BaseJournalFactory.getBaseJournal(operacao, AelGrpTxtPadraoMicroJn.class, servidorLogado.getUsuario());
		
		journal.setSeq(reg.getSeq());
		journal.setDescricao(reg.getDescricao());
		journal.setIndSituacao(reg.getIndSituacao());
		journal.setCriadoEm(reg.getCriadoEm());
		journal.setServidor(reg.getServidor());

		this.getAelGrpTxtPadraoMicroJnDAO().persistir(journal);
		
	}

	/**
	 * ORADB Trigger AELT_LUU_ARD
	 * 
	 * @param aelGrpTxtPadraoMicro
	 * @throws ApplicationBusinessException 
	 */
	public void executarAposExcluirAelGrpTxtPadraoMicro(
			AelGrpTxtPadraoMicro aelGrpTxtPadraoMicro) throws ApplicationBusinessException {

		createJournal(aelGrpTxtPadraoMicro, DominioOperacoesJournal.DEL);
		
	}		
	
	
	//-------------------------------------------------
	
	
	/**
	 * ORADB Trigger AELT_LU9_BRI
	 * 
	 * @param AelGrpMicroLacunaNew
	 * @throws ApplicationBusinessException  
	 */
	public void executarAntesInserirAelGrpMicroLacuna(
			AelGrpMicroLacuna aelGrpMicroLacunaNew) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		aelGrpMicroLacunaNew.setCriadoEm(new Date());
		aelGrpMicroLacunaNew.setServidor(servidorLogado);
		
	}
	
	/**
	 * ORADB Trigger AELT_LU9_BRU
	 * 
	 * @param AelGrpMicroLacunaNew
	 * @param AelGrpMicroLacunaOld
	 */
	public void executarAntesAtualizarAelGrpMicroLacuna(
			AelGrpMicroLacuna aelGrpMicroLacunaNew, AelGrpMicroLacuna aelGrpMicroLacunaOld) {//NOPMD

		// Não foi migrado pq chama a procedure AELK_LU9_RN.RN_LU9P_VER_DESC que tem um return na primeira linha
		
	}	
	
	/**
	 * ORADB Trigger AELT_LU9_ARU
	 * 
	 * @param AelGrpMicroLacunaNew
	 * @param AelGrpMicroLacunaOld
	 * @throws ApplicationBusinessException 
	 */
	public void executarAposAtualizarAelGrpMicroLacuna(
			AelGrpMicroLacuna aelGrpMicroLacunaNew, AelGrpMicroLacuna aelGrpMicroLacunaOld) throws ApplicationBusinessException {

		if (CoreUtil.modificados(aelGrpMicroLacunaNew.getId().getLuvLuuSeq(), aelGrpMicroLacunaOld.getId().getLuvLuuSeq())
				|| CoreUtil.modificados(aelGrpMicroLacunaNew.getId().getLuvSeqp(), aelGrpMicroLacunaOld.getId().getLuvSeqp())
				|| CoreUtil.modificados(aelGrpMicroLacunaNew.getLacuna(), aelGrpMicroLacunaOld.getLacuna())
				|| CoreUtil.modificados(aelGrpMicroLacunaNew.getIndSituacao(), aelGrpMicroLacunaOld.getIndSituacao())
				|| CoreUtil.modificados(aelGrpMicroLacunaNew.getCriadoEm(), aelGrpMicroLacunaOld.getCriadoEm())
				|| CoreUtil.modificados(aelGrpMicroLacunaNew.getServidor(), aelGrpMicroLacunaOld.getServidor())) {
			
			createJournal(aelGrpMicroLacunaOld, DominioOperacoesJournal.UPD);
		}
			
	}

	protected void createJournal(AelGrpMicroLacuna reg,
			DominioOperacoesJournal operacao) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		final AelGrpMicroLacunaJn journal = BaseJournalFactory.getBaseJournal(operacao, AelGrpMicroLacunaJn.class, servidorLogado.getUsuario());
		
		journal.setLuvLuuSeq(reg.getId().getLuvLuuSeq());
		journal.setLuvSeqp(reg.getId().getLuvSeqp());
		journal.setSeqp(reg.getId().getSeqp());
		journal.setLacuna(reg.getLacuna());
		journal.setIndSituacao(reg.getIndSituacao());
		journal.setCriadoEm(reg.getCriadoEm());
		journal.setServidor(reg.getServidor());

		this.getAelGrpMicroLacunaJnDAO().persistir(journal);		
	}

	/**
	 * ORADB Trigger AELT_LU9_ARD
	 * 
	 * @param AelGrpMicroLacuna
	 * @throws ApplicationBusinessException 
	 */
	public void executarAposExcluirAelGrpMicroLacuna(
			AelGrpMicroLacuna aelGrpMicroLacuna) throws ApplicationBusinessException {

		createJournal(aelGrpMicroLacuna, DominioOperacoesJournal.DEL);
		
	}	
	

	
	//-------------------------------------------------
	
	
	/**
	 * ORADB Trigger AELT_LUV_BRI
	 * 
	 * @param AelTextoPadraoMicroNew
	 * @throws ApplicationBusinessException  
	 */
	public void executarAntesInserirAelTextoPadraoMicro(
			AelTextoPadraoMicro aelTextoPadraoMicroNew) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		aelTextoPadraoMicroNew.setCriadoEm(new Date());
		aelTextoPadraoMicroNew.setServidor(servidorLogado);
		
	}
	
	/**
	 * ORADB Trigger AELT_LUV_BRU
	 * 
	 * @param AelTextoPadraoMicroNew
	 * @param AelTextoPadraoMicroOld
	 */
	public void executarAntesAtualizarAelTextoPadraoMicro(
			AelTextoPadraoMicro aelTextoPadraoMicroNew, AelTextoPadraoMicro aelTextoPadraoMicroOld) {//NOPMD

		// Não foi migrado pq chama a procedure AELK_LUV_RN.RN_LUVP_VER_DESC que tem um return na primeira linha
		
	}	
	
	/**
	 * ORADB Trigger AELT_LUV_ARU
	 * 
	 * @param AelTextoPadraoMicroNew
	 * @param AelTextoPadraoMicroOld
	 * @throws ApplicationBusinessException 
	 */
	public void executarAposAtualizarAelTextoPadraoMicro(
			AelTextoPadraoMicro aelTextoPadraoMicroNew, AelTextoPadraoMicro aelTextoPadraoMicroOld) throws ApplicationBusinessException {

		if (CoreUtil.modificados(aelTextoPadraoMicroNew.getId().getLuuSeq(), aelTextoPadraoMicroOld.getId().getLuuSeq())
				|| CoreUtil.modificados(aelTextoPadraoMicroNew.getDescricao(), aelTextoPadraoMicroOld.getDescricao())
				|| CoreUtil.modificados(aelTextoPadraoMicroNew.getIndSituacao(), aelTextoPadraoMicroOld.getIndSituacao())
				|| CoreUtil.modificados(aelTextoPadraoMicroNew.getCriadoEm(), aelTextoPadraoMicroOld.getCriadoEm())
				|| CoreUtil.modificados(aelTextoPadraoMicroNew.getServidor(), aelTextoPadraoMicroOld.getServidor())) {
			
			createJournal(aelTextoPadraoMicroOld, DominioOperacoesJournal.UPD);
		}
	}

	protected void createJournal(AelTextoPadraoMicro reg,
			DominioOperacoesJournal operacao) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		final AelTextoPadraoMicroJn journal = BaseJournalFactory.getBaseJournal(operacao, AelTextoPadraoMicroJn.class, servidorLogado.getUsuario());
		
		journal.setLuuSeq(reg.getId().getLuuSeq());
		journal.setSeqp(reg.getId().getSeqp());
		journal.setDescricao(reg.getDescricao());
		journal.setIndSituacao(reg.getIndSituacao());
		journal.setCriadoEm(reg.getCriadoEm());
		journal.setServidor(reg.getServidor());
		journal.setApelido(reg.getApelido());

		this.getAelTextoPadraoMicroJnDAO().persistir(journal);
		
	}

	/**
	 * ORADB Trigger AELT_LUV_ARD
	 * 
	 * @param AelTextoPadraoMicro
	 * @throws ApplicationBusinessException 
	 */
	public void executarAposExcluirAelTextoPadraoMicro(
			AelTextoPadraoMicro aelTextoPadraoMicro) throws ApplicationBusinessException {

		createJournal(aelTextoPadraoMicro, DominioOperacoesJournal.DEL);
		
	}		
	

	//-------------------------------------------------
	
	
	/**
	 * ORADB Trigger AELT_LU0_BRI
	 * 
	 * @param AelTxtMicroLacunaNew
	 * @throws ApplicationBusinessException  
	 */
	public void executarAntesInserirAelTxtMicroLacuna(
			AelTxtMicroLacuna aelTxtMicroLacunaNew) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		aelTxtMicroLacunaNew.setCriadoEm(new Date());
		aelTxtMicroLacunaNew.setServidor(servidorLogado);
		
	}
	
	/**
	 * ORADB Trigger AELT_LU0_BRU
	 * 
	 * @param AelTxtMicroLacunaNew
	 * @param AelTxtMicroLacunaOld
	 */
	public void executarAntesAtualizarAelTxtMicroLacuna(
			AelTxtMicroLacuna aelTxtMicroLacunaNew, AelTxtMicroLacuna aelTxtMicroLacunaOld) {//NOPMD

		// Não foi migrado pq chama a procedure AELK_LU0_RN.RN_LU0P_VER_DESC que tem um return na primeira linha
		
	}	
	
	/**
	 * ORADB Trigger AELT_LU0_ARU
	 * 
	 * @param AelTxtMicroLacunaNew
	 * @param AelTxtMicroLacunaOld
	 * @throws ApplicationBusinessException 
	 */
	public void executarAposAtualizarAelTxtMicroLacuna(
			AelTxtMicroLacuna aelTxtMicroLacunaNew, AelTxtMicroLacuna aelTxtMicroLacunaOld) throws ApplicationBusinessException {

		if (CoreUtil.modificados(aelTxtMicroLacunaNew.getId().getLu9LuvLuuSeq(), aelTxtMicroLacunaOld.getId().getLu9LuvLuuSeq())
				|| CoreUtil.modificados(aelTxtMicroLacunaNew.getId().getLu9LuvSeqp(), aelTxtMicroLacunaOld.getId().getLu9LuvSeqp())
				|| CoreUtil.modificados(aelTxtMicroLacunaNew.getId().getSeqp(), aelTxtMicroLacunaOld.getId().getSeqp())
				|| CoreUtil.modificados(aelTxtMicroLacunaNew.getTextoLacuna(), aelTxtMicroLacunaOld.getTextoLacuna())
				|| CoreUtil.modificados(aelTxtMicroLacunaNew.getIndSituacao(), aelTxtMicroLacunaOld.getIndSituacao())
				|| CoreUtil.modificados(aelTxtMicroLacunaNew.getCriadoEm(), aelTxtMicroLacunaOld.getCriadoEm())
				|| CoreUtil.modificados(aelTxtMicroLacunaNew.getServidor(), aelTxtMicroLacunaOld.getServidor())) {
			
			createJournal(aelTxtMicroLacunaOld, DominioOperacoesJournal.UPD);
		}
			
	}

	protected void createJournal(AelTxtMicroLacuna reg,
			DominioOperacoesJournal operacao) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		final AelTxtMicroLacunaJn journal = BaseJournalFactory.getBaseJournal(operacao, AelTxtMicroLacunaJn.class, servidorLogado.getUsuario());
		
		journal.setLu9LuvLuuSeq(reg.getId().getLu9LuvLuuSeq());
		journal.setLu9LuvSeqp(reg.getId().getLu9LuvSeqp());
		journal.setLu9Seqp(reg.getId().getLu9Seqp());
		journal.setSeqp(reg.getId().getSeqp());
		journal.setTextoLacuna(reg.getTextoLacuna());
		journal.setIndSituacao(reg.getIndSituacao());
		journal.setCriadoEm(reg.getCriadoEm());
		journal.setServidor(reg.getServidor());

		this.getAelTxtMicroLacunaJnDAO().persistir(journal);
		
	}

	/**
	 * ORADB Trigger AELT_LU0_ARD
	 * 
	 * @param AelTxtMicroLacuna
	 * @throws ApplicationBusinessException 
	 */
	public void executarAposExcluirAelTxtMicroLacuna(
			AelTxtMicroLacuna aelTxtMicroLacuna) throws ApplicationBusinessException {
		
		createJournal(aelTxtMicroLacuna, DominioOperacoesJournal.DEL);
		
	}		

	protected AelGrpTxtPadraoMicroJnDAO getAelGrpTxtPadraoMicroJnDAO() {
		return aelGrpTxtPadraoMicroJnDAO;
	}

	protected AelGrpMicroLacunaJnDAO getAelGrpMicroLacunaJnDAO() {
		return aelGrpMicroLacunaJnDAO;
	}

	protected AelTextoPadraoMicroJnDAO getAelTextoPadraoMicroJnDAO() {
		return aelTextoPadraoMicroJnDAO;
	}
	
	protected AelTxtMicroLacunaJnDAO getAelTxtMicroLacunaJnDAO() {
		return aelTxtMicroLacunaJnDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
