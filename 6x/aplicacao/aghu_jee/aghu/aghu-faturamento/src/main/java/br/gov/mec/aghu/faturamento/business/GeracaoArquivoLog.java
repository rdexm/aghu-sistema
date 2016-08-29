package br.gov.mec.aghu.faturamento.business;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.Priority;

public class GeracaoArquivoLog {

	private static final String EXTRA_LOG_ENTRY_FORMAT = "%d{yyyy-MM-dd HH:mm:ss} [%t] %-5p - %m%n";
	private static final String EXTENSAO_ARQUIVO_LOG_EQ_TXT = ".txt";
	private static final String PREFIXO_ARQUIVO_LOG = "log-";

	private Logger extraLog = null; //NOPMD
	private URI logFileURI = null;
	private String lastMessage = null;
	@SuppressWarnings("deprecation")
	private Priority prioridade = Priority.INFO;

	protected URI getExtraFileURI()
			throws IOException {

		File file = null;

		if (this.logFileURI == null) {
			file = File.createTempFile(PREFIXO_ARQUIVO_LOG, EXTENSAO_ARQUIVO_LOG_EQ_TXT);
			this.logFileURI = file.toURI();
		}

		return this.logFileURI;
	}

	protected Logger getExtraFileLog()
			throws IOException {

		FileAppender appender = null;
		PatternLayout layout = null;
		String pattern = null;
		String filename = null;
		URI fileUri = null;

		if (this.extraLog == null) {
			fileUri = this.getExtraFileURI();
			filename = fileUri.getPath();
			pattern = EXTRA_LOG_ENTRY_FORMAT;
			layout = new PatternLayout(pattern);
			appender = new FileAppender(layout, filename);
			this.extraLog = Logger.getLogger(this.getClass());
			this.extraLog.addAppender(appender);
		}

		return this.extraLog;
	}

	public GeracaoArquivoLog() {

		super();
	}
	
	public Priority getPrioridade() {
	
		return this.prioridade;
	}
	
	public void setPrioridade(Priority prioridade) {
	
		if (prioridade != null) {
			this.prioridade = prioridade;			
		}
	}
	
	public Logger getExtraLog() {
	
		return this.extraLog;
	}
	
	public URI getLogFileURI() {
	
		return this.logFileURI;
	}

	public void log(Priority prioridade, String mensagem, Throwable excessao) throws IOException {
		
		if (prioridade == null) {
			throw new IllegalArgumentException("Parametro prioridade nao informado!!!");
		}
		if (mensagem == null) {
			throw new IllegalArgumentException("Parametro mensagem nao informado!!!");
		}
		if (!mensagem.equals(this.lastMessage)) {
			this.getExtraFileLog().log(prioridade, mensagem, excessao);
			this.lastMessage = mensagem;
		}
	}

	public void log(String mensagem, Throwable excessao) throws IOException {
		
		this.log(this.prioridade, mensagem, excessao);
	}

	public void log(String mensagem) throws IOException {
		
		this.log(mensagem, null);
	}
}
