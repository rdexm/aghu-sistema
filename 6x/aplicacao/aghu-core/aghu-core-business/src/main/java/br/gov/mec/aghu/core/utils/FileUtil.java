package br.gov.mec.aghu.core.utils;

import java.io.File;
import java.util.List;

import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

public class FileUtil {

	private enum FileUtilExceptionCode implements BusinessExceptionCode { 
		ARQUIVO_NAO_ENCONTRADO
	}	
	
	/** Retorna a pasta do caminho completo de um arquivo
	 * 
	 * Exemplo: c:\pasta\teste.txt retorna c:\pasta\
	 * 
	 * @param caminhoAbsolutoArquivo
	 * @return
	 */
	public static String obtemPasta(String caminhoAbsolutoArquivo) {
		Integer posicao = 0;
		for (int x = caminhoAbsolutoArquivo.length(); x > 1; x--) {
			if (caminhoAbsolutoArquivo.substring(x - 1, x).equals(File.separator)) {
				posicao = x;
				break;
			}
		}
		return caminhoAbsolutoArquivo.substring(0, posicao);				
	}
	
	/** Verifica se o arquivo passado como parâmetro está na lista
	 *  Após isso, verifica se o arquivo existe no sistema de arquivos
	 *  
	 * @param lista
	 * @param arquivo
	 * @return
	 * @throws AGHUNegocioExceptionSemRollback
	 */
	public static String arquivoExiste(List<String> lista, String arquivo) throws ApplicationBusinessException {
		
		String caminhoArq = "";
		for (String aux : lista) {
			if (aux.contains(arquivo)) {
				caminhoArq = aux;
				break;
			}
		}
		
		File f = new File(caminhoArq);  
        if(!f.exists()) {
        	throw new ApplicationBusinessException(FileUtilExceptionCode.ARQUIVO_NAO_ENCONTRADO);
        }
        
    	return caminhoArq;
	}	
	
	/** Verifica se o arquivo passado como parâmetro está na lista
	 *  Após isso, verifica se o arquivo existe no sistema de arquivos
	 *  retorna um boolean informando true se existe e false se não existe
	 *  
	 * @param lista
	 * @param arquivo
	 * @return
	 * @throws AGHUNegocioExceptionSemRollback
	 */
	public static boolean arquivoExisteSemExcecao(List<String> lista, String arquivo) throws ApplicationBusinessException {
		
		String caminhoArq = "";
		for (String aux : lista) {
			if (aux.contains(arquivo)) {
				caminhoArq = aux;
				break;
			}
		}
		
		File f = new File(caminhoArq);  
        if(!f.exists()) {
        	return false;
        }
        
    	return true;
	}	
}
