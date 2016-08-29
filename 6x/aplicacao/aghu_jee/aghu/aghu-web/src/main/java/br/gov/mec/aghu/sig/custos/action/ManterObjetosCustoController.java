package br.gov.mec.aghu.sig.custos.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoVersoesCustos;
import br.gov.mec.aghu.dominio.DominioTipoCentroProducaoCustos;
import br.gov.mec.aghu.dominio.DominioTipoObjetoCusto;
import br.gov.mec.aghu.dominio.DominioTipoObjetoCustoCcts;
import br.gov.mec.aghu.dominio.DominioTipoRateio;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SigCentroProducao;
import br.gov.mec.aghu.model.SigObjetoCustoCcts;
import br.gov.mec.aghu.model.SigObjetoCustoComposicoes;
import br.gov.mec.aghu.model.SigObjetoCustoVersoes;
import br.gov.mec.aghu.model.SigObjetoCustos;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.sig.custos.business.ICustosSigFacade;

public class ManterObjetosCustoController extends ActionController {
	
	private static final String COPIAR_COMPOSICAO_OBJETOS_CUSTO = "copiarComposicaoObjetosCusto";
	private static final String AJUSTAR_DIRECIONADORES = "ajustarDirecionadores";
	private static final String ASSOCIAR_CENTRO_CUSTO_COMPLEMENTAR = "associarCentroCustoComplementar";
	private static final String PESQUISAR_OBJETOS_CUSTO = "pesquisarObjetosCusto";
	private static final String PESQUISAR_ANALISE_OBJETOS_CUSTO = "pesquisarAnaliseObjetosCusto";
	private static final String VISUALIZAR_OBJETOS_CUSTO_ASSOCIADOS_CENTRO_CUSTO = "visualizarObjetosCustoAssociadosCentroCusto";
	private static final String PESO_OBJETO_CUSTO_CRUD = "pesoObjetoCustoCRUD";
	private static final String PROPAGAR_CLIENTES = "propagarClientes";
	private static final long serialVersionUID = 4047010379797954845L;

	@EJB
	private ICustosSigFacade custosSigFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@Inject
	private ManterObjetosCustoSliderPHIController manterObjetosCustoSliderPHIController;

	@Inject
	private ManterObjetosCustoSliderComposicaoController manterObjetosCustoSliderComposicaoController;

	@Inject
	private ManterObjetosCustoSliderDirecionadoresController manterObjetosCustoSliderDirecionadoresController;

	@Inject
	private ManterObjetosCustoSliderClientesController manterObjetosCustoSliderClientesController;

	private SigObjetoCustoVersoes objetoCustoVersao;
	private Integer seqObjetoCustoVersao;
	private SigCentroProducao sigCentroProducao;

	private FccCentroCustos fccCentroCustos;

	private SigObjetoCustoCcts objetoCustoCentroCustoAtual;

	private boolean possuiAlteracaoCampos;
	private boolean emEdicao;
	private boolean visualizar;
	private boolean visualizarSituacaoObjetoCusto;
	private boolean retornarPaginaPesoObjetoCustoCRUD;
	private boolean retornaPaginaConsultaObjetoCustoCC;
	private boolean retornaPaginaAnaliseObjetoCusto;

	private DominioSituacaoVersoesCustos situacaoAnterior;
	private boolean redirecionarRateioObjetoCusto;
	private Map<String, Object> clone;
	private boolean resolverPendencia;
	private boolean validaSemComposicao;
	private boolean naoRecarregar;

	private boolean novoCadastro;
	private boolean evitarExecucaoMetodoInicial;
	private List<SigObjetoCustoCcts> listaObjetoCustoCcts;
	private List<SigObjetoCustoCcts> listaObjetoCustoCctsExclusao;
	private DominioSimNao indCompartilha;
	private String activeIndex;
	

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void iniciar() {
	 

		if (this.naoRecarregar) {
			return;
		}
		if (!this.evitarExecucaoMetodoInicial) {
			this.setObjetoCustoCentroCustoAtual(null);
			this.setPossuiAlteracaoCampos(false);
			if (this.seqObjetoCustoVersao != null) {
				this.inicializarValoresAlteracao();
			} else {
				this.inicializarValoresInclusao();
			}
		} else {
			this.evitarExecucaoMetodoInicial = false;
		}
		this.activeIndex= null;
	}

	private void inicializarValoresInclusao() {
		this.objetoCustoVersao = new SigObjetoCustoVersoes();
		this.objetoCustoVersao.setSigObjetoCustos(new SigObjetoCustos());
		this.objetoCustoVersao.setNroVersao(1);
		this.objetoCustoVersao.setIndSituacao(DominioSituacaoVersoesCustos.E);
		this.objetoCustoVersao.getSigObjetoCustos().setIndTipo(DominioTipoObjetoCusto.AP);
		this.fccCentroCustos = null;
		this.indCompartilha = null;
		if (this.getFccCentroCustos() == null) {
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
		this.inicializarListasObjetosCustoCcts();
		this.manterObjetosCustoSliderPHIController.inicializarPhi(null);
		this.manterObjetosCustoSliderComposicaoController.iniciaSliderComposicoes(null);
		this.manterObjetosCustoSliderDirecionadoresController.inicializarSliderDirecionadorRateio(null);
		this.manterObjetosCustoSliderClientesController.inicializarClientes(null);
		this.setEmEdicao(true);
		this.setSituacaoAnterior(DominioSituacaoVersoesCustos.E);
		this.setNovoCadastro(true);
	}

	private void inicializarValoresAlteracao() {
		this.clone = this.custosSigFacade.obterObjetoCustoVersoesDesatachado(this.seqObjetoCustoVersao);
		this.fccCentroCustos = null;
		this.objetoCustoVersao = this.custosSigFacade.obterObjetoCustoVersoes(this.seqObjetoCustoVersao);
		SigObjetoCustoCcts sigObjetoCustoCctsPrincipal = this.custosSigFacade.obterObjetoCustoCctsPrincipal(this.seqObjetoCustoVersao);
		if (sigObjetoCustoCctsPrincipal != null && sigObjetoCustoCctsPrincipal.getFccCentroCustos().getCodigo() != null) {
			this.setFccCentroCustos(sigObjetoCustoCctsPrincipal.getFccCentroCustos());
		}
		this.inicializarListasObjetosCustoCcts();
		this.manterObjetosCustoSliderPHIController.inicializarPhi(this.objetoCustoVersao.getSeq());
		this.manterObjetosCustoSliderComposicaoController.iniciaSliderComposicoes(this.objetoCustoVersao.getSeq());
		this.manterObjetosCustoSliderDirecionadoresController.inicializarSliderDirecionadorRateio(this.objetoCustoVersao.getSeq());
		this.manterObjetosCustoSliderClientesController.inicializarClientes(this.objetoCustoVersao.getSeq());
		this.setEmEdicao(true);
		this.setSituacaoAnterior(this.objetoCustoVersao.getIndSituacao());
		this.setNovoCadastro(false);
		this.inicializarIndicadorCompartilhamento();

		if (this.objetoCustoVersao.getSigObjetoCustoCctsPrincipal() != null
				&& this.objetoCustoVersao.getSigObjetoCustoCctsPrincipal().getFccCentroCustos() != null
				&& this.objetoCustoVersao.getSigObjetoCustoCctsPrincipal().getFccCentroCustos().getCodigo() != null) {
			this.setObjetoCustoCentroCustoAtual(this.objetoCustoVersao.getSigObjetoCustoCctsPrincipal());
		} else {
			this.objetoCustoCentroCustoAtual = null;
		}
	}

	private void inicializarIndicadorCompartilhamento() {
		if (this.getObjetoCustoVersao().getSigObjetoCustos().getIndCompartilha()) {
			this.setIndCompartilha(DominioSimNao.S);
		} else {
			this.setIndCompartilha(DominioSimNao.N);
		}
	}

	private void inicializarListasObjetosCustoCcts() {
		this.setListaObjetoCustoCcts(new ArrayList<SigObjetoCustoCcts>(this.objetoCustoVersao.getListObjetoCustoCcts()));
		this.setListaObjetoCustoCctsExclusao(new ArrayList<SigObjetoCustoCcts>());
	}

	public List<FccCentroCustos> pesquisarCentroCusto(String paramPesquisa) throws BaseException {

		Integer codigoCentroProducao = null;
		if (this.getSigCentroProducao() != null) {
			codigoCentroProducao = this.getSigCentroProducao().getSeq();
		}

		switch (this.getObjetoCustoVersao().getSigObjetoCustos().getIndTipo()) {
		case AP:
			return custosSigFacade.pesquisarCentroCustoSemObrasPeloTipoCentroProducao(paramPesquisa, codigoCentroProducao, DominioSituacao.A,
					DominioTipoCentroProducaoCustos.A);
		case AS:
			return custosSigFacade.pesquisarCentroCustoSemObrasPeloTipoCentroProducao(paramPesquisa, codigoCentroProducao, DominioSituacao.A,
					DominioTipoCentroProducaoCustos.I, DominioTipoCentroProducaoCustos.F);
		default:
			return null;
		}
	}

	public void gravar(Boolean inativa) {
		
		try {
			if (this.indCompartilha != null) {
				this.objetoCustoVersao.getSigObjetoCustos().setIndCompartilha(this.indCompartilha.isSim());
			}
			
			Boolean criacao = objetoCustoVersao.getSeq() == null;
			if (this.isObjetoCustoAssistencial()) {
				this.gravarObjetoCustoAssistencial(inativa);
			} else {
				this.gravarObjetoCustoApoio(inativa);
			}
			inicializarValoresAlteracao();
			if(criacao){
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_INC_OBJETO_CUSTO", objetoCustoVersao.getSigObjetoCustos().getNome());
			}
			else{
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ALTERACAO_OBJETO_CUSTO", objetoCustoVersao.getSigObjetoCustos().getNome());
			}
		} catch (ApplicationBusinessException e) {
			this.apresentarExcecaoNegocio(e);
		}
	}

	private boolean isCentroCustoDuplicadoApoio(List<FccCentroCustos> listaCentroCustos) {
		Set<FccCentroCustos> listaSemCentroCustoRepetidos = new HashSet<FccCentroCustos>(listaCentroCustos);
		if (listaSemCentroCustoRepetidos.size() == listaCentroCustos.size()) {
			return false;
		} else {
			return true;
		}
	}

	private void gravarObjetoCustoApoio(boolean inativa) throws ApplicationBusinessException {
		List<FccCentroCustos> listaCentroCustos = new ArrayList<FccCentroCustos>(this.getListaObjetoCustoCcts().size());

		for (SigObjetoCustoCcts sigObjetoCustoCcts : this.getListaObjetoCustoCcts()) {
			if (sigObjetoCustoCcts.getIndTipo().equals(DominioTipoObjetoCustoCcts.P)
					&& sigObjetoCustoCcts.getFccCentroCustos().getCodigo().intValue() != fccCentroCustos.getCodigo().intValue()) {
				listaCentroCustos.add(fccCentroCustos);
			} else {
				listaCentroCustos.add(sigObjetoCustoCcts.getFccCentroCustos());
			}
		}

		if (this.isCentroCustoDuplicadoApoio(listaCentroCustos)) {
			this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ERRO_CENTO_CUSTO_DUPLICADO");
			return;
		}
		Object[] retorno = this.custosSigFacade.validarAtividadePertenceAoCentroCustoComposicaoApoio(
				this.manterObjetosCustoSliderComposicaoController.getListaObjetoCustoComposicoes(), listaCentroCustos);
		boolean isAtividadeForaCC = (Boolean) retorno[0];
		if (!isAtividadeForaCC) {
			this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ERRO_ATIVIDADES_NAO_PERTENCEM_AO_OBJETO_CUSTO", (String) retorno[1]);
			return;
		}
		
		this.custosSigFacade.validarSomaPercentuaisDirecionadoresRateio(this.getObjetoCustoVersao().getIndSituacao(),
				this.manterObjetosCustoSliderDirecionadoresController.getListaObjetoCustoDirRateios());
		
		if (inativa && objetoCustoVersao.getIndSituacao().equals(DominioSituacaoVersoesCustos.A)) {
			if (objetoCustoVersao.getSigObjetoCustos() != null && objetoCustoVersao.getSigObjetoCustos().getSeq() != null && verificaVersoesAtivas()) {
				this.openDialog("modalConfirmacaoVersaoAnteriorAtivaWG");
				return;
			}
		}
		this.validarObjetoCusto();

		if (this.objetoCustoVersao.getSeq() == null) {
			this.inclusaoObjetoCusto();
		} else {
			this.alteracaoObjetoCusto();
			this.verificaInativacaoVersaoAtual();
		}

		this.manterObjetosCustoSliderComposicaoController.gravarComposicaoBanco();
		this.manterObjetosCustoSliderDirecionadoresController.gravarDirecionadorRateioBanco();
		this.manterObjetosCustoSliderClientesController.gravarClientesBanco();
		this.setSituacaoAnterior(objetoCustoVersao.getIndSituacao());
		this.setPossuiAlteracaoCampos(false);
		this.setNovoCadastro(false);
		if (this.getSeqObjetoCustoVersao() == null) {
			this.setSeqObjetoCustoVersao(objetoCustoVersao.getSeq());
			inicializarValoresAlteracao();
		} else {
			this.manterObjetosCustoSliderComposicaoController.iniciaSliderComposicoes(objetoCustoVersao.getSeq());
			this.clone = this.custosSigFacade.obterObjetoCustoVersoesDesatachado(this.seqObjetoCustoVersao);
		}
	}

	private void gravarObjetoCustoAssistencial(boolean inativa) throws ApplicationBusinessException {
	
		if (!this.custosSigFacade.validarCentroCustoComposicaoAssistencial(
				this.manterObjetosCustoSliderComposicaoController.getListaObjetoCustoComposicoes(), this.getFccCentroCustos())) {
			throw new ApplicationBusinessException("MENSAGEM_ERRO_COMPOSICAO_CENTRO_CUSTO", Severity.ERROR);
		}
		
		if (inativa && this.objetoCustoVersao.getIndSituacao().equals(DominioSituacaoVersoesCustos.A)) {
			if (this.objetoCustoVersao.getSigObjetoCustos() != null && this.objetoCustoVersao.getSigObjetoCustos().getSeq() != null
					&& this.verificaVersoesAtivas()) {
				this.openDialog("modalConfirmacaoVersaoAnteriorAtivaWG");
				return;
			}
		}
		this.validarObjetoCusto();

		if (this.objetoCustoVersao.getSeq() == null) {
			this.inclusaoObjetoCusto();
		} else {
			this.alteracaoObjetoCusto();
			this.verificaInativacaoVersaoAtual();
		}
		this.manterObjetosCustoSliderPHIController.gravarPhis();
		this.manterObjetosCustoSliderComposicaoController.gravarComposicaoBanco();
		this.setSituacaoAnterior(objetoCustoVersao.getIndSituacao());
		this.setPossuiAlteracaoCampos(false);
		this.setNovoCadastro(false);
		if (this.getSeqObjetoCustoVersao() == null) {
			this.setSeqObjetoCustoVersao(objetoCustoVersao.getSeq());
			this.inicializarValoresAlteracao();
		} else {
			this.manterObjetosCustoSliderComposicaoController.iniciaSliderComposicoes(objetoCustoVersao.getSeq());
			this.clone = this.custosSigFacade.obterObjetoCustoVersoesDesatachado(this.seqObjetoCustoVersao);
		}
	}

	private void verificaInativacaoVersaoAtual() {
		this.redirecionarRateioObjetoCusto = false;
		if (this.getSituacaoAnterior() != DominioSituacaoVersoesCustos.A && this.objetoCustoVersao.getIndSituacao() == DominioSituacaoVersoesCustos.A) {
			if (this.fccCentroCustos != null && this.fccCentroCustos.getSigParamCentroCusto() != null) {
				if (this.fccCentroCustos.getSigParamCentroCusto().getTipoRateio() == DominioTipoRateio.P) {
					this.redirecionarRateioObjetoCusto = true;
				}
			}
		}
	}

	private boolean verificaVersoesAtivas() {
		return this.custosSigFacade.verificaVersoesAtivas(objetoCustoVersao);
	}

	public void inativarVersaoAnterior() {
		try {
			custosSigFacade.inativarVersaoAnterior(objetoCustoVersao);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		this.gravar(false);
	}

	private void validarObjetoCusto() throws ApplicationBusinessException {
		this.custosSigFacade.validarObjetoCusto(objetoCustoVersao, getSituacaoAnterior());
	}

	private void inclusaoObjetoCusto() throws ApplicationBusinessException, ApplicationBusinessException {
		this.objetoCustoVersao.getSigObjetoCustos().setCriadoEm(new Date());
		this.objetoCustoVersao.getSigObjetoCustos().setRapServidores(registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado()));
		this.custosSigFacade.gravarObjetoCustos(objetoCustoVersao.getSigObjetoCustos());

		this.objetoCustoVersao.setCriadoEm(new Date());
		this.objetoCustoVersao.setRapServidores(registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado()));
		this.custosSigFacade.gravarObjetoCustoVersoes(objetoCustoVersao, this.manterObjetosCustoSliderClientesController.getListaClientes(),
				this.manterObjetosCustoSliderDirecionadoresController.getListaObjetoCustoDirRateios());

		if (this.fccCentroCustos != null) {
			this.gravarObjetoCustoCentroCusto();
		}
	}

	private void gravarObjetoCustoCentroCusto() throws ApplicationBusinessException {
		SigObjetoCustoCcts sigObjetoCustoCcts = new SigObjetoCustoCcts();
		sigObjetoCustoCcts.setRapServidores(this.registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado()));
		sigObjetoCustoCcts.setSigObjetoCustoVersoes(this.getObjetoCustoVersao());
		sigObjetoCustoCcts.setFccCentroCustos(this.fccCentroCustos);
		this.custosSigFacade.gravarObjetoCustoCentroCusto(sigObjetoCustoCcts);
	}

	private void alteracaoObjetoCusto() throws ApplicationBusinessException, ApplicationBusinessException {
		if (this.isObjetoCustoAssistencial()) {
			if (this.getFccCentroCustos() != null && this.getFccCentroCustos().getCodigo() != null) {
				if (this.objetoCustoCentroCustoAtual == null || this.objetoCustoCentroCustoAtual.getFccCentroCustos() == null
						|| this.objetoCustoCentroCustoAtual.getFccCentroCustos().getCodigo() == null) {
					this.gravarObjetoCustoCentroCusto();
				} else if (!this.getFccCentroCustos().getCodigo().equals(this.objetoCustoCentroCustoAtual.getFccCentroCustos().getCodigo())) {
					this.custosSigFacade.removerObjetosCustoCentroCustos(this.objetoCustoCentroCustoAtual);
					this.gravarObjetoCustoCentroCusto();
				} else {
					this.objetoCustoVersao.getSigObjetoCustoCctsPrincipal().setRapServidores(
							this.registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado()));
					this.objetoCustoVersao.getSigObjetoCustoCctsPrincipal().setSigObjetoCustoVersoes(this.getObjetoCustoVersao());
					this.custosSigFacade.alterarObjetoCustoCentroCusto(this.objetoCustoVersao.getSigObjetoCustoCctsPrincipal());
				}
			} else {
				if (this.objetoCustoCentroCustoAtual != null && this.objetoCustoCentroCustoAtual.getFccCentroCustos() != null
						&& this.objetoCustoCentroCustoAtual.getFccCentroCustos().getCodigo() != null) {
					this.custosSigFacade.removerObjetosCustoCentroCustos(this.objetoCustoCentroCustoAtual);
					this.objetoCustoVersao.getListObjetoCustoCcts().clear();
				}
				this.fccCentroCustos = null;
				this.objetoCustoCentroCustoAtual = null;
			}
		} else {

			this.custosSigFacade.persistirListaObjetoCustoCentroCusto(objetoCustoVersao, listaObjetoCustoCcts,
					listaObjetoCustoCctsExclusao);
		}

		this.objetoCustoVersao.getSigObjetoCustos().setRapServidores(registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado()));
		this.custosSigFacade.alterarObjetoCustos(this.objetoCustoVersao.getSigObjetoCustos());
		this.objetoCustoVersao.setRapServidores(registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado()));
		this.custosSigFacade.alterarObjetoCustoVersoes(this.objetoCustoVersao, this.manterObjetosCustoSliderClientesController.getListaClientes(),
				this.manterObjetosCustoSliderDirecionadoresController.getListaObjetoCustoDirRateios());

		this.custosSigFacade.iniciaProcessoHistoricoProduto(this.objetoCustoVersao, this.clone,
				this.manterObjetosCustoSliderComposicaoController.getListaObjetoCustoComposicoes(), this.manterObjetosCustoSliderPHIController.getListaPhis(),
				this.manterObjetosCustoSliderPHIController.getListaPhisExcluir(),
				registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado()));
	}

	public void alterarTipoObjetoCusto(){
		if (this.isObjetoCustoAssistencial()) {
			this.getObjetoCustoVersao().setIndRepasse(null);
		}
		
		this.changeField();
	}
	
	public void alterarCentroCusto() {

		if (!this.isObjetoCustoAssistencial()) {
			this.getObjetoCustoVersao().getSigObjetoCustoCctsPrincipal().setFccCentroCustos(this.getFccCentroCustos());
		}

		this.changeField();
	}

	public void changeField() {
		if (!isObjetoCustoAssistencial()) {
			for (SigObjetoCustoCcts sigObjetoCustoCcts : this.getListaObjetoCustoCcts()) {
				if (sigObjetoCustoCcts.getIndTipo().equals(DominioTipoObjetoCustoCcts.P)) {
					sigObjetoCustoCcts.setFccCentroCustos(fccCentroCustos);
					break;
				}
			}
		}
		this.setPossuiAlteracaoCampos(true);
	}

	public void verificarAlteracaoCentroCusto() {
		if (this.getFccCentroCustos() != null) {
			for (SigObjetoCustoCcts objetoCustoCcts : this.getObjetoCustoVersao().getListObjetoCustoCcts()) {
				if (objetoCustoCcts.getFccCentroCustos().getCodigo().equals(this.getFccCentroCustos().getCodigo())) {
					if (!objetoCustoCcts.getIndTipo().equals(this.objetoCustoVersao.getSigObjetoCustoCctsPrincipal().getIndTipo())) {
						this.apresentarMsgNegocio(Severity.INFO, "CENTRO_CUSTO_CADASTRADO_COMO_COMPLEMENTAR",
								this.getFccCentroCustos().getDescricao());
						this.setFccCentroCustos(null);
					}
				}
			}
		}
	}

	public void gravarTrue() {
		this.gravar(true);
		this.openDialog("modalRedirecionarRateioObjetoCustoWG");
	}

	public void preGravar() {
		if (this.validaSemComposicaoObjetoCusto()) {
			this.validaSemComposicao = true;
			this.openDialog("modalValidaObjetoCustoSemComposicaoWG");
		} else {
			this.validaSemComposicao = false;
			this.gravar(true);
		}
	}

	public Boolean validaSemComposicaoObjetoCusto() {
		if (this.objetoCustoVersao.getIndSituacao().equals(DominioSituacaoVersoesCustos.A)) {
			if (this.manterObjetosCustoSliderComposicaoController.getListaObjetoCustoComposicoes() == null
					|| this.manterObjetosCustoSliderComposicaoController.getListaObjetoCustoComposicoes().isEmpty()) {
				return true;
			} else {
				boolean aux = true;
				for (SigObjetoCustoComposicoes sigObjetoCustoComposicoes : this.manterObjetosCustoSliderComposicaoController.getListaObjetoCustoComposicoes()) {
					if (sigObjetoCustoComposicoes.getIndSituacao() == DominioSituacao.A) {
						aux = false;
					}
				}
				if (aux) {
					return true;
				}
			}
		}
		return false;
	}

	public String verificaAlteracaoNaoSalva() {
		if (this.manterObjetosCustoSliderPHIController.isPossuiAlteracaoPhi()
				|| this.manterObjetosCustoSliderComposicaoController.isPossuiAlteracaoComposicao()
				|| this.manterObjetosCustoSliderDirecionadoresController.isPossuiAlteracaoDirecionadorRateio()
				|| this.manterObjetosCustoSliderClientesController.isPossuiAlteracaoCliente() || this.isPossuiAlteracaoCampos()) {

			this.seqObjetoCustoVersao = null;
			this.openDialog("modalConfirmacaoVoltarWG");
			return null;
		} else {
			this.seqObjetoCustoVersao = null;
			return this.voltar();// return RETORNA_TELA_PESQUISA;
		}
	}

	public String iniciarCopia() {
		return COPIAR_COMPOSICAO_OBJETOS_CUSTO;
	}

	public String redirecionarRateio() {
		return PESO_OBJETO_CUSTO_CRUD;
	}

	public String voltar() {
		if(redirecionarRateioObjetoCusto){
			this.openDialog("modalRedirecionarRateioObjetoCustoWG");
		}
		
		if (this.retornarPaginaPesoObjetoCustoCRUD) {
			return PESO_OBJETO_CUSTO_CRUD;
		} else if (this.retornaPaginaConsultaObjetoCustoCC) {
			return VISUALIZAR_OBJETOS_CUSTO_ASSOCIADOS_CENTRO_CUSTO;
		} else if (this.retornaPaginaAnaliseObjetoCusto) {
			return PESQUISAR_ANALISE_OBJETOS_CUSTO;
		} else {
			return PESQUISAR_OBJETOS_CUSTO;
		}
	}

	public List<DominioSituacaoVersoesCustos> listarSituacaoObjCusto() {
		if(objetoCustoVersao != null){
			List<DominioSituacaoVersoesCustos> lista = this.custosSigFacade.selecionaSituacaoPossivelDoObjetoCusto(getSituacaoAnterior(), objetoCustoVersao);
			this.setVisualizarSituacaoObjetoCusto(lista.get(0).equals(DominioSituacaoVersoesCustos.I));
			return lista;
		}
		return null;
	}

	public String associarCentrosCustoComplementares() {
		return ASSOCIAR_CENTRO_CUSTO_COMPLEMENTAR;
	}

	public String ajustarDirecionadores() {
		return AJUSTAR_DIRECIONADORES;
	}

	public String propagarClientes() {
		return PROPAGAR_CLIENTES;
	}

	public boolean isObjetoCustoAssistencial() {
		if (this.objetoCustoVersao != null && this.objetoCustoVersao.getSigObjetoCustos() != null
				&& this.objetoCustoVersao.getSigObjetoCustos().getIndTipo().equals(DominioTipoObjetoCusto.AS)) {
			return true;
		}
		return false;
	}

	public boolean isExibeSlidersTipoAssistencial() {
		if (this.objetoCustoVersao != null && this.objetoCustoVersao.getSigObjetoCustos() != null
				&& this.objetoCustoVersao.getSigObjetoCustos().getIndTipo().equals(DominioTipoObjetoCusto.AS)) {
			return true;
		}
		return false;
	}

	public Integer getSeqObjetoCustoVersao() {
		return seqObjetoCustoVersao;
	}

	public void setSeqObjetoCustoVersao(Integer seqObjetoCustoVersao) {
		this.seqObjetoCustoVersao = seqObjetoCustoVersao;
	}

	public SigObjetoCustoVersoes getObjetoCustoVersao() {
		return objetoCustoVersao;
	}

	public void setObjetoCustoVersao(SigObjetoCustoVersoes objetoCustoVersao) {
		this.objetoCustoVersao = objetoCustoVersao;
	}

	public SigCentroProducao getSigCentroProducao() {
		return sigCentroProducao;
	}

	public void setSigCentroProducao(SigCentroProducao sigCentroProducao) {
		this.sigCentroProducao = sigCentroProducao;
	}

	public boolean isEmEdicao() {
		return emEdicao;
	}

	public void setEmEdicao(boolean emEdicao) {
		this.emEdicao = emEdicao;
	}

	public boolean isVisualizar() {
		return visualizar;
	}

	public void setVisualizar(boolean visualizar) {
		this.visualizar = visualizar;
	}

	public DominioSituacaoVersoesCustos getSituacaoAnterior() {
		return situacaoAnterior;
	}

	public void setSituacaoAnterior(DominioSituacaoVersoesCustos situacaoAnterior) {
		this.situacaoAnterior = situacaoAnterior;
	}

	public void setPossuiAlteracaoCampos(boolean possuiAlteracaoCampos) {
		this.possuiAlteracaoCampos = possuiAlteracaoCampos;
	}

	public boolean isPossuiAlteracaoCampos() {
		return possuiAlteracaoCampos;
	}

	public boolean isRetornarPaginaPesoObjetoCustoCRUD() {
		return retornarPaginaPesoObjetoCustoCRUD;
	}

	public void setRetornarPaginaPesoObjetoCustoCRUD(boolean retornarPaginaPesoObjetoCustoCRUD) {
		this.retornarPaginaPesoObjetoCustoCRUD = retornarPaginaPesoObjetoCustoCRUD;
	}

	public Map<String, Object> getClone() {
		return clone;
	}

	public void setClone(Map<String, Object> clone) {
		this.clone = clone;
	}

	public boolean isRedirecionarRateioObjetoCusto() {
		return redirecionarRateioObjetoCusto;
	}

	public void setRedirecionarRateioObjetoCusto(boolean redirecionarRateioObjetoCusto) {
		this.redirecionarRateioObjetoCusto = redirecionarRateioObjetoCusto;
	}

	public boolean isResolverPendencia() {
		return resolverPendencia;
	}

	public void setResolverPendencia(boolean resolverPendencia) {
		this.resolverPendencia = resolverPendencia;
	}

	public boolean isNovoCadastro() {
		return novoCadastro;
	}

	public void setNovoCadastro(boolean novoCadastro) {
		this.novoCadastro = novoCadastro;
	}

	public void setEvitarExecucaoMetodoInicial(boolean evitarExecucaoMetodoInicial) {
		this.evitarExecucaoMetodoInicial = evitarExecucaoMetodoInicial;
	}

	public List<SigObjetoCustoCcts> getListaObjetoCustoCcts() {
		return listaObjetoCustoCcts;
	}

	public void setListaObjetoCustoCcts(List<SigObjetoCustoCcts> listaObjetoCustoCcts) {
		this.listaObjetoCustoCcts = listaObjetoCustoCcts;
	}

	public List<SigObjetoCustoCcts> getListaObjetoCustoCctsExclusao() {
		return listaObjetoCustoCctsExclusao;
	}

	public void setListaObjetoCustoCctsExclusao(List<SigObjetoCustoCcts> listaObjetoCustoCctsExclusao) {
		this.listaObjetoCustoCctsExclusao = listaObjetoCustoCctsExclusao;
	}

	public SigObjetoCustoCcts getObjetoCustoCentroCustoAtual() {
		return objetoCustoCentroCustoAtual;
	}

	public void setObjetoCustoCentroCustoAtual(SigObjetoCustoCcts objetoCustoCentroCustoAtual) {
		this.objetoCustoCentroCustoAtual = objetoCustoCentroCustoAtual;
	}

	public FccCentroCustos getFccCentroCustos() {
		return fccCentroCustos;
	}

	public void setFccCentroCustos(FccCentroCustos fccCentroCustos) {
		this.fccCentroCustos = fccCentroCustos;
	}

	public boolean isValidaSemComposicao() {
		return validaSemComposicao;
	}

	public void setValidaSemComposicao(boolean validaSemComposicao) {
		this.validaSemComposicao = validaSemComposicao;
	}

	public boolean isNaoRecarregar() {
		return naoRecarregar;
	}

	public void setNaoRecarregar(boolean naoRecarregar) {
		this.naoRecarregar = naoRecarregar;
	}

	public boolean isRetornaPaginaConsultaObjetoCustoCC() {
		return retornaPaginaConsultaObjetoCustoCC;
	}

	public void setRetornaPaginaConsultaObjetoCustoCC(boolean retornaPaginaConsultaObjetoCustoCC) {
		this.retornaPaginaConsultaObjetoCustoCC = retornaPaginaConsultaObjetoCustoCC;
	}

	public DominioSimNao getIndCompartilha() {
		return indCompartilha;
	}

	public void setIndCompartilha(DominioSimNao indCompartilha) {
		this.indCompartilha = indCompartilha;
	}

	public boolean isRetornaPaginaAnaliseObjetoCusto() {
		return retornaPaginaAnaliseObjetoCusto;
	}

	public void setRetornaPaginaAnaliseObjetoCusto(boolean retornaPaginaAnaliseObjetoCusto) {
		this.retornaPaginaAnaliseObjetoCusto = retornaPaginaAnaliseObjetoCusto;
	}

	public boolean isVisualizarSituacaoObjetoCusto() {
		return visualizarSituacaoObjetoCusto;
	}

	public void setVisualizarSituacaoObjetoCusto(boolean visualizarSituacaoObjetoCusto) {
		this.visualizarSituacaoObjetoCusto = visualizarSituacaoObjetoCusto;
	}

	public String getActiveIndex() {
		return activeIndex;
	}

	public void setActiveIndex(String activeIndex) {
		this.activeIndex = activeIndex;
	}

}