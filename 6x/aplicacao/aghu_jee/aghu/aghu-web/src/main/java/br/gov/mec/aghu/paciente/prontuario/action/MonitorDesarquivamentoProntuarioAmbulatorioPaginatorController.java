package br.gov.mec.aghu.paciente.prontuario.action;

import java.util.List;
import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.model.AipMovimentacaoProntuarios;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.vo.AipMovimentacaoProntuarioVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;

public class MonitorDesarquivamentoProntuarioAmbulatorioPaginatorController extends ActionController implements ActionPaginator {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6217738776897513646L;

	@EJB
	private IPacienteFacade pacienteFacade;

	@Inject @Paginator
	private DynamicDataModel<AipMovimentacaoProntuarioVO> ambulatorioDataModel;

	@PostConstruct
	public void init() {
		begin(this.conversation);
		ambulatorioDataModel.reiniciarPaginator();
	}
	
	@Override
	public Long recuperarCount() {
		return pacienteFacade.obterCountPesquisaDesarquivamentoProntuarios();
	}
	
	@Override
	public List<AipMovimentacaoProntuarioVO> recuperarListaPaginada(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {

		final String ordenacaoVO = "aba2.";

		if (orderProperty != null) {					
			if(orderProperty.startsWith(ordenacaoVO)){
				orderProperty = "";
			}else {
				orderProperty = orderProperty.replace("movimentacao.", "");//Faz o replace da movimentação para ordenar pelo campo certo
			}
		}
	
		List<AipMovimentacaoProntuarios> resultadoPesquisa = pacienteFacade.pesquisarDesarquivamentoProntuarios(firstResult, maxResult, 
																												orderProperty, asc);

		List<AipMovimentacaoProntuarioVO> listaMovimentacaoVO = new ArrayList<AipMovimentacaoProntuarioVO>();
		for (AipMovimentacaoProntuarios movimentacao : resultadoPesquisa) {
			listaMovimentacaoVO.add(new AipMovimentacaoProntuarioVO(movimentacao));
		}
		return listaMovimentacaoVO;
	}

	public DynamicDataModel<AipMovimentacaoProntuarioVO> getAmbulatorioDataModel() {
		return ambulatorioDataModel;
	}

	public void setAmbulatorioDataModel(
			DynamicDataModel<AipMovimentacaoProntuarioVO> ambulatorioDataModel) {
		this.ambulatorioDataModel = ambulatorioDataModel;
	}
}
