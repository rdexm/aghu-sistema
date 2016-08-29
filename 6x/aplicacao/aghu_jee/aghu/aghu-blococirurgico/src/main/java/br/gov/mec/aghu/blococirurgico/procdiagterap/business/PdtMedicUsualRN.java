package br.gov.mec.aghu.blococirurgico.procdiagterap.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.PdtMedicUsualDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtMedicUsualJnDAO;
import br.gov.mec.aghu.model.PdtMedicUsual;
import br.gov.mec.aghu.model.PdtMedicUsualJn;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class PdtMedicUsualRN extends BaseBusiness{

	private static final Log LOG = LogFactory.getLog(PdtMedicUsualRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@Inject
	private PdtMedicUsualJnDAO pdtMedicUsualJnDAO;

	@Inject
	private PdtMedicUsualDAO pdtMedicUsualDAO;


	/**
	 * 
	 */
	private static final long serialVersionUID = -7828910785674062431L;

	/**
	 * ORADB 
	 * 
	 * #24723  RN1
	 * TRIGGER "AGH".PDTT_MUN_ARD
	 */
	public void posDeletePdtMedicUsual(PdtMedicUsual pdtMedicUsualDelecao) {
		PdtMedicUsual original = getPdtMedicUsualDAO().obterOriginal(pdtMedicUsualDelecao);
		
		PdtMedicUsualJn pdtMedicUsualJn = BaseJournalFactory.getBaseJournal
			(DominioOperacoesJournal.DEL, PdtMedicUsualJn.class, servidorLogadoFacade.obterServidorLogado().getUsuario());
		pdtMedicUsualJn.setMedMatCodigo(original.getId().getMedMatCodigo());
		pdtMedicUsualJn.setUnfSeq(original.getId().getUnfSeq());
		pdtMedicUsualJn.setCriadoEm(original.getCriadoEm());
		pdtMedicUsualJn.setSerMatricula(original.getRapServidores().getId().getMatricula());
		pdtMedicUsualJn.setSerVinCodigo(original.getRapServidores().getId().getVinCodigo());
		
		getPdtMedicUsualJnDAO().persistir(pdtMedicUsualJn);
		
	}

	protected PdtMedicUsualDAO getPdtMedicUsualDAO() {
		return pdtMedicUsualDAO;
	}

	protected PdtMedicUsualJnDAO getPdtMedicUsualJnDAO() {
		return pdtMedicUsualJnDAO;
	}
}
