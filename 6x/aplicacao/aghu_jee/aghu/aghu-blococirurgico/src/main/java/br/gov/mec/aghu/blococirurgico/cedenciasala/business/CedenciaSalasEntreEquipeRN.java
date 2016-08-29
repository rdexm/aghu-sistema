package br.gov.mec.aghu.blococirurgico.cedenciasala.business;


import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcSubstEscalaSalaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcSubstEscalaSalaJnDAO;
import br.gov.mec.aghu.model.MbcSubstEscalaSala;
import br.gov.mec.aghu.model.MbcSubstEscalaSalaJn;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class CedenciaSalasEntreEquipeRN extends BaseBusiness {
@EJB
private IServidorLogadoFacade servidorLogadoFacade;

	private static final Log LOG = LogFactory.getLog(CedenciaSalasEntreEquipeRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcSubstEscalaSalaJnDAO mbcSubstEscalaSalaJnDAO;

	@Inject
	private MbcSubstEscalaSalaDAO mbcSubstEscalaSalaDAO;


	private static final long serialVersionUID = 1139968295951781728L;
	
	/**
	 * ORADB TRIGGER MBCT_SSL_ARU
	 * 
	 * #24719  
	 */
	public void posUpdateMbcSubstEscalaSala(MbcSubstEscalaSala mbcSubstEscalaSala){
		MbcSubstEscalaSala original = getMbcSubstEscalaSalaDAO().obterOriginal(mbcSubstEscalaSala);
		
		if(CoreUtil.modificados(mbcSubstEscalaSala.getId().getCseCasSeq(), original.getId().getCseCasSeq()) ||
				CoreUtil.modificados(mbcSubstEscalaSala.getId().getCseEspSeq(), original.getId().getCseEspSeq()) ||
				CoreUtil.modificados(mbcSubstEscalaSala.getId().getCseSeqp(), original.getId().getCseSeqp()) ||
				CoreUtil.modificados(mbcSubstEscalaSala.getId().getPucSerMatricula(), original.getId().getPucSerMatricula()) ||
				CoreUtil.modificados(mbcSubstEscalaSala.getId().getPucSerVinCodigo(), original.getId().getPucSerVinCodigo()) ||
				CoreUtil.modificados(mbcSubstEscalaSala.getId().getPucUnfSeq(), original.getId().getPucUnfSeq()) ||
				CoreUtil.modificados(mbcSubstEscalaSala.getId().getPucIndFuncaoProf(), original.getId().getPucIndFuncaoProf()) ||
				CoreUtil.modificados(mbcSubstEscalaSala.getId().getData(), original.getId().getData()) ||
				CoreUtil.modificados(mbcSubstEscalaSala.getCriadoEm(), original.getCriadoEm()) ||
				CoreUtil.modificados(mbcSubstEscalaSala.getRapServidores().getId().getMatricula(), original.getRapServidores().getId().getMatricula()) ||
				CoreUtil.modificados(mbcSubstEscalaSala.getRapServidores().getId().getVinCodigo(), original.getRapServidores().getId().getVinCodigo()) ||
				CoreUtil.modificados(mbcSubstEscalaSala.getIndSituacao(), original.getIndSituacao())){
			MbcSubstEscalaSalaJn mbcSubstEscalaSalaJn = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.UPD, MbcSubstEscalaSalaJn.class, servidorLogadoFacade.obterServidorLogado().getUsuario());
			mbcSubstEscalaSalaJn.setCseCasSeq(original.getId().getCseCasSeq());
			mbcSubstEscalaSalaJn.setCseEspSeq(original.getId().getCseEspSeq());
			mbcSubstEscalaSalaJn.setCseSeqp(original.getId().getCseSeqp());
			mbcSubstEscalaSalaJn.setPucSerMatricula(original.getId().getPucSerMatricula());
			mbcSubstEscalaSalaJn.setPucSerVinCodigo(original.getId().getPucSerVinCodigo());
			mbcSubstEscalaSalaJn.setPucUnfSeq(original.getId().getPucUnfSeq());
			mbcSubstEscalaSalaJn.setPucIndFuncaoProf(original.getId().getPucIndFuncaoProf().toString());
			mbcSubstEscalaSalaJn.setData(original.getId().getData());
			mbcSubstEscalaSalaJn.setCriadoEm(original.getCriadoEm());
			mbcSubstEscalaSalaJn.setSerMatricula(original.getRapServidores().getId().getMatricula());
			mbcSubstEscalaSalaJn.setSerVinCodigo(original.getRapServidores().getId().getVinCodigo());
			mbcSubstEscalaSalaJn.setIndSituacao(original.getIndSituacao().toString());
			
			getMbcSubstEscalaSalaJnDAO().persistir(mbcSubstEscalaSalaJn);
		}
	}
	
	protected MbcSubstEscalaSalaDAO getMbcSubstEscalaSalaDAO(){
		return mbcSubstEscalaSalaDAO;
	}
	
	protected MbcSubstEscalaSalaJnDAO getMbcSubstEscalaSalaJnDAO(){
		return mbcSubstEscalaSalaJnDAO;
	}
}
