package br.gov.mec.aghu.emergencia.perinatologia.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import br.gov.mec.aghu.emergencia.business.IEmergenciaFacade;
import br.gov.mec.aghu.model.McoTabAdequacaoPeso;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.locator.ServiceLocator;

/**
 * @author Rafael Garcia
 */

public class CadastroAdequacaoPesoPaginatorController extends ActionController
		implements ActionPaginator {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3735240336688571771L;

	private static final String REDIRECIONA_CADASTRO_ADEQUACAO_PESO = "cadastroAdequacaoPesoCRUD";

	@Inject
	private IEmergenciaFacade emergenciaFacade;

	private IPermissionService getPermissionService() {
		return ServiceLocator.getBeanRemote(IPermissionService.class,
				"aghu-casca");
	}

	private boolean exibirBotaoIncluir;
	private boolean permManterAdequacaoPeso;
	private Short igSemanas;

	@Inject
	@Paginator
	private DynamicDataModel<McoTabAdequacaoPeso> dataModel;

	@PostConstruct
	public void init() {
		this.begin(conversation);

		this.permManterAdequacaoPeso = getPermissionService()
				.usuarioTemPermissao(obterLoginUsuarioLogado(),
						"manterAdequacaoPeso", "executar");
	}

	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
		this.exibirBotaoIncluir = true;
	}

	public void limparPesquisa() {
		this.igSemanas = null;
		this.exibirBotaoIncluir = false;
		this.dataModel.setPesquisaAtiva(false);
	}

	// ### Paginação ###

	@Override
	public Long recuperarCount() {
		return emergenciaFacade
				.pesquisarCapacidadesAdequacaoPesoIgSemanasCount(igSemanas);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<McoTabAdequacaoPeso> recuperarListaPaginada(
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc) {

		List<McoTabAdequacaoPeso> result = new ArrayList<McoTabAdequacaoPeso>();

		result = this.emergenciaFacade.pesquisarAdequacaoPesoIgSemanas(
				firstResult, maxResults, orderProperty, asc, igSemanas);

		return result;
	}

	public void reiniciarPaginator() {
		dataModel.reiniciarPaginator();
	}

	public void excluir(Short seq) {
		this.emergenciaFacade.excluirAdequacaoPeso(seq);
		this.dataModel.reiniciarPaginator();
		this.apresentarMsgNegocio(Severity.INFO,
				"EXCLUSAO_ADEQUACAO_PESO_SUCESSO");
	}

	public String editarIncluir() {
		return REDIRECIONA_CADASTRO_ADEQUACAO_PESO;
	}

	// ### GETs e SETs ###

	public boolean isExibirBotaoIncluir() {
		return exibirBotaoIncluir;
	}

	public void setExibirBotaoIncluir(boolean exibirBotaoIncluir) {
		this.exibirBotaoIncluir = exibirBotaoIncluir;
	}

	public boolean isPermManterAdequacaoPeso() {
		return permManterAdequacaoPeso;
	}

	public void setPermManterAdequacaoPeso(boolean permManterAdequacaoPeso) {
		this.permManterAdequacaoPeso = permManterAdequacaoPeso;
	}

	public DynamicDataModel<McoTabAdequacaoPeso> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<McoTabAdequacaoPeso> dataModel) {
		this.dataModel = dataModel;
	}

	public Short getIgSemanas() {
		return igSemanas;
	}

	public void setIgSemanas(Short igSemanas) {
		this.igSemanas = igSemanas;
	}

}
