package br.gov.mec.aghu.blococirurgico.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcExtratoCirurgiaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcExtratoCirurgiaJnDAO;
import br.gov.mec.aghu.model.MbcExtratoCirurgia;
import br.gov.mec.aghu.model.MbcExtratoCirurgiaJn;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class ExtratoCirurgiaRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(ExtratoCirurgiaRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private MbcExtratoCirurgiaJnDAO mbcExtratoCirurgiaJnDAO;

	@Inject
	private MbcExtratoCirurgiaDAO mbcExtratoCirurgiaDAO;


	private static final long serialVersionUID = 5722305668331041985L;

	protected MbcExtratoCirurgiaDAO getMbcExtratoCirurgiaDAO() {
		return mbcExtratoCirurgiaDAO;
	}

	private MbcExtratoCirurgiaJnDAO getMbcExtratoCirurgiaJnDAO() {
		return mbcExtratoCirurgiaJnDAO;
	}

	/**
	 * ORADB MBCT_ECR_ARU
	 */
	public void posUpdateMbcExtratoCirurgia(MbcExtratoCirurgia extratoCirurgia) {
		MbcExtratoCirurgia original = getMbcExtratoCirurgiaDAO().obterOriginal(extratoCirurgia.getId());
		MbcExtratoCirurgiaJn extratoCirurgiaJn = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.UPD, MbcExtratoCirurgiaJn.class, servidorLogadoFacade.obterServidorLogado().getUsuario());
		extratoCirurgiaJn.setCrgSeq(original.getId().getCrgSeq());
		extratoCirurgiaJn.setSeqp(original.getId().getSeqp());
		extratoCirurgiaJn.setSerMatricula(original.getServidor().getId().getMatricula());
		extratoCirurgiaJn.setSerVinCodigo(original.getServidor().getId().getVinCodigo());
		extratoCirurgiaJn.setSituacaoCirg(original.getSituacaoCirg().toString());
		extratoCirurgiaJn.setCriadoEm(original.getCriadoEm());
		getMbcExtratoCirurgiaJnDAO().persistir(extratoCirurgiaJn);
	}
	
}
