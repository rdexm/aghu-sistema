package br.gov.mec.aghu.prescricaomedica.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.MpmTextoPadraoLaudo;
import br.gov.mec.aghu.model.MpmTextoPadraoLaudoJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.dao.MpmTextoPadraoLaudoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmTextoPadraoLaudoJnDAO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class TextoPadraoLaudoRN extends BaseBusiness {
	
	private static final Log LOG = LogFactory.getLog(TextoPadraoLaudoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private MpmTextoPadraoLaudoDAO mpmTextoPadraoLaudoDAO;
	
	@Inject
	private MpmTextoPadraoLaudoJnDAO mpmTextoPadraoLaudoJnDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 2102384453667163302L;

	private enum TextoPadraoLaudoRNExceptionCode implements BusinessExceptionCode {
		RAP_00175;
	}

	public void inserirMpmTextoPadraoLaudo(MpmTextoPadraoLaudo textoPadraoLaudo, boolean flush) throws BaseException {
		this.preInserirTextoPadraoLaudo(textoPadraoLaudo);
		this.getMpmTextoPadraoLaudoDAO().persistir(textoPadraoLaudo);
		if (flush){
			getMpmTextoPadraoLaudoDAO().flush();
		}
	}

	public void atualizarMpmTextoPadraoLaudo(MpmTextoPadraoLaudo textoPadraoLaudo, boolean flush) throws BaseException {
		MpmTextoPadraoLaudoDAO mpmTextoPadraoLaudoDAO = this.getMpmTextoPadraoLaudoDAO();

		mpmTextoPadraoLaudoDAO.desatachar(textoPadraoLaudo);
		MpmTextoPadraoLaudo oldTextoPadraoLaudo = mpmTextoPadraoLaudoDAO.obterPorChavePrimaria(textoPadraoLaudo.getSeq());
		mpmTextoPadraoLaudoDAO.desatachar(oldTextoPadraoLaudo);

		this.preAtualizarTextoPadraoLaudo(textoPadraoLaudo, oldTextoPadraoLaudo);
		mpmTextoPadraoLaudoDAO.atualizar(textoPadraoLaudo);
		if (flush){
			mpmTextoPadraoLaudoDAO.flush();
		}
		this.posAtualizarTextoPadraoLaudo(textoPadraoLaudo, oldTextoPadraoLaudo);
	}

	/**
	 * ORADB MPMT_XPL_BRI
	 * 
	 * @param textoPadraoLaudo
	 * @throws BaseException
	 */
	private void preInserirTextoPadraoLaudo(MpmTextoPadraoLaudo textoPadraoLaudo) throws BaseException {
		textoPadraoLaudo.setCriadoEm(new Date());

		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		// Atualiza cartao ponto do servidor
		textoPadraoLaudo.setServidor(servidorLogado);

		if (textoPadraoLaudo.getServidor() == null) {
			throw new ApplicationBusinessException(TextoPadraoLaudoRNExceptionCode.RAP_00175);
		}
	}

	/**
	 * ORADB MPMT_XPL_BRU
	 * 
	 * @param newTextoPadraoLaudo
	 * @param oldTextoPadraoLaudo
	 */
	private void preAtualizarTextoPadraoLaudo(MpmTextoPadraoLaudo newTextoPadraoLaudo, MpmTextoPadraoLaudo oldTextoPadraoLaudo) {
		// Este método está declarado, mas não fazia nada.
	}

	/**
	 * ORADB MPMT_XPL_ARU
	 * 
	 * @param newTextoPadraoLaudo
	 * @param oldTextoPadraoLaudo
	 */
	private void posAtualizarTextoPadraoLaudo(MpmTextoPadraoLaudo newTextoPadraoLaudo, MpmTextoPadraoLaudo oldTextoPadraoLaudo) {
		if (!CoreUtil.igual(newTextoPadraoLaudo.getSeq(), oldTextoPadraoLaudo.getSeq())
				|| !CoreUtil.igual(newTextoPadraoLaudo.getDescricao(), oldTextoPadraoLaudo.getDescricao())
				|| !CoreUtil.igual(newTextoPadraoLaudo.getIndSituacao(), oldTextoPadraoLaudo.getIndSituacao())
				|| !CoreUtil.igual(newTextoPadraoLaudo.getCriadoEm(), oldTextoPadraoLaudo.getCriadoEm())
				|| !CoreUtil.igual(newTextoPadraoLaudo.getServidor(), oldTextoPadraoLaudo.getServidor())) {
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			
			MpmTextoPadraoLaudoJn textoPadraoJn = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.UPD,
					MpmTextoPadraoLaudoJn.class, servidorLogado.getUsuario());
			textoPadraoJn.setSeq(oldTextoPadraoLaudo.getSeq());
			textoPadraoJn.setDescricao(oldTextoPadraoLaudo.getDescricao());
			textoPadraoJn.setIndSituacao(oldTextoPadraoLaudo.getIndSituacao());
			textoPadraoJn.setCriadoEm(oldTextoPadraoLaudo.getCriadoEm());

			if (oldTextoPadraoLaudo.getServidor() != null) {
				textoPadraoJn.setSerMatricula(oldTextoPadraoLaudo.getServidor().getId().getMatricula());
				textoPadraoJn.setSerVinCodigo(oldTextoPadraoLaudo.getServidor().getId().getVinCodigo());
			}

			this.getMpmTextoPadraoLaudoJnDAO().persistir(textoPadraoJn);
			this.getMpmTextoPadraoLaudoJnDAO().flush();
		}
	}

	protected MpmTextoPadraoLaudoDAO getMpmTextoPadraoLaudoDAO() {
		return mpmTextoPadraoLaudoDAO;
	}

	protected MpmTextoPadraoLaudoJnDAO getMpmTextoPadraoLaudoJnDAO() {
		return mpmTextoPadraoLaudoJnDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
