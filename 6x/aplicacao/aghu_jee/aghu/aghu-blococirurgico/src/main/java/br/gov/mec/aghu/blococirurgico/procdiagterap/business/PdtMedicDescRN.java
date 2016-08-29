package br.gov.mec.aghu.blococirurgico.procdiagterap.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.PdtDescricaoDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtMedicDescDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtMedicDescJnDAO;
import br.gov.mec.aghu.blococirurgico.procdiagterap.business.PdtMedicDescRN.PdtMedicDescRNExceptionCode;
import br.gov.mec.aghu.dominio.DominioSituacaoMedicamento;
import br.gov.mec.aghu.model.PdtMedicDesc;
import br.gov.mec.aghu.model.PdtMedicDescJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

/**
 * Classe responsável pelas regras de negócio relacionadas à entidade PdtMedicDesc.
 * 
 * @author eschweigert
 *
 */
@Stateless
public class PdtMedicDescRN extends BaseBusiness {
@EJB
private IServidorLogadoFacade servidorLogadoFacade;

	private static final Log LOG = LogFactory.getLog(PdtMedicDescRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private PdtDescricaoDAO pdtDescricaoDAO;

	@Inject
	private PdtMedicDescDAO pdtMedicDescDAO;

	@Inject
	private PdtMedicDescJnDAO pdtMedicDescJnDAO;


	private static final long serialVersionUID = -5188149529497723953L;

	public enum PdtMedicDescRNExceptionCode implements BusinessExceptionCode {
		PDT_00129, PDT_00105;
	}

	public void persistirPdtMedicDesc(final PdtMedicDesc medicDescricao) throws ApplicationBusinessException {
		if(medicDescricao.getPdtDescricao() == null){
			medicDescricao.setPdtDescricao(getPdtDescricaoDAO().obterPorChavePrimaria(medicDescricao.getId().getDdtSeq()));
		}
		
		if(medicDescricao.getId().getSeqp() == null){
			inserir(medicDescricao);
		} else {
			atualizar(medicDescricao);
		}
	}
	
	void inserir(final PdtMedicDesc medicDescricao) throws ApplicationBusinessException {
		executarAntesInserir(medicDescricao);
		getPdtMedicDescDAO().persistir(medicDescricao);
	}
	
	/**
	 * Trigger
	 * 
	 * ORADB: PDTT_DMD_BRI
	 */
	private void executarAntesInserir(final PdtMedicDesc medicDescricao) throws ApplicationBusinessException {
RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		medicDescricao.setCriadoEm(new Date());

		if(medicDescricao.getId().getSeqp() == null){
			Integer seqp = getPdtMedicDescDAO().obterMaxSeqpPorDdtSeq(medicDescricao.getId().getDdtSeq());
			medicDescricao.getId().setSeqp(seqp != null ? ++seqp : Integer.valueOf(1));
		}
		
		// atualiza servidor que incluiu registro
		if(servidorLogado == null){
			throw new ApplicationBusinessException(PdtMedicDescRNExceptionCode.PDT_00105);
		}
		medicDescricao.setRapServidores(servidorLogado);
		
		// Código do Medicamento deve estar ativo
		// pdtk_dmd_rn.rn_dmdp_ver_mdto(:new.med_mat_codigo);
		if(DominioSituacaoMedicamento.I.equals(medicDescricao.getAfaMedicamento().getIndSituacao())){
			throw new ApplicationBusinessException(PdtMedicDescRNExceptionCode.PDT_00129);
		}
	}
	
	/**
	 * Atualiza instância de PdtMedicDesc.
	 */
	void atualizar(final PdtMedicDesc medicDescricao) throws ApplicationBusinessException {
		final PdtMedicDescDAO dao = getPdtMedicDescDAO();
		final PdtMedicDesc oldCidDesc = dao.obterOriginal(medicDescricao);
		
		executarAntesAtualizar(medicDescricao, oldCidDesc);
		dao.atualizar(medicDescricao);
		executarAposAtualizar(medicDescricao, oldCidDesc);
	}
	
	/**
	 * Trigger
	 * 
	 * ORADB: PDTT_DMD_BRU
	 */
	private void executarAntesAtualizar(final PdtMedicDesc medicDescricao, final PdtMedicDesc oldDescricaoTecnica) throws ApplicationBusinessException {
RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();

		// atualiza servidor que incluiu registro
		if(servidorLogado == null){
			throw new ApplicationBusinessException(PdtMedicDescRNExceptionCode.PDT_00105);
		}
		
		medicDescricao.setRapServidores(servidorLogado);
		
		// Código do Medicamento deve estar ativo
		// pdtk_dmd_rn.rn_dmdp_ver_mdto(:new.med_mat_codigo);
		if(DominioSituacaoMedicamento.I.equals(medicDescricao.getAfaMedicamento().getIndSituacao())){
			throw new ApplicationBusinessException(PdtMedicDescRNExceptionCode.PDT_00129);
		}
	}

	/**
	 * Trigger
	 * 
	 * ORADB: PDTT_DMD_ARU
	 */
	private void executarAposAtualizar(final PdtMedicDesc newDescricaoTecnica, final PdtMedicDesc oldDescricaoTecnica) throws ApplicationBusinessException {
		if(!CoreUtil.igual(newDescricaoTecnica.getCriadoEm(), oldDescricaoTecnica.getCriadoEm()) ||
				!CoreUtil.igual(newDescricaoTecnica.getAfaMedicamento(), oldDescricaoTecnica.getAfaMedicamento()) ||
				!CoreUtil.igual(newDescricaoTecnica.getDose(), oldDescricaoTecnica.getDose()) ||
				!CoreUtil.igual(newDescricaoTecnica.getPreTrans(), oldDescricaoTecnica.getPreTrans()) ||
				!CoreUtil.igual(newDescricaoTecnica.getUnidade(), oldDescricaoTecnica.getUnidade()) ||
				!CoreUtil.igual(newDescricaoTecnica.getId().getDdtSeq(), oldDescricaoTecnica.getId().getDdtSeq()) ||
				!CoreUtil.igual(newDescricaoTecnica.getId().getSeqp(), oldDescricaoTecnica.getId().getSeqp()) ||
				!CoreUtil.igual(newDescricaoTecnica.getRapServidores(), oldDescricaoTecnica.getRapServidores())){
			final PdtMedicDescJn journal = createJournal(oldDescricaoTecnica, DominioOperacoesJournal.UPD);
			getPdtMedicDescJnDAO().persistir(journal);
		}
	}
	
	
	public void excluir(PdtMedicDesc medicDescricao) throws ApplicationBusinessException{
		medicDescricao = getPdtMedicDescDAO().obterPorChavePrimaria(medicDescricao.getId());
		getPdtMedicDescDAO().remover(medicDescricao);
		executarAposExcluir(medicDescricao);
	}
	
	/**
	 * Trigger
	 * 
	 * ORADB: PDTT_DMD_ARD
	 */
	private void executarAposExcluir(final PdtMedicDesc medicDescricao) throws ApplicationBusinessException {
		final PdtMedicDescJn journal = createJournal(medicDescricao, DominioOperacoesJournal.DEL);
		getPdtMedicDescJnDAO().persistir(journal);
	}

	private PdtMedicDescJn createJournal(
			final PdtMedicDesc medicDescricao,
			DominioOperacoesJournal dominio) {

		final PdtMedicDescJn journal = BaseJournalFactory
				.getBaseJournal(dominio, PdtMedicDescJn.class, servidorLogadoFacade.obterServidorLogado().getUsuario());
		
		journal.setCriadoEm(medicDescricao.getCriadoEm());
		journal.setDdtSeq(medicDescricao.getId().getDdtSeq());
		journal.setSeqp(medicDescricao.getId().getSeqp());
		journal.setDose(medicDescricao.getDose());
		journal.setMedMatCodigo(medicDescricao.getAfaMedicamento().getMatCodigo());
		journal.setPreTrans(medicDescricao.getPreTrans());
		journal.setUnidade(medicDescricao.getUnidade());
		journal.setSerMatricula(medicDescricao.getRapServidores().getId().getMatricula());
		journal.setSerVinCodigo(medicDescricao.getRapServidores().getId().getVinCodigo());
		
		return journal;
	}
	
	protected PdtMedicDescJnDAO getPdtMedicDescJnDAO() {
		return pdtMedicDescJnDAO;
	}
	
	protected PdtMedicDescDAO getPdtMedicDescDAO() {
		return pdtMedicDescDAO;
	}

	protected PdtDescricaoDAO getPdtDescricaoDAO() {
		return pdtDescricaoDAO;
	}
}