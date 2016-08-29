package br.gov.mec.aghu.ambulatorio.action;

import java.io.IOException;
import java.net.UnknownHostException;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.transaction.SystemException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.DocumentException;

import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.ambulatorio.vo.RelatorioAgendamentoConsultaVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.action.HostRemotoCache;
import br.gov.mec.aghu.core.commons.CoreUtil;
//import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.dominio.DominioDiaSemana;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.cups.cadastrosbasicos.business.ICadastrosBasicosCupsFacade;
import br.gov.mec.aghu.dominio.DominioOrigemConsulta;
import br.gov.mec.aghu.dominio.DominioTipoCups;
import br.gov.mec.aghu.exames.solicitacao.action.SolicitacaoExameController;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.impressao.ISistemaImpressaoCUPS;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AacCondicaoAtendimento;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AacFormaAgendamento;
import br.gov.mec.aghu.model.AacFormaAgendamentoId;
import br.gov.mec.aghu.model.AacGradeAgendamenConsultas;
import br.gov.mec.aghu.model.AacPagador;
import br.gov.mec.aghu.model.AacSituacaoConsultas;
import br.gov.mec.aghu.model.AacTipoAgendamento;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.VAacConvenioPlano;
import br.gov.mec.aghu.model.cups.ImpComputador;
import br.gov.mec.aghu.model.cups.ImpComputadorImpressora;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.prescricaomedica.action.ListaPacientesInternadosController;
import br.gov.mec.aghu.prescricaomedica.vo.ConsultaComplementoVO;
import net.sf.jasperreports.engine.JRException;

/**
 * Controller da listagem de consultas a partir da grade.
 */
@SuppressWarnings({"PMD.AghuTooManyMethods", "PMD.ExcessiveClassLength"})
public class ListarConsultasPorGradeController extends ActionReport {

    private static final String PAGE_MANTER_SUMARIO_ALTA = "prescricaomedica-manterSumarioAlta";
	private static final Log LOG = LogFactory.getLog(ListarConsultasPorGradeController.class);
    private static final long serialVersionUID = 3983819107847268001L;
    private static final String PESQUISA_FONETICA = "paciente-pesquisaPacienteComponente";
    private static final String PAGE_DISPONIBILIDADE_HORARIOS_EMERGENCIA = "ambulatorio-disponibilidadeHorariosEmergencia";
    private static final String PAGE_DISPONIBILIDADE_HORARIOS = "ambulatorio-disponibilidadeHorarios";
	private static final String PAGE_GESTAO_INTERCONSULTAS = "ambulatorio-gestaoInterconsultas";
    
	@Inject
	private HostRemotoCache hostRemoto;
	
    @Inject
    private ISistemaImpressaoCUPS sistemaImpressao;

    @EJB
    private ICadastrosBasicosCupsFacade cadastrosBasicosCupsFacade;

    @EJB
    private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

    /**
     * Dados que serão impressos em PDF.
     */
    private List<RelatorioAgendamentoConsultaVO> colecao = new ArrayList<RelatorioAgendamentoConsultaVO>(0);
    private Map<Integer, ConsultaComplementoVO> complementos = new HashMap<Integer, ConsultaComplementoVO>();
	private List<VAacConvenioPlano> listaConvenios;
	private VAacConvenioPlano convenioPlanoSelecionado;
	private VAacConvenioPlano convenioPlanoSGB;
	
    @EJB
    private IAmbulatorioFacade ambulatorioFacade;

    @EJB
    private IAghuFacade aghuFacade;

    @EJB
    private IParametroFacade parametroFacade;

    @EJB
    private IPacienteFacade pacienteFacade;

	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@EJB
    private ICascaFacade cascaFacade;

    private AacGradeAgendamenConsultas grade;
    private Integer grdSeq;
    List<AacConsultas> consultas = new ArrayList<AacConsultas>(0);
    private AacConsultas consulta;
	
	private AacConsultas consultaConvenio;
	
    private Integer prontuario;
    private Integer pacCodigo;
    private String pacNome;
    private Integer pacCodigoFonetica;
    private AipPacientes pac;
    private Integer numeroDeVias = 1;
    private AacFormaAgendamento formaAgendamento;
    private Boolean consultaExcedente = false;
    private Boolean editar = false;
    private Boolean confirmadaImpressao = true;
    private String labelZona;
    private String labelSala;
    private boolean emergencia;
    private Date dtConsulta;
    private Date mesInicio;
    private Date mesFim;
    private Date horaConsulta;
    private AacPagador pagador;
    private AacTipoAgendamento autorizacao;
    private AacCondicaoAtendimento condicao;
    private DominioDiaSemana diaSemana;
    private boolean primeiraExecucao = true;
    private Boolean exibeModalConfirmacaoImpressao = false;
    private Boolean exibeModalDataNaoProgramada = false;
    private Boolean origemSumarioAlta = false;
    private AacSituacaoConsultas situacaoAnterior;
    private Boolean consultaMarcada = false;
    private AacConsultas consultaSelecionada;
    private String prontuarioSumario;
    private Date dtConsultaExcedenteParametro;
    private AacFormaAgendamento formaAgendamentoExcedenteParametro;
    
	@Inject
	private SolicitacaoExameController solicitacaoExameController;

	@Inject
	private ListaPacientesInternadosController listaPacientesInternadosController;

	@Inject
	private RelatorioAgendaConsultasController relatorioAgendaConsultasController;
	
    private String cameFrom;
    
	public enum ListarConsultasPorGradeControllerExceptionCode implements BusinessExceptionCode {
		MENSAGEM_DADOS_MINIMOS_CONSULTA_PACIENTE, MENSAGEM_RESTRICAO_CONSULTA_PACIENTE, CODIGO_PRONTUARIO_NAO_CORRESPONDEM,
		AIP_PACIENTE_NAO_ENCONTRADO, MENSAGEM_PACIENTE_OBRIGATORIO, MENSAGEM_DT_CONSULTA_OBRIGATORIO, MENSAGEM_CONVENIO_OBRIGATORIO,
		MENSAGEM_FORMA_AGENDAMENTO_OBRIGATORIO, MENSAGEM_CONSULTA_MARCADA, AIP_PACIENTE_NAO_ENCONTRADO_CODIGO, AIP_PACIENTE_NAO_ENCONTRADO_PRONTUARIO,
		MSG_IMPRIMIR_MICRO_SEM_NOME;
	}

    @PostConstruct
    protected void inicializar() {
	this.begin(conversation);
    }

    /**
     * Método executado ao iniciar a controller
     */
    public void iniciar() {
		// Busca as informações do paciente caso já tenha sido feita a pesquisa fonética
		if (pacCodigo != null || prontuario != null || pacCodigoFonetica != null) {
		    obterInformacoesPaciente(true);
		}
	
		if (grade == null) {
		    Enum[] leftJoins = { AacGradeAgendamenConsultas.Fields.V_AAC_SIGLA_UNF_SALA,
			    AacGradeAgendamenConsultas.Fields.FORMA_AGENDAMENTO, AacGradeAgendamenConsultas.Fields.PAGADOR,
			    AacGradeAgendamenConsultas.Fields.TIPO_AGENDAMENTO, AacGradeAgendamenConsultas.Fields.CONDICAO_ATENDIMENTO,
			    AacGradeAgendamenConsultas.Fields.ESPECIALIDADE, AacGradeAgendamenConsultas.Fields.EQUIPE,
			    AacGradeAgendamenConsultas.Fields.PROF_ESPECIALIDADE, AacGradeAgendamenConsultas.Fields.PROF_ESPECIALIDADE_SERVIDOR,
			    AacGradeAgendamenConsultas.Fields.PROF_ESPECIALIDADE_SERVIDOR_PESSOA_FISICA,
			    AacGradeAgendamenConsultas.Fields.UNIDADE_FUNCIONAL };
		    grade = ambulatorioFacade.obterAacGradeAgendamenConsultas(grdSeq, null, leftJoins);
		}
	
		if (consultaExcedente && primeiraExecucao) {
		    inserirConsultaExcedente();
		}
		obterCabecalho();
		primeiraExecucao = false;
		consultaSelecionada = null;
	}
	
	// Usado em overlayPanel que acaba não atualizando ao alterar o convênio, por isso esse método para
	//forçar a atualização
	public String obterDescricaoHintConvenio(AacConsultas consulta) {
		return consulta.getConvenioPlano().getConvenioPlano();
	}
			
	public void selecionarConsultaConvenio(AacConsultas consulta) {
		this.setConsultaConvenio(consulta);
		listaConvenios = ambulatorioFacade.obterListaConvenios();		
		openDialog("modalConveniosWG" );
	}
	
	public void selecionarConvenio(){
		this.consultaConvenio.setConvenioSaudePlano(faturamentoFacade.obterConvenioSaudePlano(convenioPlanoSelecionado.getId().getCnvCodigo(), 
				convenioPlanoSelecionado.getId().getPlano()));
		closeDialog("modalConveniosWG");
	}
	
	public List<AacConsultas> obterConsultas() throws ApplicationBusinessException, ApplicationBusinessException {
		if (consultas != null && !consultas.isEmpty()) {
		    return consultas;
		}
		if (grdSeq == null || grade == null) {
		    return null;
		}
		Short pgdSeq = null;
		Short tagSeq = null;
		Short caaSeq = null;
		if (pagador != null) {
		    pgdSeq = pagador.getSeq();
		}
		if (autorizacao != null) {
		    tagSeq = autorizacao.getSeq();
		}
		if (condicao != null) {
		    caaSeq = condicao.getSeq();
		}
		
		Boolean visualizarPrimeirasConsultasSMS = cascaFacade.usuarioTemPermissao(obterLoginUsuarioLogado(), "visualizarPrimeirasConsultasSMS");
	
		consultas = ambulatorioFacade.obterConsultasNaoRealizadasPorGrade(grade, pgdSeq, tagSeq, caaSeq, emergencia, dtConsulta, mesInicio, mesFim,
																  horaConsulta, diaSemana, this.origemSumarioAlta, visualizarPrimeirasConsultasSMS);
		
		return consultas;
    }

    /**
     * Método que controla a exibição da modal de data não programada
     * 
     * @throws IOException
     * @throws SystemException
     * @throws JRException
     * @throws CloneNotSupportedException
     * @throws AGHUNegocioExceptionSemRollback
     * @throws AGHUNegocioException
     * @throws NumberFormatException
     */
    public String verificarDiaNaoProgramado() throws NumberFormatException, ApplicationBusinessException, CloneNotSupportedException,
	    JRException, SystemException, IOException {
	
		if (this.ambulatorioFacade.verificarConsultaDiaNaoProgramado(this.consulta) && !emergencia) {
		    this.setExibeModalDataNaoProgramada(true);
		} else {
		    this.setExibeModalDataNaoProgramada(false);
		    if (exibeModal(null)) {
		    	this.setExibeModalConfirmacaoImpressao(true);
		    } else {
		    	return this.salvarMarcacaoConsultaFormulario();
		    }
		}
		return null;
    }

    public String verificarConfirmacaoImpressao() throws NumberFormatException, ApplicationBusinessException, CloneNotSupportedException,
	    JRException, SystemException, IOException {
		this.setExibeModalDataNaoProgramada(false);
		if (exibeModal(null)) {
		    this.setExibeModalConfirmacaoImpressao(true);
		} else {
		    return this.salvarMarcacaoConsultaFormulario();
		}
		return null;
	}
	
	public void desabilitarModal() {
		this.setExibeModalDataNaoProgramada(false);
		this.setExibeModalConfirmacaoImpressao(false);
    }

    /**
     * Método que retorna o dia da semana por extenso.
     */
    public String obterDiaSemana(Date data) {
    	DominioDiaSemana diaSemana = CoreUtil.retornaDiaSemana(data);
    	return diaSemana.getDescricao().substring(0, 3);
    }

    /**
     * Obtem as informações do paciente (nome, codigo e prontuario) e retorna se
     * foi possivel obter
     * 
     * @return true caso consiga obter as informações, false caso contrário
     */
    public boolean obterInformacoesPaciente(boolean inicio) {
		if (pacCodigo == null && prontuario == null && pacCodigoFonetica == null) {
		    apresentarMsgNegocio(Severity.ERROR,
			    ListarConsultasPorGradeControllerExceptionCode.MENSAGEM_DADOS_MINIMOS_CONSULTA_PACIENTE.toString());
	
		} else if (prontuario != null && pacCodigo != null && !inicio
			&& (pac == null || (pac != null && (!pac.getProntuario().equals(prontuario) || !pac.getCodigo().equals(pacCodigo))))) {
	
		    apresentarMsgNegocio(Severity.ERROR,
			    ListarConsultasPorGradeControllerExceptionCode.MENSAGEM_RESTRICAO_CONSULTA_PACIENTE.toString());
	
		} else if (prontuario != null && !inicio) {
		    pac = pacienteFacade.obterPacientePorProntuario(prontuario);
		    if (pac != null) {
			pacCodigo = pac.getCodigo();
			pacNome = pac.getNome();
	
		    } else {
			apresentarMsgNegocio(Severity.ERROR, ListarConsultasPorGradeControllerExceptionCode.AIP_PACIENTE_NAO_ENCONTRADO.toString());
			pacNome = null;
			return false;
		    }
		    return true;
	
		} else if (pacCodigo != null && !inicio) {
		    pac = pacienteFacade.obterPacientePorCodigo(pacCodigo);
		    if (pac != null) {
			pacCodigo = pac.getCodigo();
			prontuario = pac.getProntuario();
			pacNome = pac.getNome();
	
		    } else {
			apresentarMsgNegocio(Severity.ERROR, ListarConsultasPorGradeControllerExceptionCode.AIP_PACIENTE_NAO_ENCONTRADO.toString());
			pacNome = null;
			return false;
		    }
		    return true;
		} else if (pacCodigoFonetica != null && inicio) {
		    pac = pacienteFacade.obterPacientePorCodigo(pacCodigoFonetica);
		    pacCodigo = pac.getCodigo();
		    prontuario = pac.getProntuario();
		    pacNome = pac.getNome();
		}
		return false;
    }

    public String obterCabecalho() {
    	return ambulatorioFacade.obterCabecalhoListaConsultasPorGrade(this.grade);
    }

    public void selecionarConsulta(){
    	if(consultaSelecionada != null){
    		//Limpar dados da selação anterior
    		limparSelecaoConsulta();
    		consultaSelecionada.getComplemento().setProntuario(prontuarioSumario);
    		selecionarPacienteConsulta(consultaSelecionada);
    	}
    }

	private void limparSelecaoConsulta() {
		for(AacConsultas consulta: consultas){
			if (consulta.getComplemento() != null && consulta.getComplemento().getNomePaciente() != null
					&& !consulta.getComplemento().getNomePaciente().isEmpty()){
				consulta.getComplemento().setNomePaciente(null);
				consulta.getComplemento().setPacCodigo(null);
				consulta.getComplemento().setProntuario(null);
				this.complementos.remove(consulta.getNumero());
			}
		}
	}
    
    public void selecionarPacienteConsulta(AacConsultas cons) {
		if ((cons.getComplemento().getPacCodigo() != null)
			|| (cons.getComplemento().getProntuario() != null && !cons.getComplemento().getProntuario().isEmpty())) {
	
		    AipPacientes paciente = ambulatorioFacade.obterPacienteConsulta(cons);
	
		    if (paciente != null) {
				cons.getComplemento().setNomePaciente(paciente.getNome());
				cons.getComplemento().setPacCodigo(paciente.getCodigo());
				if (paciente.getProntuario() != null) {
				    cons.getComplemento().setProntuario(paciente.getProntuario().toString());
				}
				
				this.sugerirConvenioConsulta(cons);
					
				this.complementos.put(cons.getNumero(), cons.getComplemento());
	
		    } else {
		    	apresentarMsgNegocio(Severity.WARN, "MENSAGEM_NENHUM_PACIENTE_ENCONTRADO");
		    }
		}
    }
    
    // #51930 - Ambulatório - Sugestão automática do convênio na marcação de Consultas
    public void sugerirConvenioConsulta(AacConsultas cons) {
    	try {
			if(cons.getTipoAgendamento()!= null) {
				Short codigoAutorizacaoHcpa = parametroFacade.buscarValorShort(AghuParametrosEnum.P_TAG_HCPA);
				Short codigoAutorizacaoUfrgs = parametroFacade.buscarValorShort(AghuParametrosEnum.P_TAG_UFRGS);
				Short codigoConvenioSus = parametroFacade.buscarValorShort(AghuParametrosEnum.P_AGHU_CONVENIO_SUS);
				Short codigoPagadorSemCobertura = parametroFacade.buscarValorShort(AghuParametrosEnum.P_PGD_SCOBERTURA);
				if (cons.getPagador().getSeq() == codigoPagadorSemCobertura){
					Short codigoConvenioSemCobertura = parametroFacade.buscarValorShort(AghuParametrosEnum.P_CONVENIO_FUNC);
					Byte codigoPlanoSemCobertura = parametroFacade.buscarValorByte(AghuParametrosEnum.P_PLANO_FUNC);
					this.setConsultaConvenio(cons);
					this.consultaConvenio.setConvenioSaudePlano(faturamentoFacade.obterConvenioSaudePlano(codigoConvenioSemCobertura, codigoPlanoSemCobertura));
				}
				else if(cons.getTipoAgendamento().getSeq() == codigoAutorizacaoHcpa) {
					Byte codigoPlanoHcpa = parametroFacade.buscarValorByte(AghuParametrosEnum.P_PLANO_HCPA);
					this.setConsultaConvenio(cons);
					this.consultaConvenio.setConvenioSaudePlano(faturamentoFacade.obterConvenioSaudePlano(codigoConvenioSus, codigoPlanoHcpa));
				}
				else if(cons.getTipoAgendamento().getSeq() == codigoAutorizacaoUfrgs) {
					Byte codigoPlanoUfrgs = parametroFacade.buscarValorByte(AghuParametrosEnum.P_PLANO_UFRGS);
					this.setConsultaConvenio(cons);
					this.consultaConvenio.setConvenioSaudePlano(faturamentoFacade.obterConvenioSaudePlano(codigoConvenioSus, codigoPlanoUfrgs));		
				}
			}
		} catch (ApplicationBusinessException e) {
		    apresentarExcecaoNegocio(e);
		}
    }
    
    // #51930 - Ambulatório - Sugestão automática do convênio na marcação de Consultas
    public void sugerirConvenioConsultaPorFormaAgendamento() {
    	if(formaAgendamento!=null && formaAgendamento.getTipoAgendamento()!= null) {
    		try {
				Short codigoAutorizacaoHcpa = parametroFacade.buscarValorShort(AghuParametrosEnum.P_TAG_HCPA);
				Short codigoAutorizacaoUfrgs = parametroFacade.buscarValorShort(AghuParametrosEnum.P_TAG_UFRGS);
				Short codigoConvenioSus = parametroFacade.buscarValorShort(AghuParametrosEnum.P_AGHU_CONVENIO_SUS);
				Short codigoPagadorSemCobertura = parametroFacade.buscarValorShort(AghuParametrosEnum.P_PGD_SCOBERTURA);
				
				if (formaAgendamento.getPagador().getSeq() == codigoPagadorSemCobertura){
					Short codigoConvenioSemCobertura = parametroFacade.buscarValorShort(AghuParametrosEnum.P_CONVENIO_FUNC);
					Byte codigoPlanoSemCobertura = parametroFacade.buscarValorByte(AghuParametrosEnum.P_PLANO_FUNC);
					this.convenioPlanoSGB = ambulatorioFacade.obterVAacConvenioPlanoAtivoPorId(codigoConvenioSemCobertura, codigoPlanoSemCobertura);
				}
				else if(formaAgendamento.getTipoAgendamento().getSeq() == codigoAutorizacaoHcpa) {
					Byte codigoPlanoHcpa = parametroFacade.buscarValorByte(AghuParametrosEnum.P_PLANO_HCPA);
					this.convenioPlanoSGB = ambulatorioFacade.obterVAacConvenioPlanoAtivoPorId(codigoConvenioSus, codigoPlanoHcpa);
				}
				else if(formaAgendamento.getTipoAgendamento().getSeq() == codigoAutorizacaoUfrgs) {
					Byte codigoPlanoUfrgs = parametroFacade.buscarValorByte(AghuParametrosEnum.P_PLANO_UFRGS);
					this.convenioPlanoSGB = ambulatorioFacade.obterVAacConvenioPlanoAtivoPorId(codigoConvenioSus, codigoPlanoUfrgs);	
				}
    		} catch (ApplicationBusinessException e) {
    			apresentarExcecaoNegocio(e);
    		}
    	}
    }
    
    public void limparConvenioConsulta() {
    	this.convenioPlanoSGB = null;
    }
    
    public String voltar() {
		fecharEdicao();
		primeiraExecucao = true;
		grade = null;
		consultas = null;
	    cameFrom = null;
	    origemSumarioAlta = false;
		if (emergencia) {
		    return PAGE_DISPONIBILIDADE_HORARIOS_EMERGENCIA;
		} else {
		    return PAGE_DISPONIBILIDADE_HORARIOS;
		}
    }
    public void limparPaciente() {
		pac = null;
		pacCodigo = null;
		pacNome = null;
		prontuario = null;
    }
    public void selecionarPacienteConsultaEdicao() {
		if (pac != null || pacCodigo != null || prontuario != null) {
		    if (pacCodigo != null) {
		    	pac = pacienteFacade.obterPacientePorCodigo(pacCodigo);
		    	if (pac == null) {
			    	apresentarMsgNegocio(Severity.ERROR, 
			    			ListarConsultasPorGradeControllerExceptionCode.AIP_PACIENTE_NAO_ENCONTRADO_CODIGO.toString(), pacCodigo);
			    	this.limparPaciente();
		    	}
		    } else if (prontuario != null) {
		    	pac = pacienteFacade.obterPacientePorProntuario(prontuario);
		    	if (pac == null) {
			    	apresentarMsgNegocio(Severity.ERROR, 
			    			ListarConsultasPorGradeControllerExceptionCode.AIP_PACIENTE_NAO_ENCONTRADO_PRONTUARIO.toString(), prontuario);
			    	this.limparPaciente();
				}
		    }
		} 
		if (pac != null) {
		    pacCodigo = pac.getCodigo();
		    prontuario = pac.getProntuario();
		    pacNome = pac.getNome();
		}
    }
    public String salvarMarcacaoConsultaSemImprimir() throws ApplicationBusinessException {
		confirmadaImpressao = false;
		return salvarMarcacaoConsulta(consulta);
    }
    public String salvarMarcacaoConsulta() throws ApplicationBusinessException {
		this.setExibeModalConfirmacaoImpressao(false);
		confirmadaImpressao = true;
		return salvarMarcacaoConsulta(consulta);
    }
    public String salvarMarcacaoConsulta(AacConsultas cons) throws ApplicationBusinessException {
		//setEditar(true);
		if (cons.getComplemento().getPacCodigo() != null && cons.getComplemento().getNomePaciente() != null) {
		    this.pacCodigo = cons.getComplemento().getPacCodigo();
		    selecionarPacienteConsultaEdicao();
		} else {
		    this.pac = null;
		    this.pacCodigo = null;
		    this.prontuario = null;
		    this.pacNome = null;
		}
		this.consulta = obterAacConsultasFull(cons.getNumero());
		consulta.setPaciente(pac);
		consulta.setComplemento(cons.getComplemento());
	
		consulta.setConvenioSaudePlano(cons.getConvenioSaudePlano());
		
		this.formaAgendamento = consulta.getFormaAgendamento();
		String returnValue = null;
		salvar();
		if(this.consultaMarcada) {
			returnValue = print();
		}

		return returnValue;
    }
    private AacConsultas obterAacConsultasFull(final Integer numero) {
		final Enum[] innerJoin = { AacConsultas.Fields.GRADE_AGENDA_CONSULTA, AacConsultas.Fields.GRADE_AGENDA_CONSULTA_ESPECIALIDADE };
		final Enum[] leftJoin = { AacConsultas.Fields.SITUACAO_CONSULTA, AacConsultas.Fields.CONSULTA, AacConsultas.Fields.PACIENTE,
			AacConsultas.Fields.RETORNO, AacConsultas.Fields.PROJETO_PESQUISA, AacConsultas.Fields.PAGADOR,
			AacConsultas.Fields.TIPO_AGENDAMENTO, AacConsultas.Fields.CONVENIO_SAUDE_PLANO,
			AacConsultas.Fields.CONVENIO_SAUDE_PLANO_CV, AacConsultas.Fields.CONDICAO_ATENDIMENTO, AacConsultas.Fields.SERVIDOR,
			AacConsultas.Fields.SERVIDOR_PESSOA_FISICA, AacConsultas.Fields.FORMA_AGENDAMENTO };
		return ambulatorioFacade.obterConsultaPorNumero(numero, innerJoin, leftJoin);
    }
    public void limparMarcacaoConsulta(AacConsultas cons) {
		cons.setPaciente(null);
		cons.setCodCentral(null);
		cons.setComplemento(new ConsultaComplementoVO());
    }
    public void inserirConsultaExcedente() {
		try {
			prontuario = null;
			pacCodigo = null;
			pacNome = null;
			consulta = new AacConsultas();
			consultaConvenio = new AacConsultas();
			pac = null;
			consulta.setGradeAgendamenConsulta(grade);
			if(this.origemSumarioAlta){
	    		limparSelecaoConsulta();
	    		consultaSelecionada = null;
				prontuario = Integer.valueOf(prontuarioSumario);
				this.selecionarPacienteConsultaEdicao();
				formaAgendamento = buscarFormaAgendamentoPadrao();
				consulta.setFormaAgendamento(formaAgendamento);
			} else {
				formaAgendamento = grade.getFormaAgendamento();
			}
			if (dtConsultaExcedenteParametro != null){
				consulta.setDtConsulta(dtConsultaExcedenteParametro);
			}else{
				consulta.setDtConsulta(new Date());
			}
			if (formaAgendamentoExcedenteParametro != null){
				formaAgendamento = formaAgendamentoExcedenteParametro;
				consulta.setFormaAgendamento(formaAgendamento);
			}
			populaPagador(consulta);
			consultaExcedente = true;
			consulta.setOrigem(DominioOrigemConsulta.A);
		} catch (BaseException e) {
		    apresentarExcecaoNegocio(e);
		    LOG.error(e.getMessage(), e);
		}
    }
    private void populaPagador(AacConsultas consulta) throws ApplicationBusinessException {
		FatConvenioSaudePlano fsp = ambulatorioFacade.popularPagador(consulta.getNumero());
		consulta.setConvenioSaudePlano(fsp);
		if (consulta.getFormaAgendamento() == null && consulta.getGradeAgendamenConsulta().getFormaAgendamento() != null) {
		    consulta.setFormaAgendamento(consulta.getGradeAgendamenConsulta().getFormaAgendamento());
		}
    }
    public List<VAacConvenioPlano> obterConvenios(Object parametro) {
    	return ambulatorioFacade.getListaConvenios((String) parametro);
    }
    public Long obterConveniosCount(Object parametro) {
    	return ambulatorioFacade.getListaConveniosCount((String) parametro);
    }
    public void limpar() {
		if (consultaExcedente) {
		    formaAgendamento = null;
		    consulta.setCodCentral(null);
		    consulta.setMotivo(null);
		    consulta.setDtConsulta(null);
		    consulta.setCodCentral(null);
			convenioPlanoSGB = null;			consultaConvenio = null;
		} else if (this.consulta != null) {
		    setConsulta(consulta);
		}
		if(this.consulta == null){
			pac = null;
			setProntuario(null);
			setPacCodigo(null);
			setPacNome(null);
			setPacCodigoFonetica(null);
		} else {
			pac = null;
			setProntuario(null);
			setPacCodigo(null);
			setPacNome(null);
			setPacCodigoFonetica(null);
			consulta.setCodCentral(null);
		}
    }
    public void fecharEdicao() {
		consulta = null;
		pac = null;
		setProntuario(null);
		setPacCodigo(null);
		setPacNome(null);
		setPacCodigoFonetica(null);
		setConsultaExcedente(false);
		setEditar(false);
		convenioPlanoSGB = null;
    }

    public void cancelar() {
    	fecharEdicao();
    }
    
        
    private String salvar() {
    	String retorno = null;
		
		try {
			if (realizaValidacoes()) {
				return null;
			}
		    situacaoAnterior = this.consulta.getSituacaoConsulta();
		    if (!emergencia) {
		    	this.ambulatorioFacade.verificarConsultaExcedenteDiaBloqueado(this.consulta);
		    }
		    String nomeMicrocomputador = null;
		    try {
		    	nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
		    } catch (UnknownHostException e1) {
		    	LOG.error("Exceção capturada:", e1);
		    }
		    AacConsultas consultaAnterior = obterAacConsultasFull(consulta.getNumero());
		    List<String> msgs = ambulatorioFacade.validarItensPreManterConsulta(consulta);
		    exibirMensagens(msgs);
		    consulta.setComplemento(this.complementos.get(consulta.getNumero()));
			
		    boolean cameFromInterconsultas = false;
		    if (PAGE_GESTAO_INTERCONSULTAS.equals(cameFrom)) {
		    	cameFromInterconsultas = true;
		    }
		    if(this.origemSumarioAlta){
		    	this.formaAgendamento = buscarFormaAgendamentoPadrao();
		    	consulta.setCondicaoAtendimento(formaAgendamento.getCondicaoAtendimento());
		    	consulta.setTipoAgendamento(formaAgendamento.getTipoAgendamento());
		    	consulta.setPagador(formaAgendamento.getPagador());
		    }
		    
		    this.consultaMarcada = false;
		    ambulatorioFacade.manterConsulta( consultaAnterior, consulta,
											  formaAgendamento != null ? formaAgendamento.getId() : null, 
										      emergencia, nomeMicrocomputador, cameFromInterconsultas);
		    this.consultaMarcada = true;

		    consultas.remove(consulta);
			Integer numeroConsulta = consulta.getNumero();
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_CONSULTA_MARCADA");
			validaUnidFuncionaPossuiIndAla();
			if(this.origemSumarioAlta){
				AghAtendimentos atendimento = aghuFacade.buscaAtendimentoPorNumeroConsulta(numeroConsulta);
				if(atendimento != null && atendimento.getSeq() != null){
					solicitacaoExameController.setAtendimentoSeq(atendimento.getSeq());
					solicitacaoExameController.setAtendimento(atendimento);
					solicitacaoExameController.setPaginaChamadora(PAGE_MANTER_SUMARIO_ALTA);
			
					retorno = listaPacientesInternadosController.realizarChamadaSolicitarExame(atendimento.getSeq(), true);
					origemSumarioAlta = false;
				}
			}
			
		} catch (BaseException e) {
			
			this.consultaMarcada = false;
		    if (consultaExcedente) {
				consulta.setNumero(null);
		    } else {
		    	consulta.setSituacaoConsulta(situacaoAnterior);
		    }
		    this.setEditar(false);
		    retorno = null;
		    
		    apresentarExcecaoNegocio(e);
		}
		return retorno;
    }

	private void validaUnidFuncionaPossuiIndAla() {
		if (!ambulatorioFacade.obterAlaPorNumeroConsulta(consulta.getNumero())) {
			apresentarMsgNegocio(Severity.WARN, "IND_ALA_NAO_RELACIONADA");
		}
	}

	private boolean realizaValidacoes() {
		if (ambulatorioFacade.verificarConsultaJaMarcada(this.consulta)){
			apresentarMsgNegocio(Severity.ERROR, "CONSULTA_JA_MARCADA");
			consultas.remove(consulta);
			fecharEdicao();
			return true;
		}
		return false;
	}

	private void exibirMensagens(List<String> msgs) {
		if (msgs != null) {
			for (String msg : msgs) {
				apresentarMsgNegocio(Severity.WARN, msg);
			}
		}
	}
    
    private AacFormaAgendamento buscarFormaAgendamentoPadrao(){
    	Enum[] fields = { AacFormaAgendamento.Fields.CONDICAO_ATENDIMENTO, AacFormaAgendamento.Fields.TIPO_AGENDAMENTO,
    			AacFormaAgendamento.Fields.PAGADOR};
    	AacFormaAgendamentoId formaAgendamentoId = new AacFormaAgendamentoId((short)5,(short)1,(short)1);
    	return ambulatorioFacade.obterAacFormaAgendamentoPorChavePrimaria(formaAgendamentoId, fields,
    			null);
    }

    public String redirecionaRelatorioAgendaConsultas(){
    	relatorioAgendaConsultasController.setSeqGrade(getGrdSeq());
    	relatorioAgendaConsultasController.setDtInicio(new Date());
    	relatorioAgendaConsultasController.setDtFim(buscarMaiorDataGradeConsultada());
    	relatorioAgendaConsultasController.setOrigemSumario(origemSumarioAlta);
    	try {
			return relatorioAgendaConsultasController.print();
		} catch (BaseException | JRException | SystemException | IOException | DocumentException e) {
			apresentarMsgNegocio(Severity.ERROR, "ERRO_VISUALIZAR_RELATORIO_AGENDA_CONSULTAS");
		}
    	return null;
    }
    
    private Date buscarMaiorDataGradeConsultada() {
    	if (consultas != null && !consultas.isEmpty()){
    		return consultas.get(consultas.size() -1).getDtConsulta();
    	} else {
    		return null;
    	}
	}

	private Boolean isGradeUBS() {
    	return aghuFacade.possuiCaracteristicaPorUnidadeEConstante(grade.getUnidadeFuncional().getSeq(),
    			ConstanteAghCaractUnidFuncionais.UBS);
    }

    public String salvarMarcacaoConsultaFormularioSemImprimir() throws ApplicationBusinessException {
    	String retorno = null;
		this.setExibeModalConfirmacaoImpressao(false);
		confirmadaImpressao = false;
		if(convenioPlanoSGB != null && convenioPlanoSGB.getId() != null){
			consulta.setConvenioSaudePlano(faturamentoFacade.obterConvenioSaudePlano(convenioPlanoSGB.getId().getCnvCodigo(), convenioPlanoSGB.getId().getPlano()));
		}
		if (validarCampos()) {
		    consulta.getComplemento().setPacCodigo(pac.getCodigo());
		    this.complementos.put(consulta.getNumero(), consulta.getComplemento());
		    retorno = this.salvar();
		    fecharEdicao();
		}
		return retorno;
    }

    public String salvarMarcacaoConsultaFormulario() throws ApplicationBusinessException {
    	String returnValue = null;
    	
		this.setExibeModalConfirmacaoImpressao(false);
		confirmadaImpressao = true;
		if(convenioPlanoSGB != null && convenioPlanoSGB.getId() != null){
			consulta.setConvenioSaudePlano(faturamentoFacade.obterConvenioSaudePlano(convenioPlanoSGB.getId().getCnvCodigo(), convenioPlanoSGB.getId().getPlano()));
		}
		if (validarCampos()) {
		    consulta.getComplemento().setPacCodigo(pac.getCodigo());
		    this.complementos.put(consulta.getNumero(), consulta.getComplemento());
		    this.salvar();
		    if (this.consultaMarcada) {
		    	returnValue = print();
		    }
		    //fecharEdicao();
		    return returnValue;
		}
		return null;
    }

    public Boolean validarCampos() {
		boolean encontrouErro = false;
		if (pac == null || pac.getCodigo() == null || pac.getNome() == null) {
		    apresentarMsgNegocio(Severity.ERROR, ListarConsultasPorGradeControllerExceptionCode.MENSAGEM_PACIENTE_OBRIGATORIO.toString());
		    encontrouErro = true;
		}
		if (consulta.getDtConsulta() == null) {
		    apresentarMsgNegocio(Severity.ERROR, ListarConsultasPorGradeControllerExceptionCode.MENSAGEM_DT_CONSULTA_OBRIGATORIO.toString());
		    encontrouErro = true;
		}
		if (formaAgendamento == null) {
		    apresentarMsgNegocio(Severity.ERROR,
			    ListarConsultasPorGradeControllerExceptionCode.MENSAGEM_FORMA_AGENDAMENTO_OBRIGATORIO.toString());
		    encontrouErro = true;
		}
	    if(this.origemSumarioAlta){
	    	try {
				this.ambulatorioFacade.verificarExisteConsultaMesmoDiaTurno(this.consulta);
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
				encontrouErro = true;
			}
	    }

		return !encontrouErro;
    }
	
    public List<AacFormaAgendamento> obterFormasAgendamento(String parametro) {
    	return this.returnSGWithCount(
    			ambulatorioFacade.pesquisaFormaAgendamentoPorStringEFormaAgendamento((String) parametro, grade.getPagador(),
    					grade.getTipoAgendamento(), grade.getCondicaoAtendimento()), obterFormasAgendamentoCount(parametro));
    }
	
    public Long obterFormasAgendamentoCount(String parametro) {
		return ambulatorioFacade.pesquisaFormaAgendamentoPorStringEFormaAgendamentoCount((String) parametro, grade.getPagador(),
			grade.getTipoAgendamento(), grade.getCondicaoAtendimento());
    }

	public List<VAacConvenioPlano> obterCovenioPlanoSGB(String pesquisa){		
		return returnSGWithCount(ambulatorioFacade.pesquisarCovenioPlanoSGB(pesquisa), ambulatorioFacade.pesquisarCovenioPlanoSGBCount(pesquisa));
	}

    public String redirecionarPesquisaFonetica() {
    	return PESQUISA_FONETICA;
    }
    
	/**
	 * Retorna impressora matricial do host remoto se houver.
	 * 
	 * @return
	 * @throws UnknownHostException
	 * @throws ApplicationBusinessException 
	 */
	private String getMatricialHost() throws UnknownHostException, ApplicationBusinessException {
		String remoteHost = hostRemoto.getEnderecoRedeHostRemoto();
		
		/*
		 * #53890
		 * Impressão de Ticket no AGHUse não pode obter o computador pelo IP
		 * No HCPA os Microcomputadores não possuem IP fixo.
		 */
		boolean isIP = isIP(remoteHost);	    	
		String nomeHospital = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_PARAMETRO_HU);
		boolean isHcpa = "HCPA".equals(nomeHospital);
		if (isHcpa && isIP){
			throw new ApplicationBusinessException(ListarConsultasPorGradeControllerExceptionCode.MSG_IMPRIMIR_MICRO_SEM_NOME);
		}
		ImpComputador computador =null;
		
		if (isHcpa) {
			computador = cadastrosBasicosCupsFacade.obterComputadorPorNome(remoteHost);
		} else {
			computador = cadastrosBasicosCupsFacade.obterComputador(remoteHost);
		}
		ImpComputadorImpressora impressora = null;
		if (computador != null) {
				impressora = cadastrosBasicosCupsFacade.obterImpressora(
						computador.getSeq(), DominioTipoCups.RAW);
		}

		return impressora == null ? null : impressora.getImpImpressora()
				.getFilaImpressora();
	}

	private boolean isIP(String remoteHost) {
		Pattern pattern;
		Matcher matcher;
		String stringPattern = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
				+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
		pattern = Pattern.compile(stringPattern);
		matcher = pattern.matcher(remoteHost);
		return matcher.matches();
	}

    /**
     * Imprime relatório.<br />
     * Se tipo de impressora cadastrada for matricial, manda impressão em texto
     * plano. Qualquer alteração neste método, deverá ser alterado também na
     * classe RelatorioAgendamentoConsultaController.
     */
	public void directPrint() {
		try {
			// prepara dados para geração do relatório.
			recuperarColecao();

			String matricial = this.getMatricialHost();

			if (StringUtils.isNotBlank(matricial)) {
				String textoMatricial = obterTextoAgendamentoConsulta();
				textoMatricial = Normalizer.normalize(textoMatricial,
						Normalizer.Form.NFD);
				textoMatricial = textoMatricial.replaceAll("[^\\p{ASCII}]", "");
				// Necessário adicionar algumas linhas no final para ficar no
				// ponto de corte correto da impressora.
				textoMatricial = textoMatricial.concat("\n\n\n\n\n\n");
				this.sistemaImpressao.imprimir(textoMatricial, matricial);
				
				apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_SUCESSO_IMPRESSAO_TICKET");
			} else {
				imprimeComprovanteMarcacaoConsulta();
				this.numeroDeVias = 1;
			}
		} catch (ApplicationBusinessException e) {
			LOG.error(e.getMessage(), e);
			apresentarExcecaoNegocio(e);
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			apresentarMsgNegocio(Severity.ERROR, e.getLocalizedMessage());
		}
	}
	
	private void imprimeComprovanteMarcacaoConsulta() throws ApplicationBusinessException,
	SistemaImpressaoException, JRException, UnknownHostException {

		if (this.numeroDeVias > 0) {
			for (int i = 0; i < this.numeroDeVias; i++) {
				DocumentoJasper documento = gerarDocumento();
				this.sistemaImpressao.imprimir(documento.getJasperPrint(),
						super.getEnderecoIPv4HostRemoto());
			}
			apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_IMPRESSAO_TICKET");
		} else {
			apresentarMsgNegocio(Severity.INFO,	"MENSAGEM_TICKET_IMPRESSAO_NAO_IMPRESSO");
		}
	}

	public String print() throws ApplicationBusinessException {
		try {
			recuperarColecao();
			if (colecao == null || colecao.isEmpty()) {
				apresentarMsgNegocio(Severity.INFO,"MENSAGEM_NENHUM_REGISTRO_ENCONTRADO_PESQUISA");
				return null;
			}
			if(this.consultaMarcada && confirmadaImpressao){
				return "ambulatorio-relatorioAgendamentoConsultas";
			}
		} catch (ApplicationBusinessException e) {
			LOG.error("Exceção capturada: ", e);
			apresentarMsgNegocio(Severity.ERROR,e.getLocalizedMessage());
			
		}
		return null;
	}

	public String imprimirRetornaPaginaMarcacaoConsultas() throws JRException, SystemException, IOException {
		directPrint();
		return "ambulatorio-voltarAposImprimir";
	}
	
	public String voltarListarConsultasPorGrade() {
		this.primeiraExecucao = true;
		this.consulta = null;
		return"ambulatorio-list";
	}

    /**
     * Método invocado pelo a:mediaOutput para geração de PDF dentro de XHTML.
     */
    public StreamedContent getRenderPdf() throws IOException, JRException, SystemException, DocumentException, ApplicationBusinessException {
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(true));
    }

    @Override
    public String recuperarArquivoRelatorio() {
    	return "br/gov/mec/aghu/ambulatorio/report/relatorioAgendamentoConsulta.jasper";
    }

    @Override
    public Map<String, Object> recuperarParametros() {
		obterParametros();
		Map<String, Object> params = new HashMap<String, Object>();
		Date dataAtual = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	
		params.put("dataAtual", sdf.format(dataAtual));
		params.put("labelZona", labelZona + ":");
		params.put("labelSala", labelSala + ":");
	
		String hospital = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();
		params.put("hospitalLocal", hospital);
		if (isGradeUBS()) {
		    String tituloUbs = null;
		    try {
			tituloUbs = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_TITULO_UBS);
		    } catch (Exception e) {
			LOG.error(e.getMessage(), e);
		    }
		    if (StringUtils.isNotBlank(tituloUbs)) {
			params.put("hospitalLocal", tituloUbs);
		    }
		}
		return params;
    }

    @Override
    public Collection<RelatorioAgendamentoConsultaVO> recuperarColecao() throws ApplicationBusinessException {
		colecao = ambulatorioFacade.obterAgendamentoConsulta(consulta.getNumero());
		return colecao;
    }

    private void obterParametros() {
		try {
		    labelZona = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_LABEL_ZONA).getVlrTexto();
		    labelSala = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_LABEL_SALA).getVlrTexto();
		} catch (ApplicationBusinessException e) {
		    apresentarExcecaoNegocio(e);
		}
    }

    /**
     * Obtem texto de Marcacao da Consulta para Impressora Matriciais
     */
    public String obterTextoAgendamentoConsulta() throws ApplicationBusinessException {
		String hospitalLocal = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoLocal();
		obterParametros();
		return ambulatorioFacade.obterTextoAgendamentoConsulta(hospitalLocal, labelZona, labelSala, consulta);
    }

    public Boolean verificaImprimeAutomatico(AacConsultas consulta) {
		Boolean imprimeAutomatico = true;
		if (consulta != null && consulta.getDtConsulta() != null) {
		    if (!DateUtil.truncaData(consulta.getDtConsulta()).after(DateUtil.truncaData(new Date()))) {
			imprimeAutomatico = false;
		    }
		}
		return imprimeAutomatico;
    }

    public boolean exibeModal(AacConsultas consultaParametro) {
		boolean exibeModal = true;
		if (consultaParametro != null) {
		    if (verificaImprimeAutomatico(consultaParametro) || !consultaParametro.getGradeAgendamenConsulta().getEmiteTicket()) {
			exibeModal = false;
		    }
		} else if (verificaImprimeAutomatico(consulta) || !consulta.getGradeAgendamenConsulta().getEmiteTicket()) {
		    exibeModal = false;
		}
		return exibeModal;
    }

   
    public void setConsulta(AacConsultas consulta) {
		this.setEditar(true);
		if (consulta.getComplemento().getPacCodigo() != null && consulta.getComplemento().getNomePaciente() != null) {
		    this.pacCodigo = consulta.getComplemento().getPacCodigo();
		    selecionarPacienteConsultaEdicao();
		} else {
		    this.pac = null;
		    this.pacCodigo = null;
		    this.prontuario = null;
		    this.pacNome = null;
		}
		this.consulta = obterAacConsultasFull(consulta.getNumero());
		formaAgendamento = consulta.getFormaAgendamento();
		checarConvenioSaudePlano(consulta);
	
		if (consulta.getComplemento().getCodCentral() != null) {
		    this.consulta.setCodCentral(consulta.getComplemento().getCodCentral());
		} else {
		    this.consulta.setCodCentral(null);
		}
		Enum[] fields = { AacFormaAgendamento.Fields.CONDICAO_ATENDIMENTO, AacFormaAgendamento.Fields.PAGADOR,
			AacFormaAgendamento.Fields.TIPO_AGENDAMENTO };
		this.formaAgendamento = ambulatorioFacade.obterAacFormaAgendamentoPorChavePrimaria(consulta.getFormaAgendamento().getId(), fields,
			null);
		this.consultaExcedente = false;
    }

	private void checarConvenioSaudePlano(AacConsultas consulta) {
		if (consulta.getConvenioSaudePlano() != null) {
			this.consulta.setConvenioSaudePlano(consulta.getConvenioSaudePlano());
		}
		if (this.consulta.getConvenioSaudePlano() == null) {
		    try {
			populaPagador(consulta);
	
		    } catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(), e);
		    }
		}
	}

    public void setExibeModalConfirmacaoImpressao(Boolean exibeModalConfirmacaoImpressao) {
		this.exibeModalConfirmacaoImpressao = exibeModalConfirmacaoImpressao;
		if (exibeModalConfirmacaoImpressao) {// showWhenRendered
		    this.openDialog("modalConfirmacaoImpressaoFormularioWG");
		} else {
		    this.closeDialog("modalConfirmacaoImpressaoFormularioWG");
		}
    }
    public void setExibeModalDataNaoProgramada(Boolean exibeModalDataNaoProgramada) {
		this.exibeModalDataNaoProgramada = exibeModalDataNaoProgramada;
		if (exibeModalDataNaoProgramada) {// showWhenRendered
		    this.openDialog("modalDataNaoProgramadaWG");
		} else {
		    this.closeDialog("modalDataNaoProgramadaWG");
		}
    }
    public Boolean getExibeModalDataNaoProgramada() {
    	return exibeModalDataNaoProgramada;
    }
    
    public AacSituacaoConsultas getSituacaoAnterior() {
		return situacaoAnterior;
	}

	public void setSituacaoAnterior(AacSituacaoConsultas situacaoAnterior) {
		this.situacaoAnterior = situacaoAnterior;
	}

	public DominioDiaSemana getDiaSemana() {
		return diaSemana;
	}

	public void setDiaSemana(DominioDiaSemana diaSemana) {
		this.diaSemana = diaSemana;
	}

	public Date getHoraConsulta() {
		return horaConsulta;
	}

	public void setHoraConsulta(Date horaConsulta) {
		this.horaConsulta = horaConsulta;
	}

	public Date getMesInicio() {
		return mesInicio;
	}

	public void setMesInicio(Date mesInicio) {
		this.mesInicio = mesInicio;
	}

	public Date getMesFim() {
		return mesFim;
	}

	public void setMesFim(Date mesFim) {
		this.mesFim = mesFim;
	}

	public Map<Integer, ConsultaComplementoVO> getComplementos() {
		return complementos;
	}

	public void setComplementos(Map<Integer, ConsultaComplementoVO> complementos) {
		this.complementos = complementos;
	}

	public Boolean getExibeModalConfirmacaoImpressao() {
		return exibeModalConfirmacaoImpressao;
	}

	public AacGradeAgendamenConsultas getGrade() {
		return grade;
	}

	public void setGrade(AacGradeAgendamenConsultas grade) {
		this.grade = grade;
	}

	public Integer getGrdSeq() {
		return grdSeq;
	}

	public void setGrdSeq(Integer grdSeq) {
		this.grdSeq = grdSeq;
	}

	public AacConsultas getConsulta() {
		return consulta;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public String getPacNome() {
		return pacNome;
	}

	public void setPacNome(String pacNome) {
		this.pacNome = pacNome;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public void setPacCodigoFonetica(Integer pacCodigoFonetica) {
		this.pacCodigoFonetica = pacCodigoFonetica;
	}

	public Integer getPacCodigoFonetica() {
		return pacCodigoFonetica;
	}

	public void setConsultaExcedente(Boolean consultaExcedente) {
		this.consultaExcedente = consultaExcedente;
	}

	public Boolean getConsultaExcedente() {
		return consultaExcedente;
	}

	public void setEditar(Boolean editar) {
		this.editar = editar;
	}

	public Boolean getEditar() {
		return editar;
	}

	public AacFormaAgendamento getFormaAgendamento() {
		return formaAgendamento;
	}

	public void setFormaAgendamento(AacFormaAgendamento formaAgendamento) {
		this.formaAgendamento = formaAgendamento;
	}

	public IAmbulatorioFacade getAmbulatorioFacade() {
		return ambulatorioFacade;
	}

	public Date getDtConsulta() {
		return dtConsulta;
	}

	public void setDtConsulta(Date dtConsulta) {
		this.dtConsulta = dtConsulta;
	}

	public boolean isPrimeiraExecucao() {
		return primeiraExecucao;
	}

	public void setPrimeiraExecucao(boolean primeiraExecucao) {
		this.primeiraExecucao = primeiraExecucao;
	}

	public boolean isEmergencia() {
		return emergencia;
	}

	public void setEmergencia(boolean emergencia) {
		this.emergencia = emergencia;
	}

	public AacPagador getPagador() {
		return pagador;
	}

	public void setPagador(AacPagador pagador) {
		this.pagador = pagador;
	}

	public AacTipoAgendamento getAutorizacao() {
		return autorizacao;
	}

	public void setAutorizacao(AacTipoAgendamento autorizacao) {
		this.autorizacao = autorizacao;
	}

	public AacCondicaoAtendimento getCondicao() {
		return condicao;
	}

	public void setCondicao(AacCondicaoAtendimento condicao) {
		this.condicao = condicao;
	}
	
	public Integer getNumeroDeVias() {
		return numeroDeVias;
	}

	public void setNumeroDeVias(Integer numeroDeVias) {
		this.numeroDeVias = numeroDeVias;
	}

	public AacConsultas getConsultaConvenio() {
		return this.consultaConvenio;
	}
		
	public void setConsultaConvenio(AacConsultas consultaConvenio) {
		this.consultaConvenio = consultaConvenio;
	}

	public List<VAacConvenioPlano> getListaConvenios() {
		return listaConvenios;
	}

	public void setListaConvenios(List<VAacConvenioPlano> listaConvenios) {
		this.listaConvenios = listaConvenios;
	}
	
	public VAacConvenioPlano getConvenioPlanoSelecionado() {
		return convenioPlanoSelecionado;
	}

	public void setConvenioPlanoSelecionado(VAacConvenioPlano convenioPlanoSelecionado) {
		this.convenioPlanoSelecionado = convenioPlanoSelecionado;
	}
	public VAacConvenioPlano getConvenioPlanoSGB() {
		return convenioPlanoSGB;
	}
	
	public void setConvenioPlanoSGB(VAacConvenioPlano convenioPlanoSGB) {
		this.convenioPlanoSGB = convenioPlanoSGB;
	}

	public Boolean getOrigemSumarioAlta() {
		return origemSumarioAlta;
	}

	public void setOrigemSumarioAlta(Boolean origemSumarioAlta) {
		this.origemSumarioAlta = origemSumarioAlta;
	}

	public String getCameFrom() {
		return cameFrom;
	}

	public void setCameFrom(String cameFrom) {
		this.cameFrom = cameFrom;
	}

	public AacConsultas getConsultaSelecionada() {
		return consultaSelecionada;
	}

	public void setConsultaSelecionada(AacConsultas consultaSelecionada) {
		this.consultaSelecionada = consultaSelecionada;
	}

	public String getProntuarioSumario() {
		return prontuarioSumario;
	}

	public void setProntuarioSumario(String prontuarioSumario) {
		this.prontuarioSumario = prontuarioSumario;
	}

	public Date getDtConsultaExcedenteParametro() {
		return dtConsultaExcedenteParametro;
	}

	public void setDtConsultaExcedenteParametro(Date dtConsultaExcedenteParametro) {
		this.dtConsultaExcedenteParametro = dtConsultaExcedenteParametro;
	}

	public AacFormaAgendamento getFormaAgendamentoExcedenteParametro() {
		return formaAgendamentoExcedenteParametro;
	}

	public void setFormaAgendamentoExcedenteParametro(
			AacFormaAgendamento formaAgendamentoExcedenteParametro) {
		this.formaAgendamentoExcedenteParametro = formaAgendamentoExcedenteParametro;
	}
	
	
}