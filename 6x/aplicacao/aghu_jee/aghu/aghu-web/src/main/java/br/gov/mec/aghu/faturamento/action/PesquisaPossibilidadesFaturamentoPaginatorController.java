package br.gov.mec.aghu.faturamento.action;

import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatItensProcedHospitalarId;
import br.gov.mec.aghu.model.FatProcedimentosHospitalares;
import br.gov.mec.aghu.core.action.ActionController;
import javax.inject.Inject;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.exception.BaseException;

public class PesquisaPossibilidadesFaturamentoPaginatorController extends
		ActionController implements ActionPaginator {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9069917046444100363L;

	private static final String PAGE_CADASTRO_POSSIBILIDADES_FATURAMENTO = "cadastroPossibilidadesFaturamento";

	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@EJB
	private IParametroFacade parametroFacade;

	@EJB
	private IPermissionService permissionService;

	@Inject @Paginator
	private DynamicDataModel<FatItensProcedHospitalar> dataModel;

	// Filtro principal da pesquisa
	private FatItensProcedHospitalar filtro = new FatItensProcedHospitalar();
	
	private FatProcedimentosHospitalares tabelaSuggestion;

	private FatItensProcedHospitalar parametroSelecionado;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
		obterTabelaPadraoSUS();
	}

	public void iniciar() {

		final Boolean permissao = this.permissionService.usuarioTemPermissao(this.obterLoginUsuarioLogado(), "manterCadastrosBasicosFaturamento", "executar");
		this.getDataModel().setUserEditPermission(permissao);
		this.getDataModel().setUserRemovePermission(permissao);
		if (filtro.getId() == null) {

			filtro.setId(new FatItensProcedHospitalarId());
		}
		if(filtro.getProcedimentoHospitalar() == null){
			filtro.setProcedimentoHospitalar(new FatProcedimentosHospitalares());
		}
	}

	@Override
	public List<FatItensProcedHospitalar> recuperarListaPaginada(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc) {
		return this.faturamentoFacade.listarItensProcedimentosHospitalares(
				firstResult, maxResult, orderProperty, asc, this.filtro);
	}

	@Override
	public Long recuperarCount() {
		return this.faturamentoFacade
				.listarItensProcedimentosHospitalaresCount(this.filtro);
	}
	
	/**
	 * Método que obtém da tabela de parâmentro o valor númerico.
	 */
	public void obterTabelaPadraoSUS(){
		
		try {
			
			Short procedimentoSeq = parametroFacade.buscarValorShort(AghuParametrosEnum.P_TABELA_FATUR_PADRAO);
			tabelaSuggestion = this.faturamentoFacade.obterProcedimentoHospitalar(procedimentoSeq);
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * Realiza pesquisa que carrega a grid.
	 */
	public void pesquisar() {
		filtro.setProcedimentoHospitalar(tabelaSuggestion);
		this.dataModel.reiniciarPaginator();
	}

	/**
	 * Limpa os campos e o grid da tela de pesquisa.
	 */
	public void limpar() {
		
		Iterator<UIComponent> componentes = FacesContext.getCurrentInstance().getViewRoot().getFacetsAndChildren();

        while (componentes.hasNext()) {
               
               limparValoresSubmetidos(componentes.next());
        }

		this.filtro = new FatItensProcedHospitalar();
		this.filtro.setId(new FatItensProcedHospitalarId());
		this.dataModel.limparPesquisa();
		this.tabelaSuggestion = null;
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

		Iterator<UIComponent> uiComponent = ((UIComponent) object)
				.getFacetsAndChildren();
		while (uiComponent.hasNext()) {
			limparValoresSubmetidos(uiComponent.next());
		}

		if (object instanceof UIInput) {
			((UIInput) object).resetValue();
		}
	}


	/**
	 * Chama a tela para edição.
	 * 
	 * @return
	 */
	public String editar() {
		return PAGE_CADASTRO_POSSIBILIDADES_FATURAMENTO;
	}

	/**
	 * Deve exibir todos os campos do registro que não aparecem na listagem
	 * 
	 * @param item
	 * @return
	 */
	public String obterHint(FatItensProcedHospitalar item) {

		String descricao = "";
		if (StringUtils.isNotBlank(item.getDescricao())) {
			descricao = StringUtils.abbreviate(item.getDescricao(), 50);
		}
		return descricao;
	}

	/*
	 * Pesquisa SuggestionBox
	 */

	public List<FatProcedimentosHospitalares> pesquisarFaturamentoProcedimentosHospitalares(
			String parametro) {
		return  this.returnSGWithCount(this.faturamentoFacade
				.listarProcedimentosHospitalaresPorSeqEDescricao(parametro),pesquisarFaturamentoProcedimentosHospitalaresCount(parametro));
	}

	public Long pesquisarFaturamentoProcedimentosHospitalaresCount(
			String parametro) {
		return this.faturamentoFacade
				.listarProcedimentosHospitalaresPorSeqEDescricaoCount(parametro);
	}

	/*
	 * Getters and setters
	 */

	public DynamicDataModel<FatItensProcedHospitalar> getDataModel() {
		return dataModel;
	}

	public void setDataModel(
			DynamicDataModel<FatItensProcedHospitalar> dataModel) {
		this.dataModel = dataModel;
	}

	public FatItensProcedHospitalar getFiltro() {
		return filtro;
	}

	public void setFiltro(FatItensProcedHospitalar filtro) {
		this.filtro = filtro;
	}

	public FatItensProcedHospitalar getParametroSelecionado() {
		return parametroSelecionado;
	}

	public void setParametroSelecionado(
			FatItensProcedHospitalar parametroSelecionado) {
		this.parametroSelecionado = parametroSelecionado;
	}

	public FatProcedimentosHospitalares getTabelaSuggestion() {
		return tabelaSuggestion;
	}

	public void setTabelaSuggestion(FatProcedimentosHospitalares tabelaSuggestion) {
		this.tabelaSuggestion = tabelaSuggestion;
	}

}
