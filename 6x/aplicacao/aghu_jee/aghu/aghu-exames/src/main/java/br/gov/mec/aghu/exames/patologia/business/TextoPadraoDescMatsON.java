package br.gov.mec.aghu.exames.patologia.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelGrpDescMatsLacunaDAO;
import br.gov.mec.aghu.exames.dao.AelGrpTxtDescMatsDAO;
import br.gov.mec.aghu.exames.dao.AelTextoDescMatsDAO;
import br.gov.mec.aghu.exames.dao.AelTxtDescMatsLacunaDAO;
import br.gov.mec.aghu.model.AelDescMatLacunas;
import br.gov.mec.aghu.model.AelGrpDescMatLacunas;
import br.gov.mec.aghu.model.AelGrpTxtDescMats;
import br.gov.mec.aghu.model.AelTxtDescMats;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class TextoPadraoDescMatsON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(TextoPadraoDescMatsON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
	return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private TextoPadraoDescMatsRN textoPadraoDescMatsRN;
	
	@Inject
	private AelTxtDescMatsLacunaDAO aelTxtDescMatsLacunaDAO;
	
	@Inject
	private AelGrpTxtDescMatsDAO aelGrpTxtDescMatsDAO;
	
	@Inject
	private AelGrpDescMatsLacunaDAO aelGrpDescMatsLacunaDAO;
	
	@Inject
	private AelTextoDescMatsDAO aelTextoDescMatsDAO;
	
	private static final long serialVersionUID = 4879378548867391256L;
	
	
	private enum TextoPadraoDescMatsCopiaONExceptionCode implements BusinessExceptionCode {
		MSG_ERRO_EXCLUIR_GRUPO_TEXTO_PADRAO_DESC_MATS_REGISTROS_DEPENDENTES, 
		MSG_ERRO_EXCLUIR_TEXTO_PADRAO_DESC_MATS_REGISTROS_DEPENDENTES, 
		MSG_ERRO_EXCLUIR_GRUPO_LACUNA_DESC_MATS_REGISTROS_DEPENDENTES;
	}

	public void persistirAelGrpTxtDescMats(final AelGrpTxtDescMats aelGrpTxtPadraoDescMatsNew)
			throws BaseException {

		if (aelGrpTxtPadraoDescMatsNew.getSeq() != null) {
			final AelGrpTxtDescMats aelGrpTxtPadraoDescMatsOld = getAelGrpTxtDescMatsDAO().obterOriginal(aelGrpTxtPadraoDescMatsNew);
			this.atualizarAelGrpTxtDescMats(aelGrpTxtPadraoDescMatsNew, aelGrpTxtPadraoDescMatsOld);
		} else {
			this.inserirAelGrpTxtDescMats(aelGrpTxtPadraoDescMatsNew);
		}
	}

	protected void inserirAelGrpTxtDescMats(AelGrpTxtDescMats aelGrpTxtPadraoDescMatsNew)
			throws ApplicationBusinessException {

		TextoPadraoDescMatsRN textoPadraoDescMatsscopiaRN = this.getTextoPadraoDescMatsRN();
		AelGrpTxtDescMatsDAO aelGrpTxtPadraoDescMatsDAO = this.getAelGrpTxtDescMatsDAO();

		textoPadraoDescMatsscopiaRN.executarAntesInserirAelGrpTxtDescMats(aelGrpTxtPadraoDescMatsNew);

		aelGrpTxtPadraoDescMatsDAO.persistir(aelGrpTxtPadraoDescMatsNew);

		aelGrpTxtPadraoDescMatsDAO.flush();

	}

	protected void atualizarAelGrpTxtDescMats(AelGrpTxtDescMats aelGrpTxtPadraoDescMatsNew, AelGrpTxtDescMats aelGrpTxtPadraoDescMatsOld) {

		TextoPadraoDescMatsRN textoPadraoDescMatsRN = this.getTextoPadraoDescMatsRN();
		AelGrpTxtDescMatsDAO aelGrpTxtPadraoDescMatsDAO = this.getAelGrpTxtDescMatsDAO();

		// textoPadraoDescMatsRN.executarAntesAtualizarAelGrpTxtDescMats(aelGrpTxtPadraoDescMatsNew,
		// aelGrpTxtPadraoDescMatsOld);

		aelGrpTxtPadraoDescMatsDAO.atualizar(aelGrpTxtPadraoDescMatsNew);

		textoPadraoDescMatsRN.executarAposAtualizarAelGrpTxtDescMats(aelGrpTxtPadraoDescMatsNew, aelGrpTxtPadraoDescMatsOld);

		aelGrpTxtPadraoDescMatsDAO.flush();

	}

	public void removerAelGrpTxtDescMats(AelGrpTxtDescMats aelGrpTxtPadraoDescMats) throws BaseException {

		TextoPadraoDescMatsRN textoPadraoDescMatsscopiaRN = this.getTextoPadraoDescMatsRN();
		AelGrpTxtDescMatsDAO aelGrpTxtPadraoDescMatsDAO = this.getAelGrpTxtDescMatsDAO();
		aelGrpTxtPadraoDescMats = aelGrpTxtPadraoDescMatsDAO.obterPorChavePrimaria(aelGrpTxtPadraoDescMats.getSeq());
		
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		AelTextoDescMatsDAO aelTextoPadraoDescMatsDAO = getAelTextoDescMatsDAO();
		List<AelTxtDescMats> listaAelTxtDescMats = aelTextoPadraoDescMatsDAO
				.pesquisarTextoPadraoDescMatsPorAelGrpTxtPadraoDescMats(aelGrpTxtPadraoDescMats.getSeq());

		if (!listaAelTxtDescMats.isEmpty()) {
			throw new ApplicationBusinessException(
					TextoPadraoDescMatsCopiaONExceptionCode.MSG_ERRO_EXCLUIR_GRUPO_TEXTO_PADRAO_DESC_MATS_REGISTROS_DEPENDENTES);
		}

		aelGrpTxtPadraoDescMatsDAO.remover(aelGrpTxtPadraoDescMats);

		textoPadraoDescMatsscopiaRN.executarAposExcluirAelGrpTxtDescMats(aelGrpTxtPadraoDescMats, servidorLogado.getUsuario());

		aelGrpTxtPadraoDescMatsDAO.flush();
	}

	// -----------------------

	public void persistirAelGrpDescMatLacunas(final AelGrpDescMatLacunas aelGrpDescMatsLacunaNew)
			throws BaseException {
		
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if (aelGrpDescMatsLacunaNew.getId().getSeqp() != null) {
			final AelGrpDescMatLacunas aelGrpDescMatsLacunaOld = getAelGrpDescMatsLacunaDAO().obterOriginal(aelGrpDescMatsLacunaNew);
			this.atualizarAelGrpDescMatLacunas(aelGrpDescMatsLacunaNew, aelGrpDescMatsLacunaOld,
					servidorLogado != null ? servidorLogado.getUsuario() : null);
		} else {
			aelGrpDescMatsLacunaNew.getId().setSeqp(
					getAelGrpDescMatsLacunaDAO().obterProximaSequence(aelGrpDescMatsLacunaNew.getId().getGtmSeq(),
							aelGrpDescMatsLacunaNew.getId().getLdaSeq()));
			this.inserirAelGrpDescMatLacunas(aelGrpDescMatsLacunaNew, servidorLogado);
		}
	}

	protected void inserirAelGrpDescMatLacunas(AelGrpDescMatLacunas aelGrpDescMatsLacunaNew, RapServidores servidorLogado)
			throws ApplicationBusinessException {

		TextoPadraoDescMatsRN textoPadraoDescMatsscopiaRN = this.getTextoPadraoDescMatsRN();
		AelGrpDescMatsLacunaDAO AelGrpDescMatsLacunaDAO = this.getAelGrpDescMatsLacunaDAO();

		textoPadraoDescMatsscopiaRN.executarAntesInserirAelGrpDescMatLacunas(aelGrpDescMatsLacunaNew, servidorLogado);

		AelGrpDescMatsLacunaDAO.persistir(aelGrpDescMatsLacunaNew);

		AelGrpDescMatsLacunaDAO.flush();

	}

	protected void atualizarAelGrpDescMatLacunas(AelGrpDescMatLacunas aelGrpDescMatsLacunaNew,
			AelGrpDescMatLacunas aelGrpDescMatsLacunaOld, String usuarioLogado) {

		TextoPadraoDescMatsRN textoPadraoDescMatsRN = this.getTextoPadraoDescMatsRN();
		AelGrpDescMatsLacunaDAO AelGrpDescMatsLacunaDAO = this.getAelGrpDescMatsLacunaDAO();

		//textoPadraoDescMatsRN.executarAntesAtualizarAelGrpDescMatLacunas(aelGrpDescMatsLacunaNew, aelGrpDescMatsLacunaOld);

		AelGrpDescMatsLacunaDAO.atualizar(aelGrpDescMatsLacunaNew);

		textoPadraoDescMatsRN.executarAposAtualizarAelGrpDescMatLacunas(aelGrpDescMatsLacunaNew, aelGrpDescMatsLacunaOld,
				usuarioLogado);

		AelGrpDescMatsLacunaDAO.flush();

	}

	public void removerAelGrpDescMatLacunas(AelGrpDescMatLacunas aelGrpDescMatsLacuna) throws BaseException {

		TextoPadraoDescMatsRN textoPadraoDescMatsscopiaRN = this.getTextoPadraoDescMatsRN();
		AelGrpDescMatsLacunaDAO aelGrpDescMatsLacunaDAO = this.getAelGrpDescMatsLacunaDAO();
		
		aelGrpDescMatsLacuna = aelGrpDescMatsLacunaDAO.obterPorChavePrimaria(aelGrpDescMatsLacuna.getId());

		AelTxtDescMatsLacunaDAO aelTxtDescMatsLacunaDAO = getAelTxtDescMatsLacunaDAO();

		List<AelDescMatLacunas> listaAelDescMatLacunas = aelTxtDescMatsLacunaDAO
				.pesquisarAelDescMatLacunasPorAelGrpDescMatsLacuna(aelGrpDescMatsLacuna, null);

		if (!listaAelDescMatLacunas.isEmpty()) {
			throw new ApplicationBusinessException(
					TextoPadraoDescMatsCopiaONExceptionCode.MSG_ERRO_EXCLUIR_GRUPO_LACUNA_DESC_MATS_REGISTROS_DEPENDENTES);
		}

		aelGrpDescMatsLacunaDAO.remover(aelGrpDescMatsLacuna);

		textoPadraoDescMatsscopiaRN.executarAposExcluirAelGrpDescMatLacunas(aelGrpDescMatsLacuna);

		aelGrpDescMatsLacunaDAO.flush();
	}

	// -----------------------

	public void persistirAelTxtDescMats(final AelTxtDescMats aelTextoPadraoDescMatsNew)
			throws BaseException {

		if (aelTextoPadraoDescMatsNew.getId().getSeqp() != null) {
			final AelTxtDescMats aelTextoPadraoDescMatsOld = getAelTextoDescMatsDAO().obterOriginal(aelTextoPadraoDescMatsNew);
			this.atualizarAelTxtDescMats(aelTextoPadraoDescMatsNew, aelTextoPadraoDescMatsOld);
		} else {
			aelTextoPadraoDescMatsNew.getId().setSeqp(
					getAelTextoDescMatsDAO().obterProximaSequence(aelTextoPadraoDescMatsNew.getId().getGtmSeq()));
			this.inserirAelTxtDescMats(aelTextoPadraoDescMatsNew);
		}
	}

	protected void inserirAelTxtDescMats(AelTxtDescMats aelTextoPadraoDescMatsNew)
			throws ApplicationBusinessException {

		TextoPadraoDescMatsRN textoPadraoDescMatsscopiaRN = this.getTextoPadraoDescMatsRN();
		AelTextoDescMatsDAO AelTextoDescMatsDAO = this.getAelTextoDescMatsDAO();

		textoPadraoDescMatsscopiaRN.executarAntesInserirAelTxtDescMats(aelTextoPadraoDescMatsNew);

		AelTextoDescMatsDAO.persistir(aelTextoPadraoDescMatsNew);

		AelTextoDescMatsDAO.flush();

	}

	protected void atualizarAelTxtDescMats(AelTxtDescMats aelTextoPadraoDescMatsNew, AelTxtDescMats aelTextoPadraoDescMatsOld) {

		TextoPadraoDescMatsRN textoPadraoDescMatsscopiaRN = this.getTextoPadraoDescMatsRN();
		AelTextoDescMatsDAO AelTextoDescMatsDAO = this.getAelTextoDescMatsDAO();

		textoPadraoDescMatsscopiaRN.executarAntesAtualizarAelTxtDescMats(aelTextoPadraoDescMatsNew, aelTextoPadraoDescMatsOld);

		AelTextoDescMatsDAO.atualizar(aelTextoPadraoDescMatsNew);

		textoPadraoDescMatsscopiaRN
				.executarAposAtualizarAelTxtDescMats(aelTextoPadraoDescMatsNew, aelTextoPadraoDescMatsOld);

		AelTextoDescMatsDAO.flush();

	}

	public void removerAelTxtDescMats(AelTxtDescMats aelTextoPadraoDescMats) throws BaseException {

		TextoPadraoDescMatsRN textoPadraoDescMatsscopiaRN = this.getTextoPadraoDescMatsRN();
		AelTextoDescMatsDAO aelTextoPadraoDescMatsDAO = getAelTextoDescMatsDAO();
		aelTextoPadraoDescMats = aelTextoPadraoDescMatsDAO.obterPorChavePrimaria(aelTextoPadraoDescMats.getId());

		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();						
		AelGrpDescMatsLacunaDAO aelGrpDescMatsLacunaDAO = getAelGrpDescMatsLacunaDAO();
		List<AelGrpDescMatLacunas> listaAelGrpDescMatLacunas = aelGrpDescMatsLacunaDAO.pesquisarAelGrpDescMatLacunasPorTextoPadraoDescMats(
				aelTextoPadraoDescMats.getId().getGtmSeq(), aelTextoPadraoDescMats.getId().getSeqp(), null);

		if (!listaAelGrpDescMatLacunas.isEmpty()) {
			throw new ApplicationBusinessException(
					TextoPadraoDescMatsCopiaONExceptionCode.MSG_ERRO_EXCLUIR_TEXTO_PADRAO_DESC_MATS_REGISTROS_DEPENDENTES);
		}
		aelTextoPadraoDescMatsDAO.remover(aelTextoPadraoDescMats);
		textoPadraoDescMatsscopiaRN.executarAposExcluirAelTxtDescMats(aelTextoPadraoDescMats, servidorLogado.getUsuario());
		aelTextoPadraoDescMatsDAO.flush();
	}


	// -----------------------

	public void persistirAelDescMatLacunas(final AelDescMatLacunas aelTxtDescMatsLacunaNew)
			throws BaseException {
		
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if (aelTxtDescMatsLacunaNew.getId().getSeqp() != null) {
			final AelDescMatLacunas aelTxtDescMatsLacunaOld = getAelTxtDescMatsLacunaDAO().obterOriginal(aelTxtDescMatsLacunaNew);
			this.atualizarAelDescMatLacunas(aelTxtDescMatsLacunaNew, aelTxtDescMatsLacunaOld,
					servidorLogado != null ? servidorLogado.getUsuario() : null);
		} else {
			aelTxtDescMatsLacunaNew.getId().setSeqp(
					getAelTxtDescMatsLacunaDAO().obterProximaSequence(aelTxtDescMatsLacunaNew.getId().getGtmSeq(),
							aelTxtDescMatsLacunaNew.getId().getLdaSeq(), aelTxtDescMatsLacunaNew.getId().getGmlSeq()));
			this.inserirAelDescMatLacunas(aelTxtDescMatsLacunaNew, servidorLogado);
		}
	}

	protected void inserirAelDescMatLacunas(AelDescMatLacunas aelTxtDescMatsLacunaNew, RapServidores servidorLogado)
			throws ApplicationBusinessException {

		TextoPadraoDescMatsRN textoPadraoDescMatsRN = this.getTextoPadraoDescMatsRN();
		AelTxtDescMatsLacunaDAO AelTxtDescMatsLacunaDAO = this.getAelTxtDescMatsLacunaDAO();

		textoPadraoDescMatsRN.executarAntesInserirAelTxtDescMatsLacuna(aelTxtDescMatsLacunaNew, servidorLogado);

		AelTxtDescMatsLacunaDAO.persistir(aelTxtDescMatsLacunaNew);

		AelTxtDescMatsLacunaDAO.flush();

	}

	protected void atualizarAelDescMatLacunas(AelDescMatLacunas aelTxtDescMatsLacunaNew, AelDescMatLacunas aelTxtDescMatsLacunaOld,
			String usuarioLogado) {

		TextoPadraoDescMatsRN textoPadraoDescMatsscopiaRN = this.getTextoPadraoDescMatsRN();
		AelTxtDescMatsLacunaDAO AelTxtDescMatsLacunaDAO = this.getAelTxtDescMatsLacunaDAO();

		//textoPadraoDescMatsscopiaRN.executarAntesAtualizarAelDescMatLacunas(aelTxtDescMatsLacunaNew, aelTxtDescMatsLacunaOld);

		AelTxtDescMatsLacunaDAO.atualizar(aelTxtDescMatsLacunaNew);

		textoPadraoDescMatsscopiaRN.executarAposAtualizarAelTxtDescMatsLacuna(aelTxtDescMatsLacunaNew, aelTxtDescMatsLacunaOld, usuarioLogado);

		AelTxtDescMatsLacunaDAO.flush();

	}

	public void removerAelDescMatLacunas(AelDescMatLacunas aelTxtDescMatsLacuna) throws BaseException {

		TextoPadraoDescMatsRN textoPadraoDescMatsRN = this.getTextoPadraoDescMatsRN();
		AelTxtDescMatsLacunaDAO aelTxtDescMatsLacunaDAO = this.getAelTxtDescMatsLacunaDAO();
		aelTxtDescMatsLacuna = aelTxtDescMatsLacunaDAO.obterPorChavePrimaria(aelTxtDescMatsLacuna.getId());
		aelTxtDescMatsLacunaDAO.remover(aelTxtDescMatsLacuna);

		textoPadraoDescMatsRN.executarAposExcluirAelTxtDescMatsLacuna(aelTxtDescMatsLacuna);

		aelTxtDescMatsLacunaDAO.flush();
	}
	
	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}

	protected AelTxtDescMatsLacunaDAO getAelTxtDescMatsLacunaDAO() {
		return this.aelTxtDescMatsLacunaDAO;
	}

	protected TextoPadraoDescMatsRN getTextoPadraoDescMatsRN() {
		return this.textoPadraoDescMatsRN;
	}
	
	protected AelGrpTxtDescMatsDAO getAelGrpTxtDescMatsDAO() {
		return aelGrpTxtDescMatsDAO;
	}
	
	protected AelGrpDescMatsLacunaDAO getAelGrpDescMatsLacunaDAO() {
		return aelGrpDescMatsLacunaDAO;
	}

	protected AelTextoDescMatsDAO getAelTextoDescMatsDAO() {
		return aelTextoDescMatsDAO;
	}
	
}
