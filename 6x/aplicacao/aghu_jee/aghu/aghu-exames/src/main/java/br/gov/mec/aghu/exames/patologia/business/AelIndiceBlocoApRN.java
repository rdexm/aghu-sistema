package br.gov.mec.aghu.exames.patologia.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelIndiceBlocoApDAO;
import br.gov.mec.aghu.exames.dao.AelIndiceBlocoApJnDAO;
import br.gov.mec.aghu.model.AelIndiceBlocoAp;
import br.gov.mec.aghu.model.AelIndiceBlocoApJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class AelIndiceBlocoApRN extends BaseBusiness {

	@EJB
	private LaudoUnicoRN laudoUnicoRN;
	
	private static final Log LOG = LogFactory.getLog(AelIndiceBlocoApRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelIndiceBlocoApJnDAO aelIndiceBlocoApJnDAO;
	
	@Inject
	private AelIndiceBlocoApDAO aelIndiceBlocoApDAO;

	private static final long serialVersionUID = 4879378548867391256L;

	public enum AelIndiceBlocoApRNExceptionCode implements BusinessExceptionCode {
		AEL_00353
	}
	
	public void persistir(final AelIndiceBlocoAp aelIndiceBlocoAp) throws BaseException {
		if (aelIndiceBlocoAp.getSeq() == null) {
			inserir(aelIndiceBlocoAp);
		}
		else {
			 AelIndiceBlocoAp aelIndiceBlocoApOld = getAelIndiceBlocoApDAO().obterOriginal(aelIndiceBlocoAp);
			 atualizar(aelIndiceBlocoAp, aelIndiceBlocoApOld);
		}
	}
	
	public void inserir(final AelIndiceBlocoAp aelIndiceBlocoAp) throws BaseException {
		final AelIndiceBlocoApDAO dao = getAelIndiceBlocoApDAO();
		this.executarAntesInserirAelIndiceBlocoAp(aelIndiceBlocoAp);
		dao.persistir(aelIndiceBlocoAp);
	}

	public void atualizar(final AelIndiceBlocoAp aelIndiceBlocoApNew, final AelIndiceBlocoAp aelIndiceBlocoApOld) throws BaseException {
		final AelIndiceBlocoApDAO dao = getAelIndiceBlocoApDAO();
		this.executarAntesAtualizarAelIndiceBlocoAp(aelIndiceBlocoApNew, aelIndiceBlocoApOld);
		dao.atualizar(aelIndiceBlocoApNew);
		this.executarDepoisAtualizarAelIndiceBlocoAp(aelIndiceBlocoApNew, aelIndiceBlocoApOld);
	}

	public void excluir(final AelIndiceBlocoAp aelIndiceBlocoAp) throws BaseException {
		final AelIndiceBlocoApDAO dao = getAelIndiceBlocoApDAO();
		dao.remover(aelIndiceBlocoAp);
		this.executarAposExcluirAelIndiceBlocoAp(aelIndiceBlocoAp);
	}
	
	/**
	 * ORADB Trigger AELT_LO8_ARD
	 * 
	 * @param aelIndiceBlocoAp
	 * @throws ApplicationBusinessException 
	 */
	protected void executarAposExcluirAelIndiceBlocoAp(final AelIndiceBlocoAp aelIndiceBlocoAp) throws ApplicationBusinessException {
		insereJournal(aelIndiceBlocoAp, DominioOperacoesJournal.DEL);		
	}

	
	/**
	 * ORADB Trigger AELT_LO8_BRI
	 * 
	 * @param aelIndiceBlocoAp
	 * @throws ApplicationBusinessException  
	 */
	protected void executarAntesInserirAelIndiceBlocoAp(final AelIndiceBlocoAp aelIndiceBlocoAp) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		final LaudoUnicoRN laudoUnicoRN = getLaudoUnicoRN();
		
		aelIndiceBlocoAp.setCriadoEm(new Date());
		
		if (servidorLogado == null) {
			throw new ApplicationBusinessException(AelIndiceBlocoApRNExceptionCode.AEL_00353);
		}
		
		aelIndiceBlocoAp.setRapServidores(servidorLogado);
		
		laudoUnicoRN.rnLo8pVerEtapLa(aelIndiceBlocoAp.getAelExameAp().getSeq());

		laudoUnicoRN.rnLo8pVerServidor(aelIndiceBlocoAp.getRapServidores());

	}
	
	/**
	 * ORADB Trigger AELT_LO8_ARU
	 * 
	 * @param aelIndiceBlocoApNew
	 * @param aelIndiceBlocoApOld
	 * @throws ApplicationBusinessException 
	 */
	protected void executarDepoisAtualizarAelIndiceBlocoAp(final AelIndiceBlocoAp aelIndiceBlocoApNew,
			final AelIndiceBlocoAp aelIndiceBlocoApOld) throws ApplicationBusinessException {
		
		if (CoreUtil.modificados(aelIndiceBlocoApNew.getSeq(), aelIndiceBlocoApOld.getSeq())
		||  CoreUtil.modificados(aelIndiceBlocoApNew.getIndiceDeBloco(), aelIndiceBlocoApOld.getIndiceDeBloco())
		||  CoreUtil.modificados(aelIndiceBlocoApNew.getCriadoEm(), aelIndiceBlocoApOld.getCriadoEm())
		||  CoreUtil.modificados(aelIndiceBlocoApNew.getAelExameAp(), aelIndiceBlocoApOld.getAelExameAp())
		||  CoreUtil.modificados(aelIndiceBlocoApNew.getRapServidores(), aelIndiceBlocoApOld.getRapServidores())) {
			
			insereJournal(aelIndiceBlocoApOld, DominioOperacoesJournal.UPD);
		}
		
	}

	private void insereJournal(final AelIndiceBlocoAp aelIndiceBlocoApOld,
			final DominioOperacoesJournal operacao) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		final AelIndiceBlocoApJn jn = new  AelIndiceBlocoApJn();
		
		jn.setNomeUsuario(servidorLogado.getUsuario());
		jn.setOperacao(operacao);
		jn.setSeq(aelIndiceBlocoApOld.getSeq());
		jn.setCriadoEm(aelIndiceBlocoApOld.getCriadoEm());
		jn.setIndiceDeBloco(aelIndiceBlocoApOld.getIndiceDeBloco());
		jn.setRapServidores(aelIndiceBlocoApOld.getRapServidores());
		
		final AelIndiceBlocoApJnDAO dao = getAelIndiceBlocoApJnDAO();
		dao.persistir(jn);
	}

	/**
	 * ORADB Trigger AELT_LO8_BRU
	 * 
	 * @param aelIndiceBlocoApNew
	 * @param aelIndiceBlocoApOld
	 * @throws ApplicationBusinessException 
	 */
	protected void executarAntesAtualizarAelIndiceBlocoAp(final AelIndiceBlocoAp aelIndiceBlocoApNew,
			final AelIndiceBlocoAp aelIndiceBlocoApOld) throws ApplicationBusinessException {
		
		final LaudoUnicoRN laudoUnicoRN = getLaudoUnicoRN();
		
		
		laudoUnicoRN.rnLo8pVerEtapLa(aelIndiceBlocoApOld.getAelExameAp().getSeq());

		laudoUnicoRN.rnLo8pVerServidor(aelIndiceBlocoApOld.getRapServidores());

		laudoUnicoRN.rnLuzpVerEtapLa2(aelIndiceBlocoApOld.getAelExameAp().getSeq());
	}

	protected AelIndiceBlocoApDAO getAelIndiceBlocoApDAO() {
		return aelIndiceBlocoApDAO;
	}

	protected LaudoUnicoRN getLaudoUnicoRN() {
		return laudoUnicoRN;
	}
	
	protected AelIndiceBlocoApJnDAO getAelIndiceBlocoApJnDAO() {
		return aelIndiceBlocoApJnDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
