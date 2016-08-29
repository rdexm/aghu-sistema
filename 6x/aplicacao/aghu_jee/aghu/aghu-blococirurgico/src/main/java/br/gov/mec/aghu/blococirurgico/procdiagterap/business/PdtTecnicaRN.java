package br.gov.mec.aghu.blococirurgico.procdiagterap.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.PdtTecnicaDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtTecnicaJnDAO;
import br.gov.mec.aghu.model.PdtTecnica;
import br.gov.mec.aghu.model.PdtTecnicaJn;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

/**
 * #24722
 */
@Stateless
public class PdtTecnicaRN extends BaseBusiness {
@EJB
private IServidorLogadoFacade servidorLogadoFacade;

	private static final Log LOG = LogFactory.getLog(PdtTecnicaRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private PdtTecnicaJnDAO pdtTecnicaJnDAO;

	@Inject
	private PdtTecnicaDAO pdtTecnicaDAO;


	private static final long serialVersionUID = -1974550544218417288L;

	protected enum TecnicasRNExceptionCode implements BusinessExceptionCode {
		PDT00109;
	}
	
	public void preUpdatePdtTecnica(PdtTecnica tecnica) throws ApplicationBusinessException {
		PdtTecnica original = getPdtTecnicaDAO().obterOriginal(tecnica);
		if (CoreUtil.modificados(tecnica.getDescricao(), original.getDescricao())) {
			throw new ApplicationBusinessException(TecnicasRNExceptionCode.PDT00109);
		}
	}
	
	public void posUpdatePdtTecnica(PdtTecnica tecnica) {
		PdtTecnica original = getPdtTecnicaDAO().obterOriginal(tecnica);
		if (CoreUtil.modificados(tecnica.getSeq(), original.getSeq()) || CoreUtil.modificados(tecnica.getDescricao(), original.getDescricao()) || CoreUtil.modificados(tecnica.getIndSituacao(), original.getIndSituacao()) || CoreUtil.modificados(tecnica.getCriadoEm(), original.getCriadoEm()) || CoreUtil.modificados(tecnica.getRapServidores().getId().getMatricula(), original.getRapServidores().getId().getMatricula()) || CoreUtil.modificados(tecnica.getRapServidores().getId().getVinCodigo(), original.getRapServidores().getId().getVinCodigo())) {
			PdtTecnicaJn tecnicaJn = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.UPD, PdtTecnicaJn.class, servidorLogadoFacade.obterServidorLogado().getUsuario());
			tecnicaJn.setSeq(original.getSeq());
			tecnicaJn.setDescricao(original.getDescricao());
			tecnicaJn.setIndSituacao(original.getIndSituacao().toString());
			tecnicaJn.setCriadoEm(original.getCriadoEm());
			tecnicaJn.setSerMatricula(original.getRapServidores().getId().getMatricula());
			tecnicaJn.setSerVinCodigo(original.getRapServidores().getId().getVinCodigo());
			getPdtTecnicaJnDAO().persistir(tecnicaJn);
		}
	}


	protected PdtTecnicaDAO getPdtTecnicaDAO() {
		return pdtTecnicaDAO;
	}
	
	protected PdtTecnicaJnDAO getPdtTecnicaJnDAO() {
		return pdtTecnicaJnDAO;
	}		
}
