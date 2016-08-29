package br.gov.mec.aghu.orcamento.cadastrosbasicos.action;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.solicitacaocompra.business.ISolicitacaoComprasFacade;
import br.gov.mec.aghu.dominio.DominioTipoSolicitacao;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.FsoGrupoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoParametrosOrcamento;
import br.gov.mec.aghu.model.FsoVerbaGestao;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoGrupoServico;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.orcamento.cadastrosbasicos.business.ICadastrosBasicosOrcamentoFacade;
import br.gov.mec.aghu.orcamento.cadastrosbasicos.vo.FsoParametrosOrcamentoCriteriaVO;
import br.gov.mec.aghu.orcamento.cadastrosbasicos.vo.FsoParametrosOrcamentoResultVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class ParametrosOrcamentoPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = -8967481180779091683L;


	@EJB
	private ICadastrosBasicosOrcamentoFacade cadastrosBasicosOrcamentoFacade;

	@EJB
	private IComprasFacade comprasFacade;

	@EJB
	private ICentroCustoFacade centroCustoFacade;

	@EJB
	private ISolicitacaoComprasFacade solicitacaoComprasFacade;

	
	private FsoParametrosOrcamentoCriteriaVO criteria;

	private static final NumberFormat NUMBER_FORMAT = new DecimalFormat("###,##0.00");

	private static final String PARAMETROS_ORCAMENTO_CRUD = "parametrosOrcamentoCRUD";
	
	@Inject @Paginator
	private DynamicDataModel<FsoParametrosOrcamentoResultVO> dataModel;
	
	private FsoParametrosOrcamentoResultVO selecionado;

	private boolean iniciouTela;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public void iniciar() {
	 

		if(!iniciouTela){
			limpar();
			iniciouTela = true;
		}
	
	}

	public void limpar() {
		dataModel.limparPesquisa();
        dataModel.setPesquisaAtiva(false);
		criteria = new FsoParametrosOrcamentoCriteriaVO();
		criteria.setAplicacao(DominioTipoSolicitacao.SC);
	}

	public List<ScoGrupoMaterial> pesquisarGruposMateriais(String filter) {
		return comprasFacade.pesquisarGrupoMaterialPorCodigoDescricao(filter);
	}
	
	public List<ScoGrupoServico> pesquisarGruposServicos(String filter) {
		return comprasFacade.listarGrupoServico(filter);
	}

	public List<ScoMaterial> pesquisarMateriais(String objPesquisa) {
		return this.returnSGWithCount(this.comprasFacade.listarScoMateriais(objPesquisa, null, true),listarMateriaisCount(objPesquisa));
	}
	
	public Long listarMateriaisCount(String param)	{
		return this.solicitacaoComprasFacade.listarMateriaisAtivosCount(param, this.obterLoginUsuarioLogado());
	}
	
	public List<ScoServico> pesquisarServicos(String filter) {
		return comprasFacade.listarServicosAtivos(filter);
	}

	public List<FccCentroCustos> pesquisarCentrosCusto(String objPesquisa) {
		return centroCustoFacade.pesquisarCentroCustos(objPesquisa);
	}

	public void pesquisar() {
		dataModel.reiniciarPaginator();
        dataModel.setPesquisaAtiva(true);
	}

	
	public void excluir() {
		try{
		    cadastrosBasicosOrcamentoFacade.excluirFsoParametrosOrcamento(selecionado.getSeq());					
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_PARAM_ORCA_DELETE_SUCESSO");
		} catch (ApplicationBusinessException e) {
			super.apresentarExcecaoNegocio(e);
		}
	}
	
	public String inserir() {
		return PARAMETROS_ORCAMENTO_CRUD;
	}
	
	public String editar(){
		return PARAMETROS_ORCAMENTO_CRUD;
	} 
	
	public String getGrupoServicoMaterialLabel() {
		String gm = super.getBundle().getString("LABEL_GRUPO_MATERIAL_PARAMETRO_ORCAMENTO");
		String gs = super.getBundle().getString("LABEL_GRUPO_SERVICO_PARAMETRO_ORCAMENTO");
		
		if (DominioTipoSolicitacao.SC.equals(criteria.getAplicacao())) {
			return gm;
		} else if (DominioTipoSolicitacao.SS.equals(criteria.getAplicacao())) {
			return gs;
		} else {
			return String.format("%s/%s", gm, gs);
		}
	}
	
	public String getGrupoServicoMaterialValue(FsoParametrosOrcamentoResultVO item) {		
		Object id, desc;
		
		if (item.getGrupoMaterialId() != null) {
			id = item.getGrupoMaterialId();
			desc = item.getGrupoMaterialDesc();
		} else if (item.getGrupoServicoId() != null) {
			id = item.getGrupoServicoId();
			desc = item.getGrupoServicoDesc();
		} else {
			return null;
		}
		
		return String.format("%s - %s", id, desc);
	}
	
	public String getServicoMaterialLabel() {
		String m = super.getBundle().getString("LABEL_MATERIAL_PARAMETRO_ORCAMENTO");
		String s = super.getBundle().getString("LABEL_SERVICO_PARAMETRO_ORCAMENTO");
		
		if (DominioTipoSolicitacao.SC.equals(criteria.getAplicacao())) {
			return m;
		} else if (DominioTipoSolicitacao.SS.equals(criteria.getAplicacao())) {
			return s;
		} else {
			return m + "/" + s;
		}
	}
	
	public Integer getServicoMaterialValue(FsoParametrosOrcamentoResultVO item) {
		return item.getMaterialId() != null ? item.getMaterialId() : item.getServicoId();
	}
	
	public String getServicoMaterialDesc(FsoParametrosOrcamentoResultVO item) {
		return item.getMaterialDesc() != null ? item.getMaterialDesc() : item.getServicoDesc();
	}

	public String format(BigDecimal number) {
		return NUMBER_FORMAT.format(number);
	}

	@Override
	public Long recuperarCount() {
		return cadastrosBasicosOrcamentoFacade.contarParametrosOrcamento(criteria);
	}

	@Override
	public List<FsoParametrosOrcamentoResultVO> recuperarListaPaginada(Integer first, Integer max, String order, boolean asc) {
		return cadastrosBasicosOrcamentoFacade.pesquisarParametrosOrcamento(criteria, first, max, FsoParametrosOrcamento.Fields.SEQ.toString(), true);
	}

	public void refreshFromAplicacao() {
		criteria.setLimite(null);
		criteria.setValor(null);		
		if (!DominioTipoSolicitacao.SC.equals(criteria.getAplicacao())) {
			criteria.setIndicador(null);
			criteria.setGrupoMaterial(null);
			criteria.setMaterial(null);
			
		}

		if (!DominioTipoSolicitacao.SS.equals(criteria.getAplicacao())) {
			criteria.setGrupoServico(null);
			criteria.setServico(null);
		}
	}
	
	/**
	 * Zera valor se limite anulado.
	 */
	public void refreshFromTpLimite() {
		if (criteria.getLimite() == null) {
			criteria.setValor(null);
		}
	}
	
	
	public List<FsoGrupoNaturezaDespesa> pesquisarGruposNaturezaDespesa(String filter) {
		return cadastrosBasicosOrcamentoFacade.pesquisarGrupoNaturezaDespesaPorCodigoEDescricao(filter);
	}
	
	public List<FsoNaturezaDespesa> pesquisarNaturezasDespesa(String filter) {
		return cadastrosBasicosOrcamentoFacade.pesquisarNaturezasDespesa(criteria.getGrupoNatureza(), filter);
	}

	public List<FsoVerbaGestao> pesquisarVerbasGestao(String filter) {
		return cadastrosBasicosOrcamentoFacade.pesquisarVerbaGestaoPorSeqOuDescricao(filter);
	}

	public FsoParametrosOrcamentoCriteriaVO getCriteria() {
		return criteria;
	}

	public boolean isIniciouTela() {
		return iniciouTela;
	}

	public void setIniciouTela(boolean iniciouTela) {
		this.iniciouTela = iniciouTela;
	}

	public DynamicDataModel<FsoParametrosOrcamentoResultVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(
			DynamicDataModel<FsoParametrosOrcamentoResultVO> dataModel) {
		this.dataModel = dataModel;
	}

	public FsoParametrosOrcamentoResultVO getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(FsoParametrosOrcamentoResultVO selecionado) {
		this.selecionado = selecionado;
	}

	public void setCriteria(FsoParametrosOrcamentoCriteriaVO criteria) {
		this.criteria = criteria;
	}
}