package br.gov.mec.aghu.blococirurgico.portalplanejamento.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;

import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.business.IBlocoCirurgicoPortalPlanejamentoFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.dominio.DominioSituacaoAgendas;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgs;
import br.gov.mec.aghu.model.MbcSalaCirurgica;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.CoreUtil;
//import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class TrocarLocalEspEquipeListaEsperaController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 8662537997409376098L;
	
	@EJB
	private IBlocoCirurgicoPortalPlanejamentoFacade blocoCirurgicoPortalPlanejamentoFacade;
	@Inject
	private AtualizarEscalaPortalAgendamentoController atualizarEscalaPortalAgendamentoController;
	@EJB
	private IAghuFacade aghuFacade;
	@EJB
	private IPacienteFacade pacienteFacade;
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	@Inject
	private PortalPesquisaCirurgiasController portalPesquisaCirurgiasController;

	@Inject
	private PesquisaAgendaCirurgiaController pesquisaAgendaCirurgiaController;
	
	@Inject
	private VisualizaListaEsperaAgendaMedicoController visualizaListaEsperaAgendaMedicoController;
	
	@Inject
	private CirurgiasCanceladasAgendaMedicoController cirurgiasCanceladasAgendaMedicoController;
	
	@Inject
	private DetalhamentoPortalAgendamentoController detalhamentoPortalAgendamentoController;
	
	private final String DETALHAMENTO_PORTAL = "blococirurgico-detalhamentoPortalAgendamento";
	private final String ATUALIZAR_ESCALA = "atualizarEscalaPortalPlanejamento";
	private final String LISTA_CANCELADOS = "blococirurgico-cirurgiasCanceladasAgendaMedico";
	private final String LISTA_ESPERA = "blococirurgico-visualizaListaEsperaAgendaMedico";
	private final String PESQUISA_AGENDA_CIRURGIA = "blococirurgico-pesquisaAgendaCirurgia";
	
	private Integer agdSeq;
	private String cameFrom;
	private MbcAgendas agenda;
	
	//Componente pesquisaPaciente
	private Integer codPac;
	private Integer prontuario;
	private AipPacientes paciente;
	
	private String descricaoProcedimento;
	
	private List<MbcSalaCirurgica> listaSalasCirurgicas;
	private Boolean readOnlySala;
	private AghUnidadesFuncionais local;
	
	public void iniciar() {
		agenda = this.blocoCirurgicoPortalPlanejamentoFacade.obterAgendaPorAgdSeq(agdSeq, 
				new Enum[] {MbcAgendas.Fields.PACIENTE, MbcAgendas.Fields.PROCEDIMENTO, MbcAgendas.Fields.UNF, 
				MbcAgendas.Fields.PROF_ATUA_UNID_CIRGS, MbcAgendas.Fields.ESP_PROC_CIRGS}, 
				new Enum[] {MbcAgendas.Fields.ESPECIALIDADE, MbcAgendas.Fields.SALA_CIRURGICA, MbcAgendas.Fields.CONVENIO_SAUDE_PLANO});
		
		MbcProcedimentoCirurgicos procedimento = this.blocoCirurgicoFacade.obterMbcProcedimentoCirurgicosPorId(agenda.getEspProcCirgs().getId().getPciSeq());
		RapServidores servidor = this.registroColaboradorFacade.obterServidorPessoa(agenda.getProfAtuaUnidCirgs().getRapServidores());
		
		agenda.getProfAtuaUnidCirgs().setRapServidores(servidor);
		descricaoProcedimento = CoreUtil.capitalizaTextoFormatoAghu(procedimento.getDescricao());
		paciente = agenda.getPaciente();
		local = agenda.getUnidadeFuncional();
		codPac = paciente.getCodigo();
		prontuario = paciente.getProntuario();
		popularCampoSala();
	}

	public void removerEquipe() {
		agenda.setProfAtuaUnidCirgs(null);
		limparComboSala();
	}
	
	private void popularCampoSala() {
		readOnlySala = (DominioSituacaoAgendas.CA.equals(agenda.getIndSituacao())
				|| DominioSituacaoAgendas.LE.equals(agenda.getIndSituacao()));
		pesquisarSalas();
	}

	public List<AghEspecialidades> pesquisarEspecialidade(String param) {
		return this.returnSGWithCount(this.aghuFacade.pesquisarEspecialidadeGenerica((String) param, 100),pesquisarEspecialidadeCount(param));
	}
	
	public Long pesquisarEspecialidadeCount(String param) {
		return this.aghuFacade.pesquisarEspecialidadeGenericaCount((String) param);
	}
	
	public List<MbcProfAtuaUnidCirgs> pesquisarEquipe(String param) {
		return this.returnSGWithCount(this.blocoCirurgicoPortalPlanejamentoFacade.buscarEquipeMedicaParaMudancaNaAgenda(
				(String) param, (agenda.getUnidadeFuncional() != null) ? agenda.getUnidadeFuncional().getSeq() : null, 
				(agenda.getEspecialidade() != null) ? agenda.getEspecialidade().getSeq() : null),pesquisarEquipeCount(param));
	}
	
	public Long pesquisarEquipeCount(String param) {
		return this.blocoCirurgicoPortalPlanejamentoFacade.buscarEquipeMedicaParaMudancaNaAgendaCount(
				(String) param, (agenda.getUnidadeFuncional() != null) ? agenda.getUnidadeFuncional().getSeq() : null, 
				(agenda.getEspecialidade() != null) ? agenda.getEspecialidade().getSeq() : null);
	}
	
	private void pesquisarSalas() {
		if(DominioSituacaoAgendas.AG.equals(agenda.getIndSituacao())
				|| DominioSituacaoAgendas.ES.equals(agenda.getIndSituacao())) {
			listaSalasCirurgicas = this.blocoCirurgicoPortalPlanejamentoFacade.buscarSalasDisponiveisParaTrocaNaAgenda(agenda.getDtAgenda(), 
				agenda.getUnidadeFuncional().getSeq(), agenda.getEspecialidade().getSeq(), agenda.getProfAtuaUnidCirgs().getId().getSerMatricula(),
				agenda.getProfAtuaUnidCirgs().getId().getSerVinCodigo(), agenda.getProfAtuaUnidCirgs().getId().getUnfSeq(), agenda.getProfAtuaUnidCirgs().getId().getIndFuncaoProf());
		}
	}	
		
	public String voltar() {
		if(cameFrom.equals(PESQUISA_AGENDA_CIRURGIA) || cameFrom.equals(LISTA_CANCELADOS) || cameFrom.equals(LISTA_ESPERA)) {
			cameFrom = PESQUISA_AGENDA_CIRURGIA;
		}
		
		agenda = null;
		descricaoProcedimento = null;
		paciente = null;
		local = null;
		codPac = null;
		prontuario = null;
		agdSeq = null;
		
		return cameFrom;
	}
	
	public String gravar() {
		try {
			blocoCirurgicoFacade.gravarTrocaLocalEspecilidadeEquipeSala(agenda, cameFrom);
			this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_TROCAR_AGENDA",
					agenda.getPaciente().getNome());
			if(cameFrom.equals(PESQUISA_AGENDA_CIRURGIA)) {
				portalPesquisaCirurgiasController.inicio();
				pesquisaAgendaCirurgiaController.pesquisar();
			} else if(cameFrom.equals(DETALHAMENTO_PORTAL)) {
				detalhamentoPortalAgendamentoController.buscarDetalhamento();
				detalhamentoPortalAgendamentoController.selecionarItemAgenda(this.agdSeq);
			}else if(cameFrom.equals(ATUALIZAR_ESCALA)) {
				atualizarEscalaPortalAgendamentoController.setReadOnlyAlterarEsp(Boolean.TRUE);
				//RN1
				atualizarEscalaPortalAgendamentoController.iniciar();
				// p_atualiza_hora_inicio_escala
				atualizarEscalaPortalAgendamentoController.atualizaHoraEscala();
			} else if(cameFrom.equals(LISTA_ESPERA)){
				cameFrom = "blococirurgico-pesquisaAgendaCirurgia";
				visualizaListaEsperaAgendaMedicoController.recebeParametros(
						pesquisaAgendaCirurgiaController.getEquipe().getId().getSerMatricula(), 
						pesquisaAgendaCirurgiaController.getEquipe().getId().getSerVinCodigo(), 
						pesquisaAgendaCirurgiaController.getEquipe().getId().getUnfSeq(), 
						pesquisaAgendaCirurgiaController.getEquipe().getId().getIndFuncaoProf(), 
						pesquisaAgendaCirurgiaController.getEspecialidade().getSeq(), 
						pesquisaAgendaCirurgiaController.getUnidadeFuncional().getSeq(), 
						pesquisaAgendaCirurgiaController.getPacCodigo());
				
				visualizaListaEsperaAgendaMedicoController.getDataModelEspera().reiniciarPaginator();
			} else if(cameFrom.equals(LISTA_CANCELADOS)){
				cameFrom = "blococirurgico-pesquisaAgendaCirurgia";
				cirurgiasCanceladasAgendaMedicoController.recebeParametros(
						pesquisaAgendaCirurgiaController.getEquipe().getId().getSerMatricula(), 
						pesquisaAgendaCirurgiaController.getEquipe().getId().getSerVinCodigo(), 
						pesquisaAgendaCirurgiaController.getEquipe().getId().getUnfSeq(), 
						pesquisaAgendaCirurgiaController.getEquipe().getId().getIndFuncaoProf(), 
						pesquisaAgendaCirurgiaController.getEspecialidade().getSeq(), 
						pesquisaAgendaCirurgiaController.getUnidadeFuncional().getSeq(), 
						pesquisaAgendaCirurgiaController.getPacCodigo());
				
				cirurgiasCanceladasAgendaMedicoController.getDataModelCancelados().reiniciarPaginator();
			}
			return cameFrom;
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	public void pesquisaPaciente(ValueChangeEvent event){
		try {
			paciente = pacienteFacade.pesquisarPacienteComponente(event.getNewValue(), event.getComponent().getId());
		}catch(BaseException e){
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void carregarComboSala() {
		if(agenda.getUnidadeFuncional() != null && agenda.getProfAtuaUnidCirgs() != null) {
			popularCampoSala();
		}
	}
	
	public void limparComboSala() {
		listaSalasCirurgicas = null;
		agenda.setSalaCirurgica(null);
	}
	
	public List<AghUnidadesFuncionais> pesquisarLocal(String param){
		return this.returnSGWithCount(this.aghuFacade.listarUnidadesFuncionaisPorUnidadeExecutora((String) param, ConstanteAghCaractUnidFuncionais.UNID_EXECUTORA_CIRURGIAS, true),pesquisarLocalContador(param));
	}
	
	public Long pesquisarLocalContador(String param){
		return this.aghuFacade.listarUnidadesFuncionaisPorUnidadeExecutoraCount((String) param, ConstanteAghCaractUnidFuncionais.UNID_EXECUTORA_CIRURGIAS, true);
	}
	
	public Integer getAgdSeq() {
		return agdSeq;
	}

	public void setAgdSeq(Integer agdSeq) {
		this.agdSeq = agdSeq;
	}

	public MbcAgendas getAgenda() {
		return agenda;
	}

	public void setAgenda(MbcAgendas agenda) {
		this.agenda = agenda;
	}

	public Integer getCodPac() {
		return codPac;
	}

	public void setCodPac(Integer codPac) {
		this.codPac = codPac;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}

	public Boolean getReadOnlySala() {
		return readOnlySala;
	}

	public void setReadOnlySala(Boolean readOnlySala) {
		this.readOnlySala = readOnlySala;
	}

	public List<MbcSalaCirurgica> getListaSalasCirurgicas() {
		return listaSalasCirurgicas;
	}

	public void setListaSalasCirurgicas(List<MbcSalaCirurgica> listaSalasCirurgicas) {
		this.listaSalasCirurgicas = listaSalasCirurgicas;
	}

	public String getDescricaoProcedimento() {
		return descricaoProcedimento;
	}

	public void setDescricaoProcedimento(String descricaoProcedimento) {
		this.descricaoProcedimento = descricaoProcedimento;
	}

	public AghUnidadesFuncionais getLocal() {
		return local;
	}

	public void setLocal(AghUnidadesFuncionais local) {
		this.local = local;
	}

	public String getCameFrom() {
		return cameFrom;
	}

	public void setCameFrom(String cameFrom) {
		this.cameFrom = cameFrom;
	}
	
}
