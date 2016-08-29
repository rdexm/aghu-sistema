package br.gov.mec.aghu.sicon.cadastrosbasicos.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoItemContrato;
import br.gov.mec.aghu.model.ScoCatalogoSicon;
import br.gov.mec.aghu.sicon.cadastrosbasicos.business.ICadastrosBasicosSiconFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;

public class ManterCatalogoPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 8747638844858253328L;

	private static final String PAGE_MANTER_CATALOGO = "manterCatalogo";

	@EJB
	private ICadastrosBasicosSiconFacade cadastrosBasicosSiconFacade;
	@Inject @Paginator
	private DynamicDataModel<ScoCatalogoSicon> dataModel;

	private ScoCatalogoSicon catalogoSicon = new ScoCatalogoSicon();
	private ScoCatalogoSicon scoCatalogoSiconSelecionado;
	private Integer codigoSicon;
	private String descricao;
	private DominioTipoItemContrato tipoItemContrato;
	private DominioSituacao situacao;
	private String origem;
	private boolean alterar;
	private Boolean exibirNovo;
	private Boolean pesquisaAtivada;
	private Integer codigoSiconFiltro;
	private String descricaoFiltro;
	private DominioTipoItemContrato tipoItemContratoFiltro;
	private DominioSituacao situacaoFiltro;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	@Override
	public Long recuperarCount() {
		return cadastrosBasicosSiconFacade.listarItensCatalogoCount(this.getCodigoSicon(), this.getDescricao(),
				this.getTipoItemContrato(), this.getSituacao());
	}

	@Override
	public List<ScoCatalogoSicon> recuperarListaPaginada(Integer _firstResult, Integer _maxResult, String _orderProperty,
			boolean _asc) {

		List<ScoCatalogoSicon> result = cadastrosBasicosSiconFacade.pesquisarItensCatalogo(_firstResult, _maxResult,
				_orderProperty, _asc, this.getCodigoSicon(), this.getDescricao(), this.getTipoItemContrato(), this.getSituacao());

		if (result == null) {
			result = new ArrayList<ScoCatalogoSicon>();
		}

		return result;
	}

	/**
	 * Método inicial da tela
	 */
	public void iniciar() {
	 


		this.setCodigoSicon(null);
		this.setDescricao(null);
		this.setSituacao(null);
		this.setTipoItemContrato(null);

		setCodigoSicon(this.getCodigoSiconFiltro());
		setDescricao(this.getDescricaoFiltro());
		setTipoItemContrato(this.getTipoItemContratoFiltro());
		setSituacao(this.getSituacaoFiltro());

	
	}

	public String redirecionaEditar() {
		return PAGE_MANTER_CATALOGO;
	}

	public String redirecionaNovo() {
		return PAGE_MANTER_CATALOGO;
	}

	/**
	 * Método para limpeza de campos na tela.
	 * 
	 */
	public void limpar() {

		this.catalogoSicon = new ScoCatalogoSicon();
		this.codigoSicon = null;
		this.descricao = null;
		this.exibirNovo = false;
		this.tipoItemContrato = null;
		this.situacao = null;
		this.origem = null;

		// limpa as variáveis de filtro
		this.codigoSiconFiltro = null;
		this.descricaoFiltro = null;
		this.tipoItemContratoFiltro = null;
		this.situacaoFiltro = null;

		this.dataModel.setPesquisaAtiva(false);
	}

	/**
	 * Realiza a pesquisa de acordo com os filtros informados.
	 */
	public void pesquisar() {

		this.codigoSiconFiltro = this.getCodigoSicon();
		this.descricaoFiltro = this.getDescricao();
		this.situacaoFiltro = this.getSituacao();
		this.tipoItemContratoFiltro = this.getTipoItemContrato();

		this.exibirNovo = true;
		this.pesquisaAtivada = true;
		this.dataModel.reiniciarPaginator();
	}

	// Getters e Setters
	public ScoCatalogoSicon getCatalogoSicon() {
		return catalogoSicon;
	}

	public void setCatalogoSicon(ScoCatalogoSicon catalogoSicon) {
		this.catalogoSicon = catalogoSicon;
	}

	public Integer getCodigoSicon() {
		return codigoSicon;
	}

	public void setCodigoSicon(Integer codigoSicon) {
		this.codigoSicon = codigoSicon;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public DominioTipoItemContrato getTipoItemContrato() {
		return tipoItemContrato;
	}

	public void setTipoItemContrato(DominioTipoItemContrato tipoItemContrato) {
		this.tipoItemContrato = tipoItemContrato;
	}

	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	public String getOrigem() {
		return origem;
	}

	public void setOrigem(String origem) {
		this.origem = origem;
	}

	public boolean isAlterar() {
		return alterar;
	}

	public void setAlterar(boolean alterar) {
		this.alterar = alterar;
	}

	public Boolean getExibirNovo() {
		return exibirNovo;
	}

	public void setExibirNovo(Boolean exibirNovo) {
		this.exibirNovo = exibirNovo;
	}

	public Boolean getPesquisaAtivada() {
		return pesquisaAtivada;
	}

	public void setPesquisaAtivada(Boolean pesquisaAtivada) {
		this.pesquisaAtivada = pesquisaAtivada;
	}

	public Integer getCodigoSiconFiltro() {
		return codigoSiconFiltro;
	}

	public void setCodigoSiconFiltro(Integer codigoSiconFiltro) {
		this.codigoSiconFiltro = codigoSiconFiltro;
	}

	public String getDescricaoFiltro() {
		return descricaoFiltro;
	}

	public void setDescricaoFiltro(String descricaoFiltro) {
		this.descricaoFiltro = descricaoFiltro;
	}

	public DominioTipoItemContrato getTipoItemContratoFiltro() {
		return tipoItemContratoFiltro;
	}

	public void setTipoItemContratoFiltro(DominioTipoItemContrato tipoItemContratoFiltro) {
		this.tipoItemContratoFiltro = tipoItemContratoFiltro;
	}

	public DominioSituacao getSituacaoFiltro() {
		return situacaoFiltro;
	}

	public void setSituacaoFiltro(DominioSituacao situacaoFiltro) {
		this.situacaoFiltro = situacaoFiltro;
	}

	public DynamicDataModel<ScoCatalogoSicon> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<ScoCatalogoSicon> dataModel) {
		this.dataModel = dataModel;
	}

	public ScoCatalogoSicon getScoCatalogoSiconSelecionado() {
		return scoCatalogoSiconSelecionado;
	}

	public void setScoCatalogoSiconSelecionado(ScoCatalogoSicon scoCatalogoSiconSelecionado) {
		this.scoCatalogoSiconSelecionado = scoCatalogoSiconSelecionado;
	}
}
