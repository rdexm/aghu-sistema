package br.gov.mec.aghu.ambulatorio.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AacTipoAgendamento;
import br.gov.mec.aghu.model.AinAcomodacoes;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * Classe responsável por controlar as ações da listagem de Tipos de
 * agendamentos mas chamada de tipo de autorização
 * 
 * @author cicero.artifon
 */
public class ManterTipoAutorizacaoPaginatorController extends ActionController implements ActionPaginator {
	private static final String MANTER_TIPO_AUTORIZACAO_CRUD = "manterTipoAutorizacaoCRUD";

	private static final long serialVersionUID = -8839139223249516282L;

	@EJB
	private IAmbulatorioFacade ambulatorioFacade;

	@EJB
	private IPermissionService permissionService;
	
	@Inject @Paginator
	private DynamicDataModel<AinAcomodacoes> dataModel;

	/**
	 * Atributos dos campo de filtro da pesquisa do tipo de Autorização.
	 */
	private Short codigoTipoAutorizacao;
	private String descricaoTipoAutorizacao;
	private DominioSituacao situacaoTipoAutorizacao;
	private AacTipoAgendamento selecionado;

	/**
	 * Atributo utilizado para controlar a exibicao do botao "incluir tipo de
	 * autorização"
	 */
	private boolean exibirBotaoIncluirTipoAutorizacao;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public void inicio(){
	 

	 

		final Boolean permissao = this.permissionService.usuarioTemPermissao(this.obterLoginUsuarioLogado(), "manterTipoAutorizacao", "alterar");
		this.getDataModel().setUserRemovePermission(permissao);
		this.getDataModel().setUserEditPermission(permissao);
	
	}
	
	
	public void pesquisar() {
		this.getDataModel().reiniciarPaginator();
		this.exibirBotaoIncluirTipoAutorizacao = true;
	}

	public void limparPesquisa() {
		this.codigoTipoAutorizacao = null;
		this.descricaoTipoAutorizacao = null;
		this.situacaoTipoAutorizacao = null;
		this.exibirBotaoIncluirTipoAutorizacao = false;
		this.getDataModel().setPesquisaAtiva(false);
	}

	@Override
	public Long recuperarCount() {
		return this.ambulatorioFacade.countTipoAgendamentoPaginado(codigoTipoAutorizacao, descricaoTipoAutorizacao, situacaoTipoAutorizacao);
	}

	@Override
	public List<AacTipoAgendamento> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		List<AacTipoAgendamento> result = this.ambulatorioFacade.pesquisarTipoAgendamentoPaginado(firstResult, maxResult, orderProperty, asc,
				codigoTipoAutorizacao, descricaoTipoAutorizacao, situacaoTipoAutorizacao);

		if (result == null) {
			result = new ArrayList<AacTipoAgendamento>();
		}
		return result;
	}

	public void excluir() {
		try {
			this.ambulatorioFacade.removerTipoAgendamento(getSelecionado().getSeq());

			this.getDataModel().reiniciarPaginator();
			this.apresentarMsgNegocio(br.gov.mec.aghu.core.exception.Severity.INFO, "MSG_TIPO_AUTORIZACAO_EXCLUIDO_SUCESSO");
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
    public String iniciarInclusao() {
    	return MANTER_TIPO_AUTORIZACAO_CRUD;
    }

	public String editar(){
		return MANTER_TIPO_AUTORIZACAO_CRUD;
	}

	public IAmbulatorioFacade getAmbulatorioFacade() {
		return ambulatorioFacade;
	}

	public void setAmbulatorioFacade(IAmbulatorioFacade ambulatorioFacade) {
		this.ambulatorioFacade = ambulatorioFacade;
	}

	public String getDescricaoTipoAutorizacao() {
		return descricaoTipoAutorizacao;
	}

	public void setDescricaoTipoAutorizacao(String descricaoTipoAutorizacao) {
		this.descricaoTipoAutorizacao = descricaoTipoAutorizacao;
	}

	public DominioSituacao getSituacaoTipoAutorizacao() {
		return situacaoTipoAutorizacao;
	}

	public void setSituacaoTipoAutorizacao(DominioSituacao situacaoTipoAutorizacao) {
		this.situacaoTipoAutorizacao = situacaoTipoAutorizacao;
	}

	public Short getCodigoTipoAutorizacao() {
		return codigoTipoAutorizacao;
	}

	public void setCodigoTipoAutorizacao(Short codigoTipoAutorizacao) {
		this.codigoTipoAutorizacao = codigoTipoAutorizacao;
	}

	public boolean isExibirBotaoIncluirTipoAutorizacao() {
		return exibirBotaoIncluirTipoAutorizacao;
	}

	public void setExibirBotaoIncluirTipoAutorizacao(boolean exibirBotaoIncluirTipoAutorizacao) {
		this.exibirBotaoIncluirTipoAutorizacao = exibirBotaoIncluirTipoAutorizacao;
	}

	public AacTipoAgendamento getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(AacTipoAgendamento selecionado) {
		this.selecionado = selecionado;
	}

	public DynamicDataModel<AinAcomodacoes> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AinAcomodacoes> dataModel) {
		this.dataModel = dataModel;
	}

}
