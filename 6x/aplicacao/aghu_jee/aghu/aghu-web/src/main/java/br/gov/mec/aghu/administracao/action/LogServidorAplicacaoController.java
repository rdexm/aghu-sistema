package br.gov.mec.aghu.administracao.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.vo.LogServidorAplicacaoVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.Severity;


public class LogServidorAplicacaoController extends ActionController {
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
		this.tempoRefresh = 15;
		this.tamanhoArquivo = 250;		
	}

	private static final Log LOG = LogFactory.getLog(LogServidorAplicacaoController.class);

	private static final long serialVersionUID = -3767585182137474755L;

	private String arquivo;

	private File tempFile;

	private boolean gerouArquivo;
	
	private boolean pause = false;

	private String logContent;
	
	// tempo padrão: 15 segundos
	private int tempoRefresh;

	// tamanho padrão: 250 kb
	private int tamanhoArquivo;

	private static final String LOGSERVIDORAPLICACAOLIST = "logServidorAplicacaoList";
	private static final String VISUALIZARLOGSERVIDORAPLICACAO = "visualizarLogServidorAplicacao";
	
	private static final File SERVER_LOG_DIR = new File(
			System.getProperty("jboss.server.log.dir"));

	private static final File SERVER_TEMP_DIR = new File(
			System.getProperty("jboss.server.temp.dir"));

	private static final Comparator<File> FILE_COMPARATOR = new Comparator<File>() {

		@Override
		public int compare(File file1, File file2) {
			if (file1.isDirectory() && !file2.isDirectory()) {
				return -1;
			} else if (!file1.isDirectory() && file2.isDirectory()) {
				return 1;
			} else {
				return file1.getName().compareTo(file2.getName());
			}
		}

	};
	
	public String voltar() {
		this.tempoRefresh = 15;
		this.tamanhoArquivo = 250;
		return LOGSERVIDORAPLICACAOLIST;
	}
	
	public String visualizarLog() {
		return VISUALIZARLOGSERVIDORAPLICACAO;
	}
	
	public List<LogServidorAplicacaoVO> getArquivosLog() {
		List<LogServidorAplicacaoVO> lista = new ArrayList<LogServidorAplicacaoVO>();

		if (SERVER_LOG_DIR.exists() && SERVER_LOG_DIR.isDirectory()) {
			int size = SERVER_LOG_DIR.getAbsolutePath().length();

			if (SERVER_LOG_DIR != null) {
				List<File> files = new ArrayList<File>();
				this.obterArquivosLog(SERVER_LOG_DIR, files);

				for (File file : files) {
					LogServidorAplicacaoVO vo = new LogServidorAplicacaoVO();

					vo.setCaminhoArquivo(file.getAbsolutePath().substring(size));
					vo.setTamanho(file.length());

					lista.add(vo);
				}
			}
		}

		return lista;
	}

	private void obterArquivosLog(File baseDir, List<File> files) {
		File[] baseDirFiles = baseDir.listFiles();
		Arrays.sort(baseDirFiles, FILE_COMPARATOR);

		for (File file : baseDirFiles) {
			if (file.isDirectory()) {
				obterArquivosLog(file, files);
			} else {
				files.add(file);
			}
		}
	}

	public void carregarLog() {
		try {
			if (logContent == null) {
				this.tempoRefresh = 15;
				this.tamanhoArquivo = 250;
			}
			File file = new File(SERVER_LOG_DIR, this.arquivo);

			long auxTamanho = file.length() - (tamanhoArquivo * 1024);

			if (auxTamanho > 0) {
				RandomAccessFile raf = new RandomAccessFile(file, "r");
				raf.seek(auxTamanho);

				StringBuffer sb = new StringBuffer();

				String line = null;
				while ((line = raf.readLine()) != null) {
					sb.append(line);
					sb.append('\n');
				}
				
				raf.close();
				
				this.logContent = sb.toString();
			} else {
				this.logContent = FileUtils.readFileToString(file);
			}
		} catch (IOException e) {
			LOG.error("Erro ao realizar a leitura do arquivo de log "
					+ this.arquivo, e);
		}
	}
	
	public void baixarLog(String arquivoLog) {
		this.preparaDownloadArquivo(arquivoLog);
		this.dispararDownload();
	}
	
	public void preparaDownloadArquivo(String arquivoLog) {
		this.gerouArquivo = false;
		if (StringUtils.isNotBlank(arquivoLog)) {
			LOG.debug("Arquivo de log selecionado: " + arquivoLog);
			File file = new File(SERVER_LOG_DIR, arquivoLog);

			String fileName = file.getName() + ".zip";
			this.tempFile = new File(SERVER_TEMP_DIR, fileName);

			try {
				// Create the ZIP file
				ZipOutputStream out = new ZipOutputStream(new FileOutputStream(
						this.tempFile));

				FileInputStream in = new FileInputStream(file);

				// Add ZIP entry to output stream.
				out.putNextEntry(new ZipEntry(file.getName()));

				// Transfer bytes from the file to the ZIP file
				int len;
				byte[] buffer = new byte[1024];
				while ((len = in.read(buffer)) > 0) {
					out.write(buffer, 0, len);
				}

				// Complete the entry
				out.closeEntry();
				in.close();

				this.gerouArquivo = true;

				// Complete the ZIP file
				out.close();
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
				this.apresentarMsgNegocio(Severity.ERROR,
						"Ocorreu um erro ao gerar o arquivo compactado.");
			}
		}
	}

	public void dispararDownload() {

		if (this.tempFile != null && this.tempFile.exists()) {
			try {
				download(this.tempFile, "application/x-zip-compressed");
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
				this.apresentarMsgNegocio(Severity.ERROR,
						"Ocorreu um erro ao gerar o arquivo compactado.");
			}
			
		}

		this.gerouArquivo = false;
	}

	public String getConteudoArquivo() {
		try {
			File file = new File(SERVER_LOG_DIR, this.arquivo);

			long auxTamanho = file.length() - (tamanhoArquivo * 1024);

			if (auxTamanho > 0) {
				RandomAccessFile raf = new RandomAccessFile(file, "r");
				raf.seek(auxTamanho);

				StringBuffer sb = new StringBuffer();

				String line = null;
				while ((line = raf.readLine()) != null) {
					sb.append(line);
					sb.append('\n');
				}
				
				raf.close();
				
				return sb.toString();
			} else {
				return FileUtils.readFileToString(file);
			}
		} catch (IOException e) {
			return "Erro ao realizar a leitura do arquivo de log "
					+ this.arquivo;
		}
	}

	public String getArquivo() {
		return arquivo;
	}

	public void setArquivo(String arquivo) {
		this.arquivo = arquivo;
	}

	public boolean isGerouArquivo() {
		return gerouArquivo;
	}

	public void setGerouArquivo(boolean gerouArquivo) {
		this.gerouArquivo = gerouArquivo;
	}

	public int getTempoRefresh() {
		return tempoRefresh;
	}

	public void setTempoRefresh(int tempoRefresh) {
		this.tempoRefresh = tempoRefresh;
	}

	public int getTamanhoArquivo() {
		return tamanhoArquivo;
	}

	public void setTamanhoArquivo(int tamanhoArquivo) {
		this.tamanhoArquivo = tamanhoArquivo;
	}

	public String getLogContent() {
		return logContent;
	}

	public void setLogContent(String logContent) {
		this.logContent = logContent;
	}

	
	public boolean isPause() {
		return pause;
	}

	
	public void setPause(boolean pause) {
		this.pause = pause;
	}

}
