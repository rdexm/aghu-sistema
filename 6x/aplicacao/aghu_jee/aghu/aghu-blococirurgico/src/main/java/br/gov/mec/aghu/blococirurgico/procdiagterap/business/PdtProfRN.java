package br.gov.mec.aghu.blococirurgico.procdiagterap.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.PdtProfDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtProfJnDAO;
import br.gov.mec.aghu.blococirurgico.procdiagterap.business.PdtProfRN.ProfRNExceptionCode;
import br.gov.mec.aghu.model.PdtProf;
import br.gov.mec.aghu.model.PdtProfJn;
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
 * Classe responsável pelas regras de negócio relacionadas à entidade PdtProf.
 * 
 * @author dpacheco
 *
 */
@Stateless
public class PdtProfRN extends BaseBusiness {
@EJB
private IServidorLogadoFacade servidorLogadoFacade;

	private static final Log LOG = LogFactory.getLog(PdtProfRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private PdtProfDAO pdtProfDAO;

	@Inject
	private PdtProfJnDAO pdtProfJnDAO;


	/**
	 * 
	 */
	private static final long serialVersionUID = -2145518438793710754L;
	
	public enum ProfRNExceptionCode implements BusinessExceptionCode {
		PDT_00122
	}
	
	/**
	 * Insere instância de PdtProf.
	 * 
	 * @param newProf
	 * @param servidorLogado
	 * @throws ApplicationBusinessException 
	 */
	public void inserirProf(PdtProf newProf)
			throws ApplicationBusinessException {
		
		executarAntesInserir(newProf);
		getPdtProfDAO().persistir(newProf);
	}
	
	/**
	 * Trigger
	 * 
	 * ORADB: PDTT_DPF_BRI
	 * 
	 * @param newProf
	 * @param servidorLogado
	 * @throws ApplicationBusinessException 
	 */
	private void executarAntesInserir(PdtProf newProf) 
			throws ApplicationBusinessException {
		
		newProf.setCriadoEm(new Date());
		newProf.setServidor(servidorLogadoFacade.obterServidorLogado());
		
		// Obriga o usuário informar o <nome do profissional> caso não tenha informado matricula/vinculo
		verificarNome(newProf);
	}
	
	/**
	 * Atualiza instância de PdtProf.
	 * 
	 * @param newProf
	 * @param servidorLogado
	 * @throws ApplicationBusinessException 
	 */
	@SuppressWarnings("ucd")
	public void atualizarProf(PdtProf newProf) throws ApplicationBusinessException {
		PdtProfDAO dao = getPdtProfDAO();
		PdtProf oldProf = dao.obterOriginal(newProf);
		executarAntesAtualizar(oldProf, newProf);
		dao.atualizar(newProf);
		executarAposAtualizar(oldProf, newProf);
	}
	
	/**
	 * Trigger
	 * 
	 * ORADB: PDTT_DPF_BRU
	 * 
	 * @param oldProf
	 * @param newProf
	 * @param servidorLogado
	 * @throws ApplicationBusinessException 
	 */
	private void executarAntesAtualizar(PdtProf oldProf, PdtProf newProf) 
			throws ApplicationBusinessException {
		
		newProf.setServidor(servidorLogadoFacade.obterServidorLogado());
		
		// Obriga o usuário informar o <nome do profissional> caso não tenha informado matricula/vinculo
		verificarNome(newProf);
		
	}
	
	/**
	 * Trigger
	 * 
	 * ORADB: PDTT_DPF_ARU
	 * 
	 * @param oldProf
	 * @param newProf
	 * @param servidorLogado
	 */
	private void executarAposAtualizar(PdtProf oldProf, PdtProf newProf) {
		if (CoreUtil.modificados(newProf.getId().getSeqp(), oldProf.getId().getSeqp()) 
				|| CoreUtil.modificados(newProf.getTipoAtuacao(), oldProf.getTipoAtuacao()) 
				|| CoreUtil.modificados(newProf.getNome(), oldProf.getNome()) 
				|| CoreUtil.modificados(newProf.getCategoria(), oldProf.getCategoria()) 
				|| CoreUtil.modificados(newProf.getPdtDescricao(), oldProf.getPdtDescricao()) 
				|| CoreUtil.modificados(newProf.getCriadoEm(), oldProf.getCriadoEm()) 
				|| CoreUtil.modificados(newProf.getServidorPrf(), oldProf.getServidorPrf()) 
				|| CoreUtil.modificados(newProf.getServidor(), oldProf.getServidor())) {
			
			inserirJournal(oldProf, DominioOperacoesJournal.UPD);
		}
	}
	
	/**
	 * Remove instância de PdtProf.
	 * 
	 * @param oldProf
	 * @param servidorLogado
	 */
	public void removerProf(PdtProf oldProf) {
		oldProf = getPdtProfDAO().obterPorChavePrimaria(oldProf.getId());
		getPdtProfDAO().remover(oldProf);
		executarAposRemover(oldProf);
	}
	
	/**
	 * Trigger
	 * 
	 * ORADB: PDTT_DPF_ARD
	 * 
	 * @param oldProf
	 * @param servidorLogado
	 */
	private void executarAposRemover(PdtProf oldProf) {
		inserirJournal(oldProf, DominioOperacoesJournal.DEL);
	}
	
	/**
	 * Procedure
	 * 
	 * ORADB: PDTK_DPF_RN.RN_DPFP_VER_NOME
	 * 
	 * @param newProf
	 * @throws ApplicationBusinessException 
	 */
	private void verificarNome(PdtProf newProf) throws ApplicationBusinessException {
		
		if (newProf.getServidorPrf() == null && newProf.getNome() == null) {
			throw new ApplicationBusinessException(ProfRNExceptionCode.PDT_00122);
		}
		
	}
	
	private void inserirJournal(PdtProf oldProf, DominioOperacoesJournal operacaoJournal) {
RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		
		PdtProfJn jn = BaseJournalFactory.getBaseJournal(
				operacaoJournal, PdtProfJn.class, servidorLogado.getUsuario());
		jn.setSeqp(oldProf.getId().getSeqp());
		jn.setTipoAtuacao(oldProf.getTipoAtuacao());
		jn.setNome(oldProf.getNome());
		jn.setCategoria(oldProf.getCategoria());
		jn.setDdtSeq(oldProf.getId().getDdtSeq());
		jn.setCriadoEm(oldProf.getCriadoEm());
		
		RapServidores servidorPrf = oldProf.getServidorPrf();
		if (servidorPrf != null) {
			jn.setSerMatriculaPrf(servidorPrf.getId().getMatricula());
			jn.setSerVinCodigoPrf(servidorPrf.getId().getVinCodigo());
		}
		
		RapServidoresId servidorId = oldProf.getServidor().getId();
		if (servidorId != null) {
			jn.setSerMatricula(servidorId.getMatricula());
			jn.setSerVinCodigo(servidorId.getVinCodigo());
		}
		
		getPdtProfJnDAO().persistir(jn);
	}
	
	protected PdtProfJnDAO getPdtProfJnDAO() {
		return pdtProfJnDAO;
	}
	
	protected PdtProfDAO getPdtProfDAO() {
		return pdtProfDAO;
	}
	
	
}
