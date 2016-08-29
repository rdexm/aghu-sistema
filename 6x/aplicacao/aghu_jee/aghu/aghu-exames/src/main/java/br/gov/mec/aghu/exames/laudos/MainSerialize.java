package br.gov.mec.aghu.exames.laudos;

import static net.sf.dynamicreports.report.builder.DynamicReports.export;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import org.apache.log4j.Logger;

import net.sf.dynamicreports.jasper.builder.export.JasperHtmlExporterBuilder;
import net.sf.dynamicreports.report.exception.DRException;

public class MainSerialize {
	
	private static final String DIR_TMP = "/home/cvagheti/tmp/";
	static final Logger LOG = Logger.getLogger(MainSerialize.class.getName());

	public static void main(String[] args) throws IOException {
		String fileName = "java_1416317739467.ser";

		File ser = new File(DIR_TMP + fileName);

		ExamesListaVO deserialize = (ExamesListaVO) deserialize(ser);
//		System.out.println(deserialize.getExames().size());

//		List<ExameVO> exs = new ArrayList<ExameVO>();
		// exs.addAll(deserialize.getExames().subList(61, 62));
		// exs.add(deserialize.getExames().get(0));
		// deserialize.setExames(exs);

		ILaudoReport laudo = new LaudoSamis();
		laudo.setExamesLista(deserialize);
		//
		// laudo.reportHeaderConfig();
		// laudo.reportFooterConfig();
		// //
		// laudo.runReport();
		laudo.executar();
		try {
			laudo.toPdf(new FileOutputStream(DIR_TMP + fileName
					+ ".pdf"));
			// new FileOutputStream(
			// "/home/cvagheti/tmp/" + fileName + ".html")
			JasperHtmlExporterBuilder exporter = export
					.htmlExporter(
							new FileOutputStream(DIR_TMP
									+ fileName + ".html"))
					.setImagesDirName(DIR_TMP)
					.setOutputImagesToDir(true).setUsingImagesToAlign(false);
			laudo.toHtml(exporter);
			laudo.toJrXml(new FileOutputStream(DIR_TMP + fileName
					+ ".jrxml"));
		} catch (DRException e1) {
			LOG.error("",e1);
		}
		//
		try {
			laudo.show();
		} catch (DRException e) {
			LOG.error("",e);
		}

	}

	private static Object deserialize(File ser) {
		Object pDetails = null;
		FileInputStream fis = null;
		ObjectInputStream in = null;
		try {
			fis = new FileInputStream(ser);
			in = new ObjectInputStream(fis);
			pDetails = in.readObject();
			in.close();
		} catch (IOException ex) {
			LOG.error("",ex);
		} catch (ClassNotFoundException ex) {
			LOG.error("",ex);
		}

		return pDetails;
	}

}
