package br.gov.mec.aghu.internacao.estornar.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.casca.model.Perfil;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;



public class EstornarAltaPacientePaginatorController  extends ActionController implements ActionPaginator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3416189909423047371L;

	@EJB
	private IInternacaoFacade internacaoFacade;

	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	private Integer prontuario;
	private String nomePaciente;
	
	@Inject @Paginator
	private DynamicDataModel<Perfil> dataModel;	
	
	private AinInternacao internacao;
	
	
	private Integer seqInternacaoFirst;
	
	
	@Inject
	private EstornarAltaPacienteController estornarAltaPacienteController;
	
	private final String PAGE_ESTOR_ALTA_PAC = "estornarAltaPaciente";
	
	/*@Create
	public void inicio() {
		this.ativo = false;
	}*/

	@PostConstruct
	public void init() {
		begin(conversation, true);	
		internacao = new AinInternacao();	
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

	
	@Override
	public Long recuperarCount() {
		return this.internacaoFacade.pesquisaInternacoesParaEstornarAltaPacienteCount(this.prontuario);
	}

	@Override
	public List recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		List<AinInternacao> lista = this.internacaoFacade.pesquisaInternacoesParaEstornarAltaPaciente(firstResult, maxResult, orderProperty, asc,this.prontuario); 
		
		seqInternacaoFirst = (lista !=null && !lista.isEmpty()) ? lista.get(0).getSeq() :0;		
		
		return lista;
	}
	
	
	public String estornar(AinInternacao internacaoPrn ){
		this.estornarAltaPacienteController.setInternacao(internacaoPrn);
		this.estornarAltaPacienteController.inicio();
		return PAGE_ESTOR_ALTA_PAC;
		
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

	public DynamicDataModel<Perfil> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<Perfil> dataModel) {
		this.dataModel = dataModel;
	}

	public AinInternacao getInternacao() {
		return internacao;
	}

	public void setInternacao(AinInternacao internacao) {
		this.internacao = internacao;
	}

	public Integer getSeqInternacaoFirst() {
		return seqInternacaoFirst;
	}

	public void setSeqInternacaoFirst(Integer seqInternacaoFirst) {
		this.seqInternacaoFirst = seqInternacaoFirst;
	}
	
}