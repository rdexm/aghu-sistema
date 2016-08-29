package br.gov.mec.aghu.compras.cadastrosbasicos.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoParamAutorizacaoSc;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;


public class ParamAutorizacaoScPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = -4649847290296636395L;

	private static final String PARAM_AUTORIZACAO_SC_CRUD = "paramAutorizacaoScCRUD";

	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;
	
	@EJB
	protected ICentroCustoFacade centroCustoFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	private ScoParamAutorizacaoSc paramAutorizacao = new ScoParamAutorizacaoSc();
	private RapServidores servidor;
	FccCentroCustos centroCustoSolicitante;
	FccCentroCustos centroCustoAplicacao;

	@Inject @Paginator
	private DynamicDataModel<ScoParamAutorizacaoSc> dataModel;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	@Override
	public Long recuperarCount() {
		return comprasCadastrosBasicosFacade.pesquisarParamAutorizacaoScCount(paramAutorizacao);
	}

	@Override
	public List<ScoParamAutorizacaoSc> recuperarListaPaginada(final Integer firstResult, final Integer maxResult, final String orderProperty, final boolean asc) {
		return comprasCadastrosBasicosFacade.pesquisarParamAutorizacaoSc(firstResult, maxResult, orderProperty, asc, paramAutorizacao);
	}
	
	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}

	public void limparPesquisa() {
		dataModel.limparPesquisa();
		this.setParamAutorizacao(new ScoParamAutorizacaoSc());
		this.centroCustoSolicitante = null;
		this.centroCustoAplicacao = null;	
		this.servidor = null;
	}
	
	public String inserir() {
		return PARAM_AUTORIZACAO_SC_CRUD;
	}
	
	public String editar() {
		return PARAM_AUTORIZACAO_SC_CRUD;
	}
	
	public String visualizar() {
		return PARAM_AUTORIZACAO_SC_CRUD;
	}
	
	public List<FccCentroCustos> pesquisarCentroCustoPorCodigoDescricao(String parametro) {
		return centroCustoFacade.pesquisarCentroCustosPorCodigoDescricao((String) parametro);
	}

	public List<RapServidores> listarServidores(String objPesquisa) {
		if (objPesquisa!=null && !"".equalsIgnoreCase((String) objPesquisa)){
			return this.registroColaboradorFacade.pesquisarServidor(objPesquisa);
			
		}else {
			return this.registroColaboradorFacade.pesquisarRapServidores();
		}
	}

	public ScoParamAutorizacaoSc getParamAutorizacao() {
		return paramAutorizacao;
	}

	public void setParamAutorizacao(ScoParamAutorizacaoSc paramAutorizacao) {
		this.paramAutorizacao = paramAutorizacao;
	} 

	public FccCentroCustos getCentroCustoSolicitante() {
		return centroCustoSolicitante;
	}

	public void setCentroCustoSolicitante(FccCentroCustos centroCustoSolicitante) {
		this.centroCustoSolicitante = centroCustoSolicitante;
	}

	public FccCentroCustos getCentroCustoAplicacao() {
		return centroCustoAplicacao;
	}

	public void setCentroCustoAplicacao(FccCentroCustos centroCustoAplicacao) {
		this.centroCustoAplicacao = centroCustoAplicacao;
	}

	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	public DynamicDataModel<ScoParamAutorizacaoSc> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<ScoParamAutorizacaoSc> dataModel) {
		this.dataModel = dataModel;
	}
}