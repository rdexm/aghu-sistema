package br.gov.mec.aghu.transplante.action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoOrgao;
import br.gov.mec.aghu.model.MtxDoencaBases;
import br.gov.mec.aghu.transplante.business.ITransplanteFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.Severity;


/**
 * @author danilo.santos
 */

public class DoencasBasePaginatorController extends ActionController implements ActionPaginator {
	private static final long serialVersionUID = 1308195042046060091L;
		
	@EJB
	private ITransplanteFacade transplanteFacade;
	@Inject @Paginator
	private DynamicDataModel<MtxDoencaBases> dataModel;
	private MtxDoencaBases mtxDoencaBases = new MtxDoencaBases();
	private DominioTipoOrgao orgaoDoencaBase;
	private String descricaoDoencaBase;
	private DominioSituacao situacaoDoencaBase;
	private DominioSituacao parametroSelecionado;
	private Boolean exibir;
	private Boolean edicaoAtiva = false;
	private static final String PAGE_CADASTRA_DOENCA_BASE = "transplante-novoDoencaBase";
	private static final String PAGE_PESQUISA_DOENCA_BASE = "transplante-doencaBase";

	
	public DynamicDataModel<MtxDoencaBases> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<MtxDoencaBases> dataModel) {
		this.dataModel = dataModel;
	}
		
	@PostConstruct
	public void init() {
		this.begin(conversation, true);
		this.orgaoDoencaBase = DominioTipoOrgao.R; //Seta Rim como padrão ao iniciar a tela		
	}
		
	public void pesquisar() {
		exibir = true;
		edicaoAtiva = false;
		this.mtxDoencaBases.setDescricao(this.descricaoDoencaBase);
		this.mtxDoencaBases.setTipoOrgao(this.orgaoDoencaBase);
		this.mtxDoencaBases.setIndSituacao(this.situacaoDoencaBase);
		dataModel.setPesquisaAtiva(true);
		this.dataModel.reiniciarPaginator();
	}
	
	public void limparPesquisa() {
		Iterator<UIComponent> componentes = FacesContext.getCurrentInstance().getViewRoot().getFacetsAndChildren();
		while (componentes.hasNext()) {
			limparValoresSubmetidos(componentes.next());
		}
		this.dataModel.limparPesquisa();
		this.situacaoDoencaBase = null;
		this.descricaoDoencaBase = null;
		this.orgaoDoencaBase = DominioTipoOrgao.R;
		pesquisar();
	
	}
	
	public String obterHint(String item, Integer tamanhoMaximo) {
		if (item.length() > tamanhoMaximo) {
			item = StringUtils.abbreviate(item, tamanhoMaximo);
		}
		return item;
	}
	
	@Override
	public Long recuperarCount() {
		return this.transplanteFacade.obterListaDoencasBaseCount(mtxDoencaBases);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MtxDoencaBases> recuperarListaPaginada(Integer firstResult, Integer maxResults, String orderProperty, boolean asc) {
		List<MtxDoencaBases> result = this.transplanteFacade.obterListaDoencasBase(firstResult, maxResults, orderProperty,asc, mtxDoencaBases);
		if (result == null) {
			result = new ArrayList<MtxDoencaBases>();
		}
		return result;
	}

	// Executar consulta C2, se retornar resultado, exibe mensagem de erro MS01 e não excluí.
	
	public String excluir(MtxDoencaBases mtxDoencaBase){
		if(this.transplanteFacade.excluirDoencaBase(mtxDoencaBase)){
			apresentarMsgNegocio(Severity.INFO,"MSG_EXCLUIR_SUCESSO_MTX_DOENCAS");
		}else{
			apresentarMsgNegocio(Severity.FATAL,"MSG_ERRO_EXCLUIR_MTX_DOENCAS");
		}
		return PAGE_PESQUISA_DOENCA_BASE;	
	}
		
	public String inserirEditar() {
		return PAGE_CADASTRA_DOENCA_BASE;
	}
		
	public DominioTipoOrgao getOrgaoDoencaBase() {
		return orgaoDoencaBase;
	}

	public void setOrgaoDoencaBase(DominioTipoOrgao orgaoDoencaBase) {
		this.orgaoDoencaBase = orgaoDoencaBase;
	}

	public String getDescricaoDoencaBase() {
		return descricaoDoencaBase;
	}
	
	public void setDescricaoDoencaBase(String descricaoDoencaBase) {
		this.descricaoDoencaBase = descricaoDoencaBase;
	}

	public DominioSituacao getSituacaoDoencaBase() {
		return situacaoDoencaBase;
	}

	public void setSituacaoDoencaBase(DominioSituacao situacaoDoencaBase) {
		this.situacaoDoencaBase = situacaoDoencaBase;
	}

	public Boolean getExibir() {
		return exibir;
	}

	public void setExibir(Boolean exibir) {
		this.exibir = exibir;
	}

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

	public Boolean getEdicaoAtiva() {
		return edicaoAtiva;
	}

	public void setEdicaoAtiva(Boolean edicaoAtiva) {
		this.edicaoAtiva = edicaoAtiva;
	}

	public MtxDoencaBases getMtxDoencaBases() {
		return mtxDoencaBases;
	}

	public void setMtxDoencaBases(MtxDoencaBases mtxDoencaBases) {
		this.mtxDoencaBases = mtxDoencaBases;
	}

	public DominioSituacao getParametroSelecionado() {
		return parametroSelecionado;
	}

	public void setParametroSelecionado(DominioSituacao parametroSelecionado) {
		this.parametroSelecionado = parametroSelecionado;
	}

	
}
