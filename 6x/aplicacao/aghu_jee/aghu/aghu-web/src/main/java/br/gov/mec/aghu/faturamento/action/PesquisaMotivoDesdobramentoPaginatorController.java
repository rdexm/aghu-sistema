package br.gov.mec.aghu.faturamento.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.FatMotivoDesdobramento;
import br.gov.mec.aghu.model.FatTipoAih;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;

public class PesquisaMotivoDesdobramentoPaginatorController extends ActionController implements ActionPaginator {


	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	private static final long serialVersionUID = 5674793387617959097L;

	private static final String PAGE_CADASTRO_MOTIVOS_DESDOBRAMENTO = "faturamento-cadastroMotivosDesdobramento"; 	
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;

	@Inject 
	@Paginator
	private DynamicDataModel<FatMotivoDesdobramento> dataModel;

	private FatMotivoDesdobramento parametroSelecionado;
	
	private FatTipoAih tipoAih = null;

	// Filtro principal da pesquisa
	private FatMotivoDesdobramento filtro = new FatMotivoDesdobramento();

	/*
	 * Filtros adicionais
	 */
	private DominioSimNao notificaSsma; // Notificação Compulsória
	
	@EJB
	private IPermissionService permissionService;

	public void iniciar() {
	
		if(super.isValidInitMethod()){
	 

		final Boolean permissao = this.permissionService.usuarioTemPermissao(this.obterLoginUsuarioLogado(), "manterCadastrosBasicosFaturamento", "executar");
		this.getDataModel().setUserRemovePermission(permissao);
		this.getDataModel().setUserEditPermission(permissao);	
		}
	}
	
	/*
	 * Pesquisa
	 */

	@Override
	public List<FatMotivoDesdobramento> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return this.faturamentoFacade.listarMotivoDesdobramento(firstResult, maxResult, orderProperty, asc, this.filtro);
	}

	@Override
	public Long recuperarCount() {
		return this.faturamentoFacade.listarMotivoDesdobramentoCount(this.filtro);
	}

	public void pesquisar() {
		this.filtro.setTipoAih(tipoAih);
		this.dataModel.reiniciarPaginator();
	}

	public void limpar() {
		this.tipoAih = null;
		this.filtro = new FatMotivoDesdobramento();
		this.notificaSsma = null;		
		this.dataModel.limparPesquisa();
	}
	
	
	public String iniciarInclusao() {
		return PAGE_CADASTRO_MOTIVOS_DESDOBRAMENTO;
	}

	public String editar() {
		return PAGE_CADASTRO_MOTIVOS_DESDOBRAMENTO;
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
	 * Pesquisas SuggestionBox
	 */

	public List<FatTipoAih> pesquisarTipoAih(String parametro) {
		return this.returnSGWithCount(this.faturamentoFacade.pesquisarTipoAih(parametro),pesquisarTipoAihCount(parametro));
	}

	public Long pesquisarTipoAihCount(String parametro) {
		return this.faturamentoFacade.pesquisarTipoAihCount(parametro);
	}
	
	public IFaturamentoFacade getFaturamentoFacade() {
		return faturamentoFacade;
	}

	public void setFaturamentoFacade(IFaturamentoFacade faturamentoFacade) {
		this.faturamentoFacade = faturamentoFacade;
	}

	public DynamicDataModel<FatMotivoDesdobramento> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<FatMotivoDesdobramento> dataModel) {
		this.dataModel = dataModel;
	}

	public FatMotivoDesdobramento getParametroSelecionado() {
		return parametroSelecionado;
	}

	public void setParametroSelecionado(FatMotivoDesdobramento parametroSelecionado) {
		this.parametroSelecionado = parametroSelecionado;
	}

	public FatMotivoDesdobramento getFiltro() {
		return filtro;
	}

	public void setFiltro(FatMotivoDesdobramento filtro) {
		this.filtro = filtro;
	}

	public DominioSimNao getNotificaSsma() {
		return notificaSsma;
	}

	public void setNotificaSsma(DominioSimNao notificaSsma) {
		this.notificaSsma = notificaSsma;
	}

	public IPermissionService getPermissionService() {
		return permissionService;
	}

	public void setPermissionService(IPermissionService permissionService) {
		this.permissionService = permissionService;
	}

	public FatTipoAih getTipoAih() {
		return tipoAih;
	}

	public void setTipoAih(FatTipoAih tipoAih) {
		this.tipoAih = tipoAih;
	}

}