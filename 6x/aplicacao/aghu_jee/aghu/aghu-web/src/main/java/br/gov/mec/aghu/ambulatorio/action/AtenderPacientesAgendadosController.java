package br.gov.mec.aghu.ambulatorio.action;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.transaction.SystemException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.exception.GenericJDBCException;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.DocumentException;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.ambulatorio.vo.DocumentosPacienteVO;
import br.gov.mec.aghu.ambulatorio.vo.MamReceituarioCuidadoVO;
import br.gov.mec.aghu.ambulatorio.vo.ProcedimentoAtendimentoConsultaVO;
import br.gov.mec.aghu.ambulatorio.vo.RelatorioAnamneseEvolucaoVO;
import br.gov.mec.aghu.ambulatorio.vo.VAacSiglaUnfSalaVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.certificacaodigital.business.ICertificacaoDigitalFacade;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.commons.CoreUtil;
//import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.controlepaciente.action.RegistrosPacienteController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.persistence.BaseEntity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.core.utils.DateValidator;
import br.gov.mec.aghu.dominio.DominioAnamneseEvolucao;
import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.dominio.DominioModulo;
import br.gov.mec.aghu.dominio.DominioMotivoPendencia;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoAtendimento;
import br.gov.mec.aghu.dominio.DominioTipoDocumento;
import br.gov.mec.aghu.dominio.DominioTipoPrescricaoReceituario;
import br.gov.mec.aghu.dominio.DominioTipoReceituario;
import br.gov.mec.aghu.exames.action.RelatorioTicketExamesPacienteController;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.questionario.action.ImprimeInformacoesComplementaresController;
import br.gov.mec.aghu.exames.solicitacao.action.SolicitacaoExameController;
import br.gov.mec.aghu.exames.solicitacao.business.ISolicitacaoExameFacade;
import br.gov.mec.aghu.exames.solicitacao.vo.SolicitacaoExameVO;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.internacao.solicitacao.business.ISolicitacaoInternacaoFacade;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AacRetornos;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.AghEquipes;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AinSolicitacoesInternacao;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MamAnamneses;
import br.gov.mec.aghu.model.MamAtestados;
import br.gov.mec.aghu.model.MamEvolucoes;
import br.gov.mec.aghu.model.MamItemAnamneses;
import br.gov.mec.aghu.model.MamItemEvolucoes;
import br.gov.mec.aghu.model.MamItemReceituario;
import br.gov.mec.aghu.model.MamNotaAdicionalAnamneses;
import br.gov.mec.aghu.model.MamNotaAdicionalEvolucoes;
import br.gov.mec.aghu.model.MamProcedimentoRealizado;
import br.gov.mec.aghu.model.MamReceituarioCuidado;
import br.gov.mec.aghu.model.MamReceituarios;
import br.gov.mec.aghu.model.MamSolicitacaoRetorno;
import br.gov.mec.aghu.model.MamTipoAtestado;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.VMamProcXCid;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.prescricaomedica.action.RelatorioConclusaoAbaReceituario;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.view.VAfaDescrMdto;
import net.sf.jasperreports.engine.JRException;

/**
 * Controller da tela de Pesquisar Consulta/Agenda
 * 
 * @author georgenes.zapalaglio
 * 
 */
@SuppressWarnings({"PMD.ExcessiveClassLength", "PMD.NcssTypeCount", "PMD.CyclomaticComplexity"})
public class AtenderPacientesAgendadosController extends ActionReport {

	private static final String ERRO_DATA_DIFERENTES_ATESTADO = "ERRO_DATA_DIFERENTES_ATESTADO";

	private static final String EXCECAO_CAPTURADA = "Exceção capturada: ";

	private static final Log LOG = LogFactory.getLog(AtenderPacientesAgendadosController.class);

	private static final long serialVersionUID = -3744812806595136737L;
	private static final String PAGE_AGRUPAMENTO_FAMILIAR = "ambulatorio-agrupamentoFamiliar";
	private static final String LABEL_REGISTRO_EXCLUIDO = "LABEL_REGISTRO_EXCLUIDO";
	private static final String MSG_ATENDER_PACIENTES_AGENDADOS_REGISTRO_SALVO = "MSG_ATENDER_PACIENTES_AGENDADOS_REGISTRO_SALVO";

	private static final String SOLICITACAO_INTERNACAO = "internacao-solicitacaoInternacao";
	private static final String SOLICITACAO_EXAME_CRUD = "exames-solicitacaoExameCRUD";
	private static final String PAGE_PESQUISAR_PACIENTES_AGENDADOS = "ambulatorio-pesquisarPacientesAgendados";
	private static final String PAGE_PESQUISAR_PACIENTES_EVOLUCAO = "ambulatorio-atenderPacientesEvolucao";
	private static final String PAGE_PESQUISAR_PACIENTES_ANAMNESE = "ambulatorio-atenderPacientesAnamnese";
	private static final String PAGE_PRESCRICAOMEDICA_VERIFICA_PRESCRICAO_MEDICA = "prescricaomedica-verificaPrescricaoMedica";
	private static final String PAGE_VISUALIZAR_PACIENTES_CONTROLES = "controlepaciente-visualizarRegistros";
	private static final String PAGE_ATENDER_PACIENTES_AGENDADOS = "ambulatorio-redirecionarAtenderPacientesAgendados";
	
	private static final String PAGE_PACIENTES_AGENDADOS_RECEITAS = "ambulatorio-receitasPaciente";
	
	@Inject
	private SistemaImpressao sistemaImpressao;


	
	private static final String DESCR_FORMULA = "FÓRMULA:";

	private static final String ANAMNESE = "A", EVOLUCAO = "E";

	@EJB
	private IAmbulatorioFacade ambulatorioFacade;

	@EJB
	private IFarmaciaFacade farmaciaFacade;

	@EJB
	private IExamesFacade exameFacade;

	@EJB
	private ISolicitacaoExameFacade solicitacaoExameFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private ISolicitacaoInternacaoFacade solicitacaoInternacaoFacade;

	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private IParametroFacade parametroFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private ICertificacaoDigitalFacade certificacaoDigitalFacade;
	
    @EJB
    private ICascaFacade cascaFacade;
	
	@Inject
	PesquisarPacientesAgendadosController pesquisarPacientesAgendadosController;
	
	@Inject
	CadastroCuidadosPacienteController cadastroCuidadosPacienteController;
	
	@Inject
	private AtenderPacientesEvolucaoController atenderPacientesEvolucaoController;
	
	@Inject
	private RelatorioAtestadoController relatorioAtestadoController;
	
	@Inject
	private SolicitacaoExameController solicitacaoExameController;
	
	@Inject
	private ImprimeInformacoesComplementaresController imprimeInformacoesComplementaresController;
	
	@Inject
	private RelatorioAtestadoAcompanhanteController relatorioAtestadoAcompanhanteController;
	
	@Inject
	private RelatorioAtestadoComparecimentoController relatorioAtestadoComparecimentoController;
	
	@Inject
	private RelatorioConclusaoAbaReceituario relatorioConclusaoAbaReceituario;

	@Inject
	private RelatorioTicketExamesPacienteController relatorioTicketExamesPacienteController;

	@Inject
	private RelatorioReceitaCuidadosController relatorioReceitaCuidadosController;
	
//	@Inject
//	private RelatorioLaudoAIHController relatorioLaudoAIHController;

	@Inject
	private ImprimeTicketRetornoController imprimirTicketRetornoController;	

	@Inject
	private ReceitasPacienteController receitasPacienteController;
	
	@Inject
	private RelatorioConsultoriaAmbulatorialController relatorioConsultaAmbularorialController;		
	
	@Inject
	private RegistrosPacienteController registrosPacienteController;
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;

	private DominioTipoDocumento dominioTipoDocumento;
	
	private Integer selectedTab;
	private String comeFrom;
	private String labelZona;
	private Boolean readonlyAnamnese;
	private Boolean readonlyEvolucao;
	private Boolean readonlyReceitaGeral;
	private Boolean readonlyReceitaEspecial;

	private Integer numeroConsulta;
	private AacConsultas consultaSelecionada;
	private AghAtendimentos atendimento;
	private List<MamNotaAdicionalAnamneses> notasAdicionaisAnamnesesList;
	private List<MamNotaAdicionalEvolucoes> notasAdicionaisEvolucaoList;
	private String textoAnamnese;
	private String textoEvolucao;
	private String descrNotaAdicionalAnamnese;
	private String descrNotaAdicionalEvolucao;
	private String tipoCorrente;
	private Boolean modoInsercaoItemAnamnese;
	private Boolean modoInsercaoItemEvolucao;
	private MamNotaAdicionalAnamneses notaAdicionalAnamneses;
	private MamNotaAdicionalEvolucoes notaAdicionalEvolucoes;

	private MamReceituarios receitaGeral;
	private MamReceituarios receitaEspecial;
	private MamItemReceituario itemReceitaGeral;
	private MamItemReceituario itemReceitaEspecial;
	private List<MamItemReceituario> itemReceitaGeralList;
	private List<MamItemReceituario> itemReceitaEspecialList;
	private VAfaDescrMdto descricaoMedicamento;
	private Integer viasGeral;
	private Integer viasEspecial;
	private String validadeUso;

	private DominioMotivoPendencia motivoPendencia;
	private Boolean mostrarModalImpressao;

	private String consultasAnterioresDescricao;
	private String finalizarDescricao;
	private List<DocumentosPacienteVO> listaDocumentosPaciente;
	private String descricaoReceitaGeral;
	private String descricaoReceitaEspecial;
	private String textoConsultaAtual = "";
	private final Integer TAB_1=0, TAB_2=1, TAB_3=2, TAB_4=3, TAB_5=4, TAB_6=5, TAB_7=6;
	
	//Atributos para Aba 1 - consultas
	private AghEquipes equipe;

	// Atributos para Aba 4 - Procedimentos
	private ProcedimentoAtendimentoConsultaVO procedimentoAtendConsultaVO;
	private String procedimentoDescricao;
	private Byte procedimentoQuantidade;
	private VMamProcXCid procedimentoCid;
	private List<ProcedimentoAtendimentoConsultaVO> listaProcedimentosAtendConsulta;
	
	private List<MamAtestados> consultaAtestado;
	private List<MamTipoAtestado> lista;

	private Boolean habilitaFinalizar;
	// Valor utilizado quando uma especialidade não possui uma especialidade
	// generica
	private static final Short VALOR_PADRAO_ESPECIALIDADE_GENERICA = 0;
	private static final Byte VALOR_PADRAO_QUANTIDADE_PROCEDIMENTO_ATEND = 1;

	private Boolean voltarListaPacientes = false;

	private boolean confirmaValidade = false;
	private Integer sliderAtual;

	/**
	 * Dados que serão impressos em PDF.
	 */
	private RelatorioAnamneseEvolucaoVO colecao = new RelatorioAnamneseEvolucaoVO();

	private Boolean habilitaAnamnese = false;

	private Boolean habilitaEvolucao = false;

	private Boolean habilitaProcedimento = false;

	private Boolean habilitaReceita = false;
	
	private Boolean habilitaAtestado = false;

	private Boolean gravaAnamnese = false;

	private Boolean gravaEvolucao = false;

	private Boolean gravaProcedimento = false;

	private Boolean gravaReceita = false;

	private Boolean gravaSolicitacaoExame = false;
	
	// Parametros para uso da solicitação de exames do AGHWEB #39289
	private String banco = null;
	private String urlBaseWebForms = null;
	private Boolean isHcpa = false;
	private Boolean isUbs = false;		
	
	private String nomeMicrocomputador; 
	//private RapServidores servidorLogado;
	private RapServidores servidorLogadoSemFimVinculo;
	
	private MpmPrescricaoMedica prescricaoMedica;
	
	private Boolean prescricaoAmbulatorialAtiva;
	
	private Date dtInicio;
	private Date dtFim;
	private AghEspecialidades especialidade;
	private List<AacConsultas> listaConsultasPaciente;
	private static final String ESPACO = " ";

	// Aributos para Aba 6 - Atestado
	private ProcedimentoAtendimentoConsultaVO procedimentoAtendConsultaAtestadoVO ;
	private MamTipoAtestado tipoAtestado;
	private MamAtestados atestadoAmbulatorio;
	private int calcularDiasEntreDatas;
	private boolean opcaoSelecionadaAtestado = Boolean.FALSE;
	private boolean opcaoSelecionadaTipoAtestado = Boolean.FALSE;
	private MamTipoAtestado opcaoTipoAtestado;
	private AghCid procedimentoCidAtestado;
	
	private Integer codigoPaciente;
	private AipPacientes pacienteInternacao;
	private AinSolicitacoesInternacao primeiraSolicitacaoInternacao;
	
	private Long evoSeq;
	
	private enum ProcedimentoAtendimentoExceptionCode implements BusinessExceptionCode {
		MAM_00646, MSG_INFORME_CID_PARA_O_PROCEDIMENTO;
	}

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public void iniciar() throws ApplicationBusinessException {

		processarMicroEUserLogado();
		
		if (this.numeroConsulta == null) {
			throw new IllegalArgumentException("É obrigatório informar o numeroConsulta da consulta! Parâmetro: numeroConsulta");
		}
		
		this.consultaSelecionada = this.ambulatorioFacade.obterAacConsultasAtenderPacientesAgendados(this.numeroConsulta);	
		processarReceituarios();

		try {
			habilitaFinalizar = this.ambulatorioFacade.concluirBlocoNaoAssina(this.consultaSelecionada, this.tipoCorrente);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(),e);
		}

		if (selectedTab == null) {
			selectedTab = TAB_2;
		}

		mostrarModalImpressao = false;
		try {
			consultasAnterioresDescricao = obtemDescricaoConsultaAnterior(this.consultaSelecionada);
		} catch (ApplicationBusinessException e) {
			LOG.error("Não encontrou o servidor logado!");
		}
		finalizarDescricao = "";
		iniciarProcedimentosAtendConsulta();

		atendimento = aghuFacade.obterAtendimentoPorConsulta(this.consultaSelecionada.getNumero());

		this.verificaUsuarioElaboraAnamnese();
		this.verificaUsuarioElaboraEvolucao();
		tipoCorrente = null;
		iniciaAnamnese();
		iniciaEvolucao();
		iniciaTextoNotasAdicionais();
		iniciaReceitas();
		this.motivoPendencia = DominioMotivoPendencia.POS;
		
		this.procedimentoCidAtestado = null;
		this.lista = ambulatorioFacade.listarTodos();

		this.carregarParametros();
		this.dtInicio =DateUtil.adicionaDias(new Date(),-365);
		this.dtFim = new Date();
		this.pesquisar();
		if(listaConsultasPaciente.size()>0){
			try {
				this.carregarConsultaSelecionada(listaConsultasPaciente.get(0));
			} catch (ApplicationBusinessException e) {
				this.apresentarExcecaoNegocio(e);
			}
		}else{
			consultasAnterioresDescricao = null;
		}
		prescricaoAmbulatorialAtiva = ambulatorioFacade
				.pesquisarAtendimentoParaPrescricaoMedica(
						consultaSelecionada.getPaciente().getCodigo(),null)
				.contains(atendimento);
	
		popularParametrosIntegracaoAghWeb();
		
		if (selectedTab == 0) {
			
			obterDescricaoConsultaAtual();
			
		}
		
		if (consultaSelecionada.getPaciente().getCodigo() != null) {
			this.setCodigoPaciente(consultaSelecionada.getPaciente().getCodigo());
			pacienteInternacao = this.pacienteFacade.obterPaciente(codigoPaciente);
			setPrimeiraSolicitacaoInternacao(solicitacaoInternacaoFacade.obterPrimeiraSolicitacaoPendentePorPaciente(codigoPaciente));
		}
		
	}

	private void obterDescricaoConsultaAtual() {
		try {
			textoConsultaAtual = this.ambulatorioFacade.visualizarConsultaAtual(this.consultaSelecionada.getNumero());
		} catch (ApplicationBusinessException e) {
			apresentarMsgNegocio(Severity.ERROR, "ERRO_CARREGAR_CONSULTA_ATUAL");
			LOG.error(e.getMessage(), e);
		}
	}

	private void processarReceituarios() {
		if(consultaSelecionada != null) {
			List<MamReceituarios> receituarios = this.ambulatorioFacade.pesquisarReceituariosPorConsulta(consultaSelecionada);
			consultaSelecionada.setReceituarios(new HashSet<MamReceituarios>(receituarios));
		}
	}
	
	private void popularParametrosIntegracaoAghWeb() {
		try {

			isHcpa = ambulatorioFacade.verificarAtendimentoHCPA();
			if (isHcpa){
				AghParametros aghParametroUrlBaseWebForms = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_URL_BASE_AGH_ORACLE_WEBFORMS);
				if (aghParametroUrlBaseWebForms != null) {
					urlBaseWebForms = aghParametroUrlBaseWebForms.getVlrTexto();
				}
				AghParametros aghParametrosBanco = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_BANCO_AGHU_AGHWEB);
				if (aghParametrosBanco != null) {
					banco = aghParametrosBanco.getVlrTexto();
				}
				isUbs = aghuFacade.possuiCaracteristicaPorUnidadeEConstante(consultaSelecionada.getGradeAgendamenConsulta().getUnidadeFuncional().getSeq(), ConstanteAghCaractUnidFuncionais.UBS);
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public boolean habilitarContgExamesAGHWeb() {

		try {
			String habilitar = this.parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_CONTINGENCIA_SOLIC_EXAMES_AGHWEB);

			return "S".equals(habilitar);
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}

		return false;
	}

	public Boolean validarUrlBaseWebFormsBanco(){
		return StringUtils.isBlank(urlBaseWebForms) || StringUtils.isBlank(banco);
	}	
	
	private void processarMicroEUserLogado() {
		try {
			nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
			//servidorLogado = registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado(), new Date());
			servidorLogadoSemFimVinculo = registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado());
		} catch (UnknownHostException e1) {
			LOG.error("Exceção capturada:", e1);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	private void carregarParametros() {
		try {
			this.labelZona = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_LABEL_ZONA).getVlrTexto();

			if (this.labelZona == null) {
				this.labelZona = "Zona";
			}

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			LOG.error("Erro ao buscar parâmetro", e);
		}
	}

	private void verificaUsuarioElaboraAnamnese() {
		try {
			setReadonlyAnamnese(!this.ambulatorioFacade.verificaUsuarioElaboraAnamnese());
		} catch (BaseException e) {
			apresentarMsgNegocio(Severity.ERROR, "ERRO_VERIFICAR_PERMISSAO_ANAMNESE");
		}
	}

	private void verificaUsuarioElaboraEvolucao() {
		try {
			setReadonlyEvolucao(!this.ambulatorioFacade.verificaUsuarioElaboraEvolucao());
		} catch (BaseException e) {
			apresentarMsgNegocio(Severity.ERROR, "ERRO_VERIFICAR_PERMISSAO_EVOLUCAO");
		}
	}

	private String obtemDescricaoConsultaAnterior(AacConsultas consulta) throws ApplicationBusinessException {
		String retorno = "";
		try {
			retorno = this.ambulatorioFacade.obtemDescricaoConsultaAnterior(consulta);
		} catch (ApplicationBusinessException e) {
			apresentarMsgNegocio(Severity.ERROR, "ERRO_CARREGAR_CONSULTA_ANTERIOR");
		}
		return retorno;
	}

	public void obtemDescricaoConsultaAtual() {
		
		if(selectedTab == 6){
			try {
	
				habilitaFinalizar = this.ambulatorioFacade.concluirBlocoNaoAssina(this.consultaSelecionada, this.tipoCorrente);
	
				finalizarDescricao = this.ambulatorioFacade.obtemDescricaoConsultaAtual(this.consultaSelecionada);
	
				MamAnamneses anamneseAtual = ambulatorioFacade
						.obterAnamneseAtivaPorNumeroConsulta(this.consultaSelecionada.getNumero());
				
				MamEvolucoes evolucaoAtual = ambulatorioFacade
						.obterEvolucaoAtivaPorNumeroConsulta(this.consultaSelecionada.getNumero());
	
				if (anamneseAtual == null && evolucaoAtual == null) {
					apresentarMsgNegocio(Severity.WARN, "MENSAGEM_EVOLUCAO_E_ANAMNESE_NAO_SALVA");
				}
			} catch (BaseException e) {
				apresentarMsgNegocio(Severity.ERROR, "ERRO_CARREGAR_CONSULTA_ATUAL");
			}
		}
	}

	// --[ANAMNESE]
	private void iniciaAnamnese() {
		if (textoAnamnese == null) {
			textoAnamnese = "";
		}
		try {
			MamAnamneses anamneseAtual = this.ambulatorioFacade.obterAnamneseAtivaPorNumeroConsulta(this.consultaSelecionada.getNumero());
			if (anamneseAtual != null && (DominioIndPendenteAmbulatorio.P.equals(anamneseAtual.getPendente()) || DominioIndPendenteAmbulatorio.V.equals(anamneseAtual.getPendente()))) {
				tipoCorrente = ANAMNESE;
				if ((DominioIndPendenteAmbulatorio.V.equals(anamneseAtual.getPendente())) && !anamneseAtual.getServidorValida().getId().equals(servidorLogadoSemFimVinculo.getId())) {
					readonlyAnamnese = true;
					readonlyEvolucao = true;
				}
				MamItemAnamneses item = this.ambulatorioFacade.primeiroItemAnamnesesPorAnamneses(anamneseAtual.getSeq());
				if (item != null && textoAnamnese.isEmpty()) {
					textoAnamnese = item.getDescricao();

				}
			} else if (ANAMNESE.equals(tipoCorrente)) {
				tipoCorrente = null;
			}
			notasAdicionaisAnamnesesList = this.ambulatorioFacade.obterNotaAdicionalAnamnesesConsulta(this.consultaSelecionada.getNumero());
			modoInsercaoItemAnamnese = true;

			this.habilitaAnamnese = this.ambulatorioFacade.validarProcessoConsultaAnamnese();
			this.gravaAnamnese = this.ambulatorioFacade.validarProcessoExecutaAnamnese();
			this.gravaSolicitacaoExame = this.ambulatorioFacade.validarProcessoExecutaSolicitacaoExame();

		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public Boolean verificarModuloExameAtivo() {
		return cascaFacade.verificarSeModuloEstaAtivo(DominioModulo.EXAMES_LAUDOS.getDescricao());
	}

//	@Restrict("#{s:hasPermission('realizarAnamneseAmbulatorio','salvar')}")
	public void salvarAnamnese() {
		try {
			this.ambulatorioFacade.salvarAnamnese(textoAnamnese, this.consultaSelecionada);
			apresentarMsgNegocio(Severity.INFO, "LABEL_REGISTRO_SALVO", "anamnese");
			tipoCorrente = ANAMNESE;
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void excluirAnamnese() {
		try {
			MamAnamneses anamneseAtual = ambulatorioFacade
					.obterAnamneseAtivaPorNumeroConsulta(consultaSelecionada
							.getNumero());
			this.ambulatorioFacade.excluiAnamese(this.consultaSelecionada, anamneseAtual);
			textoAnamnese = null;
			iniciaAnamnese();
			apresentarMsgNegocio(Severity.INFO, LABEL_REGISTRO_EXCLUIDO, "anamnese");
			if (textoEvolucao.isEmpty()) {
				iniciaEvolucao();
			}
			iniciaTextoNotasAdicionais();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void inserirItemAdicionalAnamnese() {
		try {
			if (descrNotaAdicionalAnamnese == null) {
				apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_OBRIGATORIEDADE_CAMPO_NOTA_ADICIONAL");
			} else {
				if (modoInsercaoItemAnamnese) {
					notasAdicionaisAnamnesesList.add(this.ambulatorioFacade.inserirNotaAdicionalAnamneses(descrNotaAdicionalAnamnese, this.consultaSelecionada));
				} else {
					this.ambulatorioFacade.editarNotaAdicionalAnamneses(notaAdicionalAnamneses, descrNotaAdicionalAnamnese, this.consultaSelecionada);
				}
				notasAdicionaisAnamnesesList = this.ambulatorioFacade.obterNotaAdicionalAnamnesesConsulta(this.consultaSelecionada.getNumero());
				apresentarMsgNegocio(Severity.INFO, MSG_ATENDER_PACIENTES_AGENDADOS_REGISTRO_SALVO);
				descrNotaAdicionalAnamnese = null;
				modoInsercaoItemAnamnese = true;
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return;
		}
	}

	public void editarItemAdicionalAnamnese(MamNotaAdicionalAnamneses notaAdicional) {
		modoInsercaoItemAnamnese = false;
		notaAdicionalAnamneses = notaAdicional;
		descrNotaAdicionalAnamnese = notaAdicional.getDescricao();
	}

	public void excluirItemAdicionalAnamnese() {
		try {
			this.ambulatorioFacade.excluiNotaAdicionalAnamnese(notaAdicionalAnamneses);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return;
		}
		apresentarMsgNegocio(Severity.INFO, "MSG_ATENDER_PACIENTES_AGENDADOS_REGISTRO_EXCLUIDO");
		notasAdicionaisAnamnesesList = this.ambulatorioFacade.obterNotaAdicionalAnamnesesConsulta(this.consultaSelecionada.getNumero());
		notaAdicionalAnamneses = null;
		descrNotaAdicionalAnamnese = "";
		modoInsercaoItemAnamnese = true;
	}

	private void iniciaTextoNotasAdicionais() {
		if (textoAnamnese != null && textoAnamnese.isEmpty() && readonlyAnamnese != null && !readonlyAnamnese) {
			try {
				textoAnamnese = this.ambulatorioFacade.getDescricaoItemAnamnese();
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
			}
		}
		if (textoEvolucao != null && textoEvolucao.isEmpty() && readonlyEvolucao != null && !readonlyEvolucao) {
			try {
				textoEvolucao = this.ambulatorioFacade.getDescricaoItemEvolucao();
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
			}
		}
	}

	// --[EVOLUÇÃO]
	private void iniciaEvolucao() {
		if (textoEvolucao == null) {
			textoEvolucao = "";
		}
		MamEvolucoes evolucaoAtual = this.ambulatorioFacade.obterEvolucaoAtivaPorNumeroConsulta(this.consultaSelecionada.getNumero());

		try {
			if (evolucaoAtual != null && (DominioIndPendenteAmbulatorio.P.equals(evolucaoAtual.getPendente()) || DominioIndPendenteAmbulatorio.V.equals(evolucaoAtual.getPendente()))) {
				tipoCorrente = EVOLUCAO;
				if (DominioIndPendenteAmbulatorio.V.equals(evolucaoAtual.getPendente()) && !evolucaoAtual.getServidorValida().getId().equals(servidorLogadoSemFimVinculo.getId())) {
					readonlyEvolucao = true;
					readonlyAnamnese = true;
				}
				MamItemEvolucoes item = this.ambulatorioFacade.primeiroItemEvolucoesPorEvolucao(evolucaoAtual.getSeq());
				if (item != null && textoEvolucao.isEmpty()) {
					textoEvolucao = item.getDescricao();
				}
			} else if (EVOLUCAO.equals(tipoCorrente)) {
				tipoCorrente = null;
			}
			notasAdicionaisEvolucaoList = this.ambulatorioFacade.obterNotaAdicionalEvolucoesConsulta(this.consultaSelecionada.getNumero());
			modoInsercaoItemEvolucao = true;

			this.habilitaEvolucao = this.ambulatorioFacade.validarProcessoConsultaEvolucao();
			this.gravaEvolucao = this.ambulatorioFacade.validarProcessoExecutaEvolucao();
			this.gravaSolicitacaoExame = this.ambulatorioFacade.validarProcessoExecutaSolicitacaoExame();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void salvarEvolucao() {
		try {
			this.ambulatorioFacade.salvarEvolucao(textoEvolucao, this.consultaSelecionada);
			apresentarMsgNegocio(Severity.INFO, "LABEL_REGISTRO_SALVO", "evolução");
			tipoCorrente = EVOLUCAO;
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void excluirEvolucao() {
		try {
			MamEvolucoes evolucaoAtual = ambulatorioFacade
					.obterEvolucaoAtivaPorNumeroConsulta(consultaSelecionada
							.getNumero());
			this.ambulatorioFacade.excluiEvolucao(this.consultaSelecionada, evolucaoAtual);
			textoEvolucao = null;
			iniciaEvolucao();
			apresentarMsgNegocio(Severity.INFO, LABEL_REGISTRO_EXCLUIDO, "evolução");
			if (textoAnamnese.isEmpty()) {
				iniciaAnamnese();
			}
			iniciaTextoNotasAdicionais();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}

	}

	public void inserirItemAdicionalEvolucao() {
		try {
			validaPreenchimentoCampoNotaAdicionalEvolucao(); 
			
			avaliaInsercaoOuEdicaoNotaAdicionalEvolucao();
		
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return;
		}		
		notasAdicionaisEvolucaoList = this.ambulatorioFacade.obterNotaAdicionalEvolucoesConsulta(this.consultaSelecionada.getNumero());	
		apresentarMsgNegocio(Severity.INFO, MSG_ATENDER_PACIENTES_AGENDADOS_REGISTRO_SALVO);
		descrNotaAdicionalEvolucao = null;
		modoInsercaoItemEvolucao = true;
		}
	

	private void avaliaInsercaoOuEdicaoNotaAdicionalEvolucao()
			throws ApplicationBusinessException {
		if (modoInsercaoItemEvolucao) {
			notasAdicionaisEvolucaoList.add(this.ambulatorioFacade.inserirNotaAdicionalEvolucoes(descrNotaAdicionalEvolucao, this.consultaSelecionada));
		} else {
			this.ambulatorioFacade.editarNotaAdicionalEvolucoes(notaAdicionalEvolucoes, descrNotaAdicionalEvolucao, this.consultaSelecionada);
		}
	}

	private void validaPreenchimentoCampoNotaAdicionalEvolucao()  {
		
			if (descrNotaAdicionalEvolucao == null) {
				apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_OBRIGATORIEDADE_CAMPO_NOTA_ADICIONAL");
			
			}
			
	}

	public void editarItemAdicionalEvolucao(MamNotaAdicionalEvolucoes notaAdicional) {
		notaAdicionalEvolucoes = notaAdicional;
		modoInsercaoItemEvolucao = false;
		descrNotaAdicionalEvolucao = notaAdicional.getDescricao();
	}

	public void excluirItemAdicionalEvolucao() {
		try {
			this.ambulatorioFacade.excluiNotaAdicionalEvolucao(notaAdicionalEvolucoes);
			apresentarMsgNegocio(Severity.INFO, "MSG_ATENDER_PACIENTES_AGENDADOS_REGISTRO_EXCLUIDO");
			notasAdicionaisEvolucaoList = this.ambulatorioFacade.obterNotaAdicionalEvolucoesConsulta(this.consultaSelecionada.getNumero());
			notaAdicionalEvolucoes = null;
			descrNotaAdicionalEvolucao = "";
			modoInsercaoItemEvolucao = true;
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return;
		}
	}

	public void excluirEvolucaoSalvarAnamnese() {
		excluirEvolucao();
		salvarAnamnese();
	}

	public void excluirAnamneseSalvarEvolucao() {
		excluirAnamnese();
		salvarEvolucao();
	}

	public String excluirAnamneseDirecionarEvolucao() {
		try {
			MamAnamneses anamneseAtual = ambulatorioFacade
					.obterAnamneseAtivaPorNumeroConsulta(consultaSelecionada
							.getNumero());
			this.ambulatorioFacade.excluiAnamese(this.consultaSelecionada, anamneseAtual);
			apresentarMsgNegocio(Severity.INFO, LABEL_REGISTRO_EXCLUIDO, "anamnese");
			textoAnamnese = null;
			iniciaAnamnese();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
		closeDialog("modalExcluirAnamneseWG");
		return PAGE_PESQUISAR_PACIENTES_EVOLUCAO;
	}
	
	public String excluirEvolucaoDirecionarAnamnese() {
		try {
			MamEvolucoes evolucaoAtual = ambulatorioFacade.obterEvolucaoAtivaPorNumeroConsulta(consultaSelecionada.getNumero());
			this.ambulatorioFacade.excluiEvolucao(this.consultaSelecionada, evolucaoAtual);
			apresentarMsgNegocio(Severity.INFO, LABEL_REGISTRO_EXCLUIDO, "evolução");
			textoEvolucao = null;
			iniciaEvolucao();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
		closeDialog("modalExcluirEvolucaoWG");
		return PAGE_PESQUISAR_PACIENTES_ANAMNESE;
	}
	
	public void limparNotaAdicionalAnamnese() {
		descrNotaAdicionalAnamnese = "";
		notaAdicionalAnamneses = null;
		modoInsercaoItemAnamnese = true;
	}

	public void limparNotaAdicionalEvolucao() {
		descrNotaAdicionalEvolucao = "";
		notaAdicionalEvolucoes = null;
		modoInsercaoItemEvolucao = true;
	}

	// --[RECEITAS]
	private void iniciaReceitas() {
		receitasPacienteController.setConsultaSelecionada(consultaSelecionada);
		receitasPacienteController.iniciar();
		receitaGeral = new MamReceituarios();
		atestadoAmbulatorio = new MamAtestados();
		receitaEspecial = new MamReceituarios();
		novoItemReceitaGeral();
		novoItemReceitaEspecial();
		descricaoMedicamento = null;
		itemReceitaGeralList = new ArrayList<MamItemReceituario>();
		itemReceitaEspecialList = new ArrayList<MamItemReceituario>();
		descricaoReceitaGeral = "";
		descricaoReceitaEspecial = "";
		viasGeral = 2;
		viasEspecial = 2;
		validadeUso = null;

		try {		
				if (consultaSelecionada.getReceituarios() != null
						&& !consultaSelecionada.getReceituarios().isEmpty()) {
					MamReceituarios receituarioGeral = ambulatorioFacade
								.primeiroReceituarioPorConsultaETipo(consultaSelecionada.getNumero(), DominioTipoReceituario.G);

				if (receituarioGeral != null) {
					receitaGeral = receituarioGeral;
					if (receitaGeral.getPendente().equals(DominioIndPendenteAmbulatorio.V) && !receitaGeral.getServidorValida().getId().equals(servidorLogadoSemFimVinculo.getId())) {
						readonlyReceitaGeral = true;
					}
					itemReceitaGeralList = this.ambulatorioFacade.buscarItensReceita(receitaGeral);
					viasGeral = receitaGeral.getNroVias() != null ? receitaGeral.getNroVias().intValue() : null;
				}

				MamReceituarios receituarioEspecial = this.ambulatorioFacade.primeiroReceituarioPorConsultaETipo(this.consultaSelecionada.getNumero(), DominioTipoReceituario.E);
				if (receituarioEspecial != null) {
					receitaEspecial = receituarioEspecial;
					if (receitaEspecial.getPendente().equals(DominioIndPendenteAmbulatorio.V) && !receitaEspecial.getServidorValida().getId().equals(servidorLogadoSemFimVinculo.getId())) {
						readonlyReceitaEspecial = true;
					}
					itemReceitaEspecialList = this.ambulatorioFacade.buscarItensReceita(receitaEspecial);
					viasEspecial = receitaEspecial.getNroVias() != null ? receitaEspecial.getNroVias().intValue() : null;
				}
			}

			this.habilitaReceita = true;//this.ambulatorioFacade.validarProcessoConsultaReceita();
			this.habilitaAtestado = this.ambulatorioFacade
					.validarProcessoConsultaAtestado();
			this.gravaReceita = this.ambulatorioFacade.validarProcessoExecutaReceita();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	private void novoItemReceitaGeral() {
		itemReceitaGeral = new MamItemReceituario();
		itemReceitaGeral.setTipoPrescricao(DominioTipoPrescricaoReceituario.M);
		itemReceitaGeral.setIndInterno(DominioSimNao.S);
	}

	private void novoItemReceitaEspecial() {
		itemReceitaEspecial = new MamItemReceituario();
		itemReceitaEspecial.setTipoPrescricao(DominioTipoPrescricaoReceituario.M);
		itemReceitaEspecial.setIndInterno(DominioSimNao.S);
	}

	public void atualizaReceituarioGeral() {
		atualizaReceituarioGeral(false);
	}

	public void atualizaReceituarioGeral(boolean insereItemJunto) {
		List<MamItemReceituario> novoItemReceitaGeralList = new ArrayList<MamItemReceituario>();
		if (!insereItemJunto && itemReceitaGeralList.isEmpty()) {
			apresentarMsgNegocio(Severity.ERROR, "MSG_INSIRA_ITENS_RECEITA");
			return;
		}
		try {
			novoItemReceitaGeralList = this.ambulatorioFacade.atualizarReceituarioGeral(receitaGeral, this.consultaSelecionada, viasGeral, itemReceitaGeralList);
			if (!novoItemReceitaGeralList.isEmpty()) {
				itemReceitaGeralList = novoItemReceitaGeralList;
			}
			apresentarMsgNegocio(Severity.INFO, MSG_ATENDER_PACIENTES_AGENDADOS_REGISTRO_SALVO);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} catch (CloneNotSupportedException e) {
			apresentarMsgNegocio(Severity.ERROR, "ERRO_CLONAR_ALTERACAO_ITEM_RECEITA");
		}
	}

	public void atualizaReceituarioEspecial() {
		atualizaReceituarioEspecial(false);
	}

	public void atualizaReceituarioEspecial(boolean insereItemJunto) {
		List<MamItemReceituario> novoItemReceitaEspecialList = new ArrayList<MamItemReceituario>();
		if (!insereItemJunto && itemReceitaEspecialList.isEmpty()) {
			apresentarMsgNegocio(Severity.ERROR, "MSG_INSIRA_ITENS_RECEITA");
		}
		try {
			novoItemReceitaEspecialList = this.ambulatorioFacade.atualizarReceituarioEspecial(receitaEspecial, this.consultaSelecionada, viasEspecial, itemReceitaEspecialList);
			if (!novoItemReceitaEspecialList.isEmpty()) {
				itemReceitaEspecialList = novoItemReceitaEspecialList;
			}
			apresentarMsgNegocio(Severity.INFO, MSG_ATENDER_PACIENTES_AGENDADOS_REGISTRO_SALVO);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} catch (CloneNotSupportedException e) {
			apresentarMsgNegocio(Severity.ERROR, "ERRO_CLONAR_ALTERACAO_ITEM_RECEITA");
		}
	}

	public void excluirReceituarioEspecial() {
		try {
			this.ambulatorioFacade.excluirReceituarioEspecial(receitaEspecial, this.consultaSelecionada);
			apresentarMsgNegocio(Severity.INFO, LABEL_REGISTRO_EXCLUIDO, "Receitas Especiais");
			iniciaReceitas();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return;
		}
	}

	public void excluirReceituarioGeral() {
		try {
			this.ambulatorioFacade.excluirReceituarioGeral(receitaGeral, this.consultaSelecionada);
			apresentarMsgNegocio(Severity.INFO, LABEL_REGISTRO_EXCLUIDO, "Receitas Gerais");
			iniciaReceitas();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return;
		}
	}

	public void inserirReceitaGeral() {
		Boolean receituarioValidado = false;
		Short itemReceitaGeralSeqp = null;
		if (StringUtils.isEmpty(descricaoReceitaGeral)) {
			this.apresentarMsgNegocio(Severity.ERROR, "MSG_CAMPO_DESCRICAO_RECEITA", "descricaoReceita_tab1");
			return;
		}
		try {
			if (receitaGeral.getPendente() != null && receitaGeral.getPendente().equals(DominioIndPendenteAmbulatorio.V)) {
				receituarioValidado = true;
				if (itemReceitaGeral.getId() != null) {
					itemReceitaGeralSeqp = itemReceitaGeral.getId().getSeqp();
				}
			}
			this.ambulatorioFacade.validaValidadeItemReceitaEmMeses(itemReceitaGeral);
			this.ambulatorioFacade.desatacharItemReceituario(itemReceitaGeral);
			atualizaReceituarioGeral(true);
			// É necessário obter novamente o item no caso deste já
			// estar associado a um receituario que está validado, pois este
			// item
			// foi adicionado nos itens do receituario pendente
			if (receituarioValidado && itemReceitaGeralSeqp != null) {
				for (MamItemReceituario itemReceita : itemReceitaGeralList) {
					if (itemReceita.getId().getSeqp().equals(itemReceitaGeralSeqp)) {
						itemReceitaGeral = itemReceita;
						receitaGeral = itemReceitaGeralList.get(0).getReceituario();
						break;
					}
				}
			} else if (receituarioValidado && itemReceitaGeralSeqp == null) {
				// Pega o ultimo que foi adicionado na lista
				receitaGeral = itemReceitaGeralList.get(0).getReceituario();
			}
			itemReceitaGeral.setDescricao(descricaoReceitaGeral);
			if (itemReceitaGeral.getId() == null) {
				itemReceitaGeral.setIndSituacao(DominioSituacao.A);
				itemReceitaGeral.setReceituario(receitaGeral);
				Integer ordem = itemReceitaGeralList.size() + 1;
				itemReceitaGeral.setOrdem(ordem.byteValue());
				this.ambulatorioFacade.inserirItem(receitaGeral, itemReceitaGeral);
			} else {
				this.ambulatorioFacade.atualizarItem(itemReceitaGeral);
			}
			novoItemReceitaGeral();
			descricaoReceitaGeral = "";
			processarReceituarios();
			iniciaReceitas();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return;
		}
	}

	public void inserirReceitaEspecial() {
		Boolean receituarioValidado = false;
		Short itemReceitaEspecialSeqp = null;
		if (StringUtils.isEmpty(descricaoReceitaEspecial)) {
			this.apresentarMsgNegocio(Severity.ERROR, "MSG_CAMPO_DESCRICAO_RECEITA", "descricaoReceita_tab2");
			return;
		}
		try {
			this.ambulatorioFacade.validaValidadeItemReceitaEmMeses(itemReceitaEspecial);
			if (receitaEspecial.getPendente() != null && receitaEspecial.getPendente().equals(DominioIndPendenteAmbulatorio.V)) {
				receituarioValidado = true;
				if (itemReceitaEspecial.getId() != null) {
					itemReceitaEspecialSeqp = itemReceitaEspecial.getId().getSeqp();
				}
			}
			this.ambulatorioFacade.desatacharItemReceituario(itemReceitaEspecial);
			atualizaReceituarioEspecial(true);
			// É necessário obter novamente o item no caso deste já
			// estar associado a um receituario que está validado, pois este
			// item
			// foi adicionado nos itens do receituario pendente
			if (receituarioValidado && itemReceitaEspecialSeqp != null) {
				for (MamItemReceituario itemReceita : itemReceitaEspecialList) {
					if (itemReceita.getId().getSeqp().equals(itemReceitaEspecialSeqp)) {
						itemReceitaEspecial = itemReceita;
						receitaEspecial = itemReceitaEspecialList.get(0).getReceituario();
						break;
					}
				}
			} else if (receituarioValidado && itemReceitaEspecialSeqp == null) {
				// Pega o ultimo que foi adicionado na lista
				receitaEspecial = itemReceitaEspecialList.get(0).getReceituario();
			}
			itemReceitaEspecial.setDescricao(descricaoReceitaEspecial);
			if (itemReceitaEspecial.getId() == null) {
				itemReceitaEspecial.setIndSituacao(DominioSituacao.A);
				itemReceitaEspecial.setReceituario(receitaEspecial);
				Integer ordem = itemReceitaEspecialList.size() + 1;
				itemReceitaEspecial.setOrdem(ordem.byteValue());
				this.ambulatorioFacade.inserirItem(receitaEspecial, itemReceitaEspecial);
				itemReceitaEspecialList = this.ambulatorioFacade.buscarItensReceita(receitaEspecial);
			} else {
				this.ambulatorioFacade.atualizarItem(itemReceitaEspecial);
			}
			novoItemReceitaEspecial();
			descricaoReceitaEspecial = "";
			processarReceituarios();
			iniciaReceitas();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return;
		}
	}

	public void limparReceitaEspecial() {
		if (itemReceitaEspecial.getId() != null) {
			this.ambulatorioFacade.desatacharItemReceituario(itemReceitaEspecial);
		}
		novoItemReceitaEspecial();
		itemReceitaEspecialList = this.ambulatorioFacade.buscarItensReceita(receitaEspecial);
		descricaoReceitaEspecial = "";
	}
	
	public void calcularDiasEntreDatasAtestado() {
		try {
			if (isAtestadoComparecimentoAcompanhante()) {
				if (dataInicialFinalIguais() && !horarioInicialMaiorHorarioFinal() && dataInicialIgualAtual()) {
					this.calcularDiasEntreDatas = 0;
				} else {
					apresentarMsgNegocio(Severity.ERROR, ERRO_DATA_DIFERENTES_ATESTADO, new Object[0]);
				}
			} else {
				if (regrasHoraDataAtMedico() && dataInicialIgualAtual()) {
					calculaDiasEntreDatas();
				} else {
					apresentarMsgNegocio(Severity.ERROR, ERRO_DATA_DIFERENTES_ATESTADO, new Object[0]);
				}
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
	}

	private boolean regrasHoraDataAtMedico() {
		return (dataInicialMenorDataFinal() || (dataInicialFinalIguais() && !horarioInicialMaiorHorarioFinal()));
	}

	private boolean dataInicialFinalIguais() {
		return DateValidator.validarMesmoDia(atestadoAmbulatorio.getDataInicial(), atestadoAmbulatorio.getDataFinal());
	}

	private boolean horarioInicialMaiorHorarioFinal() {
		return DateUtil.validaHoraMaior(this.atestadoAmbulatorio.getDataInicial(), this.atestadoAmbulatorio.getDataFinal());
	}

	private boolean dataInicialMenorDataFinal() {
		return DateUtil.validaDataMenor(this.atestadoAmbulatorio.getDataInicial(), this.atestadoAmbulatorio.getDataFinal());
	}
	
	private boolean dataInicialIgualAtual(){
		return DateValidator.validarMesmoDia(this.atestadoAmbulatorio.getDataInicial(), new Date());
	}

	private void calculaDiasEntreDatas() {
		if (dataInicialFinalIguais() && !horarioInicialMaiorHorarioFinal()) {
			this.calcularDiasEntreDatas = 1;
		} else {
			this.calcularDiasEntreDatas = DateUtil.calcularDiasEntreDatas(this.atestadoAmbulatorio.getDataInicial(),
					this.atestadoAmbulatorio.getDataFinal());
			this.calcularDiasEntreDatas = this.calcularDiasEntreDatas + 1;
		}
	}
	
	public boolean inicioFormAtestado() {
		limparCamposAoMudarTipoAtestado();
		return isAtestadoComparecimentoAcompanhante();
	}

	public boolean isAtestadoComparecimentoAcompanhante() {

		if (this.opcaoTipoAtestado.getDescricao().equals("Atestado de Acompanhamento")) {
			this.opcaoSelecionadaAtestado = true;
			this.opcaoSelecionadaTipoAtestado = true;
		} else if (this.opcaoTipoAtestado.getDescricao().equals("Atestado de Comparecimento")) {
			this.opcaoSelecionadaAtestado = false;
			this.opcaoSelecionadaTipoAtestado = true;
		} else {
			if (this.opcaoTipoAtestado.getDescricao().equals("Atestado Médico")) {
				this.opcaoSelecionadaAtestado = false;
				this.opcaoSelecionadaTipoAtestado = false;
			}
		}

		return this.opcaoSelecionadaTipoAtestado;
	}
	
	private void limparCamposAoMudarTipoAtestado() {
		this.procedimentoCidAtestado = null;
		this.calcularDiasEntreDatas = 0;
		this.atestadoAmbulatorio.setDataInicial(new Date());
		this.atestadoAmbulatorio.setDataFinal(new Date());
	}

	public void inserirMamAtestado() {

		try {
			if (opcaoSelecionadaTipoAtestado) {
				if (validaCamposObrigatorioAtAcompanhante()) {
					gravarAtestados();
					limparCamposAtestado();
				} else if (validaCamposObrigatorioAtComparecimento()) {
					gravarAtestados();
					limparCamposAtestado();
				} else {
					apresentarMsgNegocio(Severity.ERROR, "ERRO_GRAVAR_ATESTADOS", new Object[0]);
				}
			} else if (!opcaoSelecionadaTipoAtestado) {
				if (validaCamposObrigatorioAtMedico()) {
					gravarAtestados();
					limparCamposAtestado();
				} else {
					apresentarMsgNegocio(Severity.ERROR, "ERRO_GRAVAR_ATESTADO", new Object[0]);
				}
			}
		} catch (BaseException e) {
			e.getMessage();
		}
	}

	public void limparCamposAtestado() {
		atestadoAmbulatorio = new MamAtestados();
		this.procedimentoCidAtestado = null;
		this.calcularDiasEntreDatas = 0;
		this.opcaoTipoAtestado = null;
	}

	private void gravarAtestados() throws BaseException {
		this.servidorLogadoSemFimVinculo = registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado(), new Date());
		this.atestadoAmbulatorio.setDthrCriacao(new Date());
		this.atestadoAmbulatorio.setServidor(servidorLogadoSemFimVinculo);
		this.atestadoAmbulatorio.setMamTipoAtestado(opcaoTipoAtestado);
		this.atestadoAmbulatorio.setConsulta(consultaSelecionada);
		this.atestadoAmbulatorio.setIndImpresso(false);
		this.atestadoAmbulatorio.setAipPacientes(this.consultaSelecionada.getPaciente());
		this.atestadoAmbulatorio.setAghCid(procedimentoCidAtestado);
		this.atestadoAmbulatorio.setNumeroDiasAtestado(calcularDiasEntreDatas);
		
		if (isAtestadoComparecimentoAcompanhante()) {
			if (dataInicialFinalIguais() && !horarioInicialMaiorHorarioFinal() && dataInicialIgualAtual()) {
				this.ambulatorioFacade.inserirAtestadoAmbulatorio(atestadoAmbulatorio);
				this.apresentarMsgNegocio(Severity.INFO,"DADOS_ATESTADO_GRAVADO_SUCESSO");
			} else {
				apresentarMsgNegocio(Severity.ERROR, ERRO_DATA_DIFERENTES_ATESTADO, new Object[0]);
				limparCamposAtestado();
			}
		} else if (regrasHoraDataAtMedico() && dataInicialIgualAtual()) {
			this.ambulatorioFacade.inserirAtestadoAmbulatorio(atestadoAmbulatorio);
			this.apresentarMsgNegocio(Severity.INFO,"DADOS_ATESTADO_GRAVADO_SUCESSO");
		} else {
			apresentarMsgNegocio(Severity.ERROR, ERRO_DATA_DIFERENTES_ATESTADO, new Object[0]);
			limparCamposAtestado();
		}

	}
	
	private boolean validaCamposObrigatorioAtMedico() {
		return atestadoAmbulatorio.getDataInicial() != null && atestadoAmbulatorio.getDataFinal() != null;
	}

	private boolean validaCamposObrigatorioAtComparecimento() {
		return atestadoAmbulatorio.getTurnoConsulta().getDescricao() != null && validaCamposObrigatorioAtMedico();
	}

	private boolean validaCamposObrigatorioAtAcompanhante() {
		return atestadoAmbulatorio.getTurnoConsulta().getDescricao() != null && atestadoAmbulatorio.getNomeAcompanhante() != null
				&& validaCamposObrigatorioAtMedico();
	}

	public void limparReceitaGeral() {
		if (itemReceitaGeral.getId() != null) {
			this.ambulatorioFacade.desatacharItemReceituario(itemReceitaGeral);
		}
		novoItemReceitaGeral();
		itemReceitaGeralList = this.ambulatorioFacade.buscarItensReceita(receitaGeral);
		descricaoReceitaGeral = "";
	}

	public void editarReceitaGeral() {
		//itemReceitaGeral = item;
		descricaoReceitaGeral = itemReceitaGeral.getDescricao();
	}

	public void excluirReceitaGeral() {
		try {
			this.ambulatorioFacade.excluirReceitaGeral(receitaGeral, itemReceitaGeral, this.consultaSelecionada, viasGeral, itemReceitaGeralList);
			apresentarMsgNegocio(Severity.INFO, LABEL_REGISTRO_EXCLUIDO, "Item Receita Geral");
			iniciaReceitas();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(),e);
		} catch (CloneNotSupportedException e) {
			apresentarMsgNegocio(Severity.ERROR, "ERRO_CLONAR_EXCLUSAO_ITEM_RECEITA");
		}
	}

	public void excluirReceitaEspecial() {
		try {
			this.ambulatorioFacade.excluirReceitaEspecial(receitaEspecial, itemReceitaEspecial, this.consultaSelecionada, viasEspecial, itemReceitaEspecialList);
			//this.ambulatorioFacade.excluirReceitaEspecial(receitaEspecial, item, consultaSelecionada, viasEspecial, itemReceitaEspecialList, servidorLogadoSemFimVinculo);
			
			apresentarMsgNegocio(Severity.INFO, LABEL_REGISTRO_EXCLUIDO, "Item Receita Especial");
			iniciaReceitas();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(),e);
		} catch (CloneNotSupportedException e) {
			apresentarMsgNegocio(Severity.ERROR, "ERRO_CLONAR_EXCLUSAO_ITEM_RECEITA");
		}
	}

	public void upItemReceitaGeral(Integer ordem) {
		try {
			MamItemReceituario itemAtual = itemReceitaGeralList.get(ordem);
			MamItemReceituario itemAcima = itemReceitaGeralList.get(ordem - 1);
			Byte ordemAtual = itemAtual.getOrdem();
			itemAtual.setOrdem(itemAcima.getOrdem());
			itemAcima.setOrdem(ordemAtual);
			this.ambulatorioFacade.atualizarItem(itemAtual);
			this.ambulatorioFacade.atualizarItem(itemAcima);
			itemReceitaGeralList = this.ambulatorioFacade.buscarItensReceita(receitaGeral);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void downItemReceitaGeral(Integer ordem) {
		try {
			MamItemReceituario itemAtual = itemReceitaGeralList.get(ordem);
			MamItemReceituario itemAbaixo = itemReceitaGeralList.get(ordem + 1);
			Byte ordemAtual = itemAtual.getOrdem();
			itemAtual.setOrdem(itemAbaixo.getOrdem());
			itemAbaixo.setOrdem(ordemAtual);
			this.ambulatorioFacade.atualizarItem(itemAtual);
			this.ambulatorioFacade.atualizarItem(itemAbaixo);
			itemReceitaGeralList = this.ambulatorioFacade.buscarItensReceita(receitaGeral);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void upItemReceitaEspecial(Integer ordem) {
		try {
			MamItemReceituario itemAtual = itemReceitaEspecialList.get(ordem);
			MamItemReceituario itemAcima = itemReceitaEspecialList.get(ordem - 1);
			Byte ordemAtual = itemAtual.getOrdem();
			itemAtual.setOrdem(itemAcima.getOrdem());
			itemAcima.setOrdem(ordemAtual);
			this.ambulatorioFacade.atualizarItem(itemAtual);
			this.ambulatorioFacade.atualizarItem(itemAcima);
			itemReceitaEspecialList = this.ambulatorioFacade.buscarItensReceita(receitaEspecial);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void downItemReceitaEspecial(Integer ordem) {
		try {
			MamItemReceituario itemAtual = itemReceitaEspecialList.get(ordem);
			MamItemReceituario itemAbaixo = itemReceitaEspecialList.get(ordem + 1);
			Byte ordemAtual = itemAtual.getOrdem();
			itemAtual.setOrdem(itemAbaixo.getOrdem());
			itemAbaixo.setOrdem(ordemAtual);
			this.ambulatorioFacade.atualizarItem(itemAtual);
			this.ambulatorioFacade.atualizarItem(itemAbaixo);
			itemReceitaEspecialList = this.ambulatorioFacade.buscarItensReceita(receitaEspecial);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void editarReceitaEspecial() {
		//itemReceitaEspecial = item;
		descricaoReceitaEspecial = itemReceitaEspecial.getDescricao();
	}

	public void atualizaValidadeGeral() {
		itemReceitaGeral.setValidadeMeses(Byte.valueOf("6"));
	}

	public void atualizaValidadeEspecial() {
		itemReceitaEspecial.setValidadeMeses(Byte.valueOf("6"));
	}

	public void verificaTipoEspecial(MamItemReceituario item) {
		if (DominioTipoPrescricaoReceituario.F.equals(item.getTipoPrescricao()) && (StringUtils.isBlank(descricaoReceitaEspecial))) {
				descricaoReceitaEspecial = DESCR_FORMULA;
		} else 	if (descricaoReceitaEspecial.equalsIgnoreCase(DESCR_FORMULA)) {
				descricaoReceitaEspecial = "";
			}
		}
	
	public void verificaTipoGeral(MamItemReceituario item) {
		if (DominioTipoPrescricaoReceituario.F.equals(item.getTipoPrescricao()) && (StringUtils.isBlank(descricaoReceitaGeral))) {
				descricaoReceitaGeral = DESCR_FORMULA;
		} else if (descricaoReceitaGeral.equalsIgnoreCase(DESCR_FORMULA)) {
				descricaoReceitaGeral = "";
			}
		}

	@SuppressWarnings("PMD.NPathComplexity")
	public String getIdadeFormatada() {
		int idade = this.consultaSelecionada.getPaciente().getIdade();
		if (Boolean.TRUE.equals(this.consultaSelecionada.getGradeAgendamenConsulta().getEspecialidade().getIndEspPediatrica()) || idade < 12) {

			Calendar dtNasc = Calendar.getInstance();
			dtNasc.setTime(DateUtil.truncaData(this.consultaSelecionada.getPaciente().getDtNascimento()));
			Calendar hoje = DateUtils.truncate(Calendar.getInstance(), Calendar.DATE);
			int anos = 0, meses = 0, dias = 0;
			if (hoje.get(Calendar.YEAR) > dtNasc.get(Calendar.YEAR)) {
				anos = hoje.get(Calendar.YEAR) - dtNasc.get(Calendar.YEAR);
				dtNasc.add(Calendar.YEAR, anos);
				if (hoje.before(dtNasc)) {
					anos -= 1;
					dtNasc.add(Calendar.YEAR, -1);
				}
			}
			boolean isDia = false;
			while (dtNasc.before(hoje)) {
				if (!isDia) {
					meses += 1;
					dtNasc.add(Calendar.MONTH, 1);
					if (hoje.before(dtNasc)) {
						meses -= 1;
						dtNasc.add(Calendar.MONTH, -1);
						isDia = true;
					}
				} else {
					dias += 1;
					dtNasc.add(Calendar.DATE, 1);
				}
			}
			StringBuffer idadeStr = new StringBuffer();
			if (anos > 0) {
				idadeStr.append(anos);
				if (anos > 1) {
					idadeStr.append(" anos ");
				} else {
					idadeStr.append(" ano ");
				}
			}
			if (meses > 0) {
				/*
				 * if (anos==1 && dias>0){ idadeStr+=", "; }else
				 */
				if (anos > 0) {
					idadeStr.append(" e ");
				}
				idadeStr.append(meses);
				if (meses > 1) {
					idadeStr.append(" meses ");
				} else {
					idadeStr.append(" mês ");
				}
			}
			if (dias > 0) {
				if (anos == 0) {
					if (meses > 0) {
						idadeStr.append(" e ");
					}
					idadeStr.append(dias);
					if (dias > 1) {
						idadeStr.append(" dias ");
					} else {
						idadeStr.append(" dia ");
					}
				}
			}
			return idadeStr.toString();

		} else {
			return idade > 1 ? idade + " anos" : idade + " ano";
		}
	}

	public List<VAfaDescrMdto> obterMedicamentosReceitaVO(String strPesquisa) {
		return this.returnSGWithCount(this.farmaciaFacade.obterMedicamentosReceitaVO((String) strPesquisa),obterMedicamentosReceitaVOCount(strPesquisa));
	}

	public Long obterMedicamentosReceitaVOCount(String strPesquisa) {
		return this.farmaciaFacade.obterMedicamentosReceitaVOCount((String) strPesquisa);
	}

	public void atualizarDescMedicamentoGeral() {
		atualizarDescMedicamento(itemReceitaGeral, DominioTipoReceituario.G);
	}

	public void atualizarDescMedicamentoEspecial() {
		atualizarDescMedicamento(itemReceitaEspecial, DominioTipoReceituario.E);
	}

	public void atualizarDescMedicamento(MamItemReceituario item, DominioTipoReceituario tipo) {
		StringBuilder descricao = new StringBuilder("");
		if (StringUtils.isNotBlank(descricaoMedicamento.getId().getDescricaoMat())) {
			descricao.append(descricaoMedicamento.getId().getDescricaoMat());
		}
		if (StringUtils.isNotBlank(descricaoMedicamento.getConcentracaoFormatada())) {
			descricao.append(' ').append(descricaoMedicamento.getConcentracaoFormatada());
		}
		if (descricaoMedicamento.getUnidadeMedidaMedicas() != null && StringUtils.isNotBlank(descricaoMedicamento.getUnidadeMedidaMedicas().getDescricao())) {
			descricao.append(' ').append(descricaoMedicamento.getUnidadeMedidaMedicas().getDescricao());
		}
		item.setTipoPrescricao(DominioTipoPrescricaoReceituario.M);
		if (DominioTipoReceituario.G.equals(tipo)) {
			descricaoReceitaGeral = descricao.toString();
		} else {
			descricaoReceitaEspecial = descricao.toString();
		}
		descricaoMedicamento = null;
	}
	
	public List<AghEspecialidades> obterEspecialidade(String parametro) {
		return aghuFacade.getListaEspecialidades((String) parametro);
	}

	public String cancelarAtendimento() {
		try {
			if(!this.ambulatorioFacade.verificaExtratoPacAtendidoOuFechado(consultaSelecionada.getNumero())){
				
				this.ambulatorioFacade.procedimentosReceituarioCuidadoCancelarAtendimento(consultaSelecionada.getNumero(),
						cadastroCuidadosPacienteController.getReceituarioAnterior());
				limparValoresReceituarioCuidadoController();
				
				defineSituacaoAtendimentoAguardando(consultaSelecionada);
				
				this.ambulatorioFacade.cancelarAtendimento(this.getConsultaSelecionada(), nomeMicrocomputador);
				//this.ambulatorioFacade.cancelarAtendimento(this.getConsultaSelecionada(), nomeMicrocomputador, servidorLogado);
				textoAnamnese = null;
				textoEvolucao = null;
				//11942  Remover todos atestados relacionados a consulta
				ambulatorioFacade.acaoCancelarAtendimento(consultaSelecionada);
				this.consultaSelecionada.setAtender(null);
			}
			
			this.resetarTela();
			return PAGE_PESQUISAR_PACIENTES_AGENDADOS;
		} catch (BaseException exception) {
			apresentarExcecaoNegocio(exception);
			LOG.error(EXCECAO_CAPTURADA, exception);
		} catch (GenericJDBCException e) {
			LOG.error(EXCECAO_CAPTURADA, e);
			apresentarMsgNegocio(Severity.ERROR, "ERRO_CONTROLE_ALTERADO");
			return this.redirecionarParaListaPacientes();
		} catch (Exception e){
			apresentarMsgNegocio(Severity.ERROR, "ERRO_CONTROLE_ALTERADO");
			LOG.error(EXCECAO_CAPTURADA, e);
		}
		return null;
	}
	
	private void defineSituacaoAtendimentoPacienteAtendido(AacConsultas consulta) throws ApplicationBusinessException {
		atualizaSituacaoConsulta(DominioSituacaoAtendimento.PACIENTE_ATENDIDO, consulta);
	}
	
	private void defineSituacaoAtendimentoAguardando(AacConsultas consulta) throws ApplicationBusinessException {
		atualizaSituacaoConsulta(DominioSituacaoAtendimento.AGUARDANDO_ATENDIMENTO, consulta);
	}
	
	private void atualizaSituacaoConsulta(DominioSituacaoAtendimento situacaoAtendimento, AacConsultas consulta) 
			throws ApplicationBusinessException {
		AacRetornos retorno = ambulatorioFacade.obterRetorno(situacaoAtendimento.getCodigo());
		consulta.setRetorno(retorno);
		consulta.setRetSeq(situacaoAtendimento.getCodigo());
		ambulatorioFacade.atualizarConsultaRetorno(consulta);
	}

	private String carregarModal() {
		
		pesquisarPacientesAgendadosController.setFlagModalImprimir(false);
		
		try {
			if (this.ambulatorioFacade.existeDocumentosImprimirPaciente(this.getConsultaSelecionada(), true)) {
				this.obterListaDocumentosPaciente();
			}
			//carregarModal = true;
			this.openDialog("modalFinalizarAtendimentoWG");
			return null;
		} catch (BaseException exception) {
			//carregarModal = false;
			apresentarExcecaoNegocio(exception);
			LOG.error(EXCECAO_CAPTURADA, exception);
			return this.redirecionarParaListaPacientes();
		}
	}

	/**
	 * Limpa/Reseta a tela
	 */
	private void resetarTela() {
		this.numeroConsulta = null; // Parâmetro de tela
		this.consultaSelecionada = null; // Instância do parâmetro de tela
		textoAnamnese = null;
		textoEvolucao = null;		
	}

	public String finalizarAtendimento() {
		
		try {
			
			if(this.evoSeq != null){
				this.ambulatorioFacade.excluirRespostaEItemEvolucao(evoSeq);
			}

			this.obterListaDocumentosPacienteParaCertificacao();
			defineSituacaoAtendimentoPacienteAtendido(this.getConsultaSelecionada());
			this.ambulatorioFacade.finalizarAtendimento(this.getConsultaSelecionada(),verificarProcedimentoRealizado(), nomeMicrocomputador);
			try {
				if(certificacaoDigitalFacade.verificaProfissionalHabilitado()){
					this.geraPendenciasCertificacaoDigital();
				}
			} catch (Exception e) {
				LOG.error(e.getMessage(),e);
			}
			//11942 Alterar situação para 'V'
			this.ambulatorioFacade.acaoFinalizarAtendimento(consultaSelecionada);
			this.ambulatorioFacade.procedimentosReceituarioCuidadoFinalizarAtendimento(consultaSelecionada.getNumero(),cadastroCuidadosPacienteController.getReceituarioAnterior());
			limparValoresReceituarioCuidadoController();
			return  this.carregarModal();
		} catch (BaseException exception) {
			apresentarExcecaoNegocio(exception);
			LOG.error(EXCECAO_CAPTURADA, exception);
			return null;
		} catch (GenericJDBCException e) {
			LOG.error(EXCECAO_CAPTURADA, e);
			apresentarMsgNegocio(Severity.ERROR, "ERRO_CONTROLE_ALTERADO");
			return this.redirecionarParaListaPacientes();
		}
	}

	private void limparValoresReceituarioCuidadoController() {
		cadastroCuidadosPacienteController.setFrist(true);
		cadastroCuidadosPacienteController.limpar();
		cadastroCuidadosPacienteController.setReceituarioAtual( new MamReceituarioCuidado());
		cadastroCuidadosPacienteController.setReceituarioAnterior( new MamReceituarioCuidado());
	}

	public String redirecionarParaListaPacientes() {
		textoAnamnese = null;
		textoEvolucao = null;
		this.resetarTela();
		
		if (pesquisarPacientesAgendadosController.getFlagModalImprimir()==false){
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_FINALIZAR_ATENDIMENTO");
		}
		
		return PAGE_PESQUISAR_PACIENTES_AGENDADOS;
	}
	
	public String redirecionarEvolucaoPacientes() {
		//atenderPacientesEvolucaoController.setCameFrom("ambulatorio-atenderPacientesAgendados");
		//atenderPacientesEvolucaoController.setConsultaSelecionada(consultaSelecionada);
		
		//código movido da antiga aba de evolucao
		MamAnamneses anamneseAtual = this.ambulatorioFacade.obterAnamneseAtivaPorNumeroConsulta(this.consultaSelecionada.getNumero());
		if (anamneseAtual != null && (DominioIndPendenteAmbulatorio.P.equals(anamneseAtual.getPendente()) || DominioIndPendenteAmbulatorio.V.equals(anamneseAtual.getPendente()))) {
			if ((DominioIndPendenteAmbulatorio.V.equals(anamneseAtual.getPendente())) && !anamneseAtual.getServidorValida().getId().equals(servidorLogadoSemFimVinculo.getId())) {
				apresentarMsgNegocio(Severity.ERROR, "MAM_02027");
			} else {
				openDialog("modalExcluirAnamneseWG");
			}
			return null;
		} 
			
		return PAGE_PESQUISAR_PACIENTES_EVOLUCAO;
	}
	
	public String redirecionarAnamnesePacientes() {
		
		MamEvolucoes evolucaoAtual = this.ambulatorioFacade.obterEvolucaoAtivaPorNumeroConsulta(this.consultaSelecionada.getNumero());
		if (evolucaoAtual != null && (DominioIndPendenteAmbulatorio.P.equals(evolucaoAtual.getPendente()) || DominioIndPendenteAmbulatorio.V.equals(evolucaoAtual.getPendente()))) {
			if ((DominioIndPendenteAmbulatorio.V.equals(evolucaoAtual.getPendente())) && !evolucaoAtual.getServidorValida().getId().equals(servidorLogadoSemFimVinculo.getId())) {
				apresentarMsgNegocio(Severity.ERROR, "MAM_02028");
			} else {
				openDialog("modalExcluirEvolucaoWG");
			}
			return null;
		} 
		return PAGE_PESQUISAR_PACIENTES_ANAMNESE;
	}
	
	/**
	 * Inicializa os objetos exibidos na aba 4 - Procedimentos
	 * 
	 */
	public void iniciarProcedimentosAtendConsulta() {
		limparProcedimentoAtendConsulta();
		carregarListaProcedimentosAtendConsulta(true);
		try {
			this.habilitaProcedimento = this.ambulatorioFacade.validarProcessoConsultaProcedimento();
			this.gravaProcedimento = this.ambulatorioFacade.validarProcessoExecutaProcedimento();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * Carrega a lista de procedimentos de atendimento da consulta. Utilizado na
	 * aba Procedimentos.
	 */
	public void carregarListaProcedimentosAtendConsulta(Boolean inicio) {
		Short espSeqGenerica = null;
		AacConsultas consulta = ambulatorioFacade.obterAacConsultasJoinGradeEEspecialidade(this.consultaSelecionada.getNumero());
		AghEspecialidades especialidadeGenerica = consulta.getGradeAgendamenConsulta().getEspecialidade().getEspecialidade();
		
		if (especialidadeGenerica != null) {
			espSeqGenerica = especialidadeGenerica.getSeq();
		} else {
			espSeqGenerica = VALOR_PADRAO_ESPECIALIDADE_GENERICA;
		}

		try {
			listaProcedimentosAtendConsulta = this.ambulatorioFacade.listarProcedimentosAtendimento(consulta.getNumero(), consulta.getGradeAgendamenConsulta()
					.getEspecialidade().getSeq(), espSeqGenerica, inicio);
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(),e);
		}

		try {
			// Verifica se algum procedimento foi realizado
			verificarProcedimentoRealizado();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(),e);
		}
	}

	/**
	 * Retorna true caso algum dos procedimentos tenha sido realizado. Caso
	 * contrário retorna false.
	 * 
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public Boolean verificarProcedimentoRealizado() throws ApplicationBusinessException {
		if (listaProcedimentosAtendConsulta != null && !listaProcedimentosAtendConsulta.isEmpty()) {
			for (ProcedimentoAtendimentoConsultaVO procedimentoVO : listaProcedimentosAtendConsulta) {
				if (procedimentoVO.getSituacao().equals(DominioSituacao.A)) {
					return true;
				}
			}
		}
		return false;
	}

	private Boolean verificarProcedimentoNenhumRealizado() throws ApplicationBusinessException {
		if (listaProcedimentosAtendConsulta != null && !listaProcedimentosAtendConsulta.isEmpty()) {
			BigDecimal seqProcNenhum = this.ambulatorioFacade.buscarSeqNenhumProcedimento();
			for (ProcedimentoAtendimentoConsultaVO procedimentoVO : listaProcedimentosAtendConsulta) {
				if (this.ambulatorioFacade.verificarNenhumProcedimento(procedimentoVO.getSeq(), seqProcNenhum) && procedimentoVO.getSituacao().equals(DominioSituacao.A)) {
					return true;
				}
			}
		}
		return false;
	}

	/** Metodo para deixar consulta pendente */
	public String gravarPendencia() {
		try {
			this.ambulatorioFacade.mampPend(this.consultaSelecionada.getNumero(), new Date(), this.ambulatorioFacade.buscaSituacaoPendencia(motivoPendencia), nomeMicrocomputador);
			textoAnamnese = null;
			textoEvolucao = null;
			this.resetarTela();
			this.motivoPendencia = DominioMotivoPendencia.POS;
			return PAGE_PESQUISAR_PACIENTES_AGENDADOS;
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(),e);
		}
		return null;
	}

	public String getProntuarioFormatado() {
		return this.consultaSelecionada != null ? CoreUtil.formataProntuarioRelatorio(this.consultaSelecionada.getPaciente().getProntuario()) : null;
	}

	/**
	 * Impressão direta usando o CUPS.
	 */
	public void directPrint() {

		try {
			DocumentoJasper documento = gerarDocumento(this.dominioTipoDocumento, true);

			this.sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());

			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}

	}

	@Override
	public List<Object> recuperarColecao() throws ApplicationBusinessException {

		List<Object> lista = new ArrayList<Object>();
		lista.add(colecao);
		return lista;
	}

	protected void apresentarExcecaoNegocio(ApplicationBusinessException e) {
		// Apenas apresenta a mensagem de erro negocial para o cliente
		apresentarMsgNegocio(Severity.ERROR, e.getLocalizedMessage());
	}

	@Override
	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("dataAtual", new Date());
		try {
			params.put("caminhoLogo", recuperarCaminhoLogo());
		} catch (BaseException e) {
			LOG.error("Erro ao tentar recuparar logotipo para o relatório", e);
		}
		return params;
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/ambulatorio/report/relatorioAnamnese.jasper";
	}

	/**
	 * Método invocado pelo a:mediaOutput para geração de PDF dentro de XHTML.
	 * 
	 * @param out
	 * @param data
	 */
	public StreamedContent getRenderPdf() throws DocumentException, IOException, JRException {
		try {
			DocumentoJasper documento = gerarDocumento();
			return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
	}
	
	private void imprimirDocumentosPacienteSubParte1(DocumentosPacienteVO documento){
		if (documento.getSolicitacaoExame() != null && Boolean.TRUE.equals(documento.getSelecionado())) {
			try {
				relatorioTicketExamesPacienteController.directPrint(documento.getSolicitacaoExame().getSeq(), null, null, documento.getImprimiu());
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
			} catch (JRException e) {
				LOG.error(EXCECAO_CAPTURADA, e);
			} catch (SystemException e) {
				LOG.error(EXCECAO_CAPTURADA, e);
			} catch (IOException e) {
				LOG.error(EXCECAO_CAPTURADA, e);
			}
		}
		
		if (documento.getReceituarios() != null && Boolean.TRUE.equals(documento.getSelecionado())) {
			try {
				relatorioConclusaoAbaReceituario.imprimirReceitaMedica(documento.getReceituarios().getSeq(), documento.getImprimiu());
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
			} catch (JRException e) {
				LOG.error(EXCECAO_CAPTURADA, e);
			} catch (SystemException e) {
				LOG.error(EXCECAO_CAPTURADA, e);
			} catch (IOException e) {
				LOG.error(EXCECAO_CAPTURADA, e);
			}
			
			try {
				this.ambulatorioFacade.atualizarIndImpressaoReceituario(documento.getReceituarios().getSeq());
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
		}
	}
	
	/**
	 * Método para impressão de documentos do paciente.
	 * 
	 * @return
	 * @throws JRException 
	 * @throws IOException 
	 * @throws SystemException 
	 * @throws BaseException 
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	public String imprimirDocumentosPaciente() throws JRException, BaseException, SystemException, IOException {

		for (DocumentosPacienteVO documento : listaDocumentosPaciente) {
			if (documento.getAnamnese() != null && Boolean.TRUE.equals(documento.getSelecionado())) {
				try {
					this.colecao = this.ambulatorioFacade.retornarRelatorioAnamneseEvolucao(this.consultaSelecionada.getNumero(), DominioAnamneseEvolucao.A);
					colecao.setCaminhoLogo(recuperarCaminhoLogo());
					this.dominioTipoDocumento = DominioTipoDocumento.ANA;
				} catch (ApplicationBusinessException e) {
					e.getMessage();
					this.apresentarMsgNegocio(Severity.ERROR, e.getLocalizedMessage());
				}
				this.directPrint();
				if (!documento.getImprimiu()) {
					this.ambulatorioFacade.atualizarIndImpressaoAnamnese(documento.getAnamnese().getSeq());
				}
			}
			if (documento.getEvolucao() != null && Boolean.TRUE.equals(documento.getSelecionado())) {
				try {
					this.colecao = this.ambulatorioFacade.retornarRelatorioAnamneseEvolucao(this.consultaSelecionada.getNumero(), DominioAnamneseEvolucao.E);
					this.dominioTipoDocumento = DominioTipoDocumento.EV;

				} catch (ApplicationBusinessException e) {
					e.getMessage();
					this.apresentarMsgNegocio(Severity.ERROR, e.getLocalizedMessage());
				}
				this.directPrint();
				if (!documento.getImprimiu()) {
					this.ambulatorioFacade.atualizarIndImpressaoEvolucao(documento.getEvolucao().getSeq());
				}
			}
			if (documento.getDescricao().equalsIgnoreCase("Atestado Médico") && documento.getSelecionado()) {
				try {
					relatorioAtestadoController.recuperarAtestado(this.consultaSelecionada, documento.getAtesdado().getSeq());
					relatorioAtestadoController.recuperarColecao();
					relatorioAtestadoController.recuperarArquivoRelatorio();
				} catch (ApplicationBusinessException e) {
					e.getMessage();
					this.apresentarMsgNegocio(Severity.ERROR, e.getLocalizedMessage());
				}
				relatorioAtestadoController.directPrint();
				if (!documento.getImprimiu()) {
					ambulatorioFacade.atualizarIndImpressaoAtestado(documento.getAtesdado().getSeq());
				}

			}
			if (documento.getDescricao().equalsIgnoreCase("Atestado de Acompanhamento") && documento.getSelecionado()) {
				try {
					relatorioAtestadoAcompanhanteController.recuperarAtestado(this.consultaSelecionada,documento.getAtesdado().getSeq());
					relatorioAtestadoAcompanhanteController.recuperarColecao();
					relatorioAtestadoAcompanhanteController.recuperarArquivoRelatorio();
				} catch (ApplicationBusinessException e) {
					e.getMessage();
					this.apresentarMsgNegocio(Severity.ERROR, e.getLocalizedMessage());
				}
				relatorioAtestadoAcompanhanteController.directPrint();
				if (!documento.getImprimiu()) {
					ambulatorioFacade.atualizarIndImpressaoAtestado(documento.getAtesdado().getSeq());
				}
			}
			if (documento.getDescricao().equalsIgnoreCase("Atestado de Comparecimento") && documento.getSelecionado()) {
				try {
					relatorioAtestadoComparecimentoController.recuperarAtestado(this.consultaSelecionada, documento.getAtesdado().getSeq());
					relatorioAtestadoComparecimentoController.recuperarColecao();
					relatorioAtestadoComparecimentoController.recuperarArquivoRelatorio();
				} catch (ApplicationBusinessException e) {
					e.getMessage();
					this.apresentarMsgNegocio(Severity.ERROR, e.getLocalizedMessage());
				}
				relatorioAtestadoComparecimentoController.directPrint();
				if (!documento.getImprimiu()) {
					ambulatorioFacade.atualizarIndImpressaoAtestado(documento.getAtesdado().getSeq());
				}
			}
			/*
			 * TODO PENDÊNCIA ARQUITETURA - Passar por parâmetro para imprimeInformacoesComplementaresController
			 *  */
			if (documento.getRespostaQuestao() != null && Boolean.TRUE.equals(documento.getSelecionado())) {
				imprimeInformacoesComplementaresController.setSoeSeq(documento.getRespostaQuestao().getAelItemSolicitacaoExames().getId().getSoeSeq());
				imprimeInformacoesComplementaresController.setSeqp(documento.getRespostaQuestao().getAelItemSolicitacaoExames().getId().getSeqp());
				imprimeInformacoesComplementaresController.setQtnSeq(documento.getRespostaQuestao().getQuestionario().getSeq());
				imprimeInformacoesComplementaresController.directPrint();
				atualizarIndImpressaoQuestionario(documento);
			}
			/*
			 * FIM DA PENDÊNCIA
			 */
			
			this.imprimirDocumentosPacienteSubParte1(documento);
			
			//VERIFICA SE POSSUI DOCUMENTO TICKET RETORNO A IMPRIMIR
			verificarTicketRetornoAImprimir(documento);
			//Receituario Cuidados
			verificarReceituarioCuidado(documento);
			//CONSULTORIAL AMBULATORIAL
			verificarConsultasAmbulatorial(documento);
			/**
			 * Controller Auxiliar para correção de erro NCSS de PMD(Excesso de linhas Físicas)
			 * A Controller abaixo chama os metodos necessários para verificarImpressaoRelatorios
			 * A classe atual está cheia. Recomenda-se usar a auxiliar.
			 */
			//VERIFICAR RELATORIOS RESTANTES - IMPLEMENTAR AGORA DENTRO DESTE CONTROLLER..
			//documento.setNumeroConsulta(consultaSelecionada.getNumero());
			//this.atenderPacientesAgendadosAuxiliarController.verificarImpressaoRelatorios(documento);
			
		}
		textoAnamnese = null;
		textoEvolucao = null;
		this.resetarTela();
		this.atenderPacientesEvolucaoController.setExecutouIniciar(false);
		this.pesquisarPacientesAgendadosController.pesquisar();
		this.imprimirTicketRetornoController.setMensagem(false);
		return PAGE_PESQUISAR_PACIENTES_AGENDADOS;
	}

	/**
	 * VERIFICA SE POSSUI DOCUMENTO TICKET RETORNO A IMPRIMIR
	 */
	private void verificarTicketRetornoAImprimir(DocumentosPacienteVO documento){
		
		if(documento.getDocumentoTicketRetorno() != null && documento.getSelecionado()){
			try {
				AghParametros aghParam = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_HOSPITAL_RAZAO_SOCIAL);
				if(aghParam != null && (aghParam.getVlrTexto() != null && !aghParam.getVlrTexto().trim().isEmpty())){
					imprimirTicketRetornoController.setNomeHospital(aghParam.getVlrTexto());
				}
			} catch (ApplicationBusinessException e) {
				this.apresentarExcecaoNegocio(e);
			}
			
			MamSolicitacaoRetorno mamSolicitacaoRetorno = this.ambulatorioFacade.obterMamSolicitacaoRetornoPorChavePrimaria(documento.getDocumentoTicketRetorno().getSeq());
			Object[] buscaConsProf = null;
			try {
				if(mamSolicitacaoRetorno != null && mamSolicitacaoRetorno.getMatriculaValida() != null && mamSolicitacaoRetorno.getVinculoValido() != null){
					 buscaConsProf = prescricaoMedicaFacade.buscaConsProf(mamSolicitacaoRetorno.getMatriculaValida(), mamSolicitacaoRetorno.getVinculoValido());
				}
				
				if(buscaConsProf != null){
					
				StringBuilder builder = new StringBuilder(100);
				if (buscaConsProf[1] != null) {
					builder.append(buscaConsProf[1].toString());
				}
				if (buscaConsProf[2] != null) {
					builder.append(" - ").append(buscaConsProf[2].toString()).append(ESPACO);
				}
				if (buscaConsProf[3] != null) {
					builder.append(buscaConsProf[3].toString());
				}
								
				imprimirTicketRetornoController.setNomeMedico(builder.toString());
				}
				
			} catch (ApplicationBusinessException e) {
				this.apresentarExcecaoNegocio(e);
			}
			
			imprimirTicketRetornoController.setIdentificacao(documento.getDocumentoTicketRetorno().getSeq().toString());
			imprimirTicketRetornoController.setDescricao(this.ambulatorioFacade.mamcTicketReceita(documento.getDocumentoTicketRetorno().getSeq().intValue(), "TICKET"));
			imprimirTicketRetornoController.directPrint();
			
			if(!documento.getImprimiu()){
				this.ambulatorioFacade.atualizarIndImpressaoSolicitacaoRetorno(documento.getDocumentoTicketRetorno().getSeq());
			}
		}
	}

	public void verificarReceituarioCuidado(DocumentosPacienteVO documento) throws SistemaImpressaoException, ApplicationBusinessException, UnknownHostException, JRException{
		if (documento.getReceituarioCuidado() != null && documento.getSelecionado()) {
						
			relatorioReceitaCuidadosController.setrCuidado(documento.getReceituarioCuidado());
			relatorioReceitaCuidadosController.setDescricaoDocumento(documento.getDescricao());
			relatorioReceitaCuidadosController.imprimir();
			if (!documento.getImprimiu()) {
				for(MamReceituarioCuidadoVO rCuidado: documento.getReceituarioCuidado()){
					this.ambulatorioFacade.atualizarIndImpressaoReceituarioCuidado(rCuidado.getSeq());
			}
		}
	}
	}
	public void verificarConsultasAmbulatorial(DocumentosPacienteVO documento){
		if (documento.getConsultoriaAmbulatorial() != null && !documento.getConsultoriaAmbulatorial().isEmpty() && documento.getSelecionado()) {
			if(documento.getConsultoriaAmbulatorial().get(0).getObservacao() == null){
				documento.getConsultoriaAmbulatorial().get(0).setObservacao(StringUtils.EMPTY);
			}
			if(documento.getConsultoriaAmbulatorial().get(0).getObservacaoAdicional() == null){
				documento.getConsultoriaAmbulatorial().get(0).setObservacaoAdicional(StringUtils.EMPTY);
			}
			relatorioConsultaAmbularorialController.setConsultoriaAmbulatorial(documento.getConsultoriaAmbulatorial());
			relatorioConsultaAmbularorialController.setDescricaoDocumento(documento.getDescricao());
			relatorioConsultaAmbularorialController.directPrint();
			if (!documento.getImprimiu()) {					
				this.ambulatorioFacade.atualizarIndImpressaoInterconsultas(documento.getConsultoriaAmbulatorial().get(0).getSeq());
			}
		}
	}
	public void atualizarIndImpressaoQuestionario(DocumentosPacienteVO documento) {
		if (!documento.getImprimiu()) {
			this.exameFacade.atualizarIndImpressaoQuestionario(documento.getRespostaQuestao());
		}
	}
	public void obterListaDocumentosPaciente() throws ApplicationBusinessException {
		listaDocumentosPaciente = this.ambulatorioFacade.obterListaDocumentosPaciente(this.consultaSelecionada.getNumero(), null, true);
	}

	public String solicitarExames(){
		return SOLICITACAO_EXAME_CRUD;
	}
	
	public String solicitarInternacao(){
		return SOLICITACAO_INTERNACAO;
	}
	
	
	// --[GETTERS / SETTERS]

	public String voltar() {
		final String voltarTela = comeFrom;
		this.resetarTela();
		return voltarTela;
	}

	public Integer getSelectedTab() {
		return selectedTab;
	}

	public void setSelectedTab(Integer selectedTab) {
		this.selectedTab = selectedTab;
	}

	public Integer getNumeroConsulta() {
		return numeroConsulta;
	}

	public void setNumeroConsulta(Integer numeroConsulta) {
		this.numeroConsulta = numeroConsulta;
	}

	public AacConsultas getConsultaSelecionada() {
		return consultaSelecionada;
	}

	public void setConsultaSelecionada(AacConsultas consultaSelecionada) {
		this.consultaSelecionada = consultaSelecionada;
	}

	public String getComeFrom() {
		return comeFrom;
	}

	public void setComeFrom(String comeFrom) {
		this.comeFrom = comeFrom;
	}

	public String getLabelZona() {
		return labelZona;
	}

	public void setLabelZona(String labelZona) {
		this.labelZona = labelZona;
	}

	public List<MamNotaAdicionalAnamneses> getNotasAdicionaisAnamnesesList() {
		return notasAdicionaisAnamnesesList;
	}

	public void setNotasAdicionaisAnamnesesList(List<MamNotaAdicionalAnamneses> notasAdicionaisAnamnesesList) {
		this.notasAdicionaisAnamnesesList = notasAdicionaisAnamnesesList;
	}

	public List<MamNotaAdicionalEvolucoes> getNotasAdicionaisEvolucaoList() {
		return notasAdicionaisEvolucaoList;
	}

	public void setNotasAdicionaisEvolucaoList(List<MamNotaAdicionalEvolucoes> notasAdicionaisEvolucaoList) {
		this.notasAdicionaisEvolucaoList = notasAdicionaisEvolucaoList;
	}

	public String getConsultasAnterioresDescricao() {
		return consultasAnterioresDescricao;
	}

	public void setConsultasAnterioresDescricao(String consultasAnterioresDescricao) {
		this.consultasAnterioresDescricao = consultasAnterioresDescricao;
	}

	public String getFinalizarDescricao() {
		return finalizarDescricao;
	}

	public void setFinalizarDescricao(String finalizarDescricao) {
		this.finalizarDescricao = finalizarDescricao;
	}

	public String getTextoAnamnese() {
		return textoAnamnese;
	}

	public void setTextoAnamnese(String textoAnamnese) {
		this.textoAnamnese = textoAnamnese;
	}

	public String getTextoEvolucao() {
		return textoEvolucao;
	}

	public void setTextoEvolucao(String textoEvolucao) {
		this.textoEvolucao = textoEvolucao;
	}

	public List<VMamProcXCid> listarCidPorProcedimentoAtendimento(String parametro) {
		List<VMamProcXCid> lista = new ArrayList<VMamProcXCid>();
		if (procedimentoAtendConsultaVO != null) {
			lista = this.ambulatorioFacade.listarCidPorProcedimentoAtendimento((String) parametro, procedimentoAtendConsultaVO.getSeq());
			if (lista != null && !lista.isEmpty()) {
				for (VMamProcXCid vMamProcXCid : lista) {
					String descMinuscula = this.ambulatorioFacade.obterDescricaoCidCapitalizada(vMamProcXCid.getCidDescEdit());
					vMamProcXCid.setCidDescEdit(descMinuscula);
					this.ambulatorioFacade.desatacharVMamProcXCid(vMamProcXCid);
				}
			}
		}
		return this.returnSGWithCount(lista,listarCidPorProcedimentoAtendimentoCount(parametro));
	}
	
	public List<AghCid> pesquisarCids(String param) {
		return aghuFacade.pesquisarCidsPorDescricaoOuId(param,
				Integer.valueOf(300));
	}

	/**
	 * Suggestion: CID
	 * 
	 * @param parametro
	 * @return
	 */
	public Long listarCidPorProcedimentoAtendimentoCount(String parametro) {
		Long count = 0L;
		if (procedimentoAtendConsultaVO != null) {
			count = this.ambulatorioFacade.listarCidPorProcedimentoAtendimentoCount((String) parametro, procedimentoAtendConsultaVO.getSeq());
		}
		return count;
	}

	/**
	 * Salva alterações realizadas em um procedimento
	 */
	public void salvarProcedimentoAtendConsulta() {
		try {
			// Trecho de código inserido para contornar o erro de chamar
			// este método duas vezes após o clique no botão salvar
			if (procedimentoAtendConsultaVO == null) {
				return;
			}
			Integer consultaNumero = this.consultaSelecionada.getNumero();
			Integer prdSeq = procedimentoAtendConsultaVO.getSeq();
			Integer cidSeq = null;
			Integer phiSeq = null;
			String cidCodigo = null;

			if (procedimentoCid != null) {
				cidSeq = procedimentoCid.getId().getCidSeq();
				cidCodigo = procedimentoCid.getCidCodigo();
				phiSeq = procedimentoCid.getId().getPhiSeq();
				this.ambulatorioFacade.desatacharVMamProcXCid(this.procedimentoCid);
			}

			// Valida quantidade e cid
			this.ambulatorioFacade.validarQuantidadeProcedimentoAtendimentoConsulta(procedimentoQuantidade);
			this.ambulatorioFacade.verificarCidProcedimentoAtendimento(consultaNumero, prdSeq, cidSeq);

			procedimentoAtendConsultaVO.setQuantidade(procedimentoQuantidade);
			procedimentoAtendConsultaVO.setCidSeq(cidSeq);
			procedimentoAtendConsultaVO.setCidCodigo(cidCodigo);
			procedimentoAtendConsultaVO.setPhiSeq(phiSeq);

			// Caso seja proc já realizado e em edição, chama os updates
			if (procedimentoAtendConsultaVO.getRealizado() && procedimentoAtendConsultaVO.getProcedimentoEmEdicao() != null && procedimentoAtendConsultaVO.getProcedimentoEmEdicao()) {

				MamProcedimentoRealizado procedimentoRealizado = this.ambulatorioFacade.alterarQuantidadeProcedimentoAtendimento(consultaNumero, prdSeq, procedimentoQuantidade, false);
				if (procedimentoAtendConsultaVO.getCidCodigo() != null) {
					this.ambulatorioFacade.alterarCidProcedimentoAtendimento(procedimentoRealizado, procedimentoAtendConsultaVO.getCidCodigo(), false);
				}
			}
			// Proc ainda não foi realizado, então é marcado como realizado
			else if (!procedimentoAtendConsultaVO.getRealizado() && procedimentoAtendConsultaVO.getProcedimentoEmEdicao() != null && procedimentoAtendConsultaVO.getProcedimentoEmEdicao()) {
				procedimentoAtendConsultaVO.setRealizado(true);
				this.ambulatorioFacade.executarOperacoesAposSelecionarProcedimento(procedimentoAtendConsultaVO, consultaNumero);
				//ambulatorioFacade.executarOperacoesAposSelecionarProcedimento(
						//procedimentoAtendConsultaVO, consultaNumero, servidorLogadoSemFimVinculo);
				
				
			}

			// this.ambulatorioFacade.flush();

			// Limpa os valores dos campos e recarrega lista de procedimentos
			iniciarProcedimentosAtendConsulta();
			apresentarMsgNegocio(Severity.INFO, "MSG_PROCEDIMENTO_REALIZADO_SALVO_COM_SUCESSO");
			atualizarProcedimentoEmEdicao(false);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(),e);
			carregarListaProcedimentosAtendConsulta(false);
			atualizarProcedimentoEmEdicao(true);
		} catch (CloneNotSupportedException e) {
			LOG.error("A classe " + MamProcedimentoRealizado.class.getName() + " não implementa a interface Cloneable.", e);
		}
	}

	/**
	 * Método disparado ao clicar em checkbox de realizado para um procedimento.
	 * 
	 * @param procedimentoAtendConsultaVO
	 */
	public void marcarProcedimentAtendRealizado(ProcedimentoAtendimentoConsultaVO procedimentoAtendConsultaVO) {
		Boolean possuiProcedimentoNenhumRealizado = false;

		try {
			possuiProcedimentoNenhumRealizado = verificarProcedimentoNenhumRealizado();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(),e);
		}

		try {
			// Se possui algum procedimento que está em edição e tentou
			// marcar/desmarcar um outro
			for (ProcedimentoAtendimentoConsultaVO procVO : listaProcedimentosAtendConsulta) {
				if (procVO.getProcedimentoEmEdicao() != null && procVO.getProcedimentoEmEdicao() && !this.procedimentoAtendConsultaVO.getNenhumProcedimentoRealizado()
						&& !possuiProcedimentoNenhumRealizado) {
					return; // Nesse caso será exibida a modal, perguntando se
					// deseja cancelar edição
				}
			}

			this.procedimentoAtendConsultaVO = procedimentoAtendConsultaVO;

			// Não é possivel realizar um procedimento caso o "nenhum" esteja
			// selecionado
			if (!this.procedimentoAtendConsultaVO.getNenhumProcedimentoRealizado() && this.procedimentoAtendConsultaVO.getRealizado() && possuiProcedimentoNenhumRealizado) {
				this.procedimentoAtendConsultaVO.setRealizado(false);
				apresentarMsgNegocio(Severity.ERROR, "MAM_01897");
				limparProcedimentoAtendConsulta();
				return;
			}

			// Não é o procedimento "nenhum" e foi marcado como realizado
			if (!this.procedimentoAtendConsultaVO.getNenhumProcedimentoRealizado() && this.procedimentoAtendConsultaVO.getRealizado()) {
				// Caso procedimento não possua cid, segue o fluxo de execução,
				// senão cai no tratamento de exceção
				this.ambulatorioFacade.verificarCidProcedimentoAtendimento(this.consultaSelecionada.getNumero(), this.procedimentoAtendConsultaVO.getSeq(),
						this.procedimentoAtendConsultaVO.getCidSeq());
				
			}
			this.ambulatorioFacade.executarOperacoesAposSelecionarProcedimento(this.procedimentoAtendConsultaVO, this.consultaSelecionada.getNumero());
			//ambulatorioFacade.executarOperacoesAposSelecionarProcedimento(
					//this.procedimentoAtendConsultaVO,
					//this.consultaSelecionada.getNumero(),
					//servidorLogadoSemFimVinculo);
			
			
			// this.ambulatorioFacade.flush();

			if (!this.procedimentoAtendConsultaVO.getNenhumProcedimentoRealizado() && this.procedimentoAtendConsultaVO.getRealizado()) {
				apresentarMsgNegocio(Severity.INFO, "MSG_PROCEDIMENTO_REALIZADO_SALVO_COM_SUCESSO");
			}

			limparProcedimentoAtendConsulta();
			// Carrega novamente a lista com o procedimento atualizado
			carregarListaProcedimentosAtendConsulta(false);
			atualizarProcedimentoEmEdicao(false);

		} catch (BaseException e) {
			if (e.getCode().toString().equals(ProcedimentoAtendimentoExceptionCode.MAM_00646.toString())) {
				carregarListaProcedimentosAtendConsulta(false);
				this.procedimentoAtendConsultaVO = null;
			} else if (e.getCode().toString().equals(ProcedimentoAtendimentoExceptionCode.MSG_INFORME_CID_PARA_O_PROCEDIMENTO.toString())) {
				// Passa para a edição do procedimento
				this.procedimentoAtendConsultaVO = procedimentoAtendConsultaVO;
				this.procedimentoAtendConsultaVO.setRealizado(false);
				editarProcedimentoAtendConsulta(this.procedimentoAtendConsultaVO);
			}
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(),e);
		}
	}

	public void carregarProcedimentoAtendConsultaEmEdicao() {
		Boolean possuiProcedimentoNenhumRealizado = false;

		try {
			possuiProcedimentoNenhumRealizado = verificarProcedimentoNenhumRealizado();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(),e);
		}

		for (ProcedimentoAtendimentoConsultaVO procVO : listaProcedimentosAtendConsulta) {

			// Se possui algum procedimento que estava em edição,
			// carrega os campos desse procedimento novamente para a edição
			if (procVO.getProcedimentoEmEdicao() != null && procVO.getProcedimentoEmEdicao() && !procVO.getNenhumProcedimentoRealizado() && !possuiProcedimentoNenhumRealizado) {

				this.procedimentoAtendConsultaVO = procVO;
				this.procedimentoDescricao = procVO.getDescricao();
				this.procedimentoQuantidade = procVO.getQuantidade();
				// Procedimento possui cid
				if (this.procedimentoAtendConsultaVO.getCidSeq() != null) {
					List<VMamProcXCid> listaCids = this.ambulatorioFacade.pesquisarCidsPorPrdSeqCidSeq(this.procedimentoAtendConsultaVO.getSeq(), this.procedimentoAtendConsultaVO.getCidSeq());
					if (!listaCids.isEmpty()) {
						this.procedimentoCid = listaCids.get(0);
						String descMinuscula = this.ambulatorioFacade.obterDescricaoCidCapitalizada(procedimentoCid.getCidDescEdit());
						this.procedimentoCid.setCidDescEdit(descMinuscula);
						this.ambulatorioFacade.desatacharVMamProcXCid(this.procedimentoCid);
					}
				}
				break;
			}
		}

		carregarListaProcedimentosAtendConsulta(false);
		atualizarProcedimentoEmEdicao(true);
	}

	/**
	 * Método disparado ao clicar em Editar para um procedimento. Carrega os
	 * campos com as informações do procedimento.
	 * 
	 * @param procedimentoAtendConsultaVO
	 */
	public void editarProcedimentoAtendConsulta(ProcedimentoAtendimentoConsultaVO procedimentoAtendConsultaVO) {
		atualizarProcedimentoEmEdicao(false);
		Boolean possuiProcedimentoNenhumRealizado = false;

		try {
			possuiProcedimentoNenhumRealizado = verificarProcedimentoNenhumRealizado();
			// Não é possivel realizar um procedimento caso o "nenhum" esteja
			// selecionado
			if (possuiProcedimentoNenhumRealizado) {
				apresentarMsgNegocio(Severity.ERROR, "MAM_01897");
				carregarListaProcedimentosAtendConsulta(false);
				return;
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(),e);
		}

		this.procedimentoAtendConsultaVO = procedimentoAtendConsultaVO;

		atualizarProcedimentoEmEdicao(true);

		// Carrega os valores nos campos para edição
		if (!possuiProcedimentoNenhumRealizado && !this.procedimentoAtendConsultaVO.getNenhumProcedimentoRealizado()) {
			this.procedimentoDescricao = procedimentoAtendConsultaVO.getDescricao();
			this.procedimentoQuantidade = procedimentoAtendConsultaVO.getQuantidade();
			this.procedimentoCid = null;

			// Procedimento possui cid
			if (this.procedimentoAtendConsultaVO.getCidSeq() != null) {
				List<VMamProcXCid> listaCids = this.ambulatorioFacade.pesquisarCidsPorPrdSeqCidSeq(this.procedimentoAtendConsultaVO.getSeq(), this.procedimentoAtendConsultaVO.getCidSeq());
				if (!listaCids.isEmpty()) {
					this.procedimentoCid = listaCids.get(0);
					String descMinuscula = this.ambulatorioFacade.obterDescricaoCidCapitalizada(procedimentoCid.getCidDescEdit());
					this.procedimentoCid.setCidDescEdit(descMinuscula);
				}
			}
		} else if (possuiProcedimentoNenhumRealizado && !this.procedimentoAtendConsultaVO.getNenhumProcedimentoRealizado()) {
			// Não permite realizar um procedimento caso o nenhum já esteja
			// selecionado
			this.procedimentoAtendConsultaVO.setRealizado(false);
		}
	}

	public void atualizarProcedimentoEmEdicao(Boolean procedimentoEmEdicao) {
		if (procedimentoEmEdicao && procedimentoAtendConsultaVO != null) {
			for (ProcedimentoAtendimentoConsultaVO procedVO : listaProcedimentosAtendConsulta) {
				// Marca o procedimento que está sendo editado
				if (procedVO.getSeq().equals(procedimentoAtendConsultaVO.getSeq()) && procedimentoEmEdicao) {
					procedVO.setProcedimentoEmEdicao(procedimentoEmEdicao);
				}
			}
		} else if (!procedimentoEmEdicao) {
			for (ProcedimentoAtendimentoConsultaVO procedVO : listaProcedimentosAtendConsulta) {
				procedVO.setProcedimentoEmEdicao(procedimentoEmEdicao);
			}
		}
	}
	
	public String redirecionarControlePaciente() throws BaseException {
		AacConsultas aacConsultasVO = this.obterConsultaSelecionadaAba();
		
			if (aacConsultasVO != null){
			Integer seqAtend = ambulatorioFacade.obterAtendimentoPorConNumero(aacConsultasVO.getNumero());
			
			this.registrosPacienteController.setCodigoPaciente(aacConsultasVO.getPaciente().getCodigo());
			this.registrosPacienteController.setAtdSeq(seqAtend);
			this.registrosPacienteController.setVoltarPara(PAGE_ATENDER_PACIENTES_AGENDADOS);
			this.registrosPacienteController.setZona(obtemZonaAtendimentoPaciente());
			this.registrosPacienteController.setUnfSeq(obtemSeqUnidadeFuncionalPaciente(aacConsultasVO));
			return PAGE_VISUALIZAR_PACIENTES_CONTROLES;		
		}
		return null;
	}
	
	public VAacSiglaUnfSalaVO obtemZonaAtendimentoPaciente() throws BaseException {
		if (this.pesquisarPacientesAgendadosController != null) {
			return this.pesquisarPacientesAgendadosController.getZona();
		}
		return null;
	}
	
	public Short obtemSeqUnidadeFuncionalPaciente(AacConsultas consulta) throws BaseException {
		if (consulta.getGradeAgendamenConsulta() != null) {
			return consulta.getGradeAgendamenConsulta().getUnidadeFuncional().getSeq();
		}
		return null;
	}
	
	

	public AacConsultas obterConsultaSelecionadaAba(){
		AacConsultas linhaSelecionada = null;

		if (selectedTab.equals(TAB_1) && consultaSelecionada != null) {
			linhaSelecionada = consultaSelecionada;
		} else if (selectedTab.equals(TAB_2) && consultaSelecionada != null) {
			linhaSelecionada = consultaSelecionada;
		} else if (selectedTab.equals(TAB_3) && consultaSelecionada != null) {
			linhaSelecionada = consultaSelecionada;
		} else if (selectedTab.equals(TAB_4) && consultaSelecionada != null) {
			linhaSelecionada = consultaSelecionada;
		} else if (selectedTab.equals(TAB_5) && consultaSelecionada != null) {
			linhaSelecionada = consultaSelecionada;
		} else if (selectedTab.equals(TAB_6) && consultaSelecionada != null) {
			linhaSelecionada = consultaSelecionada;
		} else if (selectedTab.equals(TAB_7) && consultaSelecionada != null) {
			linhaSelecionada = consultaSelecionada;
		}

		return linhaSelecionada;
	}
	

	public Boolean getDesabilitarSelecaoCid() {
		try {
			if (procedimentoAtendConsultaVO != null && procedimentoAtendConsultaVO.getCidSeq() == null) {
				this.ambulatorioFacade.verificarCidProcedimentoAtendimento(this.consultaSelecionada.getNumero(), procedimentoAtendConsultaVO.getSeq(), procedimentoAtendConsultaVO.getCidSeq());
			} else if (procedimentoAtendConsultaVO != null && procedimentoAtendConsultaVO.getCidSeq() != null) {
				// Procedimento já possui um CID, então libera seleção
				return false;
			}
		} catch (BaseException e) {
			return false;
		}
		return true;
	}

	private void limparProcedimentoAtendConsulta() {
		this.procedimentoAtendConsultaVO = null;
		this.procedimentoDescricao = null;
		this.procedimentoQuantidade = VALOR_PADRAO_QUANTIDADE_PROCEDIMENTO_ATEND;
		this.procedimentoCid = null;
	}

	public void cancelarEdicaoProcedimentoAtendConsulta() {
		limparProcedimentoAtendConsulta();
		carregarListaProcedimentosAtendConsulta(false);
		atualizarProcedimentoEmEdicao(false);
	}

	public String getItemReceitaDescricao(MamItemReceituario item) {
		StringBuffer str = new StringBuffer(32);
		if (StringUtils.isNotBlank(item.getDescricao())) {
			str.append(item.getDescricao());
		}
		if (StringUtils.isNotBlank(item.getQuantidade())) {
			str.append(" - ").append(item.getQuantidade());
		}
		if (StringUtils.isNotBlank(item.getFormaUso())) {
			str.append(". ").append(item.getFormaUso());
		}
		if (item.getIndInternoEnum() != null) {
			str.append(", Uso ").append(item.getIndInternoEnum().getDescricao());
		}
		if (item.getIndUsoContinuoBoolean()) {
			str.append(", Contínuo");
		}
		if (item.getValidadeMeses() != null) {
			str.append(". Validade: ").append(item.getValidadeMeses()).append(" mese(s)");
		}
		str.append('.');
		return str.toString();
	}

	public void atualizaValidade() {
		byte meses = (byte) 6;
		if (this.sliderAtual.equals(0)) {
			this.itemReceitaGeral.setValidadeMeses(meses);
		} else {
			this.itemReceitaEspecial.setValidadeMeses(meses);
		}
		this.confirmaValidade = false;
	}

	public void naoAtualizaValidade() {
		this.confirmaValidade = false;
	}

	public void verificaValidade() {
		this.confirmaValidade = false;
		Boolean geralUsoContinuo = itemReceitaGeral.getIndUsoContinuoBoolean();
		if (geralUsoContinuo && this.itemReceitaGeral.getValidadeMeses() == null) {
				this.confirmaValidade = true;
				this.openDialog("modalConfirmacaoValidadeWG");
			}
		sliderAtual = 0;
	}

	public void verificaValidadeEspecial() {
		this.confirmaValidade = false;
		if (itemReceitaEspecial.getIndUsoContinuoBoolean() && this.itemReceitaEspecial.getValidadeMeses() == null) {
				this.confirmaValidade = true;
			}
		sliderAtual = 1;
	}
	
	/**
	 * Método para Suggestion de Equipes
	 */	
	public List<AghEquipes> obterEquipe(Object parametro) {
		return aghuFacade.getListaEquipesAtivas((String) parametro);
	}
	
	public void pesquisar() throws ApplicationBusinessException {
		Short espSeq = null;
		if (this.especialidade != null) {
			espSeq = this.especialidade.getSeq();
		}
		listaConsultasPaciente = ambulatorioFacade.pesquisarConsultasAnterioresPacienteByEspecialidade(
				this.consultaSelecionada.getNumero(), this.consultaSelecionada.getPaciente().getCodigo(), getDtInicio(),
				dtFim, espSeq, DominioSituacaoAtendimento.PACIENTE_ATENDIDO.getCodigo());
		if (listaConsultasPaciente.size() > 0) {
			this.carregarConsultaSelecionada(listaConsultasPaciente.get(0));
		} else {
			consultasAnterioresDescricao = null;
		}
	}

	public void carregarConsultaSelecionada(AacConsultas consulta) throws ApplicationBusinessException{
		consultasAnterioresDescricao = ambulatorioFacade.obtemDescricaoConsultaAtual(consulta);
		
	}
	
	public void limpar(){
		try {
			this.dtFim = null;
			this.dtInicio = null;
			this.especialidade = null;
			this.consultasAnterioresDescricao = null;
			this.pesquisar();
			if(listaConsultasPaciente.size()>0) {
				this.carregarConsultaSelecionada(listaConsultasPaciente.get(0));
			} else {
				consultasAnterioresDescricao = null;
			}
		} catch (ApplicationBusinessException e){
			apresentarExcecaoNegocio(e);
		}
	}

	public void setListaDocumentosPaciente(List<DocumentosPacienteVO> listaDocumentosPaciente) {
		this.listaDocumentosPaciente = listaDocumentosPaciente;
	}

	public List<DocumentosPacienteVO> getListaDocumentosPaciente() {
		return listaDocumentosPaciente;
	}

	public ProcedimentoAtendimentoConsultaVO getProcedimentoAtendConsultaVO() {
		return procedimentoAtendConsultaVO;
	}

	public void setProcedimentoAtendConsultaVO(ProcedimentoAtendimentoConsultaVO procedimentoAtendConsultaVO) {
		this.procedimentoAtendConsultaVO = procedimentoAtendConsultaVO;
	}

	public AghAtendimentos getAtendimento() {
		return atendimento;
	}

	public Byte getProcedimentoQuantidade() {
		return procedimentoQuantidade;
	}

	public void setAtendimento(AghAtendimentos atendimento) {
		this.atendimento = atendimento;
	}

	public VMamProcXCid getProcedimentoCid() {
		return procedimentoCid;
	}

	public void setProcedimentoCid(VMamProcXCid procedimentoCid) {
		this.procedimentoCid = procedimentoCid;
	}

	public List<ProcedimentoAtendimentoConsultaVO> getListaProcedimentosAtendConsulta() {
		return listaProcedimentosAtendConsulta;
	}

	public void setListaProcedimentosAtendConsulta(List<ProcedimentoAtendimentoConsultaVO> listaProcedimentosAtendConsulta) {
		this.listaProcedimentosAtendConsulta = listaProcedimentosAtendConsulta;
	}

	public void setProcedimentoQuantidade(Byte procedimentoQuantidade) {
		this.procedimentoQuantidade = procedimentoQuantidade;
	}

	public DominioMotivoPendencia getMotivoPendencia() {
		return motivoPendencia;
	}

	public void setMotivoPendencia(DominioMotivoPendencia motivoPendencia) {
		this.motivoPendencia = motivoPendencia;
	}

	public Boolean getMostrarModalImpressao() {
		return mostrarModalImpressao;
	}

	public void setMostrarModalImpressao(Boolean mostrarModalImpressao) {
		this.mostrarModalImpressao = mostrarModalImpressao;
	}

	public MamItemReceituario getItemReceitaEspecial() {
		return itemReceitaEspecial;
	}

	public void setItemReceitaEspecial(MamItemReceituario itemReceitaEspecial) {
		this.itemReceitaEspecial = itemReceitaEspecial;
	}

	public List<MamItemReceituario> getItemReceitaGeralList() {
		return itemReceitaGeralList;
	}

	public void setItemReceitaGeralList(List<MamItemReceituario> itemReceitaGeralList) {
		this.itemReceitaGeralList = itemReceitaGeralList;
	}

	public MamItemReceituario getItemReceitaGeral() {
		return itemReceitaGeral;
	}

	public void setItemReceitaGeral(MamItemReceituario itemReceitaGeral) {
		this.itemReceitaGeral = itemReceitaGeral;
	}

	public List<MamItemReceituario> getItemReceitaEspecialList() {
		return itemReceitaEspecialList;
	}

	public void setItemReceitaEspecialList(List<MamItemReceituario> itemReceitaEspecialList) {
		this.itemReceitaEspecialList = itemReceitaEspecialList;
	}

	public MamReceituarios getReceitaGeral() {
		return receitaGeral;
	}

	public void setReceitaGeral(MamReceituarios receitaGeral) {
		this.receitaGeral = receitaGeral;
	}

	public MamReceituarios getReceitaEspecial() {
		return receitaEspecial;
	}

	public void setReceitaEspecial(MamReceituarios receitaEspecial) {
		this.receitaEspecial = receitaEspecial;
	}

	public Boolean getHabilitaFinalizar() {
		return habilitaFinalizar;
	}

	public void setHabilitaFinalizar(Boolean habilitaFinalizar) {
		this.habilitaFinalizar = habilitaFinalizar;
	}

	public Integer getViasGeral() {
		return viasGeral;
	}

	public void setViasGeral(Integer viasGeral) {
		this.viasGeral = viasGeral;
	}

	public Integer getViasEspecial() {
		return viasEspecial;
	}

	public void setViasEspecial(Integer viasEspecial) {
		this.viasEspecial = viasEspecial;
	}

	public void setReadonlyAnamnese(Boolean readonlyAnamnese) {
		this.readonlyAnamnese = readonlyAnamnese;
	}

	public Boolean getReadonlyAnamnese() {
		return readonlyAnamnese;
	}

	public void setReadonlyEvolucao(Boolean readonlyEvolucao) {
		this.readonlyEvolucao = readonlyEvolucao;
	}

	public Boolean getReadonlyEvolucao() {
		return readonlyEvolucao;
	}

	public Boolean getVoltarListaPacientes() {
		return voltarListaPacientes;
	}

	public void setVoltarListaPacientes(Boolean voltarListaPacientes) {
		this.voltarListaPacientes = voltarListaPacientes;
	}

	public VAfaDescrMdto getDescricaoMedicamento() {
		return descricaoMedicamento;
	}

	public void setDescricaoMedicamento(VAfaDescrMdto descricaoMedicamento) {
		this.descricaoMedicamento = descricaoMedicamento;
	}

	public Integer getSizeItensGeral() {
		return itemReceitaGeralList.size();
	}

	public Integer getSizeItensEspecial() {
		return itemReceitaEspecialList.size();
	}

	public String getTipoCorrente() {
		return tipoCorrente;
	}

	public void setTipoCorrente(String tipoCorrente) {
		this.tipoCorrente = tipoCorrente;
	}

	public String getProcedimentoDescricao() {
		return procedimentoDescricao;
	}

	public void setProcedimentoDescricao(String procedimentoDescricao) {
		this.procedimentoDescricao = procedimentoDescricao;
	}

	public Boolean getModoInsercaoItemAnamnese() {
		return modoInsercaoItemAnamnese;
	}

	public void setModoInsercaoItemAnamnese(Boolean modoInsercaoItemAnamnese) {
		this.modoInsercaoItemAnamnese = modoInsercaoItemAnamnese;
	}

	public Boolean getModoInsercaoItemEvolucao() {
		return modoInsercaoItemEvolucao;
	}

	public void setModoInsercaoItemEvolucao(Boolean modoInsercaoItemEvolucao) {
		this.modoInsercaoItemEvolucao = modoInsercaoItemEvolucao;
	}

	public MamNotaAdicionalAnamneses getNotaAdicionalAnamneses() {
		return notaAdicionalAnamneses;
	}

	public void setNotaAdicionalAnamneses(MamNotaAdicionalAnamneses notaAdicionalAnamneses) {
		this.notaAdicionalAnamneses = notaAdicionalAnamneses;
	}

	public MamNotaAdicionalEvolucoes getNotaAdicionalEvolucoes() {
		return notaAdicionalEvolucoes;
	}

	public void setNotaAdicionalEvolucoes(MamNotaAdicionalEvolucoes notaAdicionalEvolucoes) {
		this.notaAdicionalEvolucoes = notaAdicionalEvolucoes;
	}

	public String getDescrNotaAdicionalAnamnese() {
		return descrNotaAdicionalAnamnese;
	}

	public void setDescrNotaAdicionalAnamnese(String descrNotaAdicionalAnamnese) {
		this.descrNotaAdicionalAnamnese = descrNotaAdicionalAnamnese;
	}

	public String getDescrNotaAdicionalEvolucao() {
		return descrNotaAdicionalEvolucao;
	}

	public void setDescrNotaAdicionalEvolucao(String descrNotaAdicionalEvolucao) {
		this.descrNotaAdicionalEvolucao = descrNotaAdicionalEvolucao;
	}

	public String getDescricaoReceitaGeral() {
		return descricaoReceitaGeral;
	}

	public void setDescricaoReceitaGeral(String descricaoReceitaGeral) {
		this.descricaoReceitaGeral = descricaoReceitaGeral;
	}

	public String getDescricaoReceitaEspecial() {
		return descricaoReceitaEspecial;
	}

	public void setDescricaoReceitaEspecial(String descricaoReceitaEspecial) {
		this.descricaoReceitaEspecial = descricaoReceitaEspecial;
	}

	public String getValidadeUso() {
		return validadeUso;
	}

	public void setValidadeUso(String validadeUso) {
		this.validadeUso = validadeUso;
	}

	public Boolean getHabilitaAnamnese() {
		return habilitaAnamnese;
	}

	public void setHabilitaAnamnese(Boolean habilitaAnamnese) {
		this.habilitaAnamnese = habilitaAnamnese;
	}

	public Boolean getHabilitaEvolucao() {
		return habilitaEvolucao;
	}

	public void setHabilitaEvolucao(Boolean habilitaEvolucao) {
		this.habilitaEvolucao = habilitaEvolucao;
	}

	public Boolean getHabilitaProcedimento() {
		return habilitaProcedimento;
	}

	public void setHabilitaProcedimento(Boolean habilitaProcedimento) {
		this.habilitaProcedimento = habilitaProcedimento;
	}

	public Boolean getHabilitaReceita() {
		return habilitaReceita;
	}

	public void setHabilitaReceita(Boolean habilitaReceita) {
		this.habilitaReceita = habilitaReceita;
	}

	public Boolean getGravaSolicitacaoExame() {
		return gravaSolicitacaoExame;
	}

	public void setGravaSolicitacaoExame(Boolean gravaSolicitacaoExame) {
		this.gravaSolicitacaoExame = gravaSolicitacaoExame;
	}

	public Boolean getGravaAnamnese() {
		return gravaAnamnese;
	}

	public void setGravaAnamnese(Boolean gravaAnamnese) {
		this.gravaAnamnese = gravaAnamnese;
	}

	public Boolean getGravaEvolucao() {
		return gravaEvolucao;
	}

	public void setGravaEvolucao(Boolean gravaEvolucao) {
		this.gravaEvolucao = gravaEvolucao;
	}

	public Boolean getGravaProcedimento() {
		return gravaProcedimento;
	}

	public void setGravaProcedimento(Boolean gravaProcedimento) {
		this.gravaProcedimento = gravaProcedimento;
	}

	public Boolean getGravaReceita() {
		return gravaReceita;
	}

	public void setGravaReceita(Boolean gravaReceita) {
		this.gravaReceita = gravaReceita;
	}

	public Boolean getReadonlyReceitaGeral() {
		return readonlyReceitaGeral;
	}

	public void setReadonlyReceitaGeral(Boolean readonlyReceitaGeral) {
		this.readonlyReceitaGeral = readonlyReceitaGeral;
	}

	public Boolean getReadonlyReceitaEspecial() {
		return readonlyReceitaEspecial;
	}

	public void setReadonlyReceitaEspecial(Boolean readonlyReceitaEspecial) {
		this.readonlyReceitaEspecial = readonlyReceitaEspecial;
	}

	public void setConfirmaValidade(boolean confirmaValidade) {
		this.confirmaValidade = confirmaValidade;
	}

	public boolean isConfirmaValidade() {
		return confirmaValidade;
	}

	public void setSliderAtual(Integer sliderAtual) {
		this.sliderAtual = sliderAtual;
	}

	public Integer getSliderAtual() {
		return sliderAtual;
	}

	// gera pendências de certificação digital
	public void geraPendenciasCertificacaoDigital() throws BaseException, ApplicationBusinessException, IOException, SystemException, JRException {

		// inativar docs de certificação digital do atendimento anterior(se
		// possuir)
		// certificacaoDigitalFacade.inativarDocumentosAtivosPorAtendimento(atendimento.getSeq());

		// this.obterListaDocumentosPacienteParaCertificacao();

		for (DocumentosPacienteVO documento : listaDocumentosPaciente) {

			if (documento.getAnamnese() != null) {
				MamAnamneses anamnese = documento.getAnamnese();
				this.colecao = this.ambulatorioFacade.retornarRelatorioAnamneseEvolucao(this.consultaSelecionada.getNumero(), DominioAnamneseEvolucao.A);

				if (anamnese.getServidor().getId().equals(servidorLogadoSemFimVinculo.getId())) {
					this.gerarDocumento(DominioTipoDocumento.ANA);
				} else {
					this.gerarDocumento(DominioTipoDocumento.NAN);
				}
			}

			if (documento.getEvolucao() != null) {
				MamEvolucoes evolucao = documento.getEvolucao();
				this.colecao = this.ambulatorioFacade.retornarRelatorioAnamneseEvolucao(this.consultaSelecionada.getNumero(), DominioAnamneseEvolucao.E);

				if (evolucao.getServidor().getId().equals(servidorLogadoSemFimVinculo.getId())) {
					this.gerarDocumento(DominioTipoDocumento.EV);
				} else {
					this.gerarDocumento(DominioTipoDocumento.NEV);
				}
			}

			/*
			 * TODO PENDÊNCIA ARQUITETURA - Passar por parâmetro para SolicitacaoExameController
			 */
			if (documento.getSolicitacaoExame() != null) {
				this.gerarPendenciaSolicitacaoExames(documento.getSolicitacaoExame().getSeq());
			}
			/*
			 * FIM DA PENDÊNCIA
			 */
		}
	}

	public String redirecionarAgrupamentoPacientes() {
		return PAGE_AGRUPAMENTO_FAMILIAR;
	}
	/**
	 * TODO PENDÊNCIA ARQUITETURA - Passar por parâmetro para SolicitacaoExameController
	 * 
	 * @param soeSeq
	 * @throws ApplicationBusinessException 
	 */
	 public void gerarPendenciaSolicitacaoExames(Integer soeSeq) throws ApplicationBusinessException {

		 AelSolicitacaoExames aelSolicitacaoExames = exameFacade.obterAelSolicitacaoExamesPeloId(soeSeq);
		 
		 if (aelSolicitacaoExames != null) {
			 SolicitacaoExameVO solicitacaoExame = solicitacaoExameFacade.buscaSolicitacaoExameVO(aelSolicitacaoExames.getAtendimento().getSeq(), null);
			 solicitacaoExame.setAtendimento(aelSolicitacaoExames.getAtendimento());
			 
			 solicitacaoExameController.setSolicitacaoExame(solicitacaoExame);
		 }
		 
		 solicitacaoExameController.gerarSolicitacaoExamePendenciaAssinaturaDigital(soeSeq);
	 }

	@Override
	protected BaseEntity getEntidadePai() {
		return atendimento;
	}

	public void obterListaDocumentosPacienteParaCertificacao() throws ApplicationBusinessException {
		listaDocumentosPaciente = this.ambulatorioFacade.obterListaDocumentosPacienteParaCertificacao(this.consultaSelecionada.getNumero());
	}
	
	public String efetuarPrescricaoAmbulatorial(){
		Long unfPmeInf =  aghuFacade.pesquisarUnidadesFuncionaisPorCodigoDescricaoECaracteristicasCount(
					atendimento.getUnidadeFuncional().getSeq(),	null,Boolean.TRUE,	Boolean.FALSE, Boolean.TRUE,
					ConstanteAghCaractUnidFuncionais.PME_INFORMATIZADA);
		if(unfPmeInf > 0){
				return PAGE_PRESCRICAOMEDICA_VERIFICA_PRESCRICAO_MEDICA;
		}else{
			apresentarMsgNegocio(
					Severity.ERROR,	"UNIDADE_FUNCIONAL_NAO_POSSUI_CARACTERISTICA",
					ConstanteAghCaractUnidFuncionais.PME_INFORMATIZADA.getDescricao());
		}
		return null;
	}
	
	public String redirecionaReceitas(){
		return PAGE_PACIENTES_AGENDADOS_RECEITAS;
	}

	public MpmPrescricaoMedica getPrescricaoMedica() {
		return prescricaoMedica;
	}
	
	public void setPrescricaoMedica(MpmPrescricaoMedica prescricaoMedica) {
		this.prescricaoMedica = prescricaoMedica;
	}

	public Boolean getPrescricaoAmbulatorialAtiva() {
		return prescricaoAmbulatorialAtiva;
	}

	public void setPrescricaoAmbulatorialAtiva(Boolean prescricaoAmbulatorialAtiva) {
		this.prescricaoAmbulatorialAtiva = prescricaoAmbulatorialAtiva;
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

	public void setDtInicio(Date dtInicio) {
		this.dtInicio = dtInicio;
	}

	public Date getDtInicio() {
		return dtInicio;
	}
	
	public Date getDtFim() {
		return dtFim;
	}

	public void setDtFim(Date dtFim) {
		this.dtFim = dtFim;
	}

	public AghEspecialidades getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(AghEspecialidades especialidade) {
		this.especialidade = especialidade;
	}

	public List<AacConsultas> getListaConsultasPaciente() {
		return listaConsultasPaciente;
	}

	public void setListaConsultasPaciente(List<AacConsultas> listaConsultasPaciente) {
		this.listaConsultasPaciente = listaConsultasPaciente;
	}

	public AghEquipes getEquipe() {
		return equipe;
	}

	public void setEquipe(AghEquipes equipe) {
		this.equipe = equipe;
	}
	
	public RapServidores getServidorLogadoSemFimVinculo() {
		return servidorLogadoSemFimVinculo;
	}

	public void setServidorLogadoSemFimVinculo(
			RapServidores servidorLogadoSemFimVinculo) {
		this.servidorLogadoSemFimVinculo = servidorLogadoSemFimVinculo;
	}

	public String getTextoConsultaAtual() {
		return textoConsultaAtual;
	}

	public void setTextoConsultaAtual(String textoConsultaAtual) {
		this.textoConsultaAtual = textoConsultaAtual;
	}

	public Long getEvoSeq() {
		return evoSeq;
}

	public void setEvoSeq(Long evoSeq) {
		this.evoSeq = evoSeq;
	}
	
	public MamTipoAtestado getTipoAtestado() {
		return tipoAtestado;
	}

	public void setTipoAtestado(MamTipoAtestado tipoAtestado) {
		this.tipoAtestado = tipoAtestado;
	}

	public MamAtestados getAtestadoAmbulatorio() {
		return atestadoAmbulatorio;
	}

	public void setAtestadoAmbulatorio(MamAtestados atestadoAmbulatorio) {
		this.atestadoAmbulatorio = atestadoAmbulatorio;
	}

	public ProcedimentoAtendimentoConsultaVO getProcedimentoAtendConsultaAtestadoVO() {
		return procedimentoAtendConsultaAtestadoVO;
	}

	public void setProcedimentoAtendConsultaAtestadoVO(ProcedimentoAtendimentoConsultaVO procedimentoAtendConsultaAtestadoVO) {
		this.procedimentoAtendConsultaAtestadoVO = procedimentoAtendConsultaAtestadoVO;
	}

	public int getCalcularDiasEntreDatas() {
		return calcularDiasEntreDatas;
	}

	public void setCalcularDiasEntreDatas(int calcularDiasEntreDatas) {
		this.calcularDiasEntreDatas = calcularDiasEntreDatas;
	}

	public boolean isOpcaoSelecionadaAtestado() {
		return opcaoSelecionadaAtestado;
	}

	public void setOpcaoSelecionadaAtestado(boolean opcaoSelecionadaAtestado) {
		this.opcaoSelecionadaAtestado = opcaoSelecionadaAtestado;
	}

	public boolean isOpcaoSelecionadaTipoAtestado() {
		return opcaoSelecionadaTipoAtestado;
	}

	public void setOpcaoSelecionadaTipoAtestado(boolean opcaoSelecionadaTipoAtestado) {
		this.opcaoSelecionadaTipoAtestado = opcaoSelecionadaTipoAtestado;
	}

	public List<MamAtestados> getConsultaAtestado() {
		return consultaAtestado;
	}

	public void setConsultaAtestado(List<MamAtestados> consultaAtestado) {
		this.consultaAtestado = consultaAtestado;
	}

	public List<MamTipoAtestado> getLista() {
		return lista;
	}

	public void setLista(List<MamTipoAtestado> lista) {
		this.lista = lista;
	}

	public MamTipoAtestado getOpcaoTipoAtestado() {
		return opcaoTipoAtestado;
	}

	public void setOpcaoTipoAtestado(MamTipoAtestado opcaoTipoAtestado) {
		this.opcaoTipoAtestado = opcaoTipoAtestado;
	}

	public AghCid getProcedimentoCidAtestado() {
		return procedimentoCidAtestado;
	}

	public void setProcedimentoCidAtestado(AghCid procedimentoCidAtestado) {
		this.procedimentoCidAtestado = procedimentoCidAtestado;
	}
	
	public Boolean getHabilitaAtestado() {
		return habilitaAtestado;
	}

	public void setHabilitaAtestado(Boolean habilitaAtestado) {
		this.habilitaAtestado = habilitaAtestado;
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

	public AinSolicitacoesInternacao getPrimeiraSolicitacaoInternacao() {
		return primeiraSolicitacaoInternacao;
	}

	public void setPrimeiraSolicitacaoInternacao(
			AinSolicitacoesInternacao primeiraSolicitacaoInternacao) {
		this.primeiraSolicitacaoInternacao = primeiraSolicitacaoInternacao;
	}
}
