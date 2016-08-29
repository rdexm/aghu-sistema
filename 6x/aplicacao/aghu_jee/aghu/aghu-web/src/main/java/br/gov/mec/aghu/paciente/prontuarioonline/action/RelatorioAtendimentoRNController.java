package br.gov.mec.aghu.paciente.prontuarioonline.action;

import static br.gov.mec.aghu.model.AipPacientes.VALOR_MAXIMO_PRONTUARIO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.ServletContext;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.weld.context.ConversationContext;
import org.jboss.weld.context.http.Http;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.dominio.DominioNomeRelatorio;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.paciente.prontuarioonline.business.IProntuarioOnlineFacade;
import br.gov.mec.aghu.paciente.vo.SumarioAtdRecemNascidoMedicamentosVO;
import br.gov.mec.aghu.paciente.vo.SumarioAtdRecemNascidoProfissionaisEnvolvidosVO;
import br.gov.mec.aghu.paciente.vo.SumarioAtdRecemNascidoSlPartoExamesMaeVO;
import br.gov.mec.aghu.paciente.vo.SumarioAtdRecemNascidoSlPartoGestacoesAnterioresVO;
import br.gov.mec.aghu.paciente.vo.SumarioAtdRecemNascidoSlPartoVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;

/**
 * @author aghu
 *
 */
public class RelatorioAtendimentoRNController extends ActionReport {

	private static final Log LOG = LogFactory.getLog(RelatorioAtendimentoRNController.class);

	@Inject
	private SistemaImpressao sistemaImpressao;
	
	@Inject @Http 
	private ConversationContext conversationContext;
	
	private static final long serialVersionUID = -2829581597108866754L;

	private static final String TITULO_RELATORIO = "Atendimento ao Recém Nascido em Sala de Parto";

	private DominioNomeRelatorio relatorio;
	
	private boolean voltarEmergencia = Boolean.FALSE;
	
	private String fileName;
	
	private String origem;
	
	private Integer atdSeq;
	
	private Integer pacCodigo;
	
	private Integer conNumero;
	
	private Short gsoSeqp;
	
	private Byte rnaSeqp;
	
	private Boolean gerouArquivo;
	
	private String titlePdfView;
	
	private String nomeRelatorio;
	
	private String nomeRelatorioRodape;
	
	private Integer seqInternacao;

	private String tipoInternacao;
	
	private Boolean previa = Boolean.FALSE;
	
	
	private List<SumarioAtdRecemNascidoSlPartoVO> dadosRelatorio = null;
	
	@EJB
	private IProntuarioOnlineFacade prontuarioOnlineFacade;


	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
		conversationContext.setConcurrentAccessTimeout(900000000000l);
		try {
			this.doLoadParameters();
		} catch (ApplicationBusinessException e) {
			LOG.error("Erro @PostConstruct", e);
		}
	}

	
	private void doLoadParameters() throws ApplicationBusinessException {
		boolean hasParameter = false;
		
		hasParameter = doSetPkParameters();
		
		hasParameter = doSetOtherParameters();
		
		if (hasParameter) {
			this.gerarDados();
		}
		
		// Informacao da pagina para o botao voltar.
		if (getRequestParameter("origem") != null && !getRequestParameter("origem").isEmpty()) {
			this.setOrigem(getRequestParameter("origem"));
		}
		
		if (getRequestParameter("voltarEmergencia") != null && !getRequestParameter("voltarEmergencia").isEmpty()) {
			this.setVoltarEmergencia(Boolean.parseBoolean(getRequestParameter("voltarEmergencia")));
		}

	}


	private boolean doSetOtherParameters() {
		boolean hasParameter = false;
		final String PREVIA = "previa";
		
		if (getRequestParameter("seqInternacao") != null && !getRequestParameter("seqInternacao").isEmpty()) {
			this.setSeqInternacao(Integer.valueOf(getRequestParameter("seqInternacao")));
			hasParameter = true;
		}
		if (getRequestParameter("tipoInternacao") != null && !getRequestParameter("tipoInternacao").isEmpty()) {
			this.setTipoInternacao(getRequestParameter("tipoInternacao"));
			hasParameter = true;
		}
		if (getRequestParameter(PREVIA) != null && !getRequestParameter(PREVIA).isEmpty()) {
			this.setPrevia(Boolean.parseBoolean(getRequestParameter(PREVIA)));
			hasParameter = true;
		}
		
		return hasParameter;
	}


	private boolean doSetPkParameters() {
		boolean hasParameter = false;
		
		if (getRequestParameter("pacCodigo") != null && !getRequestParameter("pacCodigo").isEmpty()) {
			this.setPacCodigo(Integer.valueOf(getRequestParameter("pacCodigo")));
			hasParameter = true;
		}
		if (getRequestParameter("conNumero") != null && !getRequestParameter("conNumero").isEmpty()) {
			this.setConNumero(Integer.valueOf(getRequestParameter("conNumero")));
			hasParameter = true;
		}
		if (getRequestParameter("gsoSeqp") != null && !getRequestParameter("gsoSeqp").isEmpty()) {
			this.setGsoSeqp(Short.valueOf(getRequestParameter("gsoSeqp")));
			hasParameter = true;
		}
		if (getRequestParameter("atdSeq") != null && !getRequestParameter("atdSeq").isEmpty()) {
			this.setAtdSeq(Integer.valueOf(getRequestParameter("atdSeq")));
			hasParameter = true;
		}
		if (getRequestParameter("rnaSeqp") != null && !getRequestParameter("rnaSeqp").isEmpty()) {
			this.setRnaSeqp(Byte.valueOf(getRequestParameter("rnaSeqp")));
			hasParameter = true;
		}
		return hasParameter;
	}


	/**
	 * Recupera arquivo compilado do Jasper
	 */
	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/paciente/prontuarioonline/report/sumarioAtdRnSlParto/SumarioAtendimentoRNSlParto.jasper";
	}

	/**
	 * Recupera a coleção utilizada no relatório
	 */
	@Override
	public Collection<?> recuperarColecao() throws ApplicationBusinessException {
		return dadosRelatorio;
	}

	/**
	 * Retorna os parametros utilizados no relatorio
	 */
	@Override
	public Map<String, Object> recuperarParametros() {
		
		Map<String, Object> params = new HashMap<String, Object>();
		final String PREVIA = "previa";
		
		String nomeRecemNascido = "";
		String leito = "";
		String prontuarioRN = "";
		String prontuarioMae = "";
		Integer codigoPacRN = null;
		Integer codigoPacMae = null;
		List<SumarioAtdRecemNascidoSlPartoGestacoesAnterioresVO> gestacoesAnteriores = new ArrayList<SumarioAtdRecemNascidoSlPartoGestacoesAnterioresVO>();
		List<SumarioAtdRecemNascidoSlPartoExamesMaeVO> examesMae = new ArrayList<SumarioAtdRecemNascidoSlPartoExamesMaeVO>();
		List<SumarioAtdRecemNascidoProfissionaisEnvolvidosVO> profissionais = new ArrayList<SumarioAtdRecemNascidoProfissionaisEnvolvidosVO>();
		List<SumarioAtdRecemNascidoMedicamentosVO> medicamentos = new ArrayList<SumarioAtdRecemNascidoMedicamentosVO>();
		
		if(dadosRelatorio != null && !dadosRelatorio.isEmpty()){
			SumarioAtdRecemNascidoSlPartoVO vo = dadosRelatorio.get(0);
			nomeRecemNascido = vo.getNome();
			if(vo.getProntuario()!=null){
				prontuarioRN = Integer.valueOf(vo.getProntuario().replaceAll("/", "")) > VALOR_MAXIMO_PRONTUARIO ? vo.getProntuario() : null;	
			}		
			prontuarioMae = vo.getProntuarioMae();
			//codigoPacRN = vo.getPacCodigoRN();
			codigoPacRN = vo.getConNumero();
			codigoPacMae = vo.getPacCodigo();
			gestacoesAnteriores = vo.getGestacoesAnteriores();
			examesMae = vo.getExamesMae();
			medicamentos = vo.getMedicamentosRecemNascido();
			profissionais = vo.getProfissionaisEnvolvidos();
			
		}
		
		// informa todos os parametros necessarios
		params.put("tituloRelatorio", TITULO_RELATORIO);
		params.put("textoCabecalho", "Este documento é uma compilação dos registros eletrônicos de diversos profissionais que atenderam a paciente.");
		params.put("nomeRecemNascido", nomeRecemNascido); 
		params.put("leito", leito); 
		params.put("prontuarioRN", prontuarioRN); 
		params.put("prontuarioMae", prontuarioMae);
		try {
			params.put("caminhoLogo", recuperarCaminhoLogo());
		} catch (BaseException e) {
			LOG.error("Erro ao tentar recuparar logotipo para o relatório",e);
		}		
		params.put("codigoPacRN", codigoPacRN); 
		params.put("codigoPacMae", codigoPacMae);
		params.put("voMaster", dadosRelatorio);
		params.put("voGestacoesAnteriores", gestacoesAnteriores); 
		params.put("voExamesMae", examesMae); 
		params.put("voProfissionais", profissionais);
		params.put("voMedicamentos", medicamentos);
		params.put("SUBREPORT_DIR", "br/gov/mec/aghu/paciente/prontuarioonline/report/sumarioAtdRnSlParto/");
		
		params.put(PREVIA, this.getPrevia());
		params.put("previaUrlImagem", recuperaCaminhoImgBackground());
		
		return params;
	}
	
	private String recuperaCaminhoImgBackground() {
		ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
		String path = servletContext.getRealPath("/resources/img/report_previa.png");
		return path;
	}
	
	
	public void gerarDados() throws ApplicationBusinessException {
		dadosRelatorio = new ArrayList<SumarioAtdRecemNascidoSlPartoVO>();
		SumarioAtdRecemNascidoSlPartoVO vo = null;
		try {
			
			vo = prontuarioOnlineFacade.obterRelatorioSumarioAtdRecemNascidoSlParto(getAtdSeq(), getPacCodigo(), getConNumero(), getGsoSeqp(), getRnaSeqp());
		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
		}
		if(vo != null){
			dadosRelatorio.add(vo);
		}
		setTitlePdfView(TITULO_RELATORIO);
		setNomeRelatorio(TITULO_RELATORIO);
	}

	/**
	 * Renderiza o PDF
	 * @param out
	 * @param data
	 * @throws IOException
	 * @throws JRException
	 * @throws DocumentException
	 * @throws BaseException
	 */
	public StreamedContent getRenderPdf() throws IOException, JRException, DocumentException, ApplicationBusinessException{
		DocumentoJasper documento = gerarDocumento(this.getPrevia());
		return this.criarStreamedContentPdf(documento.getPdfByteArray(true)); // Protegido? = TRUE
	}
	
	public void directPrint() {
		try {
			DocumentoJasper documento = gerarDocumento(Boolean.TRUE);
			this.sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}

	public String voltar() {
		limparDados();
		return getOrigem();
	}

	private void limparDados() {
		setAtdSeq(null);
		setConNumero(null);
		setDadosRelatorio(new ArrayList<SumarioAtdRecemNascidoSlPartoVO>());
		setGsoSeqp(null);
		setPacCodigo(null);
	}

	public DominioNomeRelatorio getRelatorio() {
		return relatorio;
	}

	public void setRelatorio(DominioNomeRelatorio relatorio) {
		this.relatorio = relatorio;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getOrigem() {
		return origem;
	}

	public void setOrigem(String origem) {
		this.origem = origem;
	}

	public Boolean getGerouArquivo() {
		return gerouArquivo;
	}

	public void setGerouArquivo(Boolean gerouArquivo) {
		this.gerouArquivo = gerouArquivo;
	}

	public String getTitlePdfView() {
		return titlePdfView;
	}

	public void setTitlePdfView(String titlePdfView) {
		this.titlePdfView = titlePdfView;
	}

	public String getNomeRelatorio() {
		return nomeRelatorio;
	}

	public void setNomeRelatorio(String nomeRelatorio) {
		this.nomeRelatorio = nomeRelatorio;
	}

	public String getNomeRelatorioRodape() {
		return nomeRelatorioRodape;
	}

	public void setNomeRelatorioRodape(String nomeRelatorioRodape) {
		this.nomeRelatorioRodape = nomeRelatorioRodape;
	}

	public List<?> getDadosRelatorio() {
		return dadosRelatorio;
	}

	public void setDadosRelatorio(List<SumarioAtdRecemNascidoSlPartoVO> dadosRelatorio) {
		this.dadosRelatorio = dadosRelatorio;
	}

	public Integer getAtdSeq() {
		return atdSeq;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public Integer getConNumero() {
		return conNumero;
	}

	public void setConNumero(Integer conNumero) {
		this.conNumero = conNumero;
	}

	public Short getGsoSeqp() {
		return gsoSeqp;
	}

	public void setGsoSeqp(Short gsoSeqp) {
		this.gsoSeqp = gsoSeqp;
	}
	
	public Byte getRnaSeqp() {
		return rnaSeqp;
	}

	public void setRnaSeqp(Byte rnaSeqp) {
		this.rnaSeqp = rnaSeqp;
	}
	
	public void setSeqInternacao(Integer seqInternacao) {
		this.seqInternacao = seqInternacao;
	}

	public Integer getSeqInternacao() {
		return seqInternacao;
	}

	public void setTipoInternacao(String tipoInternacao) {
		this.tipoInternacao = tipoInternacao;
	}

	public String getTipoInternacao() {
		return tipoInternacao;
	}


	public boolean isVoltarEmergencia() {
		return voltarEmergencia;
	}


	public void setVoltarEmergencia(boolean voltarEmergencia) {
		this.voltarEmergencia = voltarEmergencia;
	}


	public Boolean getPrevia() {
		return previa;
	}


	public void setPrevia(Boolean previa) {
		this.previa = previa;
	}

}
