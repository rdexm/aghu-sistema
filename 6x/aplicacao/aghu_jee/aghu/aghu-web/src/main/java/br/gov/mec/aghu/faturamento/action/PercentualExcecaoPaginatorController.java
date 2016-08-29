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

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatItensProcedHospitalarId;
import br.gov.mec.aghu.model.FatProcedimentosHospitalares;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;

public class PercentualExcecaoPaginatorController extends ActionController implements ActionPaginator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7460222998851704924L;
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	@EJB
	private IParametroFacade parametroFacade;
	
	private static final String PAGE_CADASTRO_PERCENTUAL_EXCECAO = "percentualExcecaoCRUD";
	
	private FatItensProcedHospitalar fatItensProcedHospitalarFiltro = new FatItensProcedHospitalar();
	private FatItensProcedHospitalar fatItensProcedHospitalarSelection = new FatItensProcedHospitalar();
	private FatProcedimentosHospitalares tabelaSuggestion = null;
	private FatItensProcedHospitalarId fatItensProcedHospitalarId = new FatItensProcedHospitalarId();
	
	@Inject @Paginator
	private DynamicDataModel<FatItensProcedHospitalar> dataModel;

	@PostConstruct
	public void inicializar() {
		begin(conversation);
		obterTabelaPadraoSUS();
	}
	
	public void pesquisar() {
		fatItensProcedHospitalarFiltro.setProcedimentoHospitalar(tabelaSuggestion);
		fatItensProcedHospitalarFiltro.setId(fatItensProcedHospitalarId);
		getDataModel().reiniciarPaginator();
	}
	
	public void limparPesquisa(){
		Iterator<UIComponent> componentes = FacesContext.getCurrentInstance().getViewRoot().getFacetsAndChildren();
		while (componentes.hasNext()) {
			limparValoresSubmetidos(componentes.next());
		}
		
		setFatItensProcedHospitalarFiltro(new FatItensProcedHospitalar());
		setFatItensProcedHospitalarId(new FatItensProcedHospitalarId());
		limparCampo();
		getDataModel().limparPesquisa();
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
	
	public String editar(){
		return PAGE_CADASTRO_PERCENTUAL_EXCECAO;
	}
	
	public void limparCampo() {
		tabelaSuggestion = null;
	}
	
	@Override
	public Long recuperarCount() {
		return this.faturamentoFacade.listarItensProcedimentosHospitalaresCount(fatItensProcedHospitalarFiltro);
	}
	
	@Override
	public List<FatItensProcedHospitalar> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return this.faturamentoFacade.listarItensProcedimentosHospitalares(firstResult, maxResult, orderProperty, asc, fatItensProcedHospitalarFiltro);
	}
	
	/**
	 * Busca Tabela SuggestionBox.
	 */
	public void obterTabelaPadraoSUS(){
        try {
            Short procedimentoSeq = parametroFacade.buscarValorShort(AghuParametrosEnum.P_TABELA_FATUR_PADRAO);
            setTabelaSuggestion(this.faturamentoFacade.obterProcedimentoHospitalar(procedimentoSeq));
        } catch (BaseException e) {
            apresentarExcecaoNegocio(e);
        }
	}
	
	public List<FatProcedimentosHospitalares> pesquisarFaturamentoProcedimentosHospitalares(String parametro) {
		List<FatProcedimentosHospitalares> lista = this.faturamentoFacade.listarProcedimentosHospitalaresPorSeqEDescricao(parametro.trim());
	    return lista;
	}
	
	public Long pesquisarFaturamentoProcedimentosHospitalaresCount(String parametro) {
	    return this.faturamentoFacade.listarProcedimentosHospitalaresPorSeqEDescricaoCount(parametro);
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

	/*
	 * GETs e SETs.
	 */

	public DynamicDataModel<FatItensProcedHospitalar> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<FatItensProcedHospitalar> dataModel) {
		this.dataModel = dataModel;
	}

	public FatItensProcedHospitalar getFatItensProcedHospitalarFiltro() {
		return fatItensProcedHospitalarFiltro;
	}

	public void setFatItensProcedHospitalarFiltro(FatItensProcedHospitalar fatItensProcedHospitalar) {
		this.fatItensProcedHospitalarFiltro = fatItensProcedHospitalar;
	}

	public FatProcedimentosHospitalares getTabelaSuggestion() {
		return tabelaSuggestion;
	}

	public void setTabelaSuggestion(FatProcedimentosHospitalares tabelaSuggestion) {
		this.tabelaSuggestion = tabelaSuggestion;
	}

	public FatItensProcedHospitalarId getFatItensProcedHospitalarId() {
		return fatItensProcedHospitalarId;
	}

	public void setFatItensProcedHospitalarId(FatItensProcedHospitalarId fatItensProcedHospitalarId) {
		this.fatItensProcedHospitalarId = fatItensProcedHospitalarId;
	}

	public FatItensProcedHospitalar getFatItensProcedHospitalarSelection() {
		return fatItensProcedHospitalarSelection;
	}

	public void setFatItensProcedHospitalarSelection(
			FatItensProcedHospitalar fatItensProcedHospitalarSelection) {
		this.fatItensProcedHospitalarSelection = fatItensProcedHospitalarSelection;
	}
	
}
