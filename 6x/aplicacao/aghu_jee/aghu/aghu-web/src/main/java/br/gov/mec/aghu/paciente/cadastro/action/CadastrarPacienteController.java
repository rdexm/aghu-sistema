package br.gov.mec.aghu.paciente.cadastro.action;

import static br.gov.mec.aghu.model.AipPacientes.VALOR_MAXIMO_PRONTUARIO;

import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.persistence.OptimisticLockException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.context.RequestContext;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.action.ConsultaPacientePaginatorController;
import br.gov.mec.aghu.ambulatorio.action.ListarConsultasPorGradeController;
import br.gov.mec.aghu.ambulatorio.action.PesquisarInterconsultasPaginatorController;
import br.gov.mec.aghu.ambulatorio.action.PesquisarPacientesAgendadosController;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.ambulatorio.vo.ConsultaAmbulatorioVO;
import br.gov.mec.aghu.ambulatorio.vo.ParametrosAghEspecialidadesAtestadoVO;
import br.gov.mec.aghu.ambulatorio.vo.ParametrosAghPerfilProcessoVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.SecurityController;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioTipoEndereco;
import br.gov.mec.aghu.emergencia.action.ListaPacientesEmergenciaPaginatorController;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.internacao.pesquisa.action.DisponibilidadeLeitoPaginatorController;
import br.gov.mec.aghu.internacao.pesquisa.business.IPesquisaInternacaoFacade;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AipCidades;
import br.gov.mec.aghu.model.AipEnderecosPacientes;
import br.gov.mec.aghu.model.AipEtnia;
import br.gov.mec.aghu.model.AipGrupoFamiliarPacientes;
import br.gov.mec.aghu.model.AipLogradouros;
import br.gov.mec.aghu.model.AipNacionalidades;
import br.gov.mec.aghu.model.AipOcupacoes;
import br.gov.mec.aghu.model.AipOrgaosEmissores;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.AipPacientesDadosCns;
import br.gov.mec.aghu.model.AipUfs;
import br.gov.mec.aghu.model.CseProcessos;
import br.gov.mec.aghu.model.MamUnidAtendem;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.VAipCeps;
import br.gov.mec.aghu.paciente.action.RelatorioBoletimIdentificacaoPacienteController;
import br.gov.mec.aghu.paciente.action.RelatorioCartaoSUSController;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.cadastro.business.ICadastroPacienteFacade;
import br.gov.mec.aghu.paciente.cadastrosbasicos.business.ICadastrosBasicosPacienteFacade;
import br.gov.mec.aghu.paciente.historico.action.HistoricoPacienteController;
import br.gov.mec.aghu.paciente.vo.PacienteFiltro;
import br.gov.mec.aghu.paciente.vo.ProfissaoVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.vo.ParametrosTelaVO;

/**
 * Classe responsável por controlar as ações da tela de cadastro e edição de
 * paciente
 * 
 * @author ehgsilva
 * 
 */
@SuppressWarnings({ "PMD.AghuTooManyMethods", "PMD.ExcessiveClassLength" })
public class CadastrarPacienteController extends ActionController {

	private static final String PACIENTE = "Paciente: ";
	private static final String HIFEM = " - ";
	private static final String TELA_EMERGENCIA_LISTA_PACIENTES = "emergencia-listaPacientesEmergencia";
	private static final String EXAMES_ATENDIMENTO_EXTERNO_CRUD = "exames-atendimentoExternoCRUD";
	private static final String INTERNACAO_SOLICITACAO_INTERNACAO = "internacao-solicitacaoInternacao";
	private static final long serialVersionUID = -5561090491427963243L;

	private enum CadastrarPacienteControllerExceptionCode implements BusinessExceptionCode {
		MESSAGEM_ERRO_DADOS_CNS, ERRO_CADASTRO_PACIENTE_DADOS_DUPLICIDADE
	};
	
	private static final String  CADASTRO_PACIENTE = "paciente-cadastroPaciente";
	private static final String REDIRECT_PESQUISA_PACIENTE = "pesquisaPaciente";
	private static final String REDIRECT_HISTORICO_PACIENTE = "paciente-historicoPaciente";
	private static final String REDIRECT_DADOS_ADICIONAIS_PACIENTE = "paciente-dadosAdicionaisPaciente";
	private static final String REDIRECT_CONVENIOS_PACIENTE = "paciente-conveniosPaciente";
	private static final String REDIRECT_RELATORIO_BOLETIM_IDENTIFICACAO_CADASTRO_PACIENTE_PDF = "paciente-relatorioBoletimIdentificacaoCadastroPacientePdf";
	private static final String REDIRECT_RELATORIO_CARTAO_SUS_PDF = "paciente-relatorioCartaoSUSPdf";
	private static final String REDIRECT_CADASTRO_INTERNACAO = "internacao-cadastroInternacao";
	private static final String REDIRECT_PESQUISAR_DISPONIBILIDADE_LEITO = "internacao-pesquisarDisponibilidadeLeito";
	private static final String REDIRECT_LISTAR_TRANSPLANTES = "transplante-listarTransplante";
	private static final String INTERNACAO = "Internacao";
	private static final String LEITOS = "Leitos";
	private static final String PAGE_PESQUISAR_INTERCONSULTAS = "ambulatorio-gestaoInterconsultas";
	private static final String PAGE_PESQUISAR_PACIENTES_AGENDADOS = "ambulatorio-pesquisarPacientesAgendados";
	private static final String REGISTRO_CIRURGIA_REALIZADA = "blococirurgico-registroCirurgiaRealizada";
	private static final String PESQUISAR_CENSO_DIARIO_PACIENTES = "internacao-pesquisarCensoDiarioPacientes";
	private static final String AGENDAMENTO_PROCEDIMENTO = "agendamentoProcedimento";
	private static final String PESQUISA_CONSULTAS_PACIENTE = "pesquisaConsultasPaciente";
	private static final String PESQUISA_FONETICA = "paciente-pesquisaPacienteComponente";
	private static final String LIBERAR_CONSULTAS_LIST = "liberarConsultasList";
	private static final String ATUALIZAR_CONSULTA = "atualizarConsulta";
	private static final String LISTAR_CIRURGIAS = "blococirurgico-listaCirurgias";
	private static final String AMBULATORIO_ATUALIZAR_CONSULTA = "ambulatorio-atualizarConsulta";
	private static final String PESQUISA_PACIENTE_COMPONENTE = "pesquisaPacienteComponente";
	private static final String LISTAR_CONSULTAS_POR_GRADE = "ambulatorio-listarConsultasPorGrade";
	private static final String PESQUISA_PACIENTE = "paciente-pesquisaPaciente";
	private static final String LISTA_PACIENTE_EMERGENCIA = "emergencia-listaPacientesEmergencia";
	private static final String ATENDIMENTO_AMBULATORIAL_ABA_AGENDADOS = "ambulatorio-pesquisarPacientesAgendados";
	private static final String PAGE_AGRUPAMENTO_FAMILIAR = "ambulatorio-agrupamentoFamiliar";
	private static final String REDIRECT_LISTAR_TRANSPLANTES_ORGAO = "transplante-listarTransplanteOrgao";
	private static final String REDIRECT_PESQUISA_FONETICA_AMBULATORIO ="ambulatorio-pesquisaConsultasPaciente";
	private static final String AMBULATORIO_PESQUISAR_CONSULTAS_GRADE = "ambulatorio-pesquisarConsultasGrade";
	private static final String PAGE_PACIENTES_LISTAS_TMO ="transplante-pacientesListaTMO" ; //50184
	private static final String PAGE_PACIENTES_LISTAS_ORGAOS ="transplante-pacientesListaOrgao" ; //50186

	@EJB
	private IPacienteFacade pacienteFacade;

	@EJB
	private ICadastroPacienteFacade cadastroPacienteFacade;

	@EJB
	private ICadastrosBasicosPacienteFacade cadastrosBasicosPacienteFacade;

	@EJB
	private IParametroFacade parametroFacade;

	@EJB
	private IAmbulatorioFacade ambulatorioFacade;

	@EJB
	private IPesquisaInternacaoFacade pesquisaInternacaoFacade;

	@Inject
	private EnderecoController enderecoController;

	@Inject
	private RelatorioCartaoSUSController relatorioCartaoSUSController;

	@Inject
	private RelatorioBoletimIdentificacaoPacienteController relatorioBoletimIdentificacaoPacienteController;
	
	@Inject
	private ListarConsultasPorGradeController listarConsultasPorGradeController;

	@Inject
	private DisponibilidadeLeitoPaginatorController disponibilidadeLeitoPaginatorController;

	@Inject
	private HistoricoPacienteController historicoPacienteController;

	@Inject
	private ConveniosPacienteController conveniosPacienteController;

	@Inject
	private DadosAdicionaisPacienteController dadosAdicionaisPacienteController;

	@Inject
	private SecurityController securityController;

	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@Inject
	private PesquisarInterconsultasPaginatorController pesquisarInterconsultasPaginatorController;
	
	@Inject
	private ConsultaPacientePaginatorController consultaPacientePaginatorController;

	private Integer numeroConsulta;

	/**
	 * Flag que indica se exige prontuário, obtida via page parameter.
	 */
	private DominioSimNao aacExigeProntuario;

	/**
	 * Nome sugerido de uma pessoa a qual foi procurada na pesquisa de pacientes
	 */
	// TODO SETAR VIA CONTROLLER DE PESQUISA.
	private String nomePesquisar = "";
	private String cameFrom;
	/*
	 * Foi criado um segundo cameFrom para atender #29403, que precisa ter
	 * exatamente o mesmo comportamento da internação.
	 */
	private String cameFromCirurgias;
	private String goingTo;
	private String idLeito;
	private Short quartoNumero;
	private Short seqUnidadeFuncional;
	private Integer seqAtendimentoUrgencia;
	private Long numeroMaximoProntuarioManual;
	
	private String nomePaciente;
	
	/**
	 * 
	 * 
	 * Paciente sendo editado ou incluído.
	 */
	// TODO setar onde for preciso
	private AipPacientes aipPaciente;

	/**
	 * Prontuário da mãe do paciente
	 */
	private Integer prontuarioMae = null;

	/**
	 * Cartão SUS do Paciente sendo editado ou incluído.
	 */
	private AipPacientesDadosCns aipPacienteDadosCns;

	/**
	 * Descrição do tipo de prontuário.
	 */
	private String descricaoTipoProntuario;

	/**
	 * Lista de enderecos do paciente.
	 */
	private List<AipEnderecosPacientes> enderecosPaciente = new ArrayList<AipEnderecosPacientes>();
	private List<AipEnderecosPacientes> enderecosRemovidos = new ArrayList<AipEnderecosPacientes>();
	private List<AipEnderecosPacientes> enderecosAdicionados = new ArrayList<AipEnderecosPacientes>();
	private List<AipEtnia> etniasPaciente;
	
	/**
	 * Endereco sendo incluído ou editado.
	 */
	private AipEnderecosPacientes endereco = new AipEnderecosPacientes();;
	
	/**
	 * Variável de controlhe relativa ao habilitação dos campos do cadastro de
	 * endereço.
	 */
	private boolean camposEnderecoAtivado = false;

	/**
	 * Variável de controlhe da modal de alerta para Nacionalidade "EIRE".
	 */
	private boolean mostrarModalNacionalidade = false;

	/**
	 * Variáveis de controle dos objetos Nacionalidade e Ocupação
	 */
	private AipNacionalidades nacionalidadeTemp = null;

	/**
	 * Orgão Emissor sendo editado ou incluído.
	 */
	private AipOrgaosEmissores aipOrgaoEmissorSelecionado;

	private ProfissaoVO profissaoVO;

	/**
	 * Variável utilizada para permitir a passagem do código do paciente por
	 * parâmetro para outra tela
	 * 
	 */
	private Integer pacCodigo = null;

	/**
	 * Edição dos dados do paciente
	 * 
	 */
	private ConsultaAmbulatorioVO consultaSelecionada;

	
	/**
	 * Variável que define se o prontuário será virtual
	 */
	private Boolean geraProntuarioVirtual = false;
	private boolean pacienteTemGMR;
	private boolean validarTrocaMunicipio;
	private boolean ignorarModais = false;
	private boolean retornoEnderecoPaciente;
	private String complementoEndereco;
	private boolean habilitaAdicionarEndereco = false;
	private ParametrosTelaVO parametrosTela;
	private Boolean edicaoInternacao;
	private Boolean edicaoEnderecos;
	private Boolean isPrepararInclusaoPaciente;
	private Integer pacienteSelecionado;
	private Boolean isPrepararEdicaoPaciente;
	private Integer cid;
	private Boolean isGeraProntuarioDesabilitado;
	private boolean exibirDadosCompRegNascimento = false;
	
	private boolean trocarPacienteConsulta = false;
	
	private Boolean orderListaPacientesAsc;
	
	private String orderPropertyListaPacientes;
	
	private Integer maxResultsListaPacientes;
	
	private static final Log LOG = LogFactory.getLog(CadastrarPacienteController.class);

	@Inject
	PesquisarPacientesAgendadosController pesquisarPacientesAgendadosController;
	
	@Inject
	private ListaPacientesEmergenciaPaginatorController listaPacientesEmergenciaPaginatorController;

	// Parametros para uso do AGHWEB
	private String banco = null;
	private String urlBaseWebForms = null;
	private Boolean isHcpa = false;
	private Boolean isUbs = false;
	private Integer pConNumero;
	private Integer vAtdSeq = 0;
	private String vDthrMvto;
	private Short vEspSeq = 0;
	private Short vEspPai = 0;
	private String pUso;

	private AipGrupoFamiliarPacientes grupoFamiliar;
	
	List<AghAtendimentos> aghAtendimentosList = new ArrayList<AghAtendimentos>();
	
	List<ParametrosAghEspecialidadesAtestadoVO> parametrosAghEspecialidadesAtestadoVOList = new ArrayList<ParametrosAghEspecialidadesAtestadoVO>();
	List<ParametrosAghPerfilProcessoVO> parametrosAghPerfilProcessoVOList = new ArrayList<ParametrosAghPerfilProcessoVO>();
	List<CseProcessos> cseProcessosList = new ArrayList<CseProcessos>();
	
	/**
	 * Campos de obrigatoriedade dos fields na tela; 
	 */
	private Boolean gerarProntuario;
	
	private Boolean nomePaiRequired;
	private Boolean grauInstrucaoRequired;
	private Boolean corRequired;
	private Boolean dddResidencialRequired;
	private Boolean telefoneResidencialPaiRequired;
	
	final static String REDIRECT_LISTA_EMERGENCIA = "emergencia-listaPacientesEmergencia";
	private MamUnidAtendem mamUnidAtendem;
	private PacienteFiltro filtroListaEmergencia;
	
	private String voltarPara;
	
	@PostConstruct
	public void init() {
		begin(conversation);
		this.parametrosTela = new ParametrosTelaVO();
		edicaoEnderecos = false;		
		
		if(getRequestParameter("param_cid") != null){		
			setCid(Integer.valueOf(getRequestParameter("param_cid")));
		}
		
		if(getRequestParameter("cameFrom") != null){		
			this.cameFrom = getRequestParameter("cameFrom");
		}
		
		if(getRequestParameter("nomePaciente") != null){		
			if (aipPaciente == null) {
				this.aipPaciente = new AipPacientes();
				this.aipPaciente.setNome(getRequestParameter("nomePaciente"));
		}
		}
		
		setIsPrepararInclusaoPaciente(getRequestParameter("prepararIncluirPaciente") != null  && getRequestParameter("prepararIncluirPaciente").equalsIgnoreCase("TRUE"));
		setIsGeraProntuarioDesabilitado(getRequestParameter("geraProntuarioDesabilitado") != null  && getRequestParameter("geraProntuarioDesabilitado").equalsIgnoreCase("TRUE"));
		setisPrepararEdicaoPaciente(getRequestParameter("prepararEdicaoPaciente") != null  && getRequestParameter("prepararEdicaoPaciente").equalsIgnoreCase("TRUE"));
		if (getRequestParameter("pacienteSelecionado") != null){
		   setPacienteSelecionado(Integer.valueOf(getRequestParameter("pacienteSelecionado")));
		}
		
		gerarProntuario = securityController.usuarioTemPermissao("paciente", "gerarProntuario");
		
		this.isHcpa = this.aghuFacade.isHCPA();

		this.popularParametrosAghWeb();		
	}
	
	public boolean isProntuarioFamiliaVisivel() {

		String nomeMicrocomputador;
		try {
			nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
		} catch (UnknownHostException e) {
			LOG.error(e.getMessage(), e);
			return false;
		}
		return cadastroPacienteFacade.verificarProntuarioFamiliaVisivel(nomeMicrocomputador);
	}

	private void recuperarParametrosTela() {
		this.aipPaciente = this.pacienteFacade.obterAipPacientesPorChavePrimaria(this.parametrosTela.getParametro("aip_pac_codigo", Integer.class));
		this.aacExigeProntuario = this.parametrosTela.getParametro("aac_exige_prontuario", DominioSimNao.class);
		this.cameFrom = this.parametrosTela.getParametro("cameFrom", String.class);
		this.goingTo = this.parametrosTela.getParametro("goingTo", String.class);
		this.idLeito = this.parametrosTela.getParametro("ain_leito_id", String.class);
		this.quartoNumero = this.parametrosTela.getParametro("ain_quarto_numero", Short.class);
		this.seqUnidadeFuncional = this.parametrosTela.getParametro("agh_uni_func_seq", Short.class);
		this.seqAtendimentoUrgencia = this.parametrosTela.getParametro("ain_atd_urgencia_seq", Integer.class);
		this.geraProntuarioVirtual = this.parametrosTela.getParametro("geraProntuarioVirtual", Boolean.class);

		// this.parametrosTela.limparParametros();
	}

	public void iniciar() {
		
		if (this.getIsPrepararInclusaoPaciente()){
			
			if (filtroListaEmergencia != null && filtroListaEmergencia.getNome() != null) {
				if (aipPaciente == null) {
					this.aipPaciente = new AipPacientes();
					this.aipPaciente.setNome(filtroListaEmergencia.getNome());
				}
				nomePesquisar = filtroListaEmergencia.getNome();
			}
			
//			if (getRequestParameter("nomePesquisar") != null && cameFrom.equals("emergencia-listarPacientesEmergencia")) {
//				nomePesquisar = getRequestParameter("nomePesquisar");
//			}
			
			prepararInclusaoPaciente(this.nomePesquisar, null, DominioSimNao.S);
			setIsPrepararInclusaoPaciente(false);
		} else if (this.getisPrepararEdicaoPaciente() && this.getPacienteSelecionado() != null) {
			this.aipPaciente = this.pacienteFacade.obterAipPacientesPorChavePrimaria(this.getPacienteSelecionado());
			this.aacExigeProntuario = null;
			this.prepararEdicaoPaciente(this.aipPaciente, this.aacExigeProntuario);		
			setisPrepararEdicaoPaciente(false);
		}
		
		if (Boolean.TRUE.equals(this.edicaoInternacao)) {
			this.recuperarParametrosTela();
			this.prepararEdicaoPaciente(this.aipPaciente, this.aacExigeProntuario);
		} else if (PAGE_PESQUISAR_PACIENTES_AGENDADOS.equals(this.cameFrom) && !trocarPacienteConsulta) {
			this.aipPaciente = this.pacienteFacade.obterPaciente(this.pacCodigo);
			this.prepararEdicaoPaciente(this.aipPaciente, this.aacExigeProntuario);

			List<String> msgs = ambulatorioFacade.validaDadosPaciente(this.pacCodigo);
			if (msgs != null && !msgs.isEmpty()) {
				for (String bundle : msgs) {
					apresentarMsgNegocio(Severity.WARN, bundle);
				}
			}
		}
		
		if(this.aipPaciente != null){
			grupoFamiliar = ambulatorioFacade.obterProntuarioFamiliaPaciente(this.aipPaciente.getCodigo());
		}
		
		this.limparEndereco();
		
		edicaoEnderecos = false;
		validarObrigatoriedadeDosCampos();
	}

	/**
	 * Método usado para lançar o endereço a ser editado no contexto.
	 * (prepararEdicaoEndereco)
	 * 
	 * @param endereco
	 */
	public void prepararEdicaoEndereco(AipEnderecosPacientes endereco) {
		if (this.endereco != null){
			this.endereco.setEmEdicao(false);
		}
		this.endereco = endereco;
		this.enderecoController.setEndereco(endereco);
		try {
			this.enderecoController.inicio();
		} catch (Exception e) {
			this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ERRO_INICIAR_ENDERECO");
		}
		this.enderecoController.setEdicao(true);
		endereco.setEmEdicao(true);
		habilitaAdicionarEndereco = false;
	}
	

	/**
	 * 
	 * @param endereco
	 */
	public void removerEndereco(AipEnderecosPacientes endereco) {

		this.aipPaciente.getEnderecos().remove(endereco);

		if (endereco.getId().getSeqp() != null) {
			this.enderecosRemovidos.add(endereco);
		} else {
			this.enderecosAdicionados.remove(endereco);
		}
		this.enderecosPaciente.addAll(this.enderecosAdicionados);

	}
	
	public void confirmarEndereco() {
		
		if (!validarCamposEndereco(this.enderecoController.getEnderecoEdicao())){
			return;
		}
		
		this.enderecoController.getEndereco().setAipPaciente(this.aipPaciente);

		boolean salvou = false;
		if(this.enderecoController.getCepCadastrado() != null && !this.enderecoController.renderizaEndereco() &&
				this.enderecoController.getCepCadastrado().getAipBairrosCepLogradouro() != null){
			salvou = this.enderecoController.salvarEnderecoCadastrado();
		}else {
			salvou = this.enderecoController.salvarEnderecoNaoCadastrado();
		}

		if(salvou){
			this.endereco = this.enderecoController.getEndereco();

			if (this.aipPaciente.getEnderecos().contains(this.endereco)) {
				this.enderecosAdicionados.remove(this.endereco);
			}
			endereco.setEmEdicao(false);
			aipPaciente.getEnderecos().add(endereco);
			
			this.enderecosAdicionados.add(this.endereco);
			this.enderecosRemovidos.remove(this.endereco);
			this.enderecosPaciente.clear();
			this.enderecosPaciente.addAll(enderecosAdicionados);
			this.enderecoController.setHabilitaCamposLogradouroLivre(false);
			limparEnderecoContexto();
			if(this.enderecoController.getEdicao()){
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_ENDERECO_ALTERADO_SUCESSO");
			}else {
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_ENDERECO_ADICIONADO_SUCESSO");
			}
			this.enderecoController.setEdicao(false);
			habilitaAdicionarEndereco = false;
			enderecoController.setEnderecoNaoLocalizado(false);
		}
	}
	
	private boolean validarCamposEndereco(AipEnderecosPacientes enderecoPaciente) {
		boolean retorno = true;
		VAipCeps cepCadastrado = this.enderecoController.getCepCadastrado();
		AipCidades cidadeCadastrada = this.enderecoController.getCidadeEnderecoCadastrado();
		retorno = validaCepLogradouro(enderecoPaciente, retorno, cepCadastrado);
		retorno = validaNroLogradouro(enderecoPaciente, retorno);		
		retorno = validaBairro(enderecoPaciente, retorno, cepCadastrado);
		retorno = validaTipoEndereco(enderecoPaciente, retorno);
		retorno = validaCidade(enderecoPaciente, retorno, cepCadastrado,cidadeCadastrada);
		retorno = validaCep(enderecoPaciente, retorno, cepCadastrado);
		return retorno;
	}

	private boolean validaCep(AipEnderecosPacientes enderecoPaciente,
			boolean retorno, VAipCeps cepCadastrado) {
		if(cepCadastrado == null && !enderecoController.renderizaEndereco() || enderecoPaciente.getCep() == null && enderecoController.renderizaEndereco()){
			this.apresentarMsgNegocio(Severity.ERROR,"CEP_OBRIGATORIO");
			retorno = false;
		}
		return retorno;
	}

	private boolean validaCidade(AipEnderecosPacientes enderecoPaciente,
			boolean retorno, VAipCeps cepCadastrado, AipCidades cidadeCadastrada) {
		if ((cepCadastrado == null || (cepCadastrado != null && cepCadastrado.getAipBairrosCepLogradouro() == null))
				&& enderecoPaciente.getAipCidade() == null
				&& StringUtils.isBlank(enderecoPaciente.getCidade()) && cidadeCadastrada == null) {
			this.apresentarMsgNegocio(Severity.ERROR,"CIDADE_OBRIGATORIO");
			retorno = false;
		}
		return retorno;
	}

	private boolean validaTipoEndereco(AipEnderecosPacientes enderecoPaciente,
			boolean retorno) {
		if (enderecoPaciente.getTipoEndereco() == null) {
			this.apresentarMsgNegocio(Severity.ERROR,"TIPO_ENDERECO_OBRIGATORIO");
			retorno = false;
		}
		return retorno;
	}

	private boolean validaNroLogradouro(AipEnderecosPacientes enderecoPaciente,
			boolean retorno) {
		if (enderecoPaciente.getNroLogradouro() == null) {
			this.apresentarMsgNegocio(Severity.ERROR, "NUMERO_LOGRADOURO_OBRIGATORIO");
			retorno = false;
		}
		return retorno;
	}

	private boolean validaCepLogradouro(AipEnderecosPacientes enderecoPaciente,
			boolean retorno, VAipCeps cepCadastrado) {
		if ((cepCadastrado == null || (cepCadastrado != null && cepCadastrado.getAipBairrosCepLogradouro() == null))
				&& enderecoPaciente.getAipLogradouro() == null && StringUtils.isBlank(enderecoPaciente.getLogradouro())) {
			this.apresentarMsgNegocio(Severity.ERROR, "LOGRADOURO_OBRIGATORIO");
			retorno = false;
		}
		return retorno;
	}

	private boolean validaBairro(AipEnderecosPacientes enderecoPaciente,
			boolean retorno, VAipCeps cepCadastrado) {
		if (enderecoPaciente.getBairro() == null && cepCadastrado == null) {
			this.apresentarMsgNegocio(Severity.ERROR, "BAIRRO_OBRIGATORIO");
			retorno = false;
		}
		return retorno;
	}
	
	public boolean pesquisaNaturalidadeOrdemAlfabetica() {
		boolean retorno = false;
		
		try {
			AghParametros aghParamRN = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_PESQUISA_NATURALIDADE_EM_ORDEM_ALFABETICA);
			String paramObrigaRN = aghParamRN.getVlrTexto();
			return "S".equalsIgnoreCase(paramObrigaRN);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		
		return retorno;
	}
	
	public void limparEndereco(){
		this.enderecoController.limparEndereco();
		this.enderecoController.setEdicao(false);
		this.enderecoController.setHabilitaCamposLogradouroLivre(false);
		this.endereco.setEmEdicao(false);
		this.habilitaAdicionarEndereco = false;
		limparEnderecoContexto();
	}
	
	public void limparEnderecoContexto() {
		AipEnderecosPacientes enderecoPaciente = new AipEnderecosPacientes();
		enderecoPaciente.setPadrao(true);
		enderecoPaciente.setTipoEndereco(DominioTipoEndereco.R);
		enderecoPaciente.setAipPaciente(this.aipPaciente);
		this.enderecoController.setEndereco(enderecoPaciente);
		
		try {
			enderecoController.inicio();
		} catch (Exception e) {
			this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ERRO_INICIAR_ENDERECO");
		}
	}
	
	public String verificarPacienteVip() {
		if (aipPaciente.getIndPacienteVip() != null) {
			if (aipPaciente.getIndPacienteVip().equals(DominioSimNao.S)) {
				return "VIP";
			}
		}
		return null;
	}

	public List<AipEtnia> buscarEtnias() {
		return pacienteFacade.obterTodasEtnias();
	}

	/**
	 * Método utilizado para ativar os campos do cadastro de endereco.
	 */
	public void ativarCamposCadastroEndereco() {
		this.camposEnderecoAtivado = true;
	}

	/**
	 * Método utilizado para verificar se a naiconalidade escolhida é a "EIRE".
	 */
	public void selecionouNacionalidade() {
		if (this.aipPaciente.getAipNacionalidades() != null && this.aipPaciente.getAipNacionalidades().getDescricao().equalsIgnoreCase("EIRE")) {
			mostrarModalNacionalidade = true;
		}
	}

	/**
	 * Método utilizado para associar um paciente já cadastrado como mãe do
	 * paciente sendo cadastrado, através do número de prontuário, e sugerir o
	 * seu nome no campo nome da mãe.
	 */
	public void atribuirMaePacientePorProntuario() throws ApplicationBusinessException {
		if (prontuarioMae == null) {
			prontuarioMae = null;
			aipPaciente.setMaePaciente(null);

		} else {
			AipPacientes paciente = pacienteFacade.obterPacientePorCodigoOuProntuario(prontuarioMae, null, null);

			if (paciente == null) {
				this.apresentarMsgNegocio(Severity.WARN, "MESSAGEM_PRONTUARIO_INVALIDO", CoreUtil.formataProntuario(prontuarioMae));
				aipPaciente.setMaePaciente(null);
				this.prontuarioMae = null;
				return;

			}

			if (aipPaciente.getCodigo() != null && aipPaciente.getCodigo().equals(paciente.getCodigo())) {
				this.apresentarMsgNegocio(Severity.WARN, "MESSAGEM_PRONTUARIO_MESMO_PACIENTE", CoreUtil.formataProntuario(prontuarioMae));
				aipPaciente.setMaePaciente(null);
				this.prontuarioMae = null;
				return;

			}

			if (paciente.getSexo().equals(DominioSexo.M)) {
				this.apresentarMsgNegocio(Severity.WARN, "MESSAGEM_PRONTUARIO_MASCULINO", CoreUtil.formataProntuario(prontuarioMae));
				aipPaciente.setMaePaciente(null);
				this.prontuarioMae = null;
				return;
			}
			aipPaciente.setMaePaciente(paciente);
			aipPaciente.setNomeMae(paciente.getNome());

		}
	}

	/**
	 * Método utilizado para confirmar a nacionalidade "EIRE" e não mostrar mais
	 * a modal.
	 */
	public void confirmarNacionalidadeEire() {
		mostrarModalNacionalidade = false;
	}

	public List<AipNacionalidades> pesquisarNacionalidades(String objParam) {

		String strPesquisa = (String) objParam;
		return returnSGWithCount(cadastrosBasicosPacienteFacade.pesquisarPorCodigoSiglaNome(strPesquisa), pesquisarNacionalidadesCount(objParam));
	}

	public Long pesquisarNacionalidadesCount(String objParam) {
		String strPesquisa = (String) objParam;
		return cadastrosBasicosPacienteFacade.pesquisarCountPorCodigoSiglaNome(strPesquisa);
	}

	/**
	 * Método que verifica se deve ser permitido gerar prontuário
	 */
	public boolean permitirGerarProntuario() {
		boolean retorno = false;
		if (aipPaciente != null && (aipPaciente.getProntuario() == null || aipPaciente.getProntuario() > VALOR_MAXIMO_PRONTUARIO)) {
			retorno = true;
		}
		return retorno;
	}
	
	/**
	 * Método que obriga informar o número do cartão SUS no cadastro do paciente
	 * @throws AGHUNegocioException 
	 */
	public boolean requererNumeroCartaoSUS() {
		boolean retorno = false;
		
		try {
			AghParametros aghParamFrn = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_OBRIGATORIEDADE_CARTAO_SUS);
			String paramCartaoSUS = aghParamFrn.getVlrTexto();
			retorno = "S".equalsIgnoreCase(paramCartaoSUS);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		
		return retorno;
	}
	
	public boolean obrigaPreenchimentoRN() {
		boolean retorno = false;
		
		try {
			AghParametros aghParamRN = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_PREENCHIMENTO_REGISTRO_DE_NASCIMENTO_NAO_OBRIGATORIO);
			String paramObrigaRN = aghParamRN.getVlrTexto();
			return "N".equalsIgnoreCase(paramObrigaRN);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		
		return retorno;
	}
	
	private boolean isConfirmaImpressao(){
		if (aipPaciente.isGerarProntuario() && aipPaciente.getProntuario() == null) {
			return true;	
		}
		return false;
	}

	/**
	 * Método que realiza a ação do botão confirmar na tela de cadastro de
	 * paciente
	 */
	public String confirmar() {

		boolean erro = false;
		this.setIgnorarModais(true);
		Boolean situacaoAnteriorProntuarioManual = aipPaciente.getInsereProntuario();
		
		boolean confirmaImpressao = this.isConfirmaImpressao();		
		
		try {
			if (cadastrarPaciente()) {
				return null;
			}
			aipPaciente.setInsereProntuario(false);
		} catch (BaseException e) {
			erro = true;
			apresentarExcecaoNegocio(e);
		} catch (OptimisticLockException e) {
			LOG.error(e.getMessage(), e);
			erro = true;
			throw e;
		}

		if (aipPaciente.getPrntAtivo() != null) {
			descricaoTipoProntuario = aipPaciente.getPrntAtivo().getDescricao();
		}
		
		if (!erro) {
			carregarDadosPacienteCns();
		}

		carregarDadosMaePaciente();

		this.setPacienteTemGMR(false);

		if (!erro) {			
			if (confirmaImpressao) {
				confirmaImpressao = false;
				super.openDialog("modalConfirmacaoImpressaoWG");
				return verificarRetornoPaginaSolicitante();
			}
			
			if(isConsultaSelecionada()){
						pesquisarPacientesAgendadosController.setSelectedTab(0);
						return ATENDIMENTO_AMBULATORIAL_ABA_AGENDADOS;
			}
			this.setIgnorarModais(false);
			
			this.setPacienteTemGMR(false);
			this.setValidarTrocaMunicipio(false);

			if(goingTo != null && goingTo.isEmpty()){
				goingTo = null;
			}
			
			if(goingTo != null){
				return this.carregaNavegcao();
			} else {
				prepararEdicaoPaciente(aipPaciente, null);
			}
			
			if (LISTA_PACIENTE_EMERGENCIA.equalsIgnoreCase(cameFrom)) {
				listaPacientesEmergenciaPaginatorController.pesquisar();
				return LISTA_PACIENTE_EMERGENCIA;
			}
			
			return goingTo;
		} else {
			aipPaciente.setInsereProntuario(situacaoAnteriorProntuarioManual);
			return null;
		}
	}

	private String verificarRetornoPaginaSolicitante() {
		if (AMBULATORIO_PESQUISAR_CONSULTAS_GRADE.equalsIgnoreCase(cameFrom)) {
			if (aipPaciente.getProntuario() != null) {
				pacienteFacade.atualizarPacienteConsulta(aipPaciente, numeroConsulta);
			}
			return AMBULATORIO_PESQUISAR_CONSULTAS_GRADE;
		} else if ("agendamentoProcedimento".equalsIgnoreCase(cameFrom)) {
			return null;	
		} else if(PAGE_PESQUISAR_PACIENTES_AGENDADOS.equalsIgnoreCase(cameFrom)) {
			return PAGE_PESQUISAR_PACIENTES_AGENDADOS;
		} else {	
			return null;
		}
	}

	private void validarObrigatoriedadeDosCampos() {
		try {
			getParametrosSistema();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	private void getParametrosSistema() throws ApplicationBusinessException {
		setNomePaiRequired(getParametroRequired(AghuParametrosEnum.P_CADASTRO_PACIENTES_NOME_PAI_OBRIGATORIO));
		setGrauInstrucaoRequired(getParametroRequired(AghuParametrosEnum.P_CADASTRO_PACIENTES_GRAU_INSTRUCAO_OBRIGATORIO));
		setCorRequired(getParametroRequired(AghuParametrosEnum.P_CADASTRO_PACIENTES_COR_OBRIGATORIO));
		setDddResidencialRequired(getParametroSimpleRequired(AghuParametrosEnum.P_CADASTRO_PACIENTES_DDD_RESIDENCIAL_OBRIGATORIO));
		setTelefoneResidencialPaiRequired(getParametroSimpleRequired(AghuParametrosEnum.P_CADASTRO_PACIENTES_TELEFONE_RESIDENCIAL_OBRIGATORIO));
	}
	
	/**
	 * O fluxo que segue apartir deste método respeita a regra previamente existente nesta tela, se o parâmetro não existir.
	 * @param parametro
	 * @return
	 * @throws AGHUNegocioException
	 */
	private Boolean getParametroRequired(AghuParametrosEnum parametro) throws ApplicationBusinessException {
		if(parametroFacade.verificarExisteAghParametro(parametro)){
			return getParameterValue(parametro);
		}else{
			return gerarProntuario;
		}
	}

	private Boolean getParameterValue(AghuParametrosEnum parametro)	throws ApplicationBusinessException {
		if("S".equalsIgnoreCase(parametroFacade.buscarAghParametro(parametro).getVlrTexto())){
			return Boolean.TRUE;
		}else{
			return gerarProntuario;
		}
	}
	
	/**
	 * O fluxo que segue apartir deste método simplismente retorna um Boolean de acordo com o valor do parâmetro.
	 * @param parametro
	 * @return
	 * @throws AGHUNegocioException
	 */
	private Boolean getParametroSimpleRequired(AghuParametrosEnum parametro) throws ApplicationBusinessException {
		if(parametroFacade.verificarExisteAghParametro(parametro)){
			return getParameterSimpleValue(parametro);
		}else{
			return Boolean.FALSE;
		}
	}

	private Boolean getParameterSimpleValue(AghuParametrosEnum parametro) throws ApplicationBusinessException {
		if("S".equalsIgnoreCase(parametroFacade.buscarAghParametro(parametro).getVlrTexto())){
			return Boolean.TRUE;
		}else{
			return Boolean.FALSE;
		}
	}
	
	private String carregaNavegcao(){
		if (INTERNACAO.equalsIgnoreCase(this.goingTo)) {
			return REDIRECT_CADASTRO_INTERNACAO; // Grava o paciente e
													// envia para tela
													// de internação
			
		} else if (LEITOS.equalsIgnoreCase(this.goingTo)) {
			if (LISTAR_CIRURGIAS.equalsIgnoreCase(this.cameFromCirurgias)){
				disponibilidadeLeitoPaginatorController.setCameFrom(this.cameFromCirurgias);
			}
			return REDIRECT_PESQUISAR_DISPONIBILIDADE_LEITO; // Pesquisa
																// disponibilidade
																// leitos
		} else if (LISTAR_CONSULTAS_POR_GRADE.equalsIgnoreCase(goingTo)) {
			listarConsultasPorGradeController.setPacCodigoFonetica(pacCodigo);
			listarConsultasPorGradeController.obterInformacoesPaciente(true);
			return LISTAR_CONSULTAS_POR_GRADE;
		}
		return verificarRetornoPaginaSolicitante();
	}

	private void carregarDadosMaePaciente() {
		if (aipPaciente.getMaePaciente() != null) {
			prontuarioMae = aipPaciente.getMaePaciente().getProntuario();
		} else {
			prontuarioMae = null;
		}
	}

	private void carregarDadosPacienteCns() {
		if (aipPaciente.getAipPacientesDadosCns() != null) {
			this.aipPacienteDadosCns = pacienteFacade.obterDadosCNSPaciente(aipPaciente.getCodigo());
		} else {
			this.aipPacienteDadosCns = new AipPacientesDadosCns();
		}
	}

	private boolean isConsultaSelecionada() {
		return consultaSelecionada != null && consultaSelecionada.getGradeSeq() != null;
	}
	
	public boolean validaTelefone(boolean error){
		
		error = false;
		
		/*checagens para validar entrada de telefones. O número do telefone e o
		 * DDD devem sempre ser informados juntos (se houver um, o outro tb deve ser
		 * informado.)
		 */
		if ((aipPaciente.getDddFoneResidencial() == null && aipPaciente.getFoneResidencial() != null)
				|| (aipPaciente.getDddFoneResidencial() != null && aipPaciente.getFoneResidencial() == null)) {
			this.apresentarMsgNegocio(Severity.ERROR, "ERRO_INFORMAR_TELEFONE");
			error = true;
		}

		if ((aipPaciente.getDddFoneRecado() != null && StringUtils.isBlank(aipPaciente.getFoneRecado()))
				|| (aipPaciente.getDddFoneRecado() == null && StringUtils.isNotBlank(aipPaciente.getFoneRecado()))) {
			this.apresentarMsgNegocio(Severity.ERROR, "ERRO_INFORMAR_TELEFONE");
			error = true;
		}
		
		return error;
	}
	
	/**
	 * Método que encaminha o cadastro/edição do paciente
	 * 
	 * return true se der erro.
	 * 
	 * @throws BaseException
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	public boolean cadastrarPaciente() throws BaseException {
		boolean error = false;
		
		error = validaTelefone(error);
		
		// Quando o registro de nascimento for preenchido e tiver menos de 30
		// caracteres, obrigatóriamente os campos abaixo devem ser preenchidos
		if (aipPaciente.getRegNascimento() != null && obrigaPreenchimentoRN()) {
			if (aipPacienteDadosCns.getDataEmissao() == null) {
				this.apresentarMsgNegocio(Severity.ERROR, "MESSAGEM_DATA_EMISSAO_NULL");
				error = true;
			}
			if (aipPaciente.getRegNascimento().toString().length() < 30) {
				if (StringUtils.isEmpty(aipPacienteDadosCns.getNomeCartorio())) {
					this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_NOME_CARTORIO_NULL");
					error = true;
				}
				if (StringUtils.isEmpty(aipPacienteDadosCns.getLivro())) {
					this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_LIVRO_NULL");
					error = true;
				}
				if (aipPacienteDadosCns.getFolhas() == null) {
					this.apresentarMsgNegocio(Severity.ERROR, "MESSAGEM_FOLHAS_NULL");
					error = true;
				}
				if (aipPacienteDadosCns.getTermo() == null) {
					this.apresentarMsgNegocio(Severity.ERROR, "MESSAGEM_TERMO_NULL");
					error = true;
				}
			}
		}
		
		if (error) {
			return true;
		}
		
		error = validarDataNascimento(aipPaciente.getDtNascimento());
		
		if (error) {
			return true;
		}

		if (nacionalidadeTemp != null) {
			aipPaciente.setAipNacionalidades(nacionalidadeTemp);
		}

		if(obrigaPreenchimentoRN()){
			cadastroPacienteFacade.validarCartaoSUS(aipPaciente, aipPacienteDadosCns);
		} else {
			cadastroPacienteFacade.validarCartaoSUSRN(aipPaciente, aipPacienteDadosCns);
		}
		
		cadastroPacienteFacade.validarDuplicidadeCartaoSUS(aipPaciente);

		cadastroPacienteFacade.validarDuplicidadeCPF(aipPaciente);
		
		aipPaciente.setAipPacientesDadosCns(aipPacienteDadosCns);

		if (profissaoVO != null) {
			aipPaciente.setAipOcupacoes(cadastrosBasicosPacienteFacade.obterOcupacao(profissaoVO.getCodigoOcupacao()));
		} else {
			aipPaciente.setAipOcupacoes(null);
		}

		this.enderecosPaciente.addAll(this.enderecosAdicionados);

		final boolean isEdicao = aipPaciente.getCodigo() != null;

		// Se existir aipPaciente.maePaciente populado, reescreve o nome da mãe,
		// pois o usuário pode ter alterado somente o campo "Nome da Mãe"
		if (this.aipPaciente.getMaePaciente() != null) {
			this.aipPaciente.setNomeMae(this.aipPaciente.getMaePaciente().getNome());
		}

		String nomeMicrocomputador = null;
		try {
			nomeMicrocomputador = super.getEnderecoIPv4HostRemoto().toString();
		} catch (UnknownHostException e) {
			LOG.error("Exceção caputada:", e);
		}

		try {
			LOG.info("<<<<<<<<<<<<<<<<<<INICIO GRAVAR Paciente>>>>>>>>>>>>>>>>");
			LOG.info(PACIENTE + aipPaciente.getProntuario() + HIFEM + aipPaciente.getNome());

			//Data de recadastro.. Referente ao chamado #41392
			aipPaciente.setDtRecadastro(new Date());
			aipPaciente = this.cadastroPacienteFacade.persistirPaciente(aipPaciente, nomeMicrocomputador);

			LOG.info("<<<<<<<<<<<<<<<<<<FINAL GRAVAR Paciente>>>>>>>>>>>>>>>>");
			LOG.info(PACIENTE + aipPaciente.getProntuario() + HIFEM + aipPaciente.getNome());
			for (AipEnderecosPacientes enderecoPaciente : enderecosRemovidos) {
				if (cadastroPacienteFacade.existeEnderecoPaciente(enderecoPaciente.getId())) {
					cadastroPacienteFacade.excluirEndereco(enderecoPaciente);
				}
			}
			if (PAGE_PESQUISAR_PACIENTES_AGENDADOS.equals(this.cameFrom)){
				pesquisarPacientesAgendadosController.setCodigoPacienteTrocado(aipPaciente.getCodigo());
			}
			aipPaciente = this.pacienteFacade.obterPaciente(aipPaciente.getCodigo());
		} catch (ConstraintViolationException e) {
			Set<ConstraintViolation<?>> arr = e.getConstraintViolations();
			for (ConstraintViolation<?> item : arr) {
				this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_POR_PARAMETRO", item.getMessage());
			}
			return true;
		} catch (BaseRuntimeException e){
			apresentarExcecaoNegocio(e);
		}

		if (aipPaciente != null && aipPaciente.getCodigo() != null) {
			pacCodigo = aipPaciente.getCodigo();
		}

		this.enderecosAdicionados.clear();
//		this.enderecosRemovidos.clear();
		this.habilitaAdicionarEndereco = false;
		this.apresentarMensagemCadastrarPaciente(isEdicao);

		return error;
	}
	
	private void apresentarMensagemCadastrarPaciente(final boolean isEdicao) {
		// Não exibe mensagem de sucesso caso esteja dentro do fluxo de
		// internação e pacientes agendados.
		if (!INTERNACAO.equalsIgnoreCase(this.goingTo) && !PAGE_PESQUISAR_PACIENTES_AGENDADOS.equalsIgnoreCase(this.goingTo)) {
			if (isEdicao) {
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EDICAO_PACIENTE");
			} else {
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CRIACAO_PACIENTE");
			}
		}
	}
	
	/**
	 * Método que realiza a ação do botão cancelar na tela de cadastro de
	 * paciente
	 */
	public String cancelar() {

		Integer codigoPacienteSelecionado = aipPaciente.getCodigo();
		
		if (this.trocarPacienteConsulta){
			pesquisarPacientesAgendadosController.setCodigoPacienteTrocado(aipPaciente.getCodigo());				
		}

		nomePesquisar = null;
		aipPaciente = null;
		aipPacienteDadosCns = null;
		prontuarioMae = null;
		this.descricaoTipoProntuario = null;
		this.enderecosAdicionados.clear();
		this.enderecosRemovidos.clear();

		this.enderecosPaciente.clear();

		LOG.info("Cancelado");
		// ...
		if (REGISTRO_CIRURGIA_REALIZADA.equalsIgnoreCase(cameFrom)) {
			return REGISTRO_CIRURGIA_REALIZADA;
			
		} else if (PESQUISAR_CENSO_DIARIO_PACIENTES.equalsIgnoreCase(cameFrom)) {
			return PESQUISAR_CENSO_DIARIO_PACIENTES;
			
		} else if (AGENDAMENTO_PROCEDIMENTO.equalsIgnoreCase(cameFrom)) {
			return PESQUISA_PACIENTE_COMPONENTE;
			
		} else if (PESQUISA_CONSULTAS_PACIENTE.equalsIgnoreCase(cameFrom)) {
			return PESQUISA_CONSULTAS_PACIENTE;
			
		} else if (PAGE_PESQUISAR_PACIENTES_AGENDADOS.equalsIgnoreCase(cameFrom)) {
			pesquisarPacientesAgendadosController.setAcaoAtualizaDadosPacienteAmb("cancelar");
			return PAGE_PESQUISAR_PACIENTES_AGENDADOS;
			
		} else if (LIBERAR_CONSULTAS_LIST.equalsIgnoreCase(cameFrom)) {
			return LIBERAR_CONSULTAS_LIST;
			
		} else if (LISTAR_CIRURGIAS.equalsIgnoreCase(cameFromCirurgias) && INTERNACAO.equalsIgnoreCase(cameFrom)) {
			return LISTAR_CIRURGIAS;
			
		} else if (INTERNACAO.equalsIgnoreCase(cameFrom)) {
			return PESQUISA_PACIENTE;
			
		} else if (LISTAR_CONSULTAS_POR_GRADE.equalsIgnoreCase(cameFrom)) {
			return PESQUISA_FONETICA;
			
		} else if (ATUALIZAR_CONSULTA.equalsIgnoreCase(cameFrom)) {
			return ATUALIZAR_CONSULTA;
			
		} else if (AMBULATORIO_ATUALIZAR_CONSULTA.equalsIgnoreCase(cameFrom)) {
			return PESQUISA_PACIENTE_COMPONENTE;
		} else if (LISTA_PACIENTE_EMERGENCIA.equalsIgnoreCase(cameFrom)) {
			return LISTA_PACIENTE_EMERGENCIA;
		} else if (REDIRECT_LISTAR_TRANSPLANTES.equalsIgnoreCase(cameFrom)) {
			return REDIRECT_LISTAR_TRANSPLANTES ;
		}else if (REDIRECT_LISTAR_TRANSPLANTES_ORGAO.equals(cameFrom)){
			return REDIRECT_LISTAR_TRANSPLANTES_ORGAO;
		}else if (PAGE_PESQUISAR_INTERCONSULTAS.equalsIgnoreCase(cameFrom)) {
			pesquisarInterconsultasPaginatorController.setCameFrom(CADASTRO_PACIENTE);
			return PAGE_PESQUISAR_INTERCONSULTAS;
		}else if(REDIRECT_PESQUISA_FONETICA_AMBULATORIO.equalsIgnoreCase(cameFrom)){
			consultaPacientePaginatorController.setPacCodigoFonetica(codigoPacienteSelecionado);
			return REDIRECT_PESQUISA_FONETICA_AMBULATORIO;
		} else if(PESQUISA_FONETICA.equalsIgnoreCase(cameFrom)){
			return PESQUISA_FONETICA;
		}else if(PAGE_PACIENTES_LISTAS_TMO.equalsIgnoreCase(cameFrom) || PAGE_PACIENTES_LISTAS_TMO.equalsIgnoreCase(voltarPara)) {	// #50814
			return PAGE_PACIENTES_LISTAS_TMO;
		}else if(PAGE_PACIENTES_LISTAS_ORGAOS.equalsIgnoreCase(cameFrom) || PAGE_PACIENTES_LISTAS_ORGAOS.equalsIgnoreCase(voltarPara)) {	// #50814
			return PAGE_PACIENTES_LISTAS_ORGAOS;
		}

		if(consultaSelecionada != null){
			if(consultaSelecionada.getGradeSeq() != null){
				pesquisarPacientesAgendadosController.setSelectedTab(0);
				return ATENDIMENTO_AMBULATORIAL_ABA_AGENDADOS;
			}
		}

		this.setPacienteTemGMR(false);
		this.setValidarTrocaMunicipio(false);
		
		this.limparParametros();
		return REDIRECT_PESQUISA_PACIENTE;
	}

	/**
	 * Limpa parâmetros de conversação
	 */
	private void limparParametros() {

		this.aacExigeProntuario = null;

		this.nomePesquisar = null;

		this.cameFrom = null; // ATENÇÃO AQUI, POIS IMPLICA NA NAVEGAÇÃO

		this.cameFromCirurgias = null;

		this.goingTo = null; // ATENÇÃO AQUI, POIS IMPLICA NA NAVEGAÇÃO

		this.idLeito = null;

		this.quartoNumero = null;

		this.seqUnidadeFuncional = null;

		this.seqAtendimentoUrgencia = null;

		this.numeroMaximoProntuarioManual = null;

		this.aipPaciente = null;

		this.prontuarioMae = null;

		this.aipPacienteDadosCns = null;

		this.descricaoTipoProntuario = null;

		limparEnderecos();

		this.etniasPaciente = null;

		this.camposEnderecoAtivado = false;

		this.mostrarModalNacionalidade = false;

		this.nacionalidadeTemp = null;

		this.aipOrgaoEmissorSelecionado = null;

		this.profissaoVO = null;

		this.pacCodigo = null;

		this.geraProntuarioVirtual = false;

		this.pacienteTemGMR = false;

		this.validarTrocaMunicipio = false;

		this.ignorarModais = false;

		this.parametrosTela = null;

		this.edicaoInternacao = null;

		this.edicaoEnderecos = null;
		
		this.setExibirDadosCompRegNascimento(false);

	}

	private void limparEnderecos() {
		this.enderecosPaciente = new ArrayList<AipEnderecosPacientes>();

		this.enderecosRemovidos = new ArrayList<AipEnderecosPacientes>();

		this.enderecosAdicionados = new ArrayList<AipEnderecosPacientes>();
	}

	public String redirecionarRelatorioCartaoSus() {
		return REDIRECT_RELATORIO_CARTAO_SUS_PDF;
	}

	public void imprimirRelatorioCartaoSus() {
		// Verifica se houve mudança nos dados do paciente
		if (cadastroPacienteFacade.verificarPacienteModificado(aipPaciente)) {
			this.apresentarMsgNegocio(Severity.WARN, "MESSAGEM_GRAVACAO_PENDENTE_CARTAO_SUS");
		} else { // NOPMD
			try {
				this.relatorioCartaoSUSController.directPrint(this.aipPaciente.getCodigo());
			} catch (SistemaImpressaoException e) {
				apresentarExcecaoNegocio(e);
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);

			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
				this.apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
			}
		}
	}

	// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public String verificarSePacienteTemGRM() throws ApplicationBusinessException {
		String retorno = null;
		// Se pacienteTemGMR = true, já passou pela modal,
		// não precisa passar por ela novamente
		if (this.isPacienteTemGMR()) {
			retorno = this.validaTrocaMunicipio();
		} else {
			boolean bAux = this.pesquisaInternacaoFacade.pacienteNotifGMR(aipPaciente.getCodigo());
			if (aipPaciente.getCodigo() != null && bAux) {
				this.setPacienteTemGMR(true);
				// Manda mostrar a modal informando que o paciente tem
				// GRMmultiresistente e pergunta se deseja continuar?
				// se sim, então grava direto,
				// se não, fica na mesma tela.
				retorno = null;	// deve ficar na mesma tela....
			} else {
				// se paciente não tem GRMmultiresistente - não informa e grava
				// direto
				retorno = this.validaTrocaMunicipio();
			}
		}	
		return retorno;
	}
	
	private boolean validarDataNascimento(Date dtNascimento) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		sdf.setLenient(false);
		try {
			sdf.parse(sdf.format(dtNascimento).trim());
			Calendar c = Calendar.getInstance();
			c.set(c.YEAR, -(c.get(Calendar.YEAR) - 140));
			int dataInicio = c.get(Calendar.YEAR);
			c.setTime(new Date());
			int dataFim = c.get(Calendar.YEAR);
			c.setTime(dtNascimento);
			int dataNasc = c.get(Calendar.YEAR);

			if (!((dataInicio <= dataNasc) && (dataNasc <= dataFim))) {
				apresentarMsgNegocio(Severity.ERROR,"ERRO_INFORMAR_DATANASCIMENTO");
				return true;
			}
		} catch (Exception ex) {
			return true;
		}
		return false;
	}

	public String validaTrocaMunicipio() throws ApplicationBusinessException {
		String retorno = null;
		// Se validarTrocaMunicipio = true, já passou pela modal,
		// não precisa passar por ela novamente
		if (this.isValidarTrocaMunicipio()) {
			retorno = this.confirmar();
		} else {
			boolean bAux = false;

			Set<AipEnderecosPacientes> enderecos = aipPaciente.getEnderecos();

			for (AipEnderecosPacientes _endereco : enderecos) {
				if (_endereco.getAipBairrosCepLogradouro() == null && _endereco.getAipCidade() == null && this.cadastroPacienteFacade.nomeCidadeJaExistente(_endereco.getCidade())) {
					bAux = true;
					break;
				}
			}

			if (aipPaciente.getCodigo() != null && bAux) {
				this.setValidarTrocaMunicipio(true);
				
				// Manda mostrar a modal informando que o paciente tem endereço
				// com municipio já cadastrado em endereço não cadastrado.
				// Pergunda se deseja alterar o endereço
				// se sim, então redireciona para o cadastro de endereço,
				// se não, grava direto.
				
				retorno =  null;	// fica na mesma tela.
			} else {
				// se paciente não tem endereços com municipios já cadastrados,
				// grava direto
				retorno = this.confirmar();
			}
		}
		return retorno;
	}

	public void redirecionaAlteracaoMunicipio() {
		this.setValidarTrocaMunicipio(false);
		
		Set<AipEnderecosPacientes> enderecos = aipPaciente.getEnderecos();
		
		for (AipEnderecosPacientes _endereco : enderecos) {
			if (_endereco.getAipBairrosCepLogradouro() == null && _endereco.getAipCidade() == null && this.cadastroPacienteFacade.nomeCidadeJaExistente(_endereco.getCidade())) {
				_endereco.setCidade(null);
				prepararEdicaoEndereco(_endereco);
				break;
			}
		}
	}

	public void cancelarConfirmar() {
		this.setPacienteTemGMR(false);
	}

	public String redirecionaSolicitacaoInternacao(){
		return INTERNACAO_SOLICITACAO_INTERNACAO;
	}
	
	public String redirecionarAtendimentoExterno(){
		return EXAMES_ATENDIMENTO_EXTERNO_CRUD;
	}
	
	
	public String editarDadosPaciente() throws ApplicationBusinessException {
		
		if (consultaSelecionada != null){
			setPacCodigo(consultaSelecionada.getPacienteCodigo());
		}
	
		this.aipPaciente = new AipPacientes();
		aipPaciente.setCodigo(consultaSelecionada.getPacienteCodigo());
        aipPaciente.getCodigo();
		setGoingTo(null);
		setIdLeito(null);
		setQuartoNumero(null);
		setSeqUnidadeFuncional(null);
		setSeqAtendimentoUrgencia(null);		
		setEdicaoInternacao(false);
		prepararEdicaoPaciente(aipPaciente, null);
		
		//setCameFrom(PAGE_GESTAO_INTERCONSULTAS);
		
		// Retirar no final da implementacao - TESTE
		return CADASTRO_PACIENTE;
	}
	
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// ### GETs e SETs ###

	public AipPacientes getAipPaciente() {
		return aipPaciente;
	}

	public void setAipPaciente(AipPacientes aipPaciente) {
		this.aipPaciente = aipPaciente;
	}

	public List<AipEnderecosPacientes> getEnderecosPaciente() {
		if (aipPaciente != null && this.aipPaciente.getEnderecos() != null && !enderecosPaciente.isEmpty()) {
			enderecosPaciente = new ArrayList<AipEnderecosPacientes>(this.aipPaciente.getEnderecos());
			
			 for (AipEnderecosPacientes endPaciente : enderecosPaciente){
				Integer cep = endPaciente.getAipBairrosCepLogradouro() != null ? endPaciente.getAipBairrosCepLogradouro().getId().getCloCep() : endPaciente.getCep();
				Integer codLogradouro = endPaciente.getAipBairrosCepLogradouro() != null ? endPaciente.getAipBairrosCepLogradouro().getId().getCloLgrCodigo() : null;
				Integer codBairroLogradouro = endPaciente.getAipBairrosCepLogradouro() != null ? endPaciente.getAipBairrosCepLogradouro().getId().getBaiCodigo() : null;
            	try {
					endPaciente.setvAipCep(cadastroPacienteFacade.obterVAipCeps(cep, codLogradouro, codBairroLogradouro));
				} catch (ApplicationBusinessException e) {
					apresentarExcecaoNegocio(e);
				}
            }
			
		}
		return enderecosPaciente;
	}

	public void setEnderecosPaciente(List<AipEnderecosPacientes> enderecos) {
		this.enderecosPaciente = enderecos;
	}

	public boolean isCamposEnderecoAtivado() {
		return camposEnderecoAtivado;
	}

	public void setCamposEnderecoAtivado(boolean camposEnderecoAtivado) {
		this.camposEnderecoAtivado = camposEnderecoAtivado;
	}

	public DominioSimNao getAacExigeProntuario() {
		return aacExigeProntuario;
	}

	public void setAacExigeProntuario(DominioSimNao aacExigeProntuario) {
		this.aacExigeProntuario = aacExigeProntuario;
	}

	public void atribuirNaturalidade() {

		if (this.aipPaciente.getAipCidades() != null) {
			atribuirNaturalidade(this.aipPaciente.getAipCidades());
		}

	}

	public void atribuirNaturalidade(AipCidades cidade) {

		this.aipPaciente.setAipCidades(cidade);
	}

	public String getDescricaoCidadeSelecionada() {

		if (this.aipPaciente.getAipCidades() == null || StringUtils.isBlank(this.aipPaciente.getAipCidades().getNome())) {
			return "";
		}
		return this.aipPaciente.getAipCidades().getNome();

	}

	public String getDescricaoNacionalidadeSelecionada() {
		if (this.aipPaciente.getAipNacionalidades() == null || StringUtils.isBlank(this.aipPaciente.getAipNacionalidades().getDescricao())) {
			return "";
		}
		return this.aipPaciente.getAipNacionalidades().getDescricao();
	}

	public String getDescricaoUFSelecionada() {
		if (this.aipPaciente == null || this.aipPaciente.getAipCidades() == null || StringUtils.isBlank(this.aipPaciente.getAipCidades().getNome())) {
			return "";
		}
		return this.aipPaciente.getAipCidades().getAipUf().getSigla();
	}

	public String getNomePesquisar() {
		return nomePesquisar;
	}

	public void setNomePesquisar(String nomePesquisar) {
		this.nomePesquisar = nomePesquisar;
	}

	public boolean isMostrarModalNacionalidade() {
		return mostrarModalNacionalidade;
	}

	public void setMostrarModalNacionalidade(boolean mostrarModalNacionalidade) {
		this.mostrarModalNacionalidade = mostrarModalNacionalidade;
	}

	public boolean isMostrarLinkExcluirNaturalidade() {
		return this.aipPaciente.getAipCidades() != null;
	}

	public boolean isMostrarLinkExcluirNacionalidade() {
		return this.aipPaciente.getAipNacionalidades() != null;
	}

	public void limparNaturalidade() {
		this.aipPaciente.setAipCidades(null);
	}

	public void limparNacionalidade() {
		this.aipPaciente.setAipNacionalidades(null);
		this.nacionalidadeTemp = null;
	}

	public List<AipNacionalidades> pesquisarNacionalidadePorCodigoSiglaNome(String paramPesquisa) {
		return cadastrosBasicosPacienteFacade.pesquisarPorCodigoSiglaNome(paramPesquisa);
	}

	public List<AipCidades> pesquisarCidadePorCodigoNome(String paramPesquisa) {
		if(!pesquisaNaturalidadeOrdemAlfabetica()){
			return returnSGWithCount(cadastrosBasicosPacienteFacade.pesquisarPorCodigoNome(paramPesquisa, true),pesquisarCountCidadePorCodigoNome(paramPesquisa)) ;
		} else {
			return cadastrosBasicosPacienteFacade.pesquisarPorCodigoNomeAlfabetica(paramPesquisa, true);
		}
			
	}

	public Long pesquisarCountCidadePorCodigoNome(String paramPesquisa) {
		return cadastrosBasicosPacienteFacade.pesquisarCountCidadePorCodigoNome((String) paramPesquisa);
	}

	public AipNacionalidades getNacionalidadeTemp() {
		return nacionalidadeTemp;
	}

	public void setNacionalidadeTemp(AipNacionalidades nacionalidadeTemp) {
		this.nacionalidadeTemp = nacionalidadeTemp;
	}

	public AipPacientesDadosCns getAipPacienteDadosCns() {
		return aipPacienteDadosCns;
	}

	public void setAipPacienteDadosCns(AipPacientesDadosCns aipPacienteDadosCns) {
		this.aipPacienteDadosCns = aipPacienteDadosCns;
	}

	public void atribuirOrgaoEmissor() {
		if (this.aipPacienteDadosCns.getAipOrgaosEmissor() != null) {
			atribuirOrgaoEmissor(this.aipPacienteDadosCns.getAipOrgaosEmissor());
		}
	}

	public void atribuirOrgaoEmissor(AipOrgaosEmissores orgaoEmissor) {
		this.aipPacienteDadosCns.setAipOrgaosEmissor(orgaoEmissor);
	}

	public void limparOrgao() {
		this.aipPacienteDadosCns.setAipOrgaosEmissor(null);
	}

	public void atribuirUF() {
		if (this.aipPacienteDadosCns.getAipUf() != null) {
			atribuirUF(this.aipPacienteDadosCns.getAipUf());
		}
	}

	public void atribuirUF(AipUfs uf) {
		this.aipPacienteDadosCns.setAipUf(uf);
	}

	public boolean isMostrarLinkExcluirUF() {
		return this.aipPacienteDadosCns.getAipUf() != null;
	}

	public boolean isMostrarLinkExcluirOrgao() {
		return this.aipPacienteDadosCns.getAipOrgaosEmissor() != null;
	}

	public void limparUF() {
		this.aipPacienteDadosCns.setAipUf(null);
	}

	public String getNomeUFSelecionada() {

		if (this.aipPacienteDadosCns.getAipUf() == null || StringUtils.isBlank(this.aipPacienteDadosCns.getAipUf().getNome())) {
			return "";
		}
		return this.aipPacienteDadosCns.getAipUf().getNome();

	}

	public String getDescricaoOrgaoSelecionado() {

		if (this.aipPacienteDadosCns.getAipOrgaosEmissor() == null || StringUtils.isBlank(this.aipPacienteDadosCns.getAipOrgaosEmissor().getDescricao())) {
			return "";
		}
		return this.aipPacienteDadosCns.getAipOrgaosEmissor().getDescricao();

	}

	public AipOrgaosEmissores getAipOrgaoEmissorSelecionado() {
		return aipOrgaoEmissorSelecionado;
	}

	public void setAipOrgaoEmissorSelecionado(AipOrgaosEmissores aipOrgaoEmissorSelecionado) {
		this.aipOrgaoEmissorSelecionado = aipOrgaoEmissorSelecionado;
	}

	/**
	 * Método que redireciona para a página de Histórico
	 */
	public String redirecionarHistorico() {
		historicoPacienteController.prepararHistorico();

		return REDIRECT_HISTORICO_PACIENTE;
	}

	/**
	 * Método que redireciona para a página de Convenios
	 */
	public String redirecionarConvenios() {
		conveniosPacienteController.preparaInicioConvenioPaciente();

		return REDIRECT_CONVENIOS_PACIENTE;
	}

	/**
	 * Método que redireciona para a página de Dados Adicionais
	 */
	public String redirecionarDadosAdicionais() {
		dadosAdicionaisPacienteController.preparaInicioDadosAdicionaisPaciente();

		return REDIRECT_DADOS_ADICIONAIS_PACIENTE;
	}

	public Integer getProntuarioMae() {
		return prontuarioMae;
	}

	public void setProntuarioMae(Integer prontuarioMae) {
		this.prontuarioMae = prontuarioMae;
	}

	public String getStyleProntuario() {
		String retorno = "";

		if (aipPaciente != null && aipPaciente.isProntuarioVirtual()) {
			retorno = "background-color:#0000ff";
		}
		return retorno;

	}

	public String getDescricaoTipoProntuario() {
		return descricaoTipoProntuario;
	}

	public void setDescricaoTipoProntuario(String descricaoTipoProntuario) {
		this.descricaoTipoProntuario = descricaoTipoProntuario;
	}

	public String getCameFrom() {
		return cameFrom;
	}

	public void setCameFrom(String cameFrom) {
		this.cameFrom = cameFrom;
	}

	/**
	 * @param goingTo
	 *            the goingTo to set
	 */
	public void setGoingTo(String goingTo) {
		this.goingTo = goingTo;
	}

	/**
	 * @return the goingTo
	 */
	public String getGoingTo() {
		return goingTo;
	}

	public String navegarInternacao() {
		String retorno = null;
		AipEnderecosPacientes enderecoResidencial = this.cadastroPacienteFacade.obterEnderecoResidencialPaciente(this.aipPaciente);
		if (enderecoResidencial == null) {
			this.apresentarMsgNegocio(Severity.ERROR, "AIN_PACIENTE_SEM_ENDERECO");
		} else {
			retorno = this.goingTo;
		}
		return retorno;

	}

	/**
	 * @param idLeito
	 *            the idLeito to set
	 */
	public void setIdLeito(String idLeito) {
		this.idLeito = idLeito;
	}

	/**
	 * @return the idLeito
	 */
	public String getIdLeito() {
		return idLeito;
	}

	/**
	 * @param quartoNumero
	 *            the quartoNumero to set
	 */
	public void setQuartoNumero(Short quartoNumero) {
		this.quartoNumero = quartoNumero;
	}

	/**
	 * @return the quartoNumero
	 */
	public Short getQuartoNumero() {
		return quartoNumero;
	}

	/**
	 * @param seqUnidadeFuncional
	 *            the seqUnidadeFuncional to set
	 */
	public void setSeqUnidadeFuncional(Short seqUnidadeFuncional) {
		this.seqUnidadeFuncional = seqUnidadeFuncional;
	}

	/**
	 * @return the seqUnidadeFuncional
	 */
	public Short getSeqUnidadeFuncional() {
		return seqUnidadeFuncional;
	}

	public String getDescricaoLogradouro(AipEnderecosPacientes endereco) {

		AipLogradouros logradouro = endereco.getAipBairrosCepLogradouro().getCepLogradouro().getLogradouro();

		StringBuilder stBuilder = new StringBuilder();

		if (logradouro.getAipTipoLogradouro() != null) {
			stBuilder.append(logradouro.getAipTipoLogradouro().getDescricao());
			stBuilder.append(' ');
		}

		if (logradouro.getAipTituloLogradouro() != null) {
			stBuilder.append(logradouro.getAipTituloLogradouro().getDescricao());
			stBuilder.append(' ');
		}

		stBuilder.append(logradouro.getNome());

		return stBuilder.toString();

	}

	public String getCEPFormatado(Integer cep) {
		
		if(cep != null  &&  cep > 0){
			return CoreUtil.formataCEP(cep);
		}
		
		return null;
	}


	public String getUfEndereco(AipEnderecosPacientes endereco) {
		if (endereco.getAipBairrosCepLogradouro() != null) {
			return endereco.getAipBairrosCepLogradouro().getCepLogradouro().getLogradouro().getAipCidade().getAipUf().getSigla();
		} else if (endereco.getAipUf() != null) {
			return endereco.getAipUf().getSigla();
		} else if (endereco.getAipCidade() != null) {
			return endereco.getAipCidade().getAipUf().getSigla();
		} else {
			return "";
		}

	}

	public String getCidadeEndereco(AipEnderecosPacientes endereco) {
		if (endereco.getAipBairrosCepLogradouro() != null) {
			return endereco.getAipBairrosCepLogradouro().getCepLogradouro().getLogradouro().getAipCidade().getNome();
		} else if (endereco.getAipCidade() != null) {
			return endereco.getAipCidade().getNome();
		} else if (endereco.getCidade() != null) {
			return endereco.getCidade();
		} else {
			return "";
		}

	}

	public void habilitaInclusaoNovoEndereco(){
		habilitaAdicionarEndereco = true;
	}

	public List<ProfissaoVO> pesquisarProfissoes(String objPesquisa) {
		return returnSGWithCount(cadastrosBasicosPacienteFacade.pesquisarProfissioesPorCodigoDescricao((String) objPesquisa), pesquisarProfissoesCount(objPesquisa));
	}

	public Long pesquisarProfissoesCount(String objPesquisa) {
		return cadastrosBasicosPacienteFacade.pesquisarProfissioesPorCodigoDescricaoCount((String) objPesquisa);
	}

	public ProfissaoVO getProfissaoVO() {
		return profissaoVO;
	}

	public void setProfissaoVO(ProfissaoVO profissaoVO) {
		this.profissaoVO = profissaoVO;
	}

	public void setSeqAtendimentoUrgencia(Integer seqAtendimentoUrgencia) {
		this.seqAtendimentoUrgencia = seqAtendimentoUrgencia;
	}

	public Integer getSeqAtendimentoUrgencia() {
		return seqAtendimentoUrgencia;
	}

	public Long getNumeroMaximoProntuarioManual() {
		return numeroMaximoProntuarioManual;
	}

	public void setNumeroMaximoProntuarioManual(Long numeroMaximoProntuarioManual) {
		this.numeroMaximoProntuarioManual = numeroMaximoProntuarioManual;
	}

	public List<AipOrgaosEmissores> pesquisarOrgaoEmissorPorCodigoDescricao(String paramPesquisa) {
		return this.cadastroPacienteFacade.pesquisarOrgaoEmissorPorCodigoDescricao(paramPesquisa);
	}

	public Long obterCountOrgaoEmissorPorCodigoDescricao(String paramPesquisa) {
		return this.cadastroPacienteFacade.obterCountOrgaoEmissorPorCodigoDescricao(paramPesquisa);
	}

	public List<AipUfs> pesquisarPorSiglaNome(String paramPesquisa) {
		return this.cadastrosBasicosPacienteFacade.pesquisarPorSiglaNome(paramPesquisa);
	}

	public Long obterCountUfPorSiglaNome(String paramPesquisa) {
		return this.cadastrosBasicosPacienteFacade.obterCountUfPorSiglaNome(paramPesquisa);
	}

	public String imprimirBoletim() {
		try {
			this.relatorioBoletimIdentificacaoPacienteController.directPrint(this.aipPaciente.getProntuario());
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			this.apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
		return REDIRECT_RELATORIO_BOLETIM_IDENTIFICACAO_CADASTRO_PACIENTE_PDF;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public void setGeraProntuarioVirtual(Boolean geraProntuarioVirtual) {
		this.geraProntuarioVirtual = geraProntuarioVirtual;
	}

	public Boolean getGeraProntuarioVirtual() {
		return geraProntuarioVirtual;
	}

	public boolean isPacienteTemGMR() {
		return pacienteTemGMR;
	}

	public void setPacienteTemGMR(boolean pacienteTemGMR) {
		this.pacienteTemGMR = pacienteTemGMR;
	}

	public String getCameFromCirurgias() {
		return cameFromCirurgias;
	}

	public void setCameFromCirurgias(String cameFromCirurgias) {
		this.cameFromCirurgias = cameFromCirurgias;
	}

	public List<AipEtnia> getEtniasPaciente() {
		if (etniasPaciente == null) {
			etniasPaciente = this.buscarEtnias();
		}
		return etniasPaciente;
	}

	public void setEtniasPaciente(List<AipEtnia> etniasPaciente) {
		this.etniasPaciente = etniasPaciente;
	}

	public void prepararEdicaoPaciente(AipPacientes pacienteSelecionado, DominioSimNao aacExigeProntuario) {

		if (pacienteSelecionado != null) {
			this.enderecosPaciente.clear();
			this.aipPaciente = this.pacienteFacade.carregarInformacoesEdicao(pacienteSelecionado);
			this.enderecosPaciente.addAll(this.cadastroPacienteFacade.obterEndPaciente(pacienteSelecionado.getCodigo()));
		}

		atualizaDadosCNS();

		if (aipPaciente.getMaePaciente() != null) {
			prontuarioMae = aipPaciente.getMaePaciente().getProntuario();
		}
		if (aipPaciente.getPrntAtivo() != null) {
			descricaoTipoProntuario = aipPaciente.getPrntAtivo().getDescricao();
		}

		AipOcupacoes ocupacao = aipPaciente.getAipOcupacoes();
		if (ocupacao != null) {
			this.profissaoVO = new ProfissaoVO(ocupacao.getCodigo(), ocupacao.getDescricao());
		}else{
			this.profissaoVO = null;
		}

		aipPaciente.setInsereProntuarioEdicao(false);

		atualizaEnderecos();

		if(aipPaciente.getRegNascimento()!=null) {
			if (aipPaciente.getRegNascimento().length() > 30) {
				setExibirDadosCompRegNascimento(true);
			}
		}
		aipPaciente.setInsereProntuarioEdicao(parametroliberaProntuarioManual());
	
		prepararCargaPagina(aacExigeProntuario);
	}
	
	private boolean parametroliberaProntuarioManual() {
		try{
			AghParametros paramPermiteProntuarioManual = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_PERMITE_PRONTUARIO_MANUAL);
			String parametrizaProntuarioManual = paramPermiteProntuarioManual.getVlrTexto();
			if (parametrizaProntuarioManual.equalsIgnoreCase("S")){
				return Boolean.TRUE;
			}
		}catch (ApplicationBusinessException e) {
			LOG.error(e.getMessage(), e);
		}
		return Boolean.FALSE;	

	}

	private void atualizaDadosCNS() {
		if (aipPaciente.getAipPacientesDadosCns() != null) {
			aipPacienteDadosCns = pacienteFacade.obterDadosCNSPaciente(aipPaciente.getCodigo());
		} else {
			aipPacienteDadosCns = new AipPacientesDadosCns();
		}
	}

	private void atualizaEnderecos() {
		for (AipEnderecosPacientes endereco : enderecosAdicionados) {
			if (this.aipPaciente.getEnderecos().contains(endereco)) {
				aipPaciente.getEnderecos().remove(endereco);
			}
			aipPaciente.getEnderecos().add(endereco);
		}

		for (AipEnderecosPacientes endereco : enderecosRemovidos) {
			aipPaciente.getEnderecos().remove(endereco);
		}
	}
	
	/**
	 * Método que obriga informar o campo ocupação no cadastro do paciente
	 * @throws AGHUNegocioException 
	 */
	public Boolean requererCampoOcupacao() {
			AghParametros aghParamFCO;
			try {
				aghParamFCO = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_OBRIGATORIEDADE_CAMPO_OCUPACAO);
				String paramCampoOcupacao = aghParamFCO.getVlrTexto();
				return "S".equalsIgnoreCase(paramCampoOcupacao);
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}

		return Boolean.FALSE;
	}

	public void prepararInclusaoPaciente(String nome, String cameFrom, DominioSimNao aacExigeProntuario) {

		aipPaciente = new AipPacientes();
		limparEnderecos();

		aipPaciente.setNome(nome);

		aipPaciente.setInsereProntuarioCadastro(parametroliberaProntuarioManual());
		
		if (!TELA_EMERGENCIA_LISTA_PACIENTES.equals(this.cameFrom)){
			aipPaciente.setGerarProntuario(securityController.usuarioTemPermissao("paciente", "gerarProntuario") && !getIsGeraProntuarioDesabilitado());
		} else {
			aipPaciente.setGerarProntuario(false);
		}
		// ver isso aqui depois
		aipPaciente.setGeraProntuarioVirtual(this.geraProntuarioVirtual);

		aipPaciente.setApresentarCadConfirmado(false);

		// Para o caso de uma inclusão durante o processo de internação.
		this.goingTo = cameFrom;

		this.profissaoVO = null;

		aipPaciente.setInsereProntuarioEdicao(parametroliberaProntuarioManual());

		prepararCargaPagina(aacExigeProntuario);

	}
	
	private void prepararCargaPagina(DominioSimNao aacExigeProntuario) {
		if (aacExigeProntuario == null) {
			this.aacExigeProntuario = DominioSimNao.N;
		}

		// ver isso aqui depois
		if (Boolean.TRUE.equals(this.geraProntuarioVirtual)) {
			aipPaciente.setGerarProntuario(false);
		} else if (aipPaciente.getIndGeraProntuario() == null) {
			if (!TELA_EMERGENCIA_LISTA_PACIENTES.equals(cameFrom)) {
				aipPaciente.setGerarProntuario(securityController.usuarioTemPermissao("paciente", "gerarProntuario"));
			} else {
				aipPaciente.setGerarProntuario(false);
			}
		}

		if (aipPacienteDadosCns == null) {
			aipPacienteDadosCns = new AipPacientesDadosCns();
		}

	}
	
	/**
	 * Este método é executado quando é digitado a certidão de nascimento na tela
	 * 
	 */
	public void changeCertidaoNascimento() {
		if(aipPaciente != null && aipPaciente.getRegNascimento() != null) {
			if (aipPaciente.getRegNascimento().length() > 30) {
				
				setExibirDadosCompRegNascimento(true);
			}
			else {
				setExibirDadosCompRegNascimento(false);
			}
		}
	}

	public String cancelarEdicaoEmergencia() {
		limparParametros();
		listaPacientesEmergenciaPaginatorController.setMamUnidAtendem(mamUnidAtendem);
		listaPacientesEmergenciaPaginatorController.setFiltro(filtroListaEmergencia);
		return REDIRECT_LISTA_EMERGENCIA;
	}
	
//	private void redirecionarPaginaPorAjax(String caminhoPagina){
//		try{
//			FacesContext ctx = FacesContext.getCurrentInstance();
//			ExternalContext extContext = ctx.getExternalContext();
//			String url = extContext.encodeActionURL(ctx.getApplication().getViewHandler().getActionURL(ctx, caminhoPagina)); 
//			extContext.redirect(url);
//		}
//		catch (IOException e) {
//			LOG.error(e.getMessage(), e);		
//		}
//	}
	
	private void popularParametrosAghWeb() {
		try {
			if (isHcpa) {
				AghParametros aghParametroUrlBaseWebForms = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_URL_BASE_AGH_ORACLE_WEBFORMS);
				if (aghParametroUrlBaseWebForms != null) {
					urlBaseWebForms = aghParametroUrlBaseWebForms.getVlrTexto();
				}
				AghParametros aghParametrosBanco = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_BANCO_AGHU_AGHWEB);
				if (aghParametrosBanco != null) {
					banco = aghParametrosBanco.getVlrTexto();
				}
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void consultaNovosParametrosAghWeb(Integer numeroConsultaSelecionada, Integer pacienteCodigo) throws ApplicationBusinessException{
		consultaSelecionada = new ConsultaAmbulatorioVO();
		consultaSelecionada.setPacienteCodigo(pacienteCodigo);
		
		
		vAtdSeq = ambulatorioFacade.getAghAtendimentosParametroVOQueryBuilder(numeroConsultaSelecionada);
		
		parametrosAghEspecialidadesAtestadoVOList = ambulatorioFacade.getAghEspecialidadesParametroVOQueryBuilder(numeroConsultaSelecionada);
		
		RapServidores servidorLogado = new RapServidores();
		
			servidorLogado = registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado());
			if(servidorLogado != null){
				parametrosAghPerfilProcessoVOList = ambulatorioFacade.getAghPerfilProcessoParametroVOQueryBuilder(servidorLogado);
				cseProcessosList = ambulatorioFacade.getAghCseUsuarioParametroVOQueryBuilder(servidorLogado, numeroConsultaSelecionada);
			}
	}
	
	public void populaNovosParametrosAghWeb(Integer numeroConsultaSelecionada, Integer pacienteCodigo) throws ApplicationBusinessException{
		
		Integer vRetorno = 3;
		consultaNovosParametrosAghWeb(numeroConsultaSelecionada, pacienteCodigo);
		
		if(parametrosAghPerfilProcessoVOList != null && !parametrosAghPerfilProcessoVOList.isEmpty()){
			for(ParametrosAghPerfilProcessoVO parametrosAghPerfilProcessoVO : parametrosAghPerfilProcessoVOList){
				if(parametrosAghPerfilProcessoVO.getIndConsulta() || parametrosAghPerfilProcessoVO.getvEspPai()){
					vRetorno = 2;		
				}
				if(parametrosAghPerfilProcessoVO.getvUnfSeq()){
					vRetorno = 1;
				}
			}

		}
		pUso = "E";
		
		if(vRetorno == 1 || vRetorno == 2){
			populaNovosParametrosAghWeb2(numeroConsultaSelecionada);
		}else{
			if(cseProcessosList.isEmpty()){
				this.apresentarMsgNegocio(Severity.ERROR, "ERRO_MAM_01456");
			} else {
				this.apresentarMsgNegocio(Severity.ERROR, "ERRO_MAM_01443");
			}
		}
		RequestContext.getCurrentInstance().execute("chamaModal()");
	}

	private void populaNovosParametrosAghWeb2(Integer numeroConsultaSelecionada) {
		pConNumero = numeroConsultaSelecionada;
		if(aghAtendimentosList != null && !aghAtendimentosList.isEmpty()){
			for(AghAtendimentos aghAtendimento :aghAtendimentosList){
				vAtdSeq = aghAtendimento.getSeq();
			}
		}
		vDthrMvto = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
		if(!parametrosAghEspecialidadesAtestadoVOList.isEmpty()){
			for(ParametrosAghEspecialidadesAtestadoVO parametrosAghEspecialidadesAtestadoVO : parametrosAghEspecialidadesAtestadoVOList){
				//vAtdSeq = parametrosAghEspecialidadesAtestadoVO.getvUnfSeq().intValue();
				vEspSeq = parametrosAghEspecialidadesAtestadoVO.getvEspSeq();
				vEspPai = parametrosAghEspecialidadesAtestadoVO.getvEspPai();
			}
		}
	}

	public String redirecionarAgrupamentoPacientes() {
		return PAGE_AGRUPAMENTO_FAMILIAR;
	}

	public ParametrosTelaVO getParametrosTela() {
		return parametrosTela;
	}

	public void setParametrosTela(ParametrosTelaVO parametrosTela) {
		this.parametrosTela = parametrosTela;
	}

	public Boolean getEdicaoInternacao() {
		return edicaoInternacao;
	}

	public void setEdicaoInternacao(Boolean edicaoInternacao) {
		this.edicaoInternacao = edicaoInternacao;
	}

	public Boolean getEdicaoEnderecos() {
		return edicaoEnderecos;
	}

	public void setEdicaoEnderecos(Boolean edicaoEnderecos) {
		this.edicaoEnderecos = edicaoEnderecos;
	}

	public boolean isValidarTrocaMunicipio() {
		return validarTrocaMunicipio;
	}

	public void setValidarTrocaMunicipio(boolean validarTrocaMunicipio) {
		this.validarTrocaMunicipio = validarTrocaMunicipio;
	}

	public boolean isIgnorarModais() {
		return ignorarModais;
	}

	public void setIgnorarModais(boolean ignorarModais) {
		this.ignorarModais = ignorarModais;
	}

	public Boolean getIsPrepararInclusaoPaciente() {
		return isPrepararInclusaoPaciente;
	}

	public void setIsPrepararInclusaoPaciente(Boolean isPrepararInclusaoPaciente) {
		this.isPrepararInclusaoPaciente = isPrepararInclusaoPaciente;
	}

	public Integer getPacienteSelecionado() {
		return pacienteSelecionado;
	}

	public void setPacienteSelecionado(Integer pacienteSelecionado) {
		this.pacienteSelecionado = pacienteSelecionado;
	}

	public Boolean getisPrepararEdicaoPaciente() {
		return isPrepararEdicaoPaciente;
	}

	public void setisPrepararEdicaoPaciente(Boolean prepararEdicaoPaciente) {
		this.isPrepararEdicaoPaciente = prepararEdicaoPaciente;
	}	

	public boolean isRetornoEnderecoPaciente() {
		return retornoEnderecoPaciente;
	}

	public void setRetornoEnderecoPaciente(boolean retornoEnderecoPaciente) {
		this.retornoEnderecoPaciente = retornoEnderecoPaciente;
	}

	public Integer getCid() {
		return cid;
	}

	public void setCid(Integer cid) {
		this.cid = cid;
	}
	
	public String getComplementoEndereco() {
		return complementoEndereco;
	}

	public void setComplementoEndereco(String complementoEndereco) {
		this.complementoEndereco = complementoEndereco;
	}

	public boolean isHabilitaAdicionarEndereco() {
		return habilitaAdicionarEndereco;
	}

	public void setHabilitaAdicionarEndereco(boolean habilitaAdicionarEndereco) {
		this.habilitaAdicionarEndereco = habilitaAdicionarEndereco;
	}

	public void preparaEdicaoComplemento(){
		this.complementoEndereco = this.enderecoController.getEnderecoEdicao().getComplLogradouro();
	}

	public void confirmaEdicaoComplemento(){
		this.enderecoController.getEnderecoEdicao().setComplLogradouro(this.complementoEndereco);
	}
	
	public Boolean getIsGeraProntuarioDesabilitado() {
		return isGeraProntuarioDesabilitado;
	}

	public void setIsGeraProntuarioDesabilitado(Boolean isGeraProntuarioDesabilitado) {
		this.isGeraProntuarioDesabilitado = isGeraProntuarioDesabilitado;
	}

	public boolean isExibirDadosCompRegNascimento() {
		return exibirDadosCompRegNascimento;
	}

	public void setExibirDadosCompRegNascimento(boolean exibirDadosCompRegNascimento) {
		this.exibirDadosCompRegNascimento = exibirDadosCompRegNascimento;
	}

	public ConsultaAmbulatorioVO getConsultaSelecionada() {
		return consultaSelecionada;
	}

	public void setConsultaSelecionada(ConsultaAmbulatorioVO consultaSelecionada) {
		this.consultaSelecionada = consultaSelecionada;
	}

	public String getBanco() {
		return banco;
	}

	public void setBanco(String banco) {
		this.banco = banco;
	}

	public String getUrlBaseWebForms() {
		return urlBaseWebForms;
	}

	public void setUrlBaseWebForms(String urlBaseWebForms) {
		this.urlBaseWebForms = urlBaseWebForms;
	}

	public Boolean getIsHcpa() {
		return isHcpa;
	}

	public void setIsHcpa(Boolean isHcpa) {
		this.isHcpa = isHcpa;
	}

	public Boolean getIsUbs() {
		return isUbs;
	}

	public void setIsUbs(Boolean isUbs) {
		this.isUbs = isUbs;
	}

	public Integer getpConNumero() {
		return pConNumero;
	}

	public void setpConNumero(Integer pConNumero) {
		this.pConNumero = pConNumero;
	}

	public Integer getvAtdSeq() {
		return vAtdSeq;
	}

	public void setvAtdSeq(Integer vAtdSeq) {
		this.vAtdSeq = vAtdSeq;
	}

	public String getvDthrMvto() {
		return vDthrMvto;
	}

	public void setvDthrMvto(String vDthrMvto) {
		this.vDthrMvto = vDthrMvto;
	}

	public Short getvEspSeq() {
		return vEspSeq;
	}

	public void setvEspSeq(Short vEspSeq) {
		this.vEspSeq = vEspSeq;
	}

	public Short getvEspPai() {
		return vEspPai;
	}

	public void setvEspPai(Short vEspPai) {
		this.vEspPai = vEspPai;
	}

	public String getpUso() {
		return pUso;
	}

	public void setpUso(String pUso) {
		this.pUso = pUso;
	}

	public AipGrupoFamiliarPacientes getGrupoFamiliar() {
		return grupoFamiliar;
	}

	public void setGrupoFamiliar(AipGrupoFamiliarPacientes grupoFamiliar) {
		this.grupoFamiliar = grupoFamiliar;
	}

	public boolean isTrocarPacienteConsulta() {
		return trocarPacienteConsulta;
}
	
	public void setTrocarPacienteConsulta(boolean trocarPacienteConsulta) {
		this.trocarPacienteConsulta = trocarPacienteConsulta;
	}

	public Integer getNumeroConsulta() {
		return numeroConsulta;
	}

	public void setNumeroConsulta(Integer numeroConsulta) {
		this.numeroConsulta = numeroConsulta;
	}	public MamUnidAtendem getMamUnidAtendem() {
		return mamUnidAtendem;
	}

	public void setMamUnidAtendem(MamUnidAtendem mamUnidAtendem) {
		this.mamUnidAtendem = mamUnidAtendem;
	}

	public PacienteFiltro getFiltroListaEmergencia() {
		return filtroListaEmergencia;
	}

	public void setFiltroListaEmergencia(PacienteFiltro filtroListaEmergencia) {
		this.filtroListaEmergencia = filtroListaEmergencia;
	}
	
	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}
	public Boolean getOrderListaPacientesAsc() {
		return orderListaPacientesAsc;
	}

	public void setOrderListaPacientesAsc(Boolean orderListaPacientesAsc) {
		this.orderListaPacientesAsc = orderListaPacientesAsc;
	}
	
	public String getOrderPropertyListaPacientes() {
		return orderPropertyListaPacientes;
}

	public void setOrderPropertyListaPacientes(String orderPropertyListaPacientes) {
		this.orderPropertyListaPacientes = orderPropertyListaPacientes;
	}

	public Integer getMaxResultsListaPacientes() {
		return maxResultsListaPacientes;
	}

	public void setMaxResultsListaPacientes(Integer maxResultsListaPacientes) {
		this.maxResultsListaPacientes = maxResultsListaPacientes;
	}
	
	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public void setNomePaiRequired(Boolean nomePaiRequired) {
		this.nomePaiRequired = nomePaiRequired;
	}

	public Boolean getNomePaiRequired() {
		return nomePaiRequired;
	}

	public void setGrauInstrucaoRequired(Boolean grauInstrucaoRequired) {
		this.grauInstrucaoRequired = grauInstrucaoRequired;
	}

	public Boolean getGrauInstrucaoRequired() {
		return grauInstrucaoRequired;
	}

	public void setCorRequired(Boolean corRequired) {
		this.corRequired = corRequired;
	}

	public Boolean getCorRequired() {
		return corRequired;
	}

	public void setDddResidencialRequired(Boolean dddResidencialRequired) {
		this.dddResidencialRequired = dddResidencialRequired;
	}

	public Boolean getDddResidencialRequired() {
		return dddResidencialRequired;
	}

	public void setTelefoneResidencialPaiRequired(
			Boolean telefoneResidencialPaiRequired) {
		this.telefoneResidencialPaiRequired = telefoneResidencialPaiRequired;
	}

	public Boolean getTelefoneResidencialPaiRequired() {
		return telefoneResidencialPaiRequired;
	}

	public void setGerarProntuario(Boolean gerarProntuario) {
		this.gerarProntuario = gerarProntuario;
	}

	public Boolean getGerarProntuario() {
		return gerarProntuario;
	}

}
