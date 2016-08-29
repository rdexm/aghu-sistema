package br.gov.mec.aghu.compras.contaspagar.action;

import java.util.List;

import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.compras.contaspagar.business.IContasPagarFacade;
import br.gov.mec.aghu.compras.contaspagar.vo.TituloVO;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.exception.BaseException;

public class TitulosDataModel implements ActionPaginator {

	private static final long serialVersionUID = 507465851683697325L;
	
	@EJB
	private IContasPagarFacade contasPagarFacade;
	
	@Inject
	private TituloPaginatorController tituloPaginatorController;

	@SuppressWarnings("unchecked")
	@Override
	public List<TituloVO> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		try {	
			if(tituloPaginatorController.isCarregarDataTable()){
				List<TituloVO> pesquisarTitulos = contasPagarFacade.pesquisarTitulos(firstResult, maxResult, orderProperty, asc, tituloPaginatorController.getFiltro());
				return pesquisarTitulos;
			}
		} catch (BaseException e) {
			tituloPaginatorController.apresentarExcecaoNegocioController(e);
		}
		return null;
	}

	@Override
	public Long recuperarCount() {
		try {
				return contasPagarFacade.pesquisarTitulosCount(tituloPaginatorController.getFiltro());
		} catch (BaseException e) {
			tituloPaginatorController.apresentarExcecaoNegocioController(e);
		}
		return Long.valueOf(0l);
	}

}
