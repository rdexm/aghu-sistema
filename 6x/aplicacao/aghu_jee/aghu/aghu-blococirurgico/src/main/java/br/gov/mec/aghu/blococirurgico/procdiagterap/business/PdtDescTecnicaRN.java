package br.gov.mec.aghu.blococirurgico.procdiagterap.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.PdtDescTecnicaDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtDescTecnicaJnDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtDescricaoDAO;
import br.gov.mec.aghu.blococirurgico.procdiagterap.business.PdtDescTecnicaRN.PdtDescTecnicaRNExceptionCode;
import br.gov.mec.aghu.model.PdtDescTecnica;
import br.gov.mec.aghu.model.PdtDescTecnicaJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

/**
 * Classe responsável pelas regras de negócio relacionadas à entidade PdtDescTecnica.
 * 
 * @author eschweigert
 *
 */
@Stateless
public class PdtDescTecnicaRN extends BaseBusiness {
@EJB
private IServidorLogadoFacade servidorLogadoFacade;

	private static final Log LOG = LogFactory.getLog(PdtDescTecnicaRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private PdtDescricaoDAO pdtDescricaoDAO;

	@Inject
	private PdtDescTecnicaJnDAO pdtDescTecnicaJnDAO;

	@Inject
	private PdtDescTecnicaDAO pdtDescTecnicaDAO;


	private static final long serialVersionUID = -5188147529497723953L;

	public enum PdtDescTecnicaRNExceptionCode implements BusinessExceptionCode {
		PDT_00126, PDT_00105;
	}

	public void persistirPdtDescTecnica(final PdtDescTecnica descricaoTecnica) throws ApplicationBusinessException {
		PdtDescTecnica descricaoTecnicaExistente = getPdtDescTecnicaDAO().obterPorChavePrimaria(descricaoTecnica.getDdtSeq());
		
		if (descricaoTecnicaExistente == null) {
			inserir(descricaoTecnica);
		} else {
			atualizar(descricaoTecnica);
		}
	}
	
	void inserir(final PdtDescTecnica descricaoTecnica) throws ApplicationBusinessException {
		executarAntesInserir(descricaoTecnica);
		getPdtDescTecnicaDAO().persistir(descricaoTecnica);
	}
	
	/**
	 * Trigger
	 * 
	 * ORADB: PDTT_DSP_BRI
	 */
	private void executarAntesInserir(final PdtDescTecnica descricaoTecnica) throws ApplicationBusinessException {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		descricaoTecnica.setCriadoEm(new Date());
		
		// atualiza servidor que incluiu registro
		if(servidorLogado == null){
			throw new ApplicationBusinessException(PdtDescTecnicaRNExceptionCode.PDT_00105);
		}
		descricaoTecnica.setServidor(servidorLogado);
	}
	
	/**
	 * Atualiza instância de PdtDescTecnica.
	 */
	void atualizar(PdtDescTecnica descricaoTecnica) throws ApplicationBusinessException {
		final PdtDescTecnicaDAO dao = getPdtDescTecnicaDAO();
		final PdtDescTecnica oldCidDesc = dao.obterOriginal(descricaoTecnica);
		
		executarAntesAtualizar(descricaoTecnica, oldCidDesc);
		descricaoTecnica = dao.merge(descricaoTecnica);
		dao.atualizar(descricaoTecnica);
		executarAposAtualizar(descricaoTecnica, oldCidDesc);
	}
	
	/**
	 * Trigger
	 * 
	 * ORADB: PDTT_DSP_BRU
	 */
	private void executarAntesAtualizar(final PdtDescTecnica descricaoTecnica, final PdtDescTecnica oldDescricaoTecnica) throws ApplicationBusinessException {
RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();

		// atualiza servidor que incluiu registro
		if(servidorLogado == null){
			throw new ApplicationBusinessException(PdtDescTecnicaRNExceptionCode.PDT_00105);
		}
		
		// Somente o Usuário que Incluiu a Descrição Poderá Alterar a Descrição Técnica.
		if(!CoreUtil.igual(oldDescricaoTecnica.getServidor(), servidorLogado)){
			throw new ApplicationBusinessException(PdtDescTecnicaRNExceptionCode.PDT_00126);
		}
		
		descricaoTecnica.setServidor(servidorLogado);
	}

	/**
	 * Trigger
	 * 
	 * ORADB: PDTT_DSP_ARU
	 */
	private void executarAposAtualizar(final PdtDescTecnica newDescricaoTecnica, final PdtDescTecnica oldDescricaoTecnica) throws ApplicationBusinessException {
		if(!CoreUtil.igual(newDescricaoTecnica.getCriadoEm(), oldDescricaoTecnica.getCriadoEm()) ||
				!CoreUtil.igual(newDescricaoTecnica.getDescricao(), oldDescricaoTecnica.getDescricao()) ||
				!CoreUtil.igual(newDescricaoTecnica.getDdtSeq(), oldDescricaoTecnica.getDdtSeq()) ||
				!CoreUtil.igual(newDescricaoTecnica.getServidor(), oldDescricaoTecnica.getServidor())){
			final PdtDescTecnicaJn journal = createJournal(oldDescricaoTecnica, DominioOperacoesJournal.UPD);
			getPdtDescTecnicaJnDAO().persistir(journal);
		}
	}
	
	
	public void excluir(PdtDescTecnica descricaoTecnica) throws ApplicationBusinessException{
		PdtDescTecnica descTecnica = getPdtDescTecnicaDAO().obterPorChavePrimaria(descricaoTecnica.getSeq());
		getPdtDescTecnicaDAO().remover(descTecnica);
		executarAposExcluir(descTecnica);
	}
	
	/**
	 * Trigger
	 * 
	 * ORADB: PDTT_DSP_ARD
	 */
	private void executarAposExcluir(final PdtDescTecnica descricaoTecnica) throws ApplicationBusinessException {
		final PdtDescTecnicaJn journal = createJournal(descricaoTecnica, DominioOperacoesJournal.DEL);
		getPdtDescTecnicaJnDAO().persistir(journal);
	}

	private PdtDescTecnicaJn createJournal(
			final PdtDescTecnica descricaoTecnica,
			DominioOperacoesJournal dominio) {

		final PdtDescTecnicaJn journal = BaseJournalFactory
				.getBaseJournal(dominio, PdtDescTecnicaJn.class, servidorLogadoFacade.obterServidorLogado().getUsuario());
		
		journal.setCriadoEm(descricaoTecnica.getCriadoEm());
		journal.setDdtSeq(descricaoTecnica.getDdtSeq());
		journal.setDescricao(descricaoTecnica.getDescricao());
		journal.setSerMatricula(descricaoTecnica.getServidor().getId().getMatricula());
		journal.setSerVinCodigo(descricaoTecnica.getServidor().getId().getVinCodigo());
		
		return journal;
	}
	
	protected PdtDescTecnicaJnDAO getPdtDescTecnicaJnDAO() {
		return pdtDescTecnicaJnDAO;
	}
	
	protected PdtDescTecnicaDAO getPdtDescTecnicaDAO() {
		return pdtDescTecnicaDAO;
	}

	protected PdtDescricaoDAO getPdtDescricaoDAO() {
		return pdtDescricaoDAO;
	}
}