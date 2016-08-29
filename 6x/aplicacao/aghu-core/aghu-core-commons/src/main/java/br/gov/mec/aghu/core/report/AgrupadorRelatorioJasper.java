package br.gov.mec.aghu.core.report;

import java.io.ByteArrayOutputStream;
import java.util.LinkedList;
import java.util.List;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRPdfExporterParameter;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;


/**
 * Classe de implementacao para os documentos gerados com JASPER<br>
 * para agrupar varios relatorios.<br>
 * 
 * @author rcorvalao
 *
 */
public class AgrupadorRelatorioJasper {
	
	private List<JasperPrint> listJasperPrint;
	
	protected List<JasperPrint> getListJasperPrint() {
		if (this.listJasperPrint == null) {
			setListJasperPrint(new LinkedList<JasperPrint>());
		}
		return this.listJasperPrint;
	}

	/**
	 * Para facilitar o controle interno desta classe:<br>get
	 * 1) Nao deve ser permitido o set da lista inteira. Deve ser usado o metodo de adicao.
	 * 
	 * @param listJasperPrint
	 */
	private void setListJasperPrint(List<JasperPrint> listJasperPrint) {
		this.listJasperPrint = listJasperPrint;
	}

	public void addReport(JasperPrint aReport) {
		if (aReport != null) {
			this.getListJasperPrint().add(aReport);
		}
	}
	
	public ByteArrayOutputStream exportReportAsOutputStream() throws JRException, ApplicationBusinessException {
		ByteArrayOutputStream outPutStream = new ByteArrayOutputStream();
		
		if (!getListJasperPrint().isEmpty()) {
			
			this.exportReport(getListJasperPrint(), JRExporterParameter.OUTPUT_STREAM, outPutStream);
			
//			JRPdfExporter exp = new JRPdfExporter();
//			exp.setParameter(JRPdfExporterParameter.JASPER_PRINT_LIST, listJasperPrint); 
//	        exp.setParameter(JRExporterParameter.OUTPUT_STREAM, outPutStream);
//	        exp.exportReport();			
		}
		
		return outPutStream;
	}
	
	public JasperPrint exportReportAsJasperPrint() throws JRException, ApplicationBusinessException {
		JasperPrint returnValue = new JasperPrint();
		
		if (!getListJasperPrint().isEmpty()) {
			
			this.exportReport(getListJasperPrint(), JRExporterParameter.JASPER_PRINT, returnValue);
			
		}
		
		return returnValue;
	}
	
	protected Object exportReport(List<JasperPrint> jasperPrints, JRExporterParameter exportParam, Object exportValue) throws JRException {
		if (!jasperPrints.isEmpty()) {
			JRPdfExporter exp = new JRPdfExporter();
			
			exp.setParameter(JRPdfExporterParameter.JASPER_PRINT_LIST, jasperPrints); 
	        exp.setParameter(exportParam, exportValue);
	        
	        exp.exportReport();			
		}
		
		return exportValue;
	}
	
}
