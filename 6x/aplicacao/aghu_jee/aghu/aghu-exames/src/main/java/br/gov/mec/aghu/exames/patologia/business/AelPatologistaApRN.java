package br.gov.mec.aghu.exames.patologia.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.dao.AelPatologistaApDAO;
import br.gov.mec.aghu.exames.dao.AelPatologistaApJnDAO;
import br.gov.mec.aghu.exames.patologia.vo.AelPatologistaLaudoVO;
import br.gov.mec.aghu.model.AelPatologistaApJn;
import br.gov.mec.aghu.model.AelPatologistaAps;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@SuppressWarnings("ucd")
@Stateless
public class AelPatologistaApRN extends BaseBusiness {

	@EJB
	private AelPatologistaApON aelPatologistaApON;
	
	@EJB
	private LaudoUnicoRN laudoUnicoRN;
	
	private static final Log LOG = LogFactory.getLog(AelPatologistaApRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelPatologistaApJnDAO aelPatologistaApJnDAO;
	
	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject
	private AelPatologistaApDAO aelPatologistaApDAO;

	private static final long serialVersionUID = 4879378548867391256L;
	
	private enum AelPatologistaApRNExceptionCode implements BusinessExceptionCode {
		  MSG_PATOLOGISTA_JA_INCLUIDO_LAUDO,AEL_00353
	}

	public void persistirAelPatologistaAps(final AelPatologistaAps aelPatologistaAp) throws BaseException {
		if (aelPatologistaAp.getSeq() == null) {
			inserirAelPatologistaAps(aelPatologistaAp);
		}
		else {
			atulizarAelPatologistaAps(aelPatologistaAp);
		}
	}	
	
	public void inserirAelPatologistaAps(final AelPatologistaAps aelPatologistaAp) throws BaseException {
		final AelPatologistaApDAO dao = getAelPatologistaApsDAO();
		this.executarAntesInserirAelPatologistaAps(aelPatologistaAp);
		dao.persistir(aelPatologistaAp);
	}

	public void atulizarAelPatologistaAps(final AelPatologistaAps aelPatologistaApNew) throws BaseException {
		final AelPatologistaApDAO dao = getAelPatologistaApsDAO();
		final AelPatologistaAps aelPatologistaApOld = dao.obterOriginal(aelPatologistaApNew.getSeq());
		this.executarAntesAtualizarAelPatologistaAps(aelPatologistaApNew, aelPatologistaApOld);
		dao.merge(aelPatologistaApNew);
		this.executarDepoisAtualizarAelPatologistaAps(aelPatologistaApNew, aelPatologistaApOld);
	}

	public void excluirAelPatologistaAps(AelPatologistaAps aelPatologistaAp) throws BaseException {
		final AelPatologistaApDAO dao = getAelPatologistaApsDAO();
		this.executarAntesExcluirAelPatologistaAps(aelPatologistaAp);
		aelPatologistaAp = dao.merge(aelPatologistaAp);
		dao.remover(aelPatologistaAp);
		this.executarAposExcluirAelPatologistaAps(aelPatologistaAp);
	}
	
	/**
	 * ORADB Trigger AELT_LUP_ARD
	 * 
	 * @param aelPatologistaAp
	 * @throws ApplicationBusinessException 
	 */
	protected void executarAntesExcluirAelPatologistaAps(
			AelPatologistaAps aelPatologistaAp) throws ApplicationBusinessException {
		
		final LaudoUnicoRN laudoUnicoRN = getLaudoUnicoRN();
		laudoUnicoRN.rnLuppVerEtapLau(aelPatologistaAp.getAelExameAps().getSeq());
	}

	/**
	 * ORADB Trigger AELT_LUP_ARD
	 * 
	 * @param aelPatologistaAp
	 * @throws ApplicationBusinessException 
	 */
	protected void executarAposExcluirAelPatologistaAps(final AelPatologistaAps aelPatologistaAp) throws ApplicationBusinessException {
		insereJournal(aelPatologistaAp, DominioOperacoesJournal.DEL);		
	}

	
	/**
	 * ORADB Trigger AELT_LUP_BRI
	 * 
	 * @param aelPatologistaAp
	 * @throws ApplicationBusinessException  
	 */
	protected void executarAntesInserirAelPatologistaAps(final AelPatologistaAps aelPatologistaAp) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		final LaudoUnicoRN laudoUnicoRN = getLaudoUnicoRN();
		
		aelPatologistaAp.setCriadoEm(new Date());

		if (aelPatologistaAp.getServidor() == null) {
			if (servidorLogado == null) {
				throw new ApplicationBusinessException(AelPatologistaApRNExceptionCode.AEL_00353);
			}
			aelPatologistaAp.setServidor(servidorLogado);
		}
		
		laudoUnicoRN.rnLuppVerEtapLau(aelPatologistaAp.getAelExameAps().getSeq());

		laudoUnicoRN.rnLuppVerServidor(aelPatologistaAp.getServidor());

		//verifica se o patologista não foi já incluido nesse laudo unico
		List<AelPatologistaLaudoVO> listaPatologistaLaudoVO = getAelPatologistaApON().listarPatologistaLaudo(aelPatologistaAp.getAelExameAps().getSeq());
		for (AelPatologistaLaudoVO vo : listaPatologistaLaudoVO) {
			if (vo.getPatologista().getServidor().equals(aelPatologistaAp.getServidor())) { //se o patologista já estiver no laudo
				throw new ApplicationBusinessException(AelPatologistaApRNExceptionCode.MSG_PATOLOGISTA_JA_INCLUIDO_LAUDO);
			}
		}
	}
	
	/**
	 * ORADB Trigger AELT_LUP_ARU
	 * 
	 * @param aelPatologistaApNew
	 * @param aelPatologistaApOld
	 * @throws ApplicationBusinessException 
	 */
	protected void executarDepoisAtualizarAelPatologistaAps(final AelPatologistaAps aelPatologistaApNew,
			final AelPatologistaAps aelPatologistaApOld) throws ApplicationBusinessException {
		
		if (CoreUtil.modificados(aelPatologistaApNew.getSeq(), aelPatologistaApOld.getSeq())
		||  CoreUtil.modificados(aelPatologistaApNew.getOrdemMedicoLaudo(), aelPatologistaApOld.getOrdemMedicoLaudo())
		||  CoreUtil.modificados(aelPatologistaApNew.getCriadoEm(), aelPatologistaApOld.getCriadoEm())
		||  CoreUtil.modificados(aelPatologistaApNew.getAelExameAps(), aelPatologistaApOld.getAelExameAps())
		||  CoreUtil.modificados(aelPatologistaApNew.getServidor(), aelPatologistaApOld.getServidor())) {
			
			insereJournal(aelPatologistaApOld, DominioOperacoesJournal.UPD);
		}
		
	}

	private void insereJournal(final AelPatologistaAps aelPatologistaApOld,
			final DominioOperacoesJournal operacao) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		final AelPatologistaApJn jn = new  AelPatologistaApJn();
		
		jn.setNomeUsuario(servidorLogado.getUsuario());
		jn.setOperacao(operacao);
		jn.setSeq(aelPatologistaApOld.getSeq());
		jn.setCriadoEm(aelPatologistaApOld.getCriadoEm());
		jn.setAelExameAps(aelPatologistaApOld.getAelExameAps());
		jn.setServidor(aelPatologistaApOld.getServidor());
		
		final AelPatologistaApJnDAO dao = getAelPatologistaApsJnDAO();
		dao.persistir(jn);
	}

	/**
	 * ORADB Trigger AELT_LUP_BRU
	 * 
	 * @param aelPatologistaApNew
	 * @param aelPatologistaApOld
	 * @throws ApplicationBusinessException 
	 */
	protected void executarAntesAtualizarAelPatologistaAps(final AelPatologistaAps aelPatologistaApNew,
			final AelPatologistaAps aelPatologistaApOld) throws ApplicationBusinessException {
		
		final LaudoUnicoRN laudoUnicoRN = getLaudoUnicoRN();
		
		
		laudoUnicoRN.rnLuppVerEtapLau(aelPatologistaApOld.getAelExameAps().getSeq());

		laudoUnicoRN.rnLuppVerServidor(aelPatologistaApOld.getServidor());
	}
		
	protected AelPatologistaApON getAelPatologistaApON() {
		return aelPatologistaApON;
	}

	protected AelPatologistaApDAO getAelPatologistaApsDAO() {
		return aelPatologistaApDAO;
	}

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

	protected IExamesFacade getExamesFacade() {
		return this.examesFacade;
	}
	
	protected LaudoUnicoRN getLaudoUnicoRN() {
		return laudoUnicoRN;
	}
	
	protected AelPatologistaApJnDAO getAelPatologistaApsJnDAO() {
		return aelPatologistaApJnDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}
