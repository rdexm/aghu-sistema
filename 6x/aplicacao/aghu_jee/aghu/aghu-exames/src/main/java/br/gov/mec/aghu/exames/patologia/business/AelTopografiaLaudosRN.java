package br.gov.mec.aghu.exames.patologia.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelTopografiaLaudosDAO;
import br.gov.mec.aghu.exames.dao.AelTopografiaLaudosJnDAO;
import br.gov.mec.aghu.model.AelTopografiaLaudos;
import br.gov.mec.aghu.model.AelTopografiaLaudosJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class AelTopografiaLaudosRN extends BaseBusiness {

	@EJB
	private LaudoUnicoRN laudoUnicoRN;
	
	private static final Log LOG = LogFactory.getLog(AelTopografiaLaudosRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelTopografiaLaudosDAO aelTopografiaLaudosDAO;
	
	@Inject
	private AelTopografiaLaudosJnDAO aelTopografiaLaudosJnDAO; 

	/**
	 * 
	 */
	private static final long serialVersionUID = 4532904490783765513L;
	
	public enum AelTopografiaLaudosRNExceptionCode implements BusinessExceptionCode {
	    MENSAGEM_INSERT_TOPOGRAFIA_JA_EXISTE
	    }

	public void persistir(final AelTopografiaLaudos topografiaLaudos) throws BaseException {
		this.inserir(topografiaLaudos);
	}

	public void inserir(final AelTopografiaLaudos topografiaLaudos) throws BaseException {
		this.executarAntesInserir(topografiaLaudos);
		getAelTopografiaLaudosDAO().persistir(topografiaLaudos);
	}
	
	public void excluir(AelTopografiaLaudos topografiaLaudos) throws BaseException {
		topografiaLaudos = this.getAelTopografiaLaudosDAO().merge(topografiaLaudos);
		this.getAelTopografiaLaudosDAO().remover(topografiaLaudos);
		this.executarAposExcluir(topografiaLaudos);
	}
	
	// @ORABD AELT_LO7_BRI
	private void executarAntesInserir(final AelTopografiaLaudos topografiaLaudos) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		topografiaLaudos.setCriadoEm(new Date());
		// aelk_ael_rn.rn_aelp_atu_servidor | Código equivalente
		topografiaLaudos.setServidor(servidorLogado);
		getLaudoUnicoRN().rnL07VerEtapLau(topografiaLaudos.getExameAp().getSeq());
		// O servidor deve estar na tab Patologistas com ind_sit = 'S' 
		getLaudoUnicoRN().rnL07VerServidor(topografiaLaudos.getServidor());
		validarTopografiaJaExiste(topografiaLaudos.getExameAp().getSeq(), topografiaLaudos.getTopografiaCidOs().getSeq());
	}

	private void validarTopografiaJaExiste(Long seqExame, Long seqCidO) throws BaseException{
		Long count = getAelTopografiaLaudosDAO().obterTopografiaLaudosCount(seqExame, seqCidO);
	
	    if(count > 0){
		throw new ApplicationBusinessException(AelTopografiaLaudosRNExceptionCode.MENSAGEM_INSERT_TOPOGRAFIA_JA_EXISTE);
	    }
	}

	// @ORABD AELT_LO7_ARD
	private void executarAposExcluir(final AelTopografiaLaudos topografiaLaudosOld) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		AelTopografiaLaudosJn jn = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.DEL, AelTopografiaLaudosJn.class, servidorLogado.getUsuario());
		jn.setSeq(topografiaLaudosOld.getSeq());
		jn.setCriadoEm(topografiaLaudosOld.getCriadoEm());
		jn.setSeq((topografiaLaudosOld.getExameAp()!=null)?topografiaLaudosOld.getExameAp().getSeq():null);
		jn.setTopografiaCidOs((topografiaLaudosOld.getTopografiaCidOs()!=null)?topografiaLaudosOld.getTopografiaCidOs().getSeq():null);
		jn.setExameAp(topografiaLaudosOld.getExameAp().getSeq());
		jn.setSerMatricula(topografiaLaudosOld.getServidor().getId().getMatricula());
		jn.setSerVinCodigo(topografiaLaudosOld.getServidor().getId().getVinCodigo());
		getAelTopografiaLaudosJnDAO().persistir(jn);
	}

	private LaudoUnicoRN getLaudoUnicoRN() {
		return laudoUnicoRN;
	}

	private AelTopografiaLaudosDAO getAelTopografiaLaudosDAO() {
		return aelTopografiaLaudosDAO;
	}

	private AelTopografiaLaudosJnDAO getAelTopografiaLaudosJnDAO() {
		return aelTopografiaLaudosJnDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}
