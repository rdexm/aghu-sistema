package br.gov.mec.aghu.exames.patologia.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelNotaAdicionalApDAO;
import br.gov.mec.aghu.model.AelNotaAdicionalAp;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * A trigger de update AELT_LUD_BRU sempre lança exceção de que não pode alterar nota adicional
 * As triggers de delete AELT_LUD_BRD, AELT_LUD_ARD não foram migradas porque a tela não permite excluir nota adicional
 */
@Stateless
public class AelNotaAdicionalApRN extends BaseBusiness {

	@EJB
	private LaudoUnicoRN laudoUnicoRN;
	
	private static final Log LOG = LogFactory.getLog(AelNotaAdicionalApRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelNotaAdicionalApDAO aelNotaAdicionalApDAO;

	private static final long serialVersionUID = 4879378548867391256L;
	
	private enum AelNotaAdicionalApRNExceptionCode implements BusinessExceptionCode {
		  AEL_02690
	}
	
	public void persistirAelNotaAdicionalAp(final AelNotaAdicionalAp aelNotaAdicionalAp) throws BaseException {
		if (aelNotaAdicionalAp.getSeq() == null) {
			inserirAelNotaAdicionalAp(aelNotaAdicionalAp);
		}
		else {
			AelNotaAdicionalAp aelNotaAdicionalApOld = getAelNotaAdicionalApDAO().obterOriginal(aelNotaAdicionalAp.getSeq());
			atualizarAelNotaAdicionalAp(aelNotaAdicionalAp, aelNotaAdicionalApOld);
		}
	}
	
	protected void inserirAelNotaAdicionalAp(final AelNotaAdicionalAp aelNotaAdicionalAp) throws BaseException {
		final AelNotaAdicionalApDAO dao = getAelNotaAdicionalApDAO();
		this.executarAntesInserirAelNotaAdicionalAp(aelNotaAdicionalAp);
		dao.persistir(aelNotaAdicionalAp);
	}
	
	protected void atualizarAelNotaAdicionalAp(final AelNotaAdicionalAp aelNotaAdicionalApNew, final AelNotaAdicionalAp aelNotaAdicionalApOld) throws BaseException {
		final AelNotaAdicionalApDAO dao = getAelNotaAdicionalApDAO();
		this.executarAntesAtualizarAelNotaAdicionalAp(aelNotaAdicionalApNew,aelNotaAdicionalApOld);
		dao.atualizar(aelNotaAdicionalApNew);
	}

	/**
	 * ORADB Trigger AELT_LUD_BRI
	 * 
	 * @param aelNotaAdicionalAp
	 * @throws ApplicationBusinessException  
	 */
	protected void executarAntesInserirAelNotaAdicionalAp(AelNotaAdicionalAp aelNotaAdicionalAp) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		LaudoUnicoRN laudoUnicoRN = getLaudoUnicoRN();
		
		aelNotaAdicionalAp.setCriadoEm(new Date());
		
		aelNotaAdicionalAp.setRapServidores(servidorLogado);
		
		laudoUnicoRN.rnLudpVerSitExme(aelNotaAdicionalAp.getAelExameAp().getSeq());

		laudoUnicoRN.rnLudpVerServidor(aelNotaAdicionalAp.getRapServidores());
	}	
	
	/**
	 * ORADB Trigger AELT_LUD_BRU
	 * 
	 * @param aelNotaAdicionalApNew
	 * @param aelNotaAdicionalApOld
	 * @throws ApplicationBusinessException
	 */
	protected void executarAntesAtualizarAelNotaAdicionalAp(
			AelNotaAdicionalAp aelNotaAdicionalApNew, AelNotaAdicionalAp aelNotaAdicionalApOld) throws ApplicationBusinessException {
		
		LaudoUnicoRN laudoUnicoRN = getLaudoUnicoRN();
		
		laudoUnicoRN.rnLudpVerEtapLau(aelNotaAdicionalApNew.getAelExameAp().getSeq());
		
		//aelk_lud_rn.rn_ludp_ver_del
		//--Não permite alteracao nem deleção
		throw new ApplicationBusinessException(AelNotaAdicionalApRNExceptionCode.AEL_02690);
	}


	protected AelNotaAdicionalApDAO getAelNotaAdicionalApDAO() {
		return aelNotaAdicionalApDAO;
	}
	
	protected LaudoUnicoRN getLaudoUnicoRN() {
		return laudoUnicoRN;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
