package br.gov.mec.aghu.paciente.prontuarioonline.action;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.governors.MaxPagesGovernorException;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.weld.context.ConversationContext;
import org.jboss.weld.context.http.Http;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.controlepaciente.business.IControlePacienteFacade;
import br.gov.mec.aghu.controlepaciente.cadastrosbasicos.business.ICadastrosBasicosControlePacienteFacade;
import br.gov.mec.aghu.controlepaciente.vo.RegistroControlePacienteVO;
import br.gov.mec.aghu.controlepaciente.vo.RelatorioRegistroControlePacienteVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.core.utils.AghuNumberFormat;
import br.gov.mec.aghu.dominio.DominioTipoGrupoControle;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.EcpItemControle;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.InternacaoVO;

import com.itextpdf.text.DocumentException;

@SuppressWarnings({ "PMD.AghuTooManyMethods" })
public class RelatorioRegistrosControlesPacienteController extends ActionReport {

	private static final Log LOG = LogFactory.getLog(RelatorioRegistrosControlesPacienteController.class);
	private static final String VISUALIZAR_REGISTROS = "controlepaciente-visualizarRegistros";
	private static final String CONSULTAR_INTERNACOES = "pol-internacao";
	private static final String LISTA_PACIENTES_TRANSFERENCIA = "internacao-listaPacientesTransferencia";
	private static final String RELATORIO_CONTROLE_PACIENTES = "paciente-relatorioControlesPaciente";
	private static final String CONSULTAR_DETALHE_INTERNACAO = "pol-detalheInternacao";
	private static final String POL_PROCEDIMENTO = "pol-procedimento";
	private static final String POL_CIRURGIA = "pol-cirurgia";
	private static final String POL_AMBULATORIO = "pol-ambulatorio";

	private static final long serialVersionUID = 5692050644724192311L;
	
	@Inject
	private SistemaImpressao sistemaImpressao;
	
	@Inject @Http 
	private ConversationContext conversationContext;

	private InternacaoVO internacao = new InternacaoVO();

	private String dataHoraInicioString = "";
	private String dataHoraFimString = "";

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@EJB
	private IInternacaoFacade internacaoFacade;

	@EJB
	private IControlePacienteFacade controlePacienteFacade;

	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private ICadastrosBasicosControlePacienteFacade cadastrosBasicosControlePacienteFacade;

	private boolean fromTransferenciaPaciente = false;
	private boolean fromVisualizarControles = false;
	private boolean fromFormsAgh = false;
	private boolean fromTransferenciaFormsAgh = false;
	private Integer internacaoSeq;
	private Integer atdSeq;
	private long dataInicialImpressaoInMillis;
	private long dataFinalImpressaoInMillis;

	private AghAtendimentos atendimentos = new AghAtendimentos();
	private Integer codigoPaciente;
	private AipPacientes paciente = new AipPacientes();
	private AinInternacao ainInternacao;
	
	private boolean voltarParaPolInternacoes;
	private boolean voltarParaPolCirurgias;
	private boolean voltarParaPolProced;
	private boolean voltarParaPolDetalhesInternacoes;
	
	private boolean erroMontagemRelatorio;

	private Collection<RelatorioRegistroControlePacienteVO> colecao;
	
	private AghAtendimentos aghAtendimentos;
	private boolean voltarAmbulatorioPOL = false;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
		colecao = null;
		conversationContext.setConcurrentAccessTimeout(900000000000l);
	}
	
	/**
	 * Dados que serÃ£o impressos em PDF.
	 */
	@Override
	public Collection<RelatorioRegistroControlePacienteVO> recuperarColecao() throws ApplicationBusinessException {
		AghAtendimentos atendimentos = null;
		Date dataInicio = null;
		Date dataFim = null;
		
		if (voltarAmbulatorioPOL){
			atendimentos = this.aghuFacade.obterAghAtendimentoPorChavePrimaria(aghAtendimentos.getSeq());
			dataInicio = this.aghAtendimentos.getDthrInicio();
			dataFim = this.aghAtendimentos.getDthrFim() != null ? this.aghAtendimentos.getDthrFim() : this.aghAtendimentos.getDthrInicio();  
		}else{
			atendimentos = this.aghuFacade.obterAghAtendimentoPorChavePrimaria(this.internacao.getAtdSeq());
			dataInicio = internacao.getDthrInicio();
			dataFim = internacao.getDthrFim();
		}
		
		return this.buscaColecao(atendimentos.getPaciente().getCodigo(),dataInicio, dataFim, aghAtendimentos);
	}

	public String voltarParaPolAcao() {
		colecao = null;
		
		if (voltarParaPolInternacoes){
			this.voltarParaPolInternacoes = false;
			return CONSULTAR_INTERNACOES;
		}
		
		if(voltarParaPolDetalhesInternacoes){
			this.voltarParaPolInternacoes = false;
			return CONSULTAR_DETALHE_INTERNACAO;
		}
		
		if(voltarParaPolProced){
			this.voltarParaPolProced = false;
			return POL_PROCEDIMENTO;
		}
		
		if (voltarParaPolCirurgias){
//			this.cirurgiasInternacaoPOLController.getDataModel().reiniciarPaginator();
			this.voltarParaPolCirurgias = false;
			return POL_CIRURGIA;
		}
		
		if (voltarAmbulatorioPOL){
			this.voltarAmbulatorioPOL = false;
			return POL_AMBULATORIO; 
		}
		
		return null;
	}

	/**
	 * Método criado para a tarefa #21014 Relatório de Controles do Paciente
	 * entra esporadicamente em loop Solução: Limitar o número de páginas do
	 * relatório em 600 e capturar a exceção MaxPagesGovernorException
	 */
	public void carregarRelatorio() throws IOException, BaseException, JRException, SystemException, DocumentException {
		try {
			DocumentoJasper documento = gerarDocumento();
			documento.executar();
		} catch (MaxPagesGovernorException maxException) {
			StringBuilder logMessage = new StringBuilder(123);
			logMessage.append("Erro ao tentar renderizar o relatório Resgistro de Controles do Paciente. ")
			.append("Relatório excedeu número de páginas. ");

			if (this.internacao != null) {
				logMessage.append("Prontuário: ").append(this.internacao.getProntuario().toString());
			}

			LOG.error(logMessage, maxException);

			apresentarMsgNegocio(Severity.ERROR,"MENSAGEM_ERRO_RELATORIO_CONTROLE_MAX_PAGES");
		}
	}

	/**
	 * Método invocado pelo a:mediaOutput para geraÃ§Ã£o de PDF dentro de XHTML.
	 */
	public StreamedContent getRenderPdf() throws IOException, ApplicationBusinessException, JRException, SystemException, DocumentException {
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(true)); // Protegido? = TRUE
	}

	public void directPrint() {
		try {
			DocumentoJasper documento = gerarDocumento(Boolean.TRUE);
			this.sistemaImpressao.imprimir(documento.getJasperPrint(), getEnderecoIPv4HostRemoto());
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/controlepaciente/report/relatorioRegistroControlePaciente.jasper";
		// alterar a chamada para o metodo da controller.(recuperarColecao())
	}

	@Override
	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();
		try {
			params.put("caminhoLogo", recuperarCaminhoLogo());
		} catch (BaseException e) {
			LOG.error("Erro ao tentar recuparar logotipo para o relatório", e);
		}
		params.put("SUBREPORT_DIR", "br/gov/mec/aghu/controlepaciente/report/");
		params.put("previaUrlImagem", recuperaCaminhoImgBackground());

		obterParametrosBalancoHidrico(params);

		return params;
	}

	private void obterParametrosBalancoHidrico(Map<String, Object> params) {
		params.put("SOMATORIO_ADMIN", "0,0");
		params.put("SOMATORIO_ELIM", "0,0");
		params.put("RESULTADO_BALANCO_HIDRICO", "0,0");
		List<EcpItemControle> listaItensControleBh = new ArrayList<EcpItemControle>();
		List<RegistroControlePacienteVO> listaRegistrosControleBh = new ArrayList<RegistroControlePacienteVO>();
		
		Date dthrInicio = null;
		Date dthrFim = null;
		AghAtendimentos atendimentos = null;
		
		if (voltarAmbulatorioPOL) {
			dthrInicio = this.aghAtendimentos.getDthrInicio();
			dthrFim = this.aghAtendimentos.getDthrFim();
			atendimentos = this.aghuFacade.obterAghAtendimentoPorChavePrimaria(this.aghAtendimentos.getSeq());
		} else {
			dthrInicio 	=	internacao.getDthrInicio();
			dthrFim 	= 	internacao.getDthrFim();
			atendimentos = this.aghuFacade.obterAghAtendimentoPorChavePrimaria(this.internacao.getAtdSeq());
		}
		
		try {
			listaItensControleBh = this.cadastrosBasicosControlePacienteFacade.buscarItensControlePorPacientePeriodo(atendimentos.getPaciente(), dthrInicio, dthrFim, null, DominioTipoGrupoControle.CA, DominioTipoGrupoControle.CE);
			
			if (listaItensControleBh != null) {
				listaRegistrosControleBh = this.getControlePacienteFacade().pesquisarRegistrosPaciente(null, atendimentos.getPaciente(), null, dthrInicio, dthrFim,	listaItensControleBh, null);
				if (listaRegistrosControleBh != null) {
					BigDecimal somatorioAdmin = BigDecimal.ZERO;
		 			BigDecimal somatorioElim = BigDecimal.ZERO;
		 			for (RegistroControlePacienteVO controlePaciente : listaRegistrosControleBh) {
		 				int i = 0;
		 				for (EcpItemControle itemControle : listaItensControleBh) {
		 					if (itemControle.getGrupo().getTipo().equals(DominioTipoGrupoControle.CA)) {
								String valor = controlePaciente.getValor()[i];
								if (valor != null && isNumero(valor)) {
		 							somatorioAdmin = somatorioAdmin.add(new BigDecimal(
		 									Double.valueOf(converteValorStringDouble(valor))));
		 						}
		 					} else if (itemControle.getGrupo().getTipo().equals(DominioTipoGrupoControle.CE)) {
		 						String valor = controlePaciente.getValor()[i];
		 						if (valor != null && isNumero(valor)) {
		 							somatorioElim = somatorioElim.add(new BigDecimal(
		 									Double.valueOf(converteValorStringDouble(valor))));
		 						}
		 					}
		 					i++;
		 				}
		 			}
		 			params.put("SOMATORIO_ADMIN", AghuNumberFormat.formatarNumeroMoeda(somatorioAdmin));
		 			params.put("SOMATORIO_ELIM", AghuNumberFormat.formatarNumeroMoeda(somatorioElim));
		 			params.put("RESULTADO_BALANCO_HIDRICO", AghuNumberFormat.formatarNumeroMoeda(somatorioAdmin.subtract(somatorioElim)));
				}
			}
		} catch (BaseException e) {
			LOG.error("Erro ao tentar recuparar colecao para o relatório", e);
		}
	}
	
	private Boolean isNumero(String valorCampoTela){
		if (StringUtils.isNotBlank(valorCampoTela) && NumberUtils.isNumber(converteValorStringDouble(valorCampoTela))) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
	
	private String converteValorStringDouble(String valorCampoTela) {
		valorCampoTela = valorCampoTela.replace(".", StringUtils.EMPTY);
		valorCampoTela = valorCampoTela.replace(',', '.');
		return valorCampoTela;
	}

	public ICadastrosBasicosControlePacienteFacade getCadastrosBasicosControlePacienteFacade() {
		return cadastrosBasicosControlePacienteFacade;
	}
	public void setCadastrosBasicosControlePacienteFacade(ICadastrosBasicosControlePacienteFacade cadastrosBasicosControlePacienteFacade) {
		this.cadastrosBasicosControlePacienteFacade = cadastrosBasicosControlePacienteFacade;
	}
	
	private String recuperaCaminhoImgBackground() {
		ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
		String path = servletContext.getRealPath("/resources/img/report_previa.png");
		return path;
	}

	private Collection<RelatorioRegistroControlePacienteVO> buscaColecao(Integer pacCodigo, Date dataHoraInicio, Date dataHoraFim, AghAtendimentos aghAtendimentos) throws ApplicationBusinessException {
		if(colecao == null){
			colecao = controlePacienteFacade.criaRelatorioRegistroControlePaciente(pacCodigo,dataHoraInicio, dataHoraFim, aghAtendimentos);
	}

		return colecao;
	}

	public String montaIntervaloPesquisa() {
		try {
			if (this.internacao.getDthrFim() == null) {
				Date dataAtual = new Date();
				this.internacao.setDthrFim(new Timestamp(dataAtual.getTime()));
			}

			this.controlePacienteFacade.verificaDuracaoAtendimento(internacao.getDthrInicio(), internacao.getDthrFim());
		} catch (ApplicationBusinessException e) {
			this.internacao.setDthrFim(null);
		} catch (BaseException e) {
			this.internacao.setDthrFim(null);
		}
		return null;
	}

	public void montaIntervaloPesquisaForms() {
		if (this.internacao.getDthrFim() == null) {
			Date dataAtual = new Date();
			this.internacao.setDthrFim(dataAtual);
		}

		if (BooleanUtils.isTrue(this.controlePacienteFacade.datasForaIntervaloAtendimento(internacao.getDthrInicio(),internacao.getDthrFim()))) {
			this.internacao.setDthrFim(null);
		}
	}
	
	public String montaRelatorioRegistroControlesPacientePol(){
		montaRelatorioRegistroControlesPaciente();
        return RELATORIO_CONTROLE_PACIENTES;
    }

	public String montaRelatorioRegistroControlesPacienteForms(){
		montaRelatorioRegistroControlesPaciente();
		if (!erroMontagemRelatorio) {
			return RELATORIO_CONTROLE_PACIENTES;
		} else {
			return null;
		}
	}

	public void montaRelatorioRegistroControlesPaciente() {

		try {

			if (this.internacao.getDthrInicio() == null) {
				apresentarMsgNegocio(Severity.ERROR,"MENSAGEM_DATA_HORA_INICIAL_NULO");
				return;
			}

			if (this.internacao.getDthrFim() == null) {
				apresentarMsgNegocio(Severity.ERROR,"MENSAGEM_DATA_HORA_FINAL_NULO");
				return;
			}

			// Validação das datas da modal quando as duas forem informadas
			validaDataInicialFinal();
			
			this.controlePacienteFacade.verificaDuracaoAtendimento(this.internacao.getDthrInicio(), this.internacao.getDthrFim());

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			erroMontagemRelatorio = true;
		}
	}

	private void validaDataInicialFinal() throws ApplicationBusinessException {
		this.validaDatasInicialFinalInternacao(this.internacao.getDthrInicio(), this.internacao.getDthrFim());
	}

	private void validaDatasInicialFinalInternacao(Date dthrInicio, Date dthrFim) throws ApplicationBusinessException {
		this.controlePacienteFacade.validaDatasInicialFinalInternacao(dthrInicio, dthrFim);
	}

	public void validaDatasInicialFinal(Integer atdSeq, Date dthrInicio,Date dthrFim) throws ApplicationBusinessException {
		this.controlePacienteFacade.validaDatasInicialFinal(atdSeq, dthrInicio,dthrFim);
	}

	/**
	 * Se o relatório vem da tela de transferencia de paciente, carrega os dados da internação
	 */
	public String carregarDadosInternacao() throws IOException, BaseException, JRException, SystemException, DocumentException {

		String result = null;

		if (this.isFromTransferenciaPaciente() || this.isFromVisualizarControles() || this.isFromTransferenciaFormsAgh()) {

			this.internacao = new InternacaoVO();
			if (getInternacaoSeq() != null) {
				ainInternacao = internacaoFacade.obterAinInternacaoPorChavePrimaria(getInternacaoSeq());
				internacao.setAtdSeq(ainInternacao.getAtendimento().getSeq());
			} else if (getAtdSeq() != null) {
				internacao.setAtdSeq(getAtdSeq());
			}
			// se tiver dataHoraInicioString e dataHoraFimString foi chamado
			// pela Transferencia do FORMS
			if (StringUtils.isNotBlank(dataHoraInicioString)|| StringUtils.isNotBlank(dataHoraFimString)) {
				try {
					internacao.setDthrInicio(sdf.parse(this.dataHoraInicioString));
					internacao.setDthrFim(sdf.parse(this.dataHoraFimString));
				} catch (ParseException e) {
					LOG.error(e.getMessage(),e);
				}
			} else {

				internacao.setDthrInicio(new Date(getDataInicialImpressaoInMillis()));
				internacao.setDthrFim(new Date(getDataFinalImpressaoInMillis()));
			}

			if ((fromTransferenciaPaciente) || dataHoraInicioFimStringPreenchidos()) {
				verificarImpressaoTransferencia();
			} else {
				montaRelatorioRegistroControlesPaciente();
			}
		}

		LOG.debug("RelatorioRegistrosControlesPacienteController.carregarDadosInternacao(): data hora inicio = ["+ internacao.getDthrInicio() + "]");
		LOG.debug("RelatorioRegistrosControlesPacienteController.carregarDadosInternacao(): data hora inicio = ["+ internacao.getDthrFim() + "]");
		LOG.debug("RelatorioRegistrosControlesPacienteController.carregarDadosInternacao(): montar relatorio");

		this.carregarRelatorio();

		return result;
	
	}

	private boolean dataHoraInicioFimStringPreenchidos() {

		return StringUtils.isNotBlank(dataHoraInicioString) || StringUtils.isNotBlank(dataHoraFimString);

	}

	/*
	private void recuperaSeqAtendimento() {

		if (getInternacaoSeq() != null) {

			ainInternacao = internacaoFacade.obterAinInternacaoPorChavePrimaria(getInternacaoSeq());

			internacao.setAtdSeq(ainInternacao.getAtendimento().getSeq());

		} else if (getAtdSeq() != null) {

			internacao.setAtdSeq(getAtdSeq());

		}

	}
	*/

	private void verificarImpressaoTransferencia() {
		LOG.debug("RelatorioRegistrosControlesPacienteController.verificarImpressaoTransferencia(): Entrando");

		try {
			DocumentoJasper documento = gerarDocumento();
			this.sistemaImpressao.imprimir(documento.getJasperPrint(), getEnderecoIPv4HostRemoto());
			apresentarMsgNegocio(Severity.INFO,"MENSAGEM_APOS_IMPRESSAO_CONTROLES_PACIENTE");
			
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}

	/**
	 * Se relatório é chamado de uma tela que recebeu atdSeq via parametro do
	 * Oracle*Forms AGH .
	 */
	public void iniciarFormsAgh() {
		if (super.getRequestParameter("atd") != null && !StringUtils.isBlank(super.getRequestParameter("atd"))) {
			this.fromFormsAgh = true;
			this.atdSeq = Integer.valueOf(super.getRequestParameter("atd"));
			this.atendimentos = this.aghuFacade.obterAghAtendimentoPorChavePrimaria(this.atdSeq);
			this.internacao.setAtdSeq(this.atdSeq);
			if (this.atendimentos != null) {
				this.internacao.setDthrInicio(this.atendimentos.getDthrInicio());
				this.internacao.setDthrFim(this.atendimentos.getDthrFim());
				montaIntervaloPesquisaForms();
			}
		}
	}

	public String voltarListaTransferencia() {
		colecao = null;
		setFromTransferenciaPaciente(false);
		return LISTA_PACIENTES_TRANSFERENCIA;
	}
	
	public String voltar() {
		colecao = null;
		
		if (isFromTransferenciaPaciente()) {
			setFromTransferenciaPaciente(false);
			return LISTA_PACIENTES_TRANSFERENCIA;
			
		} else if (isFromVisualizarControles()) {
			setFromVisualizarControles(false);
			return VISUALIZAR_REGISTROS;
		}
		
		return null;
	}

	public InternacaoVO getInternacao() {
		return internacao;
	}

	public void setInternacao(InternacaoVO internacao) {
		this.internacao = internacao;
	}

	public void setFromTransferenciaPaciente(boolean fromTransferenciaPaciente) {
		this.fromTransferenciaPaciente = fromTransferenciaPaciente;
	}

	public boolean isFromTransferenciaPaciente() {
		return fromTransferenciaPaciente;
	}

	public void setInternacaoSeq(Integer internacaoSeq) {
		this.internacaoSeq = internacaoSeq;
	}

	public Integer getInternacaoSeq() {
		return internacaoSeq;
	}

	public void setDataFinalImpressaoInMillis(long dataFinalImpressaoInMillis) {
		this.dataFinalImpressaoInMillis = dataFinalImpressaoInMillis;
	}

	public long getDataFinalImpressaoInMillis() {
		return dataFinalImpressaoInMillis;
	}

	public void setDataInicialImpressaoInMillis(
			long dataInicialImpressaoInMillis) {
		this.dataInicialImpressaoInMillis = dataInicialImpressaoInMillis;
	}

	public long getDataInicialImpressaoInMillis() {
		return dataInicialImpressaoInMillis;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	public Integer getAtdSeq() {
		return atdSeq;
	}

	public void setFromVisualizarControles(boolean fromVisualizarControles) {
		this.fromVisualizarControles = fromVisualizarControles;
	}

	public boolean isFromVisualizarControles() {
		return fromVisualizarControles;
	}

	public AghAtendimentos getAtendimentos() {
		return atendimentos;
	}

	public void setAtendimentos(AghAtendimentos atendimentos) {
		this.atendimentos = atendimentos;
	}

	public IControlePacienteFacade getControlePacienteFacade() {
		return this.controlePacienteFacade;
	}

	public Integer getCodigoPaciente() {
		return codigoPaciente;
	}

	public void setCodigoPaciente(Integer codigoPaciente) {
		this.codigoPaciente = codigoPaciente;
	}

	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}

	public boolean isFromFormsAgh() {
		return fromFormsAgh;
	}

	public void setFromFormsAgh(boolean fromFormsAgh) {
		this.fromFormsAgh = fromFormsAgh;
	}

	public String getDataHoraInicioString() {
		return dataHoraInicioString;
	}

	public void setDataHoraInicioString(String dataHoraInicioString) {
		this.dataHoraInicioString = dataHoraInicioString;
	}

	public String getDataHoraFimString() {
		return dataHoraFimString;
	}

	public void setDataHoraFimString(String dataHoraFimString) {
		this.dataHoraFimString = dataHoraFimString;
	}

	public boolean isFromTransferenciaFormsAgh() {
		return fromTransferenciaFormsAgh;
	}

	public void setFromTransferenciaFormsAgh(boolean fromTransferenciaFormsAgh) {
		this.fromTransferenciaFormsAgh = fromTransferenciaFormsAgh;
	}
	
	public boolean isVoltarParaPolInternacoes() {
		return voltarParaPolInternacoes;
	}

	public void setVoltarParaPolInternacoes(boolean voltarParaPolInternacoes) {
		this.voltarParaPolInternacoes = voltarParaPolInternacoes;
	}

	public boolean isVoltarParaPolCirurgias() {
		return voltarParaPolCirurgias;
	}

	public void setVoltarParaPolCirurgias(boolean voltarParaPolCirurgias) {
		this.voltarParaPolCirurgias = voltarParaPolCirurgias;
	}

	public boolean isVoltarParaPolProced() {
		return voltarParaPolProced;
	}

	public void setVoltarParaPolProced(boolean voltarParaPolProced) {
		this.voltarParaPolProced = voltarParaPolProced;
	}
	
	public boolean isVoltarParaPolDetalhesInternacoes() {
		return voltarParaPolDetalhesInternacoes;
	}

	public void setVoltarParaPolDetalhesInternacoes(
			boolean voltarParaPolDetalhesInternacoes) {
		this.voltarParaPolDetalhesInternacoes = voltarParaPolDetalhesInternacoes;
	}

	public boolean isErroMontagemRelatorio() {
		return erroMontagemRelatorio;
	}

	public void setErroMontagemRelatorio(boolean erroMontagemRelatorio) {
		this.erroMontagemRelatorio = erroMontagemRelatorio;
	}
	
	public Collection<RelatorioRegistroControlePacienteVO> getColecao() {
		return colecao;
	}

	public void setColecao(Collection<RelatorioRegistroControlePacienteVO> colecao) {
		this.colecao = colecao;
	}

	public AghAtendimentos getAghAtendimentos() {
		return aghAtendimentos;
	}

	public void setAghAtendimentos(AghAtendimentos aghAtendimentos) {
		this.aghAtendimentos = aghAtendimentos;
	}

	public boolean isVoltarAmbulatorioPOL() {
		return voltarAmbulatorioPOL;
	}

	public void setVoltarAmbulatorioPOL(boolean voltarAmbulatorioPOL) {
		this.voltarAmbulatorioPOL = voltarAmbulatorioPOL;
	}
}