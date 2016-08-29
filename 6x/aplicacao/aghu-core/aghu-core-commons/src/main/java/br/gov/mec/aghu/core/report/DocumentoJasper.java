package br.gov.mec.aghu.core.report;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.itextpdf.text.DocumentException;

/**
 * Classe utilitária para execução de relatórios Jasper Reports.<br />
 * <p>
 * Forma de uso:
 * </p>
 * 
 * <pre>
 * DocumentoJasper jasper = new DocumentoJasper(
 * 		&quot;br/gov/mec/aghu/report/exemplo.jasper&quot;);
 * jasper.setParametros(recuperaParametros());
 * jasper.setDados(recuperaColecao());
 * byte[] pdfByteArray = jasper.getPDFByteArray(true);
 * ou
 * JasperPrint jasperPrint = jasper.getJasperPrint();
 * </pre>
 * 
 * @author cvagheti
 * 
 */
public class DocumentoJasper implements Serializable {
	private static final long serialVersionUID = 5074410011563552898L;
	
	private static final Log LOG = LogFactory.getLog(DocumentoJasper.class);
	
	private String jasperReportPath;
	private Map<String, Object> parametros;
	private Collection<?> dados;

	// se null indica que ainda não foi executado
	private JasperPrint jasperPrint;

	public DocumentoJasper(String jasperReportPath) {
		this.jasperReportPath = jasperReportPath;
	}

	public DocumentoJasper(String jasperReportPath, Map<String, Object> parametros,
			Collection<?> dados) {
		this.jasperReportPath = jasperReportPath;
		this.parametros = parametros;
		this.dados = dados;
	}

	/**
	 * Executa o relatório.
	 * 
	 * @throws JRException
	 */
	public void executar() throws JRException {
		if (this.dados == null || this.dados.isEmpty()) {
			if(LOG.isWarnEnabled()){
				LOG.warn("A coleção de dados do relatório está vazia");
			}
//			throw new IllegalStateException(
//					"A coleção de dados do relatório deve ser definida antes da execução.");
		}
		InputStream resourceAsStream = Thread.currentThread()
				.getContextClassLoader()
				.getResourceAsStream(this.jasperReportPath);

		JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(
				this.dados);

		jasperPrint = JasperFillManager.fillReport(resourceAsStream,
				this.parametros, dataSource);

		Locale ptBr = new Locale("pt", "BR");
		jasperPrint.setLocaleCode(ptBr.getCountry());

	}

	/**
	 * Define os parâmetros do relatório.
	 * 
	 * @param parametros
	 */
	public void setParametros(Map<String, Object> parametros) {
		// muda o estado para executar com novos parâmetros
		this.jasperPrint = null;

		this.parametros = parametros;
	}

	/**
	 * Define a coleção de dados do relatório.
	 * 
	 * @param dados
	 */
	public void setDados(Collection<?> dados) {
		// muda o estado para executar com novos dados
		this.jasperPrint = null;

		this.dados = dados;
	}

	/**
	 * Retorna instância da classe que representa o documento gerado, este pode
	 * ser visualizado, impresso ou exportado para outros formatos.
	 * 
	 * @see JasperPrint
	 * @return
	 * @throws JRException
	 */
	public JasperPrint getJasperPrint() throws JRException {
		if (this.jasperPrint == null) {
			executar();
		}
		return this.jasperPrint;
	}

	/**
	 * Retorna o PDF gerado em bytes.
	 * 
	 * @param protegido
	 *            se true modifica o PDF retirando as permissões de impressão e
	 *            oculta barras de ferramentas e menu.
	 * @return
	 * @throws JRException
	 * @throws IOException
	 * @throws DocumentException
	 */
	public byte[] getPdfByteArray(boolean protegido) throws JRException,
			IOException, DocumentException {
		if (this.jasperPrint == null) {
			executar();
		}
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		JasperExportManager.exportReportToPdfStream(this.jasperPrint,
				outputStream);

		if (protegido) {
			outputStream = PdfUtil.protectPdf(outputStream);
		}

		return outputStream.toByteArray();
	}

	/**
	 * Retorna o XLS gerado em bytes.
	 * 
	 * @return
	 * @throws JRException
	 */
	public byte[] getXlsByteArray() throws JRException {
		if (this.jasperPrint == null) {
			executar();
		}
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		JRXlsExporter exporterXLS = new JRXlsExporter();
		exporterXLS.setParameter(JRXlsExporterParameter.JASPER_PRINT,
				this.jasperPrint);
		exporterXLS.setParameter(JRXlsExporterParameter.OUTPUT_STREAM,
				outputStream);
		exporterXLS.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET,
				Boolean.FALSE);
		exporterXLS.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE,
				Boolean.TRUE);
		exporterXLS.setParameter(
				JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
		exporterXLS.setParameter(
				JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS,
				Boolean.TRUE);
		exporterXLS.exportReport();

		return outputStream.toByteArray();
	}

}
