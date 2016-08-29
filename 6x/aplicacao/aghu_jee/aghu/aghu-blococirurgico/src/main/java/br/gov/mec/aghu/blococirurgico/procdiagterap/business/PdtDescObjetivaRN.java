package br.gov.mec.aghu.blococirurgico.procdiagterap.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.PdtDescObjetivaDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtDescObjetivaJnDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtDescricaoDAO;
import br.gov.mec.aghu.blococirurgico.procdiagterap.business.PdtDescObjetivaRN.PdtDescObjetivaRNExceptionCode;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.PdtAchado;
import br.gov.mec.aghu.model.PdtDescObjetiva;
import br.gov.mec.aghu.model.PdtDescObjetivaJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

/**
 * Classe responsável pelas regras de negócio relacionadas à entidade PdtDescObjetiva.
 * 
 * @author eschweigert
 *
 */
@Stateless
public class PdtDescObjetivaRN extends BaseBusiness {
@EJB
private IServidorLogadoFacade servidorLogadoFacade;

	private static final Log LOG = LogFactory.getLog(PdtDescObjetivaRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private PdtDescricaoDAO pdtDescricaoDAO;

	@Inject
	private PdtDescObjetivaJnDAO pdtDescObjetivaJnDAO;

	@Inject
	private PdtDescObjetivaDAO pdtDescObjetivaDAO;


	private static final long serialVersionUID = -5188167529597723953L;

	public enum PdtDescObjetivaRNExceptionCode implements BusinessExceptionCode {
		PDT_00105, PDT_00127;
	}

	@SuppressWarnings("ucd")
	public void persistirPdtDescObjetiva(final PdtDescObjetiva descricaoObjetiva) throws ApplicationBusinessException{
		
		if(descricaoObjetiva.getPdtDescricao() == null){
			descricaoObjetiva.setPdtDescricao(getPdtDescricaoDAO().obterPorChavePrimaria(descricaoObjetiva.getId().getDdtSeq()));
		}
		
		if(descricaoObjetiva.getId().getSeqp() == null){
			inserir(descricaoObjetiva);
		} else {
			atualizar(descricaoObjetiva);
		}
	}
	
	void inserir(final PdtDescObjetiva descricaoObjetiva)
			throws ApplicationBusinessException {
		executarAntesInserir(descricaoObjetiva);
		getPdtDescObjetivaDAO().persistir(descricaoObjetiva);
	}
	
	/**
	 * Trigger
	 * 
	 * ORADB: PDTT_DOB_BRI
	 */
	private void executarAntesInserir(final PdtDescObjetiva descricaoObjetiva) throws ApplicationBusinessException {
RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		descricaoObjetiva.setCriadoEm(new Date());
		
		Short seqP = getPdtDescObjetivaDAO().obterMaxSeqpPdtDescObjetiva(descricaoObjetiva.getId().getDdtSeq());
		if(seqP == null || Short.valueOf("0").equals(seqP)){
			descricaoObjetiva.getId().setSeqp(Short.valueOf("1"));
		} else {
			descricaoObjetiva.getId().setSeqp(++seqP);
		}
		
		if(servidorLogado == null){
			throw new ApplicationBusinessException(PdtDescObjetivaRNExceptionCode.PDT_00105);
		}
		descricaoObjetiva.setRapServidores(servidorLogado);
		
		// achado de uma descricao objetiva deve estar ATIVO
		validaSituacaoPdtAchado(descricaoObjetiva.getPdtAchado());
	}

	/**
	 * ORADB: PDTK_DOB_RN.RN_DOBP_VER_ACHADO
	 */
	public void validaSituacaoPdtAchado(final PdtAchado pdtAchado) throws ApplicationBusinessException{
		if(pdtAchado == null || DominioSituacao.I.equals(pdtAchado.getIndSituacao())){
			throw new ApplicationBusinessException(PdtDescObjetivaRNExceptionCode.PDT_00127);
		}
	}

	/**
	 * Atualiza instância de PdtDescricao.
	 * 
	 * @param newDescricao
	 * @param servidorLogado
	 */
	void atualizar(final PdtDescObjetiva descricaoObjetiva) throws ApplicationBusinessException {
		final PdtDescObjetivaDAO dao = getPdtDescObjetivaDAO();
		final PdtDescObjetiva oldDescricaoObjetiva = dao.obterOriginal(descricaoObjetiva);
		
		executarAntesAtualizar(descricaoObjetiva, oldDescricaoObjetiva);
		dao.atualizar(descricaoObjetiva);
		executarAposAtualizar(descricaoObjetiva, oldDescricaoObjetiva);
	}
	
	/**
	 * Trigger
	 * 
	 * ORADB: PDTT_DOB_BRU
	 */
	private void executarAntesAtualizar(final PdtDescObjetiva descricaoObjetiva, final PdtDescObjetiva oldDescricaoObjetiva) throws ApplicationBusinessException {
RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();

		if(servidorLogado == null){
			throw new ApplicationBusinessException(PdtDescObjetivaRNExceptionCode.PDT_00105);
		}
		
		// atualiza servidor que incluiu registro 
		descricaoObjetiva.setRapServidores(servidorLogado);
		
		if(!CoreUtil.igual(descricaoObjetiva.getPdtAchado(), oldDescricaoObjetiva.getPdtAchado())){
			validaSituacaoPdtAchado(descricaoObjetiva.getPdtAchado());
		}
	}

	/**
	 * Trigger
	 * 
	 * ORADB: PDTT_DOB_ARU
	 */
	private void executarAposAtualizar(final PdtDescObjetiva newDescricaoObjetiva, final PdtDescObjetiva oldDescricaoObjetiva) throws ApplicationBusinessException {
		if(!CoreUtil.igual(newDescricaoObjetiva.getCriadoEm(), oldDescricaoObjetiva.getCriadoEm()) ||
				!CoreUtil.igual(newDescricaoObjetiva.getComplemento(), oldDescricaoObjetiva.getComplemento()) ||
				!CoreUtil.igual(newDescricaoObjetiva.getId().getDdtSeq(), oldDescricaoObjetiva.getId().getDdtSeq()) ||
				!CoreUtil.igual(newDescricaoObjetiva.getPdtAchado(), oldDescricaoObjetiva.getPdtAchado()) ||
				!CoreUtil.igual(newDescricaoObjetiva.getRapServidores(), oldDescricaoObjetiva.getRapServidores())){
			final PdtDescObjetivaJn journal = createJournal(oldDescricaoObjetiva, DominioOperacoesJournal.UPD);
			getPdtDescObjetivaJnDAO().persistir(journal);
		}
	}
	
	
	public void excluirPdtDescObjetiva(PdtDescObjetiva descricaoObjetiva) throws ApplicationBusinessException{
		descricaoObjetiva = getPdtDescObjetivaDAO().obterPorChavePrimaria(descricaoObjetiva.getId());
		getPdtDescObjetivaDAO().remover(descricaoObjetiva);
		executarAposExcluir(descricaoObjetiva);
	}
	
	/**
	 * Trigger
	 * 
	 * ORADB: PDTT_DOB_ARD
	 */
	private void executarAposExcluir(final PdtDescObjetiva descricaoObjetiva) throws ApplicationBusinessException {
		final PdtDescObjetivaJn journal = createJournal(descricaoObjetiva, DominioOperacoesJournal.DEL);
		getPdtDescObjetivaJnDAO().persistir(journal);
	}

	private PdtDescObjetivaJn createJournal(
			final PdtDescObjetiva descricaoObjetiva,
			DominioOperacoesJournal dominio) {

		final PdtDescObjetivaJn journal = BaseJournalFactory
				.getBaseJournal(dominio, PdtDescObjetivaJn.class, servidorLogadoFacade.obterServidorLogado().getUsuario());
		
		journal.setCriadoEm(descricaoObjetiva.getCriadoEm());
		journal.setDdtSeq(descricaoObjetiva.getId().getDdtSeq());
		journal.setComplemento(descricaoObjetiva.getComplemento());
		journal.setSeqp(descricaoObjetiva.getId().getSeqp());
		journal.setSerMatricula(descricaoObjetiva.getRapServidores().getId().getMatricula());
		journal.setSerVinCodigo(descricaoObjetiva.getRapServidores().getId().getVinCodigo());
		journal.setPahDgrDptSeq(descricaoObjetiva.getPdtAchado().getId().getDgrDptSeq());
		journal.setPahDgrSeqp(descricaoObjetiva.getPdtAchado().getId().getDgrSeqp());
		journal.setPahSeqp(descricaoObjetiva.getPdtAchado().getId().getSeqp());
		
		return journal;
	}
	
	protected PdtDescObjetivaDAO getPdtDescObjetivaDAO() {
		return pdtDescObjetivaDAO;
	}
	
	protected PdtDescObjetivaJnDAO getPdtDescObjetivaJnDAO() {
		return pdtDescObjetivaJnDAO;
	}
	
	protected PdtDescricaoDAO getPdtDescricaoDAO() {
		return pdtDescricaoDAO;
	}
}