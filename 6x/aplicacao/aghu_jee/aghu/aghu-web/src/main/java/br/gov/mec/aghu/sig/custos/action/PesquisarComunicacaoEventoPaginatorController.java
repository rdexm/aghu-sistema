package br.gov.mec.aghu.sig.custos.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SigComunicacaoEventos;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.sig.custos.business.ICustosSigCadastrosBasicosFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class PesquisarComunicacaoEventoPaginatorController extends ActionController implements ActionPaginator {

	private static final String MANTER_COMUNICACAO_EVENTO = "manterComunicacaoEvento";

	@Inject @Paginator
	private DynamicDataModel<SigComunicacaoEventos> dataModel;

	private static final Log LOG = LogFactory.getLog(PesquisarComunicacaoEventoPaginatorController.class);

	private static final long serialVersionUID = 4431865955709161574L;

	@EJB
	private ICustosSigCadastrosBasicosFacade custosSigCadastrosBasicosFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@EJB
	private ICentroCustoFacade centroCustoFacade;
	
	@EJB
	private IPermissionService permissionService;
	
	private SigComunicacaoEventos parametroSelecionado;

	private SigComunicacaoEventos sigComunicacaoEventos;

	private Boolean recarregarLista = false;
	private Integer seqComunicacaoEvento;
	
	@PostConstruct
	protected void inicializar(){
		this.dataModel.setUserEditPermission(this.permissionService.usuarioTemPermissao(this.obterLoginUsuarioLogado(), "editarComunicacaoEvento","editar"));
		this.dataModel.setUserRemovePermission(this.permissionService.usuarioTemPermissao(this.obterLoginUsuarioLogado(), "excluirComunicacaoEvento","excluir"));
		this.begin(conversation);
		LOG.debug("begin conversation");
	}
	
	public void iniciar() {
	 

		if (this.getSigComunicacaoEventos() == null) {
			this.setSigComunicacaoEventos(new SigComunicacaoEventos());
			this.getSigComunicacaoEventos().setSituacao(DominioSituacao.A);
			try {
				RapServidores servidorLogado = registroColaboradorFacade.obterServidorAtivoPorUsuarioSemCache(this.obterLoginUsuarioLogado());
				if (servidorLogado.getCentroCustoAtuacao() != null) {
					this.getSigComunicacaoEventos().setFccCentroCustos(servidorLogado.getCentroCustoAtuacao());
				} else {
					this.getSigComunicacaoEventos().setFccCentroCustos(servidorLogado.getCentroCustoLotacao());
				}
			} catch (ApplicationBusinessException e) {
				this.getSigComunicacaoEventos().setFccCentroCustos(null);
			}
		}

		if (this.getRecarregarLista()) {
			this.reiniciarPaginator();
			this.setRecarregarLista(false);
		}
	
	}

	public List<FccCentroCustos> pesquisarCentroCusto(String paramPesquisa) {
		List<FccCentroCustos> listaResultado = new ArrayList<FccCentroCustos>();
		listaResultado = this.centroCustoFacade.pesquisarCentroCustosPorCentroProdExcluindoGcc(paramPesquisa, null, null);
		return  listaResultado;
	}

	public List<RapServidores> pesquisarServidor(String paramPesquisa) throws BaseException {
		List<RapServidores> listaResultado = new ArrayList<RapServidores>();
		listaResultado = this.registroColaboradorFacade.pesquisarServidorVinculoAtivoEProgramadoAtual(paramPesquisa);
		return  listaResultado;
	}

	public void limparServidor() {
		this.getSigComunicacaoEventos().setServidor(null);
	}

	public void limparCentroCusto() {
		this.getSigComunicacaoEventos().setFccCentroCustos(null);
	}

	@Override
	public Long recuperarCount() {
		return this.custosSigCadastrosBasicosFacade.pesquisarComunicacaoEventosCount(this.getSigComunicacaoEventos());
	}

	@Override
	public List<SigComunicacaoEventos> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return this.custosSigCadastrosBasicosFacade.pesquisarComunicacaoEventos(firstResult, maxResult, orderProperty, asc, this.getSigComunicacaoEventos());
	}
	
	public void reiniciarPaginator(){
		this.dataModel.reiniciarPaginator();
	}

	public void pesquisarComunicacaoEvento() {
		reiniciarPaginator();
	}

	public void limparComunicacaoEvento() {
		this.setSigComunicacaoEventos(null);
		this.iniciar();
		this.setAtivo(false);
	}

	public String cadastrarComunicacaoEvento() {
		return MANTER_COMUNICACAO_EVENTO;
	}

	public String editar() {
		return MANTER_COMUNICACAO_EVENTO;
	}

	public String visualizar() {
		return MANTER_COMUNICACAO_EVENTO;
	}

	public void excluir() {
		this.custosSigCadastrosBasicosFacade.excluirComunicacaoEvento( this.getParametroSelecionado());
		this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUSAO_COMUNICACAO_EVENTO");
		reiniciarPaginator();
	}

	public SigComunicacaoEventos getSigComunicacaoEventos() {
		return sigComunicacaoEventos;
	}

	public void setSigComunicacaoEventos(SigComunicacaoEventos sigComunicacaoEventos) {
		this.sigComunicacaoEventos = sigComunicacaoEventos;
	}

	public Integer getSeqComunicacaoEvento() {
		return seqComunicacaoEvento;
	}

	public void setSeqComunicacaoEvento(Integer seqComunicacaoEvento) {
		this.seqComunicacaoEvento = seqComunicacaoEvento;
	}

	public Boolean getRecarregarLista() {
		return recarregarLista;
	}

	public void setRecarregarLista(Boolean recarregarLista) {
		this.recarregarLista = recarregarLista;
	}

	public DynamicDataModel<SigComunicacaoEventos> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<SigComunicacaoEventos> dataModel) {
	 this.dataModel = dataModel;
	}
	
	public Boolean getAtivo(){
		return this.dataModel.getPesquisaAtiva();
	}
	
	public void setAtivo(Boolean ativo){
		this.dataModel.setPesquisaAtiva(ativo);
	}

	public SigComunicacaoEventos getParametroSelecionado() {
		return parametroSelecionado;
	}

	public void setParametroSelecionado(SigComunicacaoEventos parametroSelecionado) {
		this.parametroSelecionado = parametroSelecionado;
	}
}
