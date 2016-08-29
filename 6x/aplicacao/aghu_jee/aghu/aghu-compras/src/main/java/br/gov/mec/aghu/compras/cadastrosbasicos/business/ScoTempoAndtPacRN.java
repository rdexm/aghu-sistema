package br.gov.mec.aghu.compras.cadastrosbasicos.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.dao.ScoEtapaModPacDAO;
import br.gov.mec.aghu.compras.dao.ScoTempoAndtPacDAO;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoTempoAndtPac;
import br.gov.mec.aghu.model.ScoTemposAndtPacsId;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ScoTempoAndtPacRN extends BaseBusiness {
	
	private static final Log LOG = LogFactory.getLog(ScoTempoAndtPacRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private ScoTempoAndtPacDAO scoTempoAndtPacDAO;

	@Inject
	private ScoEtapaModPacDAO scoEtapaModPacDAO;
	
	private static final long serialVersionUID = -8765720654494687084L;

	public enum ScoTempoAndtPacRNExceptionCode implements
			BusinessExceptionCode { MENSAGEM_PARAM_OBRIG, MENSAGEM_TEMPO_LOC_PAC_DUPLICADO, MENSAGEM_LOCAL_POSSUI_ETAPAS; }

	public void persistir(ScoTempoAndtPac tempoLocalizacaoPac)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		/**
		 * @ORADB SCO_TEMPOS_ANDT_PACS - SCOT_TAP_BRI 
		 * @throws ApplicationBusinessException
		 */
		// RN2 - Servidor logado obrigatório
		if (tempoLocalizacaoPac == null || servidorLogado == null) {
			throw new ApplicationBusinessException(ScoTempoAndtPacRNExceptionCode.MENSAGEM_PARAM_OBRIG);
		}

		// RN1 - Verifica se existe algum registro com a mesma modalidade e localização
		if (this.getScoTempoAndtPacDAO().obterPorChavePrimaria(tempoLocalizacaoPac.getId()) != null){
			 throw new ApplicationBusinessException(ScoTempoAndtPacRNExceptionCode.MENSAGEM_TEMPO_LOC_PAC_DUPLICADO);
		}
		
		tempoLocalizacaoPac.setServidor(servidorLogado);
		this.getScoTempoAndtPacDAO().persistir(tempoLocalizacaoPac);
	}
	
	/**
	 * @ORADB SCO_TEMPOS_ANDT_PACS - SCOT_TAP_BRU 
	 */
	public void atualizar(ScoTempoAndtPac tempoLocalizacaoPac) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		// RN2 - Servidor logado obrigatório
		if (tempoLocalizacaoPac == null || servidorLogado == null) {
			throw new ApplicationBusinessException(ScoTempoAndtPacRNExceptionCode.MENSAGEM_PARAM_OBRIG);
		}
		
		tempoLocalizacaoPac.setServidor(servidorLogado);
		this.getScoTempoAndtPacDAO().merge(tempoLocalizacaoPac);
	}

	public void remover(ScoTemposAndtPacsId id) throws ApplicationBusinessException {
		if (scoEtapaModPacDAO.verificarLocalPacComEtapa(id)) {
			throw new ApplicationBusinessException(ScoTempoAndtPacRNExceptionCode.MENSAGEM_LOCAL_POSSUI_ETAPAS);
		}
		this.getScoTempoAndtPacDAO().removerPorId(id);
	}

	private ScoTempoAndtPacDAO getScoTempoAndtPacDAO() {
		return scoTempoAndtPacDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}