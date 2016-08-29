package br.gov.mec.aghu.compras.contaspagar.action;

import java.util.List;

import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.compras.contaspagar.business.IContasPagarFacade;
import br.gov.mec.aghu.compras.contaspagar.vo.TituloProgramadoVO;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.exception.BaseException;

public class PagamentosDataModel implements ActionPaginator{

	private static final long serialVersionUID = -6032685989645394196L;
	
	@EJB
	private IContasPagarFacade contasPagarFacade;
	
	@Inject
	private TituloPaginatorController tituloPaginatorController;

	@SuppressWarnings("unchecked")
	public List<TituloProgramadoVO> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		try {
			if(tituloPaginatorController.isCarregarDataTable()){
				List<TituloProgramadoVO> listaPagamentosProgramados = contasPagarFacade.obterListaPagamentosProgramados(
						tituloPaginatorController.getDataPagamento(), firstResult, maxResult, orderProperty, asc);
				return listaPagamentosProgramados;
			}
		} catch (BaseException e) {
			tituloPaginatorController.apresentarExcecaoNegocioController(e);
		}
		return null;
	}

	@Override
	public Long recuperarCount() {
		try {
			if(tituloPaginatorController.isCarregarDataTable()){
				return contasPagarFacade.pesquisarPagamentosProgramadosCount(tituloPaginatorController.getDataPagamento());
			}
		} catch (BaseException e) {
			tituloPaginatorController.apresentarExcecaoNegocioController(e);
		}
		return Long.valueOf(0l);
	}

}
