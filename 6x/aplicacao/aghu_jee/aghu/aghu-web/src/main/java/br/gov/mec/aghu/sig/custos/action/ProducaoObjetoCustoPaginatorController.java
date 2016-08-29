package br.gov.mec.aghu.sig.custos.action;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioTipoCalculoObjeto;
import br.gov.mec.aghu.dominio.DominioTipoDirecionadorCustos;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SigDirecionadores;
import br.gov.mec.aghu.model.SigObjetoCustos;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.sig.custos.business.ICustosSigCadastrosBasicosFacade;
import br.gov.mec.aghu.sig.custos.business.ICustosSigFacade;
import br.gov.mec.aghu.sig.custos.processamento.business.ICustosSigProcessamentoFacade;
import br.gov.mec.aghu.sig.custos.vo.DetalheProducaoObjetoCustoVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class ProducaoObjetoCustoPaginatorController extends ActionController implements ActionPaginator {

	

	private static final String CARGA_PRODUCAO_OBJETO_CUSTO = "cargaProducaoObjetoCusto";

	private static final String PRODUCAO_OBJETO_CUSTO_CRUD = "producaoObjetoCustoCRUD";

	@Inject @Paginator
	private DynamicDataModel<DetalheProducaoObjetoCustoVO> dataModel;

	private static final Log LOG = LogFactory.getLog(ProducaoObjetoCustoPaginatorController.class);

	private static final long serialVersionUID = -5469875496856896L;

	@EJB
	private ICustosSigFacade custosSigFacade;

	@EJB
	private ICustosSigCadastrosBasicosFacade custosSigCadastrosBasicosFacade;

	@EJB
	private ICustosSigProcessamentoFacade custosSigProcessamentoFacade;

	@EJB
	private ICentroCustoFacade centroCustoFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IPermissionService permissionService;
	
	private DetalheProducaoObjetoCustoVO parametroSelecionado;

	private FccCentroCustos centroCusto;
	private SigProcessamentoCusto competencia;
	private SigObjetoCustos objetoCusto;
	private SigDirecionadores direcionador;
	private Integer seqDetalheProducao;

	
	@PostConstruct
	protected void inicializar(){
		this.dataModel.setUserEditPermission(this.permissionService.usuarioTemPermissao(this.obterLoginUsuarioLogado(), "editarPesoObjetoCusto","editar"));
		this.dataModel.setUserRemovePermission(this.permissionService.usuarioTemPermissao(this.obterLoginUsuarioLogado(), "editarPesoObjetoCusto","editar"));
		this.begin(conversation, true);
		LOG.debug("begin conversation");
	}
	
	public void iniciar() {
	 

		if(centroCusto == null){
			try {
				RapServidores servidor = registroColaboradorFacade.obterServidorAtivoPorUsuarioSemCache(this.obterLoginUsuarioLogado());
				if (servidor.getCentroCustoAtuacao() != null) {
					centroCusto = servidor.getCentroCustoAtuacao();
				} else {
					centroCusto = servidor.getCentroCustoLotacao();
				}
			} catch (ApplicationBusinessException e) {
				centroCusto = null;
			}
		}
	
	}	

	public void excluir() {
		try {
			Date producao = this.custosSigCadastrosBasicosFacade.obterDetalheProducao(parametroSelecionado.getSeqDetalheProducao()).getSigProcessamentoCustos().getCompetencia();
			this.custosSigCadastrosBasicosFacade.excluirDetalheProducao(parametroSelecionado.getSeqDetalheProducao());
			this.reiniciarPaginator();
			String data = new SimpleDateFormat("MM/yyyy").format(producao);
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_PRODUCAO_MANUAL_EXCLUIDA_SUCESSO", data);
		} catch (ApplicationBusinessException e) {
			this.apresentarExcecaoNegocio(e);
		}
	}

	public String iniciarCargaProducao() {
		return CARGA_PRODUCAO_OBJETO_CUSTO;
	}

	public String iniciarNovo() {
		return PRODUCAO_OBJETO_CUSTO_CRUD;
	}

	public String editar() {
		try {
			this.custosSigCadastrosBasicosFacade.verificarEdicaoDetalheProducao(this.parametroSelecionado.getSeqDetalheProducao());
			return PRODUCAO_OBJETO_CUSTO_CRUD;
		} catch (ApplicationBusinessException e) {
			this.apresentarExcecaoNegocio(e);
			return null;
		}
	}

	public void selecionarCentroCusto() {
		this.setObjetoCusto(null);
	}

	@Override
	public Long recuperarCount() {
		return this.custosSigCadastrosBasicosFacade.pesquisarProducaoCount(this.getCentroCusto(), this.getCompetencia(), this.getObjetoCusto(),
				this.getDirecionador());
	}

	@Override
	public List<DetalheProducaoObjetoCustoVO> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return this.custosSigCadastrosBasicosFacade.pesquisarProducao(firstResult, maxResult, orderProperty, asc, this.getCentroCusto(), this.getCompetencia(),
				this.getObjetoCusto(), this.getDirecionador());
	}

	public List<SigProcessamentoCusto> listarCompetencias(){
		return this.custosSigProcessamentoFacade.pesquisarCompetencia();
	}
	
	public List<SigDirecionadores> listarDirecionadores(){
		return this.custosSigCadastrosBasicosFacade.pesquisarDirecionadores(DominioTipoDirecionadorCustos.RT, DominioTipoCalculoObjeto.PM, null);
	}
	
	public void reiniciarPaginator(){
		this.dataModel.reiniciarPaginator();
	}
	
	//Navegações
	public void pesquisar() {
		reiniciarPaginator();
	}

	public void limpar() {
		this.setCentroCusto(null);
		this.setCompetencia(null);
		this.setObjetoCusto(null);
		this.setDirecionador(null);
		this.setAtivo(false);
	}

	public List<SigObjetoCustos> pesquisarObjetoCusto(String paramPesquisa) {
		return custosSigFacade.pesquisarObjetoCustoAssociadoClientes(paramPesquisa, this.getCentroCusto());
	}

	public List<FccCentroCustos> pesquisarCentroCusto(String paramPesquisa) throws BaseException {
		return this.centroCustoFacade.pesquisarCentroCustosPorCentroProdExcluindoGcc(paramPesquisa, null, null);
	}

	public FccCentroCustos getCentroCusto() {
		return centroCusto;
	}

	public void setCentroCusto(FccCentroCustos centroCusto) {
		this.centroCusto = centroCusto;
	}

	public SigProcessamentoCusto getCompetencia() {
		return competencia;
	}

	public void setCompetencia(SigProcessamentoCusto competencia) {
		this.competencia = competencia;
	}

	public SigDirecionadores getDirecionador() {
		return direcionador;
	}

	public void setDirecionador(SigDirecionadores direcionador) {
		this.direcionador = direcionador;
	}

	public Integer getSeqDetalheProducao() {
		return seqDetalheProducao;
	}

	public void setSeqDetalheProducao(Integer seqDetalheProducao) {
		this.seqDetalheProducao = seqDetalheProducao;
	}

	public SigObjetoCustos getObjetoCusto() {
		return objetoCusto;
	}

	public void setObjetoCusto(SigObjetoCustos objetoCusto) {
		this.objetoCusto = objetoCusto;
	}
	
	public DynamicDataModel<DetalheProducaoObjetoCustoVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<DetalheProducaoObjetoCustoVO> dataModel) {
		this.dataModel = dataModel;
	}
	
	public void setAtivo(boolean ativo){
		this.dataModel.setPesquisaAtiva(ativo);
	}
	
	public boolean isAtivo(){
		return this.dataModel.getPesquisaAtiva();
	}

	public DetalheProducaoObjetoCustoVO getParametroSelecionado() {
		return parametroSelecionado;
	}

	public void setParametroSelecionado(DetalheProducaoObjetoCustoVO parametroSelecionado) {
		this.parametroSelecionado = parametroSelecionado;
	}
}
