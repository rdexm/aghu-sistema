package br.gov.mec.aghu.blococirurgico.procdiagterap.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.PdtCidDescDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtCidDescJnDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtDescricaoDAO;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.PdtCidDesc;
import br.gov.mec.aghu.model.PdtCidDescJn;
import br.gov.mec.aghu.model.PdtComplementoPorCid;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

/**
 * Classe responsável pelas regras de negócio relacionadas à entidade PdtCidDesc.
 * 
 * @author eschweigert
 *
 */
@Stateless
public class PdtCidDescRN extends BaseBusiness {
@EJB
private IServidorLogadoFacade servidorLogadoFacade;

	private static final Log LOG = LogFactory.getLog(PdtCidDescRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private PdtDescricaoDAO pdtDescricaoDAO;

	@Inject
	private PdtCidDescDAO pdtCidDescDAO;

	@Inject
	private PdtCidDescJnDAO pdtCidDescJnDAO;


	private static final long serialVersionUID = -5188147529497723953L;

	public enum PdtCidDescRNExceptionCode implements BusinessExceptionCode {
		PDT_00128, PDT_00105;
	}

	@SuppressWarnings("ucd")
	public void persistirPdtCidDesc(final PdtCidDesc cidDesc) throws ApplicationBusinessException {
		if(cidDesc.getPdtDescricao() == null){
			cidDesc.setPdtDescricao(getPdtDescricaoDAO().obterPorChavePrimaria(cidDesc.getId().getDdtSeq()));
		}
		
		if(cidDesc.getId().getSeqp() == null){
			inserir(cidDesc);
		} else {
			atualizar(cidDesc);
		}
	}
	
	void inserir(final PdtCidDesc cidDesc) throws ApplicationBusinessException {
		executarAntesInserir(cidDesc);
		getPdtCidDescDAO().persistir(cidDesc);
	}
	
	/**
	 * Trigger
	 * 
	 * ORADB: PDTT_PID_BRI
	 */
	private void executarAntesInserir(final PdtCidDesc cidDesc) throws ApplicationBusinessException {
RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		cidDesc.setCriadoEm(new Date());
		
		Short seqP = getPdtCidDescDAO().obterMaxSeqpPdtCidDesc(cidDesc.getId().getDdtSeq());
		if(seqP == null || Short.valueOf("0").equals(seqP)){
			cidDesc.getId().setSeqp(Short.valueOf("1"));
		} else {
			cidDesc.getId().setSeqp(++seqP);
		}
		
		// atualiza servidor que incluiu registro
		if(servidorLogado == null){
			throw new ApplicationBusinessException(PdtCidDescRNExceptionCode.PDT_00105);
		}
		cidDesc.setRapServidores(servidorLogado);
		
		validaSituacaoComplementoPorCid(cidDesc.getPdtComplementoPorCid());
	}
	

	/**
	 * ORADB: PDTK_PID_RN.RN_PIDP_VER_COMPL
	 */
	public void validaSituacaoComplementoPorCid(final PdtComplementoPorCid complementoPorCid) throws ApplicationBusinessException{
		if(complementoPorCid != null && DominioSituacao.I.equals(complementoPorCid.getIndSituacao())){
			throw new ApplicationBusinessException(PdtCidDescRNExceptionCode.PDT_00128);
		}
	}
	
	/**
	 * Atualiza instância de PdtCidDesc.
	 */
	void atualizar(final PdtCidDesc cidDesc) throws ApplicationBusinessException {
		final PdtCidDescDAO dao = getPdtCidDescDAO();
		final PdtCidDesc oldCidDesc = dao.obterOriginal(cidDesc);
		
		executarAntesAtualizar(cidDesc, oldCidDesc);
		dao.atualizar(cidDesc);
		executarAposAtualizar(cidDesc, oldCidDesc);
	}
	
	/**
	 * Trigger
	 * 
	 * ORADB: PDTT_PID_BRU
	 */
	private void executarAntesAtualizar(final PdtCidDesc cidDesc, final PdtCidDesc oldCidDesc) throws ApplicationBusinessException {
RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();

		// atualiza servidor que incluiu registro
		if(servidorLogado == null){
			throw new ApplicationBusinessException(PdtCidDescRNExceptionCode.PDT_00105);
		}
		
		cidDesc.setRapServidores(servidorLogado);
		
		// Complemento por Cid deve estar ATIVO
		if(!CoreUtil.igual(cidDesc.getPdtComplementoPorCid(), oldCidDesc.getPdtComplementoPorCid())){
			validaSituacaoComplementoPorCid(cidDesc.getPdtComplementoPorCid());
		}
	}

	/**
	 * Trigger
	 * 
	 * ORADB: PDTT_PID_ARU
	 */
	private void executarAposAtualizar(final PdtCidDesc newCidDesc, final PdtCidDesc oldCidDesc) throws ApplicationBusinessException {
		if(!CoreUtil.igual(newCidDesc.getCriadoEm(), oldCidDesc.getCriadoEm()) ||
				!CoreUtil.igual(newCidDesc.getAghCid(), oldCidDesc.getAghCid()) ||
				!CoreUtil.igual(newCidDesc.getId().getDdtSeq(), oldCidDesc.getId().getDdtSeq()) ||
				!CoreUtil.igual(newCidDesc.getComplementoLivre(), oldCidDesc.getComplementoLivre()) ||
				!CoreUtil.igual(newCidDesc.getPdtComplementoPorCid(), oldCidDesc.getPdtComplementoPorCid()) ||
				!CoreUtil.igual(newCidDesc.getRapServidores(), oldCidDesc.getRapServidores())){
			final PdtCidDescJn journal = createJournal(oldCidDesc, DominioOperacoesJournal.UPD);
			getPdtCidDescJnDAO().persistir(journal);
		}
	}
	
	
	public void excluir(PdtCidDesc cidDesc) throws ApplicationBusinessException{
		cidDesc = getPdtCidDescDAO().obterPorChavePrimaria(cidDesc.getId());
		getPdtCidDescDAO().remover(cidDesc);
		executarAposExcluir(cidDesc);
	}
	
	/**
	 * Trigger
	 * 
	 * ORADB: PDTT_PID_ARD
	 */
	private void executarAposExcluir(final PdtCidDesc cidDesc) throws ApplicationBusinessException {
		final PdtCidDescJn journal = createJournal(cidDesc, DominioOperacoesJournal.DEL);
		getPdtCidDescJnDAO().persistir(journal);
	}

	private PdtCidDescJn createJournal(
			final PdtCidDesc cidDesc,
			DominioOperacoesJournal dominio) {

		final PdtCidDescJn journal = BaseJournalFactory
				.getBaseJournal(dominio, PdtCidDescJn.class, servidorLogadoFacade.obterServidorLogado().getUsuario());
		
		journal.setCriadoEm(cidDesc.getCriadoEm());
		journal.setDdtSeq(cidDesc.getId().getDdtSeq());
		journal.setCidSeq(cidDesc.getAghCid().getSeq());
		journal.setSeqp(cidDesc.getId().getSeqp());
		journal.setSerMatricula(cidDesc.getRapServidores().getId().getMatricula());
		journal.setSerVinCodigo(cidDesc.getRapServidores().getId().getVinCodigo());
		journal.setComplementoLivre(cidDesc.getComplementoLivre());
		
		if(cidDesc.getPdtComplementoPorCid() != null){
			journal.setCxcSeqp(cidDesc.getPdtComplementoPorCid().getId().getSeqp());
		}
		
		
		return journal;
	}
	
	protected PdtCidDescJnDAO getPdtCidDescJnDAO() {
		return pdtCidDescJnDAO;
	}
	
	protected PdtCidDescDAO getPdtCidDescDAO() {
		return pdtCidDescDAO;
	}

	protected PdtDescricaoDAO getPdtDescricaoDAO() {
		return pdtDescricaoDAO;
	}
}