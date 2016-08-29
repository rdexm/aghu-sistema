package br.gov.mec.aghu.exames.patologia.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelGrpDescMatsLacunaJnDAO;
import br.gov.mec.aghu.exames.dao.AelGrpTxtDescMatsJnDAO;
import br.gov.mec.aghu.exames.dao.AelTextoDescMatsJnDAO;
import br.gov.mec.aghu.exames.dao.AelTxtDescMatsLacunaJnDAO;
import br.gov.mec.aghu.model.AelDescMatLacunas;
import br.gov.mec.aghu.model.AelDescMatLacunasJn;
import br.gov.mec.aghu.model.AelGrpDescMatLacunas;
import br.gov.mec.aghu.model.AelGrpDescMatLacunasJn;
import br.gov.mec.aghu.model.AelGrpTxtDescMats;
import br.gov.mec.aghu.model.AelGrpTxtDescMatsJn;
import br.gov.mec.aghu.model.AelTxtDescMats;
import br.gov.mec.aghu.model.AelTxtDescMatsJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class TextoPadraoDescMatsRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(TextoPadraoDescMatsRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
	return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@Inject
	private AelGrpTxtDescMatsJnDAO aelGrpTxtDescMatsJnDAO;
	
	@Inject
	AelGrpDescMatsLacunaJnDAO aelGrpDescMatsLacunaJnDAO;
	
	@Inject
	AelTextoDescMatsJnDAO aelTextoDescMatsJnDAO;
	
	@Inject
	AelTxtDescMatsLacunaJnDAO aelTxtDescMatsLacunaJnDAO;
	
	private static final long serialVersionUID = 4879378548867391256L;
	
	
	public void executarAntesInserirAelGrpTxtDescMats(AelGrpTxtDescMats aelGrpTxtPadraoDescMatsNew)
			throws ApplicationBusinessException {

		aelGrpTxtPadraoDescMatsNew.setCriadoEm(new Date());
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		aelGrpTxtPadraoDescMatsNew.setServidor(servidorLogado);

	}



	public void executarAposAtualizarAelGrpTxtDescMats(AelGrpTxtDescMats aelGrpTxtPadraoDescMatsNew,
			AelGrpTxtDescMats aelGrpTxtPadraoDescMatsOld) {

		if (CoreUtil.modificados(aelGrpTxtPadraoDescMatsNew.getSeq(), aelGrpTxtPadraoDescMatsOld.getSeq())
				|| CoreUtil.modificados(aelGrpTxtPadraoDescMatsNew.getDescricao(), aelGrpTxtPadraoDescMatsOld.getDescricao())
				|| CoreUtil.modificados(aelGrpTxtPadraoDescMatsNew.getIndSituacao(), aelGrpTxtPadraoDescMatsOld.getIndSituacao())
				|| CoreUtil.modificados(aelGrpTxtPadraoDescMatsNew.getCriadoEm(), aelGrpTxtPadraoDescMatsOld.getCriadoEm())
				|| CoreUtil.modificados(aelGrpTxtPadraoDescMatsNew.getServidor(), aelGrpTxtPadraoDescMatsOld.getServidor())) {

			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			createJournal(aelGrpTxtPadraoDescMatsOld, DominioOperacoesJournal.UPD, servidorLogado.getUsuario());

		}
	}

	protected void createJournal(AelGrpTxtDescMats reg, DominioOperacoesJournal operacao, String usuarioLogado) {

		final AelGrpTxtDescMatsJn journal = BaseJournalFactory.getBaseJournal(operacao, AelGrpTxtDescMatsJn.class, usuarioLogado);

		journal.setSeq(reg.getSeq());
		journal.setDescricao(reg.getDescricao());
		journal.setIndSituacao(reg.getIndSituacao());
		journal.setCriadoEm(reg.getCriadoEm());
		journal.setServidor(reg.getServidor());

		this.getAelGrpTxtDescMatsJnDAO().persistir(journal);

	}

	public void executarAposExcluirAelGrpTxtDescMats(AelGrpTxtDescMats aelGrpTxtPadraoDescMats, String usuarioLogado) {

		createJournal(aelGrpTxtPadraoDescMats, DominioOperacoesJournal.DEL, usuarioLogado);

	}

	// -------------------------------------------------

	public void executarAntesInserirAelGrpDescMatLacunas(AelGrpDescMatLacunas aelGrpDescMatsLacunaNew, RapServidores servidorLogado)
			throws ApplicationBusinessException {

		aelGrpDescMatsLacunaNew.setCriadoEm(new Date());
		aelGrpDescMatsLacunaNew.setServidor(servidorLogado);

	}


	public void executarAposAtualizarAelGrpDescMatLacunas(AelGrpDescMatLacunas aelGrpDescMatsLacunaNew,
			AelGrpDescMatLacunas aelGrpDescMatsLacunaOld, String usuarioLogado) {

		if (CoreUtil.modificados(aelGrpDescMatsLacunaNew.getId().getGtmSeq(), aelGrpDescMatsLacunaOld.getId().getGtmSeq())
				|| CoreUtil.modificados(aelGrpDescMatsLacunaNew.getId().getLdaSeq(), aelGrpDescMatsLacunaOld.getId().getLdaSeq())
				|| CoreUtil.modificados(aelGrpDescMatsLacunaNew.getId().getSeqp(), aelGrpDescMatsLacunaOld.getId().getSeqp())
				|| CoreUtil.modificados(aelGrpDescMatsLacunaNew.getLacuna(), aelGrpDescMatsLacunaOld.getLacuna())
				|| CoreUtil.modificados(aelGrpDescMatsLacunaNew.getIndSituacao(), aelGrpDescMatsLacunaOld.getIndSituacao())
				|| CoreUtil.modificados(aelGrpDescMatsLacunaNew.getCriadoEm(), aelGrpDescMatsLacunaOld.getCriadoEm())
				|| CoreUtil.modificados(aelGrpDescMatsLacunaNew.getServidor(), aelGrpDescMatsLacunaOld.getServidor())) {

			createJournal(aelGrpDescMatsLacunaOld, DominioOperacoesJournal.UPD, usuarioLogado);
		}

	}

	protected void createJournal(AelGrpDescMatLacunas reg, DominioOperacoesJournal operacao, String usuarioLogado) {

		final AelGrpDescMatLacunasJn journal = BaseJournalFactory.getBaseJournal(operacao, AelGrpDescMatLacunasJn.class,
				usuarioLogado);

		journal.setGtmSeq(reg.getId().getGtmSeq());
		journal.setLdaSeq(reg.getId().getLdaSeq());
		journal.setSeqp(reg.getId().getSeqp());
		journal.setLacuna(reg.getLacuna());
		journal.setIndSituacao(reg.getIndSituacao());
		journal.setCriadoEm(reg.getCriadoEm());
		journal.setServidor(reg.getServidor());

		this.getAelGrpDescMatLacunasJnDAO().persistir(journal);
	}

	public void executarAposExcluirAelGrpDescMatLacunas(AelGrpDescMatLacunas aelGrpDescMatsLacuna) {
		
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		createJournal(aelGrpDescMatsLacuna, DominioOperacoesJournal.DEL, servidorLogado.getUsuario());

	}

	// -------------------------------------------------

	public void executarAntesInserirAelTxtDescMats(AelTxtDescMats aelTextoPadraoDescMatsNew)
			throws ApplicationBusinessException {

		aelTextoPadraoDescMatsNew.setCriadoEm(new Date());
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		aelTextoPadraoDescMatsNew.setServidor(servidorLogado);

	}

	public void executarAntesAtualizarAelTxtDescMats(AelTxtDescMats aelTextoPadraoDescMatsNew,
			AelTxtDescMats aelTextoPadraoDescMatsOld) {// NOPMD

		// Não foi migrado pq chama a procedure AELK_LUF_RN.RN_LUFP_VER_DESC que
		// tem um return na primeira linha

	}

	public void executarAposAtualizarAelTxtDescMats(AelTxtDescMats aelTextoPadraoDescMatsNew,
			AelTxtDescMats aelTextoPadraoDescMatsOld) {

		if (CoreUtil.modificados(aelTextoPadraoDescMatsNew.getId().getGtmSeq(), aelTextoPadraoDescMatsOld.getId().getGtmSeq())
				|| CoreUtil.modificados(aelTextoPadraoDescMatsNew.getId().getSeqp(), aelTextoPadraoDescMatsOld.getId().getSeqp())
				|| CoreUtil.modificados(aelTextoPadraoDescMatsNew.getDescricao(), aelTextoPadraoDescMatsOld.getDescricao())
				|| CoreUtil.modificados(aelTextoPadraoDescMatsNew.getIndSituacao(), aelTextoPadraoDescMatsOld.getIndSituacao())
				|| CoreUtil.modificados(aelTextoPadraoDescMatsNew.getCriadoEm(), aelTextoPadraoDescMatsOld.getCriadoEm())
				|| CoreUtil.modificados(aelTextoPadraoDescMatsNew.getServidor(), aelTextoPadraoDescMatsOld.getServidor())) {
			
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			createJournal(aelTextoPadraoDescMatsOld, DominioOperacoesJournal.UPD, servidorLogado.getUsuario());
		}
	}

	protected void createJournal(AelTxtDescMats reg, DominioOperacoesJournal operacao, String usuarioLogado) {

		final AelTxtDescMatsJn journal = BaseJournalFactory.getBaseJournal(operacao, AelTxtDescMatsJn.class,
				usuarioLogado);

		journal.setGtmSeq(reg.getId().getGtmSeq());
		journal.setSeqp(reg.getId().getSeqp());
		journal.setDescricao(reg.getDescricao());
		journal.setIndSituacao(reg.getIndSituacao());
		journal.setCriadoEm(reg.getCriadoEm());
		journal.setServidor(reg.getServidor());
		journal.setApelido(reg.getApelido());

		this.getAelTxtDescMatsJnDAO().persistir(journal);

	}

	public void executarAposExcluirAelTxtDescMats(AelTxtDescMats aelTextoPadraoDescMats, String usuarioLogado) {

		createJournal(aelTextoPadraoDescMats, DominioOperacoesJournal.DEL, usuarioLogado);

	}

	// -------------------------------------------------

	public void executarAntesInserirAelTxtDescMatsLacuna(AelDescMatLacunas aelTxtDescMatsLacunaNew, RapServidores servidorLogado)
			throws ApplicationBusinessException {

		aelTxtDescMatsLacunaNew.setCriadoEm(new Date());
		aelTxtDescMatsLacunaNew.setServidor(servidorLogado);

	}

	public void executarAposAtualizarAelTxtDescMatsLacuna(AelDescMatLacunas aelTxtDescMatsLacunaNew,
			AelDescMatLacunas aelTxtDescMatsLacunaOld, String usuarioLogado) {

		if (CoreUtil.modificados(aelTxtDescMatsLacunaNew.getId().getGtmSeq(), aelTxtDescMatsLacunaOld.getId().getGtmSeq())
				|| CoreUtil.modificados(aelTxtDescMatsLacunaNew.getId().getLdaSeq(), aelTxtDescMatsLacunaOld.getId().getLdaSeq())
				|| CoreUtil.modificados(aelTxtDescMatsLacunaNew.getId().getGmlSeq(), aelTxtDescMatsLacunaOld.getId().getGmlSeq())
				|| CoreUtil.modificados(aelTxtDescMatsLacunaNew.getId().getSeqp(), aelTxtDescMatsLacunaOld.getId().getSeqp())
				|| CoreUtil.modificados(aelTxtDescMatsLacunaNew.getTextoLacuna(), aelTxtDescMatsLacunaOld.getTextoLacuna())
				|| CoreUtil.modificados(aelTxtDescMatsLacunaNew.getIndSituacao(), aelTxtDescMatsLacunaOld.getIndSituacao())
				|| CoreUtil.modificados(aelTxtDescMatsLacunaNew.getCriadoEm(), aelTxtDescMatsLacunaOld.getCriadoEm())
				|| CoreUtil.modificados(aelTxtDescMatsLacunaNew.getServidor(), aelTxtDescMatsLacunaOld.getServidor())) {

			createJournal(aelTxtDescMatsLacunaOld, DominioOperacoesJournal.UPD, usuarioLogado);
		}

	}

	protected void createJournal(AelDescMatLacunas reg, DominioOperacoesJournal operacao, String usuarioLogado) {

		final AelDescMatLacunasJn journal = BaseJournalFactory.getBaseJournal(operacao, AelDescMatLacunasJn.class, usuarioLogado);

		journal.setGtmSeq(reg.getId().getGtmSeq());
		journal.setLdaSeq(reg.getId().getLdaSeq());
		journal.setGmlSeq(reg.getId().getGmlSeq());
		journal.setSeqp(reg.getId().getSeqp());
		journal.setTextoLacuna(reg.getTextoLacuna());
		journal.setIndSituacao(reg.getIndSituacao());
		journal.setCriadoEm(reg.getCriadoEm());
		journal.setServidor(reg.getServidor());

		this.getAelTxtDescMatsLacunaJnDAO().persistir(journal);

	}

	public void executarAposExcluirAelTxtDescMatsLacuna(AelDescMatLacunas aelTxtDescMatsLacuna) {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		createJournal(aelTxtDescMatsLacuna, DominioOperacoesJournal.DEL, servidorLogado.getUsuario());

	}
	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}

	protected AelGrpTxtDescMatsJnDAO getAelGrpTxtDescMatsJnDAO() {
		return this.aelGrpTxtDescMatsJnDAO;
	}

	protected AelGrpDescMatsLacunaJnDAO getAelGrpDescMatLacunasJnDAO() {
		return this.aelGrpDescMatsLacunaJnDAO;
	}

	protected AelTextoDescMatsJnDAO getAelTxtDescMatsJnDAO() {
		return this.aelTextoDescMatsJnDAO;
	}

	protected AelTxtDescMatsLacunaJnDAO getAelTxtDescMatsLacunaJnDAO() {
		return this.aelTxtDescMatsLacunaJnDAO;
	}
	
}
