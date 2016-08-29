package br.gov.mec.aghu.compras.cadastrosbasicos.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoTempoAndtPac;
import br.gov.mec.aghu.model.ScoTemposAndtPacsId;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ScoTempoAndtPacON extends BaseBusiness {

	@EJB
	private ScoTempoAndtPacRN scoTempoAndtPacRN;
	
	private static final Log LOG = LogFactory.getLog(ScoTempoAndtPacON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	private static final long serialVersionUID = -4315028891741261842L;

	public enum ScoTempoAndtPacONExceptionCode implements
			BusinessExceptionCode { MENSAGEM_PARAM_OBRIG; }

	/**
	 * Inclui tempo localização PAC
	 * @param ScoTempoAndtPac
	 * @author dilceia.alves
	 * @since 08/02/2013
	 * @throws ApplicationBusinessException 
	 */
	public void inserirTempoAndtPac(ScoTempoAndtPac tempoLocalizacaoPac)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		if (tempoLocalizacaoPac == null || servidorLogado == null) {
			throw new ApplicationBusinessException(ScoTempoAndtPacONExceptionCode.MENSAGEM_PARAM_OBRIG);
		}
		
		this.getScoTempoAndtPacRN().persistir(tempoLocalizacaoPac);
	}
	
	/**
	 * Altera tempo localização PAC
	 * @param ScoTempoAndtPac
	 * @author dilceia.alves
	 * @since 08/02/2013
	 * @throws ApplicationBusinessException 
	 */
	public void alterarTempoAndtPac(ScoTempoAndtPac tempoLocalizacaoPac)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		if (tempoLocalizacaoPac == null || servidorLogado == null) {
			throw new ApplicationBusinessException(ScoTempoAndtPacONExceptionCode.MENSAGEM_PARAM_OBRIG);
		}
		
		this.getScoTempoAndtPacRN().atualizar(tempoLocalizacaoPac);
	}

	/**
	 * Exclui tempo localização PAC pela PK (mlc_codigo e lcp_codigo)
	 */
	public void excluirTempoAndtPac(ScoTemposAndtPacsId id) throws ApplicationBusinessException {
		this.getScoTempoAndtPacRN().remover(id);
	}

	protected ScoTempoAndtPacRN getScoTempoAndtPacRN(){
		return scoTempoAndtPacRN;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}