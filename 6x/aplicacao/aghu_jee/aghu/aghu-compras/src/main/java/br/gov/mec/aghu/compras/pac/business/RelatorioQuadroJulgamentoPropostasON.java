package br.gov.mec.aghu.compras.pac.business;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.compras.dao.ScoItemPropostaFornecedorDAO;
import br.gov.mec.aghu.compras.pac.vo.ItemQuadroPropostasVO;
import br.gov.mec.aghu.compras.pac.vo.PropostaFornecedorVO;
import br.gov.mec.aghu.compras.pac.vo.RelatorioQuadroPropostasLicitacaoVO;
import br.gov.mec.aghu.model.ScoItemPropostaFornecedor;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class RelatorioQuadroJulgamentoPropostasON extends BaseBusiness {


@EJB
private ScoItemPropostaFornecedorON scoItemPropostaFornecedorON;

@EJB
private RelatorioQuadroPropostasProvisisorioON relatorioQuadroPropostasProvisisorioON;

private static final Log LOG = LogFactory.getLog(RelatorioQuadroJulgamentoPropostasON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IParametroFacade parametroFacade;

@Inject
private ScoItemPropostaFornecedorDAO scoItemPropostaFornecedorDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 6278321564331892483L;

	public List<RelatorioQuadroPropostasLicitacaoVO> pesquisarQuadroJulgamentoItensPropostas(
			Set<Integer> listaNumPac , Short numeroItemInicial, Short numeroItemFinal, String listaItens) throws ApplicationBusinessException {	
		
		List<RelatorioQuadroPropostasLicitacaoVO> listaRelatorioQuadroPropostasLicitacaoVO = new ArrayList<RelatorioQuadroPropostasLicitacaoVO>();

		List<RelatorioQuadroPropostasLicitacaoVO> quadroProvisorioItensPropostas = getRelatorioQuadroPropostasProvisisorioON()
				.pesquisarQuadroProvisorioItensPropostas(listaNumPac, numeroItemInicial, numeroItemFinal, listaItens);

		listaRelatorioQuadroPropostasLicitacaoVO.addAll(quadroProvisorioItensPropostas);

		/** Se Existe parametro cadastrado como Sim entao nao retira propostas sem parecer ***/		
		if (this.getScoItemPropostaFornecedorON().verificarUtilizaParecerTecnico()){
			retirarPropostasSemParecerFavoravel(listaRelatorioQuadroPropostasLicitacaoVO);
		}
		
		retirarItensSemPropostas(listaRelatorioQuadroPropostasLicitacaoVO);
		atualizarListaFornecedoresParticipantes(listaRelatorioQuadroPropostasLicitacaoVO);

		return listaRelatorioQuadroPropostasLicitacaoVO;
	}
	
	private void retirarItensSemPropostas(List<RelatorioQuadroPropostasLicitacaoVO> listaRelatorioQuadroPropostasLicitacaoVO) {
		// Loop para percorrer a lista de PAC's
		for (RelatorioQuadroPropostasLicitacaoVO quadroPropostasLicitacaoVO : listaRelatorioQuadroPropostasLicitacaoVO) {
			
			Iterator<ItemQuadroPropostasVO> it = quadroPropostasLicitacaoVO.getListaItemPropostas().iterator();
			
			// Loop para percorrer lista de itens, verificando existência de proposta
			while (it.hasNext()){
				ItemQuadroPropostasVO itemAf = it.next();
				
				if (itemAf.getListaProposta().isEmpty()){
					it.remove();
				}
			}
		}		
	}

	private void retirarPropostasSemParecerFavoravel(
			List<RelatorioQuadroPropostasLicitacaoVO> listaRelatorioQuadroPropostasLicitacaoVO) {	
		// Loop para verificar a lista de PAC's
		for (RelatorioQuadroPropostasLicitacaoVO quadroPropostasLicitacaoVO : listaRelatorioQuadroPropostasLicitacaoVO) {
			
			// Loop para verificar a lista de itens de um PAC
			for (ItemQuadroPropostasVO itemProposta : quadroPropostasLicitacaoVO.getListaItemPropostas()) {
				
				 Iterator<PropostaFornecedorVO> it = itemProposta.getListaProposta().iterator();

				 // Loop que verifica cada proposta deste item
				 while (it.hasNext()){
					 PropostaFornecedorVO proposta = it.next();
						if (!itemProposta.getTipoSolicitacao().equals("SS") &&
								  (proposta.getParecer() == null
								|| proposta.getParecer().contentEquals("Desfavorável")
								|| proposta.getParecer().contentEquals(""))) {
							// Caso o item sem parecer não for autorizado pelo usuário deve-se retira-lo da lista
							if (!verificaItemSemParecerAutorizadoPorUsuario(
									proposta.getNumeroFornecedor(),
									quadroPropostasLicitacaoVO.getPac(),
									itemProposta.getNumItem().shortValue())) {
								it.remove();
							}					 
						}
				}	 				
			}
		}
	}
		
	private void atualizarListaFornecedoresParticipantes(List<RelatorioQuadroPropostasLicitacaoVO> listaRelatorioQuadroPropostasLicitacaoVO){
		for (RelatorioQuadroPropostasLicitacaoVO quadroPropostasLicitacaoVO : listaRelatorioQuadroPropostasLicitacaoVO) {
			quadroPropostasLicitacaoVO.setListaFornecedorParticipante(getRelatorioQuadroPropostasProvisisorioON().
					pesquisarListaFornecedorParticipante(quadroPropostasLicitacaoVO.getListaItemPropostas()));
		}
	}

	private boolean verificaItemSemParecerAutorizadoPorUsuario(
			Integer pfrnNumero, Integer pfrnLctNumero, Short numero) {
		ScoItemPropostaFornecedor itemPropostaFornecedor;

		itemPropostaFornecedor = getScoItemPropostaFornecedorDAO()
				.obterItemPorLicitacaoFornecedorNumeroItem(pfrnNumero, pfrnLctNumero, numero);
		if (itemPropostaFornecedor == null){
			return false;
		}
		
		return itemPropostaFornecedor.getIndAutorizUsr();
	}
	
	

	private ScoItemPropostaFornecedorDAO getScoItemPropostaFornecedorDAO() {
		return scoItemPropostaFornecedorDAO;
	}

	private RelatorioQuadroPropostasProvisisorioON getRelatorioQuadroPropostasProvisisorioON() {
		return relatorioQuadroPropostasProvisisorioON;
	}
	
	private ScoItemPropostaFornecedorON getScoItemPropostaFornecedorON() {
		return scoItemPropostaFornecedorON;
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
}
