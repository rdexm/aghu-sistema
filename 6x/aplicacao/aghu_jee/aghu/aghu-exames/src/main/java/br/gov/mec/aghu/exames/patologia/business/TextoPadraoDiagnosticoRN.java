package br.gov.mec.aghu.exames.patologia.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelGrpDiagLacunasJnDAO;
import br.gov.mec.aghu.exames.dao.AelGrpTxtPadraoDiagsJnDAO;
import br.gov.mec.aghu.exames.dao.AelTextoPadraoDiagsJnDAO;
import br.gov.mec.aghu.exames.dao.AelTxtDiagLacunasJnDAO;
import br.gov.mec.aghu.model.AelGrpDiagLacunas;
import br.gov.mec.aghu.model.AelGrpDiagLacunasJn;
import br.gov.mec.aghu.model.AelGrpTxtPadraoDiags;
import br.gov.mec.aghu.model.AelGrpTxtPadraoDiagsJn;
import br.gov.mec.aghu.model.AelTextoPadraoDiags;
import br.gov.mec.aghu.model.AelTextoPadraoDiagsJn;
import br.gov.mec.aghu.model.AelTxtDiagLacunas;
import br.gov.mec.aghu.model.AelTxtDiagLacunasJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class TextoPadraoDiagnosticoRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(TextoPadraoDiagnosticoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelGrpTxtPadraoDiagsJnDAO aelGrpTxtPadraoDiagsJnDAO;
	
	@Inject
	private AelTxtDiagLacunasJnDAO aelTxtDiagLacunasJnDAO;
	
	@Inject
	private AelTextoPadraoDiagsJnDAO aelTextoPadraoDiagsJnDAO;
	
	@Inject
	private AelGrpDiagLacunasJnDAO aelGrpDiagLacunasJnDAO;

	private static final long serialVersionUID = 1603500462151218843L;

	/**
	 * ORADB Trigger AELT_LUH_BRI
	 * 
	 * @param aelGrpTxtPadraoDiags
	 * @throws ApplicationBusinessException  
	 */
	public void executarAntesInserirAelGrpTxtPadraoDiags(final AelGrpTxtPadraoDiags aelGrpTxtPadraoDiags) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		aelGrpTxtPadraoDiags.setCriadoEm(new Date());
		aelGrpTxtPadraoDiags.setServidor(servidorLogado);
	}

	/**
	 * ORADB Trigger AELT_LUH_BRU
	 * 
	 * @param aelGrpTxtPadraoDiagsNew
	 * @param aelGrpTxtPadraoDiagsOld
	 */
//	public void executarAntesAtualizarAelGrpTxtPadraoDiags(final AelGrpTxtPadraoDiags aelGrpTxtPadraoDiagsNew,
//			final AelGrpTxtPadraoDiags aelGrpTxtPadraoDiagsOld) { 
//		// aelk_luh_rn.rn_luhp_ver_desc sem funcionalidade
//		return;
//	}

	/**
	 * ORADB Trigger AELT_LUH_ARU
	 */
	public void executarAposAtualizarAelGrpTxtPadraoDiags(final AelGrpTxtPadraoDiags aelGrpTxtPadraoDiagsNew,
			final AelGrpTxtPadraoDiags aelGrpTxtPadraoDiagsOld) throws ApplicationBusinessException {
		if (CoreUtil.modificados(aelGrpTxtPadraoDiagsNew.getSeq(), aelGrpTxtPadraoDiagsOld.getSeq())
				|| CoreUtil.modificados(aelGrpTxtPadraoDiagsNew.getDescricao(), aelGrpTxtPadraoDiagsOld.getDescricao())
				|| CoreUtil.modificados(aelGrpTxtPadraoDiagsNew.getIndSituacao(), aelGrpTxtPadraoDiagsOld.getIndSituacao())
				|| CoreUtil.modificados(aelGrpTxtPadraoDiagsNew.getCriadoEm(), aelGrpTxtPadraoDiagsOld.getCriadoEm())
				|| CoreUtil.modificados(aelGrpTxtPadraoDiagsNew.getServidor(), aelGrpTxtPadraoDiagsOld.getServidor())) {
			createJournal(aelGrpTxtPadraoDiagsOld, DominioOperacoesJournal.UPD);
		}
	}

	protected void createJournal(final AelGrpTxtPadraoDiags reg, final DominioOperacoesJournal operacao) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		final AelGrpTxtPadraoDiagsJn journal = BaseJournalFactory.getBaseJournal(operacao, AelGrpTxtPadraoDiagsJn.class, servidorLogado.getUsuario());

		journal.setSeq(reg.getSeq());
		journal.setDescricao(reg.getDescricao());
		journal.setIndSituacao(reg.getIndSituacao());
		journal.setCriadoEm(reg.getCriadoEm());
		journal.setServidor(reg.getServidor());

		this.getAelGrpTxtPadraoDiagsJnDAO().persistir(journal);
	}

	/**
	 * ORADB Trigger AELT_LUH_ARD
	 * 
	 * @param aelGrpTxtPadraoDiags
	 * @throws ApplicationBusinessException 
	 */
	public void executarAposExcluirAelGrpTxtPadraoDiags(final AelGrpTxtPadraoDiags aelGrpTxtPadraoDiags) throws ApplicationBusinessException {
		createJournal(aelGrpTxtPadraoDiags, DominioOperacoesJournal.DEL);
	}

	protected AelGrpTxtPadraoDiagsJnDAO getAelGrpTxtPadraoDiagsJnDAO() {
		return aelGrpTxtPadraoDiagsJnDAO;
	}

	// -------------------------------------------------

	/**
	 * ORADB Trigger AELT_LO1_BRI
	 * 
	 * @param aelGrpDiagLacunas
	 * @throws ApplicationBusinessException  
	 */
	public void executarAntesInserirAelGrpDiagLacunas(final AelGrpDiagLacunas aelGrpDiagLacunas) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		aelGrpDiagLacunas.setCriadoEm(new Date());
		aelGrpDiagLacunas.setServidor(servidorLogado);
	}

	/**
	 * ORADB Trigger AELT_LO1_BRU
	 * 
	 * @param aelGrpDiagLacunasNew
	 * @param aelGrpDiagLacunasOld
	 */
//	public void executarAntesAtualizarAelGrpDiagsLacuna(final AelGrpDiagLacunas aelGrpDiagLacunasNew, final AelGrpDiagLacunas aelGrpDiagLacunasOld) {
//		// AELK_LO1_rn.rn_LO1P_ver_desc sem funcionalidade
//		return;
//	}

	/**
	 * ORADB Trigger AELT_LO1_ARU
	 * 
	 * @param aelGrpDiagLacunasNew
	 * @param aelGrpDiagLacunasOld
	 * @throws ApplicationBusinessException 
	 */
	public void executarAposAtualizarAelGrpDiagLacunas(final AelGrpDiagLacunas aelGrpDiagLacunasNew, final AelGrpDiagLacunas aelGrpDiagLacunasOld) throws ApplicationBusinessException {
		if (CoreUtil.modificados(aelGrpDiagLacunasNew.getId().getLujLuhSeq(), aelGrpDiagLacunasOld.getId().getLujLuhSeq())
				|| CoreUtil.modificados(aelGrpDiagLacunasNew.getId().getLujSeqp(), aelGrpDiagLacunasOld.getId().getLujSeqp())
				|| CoreUtil.modificados(aelGrpDiagLacunasNew.getLacuna(), aelGrpDiagLacunasOld.getLacuna())
				|| CoreUtil.modificados(aelGrpDiagLacunasNew.getIndSituacao(), aelGrpDiagLacunasOld.getIndSituacao())
				|| CoreUtil.modificados(aelGrpDiagLacunasNew.getCriadoEm(), aelGrpDiagLacunasOld.getCriadoEm())
				|| CoreUtil.modificados(aelGrpDiagLacunasNew.getServidor(), aelGrpDiagLacunasOld.getServidor())) {

			createJournal(aelGrpDiagLacunasOld, DominioOperacoesJournal.UPD);
		}
	}

	protected void createJournal(final AelGrpDiagLacunas reg, final DominioOperacoesJournal operacao) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		final AelGrpDiagLacunasJn journal = BaseJournalFactory.getBaseJournal(operacao, AelGrpDiagLacunasJn.class, servidorLogado.getUsuario());

		journal.setLujLuhSeq(reg.getId().getLujLuhSeq());
		journal.setLujSeqp(reg.getId().getLujSeqp());
		journal.setSeqp(reg.getId().getSeqp());
		journal.setLacuna(reg.getLacuna());
		journal.setIndSituacao(reg.getIndSituacao());
		journal.setCriadoEm(reg.getCriadoEm());
		journal.setServidor(reg.getServidor());

		this.getAelGrpDiagLacunasJnDAO().persistir(journal);
	}

	/**
	 * ORADB Trigger AELT_LO1_ARD
	 * 
	 * @param aelGrpDiagLacunas
	 * @throws ApplicationBusinessException 
	 */
	public void executarAposExcluirAelGrpDiagLacunas(final AelGrpDiagLacunas aelGrpDiagLacunas) throws ApplicationBusinessException {
		createJournal(aelGrpDiagLacunas, DominioOperacoesJournal.DEL);
	}

	// -------------------------------------------------

	/**
	 * ORADB Trigger AELT_LUJ_BRI
	 * 
	 * @param aelTextoPadraoDiags
	 * @throws ApplicationBusinessException  
	 */
	public void executarAntesInserirAelTextoPadraoDiags(final AelTextoPadraoDiags aelTextoPadraoDiags) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		aelTextoPadraoDiags.setCriadoEm(new Date());
		aelTextoPadraoDiags.setServidor(servidorLogado);
	}

	/**
	 * ORADB Trigger AELT_LUF_BRU
	 * 
	 * @param aelTextoPadraoDiagsNew
	 * @param aelTextoPadraoDiagsOld
	 */
//	public void executarAntesAtualizarAelTextoPadraoDiags(final AelTextoPadraoDiags aelTextoPadraoDiagsNew,
//			final AelTextoPadraoDiags aelTextoPadraoDiagsOld) {
//		// aelk_luj_rn.rn_lujp_ver_desc sem funcionalidade
//		return;
//	}

	/**
	 * ORADB Trigger AELT_LUJ_ARU
	 * 
	 * @param aelTextoPadraoDiagsNew
	 * @param aelTextoPadraoDiagsOld
	 * @throws ApplicationBusinessException 
	 */
	public void executarAposAtualizarAelTextoPadraoDiags(final AelTextoPadraoDiags aelTextoPadraoDiagsNew,
			final AelTextoPadraoDiags aelTextoPadraoDiagsOld) throws ApplicationBusinessException {
		if (CoreUtil.modificados(aelTextoPadraoDiagsNew.getId().getLuhSeq(), aelTextoPadraoDiagsOld.getId().getLuhSeq())
				|| CoreUtil.modificados(aelTextoPadraoDiagsNew.getDescricao(), aelTextoPadraoDiagsOld.getDescricao())
				|| CoreUtil.modificados(aelTextoPadraoDiagsNew.getIndSituacao(), aelTextoPadraoDiagsOld.getIndSituacao())
				|| CoreUtil.modificados(aelTextoPadraoDiagsNew.getCriadoEm(), aelTextoPadraoDiagsOld.getCriadoEm())
				|| CoreUtil.modificados(aelTextoPadraoDiagsNew.getServidor(), aelTextoPadraoDiagsOld.getServidor())) {

			createJournal(aelTextoPadraoDiagsOld, DominioOperacoesJournal.UPD);
		}
	}

	protected void createJournal(final AelTextoPadraoDiags reg, final DominioOperacoesJournal operacao) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		final AelTextoPadraoDiagsJn journal = BaseJournalFactory.getBaseJournal(operacao, AelTextoPadraoDiagsJn.class, servidorLogado.getUsuario());

		journal.setLuhSeq(reg.getId().getLuhSeq());
		journal.setSeqp(reg.getId().getSeqp());
		journal.setDescricao(reg.getDescricao());
		journal.setIndSituacao(reg.getIndSituacao());
		journal.setCriadoEm(reg.getCriadoEm());
		journal.setServidor(reg.getServidor());
		journal.setApelido(reg.getApelido());

		this.getAelTextoPadraoDiagsJnDAO().persistir(journal);
	}

	/**
	 * ORADB Trigger AELT_LUJ_ARD
	 */
	public void executarAposExcluirAelTextoPadraoDiags(final AelTextoPadraoDiags aelTextoPadraoDiags) throws ApplicationBusinessException {
		createJournal(aelTextoPadraoDiags, DominioOperacoesJournal.DEL);
	}

	// -------------------------------------------------

	/**
	 * ORADB Trigger AELT_LO2_BRI
	 * 
	 * @param aelTxtDiagLacunasNew
	 * @throws ApplicationBusinessException  
	 */
	public void executarAntesInserirAelTxtDiagsLacuna(final AelTxtDiagLacunas aelTxtDiagLacunasNew) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		aelTxtDiagLacunasNew.setCriadoEm(new Date());
		aelTxtDiagLacunasNew.setServidor(servidorLogado);
	}

	/**
	 * ORADB Trigger AELT_LO2_BRU
	 * 
	 * @param aelTxtDiagLacunasNew
	 * @param aelTxtDiagLacunasOld
	 */
//	public void executarAntesAtualizarAelTxtDiagsLacuna(final AelTxtDiagLacunas aelTxtDiagLacunasNew, final AelTxtDiagLacunas aelTxtDiagLacunasOld) {
//		// aelk_lo2_rn.rn_lo2p_ver_desc sem funcionalidade
//		return;
//	}

	/**
	 * ORADB Trigger AELT_LO2_ARU
	 * 
	 * @param aelTxtDiagLacunasNew
	 * @param aelTxtDiagLacunasOld
	 * @throws ApplicationBusinessException 
	 */
	public void executarAposAtualizarAelTxtDiagsLacuna(final AelTxtDiagLacunas aelTxtDiagLacunasNew, final AelTxtDiagLacunas aelTxtDiagLacunasOld) throws ApplicationBusinessException {
		if (CoreUtil.modificados(aelTxtDiagLacunasNew.getId().getLo1LujLuhSeq(), aelTxtDiagLacunasOld.getId().getLo1LujLuhSeq())
				|| CoreUtil.modificados(aelTxtDiagLacunasNew.getId().getLo1LujSeqp(), aelTxtDiagLacunasOld.getId().getLo1LujSeqp())
				|| CoreUtil.modificados(aelTxtDiagLacunasNew.getId().getLo1Seqp(), aelTxtDiagLacunasOld.getId().getLo1Seqp())
				|| CoreUtil.modificados(aelTxtDiagLacunasNew.getId().getSeqp(), aelTxtDiagLacunasOld.getId().getSeqp())
				|| CoreUtil.modificados(aelTxtDiagLacunasNew.getTextoLacuna(), aelTxtDiagLacunasOld.getTextoLacuna())
				|| CoreUtil.modificados(aelTxtDiagLacunasNew.getIndSituacao(), aelTxtDiagLacunasOld.getIndSituacao())
				|| CoreUtil.modificados(aelTxtDiagLacunasNew.getCriadoEm(), aelTxtDiagLacunasOld.getCriadoEm())
				|| CoreUtil.modificados(aelTxtDiagLacunasNew.getServidor(), aelTxtDiagLacunasOld.getServidor())) {
			createJournal(aelTxtDiagLacunasOld, DominioOperacoesJournal.UPD);
		}
	}

	protected void createJournal(final AelTxtDiagLacunas reg, final DominioOperacoesJournal operacao) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		final AelTxtDiagLacunasJn journal = BaseJournalFactory.getBaseJournal(operacao, AelTxtDiagLacunasJn.class, servidorLogado.getUsuario());

		journal.setLo1LujLuhSeq(reg.getId().getLo1LujLuhSeq());
		journal.setLo1LujSeqp(reg.getId().getLo1LujSeqp());
		journal.setLo1Seqp(reg.getId().getLo1Seqp());
		journal.setSeqp(reg.getId().getSeqp());
		journal.setTextoLacuna(reg.getTextoLacuna());
		journal.setIndSituacao(reg.getIndSituacao());
		journal.setCriadoEm(reg.getCriadoEm());
		journal.setServidor(reg.getServidor());

		this.getAelTxtDiagLacunasJnDAO().persistir(journal);
	}

	/**
	 * ORADB Trigger AELT_LO2_ARD
	 * 
	 * @param aelTxtDiagLacunas
	 * @throws ApplicationBusinessException 
	 */
	public void executarAposExcluirAelTxtDiagsLacuna(final AelTxtDiagLacunas aelTxtDiagLacunas) throws ApplicationBusinessException {
		createJournal(aelTxtDiagLacunas, DominioOperacoesJournal.DEL);
	}

	protected AelGrpDiagLacunasJnDAO getAelGrpDiagLacunasJnDAO() {
		return aelGrpDiagLacunasJnDAO;
	}

	protected AelTextoPadraoDiagsJnDAO getAelTextoPadraoDiagsJnDAO() {
		return aelTextoPadraoDiagsJnDAO;
	}

	protected AelTxtDiagLacunasJnDAO getAelTxtDiagLacunasJnDAO() {
		return aelTxtDiagLacunasJnDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
