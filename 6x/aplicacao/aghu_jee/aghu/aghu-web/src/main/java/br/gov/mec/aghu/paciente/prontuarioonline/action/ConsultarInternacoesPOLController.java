package br.gov.mec.aghu.paciente.prontuarioonline.action;

import static br.gov.mec.aghu.model.AipPacientes.VALOR_MAXIMO_PRONTUARIO;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.weld.context.ConversationContext;
import org.jboss.weld.context.http.Http;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.action.RelatorioAnaEvoInternacaoController;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.certificacaodigital.action.VisualizarDocumentoController;
import br.gov.mec.aghu.certificacaodigital.business.ICertificacaoDigitalFacade;
import br.gov.mec.aghu.checagemeletronica.business.IChecagemEletronicaFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.SecurityController;
import br.gov.mec.aghu.core.business.SelectionQualifier;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.commons.WebUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.PdfUtil;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoPim2;
import br.gov.mec.aghu.dominio.DominioSumarioExame;
import br.gov.mec.aghu.dominio.DominioTipoDocumento;
import br.gov.mec.aghu.emergencia.business.IEmergenciaFacade;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.vo.RelatorioExamesPacienteVO;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.model.AghAtendimentoPacientes;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghVersaoDocumento;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.EceOrdemDeAdministracao;
import br.gov.mec.aghu.model.EceOrdemDeAdministracaoHist;
import br.gov.mec.aghu.model.MamPcIntParada;
import br.gov.mec.aghu.model.McoLogImpressoes;
import br.gov.mec.aghu.model.McoRecemNascidos;
import br.gov.mec.aghu.model.MpmAltaSumarioId;
import br.gov.mec.aghu.model.MpmAnamneses;
import br.gov.mec.aghu.model.MpmFichaApache;
import br.gov.mec.aghu.model.MpmPim2;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.prontuarioonline.business.IProntuarioOnlineFacade;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.InternacaoVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.NodoPOLVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.OrdemDeAdministracaoVO;
import br.gov.mec.aghu.prescricaoenfermagem.util.RelatorioPrescricaoEnfermagemCallBack;
import br.gov.mec.aghu.prescricaoenfermagem.vo.RelSumarioPrescricaoEnfermagemVO;
import br.gov.mec.aghu.prescricaoenfermagem.vo.SumarioPrescricaoEnfermagemVO;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.util.RelatorioPrescricaoMedicaCallBack;
import br.gov.mec.aghu.prescricaomedica.vo.AltaSumarioVO;
import br.gov.mec.aghu.prescricaomedica.vo.RelSumarioPrescricaoVO;
import br.gov.mec.aghu.prescricaomedica.vo.SumarioPrescricaoMedicaVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;

import com.itextpdf.text.DocumentException;

@SuppressWarnings({"PMD.AghuTooManyMethods", "PMD.ExcessiveClassLength"})
public class ConsultarInternacoesPOLController extends ActionController {

	private static final String AMBULATORIO_RELATORIO_AMAMNESE_EVOLUCAO_INTERNACAO = "ambulatorio-relatorioAmamneseEvolucaoInternacao";
	private static final String CONSULTA_DETALHE_INTERNACAO = "consultaDetalheInternacao";
	private static final String CONSULTAR_INTERNACOES = "consultarInternacoes";
	private static final String CIRURGIAS_INTERNACAO_POL = "cirurgiasInternacaoListPOL";
	private static final String POL_DETALHE_INTERNACAO = "pol-detalheInternacao";
	private static final String POL_INTERNACAO = "pol-internacao";
	private static final String RELATORIO_ADMISSAO_OBSTETRICA = "relatorioAdmissaoObstetricaPdf";
	private static final String RELATORIO_ATENDIMENTO_RECEM_NASCIDO = "relatorioAtendimentoRecemNascidoPdf";
	private static final String RELATORIO_EXAME_FISICO_RN = "relatorioExameFisicoRNPdf";
	private static final String RELATORIO_PARADA_INTERNACAO = "relatorioParadaInternacaoPdf";
	private static final String RELATORIO_PRESCRICAO_MEDICA = "relatorioPrescricaoMedica";
	private static final String RELATORIO_PRESCRICAO_ENFERMAGEM = "relatorioPrescricaoEnfermagem";
	private static final String RELATORIO_SUMARIO_ASSISTENCIA_PARTO = "relatorioSumarioAssistenciaPartoPdf";
	private static final String VISUALIZAR_DOCUMENTO_ASSINADO_POL = "certificacaodigital-visualizarDocumentoAssinadoPOL";

	protected enum EnumTargetConsultaInternacoesPOL {
		RELATORIO_EVOLUCAO_PERIODO, 
		RELATORIO_ANAMNESES;
	}

	@Inject @Http 
	private ConversationContext conversationContext;

	private static final Log LOG = LogFactory.getLog(ConsultarInternacoesPOLController.class);
	
	private static final long serialVersionUID = -8262385291478332326L;


	@EJB
	private IProntuarioOnlineFacade prontuarioOnlineFacade;
	
	@EJB
	private ICertificacaoDigitalFacade certificacaoDigitalFacade;
	
	@EJB
	private IChecagemEletronicaFacade checagemEletronicaFacade;

	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private IPacienteFacade pacienteFacade;

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@EJB
	private IInternacaoFacade internacaoFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IEmergenciaFacade emergenciaFacade;

	@Inject
	private RelatorioPrescricaoMedicaCallBack relatorioPrescricaoMedicaCallBack;

	@Inject
	private RelatorioPrescricaoEnfermagemCallBack relatorioPrescricaoEnfermagemCallBack;
	
	@Inject
	private RelatorioAnaEvoInternacaoController relatorioAnaEvoInternacaoController;

	@Inject
	private RelatorioExameFisicoRNController relatorioExameFisicoRNController;
	
	@Inject
	private RelatorioAtendimentoRNController relatorioAtendimentoRNController;

	@Inject
	private RelatorioSumarioAssistenciaPartoController relatorioSumarioAssistenciaPartoController;

	@Inject
	private RelatorioAdmissaoObstetricaController relatorioAdmissaoObstetricaController;
	
	@Inject
	private RelatorioOrdemAdministracaoController relatorioOrdemAdministracaoController;
	
	@Inject
	private RelatorioRegistrosControlesPacienteController relatorioRegistrosControlesPacienteController;
	
	@Inject
	private EmitirRelatorioExamesPacienteController emitirRelatorioExamesPacienteController;	
	
	@Inject
	private ConsultaDetalheConsultoriaController consultaDetalheConsultoriaController;

	@Inject
	private ConsultaDetalheInternacaoController consultaDetalheInternacaoController;

	@Inject
	private VisualizarDocumentoController visualizarDocumentoController;
	
	@Inject
	private RelatorioPIM2Controller relatorioPIM2Controller;

	@Inject
	private VisualizacaoFichaApacheController visualizacaoFichaApacheController;
	
	@Inject
	private SistemaImpressao sistemaImpressao;
	
	@Inject
	private RelatorioParadaInternacaoController relatorioParadaInternacaoController;
	
	@Inject @SelectionQualifier @RequestScoped
	private NodoPOLVO itemPOL;	
	
	@Inject
	private SecurityController securityController;

	private AghAtendimentoPacientes atendimentoPaciente;

	private AghAtendimentos atendimento;
	
	private Date dthrInicio;
	
	private Date dthrFim;

	private String tipoRelatorio;

	private String origem;

	private Integer numeroProntuario;

	private boolean pol = false;
	
	private Integer detalheSeq;

	private String detalheTipo;
	
	private List<InternacaoVO> internacaoVOList;
	

	private Boolean moduloPrescricaoEnfermagemAtivo = Boolean.FALSE;

	private List<SumarioPrescricaoMedicaVO> listQuinzenaPrescricaoMedica;
	private List<SumarioPrescricaoMedicaVO> listQuinzenaPrescricaoMedicaSelecionados;

	private List<SumarioPrescricaoEnfermagemVO> listQuinzenaPrescricaoEnfermagem;
	private List<SumarioPrescricaoEnfermagemVO> listQuinzenaPrescricaoEnfermagemSelecionados;
	
	private Integer idxSelecionado;
	private Boolean permiteImpressao = Boolean.FALSE;

	private List<AltaSumarioVO> listaAltaSumario;
	private AltaSumarioVO transferenciaSumarioSelecionada;

	private InternacaoVO internacao;
	private String modoVoltar;

	private final Integer RELATORIO_SUMARIO_PRESCRICAO_MEDICA = 1;
	private final Integer RELATORIO_SUMARIO_PRESCRICAO_ENFERMAGEM = 2;
	private Integer relatorioSumarioSelecionado;

	private Boolean acessoAdminPOL;
	
	private Boolean acessoSumarioExamesInternacaoPOL;
	
	private Boolean permiteImprimirSumarioParada;
	
	private Boolean permiteImprimirSumarioPrescricaoEnfermagemInternacaoPol;
	
	private Boolean permiteImprimirSumarioPrescricaoMedicaInternacaoPol;
		
	private AghVersaoDocumento aghVersaoDocumento;
	
	private boolean desabilitarBotaoVisualizarImpressao = true;

	private List<McoRecemNascidos> recemNasc;
	private McoRecemNascidos selecionadoRecemNasc;
	
	private List<MamPcIntParada> paradasInternacoes;
	
	private Byte rnaSeq;
	
	private Boolean exibirModalGestacao = Boolean.FALSE;	
	
	private Integer atdSeq;
	private Integer seqAtendimento;
	private Long seqAnamnese;
	
	
	// 15836
	private Integer serMatricula;
	private Short serVinCodigo;
	private Short atendimentoGsoSeqp;
	private Date dthrMovimento;
	private Short gsoSeqp;
	
	//5945
	private StringBuilder nomePacSelecionado = new StringBuilder();
	private Boolean exibirModalExames = Boolean.FALSE;
	private String mensagemModalExames = "";
	
	//15839
	private List<McoRecemNascidos> listaSeqp;
	private McoRecemNascidos selecionadoExameFisicoRecemNascido;
	private String indImpPrevia = "D";
	
	//15823
	private OrdemDeAdministracaoVO ordemDeAdminVO;
	private List<OrdemDeAdministracaoVO> ordemDeAdminVOList;
	private List<OrdemDeAdministracaoVO> filtredOrdemDeAdminVOList;
		
	//21796
	private final Integer QTDE_MAX_SELECAO_SUMARIOS = 5;
	private Boolean qtdeMaxSumariosSelecionados = false;
	
	//3543
	private Long itemSelecionado;
	
	private Boolean btnSumarioTranfDetalheInternacao;
	
	private Date dataInicial;
	
	private Date dataFinal;
	
	private String voltarPara;
	private String urlBaseAdministracao;
	
	private String urlRelatorioOrdemAdministracao;
	
	private boolean voltarParaPolInternacoes = Boolean.TRUE;
	
	private BigDecimal enfermagemAtivo;

	private Integer dia;
	private Integer mes;
	private Integer ano;
	
	@PostConstruct
	public void inicializar() {
		try {
			this.begin(conversation, true);
			conversationContext.setConcurrentAccessTimeout(900000000000l);
			acessoAdminPOL = securityController.usuarioTemPermissao("acessoAdminPOL", "acessar");
			acessoSumarioExamesInternacaoPOL = securityController .usuarioTemPermissao("acessoSumarioExamesInternacaoPOL", "acessar");
			permiteImprimirSumarioParada = securityController.usuarioTemPermissao("permiteImprimirSumarioParada", "imprimir");
			permiteImprimirSumarioPrescricaoEnfermagemInternacaoPol = securityController.usuarioTemPermissao("permiteImprimirSumarioPrescricaoEnfermagemInternacaoPol", "imprimir");
			permiteImprimirSumarioPrescricaoMedicaInternacaoPol = securityController.usuarioTemPermissao("permiteImprimirSumarioPrescricaoMedicaInternacaoPol", "imprimir");
			enfermagemAtivo = parametroFacade.buscarValorNumerico(AghuParametrosEnum.P_AGHU_ENFERMAGEM_ATIVO);
			urlBaseAdministracao = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_ENDERECO_WEB_CHECAGEM_AGHU);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public enum EnumTargetMsgNomePac {
		MSG_MODAL_EXAME_PACIENTE;
	}
		
	// se usuario tiver a permissao acessoAdminPOL, btn desabilitado
	public Boolean getDisabledBtnSeAcessoAdminPOL() {
		return getUsuarioAdministrativo();
	}

	public Boolean getUsuarioAdministrativo(){
		return acessoAdminPOL;
	}

	public void inicio() {
		// Se estiver voltando de uma ação acionada a partir da tela, 
		// não deve reconstruiu a tela, tornando a exibição da mesma mais rápido.
		if(internacao != null){
			return;
		}
		
		limpar();
		this.setPol(true);
		preencherParametroEnfermagemAtivo();
		if (itemPOL!=null){
			this.setNumeroProntuario(itemPOL.getProntuario());
			if (itemPOL.getParametros().containsKey("seq") && itemPOL.getParametros().containsKey("tipo")){
				try {
					internacao = prontuarioOnlineFacade.obterInternacao((Integer)itemPOL.getParametros().get("seq"), (String)itemPOL.getParametros().get("tipo"));
				} catch (ApplicationBusinessException e) {
					internacao = null;
				}
			}
		}	

		pesquisar();

		limpaTransferenciaSelecionada();
		//Date today = DateUtil.truncaData(new Date());
		//this.setDataFinal(today);
		//this.setDataInicial(DateUtil.adicionaDias(today, -14));
		setIdxSelecionado(0);
		
	}
	
	public void detalharInternacao() throws ApplicationBusinessException{	
		
		if (itemPOL.getProntuario() != null) {
			this.setNumeroProntuario(itemPOL.getProntuario());
		}
		
		this.obterInternacaoVOVerificarAcaoPermitida();
		this.preencherParametroEnfermagemAtivo();
		this.setPol(true);
		this.montaQuinzenaPrescricaoEnfermagem();
		this.montaQuinzenaPrescricaoMedicaDetalhe();
	}
	
	
	public String abreDetalheInternacao(){
		consultaDetalheInternacaoController.setSeq(detalheSeq);
		consultaDetalheInternacaoController.setTipo(detalheTipo);
		consultaDetalheInternacaoController.setFromConsultarInternacoes(true);
		return POL_DETALHE_INTERNACAO;
	}
	
	public String abrirCirurgiasInternacao(){
		return CIRURGIAS_INTERNACAO_POL;
	}

	public void preencherParametroEnfermagemAtivo() {
		if (enfermagemAtivo.equals(BigDecimal.ONE)){
			setModuloPrescricaoEnfermagemAtivo(Boolean.TRUE);
		}
	}

	public void limpaTransferenciaSelecionada() {
		transferenciaSumarioSelecionada = new AltaSumarioVO();
		transferenciaSumarioSelecionada.setId(new MpmAltaSumarioId());
	}

	private void limpar() {
		setTipoRelatorio(null);
		setOrigem(null);
		//setSeqInternacao(null);
		//setInternacao(null);
		this.ordemDeAdminVO = null;
		this.ordemDeAdminVOList = null;
	}


	public void pesquisar() {
		try {
			internacaoVOList = this.prontuarioOnlineFacade.pesquisaInternacoes(this.numeroProntuario);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}		

	/*
	 * #4336 - Imprimir sumário da prescrição médica Modulo: Montagem da Lista
	 * Quinzenal de Seleção para relatório Autor: Cristiano de Quadros da Silva
	 * Data: 07/12/2010
	 */
	public void montaQuinzenaPrescricaoMedica() throws ApplicationBusinessException {
		setModoVoltar("");
		listQuinzenaPrescricaoMedica = prontuarioOnlineFacade
				.montaQuinzenaPrescricaoMedica(internacao, pacienteFacade
						.pesquisarPacientePorProntuario(this.numeroProntuario)
						.getCodigo());
	}
	
	public void montaQuinzenaPrescricaoMedicaDetalhe() throws ApplicationBusinessException {
		montaQuinzenaPrescricaoMedica();
		setModoVoltar("D");
	}

	/**
	 * Método que carrega a lista quinzenal de seleção para o relatório de prescrição enfermagem
	 */
	public void montaQuinzenaPrescricaoEnfermagem() throws ApplicationBusinessException {
		setDesabilitarBotaoVisualizarImpressao(Boolean.TRUE);
		listQuinzenaPrescricaoEnfermagem = prontuarioOnlineFacade
				.montaQuinzenaPrescricaoEnfermagem(internacao, pacienteFacade
						.pesquisarPacientePorProntuario(this.numeroProntuario)
						.getCodigo());
	}

	/*
	 * #16679 - Visualizar Listas de Sumários de Transferência Modulo: Montagem
	 * da Lista de Sumários de Transferência Autor: Angela Gallassini Picinin
	 * Data: 07/05/2012
	 */

	public void carregarListaSumarioVO() {
		setBtnSumarioTranfDetalheInternacao(Boolean.FALSE);
		try {
			listaAltaSumario = prontuarioOnlineFacade.carregarListaSumarioVO(getInternacao().getAtdSeq());
			transferenciaSumarioSelecionada = null;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}

	}
	
	public void carregarListaSumarioVODetalheInternacao() {
		carregarListaSumarioVO();
		setBtnSumarioTranfDetalheInternacao(Boolean.TRUE);
	}

	/*
	 * #4336 - Imprimir sumário da prescrição médica Modulo: geração do
	 * relatório Autor: Cristiano de Quadros da Silva Data: 14/12/2010
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public String montaRelatorioPrescricaoMedica() throws BaseException,
			JRException, SystemException, IOException, DocumentException {
				
		relatorioSumarioSelecionado = RELATORIO_SUMARIO_PRESCRICAO_MEDICA;

		listQuinzenaPrescricaoMedicaSelecionados = new ArrayList<SumarioPrescricaoMedicaVO>();

		setIdxSelecionado(0);

		String nomeEquipe = null;
		Date dataInternacao = null;

		Integer hodSeq = null;
		Integer atuSeq = null;
		Integer intSeq = null;

		String prontuarioMae = null;

		if (!listQuinzenaPrescricaoMedica.isEmpty()) {
			this.atendimentoPaciente = this.aghuFacade
					.obterAtendimentoPaciente(
							listQuinzenaPrescricaoMedica.get(0).getAtdSeq(),
							listQuinzenaPrescricaoMedica.get(0).getAtdPac());
			this.atendimento = aghuFacade
					.obterAghAtendimentoPorChavePrimaria(this.atendimentoPaciente.getId()
							.getAtdSeq());
			if (atendimento.getServidor() != null) {
				nomeEquipe = prescricaoMedicaFacade.obtemNomeServidorEditado(
						atendimento.getServidor().getId().getVinCodigo(),
						atendimento.getServidor().getId().getMatricula());
			}

			if (atendimento.getHospitalDia() != null) {
				hodSeq = atendimento.getHospitalDia().getSeq();
			}
			if (atendimento.getInternacao() != null) {
				intSeq = atendimento.getInternacao().getSeq();
			}
			if (atendimento.getAtendimentoUrgencia() != null) {
				atuSeq = atendimento.getAtendimentoUrgencia().getSeq();
			}

			dataInternacao = prescricaoMedicaFacade.obterDataInternacao(intSeq, atuSeq, hodSeq);

			// paciente recém nacido requer prontuário da mãe no relatório
			if (this.atendimento.getPaciente().getProntuario() != null && atendimento.getPaciente().getProntuario() > VALOR_MAXIMO_PRONTUARIO) {

				if (this.atendimento.getPaciente().getMaePaciente() != null) {
					StringBuilder sb = new StringBuilder();
					sb.append(CoreUtil.formataProntuarioRelatorio(atendimento.getPaciente().getProntuario()))
					  .append("          Mãe: ")
					  .append(CoreUtil.formataProntuarioRelatorio(this.atendimento.getPaciente().getMaePaciente().getProntuario()));
					prontuarioMae = sb.toString();
				}

			}
		}
		
		String enderecoHost = null;
		try {
			enderecoHost = super.getEnderecoRedeHostRemoto();
		} catch (UnknownHostException e) {
			LOG.error("Exceção caputada:", e);
		}

		int idx = 0;
		for (SumarioPrescricaoMedicaVO vo : listQuinzenaPrescricaoMedica) {
			if (vo.isSelected()) {
				prontuarioOnlineFacade.atualizarDataImpSumario(vo.getAtdSeq(),
						vo.getDthrInicio(), vo.getDthrFim(), enderecoHost);

				List<String> listaDias = new ArrayList<String>();
				Calendar dataInicial = Calendar.getInstance();
				dataInicial.setTime(vo.getDthrInicio());

				Calendar dataFinal = Calendar.getInstance();
				dataFinal.setTime(vo.getDthrFim());

				while (!dataInicial.after(dataFinal)) {
					String dia = String.valueOf(dataInicial.get(Calendar.DATE));
					listaDias.add(StringUtils.leftPad(dia, 2, '0'));
					dataInicial.add(Calendar.DATE, 1);
				}
				List<RelSumarioPrescricaoVO> dados = prontuarioOnlineFacade
						.buscaRelSumarioPrescricao(vo, false);
				
				if(atendimento != null && atendimento.getEspecialidade() != null){
					AghEspecialidades especialidade = aghuFacade.obterAghEspecialidadesPorChavePrimaria(atendimento.getEspecialidade().getSeq());
					atendimento.setEspecialidade(especialidade);
				}

				relatorioPrescricaoMedicaCallBack.setAtendimento(atendimento);
				relatorioPrescricaoMedicaCallBack.setAtendimentoPaciente(atendimentoPaciente);
				relatorioPrescricaoMedicaCallBack.setColecao(dados);
				relatorioPrescricaoMedicaCallBack.setListaDias(listaDias);				
				
				relatorioPrescricaoMedicaCallBack.setDataInicioPeriodo(vo.getDthrInicio());
				relatorioPrescricaoMedicaCallBack.setDataFimPeriodo(vo.getDthrFim());
				relatorioPrescricaoMedicaCallBack.setNomeEquipe(nomeEquipe);
				relatorioPrescricaoMedicaCallBack.setDataInternacao(dataInternacao);
				relatorioPrescricaoMedicaCallBack.setProntuarioMae(prontuarioMae);
				
				vo.setPdfFileNaoProtegido(relatorioPrescricaoMedicaCallBack.geraPdf(Boolean.FALSE));
				vo.setPdfFile(PdfUtil.protectPdf(vo.getPdfFileNaoProtegido()).toByteArray());
				//Teste executado no botão imprimir
				vo.setIdx(idx++);
				listQuinzenaPrescricaoMedicaSelecionados.add(vo);
			}
		}

		return RELATORIO_PRESCRICAO_MEDICA;
	}

	/**
	 * Método que carrega os dados para o relatório de prescrição de enfermagem
	 */
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	public String montaRelatorioPrescricaoEnfermagem() throws BaseException,
			JRException, SystemException, IOException, DocumentException {
				
		relatorioSumarioSelecionado = RELATORIO_SUMARIO_PRESCRICAO_ENFERMAGEM;

		listQuinzenaPrescricaoEnfermagemSelecionados = new ArrayList<SumarioPrescricaoEnfermagemVO>();
		
		setIdxSelecionado(0);

		String unidadeInternacao = null;
		Date dataInternacao = null;

		Integer hodSeq = null;
		Integer atuSeq = null;
		Integer intSeq = null;

		String prontuarioMae = null;

		if (!listQuinzenaPrescricaoEnfermagem.isEmpty()) {
			atendimentoPaciente = this.aghuFacade.obterAtendimentoPaciente(listQuinzenaPrescricaoEnfermagem.get(0).getAtdSeq(), listQuinzenaPrescricaoEnfermagem.get(0).getAtdPac());
			atendimento = aghuFacade.obterAghAtendimentoPorChavePrimaria(atendimentoPaciente.getId().getAtdSeq());

			unidadeInternacao = prontuarioOnlineFacade.obterUnidadeInternacao(atendimentoPaciente.getId().getAtdSeq(), atendimentoPaciente.getId().getSeq());

			if (atendimento.getHospitalDia() != null) {
				hodSeq = atendimento.getHospitalDia().getSeq();
			}
			if (atendimento.getInternacao() != null) {
				intSeq = atendimento.getInternacao().getSeq();
			}
			if (atendimento.getAtendimentoUrgencia() != null) {
				atuSeq = atendimento.getAtendimentoUrgencia().getSeq();
			}

			dataInternacao = prescricaoMedicaFacade.obterDataInternacao(intSeq, atuSeq, hodSeq);

			// paciente recém nacido requer prontuário da mãe no relatório
			if (this.atendimento.getPaciente().getProntuario() != null && atendimento.getPaciente().getProntuario() > VALOR_MAXIMO_PRONTUARIO) {

				if (this.atendimento.getPaciente().getMaePaciente() != null) {
					StringBuilder sb = new StringBuilder();
					sb.append(CoreUtil.formataProntuarioRelatorio(atendimento.getPaciente().getProntuario()))
					  .append("          Mãe: ")
					  .append(CoreUtil.formataProntuarioRelatorio(this.atendimento.getPaciente().getMaePaciente().getProntuario()));
					prontuarioMae = sb.toString();
				}
			}
		}

		int idx = 0;
		for (SumarioPrescricaoEnfermagemVO vo : listQuinzenaPrescricaoEnfermagem) {
			if (vo.isSelected()) {
				prontuarioOnlineFacade.atualizarDataImpSumarioEnfermagem(vo.getAtdSeq(), vo.getDthrInicio(), vo.getDthrFim());

				List<String> listaDias = new ArrayList<String>();
				Calendar dataInicial = Calendar.getInstance();
				dataInicial.setTime(vo.getDthrInicio());

				Calendar dataFinal = Calendar.getInstance();
				dataFinal.setTime(vo.getDthrFim());

				while (!dataInicial.after(dataFinal)) {
					String dia = String.valueOf(dataInicial.get(Calendar.DATE));
					listaDias.add(StringUtils.leftPad(dia, 2, '0'));
					dataInicial.add(Calendar.DATE, 1);
				}
				List<RelSumarioPrescricaoEnfermagemVO> dados = prontuarioOnlineFacade.buscaRelSumarioPrescricaoEnfermagem(vo, false);
				
				
				
				if (atendimento != null && atendimento.getInternacao() != null){
					AinInternacao internacao = internacaoFacade.obterAinInternacaoPorChavePrimaria(atendimento.getInternacao().getSeq());
					atendimento.setInternacao(internacao);
				}

				relatorioPrescricaoEnfermagemCallBack.setAtendimento(atendimento);
				relatorioPrescricaoEnfermagemCallBack.setAtendimentoPaciente(atendimentoPaciente);
				relatorioPrescricaoEnfermagemCallBack.setColecao(dados);
				relatorioPrescricaoEnfermagemCallBack.setListaDias(listaDias);
				relatorioPrescricaoEnfermagemCallBack.setarCaminhoLogo();
				relatorioPrescricaoEnfermagemCallBack.setDataInicioPeriodo(vo.getDthrInicio());
				relatorioPrescricaoEnfermagemCallBack.setDataFimPeriodo(vo.getDthrFim());
				relatorioPrescricaoEnfermagemCallBack.setUnidadeInternacao(unidadeInternacao);
				relatorioPrescricaoEnfermagemCallBack.setDataInternacao(dataInternacao);
				relatorioPrescricaoEnfermagemCallBack.setProntuarioMae(prontuarioMae);

				vo.setPdfFileNaoProtegido(relatorioPrescricaoEnfermagemCallBack.geraPdf(Boolean.FALSE));
				vo.setPdfFile(PdfUtil.protectPdf(vo.getPdfFileNaoProtegido()).toByteArray());
				//Teste executado no botão imprimir
				vo.setIdx(idx++);
				listQuinzenaPrescricaoEnfermagemSelecionados.add(vo);
			}
		}
		return RELATORIO_PRESCRICAO_ENFERMAGEM;
	}

	public String voltaRelatorioSumario() {
		if (modoVoltar.equals("D")) {
			return CONSULTA_DETALHE_INTERNACAO;
		} else {
			return CONSULTAR_INTERNACOES;
		}
	}

	public StreamedContent getRenderPdf() throws IOException,
			BaseException, JRException, SystemException, DocumentException {
		if (relatorioSumarioSelecionado
				.equals(RELATORIO_SUMARIO_PRESCRICAO_MEDICA)
				&& !listQuinzenaPrescricaoMedicaSelecionados.isEmpty()) {
			setPermiteImpressao(permiteImprimirSumarioPrescricaoMedicaInternacaoPol);
			return ActionReport.criarStreamedContentPdfPorByteArray(listQuinzenaPrescricaoMedicaSelecionados.get(idxSelecionado).getPdfFile());
		} else if (relatorioSumarioSelecionado
				.equals(RELATORIO_SUMARIO_PRESCRICAO_ENFERMAGEM)
				&& !listQuinzenaPrescricaoEnfermagemSelecionados.isEmpty()) {
			setPermiteImpressao(permiteImprimirSumarioPrescricaoEnfermagemInternacaoPol);
			return ActionReport.criarStreamedContentPdfPorByteArray(listQuinzenaPrescricaoEnfermagemSelecionados.get(idxSelecionado).getPdfFile());
		}
		return null;
	}
	
	public String directPrint() {
		try {
			byte[] pdfFile = null;			
			
			if (relatorioSumarioSelecionado
					.equals(RELATORIO_SUMARIO_PRESCRICAO_MEDICA)
					&& !listQuinzenaPrescricaoMedicaSelecionados.isEmpty()) {
				pdfFile = listQuinzenaPrescricaoMedicaSelecionados.get(idxSelecionado).getPdfFileNaoProtegido();
			} else if (relatorioSumarioSelecionado 
					.equals(RELATORIO_SUMARIO_PRESCRICAO_ENFERMAGEM)
					&& !listQuinzenaPrescricaoEnfermagemSelecionados.isEmpty()) {
				pdfFile = listQuinzenaPrescricaoEnfermagemSelecionados.get(idxSelecionado).getPdfFileNaoProtegido();
			}
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			IOUtils.write(pdfFile, baos);
			this.sistemaImpressao.imprimir(baos, super.getEnderecoIPv4HostRemoto(), "RELATORIO_SUMARIO_PRESCRICAO");
			
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
		return null;
	}

	public void obterInternacaoVOVerificarAcaoPermitida() {
		try {
			recemNasc = new ArrayList<McoRecemNascidos>();
			if (internacao.getSeq() != null) {
				carregaAdministracaoPrescricao();
				carregaRecemNascido();
				exibirModalExamesInternacao();
				if(getInternacao() != null){
					consultaDetalheConsultoriaController.possuiConsultoriasAtivas(getInternacao().getAtdSeq());
					this.dthrInicio = getInternacao().getDthrInicio();
					this.dthrFim = getInternacao().getDthrFim();
				}
				montaQuinzenaPrescricaoEnfermagem();
				montaQuinzenaPrescricaoMedica();
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			setInternacao(null);
		}
	}	

	private void carregaRecemNascido() {
		if (getInternacao().getCodigoPaciente() != null && getInternacao().getGsoSeqp() != null) {
			recemNasc = emergenciaFacade.pesquisarMcoRecemNascidoPorGestacaoOrdenado(getInternacao().getCodigoPaciente(), getInternacao().getGsoSeqp());
			
			if (recemNasc != null) {
				if (recemNasc.size() > 1) {
					exibirModalGestacao = Boolean.TRUE;
				} else if (recemNasc.size() == 1) {
					setRnaSeq(recemNasc.get(0).getId().getSeqp());
					exibirModalGestacao = Boolean.FALSE;
				}
			}
		}
	}	
	
	public String prepararRelatorioControles(){
		
		try {
			InternacaoVO internacaoVO = (InternacaoVO) BeanUtils.cloneBean(internacao);
			relatorioRegistrosControlesPacienteController.setInternacao(internacaoVO);
			relatorioRegistrosControlesPacienteController.setVoltarParaPolInternacoes(true);
			relatorioRegistrosControlesPacienteController.montaIntervaloPesquisa();
			openDialog("i_controlesPaciente_modalWG");
		} catch (Exception e) {
			apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ERRO_CLONAR_VO");
		}
		return null;
	}

	/**
	 * Verifica se o botão Exames deverá estar habilitado.
	 */
	public Boolean habilitarBotaoExames() {
		return habilitarBotaoPadrao();
	}

	/**
	 * Verifica se o botao(botão padrão) deverá ser habilitado.
	 */
	public Boolean habilitarBotaoPadrao() {
		Boolean retorno = Boolean.FALSE;
		if (getInternacao() != null && getInternacao().getSeq()!=null) {
			if (getUsuarioAdministrativo() == false) {
				retorno = Boolean.TRUE;
			}
		}
		return retorno;
	}
	
	/**
	 * Verifica se o botao exame fisicoRN deverá ser habilitado.
	 */
	public Boolean habilitarExameFisicoRecemNascido() {
		Boolean retorno = Boolean.FALSE;
		if(habilitarBotaoPadrao()){
			if (internacao.getSeq() != null) {
				if (getInternacao() != null  && getInternacao().getNumeroConsulta() != null && getInternacao().getCodigoPaciente() != null
						&& prontuarioOnlineFacade.habilitarBotaoExameFisico(DominioOrigemAtendimento.I,
								getInternacao().getCodigoPaciente(), getInternacao().getGsoSeqp())) {
					retorno = Boolean.TRUE;
				}
			}
		}
		return retorno;
	}
	
	public String visualizarRelatorioExameFisicoRecemNascido() {
		AghVersaoDocumento documento = certificacaoDigitalFacade.obterPrimeiroDocumentoAssinadoPorAtendimento(getInternacao().getAtdSeq(), DominioTipoDocumento.EF);
		
		if (documento != null) {
			setAghVersaoDocumento(documento);
			visualizarDocumentoController.setOrigem(POL_INTERNACAO);
			visualizarDocumentoController.setSeqAghVersaoDocumento(documento.getSeq());
			
			return VISUALIZAR_DOCUMENTO_ASSINADO_POL;
		}
		
		listaSeqp = prontuarioOnlineFacade.listarSeqpRecemNascido(internacao.getCodigoPaciente(), internacao.getNumeroConsulta());
		if(listaSeqp != null && listaSeqp.size() == 1){
			selecionadoExameFisicoRecemNascido = listaSeqp.get(0);
			return obterRetornoRelatorioRN();
			
		}else{
			openDialog("modalExameFisicoRecemNascidoWG");
			selecionadoExameFisicoRecemNascido = null;
			return null;
		}
	}
	
	public String obterRetornoRelatorioRN(){
		relatorioExameFisicoRNController.setPacCodigo(getInternacao().getCodigoPaciente());
		relatorioExameFisicoRNController.setGsoSeqp(getInternacao().getGsoSeqp());
		relatorioExameFisicoRNController.setSeqp(selecionadoExameFisicoRecemNascido.getId().getSeqp());
		relatorioExameFisicoRNController.setConNumero(getInternacao().getNumeroConsulta());
		relatorioExameFisicoRNController.setIndImpPrevia(indImpPrevia);
		relatorioExameFisicoRNController.setVoltarPara(POL_INTERNACAO);
		
		return RELATORIO_EXAME_FISICO_RN;
	}
	
	public String visualizarDiretoRelatorioExameFisicoRecemNascido() {
		return obterRetornoRelatorioRN();
	}
	
	/**
	 * Verifica se o botao Nascimento(RN) deverá ser habilitado.
	 */
	public Boolean habilitarBotaoNascimento() {
		Boolean ret = false;
		if (habilitarBotaoPadrao()) {
			ret = prontuarioOnlineFacade
					.habilitarBotaoNascimentoComAtendimento(
								getInternacao().getCodigoPaciente(),
								getInternacao().getNumeroConsulta(),
								getInternacao().getGsoSeqp());
		}
		return ret;
	}
	
	/**
	 * Verifica se o botao Admissao deverá ser habilitado.
	 */
	public Boolean habilitarBotaoAdmissao() {
		boolean retorno = false;		
		if (habilitarBotaoPadrao()) {
			AghAtendimentos atendimento = null;
			List<AghAtendimentos> atendimentos = aghuFacade
					.pesquisarAghAtendimentosPorPacienteEConsulta(
							getInternacao().getCodigoPaciente(),
							internacao.getNumeroConsulta());
			if(atendimentos != null && !atendimentos.isEmpty()) {
				atendimento = atendimentos.get(0);
				retorno = prontuarioOnlineFacade.habilitarBotaoAdmObs(DominioOrigemAtendimento.I,
							getInternacao().getCodigoPaciente(),
							getInternacao().getGsoSeqp(),
							getInternacao().getNumeroConsulta());
			}
			// parametro a ser utilizado na consulta param1 (abrirRelatorioAdmissaoObstetrica)
			if(atendimento != null) {
				setAtendimentoGsoSeqp(atendimento.getGsoSeqp());
			}
		}
		return retorno;
	}

	/**
	 * Verifica se o Paciente Protegido e deverá disponibilizar os botões especificos.
	 */
	public Boolean renderizarBotaoPacienteProtegido() {
		Boolean habilitar = Boolean.TRUE;
		if (getInternacao() == null
				|| (getInternacao() != null && (getInternacao()
						.getIndPacProtegido() == null || !getInternacao()
						.getIndPacProtegido().isSim()))) {
			habilitar = Boolean.FALSE;
		}
		return habilitar;
	}

	/**
	 * Verifica se o botão Cuidados deverá estar habilitado.
	 */
	public Boolean habilitarBotaoCuidados() {
		return habilitarBotaoPadrao()
				&& (getInternacao().getPossuiDataItemSumariosPE()!=null && getInternacao().getPossuiDataItemSumariosPE())
				&& getModuloPrescricaoEnfermagemAtivo();
	}

	
	private void carregaAdministracaoPrescricao(){
		if(getInternacao().getAtdSeq()!=null) {
			String leitoId = null;
			AghAtendimentos atendimento = this.aghuFacade.obterAghAtendimentoPorChavePrimaria(getInternacao().getAtdSeq());
			if (atendimento != null && atendimento.getLeito()!=null){
				leitoId =  atendimento.getLeito().getLeitoID();
			}			
			this.ordemDeAdminVOList = new ArrayList<OrdemDeAdministracaoVO>();
			
			List<EceOrdemDeAdministracao> ordemDeAdminList = this.checagemEletronicaFacade.buscarOrdemAdmin(getInternacao().getAtdSeq());
			if (!ordemDeAdminList.isEmpty() && ordemDeAdminList != null){
				String ultimaOrdemDeAdmin = null;
				for(EceOrdemDeAdministracao ordemDeAdministracao: ordemDeAdminList){
					String dataAdminFormatada = DateUtil.obterDataFormatada(ordemDeAdministracao.getDataReferencia(), "dd/MM/yyyy");
					if (!dataAdminFormatada.equalsIgnoreCase(ultimaOrdemDeAdmin)){
						ultimaOrdemDeAdmin = String.valueOf(dataAdminFormatada);
						
						OrdemDeAdministracaoVO ordemDeAdminVOAux = new OrdemDeAdministracaoVO();
		
						ordemDeAdminVOAux.setDataReferencia(ordemDeAdministracao.getDataReferencia());
						ordemDeAdminVOAux.setSeq(ordemDeAdministracao.getSeq());
						ordemDeAdminVOAux.setServidor(ordemDeAdministracao.getServidor());
						ordemDeAdminVOAux.setSituacao(ordemDeAdministracao.getSituacao());
						ordemDeAdminVOAux.setTurno(ordemDeAdministracao.getTurno());
						ordemDeAdminVOAux.setLeitoId(leitoId);
						
						ordemDeAdminVOAux.setOrdemAdministracao(ordemDeAdministracao);
						
						this.ordemDeAdminVOList.add(ordemDeAdminVOAux);
					}
				}
			}else{
				List<EceOrdemDeAdministracaoHist> ordemDeAdminHistList = this.checagemEletronicaFacade.buscarOrdemAdminHist(getInternacao().getAtdSeq());
				if (!ordemDeAdminHistList.isEmpty() && ordemDeAdminHistList !=null){
					String ultimaOrdemDeAdmin = null;
					for(EceOrdemDeAdministracaoHist ordemDeAdministracaoHist: ordemDeAdminHistList){
						String dataAdminFormatada = DateUtil.obterDataFormatada(ordemDeAdministracaoHist.getDataReferencia(), "dd/MM/yyyy");
						if (!dataAdminFormatada.equalsIgnoreCase(ultimaOrdemDeAdmin)){
							ultimaOrdemDeAdmin = String.valueOf(dataAdminFormatada);
							
							OrdemDeAdministracaoVO ordemDeAdminVOAux = new OrdemDeAdministracaoVO();
							
							ordemDeAdminVOAux.setDataReferencia(ordemDeAdministracaoHist.getDataReferencia());
							ordemDeAdminVOAux.setSeq(ordemDeAdministracaoHist.getSeq());
							ordemDeAdminVOAux.setServidor(ordemDeAdministracaoHist.getServidor());
							ordemDeAdminVOAux.setSituacao(ordemDeAdministracaoHist.getSituacao());
							ordemDeAdminVOAux.setTurno(ordemDeAdministracaoHist.getTurno());
							ordemDeAdminVOAux.setLeitoId(leitoId);
							
							ordemDeAdminVOAux.setOrdemAdministracaoHist(ordemDeAdministracaoHist);
							this.ordemDeAdminVOList.add(ordemDeAdminVOAux);
						}
					}
				}
			}
		}
	}
	
	/**
	 * Verifica se o botão Administração deverá estar habilitado.
	 */
	public Boolean habilitarBotaoAdministracao() {
		Boolean habilitarBotao = Boolean.FALSE;
		if (this.ordemDeAdminVOList!=null && !this.ordemDeAdminVOList.isEmpty()){
			habilitarBotao = Boolean.TRUE;
		}
		return habilitarBotao;
	}
	
	
	/**
	 * Verifica se exibe modal para seleção de administrações de prescrição
	 */
	public Boolean exibirModalBotaoAdministracao() {
		Boolean exibeModal = Boolean.FALSE;
		if (this.ordemDeAdminVOList!=null && this.ordemDeAdminVOList.size()>1){
			exibeModal = Boolean.TRUE;
		}else{
			if(this.ordemDeAdminVOList!=null && this.ordemDeAdminVOList.size()==1){
				this.ordemDeAdminVO = this.ordemDeAdminVOList.get(0);
			}
		}
		return exibeModal;
	}
	
	public void setUrlRelatorioOrdemAdministracao(String urlRelatorioOrdemAdministracao) {
		this.urlRelatorioOrdemAdministracao = urlRelatorioOrdemAdministracao;
	}

	public String getUrlRelatorioOrdemAdministracao() {
		return urlRelatorioOrdemAdministracao;
	}

	private void gerarUrlRelatorioOrdemAdministracao(OrdemDeAdministracaoVO item) {
		StringBuilder url = new StringBuilder();
		if (StringUtils.isBlank(getUrlBaseAdministracao())) {
			setUrlRelatorioOrdemAdministracao(null);
		} else {
			url.append(getUrlBaseAdministracao())

			.append('?')
			.append("aghw_token=")
			.append(this.obterTokenUsuarioLogado());

			if(item != null) {
				url.append("&ordemDeAdministracao.seq=")
				.append(item.getSeq())
				.append("&criterios.leitos=")
				.append(item.getLeitoId())
				.append("&criterios.dataReferencia=")
				.append(item.getDia()).append('/').append(item.getMes()).append('/').append(item.getAno());
			} else {
				url.append("&ordemDeAdministracao.seq=")
				.append(this.ordemDeAdminVO.getSeq())
				.append("&criterios.leitos=")
				.append(this.ordemDeAdminVO.getLeitoId())
				.append("&criterios.dataReferencia=")
				.append(this.ordemDeAdminVO.getDataReferencia());
			}		

			this.setUrlRelatorioOrdemAdministracao(url.toString());
		}	
	}

	public String visualizarRelatorioOrdemAdministracao(){
		return visualizarRelatorioOrdemAdministracao(null);
	}
	
	public void exibirModalOrdemDeAdministracao(){
		dia = mes = ano = null;
		openDialog("modalOrdemDeAdministracaoWG");
	}
	
	public String visualizarRelatorioOrdemAdministracao(OrdemDeAdministracaoVO ordemDeAdminVO) {
		if(StringUtils.isNotBlank(urlBaseAdministracao)){
			gerarUrlRelatorioOrdemAdministracao(ordemDeAdminVO);
			relatorioOrdemAdministracaoController.setUrlRelatorio(getUrlRelatorioOrdemAdministracao());
			relatorioOrdemAdministracaoController.inicio();
			if (!relatorioOrdemAdministracaoController.isErroAoGerarRelatorio()){
				return "relatorioOrdemAdministracao";				
			}
		} else {
			apresentarMsgNegocio(Severity.WARN,"AVISO_SISTEMA_CHEC_ELETRONICA_INDISPONIVEL");
		}
		return null;
	}
	
	/**
	 * Verifica se o botão Alta deverá estar habilitado.
	 */
	public Boolean habilitarBotaoAlta() {
		return habilitarBotaoPadrao()
				&& (getInternacao().getPossuiDocumentoAssinado()
						|| getInternacao().getPossuiDocumentoPendente() || getInternacao()
						.getPossuiRelatorioAltaObito()) ;
	}

	/**
	 * Verifica se o botão Sumário Parada deverá estar habilitado.
	 */
	public Boolean habilitarBotaoSumarioParada() {
		if (habilitarBotaoPadrao() && getInternacao().getAtdSeq()!=null) {
			if (permiteImprimirSumarioParada != null && permiteImprimirSumarioParada) {
				return prontuarioOnlineFacade.habilitarImprimirParadaInternacao(getInternacao().getAtdSeq());
			}
		}
		return Boolean.FALSE;
	}

	/**
	 * Verifica se o botão Transferência deverá estar habilitado.
	 */
	public Boolean habilitarBotaoTransferencia() {
		Boolean habilitar = Boolean.FALSE;
		if (getInternacao() != null) {
			habilitar = getInternacao().getFlagExibeSumarioDeTransferencia();
		}
		return habilitar;
	}

	/**
	 * Verifica se o botão Transferência deverá estar habilitado.
	 */
	public Boolean renderizaBotaoTransferenciaMulti() {
		Boolean habilitar = Boolean.FALSE;
		if (getInternacao() != null) {
			habilitar = prontuarioOnlineFacade.verificarSeInternacaoVariasTransferencias(getInternacao().getAtdSeq());
		}
		return habilitar;
	}

	

	/**
	 * Verifica se o botão Ananmese deverá estar habilitado.
	 */
	public Boolean habilitarBotaoAnanmese() {
		Boolean habilitar = Boolean.FALSE;
		if (getInternacao() != null) {
			habilitar = ambulatorioFacade
					.existeDadosImprimirRelatorioAnamneses(getInternacao()
							.getAtdSeq());
		}
		return habilitar && (getUsuarioAdministrativo() == false);
	}

	/**
	 * Verifica se o botão Evolução deverá estar habilitado.
	 */
	public Boolean habilitarBotaoEvolucao() {
		Boolean habilitar = Boolean.FALSE;
		if (getInternacao() != null) {
			habilitar = ambulatorioFacade
					.existeDadosImprimirRelatorioEvolucao(getInternacao()
							.getAtdSeq());
		}
		return habilitar && (getUsuarioAdministrativo() == false);
	}

	/**
	 * Verifica se o botão Controles deverá estar habilitado.
	 */
	public Boolean habilitarBotaoControles() {
		Boolean retorno = Boolean.FALSE;
		if (getInternacao() != null) {
			retorno = getInternacao().getPossuiRegistroControlepaciente();
			if(retorno == null) {
				retorno = Boolean.FALSE;
			}
		}
		return (retorno &&  securityController.usuarioTemPermissao("pesquisarRegistrosControlePaciente", "pesquisar"));
	}
	
	/**
	 * Verifica se o botão Parto deverá estar habilitado.
	 */
	public Boolean habilitarBotaoParto() {
		
		if (habilitarBotaoPadrao()) {
			return prontuarioOnlineFacade.habilitarBotaoPartoComAtendimento(
					getInternacao().getCodigoPaciente(), getInternacao()
							.getNumeroConsulta(), getInternacao().getGsoSeqp());
		}
		return false;
	}

	/**
	 * Acionado pelo botão Anamnese na tela consultaInternacoesPOL
	 */
	public String pesquisarAnamnese() {
		relatorioAnaEvoInternacaoController.setTipoRelatorio(EnumTargetConsultaInternacoesPOL.RELATORIO_ANAMNESES.toString());
		
		gerarRelatorioAnaEvoInternacao(); 
		
		if (relatorioAnaEvoInternacaoController.getDadosRelatorioIsNotEmpty()) {
			return "ambulatorio-relatorioAmamneseEvolucaoInternacao";
		}
		return null;
	}

	/**
	 * Acionado pelo botão Visualizar Últimos 15 dias na tela
	 */
	public String pesquisarEvolucaoPeriodo() {
		String retorno = null;
		boolean dataNula = false;
		if (getDataInicial() == null){
			this.apresentarMsgNegocio("dataInicio",Severity.ERROR, "RELATORIO_EVOLUCAO_PERIODO_DATA_OBRIGATORIA", "Data Inicial");
			dataNula = true;		
		}
		if (getDataFinal() == null){
			this.apresentarMsgNegocio("dataFim",Severity.ERROR, "RELATORIO_EVOLUCAO_PERIODO_DATA_OBRIGATORIA", "Data Final");
			dataNula = true;			
		}

		if(getDataInicial() != null && getDataFinal() != null){
			if (getDataInicial().after(getDataFinal())) {
				apresentarMsgNegocio(Severity.ERROR, "RELATORIO_EVOLUCAO_PERIODO_DATA_FINAL_MENOR_DATA_INICIAL");
				return null;
			}
		}

		if (dataNula){
			return null;
		}
		
		if (DateUtil.calcularDiasEntreDatas(getDataInicial(), getDataFinal()) > 30){
			this.apresentarMsgNegocio(Severity.ERROR ,"RELATORIO_EVOLUCAO_PERIODO_MAIOR_30_DIAS");
			return null;			
		}
		relatorioAnaEvoInternacaoController.setDataInicial(getDataInicial());
		relatorioAnaEvoInternacaoController.setDataFinal(getDataFinal());
		relatorioAnaEvoInternacaoController.setTipoRelatorio(EnumTargetConsultaInternacoesPOL.RELATORIO_EVOLUCAO_PERIODO.toString());
		
		gerarRelatorioAnaEvoInternacao();

		if (relatorioAnaEvoInternacaoController.getDadosRelatorioIsNotEmpty()) {
			retorno = AMBULATORIO_RELATORIO_AMAMNESE_EVOLUCAO_INTERNACAO;
		}
			
		 return retorno;
	}
	


	public boolean exibirBotaoAnamneseEvolucaoPrescricaoMedica() {

		try {

			if(this.parametroFacade.verificarExisteAghParametro(AghuParametrosEnum.P_AGHU_POL_COM_ANAMNESE_EVOLUCAO_PRESCRICAO_MEDICA)) {

				String vlrTexto = this.parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_POL_COM_ANAMNESE_EVOLUCAO_PRESCRICAO_MEDICA);

				return "S".equalsIgnoreCase(vlrTexto) ? true : false;

			}

		} catch (ApplicationBusinessException e) {

			apresentarExcecaoNegocio(e);			

		}

		return false;	

	}

	public boolean habilitarBotaoAnanmesePrescricao(){
		if (getInternacao().getAtdSeq() != null) {
			seqAtendimento = getInternacao().getAtdSeq();
		}
		
		if(getInternacao().getAtdSeq() != null) {
			return this.prescricaoMedicaFacade.existeAnamneseValidaParaAtendimento(getInternacao().getAtdSeq());
		}
		return false;
	}

	public boolean habilitarBotaoEvolucaoPrescricao() {
		if(this.seqAtendimento != null) {
			MpmAnamneses anamnese = this.prescricaoMedicaFacade.obterAnamneseValidaParaAtendimento(this.seqAtendimento);
			if(anamnese == null) {
				return false;
			}
			this.seqAnamnese = anamnese.getSeq();
			return this.prescricaoMedicaFacade.verificarEvolucoesAnamnesePorSituacao(this.seqAnamnese, DominioIndPendenteAmbulatorio.V, true);
		}
		return false;
	}
	

	/**
	 * Acionado pelo botão Visualizar Todos na tela consultaInternacoesPOL
	 */
	
	public void pesquisarEvolucaoTodos() {
		RapServidores servidorLogado = null;
		try {
			// Seta sessão para 12 horas
			pacienteFacade.commit(60 * 60 * 12);
			
			servidorLogado = registroColaboradorFacade
					.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado(),
							new Date());

			relatorioAnaEvoInternacaoController.imprimirRelatorioEvolucaoTodos(
					getInternacao().getAtdSeq(), servidorLogado,
					this.getEnderecoIPv4HostRemoto());

			apresentarMsgNegocio(Severity.INFO,
					"MENSAGEM_SUCESSO_IMPRESSAO_RELATORIO", "Evoluções");
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} catch (UnknownHostException e) {
			LOG.error(e.getMessage());
		}
		
		
		
		
	}
	
	public String gerarDadosRelatorioExamesPaciente(){
		String retorno = null;
		
		try {
			RelatorioExamesPacienteVO vo = null;
			emitirRelatorioExamesPacienteController.setOrigem(POL_INTERNACAO);
			emitirRelatorioExamesPacienteController.setPacCodigo(getInternacao().getCodigoPaciente());
			emitirRelatorioExamesPacienteController.setAtdSeq(getInternacao().getAtdSeq());
			emitirRelatorioExamesPacienteController.setDataHoraEvento(getDthrMovimento());
			emitirRelatorioExamesPacienteController.setNumeroProntuario(getNumeroProntuario());
			vo = examesFacade.montarRelatorio(getNumeroProntuario(), getInternacao().getAtdSeq(), DominioSumarioExame.B, getDthrMovimento(), true, "H", getInternacao().getCodigoPaciente() );
			retorno = "relatorioExamePacientePdf";
			
			emitirRelatorioExamesPacienteController.setVo(vo);
			
		} catch (NumberFormatException e) {
			LOG.error(e.getMessage() ,e);
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		
		return retorno;
	}
	
	public String gerarDadosRelatorioExamesPacienteDetalhe(){
		String retornoExame = gerarDadosRelatorioExamesPaciente();
		emitirRelatorioExamesPacienteController.setOrigem(POL_DETALHE_INTERNACAO);
		return retornoExame;
	}
	

	private void gerarRelatorioAnaEvoInternacao() {	
	
			relatorioAnaEvoInternacaoController.setOrigem(POL_INTERNACAO);
			relatorioAnaEvoInternacaoController.setAtdSeq(getInternacao().getAtdSeq());		
			relatorioAnaEvoInternacaoController.gerarDados();	

	}
	
	public String abrirRelatorioNascimento() throws ApplicationBusinessException{
		String retorno = obterPrimeiroDocumentoAssinadoPorAtendimento(DominioTipoDocumento.ARN);
		if(retorno == null){
			relatorioAtendimentoRNController.setPacCodigo(getInternacao().getCodigoPaciente());
			relatorioAtendimentoRNController.setConNumero(getInternacao().getNumeroConsulta());
			relatorioAtendimentoRNController.setGsoSeqp(getInternacao().getGsoSeqp());
			relatorioAtendimentoRNController.setAtdSeq(getInternacao().getAtdSeq());

			if(exibirModalGestacao){
				relatorioAtendimentoRNController.setRnaSeqp(selecionadoRecemNasc.getId().getSeqp());
				
			} else {
				relatorioAtendimentoRNController.setRnaSeqp(recemNasc.get(0).getId().getSeqp());
			}
			
			relatorioAtendimentoRNController.gerarDados();
			relatorioAtendimentoRNController.setOrigem(POL_INTERNACAO);
			retorno = RELATORIO_ATENDIMENTO_RECEM_NASCIDO;
		}
		return retorno;
	}
	
	/**
	 * Estoria #15836, botao admissao
	 */
	public String abrirRelatorioAdmissaoObstetrica() {
		
		String retorno = obterPrimeiroDocumentoAssinadoPorAtendimento(DominioTipoDocumento.ACO);
		
		if(retorno == null){
			
			retorno = RELATORIO_ADMISSAO_OBSTETRICA;
		
			// consulta PARAM 2
			List<McoLogImpressoes> impressoes = emergenciaFacade.pesquisarLogImpressoesEventoMcorAdmissaoObs(getInternacao().getCodigoPaciente(), getInternacao().getGsoSeqp(), null);
			if(impressoes != null && !impressoes.isEmpty()) {
				McoLogImpressoes logImpressao = impressoes.get(0);
				if(logImpressao != null) {
					setDthrMovimento(logImpressao.getCriadoEm());
					setSerMatricula(logImpressao.getServidor().getId().getMatricula());
					setSerVinCodigo(logImpressao.getServidor().getId().getVinCodigo());
				}
			}
		}
		
		relatorioAdmissaoObstetricaController.setPacCodigo(getInternacao().getCodigoPaciente());
		relatorioAdmissaoObstetricaController.setConNumero(getInternacao().getNumeroConsulta());
		relatorioAdmissaoObstetricaController.setGsoSeqp(getInternacao().getGsoSeqp());
		relatorioAdmissaoObstetricaController.setMatricula(getSerMatricula());
		relatorioAdmissaoObstetricaController.setVinculo(getSerVinCodigo());
		relatorioAdmissaoObstetricaController.setDthrMovimento(getDthrMovimento());
		relatorioAdmissaoObstetricaController.setOrigem(POL_INTERNACAO);

		return retorno;	
	}
	
	public String abrirRelatorioAdmissaoObstetricaDetalhe() {
		String retornoDetalhe = abrirRelatorioAdmissaoObstetrica();
		relatorioAdmissaoObstetricaController.setOrigem(POL_DETALHE_INTERNACAO);
		return retornoDetalhe;
	}
	
	/**
	 * Estoria #15834, botão Sumário de Parada
	 * @author bruno.mourao
	 * @since 11/09/2012
	 */
	public String abrirRelatorioParadaInternacao() throws BaseException {
	
		
		paradasInternacoes = prescricaoMedicaFacade.pesquisarParadaInternacaoPorAtendimento(getInternacao().getAtdSeq());
		relatorioParadaInternacaoController.setOrigem(getVoltarPara());
		relatorioParadaInternacaoController.setAtdSeq(getInternacao().getAtdSeq());
		relatorioParadaInternacaoController.setNomeArquivo(prontuarioOnlineFacade.obterNomeArquivoRelatorioSumarioParadaAtual(getInternacao().getAtdSeq()));
		relatorioParadaInternacaoController.setDthrCriacao(paradasInternacoes.get(0).getId().getDthrCriacao());
		relatorioParadaInternacaoController.setSeqInternacao(internacao.getSeq());
		relatorioParadaInternacaoController.setTipoInternacao(tipoRelatorio);
		return RELATORIO_PARADA_INTERNACAO;
	}
	
	public void cleanParamRecemNascido(){
		setRnaSeq(null);
		selecionadoRecemNasc = null;
	}

	private String obterPrimeiroDocumentoAssinadoPorAtendimento(DominioTipoDocumento tipoDocumento) {
		AghVersaoDocumento documento = certificacaoDigitalFacade.obterPrimeiroDocumentoAssinadoPorAtendimento(getInternacao().getAtdSeq(), tipoDocumento);
		
		if (documento != null) {
			setAghVersaoDocumento(documento);
			visualizarDocumentoController.setOrigem(POL_INTERNACAO);
			visualizarDocumentoController.setSeqAghVersaoDocumento(documento.getSeq());
			
			return VISUALIZAR_DOCUMENTO_ASSINADO_POL;
			
		} else{
			return null;
		}
	}
	
	public String visualizarSumarioAssistenciaParto(){
		
		String retorno = obterPrimeiroDocumentoAssinadoPorAtendimento(DominioTipoDocumento.ARN);
		
		if(retorno == null){
			retorno = RELATORIO_SUMARIO_ASSISTENCIA_PARTO;
			
			relatorioSumarioAssistenciaPartoController.setPacCodigo(getInternacao().getCodigoPaciente());
			relatorioSumarioAssistenciaPartoController.setGsoSeqp(getInternacao().getGsoSeqp());
			relatorioSumarioAssistenciaPartoController.setConNumero(getInternacao().getNumeroConsulta());
			relatorioSumarioAssistenciaPartoController.setExibirBotaoVoltar(true);
			relatorioSumarioAssistenciaPartoController.setVoltarPara(POL_INTERNACAO);
		}
		return retorno;
	}
	
	public String visualizarSumarioAssistenciaPartoDetalhe(){
		String retornoDetalhe = visualizarSumarioAssistenciaParto();
		relatorioSumarioAssistenciaPartoController.setVoltarPara(POL_DETALHE_INTERNACAO);
		return retornoDetalhe;
	}
	
	public void atualizarListaPrescricaoEnfermagem(Integer idx) {
		validarSelecaoSumariosEnfermagem(idx);
		Boolean possuiSelected = Boolean.TRUE;
		for (SumarioPrescricaoEnfermagemVO sumario : listQuinzenaPrescricaoEnfermagem) {
			if (sumario.isSelected()) {
				possuiSelected = Boolean.FALSE;
			}
		}
		setDesabilitarBotaoVisualizarImpressao(possuiSelected);
	}
	
	/**
	 * Estoria #5945, botao exames
	 */
	public boolean exibirModalExamesInternacao() {

		exibirModalExames = Boolean.FALSE;		
		
		//Verifica se o usuário tem permissão 
		if(!acessoSumarioExamesInternacaoPOL){
			//1.1)Não.Verifica se paciente é protegido.
			if((getInternacao() != null && getInternacao().getIndPacProtegido() != null) && (getInternacao().getIndPacProtegido().equals(DominioSimNao.S))){
				/*1.1.1)Sim.Exibir modal com msg:"O Paciente 'Nome de q_pac' e protegido.Deseja continuar? e botões Sim e Não"
				1.1.1.1)Não.Cancelar ação,retonando para lista de internações.*/
				exibirModalExames = Boolean.TRUE;				
				//(ind_pac_protegido = 'S' de Q_PAC)
				AipPacientes paciente = pacienteFacade.obterPaciente(getInternacao().getCodigoPaciente());
				nomePacSelecionado.append(paciente.getNome());
				setMensagemModalExames(recuperarMessages(EnumTargetMsgNomePac.MSG_MODAL_EXAME_PACIENTE.toString(), paciente.getNome()));
			}
		}
//		else{
//			//2)Gerar dados para o relatório.
//			if((getInternacao() != null && getInternacao().getIndPacProtegido() != null) && (getInternacao().getIndPacProtegido().equals(DominioSimNao.S))){
//				gerarDadosRelatorioExamesPaciente();
//			}
//		}

		return exibirModalExames;
	}
	
	public void validarSelecaoSumariosEnfermagem(Integer idxSumario){
		Integer countSumariosSelect = 0;
		if(listQuinzenaPrescricaoEnfermagem != null){
			for (SumarioPrescricaoEnfermagemVO sumario : listQuinzenaPrescricaoEnfermagem) {
				if(sumario.isSelected()){
					countSumariosSelect++;
				}
			}
			
			if(countSumariosSelect > QTDE_MAX_SELECAO_SUMARIOS){
				qtdeMaxSumariosSelecionados = true;
				//Desmarca o item selecionado por ultimo
				for (SumarioPrescricaoEnfermagemVO sumario : listQuinzenaPrescricaoEnfermagem) {
					if(sumario.getIdx() == idxSumario){
						sumario.setSelected(false);
					}
				}
			}
		}
		else{
			qtdeMaxSumariosSelecionados = false;
		}
	}
	
	
	public List<SelectItem> listarCTIUTIP(){
		
		List<SelectItem> result = new ArrayList<SelectItem>();
		
		if(getInternacao() != null && getInternacao().getAtdSeq()!= null){
			
			List<MpmFichaApache> listaFichaApache = prescricaoMedicaFacade.pesquisarFichasApachePorAtendimento(getInternacao().getAtdSeq());
			
			if (!listaFichaApache.isEmpty()) {
				SelectItem item = new SelectItem();
				item.setDescription("APACHE");
				item.setLabel("APACHE");
				item.setValue(-1L);
				result.add(item);
				
				for (MpmFichaApache fichaApache : listaFichaApache) {
					item = new SelectItem();
					item.setDescription(DateUtil.dataToString(fichaApache.getDthrIngressoUnidade(), "dd/MM/yyyy HH:mm"));
					item.setLabel(item.getDescription());
					item.setValue(fichaApache.getId().getSeqp());
					result.add(item);
				}
			}
			else {
				SelectItem item = new SelectItem();
				item.setDescription("PIM2");
				item.setLabel("PIM2");
				item.setValue(-1L);
				result.add(item);
				List<DominioSituacaoPim2> situacoes = new ArrayList<DominioSituacaoPim2>();
				situacoes.add(DominioSituacaoPim2.A);
				situacoes.add(DominioSituacaoPim2.E);
				List<MpmPim2> pim2 = prescricaoMedicaFacade.pesquisarPim2PorAtendimentoSituacao(getInternacao().getAtdSeq(), situacoes);
				for (MpmPim2 mpmPim2 : pim2) {
					item = new SelectItem();
					item.setDescription(DateUtil.dataToString(mpmPim2.getDthrIngressoUnidade(), "dd/MM/yyyy HH:mm"));
					item.setLabel(item.getDescription());//Precisa ser definido, senao dá nullpointer
					item.setValue(mpmPim2.getSeq());
					result.add(item);
				}
			}
		}
		return result;
	}
	
	public void selecionarCTIUTI(){
		if(itemSelecionado != -1){
			List<MpmFichaApache> listaFichaApache = prescricaoMedicaFacade.pesquisarFichasApachePorAtendimento(getInternacao().getAtdSeq());
			if (!listaFichaApache.isEmpty()) { //se for ficha apache
				visualizacaoFichaApacheController.setAtdSeq(getInternacao().getAtdSeq());
				visualizacaoFichaApacheController.setSeqp(itemSelecionado.shortValue());
				visualizacaoFichaApacheController.setVoltarPara(POL_INTERNACAO);
				visualizacaoFichaApacheController.inicio();
				setItemSelecionado(null);
				try {
					FacesContext.getCurrentInstance().getExternalContext().redirect("visualizacaoFichaApache.xhtml?cid="+super.conversation.getId());
				} catch (IOException e) {
					LOG.error("A página visualizacaoFichaApache.xhtml não foi encontrada: ", e);
				}
			} else { //se for PIM2
				relatorioPIM2Controller.setSeqPim2(itemSelecionado);
				relatorioPIM2Controller.gerarDados();
				relatorioPIM2Controller.setOrigem(POL_INTERNACAO);
				setItemSelecionado(null);
				try {
					FacesContext.getCurrentInstance().getExternalContext().redirect("relatorioPIM2Pdf.xhtml?cid="+super.conversation.getId());
				} catch (IOException e) {
					LOG.error("A página relatorioPIM2Pdf.xhtml não foi encontrada: ", e);
				}
			}
		}
	}
	
	private boolean hasPIM2FichaApache(){
		if(getInternacao().getAtdSeq() == null){
			return Boolean.FALSE;
		}
		List<DominioSituacaoPim2> situacoes = new ArrayList<DominioSituacaoPim2>();
		situacoes.add(DominioSituacaoPim2.A);
		situacoes.add(DominioSituacaoPim2.E);
		List<MpmPim2> pim2 = prescricaoMedicaFacade.pesquisarPim2PorAtendimentoSituacao(getInternacao().getAtdSeq(), situacoes);
		
		List<MpmFichaApache> listaFichaApache = prescricaoMedicaFacade.pesquisarFichasApachePorAtendimento(getInternacao().getAtdSeq());
		
		return (pim2 != null && pim2.size() > 0) || (!listaFichaApache.isEmpty());
	}
	
	public boolean possuiConsultoriasAtivas() {
		
		Long sol = prescricaoMedicaFacade.pesquisaSolicitacoesConsultoriaCount(getInternacao().getAtdSeq());
		
		if(sol != null && sol > 0) {
			return true;
		}
		return false;	
	}

	public void carregarPacSelecionado() {
		AipPacientes paciente = pacienteFacade.obterPaciente(getInternacao().getCodigoPaciente());
		setMensagemModalExames(recuperarMessages(EnumTargetMsgNomePac.MSG_MODAL_EXAME_PACIENTE.toString(),paciente.getNome()));
	}
	
	

	
	public AghVersaoDocumento getAghVersaoDocumento() {
		return aghVersaoDocumento;
	}

	public void setAghVersaoDocumento(AghVersaoDocumento aghVersaoDocumento) {
		this.aghVersaoDocumento = aghVersaoDocumento;
	}

	public String getOrigem() {
		return origem;
	}

	public void setOrigem(String origem) {
		this.origem = origem;
	}

	public boolean isDesabilitarBotaoVisualizarImpressao() {
		return desabilitarBotaoVisualizarImpressao;
	}

	public void setDesabilitarBotaoVisualizarImpressao(boolean desabilitarBotaoVisualizarImpressao) {
		this.desabilitarBotaoVisualizarImpressao = desabilitarBotaoVisualizarImpressao;
	}

	public Integer getNumeroProntuario() {
		return numeroProntuario;
	}

	public void setNumeroProntuario(Integer numeroProntuario) {
		this.numeroProntuario = numeroProntuario;
	}

	public boolean isPol() {
		return pol;
	}

	public void setPol(boolean pol) {
		this.pol = pol;
	}

	public List<SumarioPrescricaoMedicaVO> getListQuinzenaPrescricaoMedica() {
		return listQuinzenaPrescricaoMedica;
	}

	public void setListQuinzenaPrescricaoMedica(
			List<SumarioPrescricaoMedicaVO> listQuinzenaPrescricaoMedica) {
		this.listQuinzenaPrescricaoMedica = listQuinzenaPrescricaoMedica;
	}

	public InternacaoVO getInternacao() {
		if (internacao == null) {
			internacao = new InternacaoVO();
		}
		return internacao;
	}

	public void setInternacao(InternacaoVO internacao) {
		this.internacao = internacao;
	}

	public List<SumarioPrescricaoMedicaVO> getListQuinzenaPrescricaoMedicaSelecionados() {
		return listQuinzenaPrescricaoMedicaSelecionados;
	}

	public void setListQuinzenaPrescricaoMedicaSelecionados(
			List<SumarioPrescricaoMedicaVO> listQuinzenaPrescricaoMedicaSelecionados) {
		this.listQuinzenaPrescricaoMedicaSelecionados = listQuinzenaPrescricaoMedicaSelecionados;
	}

	public String getModoVoltar() {
		return modoVoltar;
	}

	public void setModoVoltar(String modoVoltar) {
		this.modoVoltar = modoVoltar;
	}

	public List<SumarioPrescricaoEnfermagemVO> getListQuinzenaPrescricaoEnfermagem() {
		return listQuinzenaPrescricaoEnfermagem;
	}

	public void setListQuinzenaPrescricaoEnfermagem(
			List<SumarioPrescricaoEnfermagemVO> listQuinzenaPrescricaoEnfermagem) {
		this.listQuinzenaPrescricaoEnfermagem = listQuinzenaPrescricaoEnfermagem;
	}

	public List<SumarioPrescricaoEnfermagemVO> getListQuinzenaPrescricaoEnfermagemSelecionados() {
		return listQuinzenaPrescricaoEnfermagemSelecionados;
	}

	public void setListQuinzenaPrescricaoEnfermagemSelecionados(
			List<SumarioPrescricaoEnfermagemVO> listQuinzenaPrescricaoEnfermagemSelecionados) {
		this.listQuinzenaPrescricaoEnfermagemSelecionados = listQuinzenaPrescricaoEnfermagemSelecionados;
	}

	public Boolean getModuloPrescricaoEnfermagemAtivo() {
		return moduloPrescricaoEnfermagemAtivo;
	}

	public void setModuloPrescricaoEnfermagemAtivo(
			Boolean moduloPrescricaoEnfermagemAtivo) {
		this.moduloPrescricaoEnfermagemAtivo = moduloPrescricaoEnfermagemAtivo;
	}

	public AltaSumarioVO getTransferenciaSumarioSelecionada() {
		return transferenciaSumarioSelecionada;
	}

	public void setTransferenciaSumarioSelecionada(
			AltaSumarioVO transferenciaSumarioSelecionada) {
		this.transferenciaSumarioSelecionada = transferenciaSumarioSelecionada;
	}

	public List<AltaSumarioVO> getListaAltaSumario() {
		return listaAltaSumario;
	}

	public void setListaAltaSumario(List<AltaSumarioVO> listaAltaSumario) {
		this.listaAltaSumario = listaAltaSumario;
	}

	public String getTipoRelatorio() {
		return tipoRelatorio;
	}

	public void setTipoRelatorio(String tipoRelatorio) {
		this.tipoRelatorio = tipoRelatorio;
	}

	public List<McoRecemNascidos> getRecemNasc() {
		return recemNasc;
	}

	public void setRecemNasc(List<McoRecemNascidos> recemNasc) {
		this.recemNasc = recemNasc;
	}

	public Byte getRnaSeq() {
		return rnaSeq;
	}

	public void setRnaSeq(Byte rnaSeq) {
		this.rnaSeq = rnaSeq;
	}

	public Integer getSerMatricula() {
		return serMatricula;
	}

	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}

	public Short getSerVinCodigo() {
		return serVinCodigo;
	}

	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}

	public Short getAtendimentoGsoSeqp() {
		return atendimentoGsoSeqp;
	}

	public void setAtendimentoGsoSeqp(Short atendimentoGsoSeqp) {
		this.atendimentoGsoSeqp = atendimentoGsoSeqp;
	}

	public Date getDthrMovimento() {
		return dthrMovimento;
	}

	public void setDthrMovimento(Date dthrMovimento) {
		this.dthrMovimento = dthrMovimento;
	}

	public Short getGsoSeqp() {
		return gsoSeqp;
	}

	public void setGsoSeqp(Short gsoSeqp) {
		this.gsoSeqp = gsoSeqp;
	}

	public Boolean getExibirModalGestacao() {
		return exibirModalGestacao;
	}

	public void setExibirModalGestacao(Boolean exibirModalGestacao) {
		this.exibirModalGestacao = exibirModalGestacao;
	}

	public Integer getAtdSeq() {
		return atdSeq;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	public RelatorioAdmissaoObstetricaController getRelatorioAdmissaoObstetricaController() {
		return relatorioAdmissaoObstetricaController;
	}

	public void setRelatorioAdmissaoObstetricaController(
			RelatorioAdmissaoObstetricaController relatorioAdmissaoObstetricaController) {
		this.relatorioAdmissaoObstetricaController = relatorioAdmissaoObstetricaController;
	}

	public StringBuilder getNomePacSelecionado() {
		return nomePacSelecionado;
	}

	public void setNomePacSelecionado(StringBuilder nomePacSelecionado) {
		this.nomePacSelecionado = nomePacSelecionado;
	}

	public EmitirRelatorioExamesPacienteController getEmitirRelatorioExamesPacienteController() {
		return emitirRelatorioExamesPacienteController;
	}

	public Boolean getExibirModalExames() {
		return exibirModalExames;
	}

	public void setExibirModalExames(Boolean exibirModalExames) {
		this.exibirModalExames = exibirModalExames;
	}

	private String recuperarMessages(String key, Object... params) {
		return WebUtil.initLocalizedMessage(key, null, params);
	}

	public String getMensagemModalExames() {
		return mensagemModalExames;
	}

	public void setMensagemModalExames(String mensagemModalExames) {
		this.mensagemModalExames = mensagemModalExames;
	}

	public Boolean habilitarBotaoVisualizar() {
		return getRnaSeq() != null;
	}

	public void setListaSeqp(List<McoRecemNascidos> listaSeqp) {
		this.listaSeqp = listaSeqp;
	}

	public List<McoRecemNascidos> getListaSeqp() {
		return listaSeqp;
	}

	public void setIndImpPrevia(String indImpPrevia) {
		this.indImpPrevia = indImpPrevia;
	}

	public String getIndImpPrevia() {
		return indImpPrevia;
	}

	public Boolean getQtdeMaxSumariosSelecionados() {
		return qtdeMaxSumariosSelecionados;
	}

	public void setQtdeMaxSumariosSelecionados(
			Boolean qtdeMaxSumariosSelecionados) {
		this.qtdeMaxSumariosSelecionados = qtdeMaxSumariosSelecionados;
	}

	public Long getItemSelecionado() {
		return itemSelecionado;
	}

	public void setItemSelecionado(Long itemSelecionado) {
		this.itemSelecionado = itemSelecionado;
	}

	public Boolean habilitarComboCTIUTI() {
		return !acessoAdminPOL && hasPIM2FichaApache();
	}

	public OrdemDeAdministracaoVO getOrdemDeAdminVO() {
		return ordemDeAdminVO;
	}

	public void setOrdemDeAdminVO(OrdemDeAdministracaoVO ordemDeAdminVO) {
		this.ordemDeAdminVO = ordemDeAdminVO;
	}

	public List<OrdemDeAdministracaoVO> getOrdemDeAdminVOList() {
		return ordemDeAdminVOList;
	}

	public void setOrdemDeAdminVOList(
			List<OrdemDeAdministracaoVO> ordemDeAdminVOList) {
		this.ordemDeAdminVOList = ordemDeAdminVOList;
	}

	public Boolean getBtnSumarioTranfDetalheInternacao() {
		return btnSumarioTranfDetalheInternacao;
	}

	public void setBtnSumarioTranfDetalheInternacao(
			Boolean btnSumarioTranfDetalheInternacao) {
		this.btnSumarioTranfDetalheInternacao = btnSumarioTranfDetalheInternacao;
	}

	public Date getDataInicial() {
		return dataInicial;
	}

	public void setDataInicial(Date dataInicial) {
		this.dataInicial = dataInicial;
	}

	public Date getDataFinal() {
		return dataFinal;
	}

	public void setDataFinal(Date dataFinal) {
		this.dataFinal = dataFinal;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public String getUrlBaseAdministracao() {
		return urlBaseAdministracao;
	}

	public void setUrlBaseAdministracao(String urlBaseAdministracao) {
		this.urlBaseAdministracao = urlBaseAdministracao;
	}

	public void setPermiteImpressao(Boolean permiteImpressao) {
		this.permiteImpressao = permiteImpressao;
	}

	public Boolean getPermiteImpressao() {
		return permiteImpressao;
	}

	public Integer getIdxSelecionado() {
		return idxSelecionado;
	}

	public void setIdxSelecionado(Integer idxSelecionado) {
		this.idxSelecionado = idxSelecionado;
	}

	public List<MamPcIntParada> getParadasInternacoes() {
		return paradasInternacoes;
	}

	public void setParadasInternacoes(List<MamPcIntParada> paradasInternacoes) {
		this.paradasInternacoes = paradasInternacoes;
	}

	public Integer getDetalheSeq() {
		return detalheSeq;
	}

	public void setDetalheSeq(Integer detalheSeq) {
		this.detalheSeq = detalheSeq;
	}

	public String getDetalheTipo() {
		return detalheTipo;
	}

	public void setDetalheTipo(String detalheTipo) {
		this.detalheTipo = detalheTipo;
	}

	public List<InternacaoVO> getInternacaoVOList() {
		return internacaoVOList;
	}

	public void setInternacaoVOList(List<InternacaoVO> internacaoVOList) {
		this.internacaoVOList = internacaoVOList;
	}

	public AghAtendimentos getAtendimento() {
		return atendimento;
	}

	public void setAtendimento(AghAtendimentos atendimento) {
		this.atendimento = atendimento;
	}

	public Date getDthrInicio() {
		return dthrInicio;
	}

	public void setDthrInicio(Date dthrInicio) {
		this.dthrInicio = dthrInicio;
	}

	public Date getDthrFim() {
		return dthrFim;
	}

	public void setDthrFim(Date dthrFim) {
		this.dthrFim = dthrFim;
	}

	public boolean isVoltarParaPolInternacoes() {
		return voltarParaPolInternacoes;
	}

	public void setVoltarParaPolInternacoes(boolean voltarParaPolInternacoes) {
		this.voltarParaPolInternacoes = voltarParaPolInternacoes;
	}

	public List<OrdemDeAdministracaoVO> getFiltredOrdemDeAdminVOList() {
		return filtredOrdemDeAdminVOList;
	}

	public void setFiltredOrdemDeAdminVOList(
			List<OrdemDeAdministracaoVO> filtredOrdemDeAdminVOList) {
		this.filtredOrdemDeAdminVOList = filtredOrdemDeAdminVOList;
	}

	public McoRecemNascidos getSelecionadoRecemNasc() {
		return selecionadoRecemNasc;
	}

	public void setSelecionadoRecemNasc(McoRecemNascidos selecionadoRecemNasc) {
		this.selecionadoRecemNasc = selecionadoRecemNasc;
	}

	public McoRecemNascidos getSelecionadoExameFisicoRecemNascido() {
		return selecionadoExameFisicoRecemNascido;
	}

	public void setSelecionadoExameFisicoRecemNascido(
			McoRecemNascidos selecionadoExameFisicoRecemNascido) {
		this.selecionadoExameFisicoRecemNascido = selecionadoExameFisicoRecemNascido;
	}

	public BigDecimal getEnfermagemAtivo() {
		return enfermagemAtivo;
	}

	public void setEnfermagemAtivo(BigDecimal enfermagemAtivo) {
		this.enfermagemAtivo = enfermagemAtivo;
	}

	public Integer getDia() {
		return dia;
	}

	public void setDia(Integer dia) {
		this.dia = dia;
	}

	public Integer getMes() {
		return mes;
	}

	public void setMes(Integer mes) {
		this.mes = mes;
	}

	public Integer getAno() {
		return ano;
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}

	public Integer getSeqAtendimento() {
		return seqAtendimento;
	}

	public void setSeqAtendimento(Integer seqAtendimento) {
		this.seqAtendimento = seqAtendimento;
	}

	public Long getSeqAnamnese() {
		return seqAnamnese;
	}

}