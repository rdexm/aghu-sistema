package br.gov.mec.aghu.controleinfeccao.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.controleinfeccao.business.IControleInfeccaoFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MciTipoGrupoProcedRisco;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class GrupoProcedRiscoPaginatorController extends ActionController implements ActionPaginator {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3840201884177563828L;

	private static final String PAGINA_TIPO_GRUPO_CRUD = "grupoProcedRiscoCRUD";

	// Filtros
	private String descricao;
	private DominioSituacao situacao;
	private Short codigo;

	private MciTipoGrupoProcedRisco itemSelecionado;
	@EJB
	private IControleInfeccaoFacade controleInfeccaoFacade;

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@Inject @Paginator
	private DynamicDataModel<MciTipoGrupoProcedRisco> dataModel;

	private List<MciTipoGrupoProcedRisco> itens = new ArrayList<MciTipoGrupoProcedRisco>();

	public String criarTipoGrupo() {
		return PAGINA_TIPO_GRUPO_CRUD;
	}

	public void deletar() {
		try {
			dataModel.reiniciarPaginator();
			controleInfeccaoFacade.deletarMciTipoGrupoProcedRisco(itemSelecionado, servidorLogadoFacade.obterServidorLogado());
			apresentarMsgNegocio(Severity.INFO, "AGPR_MENSAGEM_SUCESSO_EXCLUSAO", itemSelecionado.getDescricao());
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public String editar() {
		return PAGINA_TIPO_GRUPO_CRUD;
	}

	public Short getCodigo() {
		return codigo;
	}

	public DynamicDataModel<MciTipoGrupoProcedRisco> getDataModel() {
		return dataModel;
	}

	public String getDescricao() {
		return descricao;
	}

	public MciTipoGrupoProcedRisco getItemSelecionado() {
		return itemSelecionado;
	}

	public List<MciTipoGrupoProcedRisco> getItens() {
		return itens;
	}

	public DominioSituacao getSituacao() {
		return situacao;
	}
	@PostConstruct
	public void init(){
		this.begin(conversation);
	}
	public void limpar() {
		descricao = null;
		situacao = null;
		codigo = null;
		dataModel.limparPesquisa();
		dataModel.setPesquisaAtiva(false);
	}
	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}

	@Override
	public Long recuperarCount() {
		return  controleInfeccaoFacade.pesquisarMciTipoGrupoProcedRiscoCount(codigo, descricao, situacao);
	}
	@Override
	public List<MciTipoGrupoProcedRisco> recuperarListaPaginada(
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc) {
		itens = controleInfeccaoFacade.pesquisarMciTipoGrupoProcedRisco(
				firstResult, maxResults, orderProperty, asc, codigo, descricao,
				situacao);
		return  itens;
	}
	public void setCodigo(Short codigo) {
		this.codigo = codigo;
	}
	public void setDataModel(DynamicDataModel<MciTipoGrupoProcedRisco> dataModel) {
		this.dataModel = dataModel;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public void setItemSelecionado(MciTipoGrupoProcedRisco itemSelecionado) {
		this.itemSelecionado = itemSelecionado;
	}

	public void setItens(List<MciTipoGrupoProcedRisco> itens) {
		this.itens = itens;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

}
