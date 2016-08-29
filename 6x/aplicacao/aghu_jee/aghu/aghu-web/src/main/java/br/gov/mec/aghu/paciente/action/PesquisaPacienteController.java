package br.gov.mec.aghu.paciente.action;

import static br.gov.mec.aghu.model.AipPacientes.VALOR_MAXIMO_PRONTUARIO;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.business.SelectionQualifier;
import br.gov.mec.aghu.core.commons.WebUtil;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioCor;
import br.gov.mec.aghu.dominio.DominioEstadoCivil;
import br.gov.mec.aghu.dominio.DominioListaOrigensAtendimentos;
import br.gov.mec.aghu.dominio.DominioModulo;
import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioTipoDataObito;
import br.gov.mec.aghu.internacao.action.CadastroInternacaoController;
import br.gov.mec.aghu.internacao.pesquisa.action.PesquisaSolicitacaoInternacaoPaginatorController;
import br.gov.mec.aghu.internacao.pesquisa.business.IPesquisaInternacaoFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AinAtendimentosUrgencia;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MptTipoSessao;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.business.PesquisaFoneticaPaciente;
import br.gov.mec.aghu.paciente.cadastro.action.CadastrarPacienteController;
import br.gov.mec.aghu.paciente.cadastro.business.ICadastroPacienteFacade;
import br.gov.mec.aghu.paciente.vo.CodPacienteFoneticaVO;
import br.gov.mec.aghu.procedimentoterapeutico.action.PesquisarPacienteSessoesTerapeuticasPaginatorController;
import br.gov.mec.aghu.vo.ParametrosTelaVO;

/**
 * Classe responsável por controlar as ações da tela de pesquisa de paciente
 * 
 * @author JoseVaranda
 * 
 */
@SuppressWarnings({"PMD.ExcessiveClassLength","PMD.CyclomaticComplexity", "PMD.AvoidDuplicateLiterals"})
public class PesquisaPacienteController extends ActionController implements ActionPaginator {

	private static final String AGENDAMENTO_PROCEDIMENTO = "agendamentoProcedimento";
	private static final String CADASTRO_PACIENTE = "paciente-cadastroPaciente";
	private static final long serialVersionUID = 5036036811045857270L;
	private static final Log LOG = LogFactory.getLog(PesquisaPacienteController.class);	
	
	private static final String REDIRECIONA_CADASTRO_PACIENTE = CADASTRO_PACIENTE;
	private static final String REDIRECIONA_PESQUISA_SOLICITACAO_INTERNACAO = "internacao-pesquisaSolicitacaoInternacao";
	private static final String REDIRECIONA_SOLICITACAO_INTERNACAO = "internacao-solicitacaoInternacao";
	private static final String REDIRECIONA_ATENDIMENTO_EXTERNO_CRUD = "exames-atendimentoExternoCRUD";
	private static final String REDIRECIONA_CADASTRO_INTERNACAO = "internacao-cadastroInternacao";
	private static final String REDIRECIONA_DISPONIBILIDADE_LEITO = "internacao-pesquisarDisponibilidadeLeito";
	private static final String PAGE_PESQUISAR_PACIENTE = "pesquisarPaciente";
	private static final String PAGINA_SELECIONAR_PRESCRICAO_CONSULTAR = "prescricaomedica-selecionarPrescricaoConsultar";
	private static final String PESQUISAR_PACIENTE_CIRURGIA = "blococirurgico-pesquisarPacientesCirurgia";
	private static final String INTERNACAO = "Internacao";
	private static final String LEITOS = "Leitos";
	private static final String AGENDA_PROCEDIMENTOS = "blococirurgico-agendaProcedimentos";
	private static final String PORTAL_PESQUISA_CIRURGIAS = "blococirurgico-portalPesquisaCirurgias";
	private static final String PESQUISA_AGENDA_CIRURGIA = "blococirurgico-pesquisaAgendaCirurgia";
	private static final String CADASTRO_PLANEJAMENTO_PACIENTE_AGENDA = "blococirurgico-planejamentoPacienteAgendaCRUD";
	private static final String PAGE_PESQUISA_CIRURGIA_REALIZADA_NOTA_CONSUMO = "blococirurgico-pesquisaCirurgiaRealizadaNotaConsumo";
	private static final String PAGE_REGISTRO_CIRURGIA_REALIZADA_NOTA_CONSUMO = "blococirurgico-registroCirurgiaRealizada";
	private static final String PAGE_BLOCO_LAUDO_AIH = "blococirurgico-laudoAIH";
	private static final String PAGE_BLOCO_RELATORIO_IDENTIFICACAO = "blococirurgico-relatorioEtiquetasIdentificacao";
	private static final String PAGE_AGENDAMENTO_SESSAO = "procedimentoterapeutico-agendamentoSessao";	
	private static final String PAGE_MANUTENCAO_AGENDAMENTO_SESSAO_TERAPEUTICA= "procedimentoterapeutico-manutencaoAgendamentoSessaoTerapeutica";
	private static final String PAGE_INCLUIR_PACIENTE_LISTA_TRANSPLANTE_TMO = "transplante-incluirPacienteListaTransplanteTMOCRUD";
	private static final String PAGE_INCLUIR_PACIENTE_LISTA_TRANSPLANTE = "transplante-incluirPacienteListaTransplanteCRUD";
	private static final String PAGE_AMBULATORIO_PESQUISAR_CONSULTAS_GRADE = "ambulatorio-pesquisarConsultasGrade";
	private static final String PAGE_EXAMES_PESQUISAR_SOLICITACAO_DIVERSOS =  "exames-pesquisarSolicitacaoDiversos";
	private static final String PAGE_PESQUISAR_PACIENTES_AGENDADOS = "ambulatorio-pesquisarPacientesAgendados";
	private static final String PAGE_RECEPCIONAR_PACIENTES = "exames-listarExamesCriteriosSelecionados";
	private static final String PAGE_VISUALIZAR_PACIENTES_LISTA_ESPERA = "procedimentoterapeutico-visualizarPacientesListaEspera";
	private static final String PAGE_IMPRESSAO_TICKET_AGENDAMENTO = "procedimentoterapeutico-impressaoTicketAgendamento";
	private static final String PAGE_TEMPO_SOBREVIDA_PACIENTES_TRANSPLANTES = "transplante-relatorioTempoSobrevidaPacientesTransplantes"; // #41792
	private static final String PAGE_RELATORIO_EXTRATO_TRANSPLANTE_PACIENTE = "transplante-relatorioExtratoTransplantePorPaciente"; // #49361
	private static final String PAGE_PACIENTES_LISTAS_TMO = "transplante-pacientesListaTMO"; // #50814
	private static final String PAGE_PACIENTES_LISTAS_ORGAO = "transplante-pacientesListaOrgao"; //#50816
	private static final String PAGE_PESQUISAR_PACIENTES_SESSOES_TERAPEUTICAS = "procedimentoterapeutico-pesquisarPacientesSessoesTerapeuticas";

	@EJB
	private IPacienteFacade pacienteFacade;

	@EJB
	private IPesquisaInternacaoFacade pesquisaInternacaoFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private ICadastroPacienteFacade cadastroPacienteFacade;
	
    @EJB
    private ICascaFacade cascaFacade;
	
	//**********FAVOR NÃO INJETAR MAIS CONTROLLERS NESTA TELA. USE O PRODUCER
	@Inject
	private CadastrarPacienteController cadastrarPacienteController;
	
	@Inject
	private RetornoPesquisaPaciente retornoPesquisaPaciente;
	
	@Inject
	private PesquisaSolicitacaoInternacaoPaginatorController  pesquisaSolicitacaoInternacaoPaginatorController;
	
	@Inject
	private CadastroInternacaoController cadastroInternacaoController;
	
	@Inject
	private PesquisarPacienteSessoesTerapeuticasPaginatorController pesquisarPacienteSessoesTerapeuticasPaginatorController;
	
	private boolean registroAlteradoOutroUsuario = false;
	private boolean exibirTabelaResultados = false;
	private boolean exibirBotaoIncluir = false;
	private boolean exibirBotaoIncluirPacienteCirurgico = false;
	private boolean obrigarLista = false;
	private Boolean paramExibeBotaoIncluir = true;
	private Boolean executaPesquisaFonetica;
	private Integer codigoPaciente;
	private Integer crgSeq;
	private AipPacientes aipPaciente = new AipPacientes();
	private String mensagemModal;
	private String tituloModal;
	private Boolean exibirBotaoModalDispLeito = false;
	private Boolean exibirBotaoModalDesistir = false;
	private Boolean exibirBotaoModalInternar = false;
	private Boolean exibirBotaoModalInternarEdicao = false;
	private Boolean exibirBotaoModalContinuar = false;
	private Boolean exibirBotaoModalInternarAtendimentoUrgencia = false;
	private Boolean exibirBotaoModalCancelar = false;
	private String idLeito;
	private Short cnvCodigo;
	private Short cnvCodigoSUS;
	private Boolean modoInternacao = false;
	private Short quartoNumero;
	private Short seqUnidadeFuncional;
	private Integer seqAtendimentoUrgencia;
	private String cameFrom = "";
	private String goingTo;
	private Boolean geraProntuarioVirtual = false;
	private String nomePacienteCirurgico;
	private Boolean exibirBotaoModalOk = false;	
	private Integer codProntuarioFamiliar;

	// Constante utilizada para definir a cor do prontuario para azul na tela
	// caso o número do prontuário seja maior que 90.000.000
	private static final int LIMITE_COR = VALOR_MAXIMO_PRONTUARIO;
	private static final String NOME_PAI_MAE_PAC_CIRURGICO = "A CONF";
	private static final String OBS_PACIENTE_CIRURGICO = "Paciente Cirúrgico. Cadastro deve ser complementado";
	private SimpleDateFormat sdf = new SimpleDateFormat();
	private Boolean realizouPesquisaFonetica = false;
	private Integer numeroConsulta;
	// Utilizado para a exibição do botão de Editar
	// no componente Pesquisa de Paciente.
	// O valor é setado para true quando a tela
	// que chamou for a Pesquisa consultas do paciente
	private boolean exibeBotaoEditar = false;
	private DominioListaOrigensAtendimentos listaOrigensAtendimentos;
	
	// Filtros para pesquisa
	private Integer codigo;
	private Integer prontuario;
	private String nome;
	private String nomeMae;
	private String nomeSocial;
	private Date dtNascimento;
	private Boolean respeitarOrdem = Boolean.FALSE;
	private boolean pesquisaFonetica;
	private BigInteger nroCartaoSaude;
	private Long cpf;
	private AipPacientes pacienteSelecionado;
	
	// Classe responsável por realizar a paginação da pesquisa (inclusive pesquisa fonética).
	@Inject @Paginator
	private DynamicDataModel<AipPacientes> dataModel;
	
	private ParametrosTelaVO parametrosTela;
	
	//41770 Paramentros da tela IncluirPacienteListaTransplanteTMO
	private Boolean incluirPacienteTMO = Boolean.FALSE;
	private AipPacientes doador;
	
	//Atributos utilizados quando a tela for chamada da lista de pacientes do Ambulatório
	private String nomePacienteAmbulatorio = null;
	private Integer numeroConsultaAmbulatorio = null;	
	//#45992 parametros de retorno
	private MptTipoSessao tipoSessao;
	private Date periodoInicial;
	private Date periodoFinal;
	private Boolean checkAberto;
	private Boolean checkFechado;
	private Boolean checkPrimeiro;
	private Boolean checkSessao;
	private Integer tipoFiltroPersonalizado;
	private Integer codProntuario;
	
	@PostConstruct
	public void inicializar() {
		this.parametrosTela = new ParametrosTelaVO();
		this.begin(conversation,true);
	}
	
	public void iniciar() {
		//Execução especifica da tela de Evolução do Paciente.
		carregarPagAltaAmbulatorial();
		
		this.registroAlteradoOutroUsuario = false;
		/*Pego o valor do parametro para comparar com o vindo da tela de agendarProcedimentos, se for SUS....*/
		try {
			AghParametros pConvSUS = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_CONVENIO_SUS);
			this.cnvCodigoSUS = pConvSUS.getVlrNumerico().shortValue();
			
			// Inicializa o paciente selecionado para null, assim o Produces ignora uma selecao anterior.
			this.codigoPaciente = null;
		} catch (ApplicationBusinessException e) {
			super.apresentarExcecaoNegocio(e);
		}
	
	}

	private void carregarPagAltaAmbulatorial() {
		if(getRequestParameter("codProntuario") != null){
			this.codProntuario = Integer.valueOf(getRequestParameter("codProntuario").trim());
			AipPacientes paciente = null;
			try {
				paciente = pacienteFacade.obterPacientePorCodigoOuProntuario(this.codProntuario, null,null);
			} catch (ApplicationBusinessException e) {
				// TODO Auto-generated catch block
				apresentarExcecaoNegocio(e);
			}
			this.setAipPaciente(paciente);
			this.setProntuario(this.codProntuario);
			this.pesquisar();
		}
	}

	public void iniciaForm() {
	 

		if (!StringUtils.isBlank(this.idLeito) || quartoNumero != null	|| seqUnidadeFuncional != null) {
			this.modoInternacao = true;
		} else {
			this.modoInternacao = false;
		}

		if(aipPaciente.getNome() != null) {
			if (PAGE_BLOCO_LAUDO_AIH.equalsIgnoreCase(this.cameFrom) || 
					PORTAL_PESQUISA_CIRURGIAS.equalsIgnoreCase(this.cameFrom) ||
					PAGE_PESQUISAR_PACIENTES_AGENDADOS.equalsIgnoreCase(this.cameFrom)) {
				setExibirBotaoIncluirPacienteCirurgico(false);
				setExibirBotaoIncluir(false);
			}
		}

		if (Objects.equals(this.cameFrom,CADASTRO_PACIENTE)){
			dataModel.reiniciarPaginator();
			this.cameFrom = null;
			
		} else if (Objects.equals(this.cameFrom, "listarConsultasPorGrade")
				|| (PAGE_PESQUISAR_PACIENTES_AGENDADOS
						.equalsIgnoreCase(cameFrom) && nomePacienteAmbulatorio != null)) {
			setExibirBotaoIncluir(false);
			aipPaciente.setNome(nomePacienteAmbulatorio);
			aipPaciente.setNomeMae(null);
			aipPaciente.setDtNascimento(null);
			respeitarOrdem = false;
			if(this.executaPesquisaFonetica){
				this.pesquisarFonetica(false, false);				
			}
		}
	
	}		
	
	
	public String pesquisarFoneticaComponente() {
		return pesquisarFonetica(true, true);
	}
	
	public String pesquisarFonetica() {
		return pesquisarFonetica(true, false);
	}

	/**
	 * Método que realiza a ação do botão pesquisa fonetica na tela do paciente.
	 * Ou seja realiza a pesquisa textual utilizando os serviços do Hibernate
	 * search. No caso do AGH a fonetização irá ser feita pela classe
	 * FonetizadorUtil.
	 */
	public String pesquisarFonetica(boolean reiniciar, boolean componente) {
		
		this.realizouPesquisaFonetica = true;
		if (reiniciar) {
			dataModel.reiniciarPaginator();
		}
		
		if (!StringUtils.isBlank(aipPaciente.getNome()) || !StringUtils.isBlank(aipPaciente.getNomeMae())
				|| !StringUtils.isBlank(aipPaciente.getNomeSocial())) {
			setNomePacienteCirurgico(aipPaciente.getNome());
			nome = aipPaciente.getNome();
			nomeMae = aipPaciente.getNomeMae();
			nomeSocial = aipPaciente.getNomeSocial();
			dtNascimento = aipPaciente.getDtNascimento();
			pesquisaFonetica = true;
			this.dataModel.setPesquisaAtiva(true);
			//paginator.setLimparPersistenceContext(isLimparPersistenceContext);
			exibirBotaoIncluir = true;

			if (PAGE_BLOCO_LAUDO_AIH.equalsIgnoreCase(this.cameFrom) ||
					PORTAL_PESQUISA_CIRURGIAS.equalsIgnoreCase(this.cameFrom) ||
					PAGE_PESQUISAR_PACIENTES_AGENDADOS.equalsIgnoreCase(this.cameFrom)) {
				setExibirBotaoIncluirPacienteCirurgico(false);
				setExibirBotaoIncluir(false);
			}

			this.aipPaciente.setCodigo(null);
			this.aipPaciente.setProntuario(null);
			this.aipPaciente.setNroCartaoSaude(null);
			
			if (componente) {
				return null;
			} else {
				return detalharPacienteResultadoUnico();
			}
		} else {
			setNomePacienteCirurgico(null);
			this.dataModel.setPesquisaAtiva(false);
			exibirBotaoIncluir = false;

			if (PAGE_BLOCO_LAUDO_AIH.equalsIgnoreCase(this.cameFrom) ||
						PORTAL_PESQUISA_CIRURGIAS.equalsIgnoreCase(this.cameFrom)) {
				setExibirBotaoIncluirPacienteCirurgico(false);
				setExibirBotaoIncluir(false);
			}

			apresentarMsgNegocio(Severity.ERROR,
					"MENSAGEM_ERRO_PESQUISA_FONETICA_NOME_OBRIGATORIO");
			return null;
		}
	}

	/**
	 * Método que realiza a ação do botão pesquisa na tela de pesquisa paciente
	 */
	public String pesquisar() {
		this.realizouPesquisaFonetica = false;
		this.dataModel.reiniciarPaginator();
		
		if(this.aipPaciente.getCodigo() == null && 
				this.aipPaciente.getProntuario() == null && 
				this.aipPaciente.getCpf() == null &&
				this.aipPaciente.getNroCartaoSaude() == null &&
				this.codProntuarioFamiliar == null){
			setExibirBotaoIncluirPacienteCirurgico(false);
			this.dataModel.setPesquisaAtiva(false);
			exibirBotaoIncluir = false;
			apresentarMsgNegocio(Severity.ERROR, "AIP_PRONTUARIO_E_COGIDO_NULOS");
			return null;
		} else if (this.aipPaciente.getCodigo() != null || 
				this.aipPaciente.getProntuario() != null || 
				this.aipPaciente.getNroCartaoSaude() != null ||
				this.aipPaciente.getCpf() != null ||
				this.codProntuarioFamiliar != null) {
			codigo = this.aipPaciente.getCodigo();
			prontuario = this.aipPaciente.getProntuario();
			nroCartaoSaude = this.aipPaciente.getNroCartaoSaude();
			cpf = this.aipPaciente.getCpf();			
			pesquisaFonetica = false;
			//paginator.setLimparPersistenceContext(isLimparPersistenceContext);
			exibirBotaoIncluir = true;

			if (PAGE_BLOCO_LAUDO_AIH.equalsIgnoreCase(this.cameFrom) ||
						PORTAL_PESQUISA_CIRURGIAS.equalsIgnoreCase(this.cameFrom)
						||PAGE_PESQUISAR_PACIENTES_AGENDADOS.equalsIgnoreCase(this.cameFrom)) {
				setExibirBotaoIncluirPacienteCirurgico(false);
				setExibirBotaoIncluir(false);
			}

			this.dataModel.setPesquisaAtiva(true);

			this.aipPaciente.setNome(null);
			this.aipPaciente.setNomeMae(null);
			this.aipPaciente.setDtNascimento(null);
			this.respeitarOrdem = Boolean.FALSE;
		} else {
			this.dataModel.setPesquisaAtiva(false);
			exibirBotaoIncluir = false;

			if (PAGE_BLOCO_LAUDO_AIH.equalsIgnoreCase(this.cameFrom) ||
						PORTAL_PESQUISA_CIRURGIAS.equalsIgnoreCase(this.cameFrom)) {
				setExibirBotaoIncluirPacienteCirurgico(false);
				setExibirBotaoIncluir(false);
			}
		}

		return detalharPacienteResultadoUnico();
		
	}

	private String detalharPacienteResultadoUnico() {
		List<AipPacientes> listPacienteResult = this.dataModel.getPaginator().recuperarListaPaginada(0, 2, null, true);
		if (!PAGE_PESQUISAR_PACIENTES_AGENDADOS.equalsIgnoreCase(cameFrom) || nomePacienteAmbulatorio == null){
			if (listPacienteResult != null && listPacienteResult.size() == 1){
				AipPacientes pacienteResult = listPacienteResult.get(0);
				if (pacienteResult.getNome().equalsIgnoreCase(aipPaciente.getNome())){
					pacienteSelecionado = pacienteResult;
					codigoPaciente = pacienteResult.getCodigo();
					if ("internacao-solicitacaoInternacao".equalsIgnoreCase(cameFrom)
							|| "internacao-pesquisarDisponibilidadeLeito".equalsIgnoreCase(cameFrom)
							|| "internacao-pesquisarDisponibilidadeUnidade".equalsIgnoreCase(cameFrom)) {
						return navegarInternacao(pacienteResult);
					} else {
						return detalhar();
					}				
				}
			}			
		}
		return null;
	}
	/**
	 * Método que seta a cor do prontuario para azul na tela caso o número do
	 * prontuário seja maior que 90.000.000
	 * 
	 * @return String
	 */
	public String buscarEstiloCampoProntuario(Integer prontuario) {
		String cor = "";
		if (prontuario!=null && prontuario > LIMITE_COR) {
			cor = "color:blue";
		}
		return cor;
	}

	public void limparCampos() {
		aipPaciente.setProntuario(null);
		aipPaciente.setCodigo(null);
		this.aipPaciente.setNome(null);
		this.aipPaciente.setNomeMae(null);
		this.aipPaciente.setNomeSocial(null);
		this.aipPaciente.setDtNascimento(null);
		this.aipPaciente.setCpf(null);
		this.aipPaciente.setNroCartaoSaude(null);
		
		setCodProntuarioFamiliar(null);	
		this.respeitarOrdem = Boolean.FALSE;
		exibirBotaoIncluir = false;
		setNomePacienteCirurgico(null);

		if (PAGE_BLOCO_LAUDO_AIH.equalsIgnoreCase(this.cameFrom) ||
					PORTAL_PESQUISA_CIRURGIAS.equalsIgnoreCase(this.cameFrom)) {
			setExibirBotaoIncluirPacienteCirurgico(false);
			setExibirBotaoIncluir(false);
		}

		this.dataModel.setPesquisaAtiva(false);
	}

	public String cancelar() {
		// TODO: verificar para este método as páginas usadas nas estórias que
		// chamam a Pesquisa por Paciente quando estas estórias forem migradas
		// para a nova arquitetura
		if ("pesquisarCensoDiarioPacientes".equalsIgnoreCase(cameFrom)) {
			return REDIRECIONA_SOLICITACAO_INTERNACAO;
		} else if ("solicitacaoInternacao".equalsIgnoreCase(cameFrom)) {
			return REDIRECIONA_PESQUISA_SOLICITACAO_INTERNACAO;
		} else if ("listarConsultasPorGrade".equalsIgnoreCase(cameFrom)){
			return "listarConsultasPorGrade";
		} else if ("listarExamesCriteriosSelecionados".equalsIgnoreCase(cameFrom)){
			return "listarExamesCriteriosSelecionados";
		} else if ("pesquisarExames".equalsIgnoreCase(cameFrom)){
			return "pesquisarExames";
		} else if ("elaboracaoPrescricaoEnfermagem".equalsIgnoreCase(cameFrom)){
			return "elaboracaoPrescricaoEnfermagem";
		} else if ("liberaLimitacaoExameSolicitacao".equalsIgnoreCase(cameFrom)){
			return "liberaLimitacaoExameSolicitacao";
		} else if ("tratarOcorrencias".equalsIgnoreCase(cameFrom)){
			return "tratarOcorrencias";
		} else if ("alterarDispensacaoMedicamento".equalsIgnoreCase(cameFrom)){
			return "alterarDispensacaoMedicamento";
		} else if ("estornaMedicamentoDispensado".equalsIgnoreCase(cameFrom)){
			return "estornaMedicamentoDispensado";
		} else if (REDIRECIONA_DISPONIBILIDADE_LEITO.equalsIgnoreCase(cameFrom)){
			return REDIRECIONA_DISPONIBILIDADE_LEITO;
		} else if ("pesquisarDisponibilidadeQuarto".equalsIgnoreCase(cameFrom)){
			return "internacao-pesquisarDisponibilidadeQuarto";
		} else if ("pesquisarDisponibilidadeUnidade".equalsIgnoreCase(cameFrom)){
			return "pesquisarDisponibilidadeUnidade";
		} else if ("atualizarConsulta".equalsIgnoreCase(cameFrom)){
			return "atualizarConsulta";
		} else if ("blococirurgico-pesquisarPacientesCirurgia".equalsIgnoreCase(cameFrom)) {
			return PESQUISAR_PACIENTE_CIRURGIA;
		} else if ("pesquisarPacientesCirurgia".equalsIgnoreCase(cameFrom)){
			return "pesquisarPacientesCirurgia";
		} else if ("farmacia-tratarOcorrenciasList".equalsIgnoreCase(cameFrom)){
			return "farmacia-tratarOcorrenciasList";
		} else if ("farmacia-estornar-mdto".equalsIgnoreCase(cameFrom)){
			return "farmacia-estornar-mdto";
		} else if ("blococirurgico-pesquisaAgendaCirurgia".equalsIgnoreCase(cameFrom)){
			return PESQUISA_AGENDA_CIRURGIA;
		} else if("blococirurgico-pesquisaCirurgiaRealizadaNotaConsumo".equalsIgnoreCase(cameFrom)){
			return PAGE_PESQUISA_CIRURGIA_REALIZADA_NOTA_CONSUMO;
		} else if (PORTAL_PESQUISA_CIRURGIAS.equalsIgnoreCase(cameFrom)) {
			return PORTAL_PESQUISA_CIRURGIAS;
		} else if("blococirurgico-planejamentoPacienteAgendaCRUD".equalsIgnoreCase(cameFrom)){
			return CADASTRO_PLANEJAMENTO_PACIENTE_AGENDA;
		} else if (PAGINA_SELECIONAR_PRESCRICAO_CONSULTAR.equalsIgnoreCase(cameFrom)) {
			return PAGINA_SELECIONAR_PRESCRICAO_CONSULTAR;
		} else if (AGENDAMENTO_PROCEDIMENTO.equalsIgnoreCase(cameFrom)) {
			return AGENDA_PROCEDIMENTOS;
		} else if (PAGE_BLOCO_LAUDO_AIH.equalsIgnoreCase(cameFrom)) {
			return PAGE_BLOCO_LAUDO_AIH;
		} else if (PAGE_BLOCO_RELATORIO_IDENTIFICACAO.equalsIgnoreCase(cameFrom)) {
			return PAGE_BLOCO_RELATORIO_IDENTIFICACAO;
		} else if ("agendamentoSessao".equalsIgnoreCase(cameFrom)) {
			return PAGE_AGENDAMENTO_SESSAO;
		} else if (PAGE_REGISTRO_CIRURGIA_REALIZADA_NOTA_CONSUMO.equals(cameFrom)) {
			return PAGE_REGISTRO_CIRURGIA_REALIZADA_NOTA_CONSUMO;
		} else if(PAGE_EXAMES_PESQUISAR_SOLICITACAO_DIVERSOS.equalsIgnoreCase(cameFrom)){
			return PAGE_EXAMES_PESQUISAR_SOLICITACAO_DIVERSOS;		} else if (PAGE_MANUTENCAO_AGENDAMENTO_SESSAO_TERAPEUTICA.equals(cameFrom)){
			return PAGE_MANUTENCAO_AGENDAMENTO_SESSAO_TERAPEUTICA;
		} else if (PAGE_IMPRESSAO_TICKET_AGENDAMENTO.equalsIgnoreCase(cameFrom)) {
			return PAGE_IMPRESSAO_TICKET_AGENDAMENTO;
		}else if(PAGE_VISUALIZAR_PACIENTES_LISTA_ESPERA.equalsIgnoreCase(cameFrom)){
			return PAGE_VISUALIZAR_PACIENTES_LISTA_ESPERA;
		}else if (PAGE_TEMPO_SOBREVIDA_PACIENTES_TRANSPLANTES.equalsIgnoreCase(cameFrom)) {		// #41792
			return PAGE_TEMPO_SOBREVIDA_PACIENTES_TRANSPLANTES;
		}else if (PAGE_RELATORIO_EXTRATO_TRANSPLANTE_PACIENTE.equalsIgnoreCase(cameFrom)) {		// #49356
			return PAGE_RELATORIO_EXTRATO_TRANSPLANTE_PACIENTE;
		}else if (PAGE_PACIENTES_LISTAS_TMO.equalsIgnoreCase(cameFrom)) {		// #50814
			return PAGE_PACIENTES_LISTAS_TMO;
		}else if (PAGE_PACIENTES_LISTAS_ORGAO.equalsIgnoreCase(cameFrom)) {		// #50816
			return PAGE_PACIENTES_LISTAS_ORGAO;
		}
		return cameFrom;
	}
	
	/**
	 * Utilizar o método comentado para fazer o retorno a página de origem
	 */
	
	public String retornar(boolean isBotaoVoltar){
		limparCampos();
		
		if(isBotaoVoltar){
			this.codigoPaciente = null;
		}
		
		if(cameFrom != null) {
			if(cameFrom.equals(PAGE_PESQUISAR_PACIENTES_SESSOES_TERAPEUTICAS)){
				pesquisarPacienteSessoesTerapeuticasPaginatorController.setTipoSessao(tipoSessao);
				pesquisarPacienteSessoesTerapeuticasPaginatorController.setPeriodoInicial(periodoInicial);
				pesquisarPacienteSessoesTerapeuticasPaginatorController.setPeriodoFinal(periodoFinal);
				pesquisarPacienteSessoesTerapeuticasPaginatorController.setCheckAberto(checkAberto);
				pesquisarPacienteSessoesTerapeuticasPaginatorController.setCheckFechado(checkFechado);
				pesquisarPacienteSessoesTerapeuticasPaginatorController.setCheckPrimeiro(checkPrimeiro);
				pesquisarPacienteSessoesTerapeuticasPaginatorController.setCheckSessao(checkSessao);
				pesquisarPacienteSessoesTerapeuticasPaginatorController.setTipoFiltroPersonalizado(tipoFiltroPersonalizado);
				pesquisarPacienteSessoesTerapeuticasPaginatorController.processarBuscaPacientePorCodigo(this.codigoPaciente);
				
				return PAGE_PESQUISAR_PACIENTES_SESSOES_TERAPEUTICAS;
				
			} else {
				return retornoPesquisaPaciente.execute(cameFrom, codigoPaciente, isBotaoVoltar );
			}
		} else {
			apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ERRO_RETORNO_PAGINA");
		}
		
		return cameFrom;
	}
	
	public String detalhar() {
		if (AGENDAMENTO_PROCEDIMENTO.equalsIgnoreCase(this.cameFrom)
				|| PAGE_BLOCO_LAUDO_AIH.equalsIgnoreCase(this.cameFrom)
				|| PORTAL_PESQUISA_CIRURGIAS.equalsIgnoreCase(this.cameFrom)
				|| PAGE_RECEPCIONAR_PACIENTES.equalsIgnoreCase(this.cameFrom)
				|| PAGE_BLOCO_RELATORIO_IDENTIFICACAO.equalsIgnoreCase(this.cameFrom)) {
			return null;
		}

		this.dataModel.reiniciarPaginator();
		this.parametrosTela.limparParametros();
		this.cadastrarPacienteController.setCameFrom(this.cameFrom);
		if (PAGE_PESQUISAR_PACIENTES_AGENDADOS.equalsIgnoreCase(this.cameFrom)){
			this.cadastrarPacienteController.setGoingTo(cameFrom);
			this.cadastrarPacienteController.setTrocarPacienteConsulta(true);
		}
		else{
			this.cadastrarPacienteController.setGoingTo(null);			
		}
		this.cadastrarPacienteController.setIdLeito(null);
		this.cadastrarPacienteController.setQuartoNumero(null);
		this.cadastrarPacienteController.setSeqUnidadeFuncional(null);
		this.cadastrarPacienteController.setSeqAtendimentoUrgencia(null);		
		this.cadastrarPacienteController.setEdicaoInternacao(false);
		this.cadastrarPacienteController.prepararEdicaoPaciente(pacienteSelecionado, null);
		
		return REDIRECIONA_CADASTRO_PACIENTE;
	}
	
	public String detalharComponente(AipPacientes paciente){
		this.cadastrarPacienteController.setCameFrom(this.cameFrom);
		this.cadastrarPacienteController.setGoingTo(null);
		this.cadastrarPacienteController.setIdLeito(null);
		this.cadastrarPacienteController.setQuartoNumero(null);
		this.cadastrarPacienteController.setSeqUnidadeFuncional(null);
		this.cadastrarPacienteController.setSeqAtendimentoUrgencia(null);		
		this.cadastrarPacienteController.prepararEdicaoPaciente(paciente, null);
		return REDIRECIONA_CADASTRO_PACIENTE;
	}
	
	/**
	 * #6171 - Atualiza consulta com paciente selecionado.
	 */
	public String atualizarPacienteConsulta() {
		if ((this.pacienteSelecionado.getProntuario() != null)
				&& (this.pacienteSelecionado.getCadConfirmado() != null && this.pacienteSelecionado
						.getCadConfirmado().equals(DominioSimNao.S))) {
			pacienteFacade.atualizarPacienteConsulta(this.pacienteSelecionado, this.numeroConsulta);
			apresentarMsgNegocio("SUCESSO_ATUALIZACAO_PACIENTE");
			return PAGE_AMBULATORIO_PESQUISAR_CONSULTAS_GRADE;
		} else {
			cadastrarPacienteController.setNumeroConsulta(numeroConsulta);
			return this.detalhar();
		}
	}

	/**
	 * Método que realiza a ação do botão Gravar na Modal de cadastro de
	 * paciente cirúrgico
	 * 
	 * @return Tela de agendamento de cirurgias eletivas e não previstas
	 */
	public String incluirPacienteCirurgico() {
		try {
			if (!StringUtils.isBlank(getNomePacienteCirurgico())) {
				aipPaciente.setCodigo(null);
				aipPaciente.setProntuario(null);
				aipPaciente.setNome(getNomePacienteCirurgico());
				aipPaciente.setSexo(DominioSexo.M);
				aipPaciente.setCor(DominioCor.B);
				aipPaciente.setEstadoCivil(DominioEstadoCivil.S);
				aipPaciente.setDtNascimento(DateUtil.obterData(1800, 0, 1));
				aipPaciente.setNomeMae(NOME_PAI_MAE_PAC_CIRURGICO);
				aipPaciente.setNomePai(NOME_PAI_MAE_PAC_CIRURGICO);
				aipPaciente.setObservacao(OBS_PACIENTE_CIRURGICO);
				aipPaciente.setCadConfirmado(DominioSimNao.N);
				aipPaciente.setIndGeraProntuario(DominioSimNao.N);

				// Persiste o paciente
				this.cadastroPacienteFacade.persistirPacienteCirurgico(aipPaciente);
				
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CRIACAO_PACIENTE_CIRURGICO");
			}

		} catch (ApplicationBusinessException e) {
			LOG.error(e.getMessage(), e);
			super.apresentarExcecaoNegocio(e);
			
			return null;
		} catch (BaseException e) {
			LOG.error(e.getMessage(), e);
			super.apresentarExcecaoNegocio(e);
			
			return null;
		}
		
		return this.selecionarPaciente(aipPaciente.getCodigo());
	}

	// ### GETs e SETs ###
	
	public Integer getNumeroConsulta() {
		return numeroConsulta;
	}

	public void setNumeroConsulta(Integer numeroConsulta) {
		this.numeroConsulta = numeroConsulta;
	}

	public void setRespeitarOrdem(Boolean respeitarOrdem) {
		this.respeitarOrdem = respeitarOrdem;
	}

	public Boolean getRespeitarOrdem() {
		return respeitarOrdem;
	}

	public AipPacientes getAipPaciente() {
		return aipPaciente;
	}

	public void setAipPaciente(AipPacientes aipPaciente) {
		this.aipPaciente = aipPaciente;
	}
	
	public boolean isRegistroAlteradoOutroUsuario() {
		return registroAlteradoOutroUsuario;
	}
	
	public void setRegistroAlteradoOutroUsuario(boolean registroAlteradoOutroUsuario) {
		this.registroAlteradoOutroUsuario = registroAlteradoOutroUsuario;
	}

	public boolean isExibirTabelaResultados() {
		return exibirTabelaResultados;
	}

	public void setExibirTabelaResultados(boolean exibirTabelaResultados) {
		this.exibirTabelaResultados = exibirTabelaResultados;
	}

	public void reindexarPosicaoFonemas() {
		// pacienteON.reindexarPosicaoFonemas();
	}

	public String getCameFrom() {
		return cameFrom;
	}

	public void setCameFrom(String cameFrom) {
		this.cameFrom = cameFrom;
	}
	
	public String navegarInternacao(final Integer codPaciente){
		AipPacientes pacienteSelecionado = 	this.pacienteFacade.obterAipPacientesPorChavePrimaria(codPaciente);
		return navegarInternacao(pacienteSelecionado);
	}
	
	public String navegarInternacao(final AipPacientes pacienteSelecionado) {
		String retorno = null;
		this.registroAlteradoOutroUsuario = false;
		try {

			this.pacienteSelecionado = pacienteSelecionado;
			this.codigoPaciente = pacienteSelecionado.getCodigo();

			Boolean alerta = false;
			exibirBotaoModalInternar = false;
			exibirBotaoModalInternarEdicao = false;
			exibirBotaoModalInternarAtendimentoUrgencia = false;
			exibirBotaoModalDispLeito = false;
			exibirBotaoModalContinuar = false;
			exibirBotaoModalDesistir = false;
			exibirBotaoModalCancelar = false;
			exibirBotaoModalOk = false;
			tituloModal = WebUtil.initLocalizedMessage("LABEL_CONFIRMAR", null);

			if (this.pesquisaInternacaoFacade.verificarPacienteInternado(codigoPaciente)) {
				alerta = true;
				exibirBotaoModalInternar = true;
				exibirBotaoModalCancelar = true;
				this.mensagemModal = "Paciente está internado. Deseja Alterar/Consultar?";
			} else if (this.pesquisaInternacaoFacade.verificarPacienteHospitalDia(codigoPaciente)) {
				alerta = true;
				if (!this.modoInternacao) {
					exibirBotaoModalInternarEdicao = true;
					exibirBotaoModalDispLeito = true;
					exibirBotaoModalCancelar = true;
				}
				this.mensagemModal = "Paciente está em atendimento no Hospital Dia. Se o paciente for internado este atendimento será encerrado.";

			} else if (!StringUtils.isBlank(idLeito) || quartoNumero != null) {
				try {
					if (!StringUtils.isBlank(idLeito)) {
						this.pesquisaInternacaoFacade.consistirSexoLeito(codigoPaciente, idLeito);
					}
					if (quartoNumero != null) {
						this.pesquisaInternacaoFacade.consistirSexoQuarto(codigoPaciente, quartoNumero);
					}
				} catch (ApplicationBusinessException ex) {
					alerta = true;
					exibirBotaoModalOk = true;
					tituloModal = WebUtil.initLocalizedMessage("LABEL_INTERNAR", null);
					this.mensagemModal = WebUtil.initLocalizedMessage(ex.getCode().toString(), null);
				}
			}

			if (!alerta) {
				AinAtendimentosUrgencia atUrg = this.pesquisaInternacaoFacade.obterPacienteAtendimentoUrgencia(codigoPaciente);
				if (atUrg != null) {
					alerta = true;
					setSeqAtendimentoUrgencia(atUrg.getSeq());

					if (!this.modoInternacao) {
						exibirBotaoModalDispLeito = true;
						exibirBotaoModalInternarAtendimentoUrgencia = true;
						exibirBotaoModalDesistir = true;
					} else {
						exibirBotaoModalContinuar = true;
						exibirBotaoModalDesistir = true;
					}
					this.mensagemModal = "Paciente está em atendimento de urgência.";
				} else {
					setSeqAtendimentoUrgencia(null);
				}
			}

			if (!alerta) {
				if (this.modoInternacao) {

					parametrosTela.limparParametros();
					parametrosTela.setParametro("aip_pac_codigo", codigoPaciente);
					// parametrosTela.setParametro("aac_exige_prontuario",
					// null);
					parametrosTela.setParametro("cameFrom", INTERNACAO);
					parametrosTela.setParametro("goingTo", INTERNACAO);
					parametrosTela.setParametro("ain_leito_id", this.idLeito);
					parametrosTela.setParametro("ain_quarto_numero", this.quartoNumero);
					parametrosTela.setParametro("agh_uni_func_seq", this.seqUnidadeFuncional);
					// parametrosTela.setParametro("ain_atd_urgencia_seq",
					// null);
					// parametrosTela.setParametro("geraProntuarioVirtual",
					// null);
					parametrosTela.setParametro("edicaoInternacao", true);
					cadastrarPacienteController.setEdicaoInternacao(true);
					cadastrarPacienteController.setParametrosTela(parametrosTela);
					retorno = redirecionarEdicaoInternacao();
				} else {

					parametrosTela.limparParametros();
					parametrosTela.setParametro("aip_pac_codigo", codigoPaciente);
					// parametrosTela.setParametro("aac_exige_prontuario",
					// null);
					parametrosTela.setParametro("cameFrom", INTERNACAO);
					parametrosTela.setParametro("goingTo", LEITOS);
					// parametrosTela.setParametro("ain_leito_id", null);
					// parametrosTela.setParametro("ain_quarto_numero", null);
					// parametrosTela.setParametro("agh_uni_func_seq", null);
					parametrosTela.setParametro("ain_atd_urgencia_seq", this.seqAtendimentoUrgencia);
					// parametrosTela.setParametro("geraProntuarioVirtual",
					// null);
					parametrosTela.setParametro("edicaoInternacao", true);
					cadastrarPacienteController.setEdicaoInternacao(true);

					cadastrarPacienteController.setParametrosTela(parametrosTela);
					retorno = redirecionarEdicaoPaciente();
				}
			}else{
				openDialog("modalConfirmacaoWG");
			}
			if (exibirBotaoModalOk){
				openDialog("modalConfirmacaoWG");
			}

		} catch (BaseRuntimeException e) {
			this.registroAlteradoOutroUsuario = true;
			apresentarExcecaoNegocio(e);
			return null;
		}
		if (registroAlteradoOutroUsuario){
			openDialog("modalConfirmacaoWG");
		}
		return retorno;
	}

	public String getModalMessage() {
		return this.mensagemModal;
	}

	public Boolean renderLinkInternar(AipPacientes paciente) {
		Boolean retorno = true;

		if (this.cameFrom != null && this.cameFrom.equalsIgnoreCase("internacao-solicitacaoInternacao") 
				|| paciente.getDtObito() != null
				|| paciente.getTipoDataObito() != null
				|| paciente.getProntuario() == null) {
			retorno = false;
		}
		return retorno;
	}

	public Boolean getModuloExamesAtivo() {
		return cascaFacade.verificarSeModuloEstaAtivo(DominioModulo.EXAMES_LAUDOS.getDescricao());		
	}
	
	public String buscaDescricaoObito(AipPacientes paciente) {
		if (paciente.getDtObito() != null) {
			sdf.applyPattern("dd/MM/yyyy");
			return WebUtil.initLocalizedMessage("LABEL_OBITO", null) + ": " + sdf.format(paciente.getDtObito());
		} else if (paciente.getTipoDataObito() != null) {
			if (paciente.getTipoDataObito() == DominioTipoDataObito.IGN) {
				return WebUtil.initLocalizedMessage("LABEL_OBITO_IGNORADO", null);
			} else {
				if (paciente.getTipoDataObito()
						.equals(DominioTipoDataObito.DMA)) {
					sdf.applyPattern("dd/MM/yyyy");
				} else {
					sdf.applyPattern(paciente.getTipoDataObito().getPattern());
				}

				if (paciente.getDtObitoExterno() != null) {
					return WebUtil.initLocalizedMessage("LABEL_OBITO_EXTERNO",
							null) + ": "
							+ sdf.format(paciente.getDtObitoExterno());
				}
			}
		}

		return "";
	}

	public String selecionarPaciente(Integer codigoPaciente) {
		this.codigoPaciente = codigoPaciente; 
		return this.retornar(false);
	}	
	
	@Produces @RequestScoped @SelectionQualifier
	public CodPacienteFoneticaVO obterPacienteSelecionado(){
		CodPacienteFoneticaVO vo = new CodPacienteFoneticaVO();
		if (codigoPaciente!=null){ 
			vo.setCodigo(codigoPaciente);
			return vo;
		} else {
			vo.setCodigo(-1);
			return vo;
		}
	}
	
	
	@Override
	public Long recuperarCount() {
		Long count = 0L;

		try {
			if (pesquisaFonetica) {
				count = pacienteFacade.pesquisarPorFonemasCount(criaPesquisaFoneticaPacienteCount());
			} else {
				
				if(this.codProntuarioFamiliar != null){
					 Integer contador = pacienteFacade.obterPacientePorCodigoOuProntuarioFamiliar(prontuario, codigo, this.codProntuarioFamiliar).size();					
					 count = contador.longValue();
				}else{
					
				count = pacienteFacade.pesquisarPacienteCount(prontuario, codigo, cpf, nroCartaoSaude);
			}

			}
		} catch (ApplicationBusinessException e) {
			exibirBotaoIncluir = false;
			this.dataModel.setPesquisaAtiva(false);
			apresentarExcecaoNegocio(e);
		}

		//Detalhar paciente quando retorno da pesquisa devolver somente 1 registro
		if(count == 1 && codigo == null){
			AipPacientes pac;
			try {
				pac = pacienteFacade.obterPacientePorCodigoOuProntuario(prontuario, codigo, cpf, nroCartaoSaude);
				if (pac != null) {
					codigo = pac.getCodigo();
				}
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}

		}
		
		return count;
	}

	private PesquisaFoneticaPaciente criaPesquisaFoneticaPacienteCount() {
		PesquisaFoneticaPaciente paramPesquisa = new PesquisaFoneticaPaciente();
		paramPesquisa.setNome(nome);
		paramPesquisa.setNomeMae(nomeMae);
		paramPesquisa.setNomeSocial(nomeSocial);
		paramPesquisa.setRespeitarOrdem(respeitarOrdem);
		paramPesquisa.setDataNascimento(dtNascimento);
		paramPesquisa.setListaOrigensAtendimentos(listaOrigensAtendimentos);
		paramPesquisa.setIsCount(true);
		
		return paramPesquisa;
	}
	
	public AipPacientes buscarPacienteUnico(){
		AipPacientes paciente = null;
		List<AipPacientes> result = recuperarListaPaginada(0,10,null, false);
		if(result != null && result.size() == 1){
			//Retorna o único paciente encontrado
			paciente = result.get(0);
		}
		return paciente;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<AipPacientes> recuperarListaPaginada(Integer firstResult,
			Integer maxResults, String orderProperty, boolean asc) {
		List<AipPacientes> result = null;

		try {
			
			if (PAGE_BLOCO_LAUDO_AIH.equalsIgnoreCase(this.cameFrom) || 
				PORTAL_PESQUISA_CIRURGIAS.equalsIgnoreCase(this.cameFrom) ||
				PAGE_PESQUISAR_PACIENTES_AGENDADOS.equalsIgnoreCase(this.cameFrom)) {
				exibirBotaoIncluir = false;
			} else {
				exibirBotaoIncluir = true;
			}

			if (pesquisaFonetica) {
				PesquisaFoneticaPaciente paramPequisa = criaParametroPesquisaFonetica(firstResult, maxResults, nome,
						nomeMae, nomeSocial, respeitarOrdem, dtNascimento, listaOrigensAtendimentos);
				result = pacienteFacade.pesquisarPorFonemas(paramPequisa);
			} else {
				if (this.codProntuarioFamiliar != null) {
					result =  pacienteFacade.obterPacientePorCodigoOuProntuarioFamiliar(prontuario, codigo, this.codProntuarioFamiliar);
				} else {
					result = pacienteFacade.obterPacientePorCartaoCpfCodigoPronturario(prontuario, codigo, cpf, nroCartaoSaude);
				}
			}
		} catch (ApplicationBusinessException e) {
			exibirBotaoIncluir = false;
			this.dataModel.setPesquisaAtiva(false);
			apresentarExcecaoNegocio(e);
		}

		if (result == null) {
			result = new ArrayList<AipPacientes>();
		}

		return result;
	}	
	
	private PesquisaFoneticaPaciente criaParametroPesquisaFonetica(Integer firstResult, Integer maxResults,
			String nome, String nomeMae, String nomeSocial, Boolean respeitarOrdem, Date dtNascimento,
			DominioListaOrigensAtendimentos listaOrigensAtendimentos) {
		
		PesquisaFoneticaPaciente param = new PesquisaFoneticaPaciente();
		param.setFirstResult(firstResult);
		param.setMaxResults(maxResults);
		param.setNome(nome);
		param.setNomeMae(nomeMae);
		param.setNomeSocial(nomeSocial);
		param.setRespeitarOrdem(respeitarOrdem);
		param.setDataNascimento(dtNascimento);
		param.setListaOrigensAtendimentos(listaOrigensAtendimentos);
		
		return param;
	}
	
	public String redirecionarIncluirPaciente() {
		// TODO: injetar CadastrarPacienteController e passar os seguintes parametros (considerar o atributo pacienteSelecionado) 
		// que antes eram passados usando o Seam (page.xml):
		//		   <param name="aac_exige_prontuario" value="#{pesquisaPacienteController.aipPaciente.indGeraProntuario}"/>
		//		   <param name="aip_pac_codigo" value=""/>
		//		   <param name="cameFrom" value="#{pesquisaPacienteController.cameFrom}"/>
		//		   <param name="ain_leito_id" value="#{pesquisaPacienteController.idLeito}"/>
		//		   <param name="ain_quarto_numero"  value="#{pesquisaPacienteController.quartoNumero}"/>
		//		   <param name="agh_uni_func_seq"  value="#{pesquisaPacienteController.seqUnidadeFuncional}"/>
		
		// TODO: setar a sugestão de nome do paciente (o nome que foi pesquisado é setado para já fazer o cadastro) 
		// cadastrarPacienteController.setNomePesquisar(nomePesquisaPaciente);

		cadastrarPacienteController.setAacExigeProntuario(this.aipPaciente.getIndGeraProntuario());
		cadastrarPacienteController.setPacCodigo(null);
		cadastrarPacienteController.setCameFrom(this.cameFrom);
		cadastrarPacienteController.setIdLeito(this.idLeito);
		cadastrarPacienteController.setQuartoNumero(this.quartoNumero);
		cadastrarPacienteController.setSeqUnidadeFuncional(this.seqUnidadeFuncional);
		cadastrarPacienteController.setEdicaoInternacao(false);

		cadastrarPacienteController.prepararInclusaoPaciente(this.aipPaciente.getNome(), this.cameFrom, this.aipPaciente.getIndGeraProntuario());
		return REDIRECIONA_CADASTRO_PACIENTE;
	}
	
	public String redirecionarPesquisaSolicitacaoInternacao() {
		this.pesquisaSolicitacaoInternacaoPaginatorController.setCodigoPaciente(pacienteSelecionado.getCodigo());
		this.pesquisaSolicitacaoInternacaoPaginatorController.carregarPaciente();
		return REDIRECIONA_PESQUISA_SOLICITACAO_INTERNACAO;
	}
	
	public String redirecionarSolicitacaoInternacao() {
		// TODO: injetar SolicitacaoInternacaoController e
		// setar o codigo do pacienteSelecionado na mesma antes de redirecionar
		return REDIRECIONA_SOLICITACAO_INTERNACAO;
	}
	
	public String redirecionarAtendimentoExternoCrud() {
		return REDIRECIONA_ATENDIMENTO_EXTERNO_CRUD;
	}
	
	public String redirecionarCadastroInternacao() {
		cadastroInternacaoController.setAipPacCodigo(this.codigoPaciente);
		cadastroInternacaoController.setCameFrom(this.cameFrom != null ? cameFrom : PAGE_PESQUISAR_PACIENTE);
		cadastroInternacaoController.inicio();
		
		return REDIRECIONA_CADASTRO_INTERNACAO;
	}
	
	public String redirecionarEdicaoInternacao() {
		
		cadastrarPacienteController.setPacCodigo(codigoPaciente);
		cadastrarPacienteController.setIdLeito(idLeito);
		cadastrarPacienteController.setCameFrom(REDIRECIONA_DISPONIBILIDADE_LEITO);
		cadastrarPacienteController.setQuartoNumero(quartoNumero);
		cadastrarPacienteController.setSeqUnidadeFuncional(seqUnidadeFuncional);
		cadastrarPacienteController.prepararEdicaoPaciente(pacienteSelecionado, null);
		cadastrarPacienteController.setGoingTo("internacao-cadastroInternacao");	
		
		// TODO: injetar CadastrarPacienteController e
		// setar o codigo do pacienteSelecionado e parametros abaixo na mesma antes de redirecionar
		//	    <param name="aip_pac_codigo" value="#{pesquisaPacienteController.codigoPaciente}"/>
		//	    <param name="aac_exige_prontuario" value=""/>
		//	    <param name="cameFrom" value="Internacao"/>
		//	    <param name="goingTo" value="Internacao"/>
		//	    <param name="ain_leito_id" value="#{pesquisaPacienteController.idLeito}"/>
		//	    <param name="ain_quarto_numero"  value="#{pesquisaPacienteController.quartoNumero}"/>
		//	    <param name="agh_uni_func_seq"  value="#{pesquisaPacienteController.seqUnidadeFuncional}"/>
		//	    <param name="ain_atd_urgencia_seq" value=""/>
		//	    <param name="geraProntuarioVirtual" value=""/>
		return REDIRECIONA_CADASTRO_PACIENTE;
	}
	
	public String redirecionarInternacaoAtendimentoUrgencia() {
		parametrosTela.limparParametros();
		parametrosTela.setParametro("aip_pac_codigo", codigoPaciente);
		parametrosTela.setParametro("cameFrom", INTERNACAO);
		parametrosTela.setParametro("goingTo", INTERNACAO);
		parametrosTela.setParametro("ain_leito_id", idLeito);
		parametrosTela.setParametro("ain_quarto_numero", quartoNumero);
		parametrosTela.setParametro("agh_uni_func_seq", seqUnidadeFuncional);
		parametrosTela.setParametro("ain_atd_urgencia_seq", seqAtendimentoUrgencia);

		parametrosTela.setParametro("edicaoInternacao", true);
		cadastrarPacienteController.setEdicaoInternacao(true);

		cadastrarPacienteController.setParametrosTela(parametrosTela);
	    
		// TODO: injetar CadastrarPacienteController e
		// setar o codigo do pacienteSelecionado e parametros abaixo na mesma antes de redirecionar
		//	    <param name="aip_pac_codigo" value="#{pesquisaPacienteController.codigoPaciente}"/>
		//	    <param name="aac_exige_prontuario" value=""/>
		//	    <param name="cameFrom" value="Internacao"/>
		//	    <param name="goingTo" value="Internacao"/>
		//	    <param name="ain_leito_id" value="#{pesquisaPacienteController.idLeito}"/>
		//	    <param name="ain_quarto_numero"  value="#{pesquisaPacienteController.quartoNumero}"/>
		//	    <param name="agh_uni_func_seq"  value="#{pesquisaPacienteController.seqUnidadeFuncional}"/>
		//	    <param name="ain_atd_urgencia_seq"  value="#{pesquisaPacienteController.seqAtendimentoUrgencia}"/>
		//	    <param name="geraProntuarioVirtual" value=""/>
		return REDIRECIONA_CADASTRO_PACIENTE;
	}
	
	public String redirecionarEdicaoPaciente() {
		parametrosTela.limparParametros();
		parametrosTela.setParametro("aip_pac_codigo", codigoPaciente);
		parametrosTela.setParametro("cameFrom", INTERNACAO);
		parametrosTela.setParametro("goingTo", LEITOS);
		parametrosTela.setParametro("ain_atd_urgencia_seq", this.seqAtendimentoUrgencia);
		
		parametrosTela.setParametro("edicaoInternacao", true);
		cadastrarPacienteController.setEdicaoInternacao(true);

		cadastrarPacienteController.setParametrosTela(parametrosTela);
				
		//cadastrarPacienteController.setPacCodigo(codigoPaciente);
		//cadastrarPacienteController.setGoingTo("internacao-pesquisarDisponibilidadeLeito");
		//cadastrarPacienteController.prepararEdicaoPaciente(pacienteSelecionado, null);
		// TODO: injetar CadastrarPacienteController e
		// setar o codigo do pacienteSelecionado e parametros abaixo na mesma antes de redirecionar
		//	    <param name="aip_pac_codigo" value="#{pesquisaPacienteController.codigoPaciente}"/>
		//	    <param name="aac_exige_prontuario" value=""/>
		//	    <param name="cameFrom" value="Internacao"/>
		//	    <param name="goingTo" value="Leitos"/>
		//	    <param name="ain_leito_id" value=""/>
		//	    <param name="ain_quarto_numero"  value=""/>
		//	    <param name="agh_uni_func_seq"  value=""/>
		//	    <param name="ain_atd_urgencia_seq"  value="#{pesquisaPacienteController.seqAtendimentoUrgencia}"/>
		//	    <param name="geraProntuarioVirtual" value=""/>
		return REDIRECIONA_CADASTRO_PACIENTE;
	}
	
	public Integer getCodigoPaciente() {
		return codigoPaciente;
	}

	public void setCodigoPaciente(Integer codigoPaciente) {
		this.codigoPaciente = codigoPaciente;
	}

	public void setExibirBotaoModalDispLeito(Boolean exibirBotaoModalDispLeito) {
		this.exibirBotaoModalDispLeito = exibirBotaoModalDispLeito;
	}

	public Boolean getExibirBotaoModalDispLeito() {
		return exibirBotaoModalDispLeito;
	}

	public boolean isExibirBotaoIncluir() {
		return exibirBotaoIncluir;
	}

	public void setExibirBotaoIncluir(boolean exibirBotaoIncluir) {
		this.exibirBotaoIncluir = exibirBotaoIncluir;
	}
	
	public boolean isExibirBotaoIncluirPacienteCirurgico() {
		return exibirBotaoIncluirPacienteCirurgico;
	}

	public void setExibirBotaoIncluirPacienteCirurgico(boolean exibirBotaoIncluirPacienteCirurgico) {
		this.exibirBotaoIncluirPacienteCirurgico = exibirBotaoIncluirPacienteCirurgico;
	}

	public void setIdLeito(String idLeito) {
		this.idLeito = idLeito;
	}

	public String getIdLeito() {
		return idLeito;
	}

	public Boolean getModoInternacao() {
		return modoInternacao;
	}

	public void setModoInternacao(Boolean modoInternacao) {
		this.modoInternacao = modoInternacao;
	}

	public void setQuartoNumero(Short quartoNumero) {
		this.quartoNumero = quartoNumero;
	}

	public Short getQuartoNumero() {
		return quartoNumero;
	}

	public void setSeqUnidadeFuncional(Short seqUnidadeFuncional) {
		this.seqUnidadeFuncional = seqUnidadeFuncional;
	}

	public Short getSeqUnidadeFuncional() {
		return seqUnidadeFuncional;
	}

	public void setExibirBotaoModalDesistir(Boolean exibirBotaoModalDesistir) {
		this.exibirBotaoModalDesistir = exibirBotaoModalDesistir;
	}

	public Boolean getExibirBotaoModalDesistir() {
		return exibirBotaoModalDesistir;
	}

	public void setExibirBotaoModalInternar(Boolean exibirBotaoModalInternar) {
		this.exibirBotaoModalInternar = exibirBotaoModalInternar;
	}

	public Boolean getExibirBotaoModalInternar() {
		return exibirBotaoModalInternar;
	}

	public Boolean getExibirBotaoModalInternarEdicao() {
		return exibirBotaoModalInternarEdicao;
	}

	public void setExibirBotaoModalInternarEdicao(
			Boolean exibirBotaoModalInternarEdicao) {
		this.exibirBotaoModalInternarEdicao = exibirBotaoModalInternarEdicao;
	}

	public void setExibirBotaoModalContinuar(Boolean exibirBotaoModalContinuar) {
		this.exibirBotaoModalContinuar = exibirBotaoModalContinuar;
	}

	public Boolean getExibirBotaoModalContinuar() {
		return exibirBotaoModalContinuar;
	}

	public Boolean getExibirBotaoModalInternarAtendimentoUrgencia() {
		return exibirBotaoModalInternarAtendimentoUrgencia;
	}

	public void setExibirBotaoModalInternarAtendimentoUrgencia(
			Boolean exibirBotaoModalInternarAtendimentoUrgencia) {
		this.exibirBotaoModalInternarAtendimentoUrgencia = exibirBotaoModalInternarAtendimentoUrgencia;
	}

	public Boolean getExibirBotaoModalCancelar() {
		return exibirBotaoModalCancelar;
	}

	public void setExibirBotaoModalCancelar(Boolean exibirBotaoModalCancelar) {
		this.exibirBotaoModalCancelar = exibirBotaoModalCancelar;
	}

	public void setSeqAtendimentoUrgencia(Integer seqAtendimentoUrgencia) {
		this.seqAtendimentoUrgencia = seqAtendimentoUrgencia;
	}

	public Integer getSeqAtendimentoUrgencia() {
		return seqAtendimentoUrgencia;
	}

	public void setTituloModal(String tituloModal) {
		this.tituloModal = tituloModal;
	}

	public String getTituloModal() {
		return tituloModal;
	}

	public Boolean getRealizouPesquisaFonetica() {
		return realizouPesquisaFonetica;
	}

	public boolean isExibeBotaoEditar() {
		return exibeBotaoEditar;
	}

	public void setExibeBotaoEditar(boolean exibeBotaoEditar) {
		this.exibeBotaoEditar = exibeBotaoEditar;
	}

	public void setGeraProntuarioVirtual(Boolean geraProntuarioVirtual) {
		this.geraProntuarioVirtual = geraProntuarioVirtual;
	}

	public Boolean getGeraProntuarioVirtual() {
		return geraProntuarioVirtual;
	}

	public DominioListaOrigensAtendimentos getListaOrigensAtendimentos() {
		return listaOrigensAtendimentos;
	}

	public void setListaOrigensAtendimentos(
			DominioListaOrigensAtendimentos listaOrigensAtendimentos) {
		this.listaOrigensAtendimentos = listaOrigensAtendimentos;
	}

	public CadastroInternacaoController getCadastroInternacaoController() {
		return cadastroInternacaoController;
	}


	public void setCadastroInternacaoController(
			CadastroInternacaoController cadastroInternacaoController) {
		this.cadastroInternacaoController = cadastroInternacaoController;
	}


	public Boolean getParamExibeBotaoIncluir() {
		return paramExibeBotaoIncluir;
	}

	public void setParamExibeBotaoIncluir(Boolean paramExibeBotaoIncluir) {
		this.paramExibeBotaoIncluir = paramExibeBotaoIncluir;
	}

	public Short getCnvCodigo() {
		return cnvCodigo;
	}

	public void setCnvCodigo(Short cnvCodigo) {
		this.cnvCodigo = cnvCodigo;
	}

	public Short getCnvCodigoSUS() {
		return cnvCodigoSUS;
	}

	public void setCnvCodigoSUS(Short cnvCodigoSUS) {
		this.cnvCodigoSUS = cnvCodigoSUS;
	}

	public String getNomePacienteCirurgico() {
		return nomePacienteCirurgico;
	}

	public void setNomePacienteCirurgico(String nomePacienteCirurgico) {
		this.nomePacienteCirurgico = nomePacienteCirurgico;
	}

	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getNomeMae() {
		return nomeMae;
	}

	public void setNomeMae(String nomeMae) {
		this.nomeMae = nomeMae;
	}

	public boolean isRespeitarOrdem() {
		return respeitarOrdem;
	}

	public void setRespeitarOrdem(boolean respeitarOrdem) {
		this.respeitarOrdem = respeitarOrdem;
	}

	public Date getDtNascimento() {
		return dtNascimento;
	}

	public void setDtNascimento(Date dtNascimento) {
		this.dtNascimento = dtNascimento;
	}

	public IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}

	public void setPacienteFacade(IPacienteFacade pacienteFacade) {
		this.pacienteFacade = pacienteFacade;
	}

	public boolean isPesquisaFonetica() {
		return pesquisaFonetica;
	}

	public void setPesquisaFonetica(boolean pesquisaFonetica) {
		this.pesquisaFonetica = pesquisaFonetica;
	}

	public AipPacientes getPacienteSelecionado() {
		return pacienteSelecionado;
	}

	public void setPacienteSelecionado(AipPacientes pacienteSelecionado) {
		this.pacienteSelecionado = pacienteSelecionado;
	}

	public DynamicDataModel<AipPacientes> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AipPacientes> dataModel) {
		this.dataModel = dataModel;
	}

	public void setRealizouPesquisaFonetica(Boolean realizouPesquisaFonetica) {
		this.realizouPesquisaFonetica = realizouPesquisaFonetica;
	}
	
	public ParametrosTelaVO getParametrosTela() {
		return parametrosTela;
	}
	
	public void setParametrosTela(ParametrosTelaVO parametrosTela) {
		this.parametrosTela = parametrosTela;
	}

	public Boolean getExecutaPesquisaFonetica() {
		return executaPesquisaFonetica;
	}

	public void setExecutaPesquisaFonetica(Boolean executaPesquisaFonetica) {
		this.executaPesquisaFonetica = executaPesquisaFonetica;
	}
	
	public Integer getCrgSeq() {
		return crgSeq;
	}

	public void setCrgSeq(Integer crgSeq) {
		this.crgSeq = crgSeq;
	}

	public Boolean getExibirBotaoModalOk() {
		return exibirBotaoModalOk;
	}

	public void setExibirBotaoModalOk(Boolean exibirBotaoModalOk) {
		this.exibirBotaoModalOk = exibirBotaoModalOk;
	}
	
	public boolean isObrigarLista() {
		return obrigarLista;
	}

	public void setObrigarLista(boolean obrigarLista) {
		this.obrigarLista = obrigarLista;
	}
	
	/**
	 * Redirecionar para tela de INCLUIR PACIENTE LISTA TRANSPLANTE TMO
	 * @return
	 */
	public String incluirPacienteListaTransplanteTMO(){
		return PAGE_INCLUIR_PACIENTE_LISTA_TRANSPLANTE_TMO;
	}
	public Integer getCodProntuarioFamiliar() {
		return codProntuarioFamiliar;
	}
	
	/**
	 * Redirecionar para tela de INCLUIR PACIENTE LISTA TRANSPLANTE
	 * @return
	 */
	public String incluirPacienteListaTransplante(){
		return PAGE_INCLUIR_PACIENTE_LISTA_TRANSPLANTE;
	}
	
	public void carregarPaciente(AipPacientes paciente){
		this.pacienteSelecionado = paciente;
	}

	public Boolean getIncluirPacienteTMO() {
		return incluirPacienteTMO;
	}

	public void setIncluirPacienteTMO(Boolean incluirPacienteTMO) {
		this.incluirPacienteTMO = incluirPacienteTMO;
	}

	public AipPacientes getDoador() {
		return doador;
	}

	public void setDoador(AipPacientes doador) {
		this.doador = doador;
	}
	public void setCodProntuarioFamiliar(Integer codProntuarioFamiliar) {
		this.codProntuarioFamiliar = codProntuarioFamiliar;
	}

	public String getGoingTo() {
		return goingTo;
	}

	public void setGoingTo(String goingTo) {
		this.goingTo = goingTo;
	}

	public String getNomePacienteAmbulatorio() {
		return nomePacienteAmbulatorio;
	}

	public void setNomePacienteAmbulatorio(String nomePacienteAmbulatorio) {
		this.nomePacienteAmbulatorio = nomePacienteAmbulatorio;
	}

	public Integer getNumeroConsultaAmbulatorio() {
		return numeroConsultaAmbulatorio;
	}

	public void setNumeroConsultaAmbulatorio(Integer numeroConsultaAmbulatorio) {
		this.numeroConsultaAmbulatorio = numeroConsultaAmbulatorio;
	}

	public MptTipoSessao getTipoSessao() {
		return tipoSessao;
	}

	public void setTipoSessao(MptTipoSessao tipoSessao) {
		this.tipoSessao = tipoSessao;
	}

	public Date getPeriodoInicial() {
		return periodoInicial;
	}

	public void setPeriodoInicial(Date periodoInicial) {
		this.periodoInicial = periodoInicial;
	}

	public Date getPeriodoFinal() {
		return periodoFinal;
	}

	public void setPeriodoFinal(Date periodoFinal) {
		this.periodoFinal = periodoFinal;
	}

	public Boolean getCheckAberto() {
		return checkAberto;
	}

	public void setCheckAberto(Boolean checkAberto) {
		this.checkAberto = checkAberto;
	}

	public Boolean getCheckFechado() {
		return checkFechado;
	}

	public void setCheckFechado(Boolean checkFechado) {
		this.checkFechado = checkFechado;
	}

	public Boolean getCheckPrimeiro() {
		return checkPrimeiro;
	}

	public void setCheckPrimeiro(Boolean checkPrimeiro) {
		this.checkPrimeiro = checkPrimeiro;
	}

	public Boolean getCheckSessao() {
		return checkSessao;
	}

	public void setCheckSessao(Boolean checkSessao) {
		this.checkSessao = checkSessao;
	}

	public Integer getTipoFiltroPersonalizado() {
		return tipoFiltroPersonalizado;
	}

	public void setTipoFiltroPersonalizado(Integer tipoFiltroPersonalizado) {
		this.tipoFiltroPersonalizado = tipoFiltroPersonalizado;
	}

	public Integer getCodProntuario() {
		return codProntuario;
	}

	public void setCodProntuario(Integer codProntuario) {
		this.codProntuario = codProntuario;
	}

	public String getNomeSocial() {
		return nomeSocial;
	}

	public void setNomeSocial(String nomeSocial) {
		this.nomeSocial = nomeSocial;
	}	
	
}