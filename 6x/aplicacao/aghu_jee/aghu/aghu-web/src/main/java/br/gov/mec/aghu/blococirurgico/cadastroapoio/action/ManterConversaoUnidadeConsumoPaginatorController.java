package br.gov.mec.aghu.blococirurgico.cadastroapoio.action;

import java.math.BigDecimal;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.cadastroapoio.business.IBlocoCirurgicoCadastroApoioFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.model.SceConversaoUnidadeConsumos;
import br.gov.mec.aghu.model.SceConversaoUnidadeConsumosId;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoUnidadeMedida;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.Severity;


public class ManterConversaoUnidadeConsumoPaginatorController extends ActionController implements ActionPaginator {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	@Inject @Paginator
	private DynamicDataModel<SceConversaoUnidadeConsumos> dataModel;

	/**
	 * 
	 */
	private static final long serialVersionUID = 3288102063913966552L;

	private static final String CONVERSAO_CRUD = "manterConversaoUnidadeConsumo";
	
	@EJB
	private IBlocoCirurgicoCadastroApoioFacade blocoCirurgicoCadastroApoioFacade;
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	@EJB
	private IComprasFacade comprasFacade;
	
	@EJB
	private IEstoqueFacade estoqueFacade;
	
	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;
	
	@EJB
	private IAghuFacade aghuFacade;	
	
	// Controla exibição do botão "Novo"
	private boolean exibirBotaoNovo;
	
	private String voltarPara;
	
	private ScoMaterial material;
	private ScoUnidadeMedida scoUnidadeMedida;
	private BigDecimal fatorConversao;
	
	//exclusao
	private Integer codMaterial;
	private String umdCodigo;

	/**
	 * Chamado no inicio de "cada conversação"
	 */
	public void inicio() {
		if(this.dataModel.getPesquisaAtiva()){
			pesquisar();
		}
	}

	public void pesquisar(){
		dataModel.reiniciarPaginator();
		exibirBotaoNovo=true;
	}
	
	@Override
	public Long recuperarCount() {
		return estoqueFacade.listarConversaoUnidadeConsumoFiltroCount(material, scoUnidadeMedida, fatorConversao);
	}

	@Override
	public List<SceConversaoUnidadeConsumos> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return estoqueFacade.listarConversaoUnidadeConsumoFiltro(firstResult, maxResult, SceConversaoUnidadeConsumos.Fields.MATERIAL.toString(), true, material, scoUnidadeMedida, fatorConversao);
	}

	public void editar(SceConversaoUnidadeConsumos conversao){
		this.estoqueFacade.atualizarConversao(conversao);
		this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ALTERACAO_CONVERSAO");
	}

	public void limparPesquisa() {
		material=null;
		scoUnidadeMedida=null;
		fatorConversao=null;
		this.exibirBotaoNovo = false;
		this.dataModel.setPesquisaAtiva(false);
	}
	
	public String voltar() {
		return this.voltarPara;
	}
	
	/**
	 * Excluir
	 */
	public void excluir(SceConversaoUnidadeConsumosId conversaoId){
		estoqueFacade.excluirConversao(conversaoId);
		this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUSAO_CONVERSAO");
		dataModel.reiniciarPaginator();
	}

	// Metodo para Suggestion Box de Material
	public List<ScoMaterial>pesquisarMateriais(String param){
		return this.returnSGWithCount(this.comprasFacade.listarScoMateriaisAtivos(param),pesquisarMateriaisCount(param));
	}
	
    public Long pesquisarMateriaisCount(String param){
        return this.comprasFacade.listarScoMatriaisAtivosCount(param);
    }
	
	//Metodo para Suggestion Box de especialidades
	public List<ScoUnidadeMedida> obterUnidades(String objPesquisa) {
		return this.returnSGWithCount(this.comprasCadastrosBasicosFacade.pesquisarUnidadeMedidaPorCodigoDescricao(objPesquisa),obterUnidadesCount(objPesquisa));
	}
	
	public Long obterUnidadesCount(String objParam) {
		return this.comprasCadastrosBasicosFacade.pesquisarUnidadeMedidaPorCodigoDescricaoCount(objParam);
	}
	
	public String iniciarInclusao() {
		return CONVERSAO_CRUD;
	}
	
	/*
	 * Getters and Setters abaixo...
	 */
	public IBlocoCirurgicoCadastroApoioFacade getBlocoCirurgicoCadastroApoioFacade() {
		return blocoCirurgicoCadastroApoioFacade;
	}

	public void setBlocoCirurgicoCadastroApoioFacade(
			IBlocoCirurgicoCadastroApoioFacade blocoCirurgicoCadastroApoioFacade) {
		this.blocoCirurgicoCadastroApoioFacade = blocoCirurgicoCadastroApoioFacade;
	}

	public IBlocoCirurgicoFacade getBlocoCirurgicoFacade() {
		return blocoCirurgicoFacade;
	}

	public void setBlocoCirurgicoFacade(IBlocoCirurgicoFacade blocoCirurgicoFacade) {
		this.blocoCirurgicoFacade = blocoCirurgicoFacade;
	}

	public IAghuFacade getAghuFacade() {
		return aghuFacade;
	}

	public void setAghuFacade(IAghuFacade aghuFacade) {
		this.aghuFacade = aghuFacade;
	}

	public IComprasFacade getComprasFacade() {
		return comprasFacade;
	}

	public void setComprasFacade(IComprasFacade comprasFacade) {
		this.comprasFacade = comprasFacade;
	}

	public IComprasCadastrosBasicosFacade getComprasCadastrosBasicosFacade() {
		return comprasCadastrosBasicosFacade;
	}

	public void setComprasCadastrosBasicosFacade(
			IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade) {
		this.comprasCadastrosBasicosFacade = comprasCadastrosBasicosFacade;
	}

	public ScoMaterial getMaterial() {
		return material;
	}

	public void setMaterial(ScoMaterial material) {
		this.material = material;
	}

	public ScoUnidadeMedida getScoUnidadeMedida() {
		return scoUnidadeMedida;
	}

	public void setScoUnidadeMedida(ScoUnidadeMedida scoUnidadeMedida) {
		this.scoUnidadeMedida = scoUnidadeMedida;
	}

	public BigDecimal getFatorConversao() {
		return fatorConversao;
	}

	public void setFatorConversao(BigDecimal fatorConversao) {
		this.fatorConversao = fatorConversao;
	}

	public boolean isExibirBotaoNovo() {
		return exibirBotaoNovo;
	}

	public void setExibirBotaoNovo(boolean exibirBotaoNovo) {
		this.exibirBotaoNovo = exibirBotaoNovo;
	}

	public Integer getCodMaterial() {
		return codMaterial;
	}

	public void setCodMaterial(Integer codMaterial) {
		this.codMaterial = codMaterial;
	}

	public String getUmdCodigo() {
		return umdCodigo;
	}

	public void setUmdCodigo(String umdCodigo) {
		this.umdCodigo = umdCodigo;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	} 


	public DynamicDataModel<SceConversaoUnidadeConsumos> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<SceConversaoUnidadeConsumos> dataModel) {
	 this.dataModel = dataModel;
	}
}