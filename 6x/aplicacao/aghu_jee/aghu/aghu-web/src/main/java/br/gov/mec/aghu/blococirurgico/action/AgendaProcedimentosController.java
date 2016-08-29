package br.gov.mec.aghu.blococirurgico.action;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.cadastroapoio.action.AgendarProcedimentosEletUrgOuEmergPaginatorController;
import br.gov.mec.aghu.blococirurgico.cadastroapoio.business.IBlocoCirurgicoCadastroApoioFacade;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.action.DetalhamentoPortalAgendamentoController;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.business.IBlocoCirurgicoPortalPlanejamentoFacade;
import br.gov.mec.aghu.blococirurgico.vo.AlertaModalVO;
import br.gov.mec.aghu.blococirurgico.vo.CirurgiaTelaAnestesiaVO;
import br.gov.mec.aghu.blococirurgico.vo.CirurgiaTelaProcedimentoVO;
import br.gov.mec.aghu.blococirurgico.vo.CirurgiaTelaProfissionalVO;
import br.gov.mec.aghu.blococirurgico.vo.CirurgiaTelaVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.SecurityController;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.core.utils.StringUtil;
import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.dominio.DominioGrupoConvenio;
import br.gov.mec.aghu.dominio.DominioIndRespProc;
import br.gov.mec.aghu.dominio.DominioLadoCirurgiaAgendas;
import br.gov.mec.aghu.dominio.DominioNaturezaFichaAnestesia;
import br.gov.mec.aghu.dominio.DominioOrigemPacienteCirurgia;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoPlano;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.faturamento.cadastrosapoio.business.IFaturamentoApoioFacade;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.internacao.pesquisa.business.IPesquisaInternacaoFacade;
import br.gov.mec.aghu.model.AelProjetoPesquisas;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghProfEspecialidades;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.FatConvenioSaudePlanoId;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.model.MbcAnestesiaCirurgiasId;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcEspecialidadeProcCirgs;
import br.gov.mec.aghu.model.MbcEspecialidadeProcCirgsId;
import br.gov.mec.aghu.model.MbcProcEspPorCirurgiasId;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgs;
import br.gov.mec.aghu.model.MbcProfCirurgiasId;
import br.gov.mec.aghu.model.MbcSalaCirurgica;
import br.gov.mec.aghu.model.MbcSolicitacaoCirurgiaPosEscala;
import br.gov.mec.aghu.model.MbcTipoAnestesias;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.model.VMbcProcEsp;
import br.gov.mec.aghu.model.VRapServidorConselho;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.vo.AgendaProcedimentoPesquisaProfissionalVO;

/**
 * Controller de #22460: Agendar procedimentos eletivo, urgência ou emergência
 * 
 * @author aghu
 * 
 */
@SuppressWarnings({"PMD.AghuTooManyMethods", "PMD.ExcessiveClassLength" })
public class AgendaProcedimentosController extends ActionController {

	private static final String CAMPO_OBRIGATORIO = "CAMPO_OBRIGATORIO";

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(AgendaProcedimentosController.class);

	private static final long serialVersionUID = 3661785096909101631L;

	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private IBlocoCirurgicoCadastroApoioFacade blocoCirurgicoCadastroApoioFacade;

	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;

	@EJB
	private IPacienteFacade pacienteFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@EJB
	private IParametroFacade parametroFacade;

	@EJB
	private IExamesFacade examesFacade;

	@EJB
	private IFaturamentoApoioFacade faturamentoApoioFacade;

	@EJB
	private IInternacaoFacade internacaoFacade;
	
	@EJB
	private IPesquisaInternacaoFacade pesquisaInternacaoFacade;

	@Inject
	private AgendarProcedimentosEletUrgOuEmergPaginatorController agendarProcedimentosEletUrgOuEmergPaginatorController;
	
	@Inject
	private CadastroAnotacoesController cadastroAnotacoesController;
	
	@Inject
	private DetalhaSolicitacaoHemoterapicaController detalhaSolicitacaoHemoterapicaController;
	
	@Inject
	private CadastroSolicitacoesEspeciaisController cadastroSolicitacoesEspeciaisController;
	
	@Inject
	private CadastroOrteseProteseController cadastroOrteseProteseController;
	
	@Inject
	private RelatorioNotasDeConsumoDaSalaController relatorioNotasDeConsumoDaSalaController;
	
	@Inject
	private SecurityController securityController;
	
	@Inject
	private DetalhamentoPortalAgendamentoController detalhamentoPortalAgendamentoController;
	
	@EJB
	private IBlocoCirurgicoPortalPlanejamentoFacade blocoCirurgicoPortalPlanejamentoFacade;
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;

	/*
	 * PARÂMETROS DE CONVERSAÇÃO
	 */
	private Integer crgSeq;
	private boolean emEdicao; // Modo de edição
	private Short espSeqPrincipal;
	private Integer speSeq;
	private String voltarPara;
	
	//Integração Detalhamento Portal Planejamento
	private final String DETALHAMENTO_PORTAL = "blococirurgico-detalhamentoPortalAgendamento";
	private Short agendaUnfSeq, agendaEspSeq, agendaUnfSeqSala, agendaSalaSeqp, agendaVinCodigoEquipe;
	private Integer agendaMatriculaEquipe, agendaSeq;
	private Long agendaData;
	private Boolean voltouPesqFoneticaOuDisponibilidadeHorario; //Controla o retorno da tela de disponib. de horário ou pesquisa Fonética para não carregar novamente os parâmetros.

	// Integração #22463 disp.horário
	private Short dispHorarioUnfSeq, dispHorarioEspSeq, dispHorarioSalaSeq;
	private Long dispHorarioDataAgenda, dispHorarioHoraInicio;

	private Boolean origemOk;
	

	// Retorna para a lista de pacientes da prescrição
	private boolean exibirBotaoVoltar = false, botaoImagens = false;

	/*
	 * CONTROLE DOS SLIDERS
	 */
	private boolean sliderCirurgiaAberto = true, sliderProcedimentoAberto = true, sliderProfissionaisAberto = true, sliderAnestesiasAberto = true;

	/*
	 * CAMPOS DA CIRURGIA
	 */
	private CirurgiaTelaVO vo; // A Cirurgia está neste VO
	private Date tempoPrevisto;
	private DominioNaturezaFichaAnestesia[] itensOrigemAtendimento = new DominioNaturezaFichaAnestesia[] { DominioNaturezaFichaAnestesia.ELE, DominioNaturezaFichaAnestesia.URG,
			DominioNaturezaFichaAnestesia.EMG };

	// Plano de saúde e convênio
	private FatConvenioSaudePlano plano;
	private Short convenioId; // Convênio
	private Byte planoId; // Plano

	// Prontuário da lista de pacientes da prescrição
	private AipPacientes paciente;
	private AinLeitos leito;
	private Boolean voltouPesquisaPacientes; // Determina que as validações de paciente ocorram somente após o retorno da tela do componente de pesquisa de paciente

	/*
	 * LISTAS DE PROCEDIMENTOS, PROFISSIONAIS E ANESTESIAS
	 */
	private List<CirurgiaTelaProcedimentoVO> listaProcedimentos; // PROCEDIMENTOS
	private AghEspecialidades especialidadeProcedimentoAdd; // Especialidade do item ADICIONADO na lista de procedimentos
	private VMbcProcEsp procedimentoAdd; // Procedimento do item ADICIONADO na lista de procedimentos
	private CirurgiaTelaProcedimentoVO procedimentoQuantidadeAlterada;

	private List<CirurgiaTelaProfissionalVO> listaProfissionais; // PROFISSIONAIS
	private AgendaProcedimentoPesquisaProfissionalVO profissionalAdd; // Profissional do item ADICIONADO na lista de profissionais

	private List<CirurgiaTelaAnestesiaVO> listaAnestesias; // ANESTESIAS
	private MbcTipoAnestesias tipoAnestesiaAdd; // Anestesia do item ADICIONADO na lista de anestesias

	// Parâmetros do jQuery para setar o procedimento principal e profissional responsável.
	// ATENÇÃO: Como os itens pertencem a mesma classe, somente a comparação
	// de HASH CODE será necessária para definir o selecionado
	private int procedimentoPrincipalHashCode, profissionalResponsavelPrincipalId;
	
	private Date data;

	/*
	 * CONTROLA MODAIS COM A CONFIRMAÇÃO DE AÇÕES OU PASSOS DO BOTÃO GRAVAR
	 */
	private AlertaModalVO alertaVO;
	private boolean exibirModalAlertaGravar = false; // Flag que controla a exibição da modal de alerta
	private boolean exibirModalValidacaoTempoMinimoPrevisto = false; // Flag que controla a exibição da modal de validação de tempo mínimo
	private boolean permiteAgendarSUS;
	private boolean permiteAgendarConvenio;
	
	private boolean readOnlyLado;
	private DominioLadoCirurgiaAgendas lateralidade;
	private DominioLadoCirurgiaAgendas[] itensLateralidade = new DominioLadoCirurgiaAgendas [] {
			DominioLadoCirurgiaAgendas.D,
			DominioLadoCirurgiaAgendas.E,
			DominioLadoCirurgiaAgendas.B,
			DominioLadoCirurgiaAgendas.N
		};
	
	private Boolean indPrincipal = true;
	private boolean existePrincipal = false;
	private CirurgiaTelaProcedimentoVO procedimentoLadoAlterado = new CirurgiaTelaProcedimentoVO();
	private Boolean carregaUnidadeFuncional = Boolean.TRUE;
	
	private DominioFuncaoProfissional indFuncaoProf;

	private static final String DISPONIBILIDADE_HORARIO = "blococirurgico-agendarProcedimentosEletUrgOuEmerg";
	private static final String AGENDA_PROCEDIMENTOS = "blococirurgico-agendaProcedimentos";
	private static final String CADASTRO_ANOTACOES = "cadastroAnotacoes";
	private static final String SOLICITACAO_HEMOTERAPICA = "blococirurgico-detalhaSolicitacaoHemoterapica";
	private static final String SOLICITACOES_ESPECIAIS = "blococirurgico-cadastroSolicitacoesEspeciais";
	private static final String CADASTRO_OPME = "blococirurgico-cadastroOrteseProtese";
	private static final String PESQUISA_FONETICA = "paciente-pesquisaPacienteComponente";
	

	private MbcAgendas agenda;
	
	/**
	 * Chamado no inicio da conversação
	 * @throws ApplicationBusinessException 
	 */
	public void inicio() throws ApplicationBusinessException {
	 
		// A permissão "AGENDAR CIRURGIA CONV PARTICUL" foi migrada pra agendarCirurgiaConvParticular
		if (Boolean.TRUE.equals(this.voltouPesquisaPacientes)) { // Retorno da pesquisa fonética
			this.atualizaInfoLeitosPacienteSelecionado();
		}
		this.integrarDisponibilidadeHorario(); // INTEGRAÇÃO #22463 Disp.Horário
		
		/**
		 * INTEGRAÇÃO PORTAL AGENDAMENTO
		 * Vindo da tela do Portal de Planejamento, verifica se existe cirurgia agendada
		 * através dos parâmetros agendaData e agendaSeq
		 */
		if (agendaData != null && agendaSeq != null) {
			List<MbcCirurgias> cirurgias = this.blocoCirurgicoFacade.pesquisarCirurgiasDeReserva(new Date(agendaData), agendaSeq);
			if (!cirurgias.isEmpty()){
				this.crgSeq = cirurgias.get(0).getSeq();
			}
		}
		
		if (this.vo == null) {
			this.popularPlanoConveioSusPadrao();
			this.instanciarAgendaProcedimentos(this.crgSeq); // Cria/Popula uma agenda de procedimentos
		}
		
		permiteAgendarConvenio = securityController.usuarioTemPermissao("agendarProcedimentoEletUrgEmergencia", "permiteAgendarApenasConvenioParticular");
		permiteAgendarSUS = securityController.usuarioTemPermissao("agendarProcedimentoEletUrgEmergencia", "permiteAgendarApenasSUS");
		if (!Boolean.TRUE.equals(this.emEdicao) && !Boolean.TRUE.equals(this.voltouPesqFoneticaOuDisponibilidadeHorario)) {
			this.integrarPortalAgendamento(); //INTEGRAÇÃO Portal Agendamento (novo agendamento)
		}
		
		this.setReadOnlyLado(true);
	
		// #52245
		if(agendaSeq != null){
			agenda = blocoCirurgicoPortalPlanejamentoFacade.obterAgendaPorSeq(agendaSeq);
			paciente = agenda.getPaciente();
			plano = agenda.getConvenioSaudePlano();
			procedimentoAdd = getCarregarProcedimentoPrincipal();
			if (this.vo != null) {
				// #54437
				this.vo.getCirurgia().setDataPrevisaoInicio(agenda.getDthrPrevInicio());
				tempoPrevisto = agenda.getTempoSala();
			}
			if(paciente == null){
				this.leito = null;
			}
			if(pesquisaInternacaoFacade.verificarPacienteInternado(this.getPaciente().getCodigo()) && this.getPaciente().getLeito() != null){
				this.setLeito(prescricaoMedicaFacade.pesquisarAinLeitoPorId(paciente.getLeito().getLeitoID()));
			}else{
				leito = null;
			}
		}
	}
	
	private void atualizaInfoLeitosPacienteSelecionado(){
		if(paciente == null){
			this.leito = null;
			return;
		}
		try {
			final Enum[] fetchArgsLeftJoin = {AipPacientes.Fields.LEITO};
			this.setPaciente(this.pacienteFacade.obterPacientePorCodigo(this.getPaciente().getCodigo(),null,fetchArgsLeftJoin));
			if(pesquisaInternacaoFacade.verificarPacienteInternado(this.getPaciente().getCodigo())){
				this.setLeito(paciente.getLeito());
			}else{
				leito = null;
			}
			
			final Long numeroCirurgiasAgendadasPorPacienteDia = this.blocoCirurgicoFacade.obterNumeroCirurgiasAgendadasPorPacienteDia(this.paciente.getCodigo(), vo
					.getCirurgia().getData());
			if (numeroCirurgiasAgendadasPorPacienteDia > 0) {
				// O paciente tem cirurgia marcada para o mesmo dia.
				this.apresentarMsgNegocio(Severity.WARN, "MENSAGEM_PACIENTE_CIRURGIA_AGENDADA_MESMO_DIA");
			}
		} finally {
			this.voltouPesquisaPacientes = false;
		}
	}
	
	public void pesquisaPaciente(ValueChangeEvent event){
		try {
			paciente = pacienteFacade.pesquisarPacienteComponente(event.getNewValue(), event.getComponent().getId());
		}catch(BaseException e){
			apresentarExcecaoNegocio(e);
		}
		this.atualizaInfoLeitosPacienteSelecionado();
	}
	/**
	 * Popula com o plano convênio SUS padrão
	 */
	private void popularPlanoConveioSusPadrao() {
		if (permiteAgendarSUS) {
			try {
				final AghParametros convenioSus = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_CONVENIO_SUS);
				final AghParametros convenioPlanoSus = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_PLANO_CONVENIO_PADRAO);
				if (convenioSus != null && convenioPlanoSus != null) { // Define o plano SUS automáticamente quando informado via parâmetro do sistema
					if (convenioSus.getVlrNumerico() != null) {
						this.convenioId = convenioSus.getVlrNumerico().shortValue();
					}
					if (convenioPlanoSus.getVlrNumerico() != null) {
						this.planoId = convenioPlanoSus.getVlrNumerico().byteValue();
					}
					this.plano = faturamentoApoioFacade.obterConvenioSaudePlano(this.convenioId, this.planoId);
				}
			} catch (ApplicationBusinessException e) {
				LOG.error("Excecao Capturada: ", e);
				apresentarExcecaoNegocio(e);
			}
		}
	}

	/**
	 * Instancia uma novo AgendaProcedimentosVO
	 * 
	 * @param crgSeqAgendaProcedimento
	 */
	public  void instanciarAgendaProcedimentos(Integer crgSeqAgendaProcedimento) {
		this.vo = new CirurgiaTelaVO();
		if (crgSeqAgendaProcedimento != null) {
			MbcCirurgias cirurgia = this.blocoCirurgicoFacade.obterCirurgiaPorChavePrimaria(crgSeqAgendaProcedimento, 
						new Enum[] {MbcCirurgias.Fields.PACIENTE, MbcCirurgias.Fields.UNIDADE_FUNCIONAL, MbcCirurgias.Fields.ESPECIALIDADE, 
						MbcCirurgias.Fields.SALA_CIRURGICA, MbcCirurgias.Fields.CONVENIO_SAUDE_PLANO},
						new Enum[] {MbcCirurgias.Fields.PROJETO_PESQUISAS, MbcCirurgias.Fields.ATENDIMENTO}); 
			vo.setCirurgia(cirurgia);
			this.tempoPrevisto = this.blocoCirurgicoFacade.converterTempoPrevistoHoras(cirurgia.getTempoPrevistoHoras(), cirurgia.getTempoPrevistoMinutos());
			this.plano = cirurgia.getConvenioSaudePlano();
			this.convenioId = this.plano.getId().getCnvCodigo();
			this.planoId = this.plano.getId().getSeq();
			this.paciente = cirurgia.getPaciente();
			AipPacientes pac = this.pacienteFacade.obterPacientePorCodigo(cirurgia.getPaciente().getCodigo(), null, new Enum[] {AipPacientes.Fields.LEITO});
			this.leito = pac.getLeito();
			this.especialidadeProcedimentoAdd = this.vo.getCirurgia().getEspecialidade();

			// Popula as listas
			if (cirurgia.getDigitaNotaSala()){
				this.listaProcedimentos = this.blocoCirurgicoFacade.pesquisarProcedimentosAgendaProcedimentos(this.vo.getCirurgia().getSeq(), DominioIndRespProc.NOTA);
			}
			else {
				this.listaProcedimentos = this.blocoCirurgicoFacade.pesquisarProcedimentosAgendaProcedimentos(this.vo.getCirurgia().getSeq(), DominioIndRespProc.AGND);
			}

			this.listaProfissionais = this.blocoCirurgicoFacade.pesquisarProfissionaisAgendaProcedimentos(this.vo.getCirurgia().getSeq());
			this.listaAnestesias = this.blocoCirurgicoFacade.pesquisarAnestesiasAgendaProcedimentos(this.vo.getCirurgia().getSeq());

			this.emEdicao = true;
			this.voltouPesquisaPacientes = false;
		} else {
			this.popularPlanoConveioSusPadrao();
			this.tempoPrevisto = null;
			this.paciente = null;
			this.leito = null;
			this.especialidadeProcedimentoAdd = null;
			this.procedimentoAdd = null;
			this.profissionalAdd = null;
			this.tipoAnestesiaAdd = null;

			// Instancia as listas
			this.listaProcedimentos = new ArrayList<CirurgiaTelaProcedimentoVO>();
			this.listaProfissionais = new ArrayList<CirurgiaTelaProfissionalVO>();
			this.listaAnestesias = new ArrayList<CirurgiaTelaAnestesiaVO>();

			this.vo.getCirurgia().setPrecaucaoEspecial(false);
			this.emEdicao = false;
			
			this.vo.getCirurgia().setUnidadeFuncional(this.carregarUnidadeFuncionalInicial());
			
		}

		// Seta a SOLICITAÇÃO PÓS-ESCALA quando o parâmetro de conversação speSeq (SPE_SEQ) for informado
		if (this.speSeq != null) {
			MbcSolicitacaoCirurgiaPosEscala solicitacaoCirurgiaPosEscala = this.blocoCirurgicoFacade.obterSolicitacaoCirurgiaPosEscalPorChavePrimaria(this.speSeq);
			this.vo.setSolicitacaoCirurgiaPosEscala(solicitacaoCirurgiaPosEscala);
		}
	}
	
	/**
	 * verifica se existe procedimento principal, pode ser adicionados outros procedimentos sem serem principais,
	 *  e ao ser seleciocionado um procedimento e marcado como principal mas não for adicionado a lista de procedimento pelo botão
	 *  estava gerando erro esse erro foi controlado pelo metodo abaixo
	 * @return
	 */
	private boolean verificarExisteProcedimentoPrincipal(){
		boolean retorno = false;
		for(CirurgiaTelaProcedimentoVO procedimento : listaProcedimentos){
			if(procedimento.getIndPrincipal()){
				retorno = procedimento.getIndPrincipal();
				break;
			}
		}
		return retorno;
	}

	/**
	 * Gravar (AGENDAR PROCEDIMENTO): Chama a parte 1 da RN1
	 */
	public void agendarProcedimentosParte1() {
		try {
			this.validaOrigemComPlano();
			if(!this.getOrigemOk()){
				return;
			}

			this.exibirModalAlertaGravar = false;
			this.exibirModalValidacaoTempoMinimoPrevisto = false;
			
			if (this.paciente == null) {

				this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_PACIENTE_OBRIGATORIO");
				return;
			}

			// Valida a obrigatoriedade de preencher as listas de procedimentos e profissionas
			if (this.listaProcedimentos.isEmpty()) {
				apresentarMsgNegocio("sbEspecialidadeProcedimento", Severity.ERROR, "LISTA_OBRIGATORIA", "procedimentos", "procedimento");
				return;
			}
			//Valida se existe procedimentos principais
			if(!this.verificarExisteProcedimentoPrincipal()){
				apresentarMsgNegocio("sbEspecialidadeProcedimento", Severity.ERROR, "MBC_00952");
				return;
			} 

			if (this.listaProfissionais.isEmpty()) {
				apresentarMsgNegocio("sbProfissional", Severity.ERROR, "LISTA_OBRIGATORIA", "profissionais", "profissional");
				return;
			} 

			// Seta os TEMPO PREVISTOS em HORAS e MINUTOS
			if (this.getTempoPrevisto() != null) {
				// Horas
				final String tempoPrevistoHoras = DateUtil.obterDataFormatada(this.tempoPrevisto, "HH");
				this.vo.getCirurgia().setTempoPrevistoHoras(Short.valueOf(tempoPrevistoHoras));
				// Minutos
				final String tempoPrevistoMinutos = DateUtil.obterDataFormatada(this.tempoPrevisto, "mm");
				this.vo.getCirurgia().setTempoPrevistoMinutos(Byte.valueOf(tempoPrevistoMinutos));
			}

			this.vo.getCirurgia().setConvenioSaudePlano(this.plano); // Seta convênio do plano de saúde
			this.vo.getCirurgia().setConvenioSaude(this.plano.getConvenioSaude());
			this.vo.getCirurgia().setPaciente(this.paciente); // Seta paciente
			this.vo.setListaProcedimentosVO(this.listaProcedimentos); // Seta LISTA DE PROCEDIMENTOS
			this.vo.setListaProfissionaisVO(this.listaProfissionais); // Seta LISTA DE PROFISSIONAIS
			this.vo.setListaAnestesiasVO(this.listaAnestesias); // Seta LISTA DE ANESTESIAS
			this.vo.getCirurgia().setSciSeqp(vo.getCirurgia().getSalaCirurgica().getId().getSeqp()); //Seta a informação da sala nas variaveis primiticas que guardam o valor da sala na entidade cirurgia
			this.vo.getCirurgia().setSciUnfSeq(vo.getCirurgia().getSalaCirurgica().getId().getUnfSeq());

			// PARTE 1 DE AGENDAR PROCEDIMENTOS que poderá retornar ALERTAS DE CONFIRMAÇÃO
			this.alertaVO = this.blocoCirurgicoFacade.agendarProcedimentosParte1(this.emEdicao, this.vo, super.getEnderecoRedeHostRemoto());

			exibirModalAlertaGravar = alertaVO.isExibirModalAlertaGravar();
			exibirModalValidacaoTempoMinimoPrevisto = alertaVO.isExibirModalValidacaoTempoMinimoPrevisto();
			
			if (exibirModalAlertaGravar || exibirModalValidacaoTempoMinimoPrevisto) {
				return;
			}

			// Executa a PARTE 2 DE AGENDAR PROCEDIMENTOS
			this.agendarProcedimentosParte2();

		} catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);
		} catch (UnknownHostException e) {
			LOG.error("Exceção caputada:", e);
		}
	}

    public void popularInputTempoMinimo() {
		this.tempoPrevisto = this.blocoCirurgicoFacade.converterTempoPrevistoHoras(
				this.vo.getCirurgia().getTempoPrevistoHoras(), 
				this.vo.getCirurgia().getTempoPrevistoMinutos());
		
		this.exibirModalValidacaoTempoMinimoPrevisto = false;
	}

	/**
	 * Ação do botão SIM das mensagens de alerta para confirmação da parte 1 da RN1
	 */
	public void pressionarBotaoSimModal() {
		// Verifica se a mensagem de alerta continuará o processo após um clique no BOTÃO SIM
		if (Boolean.TRUE.equals(this.alertaVO.isCancelarAlertaContinuaProcesso())) {
			if (this.alertaVO.getConvenioEncontradoCodigo() != null && this.alertaVO.getConvenioEncontradoCodigoSeq() != null) {
				// ATENÇÃO: O BOTÃO SIM NESTE CASO, ATUALIZA COM O CONVÊNIO E PLANO DA RN14
				FatConvenioSaudePlano convenioSaudePlanoEncontrado = this.internacaoFacade.obterConvenioSaudePlano(new FatConvenioSaudePlanoId(this.alertaVO
						.getConvenioEncontradoCodigo(), this.alertaVO.getConvenioEncontradoCodigoSeq()));
				this.vo.getCirurgia().setConvenioSaudePlano(convenioSaudePlanoEncontrado);
				this.vo.getCirurgia().setConvenioSaude(convenioSaudePlanoEncontrado.getConvenioSaude());
			}
		}
		this.agendarProcedimentosParte2();
	}

	/**
	 * Ação do botão NÃO das mensagens de alerta para confirmação da parte 1 da RN1
	 */
	public void pressionarBotaoNaoModal() {
		// Verifica se a mensagem de alerta continuará o processo após um clique no BOTÃO NÃO
		if (Boolean.TRUE.equals(this.alertaVO.isCancelarAlertaContinuaProcesso())) {
			// ATENÇÃO: O BOTÃO NÃO INTERFERE NO PROCESSO!
			this.agendarProcedimentosParte2(); // Chamada da parte 2
		}
		this.exibirModalAlertaGravar = false;
	}

	/**
	 * Chama a parte 2 da RN1
	 * 
	 * @return
	 */
	public void agendarProcedimentosParte2() {
		try {
			this.exibirModalAlertaGravar = false;

			// Executa a PARTE 2 DE AGENDAR PROCEDIMENTOS
			this.blocoCirurgicoFacade.agendarProcedimentosParte2(this.emEdicao, this.vo, alertaVO, super.getEnderecoRedeHostRemoto());

			// Limpa lista de ITENS REMOVIDOS
			this.vo.getListaProcedimentosRemovidos().clear();
			this.vo.getListaProfissionaisRemovidos().clear();
			this.vo.getListaAnestesiasVO().clear();

			this.instanciarAgendaProcedimentos(this.vo.getCirurgia().getSeq()); // Entra no modo edição

			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_INSERIR_AGENDAR_PROCEDIMENTOS");

			if (this.alertaVO.isSangueSolicitado()) {
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_AGENDA_HEMOTERAPICA");
			}
			
		} catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);
		} catch (UnknownHostException e) {
			LOG.error("Exceção caputada:", e);
		} 
	}
	
	/**
	 * Obtém Unidade Funcional de acordo com o cadastro do microcomputador
	 * @return
	 */
	private AghUnidadesFuncionais carregarUnidadeFuncionalInicial() {
		String nomeMicrocomputador = null;
		if(carregaUnidadeFuncional){	
			try {
				nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
				return blocoCirurgicoFacade.obterUnidadeFuncionalCirurgia(nomeMicrocomputador);
			} catch (ApplicationBusinessException e) {
				this.apresentarExcecaoNegocio(e);
			} catch (UnknownHostException e1) {
				LOG.error("Exceção capturada:", e1);
			}
		}
		return null;
	}

	/*
	 * INTEGRAÇÕES
	 */
	public String cancelar() {
		if (this.voltarPara != null && this.DETALHAMENTO_PORTAL.equals(this.voltarPara)) {
			detalhamentoPortalAgendamentoController.buscarDetalhamento();
		}
		
		limparParametros();		
		return voltarPara;
	}
	
	public void novoAgendamento(){
		aghuFacade.limparEntityManager();
		instanciarAgendaProcedimentos(null);
		indPrincipal = true;
		plano = null;
		convenioId = null;
		planoId = null; 
		existePrincipal = false;
	}

	public String redirecionarPesquisaFonetica() {
		this.voltouPesquisaPacientes = true;
		this.voltouPesqFoneticaOuDisponibilidadeHorario = true;
		return PESQUISA_FONETICA;
	}

	public String getDisponibilidadeHorario() {
		agendarProcedimentosEletUrgOuEmergPaginatorController.setPaginaVoltar(AGENDA_PROCEDIMENTOS);
		this.voltouPesqFoneticaOuDisponibilidadeHorario = true;
		return DISPONIBILIDADE_HORARIO;
	}

	public String getSolicitacaoHemoterapica() {
		detalhaSolicitacaoHemoterapicaController.setMbcCirurgiaCodigo(this.vo.getCirurgia().getSeq());
		return SOLICITACAO_HEMOTERAPICA;
	}

	public String getCadastroSolicitacoesEspeciais() {
		cadastroSolicitacoesEspeciaisController.setMbcCirurgiaCodigo(this.vo.getCirurgia().getSeq());
		return SOLICITACOES_ESPECIAIS;
	}

	public String getCadastroOrteseProtese() {
		cadastroOrteseProteseController.setMbcCirurgiaCodigo(this.vo.getCirurgia().getSeq());
		return CADASTRO_OPME;
	}

	public void getRelatorioImpNotaSala() {
		MbcCirurgias cir = this.blocoCirurgicoFacade.obterCirurgiaPorChavePrimaria(this.vo.getCirurgia().getSeq(), new Enum[] {MbcCirurgias.Fields.PACIENTE, MbcCirurgias.Fields.UNIDADE_FUNCIONAL}, new Enum[] {MbcCirurgias.Fields.AGENDA});
		relatorioNotasDeConsumoDaSalaController.setUnidadeCirurgica(cir.getUnidadeFuncional());
		relatorioNotasDeConsumoDaSalaController.setDataCirurgia(vo.getCirurgia().getData());
		relatorioNotasDeConsumoDaSalaController.setNumeroAgenda(vo.getCirurgia().getNumeroAgenda());
		relatorioNotasDeConsumoDaSalaController.setVoltarPara(AGENDA_PROCEDIMENTOS);
		relatorioNotasDeConsumoDaSalaController.setIsDirectPrint(true);
		relatorioNotasDeConsumoDaSalaController.setNsDigitada(false);
		try {
			relatorioNotasDeConsumoDaSalaController.directPrint();
		} catch (ApplicationBusinessException e) {
			LOG.error("Excecao capturada: ",e);
		}
	}

	public String getCadastroAnotacoes() {
		cadastroAnotacoesController.setMbcCirurgiaCodigo(this.vo.getCirurgia().getSeq());
		return CADASTRO_ANOTACOES;
	}

	/**
	 * INTEGRAÇÃO #22463 Disp.Horário
	 */
	private void integrarDisponibilidadeHorario() {
		if (!this.emEdicao && this.dispHorarioUnfSeq != null) {
			AghUnidadesFuncionais dispHorarioUnidadesFuncional = this.aghuFacade.obterAghUnidadesFuncionaisPorChavePrimaria(this.dispHorarioUnfSeq);
			if (this.dispHorarioEspSeq != null && this.dispHorarioEspSeq > 0) {
				AghEspecialidades especialidade = this.aghuFacade.obterAghEspecialidadesPorChavePrimaria(this.dispHorarioEspSeq);
				this.vo.getCirurgia().setEspecialidade(especialidade);
			}
			this.vo.getCirurgia().setUnidadeFuncional(dispHorarioUnidadesFuncional);
			this.vo.getCirurgia().setData(new Date(this.dispHorarioDataAgenda)); // Transforma Long para data
			this.vo.getCirurgia().setDataPrevisaoInicio(new Date(this.dispHorarioHoraInicio)); // Transforma Long para data
			MbcSalaCirurgica dispHorariosalaCirurgica = this.blocoCirurgicoCadastroApoioFacade.obterSalaCirurgicaBySalaCirurgicaId(this.dispHorarioSalaSeq, this.dispHorarioUnfSeq);
			this.vo.getCirurgia().setSalaCirurgica(dispHorariosalaCirurgica);
			
			//#41209 - Limpar as variáves para não voltar para esta data
			this.dispHorarioUnfSeq = null;
			this.dispHorarioEspSeq = null;
			this.dispHorarioSalaSeq = null;
			this.dispHorarioDataAgenda = null;
			this.dispHorarioHoraInicio = null;

		}
	}
	
	/**
	 * INTEGRAÇÃO Portal Agendamento
	 */
	private void integrarPortalAgendamento() {
		
		if(this.voltarPara != null && this.DETALHAMENTO_PORTAL.equals(this.voltarPara)) {
			if (agendaUnfSeq != null) {
				this.vo.getCirurgia().setUnidadeFuncional(aghuFacade.obterUnidadeFuncional(agendaUnfSeq));
			}
			if (agendaEspSeq != null) {
				this.vo.getCirurgia().setEspecialidade(aghuFacade.obterAghEspecialidadesPorChavePrimaria(agendaEspSeq));
				this.especialidadeProcedimentoAdd = vo.getCirurgia().getEspecialidade();
			}
			if (agendaData != null) {
				this.vo.getCirurgia().setData(new Date(agendaData));
			}
			if (agendaUnfSeqSala != null && agendaSalaSeqp != null) {
				this.vo.getCirurgia().setSalaCirurgica(blocoCirurgicoCadastroApoioFacade.obterSalaCirurgicaBySalaCirurgicaId(agendaSalaSeqp, agendaUnfSeqSala));
			}
			if (agendaMatriculaEquipe != null && agendaVinCodigoEquipe != null) {
				this.profissionalAdd = new AgendaProcedimentoPesquisaProfissionalVO();
				this.profissionalAdd.setMatricula(agendaMatriculaEquipe);
				this.profissionalAdd.setVinCodigo(agendaVinCodigoEquipe);
				this.profissionalAdd.setFuncao(indFuncaoProf);
				this.adicionarProfissional();
			}
		}
	}

	/*
	 * MÉTODOS DAS SUGGESTION BOX DAS LISTAS
	 */
	public List<AghEspecialidades> pesquisarEspecialidadesProcedimentos(String objPesquisa) {
		return this.aghuFacade.pesquisarPorNomeOuSiglaExata(objPesquisa != null ? objPesquisa : null);
	}

	public void posRemoverEspecialidadesProcedimento() {
		this.especialidadeProcedimentoAdd = null;
		this.procedimentoAdd = null;
		limparLateralidade();
	}

	public List<VMbcProcEsp> pesquisarProcedimentoCirurgicos(String objPesquisa) {
		Short espSeq = this.especialidadeProcedimentoAdd != null ? this.especialidadeProcedimentoAdd.getSeq() : null;
		Integer pjqSeq = this.vo != null && this.vo.getCirurgia().getProjetoPesquisa() != null ? this.vo.getCirurgia().getProjetoPesquisa().getSeq() : null;
		return this.returnSGWithCount(this.blocoCirurgicoFacade.pesquisarProcedimentosEspecialidadeProjeto(objPesquisa != null ? objPesquisa : null, espSeq, pjqSeq),pesquisarProcedimentoCirurgicosCount(objPesquisa));
	}

	public Long pesquisarProcedimentoCirurgicosCount(String objPesquisa) {
		Short espSeq = this.especialidadeProcedimentoAdd != null ? this.especialidadeProcedimentoAdd.getSeq() : null;
		Integer pjqSeq = this.vo != null && this.vo.getCirurgia().getProjetoPesquisa() != null ? this.vo.getCirurgia().getProjetoPesquisa().getSeq() : null;
		return this.blocoCirurgicoFacade.pesquisarProcedimentosEspecialidadeProjetoCount(objPesquisa != null ? objPesquisa : null, espSeq, pjqSeq);
	}

	public List<AgendaProcedimentoPesquisaProfissionalVO> pesquisarProfissionais(String objPesquisa) {
		Short unfSeq = this.vo != null && this.vo.getCirurgia().getUnidadeFuncional() != null ? this.vo.getCirurgia().getUnidadeFuncional().getSeq() : null;
		return this.returnSGWithCount(this.blocoCirurgicoFacade.pesquisarProfissionaisAgendaENotaProcedimento(objPesquisa, unfSeq, this.obterConselhosMedicos(), true),
				pesquisarProfissionaisCount(objPesquisa));
	}
	
	public Long pesquisarProfissionaisCount(String objPesquisa) {
		Short unfSeq = this.vo != null && this.vo.getCirurgia().getUnidadeFuncional() != null ? this.vo.getCirurgia().getUnidadeFuncional().getSeq() : null;
		return this.blocoCirurgicoFacade.pesquisarProfissionaisAgendaENotaProcedimentoCount(objPesquisa, unfSeq, this.obterConselhosMedicos(), true);
	}
	
	public List<MbcTipoAnestesias> pesquisarAnestesias(String objPesquisa) {
		return this.returnSGWithCount(blocoCirurgicoCadastroApoioFacade.pequisarTiposAnestesiaSB(objPesquisa, null), 
				this.pesquisarAnestesiasCount(objPesquisa));
	}

	public Long pesquisarAnestesiasCount(String objPesquisa) {
		return blocoCirurgicoCadastroApoioFacade.pequisarTiposAnestesiaSBCount(objPesquisa, null);
	}

    private List<String> obterConselhosMedicos(){
		try {
			List<String> retorno = new ArrayList<String>();
			String strListaSiglaConselho = this.parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_LISTA_CONSELHOS_PROF_MBC);
			List<String> listaSiglaConselho = Arrays.asList(strListaSiglaConselho.replaceAll(" ", "").split(","));
			String siglaConselhoRegionalEnf = this.parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_SIGLA_CONSELHO_REGIONAL_ENF);
            retorno.addAll(listaSiglaConselho);
            retorno.add(siglaConselhoRegionalEnf);
            return retorno;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);			
		}
		return null;
	}

	/*
	 * MÉTODOS PARA ADICIONAR ITENS NAS LISTAS
	 */
	public void adicionarProcedimento() {

		if (this.especialidadeProcedimentoAdd == null) {
			apresentarMsgNegocio("sbEspecialidadeProcedimento", Severity.ERROR, CAMPO_OBRIGATORIO, "Especialidade");
			return;
		} else if (this.procedimentoAdd == null) {
			apresentarMsgNegocio("sbProcedimento", Severity.ERROR, CAMPO_OBRIGATORIO, "Procedimento");
			return;
		} else if (!this.isReadOnlyLado() && this.lateralidade == null && this.indPrincipal && !this.existePrincipal) {
			apresentarMsgNegocio("idLateralidade", Severity.ERROR, CAMPO_OBRIGATORIO, 
					getBundle().getString("LABEL_PROCEDIMENTO_CIRURGICO_LADO"));
			return;
		}

		for (CirurgiaTelaProcedimentoVO procedimento : this.listaProcedimentos) {
			if (procedimento.getIndPrincipal()) {
				existePrincipal = true;
				break;
			}
		}

		// 1. Instância que será gravada
		CirurgiaTelaProcedimentoVO procedimentoVO = new CirurgiaTelaProcedimentoVO();

		// Resgata o procedimento de VMbcProcEsp
		MbcProcedimentoCirurgicos procedimentoCirurgico = this.blocoCirurgicoFacade.obterMbcProcedimentoCirurgicosPorId(this.procedimentoAdd.getId().getPciSeq());

		procedimentoVO.setSeqPhi(procedimentoCirurgico.getSeq());
		procedimentoVO.setDescricaoPhi(procedimentoCirurgico.getDescricao());
		procedimentoVO.setIndContaminacao(procedimentoCirurgico.getIndContaminacao());
		procedimentoVO.setRegimeProcedSus(procedimentoCirurgico.getRegimeProcedSus());
		procedimentoVO.setTempoMinimo(procedimentoCirurgico.getTempoMinimo());
		
		// 2. ID da instância que será gravada
		MbcProcEspPorCirurgiasId id = new MbcProcEspPorCirurgiasId();
		if (this.vo.getCirurgia().getSeq() != null) {
			id.setCrgSeq(vo.getCirurgia().getSeq()); // Cirurgia aqui
		}
		id.setEprEspSeq(this.especialidadeProcedimentoAdd.getSeq());
		id.setEprPciSeq(procedimentoCirurgico.getSeq());
		id.setIndRespProc(DominioIndRespProc.AGND);
		procedimentoVO.setId(id); // Seta ID

		// 3. Associar atributos
		if (!existePrincipal && indPrincipal) {
			procedimentoVO.setIndPrincipal(true);
			this.setIndPrincipal(true);
			this.setExistePrincipal(true);
		} else {
			procedimentoVO.setIndPrincipal(false);
			this.setIndPrincipal(false);
		}

		procedimentoVO.setProcedimentoCirurgico(procedimentoCirurgico);
		
		Enum[] innerJoins = {MbcEspecialidadeProcCirgs.Fields.ESPECIALIDADE,MbcEspecialidadeProcCirgs.Fields.PROCEDIMENTO_CIRURGICOS};
		
		MbcEspecialidadeProcCirgs espProc = this.blocoCirurgicoCadastroApoioFacade.obterEspecialidadeProcedimentoCirurgico(new MbcEspecialidadeProcCirgsId(procedimentoCirurgico
				.getSeq(), this.especialidadeProcedimentoAdd.getSeq()), innerJoins, null);
		procedimentoVO.setMbcEspecialidadeProcCirgs(espProc);
		procedimentoVO.setSituacao(DominioSituacao.A);
		procedimentoVO.setQtd((byte) 1); // Quantidade padrão é 1
		procedimentoVO.setSigla(espProc.getEspecialidade().getSigla());
		
		if (procedimentoVO.getIndPrincipal() != null
				&& procedimentoVO.getIndPrincipal()) {
			procedimentoVO.setLado(lateralidade);
		}
		limparLateralidade();
		

		if (this.listaProcedimentos.contains(procedimentoVO)) { // Caso o item já exista na lista...
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_ITEM_JA_CONSTA_LISTA", "Procedimento");
		} else {
			this.listaProcedimentos.add(procedimentoVO); // 4. Acrescentar na lista
			this.procedimentoAdd = null; // Ajustes e operações na lista
		}
	}

	public void adicionarProfissional() {

		if (this.profissionalAdd == null) {
			apresentarMsgNegocio("sbProfissional", Severity.ERROR, CAMPO_OBRIGATORIO, "Profissional");
			return;
		}

		boolean existeResponsavel = false;
		for (CirurgiaTelaProfissionalVO profissionalVO : this.listaProfissionais) {
			if (profissionalVO.getIndResponsavel()) {
				existeResponsavel = true;
				break;
			}
		}

		// 1. Instância que será gravada
		CirurgiaTelaProfissionalVO profissionalVO = new CirurgiaTelaProfissionalVO();

		// Instancia o servidor do profissional
		RapServidores servidorProfissional = this.registroColaboradorFacade.obterRapServidoresPorChavePrimaria(new RapServidoresId(this.profissionalAdd.getMatricula(),
				this.profissionalAdd.getVinCodigo()));
		Short unfSeq = this.vo != null && this.vo.getCirurgia().getUnidadeFuncional() != null ? this.vo.getCirurgia().getUnidadeFuncional().getSeq() : null;

		// Resgata o profissional que atua na unidade
		MbcProfAtuaUnidCirgs profAtuaUnidCirgs = this.blocoCirurgicoCadastroApoioFacade.pesquisarProfUnidCirgAtivoPorServidorUnfSeqFuncao(servidorProfissional,
				unfSeq, profissionalAdd.getFuncao());
	
		// 2. ID da instância que será gravada
		MbcProfCirurgiasId id = new MbcProfCirurgiasId();
		id.setCrgSeq(this.vo.getCirurgia().getSeq());
		id.setPucIndFuncaoProf(profAtuaUnidCirgs.getId().getIndFuncaoProf());
		id.setPucSerMatricula(this.profissionalAdd.getMatricula());
		id.setPucSerVinCodigo(this.profissionalAdd.getVinCodigo());
		id.setPucUnfSeq(profAtuaUnidCirgs.getId().getUnfSeq());
		profissionalVO.setId(id);

		// 3. Associar atributos
		if (!existeResponsavel) {
			profissionalVO.setIndResponsavel(true);
		} else {
			profissionalVO.setIndResponsavel(false);
		}
		profissionalVO.setCirurgia(null);
		profissionalVO.setServidorPuc(profAtuaUnidCirgs.getRapServidores());
		profissionalVO.setMbcProfAtuaUnidCirgs(profAtuaUnidCirgs);
		profissionalVO.setFuncaoProfissional(profAtuaUnidCirgs.getIndFuncaoProf());
		VRapServidorConselho servidorConselho = this.registroColaboradorFacade.obterVRapServidorConselhoPeloId(this.profissionalAdd.getMatricula(), 
				this.profissionalAdd.getVinCodigo(), this.profissionalAdd.getCprSigla());
		profissionalVO.setServidorConselho(servidorConselho);
		// Busca especialidade
		profissionalVO.setEspecialidade(buscarEspecialidades(this.profissionalAdd.getMatricula(), 
				this.profissionalAdd.getVinCodigo()));

		if (this.listaProfissionais.contains(profissionalVO)) { // Caso o item já exista na lista...
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_ITEM_JA_CONSTA_LISTA", "Profissional");
		} else {
			this.listaProfissionais.add(profissionalVO); // 4. Acrescentar na lista
			profissionalAdd = null; // Ajustes e operações na lista
		}

		// Verifica se o profissional pertence a mesma especialidade da cirurgia
		if (this.vo != null && DominioNaturezaFichaAnestesia.URG.equals(this.vo.getCirurgia().getNaturezaAgenda())) {
			List<AghProfEspecialidades> listaProfEspecialidades = this.aghuFacade.pesquisarProfEspecialidadesCirurgiao(this.vo.getCirurgia().getEspecialidade().getSeq(),
					servidorProfissional.getId().getVinCodigo(), servidorProfissional.getId().getMatricula());
			if (listaProfEspecialidades.isEmpty()) {
				// Exibir alerta 'Profissional não é da mesma especialidade da cirurgia. MAS PERMITE ADICIONAR!
				this.apresentarMsgNegocio(Severity.WARN, "MSG_ESP_PROFISSIONAL_DIFERENTE_CIRURGIA");
			}
		}
	}

	private String buscarEspecialidades(Integer serMatricula, Short serVinCodigo) {
		List<AghEspecialidades> listaEspecialidades = this.aghuFacade.obterEspecialidadeProfCirurgiaoPorServidor(serMatricula, serVinCodigo);
		String especialidade = null;
		for (AghEspecialidades esp : listaEspecialidades) {
			if (especialidade == null) {
				especialidade = esp.getSigla();
				
			} else {
				especialidade = especialidade.concat("/").concat(esp.getSigla());
			}
		}
		return especialidade;
	}
	
	public void adicionarAnestesia() {

		if (this.tipoAnestesiaAdd == null) {
			apresentarMsgNegocio("sbAnestesia", Severity.ERROR, CAMPO_OBRIGATORIO, "Anestesia");
			return;
		}

		// 1. Instancia que será gravada
		CirurgiaTelaAnestesiaVO anestesiaVO = new CirurgiaTelaAnestesiaVO();

		MbcAnestesiaCirurgiasId id = new MbcAnestesiaCirurgiasId(); // 2. ID da instância que será gravada
		id.setCrgSeq(this.vo.getCirurgia().getSeq());
		id.setTanSeq(this.tipoAnestesiaAdd.getSeq());
		anestesiaVO.setId(id); // Seta ID

		// 3. Associar atributos
		anestesiaVO.setMbcTipoAnestesias(this.tipoAnestesiaAdd);

		if (this.listaAnestesias.contains(anestesiaVO)) { // Caso o item já exista na lista...
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_ITEM_JA_CONSTA_LISTA", "Anestesia");
		} else {
			listaAnestesias.add(anestesiaVO); // 4. Acrescentar na lista
			tipoAnestesiaAdd = null; // Ajustes e operações na lista
		}
	}

	/*
	 * Métodos para setar o principal ou responsável nas listas ATENÇÃO: Como os itens pertencem a mesma classe, somente a comparação de HASH CODE será necessária para definir o
	 * selecionado
	 */
	public void setProcedimentoPrincipal() {
		for (CirurgiaTelaProcedimentoVO procedimentoVO : this.listaProcedimentos) {
			if (procedimentoVO.hashCode() == this.procedimentoPrincipalHashCode) {
				procedimentoVO.setIndPrincipal(true);
			} else {
				procedimentoVO.setIndPrincipal(false);
			}
		}
	}

	public void setProfissionalResponsavel() {
		List<CirurgiaTelaProfissionalVO> lista = new ArrayList<>();
		for (CirurgiaTelaProfissionalVO profissionalVO : this.listaProfissionais) {
			lista.add(setIndResponsavelVO(profissionalVO));
	    }
		this.listaProfissionais = new ArrayList<>();
		this.listaProfissionais.addAll(lista);
	}

	private CirurgiaTelaProfissionalVO setIndResponsavelVO(CirurgiaTelaProfissionalVO profissionalVO) {
		if (profissionalVO.hashCode() == this.profissionalResponsavelPrincipalId) {
			profissionalVO.setIndResponsavel(true);
		} else {
			profissionalVO.setIndResponsavel(false);
		}
		return profissionalVO;
	}

	/*
	 * MÉTODOS REMOVER DAS LISTAS
	 */
	public void removerProcedimento(CirurgiaTelaProcedimentoVO procedimentoRemove) {
		if (this.listaProcedimentos.contains(procedimentoRemove)) {
			this.listaProcedimentos.remove(procedimentoRemove);
			this.vo.getListaProcedimentosRemovidos().add(procedimentoRemove.getId());
			if (Boolean.TRUE.equals(procedimentoRemove.getIndPrincipal())) {
				if (!this.listaProcedimentos.isEmpty()) {
					this.setIndPrincipal(true);
					this.setExistePrincipal(false);
				} else {
					this.setIndPrincipal(false);
					this.setExistePrincipal(false);
				}
			}
		}
		procedimentoRemove = null;
	}

	public void removerProfissionais(CirurgiaTelaProfissionalVO profissionalRemove) {
		if (this.listaProfissionais.contains(profissionalRemove)) {
			this.listaProfissionais.remove(profissionalRemove);
			this.vo.getListaProfissionaisRemovidos().add(profissionalRemove.getId());
			if (Boolean.TRUE.equals(profissionalRemove.getIndResponsavel())) {
				if (!this.listaProfissionais.isEmpty()) {
					this.listaProfissionais.get(0).setIndResponsavel(Boolean.TRUE);
				}
			}
		}
		profissionalRemove = null;
	}

	public void removerAnestesia(CirurgiaTelaAnestesiaVO anestesiaRemove) {
		if (this.listaAnestesias.contains(anestesiaRemove)) {
			this.listaAnestesias.remove(anestesiaRemove);
			this.vo.getListaAnestesiasRemovidas().add(anestesiaRemove.getId());
		}
		anestesiaRemove = null;
	}

	/*
	 * METÓDOS COM AS AÇÕES DA LISTA DE PROCEDIMENTO
	 */
	public void ativarInativarProcedimento(CirurgiaTelaProcedimentoVO procedimentoVO) {
		procedimentoVO.setSituacao((procedimentoVO.getSituacao().isAtivo()) ? DominioSituacao.I : DominioSituacao.A);
	}

	/**
	 * Verifica se é possível alterar a quantidade de um procedimento listado
	 * 
	 * @param procedimentoVO
	 * @return
	 */
	public boolean isPermitirAlterarQuantidadeProcedimento(CirurgiaTelaProcedimentoVO procedimentoVO) {
		boolean isProcedimentoCirurgicoMultiplo = procedimentoVO.getMbcEspecialidadeProcCirgs().getMbcProcedimentoCirurgicos().getIndProcMultiplo();
		boolean isProcedimentoEspecialidadeAtivo = DominioSituacao.A.equals(procedimentoVO.getSituacao());
		return isProcedimentoCirurgicoMultiplo && isProcedimentoEspecialidadeAtivo;
	}

	/*
	 * MÉTODOS DAS SUGGESTION BOX
	 */
	public List<AinLeitos> pesquisarLeitos(String objPesquisa) {
		return this.returnSGWithCount(this.internacaoFacade.obterLeitosAtivosPorUnf(objPesquisa, null),pesquisarLeitosCount(objPesquisa));
	}

	public Long pesquisarLeitosCount(String objPesquisa) {
		return this.internacaoFacade.obterLeitosAtivosPorUnfCount(objPesquisa, null);
	}

	public void posSelecionarLeito() {
		try {
			AipPacientes paciente = this.blocoCirurgicoFacade.obterPacientePorLeito(this.leito);
			if(paciente != null){
				this.setPaciente(paciente);
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			this.setPaciente(null);
		}
	}

	public List<AghUnidadesFuncionais> pesquisarUnidadesCirurgicas(String objPesquisa) {
		return this.returnSGWithCount(blocoCirurgicoCadastroApoioFacade.buscarUnidadesFuncionaisCirurgia(objPesquisa),pesquisarUnidadesCirurgicasCount(objPesquisa));
	}

	public Long pesquisarUnidadesCirurgicasCount(String objPesquisa) {
		return blocoCirurgicoCadastroApoioFacade.contarUnidadesFuncionaisCirurgia(objPesquisa);
	}

	public void posRemoverUnidadeCirurgica() {
		this.carregaUnidadeFuncional = Boolean.FALSE;
		indPrincipal = true;
		existePrincipal = false;
		this.instanciarAgendaProcedimentos(null); // Limpa os campos pois instancia uma nova versão do agendamento
	}

	public void posSelecionarEspecialidadeCirurgia() {
		if (this.especialidadeProcedimentoAdd == null && this.vo.getCirurgia().getEspecialidade() != null) {
			this.especialidadeProcedimentoAdd = this.vo.getCirurgia().getEspecialidade();
		}
	}

	public List<MbcSalaCirurgica> pesquisarSalasCirurgicas(String objPesquisa) {
		Short unfSeq = this.vo != null && this.vo.getCirurgia().getUnidadeFuncional() != null ? this.vo.getCirurgia().getUnidadeFuncional().getSeq() : null;
		Short seqpSala = objPesquisa != null && StringUtils.isNotEmpty(objPesquisa) ? Short.valueOf(objPesquisa) : null;
		return this.returnSGWithCount(this.blocoCirurgicoCadastroApoioFacade.buscarSalaCirurgica(seqpSala, unfSeq, null, null, null, null, Boolean.TRUE, MbcSalaCirurgica.Fields.ID_SEQP),pesquisarSalasCirurgicasCount(objPesquisa));
	}

	public Long pesquisarSalasCirurgicasCount(String objPesquisa) {
		Short unfSeq = this.vo != null && this.vo.getCirurgia().getUnidadeFuncional() != null ? this.vo.getCirurgia().getUnidadeFuncional().getSeq() : null;
		String nome = objPesquisa != null && StringUtils.isNotEmpty(objPesquisa) ? objPesquisa : null;
		return this.blocoCirurgicoCadastroApoioFacade.buscarSalaCirurgicaCount(null, unfSeq, nome, null, null, null);
	}

	public List<AelProjetoPesquisas> pesquisarProjetosPesquisa(String objPesquisa) {
		return this.returnSGWithCount(this.examesFacade.pesquisarProjetosPesquisaAgendaCirurgiaPorNumeroNome(objPesquisa),pesquisarProjetosPesquisaCount(objPesquisa));
	}

	public Long pesquisarProjetosPesquisaCount(String objPesquisa) {
		return this.examesFacade.pesquisarProjetosPesquisaAgendaCirurgiaPorNumeroNomeCount(objPesquisa);
	}

	public List<FatConvenioSaudePlano> pesquisarConvenioSaudePlanos(final String objPesquisa) {
		if (this.permiteAgendarConvenio && this.permiteAgendarSUS) {
			return this.returnSGWithCount(this.faturamentoApoioFacade.pesquisarConvenioSaudePlanos((String) objPesquisa),pesquisarCountConvenioSaudePlanos(objPesquisa));
		} else if (this.permiteAgendarSUS) {
			return this.faturamentoApoioFacade.pesquisarConvenioPorGrupoConvenio((String) objPesquisa, DominioGrupoConvenio.S);
		} else if (this.permiteAgendarConvenio) {
			return this.faturamentoApoioFacade.pesquisarConvenioPorGrupoConvenio((String) objPesquisa, null);
		} else {
			return null;
		}
	}

	public Long pesquisarCountConvenioSaudePlanos(final String objPesquisa) {
		final String strPesquisa = (String) objPesquisa;
		return this.faturamentoApoioFacade.pesquisarCountConvenioSaudePlanos(strPesquisa);
	}

	/*
	 * MÉTODOS DA SELEÇÃO DE PLANO E CONVÊNIO
	 */
	public void selecionarPlanoConvenio() {
		if (this.planoId != null && this.convenioId != null) {
			FatConvenioSaudePlano plano = null;
			if (this.permiteAgendarConvenio && this.permiteAgendarSUS) {
				plano = this.faturamentoApoioFacade.obterPlanoPorId(this.planoId, this.convenioId);
			} else if (this.permiteAgendarSUS) {
				plano = this.faturamentoApoioFacade.obterConvenioPorGrupoConvenio(this.planoId, this.convenioId, DominioGrupoConvenio.S);
			} else if (this.permiteAgendarConvenio) {
				plano = this.faturamentoApoioFacade.obterConvenioPorGrupoConvenio(this.planoId, this.convenioId, null);
			}
			if (plano == null) {
				this.apresentarMsgNegocio(Severity.WARN, "MENSAGEM_CONVENIO_PLANO_NAO_ENCONTRADO", this.convenioId, this.planoId);
			}
			this.atribuirPlano(plano); // Necessário para resetar o plano anterior
		}
	}
	
	
	/**
	 * Tratamento feito para volta do botão pesquisa fonetica	
	 * @param codigoPaciente
	 */
	public void processarBuscaPacientePorCodigo(Integer codigoPaciente){
		if(codigoPaciente != null){
			this.setPaciente(pacienteFacade.buscaPaciente(codigoPaciente));
		}else{
			this.paciente = null;
		}
	}
	
	public void atribuirPlano(final FatConvenioSaudePlano plano) {
		if (plano != null) {
			this.plano = plano;
			this.convenioId = plano.getConvenioSaude().getCodigo();
			this.planoId = plano.getId().getSeq();
			this.vo.getCirurgia().setConvenioSaudePlano(plano);
		} else {
			this.plano = null;
			this.convenioId = null;
			this.planoId = null;
			this.vo.getCirurgia().setConvenioSaudePlano(null);
		}
		this.validaOrigemComPlano();
	}

	public void atribuirPlano() {
		if (this.plano != null) {
			this.convenioId = this.plano.getConvenioSaude().getCodigo();
			this.planoId = this.plano.getId().getSeq();
		} else {
			this.plano = null;
			this.convenioId = null;
			this.planoId = null;
		}
	}

	public String obterTempoMinimoFormatado(Short tempoMinimo) {
		String lpad = StringUtil.adicionaZerosAEsquerda(tempoMinimo, 4);
		String substr1 = lpad.substring(0, 2);
		String substr2 = lpad.substring(2, 4);
		Short horas = Short.valueOf(substr1);
		Short minutos = Short.valueOf(substr2);
		return String.format("%02d:%02d", horas, minutos);
	}
	
	//Verificar se o procedimento possui indicação de lateralidade
	public void isLateralidade(){
		this.setReadOnlyLado(blocoCirurgicoFacade.isLateralidade(procedimentoAdd.getId().getPciSeq()));
	}
	
	public void limparLateralidade(){
		this.setReadOnlyLado(true);
		this.setLateralidade(null);
	}

	public void verificaLateralidade(CirurgiaTelaProcedimentoVO vo){
		this.setReadOnlyLado(blocoCirurgicoFacade.isLateralidade(vo.getId().getEprPciSeq()));
		this.setProcedimentoLadoAlterado(vo);
	}

	public void setaLado() {
		
		for (CirurgiaTelaProcedimentoVO procedimento : this.listaProcedimentos) {
			if (procedimento.getIndPrincipal()) {
				this.setExistePrincipal(true);
				this.getProcedimentoLadoAlterado().setIndPrincipal(false);
				this.setIndPrincipal(false);
				break;
			}
		}		
		
		if(getProcedimentoLadoAlterado().getLado() != null){
			this.setLateralidade(getProcedimentoLadoAlterado().getLado());
			this.getProcedimentoLadoAlterado().setIndPrincipal(true);
			this.setReadOnlyLado(true);
			this.setExistePrincipal(true);
			this.setIndPrincipal(true);
			this.setLateralidade(null);
		}
		
	}

	public boolean editaLado(CirurgiaTelaProcedimentoVO procedimentoVO){
		
		boolean lateralidade = blocoCirurgicoFacade.isLateralidade(procedimentoVO.getId().getEprPciSeq());		
		boolean retorno = !procedimentoVO.getIndPrincipal() && !this.isExistePrincipal() && !lateralidade;
		
		return retorno;
	}
	
	/* MBC-00506 / MBC-00507
	 * Para cirurgia com Convênio/Plano de (Internação OU Ambulatório) a origem deve ser de (internação OU ambulatorial)
	 * #40869
	 */
	public void validaOrigemComPlano(){
		
		if (this.plano != null && this.plano.getIndTipoPlano() != null && this.vo.getCirurgia() != null && this.vo.getCirurgia().getOrigemPacienteCirurgia() != null){
			
			if(DominioTipoPlano.A.equals(this.plano.getIndTipoPlano()) && 
					DominioOrigemPacienteCirurgia.I.equals(this.vo.getCirurgia().getOrigemPacienteCirurgia())){
				this.apresentarMsgNegocio(Severity.ERROR, "MBC_00506");
				this.setOrigemOk(Boolean.FALSE);
				return;
			} else if (DominioTipoPlano.I.equals(this.plano.getIndTipoPlano()) && 
					 DominioOrigemPacienteCirurgia.A.equals(this.vo.getCirurgia().getOrigemPacienteCirurgia())){
				this.apresentarMsgNegocio(Severity.ERROR, "MBC_00507");
				this.setOrigemOk(Boolean.FALSE);
				return;
			}
		
		}
		this.setOrigemOk(Boolean.TRUE);
	}
	
	public void limparParametros() {
		this.agendaData = null;
		this.agendaEspSeq = null;
		this.agendaMatriculaEquipe = null;
		this.agendaSalaSeqp = null;
		this.agendaSeq = null;
		this.agendaUnfSeq = null;
		this.agendaUnfSeqSala = null;
		this.agendaVinCodigoEquipe = null;
		this.alertaVO = null;
		this.botaoImagens = false;
		this.carregaUnidadeFuncional = Boolean.TRUE;
		this.convenioId = null;
		this.crgSeq = null;
		this.data = null;
		this.dispHorarioDataAgenda = null;
		this.dispHorarioEspSeq = null;
		this.dispHorarioHoraInicio = null;
		this.dispHorarioSalaSeq = null;
		this.dispHorarioUnfSeq = null;
		this.emEdicao = false;
		this.especialidadeProcedimentoAdd = null;
		this.espSeqPrincipal = null;
		this.exibirBotaoVoltar = false;
		this.exibirModalAlertaGravar = false;
		this.exibirModalValidacaoTempoMinimoPrevisto = false;
		this.existePrincipal = false;
		this.indFuncaoProf = null;
		this.indPrincipal = true;
		this.lateralidade = null;
		this.leito = null;
		this.listaAnestesias = null;
		this.listaProcedimentos = null;
		this.listaProfissionais = null;
		this.origemOk = false;
		this.paciente = null;
		this.permiteAgendarConvenio = false;
		this.permiteAgendarSUS = false;
		this.plano = null;
		this.planoId = null;
		this.procedimentoAdd = null;
		this.procedimentoLadoAlterado = null;
		this.procedimentoQuantidadeAlterada = null;
		this.profissionalAdd = null;
		this.readOnlyLado = true;
		this.sliderAnestesiasAberto = true;
		this.sliderCirurgiaAberto = true;
		this.sliderProcedimentoAberto = true;
		this.sliderProfissionaisAberto = true;
		this.speSeq = null;
		this.tempoPrevisto = null;
		this.tipoAnestesiaAdd = null;
		this.vo = null;
		this.voltouPesqFoneticaOuDisponibilidadeHorario = null;
		this.voltouPesquisaPacientes = null;
	}
	
	private VMbcProcEsp getCarregarProcedimentoPrincipal() {
		MbcEspecialidadeProcCirgs espProcCirgs = this.blocoCirurgicoCadastroApoioFacade.obterEspecialidadeProcedimentoCirurgico(agenda.getEspProcCirgs().getId(), new Enum[] {MbcEspecialidadeProcCirgs.Fields.PROCEDIMENTO_CIRURGICOS, MbcEspecialidadeProcCirgs.Fields.ESPECIALIDADE}, null);
		if (espProcCirgs != null) {
			Short espSeq = null;
			if(especialidadeProcedimentoAdd != null) {
				espSeq = especialidadeProcedimentoAdd.getSeq();
			}
			List<VMbcProcEsp> procedimentos = blocoCirurgicoPortalPlanejamentoFacade.pesquisarVMbcProcEspPorEsp(espProcCirgs.getMbcProcedimentoCirurgicos().getSeq().toString(), espSeq, 100);
			if(procedimentos != null && !procedimentos.isEmpty()) {
				return procedimentos.get(0);
			}			}
		return null;
	}
	
	public void horaAtual(){
		this.vo.getCirurgia().setDataPrevisaoInicio((new Date()));
	}

	
	/*
	 * GETTERS E SETTERS
	 */
	public CirurgiaTelaVO getVo() {
		return vo;
	}

	public boolean isExibirBotaoVoltar() {
		return exibirBotaoVoltar;
	}

	public void setExibirBotaoVoltar(boolean exibirBotaoVoltar) {
		this.exibirBotaoVoltar = exibirBotaoVoltar;
	}

	public boolean isSliderCirurgiaAberto() {
		return sliderCirurgiaAberto;
	}

	public void setSliderCirurgiaAberto(boolean sliderCirurgiaAberto) {
		this.sliderCirurgiaAberto = sliderCirurgiaAberto;
	}

	public boolean isSliderProcedimentoAberto() {
		return sliderProcedimentoAberto;
	}

	public void setSliderProcedimentoAberto(boolean sliderProcedimentoAberto) {
		this.sliderProcedimentoAberto = sliderProcedimentoAberto;
	}

	public boolean isBotaoImagens() {
		return botaoImagens;
	}

	public void setBotaoImagens(boolean botaoImagens) {
		this.botaoImagens = botaoImagens;
	}

	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
		if (paciente != null && StringUtils.isNotBlank(paciente.getNome())) {
			if (this.emEdicao && this.vo.getPacienteAntigo() == null && !CoreUtil.igual(this.vo.getCirurgia().getPaciente(), this.paciente)) {
				// Em caso de EDIÇÃO E MUDANÇA DO PACIENTE, o paciente antigo deverá ser preservado para RN14 do documento de análise
				this.vo.setPacienteAntigo(this.vo.getCirurgia().getPaciente());
			}
		} 
	}
	
	public AinLeitos getLeito() {
		return leito;
	}

	public void setLeito(AinLeitos leito) {
		this.leito = leito;
	}

	public Date getTempoPrevisto() {
		return tempoPrevisto;
	}

	public void setTempoPrevisto(Date tempoPrevisto) {
		this.tempoPrevisto = tempoPrevisto;
	}

	public FatConvenioSaudePlano getPlano() {
		return plano;
	}

	public void setPlano(FatConvenioSaudePlano plano) {
		this.plano = plano;
	}

	public Short getConvenioId() {
		return convenioId;
	}

	public void setConvenioId(Short convenioId) {
		this.convenioId = convenioId;
	}

	public Byte getPlanoId() {
		return planoId;
	}

	public void setPlanoId(Byte planoId) {
		this.planoId = planoId;
	}

	public VMbcProcEsp getProcedimentoAdd() {
		return procedimentoAdd;
	}

	public void setProcedimentoAdd(VMbcProcEsp procedimentoAdd) {
		this.procedimentoAdd = procedimentoAdd;
	}

	public List<CirurgiaTelaProcedimentoVO> getListaProcedimentos() {
		return listaProcedimentos;
	}

	public AghEspecialidades getEspecialidadeProcedimentoAdd() {
		return especialidadeProcedimentoAdd;
	}

	public void setEspecialidadeProcedimentoAdd(AghEspecialidades especialidadeProcedimentoAdd) {
		this.especialidadeProcedimentoAdd = especialidadeProcedimentoAdd;
	}

	public boolean isSliderProfissionaisAberto() {
		return sliderProfissionaisAberto;
	}

	public void setSliderProfissionaisAberto(boolean sliderProfissionaisAberto) {
		this.sliderProfissionaisAberto = sliderProfissionaisAberto;
	}

	public AgendaProcedimentoPesquisaProfissionalVO getProfissionalAdd() {
		return profissionalAdd;
	}

	public void setProfissionalAdd(AgendaProcedimentoPesquisaProfissionalVO profissionalAdd) {
		this.profissionalAdd = profissionalAdd;
	}

	public List<CirurgiaTelaProfissionalVO> getListaProfissionais() {
		return listaProfissionais;
	}

	public boolean isSliderAnestesiasAberto() {
		return sliderAnestesiasAberto;
	}

	public void setSliderAnestesiasAberto(boolean sliderAnestesiasAberto) {
		this.sliderAnestesiasAberto = sliderAnestesiasAberto;
	}

	public MbcTipoAnestesias getTipoAnestesiaAdd() {
		return tipoAnestesiaAdd;
	}

	public void setTipoAnestesiaAdd(MbcTipoAnestesias tipoAnestesiaAdd) {
		this.tipoAnestesiaAdd = tipoAnestesiaAdd;
	}

	public List<CirurgiaTelaAnestesiaVO> getListaAnestesias() {
		return listaAnestesias;
	}

	public CirurgiaTelaProcedimentoVO getProcedimentoQuantidadeAlterada() {
		return procedimentoQuantidadeAlterada;
	}

	public void setProcedimentoQuantidadeAlterada(CirurgiaTelaProcedimentoVO procedimentoQuantidadeAlterada) {
		this.procedimentoQuantidadeAlterada = procedimentoQuantidadeAlterada;
	}

	public Short getEspSeqPrincipal() {
		return espSeqPrincipal;
	}

	public void setEspSeqPrincipal(Short espSeqPrincipal) {
		this.espSeqPrincipal = espSeqPrincipal;
	}

	public DominioNaturezaFichaAnestesia[] getItensOrigemAtendimento() {
		return itensOrigemAtendimento;
	}

	public void setItensOrigemAtendimento(DominioNaturezaFichaAnestesia[] itensOrigemAtendimento) {
		this.itensOrigemAtendimento = itensOrigemAtendimento;
	}

	public boolean isEmEdicao() {
		return emEdicao;
	}

	public int getProcedimentoPrincipalHashCode() {
		return procedimentoPrincipalHashCode;
	}

	public void setProcedimentoPrincipalHashCode(int procedimentoPrincipalHashCode) {
		this.procedimentoPrincipalHashCode = procedimentoPrincipalHashCode;
	}

	public int getProfissionalResponsavelPrincipalId() {
		return profissionalResponsavelPrincipalId;
	}

	public void setProfissionalResponsavelPrincipalId(int profissionalResponsavelPrincipalHashCode) {
		this.profissionalResponsavelPrincipalId = profissionalResponsavelPrincipalHashCode;
	}

	public Integer getSpeSeq() {
		return speSeq;
	}

	public void setSpeSeq(Integer speSeq) {
		this.speSeq = speSeq;
	}

	public Boolean getVoltouPesquisaPacientes() {
		return voltouPesquisaPacientes;
	}

	public void setVoltouPesquisaPacientes(Boolean voltouPesquisaPacientes) {
		this.voltouPesquisaPacientes = voltouPesquisaPacientes;
	}

	public Short getDispHorarioUnfSeq() {
		return dispHorarioUnfSeq;
	}

	public void setDispHorarioUnfSeq(Short dispHorarioUnfSeq) {
		this.dispHorarioUnfSeq = dispHorarioUnfSeq;
	}

	public Long getDispHorarioDataAgenda() {
		return dispHorarioDataAgenda;
	}

	public void setDispHorarioDataAgenda(Long dispHorarioDataAgenda) {
		this.dispHorarioDataAgenda = dispHorarioDataAgenda;
	}

	public Long getDispHorarioHoraInicio() {
		return dispHorarioHoraInicio;
	}

	public void setDispHorarioHoraInicio(Long dispHorarioHoraInicio) {
		this.dispHorarioHoraInicio = dispHorarioHoraInicio;
	}

	public Short getDispHorarioEspSeq() {
		return dispHorarioEspSeq;
	}

	public void setDispHorarioEspSeq(Short dispHorarioEspSeq) {
		this.dispHorarioEspSeq = dispHorarioEspSeq;
	}

	public Short getDispHorarioSalaSeq() {
		return dispHorarioSalaSeq;
	}

	public void setDispHorarioSalaSeq(Short dispHorarioSalaSeq) {
		this.dispHorarioSalaSeq = dispHorarioSalaSeq;
	}

	public Integer getCrgSeq() {
		return crgSeq;
	}

	public void setCrgSeq(Integer crgSeq) {
		this.crgSeq = crgSeq;
	}

	public AlertaModalVO getAlertaVO() {
		return alertaVO;
	}

	public boolean isExibirModalAlertaGravar() {
		return exibirModalAlertaGravar;
	}

	public boolean isExibirModalValidacaoTempoMinimoPrevisto() {
		return exibirModalValidacaoTempoMinimoPrevisto;
	}

	public void setExibirModalValidacaoTempoMinimoPrevisto(
			boolean exibirModalValidacaoTempoMinimoPrevisto) {
		this.exibirModalValidacaoTempoMinimoPrevisto = exibirModalValidacaoTempoMinimoPrevisto;
	}

	public boolean isPermiteAgendarSUS() {
		return permiteAgendarSUS;
	}

	public void setPermiteAgendarSUS(boolean permiteAgendarSUS) {
		this.permiteAgendarSUS = permiteAgendarSUS;
	}

	public boolean isPermiteAgendarConvenio() {
		return permiteAgendarConvenio;
	}

	public void setPermiteAgendarConvenio(boolean permiteAgendarConvenio) {
		this.permiteAgendarConvenio = permiteAgendarConvenio;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public Short getAgendaUnfSeq() {
		return agendaUnfSeq;
	}

	public void setAgendaUnfSeq(Short agendaUnfSeq) {
		this.agendaUnfSeq = agendaUnfSeq;
	}

	public Short getAgendaEspSeq() {
		return agendaEspSeq;
	}

	public void setAgendaEspSeq(Short agendaEspSeq) {
		this.agendaEspSeq = agendaEspSeq;
	}

	public Long getAgendaData() {
		return agendaData;
	}

	public void setAgendaData(Long agendaData) {
		this.agendaData = agendaData;
	}

	public Short getAgendaUnfSeqSala() {
		return agendaUnfSeqSala;
	}

	public void setAgendaUnfSeqSala(Short agendaUnfSeqSala) {
		this.agendaUnfSeqSala = agendaUnfSeqSala;
	}

	public Short getAgendaSalaSeqp() {
		return agendaSalaSeqp;
	}

	public void setAgendaSalaSeqp(Short agendaSalaSeqp) {
		this.agendaSalaSeqp = agendaSalaSeqp;
	}

	public Short getAgendaVinCodigoEquipe() {
		return agendaVinCodigoEquipe;
	}

	public void setAgendaVinCodigoEquipe(Short agendaVinCodigoEquipe) {
		this.agendaVinCodigoEquipe = agendaVinCodigoEquipe;
	}

	public Integer getAgendaMatriculaEquipe() {
		return agendaMatriculaEquipe;
	}

	public void setAgendaMatriculaEquipe(Integer agendaMatriculaEquipe) {
		this.agendaMatriculaEquipe = agendaMatriculaEquipe;
	}

	public Integer getAgendaSeq() {
		return agendaSeq;
	}

	public void setAgendaSeq(Integer agendaSeq) {
		this.agendaSeq = agendaSeq;
	}

	public boolean isReadOnlyLado() {
		return readOnlyLado;
	}

	public void setReadOnlyLado(boolean readOnlyLado) {
		this.readOnlyLado = readOnlyLado;
	}

	public DominioLadoCirurgiaAgendas getLateralidade() {
		return lateralidade;
	}

	public void setLateralidade(DominioLadoCirurgiaAgendas lateralidade) {
		this.lateralidade = lateralidade;
	}

	public DominioLadoCirurgiaAgendas[] getItensLateralidade() {
		return itensLateralidade;
	}

	public void setItensLateralidade(DominioLadoCirurgiaAgendas[] itensLateralidade) {
		this.itensLateralidade = itensLateralidade;
	}

	public boolean isExistePrincipal() {
		return existePrincipal;
	}

	public void setExistePrincipal(boolean existePrincipal) {
		this.existePrincipal = existePrincipal;
	}

	public CirurgiaTelaProcedimentoVO getProcedimentoLadoAlterado() {
		return procedimentoLadoAlterado;
	}

	public void setProcedimentoLadoAlterado(
			CirurgiaTelaProcedimentoVO procedimentoLadoAlterado) {
		this.procedimentoLadoAlterado = procedimentoLadoAlterado;
	}

	public Boolean getIndPrincipal() {
		return indPrincipal;
	}

	public void setIndPrincipal(Boolean indPrincipal) {
		this.indPrincipal = indPrincipal;
	}

	public Boolean getCarregaUnidadeFuncional() {
		return carregaUnidadeFuncional;
	}

	public void setCarregaUnidadeFuncional(Boolean carregaUnidadeFuncional) {
		this.carregaUnidadeFuncional = carregaUnidadeFuncional;
	}
	
	public DominioFuncaoProfissional getIndFuncaoProf() {
		return indFuncaoProf;
	}

	public void setIndFuncaoProf(DominioFuncaoProfissional indFuncaoProf) {
		this.indFuncaoProf = indFuncaoProf;
	}

	public Boolean getOrigemOk() {
		return origemOk;
	}

	public void setOrigemOk(Boolean origemOk) {
		this.origemOk = origemOk;
	}
	
	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public MbcAgendas getAgenda() {
		return agenda;
	}

	public void setAgenda(MbcAgendas agenda) {
		this.agenda = agenda;
	}
	
	
	
}