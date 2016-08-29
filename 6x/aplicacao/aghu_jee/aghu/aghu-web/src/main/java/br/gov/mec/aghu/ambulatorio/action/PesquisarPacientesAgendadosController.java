package br.gov.mec.aghu.ambulatorio.action;

import static br.gov.mec.aghu.model.AipPacientes.VALOR_MAXIMO_PRONTUARIO;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.context.RequestContext;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.administracao.business.IAdministracaoFacade;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.ambulatorio.vo.ConsultaAmbulatorioVO;
import br.gov.mec.aghu.ambulatorio.vo.ConsultasGradeVO;
import br.gov.mec.aghu.ambulatorio.vo.DocumentosPacienteVO;
import br.gov.mec.aghu.ambulatorio.vo.GradeAgendamentoVO;
import br.gov.mec.aghu.ambulatorio.vo.LaudoSolicitacaoAutorizacaoProcedAmbVO;
import br.gov.mec.aghu.ambulatorio.vo.PesquisarConsultasPendentesVO;
import br.gov.mec.aghu.ambulatorio.vo.ProcedHospEspecialidadeVO;
import br.gov.mec.aghu.ambulatorio.vo.VAacSiglaUnfSalaVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.certificacaodigital.action.ListarPendenciasAssinaturaPaginatorController;
import br.gov.mec.aghu.certificacaodigital.business.ICertificacaoDigitalFacade;
import br.gov.mec.aghu.controlepaciente.action.RegistrosPacienteController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.commons.WebUtil;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioCaracEspecialidade;
import br.gov.mec.aghu.dominio.DominioSinalizacaoControleFrequencia;
import br.gov.mec.aghu.dominio.DominioSituacaoAtendimento;
import br.gov.mec.aghu.dominio.DominioSituacaoControle;
import br.gov.mec.aghu.dominio.DominioTurno;
import br.gov.mec.aghu.dominio.StatusPacienteAgendado;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.faturamento.vo.CidVO;
import br.gov.mec.aghu.faturamento.vo.ParametrosGeracaoLaudoOtorrinoVO;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.internacao.pesquisa.business.IPesquisaInternacaoFacade;
import br.gov.mec.aghu.internacao.vo.EspCrmVO;
import br.gov.mec.aghu.model.AacConsultaProcedHospitalar;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AacFormaAgendamento;
import br.gov.mec.aghu.model.AacFormaAgendamentoId;
import br.gov.mec.aghu.model.AacRetornos;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.AghEquipes;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghMicrocomputador;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatAutorizaApac;
import br.gov.mec.aghu.model.FatConvGrupoItemProced;
import br.gov.mec.aghu.model.FatLaudoPacApac;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.MamControles;
import br.gov.mec.aghu.model.MamSituacaoAtendimentos;
import br.gov.mec.aghu.model.RapPessoaTipoInformacoes;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.VAacSiglaUnfSala;
import br.gov.mec.aghu.model.VAipEnderecoPaciente;
import br.gov.mec.aghu.model.VFatAssociacaoProcedimento;
import br.gov.mec.aghu.paciente.action.PesquisaPacienteController;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.cadastro.action.CadastrarPacienteController;
import br.gov.mec.aghu.paciente.cadastro.business.ICadastroPacienteFacade;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

import com.itextpdf.text.DocumentException;

/**
 * Controller da tela de Pesquisar Consulta/Agenda
 *  
 *  @author cqsilva
 * 
 */
@SuppressWarnings({"PMD.ExcessiveClassLength", "PMD.NPathComplexity", "PMD.NcssTypeCount"})
public class PesquisarPacientesAgendadosController extends ActionReport implements ActionPaginator {

	private static final Integer VALUE_OF_MAX_RESULT = Integer.valueOf("100");
	private static final String CNS = "CNS";
	
	private static final Log LOG = LogFactory.getLog(PesquisarPacientesAgendadosController.class);
	
	private static final long serialVersionUID = -3744812806893136737L;
	private final Integer PERC_MAX=390;
	private final Integer PERC_MIN=100;
	private final Integer TAB_1=0, TAB_2=1, TAB_3=2, TAB_4=3, TAB_5=4, TAB_6=5;
	
	private static final String PAGE_LISTAR_PENDENCIAS_ASSINATURA = "certificacaodigital-listarPendenciasAssinatura";
	private static final String PAGE_PESQUISAR_PACIENTES_AGENDADOS = "ambulatorio-pesquisarPacientesAgendados";
	private static final String AACK_PRH_RN_V_APAC_DIARIA = "AACK_PRH_RN_V_APAC_DIARIA";
	private static final String AACK_AAA_RN_V_PROTESE_AUDITIVA = "AACK_AAA_RN_V_PROTESE_AUDITIVA";
	private static final String FATK_CAP_RN_V_CAP_ENCERRAMENTO = "FATK_CAP_RN_V_CAP_ENCERRAMENTO";
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@EJB
	private IAdministracaoFacade administracaoFacade;	
	
	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private IParametroFacade parametroFacade;

	@EJB
	private IInternacaoFacade internacaoFacade;

	@EJB
	private ICascaFacade cascaService;	
	
	@EJB
	private ICertificacaoDigitalFacade certificacaoDigitalFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private ICadastroPacienteFacade cadastroPacienteFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IPesquisaInternacaoFacade pesquisaInternacaoFacade;
	
	private List<AacConsultas> consultas;
	
	@Inject @Paginator
	private DynamicDataModel<ConsultaAmbulatorioVO> dataModel;

	// FILTRO
	private Date dtPesquisa;
	private VAacSiglaUnfSalaVO zona;
	private VAacSiglaUnfSala zonaSala;
	private List<VAacSiglaUnfSala> zonaSalaList;
	private DominioTurno turno;
	private AghEquipes equipe;
	private EspCrmVO espCrmVO;
	private AghEspecialidades especialidade;
	private RapServidores profissional;
	private Integer percPanel;
	private Boolean pesquisaAtiva;
	private String nomeMicrocomputador;
	
	// FILTRO MODAL DE RETORNO DA CONSULTA
	private AacRetornos retorno;
	
	private List<ConsultaAmbulatorioVO> pacientesAgendadosList;
	private List<ConsultaAmbulatorioVO> pacientesAguardandoList;
	private List<ConsultaAmbulatorioVO> pacientesAtendimentoList;
	private List<ConsultaAmbulatorioVO> pacientesAtendidosList;
	private List<PesquisarConsultasPendentesVO> pacientesPendentesList;
	private List<ConsultaAmbulatorioVO> pacientesAusentesList;
	
	//DIALOG LAUDO
	private Boolean imprimirControleFrequencia;
	private Boolean imprimirAutorizacao;
	
	private StreamedContent media;
	
	private Integer selectedTab;
	private String labelZona;
	private String labelSala;
	private String titleZona;
	private String titleSala;
	private Boolean continuaChegada;
	private String acaoAtualizaDadosPacienteAmb;
	
	// Atributos para a Modal dos procedimentos da consulta
	private ConsultaAmbulatorioVO consultaSelecionada;
	private ConsultaAmbulatorioVO consultaSelecionadaAba1;
	private ConsultaAmbulatorioVO consultaSelecionadaAba2;
	private ConsultaAmbulatorioVO consultaSelecionadaAba3;
	private ConsultaAmbulatorioVO consultaSelecionadaAba4;
	private PesquisarConsultasPendentesVO consultaPendenteSelecionadaAba5;
	private ProcedHospEspecialidadeVO procedimentoHospEspecialidadeVO;
	private AghCid cid;
	private Byte procedQuantidade;
	private AacConsultaProcedHospitalar procedimentoConsulta;
	private List<AacConsultaProcedHospitalar> listaProcedimentosHospConsulta;
	
	private ConsultaAmbulatorioVO consultaSelecionadaRetorno;
	private AacConsultas aacConsulta;
	//private AacConsultas aacConsulta = new AacConsultas();
	private AacConsultas consulta = new AacConsultas();
	
	private Boolean exibirModalInformarChaveSisreg = Boolean.FALSE;
	private Boolean executarMetodoChegou = Boolean.FALSE;
	private Boolean exibirMensagemInformarChaveSisreg = Boolean.FALSE;
	
	private Integer codigoPacienteTrocado;
	
	private static final Byte VALOR_PADRAO_PROCED_QUANTIDADE = 1;

	private String url_documento_jasper = "br/gov/mec/aghu/ambulatorio/report/controleFrequencia.jasper";

	private static final String OTORRINO = "O";
	private static final String PRE_TRANSPLANTE = "P";
	private static final String TRANSPLANTADO = "T";
	private static final String FOTOCOAGULACAO = "F";

	private String atualizarModal = "";

	//verifica se veio do botão 'imprimir Apac'
	Boolean imprimirApac = Boolean.FALSE;
	
	// manter posição do scroll
	private Integer posicaoScroll;
	
	private enum PesquisarPacientesAgendadosControllerExceptionCode {
		PRONTUARIO_VIRTUAL;
	}
	
	//certificacao digital
	private Integer pacCodigo;
	private String tipo;
	
	private String comeFrom;
	
	private Integer openToggle = 0;
	
	private Boolean carregarModal = true;
	
	private Boolean flagAba1 = false; 
	private Boolean flagModalImprimir = false;
	
	private Boolean flagSelecao = false;
	
	private List<DocumentosPacienteVO> listaDocumentosPaciente;
	
	@Inject
	private CadastrarPacienteController cadastrarPacienteController;

	@Inject
	private PesquisaPacienteController pesquisaPacienteController;

	@Inject
	private AtenderPacientesAgendadosController atenderPacientesAgendadosController;

	@Inject
	private RegistrosPacienteController registrosPacienteController;

	@Inject
	private ListarPendenciasAssinaturaPaginatorController listarPendenciasAssinaturaPaginatorController;

	private Boolean houveErroRelatorio;
	
	@Inject
	private PesquisaConsultasGradePaginatorController pesquisaConsultasGradePaginatorController;

	private String orderPropertyListaPacientes;

	private Boolean orderListaPacientesAsc = Boolean.FALSE;
	
	private Integer maxResultsListapacientes = VALUE_OF_MAX_RESULT;
	
	private Boolean chamouPesquisar = Boolean.FALSE;
	
	private Boolean flagCodigoCentral = Boolean.FALSE;
	
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
		limparPesquisa();
		carregarParametros();
		try {
			nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
			if(this.verificaValorTurno() == true){
				turno  = ambulatorioFacade.defineTurnoAtual();
			}
		} catch (UnknownHostException e) {
			LOG.error("Erro ao buscar nome do microcomputador", e);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		this.carregarParametros();
		selectedTab=TAB_1;
		continuaChegada=false;	
		List<VAacSiglaUnfSalaVO> pesquisarZonas = ambulatorioFacade.pesquisarZonas("");
		
		if (pesquisarZonas.size() == 1) {
			this.zona = pesquisarZonas.get(0);
		}
	}
	
	

	/**
	+	 * Método que obriga informar o número do cartão SUS no cadastro do paciente
	+	 * @throws AGHUNegocioException 
	+	 */
	public boolean verificaValorTurno() {
		boolean retorno = false;
		
		try {
			AghParametros aghParamFrn = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_TURNO_CONSULTA);
			String paramValorTurno = aghParamFrn.getVlrTexto();
			retorno = "S".equalsIgnoreCase(paramValorTurno);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		
		return retorno;
	}
	
	/**
	 * Método executado ao iniciar a controller
	 */
	public void iniciar() {
		atualizarModal = "";
		houveErroRelatorio = false;

		marcarChegouPaciente();
		
		if (pesquisaAtiva){
			pesquisar();
		}
		
		if (this.posicaoScroll != null){
			RequestContext.getCurrentInstance().execute("setScrollPosition("+ this.posicaoScroll + ");");
			this.posicaoScroll = null;
		}
	}
	
	public void marcarChegouPaciente(){
		if(acaoAtualizaDadosPacienteAmb == null){
			acaoAtualizaDadosPacienteAmb = "";
		}
		
		if(acaoAtualizaDadosPacienteAmb.equals("cancelar") && isParametroBotaoCancelarAoAlterarDadosPaciente()){
			continuaChegada = false;
			apresentarMsgNegocio(Severity.WARN, "OPERACAO_CANCELADA", this.labelZona);
			acaoAtualizaDadosPacienteAmb = "";
		}

		
		if (continuaChegada){
			finalizaChegada();
		}
	
	}
	
	public boolean isParametroBotaoCancelarAoAlterarDadosPaciente(){
		boolean parametro = false;
		try{
			AghParametros parametroBotaoCancelar = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_FLUXO_BOTAO_CANCELAR_AMBULATORIO_ASSISTENCIAL);
			String valorParametroBotaoCancelar = parametroBotaoCancelar.getVlrTexto();
			return "S".equalsIgnoreCase(valorParametroBotaoCancelar);
		}catch(ApplicationBusinessException e){
			apresentarExcecaoNegocio(e);
		}
		return parametro;
	}

	private void carregarParametros() {
		try {
			this.labelZona = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_LABEL_ZONA).getVlrTexto();
			this.labelSala = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_LABEL_SALA).getVlrTexto();

			if (this.labelZona==null){
				this.labelZona="Zona";
			}
			if(this.labelSala == null) {
				this.labelSala = "Sala";
			}
			
			this.titleZona = WebUtil.initLocalizedMessage("TITLE_ZONA_GRADE_AGENDAMENTO", null, this.labelZona);
			this.titleSala = WebUtil.initLocalizedMessage("TITLE_PESQUISAR_PACIENTES_AGENDADOS_SALA", null, this.labelSala);
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	/**
	 * Método executado ao clicar no botão limpar
	 */	
	public void limparPesquisa() {
		dtPesquisa=new Date();
		profissional=null;
		zona = null;
		equipe = null;
		espCrmVO  = null;
		zonaSalaList=new ArrayList<VAacSiglaUnfSala>();
		zonaSala=null;
		especialidade=null;
		profissional=null;
		percPanel=PERC_MIN;
		pesquisaAtiva=false;
		turno=null;
		imprimirControleFrequencia = false;
		imprimirAutorizacao = false;
		selectedTab=TAB_1;
		pacientesAgendadosList=new ArrayList<ConsultaAmbulatorioVO>();
		pacientesAguardandoList=new ArrayList<ConsultaAmbulatorioVO>();
		pacientesAtendidosList=new ArrayList<ConsultaAmbulatorioVO>();
		pacientesAtendimentoList=new ArrayList<ConsultaAmbulatorioVO>();
		pacientesPendentesList=new ArrayList<PesquisarConsultasPendentesVO>();
		pacientesAusentesList=new ArrayList<ConsultaAmbulatorioVO>();
		limparAtributosModalProcedimentos();
		this.carregarParametros();
	}

	
	public void refazerPesquisa() {
		pacientesAgendadosList=new ArrayList<ConsultaAmbulatorioVO>();
		pacientesAguardandoList=new ArrayList<ConsultaAmbulatorioVO>();
		pacientesAtendidosList=new ArrayList<ConsultaAmbulatorioVO>();
		pacientesAtendimentoList=new ArrayList<ConsultaAmbulatorioVO>();
		pacientesPendentesList=new ArrayList<PesquisarConsultasPendentesVO>();
		pacientesAusentesList=new ArrayList<ConsultaAmbulatorioVO>();
		pesquisaAtiva=false;
		percPanel=PERC_MIN;
		
		limparAtributosModalProcedimentos();
	}
	
	
	public void pesquisar() {
		if (zona==null){
			apresentarMsgNegocio(Severity.ERROR, "ERRO_ZONA_OBRIGATORIA", this.labelZona);
			return;
		}
		try {
			pesquisaAtiva=
				ambulatorioFacade.existeConsultaPacientesAgendados(
						dtPesquisa, zonaSalaList, zonaSala, 
						ambulatorioFacade.definePeriodoTurno(turno), equipe, espCrmVO, 
						especialidade, profissional);
			
			if (pesquisaAtiva){	
				if (selectedTab.equals(TAB_1)){
					consultaSelecionada=null;
					if (chamouPesquisar) {
						maxResultsListapacientes = VALUE_OF_MAX_RESULT;
					}
					this.dataModel.setDefaultMaxRow(maxResultsListapacientes);
					this.dataModel.reiniciarPaginator();
				}else if (selectedTab.equals(TAB_2)){
					refreshListaAba2();
				}else if (selectedTab.equals(TAB_3)){
					refreshListaAba3();
				}else if (selectedTab.equals(TAB_4)){
					refreshListaAba4();
				}else if (selectedTab.equals(TAB_5)){
					refreshListaAba5();
				}else if (selectedTab.equals(TAB_6)){
					refreshListaAba6();
				}	
				percPanel=PERC_MAX;
			}else{
				pacientesAgendadosList=new ArrayList<ConsultaAmbulatorioVO>();
				pacientesAguardandoList=new ArrayList<ConsultaAmbulatorioVO>();
				pacientesAtendidosList=new ArrayList<ConsultaAmbulatorioVO>();
				pacientesAtendimentoList=new ArrayList<ConsultaAmbulatorioVO>();	
				pacientesPendentesList=new ArrayList<PesquisarConsultasPendentesVO>();
				pacientesAusentesList=new ArrayList<ConsultaAmbulatorioVO>();
				apresentarMsgNegocio(Severity.WARN, "MSG_NENHUM_PACIENTE_ENCONTRADO");
			}
		    
			consultaSelecionada=null;	

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}		
	}
	
	private Short obterZonaUnfSeq() {
		if (zona != null) {
			return zona.getUnfSeq();
		} else {
			return null;
		}
	}
	
	private List<Byte> obterListaZonaSala() {
		// Lista de salas da zona
		List<Byte> salaList = new ArrayList<Byte>();
		for (VAacSiglaUnfSala vSiglaSala : zonaSalaList) {
			salaList.add(vSiglaSala.getId().getSala());
		}
		return salaList;
	}
	
	
	@Override
	public List<ConsultaAmbulatorioVO> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		try {

			aplicarOrdenacoes(orderProperty, asc, maxResult);
			
			pacientesAgendadosList=this.ambulatorioFacade.consultaPacientesAgendados(firstResult, maxResultsListapacientes, orderPropertyListaPacientes, orderListaPacientesAsc, dtPesquisa, obterZonaUnfSeq(), 
					obterListaZonaSala(), zonaSala, ambulatorioFacade.definePeriodoTurno(turno), equipe, espCrmVO, 
					especialidade, profissional, StatusPacienteAgendado.AGENDADO);
			for (ConsultaAmbulatorioVO consulta: pacientesAgendadosList){
				if (consulta.getPacienteCodigo() != null){
					consulta.setPacienteNotifGMR(pesquisaInternacaoFacade.pacienteNotifGMR(consulta.getPacienteCodigo()));
				}
			}
			imprimirApac = Boolean.FALSE;
			chamouPesquisar = Boolean.FALSE;
			this.cadastrarPacienteController.setOrderPropertyListaPacientes(orderPropertyListaPacientes);
			this.cadastrarPacienteController.setOrderListaPacientesAsc(Boolean.valueOf(orderListaPacientesAsc));
			this.cadastrarPacienteController.setMaxResultsListaPacientes(maxResultsListapacientes);
			return pacientesAgendadosList;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}


	private void aplicarOrdenacoes(String orderProperty, boolean asc, Integer maxResult) {
		if (!chamouPesquisar) {
			if (StringUtils.isNotEmpty(orderProperty)) {
				orderPropertyListaPacientes = orderProperty;
				orderListaPacientesAsc = asc;
			} else if (StringUtils.isNotEmpty(cadastrarPacienteController.getOrderPropertyListaPacientes())) {
				orderPropertyListaPacientes = cadastrarPacienteController.getOrderPropertyListaPacientes();
				if (cadastrarPacienteController.getOrderListaPacientesAsc() != null) {
					orderListaPacientesAsc = cadastrarPacienteController.getOrderListaPacientesAsc();
				} else {
					orderListaPacientesAsc = Boolean.FALSE;
				}
			} else {
				orderListaPacientesAsc = Boolean.FALSE;
			}
			aplicarMaxResults(maxResult);
		} else {
			orderPropertyListaPacientes = StringUtils.EMPTY;
			orderListaPacientesAsc = Boolean.FALSE;
			maxResultsListapacientes = VALUE_OF_MAX_RESULT;
		}
	}


	private void aplicarMaxResults(Integer maxResult) {
		if (maxResult != null) {
			maxResultsListapacientes = maxResult;
		} else if (cadastrarPacienteController.getMaxResultsListaPacientes() != null) {
			maxResultsListapacientes = cadastrarPacienteController.getMaxResultsListaPacientes();
		} else {
			maxResultsListapacientes = VALUE_OF_MAX_RESULT;
		}
	}
	
	@Override
	public Long recuperarCount() {
		try {
			return this.ambulatorioFacade.consultaPacientesAgendadosCount(dtPesquisa, obterZonaUnfSeq(), 
					obterListaZonaSala(), zonaSala, ambulatorioFacade.definePeriodoTurno(turno), equipe, espCrmVO, 
					especialidade, profissional, StatusPacienteAgendado.AGENDADO);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}
	
	
	public void refreshListaAba1() throws ApplicationBusinessException{
		if (pesquisaAtiva){	
			consultaSelecionada=null;
			pacientesAgendadosList=ambulatorioFacade.consultaPacientesAgendados(null, null, "", true, dtPesquisa, obterZonaUnfSeq(), 
					obterListaZonaSala(), zonaSala, ambulatorioFacade.definePeriodoTurno(turno), equipe, espCrmVO, 
					especialidade, profissional, StatusPacienteAgendado.AGENDADO);
			for (ConsultaAmbulatorioVO consulta: pacientesAgendadosList){
				if (consulta.getPacienteCodigo() != null){
					consulta.setPacienteNotifGMR(pesquisaInternacaoFacade.pacienteNotifGMR(consulta.getPacienteCodigo()));
				}
			}
			imprimirApac = Boolean.FALSE;
		}	
	}

	public void refreshListaAba2() throws ApplicationBusinessException{
		if (pesquisaAtiva){		
			pacientesAguardandoList=ambulatorioFacade.consultaPacientesAgendados(null, null, "", true, dtPesquisa, obterZonaUnfSeq(), 
					obterListaZonaSala(), zonaSala, ambulatorioFacade.definePeriodoTurno(turno), equipe, espCrmVO, 
					especialidade, profissional, StatusPacienteAgendado.AGUARDANDO);
			for (ConsultaAmbulatorioVO consulta: pacientesAguardandoList){
				if (consulta.getPacienteCodigo() != null){
					consulta.setPacienteNotifGMR(pesquisaInternacaoFacade.pacienteNotifGMR(consulta.getPacienteCodigo()));
				}
			}
		}	
	}

	public void refreshListaAba3() throws ApplicationBusinessException{
		if (pesquisaAtiva){
			pacientesAtendimentoList=ambulatorioFacade.consultaPacientesAgendados(null, null, "", true, dtPesquisa, obterZonaUnfSeq(), 
					obterListaZonaSala(), zonaSala, ambulatorioFacade.definePeriodoTurno(turno), equipe, espCrmVO, 
					especialidade, profissional, StatusPacienteAgendado.EM_ATENDIMENTO);
			for (ConsultaAmbulatorioVO consulta: pacientesAtendimentoList){
				if (consulta.getPacienteCodigo() != null){
					consulta.setPacienteNotifGMR(pesquisaInternacaoFacade.pacienteNotifGMR(consulta.getPacienteCodigo()));
				}
			}
		}	
	}
	
	public void refreshListaAba4() throws ApplicationBusinessException{
		if (pesquisaAtiva){
			consultaSelecionadaAba4=null;
			pacientesAtendidosList=ambulatorioFacade.consultaPacientesAgendados(null, null, "", true, dtPesquisa, obterZonaUnfSeq(), 
					obterListaZonaSala(), zonaSala,	ambulatorioFacade.definePeriodoTurno(turno), equipe, espCrmVO, 
					especialidade, profissional, StatusPacienteAgendado.ATENDIDO);
			for (ConsultaAmbulatorioVO consulta: pacientesAtendidosList){
				if (consulta.getPacienteCodigo() != null){
					consulta.setPacienteNotifGMR(pesquisaInternacaoFacade.pacienteNotifGMR(consulta.getPacienteCodigo()));
				}
			}
		}	
	}

	public void refreshListaAba5() throws ApplicationBusinessException {

		try {
			if (pesquisaAtiva){
				RapServidores usuario = servidorLogadoFacade.obterServidorLogado();
				
				AghParametros paramSeqSituacaoAtendimento = parametroFacade.obterAghParametro(AghuParametrosEnum.P_SEQ_SIT_EM_ATEND);
				
				AghParametros paramDiasReabrirPendente = parametroFacade.obterAghParametro(AghuParametrosEnum.P_DIAS_REABRIR_PENDENTE);
				
				pacientesPendentesList = ambulatorioFacade.pesquisarConsultasPendentes(usuario, dtPesquisa, especialidade, equipe, zona, zonaSala, profissional,
						ambulatorioFacade.definePeriodoTurno(turno), paramSeqSituacaoAtendimento.getVlrNumerico().shortValue(),
						paramDiasReabrirPendente.getVlrNumerico().intValue());
			}
				
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void refreshListaAba6() throws ApplicationBusinessException {
		if(pesquisaAtiva){			
			pacientesAusentesList=ambulatorioFacade.consultaAbaPacientesAusentes(dtPesquisa, obterZonaUnfSeq() , zonaSala, especialidade, 
					equipe, profissional);			
		}
	}

	
	/**
	 * Método para Suggestion de Equipes
	 */	
	public List<AghEquipes> obterEquipe(String parametro) {
		return aghuFacade.getListaEquipes((String) parametro);
	}

	
	/**
	 * Método para Suggestion Box de Zona
	 */	
	public List<VAacSiglaUnfSalaVO> obterZona(String objPesquisa) throws BaseException  {
		if (objPesquisa!=null){
			objPesquisa = objPesquisa.trim();
		}
		return ambulatorioFacade.pesquisarTodasZonas(objPesquisa);
	}
	
	public void obterZonaSala()  {	
		zonaSalaList = new ArrayList<VAacSiglaUnfSala>();
		if (zona!=null) {
			List<AghUnidadesFuncionais> undFuncionais = aghuFacade.listarUnidadeFuncionalPorFuncionalSala(zona.getDescricao());	
			if (!undFuncionais.isEmpty()){
				zonaSalaList =  this.aghuFacade.pesquisarSalasUnidadesFuncionais(undFuncionais,null);
			}
		}else{
			zonaSala=null;
		}
	}	
	
	
	public void colapsePanel(){
		if (PERC_MIN.equals(percPanel)){
			percPanel=PERC_MAX;
		}else{
			refazerPesquisa();
		}		
	}
	
	public void gerarImpressoes(ConsultaAmbulatorioVO consulta) {
//		consultaSelecionada = consulta;
		consultaSelecionadaAba1 = consulta;
		if (OTORRINO.equalsIgnoreCase(consulta.getTipoSinalizacao())) {
			setImprimirControleFrequencia(true);
			setImprimirAutorizacao(true);
		} else if (TRANSPLANTADO.equals(consulta.getTipoSinalizacao()) 
				&& consulta.getControleFrequencia().equals(DominioSinalizacaoControleFrequencia.TR)) {
			setImprimirAutorizacao(false);
			setImprimirControleFrequencia(true);
		} else {
			setImprimirControleFrequencia(true);
			setImprimirAutorizacao(true);
		}
	}
	
	public void imprimirApac(boolean imprimirApac){
		limparCheckModalImpressaoAPAC();
		this.imprimirApac = imprimirApac;
	}

	private void limparCheckModalImpressaoAPAC() {
			imprimirControleFrequencia = Boolean.FALSE;
			imprimirAutorizacao = Boolean.FALSE;
	}
	
	//foi criado um parametro no sistema para imprimir apac direto, pois o modulo faturamento ainda nao foi implementado no aghu, assim que o mesmo for implementado
	//terá que reavaliar a regra para imprimir e reimprimir ( 'I' e 'C' )
	public boolean parametrizaImpressaoDiretaAPAC() {
		try {
			AghParametros impressaoDireta = parametroFacade
					.buscarAghParametro(AghuParametrosEnum.P_AGHU_IMPRIMIR_APAC_DIRETO_AMBULATORIO);
			String imprimirDireto = impressaoDireta.getVlrTexto();

			if (imprimirDireto.equalsIgnoreCase("S")) {
				return Boolean.TRUE;
			}
		} catch (ApplicationBusinessException e) {
			LOG.error(e.getMessage(), e);
		}
		return Boolean.FALSE;
	}
	
	public void imprimir() throws ApplicationBusinessException, JRException, IOException, DocumentException {
		int pacientesImpressos = 0;
		try{
			houveErroRelatorio = false;
			if (imprimirApac && (imprimirControleFrequencia || imprimirAutorizacao) ) {
				Boolean maisDeQuinze = Boolean.FALSE;
				int maximoImpressao = parametroFacade.buscarValorInteiro(AghuParametrosEnum.P_AGHU_LIMITE_IMP_AMBU);
				for (int i = 0; i < pacientesAgendadosList.size(); i++) {
					if (imprimirControleFrequencia != null && imprimirControleFrequencia) {
						if (pacientesImpressos < maximoImpressao) {
							if(pacientesAgendadosList.get(i).getIndicaImagem() != null){
								if(pacientesAgendadosList.get(i).getIndicaImagem().equalsIgnoreCase("C") || parametrizaImpressaoDiretaAPAC()){
									consultaSelecionadaAba1 = pacientesAgendadosList.get(i); 
									url_documento_jasper = "br/gov/mec/aghu/ambulatorio/report/controleFrequencia.jasper";
									printRelatorio(pacientesAgendadosList.get(i));
									msgImpressaoSucesso();
								}
							}
						} else {
							maisDeQuinze = true;
							break;
						}
					}
				
					if (imprimirAutorizacao != null && imprimirAutorizacao) {
						if (pacientesImpressos < maximoImpressao) {
							if(pacientesAgendadosList.get(i).getIndicaImagem() != null){
								if(pacientesAgendadosList.get(i).getIndicaImagem().equalsIgnoreCase("C") || parametrizaImpressaoDiretaAPAC()){
									if ( consultaSelecionadaAba1 == null){
										consultaSelecionadaAba1 = pacientesAgendadosList.get(i);
									}
									consultaSelecionadaAba1.setProntuario(pacientesAgendadosList.get(i).getProntuario());
									url_documento_jasper = "br/gov/mec/aghu/ambulatorio/report/laudoSolicitacaoAutorizacaoProcedimentoAmbulatorial.jasper";
									printRelatorio(pacientesAgendadosList.get(i));
									msgImpressaoSucesso();
								}
							}
						} else {
							maisDeQuinze = true;
							break;
						}
					}
					
					if(maisDeQuinze){
						apresentarMsgNegocio(Severity.WARN, "O filtro excedeu o número máximo de documentos a serem impressos.");
					}
					
					if(pacientesAgendadosList.get(i).getIndicaImagem() != null){
						if(pacientesAgendadosList.get(i).getIndicaImagem().equalsIgnoreCase("C") || parametrizaImpressaoDiretaAPAC()){
							pacientesImpressos++;
						}
					}
					
				}				
				
				refreshListaAba1();
				imprimirApac = Boolean.FALSE;
			} else {
				if (!imprimirAutorizacao && !imprimirControleFrequencia) {
					apresentarMsgNegocio(Severity.WARN, "NECESSÁRIO SELECIONAR UMA OPÇÃO.");
				} else {
					if (imprimirAutorizacao) {
						url_documento_jasper = "br/gov/mec/aghu/ambulatorio/report/laudoSolicitacaoAutorizacaoProcedimentoAmbulatorial.jasper";
						printRelatorio(consultaSelecionadaAba1);
						msgImpressaoSucesso();
					}
					
					if (imprimirControleFrequencia) {
						url_documento_jasper = "br/gov/mec/aghu/ambulatorio/report/controleFrequencia.jasper";
						printRelatorio(consultaSelecionadaAba1);
						msgImpressaoSucesso();
					}
					
					refreshListaAba1();
					atualizarModal = ":formPesquisa";
					closeDialog("modalImprimirLaudoAtendimentoApacWG");
					pesquisar();
				}
			}
		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
		}
	}

	public void printRelatorio(ConsultaAmbulatorioVO consultaAmbulatorioVO) throws SistemaImpressaoException, ApplicationBusinessException, UnknownHostException, JRException {
		consultaSelecionada = consultaAmbulatorioVO;
		this.directPrint();
		
		//insere registro no controle de impressão do laudo
		ambulatorioFacade.persistirControleImpressaoLaudo(consultaSelecionada);
	}
	
	public void msgImpressaoSucesso(){
		apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
	}
	
	public void directPrint() throws ApplicationBusinessException, SistemaImpressaoException {
		try {
			DocumentoJasper documento = gerarDocumento();
			this.sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
			throw e;
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			houveErroRelatorio = true;
			apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}
	
	public String getDescricaoFiltro(){
		StringBuilder sb = new StringBuilder();		
		if (percPanel.equals(PERC_MAX) && pesquisaAtiva){			
			sb.append("|Data: ")
			.append(DateUtil.dataToString(dtPesquisa, "dd/MM/yyyy") ).append( ' ');
			if (zona!=null) {
				//sb.append("|Zona: ");
				sb.append('|').append(this.labelZona).append(": ")
				.append(zona.getSigla() ).append( ' ');
			}	
			if (zonaSala!=null) {
				//sb.append("|Sala: ");
				sb.append('|').append(this.labelSala).append(": ")
				.append(zonaSala.getId().getSala() ).append( ' ');
			}	
			if (turno!=null) {
				sb.append("|Turno: ")
				.append(turno.getDescricao());
			}
			if (especialidade!=null){
				sb.append("|Espec: " ).append( especialidade.getNomeEspecialidade());
			}	
			if (equipe!=null){		
				sb.append("|Equipe: " ).append( equipe.getNome());
			}
			if (profissional!=null){		
				sb.append("|Profis: " ).append( buscaCons(profissional.getId().getMatricula(), profissional.getId().getVinCodigo()));
			}	
		}
		return sb.toString();
	}	
	
	
	public void localizaPaciente(ConsultaAmbulatorioVO consulta){
		consultaSelecionada=consulta;
		MamSituacaoAtendimentos situacao = ambulatorioFacade.obterSituacaoAtendimentos(consultaSelecionada.getControleSituacaoAtendimentoSeq());
		
		try {		
			if (situacao==null || situacao.getAgendado()){
				return;
			}else if (situacao.getAguardando()){
				selectedTab=TAB_2;
				refreshListaAba2();
			}else if (situacao.getAtendConcluido()){
				selectedTab=TAB_4;
				refreshListaAba4();
			}else if (situacao.getAtendPend()){
				selectedTab=TAB_5;
				refreshListaAba5();
			}else{
				selectedTab=TAB_3;
				refreshListaAba3();
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}		
	}
	
	public List<AghEspecialidades> obterEspecialidade(String parametro) {
		return aghuFacade.getListaTodasEspecialidades((String) parametro);
	}	

	
	public List<RapServidores> obterProfissionais(String parametro) {
		return registroColaboradorFacade.listarServidoresComPessoaFisicaPorNome((String) parametro);
	}	
	
	public List<ProcedHospEspecialidadeVO> buscarProcedimentos(String parametro) {
		if(consultaSelecionada != null && consultaSelecionada.getGradeEspSeq()!=null){
			AghEspecialidades especialidade = aghuFacade.obterEspecialidade(consultaSelecionada.getGradeEspSeq());
			return  this.returnSGWithCount(this.ambulatorioFacade.listarProcedimentosEspecialidadesComGenericas(parametro, 
					especialidade),buscarProcedimentosCount(parametro));	
		} else {
			return null;
		}
	}
	
	public Integer buscarProcedimentosCount(String parametro) {
		Integer count = 0;
		if(consultaSelecionada != null &&consultaSelecionada.getGradeEspSeq()!=null){
			AghEspecialidades especialidade = aghuFacade.obterEspecialidade(consultaSelecionada.getGradeEspSeq());
			count = this.ambulatorioFacade.listarProcedimentosEspecialidadesComGenericasCount(parametro, especialidade);
		} 
		return count;
	}
	
	public List<AghCid> buscarCid(String parametro) {
		if (procedimentoHospEspecialidadeVO != null) {
			return  this.returnSGWithCount(aghuFacade.listarCidPorProcedimento(parametro, procedimentoHospEspecialidadeVO.getPhiSeq()),buscarCidCount(parametro));
		} else {
			return new ArrayList<AghCid>();
		}
	}
	
	public Long buscarCidCount(String parametro) {
		Long count = 0L;
		if (procedimentoHospEspecialidadeVO != null) {
			count = aghuFacade.listarCidPorProcedimentoCount(parametro, procedimentoHospEspecialidadeVO.getPhiSeq());
		}
		return count;
	}
	
	/**
	 * Verifica se o procedimento selecionado possui algum CID associado.
	 * Caso possua, a suggestion de CIDs é habilitada.
	 */
	public Boolean getProcedimentoPossuiCid() {
		Long count = 0L;
		if (procedimentoHospEspecialidadeVO != null) {
			count = aghuFacade.listarCidPorProcedimentoCount(null, procedimentoHospEspecialidadeVO.getPhiSeq());
		} else {
			cid = null; // Limpa seleção do CID
		}
		if (count > 0) {
			return false;
		}
		else {
			return true;
		}
	}
	
	/**
	 * Adiciona um procedimento à consulta selecionada
	 * @throws UnknownHostException 
	 */
//	@Restrict("#{s:hasPermission('inserirProcedimentosConsulta','adicionar')}")
	public void adicionarProcedimentoConsulta() {
		try {
			if (procedimentoHospEspecialidadeVO == null || procedQuantidade == null) { 
				if (procedimentoHospEspecialidadeVO == null) {
					apresentarMsgNegocio(Severity.ERROR, "MSG_PROCEDIMENTO_CONSULTA_NAO_INFORMADO");
				}
				
				if (procedQuantidade == null) {
					apresentarMsgNegocio(Severity.ERROR, "MSG_QUANTIDADE_PROCEDIMENTO_CONSULTA_NAO_INFORMADA");
				}
			}
			else {

				ambulatorioFacade.adicionarProcedimentoConsulta(
						consultaVOToAacConsulta(consultaSelecionada), procedimentoConsulta, cid,
						procedimentoHospEspecialidadeVO.getPhiSeq(), 
						consultaSelecionada.getGradeEspSeq(),
						procedQuantidade, listaProcedimentosHospConsulta, nomeMicrocomputador, new Date(),
						obterVariavelContextoProcedimentoConsulta(AACK_PRH_RN_V_APAC_DIARIA), 
						obterVariavelContextoProcedimentoConsulta(AACK_AAA_RN_V_PROTESE_AUDITIVA), 
						obterVariavelContextoProcedimentoConsulta(FATK_CAP_RN_V_CAP_ENCERRAMENTO));
				
				// Após adicionar, carrega a lista de procedimentos atualizada
				carregarAtributosModalProcedimentos();		
				
				//-- Milena 05/2005
				//-- verificar se os aparelhos auditivos lançados são compatíveis com as
				//-- solicitações de compra
				//-- Milena 09/2008 - exceto para os aparelhos de doação da receita federal. --
				//  --IF aacc_ver_prot_audit(:new.con_numero)= 'S--
				//   --AGHP_ENVIA_EMAIL('T',1,'regra phi_bri '||aack_aaa_rn.v_protese_auditiva
				//  --     ,'MPONS@HCPA.UFRGS.BR','correio;');--	
				if (Boolean.TRUE.equals(obterVariavelContextoProcedimentoConsulta(AACK_AAA_RN_V_PROTESE_AUDITIVA))) {
					FacesContext.getCurrentInstance().getExternalContext().getApplicationMap().put(AACK_AAA_RN_V_PROTESE_AUDITIVA, Boolean.FALSE);
				}
			}
		}
		catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * Resgata as variáveis do contexto para a gravação de procedimentos
	 * @param variavelContexto
	 * @return
	 */
	private Boolean obterVariavelContextoProcedimentoConsulta(String variavelContexto) {
		return Boolean.class.cast(FacesContext.getCurrentInstance().getExternalContext().getApplicationMap().get(variavelContexto));
	}
	
	/**
	 * Remove um determinado procedimento da consulta 
	 * 
	 * @param procedimento
	 */
	public void removerProcedimentoConsulta(AacConsultaProcedHospitalar procedimento) {
		try {
			ambulatorioFacade.removerProcedimentoConsulta(procedimento, nomeMicrocomputador, new Date());
			listaProcedimentosHospConsulta = ambulatorioFacade.buscarConsultaProcedHospPorNumeroConsulta(consultaSelecionada.getNumero());
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	
	private AacConsultas consultaVOToAacConsulta(ConsultaAmbulatorioVO vo){
		return consultaVOToAacConsulta(vo, false);
	}
	
	private AacConsultas consultaVOToAacConsulta(ConsultaAmbulatorioVO vo, boolean refresh){
		if (refresh || aacConsulta==null || 
				(aacConsulta.getNumero() != null && 
				!aacConsulta.getNumero().equals(consultaSelecionada.getNumero() != null ? 
						consultaSelecionada.getNumero() : null))){
			aacConsulta=ambulatorioFacade.obterAacConsulta(vo.getNumero());
		}
		return aacConsulta;
	}	
	

// [ATENDER]
	
	public void atender(){
		atender(consultaSelecionada);
	}
	
	public void atender(ConsultaAmbulatorioVO consultaVO){
		consultaSelecionada=consultaVO;
		boolean redirecionarPaginaAtenderPacientes = true;
		boolean reabrir=false;
		MamSituacaoAtendimentos situacao = ambulatorioFacade.obterSituacaoAtendimentos(consultaVO.getControleSituacaoAtendimentoSeq());
		
		try{
			if ((selectedTab.equals(TAB_2) && !situacao.getAguardando()) || (selectedTab.equals(TAB_3) &&
						(situacao.getAguardando() || situacao.getAtendConcluido()))
				|| (selectedTab.equals(TAB_4) && !situacao.getAtendConcluido())) {
				apresentarMsgNegocio(Severity.WARN, "MSG_CONSULTA_ATUALIZADA_TERMINAL");				
				consultaVO.setAtender(!consultaVO.getAtender());
				return;
			} else if (!situacao.getAtendConcluido() && !situacao.getAtendPend() && !situacao.getPacAtend()){
				ambulatorioFacade.atualizarDataInicioAtendimento(consultaVO.getNumero(), new Date(), nomeMicrocomputador);
			}else{
				reabrir=true;
			}
			
			ambulatorioFacade.validaRegrasAtendimento(consultaVOToAacConsulta(consultaVO), false, true, nomeMicrocomputador);
			if (consultaVO.getControleSituacao()!=null ) {
				defineSituacaoAtendimentoEmAtendimento(consultaVOToAacConsulta(consultaVO));
				if (!DominioSituacaoControle.U.equals(consultaVO.getControleSituacao())){
					ambulatorioFacade.atualizaControleAguardandoLivre(consultaVO.getNumero(), new Date(), nomeMicrocomputador);
				}else{
					ambulatorioFacade.atualizaControleAguardandoUso(consultaVO.getNumero(), new Date());
				}
			}	
			consultaSelecionada=consultaVO;
		}catch(BaseException e){
			if (!reabrir){
				consultaVO.setAtender(!consultaVO.getAtender());
			}
			redirecionarPaginaAtenderPacientes = false;
			apresentarExcecaoNegocio(e);
		}catch (Exception e) {
			LOG.error("Exceção capturada: ", e);
			if (!reabrir){
				consultaVO.setAtender(!consultaVO.getAtender());
			}
			redirecionarPaginaAtenderPacientes = false;
			apresentarMsgNegocio(Severity.ERROR, "ERRO_ATENDER");
		}
		
		if(redirecionarPaginaAtenderPacientes){
			this.atenderPacientesAgendadosController.setNumeroConsulta(consultaVO.getNumero());
			this.atenderPacientesAgendadosController.setComeFrom(PAGE_PESQUISAR_PACIENTES_AGENDADOS);
			this.atenderPacientesAgendadosController.setSelectedTab(0);
			this.redirecionarPaginaPorAjax("/pages/ambulatorio/pacientesagendados/atenderPacientesAgendados.xhtml");
		}
	}	
	
	private void defineSituacaoAtendimentoEmAtendimento(AacConsultas consulta) throws BaseException {
		AacRetornos retornoEmAtendimento = this.getAmbulatorioFacade().obterRetorno(DominioSituacaoAtendimento.EM_ATENDIMENTO.getCodigo());
		consulta.setRetorno(retornoEmAtendimento);
		consulta.setRetSeq(DominioSituacaoAtendimento.EM_ATENDIMENTO.getCodigo());
		ambulatorioFacade.atualizarConsultaRetorno(consulta);
		
	}
	
	private RapServidores recuperarServidorLogado() throws ApplicationBusinessException {
		return registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado(), new Date());
	}
	
	public void prepararReabrirPendenteEmtatendimento(PesquisarConsultasPendentesVO consultaPendenteVO){
		setConsultaPendenteSelecionadaAba5(consultaPendenteVO);
	}
	
	public void prepararReabrirPendente(PesquisarConsultasPendentesVO consultaPendenteVO){
		setConsultaPendenteSelecionadaAba5(consultaPendenteVO);
		reabrirPendente();
	}
	
	public void reabrirPendente(){
		boolean redirecionarPaginaAtenderPacientes = false;
		if(consultaPendenteSelecionadaAba5.isReaberto()){//testa se o chekbox esta marcado
			try {
				boolean reabrir = ambulatorioFacade.reabrirConsulta(consultaPendenteSelecionadaAba5,nomeMicrocomputador);
				if(reabrir){
					ambulatorioFacade.validaRegrasAtendimento(ambulatorioFacade.obterAacConsulta(consultaPendenteSelecionadaAba5.getNumero()), false, true, nomeMicrocomputador);
					ambulatorioFacade.chamaPortal(consultaPendenteSelecionadaAba5, nomeMicrocomputador);
					redirecionarPaginaAtenderPacientes=true;
				}
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
				redirecionarPaginaAtenderPacientes=false;
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
			}
			if(redirecionarPaginaAtenderPacientes){
				this.atenderPacientesAgendadosController.setNumeroConsulta(consultaPendenteSelecionadaAba5.getNumero());
				this.atenderPacientesAgendadosController.setComeFrom("ambulatorio-pesquisarPacientesAgendados");
				this.atenderPacientesAgendadosController.setSelectedTab(0);
				this.redirecionarPaginaPorAjax("/pages/ambulatorio/pacientesagendados/atenderPacientesAgendados.xhtml");
			}
		}
		
	}
	
	public void verificarChegouSisreg(ConsultaAmbulatorioVO consulta) {
		this.setExecutarMetodoChegou(true);
		this.setConsultaSelecionada(consulta);
		this.setConsulta(ambulatorioFacade.obterConsultaPorNumero(consultaSelecionada.getNumero()));
		//editarConsultaSisreg(consulta.getCaaSeq(),consulta.getTagSeq(),consulta.getPgdSeq());
		editarConsultaSisreg(consulta.getCaaSeq(),consulta.getTagSeq(),consulta.getPgdSeq());
	}
	
// [CHEGOU]
	public void chegou() {
		chegou(consultaSelecionada);
	}
	
	public void chegou(ConsultaAmbulatorioVO consultaVO){
		
		MamControles controle=ambulatorioFacade.obterMamControlePorNumeroConsulta(consultaVO.getNumero());
		AacRetornos retornoAntigo = null;
		if(consultaVO.getRetornoSeq() != null){
			retornoAntigo = this.getAmbulatorioFacade().obterRetorno(consultaVO.getRetornoSeq());
		}
		consultaSelecionada=consultaVO;
		
		try {
						
			MamSituacaoAtendimentos situacao=null;
			if (controle!=null){
				situacao=controle.getSituacaoAtendimento();	
			}
			
			if (situacao!=null && !situacao.equals(situacao)){
				apresentarMsgNegocio(Severity.WARN, "MSG_CONSULTA_ATUALIZADA_TERMINAL");
				consultaVO.setChegou(null);
				return;
			}
			
			boolean atualizaPaciente = Boolean.FALSE;
			boolean trocaPaciente = Boolean.FALSE;
			
			if (consultaVO.getChegou()) {
				AacRetornos retornoPacienteAtendido = getAmbulatorioFacade().obterRetorno(
						DominioSituacaoAtendimento.AGUARDANDO_ATENDIMENTO.getCodigo());
				consulta.setRetorno(retornoPacienteAtendido);
				consultaVO.setRetornoDescricao(retornoPacienteAtendido.getDescricao());
				consultaVO.setRetornoSeq(retornoPacienteAtendido.getSeq());
				
				if (controle != null) {
					getAmbulatorioFacade().verificarSituacaoAtendimento(controle.getSituacaoAtendimento().getSeq());
				}	
				if (getAmbulatorioFacade().verificarTrocaPacienteConsulta(consultaVO.getPacienteCodigo())) {
					trocaPaciente = true;
				} else if (getAmbulatorioFacade().validarDadosPacienteAmbulatorio(consultaVO.getPacienteCodigo())) {
					// Chama direto a tela de cadastro do paciente
					atualizaPaciente = true;
				}

				continuaChegada=atualizaPaciente;
				codigoPacienteTrocado = null;
				this.cadastrarPacienteController.setTrocarPacienteConsulta(false);

				getAmbulatorioFacade().processarProcedimentoConsultaPorRetorno(consultaVO.getNumero(), nomeMicrocomputador, new Date(), retorno, 
						false, false, false);
				
				if(trocaPaciente || atualizaPaciente){
					continuaChegada = true;
					this.cadastrarPacienteController.setPacCodigo(consultaSelecionada.getPacienteCodigo());
					this.cadastrarPacienteController.setCameFrom(PAGE_PESQUISAR_PACIENTES_AGENDADOS);
					this.cadastrarPacienteController.setGoingTo(PAGE_PESQUISAR_PACIENTES_AGENDADOS);
					this.redirecionarPaginaPorAjax("/pages/paciente/cadastroPaciente.xhtml");					
				} else {
					continuaChegada = true;
					this.redirecionarPaginaPorAjax("/pages/ambulatorio/pacientesagendados/pesquisarPacientesAgendados.xhtml");	
				}
				
				//Metodo finaliza chegada eh chamado no inicio quando volta do cadastro do paciente
				
			} else {
				AacRetornos retornoPacienteAgendado = getAmbulatorioFacade().obterRetorno(
						DominioSituacaoAtendimento.PACIENTE_AGENDADO.getCodigo());
				consultaVO.setRetornoDescricao(retornoPacienteAgendado.getDescricao());
				consultaVO.setRetornoSeq(retornoPacienteAgendado.getSeq());		    
				
				ambulatorioFacade.desmarcaChegadaPaciente(consultaVO.getNumero(), nomeMicrocomputador);
				consultaVO.setControledthrChegada(null);
				getAmbulatorioFacade().processarProcedimentoConsultaPorRetorno(consultaVO.getNumero(), nomeMicrocomputador, new Date(), retorno,false, false, false);	
				
				AacConsultas consulta = new AacConsultas();
				consulta.setRetorno(retornoPacienteAgendado);
				consulta.setNumero(consultaVO.getNumero());
				consulta.setRetSeq(consultaVO.getRetornoSeq());
				ambulatorioFacade.atualizarConsultaRetorno(consulta);
				refreshListaAba2();
			}

		} catch (BaseException e) {
			consultaVO.setRetornoDescricao(retornoAntigo.getDescricao());
			consultaVO.setRetornoSeq(retornoAntigo.getSeq());		    
		    consultaVO.setChegou(null);
			apresentarExcecaoNegocio(e);
		}
	}
	
	private void redirecionarPaginaPorAjax(String caminhoPagina){
		try{
			FacesContext ctx = FacesContext.getCurrentInstance();
			ExternalContext extContext = ctx.getExternalContext();
			String url = extContext.encodeActionURL(ctx.getApplication().getViewHandler().getActionURL(ctx, caminhoPagina)); 
			extContext.redirect(url);
		}
		catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
	}
	
	public void selecionarConsulta(ConsultaAmbulatorioVO consulta){
		this.setConsultaSelecionada(consulta);
	}
	
	public void verificaConsultaInformarCodigoCentral(ConsultaAmbulatorioVO consultaVO){
		this.setExecutarMetodoChegou(true);
		this.setConsultaSelecionada(consultaVO);
		AghParametros parametro = null;
		try {
			parametro = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_INFORMA_COD_CENTRAL_CHEGADA);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} 
		//exibe modal para informar codigo sisreg
		if(consultaVO.getCodCentral() == null && parametro != null && !parametro.getVlrNumerico().equals(BigDecimal.ZERO)){
			editarConsultaSisreg(consulta.getCaaSeq(),consulta.getTagSeq(),consulta.getPgdSeq());
			executarMetodoChegou = Boolean.TRUE;
			if(!exibirModalInformarChaveSisreg){
				chegou(consultaVO);
			}
		} else {
			chegou(consultaVO);
		}
	}	
	
	private void finalizaChegada() {
		if (consultaSelecionada.getChegou()) {
			try{
				String nomePacienteConsulta = consultaSelecionada.getPacienteNome();
				if (codigoPacienteTrocado != null){
					/*----obtem a consulta com o paciente antigo e desatacha---*/
					AacConsultas consultaOriginal = ambulatorioFacade.obterConsultasMarcada(consultaSelecionada.getNumero(), true);
					ambulatorioFacade.desatacharConsulta(consultaOriginal);
					/*---------------------------------------------------------*/
					AacConsultas consultaAtualizar = ambulatorioFacade.obterConsultasMarcada(consultaSelecionada.getNumero(), true);
					if (!codigoPacienteTrocado.equals(consultaAtualizar.getPaciente().getCodigo())){
						AipPacientes pacienteAtualizar = pacienteFacade.obterPaciente(codigoPacienteTrocado);
						consultaAtualizar.setPaciente(pacienteAtualizar);
						nomePacienteConsulta = pacienteAtualizar.getNome();
						ambulatorioFacade.atualizarConsulta(consultaOriginal, consultaAtualizar, false, nomeMicrocomputador, new Date(), false);						
					}
				}
				
				getAmbulatorioFacade().registraChegadaPaciente(consultaSelecionada.getNumero(), nomeMicrocomputador, consultaSelecionada.getRetornoSeq());
				apresentarMsgNegocio(Severity.INFO, "MSG_INFO_PACIENTE_ATENDIDO", nomePacienteConsulta);
				
				getAmbulatorioFacade().verificaPacienteOutraConsulta(consultaSelecionada.getNumero());
				
				if(this.exibirMensagemInformarChaveSisreg) {
					apresentarMsgNegocio(Severity.INFO, "MSG_CHAVE_SISREG_INFORMADA");
				}
			}catch (BaseListException e) {
				apresentarExcecaoNegocio(e);
			}catch (BaseException e) {
				consultaSelecionada.setChegou(null);
				apresentarExcecaoNegocio(e);
			}finally{
				continuaChegada=false;
				this.ocultarModalInformarChaveSisreg();
			}			
		}	
	}
	
	public String buscaCons(ConsultaAmbulatorioVO consultaVO){
		if (consultaVO.getGradeProfmatricula()==null || consultaVO.getGradeProfvinCodigo()==null){
			return "";
		}
		return buscaCons(consultaVO.getGradeProfmatricula(), consultaVO.getGradeProfvinCodigo());
	}
	
	private String buscaCons(Integer matricula, Short vincodigo){
		Object[] objs=null;
		try {
			objs = prescricaoMedicaFacade.buscaConsProf(matricula, vincodigo);
		} catch ( BaseException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
		return (String) objs[1];

	}
	
	
	public void setConsultas(List<AacConsultas> consultas) {
		this.consultas = consultas;
	}

	public List<AacConsultas> getConsultas() {
		return consultas;
	}

	public void setEquipe(AghEquipes equipe) {
		this.equipe = equipe;
	}

	public AghEquipes getEquipe() {
		return equipe;
	}

	public void setEspCrmVO(EspCrmVO espCrmVO) {
		this.espCrmVO = espCrmVO;
	}

	public EspCrmVO getEspCrmVO() {
		return espCrmVO;
	}
	
	public String getLabelZona() {
		return labelZona;
	}

	public void setLabelZona(String labelZona) {
		this.labelZona = labelZona;
	}

	
	public String getDataExtenso() {
		if (dtPesquisa==null){
			return "";
		}
		SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMMM yyyy", new Locale("pt", "BR"));
		return sdf.format(dtPesquisa);
	}
	
	
	public Date getDtPesquisa() {
		return dtPesquisa;
	}


	public void setDtPesquisa(Date dtPesquisa) {
		this.dtPesquisa = dtPesquisa;
	}


	public DominioTurno getTurno() {
		return turno;
	}


	public void setTurno(DominioTurno turno) {
		this.turno = turno;
	}


	public VAacSiglaUnfSalaVO getZona() {
		return zona;
	}


	public void setZona(VAacSiglaUnfSalaVO zona) {
		this.zona = zona;
	}


	public VAacSiglaUnfSala getZonaSala() {
		return zonaSala;
	}


	public void setZonaSala(VAacSiglaUnfSala zonaSala) {
		this.zonaSala = zonaSala;
	}


	public List<VAacSiglaUnfSala> getZonaSalaList() {
		return zonaSalaList;
	}


	public void setZonaSalaList(List<VAacSiglaUnfSala> zonaSalaList) {
		this.zonaSalaList = zonaSalaList;
	}


	public AghEspecialidades getEspecialidade() {
		return especialidade;
	}


	public void setEspecialidade(AghEspecialidades especialidade) {
		this.especialidade = especialidade;
	}


	public RapServidores getProfissional() {
		return profissional;
	}


	public void setProfissional(RapServidores profissional) {
		this.profissional = profissional;
	}


	public List<ConsultaAmbulatorioVO> getPacientesAgendadosList() {
		return pacientesAgendadosList;
	}


	public void setPacientesAgendadosList(List<ConsultaAmbulatorioVO> pacientesAgendadosList) {
		this.pacientesAgendadosList = pacientesAgendadosList;
	}

	public void marcarFalta() throws NumberFormatException, BaseException {
		this.marcarFaltaPacientes(dtPesquisa, zonaSalaList, zonaSala, turno, equipe, espCrmVO, especialidade, profissional);
		this.refreshListaAba1();
		this.refreshListaAba6();
	}

	private void marcarFaltaPacientes(Date dtPesquisa,  List<VAacSiglaUnfSala> zonaSalas, VAacSiglaUnfSala zonaSala,	
			DominioTurno turno, AghEquipes equipe, EspCrmVO espCrmVO, AghEspecialidades especialidade,
			RapServidores profissional) throws NumberFormatException, BaseException {
		try{
			ambulatorioFacade.marcarFaltaPacientes(dtPesquisa, zonaSalaList, zonaSala, 
					turno, equipe, espCrmVO, especialidade, profissional, nomeMicrocomputador);
			
		} catch(BaseException exception){
			apresentarExcecaoNegocio(exception);
		}
	}
    private void marcarFaltaPaciente(ConsultaAmbulatorioVO consultaSelecionada,Integer codSituacaoAtend) throws NumberFormatException, BaseException{
			ambulatorioFacade.marcarFaltaPaciente(consultaSelecionada.getNumero(), nomeMicrocomputador,consultaSelecionada.getChegou(),codSituacaoAtend);

    }
	public Integer getPercPanel() {
		return percPanel;
	}


	public void setPercPanel(Integer percPanel) {
		this.percPanel = percPanel;
	}


	public List<ConsultaAmbulatorioVO> getPacientesAguardandoList() {
		return pacientesAguardandoList;
	}


	public void setPacientesAguardandoList(
			List<ConsultaAmbulatorioVO> pacientesAguardandoList) {
		this.pacientesAguardandoList = pacientesAguardandoList;
	}


	public Integer getSelectedTab() {
		return selectedTab;
	}


	public void setSelectedTab(Integer selectedTab) {
		this.selectedTab = selectedTab;
	}


	public ConsultaAmbulatorioVO getConsultaSelecionada() {
		return consultaSelecionada;
	}


	public void setConsultaSelecionada(ConsultaAmbulatorioVO consultaSelecionada) {		
		this.consultaSelecionada = consultaSelecionada;
	}


	public AghCid getCid() {
		return cid;
	}


	public void setCid(AghCid cid) {
		this.cid = cid;
	}


	public Byte getProcedQuantidade() {
		return procedQuantidade;
	}


	public void setProcedQuantidade(Byte procedQuantidade) {
		this.procedQuantidade = procedQuantidade;
	}


	public AacConsultaProcedHospitalar getProcedimentoConsulta() {
		return procedimentoConsulta;
	}


	public void setProcedimentoConsulta(
			AacConsultaProcedHospitalar procedimentoConsulta) {
		this.procedimentoConsulta = procedimentoConsulta;
	}


	public List<AacConsultaProcedHospitalar> getListaProcedimentosHospConsulta() {
		return listaProcedimentosHospConsulta;
	}


	public IAmbulatorioFacade getAmbulatorioFacade() {
		return ambulatorioFacade;
	}


	public void setAmbulatorioFacade(IAmbulatorioFacade ambulatorioFacade) {
		this.ambulatorioFacade = ambulatorioFacade;
	}


	public void setListaProcedimentosHospConsulta(
			List<AacConsultaProcedHospitalar> listaProcedimentosHospConsulta) {
		this.listaProcedimentosHospConsulta = listaProcedimentosHospConsulta;
	}
	
	public void carregarAtributosModalProcedimentos() {
		procedimentoHospEspecialidadeVO = null;
		cid = null;
		procedQuantidade = VALOR_PADRAO_PROCED_QUANTIDADE;
		procedimentoConsulta = new AacConsultaProcedHospitalar();
		listaProcedimentosHospConsulta = ambulatorioFacade.buscarConsultaProcedHospPorNumeroConsulta(consultaSelecionada.getNumero());
	}
	
	public void limparAtributosModalProcedimentos() {
		procedimentoHospEspecialidadeVO = null;
		cid = null;
		procedQuantidade = VALOR_PADRAO_PROCED_QUANTIDADE;
		procedimentoConsulta = null;
		listaProcedimentosHospConsulta = null;
	}

	
	public Byte buscaSalaDeAtendimento(ConsultaAmbulatorioVO consultaVO) throws ApplicationBusinessException{
		if (consultaVO!=null){
			MamControles controle = ambulatorioFacade.obterMamControlePorNumeroConsulta(consultaVO.getNumero());
			if(controle==null || controle.getMicNome() == null) {
				return null;
			}
			else if (controle.getSalaAtendimento()!=null){
				return controle.getSalaAtendimento();
			}
			AghMicrocomputador micro = administracaoFacade.buscarMicrocomputador(controle.getMicNome());
			if (micro!=null && micro.getAacUnidFuncionalSala()!=null) {
				consultaVO.setSalaAtendimento(micro.getAacUnidFuncionalSala().getId().getSala());
				return micro.getAacUnidFuncionalSala().getId().getSala();
			}else{
				return null;
			}
		}
		return null;
	}
	
	
	public String getControleDthrMovimento(){
		if (consultaSelecionada!=null){
			MamControles controle=ambulatorioFacade.obterMamControlePorNumeroConsulta(consultaSelecionada.getNumero());
			if (controle!=null){
				return DateUtil.dataToString(controle.getDthrMovimento(), "dd/MM/yyyy HH:mm");
			}
		}	
		return "";
	}
	
	public String getControleServidorNome(){
		if (consultaSelecionada!=null){
			MamControles controle=ambulatorioFacade.obterMamControlePorNumeroConsulta(consultaSelecionada.getNumero());
			if (controle!=null && controle.getServidorAtualiza()!=null){
				return "por " + controle.getServidorAtualiza().getPessoaFisica().getNome();
			}
		}	
		return "";
	}	
	
	public String getControleServidorNomePendente(){
		if (consultaPendenteSelecionadaAba5!=null){
			MamControles controle=ambulatorioFacade.obterMamControlePorNumeroConsulta(consultaPendenteSelecionadaAba5.getNumero());
			if (controle!=null && controle.getServidorAtualiza()!=null){
				return "por " + controle.getServidorAtualiza().getPessoaFisica().getNome();
			}
		}	
		return "";
	}
	
	public String getControleDthrMovimentoPendente(){
		if (consultaPendenteSelecionadaAba5!=null){
			MamControles controle=ambulatorioFacade.obterMamControlePorNumeroConsulta(consultaPendenteSelecionadaAba5.getNumero());
			if (controle!=null){
				return DateUtil.dataToString(controle.getDthrMovimento(), "dd/MM/yyyy HH:mm");
			}
		}	
		return "";
	}
	
	public void gerarMovimentacaoProntuario(AacConsultas consulta){
		try{
			RapServidores servidorLogado = recuperarServidorLogado();
			Boolean exibeMsgProntuarioJaMovimentado = Boolean.TRUE;
			this.ambulatorioFacade.gerarMovimentacaoProntuario(consulta, servidorLogado, exibeMsgProntuarioJaMovimentado);	
			apresentarMsgNegocio(Severity.INFO, "SOLICITACAO_MOVIMENTACAO_PRONTUARIO_SUCESSO");
		} catch(BaseException exception){
			this.apresentarExcecaoNegocio(exception);
			LOG.error("Exceção capturada: ", exception);
		}
	}
	


	public Integer getListSize(){
		Integer size=0;
		if (selectedTab.equals(TAB_1)){
			size=pacientesAgendadosList.size();
		}else if (selectedTab.equals(TAB_2)){
			size=pacientesAguardandoList.size();
		}else if (selectedTab.equals(TAB_3)){
			size=pacientesAtendimentoList.size();
		}else if (selectedTab.equals(TAB_4)){
			size=pacientesAtendidosList.size();
		}else if (selectedTab.equals(TAB_5)){
			size=pacientesPendentesList.size();
		}else if (selectedTab.equals(TAB_6)){
			size=pacientesAusentesList.size();
		}
		return size;
	}
	
	public String getAcaoAtualizaDadosPacienteAmb() {
		return acaoAtualizaDadosPacienteAmb;
	}


	public void setAcaoAtualizaDadosPacienteAmb(String acaoAtualizaDadosPacienteAmb) {
		this.acaoAtualizaDadosPacienteAmb = acaoAtualizaDadosPacienteAmb;
	}

	/**
	 * Método para Suggestion de Retornos
	 */	
	public List<AacRetornos> obterRetornos(String parametro) {
		return ambulatorioFacade.getListaRetornos((String) parametro);
	}

	
	public void carregarAtributosModalRetornoConsulta() {
		retorno = null;
	}
	
	/**
	 * Método para atualizar o retorno da consulta
	 */	
	public void atualizarRetornoConsulta() throws ApplicationBusinessException, NumberFormatException, BaseException {
		AacRetornos retornoAntigo = null;
		if(consultaSelecionadaRetorno.getRetornoSeq() != null){
			retornoAntigo = this.getAmbulatorioFacade().obterRetorno(consultaSelecionadaRetorno.getRetornoSeq());
		}
		try{

			if(consultaSelecionadaRetorno.getRetornoSeq() != null && !retornoAntigo.getSeq().equals(retorno.getSeq())){
				
				if(retorno.getSeq().equals(DominioSituacaoAtendimento.PACIENTE_AGENDADO.getCodigo())){
					
				    //PACIENTE AGENDADO
					consultaSelecionadaRetorno = setRetornoConsultaAmbulatorioVo(consultaSelecionadaRetorno,DominioSituacaoAtendimento.PACIENTE_AGENDADO.getCodigo());
					getAmbulatorioFacade().desmarcaChegadaPaciente(consultaSelecionadaRetorno.getNumero(), nomeMicrocomputador);
					consultaSelecionadaRetorno.setChegou(false);
				}else if(retorno.getSeq().equals(DominioSituacaoAtendimento.AGUARDANDO_ATENDIMENTO.getCodigo())){
					
					//AGUARDANDO ATENDIMENTO
					if(consultaSelecionadaRetorno.getRetornoSeq().equals(DominioSituacaoAtendimento.EM_ATENDIMENTO.getCodigo())){
						
						consultaSelecionadaRetorno = setRetornoConsultaAmbulatorioVo(consultaSelecionadaRetorno,DominioSituacaoAtendimento.AGUARDANDO_ATENDIMENTO.getCodigo());
						getAmbulatorioFacade().cancelarAtendimentoSituacao(consultaSelecionadaRetorno.getNumero(), nomeMicrocomputador);					
						this.consultaSelecionadaRetorno.setAtender(null);
					}else{
					
						consultaSelecionadaRetorno = setRetornoConsultaAmbulatorioVo(consultaSelecionadaRetorno,DominioSituacaoAtendimento.AGUARDANDO_ATENDIMENTO.getCodigo());						
						getAmbulatorioFacade().registraChegadaPaciente(consultaSelecionadaRetorno.getNumero(), nomeMicrocomputador,consultaSelecionadaRetorno.getRetornoSeq());
					}
			    }else if(retorno.getSeq().equals(DominioSituacaoAtendimento.EM_ATENDIMENTO.getCodigo())){	
			    	
			    	if(consultaSelecionadaRetorno.getRetornoSeq().equals(DominioSituacaoAtendimento.PACIENTE_DESISTIU_CONS.getCodigo()) ||
			           consultaSelecionadaRetorno.getRetornoSeq().equals(DominioSituacaoAtendimento.PACIENTE_FALTOU.getCodigo()) ||
			    	   consultaSelecionadaRetorno.getRetornoSeq().equals(DominioSituacaoAtendimento.PACIENTE_FALTOU.getCodigo()) ||
			    	   consultaSelecionadaRetorno.getRetornoSeq().equals(DominioSituacaoAtendimento.PACIENTE_AGENDADO.getCodigo())){
				    			
			    		apresentarMsgNegocio(Severity.ERROR, "ERRO_ATENDER_SITUACAO");
			    		return;
			    	}
			    	MamControles controle = getAmbulatorioFacade().obterMamControlePorNumeroConsulta(consultaSelecionadaRetorno.getNumero());
			    	MamSituacaoAtendimentos situacao = getAmbulatorioFacade().obterSituacaoAtendimentos(controle.getSituacaoAtendimento().getSeq());

			    	//EM ATENDIMENTO
			    	if(!situacao.getAtendConcluido() && !situacao.getAtendPend() && !situacao.getPacAtend()){
			        	getAmbulatorioFacade().atualizarDataInicioAtendimento(consultaSelecionadaRetorno.getNumero(), new Date(), nomeMicrocomputador);
			        }else{
			        	AacConsultas aacConsultaAtual = ambulatorioFacade.obterAacConsultasJoinGradeEEspecialidade(consultaSelecionadaRetorno.getNumero());
			        	
			        	if(aacConsultaAtual.getDthrInicio() == null){
			        		getAmbulatorioFacade().atualizarDataInicioAtendimento(consultaSelecionadaRetorno.getNumero(), new Date(), nomeMicrocomputador);						      
			        	}
			        }
			        consultaSelecionadaRetorno = setRetornoConsultaAmbulatorioVo(consultaSelecionadaRetorno,DominioSituacaoAtendimento.EM_ATENDIMENTO.getCodigo());
			    	getAmbulatorioFacade().atualizaControleAguardandoLivre(consultaSelecionadaRetorno.getNumero(), new Date(), nomeMicrocomputador);
			    }else if(retorno.getSeq().equals(DominioSituacaoAtendimento.PACIENTE_ATENDIDO.getCodigo())){
			    	
			    	if(consultaSelecionadaRetorno.getRetornoSeq().equals(DominioSituacaoAtendimento.PACIENTE_DESISTIU_CONS.getCodigo()) ||
			    	   consultaSelecionadaRetorno.getRetornoSeq().equals(DominioSituacaoAtendimento.PACIENTE_FALTOU.getCodigo()) ||
			    	   consultaSelecionadaRetorno.getRetornoSeq().equals(DominioSituacaoAtendimento.PROFISSIONAL_FALTOU.getCodigo())){
			    		
			    		apresentarMsgNegocio(Severity.ERROR, "ERRO_ATENDER_SITUACAO");
			    		return;
			    	}
			    	
			    	//PACIENTE ATENDIDO
			    	consultaSelecionadaRetorno = setRetornoConsultaAmbulatorioVo(consultaSelecionadaRetorno, DominioSituacaoAtendimento.PACIENTE_ATENDIDO.getCodigo());
			    	getAmbulatorioFacade().finalizarAtendimento(consultaSelecionadaRetorno.getNumero(), nomeMicrocomputador);
			    }else if(retorno.getSeq().equals(DominioSituacaoAtendimento.PACIENTE_DESISTIU_CONS.getCodigo())
			    	  || retorno.getSeq().equals(DominioSituacaoAtendimento.PACIENTE_FALTOU.getCodigo())
			    	  || retorno.getSeq().equals(DominioSituacaoAtendimento.PROFISSIONAL_FALTOU.getCodigo())){
			    	
			    	//PACIENTE FALTOU OU PACIENTE DESISTIU CONS
			    	//PROFISSIONAL FALTOU
			    	//Profisional Faltou
			    	consultaSelecionadaRetorno = setRetornoConsultaAmbulatorioVo(consultaSelecionadaRetorno,retorno.getSeq());
					marcarFaltaPaciente(consultaSelecionadaRetorno,retorno.getSeq());
					
			    }else{
			    	apresentarMsgNegocio(Severity.ERROR, "SITUACAO_ATENDIMENTO_NAO_ESPERADA");
		    		return;
			    }
				
				AacRetornos retornoPacienteNovo = getAmbulatorioFacade().obterRetorno(consultaSelecionadaRetorno.getRetornoSeq());
				AacConsultas consulta = new AacConsultas();
				consulta.setRetorno(retornoPacienteNovo);
				consulta.setNumero(consultaSelecionadaRetorno.getNumero());
				consulta.setRetSeq(retornoPacienteNovo.getSeq());
				ambulatorioFacade.atualizarConsultaRetorno(consulta);
			}
		} catch (BaseException e) {
			consultaSelecionadaRetorno = setRetornoConsultaAmbulatorioVo(consultaSelecionadaRetorno,retornoAntigo.getSeq());
			apresentarExcecaoNegocio(e);
		}
	}
	public ConsultaAmbulatorioVO setRetornoConsultaAmbulatorioVo(ConsultaAmbulatorioVO vo,Integer codDominio){
	        AacRetornos retorno = getAmbulatorioFacade().obterRetorno(codDominio);
	        vo.setRetornoDescricao(retorno.getDescricao());
	        vo.setRetornoSeq(retorno.getSeq());
	        return vo;
	}
	/**
	 * Método que seta a cor do prontuario para azul na tela caso o número do
	 * prontuário seja maior que 90.000.000
	 * 
	 * @return String
	 */
	public String buscarEstiloCampoProntuario(ConsultaAmbulatorioVO vo) {
		String retorno = "";
		if(vo != null){
			if (vo.getProntuario() != null && vo.getProntuario() > VALOR_MAXIMO_PRONTUARIO) {
				if(vo.getPacienteNotifGMR()){
					retorno = "background-color:#00FFFF;color:blue";	
				}
				else{
					retorno = "color:blue";
				}
			}
			else{
				if(vo.getPacienteNotifGMR()){
					retorno = "background-color:#00FFFF;";	
				}	
			}
		}
		return retorno;
	}
	
	public String obterEstiloCampos(ConsultaAmbulatorioVO vo){
		String retorno = "";
		if(vo != null && vo.getPacienteNotifGMR() != null && vo.getPacienteNotifGMR()){
			retorno = "background-color:#00FFFF;";	
		}			
		return retorno;
	}

	public String obterDescricaoNotifGMR(){
		return "Fundo Ciano indica paciente sinalizado portador de germe multirresistente";
	}
	
	//#{(bean.paciente.nroCartaoSaude == null)?'font-weight:bold':''}
	
	public String verificarProntuarioVirtual(Integer prontuario) {
		if (prontuario != null && prontuario > VALOR_MAXIMO_PRONTUARIO) {
			return super.getBundle().getString(PesquisarPacientesAgendadosControllerExceptionCode.PRONTUARIO_VIRTUAL.toString());
		}
		return "";
	}
	
	public Boolean verificaRetornoConsultaVazio() {
		if(retorno==null) {
			return true;
		}
		else {
			return false;
		}
	}
	
	
	/**
	 * Obtem um AAC_Consultas atraves
	 * do numero de consulta.
	 * 
	 * @param numero
	 */
	public void editarConsultaSisreg(Short caaSeq, Short tagSeq, Short pgdSeq) {
		AacFormaAgendamentoId aacFormaAgendamentoId = new AacFormaAgendamentoId(caaSeq,tagSeq,pgdSeq);
		AacFormaAgendamento formaAgendamento = ambulatorioFacade.obterAacFormaAgendamentoPorChavePrimaria(aacFormaAgendamentoId);
		if (formaAgendamento != null && formaAgendamento.getSenhaAutoriza() != null && formaAgendamento.getSenhaAutoriza()) {
			exibirModalInformarChaveSisreg = Boolean.TRUE;			
		} else {
			exibirModalInformarChaveSisreg = Boolean.FALSE;
		}	
	}
	
	public void editarCodCentralSisreg(){
		try {
			String campoObrigatorio = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_INFORMA_COD_CENTRAL_CHEGADA);
			if(campoObrigatorio != null && !campoObrigatorio.isEmpty()){
				if(campoObrigatorio.equalsIgnoreCase("S")){
					setFlagCodigoCentral(Boolean.TRUE);
				}else{
					setFlagCodigoCentral(Boolean.FALSE);
				}
			}
			this.consulta = this.ambulatorioFacade.obterConsultaPorNumero(consultaSelecionada.getNumero());
			this.exibirModalInformarChaveSisreg = Boolean.TRUE;
		} catch (ApplicationBusinessException e) {
			super.apresentarExcecaoNegocio(e);
		}
	}
	
	
	/**
	 * Atualiza a chave da Sisreg na
	 * tabela AAC_Consultas
	 */
	public void gravarConsultaSisreg() {	
		try {
			this.ambulatorioFacade.atualizarConsulta(consulta);
			if(this.executarMetodoChegou) {
				this.exibirMensagemInformarChaveSisreg = Boolean.TRUE;
				this.chegou(this.consultaSelecionada);
			} else {
				apresentarMsgNegocio(Severity.INFO, "MSG_CHAVE_SISREG_INFORMADA");
				this.ocultarModalInformarChaveSisreg();
			}
			
		} catch (ApplicationBusinessException e) {
			super.apresentarExcecaoNegocio(e);
		}
	}
	
	/**
	 * Cancela a modal da Sisreg na tela
	 * 
	 * @return
	 */
	public void cancelarConsultaSisreg() {
		if(this.executarMetodoChegou) {
			this.chegou(this.consultaSelecionada);
			this.ocultarModalInformarChaveSisreg();
		} else {
			this.ocultarModalInformarChaveSisreg();
		}
		consultaSelecionada.setCodCentral(null);
	}
	
	public void ocultarModalInformarChaveSisreg() {
		this.exibirMensagemInformarChaveSisreg = Boolean.FALSE;
		this.exibirModalInformarChaveSisreg = Boolean.FALSE;
		this.executarMetodoChegou = Boolean.FALSE;
	}
	
	public String redirecionarListarPendenciasAssinatura() {
		String retorno = null;
		RapServidores profissional = null;

		try {
			profissional = registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado());
		} catch ( BaseException e) {
			LOG.error("Não encontrou o servidor logado!");
		}
		
		boolean habilitado = this.certificacaoDigitalFacade.verificaProfissionalHabilitado();
		
		boolean permissaoAssDigital = this.cascaService.usuarioTemPermissao(profissional.getUsuario(),"assinaturaDigital");
		
		boolean permissaoSamis = this.cascaService.usuarioTemPermissao(profissional.getUsuario(),"samisAssinaturaDigital");
		
		if((permissaoSamis || permissaoAssDigital) && habilitado){
			listarPendenciasAssinaturaPaginatorController.setVoltarPara(PAGE_PESQUISAR_PACIENTES_AGENDADOS);
			retorno = PAGE_LISTAR_PENDENCIAS_ASSINATURA;
			// A parte onde era setado o tipo foi transferida para o método de inicio de ListarPendenciasAssinaturaPaginatorController
		}else {
			apresentarMsgNegocio(Severity.ERROR, "MSG_ERRO_PERMISSAO_LISTA_DOCUMENTOS_PENDENTES");
		}
		return retorno;
	}
	
	public void prepararAbas(){
		try {
		
			if (this.selectedTab.equals(TAB_1)) {

				refreshListaAba1();

			} else if (this.selectedTab.equals(TAB_2)) {
				refreshListaAba2();
			} else if (this.selectedTab.equals(TAB_3)) {
				refreshListaAba3();
			} else if (this.selectedTab.equals(TAB_4)) {
				refreshListaAba4();
			}else if (this.selectedTab.equals(TAB_5)) {
				refreshListaAba5();
			} else if (this.selectedTab.equals(TAB_6)){
				refreshListaAba6();
			}
		} catch (ApplicationBusinessException e) {
			LOG.info("ERRO prepararAbas");
		}
	}
	
	@Override
	protected List<LaudoSolicitacaoAutorizacaoProcedAmbVO> recuperarColecao() throws ApplicationBusinessException {
		LaudoSolicitacaoAutorizacaoProcedAmbVO laudoSolicitacaoAutorizacaoProcedAmbVO = new LaudoSolicitacaoAutorizacaoProcedAmbVO();
		
		if (imprimirControleFrequencia) {
			laudoSolicitacaoAutorizacaoProcedAmbVO.setMesReferencia(ambulatorioFacade.obterMesAnoAtual(consultaSelecionadaAba1.getNumero()));
			
			AipPacientes aipPacientes = ambulatorioFacade.obterPacientePorProntuario(consultaSelecionadaAba1.getProntuario());
			if (aipPacientes != null) {
				laudoSolicitacaoAutorizacaoProcedAmbVO.setNomePaciente(aipPacientes.getNome());
				laudoSolicitacaoAutorizacaoProcedAmbVO.setNomeResponsavel(aipPacientes.getNomeMae());
				laudoSolicitacaoAutorizacaoProcedAmbVO.setCpf(aipPacientes.getCpf());
				laudoSolicitacaoAutorizacaoProcedAmbVO.setDataNascimentoPaciente(aipPacientes.getDtNascimento());
				laudoSolicitacaoAutorizacaoProcedAmbVO.setSexoPaciente(aipPacientes.getSexo());
				laudoSolicitacaoAutorizacaoProcedAmbVO.setNroCartaoSaude(aipPacientes.getNroCartaoSaude());
				
				VAipEnderecoPaciente endereco = cadastroPacienteFacade.obterEndecoPacienteSemValidacaoPermissao(aipPacientes.getCodigo());
				if (endereco != null) {
					laudoSolicitacaoAutorizacaoProcedAmbVO.setEndereco(endereco.getStringEnderecoSemiCompleto());
					laudoSolicitacaoAutorizacaoProcedAmbVO.setMunicipio(ambulatorioFacade.mpmcMinusculo(endereco.getCidade(),2));
					laudoSolicitacaoAutorizacaoProcedAmbVO.setCep(endereco.getCep());
					laudoSolicitacaoAutorizacaoProcedAmbVO.setTelefonePaciente(internacaoFacade.obterTelefonePaciente(aipPacientes.getCodigo()));
					laudoSolicitacaoAutorizacaoProcedAmbVO.setUf(endereco.getUf());
				}
			}
			LaudoSolicitacaoAutorizacaoProcedAmbVO dadosDeclaracao = null ;
		
			if(consultaSelecionadaAba1.getTipoSinalizacao().equals("O")){
				dadosDeclaracao = ambulatorioFacade.obterDadosDeclaracaoConsulta(consultaSelecionadaAba1.getNumero());
			}else{
				dadosDeclaracao = ambulatorioFacade.obterDadosDeclaracao(consultaSelecionadaAba1.getNumero());
			}
			if (dadosDeclaracao != null) {
				if(dadosDeclaracao.getDataDeclaracao()==null){
					laudoSolicitacaoAutorizacaoProcedAmbVO.setDataDeclaracao(ambulatorioFacade.obterDataConsultaPorNumero(consultaSelecionadaAba1.getNumero()));
				}else{
					laudoSolicitacaoAutorizacaoProcedAmbVO.setDataDeclaracao(dadosDeclaracao.getDataDeclaracao());										
				}
				laudoSolicitacaoAutorizacaoProcedAmbVO.setDescricao(dadosDeclaracao.getDescricao());
				laudoSolicitacaoAutorizacaoProcedAmbVO.setCodigoTabela(dadosDeclaracao.getCodigoTabela());				
				setarCodigoTabelaEDescricao(laudoSolicitacaoAutorizacaoProcedAmbVO);
			}else{
				laudoSolicitacaoAutorizacaoProcedAmbVO.setDataDeclaracao(ambulatorioFacade.obterDataConsultaPorNumero(consultaSelecionadaAba1.getNumero()));
				setarCodigoTabelaEDescricao(laudoSolicitacaoAutorizacaoProcedAmbVO);
			}
			if (consultaSelecionadaAba1.getTipoSinalizacao().equalsIgnoreCase(OTORRINO)) {
				laudoSolicitacaoAutorizacaoProcedAmbVO.setOtorrino(true);
			}
			laudoSolicitacaoAutorizacaoProcedAmbVO.setLocalData(ambulatorioFacade.obterDataLocalFormula(consultaSelecionadaAba1.getNumero()));
			laudoSolicitacaoAutorizacaoProcedAmbVO.setProntuarioPaciente(consultaSelecionadaAba1.getProntuario());
			
			//AJUSTE 47617
			if(consultaSelecionadaAba1.getTipoSinalizacao().equals(FOTOCOAGULACAO)){
				laudoSolicitacaoAutorizacaoProcedAmbVO.setCodigoTabela(Long.valueOf(405030045));
				laudoSolicitacaoAutorizacaoProcedAmbVO.setDescricao("FOTOCOAGULACAO A LASER");
			}
			
		}
		
		if (imprimirAutorizacao) {
			//C2
			AipPacientes paciente = pacienteFacade.obterPacientePorProntuario(consultaSelecionadaAba1.getProntuario());
			VAipEnderecoPaciente enderecoPaciente = cadastroPacienteFacade.obterEndecoPaciente(paciente.getCodigo());
			if (DateUtil.obterQtdAnosEntreDuasDatas(paciente.getDtNascimento(), new Date()) < 18) {
				laudoSolicitacaoAutorizacaoProcedAmbVO.setNomeResponsavel(paciente.getNomeMae());
			} else {
				laudoSolicitacaoAutorizacaoProcedAmbVO.setNomeResponsavel(paciente.getNome());
			}
			
			if (consultaSelecionadaAba1.getTipoSinalizacao().equalsIgnoreCase(OTORRINO)) {
				laudoSolicitacaoAutorizacaoProcedAmbVO.setQuantidade(1);
				//C8
				ParametrosGeracaoLaudoOtorrinoVO parametrosGeracaoLaudoOtorrinoVO = faturamentoFacade.obterParametrosGeracaoLaudoOtorrino(consultaSelecionadaAba1.getNumero());
				if (parametrosGeracaoLaudoOtorrinoVO != null) {
//					String candidato = faturamentoFacade.fatpCandidatoApacDesc(consultaSelecionadaAba1.getNumero(), parametrosGeracaoLaudoOtorrinoVO.getDthrRealizado());
//					if(candidato != null){
//						parametrosGeracaoLaudoOtorrinoVO.setCandidato(candidato);
//					}
					//C9 C10
					LaudoSolicitacaoAutorizacaoProcedAmbVO laudoSolicitacaoAutorizacaoProcedAmbVO2 = faturamentoFacade.obterCandidatosApacOtorrino(parametrosGeracaoLaudoOtorrinoVO);
					if(laudoSolicitacaoAutorizacaoProcedAmbVO2 != null){
						laudoSolicitacaoAutorizacaoProcedAmbVO.setCodigoTabela(laudoSolicitacaoAutorizacaoProcedAmbVO2.getCodigoTabela());
						laudoSolicitacaoAutorizacaoProcedAmbVO.setDescricaoIph(laudoSolicitacaoAutorizacaoProcedAmbVO2.getDescricaoIph());
						//laudoSolicitacaoAutorizacaoProcedAmbVO.setCmce(laudoSolicitacaoAutorizacaoProcedAmbVO2.getCmce());
						laudoSolicitacaoAutorizacaoProcedAmbVO.setCmce(ambulatorioFacade.consultarCMCEpaciente(consultaSelecionadaAba1.getPacienteCodigo(), consultaSelecionadaAba1.getNumero()));
						laudoSolicitacaoAutorizacaoProcedAmbVO.setCid10principal(laudoSolicitacaoAutorizacaoProcedAmbVO2.getCid10principal());
						laudoSolicitacaoAutorizacaoProcedAmbVO.setDescricaoCid(laudoSolicitacaoAutorizacaoProcedAmbVO2.getDescricaoCid());
					}
					//C11
					LaudoSolicitacaoAutorizacaoProcedAmbVO laudoSolicitacaoAutorizacaoProcedAmbVO3 = aghuFacade.obterEquipePorSeq(parametrosGeracaoLaudoOtorrinoVO.getSeqEquipe());
					laudoSolicitacaoAutorizacaoProcedAmbVO.setNomeProfissionalSolicitante(laudoSolicitacaoAutorizacaoProcedAmbVO3.getNomeProfissionalSolicitante());
					laudoSolicitacaoAutorizacaoProcedAmbVO.setNumeroCnsProfissionalSolicitante(registroColaboradorFacade.obterRapPessoa(laudoSolicitacaoAutorizacaoProcedAmbVO3.getPesCodigo()).getValor());
				}
				//melhoria 50616
				laudoSolicitacaoAutorizacaoProcedAmbVO = preencheCamposJustificativaProcedSolicit(laudoSolicitacaoAutorizacaoProcedAmbVO);
				//C12
				laudoSolicitacaoAutorizacaoProcedAmbVO = popularCamposRelatorioFotoPosTransplanteOtorrino(laudoSolicitacaoAutorizacaoProcedAmbVO);
				laudoSolicitacaoAutorizacaoProcedAmbVO = setarDataCosunsultaFatlaudosProtocolosApac(laudoSolicitacaoAutorizacaoProcedAmbVO);
//				String cpfAutorizaPreTransplante = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_CPF_AUTORIZA_PRE_TRANS);
//				FatAutorizaApac fatAutorizaApac = faturamentoFacade.obterFatAutorizaApacPorCpf(Long.valueOf(cpfAutorizaPreTransplante));
//				laudoSolicitacaoAutorizacaoProcedAmbVO.setNumeroDocumetoProfissionalAutorizador(fatAutorizaApac.getCns());
//				laudoSolicitacaoAutorizacaoProcedAmbVO.setNomeProfissionalAutorizador(fatAutorizaApac.getNome());
				laudoSolicitacaoAutorizacaoProcedAmbVO.setCmce(ambulatorioFacade.consultarCMCEpaciente(consultaSelecionadaAba1.getPacienteCodigo(), consultaSelecionadaAba1.getNumero()));
			} else if (consultaSelecionadaAba1.getTipoSinalizacao().equalsIgnoreCase(PRE_TRANSPLANTE)) {
				laudoSolicitacaoAutorizacaoProcedAmbVO.setQuantidade(1);
				//C1 
				FatLaudoPacApac laudoPacApac = faturamentoFacade.obterLaudoRelacionadoConsulta(consultaSelecionadaAba1.getNumero());
				if (laudoPacApac != null) {
					//C3 a C6
					LaudoSolicitacaoAutorizacaoProcedAmbVO laudoSolicitacaoAutorizacaoProcedAmbVO2 = faturamentoFacade.obterProcedimentoSolicitado(laudoPacApac.getSeq());
					if( laudoSolicitacaoAutorizacaoProcedAmbVO2 != null ){
						laudoSolicitacaoAutorizacaoProcedAmbVO.setCodigoTabela(laudoSolicitacaoAutorizacaoProcedAmbVO2.getCodigoTabela());
						laudoSolicitacaoAutorizacaoProcedAmbVO.setDescricaoIph(laudoSolicitacaoAutorizacaoProcedAmbVO2.getDescricaoIph());
						laudoSolicitacaoAutorizacaoProcedAmbVO.setDescricaoCid(laudoSolicitacaoAutorizacaoProcedAmbVO2.getDescricaoCid());
						laudoSolicitacaoAutorizacaoProcedAmbVO.setCid10principal(laudoSolicitacaoAutorizacaoProcedAmbVO2.getCid10principal());
						laudoSolicitacaoAutorizacaoProcedAmbVO.setObservacaoLap(laudoSolicitacaoAutorizacaoProcedAmbVO2.getObservacaoLap());
						//laudoSolicitacaoAutorizacaoProcedAmbVO.setCmce(laudoSolicitacaoAutorizacaoProcedAmbVO2.getCmce());
						laudoSolicitacaoAutorizacaoProcedAmbVO.setCmce(ambulatorioFacade.consultarCMCEpaciente(consultaSelecionadaAba1.getPacienteCodigo(), consultaSelecionadaAba1.getNumero()));
						laudoSolicitacaoAutorizacaoProcedAmbVO.setNomeProfissionalSolicitante(laudoSolicitacaoAutorizacaoProcedAmbVO2.getNomeProfissionalSolicitante());
						laudoSolicitacaoAutorizacaoProcedAmbVO.setDataSolicitacao(laudoSolicitacaoAutorizacaoProcedAmbVO2.getDataSolicitacao());
						laudoSolicitacaoAutorizacaoProcedAmbVO.setNumeroCnsProfissionalSolicitante(faturamentoFacade.getCnsResp(laudoSolicitacaoAutorizacaoProcedAmbVO2.getPesCpf()));
						laudoSolicitacaoAutorizacaoProcedAmbVO.setPeriodoValidadeApac("");
						laudoSolicitacaoAutorizacaoProcedAmbVO.setDocumento(CNS);
						laudoSolicitacaoAutorizacaoProcedAmbVO.setDocumentoAtorizacao(CNS);
						laudoSolicitacaoAutorizacaoProcedAmbVO.setNumeroAutorizacao(laudoSolicitacaoAutorizacaoProcedAmbVO2.getNumeroAutorizacao());
					}
					FatAutorizaApac fatAutorizaApac = faturamentoFacade.obterFatAutorizaApacPorCpf(Long.valueOf(parametroFacade.buscarValorTexto(AghuParametrosEnum.P_CPF_AUTORIZA_PRE_TRANS)));
					laudoSolicitacaoAutorizacaoProcedAmbVO.setNumeroDocumetoProfissionalAutorizador(fatAutorizaApac.getCns());
					laudoSolicitacaoAutorizacaoProcedAmbVO.setNomeProfissionalAutorizador(fatAutorizaApac.getNome());
				}
				laudoSolicitacaoAutorizacaoProcedAmbVO.setCmce(ambulatorioFacade.consultarCMCEpaciente(consultaSelecionadaAba1.getPacienteCodigo(), consultaSelecionadaAba1.getNumero()));
				//laudoSolicitacaoAutorizacaoProcedAmbVO.setCmce(ambulatorioFacade.obterCMCE(consultaSelecionadaAba1.getNumero()));
			} else if(consultaSelecionadaAba1.getTipoSinalizacao().equalsIgnoreCase(FOTOCOAGULACAO)) {
				laudoSolicitacaoAutorizacaoProcedAmbVO = obterCodigoTabelaDescricao(laudoSolicitacaoAutorizacaoProcedAmbVO);//c19 47668
				laudoSolicitacaoAutorizacaoProcedAmbVO = pesquisarJustificativaFoto(laudoSolicitacaoAutorizacaoProcedAmbVO);//c16 47668
				laudoSolicitacaoAutorizacaoProcedAmbVO = setarNomeEspecialidadeDataConsulta(laudoSolicitacaoAutorizacaoProcedAmbVO);//c17 47668
				laudoSolicitacaoAutorizacaoProcedAmbVO = popularCamposRelatorioFotoPosTransplanteOtorrino(laudoSolicitacaoAutorizacaoProcedAmbVO);//c12 47668
				laudoSolicitacaoAutorizacaoProcedAmbVO = setarDataCosunsultaFatlaudosProtocolosApac(laudoSolicitacaoAutorizacaoProcedAmbVO);//c18 47668
				/*laudoSolicitacaoAutorizacaoProcedAmbVO.setCodigoTabela(405030045L); 47668
				laudoSolicitacaoAutorizacaoProcedAmbVO.setDescricaoIph("FOTOCOAGULAÇÃO A LASER");*/
				laudoSolicitacaoAutorizacaoProcedAmbVO.setQuantidade(1);
				//laudoSolicitacaoAutorizacaoProcedAmbVO.setCmce(ambulatorioFacade.obterCMCE(consultaSelecionadaAba1.getNumero()));
				laudoSolicitacaoAutorizacaoProcedAmbVO.setCmce(ambulatorioFacade.consultarCMCEpaciente(consultaSelecionadaAba1.getPacienteCodigo(), consultaSelecionadaAba1.getNumero()));
			}else{//Pos transplante

				//laudoSolicitacaoAutorizacaoProcedAmbVO.setCmce(ambulatorioFacade.obterCMCE(consultaSelecionadaAba1.getNumero()));
				laudoSolicitacaoAutorizacaoProcedAmbVO.setCmce(ambulatorioFacade.consultarCMCEpaciente(consultaSelecionadaAba1.getPacienteCodigo(), consultaSelecionadaAba1.getNumero()));
				laudoSolicitacaoAutorizacaoProcedAmbVO = obterCodigoDescricaoProcedimentoProTransplante(laudoSolicitacaoAutorizacaoProcedAmbVO);//47668
				laudoSolicitacaoAutorizacaoProcedAmbVO = pesquisarCodigoDescricaoCidPorAghParametro(laudoSolicitacaoAutorizacaoProcedAmbVO);//47668
				laudoSolicitacaoAutorizacaoProcedAmbVO = setarNomeEspecialidadeDataConsulta(laudoSolicitacaoAutorizacaoProcedAmbVO);//c17 47668
				laudoSolicitacaoAutorizacaoProcedAmbVO = popularCamposRelatorioFotoPosTransplanteOtorrino(laudoSolicitacaoAutorizacaoProcedAmbVO);//c12 47668
				laudoSolicitacaoAutorizacaoProcedAmbVO = setarDataCosunsultaFatlaudosProtocolosApac(laudoSolicitacaoAutorizacaoProcedAmbVO);//c18 47668
				//laudoSolicitacaoAutorizacaoProcedAmbVO.setQuantidade(1);setado por obterCodigoDescricaoProcedimentoProTransplante 
			}
			
			laudoSolicitacaoAutorizacaoProcedAmbVO.setNomePaciente(paciente.getNome());
			laudoSolicitacaoAutorizacaoProcedAmbVO.setProntuarioPaciente(paciente.getProntuario());
			laudoSolicitacaoAutorizacaoProcedAmbVO.setCns(paciente.getNroCartaoSaude());
			laudoSolicitacaoAutorizacaoProcedAmbVO.setDataNascimentoPaciente(paciente.getDtNascimento());
			laudoSolicitacaoAutorizacaoProcedAmbVO.setSexoPaciente(paciente.getSexo());
			laudoSolicitacaoAutorizacaoProcedAmbVO.setRaca(paciente.getCor());
			laudoSolicitacaoAutorizacaoProcedAmbVO.setNomeMaePaciente(paciente.getNomeMae());
			laudoSolicitacaoAutorizacaoProcedAmbVO.setTelefoneContatoMae(internacaoFacade.obterTelefonePaciente(paciente.getCodigo()));
			if (paciente.getDddFoneRecado() != null && paciente.getFoneRecado() != null) {
				laudoSolicitacaoAutorizacaoProcedAmbVO.setTelefoneContatoResponsavel(paciente.getDddFoneRecado() + paciente.getFoneRecado());
			}
			if(enderecoPaciente != null ){
				laudoSolicitacaoAutorizacaoProcedAmbVO.setEndereco(enderecoPaciente.getLogradouro()+" "+(enderecoPaciente.getNroLogradouro() !=null ? enderecoPaciente.getNroLogradouro() : "")+" / "
																 +(enderecoPaciente.getComplLogradouro()!=null ? enderecoPaciente.getComplLogradouro() : "")
																 + " " + (enderecoPaciente.getBairro() !=null ? enderecoPaciente.getBairro() : ""));
				laudoSolicitacaoAutorizacaoProcedAmbVO.setMunicipioResidencia(enderecoPaciente.getCidade());
				laudoSolicitacaoAutorizacaoProcedAmbVO.setCodMunicipio(enderecoPaciente.getCodIbge());
				laudoSolicitacaoAutorizacaoProcedAmbVO.setUf(enderecoPaciente.getUf());
				laudoSolicitacaoAutorizacaoProcedAmbVO.setCep(enderecoPaciente.getCep());	
			}
		}
		preencherCodigoTabelaEDescricaoOtorrino(laudoSolicitacaoAutorizacaoProcedAmbVO);

		List<LaudoSolicitacaoAutorizacaoProcedAmbVO> retorno = new ArrayList<LaudoSolicitacaoAutorizacaoProcedAmbVO>();
		retorno.add(laudoSolicitacaoAutorizacaoProcedAmbVO);
		
		return retorno;
	}
	
	/**
	 * Executa C12 #47668 
	 * popula campos 46, 47, 48, 49, 50, 51, 54, 55 para foto, pÃ³s-transplante e otorrino.
	 */
	public LaudoSolicitacaoAutorizacaoProcedAmbVO popularCamposRelatorioFotoPosTransplanteOtorrino(LaudoSolicitacaoAutorizacaoProcedAmbVO laudoSolicitacaoAutorizacaoProcedAmbVO){
		try {
			FatAutorizaApac fatAutorizaApac = new FatAutorizaApac();
			Long codTabela = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_ACOMP_POS_CORNEA_COD_TABELA).getVlrNumerico().longValue();
			//Fotocoagulação
			if (consultaSelecionadaAba1.getTipoSinalizacao().equalsIgnoreCase(FOTOCOAGULACAO)){
				fatAutorizaApac = faturamentoFacade.obterFatAutorizaApacPorCpf(Long.valueOf(parametroFacade.buscarAghParametro(AghuParametrosEnum.P_CPF_AUTORIZA_FOTO).getVlrTexto()));
			}
			//Otorrino ou Pós (não de córnea) - ambos possuem o mesmo autorizador
			else if (consultaSelecionadaAba1.getTipoSinalizacao().equalsIgnoreCase(OTORRINO) || (!codTabela.equals(laudoSolicitacaoAutorizacaoProcedAmbVO.getCodigoTabela()))){
				fatAutorizaApac = faturamentoFacade.obterFatAutorizaApacPorCpf(Long.valueOf(parametroFacade.buscarAghParametro(AghuParametrosEnum.P_CPF_AUTORIZA_OUT).getVlrTexto()));
			}
			//Pós de Córnea - Hoje possui o mesmo autorizador da Foto
			else{
				fatAutorizaApac = faturamentoFacade.obterFatAutorizaApacPorCpf(Long.valueOf(parametroFacade.buscarAghParametro(AghuParametrosEnum.P_CPF_AUTORIZA_FOTO).getVlrTexto()));
			}
			if(fatAutorizaApac != null){
				laudoSolicitacaoAutorizacaoProcedAmbVO.setNomeProfissionalAutorizador(fatAutorizaApac.getNome());
				laudoSolicitacaoAutorizacaoProcedAmbVO.setNumeroDocumetoProfissionalAutorizador(fatAutorizaApac.getCns());
			}
			laudoSolicitacaoAutorizacaoProcedAmbVO.setDocumentoAtorizacao(CNS);
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		
		return laudoSolicitacaoAutorizacaoProcedAmbVO;
	}
	
	/**
	 * Executa C18 #47668 
	 * Popula campos 52 e 53 se para foto, pÃ³s-transplante e otorrino
	 * @param laudoSolicitacaoAutorizacaoProcedAmbVO
	 * @return
	 */
	public LaudoSolicitacaoAutorizacaoProcedAmbVO setarDataCosunsultaFatlaudosProtocolosApac(LaudoSolicitacaoAutorizacaoProcedAmbVO laudoSolicitacaoAutorizacaoProcedAmbVO){		
		laudoSolicitacaoAutorizacaoProcedAmbVO.setNumeroAutorizacao(ambulatorioFacade.obterApacNumero(consultaSelecionadaAba1.getNumero()));
		laudoSolicitacaoAutorizacaoProcedAmbVO.setPeriodoValidadeApac("");
		return laudoSolicitacaoAutorizacaoProcedAmbVO;
	}
	
	/**
	 * @param laudoSolicitacaoAutorizacaoProcedAmbVO
	 * @throws ApplicationBusinessException
	 */
	private LaudoSolicitacaoAutorizacaoProcedAmbVO preencheCamposJustificativaProcedSolicit(LaudoSolicitacaoAutorizacaoProcedAmbVO laudoSolicitacaoAutorizacaoProcedAmbVO)throws ApplicationBusinessException {
		LaudoSolicitacaoAutorizacaoProcedAmbVO laudoSolicitacaoAutorizacaoProcedAmbVO3 = ambulatorioFacade.preencheCamposdaJustificativaDoProcedSolicitado(consultaSelecionadaAba1.getNumero());
		laudoSolicitacaoAutorizacaoProcedAmbVO.setCid10principal(laudoSolicitacaoAutorizacaoProcedAmbVO3.getCid10principal());
		laudoSolicitacaoAutorizacaoProcedAmbVO.setDescricaoCid(laudoSolicitacaoAutorizacaoProcedAmbVO3.getDescricaoCid());
		laudoSolicitacaoAutorizacaoProcedAmbVO.setNomeProfissionalSolicitante(laudoSolicitacaoAutorizacaoProcedAmbVO3.getNomeProfissionalSolicitante());
		laudoSolicitacaoAutorizacaoProcedAmbVO.setNumeroCnsProfissionalSolicitante(laudoSolicitacaoAutorizacaoProcedAmbVO3.getNumeroCnsProfissionalSolicitante());
		//alteração devido melhoria 50616
		laudoSolicitacaoAutorizacaoProcedAmbVO.setDataSolicitacao(consultaSelecionadaAba1.getDtConsulta());
		//alteração devido melhoria 50616 'CNS', -- CAMPO 43
		laudoSolicitacaoAutorizacaoProcedAmbVO.setDocumento(CNS);
		return laudoSolicitacaoAutorizacaoProcedAmbVO;
	}


	/** Ajuste 47670
	 * @throws ApplicationBusinessException 
	 * @throws NumberFormatException */
	private void setarCodigoTabelaEDescricao(LaudoSolicitacaoAutorizacaoProcedAmbVO laudoSolicitacaoAutorizacaoProcedAmbVO) throws NumberFormatException, ApplicationBusinessException {
		if(consultaSelecionadaAba1.getTipoSinalizacao().equals("T")){
			FatProcedHospInternos procedimento = null;
			try {
				procedimento = ambulatorioFacade.obterCodigoDescricaoProcedimentoProTransplante(consultaSelecionadaAba1.getPacienteCodigo());
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
			if(procedimento != null){
				final AghParametros convenioSUS = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_CONVENIO_SUS);
				final AghParametros planoSUS = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_SUS_PLANO_AMBULATORIO);
				final AghParametros tipoGrupoContaSUS = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_TIPO_GRUPO_CONTA_SUS);
				VFatAssociacaoProcedimento fatAssociacaoProcedimento = ambulatorioFacade
						.obterCodigoTabelaEDescricao(
								procedimento.getSeq(),
								Short.valueOf(parametroFacade
										.buscarAghParametro(
												AghuParametrosEnum.P_TABELA_FATUR_PADRAO)
										.getVlrNumerico().toString()),
								convenioSUS.getVlrNumerico()
										.shortValue(), planoSUS
										.getVlrNumerico().byteValue(),
								tipoGrupoContaSUS.getVlrNumerico()
										.shortValue());	
				laudoSolicitacaoAutorizacaoProcedAmbVO.setCodigoTabela(fatAssociacaoProcedimento.getId().getCodTabela());
				laudoSolicitacaoAutorizacaoProcedAmbVO.setDescricao(fatAssociacaoProcedimento.getId().getIphDescricao());
			}
		}
	}

	/** 42803/42801 - melhorias **/
	private void preencherCodigoTabelaEDescricaoOtorrino(LaudoSolicitacaoAutorizacaoProcedAmbVO laudoSolicitacaoAutorizacaoProcedAmbVO){
		if(consultaSelecionadaAba1 != null && consultaSelecionadaAba1.getTipoSinalizacao() != null && 
				consultaSelecionadaAba1.getTipoSinalizacao().equalsIgnoreCase("O") && consultaSelecionadaAba1.getControleFrequencia() != null){
			if(consultaSelecionadaAba1.getControleFrequencia().equals(DominioSinalizacaoControleFrequencia.IC)){
				AghParametros implanteCoclear = null;
				try {
					implanteCoclear = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_ACOMP_PAC_IMPL_COC);
				} catch (ApplicationBusinessException e) {
					apresentarExcecaoNegocio(e);
				}
				if(implanteCoclear != null){
					if(implanteCoclear.getVlrNumerico() != null){
						VFatAssociacaoProcedimento procedimento = ambulatorioFacade.obterDescricaoPorCodTabela(Long.valueOf(implanteCoclear.getVlrNumerico().toString()));
						if(procedimento != null && procedimento.getId() != null && procedimento.getId().getIphDescricao() != null){
							laudoSolicitacaoAutorizacaoProcedAmbVO.setDescricao(procedimento.getId().getIphDescricao());							
							laudoSolicitacaoAutorizacaoProcedAmbVO.setDescricaoIph(procedimento.getId().getIphDescricao());							
						}else{
							laudoSolicitacaoAutorizacaoProcedAmbVO.setDescricao(null);							
							laudoSolicitacaoAutorizacaoProcedAmbVO.setDescricaoIph(null);							
						}
						laudoSolicitacaoAutorizacaoProcedAmbVO.setCodigoTabela(Long.valueOf(implanteCoclear.getVlrNumerico().toString()));
					}
				}
			}else if(consultaSelecionadaAba1.getControleFrequencia().equals(DominioSinalizacaoControleFrequencia.AP)){
				//C20 - #48096 | C6 - #48550
				AacConsultaProcedHospitalar consulta = ambulatorioFacade.obterConsultaProcedGHospPorNumeroEInd(consultaSelecionadaAba1.getNumero(), Boolean.FALSE);
				//se nao retornar valor entao codido e descricao ficam em branco
				if(consulta != null){
					try {
						final AghParametros convenioSUS = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_CONVENIO_SUS);
						final AghParametros planoSUS = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_SUS_PLANO_AMBULATORIO);
						final AghParametros tipoGrupoContaSUS = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_TIPO_GRUPO_CONTA_SUS);
						VFatAssociacaoProcedimento fatAssociacaoProcedimento = ambulatorioFacade
								.obterCodigoTabelaEDescricao(
										consulta.getProcedHospInterno().getSeq(),
										Short.valueOf(parametroFacade
												.buscarAghParametro(
														AghuParametrosEnum.P_TABELA_FATUR_PADRAO)
												.getVlrNumerico().toString()),
										convenioSUS.getVlrNumerico()
												.shortValue(), planoSUS
												.getVlrNumerico().byteValue(),
										tipoGrupoContaSUS.getVlrNumerico()
												.shortValue());
						
						if(fatAssociacaoProcedimento != null && fatAssociacaoProcedimento.getId() != null){
							laudoSolicitacaoAutorizacaoProcedAmbVO.setCodigoTabela(fatAssociacaoProcedimento.getId().getCodTabela());
							laudoSolicitacaoAutorizacaoProcedAmbVO.setDescricao(fatAssociacaoProcedimento.getId().getIphDescricao());
							laudoSolicitacaoAutorizacaoProcedAmbVO.setDescricaoIph(fatAssociacaoProcedimento.getId().getIphDescricao());
							if(consulta.getQuantidade()!= null){
								laudoSolicitacaoAutorizacaoProcedAmbVO.setQuantidade(Integer.valueOf(consulta.getQuantidade().toString()));								
							}else{
								laudoSolicitacaoAutorizacaoProcedAmbVO.setQuantidade(null);
							}
						}			
					} catch (ApplicationBusinessException e) {
						apresentarExcecaoNegocio(e);
					}
				}else{
					laudoSolicitacaoAutorizacaoProcedAmbVO.setQuantidade(null);
				}
			}
		}
	}

	@Override
	protected String recuperarArquivoRelatorio() {
		return url_documento_jasper;
	}

	@Override
	protected Map<String, Object> recuperarParametros() {
			Map<String, Object> parametros = new HashMap<String, Object>();
		
		try {		
			AghParametros nomeHospital = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_HOSPITAL_RAZAO_SOCIAL);
			if (imprimirAutorizacao) {
			AghParametros cnes = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_CNES_APAC);
			AghParametros codigoOrgaoEmissor = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_ORGAO_AUTOR_APAC);
			
			parametros.put("cnes", cnes.getValor());
			parametros.put("codOrgaoEmissor", codigoOrgaoEmissor.getVlrTexto());
			parametros.put("documento", CNS);
			}
			
			if (imprimirControleFrequencia) {
				AghParametros cgc = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_HOSPITAL_CGC);
				AghParametros cidade = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_HOSPITAL_END_CIDADE);
				AghParametros cnes = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_CNES_HCPA);
				AghParametros uf = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_HOSPITAL_UF_SIGLA);
				AghParametros fone = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_HOSPITAL_END_FONE);
				
				parametros.put("codigo", cnes.getVlrNumerico());
				parametros.put("cgc", cgc.getVlrTexto());
				parametros.put("municipio", cidade.getVlrTexto());
				parametros.put("estado", uf.getVlrTexto());
				parametros.put("fone", fone.getVlrTexto());
				parametros.put("imagem", parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_LOGO_HOSPITAL_JEE7));
				parametros.put("codigoFormula", ambulatorioFacade.obterCodigoFormulaPaciente(consultaSelecionadaAba1.getProntuario()));
				parametros.put("imagem", parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_LOGO_HOSPITAL_JEE7));
			}
			
			parametros.put("codigoBarras", formataProntuarioBarcode(consultaSelecionadaAba1.getProntuario()));
			parametros.put("nomeEstabelecimeto", nomeHospital.getVlrTexto());
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		
		return parametros;
	}
	
	/** #51261
	 *  Formata o prontuário para ler no scanner de código de barras
	 * 
	 * @return
	 */
	public String formataProntuarioBarcode(Integer prontuarioPaciente){
		if (prontuarioPaciente == null) {
			return "";
		}
		String prontFormatado = StringUtils.leftPad(String.valueOf(prontuarioPaciente), 9, '0');
		prontFormatado = StringUtils.rightPad(prontFormatado, 12, '0');
		return prontFormatado;
	}
	
	//@uthor: luiz.rosario
	//Metodo chamado ao clicar no botão imprimir documentos da aba agendados. 
	//Verifica se existem documentos a serem impressos e caso existam, abre o modal passando o número da consulta por parametro.
	
	public void existeDocumentosImprimirPaciente(){
		
		flagModalImprimir = true;
        
		if(this.consultaSelecionada != null && TAB_1.equals(selectedTab)){
        
			flagAba1 = true;
			aacConsulta = ambulatorioFacade.obterAacConsultasJoinGradeEEspecialidade(this.consultaSelecionada.getNumero());
			AipPacientes paciente = new AipPacientes();
			paciente.setNome(this.consultaSelecionada.getPacienteNome());
			paciente.setIdade(this.consultaSelecionada.getIdade());
			paciente.setProntuario(this.consultaSelecionada.getProntuario());
			aacConsulta.setPaciente(paciente);
		
		
			if (aacConsulta != null) {
				try {
					if(ambulatorioFacade.existeDocumentosImprimirPaciente(aacConsulta, false)){
						atenderPacientesAgendadosController.setConsultaSelecionada(aacConsulta);
						atenderPacientesAgendadosController.setListaDocumentosPaciente(obterListaDocumentosPacientePesquisa()); 
						RequestContext.getCurrentInstance().execute("PF('modalFinalizarAtendimentoWG').show()");
					}
				} catch (ApplicationBusinessException e) {
					apresentarExcecaoNegocio(e);
				}
			}
		} else if(this.consultaSelecionadaAba4 != null && TAB_4.equals(selectedTab)){
			
			flagAba1 = false;
			aacConsulta = ambulatorioFacade.obterAacConsultasJoinGradeEEspecialidade(this.consultaSelecionadaAba4.getNumero());
			AipPacientes paciente = new AipPacientes();
			paciente.setNome(this.consultaSelecionadaAba4.getPacienteNome());
			paciente.setIdade(this.consultaSelecionadaAba4.getIdade());
			paciente.setProntuario(this.consultaSelecionadaAba4.getProntuario());
			aacConsulta.setPaciente(paciente);
			
		
			if (aacConsulta != null) {
				try {
					if(ambulatorioFacade.existeDocumentosImprimirPaciente(aacConsulta, false)){
						atenderPacientesAgendadosController.setConsultaSelecionada(aacConsulta);
						atenderPacientesAgendadosController.setListaDocumentosPaciente(obterListaDocumentosPacientePesquisa()); 
						RequestContext.getCurrentInstance().execute("PF('modalFinalizarAtendimentoWG').show()");
					}
				} catch (ApplicationBusinessException e) {
					apresentarExcecaoNegocio(e);
				}
			}
		}
	}
	
	public List<DocumentosPacienteVO> obterListaDocumentosPacientePesquisa() throws ApplicationBusinessException {
		
		 if (flagAba1 == true){
			 listaDocumentosPaciente = this.ambulatorioFacade.obterListaDocumentosPaciente(this.consultaSelecionada.getNumero(), this.consultaSelecionada.getGradeSeq(), false);
		 } else{
			 listaDocumentosPaciente = this.ambulatorioFacade.obterListaDocumentosPaciente(this.consultaSelecionadaAba4.getNumero(), this.consultaSelecionadaAba4.getGradeSeq(), false);
		 }

		 if(listaDocumentosPaciente != null && !listaDocumentosPaciente.isEmpty()){
			 for (DocumentosPacienteVO element : listaDocumentosPaciente) {
				 element.setSelecionado(false);
			 }
		}
		 return listaDocumentosPaciente;
	}
	
	public String formatarProntuario(Integer prontuario) {

		if (prontuario != null) {
			String prontuarioStr = prontuario.toString();

			if (prontuarioStr.length() > 1) {
				StringBuilder retorno = new StringBuilder();

				retorno.append(prontuarioStr.substring(0, prontuarioStr.length() - 2)).append('/')
				.append(prontuarioStr.substring(prontuarioStr.length() - 1));
				
				return retorno.toString();
			} else {
				return prontuarioStr;
			}
		}

		return null;
	}

	public String formatarValorTamanhoLimite(String valor, int tamanho) {

		if (valor != null) {
			if (valor.length() > tamanho) {
				return valor.substring(0, tamanho) + "...";
			}

			return valor;
		}
		
		return null;
	}
	
	public Boolean getVerificaItemSelecionado(){
		if(selectedTab.equals(TAB_1)){
			return this.consultaSelecionada != null;
		} else if(selectedTab.equals(TAB_4)){
			return this.consultaSelecionadaAba4 != null; 
		} else{
			return false;
		}
	}
	
	public String redirecionarPesquisaConsultasGrade(ConsultaAmbulatorioVO consultaAmbulatorioVO){
		GradeAgendamentoVO gradeAgendamentoVO = new GradeAgendamentoVO();
		gradeAgendamentoVO.setSeq(consultaAmbulatorioVO.getGradeSeq());
		gradeAgendamentoVO.setSala(consultaAmbulatorioVO.getGradeUnidIdsala());
		gradeAgendamentoVO.setSigla(consultaAmbulatorioVO.getGradeUnidadeSigla());
		gradeAgendamentoVO.setNomeEquipe(consultaAmbulatorioVO.getGradeEquipeNome());
		gradeAgendamentoVO.setNomeEspecialidade(consultaAmbulatorioVO.getGradeEspNome());
		gradeAgendamentoVO.setSiglaEspecialidade(consultaAmbulatorioVO.getGradeEspSigla());
		gradeAgendamentoVO.setNomeServidorProfEspecialidade(buscaCons(consultaAmbulatorioVO));
		
		ConsultasGradeVO filtro = new ConsultasGradeVO();
		filtro.setSeqGrade(consultaAmbulatorioVO.getGradeSeq());
		filtro.setNumeroConsulta(consultaAmbulatorioVO.getNumero());
		filtro.setDataInicial(null);
		
		pesquisaConsultasGradePaginatorController.setGradeAgendamentoVO(gradeAgendamentoVO);
		pesquisaConsultasGradePaginatorController.setIsAgendados(true);
		pesquisaConsultasGradePaginatorController.setIsLimpou(false);
		pesquisaConsultasGradePaginatorController.setFiltro(filtro);
		return "ambulatorio-pesquisarConsultasGrade";
	}
	
	
	/**GET/SET**/
	public Boolean getPesquisaAtiva() {
		return pesquisaAtiva;
	}

	public void setPesquisaAtiva(Boolean pesquisaAtiva) {
		this.pesquisaAtiva = pesquisaAtiva;
	}

	public List<ConsultaAmbulatorioVO> getPacientesAtendimentoList() {
		return pacientesAtendimentoList;
	}

	public void setPacientesAtendimentoList(
			List<ConsultaAmbulatorioVO> pacientesAtendimentoList) {
		this.pacientesAtendimentoList = pacientesAtendimentoList;
	}

	public List<ConsultaAmbulatorioVO> getPacientesAtendidosList() {
		return pacientesAtendidosList;
	}

	public void setPacientesAtendidosList(List<ConsultaAmbulatorioVO> pacientesAtendidosList) {
		this.pacientesAtendidosList = pacientesAtendidosList;
	}

	public List<PesquisarConsultasPendentesVO> getPacientesPendentesList() {
		return pacientesPendentesList;
	}

	public void setPacientesPendentesList(List<PesquisarConsultasPendentesVO> pacientesPendentesList) {
		this.pacientesPendentesList = pacientesPendentesList;
	}
	
	public List<ConsultaAmbulatorioVO> getPacientesAusentesList() {
		return pacientesAusentesList;
	}

	public void setPacientesAusentesList(List<ConsultaAmbulatorioVO> pacientesAusentesList) {
		this.pacientesAusentesList = pacientesAusentesList;
	}

	public ProcedHospEspecialidadeVO getProcedimentoHospEspecialidadeVO() {
		return procedimentoHospEspecialidadeVO;
	}

	public void setProcedimentoHospEspecialidadeVO(
			ProcedHospEspecialidadeVO procedimentoHospEspecialidadeVO) {
		this.procedimentoHospEspecialidadeVO = procedimentoHospEspecialidadeVO;
	}

	public void setConsultaSelecionadaRetorno(ConsultaAmbulatorioVO consultaSelecionadaRetorno) {
		this.consultaSelecionadaRetorno = consultaSelecionadaRetorno;
	}
	
	public ConsultaAmbulatorioVO getConsultaSelecionadaRetorno() {
		return consultaSelecionadaRetorno;
	}

	public void setRetorno(AacRetornos retorno) {
		this.retorno = retorno;
	}

	public AacRetornos getRetorno() {
		return retorno;
	}


	public String getLabelSala() {
		return labelSala;
	}


	public void setLabelSala(String labelSala) {
		this.labelSala = labelSala;
	}


	public String getTitleSala() {
		return titleSala;
	}


	public void setTitleSala(String titleSala) {
		this.titleSala = titleSala;
	}


	public String getTitleZona() {
		return titleZona;
	}
	
	
	public void setTitleZona(String titleZona) {
		this.titleZona = titleZona;
	}

	public AacConsultas getConsulta() {
		return consulta;
	}


	public void setConsulta(AacConsultas consulta) {
		this.consulta = consulta;
	}


	public Boolean getExibirModalChaveSisreg() {
		return exibirModalInformarChaveSisreg;
	}

	public Boolean getExibirModalInformarChaveSisreg() {
		return exibirModalInformarChaveSisreg;
	}
	
	public void setExibirModalChaveSisreg(Boolean exibirModalChaveSisreg) {
		this.exibirModalInformarChaveSisreg = exibirModalChaveSisreg;
	}


	public Boolean getExecutarMetodoChegou() {
		return executarMetodoChegou;
	}


	public void setExecutarMetodoChegou(Boolean executarMetodoChegou) {
		this.executarMetodoChegou = executarMetodoChegou;
	}
	
	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getComeFrom() {
		return comeFrom;
	}

	public void setComeFrom(String comeFrom) {
		this.comeFrom = comeFrom;
	}

	public IFaturamentoFacade getFaturamentoFacade() {
		return faturamentoFacade;
	}


	public void setFaturamentoFacade(IFaturamentoFacade faturamentoFacade) {
		this.faturamentoFacade = faturamentoFacade;
	}

	public Boolean getImprimirAutorizacao() {
		return imprimirAutorizacao;
	}


	public void setImprimirAutorizacao(Boolean imprimirAutorizacao) {
		this.imprimirAutorizacao = imprimirAutorizacao;
	}


	public Boolean getImprimirControleFrequencia() {
		return imprimirControleFrequencia;
	}


	public void setImprimirControleFrequencia(Boolean imprimirControleFrequencia) {
		this.imprimirControleFrequencia = imprimirControleFrequencia;
	}


	public IExamesFacade getExamesFacade() {
		return examesFacade;
	}


	public void setExamesFacade(IExamesFacade examesFacade) {
		this.examesFacade = examesFacade;
	}

	public Integer getOpenToggle() {
		if (PERC_MIN.equals(percPanel)){
			openToggle = 0;			
			dataModel.limparPesquisa();			
		}else{
			openToggle = -1;		
		}
		return openToggle;
	}

	public StreamedContent getMedia() {
		return media;
	}


	public void setMedia(StreamedContent media) {
		this.media = media;
	}


	public void setOpenToggle(Integer openToggle) {
		this.openToggle = openToggle;
	}


	public ConsultaAmbulatorioVO getConsultaSelecionadaAba1() {
		return consultaSelecionadaAba1;
	}


	public void setConsultaSelecionadaAba1(ConsultaAmbulatorioVO consultaSelecionadaAba1) {
		this.consultaSelecionadaAba1 = consultaSelecionadaAba1;
	}


	public ConsultaAmbulatorioVO getConsultaSelecionadaAba2() {
		return consultaSelecionadaAba2;
	}


	public void setConsultaSelecionadaAba2(ConsultaAmbulatorioVO consultaSelecionadaAba2) {
		this.consultaSelecionadaAba2 = consultaSelecionadaAba2;
	}


	public ConsultaAmbulatorioVO getConsultaSelecionadaAba3() {
		return consultaSelecionadaAba3;
	}

	public void setConsultaSelecionadaAba3(ConsultaAmbulatorioVO consultaSelecionadaAba3) {
		this.consultaSelecionadaAba3 = consultaSelecionadaAba3;
	}


	public ConsultaAmbulatorioVO getConsultaSelecionadaAba4() {
		return consultaSelecionadaAba4;
	}


	public void setConsultaSelecionadaAba4(ConsultaAmbulatorioVO consultaSelecionadaAba4) {
		this.consultaSelecionadaAba4 = consultaSelecionadaAba4;
	}
	
	/**
	 * Método que obtém a consulta selecionada da grid, conforme a aba selecionada
	 * #42797
	 * 
	 * @return Instância do ConsultaAmbulatorioVO
	 */
	public ConsultaAmbulatorioVO obterConsultaSelecionadaAba(){
		ConsultaAmbulatorioVO linhaSelecionada = null;

		if (selectedTab.equals(TAB_1) && consultaSelecionada != null) {
			linhaSelecionada = consultaSelecionada;
		} else if (selectedTab.equals(TAB_2) && consultaSelecionadaAba2 != null) {
			linhaSelecionada = consultaSelecionadaAba2;
		} else if (selectedTab.equals(TAB_3) && consultaSelecionadaAba3 != null) {
			linhaSelecionada = consultaSelecionadaAba3;
		} else if (selectedTab.equals(TAB_4) && consultaSelecionadaAba4 != null) {
			linhaSelecionada = consultaSelecionadaAba4;
		}

		return linhaSelecionada;
	}
	
	/**
	 * Método que redireciona para tela de pesquisa controles do paciente
	 * #42797
	 */
	public void redirecionarControlePaciente(){
		ConsultaAmbulatorioVO consultaAmbulatorioVO = this.obterConsultaSelecionadaAba();
		
		if (consultaAmbulatorioVO != null){
			Integer seqAtend = ambulatorioFacade.obterAtendimentoPorConNumero(consultaAmbulatorioVO.getNumero());
			
			this.registrosPacienteController.setCodigoPaciente(consultaAmbulatorioVO.getPacienteCodigo());
			this.registrosPacienteController.setAtdSeq(seqAtend);
			this.registrosPacienteController.setVoltarPara(PAGE_PESQUISAR_PACIENTES_AGENDADOS);
			this.registrosPacienteController.setZona(this.zona);
			this.registrosPacienteController.setUnfSeq(this.zona.getUnfSeq());
			this.redirecionarPaginaPorAjax("/pages/controlepaciente/visualizarRegistrosControle.xhtml");			
		}
	}

	public PesquisaPacienteController getPesquisaPacienteController() {
		return pesquisaPacienteController;
	}


	public void setPesquisaPacienteController(
			PesquisaPacienteController pesquisaPacienteController) {
		this.pesquisaPacienteController = pesquisaPacienteController;
	}


	public Integer getCodigoPacienteTrocado() {
		return codigoPacienteTrocado;
	}


	public void setCodigoPacienteTrocado(Integer codigoPacienteTrocado) {
		this.codigoPacienteTrocado = codigoPacienteTrocado;
	}
	
	public Boolean getCarregarModal() {
		return carregarModal;
	}


	public void setCarregarModal(Boolean carregarModal) {
		this.carregarModal = carregarModal;
	}


	public List<DocumentosPacienteVO> getListaDocumentosPaciente() {
		return listaDocumentosPaciente;
	}


	public void setListaDocumentosPaciente(
			List<DocumentosPacienteVO> listaDocumentosPaciente) {
		this.listaDocumentosPaciente = listaDocumentosPaciente;
	}


	public Boolean getFlagModalImprimir() {
		return flagModalImprimir;
	}


	public void setFlagModalImprimir(Boolean flagModalImprimir) {
		this.flagModalImprimir = flagModalImprimir;
	}


	public Boolean getFlagSelecao() {
		return flagSelecao;
	}


	public void setFlagSelecao(Boolean flagSelecao) {
		this.flagSelecao = flagSelecao;
	}

	public Boolean isImprimirApac() {
		return imprimirApac;
	}


	public void isImprimirApac(Boolean imprimirApac) {
		this.imprimirApac = imprimirApac;
	}

	public DynamicDataModel<ConsultaAmbulatorioVO> getDataModel() {
		return dataModel;
	}


	public void setDataModel(DynamicDataModel<ConsultaAmbulatorioVO> dataModel) {
		this.dataModel = dataModel;
	}


	public String getAtualizarModal() {
		return atualizarModal;
	}


	public void setAtualizarModal(String atualizarModal) {
		this.atualizarModal = atualizarModal;
	}

	public Boolean getFlagAba1() {
		return flagAba1;
	}


	public void setFlagAba1(Boolean flagAba1) {
		this.flagAba1 = flagAba1;
	}

	public Boolean getHouveErroRelatorio() {
		return houveErroRelatorio;
	}

	public void setHouveErroRelatorio(Boolean houveErroRelatorio) {
		this.houveErroRelatorio = houveErroRelatorio;
	}
	
	/**
	 * Executa C16 #47668
	 * @param laudoSolicitacaoAutorizacaoProcedAmbVO
	 * @return 
	 * polula campos de 36 a 40 se bloco justificativa para a foto
	 */
	public LaudoSolicitacaoAutorizacaoProcedAmbVO pesquisarJustificativaFoto(LaudoSolicitacaoAutorizacaoProcedAmbVO laudoSolicitacaoAutorizacaoProcedAmbVO){
		CidVO temp = ambulatorioFacade.pesquisarJustificativaFoto(consultaSelecionadaAba1.getNumero());
		if(temp!=null){
			laudoSolicitacaoAutorizacaoProcedAmbVO.setDescricaoCid(temp.getDescricao());
			laudoSolicitacaoAutorizacaoProcedAmbVO.setCid10principal(temp.getCodigo());
		}
		laudoSolicitacaoAutorizacaoProcedAmbVO.setCid10("");
		laudoSolicitacaoAutorizacaoProcedAmbVO.setCid10causas("");
		laudoSolicitacaoAutorizacaoProcedAmbVO.setObservacaoLap("");
		return laudoSolicitacaoAutorizacaoProcedAmbVO;
	}
	
	/**
	 * C19 #47668
	 * popula campos 18 e 19
	 * @param laudoSolicitacaoAutorizacaoProcedAmbVO
	 * @return
	 */
	public LaudoSolicitacaoAutorizacaoProcedAmbVO obterCodigoTabelaDescricao(LaudoSolicitacaoAutorizacaoProcedAmbVO laudoSolicitacaoAutorizacaoProcedAmbVO){
		try {
			FatConvGrupoItemProced temp = ambulatorioFacade.obterCodigoTabelaDescricaoPorPhiSeq(parametroFacade.obterAghParametro(AghuParametrosEnum.P_AGHU_PHI_FOTO).getVlrNumerico().intValue());
			if(temp != null && temp.getItemProcedHospitalar() != null){
				laudoSolicitacaoAutorizacaoProcedAmbVO.setCodigoTabela(temp.getItemProcedHospitalar().getCodTabela());
				laudoSolicitacaoAutorizacaoProcedAmbVO.setDescricaoIph(temp.getItemProcedHospitalar().getDescricao());
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return laudoSolicitacaoAutorizacaoProcedAmbVO;
	}
	
	/**
	 * Executa C17 #47668 
	 * Popula campos de 41 a 45 se solicitaÃ§Ã£o para pÃ³s-transplante e foto
	 * @param laudoSolicitacaoAutorizacaoProcedAmbVO
	 * @return
	 * @throws ApplicationBusinessException 
	 * 
	 */
	public LaudoSolicitacaoAutorizacaoProcedAmbVO setarNomeEspecialidadeDataConsulta(LaudoSolicitacaoAutorizacaoProcedAmbVO laudoSolicitacaoAutorizacaoProcedAmbVO) throws ApplicationBusinessException{
		AacConsultas temp = ambulatorioFacade.obternomeEspecialidadeDataConsulta(consultaSelecionadaAba1.getNumero());
		if(temp!=null && temp.getGradeAgendamenConsulta() != null && temp.getGradeAgendamenConsulta().getEquipe() != null ){
			LaudoSolicitacaoAutorizacaoProcedAmbVO laudoSolicitacaoAutorizacaoProcedAmbVO2 = aghuFacade.obterEquipePorSeq(temp.getGradeAgendamenConsulta().getEquipe().getSeq());
			BigDecimal tipoCns = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_TIPO_INF_CNS).getVlrNumerico();
			RapPessoaTipoInformacoes tipoInformacaoCns = registroColaboradorFacade.obterTipoInformacao(laudoSolicitacaoAutorizacaoProcedAmbVO2.getPesCodigo(), tipoCns.shortValue());
			laudoSolicitacaoAutorizacaoProcedAmbVO.setNomeProfissionalSolicitante(laudoSolicitacaoAutorizacaoProcedAmbVO2.getNomeProfissionalSolicitante());
			laudoSolicitacaoAutorizacaoProcedAmbVO.setNumeroCnsProfissionalSolicitante(tipoInformacaoCns.getValor());
		}
		laudoSolicitacaoAutorizacaoProcedAmbVO.setDataSolicitacao(temp.getDtConsulta());
		laudoSolicitacaoAutorizacaoProcedAmbVO.setDocumento("");
		return laudoSolicitacaoAutorizacaoProcedAmbVO;
	}
	
	/**Executa C14 #47668
	 * Obter campos 18 a 20
	 */
	public LaudoSolicitacaoAutorizacaoProcedAmbVO obterCodigoDescricaoProcedimentoProTransplante(LaudoSolicitacaoAutorizacaoProcedAmbVO laudoSolicitacaoAutorizacaoProcedAmbVO){
		try {
			FatProcedHospInternos temp = ambulatorioFacade.obterCodigoDescricaoProcedimentoProTransplante(parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_ACOMP_POS_CORNEA).getVlrNumerico().longValue(),
					parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_ACOMP_POS).getVlrNumerico().longValue(),
					consultaSelecionadaAba1.getPacienteCodigo());
			final AghParametros convenioSUS = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_CONVENIO_SUS);
			final AghParametros planoSUS = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_SUS_PLANO_AMBULATORIO);
			final AghParametros tipoGrupoContaSUS = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_TIPO_GRUPO_CONTA_SUS);
			VFatAssociacaoProcedimento fatAssociacaoProcedimento = ambulatorioFacade
					.obterCodigoTabelaEDescricao(
							temp.getSeq(),
							Short.valueOf(parametroFacade
									.buscarAghParametro(
											AghuParametrosEnum.P_TABELA_FATUR_PADRAO)
									.getVlrNumerico().toString()), convenioSUS.getVlrNumerico().shortValue(),
							planoSUS.getVlrNumerico().byteValue(), tipoGrupoContaSUS.getVlrNumerico().shortValue());
			if(temp!=null){
				laudoSolicitacaoAutorizacaoProcedAmbVO.setCodigoTabela(fatAssociacaoProcedimento.getId().getCodTabela());
				laudoSolicitacaoAutorizacaoProcedAmbVO.setDescricaoIph(temp.getDescricao());
			}
			laudoSolicitacaoAutorizacaoProcedAmbVO.setQuantidade(1);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		
		return laudoSolicitacaoAutorizacaoProcedAmbVO;
	}
	
	/**
	 * Executa C15 #47668
	 * setar campos de 36 a 40
	 * @param laudoSolicitacaoAutorizacaoProcedAmbVO
	 * @return
	 */
	public LaudoSolicitacaoAutorizacaoProcedAmbVO pesquisarCodigoDescricaoCidPorAghParametro(LaudoSolicitacaoAutorizacaoProcedAmbVO laudoSolicitacaoAutorizacaoProcedAmbVO){
		String[] parametros={"P_AGHU_CID_CORACAO","P_AGHU_CID_FIGADO","P_AGHU_CID_RIM","P_AGHU_CID_PULMAO"};
		CidVO temp = ambulatorioFacade.pesquisarCodigoDescricaoCidPorAghParametro(consultaSelecionadaAba1.getNumero(), parametros);
		if(temp!=null){
			laudoSolicitacaoAutorizacaoProcedAmbVO.setDescricaoCid(temp.getDescricao());
			laudoSolicitacaoAutorizacaoProcedAmbVO.setCid10principal(temp.getCodigo());
		}
		laudoSolicitacaoAutorizacaoProcedAmbVO.setCid10("");
		laudoSolicitacaoAutorizacaoProcedAmbVO.setCid10causas("");
		laudoSolicitacaoAutorizacaoProcedAmbVO.setObservacaoLap("");
		return laudoSolicitacaoAutorizacaoProcedAmbVO;
	}


	public PesquisarConsultasPendentesVO getConsultaPendenteSelecionadaAba5() {
		return consultaPendenteSelecionadaAba5;
	}

	public void setConsultaPendenteSelecionadaAba5(
			PesquisarConsultasPendentesVO consultaPendenteSelecionadaAba5) {
		this.consultaPendenteSelecionadaAba5 = consultaPendenteSelecionadaAba5;
	}

	public String getOrderPropertyListaPacientes() {
		return orderPropertyListaPacientes;
	}

	public void setOrderPropertyListaPacientes(String orderPropertyListaPacientes) {
		this.orderPropertyListaPacientes = orderPropertyListaPacientes;
	}

	public Boolean getOrderListaPacientesAsc() {
		return orderListaPacientesAsc;
	}

	public void setOrderListaPacientesAsc(Boolean orderListaPacientesAsc) {
		this.orderListaPacientesAsc = orderListaPacientesAsc;
	}

	public Boolean getChamouPesquisar() {
		return chamouPesquisar;
	}

	public void setChamouPesquisar(Boolean chamouPesquisar) {
		this.chamouPesquisar = chamouPesquisar;
	}
	
	public Integer getPosicaoScroll() {
		return posicaoScroll;
	}

	public void setPosicaoScroll(Integer posicaoScroll) {
		this.posicaoScroll = posicaoScroll;
	}
	
	public boolean verificarEspecialidade(){
		if (especialidade != null){
			return true;
		}else{
			return false;
		}
	}
	
	public boolean verificarCaracteristicaApac(){
		return (imprimirControleFrequencia != null && imprimirControleFrequencia)
				|| (imprimirAutorizacao != null && imprimirAutorizacao);
	}
	
	public boolean verificarEspecialidadeApac(){
		boolean candApacFoto=false;
		boolean candApacOtorrino=false;
		
		if (aghuFacade.listarCaracteristicasEspecialidadesCount(especialidade.getSeq(), DominioCaracEspecialidade.CAND_APAC_FOTO) == 1){
			candApacFoto = true;
		}
		if(aghuFacade.listarCaracteristicasEspecialidadesCount(especialidade.getSeq(), DominioCaracEspecialidade.CAND_APAC_OTORRINO) == 1){
			candApacOtorrino = true;
		}
		return (candApacFoto && candApacOtorrino) ;
	}
	
	public boolean verificarEspecialidadeApacPorPaciente(ConsultaAmbulatorioVO consulta){
		boolean candApacFoto=false;
		boolean candApacOtorrino=false;
		consultaSelecionadaAba1 = consulta;
		
		if (aghuFacade.listarCaracteristicasEspecialidadesCount(consulta.getGradeEspSeq(), DominioCaracEspecialidade.CAND_APAC_FOTO) == 1){
			candApacFoto = true;
		}
		if(aghuFacade.listarCaracteristicasEspecialidadesCount(consulta.getGradeEspSeq(), DominioCaracEspecialidade.CAND_APAC_OTORRINO) == 1){
			candApacOtorrino = true;
		}
		return (candApacFoto && candApacOtorrino) ;
	}



	public Boolean getFlagCodigoCentral() {
		return flagCodigoCentral;
	}



	public void setFlagCodigoCentral(Boolean flagCodigoCentral) {
		this.flagCodigoCentral = flagCodigoCentral;
	}

}