package br.gov.mec.aghu.internacao.cadastrosbasicos.cid.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.internacao.cadastrosbasicos.cid.business.ICidFacade;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.AghGrupoCids;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Classe responsável por controlar as ações da listagem de Cids
 */
public class CidPaginatorController extends ActionController implements ActionPaginator {

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	@Inject @Paginator
	private DynamicDataModel<AghCid> dataModel;

	private static final long serialVersionUID = -3721868414995610922L;

	private static final String PAGE_CID_CRUD = "cidCRUD";

	@EJB
	private ICidFacade cidFacade;

	@Inject
	private CidController cidController;

	private AghCid cidO = new AghCid();


	/**
	 * Atributo referente aos campos de filtro
	 */
	private Integer cidSeq;
	private String codigoPesquisa;
	private String descricaoPesquisa;
	private DominioSituacao situacaoPesquisa;
	private AghGrupoCids grupoCidPesquisa;
	private AghCid categoriaPesquisa;

	// usado para exclusão de registro selecionado
	private Integer codigo;

	private String cidCodigo;

	public void pesquisar() {
		getDataModel().reiniciarPaginator();
	}

	public void limparPesquisa() {
		this.codigoPesquisa = null;
		this.descricaoPesquisa = null;
		this.situacaoPesquisa = null;
		getDataModel().limparPesquisa();
	}

	// ### Paginação ###

	@Override
	public Long recuperarCount() {
		return cidFacade.pesquisaCount(this.cidSeq, this.codigoPesquisa, this.descricaoPesquisa, this.situacaoPesquisa);
	}

	@Override
	public List<AghCid> recuperarListaPaginada(Integer firstResult, Integer maxResults, String orderProperty, boolean asc) {

		List<AghCid> result = this.cidFacade.pesquisa(firstResult, maxResults, AghCid.Fields.CODIGO.toString(), true, this.cidSeq, this.codigoPesquisa,
				descricaoPesquisa, situacaoPesquisa);

		if (result == null) {
			result = new ArrayList<AghCid>();
		}

		return result;
	}

	/**
	 * Método que realiza a ação do botão excluir na tela de Pesquisa de Manter
	 * Cids.
	 */
	public void confirmarExclusao() {
		getDataModel().reiniciarPaginator();
		try {
			if (cidO != null) {
				this.cidFacade.excluirCid(cidO);
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUSAO_CID", this.cidO.getDescricao());
			}
			this.cidSeq = null;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public String iniciarInclusao() {
		cidController.setNovo(true);
		cidController.setCidCodigo("");
		cidController.inicio();

		return PAGE_CID_CRUD;
	}

	public String editar() {
		cidController.setNovo(false);
		cidController.setCidCodigo(cidCodigo);
		cidController.inicio();

		return PAGE_CID_CRUD;
	}

	// #### SuggestionBoxes ######

	public List<AghGrupoCids> pesquisaGrupoCidSB(Object strPesquisa) {
		return cidFacade.listarPorSigla(strPesquisa);
	}

	// ### GETs e SETs ###
	public String getCodigoPesquisa() {
		return codigoPesquisa;
	}

	public void setCodigoPesquisa(String codigoPesquisa) {
		this.codigoPesquisa = codigoPesquisa;
	}

	public String getDescricaoPesquisa() {
		return descricaoPesquisa;
	}

	public void setDescricaoPesquisa(String descricaoPesquisa) {
		this.descricaoPesquisa = descricaoPesquisa;
	}

	public DominioSituacao getSituacaoPesquisa() {
		return situacaoPesquisa;
	}

	public void setSituacaoPesquisa(DominioSituacao situacaoPesquisa) {
		this.situacaoPesquisa = situacaoPesquisa;
	}

	public AghCid getCidO() {
		return cidO;
	}

	public void setCidO(AghCid cid) {
		this.cidO = cid;
	}

	public AghGrupoCids getGrupoCidPesquisa() {
		return grupoCidPesquisa;
	}

	public void setGrupoCidPesquisa(AghGrupoCids grupoCidPesquisa) {
		this.grupoCidPesquisa = grupoCidPesquisa;
	}

	public AghCid getCategoriaPesquisa() {
		return categoriaPesquisa;
	}

	public void setCategoriaPesquisa(AghCid categoriaPesquisa) {
		this.categoriaPesquisa = categoriaPesquisa;
	}

	public void setCidSeq(Integer cidSeq) {
		this.cidSeq = cidSeq;
	}

	public Integer getCidSeq() {
		return cidSeq;
	}

	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	public DynamicDataModel<AghCid> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AghCid> dataModel) {
		this.dataModel = dataModel;
	}

	public String getCidCodigo() {
		return cidCodigo;
	}

	public void setCidCodigo(String cidCodigo) {
		this.cidCodigo = cidCodigo;
	}

}
