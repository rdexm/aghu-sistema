package br.gov.mec.aghu.sig.custos.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.SigDirecionadores;
import br.gov.mec.aghu.model.SigObjetoCustoClientes;
import br.gov.mec.aghu.model.SigObjetoCustoDirRateios;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.Severity;


public class AjusteDirecionadoresController extends ActionController {

	private static final String MANTER_OBJETOS_CUSTO = "manterObjetosCusto";

	private static final long serialVersionUID = -545089456895464958L;

	@Inject
	private ManterObjetosCustoController manterObjetosCustoController;

	@Inject
	private ManterObjetosCustoSliderDirecionadoresController manterObjetosCustoSliderDirecionadoresController;

	@Inject
	private ManterObjetosCustoSliderClientesController manterObjetosCustoSliderClientesController;

	private String nomeObjetoCusto;
	private SigDirecionadores novoDirecionador;
	private List<SigDirecionadores> listaDirecionadoresRateioObjetoCusto;
	private List<SigObjetoCustoClientes> listaObjetoCustoClientes;
	private Map<SigObjetoCustoClientes, Boolean> objetoCustoClienteSelecionados;
	private Boolean valorAlternarTodos;
	private Boolean marcouTodos;

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void iniciar() {

		this.setNomeObjetoCusto(manterObjetosCustoController.getObjetoCustoVersao().getSigObjetoCustos().getNome());
		this.setNovoDirecionador(null);
		this.setValorAlternarTodos(Boolean.FALSE);
		this.setMarcouTodos(Boolean.FALSE);
		this.setObjetoCustoClienteSelecionados(new HashMap<SigObjetoCustoClientes, Boolean>());

		//List<SigObjetoCustoClientes> listaObjetoCustoClientes = manterObjetosCustoSliderClientesController.getListaClientes();
		//this.setListaObjetoCustoClientes(new ArrayList<SigObjetoCustoClientes>(manterObjetosCustoController.getObjetoCustoVersao().getListObjetoCustoClientes()));
		
		this.listaObjetoCustoClientes = new ArrayList<SigObjetoCustoClientes>();		
		for (SigObjetoCustoClientes objetoCustoCliente : this.manterObjetosCustoSliderClientesController.getListaClientes()) {
			if(objetoCustoCliente.getSituacao() == DominioSituacao.A){
				this.getObjetoCustoClienteSelecionados().put(objetoCustoCliente, Boolean.FALSE);
				this.listaObjetoCustoClientes.add(objetoCustoCliente);
			}
		}

		// Copia os direcionadores de rateio do objeto de custo
		this.setListaDirecionadoresRateioObjetoCusto(new ArrayList<SigDirecionadores>());
		for (SigObjetoCustoDirRateios sigObjetoCustoDirRateio : manterObjetosCustoSliderDirecionadoresController.getListaObjetoCustoDirRateios()) {
			if(sigObjetoCustoDirRateio.getSituacao() == DominioSituacao.A){
				this.getListaDirecionadoresRateioObjetoCusto().add(sigObjetoCustoDirRateio.getDirecionadores());
			}
		}
	
	}

	public void alternarSelecaoTodos() {
		this.marcouTodos = !this.marcouTodos;
		this.valorAlternarTodos = this.marcouTodos;
		
		for (SigObjetoCustoClientes objetoCustoCliente : this.getListaObjetoCustoClientes()) {
			this.getObjetoCustoClienteSelecionados().put(objetoCustoCliente, this.getValorAlternarTodos());
		}
	}

	public String alterar() {

		boolean alterouAlgumDirecionador = false;
		for (SigObjetoCustoClientes objetoCustoCliente : this.getListaObjetoCustoClientes()) {
			if (this.getObjetoCustoClienteSelecionados().get(objetoCustoCliente)) {
				objetoCustoCliente.setDirecionadores(this.getNovoDirecionador());
				alterouAlgumDirecionador = true;
			}
		}

		if (alterouAlgumDirecionador) {
			this.manterObjetosCustoController.setEvitarExecucaoMetodoInicial(true);
			this.manterObjetosCustoController.setPossuiAlteracaoCampos(true);
			return MANTER_OBJETOS_CUSTO;

		} else {
			this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ERRO_NENHUM_DIRECIONADOR_SELECIONADO");
			return null;
		}
	}

	public String cancelar() {
		return MANTER_OBJETOS_CUSTO;
	}

	public boolean isVisualizar() {
		return this.manterObjetosCustoController.isVisualizar();
	}

	// Getters and Setters
	public String getNomeObjetoCusto() {
		return nomeObjetoCusto;
	}

	public void setNomeObjetoCusto(String nomeObjetoCusto) {
		this.nomeObjetoCusto = nomeObjetoCusto;
	}

	public SigDirecionadores getNovoDirecionador() {
		return novoDirecionador;
	}

	public void setNovoDirecionador(SigDirecionadores novoDirecionador) {
		this.novoDirecionador = novoDirecionador;
	}

	public List<SigDirecionadores> getListaDirecionadoresRateioObjetoCusto() {
		return listaDirecionadoresRateioObjetoCusto;
	}

	public void setListaDirecionadoresRateioObjetoCusto(List<SigDirecionadores> listaDirecionadoresRateioObjetoCusto) {
		this.listaDirecionadoresRateioObjetoCusto = listaDirecionadoresRateioObjetoCusto;
	}

	public Map<SigObjetoCustoClientes, Boolean> getObjetoCustoClienteSelecionados() {
		return objetoCustoClienteSelecionados;
	}

	public void setObjetoCustoClienteSelecionados(Map<SigObjetoCustoClientes, Boolean> objetoCustoClienteSelecionados) {
		this.objetoCustoClienteSelecionados = objetoCustoClienteSelecionados;
	}

	public Boolean getValorAlternarTodos() {
		return valorAlternarTodos;
	}

	public void setValorAlternarTodos(Boolean valorAlternarTodos) {
		this.valorAlternarTodos = valorAlternarTodos;
	}

	public List<SigObjetoCustoClientes> getListaObjetoCustoClientes() {
		return listaObjetoCustoClientes;
	}

	public void setListaObjetoCustoClientes(List<SigObjetoCustoClientes> listaObjetoCustoClientes) {
		this.listaObjetoCustoClientes = listaObjetoCustoClientes;
	}

	public Boolean getMarcouTodos() {
		return marcouTodos;
	}

	public void setMarcouTodos(Boolean marcouTodos) {
		this.marcouTodos = marcouTodos;
	}
}