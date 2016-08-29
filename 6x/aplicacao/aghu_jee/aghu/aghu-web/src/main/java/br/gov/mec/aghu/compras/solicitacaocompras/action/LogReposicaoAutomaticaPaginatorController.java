package br.gov.mec.aghu.compras.solicitacaocompras.action;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.solicitacaocompra.business.ISolicitacaoComprasFacade;
import br.gov.mec.aghu.compras.vo.ProcessoGeracaoAutomaticaVO;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.ScoLogGeracaoScMatEstocavel;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.Severity;

public class LogReposicaoAutomaticaPaginatorController extends ActionController implements ActionPaginator {

	private static final String ESTOQUE_ESTATISTICA_CONSUMO = "estoque-estatisticaConsumo";

	private static final String SOLICITACAO_COMPRA_CRUD = "compras-solicitacaoCompraCRUD";

	@Inject @Paginator
	private DynamicDataModel<ScoLogGeracaoScMatEstocavel> dataModel;

	private static final long serialVersionUID = 6311944654533755902L;

	@EJB
	protected ISolicitacaoComprasFacade solicitacaoComprasFacade;

	@EJB
	private IComprasFacade comprasFacade;
	
	// filtros
	ProcessoGeracaoAutomaticaVO processo;
	ScoMaterial material;
	DominioSimNao indEmContrato;
	
	// URL para botão voltar
	private String voltarParaUrl;
	
	/** Mapa Material X SCs não em AF */
	private Map<ScoMaterial, String> materiaisScsNaoAf = new HashMap<ScoMaterial, String>();
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	/**
	 * Inicializa. 
	 */
	public void iniciar() {
		processo = solicitacaoComprasFacade.obterUltimoProcessoGeracao();
	}
	
	
	// suggestions
	public List<ProcessoGeracaoAutomaticaVO> pesquisarProcessoGeracaoCodigoData(String parametro) {
		return solicitacaoComprasFacade.pesquisarProcessoGeracaoCodigoData(parametro);
	}

	public List<ScoMaterial> pesquisarMaterialPorCodigoDescricao(String objPesquisa) {
		return this.returnSGWithCount(this.comprasFacade.listarMateriaisAtivos(objPesquisa, true),listarMateriaisCount(objPesquisa));
	}
	
	public Long listarMateriaisCount(String param)	{
		return this.comprasFacade.listarMateriaisAtivosCount(param, true);
	}

	
	// botões
	
	public void pesquisar() {
		if (this.processo == null && this.material == null) {
			this.apresentarMsgNegocio(Severity.FATAL, "MENSAGEM_PARAMETROS_FILTRO_LOG_OBRIGATORIOS");	
		} else {
			materiaisScsNaoAf.clear();
			this.dataModel.reiniciarPaginator();
		}
	}
	
	public void limpar() {
		processo = solicitacaoComprasFacade.obterUltimoProcessoGeracao();
		this.material = null;
		this.indEmContrato = null;
		this.setAtivo(false);	
	}
	
	// paginator

	@Override
	public Long recuperarCount() {		
		return this.solicitacaoComprasFacade.contarLogGeracaoScMaterialEstocavel(this.processo, this.material, this.indEmContrato);
	}

	@Override
	public List<ScoLogGeracaoScMatEstocavel> recuperarListaPaginada(Integer firstResult, Integer maxResults, String orderProperty,boolean asc) {
		List<ScoLogGeracaoScMatEstocavel> logGeracaoSC = this.solicitacaoComprasFacade.pesquisarLogGeracaoScMaterialEstocavel(firstResult, 
				maxResults, orderProperty, asc, this.processo, this.material, this.indEmContrato);
		return logGeracaoSC;
	}
	
	/**
	 * Obtem SC a partir do log de geração.
	 * 
	 * @param item Log
	 * @return SC
	 */
	public Integer getScNova(ScoLogGeracaoScMatEstocavel item) {
		return solicitacaoComprasFacade.obterSolicitacaoDeCompra(item);
	}
	
	/**
	 * Pesquisa SC's de um material que não estão em AF.
	 * 
	 * @param material Material
	 * @return SC's
	 */
	public String getScsNaoAf(ScoMaterial material) {
		if (material == null) {
			return null;
		}
		
		if (!materiaisScsNaoAf.containsKey(material)) {
			StringBuilder sb = new StringBuilder();
			List<ScoSolicitacaoDeCompra> scsNaoAf = solicitacaoComprasFacade.pesquisarScsNaoAf(material);
			
			for (ScoSolicitacaoDeCompra sc : scsNaoAf) {
				String key = getBundle().getString("TOOLTIP_SC_NAO_AF");
				
				Long qtde = sc.getQtdeAprovada() != null && sc.getQtdeAprovada() > 0 ? 
						sc.getQtdeAprovada() : sc.getQtdeSolicitada();
						
				String scNaoAfStr = MessageFormat.format(key, sc.getNumero().toString(), sc.getDtSolicitacao(), qtde);
				sb.append(scNaoAfStr).append("<br />");
			}
			
			String scsNaoAfStr = sb.toString();
			materiaisScsNaoAf.put(material, scsNaoAfStr);
			return scsNaoAfStr;
		} else {
			return materiaisScsNaoAf.get(material);
		}
	}
	
	public void reiniciarPaginator(){
		this.dataModel.reiniciarPaginator();
	}	
	
	public String voltar(){
		return this.voltarParaUrl;
	}
	
	public String redirecionarEstatisticaConsumo(){
		return ESTOQUE_ESTATISTICA_CONSUMO;
	}
	
	public String redirecionarSolicitacaoCompra(){
		return SOLICITACAO_COMPRA_CRUD;
	}
	
	// Getters/Setters

	public String getVoltarParaUrl() {
		return voltarParaUrl;
	}

	public void setVoltarParaUrl(String voltarParaUrl) {
		this.voltarParaUrl = voltarParaUrl;
	}

	public ScoMaterial getMaterial() {
		return material;
	}

	public void setMaterial(ScoMaterial material) {
		this.material = material;
	}
	
	public ProcessoGeracaoAutomaticaVO getProcesso() {
		return processo;
	}

	public void setProcesso(ProcessoGeracaoAutomaticaVO processo) {
		this.processo = processo;
	}
	
	public DominioSimNao getIndEmContrato() {
		return indEmContrato;
	}

	public void setIndEmContrato(DominioSimNao indEmContrato) {
		this.indEmContrato = indEmContrato;
	}

	public DynamicDataModel<ScoLogGeracaoScMatEstocavel> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<ScoLogGeracaoScMatEstocavel> dataModel) {
	 this.dataModel = dataModel;
	}	
	
	public void setAtivo(boolean ativo){
		this.dataModel.setPesquisaAtiva(ativo);
	}
	
	public boolean isAtivo(){
		return this.dataModel.getPesquisaAtiva();
	}
}
