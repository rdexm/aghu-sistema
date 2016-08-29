package br.gov.mec.aghu.compras.parecer.business;


import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.dao.ScoItemPropostaFornecedorDAO;
import br.gov.mec.aghu.compras.pac.business.IPacFacade;
import br.gov.mec.aghu.compras.vo.PropFornecAvalParecerVO;
import br.gov.mec.aghu.dominio.DominioSituacaoParecer;
import br.gov.mec.aghu.model.ScoItemPropostaFornecedor;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class AvaliacaoPropostasParecerTecnicoON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(AvaliacaoPropostasParecerTecnicoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IPacFacade pacFacade;

@Inject
private ScoItemPropostaFornecedorDAO scoItemPropostaFornecedorDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 7518477434707318927L;
	
	

	public List<PropFornecAvalParecerVO> pesquisarItensPropostaFornecedorPAC(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc, Integer numeroPAC, List<DominioSituacaoParecer> listaSituacaoParecer){
		
		List<PropFornecAvalParecerVO> listaPropFornecAvalParecer = this.getScoItemPropostaFornecedorDAO().pesquisarItensPropostaFornecedorPAC(firstResult, maxResult, orderProperty, asc, numeroPAC, listaSituacaoParecer);
		
		for (PropFornecAvalParecerVO itemPropFornecAvalParecer : listaPropFornecAvalParecer){			
			
			ScoItemPropostaFornecedor scoItemPropostaFornecedor = this.getScoItemPropostaFornecedorDAO().obterItemPorLicitacaoFornecedorNumero(itemPropFornecAvalParecer.getPfrFrnNumeroItemPropostaFornecedor(), 
					itemPropFornecAvalParecer.getPfrLctNumeroItemProposta(), itemPropFornecAvalParecer.getNumeroItemPropostaFornecedor());
			
			PropFornecAvalParecerVO parecerMaterialSituacao = this.getPACFacade().obterParecerMaterialSituacaoItemProposta(scoItemPropostaFornecedor);
			
			itemPropFornecAvalParecer.setCodigoParecerMaterial(parecerMaterialSituacao.getCodigoParecerMaterial());
			itemPropFornecAvalParecer.setSituacaoParecerDescricao(parecerMaterialSituacao.getSituacaoParecerDescricao());			
			
		    
		}
		
		return listaPropFornecAvalParecer;
		
	}
	
	public Long contarItensPropostaFornecedorPAC(Integer numeroPAC, List<DominioSituacaoParecer> listaSituacaoParecer){
		return this.getScoItemPropostaFornecedorDAO().contarItensPropostaFornecedorPAC(numeroPAC, listaSituacaoParecer);
	}
	
	protected ScoItemPropostaFornecedorDAO getScoItemPropostaFornecedorDAO() {
		return scoItemPropostaFornecedorDAO;
	}	
	
	protected IPacFacade getPACFacade() {
		return this.pacFacade;
	}
	
}