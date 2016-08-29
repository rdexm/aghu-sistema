package br.gov.mec.aghu.sig.custos.action;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoVersoesCustos;
import br.gov.mec.aghu.dominio.DominioTipoObjetoCusto;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SigCentroProducao;
import br.gov.mec.aghu.model.SigObjetoCustoCcts;
import br.gov.mec.aghu.model.SigObjetoCustoVersoes;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.sig.custos.business.ICustosSigCadastrosBasicosFacade;
import br.gov.mec.aghu.sig.custos.business.ICustosSigFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.commons.WebUtil;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class ManterObjetosCustoPaginatorController extends ActionController implements ActionPaginator {

	private static final String VISUALIZAR_HISTORICO_OBJETO_CUSTO = "visualizarHistoricoObjetoCusto";

	private static final String MANTER_OBJETOS_CUSTO = "manterObjetosCusto";

	@Inject @Paginator
	private DynamicDataModel<SigObjetoCustoVersoes> dataModel;

	private static final long serialVersionUID = 1449586944657299128L;

	@EJB
	private ICustosSigFacade custosSigFacade;

	@EJB
	private ICustosSigCadastrosBasicosFacade custosSigCadastrosBasicosFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@EJB
	private ICentroCustoFacade centroCustoFacade;

	@Inject
	private ManterObjetosCustoController manterObjetosCustoController;
	
	private SigCentroProducao sigCentroProducao;
	private FccCentroCustos fccCentroCustos;
	private DominioSituacaoVersoesCustos situacao;
	private String nome;
	private DominioSimNao possuiComposicao;
	private Integer seqObjetoCustoVersao;
	private boolean confirmarNovaVersao;


	private boolean exibeExclusaoVersaoObjCustoAtivo;

	private DominioTipoObjetoCusto tipoObjetoCusto;
	private List<FccCentroCustos> listaCentrosCusto;
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void iniciar() {
		if(!this.isAtivo()){
			this.confirmarNovaVersao = false;
			
			// centro de custo
			if(this.getFccCentroCustos() == null) {
				try {
					RapServidores servidor = registroColaboradorFacade.obterServidorAtivoPorUsuarioSemCache(this.obterLoginUsuarioLogado());
					if (servidor.getCentroCustoAtuacao() != null) {
						this.setFccCentroCustos(servidor.getCentroCustoAtuacao());
					} else {
						this.setFccCentroCustos(servidor.getCentroCustoLotacao());
					}
				} catch (ApplicationBusinessException e) {
					this.setFccCentroCustos(null);
				}
			}
			
			// centro de produção
			if (this.getFccCentroCustos() != null) {
				this.setSigCentroProducao(this.getFccCentroCustos().getCentroProducao());
			}
			this.exibeExclusaoVersaoObjCustoAtivo = false;
		}
	}

	@Override
	public Long recuperarCount() {
		return this.custosSigFacade.pesquisarObjetoCustoVersoesCount(this.sigCentroProducao, this.fccCentroCustos, this.situacao, this.nome,
				this.tipoObjetoCusto, ((this.possuiComposicao == null) ? null : this.possuiComposicao.isSim()));
	}

	@Override
	public List<SigObjetoCustoVersoes> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return this.custosSigFacade.pesquisarObjetoCustoVersoes(firstResult, maxResult, orderProperty, asc, this.sigCentroProducao, this.fccCentroCustos,
				this.situacao, this.nome, this.tipoObjetoCusto, ((this.possuiComposicao == null) ? null : this.possuiComposicao.isSim()));
	}
	
	public boolean possuiComposicao(SigObjetoCustoVersoes objetoCustoVersao){
		return this.custosSigFacade.verificarObjetoCustoPossuiComposicao(objetoCustoVersao.getSeq());
	}
	
	public void reiniciarPaginator(){
		this.dataModel.reiniciarPaginator();
	}	

	public List<SigCentroProducao> listarCentroProducao() {
		return this.custosSigCadastrosBasicosFacade.pesquisarCentroProducao();
	}

	public List<FccCentroCustos> pesquisarCentroCusto(String paramPesquisa) throws BaseException {
		List<FccCentroCustos> listaResultado = new ArrayList<FccCentroCustos>();
		if (this.getSigCentroProducao() != null) {
			listaResultado = this.centroCustoFacade.pesquisarCentroCustosPorCentroProdExcluindoGcc(paramPesquisa, this.getSigCentroProducao().getSeq(), null);
		} else {
			listaResultado = this.centroCustoFacade.pesquisarCentroCustosPorCentroProdExcluindoGcc(paramPesquisa, null, null);
		}
		return listaResultado;
	}

	public void limparCentroCusto() {
		this.setFccCentroCustos(null);
	}

	public void pesquisarObjetoCusto() {
		reiniciarPaginator();
	}

	public void limparObjetoCusto() {
		this.setNome(null);
		this.setFccCentroCustos(null);
		this.setSigCentroProducao(null);
		this.setSituacao(null);
		this.setAtivo(false);
		this.setConfirmarNovaVersao(false);
		this.setExibeExclusaoVersaoObjCustoAtivo(false);
	}

	public String cadastrarObjetoCusto() {
		this.seqObjetoCustoVersao = null;
		return MANTER_OBJETOS_CUSTO;
	}

	public String editar() {
		if (verificaObjetoAtivoMes()) {
			this.openDialog("modalConfirmacaoAlteracaoAtivoMesWG");
			return null;
		}
		else{
			return continuarAlteracao();
		}
	}

	public String continuarAlteracao() {
		return MANTER_OBJETOS_CUSTO;
	}

	public String visualizar() {
		return MANTER_OBJETOS_CUSTO;
	}

	public String visualizarHistorico() {
		return VISUALIZAR_HISTORICO_OBJETO_CUSTO;
	}

	public void excluirVersaoObjetoCusto() {
		try {
			this.exibeExclusaoVersaoObjCustoAtivo = false;
			SigObjetoCustoVersoes objetoCustoVersao = custosSigFacade.obterObjetoCustoVersoes(seqObjetoCustoVersao);
			this.custosSigFacade.excluirVersaoObjetoCusto(objetoCustoVersao);
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUSAO_OBJETO_CUSTO");
			this.seqObjetoCustoVersao = null;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		this.reiniciarPaginator();
	}

	public void novaVersao(Integer seq) {
		SigObjetoCustoVersoes objetoCustoVersao = custosSigFacade.obterObjetoCustoVersoes(seq);
		this.confirmarNovaVersao = objetoCustoVersao.getIndSituacao() == DominioSituacaoVersoesCustos.A;
		this.seqObjetoCustoVersao = seq;
		
		if(confirmarNovaVersao) {
			this.openDialog("modalConfirmacaoNovaVersaoWG"); 
		}
		else{
			this.openDialog("modalNaoPossivelCriarVersaoWG");
		}
		
	}
	
	public String criarNovaVersaoObjetoCusto() {
		try {
			SigObjetoCustoVersoes objetoCustoVersao = custosSigFacade.obterObjetoCustoVersoes(seqObjetoCustoVersao);
			if (objetoCustoVersao.getIndSituacao() != DominioSituacaoVersoesCustos.A) {
				this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_NOVA_VERSAO_NAO_ATIVO",
						objetoCustoVersao.getIndSituacao().getDescricao());
				return null;
			}
			SigObjetoCustoVersoes novo = this.custosSigFacade.criaNovaVersao(objetoCustoVersao);
			this.seqObjetoCustoVersao = novo.getSeq();
			manterObjetosCustoController.setSeqObjetoCustoVersao(seqObjetoCustoVersao);//Informa o seq que foi criado
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_NOVA_VERSAO_SUCESSO", novo.getNroVersao(),novo.getSigObjetoCustos().getNome());
			return MANTER_OBJETOS_CUSTO;
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
	}

	public void validarExclusaoVersaoObjetoCusto(SigObjetoCustoVersoes objetoCustoVersao) {
		this.seqObjetoCustoVersao = objetoCustoVersao.getSeq();
		this.exibeExclusaoVersaoObjCustoAtivo = false;

		if (custosSigFacade.validarExclusaoAssociacaoVersoesObjetoCusto(objetoCustoVersao)) {
			this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_EXCLUSAO_NAO_PERMITIDA_OBJETO_CUSTO_ASSOCIADO");

		} else {
			if (custosSigFacade.validarExclusaoObjetoCustoAtivoMaisUmMes(objetoCustoVersao)) {
				this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_EXCLUSAO_NAO_PERMITIDA_OBJETO_CUSTO_ATIVO");
			} else {
				exibeExclusaoVersaoObjCustoAtivo = true;
			}
		}
		
		
		if(exibeExclusaoVersaoObjCustoAtivo){
			this.openDialog("modalConfirmacaoExclusaoVersaoAtivaWG");
		}
	}

	private boolean verificaObjetoAtivoMes() {

		SigObjetoCustoVersoes objetoCustoVersao = custosSigFacade.obterObjetoCustoVersoes(seqObjetoCustoVersao);

		if (objetoCustoVersao.getDataInicio() != null) {
			Calendar dtIniVig = Calendar.getInstance();
			dtIniVig.setTime(objetoCustoVersao.getDataInicio());

			Calendar dataAtual = Calendar.getInstance();
			dataAtual.setTime(new Date());
			dataAtual.set(Calendar.HOUR_OF_DAY, 0);
			dataAtual.set(Calendar.MINUTE, 0);
			dataAtual.set(Calendar.SECOND, 0);
			dataAtual.set(Calendar.MILLISECOND, 0);

			if (objetoCustoVersao != null && objetoCustoVersao.getIndSituacao().equals(DominioSituacaoVersoesCustos.A)
					&& dtIniVig.getTime().before(dataAtual.getTime()) && dtIniVig.get(Calendar.MONTH) != dataAtual.get(Calendar.MONTH)) {
				return true;
			}
		}

		return false;
	}

	public void visualizarCentrosCusto(Integer seq) {
		this.setListaCentrosCusto(new ArrayList<FccCentroCustos>());
		if (seq != null) {
			SigObjetoCustoVersoes objetoCustoVersoes = custosSigFacade.obterObjetoCustoVersoes(seq);
			if (objetoCustoVersoes != null) {
				for (SigObjetoCustoCcts objetoCustoCcts : objetoCustoVersoes.getListObjetoCustoCcts()) {
					this.getListaCentrosCusto().add(objetoCustoCcts.getFccCentroCustos());
				}
			}
			this.openDialog("modalVisualizarCentrosCustoWG");
		}
	}

	// gets e sets
	public SigCentroProducao getSigCentroProducao() {
		return sigCentroProducao;
	}

	public void setSigCentroProducao(SigCentroProducao sigCentroProducao) {
		this.sigCentroProducao = sigCentroProducao;
	}

	public FccCentroCustos getFccCentroCustos() {
		return fccCentroCustos;
	}

	public void setFccCentroCustos(FccCentroCustos fccCentroCustos) {
		this.fccCentroCustos = fccCentroCustos;
	}

	public DominioSituacaoVersoesCustos getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacaoVersoesCustos situacao) {
		this.situacao = situacao;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Integer getSeqObjetoCustoVersao() {
		return seqObjetoCustoVersao;
	}

	public void setSeqObjetoCustoVersao(Integer seqObjetoCustoVersao) {
		this.seqObjetoCustoVersao = seqObjetoCustoVersao;
	}

	public DominioSimNao getPossuiComposicao() {
		return possuiComposicao;
	}

	public void setPossuiComposicao(DominioSimNao possuiComposicao) {
		this.possuiComposicao = possuiComposicao;
	}

	public boolean isExibeExclusaoVersaoObjCustoAtivo() {
		return exibeExclusaoVersaoObjCustoAtivo;
	}

	public void setExibeExclusaoVersaoObjCustoAtivo(boolean exibeExclusaoVersaoObjCustoAtivo) {
		this.exibeExclusaoVersaoObjCustoAtivo = exibeExclusaoVersaoObjCustoAtivo;
	}

	public String getMensagemNovaVersao() {
		String mensagemNovaVersao = "";
		if (this.seqObjetoCustoVersao != null && this.seqObjetoCustoVersao != 0) {
			SigObjetoCustoVersoes objetoCustoVersao = custosSigFacade.obterObjetoCustoVersoes(seqObjetoCustoVersao);
			mensagemNovaVersao = WebUtil.initLocalizedMessage("MENSAGEM_CONFIRMACAO_NOVA_VERSAO_ATIVA", null, objetoCustoVersao.getSigObjetoCustos().getNome());
		}
		
		return mensagemNovaVersao;
	}

	public boolean isConfirmarNovaVersao() {
		return confirmarNovaVersao;
	}

	public void setConfirmarNovaVersao(boolean confirmarNovaVersao) {
		this.confirmarNovaVersao = confirmarNovaVersao;
	}

	public DominioTipoObjetoCusto getTipoObjetoCusto() {
		return tipoObjetoCusto;
	}

	public void setTipoObjetoCusto(DominioTipoObjetoCusto tipoObjetoCusto) {
		this.tipoObjetoCusto = tipoObjetoCusto;
	}

	public List<FccCentroCustos> getListaCentrosCusto() {
		return listaCentrosCusto;
	}

	public void setListaCentrosCusto(List<FccCentroCustos> listaCentrosCusto) {
		this.listaCentrosCusto = listaCentrosCusto;
	}
	
	public DynamicDataModel<SigObjetoCustoVersoes> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<SigObjetoCustoVersoes> dataModel) {
	 this.dataModel = dataModel;
	}
	
	public void setAtivo(boolean ativo){
		this.dataModel.setPesquisaAtiva(ativo);
	}
	
	public boolean isAtivo(){
		return this.dataModel.getPesquisaAtiva();
	}
}
