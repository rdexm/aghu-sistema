package br.gov.mec.aghu.suprimentos.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.model.ScoMarcaComercial;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;

public class PesquisarMarcaComercialPaginatorController extends ActionController implements ActionPaginator {

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	private static final long serialVersionUID = -8702625645962028050L;

	private static final String PAGE_COMPRAS_MANTER_MARCA_COMERCIAL = "manterMarcaComercial";

	@Inject @Paginator
	private DynamicDataModel<ScoMarcaComercial> dataModel;

	private ScoMarcaComercial parametroSelecionado;

	@EJB
	protected IComprasFacade comprasFacade;

	private boolean visivel = false;

	private String voltar = "compras-manterCadastroMarcasFornecedor";
	private ScoMarcaComercial scoMarcaComercial = new ScoMarcaComercial();
	private String voltarPara;
	
	public String voltarManterCadastroMarcasFornecedor() {
		//this.getDataModel().limparPesquisa();
		///this.scoMarcaComercial = new ScoMarcaComercial();
		//this.visivel = false;
		this.limparPesquisa();
		this.voltarPara = null;
		return this.voltar;
	}

	public void pesquisar() {
		this.getDataModel().reiniciarPaginator();
		this.visivel = true;
	}

	public String inserirEditar() {
		return PAGE_COMPRAS_MANTER_MARCA_COMERCIAL;
	}

	public void limparPesquisa() {
		this.scoMarcaComercial = new ScoMarcaComercial();
		this.getDataModel().limparPesquisa();
		this.visivel = false;
	}

	@SuppressWarnings("rawtypes")
	public List recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		List<ScoMarcaComercial> result = this.comprasFacade.listaMarcaComercial(firstResult, maxResult, "codigo", true, this.scoMarcaComercial);
		if (result == null) {
			result = new ArrayList<ScoMarcaComercial>();
		}
		return result;
	}

	public Long recuperarCount() {
		return comprasFacade.pesquisarMarcaComecialCount(this.scoMarcaComercial);	
	}

	public ScoMarcaComercial getScoMarcaComercial() {
		return scoMarcaComercial;
	}

	public void setScoMarcaComercial(ScoMarcaComercial scoMarcaComercial) {
		this.scoMarcaComercial = scoMarcaComercial;
	}

	public Boolean getVisivel() {
		return visivel;
	}

	public void setVisivel(Boolean visivel) {
		this.visivel = visivel;
	}

	public String getVoltar() {
		return voltar;
	}

	public void setVoltar(String voltar) {
		this.voltar = voltar;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public DynamicDataModel<ScoMarcaComercial> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<ScoMarcaComercial> dataModel) {
		this.dataModel = dataModel;
	}

	public ScoMarcaComercial getParametroSelecionado() {
		return parametroSelecionado;
	}

	public void setParametroSelecionado(ScoMarcaComercial parametroSelecionado) {
		this.parametroSelecionado = parametroSelecionado;
	}
}