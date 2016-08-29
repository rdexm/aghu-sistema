package br.gov.mec.aghu.blococirurgico.procdiagterap.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.PdtAvalPreSedacaoDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtAvalPreSedacaoJnDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtDescricaoDAO;
import br.gov.mec.aghu.model.PdtAvalPreSedacao;
import br.gov.mec.aghu.model.PdtAvalPreSedacaoJn;
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
 * Classe responsável pelas regras de negócio relacionadas à entidade PdtAvalPreSedacao.
 * 
 * @author rpanassolo
 *
 */
@Stateless
public class PdtAvalPreSedacaoRN extends BaseBusiness {
@EJB
private IServidorLogadoFacade servidorLogadoFacade;

	private static final Log LOG = LogFactory.getLog(PdtAvalPreSedacaoRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private PdtDescricaoDAO pdtDescricaoDAO;

	@Inject
	private PdtAvalPreSedacaoDAO pdtAvalPreSedacaoDAO;

	@Inject
	private PdtAvalPreSedacaoJnDAO pdtAvalPreSedacaoJnDAO;


	/**
	 * 
	 */
	private static final long serialVersionUID = -8296349680803715898L;
	
	public enum ProcRNExceptionCode implements BusinessExceptionCode {
		PDT_00124
	}
	

	public void persistirPdtAvalPreSedacao(final PdtAvalPreSedacao pdtAvalPreSedacao) throws ApplicationBusinessException {
		PdtAvalPreSedacaoDAO dao = getPdtAvalPreSedacaoDAO();
		pdtAvalPreSedacao.setPdtDescricao(getPdtDescricaoDAO().obterPorChavePrimaria(pdtAvalPreSedacao.getDdtSeq()));
		
		if(pdtAvalPreSedacao.getVersion() == null){
			inserirAvalPreSedacao(pdtAvalPreSedacao);
		} else {
			PdtAvalPreSedacao oldpdtAvalPreSedacao = dao.obterOriginal(pdtAvalPreSedacao);
			atualizarAvalPreSedacao(oldpdtAvalPreSedacao, pdtAvalPreSedacao);
		}
	}
	
	
	/**
	 * Insere instância de PdtProc.
	 * 
	 * @param newProc
	 * @param servidorLogado
	 * @throws ApplicationBusinessException
	 */
	public void inserirAvalPreSedacao(PdtAvalPreSedacao newAvalPreSedacao) throws ApplicationBusinessException {
		executarAntesInserir(newAvalPreSedacao);
		getPdtAvalPreSedacaoDAO().persistir(newAvalPreSedacao);
	}
	
	/**
	 * Trigger
	 * 
	 * ORADB: PDTT_CXPJ_BRI
	 * 
	 * @param newProc
	 * @param servidorLogado
	 * @throws ApplicationBusinessException 
	 */
	public void executarAntesInserir(PdtAvalPreSedacao newPdtAvalPreSedacao) throws ApplicationBusinessException {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		
		newPdtAvalPreSedacao.setCriadoEm(new Date());
		
		// Atualiza servidor que incluiu registro
		newPdtAvalPreSedacao.setRapServidores(servidorLogado);
		//tempo jejum >0
		verificarTempoJejum(newPdtAvalPreSedacao);
	}
	
	/**
	 * Procedure
	 * @param proc
	 * @throws ApplicationBusinessException
	 */
	private void verificarTempoJejum(PdtAvalPreSedacao pdtAvalPreSedacao) throws ApplicationBusinessException {
		if(pdtAvalPreSedacao.getTempoJejum()!=null){
			if (pdtAvalPreSedacao.getTempoJejum()==0) {
				throw new ApplicationBusinessException(ProcRNExceptionCode.PDT_00124);
			}
		}
	}
	
	
	/**
	 * Atualiza instância de PdtProc.
	 * 
	 * @param newProc
	 * @param servidorLogado
	 */
	private void atualizarAvalPreSedacao(PdtAvalPreSedacao oldPdtAvalPreSedacao, PdtAvalPreSedacao newpdtAvalPreSedacao) {
		PdtAvalPreSedacaoDAO dao = getPdtAvalPreSedacaoDAO();
		//oldpdtAvalPreSedacao.setPdtDescricao(getPdtDescricaoDAO().obterPorChavePrimaria(oldpdtAvalPreSedacao.getDdtSeq()));	
		executarAntesAtualizar(newpdtAvalPreSedacao);
		dao.atualizar(newpdtAvalPreSedacao);
		executarAposAtualizar(oldPdtAvalPreSedacao, newpdtAvalPreSedacao);
	}
	
	/**
	 * Trigger
	 *         
	 * ORADB: PDTT_CXPJ_BRU
	 * 
	 * @param newProc
	 * @param servidorLogado
	 */
	private void executarAntesAtualizar(PdtAvalPreSedacao newpdtAvalPreSedacao) {
RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		
		newpdtAvalPreSedacao.setRapServidores(servidorLogado);
	}
	
	/**
	 * Trigger
	 * 
	 * 
	 * 
	 * @param oldProc
	 * @param newProc
	 * @param servidorLogado
	 */
	private void executarAposAtualizar(PdtAvalPreSedacao oldPdtAvalPreSedacao, PdtAvalPreSedacao newpdtAvalPreSedacao) {
		
		if (CoreUtil.modificados(newpdtAvalPreSedacao.getDdtSeq(), oldPdtAvalPreSedacao.getDdtSeq())
			|| CoreUtil.modificados(newpdtAvalPreSedacao.getPdtDescricao(), oldPdtAvalPreSedacao.getPdtDescricao())
			|| CoreUtil.modificados(newpdtAvalPreSedacao.getAsa(), oldPdtAvalPreSedacao.getAsa())	
			|| CoreUtil.modificados(newpdtAvalPreSedacao.getIndParticAvalCli(), oldPdtAvalPreSedacao.getIndParticAvalCli())
			|| CoreUtil.modificados(newpdtAvalPreSedacao.getAvaliacaoClinica(), oldPdtAvalPreSedacao.getAvaliacaoClinica())
			|| CoreUtil.modificados(newpdtAvalPreSedacao.getComorbidades(), oldPdtAvalPreSedacao.getComorbidades())
			|| CoreUtil.modificados(newpdtAvalPreSedacao.getExameFisico(), oldPdtAvalPreSedacao.getExameFisico())
			|| CoreUtil.modificados(newpdtAvalPreSedacao.getTempoJejum(), oldPdtAvalPreSedacao.getTempoJejum())
			|| CoreUtil.modificados(newpdtAvalPreSedacao.getViaAereas(), oldPdtAvalPreSedacao.getViaAereas())
			|| CoreUtil.modificados(newpdtAvalPreSedacao.getCriadoEm(), oldPdtAvalPreSedacao.getCriadoEm())
			|| CoreUtil.modificados(newpdtAvalPreSedacao.getRapServidores(), oldPdtAvalPreSedacao.getRapServidores())) {
			inserirJournal(oldPdtAvalPreSedacao, DominioOperacoesJournal.UPD);
		}
		
	}
	
	/**
	 * Remove instância de PdtAvalPreSedacao.
	 * 
	 * @param pdtProc
	 * @param servidorLogado
	 */
	public void removerPdtAvalPreSedacao(PdtAvalPreSedacao pdtAvalPreSedacao) {
		pdtAvalPreSedacao = getPdtAvalPreSedacaoDAO().obterPorChavePrimaria(pdtAvalPreSedacao.getSeq());
		getPdtAvalPreSedacaoDAO().remover(pdtAvalPreSedacao);
		executarAposRemover(pdtAvalPreSedacao);
	}
	
	/**
	 * Trigger
	 * 
	 * ORADB: PDTT_DPC_ARD
	 * 
	 * @param oldProc
	 * @param servidorLogado
	 */
	private void executarAposRemover(PdtAvalPreSedacao oldPdtAvalPreSedacao) {
		inserirJournal(oldPdtAvalPreSedacao, DominioOperacoesJournal.DEL);
	}
	
	private void inserirJournal(PdtAvalPreSedacao oldPdtAvalPreSedacao, DominioOperacoesJournal operacaoJournal) {
RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		
		PdtAvalPreSedacaoJn jn = BaseJournalFactory.getBaseJournal(
				operacaoJournal, PdtAvalPreSedacaoJn.class, servidorLogado.getUsuario());
		jn.setDdtSeq(oldPdtAvalPreSedacao.getDdtSeq());
		jn.setCriadoEm(oldPdtAvalPreSedacao.getCriadoEm());
		jn.setAsa(oldPdtAvalPreSedacao.getAsa());
		jn.setAvaliacaoClinica(oldPdtAvalPreSedacao.getAvaliacaoClinica());
		jn.setComorbidades(oldPdtAvalPreSedacao.getComorbidades());
		jn.setExameFisico(oldPdtAvalPreSedacao.getExameFisico());
		jn.setIndParticAvalCli(oldPdtAvalPreSedacao.getIndParticAvalCli());
		jn.setPdtDescricao(oldPdtAvalPreSedacao.getPdtDescricao());
		jn.setTempoJejum(oldPdtAvalPreSedacao.getTempoJejum());
		jn.setViaAereas(oldPdtAvalPreSedacao.getViaAereas());
		RapServidoresId servidorId = oldPdtAvalPreSedacao.getRapServidores().getId();
		jn.setSerMatricula(servidorId.getMatricula());
		jn.setSerVinCodigo(servidorId.getVinCodigo());
		getPdtAvalPreSedacaoJnDAO().persistir(jn);
	}
	
	
	protected PdtAvalPreSedacaoDAO getPdtAvalPreSedacaoDAO() {
		return pdtAvalPreSedacaoDAO;
	}
	
	protected PdtAvalPreSedacaoJnDAO getPdtAvalPreSedacaoJnDAO() {
		return pdtAvalPreSedacaoJnDAO;
	}

	protected PdtDescricaoDAO getPdtDescricaoDAO() {
		return pdtDescricaoDAO;
	}
}
