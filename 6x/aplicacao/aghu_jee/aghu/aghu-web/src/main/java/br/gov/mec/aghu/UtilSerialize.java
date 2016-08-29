package br.gov.mec.aghu;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Calendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class UtilSerialize {
	private static final Log LOG = LogFactory.getLog(UtilSerialize.class);

	public static void serializa(Object o) {
		long timeInMillis = Calendar.getInstance().getTimeInMillis();
		String filename = System.getProperty("java.io.tmpdir") + "/java_"
				+ timeInMillis + ".ser";
		LOG.info("Objeto serializado em " + filename);
		FileOutputStream fos = null;
		ObjectOutputStream out = null;
		try {
			fos = new FileOutputStream(filename);
			out = new ObjectOutputStream(fos);
			out.writeObject(o);
			out.close();
		} catch (IOException ex) {
			LOG.error("Erro ao serializar objeto", ex);
		}

	}

}
