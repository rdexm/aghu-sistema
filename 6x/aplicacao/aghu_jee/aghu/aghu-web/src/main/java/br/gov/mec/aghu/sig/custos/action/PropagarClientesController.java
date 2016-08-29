package br.gov.mec.aghu.sig.custos.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.SigCentroProducao;
import br.gov.mec.aghu.model.SigDirecionadores;
import br.gov.mec.aghu.model.SigObjetoCustoClientes;
import br.gov.mec.aghu.model.SigObjetoCustoDirRateios;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.sig.custos.business.ICustosSigCadastrosBasicosFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;


public class PropagarClientesController extends ActionController {

	private static final String MANTER_OBJETOS_CUSTO = "manterObjetosCusto";

	private static final long serialVersionUID = -545089456895464958L;

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

	@Inject
	private ManterObjetosCustoSliderClientesController manterObjetosCustoSliderClientesController;

	private String codigoCentroCusto;
	private Integer codigoCentroProducao;
	private Integer codigoDirecionador;

	private FccCentroCustos fccCentroCustos;
	private SigCentroProducao sigCentroProducao;

	private String nomeObjetoCusto;
	private String nomeCentroCusto;
	private String nomeCentroProducao;

	private SigDirecionadores novoDirecionador;

	private List<SigDirecionadores> listaDirecionadoresRateioObjetoCusto;
	private List<FccCentroCustos> listaFccCentroCustos;

	private Map<FccCentroCustos, Boolean> objetoCustoSelecionados;
	private Boolean valorAlternarTodos;
	private Boolean marcarTodos;

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void iniciar() {
	 

		this.setNomeObjetoCusto(this.manterObjetosCustoController.getObjetoCustoVersao().getSigObjetoCustos().getNome());
		if (this.codigoCentroCusto != null && !this.codigoCentroCusto.equals("")) {
			this.fccCentroCustos = this.centroCustoFacade.obterCentroCustoPorChavePrimaria(Integer.parseInt(this.codigoCentroCusto));
			this.nomeCentroCusto = this.getFccCentroCustos().getCodigo() + " - " + this.getFccCentroCustos().getDescricao();
			this.setListaFccCentroCustos(this.custosSigCadastrosBasicosFacade.pesquisarCentroCustosHierarquia(this.fccCentroCustos));

		} else if (this.codigoCentroProducao != null && !this.codigoCentroProducao.equals("")) {
			this.sigCentroProducao = this.custosSigCadastrosBasicosFacade.obterSigCentroProducao(this.codigoCentroProducao);
			this.nomeCentroProducao = this.getSigCentroProducao().getSeq() + " - " + this.getSigCentroProducao().getNome();
			this.setListaFccCentroCustos(this.centroCustoFacade.pesquisarCentroCustosPorCentroProdAtivo(sigCentroProducao));
		}

		this.setNovoDirecionador(this.custosSigCadastrosBasicosFacade.obterDirecionador(this.getCodigoDirecionador()));
		this.setValorAlternarTodos(Boolean.FALSE);
		this.setMarcarTodos(Boolean.FALSE);

		this.setObjetoCustoSelecionados(new HashMap<FccCentroCustos, Boolean>());
		for (FccCentroCustos fccCentroCustos : getListaFccCentroCustos()) {
			this.getObjetoCustoSelecionados().put(fccCentroCustos, Boolean.FALSE);
		}

		this.setListaDirecionadoresRateioObjetoCusto(new ArrayList<SigDirecionadores>());
		for (SigObjetoCustoDirRateios sigObjetoCustoDirRateio : this.manterObjetosCustoSliderDirecionadoresController.getListaObjetoCustoDirRateios()) {
			this.getListaDirecionadoresRateioObjetoCusto().add(sigObjetoCustoDirRateio.getDirecionadores());
		}
	
	}

	public void alternarSelecaoTodos() {
		this.marcarTodos = !this.marcarTodos;
		this.valorAlternarTodos = this.marcarTodos;
		
		for (FccCentroCustos fccCentroCustos : this.listaFccCentroCustos) {
			this.objetoCustoSelecionados.put(fccCentroCustos, this.getValorAlternarTodos());
		}
	}

	public String alterar() {
		boolean alterouAlgumDirecionador = false;
		for (FccCentroCustos fccCentroCustos : this.listaFccCentroCustos) {
			if (this.objetoCustoSelecionados.get(fccCentroCustos)) {
				SigObjetoCustoClientes sigObjetoCustoClientes = new SigObjetoCustoClientes();
				sigObjetoCustoClientes.setCriadoEm(new Date());
				try {
					sigObjetoCustoClientes.setServidor(this.registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado()));
				} catch (ApplicationBusinessException e) {
					sigObjetoCustoClientes.setServidor(null);
				}
				sigObjetoCustoClientes.setObjetoCustoVersoes(this.manterObjetosCustoController.getObjetoCustoVersao());
				sigObjetoCustoClientes.setSituacao(DominioSituacao.A);
				sigObjetoCustoClientes.setDirecionadores(this.novoDirecionador);
				sigObjetoCustoClientes.setCentroCusto(fccCentroCustos);
				this.manterObjetosCustoSliderClientesController.getListaClientes().add(sigObjetoCustoClientes);
				alterouAlgumDirecionador = true;
			}
		}

		if (alterouAlgumDirecionador) {
			this.manterObjetosCustoController.setEvitarExecucaoMetodoInicial(true);
			this.manterObjetosCustoController.setPossuiAlteracaoCampos(true);
			this.manterObjetosCustoSliderClientesController.setSigObjetoCustoClientes(new SigObjetoCustoClientes());
			this.manterObjetosCustoSliderClientesController.getSigObjetoCustoClientes().setSituacao(DominioSituacao.A);
			this.manterObjetosCustoSliderClientesController.setPropagarClientes(false);
			this.manterObjetosCustoSliderClientesController.setPossuiAlteracaoCliente(true);
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

	public Boolean getValorAlternarTodos() {
		return valorAlternarTodos;
	}

	public void setValorAlternarTodos(Boolean valorAlternarTodos) {
		this.valorAlternarTodos = valorAlternarTodos;
	}

	public String getNomeCentroCusto() {
		return nomeCentroCusto;
	}

	public void setNomeCentroCusto(String nomeCentroCusto) {
		this.nomeCentroCusto = nomeCentroCusto;
	}

	public Integer getCodigoCentroProducao() {
		return codigoCentroProducao;
	}

	public void setCodigoCentroProducao(Integer codigoCentroProducao) {
		this.codigoCentroProducao = codigoCentroProducao;
	}

	public String getCodigoCentroCusto() {
		return codigoCentroCusto;
	}

	public void setCodigoCentroCusto(String codigoCentroCusto) {
		this.codigoCentroCusto = codigoCentroCusto;
	}

	public FccCentroCustos getFccCentroCustos() {
		return fccCentroCustos;
	}

	public void setFccCentroCustos(FccCentroCustos fccCentroCustos) {
		this.fccCentroCustos = fccCentroCustos;
	}

	public SigCentroProducao getSigCentroProducao() {
		return sigCentroProducao;
	}

	public void setSigCentroProducao(SigCentroProducao sigCentroProducao) {
		this.sigCentroProducao = sigCentroProducao;
	}

	public String getNomeCentroProducao() {
		return nomeCentroProducao;
	}

	public void setNomeCentroProducao(String nomeCentroProducao) {
		this.nomeCentroProducao = nomeCentroProducao;
	}

	public Map<FccCentroCustos, Boolean> getObjetoCustoSelecionados() {
		return objetoCustoSelecionados;
	}

	public void setObjetoCustoSelecionados(Map<FccCentroCustos, Boolean> objetoCustoSelecionados) {
		this.objetoCustoSelecionados = objetoCustoSelecionados;
	}

	public List<FccCentroCustos> getListaFccCentroCustos() {
		return listaFccCentroCustos;
	}

	public void setListaFccCentroCustos(List<FccCentroCustos> listaFccCentroCustos) {
		this.listaFccCentroCustos = listaFccCentroCustos;
	}

	public Integer getCodigoDirecionador() {
		return codigoDirecionador;
	}

	public void setCodigoDirecionador(Integer codigoDirecionador) {
		this.codigoDirecionador = codigoDirecionador;
	}

	public Boolean getMarcarTodos() {
		return marcarTodos;
	}

	public void setMarcarTodos(Boolean marcarTodos) {
		this.marcarTodos = marcarTodos;
	}

}