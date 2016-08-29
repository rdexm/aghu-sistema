package br.gov.mec.aghu.compras.pac.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.compras.autfornecimento.business.IAutFornecimentoFacade;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.pac.business.IPacFacade;
import br.gov.mec.aghu.dominio.DominioTipoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoItemLicitacao;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.suprimentos.vo.ScoCondicaoPgtoLicitacaoVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.action.SecurityController;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;

public class CondicoesPagamentoPaginatorController extends ActionController implements ActionPaginator {

	@Inject @Paginator
	private DynamicDataModel<ScoCondicaoPgtoLicitacaoVO> dataModel;


	private static final long serialVersionUID = -29478088645050019L;
	
	private static final String PAGE_CONDICOES_PAGAMENTO_CRUD = "condicoesPagamentoPacCRUD";
	private static final String PAGE_MANTER_CONDICOES_PAGAMENTO_CRUD = "manterCondicoesPagamentoPac";

	@EJB
	private IPacFacade pacFacade;

	@EJB
	private IAutFornecimentoFacade autFornecimentoFacade;
	
	@EJB
	private IComprasFacade comprasFacade;
	
	
	@Inject
	private SecurityController securityController;

	private String urlRetorno;
	private Short numeroItemPac;
	private boolean mostraAdvertenciaExclusao;
	private Integer seqCondicaoPgtoExcluida;
	private boolean realizarPesquisa = true;
	private boolean permiteNovaCondicaoPgto;

	// Dados PAC
	private ScoLicitacao pac;
	private Integer numeroPac;
	private String modalidade;
	private Integer artigo;
	private String inciso;
	private Date dataGeracao;
	private String descricaoPAC;
	private BigDecimal valorTotal;

	// Dados Item
	private String solicitacao;
	private Integer quantidade;
	private BigDecimal vlUnitarioPrevisto;
	private String unidade;
	private String nomeItem;
	private String descricaoItem;

	private List<ScoCondicaoPgtoLicitacaoVO> listaCondicoesPgtoLicitacaoVO;
	
	private ScoCondicaoPgtoLicitacaoVO itemSelecionado;

	public void iniciar() {
	 

	 

	 

	 

		
		dataModel.setUserEditPermission(securityController.usuarioTemPermissao("cadastrarPAC", "gravar"));
		dataModel.setUserRemovePermission(securityController.usuarioTemPermissao("cadastrarPAC", "gravar"));
		
		buscaPac();
		buscaItemPac();

		if (realizarPesquisa) {
			pesquisar();
			realizarPesquisa = false;
		}
	
	}
	
	
	/**
	 * Ação executada na página: pesquisaCondicoesPagamentoPac
	 * @return
	 */
	public String redirecionaEditar(){
		return PAGE_MANTER_CONDICOES_PAGAMENTO_CRUD;
	}
	
	/**
	 * Ação executada na página: pesquisaCondicoesPagamentoPac
	 * @return
	 */
	public String redirecionaVisualizar(){
		return PAGE_MANTER_CONDICOES_PAGAMENTO_CRUD;
	}
	
	/**
	 * Ação executada na página: condicoesPagamentoPacList
	 * @return
	 */
	public String editar(){
		return PAGE_CONDICOES_PAGAMENTO_CRUD;
	}
	
	/**
	 * Ação executada na página: condicoesPagamentoPacList
	 * @return
	 */
	public String visualizar(){
		return PAGE_CONDICOES_PAGAMENTO_CRUD;
	}

	public String voltar() {
		return urlRetorno;
	}

	public String novaCondicaoPagamento() {
		if (isPermiteNovaCondicaoPgto()) {
			realizarPesquisa = true;
			return PAGE_CONDICOES_PAGAMENTO_CRUD;
		} else {
			return null;
		}
	}

	private void buscaPac() {
		pac = pacFacade.obterLicitacao(this.numeroPac);

		modalidade = pac.getModalidadeLicitacao().getCodigo().toString() + " - " + pac.getModalidadeLicitacao().getDescricao();
		artigo = pac.getArtigoLicitacao();
		inciso = pac.getIncisoArtigoLicitacao();
		dataGeracao = pac.getDtDigitacao();
		descricaoPAC = pac.getDescricao();
	}

	private void buscaItemPac() {
		if (numeroItemPac != null) {
			ScoItemLicitacao itemPac = pacFacade.obterItemLicitacaoPorNumeroLicitacaoENumeroItem(this.numeroPac,
					this.numeroItemPac);

			ScoFaseSolicitacao fase = this.comprasFacade.pesquisarFasePorItemLicitacao(this.numeroPac,	this.numeroItemPac).iterator().next();
			//ScoFaseSolicitacao fase = itemPac.getFasesSolicitacao()
			DominioTipoFaseSolicitacao tipoSolicitacao = fase.getTipo();
			List<ScoFaseSolicitacao> fases = new ArrayList<ScoFaseSolicitacao>();
			fases.add(fase);
			solicitacao = autFornecimentoFacade.obterNumeroSolicitacao(fases).toString();
			solicitacao = solicitacao + "/" + tipoSolicitacao.getDescricao();

			quantidade = obterQuantidadeMaterialServico(itemPac);
			vlUnitarioPrevisto = itemPac.getValorUnitario();
			unidade = obterUnidadeMaterial(itemPac);
			nomeItem = getNomeMaterialTruncado(this.obterNomeMaterialServico(itemPac), 40);
			descricaoItem = pacFacade.obterDescricaoMaterialServico(itemPac);

			valorTotal = pacFacade.obterValorTotalItemPac(this.numeroPac, this.numeroItemPac);
		} else {
			valorTotal = pacFacade.obterValorTotalPorNumeroLicitacao(this.numeroPac);
		}
	}

	public void excluirCondicaoPgto() {
		pacFacade.excluirCondicaoPgto(itemSelecionado.getCondicaoPgtoLicitacao().getSeq());

		pesquisar();
		setMostraAdvertenciaExclusao(false);
	}

	public Integer obterQuantidadeMaterialServico(ScoItemLicitacao item) {
		return pacFacade.obterQuantidadeMaterialServico(item);
	}

	public String obterUnidadeMaterial(ScoItemLicitacao item) {
		return pacFacade.obterUnidadeMaterial(item);
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

	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}

	@Override
	public Long recuperarCount() {
		Long numeroCondicoes = pacFacade.obterCondicaoPgtoPacCount(numeroPac, numeroItemPac);
		return numeroCondicoes;
	}

	@Override
	public List<ScoCondicaoPgtoLicitacaoVO> recuperarListaPaginada(Integer firstResult, Integer maxResult, String order,
			boolean asc) {
		listaCondicoesPgtoLicitacaoVO = pacFacade.obterCondicaoPgtoPac(numeroPac, numeroItemPac, firstResult, maxResult, order,
				asc);

		return listaCondicoesPgtoLicitacaoVO;
	}	

	public String getUrlRetorno() {
		return urlRetorno;
	}

	public void setUrlRetorno(String urlRetorno) {
		this.urlRetorno = urlRetorno;
	}

	public Short getNumeroItemPac() {
		return numeroItemPac;
	}

	public void setNumeroItemPac(Short numeroItemPac) {
		this.numeroItemPac = numeroItemPac;
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

	public Integer getArtigo() {
		return artigo;
	}

	public void setArtigo(Integer artigo) {
		this.artigo = artigo;
	}

	public String getInciso() {
		return inciso;
	}

	public void setInciso(String inciso) {
		this.inciso = inciso;
	}

	public Date getDataGeracao() {
		return dataGeracao;
	}

	public void setDataGeracao(Date dataGeracao) {
		this.dataGeracao = dataGeracao;
	}

	public String getDescricaoPAC() {
		return descricaoPAC;
	}

	public void setDescricaoPAC(String descricaoPAC) {
		this.descricaoPAC = descricaoPAC;
	}

	public BigDecimal getValorTotal() {
		return valorTotal;
	}

	public void setValorTotal(BigDecimal valorTotal) {
		this.valorTotal = valorTotal;
	}

	public String getSolicitacao() {
		return solicitacao;
	}

	public void setSolicitacao(String solicitacao) {
		this.solicitacao = solicitacao;
	}

	public Integer getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}

	public BigDecimal getVlUnitarioPrevisto() {
		return vlUnitarioPrevisto;
	}

	public void setVlUnitarioPrevisto(BigDecimal vlUnitarioPrevisto) {
		this.vlUnitarioPrevisto = vlUnitarioPrevisto;
	}

	public String getUnidade() {
		return unidade;
	}

	public void setUnidade(String unidade) {
		this.unidade = unidade;
	}

	public String getNomeItem() {
		return nomeItem;
	}

	public void setNomeItem(String nomeItem) {
		this.nomeItem = nomeItem;
	}

	public String getDescricaoItem() {
		return descricaoItem;
	}

	public void setDescricaoItem(String descricaoItem) {
		this.descricaoItem = descricaoItem;
	}

	public List<ScoCondicaoPgtoLicitacaoVO> getListaCondicoesPgtoLicitacaoVO() {
		return listaCondicoesPgtoLicitacaoVO;
	}

	public void setListaCondicoesPgtoLicitacaoVO(List<ScoCondicaoPgtoLicitacaoVO> listaCondicoesPgtoLicitacaoVO) {
		this.listaCondicoesPgtoLicitacaoVO = listaCondicoesPgtoLicitacaoVO;
	}

	public boolean isMostraAdvertenciaExclusao() {
		return mostraAdvertenciaExclusao;
	}

	public void setMostraAdvertenciaExclusao(boolean mostraAdvertenciaExclusao) {
		this.mostraAdvertenciaExclusao = mostraAdvertenciaExclusao;
	}

	public Integer getSeqCondicaoPgtoExcluida() {
		return seqCondicaoPgtoExcluida;
	}

	public void setSeqCondicaoPgtoExcluida(Integer seqCondicaoPgtoExcluida) {
		this.seqCondicaoPgtoExcluida = seqCondicaoPgtoExcluida;
	}

	public boolean isRealizarPesquisa() {
		return realizarPesquisa;
	}

	public void setRealizarPesquisa(boolean realizarPesquisa) {
		this.realizarPesquisa = realizarPesquisa;
	}

	public boolean isPermiteNovaCondicaoPgto() {
		permiteNovaCondicaoPgto = pacFacade.permitirNovaCondicaoPgto(numeroPac, numeroItemPac);
		return permiteNovaCondicaoPgto;
	}

	public void setPermiteNovaCondicaoPgto(boolean permiteNovaCondicaoPgto) {
		this.permiteNovaCondicaoPgto = permiteNovaCondicaoPgto;
	}

	public DynamicDataModel<ScoCondicaoPgtoLicitacaoVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<ScoCondicaoPgtoLicitacaoVO> dataModel) {
		this.dataModel = dataModel;
	}

	public ScoCondicaoPgtoLicitacaoVO getItemSelecionado() {
		return itemSelecionado;
	}

	public void setItemSelecionado(ScoCondicaoPgtoLicitacaoVO itemSelecionado) {
		this.itemSelecionado = itemSelecionado;
	}
}
