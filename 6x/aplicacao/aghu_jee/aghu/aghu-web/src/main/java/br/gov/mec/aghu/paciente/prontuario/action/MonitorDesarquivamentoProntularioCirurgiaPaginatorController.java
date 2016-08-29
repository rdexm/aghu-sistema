package br.gov.mec.aghu.paciente.prontuario.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.internacao.vo.ProntuarioCirurgiaVO;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;

public class MonitorDesarquivamentoProntularioCirurgiaPaginatorController extends ActionController implements ActionPaginator {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6217438776897513646L;

	@EJB
	private IPacienteFacade pacienteFacade;	
	
	@Inject @Paginator
	private DynamicDataModel<ProntuarioCirurgiaVO> cirurgiaDataModel;
	
	@PostConstruct
	public void init() {
		begin(this.conversation);
		cirurgiaDataModel.reiniciarPaginator();
	}

	
	@Override
	public Long recuperarCount() {
		return pacienteFacade.obterCountPesquisaDesarquivamentoProntuariosCirurgia();
	}

	@Override
	public List<ProntuarioCirurgiaVO> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {

		final String ordenacaoVO = "aba3.";

		if (orderProperty != null && orderProperty.startsWith(ordenacaoVO)) {
			orderProperty = "";
		}
		
		return pacienteFacade.pesquisarDesarquivamentoProntuariosCirurgia(firstResult, maxResult,orderProperty, asc);
	}

	public DynamicDataModel<ProntuarioCirurgiaVO> getCirurgiaDataModel() {
		return cirurgiaDataModel;
	}

	public void setCirurgiaDataModel(
			DynamicDataModel<ProntuarioCirurgiaVO> cirurgiaDataModel) {
		this.cirurgiaDataModel = cirurgiaDataModel;
	}
}

