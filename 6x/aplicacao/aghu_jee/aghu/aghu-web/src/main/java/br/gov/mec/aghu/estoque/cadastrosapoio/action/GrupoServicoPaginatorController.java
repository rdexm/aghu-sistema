package br.gov.mec.aghu.estoque.cadastrosapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.ScoGrupoServico;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class GrupoServicoPaginatorController extends ActionController implements
		ActionPaginator {

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	@Inject @Paginator
	private DynamicDataModel<ScoGrupoServico> dataModel;
	private final String PAGE_GRUPO_SERVICO_CRUD = "grupoServicoList";

	private static final long serialVersionUID = 4696532475030194013L;

	@EJB
	private IComprasFacade comprasFacade;

	// filtros da tela
	private Integer codigo;
	private Integer codigoExcluir;
	private String descricao;
	private DominioSimNao engenharia;
	
	private Boolean indTituloAvulso;
	private Boolean indEngenharia;
	private ScoGrupoServico grupoServico = new ScoGrupoServico();
	private DominioSimNao[] situacaoIndEngenharia = { DominioSimNao.S,
			DominioSimNao.N };
	// private Boolean engenhariaEdicao = Boolean.FALSE;
	private boolean exibirBotaoAdicionar = Boolean.TRUE;

	/**
	 * Inicia.
	 */
	public void iniciar() {
	 

		if (exibirBotaoAdicionar) {
			grupoServico = new ScoGrupoServico();
		}
	
	}

	public void limpar() {
		this.codigo = null;
		this.descricao = null;
		this.indEngenharia = null;
		this.engenharia = null;
		this.indTituloAvulso = null;
		this.dataModel.setPesquisaAtiva(Boolean.TRUE);
	}

	public void pesquisar() {
		if (engenharia != null && engenharia.equals(DominioSimNao.S)) {
			indEngenharia = Boolean.TRUE;
		} else if (engenharia != null) {
			indEngenharia = Boolean.FALSE;
		}
		this.dataModel.reiniciarPaginator();
	}

	/**
	 * Incluir.
	 * 
	 * @return "incluir"
	 */
	public String salvar() {

		try {

			if (grupoServico == null || grupoServico.getCodigo() == null) {
				this.comprasFacade.salvarScoGrupoServico(grupoServico);
				this.apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_M4_GRUPO_SERVICO");

			} else {
				this.comprasFacade.editarScoGrupoServico(grupoServico);
				this.apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_M5_GRUPO_SERVICO");
				this.exibirBotaoAdicionar = Boolean.TRUE;

			}
			grupoServico = new ScoGrupoServico();
			this.pesquisar();

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}

		return PAGE_GRUPO_SERVICO_CRUD;
	}

	/**
	 * @param ScoGrupoServico
	 *            grupoServico
	 * @return "editar"
	 */
	public String editar(ScoGrupoServico grupoServico) {
		this.exibirBotaoAdicionar = false;
		this.grupoServico = grupoServico;
		return PAGE_GRUPO_SERVICO_CRUD;
	}

	public String cancelarEdicao() {
		this.exibirBotaoAdicionar = true;
		grupoServico = new ScoGrupoServico();
		return PAGE_GRUPO_SERVICO_CRUD;
	}

	public void excluir() {

		try {
			ScoGrupoServico grupoServicoEx = this.comprasFacade
					.obterGrupoServico(codigoExcluir);
			this.comprasFacade.excluirScoGrupoServico(grupoServicoEx);

			this.apresentarMsgNegocio(Severity.INFO,
					"MENSAGEM_M6_GRUPO_SERVICO");

			this.pesquisar();

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	@Override
	public Long recuperarCount() {
		return comprasFacade.pesquisarGrupoServicosCount(codigo, descricao,
				indTituloAvulso, indEngenharia);
	}

	@Override
	public List<ScoGrupoServico> recuperarListaPaginada(Integer first,
			Integer max, String order, boolean asc) {
		return comprasFacade.pesquisarGrupoServicos(codigo, descricao,
				indEngenharia, indTituloAvulso,first, max, order, asc);
	}

	public Boolean getIndTituloAvulso() {
		return indTituloAvulso;
	}

	public void setIndTituloAvulso(Boolean indTituloAvulso) {
		this.indTituloAvulso = indTituloAvulso;
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

	public ScoGrupoServico getGrupoServico() {
		return grupoServico;
	}

	public void setGrupoServico(ScoGrupoServico grupoServico) {
		this.grupoServico = grupoServico;
	}

	public DominioSimNao[] getSituacaoIndEngenharia() {
		return situacaoIndEngenharia;
	}

	public void setSituacaoIndEngenharia(DominioSimNao[] situacaoIndEngenharia) {
		this.situacaoIndEngenharia = situacaoIndEngenharia;
	}

	public boolean isExibirBotaoAdicionar() {
		return exibirBotaoAdicionar;
	}

	public void setExibirBotaoAdicionar(boolean exibirBotaoAdicionar) {
		this.exibirBotaoAdicionar = exibirBotaoAdicionar;
	}

	public DominioSimNao getEngenharia() {
		return engenharia;
	}

	public void setEngenharia(DominioSimNao engenharia) {
		this.engenharia = engenharia;
	}

	public Boolean getIndEngenharia() {
		return indEngenharia;
	}

	public void setIndEngenharia(Boolean indEngenharia) {
		this.indEngenharia = indEngenharia;
	}

	public Integer getCodigoExcluir() {
		return codigoExcluir;
	}

	public void setCodigoExcluir(Integer codigoExcluir) {
		this.codigoExcluir = codigoExcluir;
	}

	public DynamicDataModel<ScoGrupoServico> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<ScoGrupoServico> dataModel) {
		this.dataModel = dataModel;
	}
	
	
}