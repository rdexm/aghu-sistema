package br.gov.mec.aghu.faturamento.action;

import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.dominio.DominioDiariaInternacao;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.FatDiariaInternacao;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Classe responsável por controlar a pesquisa de Diárias Autorizadas
 * #2146
 * @author thiago.cortes
 *
 */

public class PesquisaDiariaAutorizadaPaginatorController extends ActionController
		implements ActionPaginator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5933839159812763703L;

	private static final String NOVO = "cadastroDiariaInternacaoCRUD";

	@EJB
	private IFaturamentoFacade faturamentoFacade;

	@EJB
	private IPermissionService permissionService;

	@Inject @Paginator
	private DynamicDataModel<FatDiariaInternacao> dataModel;
	
	private FatDiariaInternacao fatDiariaInternacao = new FatDiariaInternacao();
	
	/*
	 * Váriavel criada apenas para receber o obejto da propiredade selection da serverDataTable
	 */
	private FatDiariaInternacao fatDiariaInternacaoFiltro;
	
	private Integer seq;
	private String descricao;
	private Integer quantidadeDias;
	private DominioDiariaInternacao tipoValorConta;
	private Boolean exibirBotaoNovo = Boolean.FALSE;

	@PostConstruct
	protected void inicializar() {
		begin(conversation);
		
		if(tipoValorConta == null){
			tipoValorConta = DominioDiariaInternacao.U;
		}
	}

	public void iniciar() {
		final Boolean permissao = this.permissionService.usuarioTemPermissao(
				this.obterLoginUsuarioLogado(),"manterCadastrosBasicosFaturamento", "executar");		
		this.getDataModel().setUserEditPermission(permissao);
		this.getDataModel().setUserRemovePermission(permissao);
	}
	
	public void limparCampos(){
		seq = null;
		descricao = null;
		quantidadeDias = null;
		tipoValorConta = DominioDiariaInternacao.U;
		fatDiariaInternacao = new FatDiariaInternacao();
	}
	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
		exibirBotaoNovo = Boolean.TRUE;
	}
	
	/**
	 * Trunca descrição da Grid.
	 * @param item
	 * @param tamanhoMaximo
	 * @return String truncada.
	 */
	public String obterHint(String item, Integer tamanhoMaximo) {
		
		if (item.length() > tamanhoMaximo) {
			item = StringUtils.abbreviate(item, tamanhoMaximo);
		}
		return item;
	}	
	
	public void limpar() {
		limparCampos();
		this.dataModel.limparPesquisa();
		exibirBotaoNovo = Boolean.FALSE;
		Iterator<UIComponent> componentes = FacesContext.getCurrentInstance().getViewRoot().getFacetsAndChildren();
		while (componentes.hasNext()) {
			limparValoresSubmetidos(componentes.next());
		}
	}
	/**
	 * Percorre o formulário resetando os valores digitados nos campos (inputText, inputNumero, selectOneMenu, ...)
	 * 
	 * @param object {@link Object}
	 */
	private void limparValoresSubmetidos(Object object) {
		
		if (object == null || object instanceof UIComponent == false) {
			return;
		}
		
		Iterator<UIComponent> uiComponent = ((UIComponent) object).getFacetsAndChildren();
		while (uiComponent.hasNext()) {
			limparValoresSubmetidos(uiComponent.next());
		}
		
		if (object instanceof UIInput) {
			((UIInput) object).resetValue();
		}
	}
	
	public String novo(){
		this.fatDiariaInternacao = new FatDiariaInternacao();
		return NOVO;
	}
	
	public String editar() {
		return NOVO;
	}
	
	public String remover() throws ApplicationBusinessException{
		try{
			this.faturamentoFacade.removerDiariaInternacaoAutorizada(fatDiariaInternacao);
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_DIARIA_EXCLUIDA_SUCESSO");
		}
		catch (ApplicationBusinessException e) {
			apresentarMsgNegocio(Severity.ERROR, e.getMessage());
		}
		fatDiariaInternacao = new FatDiariaInternacao();
		return null;
	}
	
	public void filtroDiariaInternacao(){
		fatDiariaInternacao = new FatDiariaInternacao();
		
		if(this.tipoValorConta!=null){
			fatDiariaInternacao.setTipoValorConta(tipoValorConta);
		}
		if(this.seq != null){
			fatDiariaInternacao.setSeq(this.seq);
		}
		if(StringUtils.isNotEmpty(this.descricao)){
			fatDiariaInternacao.setDescricao(this.descricao);
		}
		if(this.quantidadeDias != null){
			fatDiariaInternacao.setQuantidadeDias(this.quantidadeDias);
		}
	}
	
	@Override
	public List<FatDiariaInternacao> recuperarListaPaginada(Integer firstResult,
			Integer maxResults, String orderProperty, boolean asc) {
		filtroDiariaInternacao();
		return this.faturamentoFacade.listarDiariasInternacao(firstResult, maxResults, orderProperty, asc, this.fatDiariaInternacao);
	}

	@Override
	public Long recuperarCount() {
		filtroDiariaInternacao();
		return faturamentoFacade.listarDiariasInternacaoCount(fatDiariaInternacao);
	}
	
	//	Getters e Setters
	
	public DynamicDataModel<FatDiariaInternacao> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<FatDiariaInternacao> dataModel) {
		this.dataModel = dataModel;
	}

	public FatDiariaInternacao getFatDiariaInternacao() {
		return fatDiariaInternacao;
	}

	public void setFatDiariaInternacao(FatDiariaInternacao fatDiariaInternacao) {
		this.fatDiariaInternacao = fatDiariaInternacao;
	}

	public DominioDiariaInternacao getTipoValorConta() {
		return tipoValorConta;
	}

	public void setTipoValorConta(DominioDiariaInternacao tipoValorConta) {
		this.tipoValorConta = tipoValorConta;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Integer getQuantidadeDias() {
		return quantidadeDias;
	}

	public void setQuantidadeDias(Integer quantidadeDias) {
		this.quantidadeDias = quantidadeDias;
	}

	public FatDiariaInternacao getFatDiariaInternacaoFiltro() {
		return fatDiariaInternacaoFiltro;
	}

	public void setFatDiariaInternacaoFiltro(
			FatDiariaInternacao fatDiariaInternacaoFiltro) {
		this.fatDiariaInternacaoFiltro = fatDiariaInternacaoFiltro;
	}

	public Boolean getExibirBotaoNovo() {
		return exibirBotaoNovo;
	}

	public void setExibirBotaoNovo(Boolean exibirBotaoNovo) {
		this.exibirBotaoNovo = exibirBotaoNovo;
	}
}