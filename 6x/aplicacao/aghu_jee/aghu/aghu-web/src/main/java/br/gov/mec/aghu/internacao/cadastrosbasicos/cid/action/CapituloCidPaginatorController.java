package br.gov.mec.aghu.internacao.cadastrosbasicos.cid.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.internacao.cadastrosbasicos.cid.business.ICidFacade;
import br.gov.mec.aghu.model.AghCapitulosCid;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class CapituloCidPaginatorController extends ActionController implements
		ActionPaginator {

	private static final long serialVersionUID = -5047541043861271859L;
	private static final String REDIRECT_MANTER_CAPITULOCID = "capituloCidCRUD";

	@EJB
	private ICidFacade cidFacade;

	/*
	 * Model que contem os dados para pesquisa
	 */
	private AghCapitulosCid capituloCid;

	/*
	 * Model com o elemento selecionado para edição/exclusão
	 */
	private AghCapitulosCid capituloCidSelecionado;
	private Integer aghCapituloCidSeq;
	@Inject @Paginator
	private DynamicDataModel<AghCapitulosCid> dataModel;

	@PostConstruct
	protected void inicializar() {
		capituloCid = new AghCapitulosCid();
		this.begin(conversation);
	}

	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}

	@Override
	public Long recuperarCount() {
		return cidFacade.obterCapituloCidCount(capituloCid.getNumero(),
				capituloCid.getDescricao(),
				capituloCid.getIndExigeCidSecundario(),
				capituloCid.getIndSituacao());
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AghCapitulosCid> recuperarListaPaginada(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {

		List<AghCapitulosCid> result = cidFacade.pesquisar(firstResult,
				maxResult, capituloCid.getNumero(), capituloCid.getDescricao(),
				capituloCid.getIndExigeCidSecundario(),
				capituloCid.getIndSituacao());

		return result == null ? new ArrayList<AghCapitulosCid>() : result;
	}

	/**
	 * Método que realiza a ação do botão excluir na tela de Pesquisar Capítulos
	 * CID
	 */
	public void excluir() {

		try {
			if (capituloCidSelecionado != null) {
				this.cidFacade.removerCapituloCid(capituloCidSelecionado
						.getSeq());
				apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_SUCESSO_REMOCAO_CAPITULO_CID",
						this.capituloCid.getDescricao());
			} else {
				apresentarMsgNegocio(Severity.INFO,
						"ERRO_REMOVER_CAPITULO_CID",
						this.capituloCid.getDescricao());
			}
			this.aghCapituloCidSeq = null;

		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}

		this.dataModel.reiniciarPaginator();
	}

	/**
	 * Método que realiza a ação de limpar na tela de Pesquisar Capitulos CID
	 */
	public void limparCampos() {
		// ajustar campos
		capituloCid.setNumero(null);
		capituloCid.setDescricao("");
		capituloCid.setIndExigeCidSecundario(null);
		capituloCid.setIndSituacao(null);

		this.dataModel.limparPesquisa();
		this.dataModel.setPesquisaAtiva(false);
	}

	/**
	 * Redireciona para tela de inclusão de novo capítulo cid. Executado ao
	 * cliar no botão "Novo" da tela de pesquisa
	 * 
	 * @return Nome da tela a ser redirecionada
	 */
	public String redirecionaIncluirCapituloCid() {
		return REDIRECT_MANTER_CAPITULOCID;
	}

	/**
	 * Redireciona para a tela de edição de um capítulo CID. Execuado ao clicar
	 * na action editar da tabela de pesquisa
	 * 
	 * @return Nome da tela a ser redirecionada
	 */
	public String prepararEdicao() {
		return REDIRECT_MANTER_CAPITULOCID;
	}

	// SET's e GET's
	public AghCapitulosCid getCapituloCid() {
		return capituloCid;
	}

	public void setCapituloCid(AghCapitulosCid capitulosCid) {
		this.capituloCid = capitulosCid;
	}

	public Integer getAghCapituloCidSeq() {
		return aghCapituloCidSeq;
	}

	public void setAghCapituloCidSeq(Integer aghCapituloCidSeq) {
		this.aghCapituloCidSeq = aghCapituloCidSeq;
	}

	public DynamicDataModel<AghCapitulosCid> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AghCapitulosCid> dataModel) {
		this.dataModel = dataModel;
	}

	public AghCapitulosCid getCapituloCidSelecionado() {
		return capituloCidSelecionado;
	}

	public void setCapituloCidSelecionado(AghCapitulosCid capituloCidSelecionado) {
		this.capituloCidSelecionado = capituloCidSelecionado;
	}
}