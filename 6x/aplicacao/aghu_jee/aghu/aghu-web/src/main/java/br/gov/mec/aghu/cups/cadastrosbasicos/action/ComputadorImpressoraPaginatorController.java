package br.gov.mec.aghu.cups.cadastrosbasicos.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.cups.cadastrosbasicos.business.ICadastrosBasicosCupsFacade;
import br.gov.mec.aghu.model.cups.ImpClasseImpressao;
import br.gov.mec.aghu.model.cups.ImpComputador;
import br.gov.mec.aghu.model.cups.ImpComputadorImpressora;
import br.gov.mec.aghu.model.cups.ImpImpressora;
import br.gov.mec.aghu.model.cups.ImpServidorCups;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;


public class ComputadorImpressoraPaginatorController extends ActionController implements ActionPaginator {
	
	private static final long serialVersionUID = 8630845882997825940L;
	
	private static final String CADASTRAR_COMPUTADOR_IMPRESSORA = "cadastrarComputadorImpressora";

	@Inject @Paginator
	private DynamicDataModel<ImpComputadorImpressora> dataModel;

	@Inject
	private ComputadorImpressoraController computadorImpressoraController;
	
	private ImpComputadorImpressora selecionado;

	private ImpComputadorImpressora impComputadorImpressora;

	@EJB
	private ICadastrosBasicosCupsFacade cadastrosBasicosCupsFacade;

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
		impComputadorImpressora = new ImpComputadorImpressora();
	}

	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}

	public void limparPesquisa() {
		impComputadorImpressora = new ImpComputadorImpressora();
		dataModel.limparPesquisa();
	}

	@Override
	public Long recuperarCount() {
		return cadastrosBasicosCupsFacade.pesquisarComputadorImpressoraCount(impComputadorImpressora);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return cadastrosBasicosCupsFacade.pesquisarComputadorImpressora(firstResult, maxResult,orderProperty, asc, impComputadorImpressora);
	}
	
	public void excluir() {
		try {
			cadastrosBasicosCupsFacade.excluirComputadorImpressora(selecionado.getId());
			apresentarMsgNegocio(Severity.INFO, "SUCESSO_EXCLUSAO_COMPUTADOR_IMPRESSORA");
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		dataModel.reiniciarPaginator();
	}
	
	public String inserir(){
		computadorImpressoraController.setIdComputadorImpressora(null);
		computadorImpressoraController.setComputadorReadOnly(false);
		computadorImpressoraController.setClasseImpressaoReadOnly(false);
		computadorImpressoraController.iniciar();
		return CADASTRAR_COMPUTADOR_IMPRESSORA;
	}

	public String editar(){
		computadorImpressoraController.iniciar();
		return CADASTRAR_COMPUTADOR_IMPRESSORA;
	}

	public List<ImpComputador> pesquisarComputador(String paramPesquisa) {
		return this.cadastrosBasicosCupsFacade.pesquisarComputador(paramPesquisa);
	}

	public List<ImpImpressora> pesquisarImpressora(String paramPesquisa) {
		return this.cadastrosBasicosCupsFacade.pesquisarImpressora(paramPesquisa);
	}

	public List<ImpClasseImpressao> pesquisarClasseImpressao(String paramPesquisa) {
		return this.cadastrosBasicosCupsFacade.pesquisarClasseImpressao(paramPesquisa);
	}

	public List<ImpServidorCups> pesquisarServidorCups(String paramPesquisa) {
		return this.cadastrosBasicosCupsFacade.pesquisarServidorCups(paramPesquisa);
	}
	
	public ImpComputadorImpressora getImpComputadorImpressora() {
		return impComputadorImpressora;
	}

	public void setImpComputadorImpressora(
			ImpComputadorImpressora impComputadorImpressora) {
		this.impComputadorImpressora = impComputadorImpressora;
	}

	public DynamicDataModel<ImpComputadorImpressora> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<ImpComputadorImpressora> dataModel) {
		this.dataModel = dataModel;
	}

	public ImpComputadorImpressora getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(ImpComputadorImpressora selecionado) {
		this.selecionado = selecionado;
	}

	public ComputadorImpressoraController getComputadorImpressoraController() {
		return computadorImpressoraController;
	}

	public void setComputadorImpressoraController(
			ComputadorImpressoraController computadorImpressoraController) {
		this.computadorImpressoraController = computadorImpressoraController;
	}
}