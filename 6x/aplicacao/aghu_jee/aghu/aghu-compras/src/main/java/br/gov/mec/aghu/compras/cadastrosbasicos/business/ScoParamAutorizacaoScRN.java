package br.gov.mec.aghu.compras.cadastrosbasicos.business;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.dao.ScoParamAutorizacaoScDAO;
import br.gov.mec.aghu.model.ScoParamAutorizacaoSc;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class ScoParamAutorizacaoScRN extends BaseBusiness {
	
	private static final Log LOG = LogFactory.getLog(ScoParamAutorizacaoScRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@Inject
	private ScoParamAutorizacaoScDAO scoParamAutorizacaoScDAO;

	private static final long serialVersionUID = -8880591796929105301L;
	
	public void persistir(ScoParamAutorizacaoSc paramAutorizacaoSc){
		this.getScoParamAutorizacaoScDAO().persistir(paramAutorizacaoSc);
	}
	
	public void atualizar(ScoParamAutorizacaoSc paramAutorizacaoSc){
		this.getScoParamAutorizacaoScDAO().merge(paramAutorizacaoSc);
	}
	
	protected ScoParamAutorizacaoScDAO getScoParamAutorizacaoScDAO(){
		return scoParamAutorizacaoScDAO;
	}
}
