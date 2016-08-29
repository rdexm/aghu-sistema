package br.gov.mec.aghu.prescricaomedica.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.MpmTipoLaudo;
import br.gov.mec.aghu.model.MpmTipoLaudoJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.dao.MpmTipoLaudoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmTipoLaudoJnDAO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class TipoLaudoRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(TipoLaudoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private MpmTipoLaudoDAO mpmTipoLaudoDAO;
	
	@Inject
	private MpmTipoLaudoJnDAO mpmTipoLaudoJnDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 7539019746159545615L;

	private enum TipoLaudoRNExceptionCode implements BusinessExceptionCode {
		MPM_03595, RAP_00175;
	}

	public void inserirMpmTipoLaudo(MpmTipoLaudo tipoLaudo, boolean flush) throws BaseException {
		this.preInserirTipoLaudo(tipoLaudo);
		this.getMpmTipoLaudoDAO().persistir(tipoLaudo);
		if (flush){
			this.getMpmTipoLaudoDAO().flush();
		}
	}

	public void atualizarMpmTipoLaudo(MpmTipoLaudo tipoLaudo, boolean flush) throws BaseException {
		try {
			MpmTipoLaudoDAO mpmTipoLaudoDAO = this.getMpmTipoLaudoDAO();

			mpmTipoLaudoDAO.desatachar(tipoLaudo);
			MpmTipoLaudo oldTipoLaudo = mpmTipoLaudoDAO.obterPorChavePrimaria(tipoLaudo.getSeq());
			mpmTipoLaudoDAO.desatachar(oldTipoLaudo);

			this.preAtualizarTipoLaudo(tipoLaudo, oldTipoLaudo);
			mpmTipoLaudoDAO.atualizar(tipoLaudo);
			if (flush){
				mpmTipoLaudoDAO.flush();
			}
			this.posAtualizarTipoLaudo(tipoLaudo, oldTipoLaudo);
		} catch (BaseRuntimeException e) {
			throw new ApplicationBusinessException(e.getCode());
		}
	}

	/**
	 * ORADB MPMT_TUO_BRI
	 * 
	 * @param tipoLaudo
	 * @throws BaseException
	 */
	private void preInserirTipoLaudo(MpmTipoLaudo tipoLaudo) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		tipoLaudo.setCriadoEm(new Date());

		// Atualiza cartao ponto do servidor
		tipoLaudo.setServidor(servidorLogado);

		if (tipoLaudo.getServidor() == null) {
			throw new ApplicationBusinessException(TipoLaudoRNExceptionCode.RAP_00175);
		}
	}

	/**
	 * ORADB MPMT_TUO_BRU
	 * 
	 * @param tipoLaudo
	 * @param oldTipoLaudo
	 * @throws BaseException
	 */
	private void preAtualizarTipoLaudo(MpmTipoLaudo tipoLaudo, MpmTipoLaudo oldTipoLaudo) throws BaseException {
		this.verificaAlteracaoTipoLaudo(oldTipoLaudo.getDescricao(), tipoLaudo.getDescricao());
	}

	/**
	 * ORADB MPMT_TUO_ARU
	 * 
	 * @param newTipoLaudo
	 * @param oldTipoLaudo
	 */
	private void posAtualizarTipoLaudo(MpmTipoLaudo newTipoLaudo, MpmTipoLaudo oldTipoLaudo) {
		if (!CoreUtil.igual(newTipoLaudo.getSeq(), oldTipoLaudo.getSeq())
				|| !CoreUtil.igual(newTipoLaudo.getDescricao(), oldTipoLaudo.getDescricao())
				|| !CoreUtil.igual(newTipoLaudo.getTempoValidade(), oldTipoLaudo.getTempoValidade())
				|| !CoreUtil.igual(newTipoLaudo.getSituacao(), oldTipoLaudo.getSituacao())
				|| !CoreUtil.igual(newTipoLaudo.getInformaTempoTratamento(), oldTipoLaudo.getInformaTempoTratamento())
				|| !CoreUtil.igual(newTipoLaudo.getCriadoEm(), oldTipoLaudo.getCriadoEm())
				|| !CoreUtil.igual(newTipoLaudo.getServidor(), oldTipoLaudo.getServidor())
				|| !CoreUtil.igual(newTipoLaudo.getLaudoUnicoAtend(), oldTipoLaudo.getLaudoUnicoAtend())) {
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			
			MpmTipoLaudoJn tipoLaudoJn = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.UPD, MpmTipoLaudoJn.class, servidorLogado.getUsuario());
			tipoLaudoJn.setSeq(oldTipoLaudo.getSeq());
			tipoLaudoJn.setDescricao(oldTipoLaudo.getDescricao());
			tipoLaudoJn.setTempoValidade(oldTipoLaudo.getTempoValidade());
			tipoLaudoJn.setIndSituacao(oldTipoLaudo.getSituacao());
			tipoLaudoJn.setIndInformaTempoTrat(oldTipoLaudo.getInformaTempoTratamento());
			tipoLaudoJn.setCriadoEm(oldTipoLaudo.getCriadoEm());

			if (oldTipoLaudo.getServidor() != null) {
				tipoLaudoJn.setSerMatricula(oldTipoLaudo.getServidor().getId().getMatricula());
				tipoLaudoJn.setSerVinCodigo(oldTipoLaudo.getServidor().getId().getVinCodigo());
			}

			this.getMpmTipoLaudoJnDAO().persistir(tipoLaudoJn);
			this.getMpmTipoLaudoJnDAO().flush();
		}
	}

	/**
	 * ORADB RN_TUOP_VER_ALTERA
	 * 
	 * @param oldDescricao
	 * @param newDescricao
	 * @throws ApplicationBusinessException
	 */
	public void verificaAlteracaoTipoLaudo(String oldDescricao, String newDescricao) throws ApplicationBusinessException {
		if (oldDescricao != null && newDescricao != null && !oldDescricao.trim().equalsIgnoreCase(newDescricao.trim())) {
			throw new ApplicationBusinessException(TipoLaudoRNExceptionCode.MPM_03595);
		}
	}
	
	protected MpmTipoLaudoDAO getMpmTipoLaudoDAO() {
		return mpmTipoLaudoDAO;
	}

	protected MpmTipoLaudoJnDAO getMpmTipoLaudoJnDAO() {
		return mpmTipoLaudoJnDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
