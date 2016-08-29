package br.gov.mec.aghu.action.impressao;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Controla os documentos a serem impressos no cliente.<br />
 * 
 * A classe que implementa a impressão de documentos no cliente adiciona o
 * documento na fila, quando o sistema de impressão no cliente estiver ativado e
 * houver documentos na fila um javascript é adicionado as páginas, este faz o
 * download automático do documento.
 * 
 * @see SistemaImpressaoCliente#imprimir(JasperPrint, java.net.InetAddress)
 * @see SistemaImpressaoCliente#imprimir(String, java.net.InetAddress)
 * 
 * @author cvagheti
 * 
 */
@SessionScoped
public class SistemaImpressaoClienteManager implements Serializable {

	private static final String CONTENT_DISPOSITION = "Content-disposition";

	private static final String CONTENT_TYPE_JP_AUTO_FILE = "application/jp_auto_file";

	private static final long serialVersionUID = -4320933885124131023L;

	private static final Log LOG = LogFactory.getLog(SistemaImpressaoClienteManager.class);

	private List<Object> fila = new ArrayList<Object>();

	public boolean isEmpty() {
		return this.fila.isEmpty();
	}

	public int count() {
		return this.fila.size();
	}

	public void put(String documento) {
		this.fila.add(documento);
	}

	public void put(JasperPrint documento) {
		this.fila.add(documento);
	}

	public void put(ByteArrayOutputStream documento){
		this.fila.add(documento);
	}

	public String download() throws IOException, JRException {

		if (this.fila.isEmpty()) {
			return null;
		}

		if (this.isMultiplosJasper()) {
			this.downloadAll();
		} else {
			this.downloadPrimeiro();
		}
		
		return null;
	}

	/**
	 * Executa o download do primeiro da fila independente do formato.
	 * 
	 * @throws IOException
	 * @throws JRException
	 */
	private void downloadPrimeiro() throws IOException, JRException {
		FacesContext fc = FacesContext.getCurrentInstance();
		HttpServletResponse response = (HttpServletResponse) fc
				.getExternalContext().getResponse();
		response.setContentType(CONTENT_TYPE_JP_AUTO_FILE);

		OutputStream responseOutputStream = response.getOutputStream();

		Object object = this.fila.get(0);
		if (object instanceof String) {
				response.addHeader(CONTENT_DISPOSITION, "inline;filename=documento.jpt");
			String documento = (String) object;
			IOUtils.write(documento.getBytes(), responseOutputStream);

		} else if (object instanceof JasperPrint) {
				ByteArrayOutputStream bos = writePdf(object);
				response.addHeader(CONTENT_DISPOSITION, "inline;filename=documento.jpp");
				IOUtils.write(bos.toByteArray(), responseOutputStream);
			} else if (object instanceof ByteArrayOutputStream) {
				ByteArrayOutputStream bos = (ByteArrayOutputStream)object;
	            response.addHeader(CONTENT_DISPOSITION, "inline;filename=documento.jpp");
			IOUtils.write(bos.toByteArray(), responseOutputStream);
			} 
			else {
				LOG.warn(String.format("tipo de documento desconhecido %s",	object.getClass()));
			}
		responseOutputStream.flush();
		responseOutputStream.close();
		fc.responseComplete();
		fila.remove(0);
	}

	/**
	 * Retorna true se houver mais de um relatório do tipo JasperPrint para
	 * download.
	 * 
	 * @return
	 */
	private boolean isMultiplosJasper() {
		int count = 0;
		for (Object obj:this.fila){
			if (obj instanceof JasperPrint) {
				count++;
			}
		}
		
		return count > 1;
	}

	/**
	 * Executa o download de vários arquivos do tipo JasperPrint
	 * concatenando-os.
	 * 
	 * @throws IOException
	 * @throws JRException
	 */
	private String downloadAll() throws IOException, JRException {
		if (fila.size() > 1 && fila.get(0) instanceof JasperPrint) {
			FacesContext fc = FacesContext.getCurrentInstance();
			HttpServletResponse response = (HttpServletResponse) fc.getExternalContext().getResponse();
			response.setContentType("application/jp_auto_file");
			OutputStream out = response.getOutputStream();
			String fileName = ((JasperPrint) fila.get(0)).getName();
            response.addHeader(CONTENT_DISPOSITION, "inline;filename=" + fileName+fila.size() + ".jpp");
            
			List<JasperPrint> list = new ArrayList<JasperPrint>();
			for (Object o : fila){
				list.add((JasperPrint)o);
			}
			JRPdfExporter exporter = new JRPdfExporter();
			exporter.setExporterInput(SimpleExporterInput.getInstance(list));
			exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(out));
			SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
			exporter.setConfiguration(configuration);						
			exporter.exportReport();

			out.flush();
			out.close();
			fc.responseComplete();
			
			fila.clear();

		}else if (!fila.isEmpty()) {
			return download();
		}
		return null;
	}
	
	/**
     * Retorna o byte array do pdf.
     * 
     * @param response
     * @param object
     * @return
     * @throws IOException
     * @throws JRException 
     */
    private ByteArrayOutputStream writePdf(Object object) throws IOException, JRException {
            JasperPrint documento = (JasperPrint) object;

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            JasperExportManager.exportReportToPdfStream(documento, outputStream);

            return outputStream;
    }
    
}