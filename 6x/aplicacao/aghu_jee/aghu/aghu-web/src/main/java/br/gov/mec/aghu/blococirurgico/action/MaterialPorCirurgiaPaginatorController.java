package br.gov.mec.aghu.blococirurgico.action;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;


public class MaterialPorCirurgiaPaginatorController extends	ActionController implements ActionPaginator {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	@Inject @Paginator
	private DynamicDataModel<MbcCirurgias> dataModel;

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8502623876437237543L;

	@EJB
	private ICentroCustoFacade centroCustoFacade;
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	@Inject
	private MaterialPorCirurgiaController materialPorCirurgiaController;
	
	//Campos filtro
	private Short agenda;
	private Date data;
	private FccCentroCustos centroCusto;
	
	//#32001
	private  Integer seq;
	
	private final String PAGE_MATERIAL_CIRURGIA_CRUD = "materialPorCirurgiaCRUD";
	
	
	/**
	 * Método executado ao clicar no botão pesquisar
	 */
	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
	}
	
	/**
	 * Método executado ao clicar no botão limpar
	 */
	public void limpar() {
		//Limpa filtro
		this.agenda = null;
		this.data = null;
		this.centroCusto = null;
		this.dataModel.limparPesquisa();
		this.dataModel.setPesquisaAtiva(false);
	}

	@Override
	public Long recuperarCount() {
		Integer centroCusto = this.centroCusto != null ? this.centroCusto.getCodigo() : null;
		return blocoCirurgicoFacade.pesquisarCirurgiasAgendaDataCentroCustoCount(this.agenda, this.data, centroCusto);
	}

	@Override
	public List<MbcCirurgias> recuperarListaPaginada(Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc) {
		Integer centroCusto = this.centroCusto != null ? this.centroCusto.getCodigo() : null;
		
		List<MbcCirurgias> listaCirurgias = blocoCirurgicoFacade.pesquisarCirurgiasAgendaDataCentroCusto(firstResult, maxResult, orderProperty, 
				asc, this.agenda, this.data, centroCusto);
		
		this.obterCodigoCentroCustoResponsavel(listaCirurgias);
		
		return listaCirurgias;
	}
	
	/**
	 * Obtém o centro de custo onde está alocado o cirurgião responsável
	 * pela cirurgia
	 */
	private void obterCodigoCentroCustoResponsavel(List<MbcCirurgias> cirurgias) {
		for (MbcCirurgias mbcCirurgias : cirurgias) {
			if (mbcCirurgias.getCentroCustos() == null) {
				mbcCirurgias.setCentroCustos(this.materialPorCirurgiaController.obterCodigoCentroCustoResponsavel(mbcCirurgias));
			}
		}
	}
	
	
	public String editar(){
		return PAGE_MATERIAL_CIRURGIA_CRUD;
	}
	
	//Métodos para suggestion box de Centro de Custos
	public List<FccCentroCustos> pesquisarCentroCusto(String param) {
		return this.returnSGWithCount(this.centroCustoFacade.pesquisarCentroCustosPorCodigoDescricao((String) param),pesquisarCentroCustoCount(param));
	}
	
	public Long pesquisarCentroCustoCount(String param) {
		return this.centroCustoFacade.pesquisarCentroCustosPorCodigoDescricaoCount((String) param);
	}

	public Short getAgenda() {
		return agenda;
	}

	public void setAgenda(Short agenda) {
		this.agenda = agenda;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public FccCentroCustos getCentroCusto() {
		return centroCusto;
	}

	public void setCentroCusto(FccCentroCustos centroCusto) {
		this.centroCusto = centroCusto;
	}

	public String conciliacaoMateriais(MbcCirurgias item) {
		seq = item.getSeq();
		return "conciliacaoMateriais";
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	} 


	public DynamicDataModel<MbcCirurgias> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<MbcCirurgias> dataModel) {
	 this.dataModel = dataModel;
	}
}
