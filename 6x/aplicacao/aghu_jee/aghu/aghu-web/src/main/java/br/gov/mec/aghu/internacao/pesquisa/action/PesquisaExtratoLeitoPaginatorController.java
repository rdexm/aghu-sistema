package br.gov.mec.aghu.internacao.pesquisa.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.internacao.pesquisa.business.IPesquisaInternacaoFacade;
import br.gov.mec.aghu.internacao.pesquisa.vo.ExtratoLeitoVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;

public class PesquisaExtratoLeitoPaginatorController extends ActionController implements ActionPaginator {
	private static final long serialVersionUID = -610616011493718896L;

	@EJB
	private IPesquisaInternacaoFacade pesquisaInternacaoFacade;

	private String leito;

	private Date dataPesquisa;
	
	@Inject @Paginator
	private DynamicDataModel<ExtratoLeitoVO> dataModel;

	@Override
	public Long recuperarCount() {
		return pesquisaInternacaoFacade.pesquisarExtratoLeitoCount(leito, dataPesquisa);
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ExtratoLeitoVO> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {

		List<ExtratoLeitoVO>  result = pesquisaInternacaoFacade.montarExtratoLeitoVO(leito, dataPesquisa, firstResult, maxResult);

		if (result == null) {
			result = new ArrayList<ExtratoLeitoVO>();
		}
		return result;

	}

	// getters and setters
	public String getLeito() {
		return leito;
	}

	public void setLeito(String leito) {
		this.leito = leito;
	}

	public Date getDataPesquisa() {
		return dataPesquisa;
	}

	public void setDataPesquisa(Date dataPesquisa) {
		this.dataPesquisa = dataPesquisa;
	}

	public IPesquisaInternacaoFacade getPesquisaInternacaoFacade() {
		return pesquisaInternacaoFacade;
	}

	public void setPesquisaInternacaoFacade(IPesquisaInternacaoFacade pesquisaInternacaoFacade) {
		this.pesquisaInternacaoFacade = pesquisaInternacaoFacade;
	}

	public DynamicDataModel<ExtratoLeitoVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<ExtratoLeitoVO> dataModel) {
		this.dataModel = dataModel;
	}
}
