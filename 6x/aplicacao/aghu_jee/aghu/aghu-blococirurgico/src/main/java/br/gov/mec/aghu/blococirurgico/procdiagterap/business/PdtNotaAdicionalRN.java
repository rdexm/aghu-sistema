package br.gov.mec.aghu.blococirurgico.procdiagterap.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.PdtDescricaoDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtNotaAdicionalDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtNotaAdicionalJnDAO;
import br.gov.mec.aghu.blococirurgico.procdiagterap.business.PdtNotaAdicionalRN.PdtNotaAdicionalRNExceptionCode;
import br.gov.mec.aghu.model.PdtNotaAdicional;
import br.gov.mec.aghu.model.PdtNotaAdicionalJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;


/**
 * Classe responsável pelas regras de negócio relacionadas à entidade PdtNotaAdicional.
 * 
 * @author eschweigert
 *
 */
@Stateless
public class PdtNotaAdicionalRN extends BaseBusiness {
@EJB
private IServidorLogadoFacade servidorLogadoFacade;

	private static final Log LOG = LogFactory.getLog(PdtNotaAdicionalRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private PdtNotaAdicionalJnDAO pdtNotaAdicionalJnDAO;

	@Inject
	private PdtDescricaoDAO pdtDescricaoDAO;

	@Inject
	private PdtNotaAdicionalDAO pdtNotaAdicionalDAO;


	private static final long serialVersionUID = -5188167529497723953L;

	public enum PdtNotaAdicionalRNExceptionCode implements BusinessExceptionCode {
		PDT_00105;
	}
	
	public void persistirPdtNotaAdicional(final PdtNotaAdicional notaAdicional) throws ApplicationBusinessException{
		if(notaAdicional.getPdtDescricao() == null){
			notaAdicional.setPdtDescricao(getPdtDescricaoDAO().obterPorChavePrimaria(notaAdicional.getId().getDdtSeq()));
		}
		
		if(notaAdicional.getId().getSeqp() == null){
			inserir(notaAdicional);
		} else {
			atualizar(notaAdicional);
		}
	}
	
	@SuppressWarnings("ucd")
	void inserir(final PdtNotaAdicional notaAdicional)
			throws ApplicationBusinessException {
		executarAntesInserir(notaAdicional);
		getPdtNotaAdicionalDAO().persistir(notaAdicional);
	}
	
	/**
	 * Trigger
	 * 
	 * ORADB: PDTT_NAD_BRI
	 */
	private void executarAntesInserir(final PdtNotaAdicional notaAdicional) throws ApplicationBusinessException {
RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		notaAdicional.setCriadoEm(new Date());
		
		Short seqP = getPdtNotaAdicionalDAO().obterMaxSeqpPdtNotaAdicional(notaAdicional.getId().getDdtSeq());
		if(seqP == null || Short.valueOf("0").equals(seqP)){
			notaAdicional.getId().setSeqp(Short.valueOf("1"));
			
		} else {
			notaAdicional.getId().setSeqp(++seqP);
		}
		
		if(servidorLogado == null){
			throw new ApplicationBusinessException(PdtNotaAdicionalRNExceptionCode.PDT_00105);
		}
		
		notaAdicional.setRapServidores(servidorLogado);
	}
	

	/**
	 * Atualiza instância de PdtDescricao.
	 * 
	 * @param newDescricao
	 * @param servidorLogado
	 */
	@SuppressWarnings("ucd")
	void atualizar(final PdtNotaAdicional notaAdicional) throws ApplicationBusinessException {
		final PdtNotaAdicionalDAO dao = getPdtNotaAdicionalDAO();
		final PdtNotaAdicional oldNotaAdicional = dao.obterOriginal(notaAdicional);
		
		executarAntesAtualizar(notaAdicional);
		dao.atualizar(notaAdicional);
		executarAposAtualizar(notaAdicional, oldNotaAdicional);
	}
	
	/**
	 * Trigger
	 * 
	 * ORADB: PDTT_NAD_BRU
	 */
	private void executarAntesAtualizar(final PdtNotaAdicional notaAdicional) throws ApplicationBusinessException {
RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();

		if(servidorLogado == null){
			throw new ApplicationBusinessException(PdtNotaAdicionalRNExceptionCode.PDT_00105);
		}
		
		// Atualiza servidor que atualizou o registro
		notaAdicional.setRapServidores(servidorLogado);
	}

	/**
	 * Trigger
	 * 
	 * ORADB: PDTT_NAD_BRU
	 */
	private void executarAposAtualizar(final PdtNotaAdicional newNotaAdicional, final PdtNotaAdicional oldNotaAdicional) throws ApplicationBusinessException {
		if(!CoreUtil.igual(newNotaAdicional.getCriadoEm(), oldNotaAdicional.getCriadoEm()) ||
				!CoreUtil.igual(newNotaAdicional.getNotaAdicional(), oldNotaAdicional.getNotaAdicional()) ||
				!CoreUtil.igual(newNotaAdicional.getId().getDdtSeq(), oldNotaAdicional.getId().getDdtSeq()) ||
				!CoreUtil.igual(newNotaAdicional.getRapServidores(), oldNotaAdicional.getRapServidores())){
			final PdtNotaAdicionalJn journal = createJournal(oldNotaAdicional, DominioOperacoesJournal.UPD);
			getPdtNotaAdicionalJnDAO().persistir(journal);
		}
	}
	
	
	public void excluirPdtNotaAdicional(PdtNotaAdicional notaAdicional) throws ApplicationBusinessException{
		notaAdicional = getPdtNotaAdicionalDAO().obterPorChavePrimaria(notaAdicional.getId());
		getPdtNotaAdicionalDAO().remover(notaAdicional);
		executarAposExcluir(notaAdicional);
	}
	
	/**
	 * Trigger
	 * 
	 * ORADB: PDTT_NAD_ARD
	 */
	private void executarAposExcluir(final PdtNotaAdicional notaAdicional) throws ApplicationBusinessException {
		final PdtNotaAdicionalJn journal = createJournal(notaAdicional, DominioOperacoesJournal.DEL);
		getPdtNotaAdicionalJnDAO().persistir(journal);
	}

	private PdtNotaAdicionalJn createJournal(
			final PdtNotaAdicional notaAdicional,
			DominioOperacoesJournal dominio) {

		final PdtNotaAdicionalJn journal = BaseJournalFactory
				.getBaseJournal(dominio, PdtNotaAdicionalJn.class, servidorLogadoFacade.obterServidorLogado().getUsuario());
		
		journal.setCriadoEm(notaAdicional.getCriadoEm());
		journal.setDdtSeq(notaAdicional.getId().getDdtSeq());
		journal.setNotaAdicional(notaAdicional.getNotaAdicional());
		journal.setSeqp(notaAdicional.getId().getSeqp());
		journal.setSerMatricula(notaAdicional.getRapServidores().getId().getMatricula());
		journal.setSerVinCodigo(notaAdicional.getRapServidores().getId().getVinCodigo());
		
		return journal;
	}

	protected PdtNotaAdicionalDAO getPdtNotaAdicionalDAO() {
		return pdtNotaAdicionalDAO;
	}
	
	protected PdtNotaAdicionalJnDAO getPdtNotaAdicionalJnDAO() {
		return pdtNotaAdicionalJnDAO;
	}

	protected PdtDescricaoDAO getPdtDescricaoDAO() {
		return pdtDescricaoDAO;
	}
}