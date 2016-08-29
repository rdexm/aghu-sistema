package br.gov.mec.aghu.blococirurgico.procdiagterap.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.PdtDescricaoDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtProcDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtProcJnDAO;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.PdtProc;
import br.gov.mec.aghu.model.PdtProcJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

/**
 * Classe responsável pelas regras de negócio relacionadas à entidade PdtProc.
 * 
 * @author dpacheco
 *
 */
@Stateless
public class PdtProcRN extends BaseBusiness {
@EJB
private IServidorLogadoFacade servidorLogadoFacade;

	private static final Log LOG = LogFactory.getLog(PdtProcRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private PdtProcJnDAO pdtProcJnDAO;

	@Inject
	private PdtProcDAO pdtProcDAO;

	@Inject
	private PdtDescricaoDAO pdtDescricaoDAO;


	/**
	 * 
	 */
	private static final long serialVersionUID = -8296349680803715898L;
	
	public enum ProcRNExceptionCode implements BusinessExceptionCode {
		PDT_00124
	}
	

	public void persistirPdtProc(final PdtProc pdtProc) throws ApplicationBusinessException {
		if(pdtProc.getPdtDescricao() == null){
			pdtProc.setPdtDescricao(getPdtDescricaoDAO().obterPorChavePrimaria(pdtProc.getId().getDdtSeq()));
		}
		
		if(pdtProc.getId().getSeqp() == null){
			inserirProc(pdtProc);
		} else {
			atualizarProc(pdtProc);
		}
	}
	
	
	/**
	 * Insere instância de PdtProc.
	 * 
	 * @param newProc
	 * @param servidorLogado
	 * @throws ApplicationBusinessException
	 */
	public void inserirProc(PdtProc newProc) throws ApplicationBusinessException {
		executarAntesInserir(newProc);
		getPdtProcDAO().persistir(newProc);
	}
	
	/**
	 * Trigger
	 * 
	 * ORADB: PDTT_DPC_BRI
	 * 
	 * @param newProc
	 * @param servidorLogado
	 * @throws ApplicationBusinessException 
	 */
	public void executarAntesInserir(PdtProc newProc) throws ApplicationBusinessException {
RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		
		newProc.setCriadoEm(new Date());
		
		if(newProc.getId().getSeqp() == null){
			Short seqp = getPdtProcDAO().obterMaiorSeqpProcPorDdtSeq(newProc.getId().getDdtSeq());
			newProc.getId().setSeqp(seqp == null ? Short.valueOf("1") : ++seqp);
		}
		
		// Atualiza servidor que incluiu registro
		newProc.setRapServidores(servidorLogado);
		
		// Procedimento diag terap deve estar Ativo.
		verificarProcDiagTerapAtivo(newProc);
	}
	
	/**
	 * Procedure
	 * 
	 * ORADB: PDTK_DPC_RN.RN_DPCP_VER_PROCED
	 * 
	 * @param proc
	 * @throws ApplicationBusinessException
	 */
	private void verificarProcDiagTerapAtivo(PdtProc proc) throws ApplicationBusinessException {
		if (DominioSituacao.I.equals(proc.getPdtProcDiagTerap().getSituacao())) {
			// Procedimento Diagnóstico Terapêutico deve estar ATIVO !
			throw new ApplicationBusinessException(ProcRNExceptionCode.PDT_00124);
		}
	}
	
	
	/**
	 * Atualiza instância de PdtProc.
	 * 
	 * @param newProc
	 * @param servidorLogado
	 */
	private void atualizarProc(PdtProc newProc) {
		PdtProcDAO dao = getPdtProcDAO();
		PdtProc oldProc = dao.obterOriginal(newProc);
		executarAntesAtualizar(newProc);
		dao.atualizar(newProc);
		executarAposAtualizar(oldProc, newProc);
	}
	
	/**
	 * Trigger
	 * 
	 * ORADB: PDTT_DPC_BRU
	 * 
	 * @param newProc
	 * @param servidorLogado
	 */
	private void executarAntesAtualizar(PdtProc newProc) {
RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		
		newProc.setRapServidores(servidorLogado);
	}
	
	/**
	 * Trigger
	 * 
	 * ORADB: PDTT_DPC_ARU
	 * 
	 * @param oldProc
	 * @param newProc
	 * @param servidorLogado
	 */
	private void executarAposAtualizar(PdtProc oldProc, PdtProc newProc) {
		if (CoreUtil.modificados(newProc.getId().getSeqp(), oldProc.getId().getSeqp())
			|| CoreUtil.modificados(newProc.getId().getDdtSeq(), oldProc.getId().getDdtSeq())
			|| CoreUtil.modificados(newProc.getPdtProcDiagTerap(), oldProc.getPdtProcDiagTerap())
			|| CoreUtil.modificados(newProc.getIndContaminacao(), oldProc.getIndContaminacao())	
			|| CoreUtil.modificados(newProc.getComplemento(), oldProc.getComplemento())
			|| CoreUtil.modificados(newProc.getCriadoEm(), oldProc.getCriadoEm())
			|| CoreUtil.modificados(newProc.getRapServidores(), oldProc.getRapServidores())) {
			
			inserirJournal(oldProc, DominioOperacoesJournal.UPD);
		}
		
	}
	
	/**
	 * Remove instância de PdtProc.
	 * 
	 * @param pdtProc
	 * @param servidorLogado
	 */
	public void removerProc(PdtProc pdtProc) {
		pdtProc = getPdtProcDAO().obterPorChavePrimaria(pdtProc.getId());
		getPdtProcDAO().remover(pdtProc);
		executarAposRemover(pdtProc);
	}
	
	/**
	 * Trigger
	 * 
	 * ORADB: PDTT_DPC_ARD
	 * 
	 * @param oldProc
	 * @param servidorLogado
	 */
	private void executarAposRemover(PdtProc oldProc) {
		inserirJournal(oldProc, DominioOperacoesJournal.DEL);
	}
	
	private void inserirJournal(PdtProc oldProc, DominioOperacoesJournal operacaoJournal) {
RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		
		PdtProcJn jn = BaseJournalFactory.getBaseJournal(
				operacaoJournal, PdtProcJn.class, servidorLogado.getUsuario());
		jn.setSeqp(oldProc.getId().getSeqp());
		jn.setDdtSeq(oldProc.getId().getDdtSeq());
		jn.setDptSeq(oldProc.getPdtProcDiagTerap().getSeq());
		jn.setIndContaminacao(oldProc.getIndContaminacao());
		jn.setComplemento(oldProc.getComplemento());
		jn.setCriadoEm(oldProc.getCriadoEm());
		
		RapServidoresId servidorId = oldProc.getRapServidores().getId();
		jn.setSerMatricula(servidorId.getMatricula());
		jn.setSerVinCodigo(servidorId.getVinCodigo());
		getPdtProcJnDAO().persistir(jn);
	}

	protected PdtProcDAO getPdtProcDAO() {
		return pdtProcDAO;
	}
	
	protected PdtProcJnDAO getPdtProcJnDAO() {
		return pdtProcJnDAO;
	}

	protected PdtDescricaoDAO getPdtDescricaoDAO() {
		return pdtDescricaoDAO;
	}
}
