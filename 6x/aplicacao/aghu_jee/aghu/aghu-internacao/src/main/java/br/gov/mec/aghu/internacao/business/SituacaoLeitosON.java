package br.gov.mec.aghu.internacao.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.internacao.dao.AinLeitosDAO;
import br.gov.mec.aghu.internacao.vo.PesquisaSituacoesLeitosVO;
import br.gov.mec.aghu.internacao.vo.SituacaoLeitosVO;
import br.gov.mec.aghu.model.AghClinicas;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class SituacaoLeitosON extends BaseBusiness {


@EJB
private SituacaoLeitosRN situacaoLeitosRN;

private static final Log LOG = LogFactory.getLog(SituacaoLeitosON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AinLeitosDAO ainLeitosDAO;

@EJB
private IAghuFacade aghuFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -7043057754212324139L;

	public PesquisaSituacoesLeitosVO pesquisaSituacoesLeitos(AghClinicas clinica) {
		return this.getSituacaoLeitosRN().pesquisaSituacoesLeitos(clinica);
	}
	
	public SituacaoLeitosVO pesquisaCapacidadeInstaladaLeitos(AghClinicas clinica) {
		return this.getAinLeitosDAO().pesquisaCapacidadeInstaladaLeitos(clinica);
	}
	public SituacaoLeitosVO pesquisaSituacaoSemLeitos(){
		return this.getAghuFacade().pesquisaSituacaoSemLeitos();
	}
	
	protected AinLeitosDAO getAinLeitosDAO(){
		return ainLeitosDAO;
	}
	
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

	protected SituacaoLeitosRN getSituacaoLeitosRN() {
		return situacaoLeitosRN;
	}
	
}
