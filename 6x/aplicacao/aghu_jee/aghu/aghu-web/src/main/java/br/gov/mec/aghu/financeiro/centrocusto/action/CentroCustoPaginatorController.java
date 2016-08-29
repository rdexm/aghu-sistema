package br.gov.mec.aghu.financeiro.centrocusto.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.FcuGrupoCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SigCentroProducao;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.sig.custos.business.ICustosSigCadastrosBasicosFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.dominio.DominioTipoCentroProducaoCustos;



public class CentroCustoPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = -3181488786651124362L;

	private static final String CENTRO_CUSTO_CRUD = "centroCustoCRUD";
	
	private FccCentroCustos centroCustoSuperior;
	private FcuGrupoCentroCustos grupoCentroCusto;
	private RapServidores servidor;

	private FccCentroCustos centroCusto = new FccCentroCustos();
	private Boolean operacaoConcluida = false;
	private Integer codigo;
	
	@EJB
	protected ICentroCustoFacade centroCustoFacade;
	
	@EJB
	protected IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private ICustosSigCadastrosBasicosFacade custosSigCadastrosBasicosFacade;

	@Inject @Paginator
	private DynamicDataModel<FccCentroCustos> dataModel;
	
	@Inject
	private CentroCustoController centroCustoController;
	
	private FccCentroCustos selecionado;
	
	private DominioTipoCentroProducaoCustos[] tiposCentroProducao;

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void pesquisarCentroCustos() {
		dataModel.reiniciarPaginator();
	}
	
	public List<SigCentroProducao> getListaCentroProducao() {
		return this.custosSigCadastrosBasicosFacade.pesquisarCentroProducao();
	}

	public List<FccCentroCustos> pesquisarCentroCustosSuperior(final String strPesquisa) {
		return this.returnSGWithCount(centroCustoFacade.pesquisarCentroCustosSuperior(strPesquisa != null ? strPesquisa : null),pesquisarCentroCustosSuperiorCount(strPesquisa));
	}
	
	public Long pesquisarCentroCustosSuperiorCount(final String strPesquisa) {
		return centroCustoFacade.pesquisarCentroCustosSuperiorCount(strPesquisa != null ? strPesquisa : null);
	}

	public List<FcuGrupoCentroCustos> pesquisarGrupoCentroCustos(final String strPesquisa) {
		return this.returnSGWithCount(centroCustoFacade.pesquisarGruposCentroCustos(strPesquisa != null ? strPesquisa : null),pesquisarGrupoCentroCustosCount(strPesquisa));
	}

	public Long pesquisarGrupoCentroCustosCount(final String strPesquisa) {
		return centroCustoFacade.pesquisarGruposCentroCustosCount(strPesquisa != null ? strPesquisa : null);
	}

	public List<RapServidores> pesquisarServidores(String strPesquisa) {
		return this.returnSGWithCount(registroColaboradorFacade.pesquisarRapServidores(strPesquisa),pesquisarServidorCount(strPesquisa));
	}

	public Long pesquisarServidorCount(String strPesquisa) {
		return registroColaboradorFacade.pesquisarRapServidoresCount(strPesquisa);
	}
	

	public String inserir(){
		centroCustoController.setIsEdicao(false);
		return CENTRO_CUSTO_CRUD;
	}
	
	public String editar(){
		centroCustoController.setIsEdicao(true);
		return CENTRO_CUSTO_CRUD;
	}

	@Override
	public Long recuperarCount() {
		return centroCustoFacade.obterFccCentroCustoCount(centroCusto,grupoCentroCusto, centroCustoSuperior, servidor, tiposCentroProducao);
	}

	@Override
	public List<FccCentroCustos> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return centroCustoFacade.pesquisarCentroCustos(firstResult, maxResult, centroCusto, grupoCentroCusto, centroCustoSuperior, servidor, tiposCentroProducao);
	}

	public void atribuirCentroCustoSuperior(FccCentroCustos centroCustoSuperior) {
		this.centroCustoSuperior = centroCustoSuperior;
	}

	public void atribuirGrupoCentroCusto(FcuGrupoCentroCustos grupoCC) {
		this.grupoCentroCusto = grupoCC;
	}

	public void limparCampos() {
		dataModel.limparPesquisa();
		centroCustoSuperior = null;
		servidor = null;
		grupoCentroCusto = null;
		centroCusto = new FccCentroCustos();
		tiposCentroProducao = null;
	}

	public FccCentroCustos getCentroCustoSuperior() {
		return centroCustoSuperior;
	}

	public void setCentroCustoSuperior(FccCentroCustos centroCustoSuperior) {
		this.centroCustoSuperior = centroCustoSuperior;
	}

	public FcuGrupoCentroCustos getGrupoCentroCusto() {
		return grupoCentroCusto;
	}

	public void setGrupoCentroCusto(FcuGrupoCentroCustos grupoCentroCusto) {
		this.grupoCentroCusto = grupoCentroCusto;
	}
	
	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	public FccCentroCustos getCentroCusto() {
		return centroCusto;
	}

	public void setCentroCusto(FccCentroCustos centroCusto) {
		this.centroCusto = centroCusto;
	}

	public Boolean getOperacaoConcluida() {
		return operacaoConcluida;
	}

	public void setOperacaoConcluida(Boolean operacaoConcluida) {
		this.operacaoConcluida = operacaoConcluida;
	}

	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	public DynamicDataModel<FccCentroCustos> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<FccCentroCustos> dataModel) {
		this.dataModel = dataModel;
	}

	public FccCentroCustos getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(FccCentroCustos selecionado) {
		this.selecionado = selecionado;
	}

	public DominioTipoCentroProducaoCustos[] getTiposCentroProducao() {
		return tiposCentroProducao;
	}

	public void setTiposCentroProducao(DominioTipoCentroProducaoCustos[] tiposCentroProducao) {
		this.tiposCentroProducao = tiposCentroProducao;
	}
}