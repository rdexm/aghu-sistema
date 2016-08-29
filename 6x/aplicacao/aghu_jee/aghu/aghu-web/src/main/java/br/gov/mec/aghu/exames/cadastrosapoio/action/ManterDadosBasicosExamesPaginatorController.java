package br.gov.mec.aghu.exames.cadastrosapoio.action;

import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.model.AelExames;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.Severity;

public class ManterDadosBasicosExamesPaginatorController extends ActionController implements ActionPaginator {

	@PostConstruct
	protected void inicializar() {
		this.dataModel.setUserEditPermission(this.permissionService.usuarioTemPermissao(this.obterLoginUsuarioLogado(), "manterDadosBasicosExames", "pesquisar"));
		this.dataModel.setUserRemovePermission(this.permissionService.usuarioTemPermissao(this.obterLoginUsuarioLogado(), "manterDadosBasicosExames", "executar"));
		this.begin(conversation);
	}

	@Inject @Paginator
	private DynamicDataModel<AelExames> dataModel;

	private AelExames parametroSelecionado;

	private static final long serialVersionUID = -3912312994328661993L;

	private static final String PAGE_EXAMES_MANTER_DADOS_BASICOS_EXAMES_CRUD = "exames-manterDadosBasicosExamesCRUD";

	private static final String PAGE_EXAMES_MANTER_DADOS_BASICOS_EXAMES_PESQUISA = "exames-manterDadosBasicosExamesPesquisa";

	@EJB
	private IExamesFacade examesFacade;

	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;

	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private IPermissionService permissionService;

	// Campos de filtro para pesquisa
	private String sigla;
	private String descricao;
	private String descricaoUsual;
	private DominioSimNao indImpressao;
	private DominioSimNao indConsisteInterface;
	private DominioSimNao indPermiteAnexarDoc;
	private DominioSituacao indSituacao;

	// Controla exibição do botão "Novo"
	private boolean exibirBotaoNovo;

	private AghUnidadesFuncionais unidadeExecutora;

	public void pesquisar() {
		this.getDataModel().reiniciarPaginator();
		this.exibirBotaoNovo = true;
	}

	/**
	 * Metodo para pesquisa na suggestion box de unidade executora
	 */
	public List<AghUnidadesFuncionais> obterAghUnidadesFuncionaisExecutoras(String objPesquisa) {
		return this.aghuFacade.pesquisarUnidExecPorCodDescCaractExames(objPesquisa);
	}

	/**
	 * Método executado ao clicar no botão limpar
	 */
	public void limparPesquisa() {
		this.sigla = null;
		this.descricao = null;
		this.descricaoUsual = null;
		this.indImpressao = null;
		this.indConsisteInterface = null;
		this.indPermiteAnexarDoc = null;
		this.indSituacao = null;
		this.exibirBotaoNovo = false;
		this.unidadeExecutora = null;
		this.getDataModel().limparPesquisa();
	}

	/**
	 * Recupera uma instância com as informações de pesquisa atualizadas
	 * 
	 * @return
	 */
	private AelExames getElementoDadosBasicosExames() {
		// Cria objeto com os parâmetros para busca
		AelExames elemento = new AelExames();
		this.sigla = StringUtils.trim(this.sigla);
		elemento.setSigla(this.sigla);
		this.descricao = StringUtils.trim(this.descricao);
		elemento.setDescricao(this.descricao);
		this.descricaoUsual = StringUtils.trim(this.descricaoUsual);
		elemento.setDescricaoUsual(this.descricaoUsual);
		elemento.setIndImpressao(this.indImpressao != null ? this.indImpressao.isSim() : null);
		elemento.setIndConsisteInterface(this.indConsisteInterface != null ? this.indConsisteInterface.isSim() : null);
		elemento.setIndPermiteAnexarDoc(this.indPermiteAnexarDoc != null ? this.indPermiteAnexarDoc.isSim() : null);
		elemento.setIndSituacao(this.indSituacao);
		return elemento;
	}

	@Override
	public Long recuperarCount() {
		return this.examesFacade.pesquisarDadosBasicosExamesCount(getElementoDadosBasicosExames(), this.unidadeExecutora);
	}

	@Override
	public List<AelExames> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return this.examesFacade.pesquisarDadosBasicosExames(firstResult, maxResult, orderProperty, asc, getElementoDadosBasicosExames(), this.unidadeExecutora);
	}

	public String editarExame() {
		if (verificarAlteradoOutroUsuario()) {
			return PAGE_EXAMES_MANTER_DADOS_BASICOS_EXAMES_PESQUISA;
		}
		return PAGE_EXAMES_MANTER_DADOS_BASICOS_EXAMES_CRUD;
	}

	/**
	 * Excluir
	 */
	public void excluir() {

		if (verificarAlteradoOutroUsuario()) {
			return;
		}

		try {
			String descricao = this.parametroSelecionado.getDescricao();
			if (this.parametroSelecionado != null) {
				this.cadastrosApoioExamesFacade.removerAelExames(this.parametroSelecionado);
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_REMOCAO_DADOS_BASICOS_EXAMES", descricao);
			} else {
				this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ERRO_REMOCAO_DADOS_BASICOS_EXAMES");
			}
			this.getDataModel().reiniciarPaginator();
		} catch (BaseListException e) {
			for (Iterator<BaseException> errors = e.iterator(); errors.hasNext();) {
				BaseException aghuNegocioException = (BaseException) errors.next();
				super.apresentarExcecaoNegocio(aghuNegocioException);
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} finally {
			this.parametroSelecionado = null;
		}
	}

	private boolean verificarAlteradoOutroUsuario() {
		if (this.parametroSelecionado == null || this.examesFacade.obterAelExamesPeloId(this.parametroSelecionado.getSigla()) == null) {
			apresentarMsgNegocio(Severity.INFO, "OPTIMISTIC_LOCK");
			this.parametroSelecionado = null;
			this.pesquisar();
			return true;
		}
		return false;
	}

	/*
	 * Getters and Setters abaixo...
	 */

	public boolean isExibirBotaoNovo() {
		return exibirBotaoNovo;
	}

	public void setExibirBotaoNovo(boolean exibirBotaoNovo) {
		this.exibirBotaoNovo = exibirBotaoNovo;
	}

	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricaoUsual() {
		return descricaoUsual;
	}

	public void setDescricaoUsual(String descricaoUsual) {
		this.descricaoUsual = descricaoUsual;
	}

	public DominioSimNao getIndImpressao() {
		return indImpressao;
	}

	public void setIndImpressao(DominioSimNao indImpressao) {
		this.indImpressao = indImpressao;
	}

	public DominioSimNao getIndConsisteInterface() {
		return indConsisteInterface;
	}

	public void setIndConsisteInterface(DominioSimNao indConsisteInterface) {
		this.indConsisteInterface = indConsisteInterface;
	}

	public DominioSimNao getIndPermiteAnexarDoc() {
		return indPermiteAnexarDoc;
	}

	public void setIndPermiteAnexarDoc(DominioSimNao indPermiteAnexarDoc) {
		this.indPermiteAnexarDoc = indPermiteAnexarDoc;
	}

	public AghUnidadesFuncionais getUnidadeExecutora() {
		return unidadeExecutora;
	}

	public void setUnidadeExecutora(AghUnidadesFuncionais unidadeExecutora) {
		this.unidadeExecutora = unidadeExecutora;
	}

	public DynamicDataModel<AelExames> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AelExames> dataModel) {
		this.dataModel = dataModel;
	}

	public AelExames getParametroSelecionado() {
		return parametroSelecionado;
	}

	public void setParametroSelecionado(AelExames parametroSelecionado) {
		this.parametroSelecionado = parametroSelecionado;
	}
}
