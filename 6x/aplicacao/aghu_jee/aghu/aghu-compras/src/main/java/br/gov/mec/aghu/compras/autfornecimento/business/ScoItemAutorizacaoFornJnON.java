package br.gov.mec.aghu.compras.autfornecimento.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.autfornecimento.vo.ScoItemAutorizacaoFornJnVO;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.dao.ScoItemAutorizacaoFornJnDAO;
import br.gov.mec.aghu.compras.dao.ScoItemPropostaFornecedorDAO;
import br.gov.mec.aghu.compras.pac.business.IPacFacade;
import br.gov.mec.aghu.compras.solicitacaoservico.business.ISolicitacaoServicoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;

/**
 * Objeto de negócio responsável por journaling do item da AF.
 * 
 * @author sgralha
 *
 */
@Stateless
public class ScoItemAutorizacaoFornJnON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(ScoItemAutorizacaoFornJnON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private ScoItemAutorizacaoFornJnDAO scoItemAutorizacaoFornJnDAO;

@Inject
private ScoItemPropostaFornecedorDAO scoItemPropostaFornecedorDAO;

@EJB
private IPacFacade pacFacade;

@EJB
private IComprasFacade comprasFacade;

@EJB
private ISolicitacaoServicoFacade solicitacaoServicoFacade;

	private static final long serialVersionUID = 7276860329724573920L;
	
	
	
	/**
	 * Método que retorna a lista dos itend de uma af da journal e popula com a respectiva solicitcacao de compra ou servico
	 * @param seqAF (numero da Af)
	 * @param sequenciaAlteracao
	 * @return
	 */
	public List<ScoItemAutorizacaoFornJnVO> obterScoItemAutorizacaoFornJnPorNumPacSeq(Integer seqAF, Integer sequenciaAlteracao){
		
		List<ScoItemAutorizacaoFornJnVO> listaItens = getScoItemAutorizacaoFornJnDAO().obterScoItemAutFornJnScSsPorNumPacSeq(seqAF, sequenciaAlteracao);
		if (listaItens!=null && listaItens.size()>0){
			for(ScoItemAutorizacaoFornJnVO itemVO:listaItens){
				itemVO.setItemAutorizacaoForn(getScoItemAutorizacaoFornJnDAO().obterPorChavePrimaria(itemVO.getSeqJn()));
				if(itemVO.getItemAutorizacaoForn().getItemPropostaFornecedor() != null){
				   itemVO.getItemAutorizacaoForn().setItemPropostaFornecedor(scoItemPropostaFornecedorDAO.obterPorChavePrimaria(itemVO.getItemAutorizacaoForn().getItemPropostaFornecedor().getId()));
				   if(itemVO.getItemAutorizacaoForn().getItemPropostaFornecedor().getItemLicitacao() != null){
				      itemVO.getItemAutorizacaoForn().getItemPropostaFornecedor().setItemLicitacao(this.pacFacade.obterItemLicitacaoPorNumeroLicitacaoENumeroItem(itemVO.getItemAutorizacaoForn().getItemPropostaFornecedor().getItemLicitacao().getId().getLctNumero(), itemVO.getItemAutorizacaoForn().getItemPropostaFornecedor().getItemLicitacao().getId().getNumero()));
				   }
				   if(itemVO.getItemAutorizacaoForn().getMarcaComercial()!= null){
					   itemVO.getItemAutorizacaoForn().setMarcaComercial(this.comprasFacade.obterMarcaComercialPorCodigo(itemVO.getItemAutorizacaoForn().getMarcaComercial().getCodigo()));
				   }
				  
				}
				
				if(itemVO.getSolicitacaoCompra()!= null){
					itemVO.setSolicitacaoCompra(this.comprasFacade.obterScoSolicitacaoDeCompraPorChavePrimaria(itemVO.getSolicitacaoCompra().getNumero()));
					itemVO.getSolicitacaoCompra().setMaterial(this.comprasFacade.obterMaterialPorId(itemVO.getSolicitacaoCompra().getMaterial().getCodigo()));
				}
				else if(itemVO.getSolicitacaoServico()!= null){
					itemVO.setSolicitacaoServico(this.solicitacaoServicoFacade.obterSolicitacaoServico(itemVO.getSolicitacaoServico().getNumero()));
					itemVO.getSolicitacaoServico().setServico(this.comprasFacade.obterServicoPorId(itemVO.getSolicitacaoServico().getServico().getCodigo()));
				}
				
			}		
		}
		return listaItens;
	}
	
	
	
	// Dependências

	protected ScoItemAutorizacaoFornJnDAO getScoItemAutorizacaoFornJnDAO() {
		return scoItemAutorizacaoFornJnDAO;
	}
}
