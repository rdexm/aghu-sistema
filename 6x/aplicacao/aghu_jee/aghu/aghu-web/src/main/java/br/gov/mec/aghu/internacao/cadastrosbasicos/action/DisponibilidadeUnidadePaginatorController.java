package br.gov.mec.aghu.internacao.cadastrosbasicos.action;

import java.util.List;

import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.internacao.pesquisa.business.IPesquisaInternacaoFacade;
import br.gov.mec.aghu.model.AghAla;
import br.gov.mec.aghu.model.AghClinicas;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.VAinDispVagas;
import br.gov.mec.aghu.paciente.action.PesquisaPacienteController;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;

public class DisponibilidadeUnidadePaginatorController extends ActionController implements ActionPaginator{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2908076915941387676L;

	private static final String REDIRECT_PESQUISA_PACIENTE = "paciente-pesquisaPaciente";
	private final String PAGE_PESQUISAR_DISPONIBILIDADE_UNIDADE = "internacao-pesquisarDisponibilidadeUnidade";

	@EJB
	protected IAghuFacade aghuFacade;
	
	@EJB
	private IPesquisaInternacaoFacade pesquisaInternacaoFacade;

	@EJB
	private IPacienteFacade pacienteFacade;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	private Integer codigoPaciente;

	private AipPacientes pacienteInternacao;

	private AghClinicas clinica = null;
	private AghUnidadesFuncionais unidadeFuncional;

	private Integer codigoClinica = null;
	private Short codigoUnidadeFuncional = null;
	private Integer seqAtendimentoUrgencia = null;
	private String cameFrom;
	
//	private List<UnidadeDisponibilidadeVO> unidadeList = new ArrayList<UnidadeDisponibilidadeVO>();
	
	private String voltarPara = null;
	
	private VAinDispVagas unidade = null;
	
	@Inject @Paginator
	private DynamicDataModel<VAinDispVagas> dataModel;
	
	@Inject
	private PesquisaPacienteController pesquisaPacienteController;

	public void iniciar() {
	 
		if (cameFrom == null || cameFrom.isEmpty()){
			cameFrom = "internacao-pesquisarDisponibilidadeUnidade";
		}
		if (codigoClinica != null || codigoUnidadeFuncional != null) {

			if (codigoUnidadeFuncional != null) {
				unidadeFuncional = this.aghuFacade.obterAghUnidadesFuncionaisPorChavePrimaria(codigoUnidadeFuncional);
			}

			if (codigoClinica != null) {
				clinica = aghuFacade.obterClinica(codigoClinica);
			}

			if (this.codigoPaciente != null) {
				pacienteInternacao = this.pacienteFacade.obterPaciente(codigoPaciente);
			}

			this.pesquisar();
		}
	
	}

	/**
	 * Método que retorna uma coleções de clínicas p/ preencher a suggestion
	 * box, de acordo com filtro informado.
	 * 
	 * @param parametro
	 * @return
	 */
	public List<AghClinicas> pesquisarClinicas(String paramPesquisa) {
		return this.aghuFacade.pesquisarClinicas(paramPesquisa == null ? null
				: paramPesquisa);
	}
	
	public String actionInternar(final VAinDispVagas unidade) {
		this.unidade = unidade;
		
		pesquisaPacienteController.setSeqUnidadeFuncional(unidade.getSeq());
		pesquisaPacienteController.setCameFrom(PAGE_PESQUISAR_DISPONIBILIDADE_UNIDADE);
		
		return REDIRECT_PESQUISA_PACIENTE;
	}

	/**
	 * Método para atribuir clinica selecionado na suggestion ao atributo
	 * this.clinica da controller.
	 * 
	 * @param clinica
	 */
	public void atribuirClinica(AghClinicas clinica) {
		this.clinica = clinica;
	}

	/**
	 * Método que retorna uma coleções de unidades funcionais p/ preencher a
	 * suggestion box, de acordo com filtro informado.
	 * 
	 * @param parametro
	 * @return
	 */
	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncional(
			String strPesquisa) {
		return cadastrosBasicosInternacaoFacade
				.pesquisarUnidadeFuncional(strPesquisa == null ? null
						: strPesquisa);
	}
	
	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalInternacaoAtiva(String strPesquisa){
		return cadastrosBasicosInternacaoFacade.pesquisarUnidadeFuncionalInternacaoAtiva(strPesquisa);
	}

	/**
	 * Método para atribuir unidade funcional selecionada na suggestion "andar"
	 * ao atributo unidadeFuncional da controller.
	 * 
	 * @param unidadeFuncional
	 */
	public void atribuirAndar(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	/**
	 * Método para limpar campos da tela
	 */
	public void limpar() {
		this.clinica = null;
		this.unidadeFuncional = null;
		this.dataModel.limparPesquisa();

		this.codigoClinica = null;
		this.codigoUnidadeFuncional = null;
	}

	/**
	 * Método que realiza a ação do botão pesquisar.
	 */
	public void pesquisar() {
		
		// Seta a ordenação "default" para quando clicar na coluna "unidade" da
		// grid após uma pesquisa pelo botão "Pesquisar", fazer a ordenação desc
//		this.setOrder(VAinDispVagas.Fields.VUF_ANDAR_ALA_DESCRICAO.toString()+ " asc");
		
		this.dataModel.reiniciarPaginator();
	}
	
	public List<AghAla> listarAlas() {
		return aghuFacade.buscarTodasAlas();
	}

	public String redirecionarSolicitacoesPendentes() {
		return "redirecionarSolicitacoesPendentes";
	}

	@Override
	public Long recuperarCount() {
		return pesquisaInternacaoFacade.pesquisarVAinDispVagasCount(clinica, unidadeFuncional);
	}

	@Override
	@SuppressWarnings("all")
	public List recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return pesquisaInternacaoFacade.pesquisarVAinDispVagas(firstResult, maxResult, orderProperty, asc, clinica, unidadeFuncional);
	}
	
	public String voltar() {
		return voltarPara;
	}
	
	public boolean exibirVoltar() {
		return voltarPara != null;
	}
	
	// GETTERs E SETTERs

	public AghClinicas getClinica() {
		return clinica;
	}

	public void setClinica(AghClinicas clinica) {
		this.clinica = clinica;
		if (clinica == null) {
			this.codigoClinica = null;
		}
	}

	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}

	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
		if (unidadeFuncional == null){
			this.codigoUnidadeFuncional = null;
		}
	}

	public Integer getCodigoClinica() {
		return codigoClinica;
	}

	public void setCodigoClinica(Integer codigoClinica) {
		this.codigoClinica = codigoClinica;
	}

	public Short getCodigoUnidadeFuncional() {
		return codigoUnidadeFuncional;
	}

	public void setCodigoUnidadeFuncional(Short codigoUnidadeFuncional) {
		this.codigoUnidadeFuncional = codigoUnidadeFuncional;
	}

	public void setSeqAtendimentoUrgencia(Integer seqAtendimentoUrgencia) {
		this.seqAtendimentoUrgencia = seqAtendimentoUrgencia;
	}

	public Integer getSeqAtendimentoUrgencia() {
		return seqAtendimentoUrgencia;
	}

	public Integer getCodigoPaciente() {
		return codigoPaciente;
	}

	public void setCodigoPaciente(Integer codigoPaciente) {
		this.codigoPaciente = codigoPaciente;
	}

	public AipPacientes getPacienteInternacao() {
		return pacienteInternacao;
	}

	public void setPacienteInternacao(AipPacientes pacienteInternacao) {
		this.pacienteInternacao = pacienteInternacao;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}
	
	public VAinDispVagas getUnidade() {
		return unidade;
	}

	public void setUnidade(VAinDispVagas unidade) {
		this.unidade = unidade;
	}

	public DynamicDataModel<VAinDispVagas> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<VAinDispVagas> dataModel) {
		this.dataModel = dataModel;
	}

	public String getCameFrom() {
		return cameFrom;
	}

	public void setCameFrom(String cameFrom) {
		this.cameFrom = cameFrom;
	}
}
