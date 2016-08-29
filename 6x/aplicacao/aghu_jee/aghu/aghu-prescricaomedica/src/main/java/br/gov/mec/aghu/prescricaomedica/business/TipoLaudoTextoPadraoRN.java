package br.gov.mec.aghu.prescricaomedica.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.MpmTipoLaudoTextoPadrao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.dao.MpmTipoLaudoTextoPadraoDAO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class TipoLaudoTextoPadraoRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(TipoLaudoTextoPadraoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private MpmTipoLaudoTextoPadraoDAO mpmTipoLaudoTextoPadraoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 9040938738673122145L;

	private enum TipoLaudoTextoPadraoRNExceptionCode implements BusinessExceptionCode {
		RAP_00175;
	}

	public void inserirMpmTipoLaudoTextoPadrao(MpmTipoLaudoTextoPadrao tipoLaudoTextoPadrao, boolean flush) throws BaseException {
		this.preInserirTipoLaudoTextoPadrao(tipoLaudoTextoPadrao);
		this.getMpmTipoLaudoTextoPadraoDAO().persistir(tipoLaudoTextoPadrao);
		if (flush){
			this.getMpmTipoLaudoTextoPadraoDAO().flush();
		}
	}

	/**
	 * ORADB MPMT_TXT_BRI
	 * 
	 * @param tipoLaudoTextoPadrao
	 * @throws BaseException
	 */
	private void preInserirTipoLaudoTextoPadrao(MpmTipoLaudoTextoPadrao tipoLaudoTextoPadrao) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		tipoLaudoTextoPadrao.setCriadoEm(new Date());

		// Atualiza cartao ponto do servidor
		tipoLaudoTextoPadrao.setServidor(servidorLogado);

		if (tipoLaudoTextoPadrao.getServidor() == null) {
			throw new ApplicationBusinessException(TipoLaudoTextoPadraoRNExceptionCode.RAP_00175);
		}
	}
	
	public void removerMpmTipoLaudoTextoPadrao(MpmTipoLaudoTextoPadrao tipoLaudoTextoPadrao, boolean flush) {
		this.preRemoverMpmTipoLaudoTextoPadrao(tipoLaudoTextoPadrao);
		this.getMpmTipoLaudoTextoPadraoDAO().remover(tipoLaudoTextoPadrao);
		this.getMpmTipoLaudoTextoPadraoDAO().flush();
	}
	
	/**
	 * ORADB MPMT_TXT_BRU
	 * 
	 * @param tipoLaudoTextoPadrao
	 */
	private void preRemoverMpmTipoLaudoTextoPadrao(MpmTipoLaudoTextoPadrao tipoLaudoTextoPadrao) {
		// Não existe nada a ser migrado nesta função.
	}
	
	protected MpmTipoLaudoTextoPadraoDAO getMpmTipoLaudoTextoPadraoDAO() {
		return mpmTipoLaudoTextoPadraoDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
