package br.gov.mec.aghu.internacao.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;

public class DarAltaPacientePaginatorController extends ActionController implements ActionPaginator {
	private static final long serialVersionUID = 3047173626664346996L;
	
	private final String PAGE_DADOS_PACIENTE = "dadosDaAltaPaciente";
	@EJB
	private IInternacaoFacade internacaoFacade;
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@Inject @Paginator
	private DynamicDataModel<AinInternacao> dataModel;	
	
	private Integer prontuario;
	private String nomePaciente;
	private AinInternacao internacao;
	
	@PostConstruct
	public void init() {
		begin(conversation, true);
		this.dataModel.limparPesquisa();
	}

	/**
	 * Limpa os campos da tela.
	 */
	public void limparPesquisa() {
		this.prontuario = null;
		this.nomePaciente = null;
		this.dataModel.limparPesquisa();
	}

	/**
	 * Método que realiza a ação do botão pesquisar.
	 */
	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
		this.nomePaciente = pacienteFacade.pesquisarNomePaciente(this.prontuario);
	}

	public String editar(){
		begin(conversation);
		return PAGE_DADOS_PACIENTE;
	}
	
	@Override
	public Long recuperarCount() {
		return this.internacaoFacade.pesquisaInternacoesParaDarAltaPacienteCount(this.prontuario);
	}

	@Override
	public List<AinInternacao> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return this.internacaoFacade.pesquisaInternacoesParaDarAltaPaciente(firstResult, maxResult, orderProperty, asc,this.prontuario); 
	}
		
	//### GETs e SETs ###
	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public DynamicDataModel<AinInternacao> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AinInternacao> dataModel) {
		this.dataModel = dataModel;
	}

	public AinInternacao getInternacao() {
		return internacao;
	}

	public void setInternacao(AinInternacao internacao) {
		this.internacao = internacao;
	}
}