package br.gov.mec.aghu.faturamento.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.FatMotivoPendencia;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.Severity;

public class PesquisaMotivoDePendenciaDaContaPaginatorController extends
		ActionController implements ActionPaginator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 183967902808733083L;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	private static final String PAGE_CADASTRO_MOTIVOS_PENDENCIA_CONTA = "cadastroMotivoDePendenciaDaConta";

	@EJB
	private IFaturamentoFacade faturamentoFacade;

	@Inject @Paginator
	private DynamicDataModel<FatMotivoPendencia> dataModel;

	private FatMotivoPendencia parametroSelecionado;

	// Filtro principal da pesquisa
	private FatMotivoPendencia filtro = new FatMotivoPendencia();

	@EJB
	private IPermissionService permissionService;

	/**
	 * Inicializa a tela passando as permissões para dataTable
	 */
	public void iniciar() {
	 

		final Boolean permissao = this.permissionService.usuarioTemPermissao(
				this.obterLoginUsuarioLogado(),
				"manterCadastrosBasicosFaturamento", "executar");
		this.getDataModel().setUserEditPermission(permissao);
		this.getDataModel().setUserRemovePermission(permissao);
	
	}

	/*
	 * Pesquisa
	 */

	@Override
	public List<FatMotivoPendencia> recuperarListaPaginada(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {

		return this.faturamentoFacade.pesquisarMotivosPendencia(firstResult,
				maxResult, orderProperty, asc, this.filtro);
	}

	@Override
	public Long recuperarCount() {
		return this.faturamentoFacade
				.pesquisarMotivosPendenciaCount(this.filtro);
	}

	/**
	 * Pesquisa Motivo de Pendência da Conta passando filtro
	 */
	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
	}

	/**
	 * Limpar os campos das tela e remove a grid e botão novo
	 */
	public void limpar() {
		this.filtro = new FatMotivoPendencia();
		this.dataModel.limparPesquisa();
	}

	/**
	 * Abri a tela de cadastroMotivoDePendenciaDaConta para alterar o item
	 * selecionado
	 * 
	 * @return
	 */
	public String editar() {
		return PAGE_CADASTRO_MOTIVOS_PENDENCIA_CONTA;
	}

	/**
	 * Exclui Motivo de Pendência da Conta selecionado
	 */
	public void excluir() {
		try {

			this.faturamentoFacade
					.removerFatMotivoPendencia(this.parametroSelecionado
							.getSeq());

			this.apresentarMsgNegocio(Severity.INFO,
					"MENSAGEM_SUCESSO_EXCLUSAO_MOTIVO_PENDENCIA",
					this.parametroSelecionado.getDescricao());
		} catch (BaseListException e) {
			apresentarExcecaoNegocio(e);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * @return the dataModel
	 */
	public DynamicDataModel<FatMotivoPendencia> getDataModel() {
		return dataModel;
	}

	/**
	 * @param dataModel
	 *            the dataModel to set
	 */
	public void setDataModel(DynamicDataModel<FatMotivoPendencia> dataModel) {
		this.dataModel = dataModel;
	}

	/**
	 * @return the parametroSelecionado
	 */
	public FatMotivoPendencia getParametroSelecionado() {
		return parametroSelecionado;
	}

	/**
	 * @param parametroSelecionado
	 *            the parametroSelecionado to set
	 */
	public void setParametroSelecionado(FatMotivoPendencia parametroSelecionado) {
		this.parametroSelecionado = parametroSelecionado;
	}

	/**
	 * @return the filtro
	 */
	public FatMotivoPendencia getFiltro() {
		return filtro;
	}

	/**
	 * @param filtro
	 *            the filtro to set
	 */
	public void setFiltro(FatMotivoPendencia filtro) {
		this.filtro = filtro;
	}

}
