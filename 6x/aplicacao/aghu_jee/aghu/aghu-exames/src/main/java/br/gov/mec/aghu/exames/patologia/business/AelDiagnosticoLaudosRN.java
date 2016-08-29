package br.gov.mec.aghu.exames.patologia.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelDiagnosticoLaudosDAO;
import br.gov.mec.aghu.exames.dao.AelDiagnosticoLaudosJnDAO;
import br.gov.mec.aghu.model.AelDiagnosticoLaudos;
import br.gov.mec.aghu.model.AelDiagnosticoLaudosJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class AelDiagnosticoLaudosRN extends BaseBusiness {

	@EJB
	private LaudoUnicoRN laudoUnicoRN;
	
	private static final Log LOG = LogFactory.getLog(AelDiagnosticoLaudosRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelDiagnosticoLaudosJnDAO aelDiagnosticoLaudosJnDAO;
	
	@Inject
	private AelDiagnosticoLaudosDAO aelDiagnosticoLaudosDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 4532904490783765513L;
	
	private enum DiagnosticoLaudosRNExceptionCode implements BusinessExceptionCode {
		MENSAGEM_CODIGO_CID10_CIDO_DEVE_SER_INFORMADO, MENSAGEM_DIAGNOSTICO_LAUDOS_JA_ASSOCIADO, MENSAGEM_DIAGNOSTICO_NAO_INFORMADO;
	}


	public void persistir(final AelDiagnosticoLaudos diagnosticoLaudos) throws BaseException {
		this.inserir(diagnosticoLaudos);
	}

	public void inserir(final AelDiagnosticoLaudos diagnosticoLaudos) throws BaseException {
		this.executarAntesInserir(diagnosticoLaudos);
		getAelDiagnosticoLaudosDAO().persistir(diagnosticoLaudos);
	}
	
	public void excluir(AelDiagnosticoLaudos diagnosticoLaudos) throws BaseException {
		this.antesDeExcluir(diagnosticoLaudos);
		diagnosticoLaudos = this.getAelDiagnosticoLaudosDAO().merge(diagnosticoLaudos);
		this.getAelDiagnosticoLaudosDAO().remover(diagnosticoLaudos);
		this.executarAposExcluir(diagnosticoLaudos);
	}
	
	// @ORABD AELT_LO6_BRI
	private void executarAntesInserir(final AelDiagnosticoLaudos diagnosticoLaudos) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		diagnosticoLaudos.setCriadoEm(new Date());

		// aelk_ael_rn.rn_aelp_atu_servidor | Código equivalente
		diagnosticoLaudos.setServidor(servidorLogado);

		getLaudoUnicoRN().rnL06VerEtapLau(diagnosticoLaudos.getExameAp().getSeq());
		
		// O servidor deve estar na tab Patologistas com ind_sit = 'S'
		getLaudoUnicoRN().rnL06VerServidor(diagnosticoLaudos.getServidor());
		validarAntesInclusaoDiagnostico(diagnosticoLaudos);
	}

	// @ORABD AELT_LO6_ARD
	private void executarAposExcluir(final AelDiagnosticoLaudos diagnosticoLaudosOld) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		AelDiagnosticoLaudosJn jn = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.DEL,AelDiagnosticoLaudosJn.class, servidorLogado.getUsuario());
		jn.setSeq(diagnosticoLaudosOld.getSeq());
		jn.setCriadoEm(diagnosticoLaudosOld.getCriadoEm());
		jn.setAghCid((diagnosticoLaudosOld.getAghCid()!=null)?diagnosticoLaudosOld.getAghCid().getSeq():null);
		jn.setAelCidos((diagnosticoLaudosOld.getAelCidos()!=null)?diagnosticoLaudosOld.getAelCidos().getSeq():null);
		jn.setSerMatricula(diagnosticoLaudosOld.getServidor().getId().getMatricula());
		jn.setSerVinCodigo(diagnosticoLaudosOld.getServidor().getId().getVinCodigo());
		getAelDiagnosticoLaudosJnDAO().persistir(jn);
	}

	// @ORADB AELK_LUZ_RN.RN_LUZP_VER_ETAP_LA2
	private void antesDeExcluir(final AelDiagnosticoLaudos diagnosticoLaudos) throws BaseException {
		//Não é permitido nenhum tipo de alteração! Laudo está Assinado ou Diagnóstico Concluído('MC','DC','LA').
		getLaudoUnicoRN().rnLuzpVerEtapLa2(diagnosticoLaudos.getExameAp().getSeq());
	}
	 
    public void validarAntesInclusaoDiagnostico(AelDiagnosticoLaudos aelDiagnosticoLaudos) throws BaseException {
    	Long existeCodigoCid = 0l;
    	Long existeCodigoCidO = 0l;
		if (aelDiagnosticoLaudos == null) {
		    throw new ApplicationBusinessException(DiagnosticoLaudosRNExceptionCode.MENSAGEM_DIAGNOSTICO_NAO_INFORMADO);
		}
		if (aelDiagnosticoLaudos.getAghCid() == null && aelDiagnosticoLaudos.getAelCidos() == null) {
		    throw new ApplicationBusinessException(DiagnosticoLaudosRNExceptionCode.MENSAGEM_CODIGO_CID10_CIDO_DEVE_SER_INFORMADO);
		}
		if (aelDiagnosticoLaudos.getAghCid() != null) {
		    existeCodigoCid = getAelDiagnosticoLaudosDAO().obterDiagnosticoLaudosCid(aelDiagnosticoLaudos.getExameAp().getSeq(),
			    aelDiagnosticoLaudos.getAghCid().getSeq());
		}
		if (aelDiagnosticoLaudos.getAelCidos() != null) {
		    existeCodigoCidO = getAelDiagnosticoLaudosDAO().obterDiagnosticoLaudosCidO(aelDiagnosticoLaudos.getExameAp().getSeq(),
			    aelDiagnosticoLaudos.getAelCidos().getSeq());
		}
		if (existeCodigoCid > 0 || existeCodigoCidO > 0) {
		    throw new ApplicationBusinessException(DiagnosticoLaudosRNExceptionCode.MENSAGEM_DIAGNOSTICO_LAUDOS_JA_ASSOCIADO);
		}
    }

	private LaudoUnicoRN getLaudoUnicoRN() {
		return laudoUnicoRN;
	}

	private AelDiagnosticoLaudosDAO getAelDiagnosticoLaudosDAO() {
		return aelDiagnosticoLaudosDAO;
	}

	private AelDiagnosticoLaudosJnDAO getAelDiagnosticoLaudosJnDAO() {
		return aelDiagnosticoLaudosJnDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
