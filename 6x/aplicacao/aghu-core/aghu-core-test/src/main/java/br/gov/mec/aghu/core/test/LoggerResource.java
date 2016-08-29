package br.gov.mec.aghu.core.test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;

@SuppressWarnings("PMD")
class LoggerResource {

	protected static void iniciarLog() {
		try {
			InputStream fi = ClassLoader.getSystemResourceAsStream("logging.properties");
			LogManager.getLogManager().readConfiguration(fi);
			fi.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
