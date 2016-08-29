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

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoMensagemLog;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.FatMensagemLog;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Classe responsável por controlar a pesquisa de mensagem de erro
 * 
 * @author thiago.cortes
 *
 */

public class CadastroMensagemErroPaginatorController extends ActionController
		implements ActionPaginator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5933839159812763703L;

	private static final String PAGE_CADASTRO_MENSAGEM_ERRO = "cadastroMensagemErroCRUD";

	private Integer codigo;
	private String descricao;
	private DominioSimNao indSecretario;
	private DominioSituacaoMensagemLog situacao;

	private FatMensagemLog fatMensagemErroFiltro = new FatMensagemLog();

	@EJB
	private IFaturamentoFacade faturamentoFacade;

	@EJB
	private IPermissionService permissionService;

	@Inject @Paginator
	private DynamicDataModel<FatMensagemLog> dataModel;

	@PostConstruct
	protected void inicializar() {
		begin(conversation);
	}

	public void iniciar() {
		final Boolean PERMISSAO_MANTER = this.permissionService.usuarioTemPermissao(
				this.obterLoginUsuarioLogado(),"manterCadastrosBasicosFaturamento", "executar");		
		this.getDataModel().setUserEditPermission(PERMISSAO_MANTER);
	}

	public void pesquisar() {
 		atualizarDataModel();
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
		Iterator<UIComponent> componentes = FacesContext.getCurrentInstance().getViewRoot().getFacetsAndChildren();
		while (componentes.hasNext()) {
			limparValoresSubmetidos(componentes.next());
		}
		
		this.codigo = null;
		this.descricao = null;
		this.indSecretario = null;
		this.situacao = null;
		this.dataModel.limparPesquisa();
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

	public String editar() {
		return PAGE_CADASTRO_MENSAGEM_ERRO;
	}

	public void atualizarDataModel() {
		this.dataModel.reiniciarPaginator();
	}

	public void configurarFiltroPesquisa() {
		
		fatMensagemErroFiltro = new FatMensagemLog();
		
		if (codigo != null) {
			fatMensagemErroFiltro.setCodigo(codigo);
		}
		if (StringUtils.isNotEmpty(this.descricao)) {			
			fatMensagemErroFiltro.setErro(descricao);
		}
		if(this.indSecretario != null){
			String indSec = indSecretario.equals(DominioSimNao.S) ? DominioSituacao.A.toString() : DominioSituacao.I.toString();
			fatMensagemErroFiltro.setIndSecretario(indSec);
		}
		if(this.situacao != null){
			fatMensagemErroFiltro.setSituacao(situacao);
		}
	}	
	
	public void ativarCaracteristica(FatMensagemLog fatMensagemErro) throws ApplicationBusinessException{
		fatMensagemErro.setIndSecretario(DominioSituacao.A.toString());
		alterarSituacao(fatMensagemErro);
	}
	
	public void desativarCaracteristica(FatMensagemLog fatMensagemErro) throws ApplicationBusinessException{
		fatMensagemErro.setIndSecretario(DominioSituacao.I.toString());
		alterarSituacao(fatMensagemErro);
	}
	
	private void alterarSituacao(FatMensagemLog fatMensagemErro) throws ApplicationBusinessException{
		try{
			this.faturamentoFacade.alterarMensagemErro(fatMensagemErro);
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM DE ERRO ALTERADA COM SUCESSO");
			atualizarDataModel();
		}
		catch (BaseException e) {			
			apresentarExcecaoNegocio(e);
		}
	}

	@Override
	public List<FatMensagemLog> recuperarListaPaginada(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {
		configurarFiltroPesquisa();
		return this.faturamentoFacade.listarMensagemErro(firstResult, maxResult, orderProperty, asc, fatMensagemErroFiltro);
	}

	@Override
	public Long recuperarCount() {
		configurarFiltroPesquisa();
		return faturamentoFacade.listarMensagensLogCount(fatMensagemErroFiltro);
	}
	
	//	Getters e Setters
	
	public DynamicDataModel<FatMensagemLog> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<FatMensagemLog> dataModel) {
		this.dataModel = dataModel;
	}

	public FatMensagemLog getfatMensagemErroFiltro() {
		return fatMensagemErroFiltro;
	}

	public void setFatMensagemErroFiltro(
			FatMensagemLog fatMensagemLog) {
		this.fatMensagemErroFiltro = fatMensagemLog;
	}

	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public DominioSimNao getIndSecretario() {
		return indSecretario;
	}

	public void setIndSecretario(DominioSimNao indSecretario) {
		this.indSecretario = indSecretario;
	}

	public DominioSituacaoMensagemLog getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacaoMensagemLog situacao) {
		this.situacao = situacao;
	}

	public FatMensagemLog getFatMensagemErroFiltro() {
		return fatMensagemErroFiltro;
	}

	public DominioSituacaoMensagemLog getSituacaoNaoEnc() {
		return DominioSituacaoMensagemLog.NAOENC;
	}

	public DominioSituacaoMensagemLog getSituacaoNaoCob() {
		return DominioSituacaoMensagemLog.NAOCOBR;
	}

	public DominioSituacaoMensagemLog getSituacaoNaoIncons() {
		return DominioSituacaoMensagemLog.INCONS;
	}
	
}
