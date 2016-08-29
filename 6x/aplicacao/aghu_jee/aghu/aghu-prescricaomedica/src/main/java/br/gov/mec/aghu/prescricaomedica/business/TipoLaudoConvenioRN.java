package br.gov.mec.aghu.prescricaomedica.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.MpmTipoLaudoConvenio;
import br.gov.mec.aghu.model.MpmTipoLaudoConvenioJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.dao.MpmTipoLaudoConvenioDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmTipoLaudoConvenioJnDAO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class TipoLaudoConvenioRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(TipoLaudoConvenioRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private MpmTipoLaudoConvenioJnDAO mpmTipoLaudoConvenioJnDAO;
	
	@Inject
	private MpmTipoLaudoConvenioDAO mpmTipoLaudoConvenioDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1044168922780680543L;

	private enum TipoLaudoConvenioRNExceptionCode implements BusinessExceptionCode {
		RAP_00175;
	}

	public void inserirMpmTipoLaudoConvenio(MpmTipoLaudoConvenio tipoLaudoConvenio, boolean flush) throws BaseException {
		this.preInserirTipoLaudo(tipoLaudoConvenio);
		this.getMpmTipoLaudoConvenioDAO().persistir(tipoLaudoConvenio);
		if (flush){
			this.getMpmTipoLaudoConvenioDAO().flush();
		}
	}

	/**
	 * ORADB MPMT_TUC_BRI
	 * 
	 * @param tipoLaudoConvenio
	 * @throws BaseException
	 */
	private void preInserirTipoLaudo(MpmTipoLaudoConvenio tipoLaudoConvenio) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		tipoLaudoConvenio.setCriadoEm(new Date());

		// Atualiza cartao ponto do servidor
		tipoLaudoConvenio.setServidor(servidorLogado);

		if (tipoLaudoConvenio.getServidor() == null) {
			throw new ApplicationBusinessException(TipoLaudoConvenioRNExceptionCode.RAP_00175);
		}
	}
	
	public void removerMpmTipoLaudoConvenio(MpmTipoLaudoConvenio tipoLaudoConvenio, boolean flush) {
		this.getMpmTipoLaudoConvenioDAO().remover(tipoLaudoConvenio);
		this.getMpmTipoLaudoConvenioDAO().flush();
		
		this.posRemoverMpmTipoLaudoConvenio(tipoLaudoConvenio);
	}
	
	/**
	 * ORADB MPMT_TUC_ARD
	 * @param tipoLaudoConvenio
	 */
	private void posRemoverMpmTipoLaudoConvenio(MpmTipoLaudoConvenio tipoLaudoConvenio) {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		MpmTipoLaudoConvenioJn tipoLaudoConvenioJn = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.DEL, MpmTipoLaudoConvenioJn.class, servidorLogado.getUsuario());
		tipoLaudoConvenioJn.setCspCnvCodigo(tipoLaudoConvenio.getId().getCspCnvCodigo());
		tipoLaudoConvenioJn.setCspSeq(tipoLaudoConvenio.getId().getCspSeq());
		tipoLaudoConvenioJn.setTuoSeq(tipoLaudoConvenio.getId().getTuoSeq());
		tipoLaudoConvenioJn.setCriadoEm(tipoLaudoConvenio.getCriadoEm());
		
		if (tipoLaudoConvenio.getServidor() != null) {
			tipoLaudoConvenioJn.setSerMatricula(tipoLaudoConvenio.getServidor().getId().getMatricula());
			tipoLaudoConvenioJn.setSerVinCodigo(tipoLaudoConvenio.getServidor().getId().getVinCodigo());
		}

		this.getMpmTipoLaudoConvenioJnDAO().persistir(tipoLaudoConvenioJn);
		this.getMpmTipoLaudoConvenioJnDAO().flush();
	}

	private MpmTipoLaudoConvenioJnDAO getMpmTipoLaudoConvenioJnDAO() {
		return mpmTipoLaudoConvenioJnDAO;
	}

	protected MpmTipoLaudoConvenioDAO getMpmTipoLaudoConvenioDAO() {
		return mpmTipoLaudoConvenioDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
