package br.gov.mec.aghu.blococirurgico.procdiagterap.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.cadastroapoio.business.IBlocoCirurgicoCadastroApoioFacade;
import br.gov.mec.aghu.blococirurgico.dao.PdtTecnicaPorProcJnDAO;
import br.gov.mec.aghu.model.PdtTecnicaPorProc;
import br.gov.mec.aghu.model.PdtTecnicaPorProcJn;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class PdtTecnicaPorProcRN extends BaseBusiness {
@EJB
private IServidorLogadoFacade servidorLogadoFacade;

	private static final Log LOG = LogFactory.getLog(PdtTecnicaPorProcRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private PdtTecnicaPorProcJnDAO pdtTecnicaPorProcJnDAO;


	@EJB
	private IBlocoCirurgicoProcDiagTerapFacade iBlocoCirurgicoProcDiagTerapFacade;

	@EJB
	private IBlocoCirurgicoCadastroApoioFacade iBlocoCirurgicoCadastroApoioFacade;

	private static final long serialVersionUID = 7462841666120297724L;

	/**
	 * ORADB TRIGGER PDTT_TPX_ARD
	 */
	public void posDeletePdtTecnicaPorProc(PdtTecnicaPorProc tecnicaPorProc){
		PdtTecnicaPorProc original = getBlocoCirurgicoProcDiagTerapFacade().obterOriginalPdtTecnicaPorProc(tecnicaPorProc);
		
		PdtTecnicaPorProcJn tecnicaPorProcJn = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.DEL, PdtTecnicaPorProcJn.class, servidorLogadoFacade.obterServidorLogado().getUsuario());
		tecnicaPorProcJn.setDteSeq(original.getId().getDteSeq());
		tecnicaPorProcJn.setDptSeq(original.getId().getDptSeq());
		tecnicaPorProcJn.setCriadoEm(original.getCriadoEm());
		tecnicaPorProcJn.setSerMatricula(original.getRapServidores().getId().getMatricula());
		tecnicaPorProcJn.setSerVinCodigo(original.getRapServidores().getId().getVinCodigo());
		
		
		getPdtTecnicaPorProcJnDAO().persistir(tecnicaPorProcJn);
	}
	
	/**
	 * ORADB TRIGGER PDTT_TPX_BRI
	 * @throws ApplicationBusinessException 
	 */
	public void antesInserirPdtTecnicaPorProc() throws ApplicationBusinessException {
		getBlocoCirurgicoCadastroApoioFacade().atualizarServidor();
		
	}

	private IBlocoCirurgicoCadastroApoioFacade getBlocoCirurgicoCadastroApoioFacade() {
		return iBlocoCirurgicoCadastroApoioFacade;
	}

	private IBlocoCirurgicoProcDiagTerapFacade getBlocoCirurgicoProcDiagTerapFacade() {
		return iBlocoCirurgicoProcDiagTerapFacade;
	}
	
	private PdtTecnicaPorProcJnDAO getPdtTecnicaPorProcJnDAO() {
		return pdtTecnicaPorProcJnDAO;
	}
}
