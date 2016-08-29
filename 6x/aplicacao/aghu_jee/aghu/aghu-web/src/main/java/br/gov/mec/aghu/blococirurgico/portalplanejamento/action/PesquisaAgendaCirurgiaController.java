package br.gov.mec.aghu.blococirurgico.portalplanejamento.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;

import org.primefaces.event.TabChangeEvent;

import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.business.IBlocoCirurgicoPortalPlanejamentoFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgs;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgsId;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * #27417
 */

public class PesquisaAgendaCirurgiaController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final long serialVersionUID = 8377124220277489320L;

	@EJB
	private IPacienteFacade pacienteFacade;

	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;

	@EJB
	private IBlocoCirurgicoPortalPlanejamentoFacade blocoCirurgicoPortalPlanejamentoFacade;
	
	@Inject
	private VisualizaListaEsperaAgendaMedicoController visualizaListaEsperaAgendaMedicoController;

	@Inject
	private CirurgiasCanceladasAgendaMedicoController cirurgiasCanceladasAgendaMedicoController;
	
	@Inject
	private DetalhamentoPortalAgendamentoController detalhamentoPortalAgendamentoController;

	private AghUnidadesFuncionais unidadeFuncional;
	private AghEspecialidades especialidade;
	private MbcProfAtuaUnidCirgs equipe;
	private Integer pacCodigo;
//	private Integer pacProntuario;
//	private String pacNome;
	private AipPacientes paciente;
	private Boolean pesquisaEspecialidadeLiberada;
	private Boolean pesquisaEquipeLiberada;
	private Boolean exibirLista = false;
	private Integer abaAtiva;
	private String voltarPara;
	
	private Short seqUnidadeFuncional;
	private Short seqEspecialidade;
	private Integer serMatriculaEquipe;
	private Short serVinCodigoEquipe;
	private Short unfSeqEquipe;
	private DominioFuncaoProfissional indFuncaoProfEquipe;
	private static final String TAB_1 = "tab1";
	private static final String TAB_2 = "tab2";

	private static final String PESQUISA_FONETICA = "paciente-pesquisaPacienteComponente";
	
	private final String DETALHAMENTO_PORTAL = "blococirurgico-detalhamentoPortalAgendamento";
	
	public void inicio() {
		if (pacCodigo != null) {
			buscaPacientePorCodigo();
		}
		if(seqUnidadeFuncional != null){
			unidadeFuncional = aghuFacade.obterAghUnidadesFuncionaisPorChavePrimaria(seqUnidadeFuncional);
			liberaPesquisaEspecialidade();
		}
		if(seqEspecialidade != null){
			especialidade = aghuFacade.obterEspecialidadePorChavePrimaria(seqEspecialidade);
			liberaPesquisaEquipe();
		}
		if(serMatriculaEquipe != null && serVinCodigoEquipe != null && unfSeqEquipe != null && indFuncaoProfEquipe != null){
			MbcProfAtuaUnidCirgsId idEquipe = new MbcProfAtuaUnidCirgsId(serMatriculaEquipe, serVinCodigoEquipe, unfSeqEquipe, indFuncaoProfEquipe);
			equipe = blocoCirurgicoFacade.obterMbcProfAtuaUnidCirgs(idEquipe);
		}
		if(unidadeFuncional != null && especialidade != null && equipe != null){
			pesquisar();
		}
		abaAtiva = 0;

	}

	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncional(String pesquisa) {
		return this.returnSGWithCount(aghuFacade.pesquisarUnidadesFuncionaisUnidadeExecutoraCirurgias(pesquisa), 
				aghuFacade.pesquisarUnidadesFuncionaisUnidadeExecutoraCirurgiasCount(pesquisa));
	}

	public List<AghEspecialidades> pesquisarEspecialidade(String parametroPesquisa) {
		return this.returnSGWithCount(aghuFacade.pesquisarEspecialidadeGenerica((String) parametroPesquisa, 100),pesquisarEspecialidadeCount(parametroPesquisa));
	}

	public Long pesquisarEspecialidadeCount(String parametroPesquisa) {
		return aghuFacade.pesquisarEspecialidadeGenericaCount((String) parametroPesquisa);
	}

	public List<MbcProfAtuaUnidCirgs> pesquisarEquipe(String parametroPesquisa) {
		return this.returnSGWithCount(blocoCirurgicoPortalPlanejamentoFacade.buscarEquipeMedicaParaMudancaNaAgenda((String) parametroPesquisa, unidadeFuncional.getSeq(), especialidade.getSeq()),pesquisarEquipeCount(parametroPesquisa));
	}

	public Long pesquisarEquipeCount(String parametroPesquisa) {
		return blocoCirurgicoPortalPlanejamentoFacade.buscarEquipeMedicaParaMudancaNaAgendaCount((String) parametroPesquisa, unidadeFuncional.getSeq(), especialidade.getSeq());
	}

	public void liberaPesquisaEspecialidade() {
		pesquisaEspecialidadeLiberada = true;
		alteraValorFiltroUnidadeFuncional();
	}

	public void restringePesquisaEspecialidade() {
		pesquisaEspecialidadeLiberada = false;
		especialidade = null;
		seqEspecialidade = null;
		restringePesquisaEquipe();
	}

	public void liberaPesquisaEquipe() {
		pesquisaEquipeLiberada = true;
		alteraValorFiltroEspecialidade();
	}

	public void alteraValorFiltroEspecialidade(){
		if (especialidade != null){
			seqEspecialidade = especialidade.getSeq();
		}
		
	}

	public String redirecionarPesquisaFonetica() {
		return PESQUISA_FONETICA;
	}
	
	public void alteraValorFiltroUnidadeFuncional(){
		if (unidadeFuncional != null){
			seqUnidadeFuncional = unidadeFuncional.getSeq();
		}
		
	}
	
	public void alteraValorFiltroEquipe(){
		if (equipe != null && equipe.getId() != null){
			serMatriculaEquipe = equipe.getId().getSerMatricula();
			serVinCodigoEquipe = equipe.getId().getSerVinCodigo();
			unfSeqEquipe = equipe.getId().getUnfSeq();
			indFuncaoProfEquipe = equipe.getId().getIndFuncaoProf();
		}
	}
	
	public void restringePesquisaEquipe() {
		pesquisaEquipeLiberada = false;
		equipe = null;
		serMatriculaEquipe = null;
		serVinCodigoEquipe = null;
		unfSeqEquipe = null;
		indFuncaoProfEquipe = null;
	}
	
	public void pesquisaPaciente(ValueChangeEvent event){
		try {
			paciente = pacienteFacade.pesquisarPacienteComponente(event.getNewValue(), event.getComponent().getId());
		}catch(BaseException e){
			apresentarExcecaoNegocio(e);
		}
	}

	public void buscaPacientePorCodigo() {
		if (pacCodigo != null) {
			carregaPaciente(pacienteFacade.buscaPaciente(pacCodigo));
		} else {
			carregaPaciente(null);
		}
	}

//	public void buscaPacientePorProntuario() {
//		if (pacProntuario != null) {
//			carregaPaciente(pacienteFacade.obterPacientePorProntuario(pacProntuario));
//		} else {
//			carregaPaciente(null);
//		}
//	}

	public void carregaPaciente(AipPacientes paciente) {
		if (paciente != null) {
			pacCodigo = paciente.getCodigo();
//			pacProntuario = paciente.getProntuario();
//			pacNome = paciente.getNome();
			this.paciente = paciente;
		} else {
			pacCodigo = null;
//			pacProntuario = null;
//			pacNome = null;
			paciente = null;
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_NENHUM_PACIENTE_ENCONTRADO");
		}
	}
	
	public String pesquisar() {
		
		if (unidadeFuncional != null && especialidade != null && equipe != null) {
			
			if (paciente != null && paciente.getCodigo() != null) {
				pacCodigo = paciente.getCodigo();
			} else {
				pacCodigo = null;
			}

			exibirLista = true;
			cirurgiasCanceladasAgendaMedicoController.recebeParametros(equipe.getId().getSerMatricula(), equipe.getId().getSerVinCodigo(), equipe.getId().getUnfSeq(), equipe.getId().getIndFuncaoProf(), especialidade.getSeq(), unidadeFuncional.getSeq(), pacCodigo);
			cirurgiasCanceladasAgendaMedicoController.getDataModelCancelados().reiniciarPaginator();
			visualizaListaEsperaAgendaMedicoController.recebeParametros(equipe.getId().getSerMatricula(), equipe.getId().getSerVinCodigo(), equipe.getId().getUnfSeq(), equipe.getId().getIndFuncaoProf(), especialidade.getSeq(), unidadeFuncional.getSeq(), pacCodigo);
			visualizaListaEsperaAgendaMedicoController.getDataModelEspera().reiniciarPaginator();

			
		} else {
			if (unidadeFuncional != null && especialidade == null) {
				liberaPesquisaEspecialidade();
			} else if (unidadeFuncional != null && especialidade != null && equipe == null) {
				liberaPesquisaEquipe();
			}
		}
			
		return null;
	}
	
	public String voltar() {

		if (DETALHAMENTO_PORTAL.equals(voltarPara)) {
			detalhamentoPortalAgendamentoController.buscarDetalhamento();
		}
		
//		reiniciarPaginator(CirurgiasCanceladasAgendaMedicoController.class);
//		reiniciarPaginator(VisualizaListaEsperaAgendaMedicoController.class);
		pacCodigo = null; 
		seqUnidadeFuncional = null; 
		seqEspecialidade = null; 
		serMatriculaEquipe = null;
		serVinCodigoEquipe = null;
		unfSeqEquipe = null;
		indFuncaoProfEquipe = null; 
//		pacProntuario = null;
//		pacNome = null;
		paciente = null;
		unidadeFuncional = null;
		especialidade = null;
		equipe = null;
		exibirLista = false;
		return voltarPara;
	}	
	
	public String limpar() {
		especialidade = null;
		equipe = null;
		pacCodigo = null;
//		pacProntuario = null;
//		pacNome = null;
		paciente = null;
		restringePesquisaEspecialidade();
		exibirLista = false;
		if (seqUnidadeFuncional != null) {
			unidadeFuncional = aghuFacade.obterAghUnidadesFuncionaisPorChavePrimaria(seqUnidadeFuncional);
			liberaPesquisaEspecialidade();
		} else {
			unidadeFuncional = null;
		}
		return null;
	}
	
	public void setAbaAtiva(Integer aba) {
		abaAtiva = aba;
	}
	
	public void tabChange(TabChangeEvent event) {
		if(event != null && event.getTab() != null) {
			if(TAB_1.equals(event.getTab().getId())) {
				abaAtiva = 0;
			} else if(TAB_2.equals(event.getTab().getId())) {
				abaAtiva = 1;
			}
		}
	}

	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}

	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	public AghEspecialidades getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(AghEspecialidades especialidade) {
		this.especialidade = especialidade;
	}

	public MbcProfAtuaUnidCirgs getEquipe() {
		return equipe;
	}

	public void setEquipe(MbcProfAtuaUnidCirgs equipe) {
		this.equipe = equipe;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

//	public Integer getPacProntuario() {
//		return pacProntuario;
//	}
//
//	public void setPacProntuario(Integer pacProntuario) {
//		this.pacProntuario = pacProntuario;
//	}
//
//	public String getPacNome() {
//		return pacNome;
//	}
//
//	public void setPacNome(String pacNome) {
//		this.pacNome = pacNome;
//	}

	public Boolean getPesquisaEspecialidadeLiberada() {
		return pesquisaEspecialidadeLiberada;
	}

	public void setPesquisaEspecialidadeLiberada(Boolean pesquisaEspecialidadeLiberada) {
		this.pesquisaEspecialidadeLiberada = pesquisaEspecialidadeLiberada;
	}

	public Boolean getPesquisaEquipeLiberada() {
		return pesquisaEquipeLiberada;
	}

	public void setPesquisaEquipeLiberada(Boolean pesquisaEquipeLiberada) {
		this.pesquisaEquipeLiberada = pesquisaEquipeLiberada;
	}

	public Boolean getExibirLista() {
		return exibirLista;
	}

	public void setExibirLista(Boolean exibirLista) {
		this.exibirLista = exibirLista;
	}

	public Integer getAbaAtiva() {
		return abaAtiva;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public Short getSeqUnidadeFuncional() {
		return seqUnidadeFuncional;
	}

	public void setSeqUnidadeFuncional(Short seqUnidadeFuncional) {
		this.seqUnidadeFuncional = seqUnidadeFuncional;
	}

	public Short getSeqEspecialidade() {
		return seqEspecialidade;
	}

	public void setSeqEspecialidade(Short seqEspecialidade) {
		this.seqEspecialidade = seqEspecialidade;
	}

	public Integer getSerMatriculaEquipe() {
		return serMatriculaEquipe;
	}

	public void setSerMatriculaEquipe(Integer serMatriculaEquipe) {
		this.serMatriculaEquipe = serMatriculaEquipe;
	}

	public Short getSerVinCodigoEquipe() {
		return serVinCodigoEquipe;
	}

	public void setSerVinCodigoEquipe(Short serVinCodigoEquipe) {
		this.serVinCodigoEquipe = serVinCodigoEquipe;
	}

	public Short getUnfSeqEquipe() {
		return unfSeqEquipe;
	}

	public void setUnfSeqEquipe(Short unfSeqEquipe) {
		this.unfSeqEquipe = unfSeqEquipe;
	}

	public DominioFuncaoProfissional getIndFuncaoProfEquipe() {
		return indFuncaoProfEquipe;
	}

	public void setIndFuncaoProfEquipe(DominioFuncaoProfissional indFuncaoProfEquipe) {
		this.indFuncaoProfEquipe = indFuncaoProfEquipe;
	}

	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}

}
