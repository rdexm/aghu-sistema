package br.gov.mec.aghu.blococirurgico.procdiagterap.business;


import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.PdtCidPorProcDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtCidPorProcJnDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtComplementoPorCidDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtComplementoPorCidJnDAO;
import br.gov.mec.aghu.model.PdtCidPorProc;
import br.gov.mec.aghu.model.PdtCidPorProcJn;
import br.gov.mec.aghu.model.PdtComplementoPorCid;
import br.gov.mec.aghu.model.PdtComplementoPorCidJn;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class PdtCidPorProcRN extends BaseBusiness {
@EJB
private IServidorLogadoFacade servidorLogadoFacade;

	private static final Log LOG = LogFactory.getLog(PdtCidPorProcRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private PdtCidPorProcJnDAO pdtCidPorProcJnDAO;

	@Inject
	private PdtComplementoPorCidDAO pdtComplementoPorCidDAO;

	@Inject
	private PdtCidPorProcDAO pdtCidPorProcDAO;

	@Inject
	private PdtComplementoPorCidJnDAO pdtComplementoPorCidJnDAO;


	private static final long serialVersionUID = -1974550544218417288L;

	/**
	 * @ORADB PDTT_CXP_ARU
	 */
	public void posUpdatePdtCidPorProc(PdtCidPorProc cidPorProc) {
		PdtCidPorProc original = getPdtCidPorProcDAO().obterOriginal(cidPorProc);
		
		if(CoreUtil.modificados(cidPorProc.getId().getDptSeq(), original.getId().getDptSeq()) ||
				CoreUtil.modificados(cidPorProc.getId().getCidSeq(), original.getId().getCidSeq()) ||
				CoreUtil.modificados(cidPorProc.getCriadoEm(), original.getCriadoEm()) ||
				CoreUtil.modificados(cidPorProc.getRapServidores().getId().getMatricula(), original.getRapServidores().getId().getMatricula()) ||
				CoreUtil.modificados(cidPorProc.getRapServidores().getId().getVinCodigo(), original.getRapServidores().getId().getVinCodigo()) ||
				CoreUtil.modificados(cidPorProc.getIndSituacao(), original.getIndSituacao())){
			
			PdtCidPorProcJn cidPorProcJn = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.UPD, PdtCidPorProcJn.class, servidorLogadoFacade.obterServidorLogado().getUsuario());
			cidPorProcJn.setDptSeq(original.getId().getDptSeq());
			cidPorProcJn.setCidSeq(original.getId().getCidSeq());
			cidPorProcJn.setCriadoEm(original.getCriadoEm());
			cidPorProcJn.setSerMatricula(original.getRapServidores().getId().getMatricula());
			cidPorProcJn.setSerVinCodigo(original.getRapServidores().getId().getVinCodigo());
			cidPorProcJn.setIndSituacao(original.getIndSituacao().toString());
			
			getPdtCidPorProcJnDAO().persistir(cidPorProcJn);
		}
	}

	/**
	 * @ORADB PDTT_CXC_ARU
	 */
	public void posUpdatePdtComplementoPorCid(PdtComplementoPorCid complementoPorCid) {
		PdtComplementoPorCid original = getPdtComplementoPorCidDAO().obterOriginal(complementoPorCid);
		
		if(CoreUtil.modificados(complementoPorCid.getId().getSeqp(), original.getId().getSeqp()) ||
				CoreUtil.modificados(complementoPorCid.getId().getCxpCidSeq(), original.getId().getCxpCidSeq()) ||
				CoreUtil.modificados(complementoPorCid.getId().getCxpDptSeq(), original.getId().getCxpDptSeq()) ||
				CoreUtil.modificados(complementoPorCid.getDescricao(), original.getDescricao()) ||
				CoreUtil.modificados(complementoPorCid.getIndSituacao(), original.getIndSituacao()) ||
				CoreUtil.modificados(complementoPorCid.getCriadoEm(), original.getCriadoEm()) ||
				CoreUtil.modificados(complementoPorCid.getRapServidores().getId().getMatricula(), original.getRapServidores().getId().getMatricula()) ||
				CoreUtil.modificados(complementoPorCid.getRapServidores().getId().getVinCodigo(), original.getRapServidores().getId().getVinCodigo())){
			
			PdtComplementoPorCidJn complementoPorCidJn = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.UPD, PdtComplementoPorCidJn.class, servidorLogadoFacade.obterServidorLogado().getUsuario());
			complementoPorCidJn.setSeqp(original.getId().getSeqp());
			complementoPorCidJn.setCxpCidSeq(original.getId().getCxpCidSeq());
			complementoPorCidJn.setCxpDptSeq(original.getId().getCxpDptSeq());
			complementoPorCidJn.setDescricao(original.getDescricao());
			complementoPorCidJn.setIndSituacao(original.getIndSituacao().toString());
			complementoPorCidJn.setCriadoEm(original.getCriadoEm());
			complementoPorCidJn.setSerMatricula(original.getRapServidores().getId().getMatricula());
			complementoPorCidJn.setSerVinCodigo(original.getRapServidores().getId().getVinCodigo());
			
			getPdtComplementoPorCidJnDAO().persistir(complementoPorCidJn);
		}
	}

	private PdtComplementoPorCidJnDAO getPdtComplementoPorCidJnDAO() {
		return pdtComplementoPorCidJnDAO;
	}

	private PdtComplementoPorCidDAO getPdtComplementoPorCidDAO() {
		return pdtComplementoPorCidDAO;
	}

	private PdtCidPorProcDAO getPdtCidPorProcDAO() {
		return pdtCidPorProcDAO;
	}

	private PdtCidPorProcJnDAO getPdtCidPorProcJnDAO() {
		return pdtCidPorProcJnDAO;
	}
}
