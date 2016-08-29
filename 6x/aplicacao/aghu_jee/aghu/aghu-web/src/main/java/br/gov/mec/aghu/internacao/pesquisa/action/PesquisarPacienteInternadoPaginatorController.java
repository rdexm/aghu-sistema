package br.gov.mec.aghu.internacao.pesquisa.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.internacao.pesquisa.business.IPesquisaInternacaoFacade;
import br.gov.mec.aghu.internacao.solicitacao.business.ISolicitacaoInternacaoFacade;
import br.gov.mec.aghu.internacao.vo.ServidorConselhoVO;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinQuartos;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;


public class PesquisarPacienteInternadoPaginatorController extends ActionController implements ActionPaginator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1846774091437097648L;

	@EJB
	private IPesquisaInternacaoFacade pesquisaInternacaoFacade;

	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	@EJB
	private ISolicitacaoInternacaoFacade solicitacaoInternacaoFacade;
	
	@Inject @Paginator
	private DynamicDataModel<AinInternacao> dataModel;
	
	private AghEspecialidades especialidade;
	private Integer prontuario;
	private AghUnidadesFuncionais unidadeFuncional;
	private AinQuartos quarto;
	private AinLeitos leito;
	private String nomePaciente;
	private ServidorConselhoVO servidorProfessor;
	
	private final String PAGE_PESQUISAR_PACIENTES_DETALHES = "pesquisarPacienteInternadoDetalhes";
	private final String PAGE_PESQUISAR_DISPONIBILIDADE_LEITO = "pesquisarDisponibilidadeLeito";
	private final String PAGE_DAR_ALTA = "internacao-dadosDaAltaPaciente";
	private final String PAGE_SOLICITAR_TRANSFERENCIA = "internacao-solicitaTransferenciaPacienteList";
	
	
	@PostConstruct
	public void init() {
		begin(conversation);
	}	
	
	
	/**
	 * Limpa os campos da tela.
	 */
	public void limparPesquisa() {
		prontuario = null;
		nomePaciente = null;
		especialidade = null;
		unidadeFuncional = null;
		quarto = null;
		leito = null;
		servidorProfessor = null;
		dataModel.limparPesquisa();
	}

	/**
	 * Método que realiza a ação do botão pesquisar.
	 */
	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}

	
	@Override
	public Long recuperarCount() {
		return pesquisaInternacaoFacade.pesquisarInternacoesAtivasCount(prontuario, null, nomePaciente, 
				especialidade != null? especialidade.getSeq() : null,
				leito != null ? leito.getLeitoID() : null,
				quarto!= null ? quarto.getNumero() : null,
				unidadeFuncional != null? unidadeFuncional.getSeq(): null,
				servidorProfessor != null? servidorProfessor.getMatricula() : null,
				servidorProfessor != null? servidorProfessor.getVinCodigo() : null);
	
	}

	@Override
	@SuppressWarnings("PMD.NPathComplexity")
	public List<AinInternacao> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		List<AinInternacao> lista = pesquisaInternacaoFacade.pesquisarInternacoesAtivas(firstResult, maxResult,
					orderProperty, asc,prontuario, null, nomePaciente,
					especialidade != null? especialidade.getSeq() : null,
					leito != null ? leito.getLeitoID() : null,
					quarto!= null ? quarto.getNumero() : null,
					unidadeFuncional != null? unidadeFuncional.getSeq(): null,
					servidorProfessor != null? servidorProfessor.getMatricula() : null,
					servidorProfessor != null? servidorProfessor.getVinCodigo() : null); 
		return lista;
	}
	

	public String pesquisarPacientesDetalhes(){
		return PAGE_PESQUISAR_PACIENTES_DETALHES;
	}
	
	public String pesquisarDisponibilidadeLeito(){
		return PAGE_PESQUISAR_DISPONIBILIDADE_LEITO;
	}
	
	public String darAlta(){
		return PAGE_DAR_ALTA;
	}
	
	public String solicitarTransferencia(){
		return PAGE_SOLICITAR_TRANSFERENCIA;
	}
	
	
	//### Metodo para a suggestion de especialidades###
	public List<AghEspecialidades> pesquisarEspecialidades(String strPesquisa) {
		return aghuFacade.listarEspecialidadesPorSiglaOuDescricao(strPesquisa, true);
	}
	
	//### Metodos para a suggestion de unidades funcionais ###
	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorCodigoEDescricao(
			String param) {
		return this.cadastrosBasicosInternacaoFacade.pesquisarUnidadeFuncionalPorCodigoPorAndarAlaDescricaoAtivasInativas(param);
	}
	
	//### Metodo para a suggestion de quartos ###
	public List<AinQuartos> pesquisarQuartos(String param) {
		return this.cadastrosBasicosInternacaoFacade.pesquisarQuartos(param == null ? null : param);
	}
	
	
	//### Metodo para a suggestion de leitos ###
	public List<AinLeitos> pesquisarLeitos(String param) {
		return this.cadastrosBasicosInternacaoFacade.pesquisarLeitosOrdenado((String)param);
	}
	
	//### Metodo para a suggestion de CRM ###
	public List<ServidorConselhoVO> pesquisarCRM(String paramPesquisa) {
		return solicitacaoInternacaoFacade.pesquisarServidorConselhoVOPorNomeeCRM(paramPesquisa == null ? null
				: paramPesquisa);
	}
	
	//### GETs e SETs ###
	public Integer getProntuario() {
		return prontuario;
	}


	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}


	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}


	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}


	public AinQuartos getQuarto() {
		return quarto;
	}


	public void setQuarto(AinQuartos quarto) {
		this.quarto = quarto;
	}


	public AinLeitos getLeito() {
		return leito;
	}


	public void setLeito(AinLeitos leito) {
		this.leito = leito;
	}


	public String getNomePaciente() {
		return nomePaciente;
	}


	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}


	public AghEspecialidades getEspecialidade() {
		return especialidade;
	}


	public void setEspecialidade(AghEspecialidades especialidade) {
		this.especialidade = especialidade;
	}


	public ServidorConselhoVO getServidorProfessor() {
		return servidorProfessor;
	}


	public void setServidorProfessor(ServidorConselhoVO servidorProfessor) {
		this.servidorProfessor = servidorProfessor;
	}

	public DynamicDataModel<AinInternacao> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AinInternacao> dataModel) {
		this.dataModel = dataModel;
	}

}