package br.gov.mec.aghu.compras.action;

import java.math.BigDecimal;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.compras.pac.business.IPacFacade;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoItemLicitacao;
import br.gov.mec.aghu.model.ScoItemPropostaFornecedor;
import br.gov.mec.aghu.model.ScoItemPropostaFornecedorId;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoModalidadeLicitacao;
import br.gov.mec.aghu.model.ScoPropostaFornecedor;
import br.gov.mec.aghu.model.ScoPropostaFornecedorId;
import br.gov.mec.aghu.suprimentos.vo.ScoCondicaoPagamentoProposVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.action.SecurityController;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class CondPagamentoPropostaPaginatorController extends ActionController implements ActionPaginator {

	@Inject @Paginator
	private DynamicDataModel<ScoCondicaoPagamentoProposVO> dataModel;

	private static final long serialVersionUID = -6918130194902588378L;

	private static final String PAGE_COND_PAGAMENTO_PROP_FORN_CRUD = "condPagamentoPropFornCRUD";

	@EJB
	private IPacFacade pacFacade;

	@EJB
	private IComprasFacade comprasFacade;

	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;

	@Inject
	private SecurityController securityController;

	private String urlRetorno;
	private Integer numeroPac;
	private Short numeroItem;
	private Short numeroItemPac;
	private Integer frnNumero;

	// nao permite fazer nada se o item da proposta ja foi escolhido
	private Boolean itemPropostaEscolhido;
	private Boolean permiteNovaCondicaoPgto;

	// Dados PAC
	private ScoLicitacao pac;
	private ScoPropostaFornecedor propostaFornecedor;
	private String modalidade;
	private String fornecedor;
	private String indExclusao;
	private String nomeItem;
	private String descricaoItem;
	private Long qtde;
	private String emb;
	private BigDecimal vlUnitario;
	private BigDecimal valorTotal;

	private Boolean mostraModalCopia;

	private ScoCondicaoPagamentoProposVO itemSelecionado;

	/** ID da condição a ser excluída. */
	private Integer condicaoSelecionadaId;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public void iniciar() {

		if (this.itemPropostaEscolhido == null) {
			this.itemPropostaEscolhido = Boolean.FALSE;
		}
		dataModel.setUserEditPermission(securityController.usuarioTemPermissao("cadastrarProposta", "gravar")
				&& !itemPropostaEscolhido);
		dataModel.setUserEditPermission(securityController.usuarioTemPermissao("cadastrarProposta", "gravar")
				&& !itemPropostaEscolhido);

		this.mostraModalCopia = Boolean.FALSE;
		buscaPropostaFornecedor();
		buscaItemProposta();
		pesquisar();
	}

	private void buscaPropostaFornecedor() {
		ScoFornecedor scoFornecedor = comprasFacade.obterScoFornecedorPorChavePrimaria(frnNumero);		
		fornecedor = FornecedorStringUtils.format(scoFornecedor);

		ScoPropostaFornecedorId idProposta = new ScoPropostaFornecedorId();
		idProposta.setFrnNumero(frnNumero);
		idProposta.setLctNumero(numeroPac);
		propostaFornecedor = pacFacade.obterPropostaFornecedor(idProposta);
		pac = propostaFornecedor.getLicitacao();
	
		if (pac.getModalidadeLicitacao() != null){
		 ScoModalidadeLicitacao scoModalidade = comprasCadastrosBasicosFacade.obterModalidadeLicitacao(pac.getModalidadeLicitacao().getCodigo());
		 
		 if(scoModalidade != null){
		   modalidade = scoModalidade.getCodigo().toString() + " - " + scoModalidade.getDescricao();
		 }		 
		}
		

		if (propostaFornecedor.getIndExclusao()) {
			indExclusao = "Sim";
		} else {
			indExclusao = "Não";
		}
	}

	private ScoItemPropostaFornecedorId obterIdItemProposta() {
		ScoItemPropostaFornecedorId id = new ScoItemPropostaFornecedorId();
		id.setNumero(numeroItem);
		id.setPfrFrnNumero(frnNumero);
		id.setPfrLctNumero(numeroPac);

		return id;
	}

	private void buscaItemProposta() {
		if (numeroItem != null) {
			ScoItemPropostaFornecedor itemProposta = comprasFacade.obterItemPropostaFornPorChavePrimaria(obterIdItemProposta());
			ScoItemLicitacao itemPac = itemProposta.getItemLicitacao();

			numeroItemPac = itemPac.getId().getNumero();
			nomeItem = getNomeMaterialTruncado(this.obterNomeMaterialServico(itemPac), 40);
			descricaoItem = pacFacade.obterDescricaoMaterialServico(itemPac);

			qtde = itemProposta.getQuantidade();
			
			if(itemProposta.getUnidadeMedida() != null) {
			   emb = this.comprasCadastrosBasicosFacade.obterUnidadeMedida(itemProposta.getUnidadeMedida().getCodigo()).getDescricao();
			}

			vlUnitario = itemProposta.getValorUnitario();

			valorTotal = pacFacade.obterValorTotalItemProposta(itemProposta);
		} else {
			valorTotal = pacFacade.obterValorTotalProposta(propostaFornecedor);
		}
	}

	public String obterNomeMaterialServico(ScoItemLicitacao item) {
		return pacFacade.obterNomeMaterialServico(item, true);
	}

	private String getNomeMaterialTruncado(String codNomeMaterial, Integer tamanhoMaximo) {
		if (codNomeMaterial.length() > tamanhoMaximo) {
			codNomeMaterial = codNomeMaterial.substring(0, tamanhoMaximo) + "...";
		}
		return codNomeMaterial;
	}

	public void copiarCondicoesLicitacao() {
		try {
			this.pacFacade.copiarCondicaoPagamentoLicitacao(this.numeroPac, this.frnNumero, this.numeroItem, this.numeroItemPac);

			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_COPIA_CONDPAG_SUCESSO");

			this.setMostraModalCopia(false);
			this.pesquisar();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public Integer obterQuantidadeMaterialServico(ScoItemLicitacao item) {
		return pacFacade.obterQuantidadeMaterialServico(item);
	}

	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}

	public String novo() {
		if (isPermiteNovaCondicaoPgto()) {
			return PAGE_COND_PAGAMENTO_PROP_FORN_CRUD;
		} else {
			return null;
		}
	}

	@Override
	public Long recuperarCount() {
		return comprasFacade.obterCondicaoPagamentoProposCount(frnNumero, numeroPac, numeroItem);
	}

	@Override
	public List<ScoCondicaoPagamentoProposVO> recuperarListaPaginada(Integer firstResult, Integer maxResult, String order,
			boolean asc) {
		return comprasFacade.obterCondicaoPagamentoPropos(frnNumero, numeroPac, numeroItem, firstResult, maxResult, order, asc);
	}

	/**
	 * Edita condição de pagamento.
	 * 
	 * @return PAGE_COND_PAGAMENTO_PROP_FORN_CRUD
	 */
	public String editar() {

		return PAGE_COND_PAGAMENTO_PROP_FORN_CRUD;
	}

	/**
	 * Exclui condição.
	 */
	public void excluir() {
		try {
			comprasFacade.excluirCondicaoPagamentoProposta(itemSelecionado.getCondicaoPgtoProposta().getNumero());

			apresentarMsgNegocio(Severity.INFO, "MESSAGE_COND_PGTO_PROP_EXCLUIDA");

			dataModel.reiniciarPaginator();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public boolean habilitarCopiarPAC() {

		boolean flagAceitaUnicaCondPagto = false;

		try {
			AghParametros parametroAceitaUnicaCondPagto = this.parametroFacade
					.buscarAghParametro(AghuParametrosEnum.P_ACEITA_UNICA_COND_PGTO);
			flagAceitaUnicaCondPagto = parametroAceitaUnicaCondPagto.getVlrTexto().equalsIgnoreCase(DominioSimNao.S.toString());
		} catch (ApplicationBusinessException e) {
			flagAceitaUnicaCondPagto = false;
		}

		return ((this.itemPropostaEscolhido != null && !this.itemPropostaEscolhido) && (!(flagAceitaUnicaCondPagto && recuperarCount() >= 1)));
	}

	/**
	 * Visualiza condição de pagamento.
	 * 
	 * @param condicaoId
	 *            ID da condição.
	 * @return "visualizar"
	 */
	public String visualizar() {
		return PAGE_COND_PAGAMENTO_PROP_FORN_CRUD;
	}

	public String voltar() {
		return urlRetorno;
	}

	public String getUrlRetorno() {
		return urlRetorno;
	}

	public void setUrlRetorno(String urlRetorno) {
		this.urlRetorno = urlRetorno;
	}

	public Short getNumeroItem() {
		return numeroItem;
	}

	public void setNumeroItem(Short numeroItem) {
		this.numeroItem = numeroItem;
	}

	public Short getNumeroItemPac() {
		return numeroItemPac;
	}

	public void setNumeroItemPac(Short numeroItemPac) {
		this.numeroItemPac = numeroItemPac;
	}

	public Integer getFrnNumero() {
		return frnNumero;
	}

	public void setFrnNumero(Integer frnNumero) {
		this.frnNumero = frnNumero;
	}

	public ScoLicitacao getPac() {
		return pac;
	}

	public void setPac(ScoLicitacao pac) {
		this.pac = pac;
	}

	public Integer getNumeroPac() {
		return numeroPac;
	}

	public void setNumeroPac(Integer numeroPac) {
		this.numeroPac = numeroPac;
	}

	public String getModalidade() {
		return modalidade;
	}

	public void setModalidade(String modalidade) {
		this.modalidade = modalidade;
	}

	public String getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(String fornecedor) {
		this.fornecedor = fornecedor;
	}

	public String getIndExclusao() {
		return indExclusao;
	}

	public void setIndExclusao(String indExclusao) {
		this.indExclusao = indExclusao;
	}

	public String getNomeItem() {
		return nomeItem;
	}

	public ScoPropostaFornecedor getPropostaFornecedor() {
		return propostaFornecedor;
	}

	public void setPropostaFornecedor(ScoPropostaFornecedor propostaFornecedor) {
		this.propostaFornecedor = propostaFornecedor;
	}

	public String getDescricaoItem() {
		return descricaoItem;
	}

	public void setDescricaoItem(String descricaoItem) {
		this.descricaoItem = descricaoItem;
	}

	public void setNomeItem(String nomeItem) {
		this.nomeItem = nomeItem;
	}

	public Long getQtde() {
		return qtde;
	}

	public void setQtde(Long qtde) {
		this.qtde = qtde;
	}

	public String getEmb() {
		return emb;
	}

	public void setEmb(String emb) {
		this.emb = emb;
	}

	public BigDecimal getVlUnitario() {
		return vlUnitario;
	}

	public void setVlUnitario(BigDecimal vlUnitario) {
		this.vlUnitario = vlUnitario;
	}

	public BigDecimal getValorTotal() {
		return valorTotal;
	}

	public void setValorTotal(BigDecimal valorTotal) {
		this.valorTotal = valorTotal;
	}

	public Integer getCondicaoSelecionadaId() {
		return condicaoSelecionadaId;
	}

	public void setCondicaoSelecionadaId(Integer condicaoSelecionadaId) {
		this.condicaoSelecionadaId = condicaoSelecionadaId;
	}

	public Boolean getMostraModalCopia() {
		return mostraModalCopia;
	}

	public void setMostraModalCopia(Boolean mostraModalCopia) {
		this.mostraModalCopia = mostraModalCopia;
	}

	public Boolean getItemPropostaEscolhido() {
		return itemPropostaEscolhido;
	}

	public void setItemPropostaEscolhido(Boolean itemPropostaEscolhido) {
		this.itemPropostaEscolhido = itemPropostaEscolhido;
	}

	public boolean isPermiteNovaCondicaoPgto() {
		permiteNovaCondicaoPgto = comprasFacade.permitirNovaCondicaoPgtoProposta(frnNumero, numeroPac, numeroItem);
		return permiteNovaCondicaoPgto;
	}

	public void setPermiteNovaCondicaoPgto(boolean permiteNovaCondicaoPgto) {
		this.permiteNovaCondicaoPgto = permiteNovaCondicaoPgto;
	}

	public DynamicDataModel<ScoCondicaoPagamentoProposVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<ScoCondicaoPagamentoProposVO> dataModel) {
		this.dataModel = dataModel;
	}

	public ScoCondicaoPagamentoProposVO getItemSelecionado() {
		return itemSelecionado;
	}

	public void setItemSelecionado(ScoCondicaoPagamentoProposVO itemSelecionado) {
		this.itemSelecionado = itemSelecionado;
	}
}