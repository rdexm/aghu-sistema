package br.gov.mec.aghu.compras.solicitacaocompras.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.solicitacaocompra.business.IPlanejamentoFacade;
import br.gov.mec.aghu.compras.solicitacaocompra.business.ISolicitacaoComprasFacade;
import br.gov.mec.aghu.dominio.DominioTipoMaterial;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoLoteReposicao;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;


public class LoteReposicaoPaginatorController extends ActionController implements ActionPaginator {

	private static final String REPOSICAO_MATERIAL = "reposicaoMaterial";

	@Inject @Paginator
	private DynamicDataModel<ScoLoteReposicao> dataModel;

	private static final long serialVersionUID = 6311943654533755942L;

	@EJB
	private ISolicitacaoComprasFacade solicitacaoComprasFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IComprasFacade comprasFacade;

	@EJB
	private IPlanejamentoFacade planejamentoFacade;

	// filtros
	private ScoLoteReposicao loteReposicao;
	private ScoGrupoMaterial grupoMaterial;
	private Date dataInicioGeracao;
	private Date dataFimGeracao;
	private RapServidores servidorGeracao;
	private ScoMaterial material;
	private String nomeLote;
	private DominioTipoMaterial tipoMaterial;
	private String voltarParaUrl;
	private Integer seqLote;
	
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}

	
	public void iniciar(){
	 

	 

		this.limpar();
		//this.//setIgnoreInitPageConfig(true);
	
	}
	
	
	// métodos das suggestions
	
	public List<ScoGrupoMaterial> pesquisarGrupoMaterialPorCodigoDescricao(String parametro) {
		return this.comprasFacade.pesquisarGrupoMaterialPorFiltro(parametro);
	}

	public List<ScoMaterial> listarMateriais(String param) throws BaseException {
		return this.returnSGWithCount(this.comprasFacade.listarScoMateriais(param, null, true),listarMateriaisCount(param));
	}
	
	public Long listarMateriaisCount(String param)	{
		return this.solicitacaoComprasFacade.listarMateriaisAtivosCount(param, this.obterLoginUsuarioLogado());
	}
	
	public List<RapServidores> obterServidor(String objPesquisa) {
		try {
			return this.registroColaboradorFacade.pesquisarServidoresVinculados(objPesquisa);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return new ArrayList<RapServidores>();
	}

	public List<ScoLoteReposicao> pesquisarLoteReposicaoPorCodigoDescricao(String param) {
		return this.planejamentoFacade.pesquisarLoteReposicaoPorCodigoDescricao(param);
	}
	
	// paginator
	
	@Override
	public Long recuperarCount() {
		return this.planejamentoFacade.pesquisarLoteReposicaoCount(loteReposicao, grupoMaterial, dataInicioGeracao, dataFimGeracao, servidorGeracao, material, nomeLote, tipoMaterial);
	}

	@Override
	public List<ScoLoteReposicao> recuperarListaPaginada(Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc) {
		return this.planejamentoFacade
				.pesquisarLoteReposicao(firstResult, maxResults, orderProperty, asc, loteReposicao, grupoMaterial, dataInicioGeracao, dataFimGeracao, servidorGeracao, material, nomeLote, tipoMaterial);
	}
	
	public void limpar() {
		this.tipoMaterial = null;
		this.loteReposicao = null;
		this.grupoMaterial = null;
		this.dataInicioGeracao = null;
		this.dataFimGeracao = null;
		this.servidorGeracao = null;
		this.material = null;
		this.setAtivo(false);
	}

	public void pesquisar() {
		this.reiniciarPaginator();
	}
	
	public void reiniciarPaginator(){
		this.dataModel.reiniciarPaginator();
	}
	
	public String voltar(){
		return voltarParaUrl;
	}
	
	public String redirecionarReposicaoMaterial(){
		return REPOSICAO_MATERIAL;
	}
	
	public Long obterQtdSc(ScoLoteReposicao item) {
		return this.planejamentoFacade.obterQtdScGerada(item);
	}
	
	public ScoLoteReposicao getLoteReposicao() {
		return loteReposicao;
	}

	public void setLoteReposicao(ScoLoteReposicao loteReposicao) {
		this.loteReposicao = loteReposicao;
	}

	public ScoGrupoMaterial getGrupoMaterial() {
		return grupoMaterial;
	}

	public void setGrupoMaterial(ScoGrupoMaterial grupoMaterial) {
		this.grupoMaterial = grupoMaterial;
	}

	public Date getDataInicioGeracao() {
		return dataInicioGeracao;
	}

	public void setDataInicioGeracao(Date dataInicioGeracao) {
		this.dataInicioGeracao = dataInicioGeracao;
	}

	public Date getDataFimGeracao() {
		return dataFimGeracao;
	}

	public void setDataFimGeracao(Date dataFimGeracao) {
		this.dataFimGeracao = dataFimGeracao;
	}

	public RapServidores getServidorGeracao() {
		return servidorGeracao;
	}

	public void setServidorGeracao(RapServidores servidorGeracao) {
		this.servidorGeracao = servidorGeracao;
	}

	public ScoMaterial getMaterial() {
		return material;
	}

	public void setMaterial(ScoMaterial material) {
		this.material = material;
	}

	public String getNomeLote() {
		return nomeLote;
	}

	public void setNomeLote(String nomeLote) {
		this.nomeLote = nomeLote;
	}

	public DominioTipoMaterial getTipoMaterial() {
		return tipoMaterial;
	}

	public void setTipoMaterial(DominioTipoMaterial tipoMaterial) {
		this.tipoMaterial = tipoMaterial;
	}

	public Integer getSeqLote() {
		return seqLote;
	}

	public void setSeqLote(Integer seqLote) {
		this.seqLote = seqLote;
	}

	public String getVoltarParaUrl() {
		return voltarParaUrl;
	}

	public void setVoltarParaUrl(String voltarParaUrl) {
		this.voltarParaUrl = voltarParaUrl;
	}		 
	public DynamicDataModel<ScoLoteReposicao> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<ScoLoteReposicao> dataModel) {
		this.dataModel = dataModel;
	}
	
	public boolean isAtivo(){
		return this.dataModel.getPesquisaAtiva();
	}
	
	public void setAtivo(boolean ativo){
		this.dataModel.setPesquisaAtiva(ativo);
	}
}
