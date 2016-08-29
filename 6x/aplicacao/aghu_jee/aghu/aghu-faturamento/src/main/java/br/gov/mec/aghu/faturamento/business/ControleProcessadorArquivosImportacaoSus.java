package br.gov.mec.aghu.faturamento.business;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ControleProcessadorArquivosImportacaoSus {

	private static final Log LOG = LogFactory.getLog(ControleProcessadorArquivosImportacaoSus.class);

	protected static final String ENCODE = "ISO-8859-1";
	protected static final String LINE_SEPARATOR = System.getProperty("line.separator");
	
	private long nrRegistrosProcesso;
	private int nrRegistrosProcessados;
	private long nrRegistrosProcessoGeral;
	private int nrRegistrosProcessadosGeral;
	private int partial;

	// Campos utilizados para atualização do arquivo
	private StringBuilder logRetorno;
	private Date inicio;

	private Writer out;
	
	public ControleProcessadorArquivosImportacaoSus() {
		inicio = new Date();
		logRetorno = new StringBuilder();
	}
	
	public void iniciarLogRetorno(){
		if(logRetorno != null){
			logRetorno.setLength(0);
			
		} else {
			logRetorno = new StringBuilder();
		}
	}
	

	public void abrirArquivoLog(final File file) {
		if (file != null) {
			try {
				this.out = new OutputStreamWriter(new FileOutputStream(file), ENCODE);
			} catch (final Exception e) {
				LOG.error(e.getMessage(), e);
				LOG.error(e.getMessage());
			}
		}
	}
	
	/**
	 * Grava log no arquivo de log
	 * 
	 * ORADBs:
	 * GERA_LOG_PROC_X_COMPAT
	 */
	public void gravarLog(final String linha) {
		if (this.out != null) {
			try {
				this.out.write(linha);
				this.out.write(LINE_SEPARATOR);
			} catch (final IOException e) {
				LOG.error(e.getMessage(), e);
				LOG.error(e.getMessage());
			}
		}
	}

	public void fechaArquivoLog() {
		if (this.out != null) {
			try {
				this.out.close();
			} catch (final IOException e) {
				LOG.error(e.getMessage(), e);
				LOG.error(e.getMessage());
			}
		}
	}
	
	public long getNrRegistrosProcesso() {
		return nrRegistrosProcesso;
	}

	public long getNrRegistrosProcessoGeral() {
		return nrRegistrosProcessoGeral;
	}

	public int getNrRegistrosProcessadosGeral() {
		return nrRegistrosProcessadosGeral;
	}

	public int getPartial() {
		return partial;
	}
	public void setPartial(int partial) {
		this.partial = partial;
	}
	
	public int getNrRegistrosProcessados() {
		return nrRegistrosProcessados;
	}

	public void incrementaNrRegistrosProcesso() {
		nrRegistrosProcesso++;		
	}
	
	public void incrementaNrRegistrosProcesso(int i) {
		nrRegistrosProcesso +=i;		
	}

	
	public void incrementaNrRegistrosProcessados() {
		nrRegistrosProcessados++;
	}
	public void incrementaNrRegistrosProcessados(int i) {
		nrRegistrosProcessados += i;		
	}

	public void setNrRegistrosProcesso(long nrRegistrosProcesso) {
		this.nrRegistrosProcesso = nrRegistrosProcesso;
	}

	public void setNrRegistrosProcessados(int nrRegistrosProcessados) {
		this.nrRegistrosProcessados = nrRegistrosProcessados;
	}

	public void setNrRegistrosProcessoGeral(long nrRegistrosProcessoGeral) {
		this.nrRegistrosProcessoGeral = nrRegistrosProcessoGeral;
	}

	public void setNrRegistrosProcessadosGeral(int nrRegistrosProcessadosGeral) {
		this.nrRegistrosProcessadosGeral = nrRegistrosProcessadosGeral;
	}

	public StringBuilder getLogRetorno() {
		return logRetorno;
	}

	public void setLogRetorno(StringBuilder logRetorno) {
		this.logRetorno = logRetorno;
	}

	public Date getInicio() {
		return inicio;
	}

	public void setInicio(Date inicio) {
		this.inicio = inicio;
	}


}
