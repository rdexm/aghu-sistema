package br.gov.mec.aghu.sig.custos.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.SigCentroProducao;
import br.gov.mec.aghu.model.SigDirecionadores;
import br.gov.mec.aghu.model.SigObjetoCustoClientes;
import br.gov.mec.aghu.model.SigObjetoCustoDirRateios;
import br.gov.mec.aghu.model.SigObjetoCustoVersoes;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.sig.custos.business.ICustosSigCadastrosBasicosFacade;
import br.gov.mec.aghu.sig.custos.business.ICustosSigFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class ManterObjetosCustoSliderClientesController extends ActionController {

	private static final Log LOG = LogFactory.getLog(ManterObjetosCustoSliderClientesController.class);
	
	private static final long serialVersionUID = -6156965692445904837L;

	@EJB
	private ICustosSigFacade custosSigFacade;
	
	@EJB
	private ICentroCustoFacade centroCustoFacade;
	
	@EJB
	private ICustosSigCadastrosBasicosFacade custosSigCadastrosBasicosFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@Inject
	private ManterObjetosCustoController manterObjetosCustoController;

	@Inject
	private ManterObjetosCustoSliderDirecionadoresController manterObjetosCustoSliderDirecionadoresController;

	private SigObjetoCustoClientes sigObjetoCustoClientes;
	private String itemSelecionadoClientes = "0"; // radio
	private Boolean propagarClientes = false; // checkbox

	private boolean possuiAlteracaoCliente;
	private Integer seqClienteExclusao;
	private boolean edicaoCliente;
	private Integer indexOfCliente;

	private List<SigObjetoCustoClientes> listaClientes;
	private List<SigObjetoCustoClientes> listaClientesExcluir;
	
	private Map<SigObjetoCustoClientes, Boolean> objetoCustoClienteSelecionados;
	private Boolean valorAlternarTodos;
	private Boolean marcouTodos;

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void inicializarClientes(Integer seqObjetoCustoVersao) {
		this.setSigObjetoCustoClientes(new SigObjetoCustoClientes());
		this.getSigObjetoCustoClientes().setSituacao(DominioSituacao.A);
		this.getSigObjetoCustoClientes().setIndTodosCct(Boolean.FALSE);
		this.setListaClientes(new ArrayList<SigObjetoCustoClientes>());
		this.setListaClientesExcluir(new ArrayList<SigObjetoCustoClientes>());
		this.setPossuiAlteracaoCliente(false);
		this.setEdicaoCliente(false);
		
		if (seqObjetoCustoVersao != null) {
			// busca lista de clientes já associados ao objeto custo
			SigObjetoCustoVersoes sigObjetoCustoVersoes = this.custosSigFacade.obterObjetoCustoVersoes(seqObjetoCustoVersao);
			List<SigObjetoCustoClientes> listResult = this.custosSigCadastrosBasicosFacade.pesquisarObjetoCustoClientePorObjetoCustoVersao(sigObjetoCustoVersoes);
			if (listResult != null && !listResult.isEmpty()) {
				this.setListaClientes(listResult);
				this.atualizarListaSelecionados();
			}
		}
	}
	
	private void  atualizarListaSelecionados(){
		this.setValorAlternarTodos(Boolean.FALSE);
		this.setMarcouTodos(Boolean.FALSE);
		this.setObjetoCustoClienteSelecionados(new HashMap<SigObjetoCustoClientes, Boolean>());
		for (SigObjetoCustoClientes objetoCustoCliente : this.getListaClientes()) {
			if(objetoCustoCliente.getSituacao() == DominioSituacao.A){
				this.getObjetoCustoClienteSelecionados().put(objetoCustoCliente, Boolean.FALSE);
			}
		}
	}

	public List<SigDirecionadores> listarDirecionadores() {
		List<SigDirecionadores> lista = new ArrayList<SigDirecionadores>();
		for (SigObjetoCustoDirRateios sigObjetoCustoDirRateios : this.manterObjetosCustoSliderDirecionadoresController.getListaObjetoCustoDirRateios()) {
			if (sigObjetoCustoDirRateios.getSituacao().equals(DominioSituacao.A)) {
				lista.add(sigObjetoCustoDirRateios.getDirecionadores());
			}
		}
		return lista;
	}

	public String adicionarCliente() {
		try {
			if (this.getListaClientes() != null && !this.getListaClientes().isEmpty()) {
				this.custosSigCadastrosBasicosFacade.validarInclusaoAlteracaoClienteObjetoCusto(this.getSigObjetoCustoClientes(), this.getListaClientes(), false);
			}
			if (this.propagarClientes) {
				this.propagarClientes = false;
				return this.manterObjetosCustoController.propagarClientes();
			}

			this.getSigObjetoCustoClientes().setCriadoEm(new Date());
			try {
				this.getSigObjetoCustoClientes().setServidor(this.registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado()));
			} catch (ApplicationBusinessException e) {
				this.getSigObjetoCustoClientes().setServidor(null);
			}
			this.getSigObjetoCustoClientes().setObjetoCustoVersoes(this.manterObjetosCustoController.getObjetoCustoVersao());
			this.getListaClientes().add(this.getSigObjetoCustoClientes());
			this.atualizarListaSelecionados();
			this.setSigObjetoCustoClientes(new SigObjetoCustoClientes());
			this.getSigObjetoCustoClientes().setSituacao(DominioSituacao.A);
			this.setItemSelecionadoClientes("0");
			this.setPossuiAlteracaoCliente(true);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	public void alternarSelecaoTodos() {
		this.marcouTodos = !this.marcouTodos;
		this.valorAlternarTodos = this.marcouTodos;
		
		for (SigObjetoCustoClientes objetoCustoCliente : this.getListaClientes()) {
			this.getObjetoCustoClienteSelecionados().put(objetoCustoCliente, this.getValorAlternarTodos());
		}
	}
	
	public boolean verificarSemSelecionados(){
		if(this.getObjetoCustoClienteSelecionados()!= null){
			for(Boolean b : this.getObjetoCustoClienteSelecionados().values()){
				if(b){
					return false;
				}
			}
		}
		return true;
	}
	
	public void excluirClientes() throws ApplicationBusinessException{
		try{
			boolean selecionouAlgumCliente = false;
			
			List<SigObjetoCustoClientes> listaCopia = new ArrayList<SigObjetoCustoClientes>(this.getListaClientes());
			for (SigObjetoCustoClientes objetoCustoCliente : listaCopia) {
				if (this.getObjetoCustoClienteSelecionados().get(objetoCustoCliente)) {
					selecionouAlgumCliente = true;
					this.realizaExclusao(objetoCustoCliente);
				}
			}
	
			if (selecionouAlgumCliente) {
				this.atualizarListaSelecionados();
			}
			else {
				this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ERRO_NENHUM_CLIENTE_SELECIONADO");
			}
		}
		catch(ApplicationBusinessException e){
			this.apresentarExcecaoNegocio(e);
		}
	}
	
	public void excluirCliente(SigObjetoCustoClientes objetoCustoCliente) {
		try{
			this.realizaExclusao(objetoCustoCliente);	
			this.atualizarListaSelecionados();
		}
		catch(ApplicationBusinessException e){
			this.apresentarExcecaoNegocio(e);
		}
	}

	private void realizaExclusao(SigObjetoCustoClientes sigObjetoCustoClientes) throws ApplicationBusinessException {
		if (sigObjetoCustoClientes.getSeq() != null) {
			this.custosSigCadastrosBasicosFacade.validarExclusaoClienteObjetoCusto(sigObjetoCustoClientes.getObjetoCustoVersoes());
			this.getListaClientesExcluir().add(sigObjetoCustoClientes);
		}
		this.getListaClientes().remove(sigObjetoCustoClientes);
		this.setPossuiAlteracaoCliente(true);
	}

	public void editarCliente(Integer indexRateio) {
		this.setIndexOfCliente(indexRateio);
		try {
			SigObjetoCustoClientes cliente = this.getListaClientes().get(indexRateio);
			cliente.setEmEdicao(Boolean.TRUE);
			
			this.setSigObjetoCustoClientes((SigObjetoCustoClientes) cliente.clone());
		} catch (CloneNotSupportedException e) {
			LOG.error("A classe SigObjetoCustoClientes " + "não implementa a interface Cloneable.", e);
		}
		this.getSigObjetoCustoClientes().setEmEdicao(Boolean.TRUE);
		this.setEdicaoCliente(true);
		this.setPossuiAlteracaoCliente(Boolean.TRUE);
		if (this.getSigObjetoCustoClientes().getCentroCusto() != null) {
			this.setItemSelecionadoClientes("0");
		} else {
			this.setItemSelecionadoClientes("1");
		}
	}

	public void gravarCliente() {
		try {
			// realiza validação se eu possuo mais itens na lista com exceção do
			// item que está sendo editado
			if (this.getListaClientes() != null && !this.getListaClientes().isEmpty() && this.getListaClientes().size() > 1) {
				this.custosSigCadastrosBasicosFacade.validarInclusaoAlteracaoClienteObjetoCusto(this.getSigObjetoCustoClientes(), this.getListaClientes(), true);
			}
			this.setEdicaoCliente(false);
			try {
				this.getSigObjetoCustoClientes().setServidor(this.registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado()));
			} catch (ApplicationBusinessException e) {
				this.getSigObjetoCustoClientes().setServidor(null);
			}
			this.getSigObjetoCustoClientes().setEmEdicao(false);
			this.getListaClientes().set(this.getIndexOfCliente(), this.getSigObjetoCustoClientes());
			this.setSigObjetoCustoClientes(new SigObjetoCustoClientes());
			this.getSigObjetoCustoClientes().setSituacao(DominioSituacao.A);
			this.setItemSelecionadoClientes("0");

		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void cancelarEdicaoCliente() {
		this.setEdicaoCliente(false);
		this.getListaClientes().get(this.getIndexOfCliente()).setEmEdicao(Boolean.FALSE);
		this.setSigObjetoCustoClientes(new SigObjetoCustoClientes());
		this.getSigObjetoCustoClientes().setSituacao(DominioSituacao.A);
	}

	public void gravarClientesBanco() {
		// exclusão
		for (SigObjetoCustoClientes clienteExcluir : this.getListaClientesExcluir()) {
			this.custosSigCadastrosBasicosFacade.excluirCliente(clienteExcluir);
		}
		// inclusão - alteração
		for (SigObjetoCustoClientes cliente : this.getListaClientes()) {
			this.custosSigCadastrosBasicosFacade.persistCliente(cliente);
		}
		
		this.setListaClientesExcluir(new ArrayList<SigObjetoCustoClientes>());
		this.setPossuiAlteracaoCliente(false);
	}

	// suggestions
	public List<FccCentroCustos> pesquisarCentroCustoClientes(String paramPesquisa) throws BaseException {
		List<FccCentroCustos> listaResultado = new ArrayList<FccCentroCustos>();
		listaResultado = this.centroCustoFacade.pesquisarCentroCustosPorCentroProdExcluindoGcc(paramPesquisa, null, DominioSituacao.A);
		return listaResultado;
	}

	public void limparCentroCustoClientes() {
		this.getSigObjetoCustoClientes().setCentroCusto(null);
	}

	public void posSelectionCentroCustoClientes() {
		this.getSigObjetoCustoClientes().setCentroProducao(null);
	}

	public List<SigCentroProducao> pesquisarCentroProducaoClientes(String paramPesquisa) throws BaseException {
		List<SigCentroProducao> listaResultado = new ArrayList<SigCentroProducao>();
		listaResultado = this.custosSigCadastrosBasicosFacade.pesquisarCentroProducao(paramPesquisa, DominioSituacao.A);
		return listaResultado;
	}

	public void limparCentroProducaoClientes() {
		this.getSigObjetoCustoClientes().setCentroProducao(null);
	}

	public void posSelectionCentroProducaoClientes() {
		this.getSigObjetoCustoClientes().setCentroCusto(null);
	}

	// gets sets
	public SigObjetoCustoClientes getSigObjetoCustoClientes() {
		return sigObjetoCustoClientes;
	}

	public void setSigObjetoCustoClientes(SigObjetoCustoClientes sigObjetoCustoClientes) {
		this.sigObjetoCustoClientes = sigObjetoCustoClientes;
	}

	public String getItemSelecionadoClientes() {
		return itemSelecionadoClientes;
	}

	public void setItemSelecionadoClientes(String itemSelecionadoClientes) {
		this.itemSelecionadoClientes = itemSelecionadoClientes;
	}

	public Boolean getPropagarClientes() {
		return propagarClientes;
	}

	public void setPropagarClientes(Boolean propagarClientes) {
		this.propagarClientes = propagarClientes;
	}

	public List<SigObjetoCustoClientes> getListaClientes() {
		return listaClientes;
	}

	public void setListaClientes(List<SigObjetoCustoClientes> listaClientes) {
		this.listaClientes = listaClientes;
	}

	public List<SigObjetoCustoClientes> getListaClientesExcluir() {
		return listaClientesExcluir;
	}

	public void setListaClientesExcluir(List<SigObjetoCustoClientes> listaClientesExcluir) {
		this.listaClientesExcluir = listaClientesExcluir;
	}

	public boolean isPossuiAlteracaoCliente() {
		return possuiAlteracaoCliente;
	}

	public void setPossuiAlteracaoCliente(boolean possuiAlteracaoCliente) {
		this.possuiAlteracaoCliente = possuiAlteracaoCliente;
	}

	public Integer getSeqClienteExclusao() {
		return seqClienteExclusao;
	}

	public void setSeqClienteExclusao(Integer seqClienteExclusao) {
		this.seqClienteExclusao = seqClienteExclusao;
	}

	public boolean isEdicaoCliente() {
		return edicaoCliente;
	}

	public void setEdicaoCliente(boolean edicaoCliente) {
		this.edicaoCliente = edicaoCliente;
	}

	public Integer getIndexOfCliente() {
		return indexOfCliente;
	}

	public void setIndexOfCliente(Integer indexOfCliente) {
		this.indexOfCliente = indexOfCliente;
	}
	
	public Boolean getValorAlternarTodos() {
		return valorAlternarTodos;
	}

	public void setValorAlternarTodos(Boolean valorAlternarTodos) {
		this.valorAlternarTodos = valorAlternarTodos;
	}

	public Boolean getMarcouTodos() {
		return marcouTodos;
	}

	public void setMarcouTodos(Boolean marcouTodos) {
		this.marcouTodos = marcouTodos;
	}

	public Map<SigObjetoCustoClientes, Boolean> getObjetoCustoClienteSelecionados() {
		return objetoCustoClienteSelecionados;
	}

	public void setObjetoCustoClienteSelecionados(
			Map<SigObjetoCustoClientes, Boolean> objetoCustoClienteSelecionados) {
		this.objetoCustoClienteSelecionados = objetoCustoClienteSelecionados;
	}
}
