package br.gov.mec.aghu.paciente.prontuarioonline.action;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AghEquipes;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * Classe responsável por pesquisar e selecionar pacientes para a exibição dos
 * mesmos na tela de Prontuário Online.
 * 
 * @author Cristiano Quadros
 */
public class PesquisarPacientesPOLController extends ActionController implements ActionPaginator {


	private static final long serialVersionUID = 487521649173798266L;

	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	@EJB
	private ICentroCustoFacade centroCustoFacade;

	@EJB 
	private IPacienteFacade pacienteFacade;

	@Inject @Paginator
	private DynamicDataModel<AipPacientes> dataModel;

	private Integer numeroProntuario;
	private String nomePaciente;
	private Date periodoAltaInicio;
	private Date periodoAltaFim;
	private Date periodoConsultaInicio;
	private Date periodoConsultaFim;
	private Date periodoCirurgiaInicio;
	private Date periodoCirurgiaFim;
	private AghEquipes equipeMedica;
	private AghEspecialidades especialidade;
	private FccCentroCustos servico;
	private AghUnidadesFuncionais unidadeFuncional;
	private MbcProcedimentoCirurgicos procedimentoCirurgico;
	private String leito;

	private boolean limpandoTela;
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
		limpandoTela = false;
	}
	
	public void pesquisar() {
		// Preciso forçar a validação aqui no controller antes de realizar a
		// pesquisa, para verificar se possível realizar mesma ou não. Isso foi
		// feito para evitar que o usuário realizasse uma pesquisa inválida e
		// fosse exibido ao mesmo a caixa de resultados da pesquisa vazia.
		try {
			if(!limpandoTela){
				this.pacienteFacade.validaDadosPesquisaPacientes(this.numeroProntuario,
						this.periodoAltaInicio, this.periodoAltaFim, this.periodoConsultaInicio,
						this.periodoConsultaFim, this.periodoCirurgiaInicio, this.periodoCirurgiaFim,
						this.nomePaciente, this.equipeMedica, this.especialidade, this.servico,
						this.unidadeFuncional, this.procedimentoCirurgico, this.leito);
				this.dataModel.reiniciarPaginator();
			}
			limpandoTela = false;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public String voltar(){
		limparPesquisa();
		return "voltar";
	}

	public void limparPesquisa() {
		this.numeroProntuario = null;
		this.periodoAltaInicio = null;
		this.periodoAltaFim = null;
		this.periodoConsultaInicio = null;
		this.periodoConsultaFim = null;
		this.periodoCirurgiaInicio = null;
		this.periodoCirurgiaFim = null;
		this.nomePaciente = null;
		this.equipeMedica = null;
		this.especialidade = null;
		this.servico = null;
		this.unidadeFuncional = null;
		this.procedimentoCirurgico = null;
		this.leito = null;
		limpandoTela = true;
	}

	@Override
	public Long recuperarCount() { 
		if(!limpandoTela){
			try {
				return pacienteFacade.pesquisaPacientesCount(this.numeroProntuario,
						this.periodoAltaInicio, this.periodoAltaFim, this.periodoConsultaInicio,
						this.periodoConsultaFim, this.periodoCirurgiaInicio, this.periodoCirurgiaFim,
						this.nomePaciente, this.equipeMedica, this.especialidade, this.servico,
						this.unidadeFuncional, this.procedimentoCirurgico, this.leito);
			}catch(ApplicationBusinessException e){
				apresentarExcecaoNegocio(e);
			}
		}
		return 0L;
	}

	@Override
	public List<AipPacientes> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		List<AipPacientes> result = null; 
		
		if(!limpandoTela){
			try {
				result = this.pacienteFacade.pesquisaPacientes(firstResult, maxResult,
						orderProperty, asc, this.numeroProntuario, this.periodoAltaInicio,
						this.periodoAltaFim, this.periodoConsultaInicio, this.periodoConsultaFim,
						this.periodoCirurgiaInicio, this.periodoCirurgiaFim, this.nomePaciente,
						this.equipeMedica, this.especialidade, this.servico, this.unidadeFuncional,
						this.procedimentoCirurgico, this.leito);
	
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
		}
		
		return result;
	}

	public List<AghEquipes> pesquisarEquipesMedica(String objPesquisa) {
		return aghuFacade.pesquisarEquipesPorNomeOuCodigo((String) objPesquisa, 25);
	}

	public List<AghEspecialidades> pesquisarEspecialidades(String objPesquisa) {
		return aghuFacade.listarPorSigla((String) objPesquisa);
	}

	public List<FccCentroCustos> pesquisarServicos(String objPesquisa) {
		return centroCustoFacade.pesquisarCentroCustosPorCodigoDescricao((String) objPesquisa);
	}

	public List<AghUnidadesFuncionais> pesquisarUnidades(String objPesquisa) {
		return cadastrosBasicosInternacaoFacade.pesquisarUnidadeFuncionalPorCodigoPorAndarAlaDescricaoAtivasInativas((String) objPesquisa);
	}

	public List<MbcProcedimentoCirurgicos> pesquisarProcedimentosCirurgicos(String objPesquisa) {
		return blocoCirurgicoFacade
				.pesquisarProcedimentosCirurgicosPorCodigoDescricao((String) objPesquisa);
	}
	
	public Integer getNumeroProntuario() {
		return numeroProntuario;
	}

	public void setNumeroProntuario(Integer numeroProntuario) {
		this.numeroProntuario = numeroProntuario;
	}

	public Date getPeriodoAltaInicio() {
		return periodoAltaInicio;
	}

	public void setPeriodoAltaInicio(Date periodoAltaInicio) {
		this.periodoAltaInicio = periodoAltaInicio;
	}

	public Date getPeriodoAltaFim() {
		return periodoAltaFim;
	}

	public void setPeriodoAltaFim(Date periodoAltaFim) {
		this.periodoAltaFim = periodoAltaFim;
	}

	public Date getPeriodoConsultaInicio() {
		return periodoConsultaInicio;
	}

	public void setPeriodoConsultaInicio(Date periodoConsultaInicio) {
		this.periodoConsultaInicio = periodoConsultaInicio;
	}

	public Date getPeriodoConsultaFim() {
		return periodoConsultaFim;
	}

	public void setPeriodoConsultaFim(Date periodoConsultaFim) {
		this.periodoConsultaFim = periodoConsultaFim;
	}

	public Date getPeriodoCirurgiaInicio() {
		return periodoCirurgiaInicio;
	}

	public void setPeriodoCirurgiaInicio(Date periodoCirurgiaInicio) {
		this.periodoCirurgiaInicio = periodoCirurgiaInicio;
	}

	public Date getPeriodoCirurgiaFim() {
		return periodoCirurgiaFim;
	}

	public void setPeriodoCirurgiaFim(Date periodoCirurgiaFim) {
		this.periodoCirurgiaFim = periodoCirurgiaFim;
	}

	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public AghEquipes getEquipeMedica() {
		return equipeMedica;
	}

	public void setEquipeMedica(AghEquipes equipeMedica) {
		this.equipeMedica = equipeMedica;
	}

	public AghEspecialidades getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(AghEspecialidades especialidade) {
		this.especialidade = especialidade;
	}

	public FccCentroCustos getServico() {
		return servico;
	}

	public void setServico(FccCentroCustos servico) {
		this.servico = servico;
	}

	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}

	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	public MbcProcedimentoCirurgicos getProcedimentoCirurgico() {
		return procedimentoCirurgico;
	}

	public void setProcedimentoCirurgico(MbcProcedimentoCirurgicos procedimentoCirurgico) {
		this.procedimentoCirurgico = procedimentoCirurgico;
	}

	public String getLeito() {
		return leito;
	}

	public void setLeito(String leito) {
		this.leito = leito;
	}
	
	public DynamicDataModel<AipPacientes> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AipPacientes> dataModel) {
		this.dataModel = dataModel;
	}

	public boolean isLimpandoTela() {
		return limpandoTela;
	}

	public void setLimpandoTela(boolean limpandoTela) {
		this.limpandoTela = limpandoTela;
	}
}