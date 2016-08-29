package br.gov.mec.aghu.blococirurgico.procdiagterap.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.PdtDescricaoDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtInstrDescDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtInstrDescJnDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtInstrumentalDAO;
import br.gov.mec.aghu.model.PdtInstrDesc;
import br.gov.mec.aghu.model.PdtInstrDescJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

/**
 * Classe responsável pelas regras de negócio relacionadas à entidade PdtInstrDesc.
 * 
 * @author eschweigert
 *
 */
@Stateless
public class PdtInstrDescRN extends BaseBusiness {
@EJB
private IServidorLogadoFacade servidorLogadoFacade;

	private static final Log LOG = LogFactory.getLog(PdtInstrDescRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private PdtDescricaoDAO pdtDescricaoDAO;

	@Inject
	private PdtInstrDescJnDAO pdtInstrDescJnDAO;

	@Inject
	private PdtInstrDescDAO pdtInstrDescDAO;

	@Inject
	private PdtInstrumentalDAO pdtInstrumentalDAO;


	private static final long serialVersionUID = -5188149529497723953L;

	public enum PdtInstrDescRNExceptionCode implements BusinessExceptionCode {
		PDT_00105;
	}

	public void inserir(final PdtInstrDesc instrucaoDescritiva) throws ApplicationBusinessException {
		executarAntesInserir(instrucaoDescritiva);
		getPdtInstrDescDAO().persistir(instrucaoDescritiva);
	}
	
	/**
	 * Trigger
	 * 
	 * ORADB: PDTT_ISD_BRI
	 */
	private void executarAntesInserir(final PdtInstrDesc instrucaoDescritiva) throws ApplicationBusinessException {
RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();

		if(instrucaoDescritiva.getPdtDescricao() == null){
			instrucaoDescritiva.setPdtDescricao(getPdtDescricaoDAO().obterPorChavePrimaria(instrucaoDescritiva.getId().getDdtSeq()));
		}
		
		if(instrucaoDescritiva.getPdtInstrumental() == null){
			instrucaoDescritiva.setPdtInstrumental(getPdtInstrumentalDAO().obterPorChavePrimaria(instrucaoDescritiva.getId().getPinSeq()));
		}
		
		instrucaoDescritiva.setCriadoEm(new Date());
		
		// atualiza servidor que incluiu registro
		if(servidorLogado == null){
			throw new ApplicationBusinessException(PdtInstrDescRNExceptionCode.PDT_00105);
		}
		instrucaoDescritiva.setRapServidores(servidorLogado);
	}
	
	/**
	 * Atualiza instância de PdtInstrDesc.
	 */
	@SuppressWarnings("ucd")
	public void atualizar(final PdtInstrDesc instrucaoDescritiva) throws ApplicationBusinessException {
		final PdtInstrDescDAO dao = getPdtInstrDescDAO();
		final PdtInstrDesc oldInstrucaoDescritiva = dao.obterOriginal(instrucaoDescritiva);
		
		executarAntesAtualizar(instrucaoDescritiva);
		dao.atualizar(instrucaoDescritiva);
		executarAposAtualizar(instrucaoDescritiva, oldInstrucaoDescritiva);
	}
	
	/**
	 * Trigger
	 * 
	 * ORADB: PDTT_ISD_BRU
	 */
	private void executarAntesAtualizar(final PdtInstrDesc instrucaoDescritiva) throws ApplicationBusinessException {
RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();

		if(instrucaoDescritiva.getPdtDescricao() == null){
			instrucaoDescritiva.setPdtDescricao(getPdtDescricaoDAO().obterPorChavePrimaria(instrucaoDescritiva.getId().getDdtSeq()));
		}
		
		if(instrucaoDescritiva.getPdtInstrumental() == null){
			instrucaoDescritiva.setPdtInstrumental(getPdtInstrumentalDAO().obterPorChavePrimaria(instrucaoDescritiva.getId().getPinSeq()));
		}
		
		// atualiza servidor que incluiu registro
		if(servidorLogado == null){
			throw new ApplicationBusinessException(PdtInstrDescRNExceptionCode.PDT_00105);
		}
		
		instrucaoDescritiva.setRapServidores(servidorLogado);
	}

	/**
	 * Trigger
	 * 
	 * ORADB: PDTT_ISD_ARU
	 */
	private void executarAposAtualizar(final PdtInstrDesc newInstrucaoDescritiva, final PdtInstrDesc oldInstrucaoDescritiva) throws ApplicationBusinessException {
		if(!CoreUtil.igual(newInstrucaoDescritiva.getCriadoEm(), oldInstrucaoDescritiva.getCriadoEm()) ||
				!CoreUtil.igual(newInstrucaoDescritiva.getId().getDdtSeq(), oldInstrucaoDescritiva.getId().getDdtSeq()) ||
				!CoreUtil.igual(newInstrucaoDescritiva.getId().getPinSeq(), oldInstrucaoDescritiva.getId().getPinSeq()) ||
				!CoreUtil.igual(newInstrucaoDescritiva.getRapServidores(), oldInstrucaoDescritiva.getRapServidores())){
			final PdtInstrDescJn journal = createJournal(oldInstrucaoDescritiva, DominioOperacoesJournal.UPD);
			getPdtInstrDescJnDAO().persistir(journal);
		}
	}
	
	
	public void excluir(PdtInstrDesc instrucaoDescritiva) throws ApplicationBusinessException{
		instrucaoDescritiva = getPdtInstrDescDAO().obterPorChavePrimaria(instrucaoDescritiva.getId());
		getPdtInstrDescDAO().remover(instrucaoDescritiva);
		executarAposExcluir(instrucaoDescritiva);
	}
	
	/**
	 * Trigger
	 * 
	 * ORADB: PDTT_ISD_ARD
	 */
	private void executarAposExcluir(final PdtInstrDesc instrucaoDescritiva) throws ApplicationBusinessException {
		final PdtInstrDescJn journal = createJournal(instrucaoDescritiva, DominioOperacoesJournal.DEL);
		getPdtInstrDescJnDAO().persistir(journal);
	}

	private PdtInstrDescJn createJournal(
			final PdtInstrDesc instrucaoDescritiva,
			DominioOperacoesJournal dominio) {

		final PdtInstrDescJn journal = BaseJournalFactory
				.getBaseJournal(dominio, PdtInstrDescJn.class, servidorLogadoFacade.obterServidorLogado().getUsuario());
		
		journal.setCriadoEm(instrucaoDescritiva.getCriadoEm());
		journal.setDdtSeq(instrucaoDescritiva.getId().getDdtSeq());
		journal.setPinSeq(instrucaoDescritiva.getId().getPinSeq());
		journal.setSerMatricula(instrucaoDescritiva.getRapServidores().getId().getMatricula());
		journal.setSerVinCodigo(instrucaoDescritiva.getRapServidores().getId().getVinCodigo());
		
		return journal;
	}
	
	protected PdtInstrDescJnDAO getPdtInstrDescJnDAO() {
		return pdtInstrDescJnDAO;
	}
	
	protected PdtInstrDescDAO getPdtInstrDescDAO() {
		return pdtInstrDescDAO;
	}

	protected PdtDescricaoDAO getPdtDescricaoDAO() {
		return pdtDescricaoDAO;
	}

	protected PdtInstrumentalDAO getPdtInstrumentalDAO() {
		return pdtInstrumentalDAO;
	}
}