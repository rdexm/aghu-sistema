package br.gov.mec.aghu.ambulatorio.business;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.ejb.Stateless;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.core.business.BaseBusiness;

/**
 * Classe responsável por exportar o log contendo os erros 
 * de importação/marcação de consultas do SISREG para um arquivo texto compactado.
 * 
 * @author diego.pacheco
 *
 */
@Stateless
public class ExportacaoLogSisregON extends BaseBusiness {





private static final Log LOG = LogFactory.getLog(ExportacaoLogSisregON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6057216490468538880L;
	private static final int NUM_ARQUIVOS_TEMP = 1;
	private static final String NOME_LOG = "Log_Importacao_SISREG";
	private static final String EXTENSAO_LOG = ".txt";
	private static final int TAMANHO_BUFFER = 2048; // 2Kb
	
	/**
	 * Serializa o log em um arquivo texto, e depois
	 * retorna um arquivo compactado contendo esse arquivo texto.
	 * 
	 * @param log
	 * @return
	 */
	public File exportarLog(StringBuilder log) {
		File zipFile = null;
		File file = null;
		InputStream fileIS = null;
		OutputStream fileOS = null;
		ZipOutputStream zipFileOS = null;
		StringBuilder novoLog = null;
		
		try {
			logInfo("Iniciada a exportação do log SISREG.");
			
			Date d1 = new Date();
			
			novoLog = new StringBuilder(log.toString());
			
			//removerLinhasHelp(novoLog);
			
			file = criaArquivoTemporario(NUM_ARQUIVOS_TEMP);
			fileOS = new FileOutputStream(file, true);
			fileOS.write(novoLog.toString().getBytes());

			logInfo("Compactando arquivo.");
			
			zipFile = criaArquivoTemporario(NUM_ARQUIVOS_TEMP);
			zipFileOS = new ZipOutputStream(new FileOutputStream(zipFile));  
			
            byte[] dados = new byte[TAMANHO_BUFFER];  
            
			fileIS = new BufferedInputStream(new FileInputStream(file), TAMANHO_BUFFER);
			
            ZipEntry entry = new ZipEntry(NOME_LOG + EXTENSAO_LOG);   
            zipFileOS.putNextEntry(entry);  
            
            int cont;
            while((cont = fileIS.read(dados, 0, TAMANHO_BUFFER)) != -1) {   
            	zipFileOS.write(dados, 0, cont);   
            }
  
            logInfo("Compactação concluída.");
			Date d2 = new Date();
			logInfo("Tempo total: "
					+ ((d2.getTime() - d1.getTime()) / 1000.0f) + " segundos.");	

			file.delete();
			
			return zipFile;			

		} catch (Exception e) {
			logError("Ocorreu um erro ao gravar o log SISREG: ", e);
			return null;
		} finally {
			if (fileOS != null) {
				IOUtils.closeQuietly(fileOS);
			}
			if (zipFileOS != null) {
				IOUtils.closeQuietly(zipFileOS);
			}
			if (fileIS != null) {
				IOUtils.closeQuietly(fileIS);
			}
			if (file != null) {
				file.delete();
			}
		}
	}
	
	/**
	 * Remove as 5 primeiras linhas (utilizadas para help)
	 * 
	 * @param sb
	 */
//	private void removerLinhasHelp(StringBuilder sb) {
//		Scanner scan = new Scanner(sb.toString());  
//		int contLinhas = 0;
//		while (scan.hasNextLine() && contLinhas < 5) {  
//		   String linha = scan.nextLine();
//		   sb.delete(0, linha.length() + 1);
//		   contLinhas++;
//		}
//	}

	@SuppressWarnings({ "PMD.AvoidThrowingRawExceptionTypes", "PMD.SignatureDeclareThrowsException"})
	private File criaArquivoTemporario(int numArquivos) throws Exception {
		File file = null;
		// Tentarei criar o arquivo temporário por 10 vezes
		int aux = 0;
		do {
			file = new File(numArquivos++ + "_" + new Date().getTime() + ".tmp");
		} while(aux++ < 10 && !file.createNewFile());
		
		if (!file.exists()) {
			throw new Exception("Erro ao criar o arquivo temporário.");
		}
		
		return file;
	}	
	
}
