package br.gov.mec.aghu.ambulatorio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.ambulatorio.vo.FiltroConsultaBloqueioConsultaVO;
import br.gov.mec.aghu.ambulatorio.vo.FiltroParametrosPadraoConsultaVO;
import br.gov.mec.aghu.model.AacSituacaoConsultas;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;

/**
 * Classe responsável por controlar as ações da listagem de grupo da condição de
 * atendimento.
 * 
 */
public class ManterBloqueioConsultaPaginatorController extends ActionController implements ActionPaginator {
	
	private static final String INCLUIR_BLOQUEIO_CONSULTA = "ambulatorio-manterBloqueioConsultaCRUD";
	
	private static final String EDITAR_BLOQUEIO_CONSULTA = "ambulatorio-manterBloqueioConsultaCRUD";
	

	private static final long serialVersionUID = 1393921256668856337L;

	@EJB
	private IAmbulatorioFacade ambulatorioFacade;

	private boolean exibirBotaoNovo;
	private FiltroParametrosPadraoConsultaVO filtroPadrao;
	private FiltroConsultaBloqueioConsultaVO filtroConsulta;

	@Inject @Paginator
	private DynamicDataModel<AacSituacaoConsultas> dataModel;
	private AacSituacaoConsultas parametroSelecionado;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public void init() {
	 

	 

		filtroConsulta = new FiltroConsultaBloqueioConsultaVO();
		filtroPadrao = new FiltroParametrosPadraoConsultaVO();
	
	}
	

	public String iniciarInclusao() {
		return INCLUIR_BLOQUEIO_CONSULTA;
	}

	public String iniciarEdicao() {
		return EDITAR_BLOQUEIO_CONSULTA;
	}

	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
		this.exibirBotaoNovo = true;
	}

	public void limparPesquisa() {
		this.filtroConsulta = new FiltroConsultaBloqueioConsultaVO();
		this.filtroPadrao = new FiltroParametrosPadraoConsultaVO();
		this.exibirBotaoNovo = false;
		this.dataModel.reiniciarPaginator();
		this.dataModel.setPesquisaAtiva(false);
	}

	@Override
	public Long recuperarCount() {
		return ambulatorioFacade.pesquisarConsultaCountSituacaoConsulta(filtroConsulta);
	}

	@Override
	public List<AacSituacaoConsultas> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		filtroPadrao.setFirstResult(firstResult);
		filtroPadrao.setMaxResult(maxResult);
		filtroPadrao.setOrdenacaoAscDesc(asc);
		filtroPadrao.setOrderProperty(orderProperty);
		return this.ambulatorioFacade.pesquisarConsultaPaginadaSituacaoConsulta(filtroPadrao, filtroConsulta);
	}

	/************************* getters and setters ***************/
	public void setAtivo(Boolean pesquisaAtiva) {
		this.dataModel.setPesquisaAtiva(pesquisaAtiva);
	}

	public boolean isExibirBotaoNovo() {
		return exibirBotaoNovo;
	}

	public void setExibirBotaoNovo(boolean exibirBotaoNovo) {
		this.exibirBotaoNovo = exibirBotaoNovo;
	}

	public FiltroParametrosPadraoConsultaVO getFiltroPadrao() {
		return filtroPadrao;
	}

	public void setFiltroPadrao(FiltroParametrosPadraoConsultaVO filtroPadrao) {
		this.filtroPadrao = filtroPadrao;
	}

	public FiltroConsultaBloqueioConsultaVO getFiltroConsulta() {
		return filtroConsulta;
	}

	public void setFiltroConsulta(FiltroConsultaBloqueioConsultaVO filtroConsulta) {
		this.filtroConsulta = filtroConsulta;
	}

	public DynamicDataModel<AacSituacaoConsultas> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AacSituacaoConsultas> dataModel) {
		this.dataModel = dataModel;
	}

	public AacSituacaoConsultas getParametroSelecionado() {
		return parametroSelecionado;
	}

	public void setParametroSelecionado(AacSituacaoConsultas parametroSelecionado) {
		this.parametroSelecionado = parametroSelecionado;
	}

}
