package br.gov.mec.aghu.internacao.administracao.action;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.internacao.pesquisa.business.IPesquisaInternacaoFacade;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;

public class TrocarPacienteInternacaoPaginatorController extends ActionController implements ActionPaginator  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2797550883857507628L;

	@EJB
	private IPesquisaInternacaoFacade pesquisaInternacaoFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@Inject
	private TrocarPacienteInternacaoController controller;
	
	@Inject @Paginator
	private DynamicDataModel<AinInternacao> dataModel;	
	
	private Integer prontuario;
	private Date dataInternacao;
	private String nomePaciente;
	
	private AinInternacao internacao;
	private AinInternacao internacaoSelecionada;
	
	private final String PAGE_TROCAR_PACIENTE_INTERNACAO = "trocarPacienteInternacao";
	
	@PostConstruct
	public void inicio() {
		this.begin(this.conversation, true);
		internacao = new AinInternacao();
		this.setDataInternacao(null);
		this.setProntuario(null);
		this.setNomePaciente(null);
	}

	/**
	 * Limpa os campos da tela.
	 */
	public void limparPesquisa() {
		this.setInternacao(new AinInternacao());
		this.dataModel.limparPesquisa();
		this.setProntuario(null);
		this.setNomePaciente(null);
		this.setDataInternacao(null);
	}

	/**
	 * Método que realiza a ação do botão pesquisar.
	 */
	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
		this.nomePaciente = pacienteFacade.pesquisarNomePaciente(this.prontuario);
	}
	
	@Override
	public Long recuperarCount() {
		Long count = this.pesquisaInternacaoFacade.pesquisarInternacoesDoPacientePorProntuarioEDataInternacaoCount(this.prontuario, this.dataInternacao);
		return count;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AinInternacao> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		List<AinInternacao> lista = this.pesquisaInternacaoFacade.pesquisarInternacoesDoPacientePorProntuarioEDataInternacao(firstResult, maxResult,
				orderProperty, asc, this.prontuario, this.dataInternacao);
		
		return lista;
	}
	
	public String editar() {
		controller.setIntSeq(this.internacaoSelecionada.getSeq());
		
		controller.setProntuario(this.prontuario);
		controller.setNomePaciente(this.nomePaciente);
		controller.setDataInternacao(this.internacaoSelecionada.getDthrInternacao());
		controller.setDataAlta(this.internacaoSelecionada.getDtPrevAlta());
		
		if(this.internacaoSelecionada.getTipoAltaMedica() != null){
			controller.setTipoAltaMedica(this.internacaoSelecionada.getTipoAltaMedica().toString());
		}
		
		controller.inicio();
		
		return PAGE_TROCAR_PACIENTE_INTERNACAO;
	}
	
	//### GETs e SETs ###
	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public Date getDataInternacao() {
		return dataInternacao;
	}

	public void setDataInternacao(Date dataInternacao) {
		this.dataInternacao = dataInternacao;
	}

	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public AinInternacao getInternacao() {
		return internacao;
	}

	public void setInternacao(AinInternacao internacao) {
		this.internacao = internacao;
	}

	public AinInternacao getInternacaoSelecionada() {
		return internacaoSelecionada;
	}

	public void setInternacaoSelecionada(AinInternacao internacaoSelecionada) {
		this.internacaoSelecionada = internacaoSelecionada;
	}

	public DynamicDataModel<AinInternacao> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AinInternacao> dataModel) {
		this.dataModel = dataModel;
	}

	public TrocarPacienteInternacaoController getController() {
		return controller;
	}

	public void setController(TrocarPacienteInternacaoController controller) {
		this.controller = controller;
	}

}