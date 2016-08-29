package br.gov.mec.aghu.paciente.prontuarioonline.action;

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
import br.gov.mec.aghu.dominio.DominioTipoDocumento;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.paciente.prontuarioonline.business.IProntuarioOnlineFacade;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.SumarioAssistenciaPartoVO;
import br.gov.mec.aghu.core.commons.BaseBean;
import br.gov.mec.aghu.core.commons.criptografia.Base64Util.OutputStream;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;

public class RelatorioSumarioAssistenciaPartoController extends ActionReport {

	private static final Log LOG = LogFactory.getLog(RelatorioSumarioAssistenciaPartoController.class);
	
	private static final String PREVIA = "previa";

	private static final long serialVersionUID = 2533511321334710349L;
	
	@EJB
	private IProntuarioOnlineFacade prontuarioOnlineFacade;
	
	@Inject
	private SistemaImpressao sistemaImpressao;
	
	@Inject @Http 
	private ConversationContext conversationContext;

	private List<SumarioAssistenciaPartoVO> colecao = new ArrayList<SumarioAssistenciaPartoVO>(0);

	private Integer pacCodigo;
	
	private Short gsoSeqp;
	
	private Integer conNumero;
	
	private Boolean	 previa;

	// Paramentros enviados do Emergência.
	private String voltarPara;
	private boolean voltarEmergencia = Boolean.FALSE;
	private boolean exibirBotaoVoltar;
	private Integer paramCid;
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
		conversationContext.setConcurrentAccessTimeout(900000000000l);
		this.setParameters();
	}
	
	@Override
	protected BaseBean getEntidadePai() {
		if(this.colecao != null) {
			return this.colecao.get(0);
		}
		return null;
	}

	private void setParameters(){
		
		if(getRequestParameter("pacCodigo") != null && !getRequestParameter("pacCodigo").isEmpty()){
			this.setPacCodigo(Integer.valueOf(getRequestParameter("pacCodigo")));
		}
		
		if(getRequestParameter("seqp") != null && !getRequestParameter("seqp").isEmpty()){
			this.setGsoSeqp(Short.valueOf(getRequestParameter("seqp")));
		}
		
		if(getRequestParameter("numeroConsulta") != null && !getRequestParameter("numeroConsulta").isEmpty()){
			this.setConNumero(Integer.valueOf(getRequestParameter("numeroConsulta")));
		}
		
		if(getRequestParameter("voltarPara") != null && !getRequestParameter("voltarPara").isEmpty()){
			this.setVoltarPara(getRequestParameter("voltarPara"));
		}
		
		if(getRequestParameter("voltarEmergencia") != null && !getRequestParameter("voltarEmergencia").isEmpty()){
			this.setVoltarEmergencia(Boolean.parseBoolean(getRequestParameter("voltarEmergencia")));
		}
		
		if(getRequestParameter("paramCid") != null && !getRequestParameter("paramCid").isEmpty()){
			this.setParamCid(Integer.valueOf(getRequestParameter("paramCid")));
		}
		
		setPreviaRelatorio();
		setPendenciaDeAssinaturaDigital();
		setDadosServidor();
	}
	
	private void setDadosServidor() {

		if (getRequestParameter("usuarioAutenticado") != null && !getRequestParameter("usuarioAutenticado").isEmpty()) {
			super.setUsuarioAutenticado(Boolean.valueOf(getRequestParameter("usuarioAutenticado")));
		}
		if (getRequestParameter("matricula") != null && !getRequestParameter("matricula").isEmpty()) {
			super.setMatricula(Integer.valueOf(getRequestParameter("matricula")));
		}
		if (getRequestParameter("vinculo") != null && !getRequestParameter("vinculo").isEmpty()) {
			super.setVinculo(Short.valueOf(getRequestParameter("vinculo")));
		}

		super.setServidorLogado();
	}
	
	private void setPreviaRelatorio() {
		if (getRequestParameter(PREVIA) != null && !getRequestParameter(PREVIA).isEmpty()) {
			this.setPrevia(Boolean.parseBoolean(getRequestParameter(PREVIA)));
		}
	}

	private void setPendenciaDeAssinaturaDigital() {
		super.setGerarPendenciaDeAssinaturaDigital(Boolean.FALSE);
		
		if(getRequestParameter("gerarPendenciaDeAssinaturaDigital") != null && !getRequestParameter("gerarPendenciaDeAssinaturaDigital").isEmpty()){
			super.setGerarPendenciaDeAssinaturaDigital(Boolean.parseBoolean(getRequestParameter("gerarPendenciaDeAssinaturaDigital")));
		}
		
		if(getRequestParameter("exibirBotaoVoltar") != null && !getRequestParameter("exibirBotaoVoltar").isEmpty()){
			this.setExibirBotaoVoltar(Boolean.parseBoolean(getRequestParameter("exibirBotaoVoltar")));
		}
	}
		

	/**
	 * Impressão direta usando o CUPS.
	 */
	public void directPrint() {

		try {
			colecao = this.prontuarioOnlineFacade.recuperarSumarioAssistenciaPartoVO(pacCodigo, gsoSeqp, conNumero);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}

		try {
			DocumentoJasper documento = gerarDocumento(!getGerarPendenciaDeAssinaturaDigital());

			this.sistemaImpressao.imprimir(documento.getJasperPrint(), getEnderecoIPv4HostRemoto());

			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
			
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
			
		} catch (Exception e) {
			apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	} 

	@Override
	public Collection<SumarioAssistenciaPartoVO> recuperarColecao() throws ApplicationBusinessException {
		return colecao = this.prontuarioOnlineFacade.recuperarSumarioAssistenciaPartoVO(pacCodigo, gsoSeqp, conNumero);		
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/paciente/prontuarioonline/report/sumarioAssistenciaParto/relatorioSumarioAssistenciaParto.jasper";
	}

	@Override
	public Map<String, Object> recuperarParametros() {
		
			Map<String, Object> params = new HashMap<String, Object>();
			
			try {
				params.put("caminhoLogo", recuperarCaminhoLogo());
			} catch (BaseException e) {
				LOG.error("Erro ao tentar recuparar logotipo para o relatório",e);
			}			
			params.put("SUBREPORT_DIR", "br/gov/mec/aghu/paciente/prontuarioonline/report/sumarioAssistenciaParto/");
			params.put("textoCabecalho", "Este documento é uma compilação dos registros eletrônicos de diversos profissionais que atenderam a paciente.");
			params.put("previaUrlImagem", recuperaCaminhoImgBackground());
			params.put(PREVIA, previa);
			
			return params;		
	}

	private String recuperaCaminhoImgBackground() {
		ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
		String path = servletContext.getRealPath("/resources/img/report_previa.png");
		return path;
	}
	
	/**
	 * Método invocado pelo a:mediaOutput para geração de PDF dentro de XHTML.
	 */
	public StreamedContent getRenderPdf() throws IOException, JRException, DocumentException, ApplicationBusinessException {
		// colecao.clear();
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(true)); // Protegido? = TRUE
	}
	
	public void renderPdfGerarPendencia(OutputStream out, Object data) throws IOException, JRException, DocumentException, ApplicationBusinessException {
		DocumentoJasper documento = gerarDocumento(DominioTipoDocumento.NAP, !getGerarPendenciaDeAssinaturaDigital());
		out.write(documento.getPdfByteArray(true)); // Protegido? = TRUE
		if(getGerarPendenciaDeAssinaturaDigital()) {
			this.apresentarMsgNegocio(Severity.INFO, "PENDENCIA_ASSINATURA_GERADA_SUCESSO");
		}				
	}
	
	public String voltar() {
		return voltarPara;
	}


	public SistemaImpressao getSistemaImpressao() {
		return sistemaImpressao;
	}

	public IProntuarioOnlineFacade getProntuarioOnlineFacade() {
		return prontuarioOnlineFacade;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public Short getGsoSeqp() {
		return gsoSeqp;
	}

	public void setGsoSeqp(Short gsoSeqp) {
		this.gsoSeqp = gsoSeqp;
	}

	public List<SumarioAssistenciaPartoVO> getColecao() {
		return colecao;
	}

	public void setColecao(List<SumarioAssistenciaPartoVO> colecao) {
		this.colecao = colecao;
	}

	public Integer getConNumero() {
		return conNumero;
	}

	public void setConNumero(Integer conNumero) {
		this.conNumero = conNumero;
	}

	public boolean isVoltarEmergencia() {
		return voltarEmergencia;
	}

	public void setVoltarEmergencia(boolean voltarEmergencia) {
		this.voltarEmergencia = voltarEmergencia;
	}
	
	public boolean isExibirBotaoVoltar() {
		return exibirBotaoVoltar;
	}

	public void setExibirBotaoVoltar(boolean exibirBotaoVoltar) {
		this.exibirBotaoVoltar = exibirBotaoVoltar;
	}

	public Integer getParamCid() {
		return paramCid;
	}

	public void setParamCid(Integer paramCid) {
		this.paramCid = paramCid;
	}
	
	public void setPrevia(boolean previa) {
		this.previa = previa;
	}

}