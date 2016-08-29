package br.gov.mec.aghu.estoque.pesquisa.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.vo.SociosFornecedoresVO;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoSocios;
import br.gov.mec.aghu.model.ScoSociosFornecedores;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;


public class PesquisarSociosFornecedoresController extends ActionController implements ActionPaginator {
	
	private static final String CADASTRAR_SOCIOS_FORNECEDORES = "estoque-cadastrarSociosFornecedores";

	@Inject @Paginator
	private DynamicDataModel<SociosFornecedoresVO> dataModel;

	private static final long serialVersionUID = 12393224457578765L;

	private static final String MANTER_CADASTRO_FORNECEDOR = "compras-manterCadastroFornecedor";

	@EJB
	protected IComprasFacade comprasFacade;
	
	@EJB
	protected IEstoqueFacade estoqueFacade;
	
	private Integer filtroCodigo;
	private String filtroNomeSocio;
	private String filtroRG;
	private Long filtroCPF;
	private ScoFornecedor filtroFornecedor;
	private SociosFornecedoresVO exclusaoSocio;
	private List<ScoFornecedor> listaScoFornecedores = new ArrayList<ScoFornecedor>();
	private Boolean modoEdicao;
	private Integer seqSocio;
	private Integer numeroFornecedor;
	private boolean desabilitaConsultaFornecedor;
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void inicio(){
	 

		this.modoEdicao = Boolean.FALSE;
		if(numeroFornecedor != null){
			this.filtroFornecedor = comprasFacade.buscarFornecedorPorNumero(numeroFornecedor);
			this.desabilitaConsultaFornecedor = true;
			this.pesquisar();
		}
	
	}
	
	public void pesquisar(){
		this.setAtivo(Boolean.TRUE);
		reiniciarPaginator();
	}
	
	public void reiniciarPaginator(){
		this.dataModel.reiniciarPaginator();
	}
	
	public void limparPesquisa(){
		this.setAtivo(Boolean.FALSE);
		this.limpaFiltros();
	}
	
	public void limpaFiltros(){
		this.filtroCodigo = null;
		this.filtroNomeSocio = null;
		this.filtroRG = null;
		this.filtroCPF = null;
		this.filtroFornecedor = null;
		this.desabilitaConsultaFornecedor = false;
	}
	
	public String novo(){
		this.modoEdicao = Boolean.FALSE;
		return CADASTRAR_SOCIOS_FORNECEDORES;
	}
	
	public String editar(Integer seq){
		this.modoEdicao = Boolean.TRUE;
		this.seqSocio = seq;
		return CADASTRAR_SOCIOS_FORNECEDORES;
	}
	
	public void excluir() {
		if (exclusaoSocio != null) {
			List<ScoSociosFornecedores> listaSociosFornecedores = this.comprasFacade.listarFornecedoresPorSeqSocio(exclusaoSocio.getSeq());
			for (ScoSociosFornecedores scoSociosFornecedores : listaSociosFornecedores) {
				this.estoqueFacade.removerScoSociosFornecedores(scoSociosFornecedores);
			}
			ScoSocios scoSocios = comprasFacade.buscarSocioPorSeq(exclusaoSocio.getSeq());
			this.estoqueFacade.removerSocios(scoSocios);
			pesquisar();
		}
	}

	public void listarFornecedores(Integer seqSocio){
		List<ScoSociosFornecedores> listaSociosFornecedores = this.comprasFacade.listarFornecedoresPorSeqSocio(seqSocio);
		
		if(listaSociosFornecedores != null && !listaSociosFornecedores.isEmpty()){
			listaScoFornecedores = new ArrayList<ScoFornecedor>();
			for (ScoSociosFornecedores socioFornecedor : listaSociosFornecedores) {
				listaScoFornecedores.add(socioFornecedor.getFornecedor());
			}
		}
	}
	
	public String voltar(){
		return MANTER_CADASTRO_FORNECEDOR;
	}
	
	@Override
	public List<SociosFornecedoresVO> recuperarListaPaginada(Integer firstResults, Integer maxResults, String orderProperty, boolean asc) {
		Integer numeroFornecedor = filtroFornecedor != null ? filtroFornecedor.getNumero() : null;
		
		return estoqueFacade.listarSociosFornecedores(firstResults, maxResults, orderProperty, asc, filtroCodigo, filtroNomeSocio, filtroRG, filtroCPF, numeroFornecedor);
	}
	
	@Override
	public Long recuperarCount() {
		Integer numeroFornecedor = filtroFornecedor != null ? filtroFornecedor.getNumero() : null;
		
		return estoqueFacade.listarSociosFornecedoresCount(filtroCodigo, filtroNomeSocio, filtroRG, filtroCPF, numeroFornecedor);
	}
	
	public List<ScoFornecedor> pesquisarFornecedoresPorCgcCpfRazaoSocial(String parametro) {
		return this.returnSGWithCount(this.comprasFacade.listarFornecedoresAtivos(parametro, 0, 100, null, true),pesquisarFornecedoresPorCgcCpfRazaoSocialCount(parametro));
	}
	
	public Long pesquisarFornecedoresPorCgcCpfRazaoSocialCount(String parametro) {
		return this.comprasFacade.listarFornecedoresAtivosCount(parametro);		
	}

	public Integer getFiltroCodigo() {
		return filtroCodigo;
	}

	public void setFiltroCodigo(Integer filtroCodigo) {
		this.filtroCodigo = filtroCodigo;
	}

	public String getFiltroNomeSocio() {
		return filtroNomeSocio;
	}

	public void setFiltroNomeSocio(String filtroNomeSocio) {
		this.filtroNomeSocio = filtroNomeSocio;
	}

	public String getFiltroRG() {
		return filtroRG;
	}

	public void setFiltroRG(String filtroRG) {
		this.filtroRG = filtroRG;
	}

	public Long getFiltroCPF() {
		return filtroCPF;
	}

	public void setFiltroCPF(Long filtroCPF) {
		this.filtroCPF = filtroCPF;
	}

	public ScoFornecedor getFiltroFornecedor() {
		return filtroFornecedor;
	}

	public void setFiltroFornecedor(ScoFornecedor filtroFornecedor) {
		this.filtroFornecedor = filtroFornecedor;
	}

	public void setModoEdicao(Boolean modoEdicao) {
		this.modoEdicao = modoEdicao;
	}

	public Boolean getModoEdicao() {
		return modoEdicao;
	}

	public List<ScoFornecedor> getListaScoFornecedores() {
		return listaScoFornecedores;
	}

	public void setListaScoFornecedores(List<ScoFornecedor> listaScoFornecedores) {
		this.listaScoFornecedores = listaScoFornecedores;
	}

	public void setNumeroFornecedor(Integer numeroFornecedor) {
		this.numeroFornecedor = numeroFornecedor;
	}

	public Integer getNumeroFornecedor() {
		return numeroFornecedor;
	}

	public SociosFornecedoresVO getExclusaoSocio() {
		return exclusaoSocio;
	}

	public void setExclusaoSocio(SociosFornecedoresVO exclusaoSocio) {
		this.exclusaoSocio = exclusaoSocio;
	}

	public void setSeqSocio(Integer seqSocio) {
		this.seqSocio = seqSocio;
	}

	public Integer getSeqSocio() {
		return seqSocio;
	}

	public void setDesabilitaConsultaFornecedor(boolean desabilitaConsultaFornecedor) {
		this.desabilitaConsultaFornecedor = desabilitaConsultaFornecedor;
	}

	public boolean isDesabilitaConsultaFornecedor() {
		return desabilitaConsultaFornecedor;
	}

	public DynamicDataModel<SociosFornecedoresVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<SociosFornecedoresVO> dataModel) {
		this.dataModel = dataModel;
	}
	
	public void setAtivo(boolean ativo){
		this.dataModel.setPesquisaAtiva(ativo);
	}
	
	public boolean isAtivo(){
		return this.dataModel.getPesquisaAtiva();
	}
}
