package br.gov.mec.aghu.exames.laudos;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import net.sf.dynamicreports.jasper.builder.export.JasperHtmlExporterBuilder;
import net.sf.dynamicreports.report.exception.DRException;

/**
 * Interface para classes de geração de laudos de exames.
 * 
 * @author
 * 
 */
public interface ILaudoReport {

	void toJrXml(OutputStream outputStream) throws DRException;

	void toPdf(OutputStream outputStream) throws DRException;
	
	void toHtml(OutputStream outputStream) throws DRException;
	void toHtml(JasperHtmlExporterBuilder exporter) throws DRException;

	void show() throws DRException;

	void setExamesLista(ExamesListaVO exames);
	
	ExamesListaVO getExamesLista();
	
	void executar() throws FileNotFoundException, IOException;
	
	void setCaminhoLogo(String caminho);
	
	String getCaminhoLogo();
	
	List<String> getFontsAlteradas();

}
