package br.gov.mec.aghu.action.report;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.SystemException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.DocumentException;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.HostRemotoCache;
import br.gov.mec.aghu.core.action.HostRemotoUtil;
import br.gov.mec.aghu.core.commons.BaseBean;
import br.gov.mec.aghu.core.etc.ViewTransacional;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.EmptyReportException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.core.report.ReportGenerator;
import br.gov.mec.aghu.dominio.DominioTipoDocumento;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import net.sf.jasperreports.engine.JRException;

/**
 * Classe a ser usada na migração do codigo legado, de forma a diminuir o
 * refactor necessário. Combina as funções de action controller e Report
 * gerenator.
 * 
 * Esta classe apenas delega os métodos relacionados a geração de relatório para
 * o DefaultReportGenerator.
 * 
 * @replaces AGHURelatorioController
 * 
 * @author geraldo
 * 
 */
public abstract class ActionReport extends ActionController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3898885962121964801L;
	
	
	@EJB @SuppressWarnings("cdi-ambiguous-dependency")
	private IServidorLogadoFacade servidorLogadoFacade;
	
	private static final Log LOG = LogFactory.getLog(ActionReport.class);

	@Inject
	protected DefaultReportGenerator reportGenerator;
	
	@Inject
	protected SistemaImpressao sistemaImpressao;
	
	@Inject
	protected HostRemotoCache hostRemotoCache;
	
	private Boolean usuarioAutenticado = Boolean.FALSE;
	
	private Integer matricula;
	
	private Short vinculo;
	
	private Boolean gerarPendenciaDeAssinaturaDigital;

	public enum ActionReportExceptionCode implements BusinessExceptionCode {
		MENSAGEM_ERRO_GERACAO_PDF
	};
	
	@PostConstruct
	@SuppressWarnings("PMD.UnusedPrivateMethod")
	private void inicializar() {
		reportGenerator.setActionReport(this);
		setServidorLogado();			
	}

	protected void setServidorLogado() {
		RapServidores servidor = null;
		
		if(this.usuarioAutenticado && this.matricula != null && this.vinculo != null) {
			servidor = getServidorLogadoFacade().obterServidorPorChavePrimaria(this.matricula, this.vinculo);
		} else {
			servidor = getServidorLogadoFacade().obterServidorLogado();
		}
		reportGenerator.setServidorLogado(servidor);
	}

	/**
	 * Retorna um DocumentoJasper preenchido com os dados fornecidos pelos
	 * métodos sobrescritos
	 * <br />
	 * Deste objeto pode ser obtidos JasperPrint, PDFs(protegidos ou não) e
	 * XLSs.
	 * 
	 * @see DocumentoJasper
	 * @return
	 * @throws ApplicationBusinessException
	 */
	protected DocumentoJasper gerarDocumento(Boolean bloquearGeracaoPendencia) throws ApplicationBusinessException, EmptyReportException {
		DocumentoJasper documento =  this.reportGenerator.gerarDocumento(bloquearGeracaoPendencia);
		return documento;
	}
	
	protected DocumentoJasper gerarDocumento() throws ApplicationBusinessException, EmptyReportException {
		return this.gerarDocumento(false);
	}
	
	/**
	 * Retorna um DocumentoJasper preenchido com os dados fornecidos pelos
	 * métodos sobrescritos e seta parâmetros para geração de pendência de
	 * assinatura.
	 * <br />
	 * Deste objeto pode ser obtidos JasperPrint, PDFs(protegidos ou não) e
	 * XLSs.
	 * 
	 * @see DocumentoJasper
	 * 
	 * @return
	 * @throws MECBaseException
	 */
	protected DocumentoJasper gerarDocumento(DominioTipoDocumento tipoDocumento)
			throws ApplicationBusinessException, EmptyReportException {
		DocumentoJasper documento =  this.reportGenerator.gerarDocumento(tipoDocumento);
		return documento;
	}

	/**
	 * Retorna um DocumentoJasper preenchido com os dados fornecidos pelos
	 * métodos sobrescritos e seta parâmetros para geração de pendência de
	 * assinatura. 
	 * <br />
	 * Deste objeto pode ser obtidos JasperPrint, PDFs(protegidos ou não) e
	 * XLSs.
	 * 
	 * @see DocumentoJasper
	 * @param tipoDocumento
	 * @param bloquearGeracaoPendencia
	 * @return
	 * @throws MECBaseException
	 */
	protected DocumentoJasper gerarDocumento(
			DominioTipoDocumento tipoDocumento, boolean bloquearGeracaoPendencia)
			throws ApplicationBusinessException,EmptyReportException {
		DocumentoJasper documento =  this.reportGenerator.gerarDocumento(tipoDocumento,bloquearGeracaoPendencia);
		return documento;
	}
	
	protected void imprimirRelatorioCopiaSeguranca(boolean protegido) throws BaseException, JRException, IOException, DocumentException {
		reportGenerator.imprimirRelatorioCopiaSeguranca(protegido);
	}
	
	/**
	 * Método abstrato que deve ser implementado por todos os Controllerrs com
	 * impressão automática via CUPS.
	 * @throws UnknownHostException 
	 * @throws ApplicationBusinessException 
	 * 
	 * @throws MECBaseException
	 * @throws JRException
	 * @throws SystemException
	 * @throws IOException
	 * @throws SistemaImpressaoException 
	 */
	protected void directPrint() throws SistemaImpressaoException, ApplicationBusinessException  {
		FacesContext fc = FacesContext.getCurrentInstance();
		HttpServletRequest request = (HttpServletRequest) fc
				.getExternalContext().getRequest();
		try {
			sistemaImpressao.imprimir(this.reportGenerator.gerarDocumento().getJasperPrint(), HostRemotoUtil.getHostRemoto(request));
		} catch (UnknownHostException e) {
			apresentarMsgNegocio(Severity.ERROR, "UNKNOWN_HOST");
			LOG.error(e.getMessage(), e);
		} catch (JRException e) {
			apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
			LOG.error(e.getMessage(), e);
		}
	}

	/**
	 * 
	 * Método invocado pelo a:mediaOutput para geração de PDF dentro de XHTML.
	 * 
	 * 
	 * @param out
	 * @param data
	 * @throws IOException
	 * @throws ApplicationBusinessException
	 * @throws JRException
	 * @throws SystemException
	 * @throws DocumentException
	 */
	@ViewTransacional
	public StreamedContent getRenderPdf() throws IOException,
			ApplicationBusinessException, JRException, SystemException,
			DocumentException {
		DocumentoJasper documento = this.gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
	}


	/***************************************************************************************************************************************************/		
	public StreamedContent criarStreamedContentPdf(byte[] byteArray){
		return ActionReport.criarStreamedContentPdfPorByteArray(byteArray);
	}
	
	public static StreamedContent criarStreamedContentPdfPorByteArray(byte[] byteArray){
		return new DefaultStreamedContent(new ByteArrayInputStream(byteArray), "application/pdf", "relatorio.pdf");
	}
	/***************************************************************************************************************************************************/
	
	protected String recuperarCaminhoLogo() throws ApplicationBusinessException {
		return this.reportGenerator.recuperarCaminhoLogo();
	}

	protected String recuperarCaminhoLogo2()
			throws ApplicationBusinessException {
		return this.reportGenerator.recuperarCaminhoLogo2();
	}

	protected abstract Collection<?> recuperarColecao() throws ApplicationBusinessException;

	protected abstract String recuperarArquivoRelatorio();

	protected Map<String, Object> recuperarParametros() {
		return null;
	}

	/**
	 * Método que retorna a entidade pai do relatório, a ser usada na geração da
	 * pendência de assinatura. Deve ser sobrescrito nas controllers do
	 * relatórios que vão gerar pendência de assinatura.
	 * 
	 * @return
	 */
	protected BaseBean getEntidadePai() {
		return null;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
	public ReportGenerator getReportGenerator(){
		return reportGenerator;
	}

	public Boolean getUsuarioAutenticado() {
		return usuarioAutenticado;
	}

	public void setUsuarioAutenticado(Boolean usuarioAutenticado) {
		this.usuarioAutenticado = usuarioAutenticado;
	}

	public Integer getMatricula() {
		return matricula;
	}

	public void setMatricula(Integer matricula) {
		this.matricula = matricula;
	}

	public Short getVinculo() {
		return vinculo;
	}

	public void setVinculo(Short vinculo) {
		this.vinculo = vinculo;
	}

	public Boolean getGerarPendenciaDeAssinaturaDigital() {
		return gerarPendenciaDeAssinaturaDigital;
	}

	public void setGerarPendenciaDeAssinaturaDigital(Boolean gerarPendenciaDeAssinaturaDigital) {
		this.gerarPendenciaDeAssinaturaDigital = gerarPendenciaDeAssinaturaDigital;
	}	
}
