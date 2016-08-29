package br.gov.mec.aghu.paciente.prontuarioonline.action;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.action.RelatorioAnaEvoInternacaoController;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.blococirurgico.procdiagterap.business.IBlocoCirurgicoProcDiagTerapFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.certificacaodigital.action.VisualizarDocumentoController;
import br.gov.mec.aghu.certificacaodigital.business.ICertificacaoDigitalFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.SecurityController;
import br.gov.mec.aghu.core.business.SelectionQualifier;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioSituacaoCirurgia;
import br.gov.mec.aghu.dominio.DominioSituacaoDescricao;
import br.gov.mec.aghu.dominio.DominioSituacaoPrescricao;
import br.gov.mec.aghu.dominio.DominioSituacaoVersaoDocumento;
import br.gov.mec.aghu.dominio.DominioTipoDocumento;
import br.gov.mec.aghu.exames.action.ConsultarResultadosNotaAdicionalController;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghVersaoDocumento;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.model.MbcDescricaoCirurgica;
import br.gov.mec.aghu.model.MbcDescricaoCirurgicaId;
import br.gov.mec.aghu.model.MbcExtratoCirurgia;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.model.PdtDescricao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.prontuarioonline.action.ConsultarInternacoesPOLController.EnumTargetConsultaInternacoesPOL;
import br.gov.mec.aghu.paciente.prontuarioonline.business.IProntuarioOnlineFacade;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.InternacaoVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.NodoPOLVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.ProcedimentosImagemPOLVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.ProcedimentosPOLVO;
import br.gov.mec.aghu.prescricaomedica.action.RelatorioPrescricaoMedicaController;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.constantes.EnumTipoImpressao;
import br.gov.mec.aghu.prescricaomedica.vo.PrescricaoMedicaVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;

import com.sun.media.jai.codec.FileSeekableStream;
import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageDecoder;
import com.sun.media.jai.codec.SeekableStream;

@SuppressWarnings("PMD.AghuTooManyMethods")
public class ProcedimentosPOLController extends ActionController {
	
	private static final long serialVersionUID = -8764119058026431909L;
	private static final Log LOG = LogFactory.getLog(ProcedimentosPOLController.class);
	private static final String RELATORIO_ATOS_ANESTESICOS_PDF = "relatorioAtosAnestesicosPdf";
	private static final String RELATORIO_DESCRICAO_CIRURGIA_PDF = "relatorioDescricaoCirurgiaPdf";
	private static final String RELATORIO_PDT_DESC_PROC_CIRURGIA_PDF = "relatorioPdtDescProcCirurgiaPdf";
	private static final String DOCUMENTO_ASSINADO = "certificacaodigital-visualizarDocumentoAssinadoPOL";
	private static final String POL_PROCEDIMENTO = "pol-procedimento";
	private static final String CONSULTAR_RESULTADO_NOTA_ADICIONAL = "exames-consultarResultadoNotaAdicional";
	private static final String CONSULTAR_PRESCRICAO_MEDICA = "pol-consultarPrescricaoMedica";
	private static final String CINZA = "cinza";
	private static final String RELATORIO_PROCEDIMENTOS_POL = "ambulatorio-relatorioAmamneseEvolucaoInternacao";
	private static final String RELATORIO_PLANEJAMENTO_CIRURGICO_PDF = "relatorioPlanejamentoCirurgicoPdf";
	private static final String RELATORIO_PRESCRICAO_MEDICA_AMBULATORIAL_PDF = "relatorioPrescricaoMedicaAmbulatorialPdf";
	private static final String MBCR_DESCR_CIRURGICA = "MBCR_DESCR_CIRURGICA";
	private static final String PDTR_DESCRICAO = "PDTR_DESCRICAO";
	private static final File DIR_RESOURCES;
	
	static {
		DIR_RESOURCES = new File(FacesContext.getCurrentInstance().getExternalContext().getRealPath("/resources/")+"/temp_imagens");
		if(!DIR_RESOURCES.exists()){
			DIR_RESOURCES.mkdir();
		}
	}
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;

	@EJB
	private ICertificacaoDigitalFacade certificacaoDigitalFacade;
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@EJB
	private IProntuarioOnlineFacade prontuarioOnlineFacade;

	@EJB
	private IInternacaoFacade internacaoFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;

	@EJB
	private IBlocoCirurgicoProcDiagTerapFacade blocoCirurgicoProcDiagTerapFacade;
	
	@Inject
	private SecurityController securityController;
	
	@Inject
	private ConsultarResultadosNotaAdicionalController consultarResultadosNotaAdicionalController;
	
	@Inject
	private RelatorioAtosAnestesicosController relatorioAtosAnestesicosController;
	
	@Inject
	private VisualizarDocumentoController visualizarDocumentoController;

	@Inject
	private RelatorioPrescricaoMedicaController relatorioPrescricaoMedicaController; 
	
	@Inject
	private RelatorioAnaEvoInternacaoController relatorioAnaEvoInternacaoController;
	private MbcAgendas agendaCirurgia;
	private String origem;	
	private Integer seqProcedimento;
	private DominioSituacaoCirurgia dominioSituacaoCirurgia;
	private List<ProcedimentosPOLVO> procedimentos;
	private ProcedimentosPOLVO registroSelecionado;
	private Boolean renderizaBotaoMotivoCancelComModal;
	private MbcExtratoCirurgia extratoCirurgia;
	private Integer seqVersaoDoc;
	private Boolean botaoPlanejamentoCirurgico;
	private Boolean botaoImagem;
	private Boolean botaoAtoAnestesico;
	private Boolean botaoExameAnatomopatologico;
	private Boolean botaoControles;
	private boolean habilitaBotaoControle = false;
	private Date dataInicial;
	private Date dataFinal;
	private String botaoDescricaoNomeRelatorio; 
	private Integer botaoDescricaoSeqCirurgia;
	private List<PdtDescricao> botaoDescricaoListaDescricao;
	private PdtDescricao botaoDescricaoDescricaoSelecionada;
	private List<MbcDescricaoCirurgica> botaoDescricaoListaDescricaoCirurgica;
	private MbcDescricaoCirurgica botaoDescricaoDescricaoCirurgicaSelecionada; 
	private Boolean botaoDescricaoPerfilAdm07;
	private Boolean apresentaBotaoDescricaoUnicoDescricao; 
	private Boolean apresentaBotaoDescricaoModalDescricao; 
	private Boolean apresentaBotaoDescricaoUnicoDescricaoCirurgias; 
	private Boolean apresentaBotaoDescricaoModalDescricaoCirurgias;
	private Boolean apresentaBotaoDescricaoDesabilitado; 
	private InternacaoVO internacao = new InternacaoVO();
	private Long seqMbcFichaAnestesia;
	private String vSessao;
	private boolean voltarParaPolProced = Boolean.TRUE;
	private Integer codPaciente;
	//16639
	private List<ProcedimentosImagemPOLVO> listaImagem;
	private String impaxUrl = "";
	private boolean exibirModalImpax;
	private boolean exibirModalSlideshow;
	private Boolean acessoAdminPOL;
	private Boolean certificacaoHuAtiva;
	private List<MpmPrescricaoMedica> prescricaoesMedicas;
	private List<AghVersaoDocumento> prescrCertific;
	@Inject @SelectionQualifier @RequestScoped
	private NodoPOLVO itemPOL;
	private Integer prmverImgImpax;
	private String prmEndWebImagens;
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
		acessoAdminPOL = securityController.usuarioTemPermissao("acessoAdminPOL", "acessar");
		habilitaBotaoControle = securityController.usuarioTemPermissao("pesquisarRegistrosControlePaciente", "pesquisar");
		try {
			prmverImgImpax = parametroFacade.buscarValorInteiro(AghuParametrosEnum.P_AGHU_VER_IMAGENS_IMPAX);
			prmEndWebImagens = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_ENDERECO_WEB_IMAGENS);
			if(certificacaoHuAtiva == null){
				try{
					certificacaoHuAtiva = "S".equals(parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_CERTIFICACAO_DIGITAL));
				}catch (ApplicationBusinessException e) {
					apresentarExcecaoNegocio(e);
		}
	}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void inicio() {
		// Quando voltar de uma tela, que foi chamada a partir desta, não deve alterar o registro selecionado.
		if(registroSelecionado == null){
		this.exibirModalSlideshow = false;
		setBotoesDescricaoFalse();
		apresentaBotaoDescricaoDesabilitado = Boolean.TRUE; 
		limparSituacaoBotoes();		
		//Limpa situação das váriaves da controller
		procedimentos = null;
		setExibirModalImpax(false);
		setExibirModalSlideshow(false);
		registroSelecionado = new ProcedimentosPOLVO();		
		//MONTAR LISTAGEM SLIDESHOW
		setListaImagem(new ArrayList<ProcedimentosImagemPOLVO>());
			pesquisar();
			}
		Date today = DateUtil.truncaData(new Date());
		this.setDataFinal(today);
		this.setDataInicial(DateUtil.adicionaDias(today, -14));
	}
	//se usuario tiver a permissao acessoAdminPOL, btn desabilitado
	public Boolean getHabilitarBtnSeAcessoAdminPOL() {
		return acessoAdminPOL;
	}
	
	public Boolean getUsuarioAdministrativo(){
		if(acessoAdminPOL){
			return Boolean.TRUE;
		}else{
			return Boolean.FALSE;
		}
	}
	
	private void limparSituacaoBotoes() {
		botaoAtoAnestesico = Boolean.FALSE;		
		botaoPlanejamentoCirurgico = Boolean.FALSE;
		botaoExameAnatomopatologico = Boolean.FALSE;
		botaoImagem = Boolean.FALSE;
		botaoControles = Boolean.FALSE;
		processarListaPrescricaoMedicas(null, certificacaoHuAtiva, null);
	}
	
	public void exibirMsgErroSemParametroMotDesmarcar(){
		this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_PARAMETRO_MOT_DESMARCAR_NAO_CONFIGURADO");
	}
	
	public void pesquisar() {
		try{
			procedimentos = prontuarioOnlineFacade.pesquisarProcedimentosPOL((Integer)itemPOL.getParametros().get(NodoPOLVO.COD_PACIENTE));
			if ((procedimentos != null) && !(procedimentos.isEmpty())){
				renderizaBotaoMotivoCancelComModal = Boolean.TRUE;
				selecionarPrimeiroRegistroDataTable();
			}
		}catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	private void selecionarPrimeiroRegistroDataTable() {
		registroSelecionado = procedimentos.get(0);
		selecionaRegistro();
			registroSelecionado.setAssinalaRadio(Boolean.TRUE);
		}

	public void selecionaRegistro(){		
		this.internacao = new InternacaoVO();
		limparSituacaoBotoes();
		Boolean botaoDescricao = prontuarioOnlineFacade.habilitarBotaoDescricao(registroSelecionado);
		processarBotaoPlanejamentoCirurgico(registroSelecionado);
		try {
			if (botaoDescricao){
				botaoDescricaoSeqCirurgia = registroSelecionado.getSeq(); 
				Object[] relatorioParametros = prontuarioOnlineFacade.getRelatorioProcedimentosCirurgiaPol(botaoDescricaoSeqCirurgia);
				setBotoesDescricaoFalse(); 
				apresentaBotaoDescricaoDesabilitado = Boolean.TRUE;
				if (relatorioParametros != null) {
					botaoDescricaoNomeRelatorio = (String) relatorioParametros[0];  
					botaoDescricaoPerfilAdm07 = (Boolean) relatorioParametros[2];
					setBotoesDescricaoFalse();
					if (botaoDescricaoNomeRelatorio.equals(MBCR_DESCR_CIRURGICA)) {
						botaoDescricaoListaDescricaoCirurgica = (List<MbcDescricaoCirurgica>) relatorioParametros[1];
						if (botaoDescricaoListaDescricaoCirurgica != null && botaoDescricaoListaDescricaoCirurgica.size() > 1) {
							apresentaBotaoDescricaoModalDescricaoCirurgias = Boolean.TRUE;
						} else {
							apresentaBotaoDescricaoUnicoDescricaoCirurgias = Boolean.TRUE;
							botaoDescricaoDescricaoCirurgicaSelecionada = botaoDescricaoListaDescricaoCirurgica.get(0);
						}
					} else if (botaoDescricaoNomeRelatorio.equals(PDTR_DESCRICAO)) {
						botaoDescricaoListaDescricao = (List<PdtDescricao>) relatorioParametros[1];
						
						if (botaoDescricaoListaDescricao != null && botaoDescricaoListaDescricao.size() > 1) {
							apresentaBotaoDescricaoModalDescricao = Boolean.TRUE;
						} else {
							apresentaBotaoDescricaoUnicoDescricao = Boolean.TRUE;
							botaoDescricaoDescricaoSelecionada = botaoDescricaoListaDescricao.get(0);
						}
					} else {
						setBotoesDescricaoFalse(); 
						apresentaBotaoDescricaoDesabilitado = Boolean.TRUE; 
					}
				} 
			} else {
				setBotoesDescricaoFalse(); 
				apresentaBotaoDescricaoDesabilitado = Boolean.TRUE; 
			}
			
			Integer verificaImagem = obterVerificaImagem(registroSelecionado);
			
			if(prmverImgImpax == 1){
				botaoImagem = prontuarioOnlineFacade.habilitarBotaoImagemProc(registroSelecionado, verificaImagem);
			}
			botaoAtoAnestesico = prontuarioOnlineFacade.habilitarBotaoAtoAnestesicoCirurgiasPol(registroSelecionado.getSeq());
			botaoExameAnatomopatologico = prontuarioOnlineFacade.habilitarBotaoExameAnatopatologicoProc(registroSelecionado);
			botaoControles = (registroSelecionado.getAtdSeq() != null ? 
											internacaoFacade.habilitarDadosControle((Integer)itemPOL.getParametros().get(NodoPOLVO.COD_PACIENTE),
																					 registroSelecionado.getAtdSeq()
																				   ) && 
								            securityController.usuarioTemPermissao("pesquisarRegistrosControlePaciente", "pesquisar") : false);
			if (botaoControles) {
				setaValoresControles();
			}
			processarListaPrescricaoMedicas(registroSelecionado.getAtdSeq(), certificacaoHuAtiva, "paciente-procedimentosListPOL");
			redirecionarParaExibirModalImagem(verificaImagem);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} 
	}

	private Integer obterVerificaImagem(ProcedimentosPOLVO registroSelecionado) {
		Integer verificaImagem = null;
		if(registroSelecionado != null && registroSelecionado.getSeq() != null){
			DominioSituacaoDescricao[] situacao = {DominioSituacaoDescricao.PRE, DominioSituacaoDescricao.DEF};
			List<PdtDescricao> pdtDescricao = blocoCirurgicoProcDiagTerapFacade.listarDescricaoPorSeqCirurgiaSituacao(registroSelecionado.getSeq(), situacao,
																													  PdtDescricao.Fields.SEQ);
			if(!pdtDescricao.isEmpty()){
				verificaImagem = pdtDescricao.get(0).getSeq();
			}
		}
		return verificaImagem;
	}

	private void setaValoresControles(){
		
		this.internacao.setAtdSeq(registroSelecionado.getAtdSeq());
		this.internacao.setCodigoPaciente(registroSelecionado.getPacCodigo());
		AghAtendimentos atendimento = aghuFacade.obterAghAtendimentosComInternacaoEAtendimentoUrgencia(registroSelecionado.getAtdSeq());
		if (atendimento.getOrigem() == DominioOrigemAtendimento.I){
			if (atendimento.getInternacao()!=null && atendimento.getInternacao().getAtendimentoUrgencia()!=null){
				this.internacao.setDthrInicio(atendimento.getInternacao().getAtendimentoUrgencia().getDtAtendimento());
				this.internacao.setDthrFim(atendimento.getInternacao().getAtendimentoUrgencia().getDtAltaAtendimento());
			}
			if (atendimento.getInternacao()!=null && atendimento.getInternacao().getAtendimentoUrgencia()==null){
				this.internacao.setDthrInicio(atendimento.getInternacao().getDthrInternacao());
				this.internacao.setDthrFim(atendimento.getInternacao().getDthrAltaMedica());
			}
		}
		if (atendimento.getOrigem() == DominioOrigemAtendimento.C){
			Date auxDateIni = DateUtils.truncate(atendimento.getDthrInicio(), Calendar.DAY_OF_MONTH); 
			auxDateIni = DateUtils.addDays(auxDateIni, -1); 
			this.internacao.setDthrInicio(auxDateIni);  
			if(atendimento.getDthrFim()!=null){
				this.internacao.setDthrFim(atendimento.getDthrFim());
			}
			else{
				this.internacao.setDthrFim(new Date());
				if (CoreUtil.diferencaEntreDatasEmDias(internacao.getDthrFim(), internacao.getDthrInicio())> 15){
					Date auxDateFim = DateUtils.truncate(this.internacao.getDthrInicio(), Calendar.DAY_OF_MONTH); 
					auxDateFim = DateUtils.addDays(auxDateFim, 15); 
					this.internacao.setDthrFim(auxDateFim);
			    }	
		   }
		}
	}

	public Boolean habilitarBotaoAnanmese() {
		Boolean habilitar = Boolean.FALSE;
		if (registroSelecionado != null) {
			habilitar = ambulatorioFacade
					.existeDadosImprimirRelatorioAnamneses(registroSelecionado.getAtdSeq());
		}
		return habilitar && (getUsuarioAdministrativo() == false);
	}
	
	public String pesquisarAnamnese() {
		relatorioAnaEvoInternacaoController.setTipoRelatorio(EnumTargetConsultaInternacoesPOL.RELATORIO_ANAMNESES.toString());
		gerarRelatorioAnaEvoInternacao(); 
		if (relatorioAnaEvoInternacaoController.getDadosRelatorioIsNotEmpty()) {
			return "ambulatorio-relatorioAmamneseEvolucaoInternacao";
		}
		return null;
	}

	private void gerarRelatorioAnaEvoInternacao() {	
		relatorioAnaEvoInternacaoController.setOrigem(POL_PROCEDIMENTO);
		relatorioAnaEvoInternacaoController.setAtdSeq(registroSelecionado.getAtdSeq());		
		relatorioAnaEvoInternacaoController.gerarDados();	

	}		
	
	public Boolean habilitarBotaoEvolucao() {
		Boolean habilitar = Boolean.FALSE;
		if (registroSelecionado != null) {
			habilitar = ambulatorioFacade
					.existeDadosImprimirRelatorioEvolucao(registroSelecionado.getAtdSeq());
		}
		return habilitar && (getUsuarioAdministrativo() == false);
	}
	
	
	
	
	public void pesquisarEvolucaoTodos() {
		RapServidores servidorLogado = null;
		try {
			// Seta sessão para 12 horas
			pacienteFacade.commit(60 * 60 * 12);
			
			servidorLogado = registroColaboradorFacade
					.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado(),
							new Date());

			relatorioAnaEvoInternacaoController.imprimirRelatorioEvolucaoTodos(
					registroSelecionado.getAtdSeq(), servidorLogado,
					this.getEnderecoIPv4HostRemoto());

			apresentarMsgNegocio(Severity.INFO,
					"MENSAGEM_SUCESSO_IMPRESSAO_RELATORIO", "Evoluções");
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} catch (UnknownHostException e) {
			LOG.error(e.getMessage());
		}
	}
	
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
		
		if (DateUtil.calcularDiasEntreDatas(getDataInicial(), getDataFinal()) > 15){
			this.apresentarMsgNegocio(Severity.ERROR ,"RELATORIO_EVOLUCAO_PERIODO_MAIOR_15_DIAS");
			return null;			
		}
		relatorioAnaEvoInternacaoController.setDataInicial(getDataInicial());
		relatorioAnaEvoInternacaoController.setDataFinal(getDataFinal());
		relatorioAnaEvoInternacaoController.setTipoRelatorio(EnumTargetConsultaInternacoesPOL.RELATORIO_EVOLUCAO_PERIODO.toString());
		
		gerarRelatorioAnaEvoInternacao();

		if (relatorioAnaEvoInternacaoController.getDadosRelatorioIsNotEmpty()) {
			retorno = RELATORIO_PROCEDIMENTOS_POL;
		}
			
		 return retorno;
	}
	
	public String exibeRelatorioPlanejamentoCirurgico() {
		try {
			Boolean docAssinado = prontuarioOnlineFacade.verificarSeDocumentoPlanejamentoCirugicoAssinado(registroSelecionado.getSeq());
			if (docAssinado) {
				seqVersaoDoc = prontuarioOnlineFacade.chamarDocCertifPlanejamentoCirurg(registroSelecionado.getSeq());
				if (seqVersaoDoc != null) {
					return "documentoAssinado_planejamentoCirurgico"; //TODO vai para onde????? Não encontrado local correspondente no 5
				}
			} else {
				return RELATORIO_PLANEJAMENTO_CIRURGICO_PDF;
			}

		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}

		return null;
	}

	private void processarBotaoPlanejamentoCirurgico(
			ProcedimentosPOLVO registroSelecionado) {
		try {
			agendaCirurgia = prontuarioOnlineFacade.obtemAgendaPlanejamentoCirurgico(registroSelecionado.getSeq());
			botaoPlanejamentoCirurgico = agendaCirurgia != null;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
//termina aqui
	private void setBotoesDescricaoFalse() {
		apresentaBotaoDescricaoUnicoDescricao = Boolean.FALSE; 
		apresentaBotaoDescricaoModalDescricao = Boolean.FALSE;
		apresentaBotaoDescricaoUnicoDescricaoCirurgias = Boolean.FALSE; 
		apresentaBotaoDescricaoModalDescricaoCirurgias = Boolean.FALSE;
		apresentaBotaoDescricaoDesabilitado = Boolean.FALSE; 
	}
	
	public void limpaRegistrosSelecionados(){
		botaoDescricaoDescricaoSelecionada = new PdtDescricao();
		botaoDescricaoDescricaoCirurgicaSelecionada = new MbcDescricaoCirurgica();
		botaoDescricaoDescricaoCirurgicaSelecionada.setId(new MbcDescricaoCirurgicaId());
	}
	
	public void exibirMsgFuncionalidadeNaoImplementada() {
		this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_FUNCIONALIDADE_NAO_IMPLEMENTADA");
	}
	
	public String verificarSeDocumentoAtoAnestesicoAssinado() {
		try {
			Boolean docAssinado = prontuarioOnlineFacade.verificarSeDocumentoAtoAnestesicoAssinado(registroSelecionado.getSeq());
			if (docAssinado) {
				seqVersaoDoc = prontuarioOnlineFacade.chamarDocCertifFicha(registroSelecionado.getSeq());
				if (seqVersaoDoc != null) {
					visualizarDocumentoController.setSeqAghVersaoDocumento(seqVersaoDoc);
					visualizarDocumentoController.setOrigem(POL_PROCEDIMENTO);
					return DOCUMENTO_ASSINADO;
				}
			} else {
				String idSessao = ((HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(false)).getId();
				Object[] relAtoAnest = prontuarioOnlineFacade.processarRelatorioAtosAnestesicos(registroSelecionado.getSeq(),null, null,idSessao);
				seqMbcFichaAnestesia = (Long) relAtoAnest[0];
				vSessao = (String) relAtoAnest[1];
				botaoDescricaoPerfilAdm07 = (Boolean) relAtoAnest[2];
				relatorioAtosAnestesicosController.setVoltarPara(POL_PROCEDIMENTO);
				relatorioAtosAnestesicosController.setvSessao(vSessao);
				relatorioAtosAnestesicosController.setSeqMbcFichaAnestesia(seqMbcFichaAnestesia);
				relatorioAtosAnestesicosController.setPermiteImpressao(botaoDescricaoPerfilAdm07);
				
				return RELATORIO_ATOS_ANESTESICOS_PDF;
			}
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		
		return null;
	}

	public String obterCorLinhaDescricao(PdtDescricao registro){
		if (botaoDescricaoDescricaoSelecionada != null 
				&& registro != null
				&& botaoDescricaoDescricaoSelecionada.getSeq() != null
				&& botaoDescricaoDescricaoSelecionada.getSeq().equals(registro.getSeq())) {
			return CINZA;
		}else{
			return "";
		}
	}
	
	public String obterCorLinhaDescricaoCirurgica(MbcDescricaoCirurgica registro){
		if (botaoDescricaoDescricaoCirurgicaSelecionada != null 
				&& registro != null
				&& botaoDescricaoDescricaoCirurgicaSelecionada.getId().getSeqp() != null
				&& botaoDescricaoDescricaoCirurgicaSelecionada.getId().getSeqp().equals(registro.getId().getSeqp())) {
			return CINZA;
		}else{
			return "";
		}
	}
	
	public String buscarDescricao() {
		return RELATORIO_DESCRICAO_CIRURGIA_PDF;
	}

	public String exibeRelatorioMbcrDescricaoModal() {
		return RELATORIO_DESCRICAO_CIRURGIA_PDF;
	}
	
	public String buscarDescricaoProcedimento() {
		return RELATORIO_PDT_DESC_PROC_CIRURGIA_PDF;
	}
	
	public String exibeRelatorioPdtrDescricaoModal() {
		return RELATORIO_PDT_DESC_PROC_CIRURGIA_PDF;
	}
	
	public String redirecionarParaVerResultadoDeExame() {
		
		try {
			// itensResultadoImpressaoVO = prontuarioOnlineFacade.insereResultadosPorCirurgia(registroSelecionado.getSeq(), 0, Boolean.FALSE);
			consultarResultadosNotaAdicionalController
					.setSolicitacoes(prontuarioOnlineFacade.processarResultadosNotasAdicionaisPorCirurgia(registroSelecionado.getSeq(), 0,	Boolean.FALSE));
			consultarResultadosNotaAdicionalController.setVoltarPara(POL_PROCEDIMENTO);
			consultarResultadosNotaAdicionalController.setOrigemProntuarioOnline(Boolean.TRUE);
			return CONSULTAR_RESULTADO_NOTA_ADICIONAL;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}	

		return null;

	}
	
	public void processarListaPrescricaoMedicas(Integer atdSeq, Boolean certifAtiva, String pagOrigem) {
		prescricaoesMedicas = null;
		prescrCertific = null;
		this.origem = pagOrigem;
		if(atdSeq != null){
			if(certifAtiva){
				prescrCertific = certificacaoDigitalFacade.pesquisarAghVersaoDocumentoPorAtendimento(atdSeq, DominioTipoDocumento.PM, null, DominioSituacaoVersaoDocumento.P, DominioSituacaoVersaoDocumento.A);
			}else{
				prescricaoesMedicas = prescricaoMedicaFacade.pesquisarPrescricoesMedicaNaoPendentes(atdSeq, null);
				CoreUtil.ordenarLista(prescricaoesMedicas, "criadoEm", false);
			}
		}
	}
	
	public String redirecionarParaPrescCertif(Integer atdSeq, Integer seqDocCertificado) {
		return CONSULTAR_PRESCRICAO_MEDICA;
	}
	
	private Integer prmAtdSeq;
	private Integer prmSeqPrescricao;

	/**
	 * Método utilizado em várias páginas
	 */
	
	public String redirecionarParaVerPrescricaoMedica(AghAtendimentos atendimento) {
		if(!DominioOrigemAtendimento.getOrigensDePrescricaoAmbulatorial().contains(atendimento.getOrigem())){
			apresentarMsgNegocio(Severity.INFO, "ATENDIMENTO_NAO_E_DE_ORIGEM_AMBULATORIAL");
			return null;
		}
		
		relatorioPrescricaoMedicaController.setTipoImpressao(EnumTipoImpressao.SEM_IMPRESSAO);
		relatorioPrescricaoMedicaController.setDataMovimento(new Date());
		relatorioPrescricaoMedicaController.setServidorValido(null);
		relatorioPrescricaoMedicaController.setVoltarPara("pol-ambulatorio");
		
		List<MpmPrescricaoMedica> prescricoes = prescricaoMedicaFacade.pesquisarPrescricoesMedicaNaoPendentes(atendimento.getSeq(), DominioSituacaoPrescricao.L);
		relatorioPrescricaoMedicaController.setPrescricoesMedicas(prescricoes);
		
		PrescricaoMedicaVO prescricaoMedicaVO = new PrescricaoMedicaVO();
		prescricaoMedicaVO.setPrescricaoMedica(prescricoes.get(0));
		relatorioPrescricaoMedicaController.setPrescricaoMedicaVO(prescricaoMedicaVO);
		
		return RELATORIO_PRESCRICAO_MEDICA_AMBULATORIAL_PDF;
	}
	
	public String redirecionarParaVisualizarPrescricaoMedica() {
		relatorioPrescricaoMedicaController.setTipoImpressao(EnumTipoImpressao.SEM_IMPRESSAO);
		relatorioPrescricaoMedicaController.setDataMovimento(new Date());
		relatorioPrescricaoMedicaController.setServidorValido(null);
	//	Contexts.getSessionContext().set("pagOrigem",	origem);
		MpmPrescricaoMedica prescricao = prescricaoMedicaFacade.obterPrescricaoPorId(prmAtdSeq, prmSeqPrescricao);
		List<MpmPrescricaoMedica> prescricoes = Arrays.asList(prescricao);//prescricaoMedicaFacade.pesquisarPrescricoesMedicaNaoPendentes(atdSeq, null); 
		relatorioPrescricaoMedicaController.setPrescricoesMedicas(prescricoes);
		PrescricaoMedicaVO prescricaoMedicaVO = new PrescricaoMedicaVO();
		prescricaoMedicaVO.setPrescricaoMedica(prescricao);
		relatorioPrescricaoMedicaController.setPrescricaoMedicaVO(prescricaoMedicaVO);
		return RELATORIO_PRESCRICAO_MEDICA_AMBULATORIAL_PDF;
	}
	
	public void buscarMotivoCancelamento() { 
		extratoCirurgia = prontuarioOnlineFacade.buscarMotivoCancelCirurgia(registroSelecionado.getSeq());	
		registroSelecionado.setMotivoCancelamento(extratoCirurgia.getDescricaoMotivoCancelamentoEditado());
	}	
			
	public DominioSituacaoCirurgia[] getDominioSituacaoCirurgiaLista(){
		return this.dominioSituacaoCirurgia.values();
	}

	private void redirecionarParaExibirModalImagem(Integer verificaImagem) {
		if(prontuarioOnlineFacade.verificaSeExisteImagem(verificaImagem)) {
			setListaImagem(prontuarioOnlineFacade.montarListaImagens(registroSelecionado.getSeq()));
			for(ProcedimentosImagemPOLVO pvo : listaImagem){
				try {
					geraImagem(pvo);
				} catch (IOException e) {
					apresentarMsgNegocio("Erro ao processar imagem.");
				}
	}
			setExibirModalImpax(false);
			setExibirModalSlideshow(true);

		} else {
			if(prmverImgImpax == 1){
				String accessiom = registroSelecionado.getPacOruAccNummer();
				String url = prmEndWebImagens + registroSelecionado.getPacCodigo() + "%26accession%3D" + accessiom;
				setImpaxUrl(url);
			}else{
				setBotaoImagem(Boolean.FALSE);
			}
			setListaImagem(new ArrayList<ProcedimentosImagemPOLVO>());
			setExibirModalImpax(true);
			setExibirModalSlideshow(false);
		}
	}

	public static void geraImagem(ProcedimentosImagemPOLVO pvo) throws IOException {
		File imagem = new File(DIR_RESOURCES, pvo.getNomeImagem()+".jpeg");
		if(!imagem.exists()){
			File arquivo = new File(DIR_RESOURCES, pvo.getNomeImagem()+".tif");
			FileUtils.writeByteArrayToFile(arquivo, pvo.getImagem());
			SeekableStream s = new FileSeekableStream(arquivo);
			ImageDecoder dec = ImageCodec.createImageDecoder("tiff", s, null);
			RenderedImage op = dec.decodeAsRenderedImage(0);
			FileOutputStream fos = new FileOutputStream(imagem);
			ImageIO.write(op, "jpeg", fos);
			fos.close();
		}
	}
	// Getters e Setters
	public String getOrigem() {
		return origem;
	}
	public void setOrigem(String origem) {
		this.origem = origem;
	}
	public ProcedimentosPOLVO getRegistroSelecionado() {
		return registroSelecionado;
	}
	public void setRegistroSelecionado(ProcedimentosPOLVO registroSelecionado) {
		this.registroSelecionado = registroSelecionado;
	}
	public DominioSituacaoCirurgia getDominioSituacaoCirurgia() {
		return dominioSituacaoCirurgia;
	}
	public void setDominioSituacaoCirurgia(
			DominioSituacaoCirurgia dominioSituacaoCirurgia) {
		this.dominioSituacaoCirurgia = dominioSituacaoCirurgia;
	}
	public Boolean getRenderizaBotaoMotivoCancelComModal() {
		return renderizaBotaoMotivoCancelComModal;
	}
	public void setRenderizaBotaoMotivoCancelComModal(
			Boolean renderizaBotaoMotivoCancelComModal) {
		this.renderizaBotaoMotivoCancelComModal = renderizaBotaoMotivoCancelComModal;
	}
	public Integer getSeqProcedimento() {
		return seqProcedimento;
	}
	public void setSeqProcedimento(Integer seqProcedimento) {
		this.seqProcedimento = seqProcedimento;
	}
	public List<ProcedimentosPOLVO> getProcedimentos() {
		return procedimentos;
	}
	public void setProcedimentos(List<ProcedimentosPOLVO> procedimentos) {
		this.procedimentos = procedimentos;
	}
	public String getBotaoDescricaoNomeRelatorio() {
		return botaoDescricaoNomeRelatorio;
	}
	public void setBotaoDescricaoNomeRelatorio(
			String botaoDescricaoNomeRelatorio) {
		this.botaoDescricaoNomeRelatorio = botaoDescricaoNomeRelatorio;
	}
	public Integer getBotaoDescricaoSeqCirurgia() {
		return botaoDescricaoSeqCirurgia;
	}
	public void setBotaoDescricaoSeqCirurgia(Integer botaoDescricaoSeqCirurgia) {
		this.botaoDescricaoSeqCirurgia = botaoDescricaoSeqCirurgia;
	}
	public List<PdtDescricao> getBotaoDescricaoListaDescricao() {
		return botaoDescricaoListaDescricao;
	}
	public void setBotaoDescricaoListaDescricao(
			List<PdtDescricao> botaoDescricaoListaDescricao) {
		this.botaoDescricaoListaDescricao = botaoDescricaoListaDescricao;
	}
	public PdtDescricao getBotaoDescricaoDescricaoSelecionada() {
		return botaoDescricaoDescricaoSelecionada;
	}
	public void setBotaoDescricaoDescricaoSelecionada(
			PdtDescricao botaoDescricaoDescricaoSelecionada) {
		this.botaoDescricaoDescricaoSelecionada = botaoDescricaoDescricaoSelecionada;
	}
	public List<MbcDescricaoCirurgica> getBotaoDescricaoListaDescricaoCirurgica() {
		return botaoDescricaoListaDescricaoCirurgica;
	}
	public void setBotaoDescricaoListaDescricaoCirurgica(
			List<MbcDescricaoCirurgica> botaoDescricaoListaDescricaoCirurgica) {
		this.botaoDescricaoListaDescricaoCirurgica = botaoDescricaoListaDescricaoCirurgica;
	}
	public MbcDescricaoCirurgica getBotaoDescricaoDescricaoCirurgicaSelecionada() {
		return botaoDescricaoDescricaoCirurgicaSelecionada;
	}
	public void setBotaoDescricaoDescricaoCirurgicaSelecionada(
			MbcDescricaoCirurgica botaoDescricaoDescricaoCirurgicaSelecionada) {
		this.botaoDescricaoDescricaoCirurgicaSelecionada = botaoDescricaoDescricaoCirurgicaSelecionada;
	}
	public Boolean getBotaoDescricaoPerfilAdm07() {
		return botaoDescricaoPerfilAdm07;
	}
	public void setBotaoDescricaoPerfilAdm07(Boolean botaoDescricaoPerfilAdm07) {
		this.botaoDescricaoPerfilAdm07 = botaoDescricaoPerfilAdm07;
	}
	public Boolean getApresentaBotaoDescricaoUnicoDescricao() {
		return apresentaBotaoDescricaoUnicoDescricao;
	}
	public void setApresentaBotaoDescricaoUnicoDescricao(
			Boolean apresentaBotaoDescricaoUnicoDescricao) {
		this.apresentaBotaoDescricaoUnicoDescricao = apresentaBotaoDescricaoUnicoDescricao;
	}
	public Boolean getApresentaBotaoDescricaoModalDescricao() {
		return apresentaBotaoDescricaoModalDescricao;
	}
	public void setApresentaBotaoDescricaoModalDescricao(
			Boolean apresentaBotaoDescricaoModalDescricao) {
		this.apresentaBotaoDescricaoModalDescricao = apresentaBotaoDescricaoModalDescricao;
	}
	public Boolean getApresentaBotaoDescricaoUnicoDescricaoCirurgias() {
		return apresentaBotaoDescricaoUnicoDescricaoCirurgias;
	}
	public void setApresentaBotaoDescricaoUnicoDescricaoCirurgias(
			Boolean apresentaBotaoDescricaoUnicoDescricaoCirurgias) {
		this.apresentaBotaoDescricaoUnicoDescricaoCirurgias = apresentaBotaoDescricaoUnicoDescricaoCirurgias;
	}
	public Boolean getApresentaBotaoDescricaoModalDescricaoCirurgias() {
		return apresentaBotaoDescricaoModalDescricaoCirurgias;
	}
	public void setApresentaBotaoDescricaoModalDescricaoCirurgias(
			Boolean apresentaBotaoDescricaoModalDescricaoCirurgias) {
		this.apresentaBotaoDescricaoModalDescricaoCirurgias = apresentaBotaoDescricaoModalDescricaoCirurgias;
	}
	public Boolean getApresentaBotaoDescricaoDesabilitado() {
		return apresentaBotaoDescricaoDesabilitado;
	}
	public void setApresentaBotaoDescricaoDesabilitado(
			Boolean apresentaBotaoDescricaoDesabilitado) {
		this.apresentaBotaoDescricaoDesabilitado = apresentaBotaoDescricaoDesabilitado;
	}
	public Boolean getBotaoImagem() {
		return botaoImagem;
	}
	public void setBotaoImagem(Boolean botaoImagem) {
		this.botaoImagem = botaoImagem;
	}
	public Boolean getBotaoAtoAnestesico() {
		return botaoAtoAnestesico;
	}
	public void setBotaoAtoAnestesico(Boolean botaoAtoAnestesico) {
		this.botaoAtoAnestesico = botaoAtoAnestesico;
	}
	public Boolean getBotaoExameAnatomopatologico() {
		return botaoExameAnatomopatologico;
	}
	public void setBotaoExameAnatomopatologico(
			Boolean botaoExameAnatomopatologico) {
		this.botaoExameAnatomopatologico = botaoExameAnatomopatologico;
	}
	public MbcExtratoCirurgia getExtratoCirurgia() {
		return extratoCirurgia;
	}
	public void setExtratoCirurgia(MbcExtratoCirurgia extratoCirurgia) {
		this.extratoCirurgia = extratoCirurgia;
	}
	public Integer getSeqVersaoDoc() {
		return seqVersaoDoc;
	}
	public void setSeqVersaoDoc(Integer seqVersaoDoc) {
		this.seqVersaoDoc = seqVersaoDoc;
	}
	public Long getSeqMbcFichaAnestesia() {
		return seqMbcFichaAnestesia;
	}
	public String getvSessao() {
		return vSessao;
	}
	public void setSeqMbcFichaAnestesia(Long seqMbcFichaAnestesia) {
		this.seqMbcFichaAnestesia = seqMbcFichaAnestesia;
	}
	public void setvSessao(String vSessao) {
		this.vSessao = vSessao;
	}
	public String getImpaxUrl() {
		return impaxUrl;
	}
	public void setImpaxUrl(String impaxUrl) {
		this.impaxUrl = impaxUrl;
	}
	public boolean isExibirModalImpax() {
		return exibirModalImpax;
	}
	public void setExibirModalImpax(boolean exibirModalImpax) {
		this.exibirModalImpax = exibirModalImpax;
	}
	public List<ProcedimentosImagemPOLVO> getListaImagem() {
		return listaImagem;
	}
	public void setListaImagem(List<ProcedimentosImagemPOLVO> listaImagem) {
		this.listaImagem = listaImagem;
	}
	public List<ProcedimentosImagemPOLVO> metodoListaImagem() {
		return getListaImagem();
	}
	public boolean isExibirModalSlideshow() {
		return exibirModalSlideshow;
	}
	public void setExibirModalSlideshow(boolean exibirModalSlideshow) {
		this.exibirModalSlideshow = exibirModalSlideshow;
	}
	public Boolean getBotaoControles() {
		return botaoControles;
	}
	public Boolean getBotaoPlanejamentoCirurgico() {
		return botaoPlanejamentoCirurgico;
	}
	public void setBotaoPlanejamentoCirurgico(Boolean botaoPlanejamentoCirurgico) {
		this.botaoPlanejamentoCirurgico = botaoPlanejamentoCirurgico;
	}
	public void setBotaoControles(Boolean botaoControles) {
		this.botaoControles = botaoControles;
	}
	public InternacaoVO getInternacao() {
		return internacao;
	}
	public void setInternacao(InternacaoVO internacao) {
		this.internacao = internacao;
	}
	public boolean isVoltarParaPolProced() {
		return voltarParaPolProced;
	}
	public MbcAgendas getAgendaCirurgia() {
		return agendaCirurgia;
	}
	public void setAgendaCirurgia(MbcAgendas agendaCirurgia) {
		this.agendaCirurgia = agendaCirurgia;
	}
	public Integer getCodPaciente() {
		return codPaciente;
	}
	public void setCodPaciente(Integer codPaciente) {
		this.codPaciente = codPaciente;
	}
	public void setVoltarParaPolProced(boolean voltarParaPolProced) {
		this.voltarParaPolProced = voltarParaPolProced;
	}
	public boolean isHabilitaBotaoControle() {
		return habilitaBotaoControle;
	}
	public void setHabilitaBotaoControle(boolean habilitaBotaoControle) {
		this.habilitaBotaoControle = habilitaBotaoControle;
	}
	public Boolean getCertificacaoHuAtiva() {
		return certificacaoHuAtiva;
	}
	public List<MpmPrescricaoMedica> getPrescricaoesMedicas() {
		return prescricaoesMedicas;
	}
	public List<AghVersaoDocumento> getPrescrCertific() {
		return prescrCertific;
	}
	public void setCertificacaoHuAtiva(Boolean certificacaoHuAtiva) {
		this.certificacaoHuAtiva = certificacaoHuAtiva;
	}
	public void setPrescricaoesMedicas(
			List<MpmPrescricaoMedica> prescricaoesMedicas) {
		this.prescricaoesMedicas = prescricaoesMedicas;
	}
	public void setPrescrCertific(List<AghVersaoDocumento> prescrCertific) {
		this.prescrCertific = prescrCertific;
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
	public Integer getPrmverImgImpax() {
		return prmverImgImpax;
	}
	public void setPrmverImgImpax(Integer prmverImgImpax) {
		this.prmverImgImpax = prmverImgImpax;
	}
	public String getPrmEndWebImagens() {
		return prmEndWebImagens;
	}
	public void setPrmEndWebImagens(String prmEndWebImagens) {
		this.prmEndWebImagens = prmEndWebImagens;
	}
	public Integer getPrmAtdSeq() {
		return prmAtdSeq;
	}
	public void setPrmAtdSeq(Integer prmAtdSeq) {
		this.prmAtdSeq = prmAtdSeq;
	}
	public Integer getPrmSeqPrescricao() {
		return prmSeqPrescricao;
	}
	public void setPrmSeqPrescricao(Integer prmSeqPrescricao) {
		this.prmSeqPrescricao = prmSeqPrescricao;
	}
}