package br.gov.mec.aghu.sig.custos.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SigAtividades;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.sig.custos.business.ICustosSigFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class ManterAtividadesPaginatorController extends ActionController implements ActionPaginator {

	private static final String VISUALIZAR_OBJETOS_CUSTO_ASSOCIADOS = "visualizarObjetosCustoAssociados";

	private static final String VISUALIZAR_HISTORICO_ATIVIDADE = "visualizarHistoricoAtividade";

	private static final String COPIAR_ATIVIDADES = "copiarAtividades";

	private static final String MANTER_ATIVIDADES = "manterAtividades";

	@Inject @Paginator
	private DynamicDataModel<SigAtividades> dataModel;
	
	private static final long serialVersionUID = -1502017164335662570L;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@EJB
	private ICustosSigFacade custosSigFacade;

	@EJB
	private ICentroCustoFacade centroCustoFacade;

	private SigAtividades atividade;
	private FccCentroCustos fccCentroCustos;
	private Integer seqAtividade;

	private boolean exibirBotaoNovo;

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void iniciar() {
	 

		if (atividade == null) {
			atividade = new SigAtividades();
			try {
				RapServidores servidor = registroColaboradorFacade.obterServidorAtivoPorUsuarioSemCache(this.obterLoginUsuarioLogado());
				if (servidor.getCentroCustoAtuacao() != null) {
					fccCentroCustos = servidor.getCentroCustoAtuacao();
				} else {
					fccCentroCustos = servidor.getCentroCustoLotacao();
				}
			} catch (ApplicationBusinessException e) {
				fccCentroCustos = null;
			}
			this.exibirBotaoNovo = false;
		}
	
	}

	public String verificaAtividadeEstaVinculadaAoObjetoCusto(SigAtividades atividade) {
		seqAtividade = atividade.getSeq();
		if (this.custosSigFacade.verificaAtividadeEstaVinculadaAoObjetoCusto(atividade)) {
			this.openDialog("modalConfirmacaoEdicaoWG");
			return null;
		}
		return MANTER_ATIVIDADES;
	}

	public String editar() {
		return MANTER_ATIVIDADES;

	}

	public String visualizar() {
		return MANTER_ATIVIDADES;
	}

	public String visualizarHistorico() {
		return VISUALIZAR_HISTORICO_ATIVIDADE;
	}

	public String visualizarObjetosCustoAssociados() {
		return VISUALIZAR_OBJETOS_CUSTO_ASSOCIADOS;
	}

	@Override
	public Long recuperarCount() {
		return custosSigFacade.pesquisarAtividadesCount(atividade, fccCentroCustos);
	}

	@Override
	public List<SigAtividades> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return custosSigFacade.pesquisarAtividades(firstResult, maxResult, SigAtividades.Fields.NOME.toString(), asc, atividade, fccCentroCustos);
	}

	public List<FccCentroCustos> pesquisarCentroCusto(String paramPesquisa) throws BaseException {
		List<FccCentroCustos> listaResultado = new ArrayList<FccCentroCustos>();
		listaResultado = this.centroCustoFacade.pesquisarCentroCustosPorCentroProdExcluindoGcc(paramPesquisa, null, null);
		return listaResultado;
	}
	
	public void reiniciarPaginator(){
		this.dataModel.reiniciarPaginator();
	}	

	public void limparCentroCusto() {
		this.setFccCentroCustos(null);
	}

	//Navegações

	public void pesquisarAtividade() {
		reiniciarPaginator();
		this.exibirBotaoNovo = true;
	}

	public void limparAtividade() {
		atividade = null;
		this.iniciar();
		this.setFccCentroCustos(null);
		//Apaga resultados da exibição
		setAtivo(false);
		this.exibirBotaoNovo = false;

	}

	public String cadastrarAtividade() {
		return MANTER_ATIVIDADES;
	}

	public String copiarAtividade() {
		if (this.getFccCentroCustos() == null) {
			this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ERRO_CENTRO_CUSTO_OBRIGATORIO");
			return null;
		} else {
			return COPIAR_ATIVIDADES;
		}
	}

	public void excluir() {
		try {
			SigAtividades sigAtividades = custosSigFacade.obterAtividade(this.getSeqAtividade());

			if (sigAtividades != null) {
				this.custosSigFacade.excluirAtividade(sigAtividades);
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUSAO_ATIVIDADE");
			} else {
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_ATIVIDADE_INEXISTENTE");
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		reiniciarPaginator();
	}

	//Getters and Setters

	public SigAtividades getAtividade() {
		return atividade;
	}

	public void setAtividade(SigAtividades atividade) {
		this.atividade = atividade;
	}

	public Integer getSeqAtividade() {
		return seqAtividade;
	}

	public void setSeqAtividade(Integer seqAtividade) {
		this.seqAtividade = seqAtividade;
	}

	public FccCentroCustos getFccCentroCustos() {
		return fccCentroCustos;
	}

	public void setFccCentroCustos(FccCentroCustos fccCentroCustos) {
		this.fccCentroCustos = fccCentroCustos;
	}

	public boolean isExibirBotaoNovo() {
		return exibirBotaoNovo;
	}

	public void setExibirBotaoNovo(boolean exibirBotaoNovo) {
		this.exibirBotaoNovo = exibirBotaoNovo;
	}

	public DynamicDataModel<SigAtividades> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<SigAtividades> dataModel) {
	 this.dataModel = dataModel;
	}
	
	public void setAtivo(boolean ativo){
		this.dataModel.setPesquisaAtiva(ativo);
	}
	
	public boolean isAtivo(){
		return this.dataModel.getPesquisaAtiva();
	}
}
