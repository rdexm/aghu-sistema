package br.gov.mec.aghu.sig.custos.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.SigAtividades;
import br.gov.mec.aghu.model.SigDirecionadores;
import br.gov.mec.aghu.sig.custos.business.ICustosSigCadastrosBasicosFacade;

public class ManterDirecionadorAtividadePaginatorController extends ActionController implements ActionPaginator {
	
	private static final String MANTER_DIRECIONADOR_ATIVIDADE = "manterDirecionadorAtividade";

	@Inject @Paginator
	private DynamicDataModel<SigAtividades> dataModel;

	private static final Log LOG = LogFactory.getLog(ManterDirecionadorAtividadePaginatorController.class);

	private static final long serialVersionUID = -1502017164335662570L;

	@EJB
	private ICustosSigCadastrosBasicosFacade custosSigCadastrosBasicosFacade;
	
	private SigDirecionadores parametroSelecionado;

	private SigDirecionadores direcionador;
	private Integer seqDirecionador;

	private boolean exibirBotaoNovo;
	private boolean recarregarLista = false;
	private DominioSimNao indColetaSistema;

	
	@PostConstruct
	protected void inicializar(){
		LOG.debug("begin conversation");
		this.begin(conversation);
	}
	
	public void iniciar() {
		
		if (direcionador == null) {
			direcionador = new SigDirecionadores();
			direcionador.setIndSituacao(DominioSituacao.A);
			this.setExibirBotaoNovo(false);
		}
		if (this.isRecarregarLista()) {
			this.reiniciarPaginator();
			this.setRecarregarLista(false);
		}
	
	}

	public void pesquisarDirecionadorAtividade() {
		this.direcionador.setIndColetaSistema(DominioSimNao.getBooleanInstance(this.indColetaSistema));
		reiniciarPaginator();
		this.setExibirBotaoNovo(true);
	}

	public void limparDirecionadorAtividade() {
		this.direcionador = null;
		this.indColetaSistema = null;
		this.iniciar();
		this.setAtivo(false);
		this.setExibirBotaoNovo(false);
	}

	public String editarDirecionadorAtividade() {
		return MANTER_DIRECIONADOR_ATIVIDADE;
	}

	public String cadastrarDirecionadorAtividade() {
		return MANTER_DIRECIONADOR_ATIVIDADE;
	}

	@Override
	public Long recuperarCount() {
		return this.custosSigCadastrosBasicosFacade.pesquisarDirecionadorAtividadeCount(direcionador);
	}

	@Override
	public List<SigAtividades> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		List<SigAtividades> result = this.custosSigCadastrosBasicosFacade.pesquisarDirecionadorAtividade(firstResult, maxResult,
				SigDirecionadores.Fields.NOME.toString(), asc, direcionador);
		if (result == null) {
			result = new ArrayList<SigAtividades>();
		}
		return result;
	}
	
	public void reiniciarPaginator(){
		this.dataModel.reiniciarPaginator();
	}

	// Getters and Setters

	public SigDirecionadores getDirecionador() {
		return direcionador;
	}

	public void setDirecionador(SigDirecionadores direcionador) {
		this.direcionador = direcionador;
	}

	public boolean isExibirBotaoNovo() {
		return exibirBotaoNovo;
	}

	public void setExibirBotaoNovo(boolean exibirBotaoNovo) {
		this.exibirBotaoNovo = exibirBotaoNovo;
	}

	public Integer getSeqDirecionador() {
		return seqDirecionador;
	}

	public void setSeqDirecionador(Integer seqDirecionador) {
		this.seqDirecionador = seqDirecionador;
	}

	public boolean isRecarregarLista() {
		return recarregarLista;
	}

	public void setRecarregarLista(boolean recarregarLista) {
		this.recarregarLista = recarregarLista;
	}

	public DynamicDataModel<SigAtividades> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<SigAtividades> dataModel) {
	 this.dataModel = dataModel;
	}
	
	public void setAtivo(Boolean ativo){
		this.dataModel.setPesquisaAtiva(ativo);
	}
	
	public Boolean getAtivo(){
		return this.dataModel.getPesquisaAtiva();
	}

	public SigDirecionadores getParametroSelecionado() {
		return parametroSelecionado;
	}

	public void setParametroSelecionado(SigDirecionadores parametroSelecionado) {
		this.parametroSelecionado = parametroSelecionado;
	}

	public DominioSimNao getIndColetaSistema() {
		return indColetaSistema;
	}

	public void setIndColetaSistema(DominioSimNao indColetaSistema) {
		this.indColetaSistema = indColetaSistema;
	}
}
