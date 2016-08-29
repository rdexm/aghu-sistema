package br.gov.mec.aghu.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ColumnToUppperCase {
	
	private static final Log LOG = LogFactory.getLog(ColumnToUppperCase.class);

	private static final String CAMINHO_CLASSES = "target/classes/br/gov/mec/aghu/util/";
	
	private static final String CAMINHO_CODIGO_FONTE = "src/main/java/br/gov/mec/aghu/model/";

	public static void main(String[] args) {
		String dirPath = ColumnToUppperCase.class.getResource("").getPath();
		int index = dirPath.lastIndexOf(CAMINHO_CLASSES);
		dirPath = dirPath.substring(0, index) + CAMINHO_CODIGO_FONTE;
		
		File dir = new File(dirPath);
		
		if (dir.exists() && dir.isDirectory()) {
			File[] files = dir.listFiles(new ColumnToUppperCase().new InnerFileFileFilter());
			
			int auxTamanho = files.length;
			for(int i = 0; i < auxTamanho; i++) {
				File file = files[i];
				try {
					LOG.info("Processando arquivo " + (i + 1)  + " de " + auxTamanho);
					columnToUpperCase(file);
				} catch (Exception e) {
					LOG.info("ERRO AO PROCESSAR O ARQUIVO #" + (i + 1) + "(" + file.getName() + ")", e);
				}
			}
		}
	}
	
	@SuppressWarnings({"PMD.NPathComplexity"})
	private static void columnToUpperCase(File file) throws IOException {
		StringBuffer conteudo = new StringBuffer(FileUtils.readFileToString(file));
		
		int index = conteudo.indexOf("@Column");
		while (index >= 0) {
			int indexInicio = conteudo.indexOf("(", index);
			int indexFim = conteudo.indexOf(")", index);
			int indexName = conteudo.indexOf("name", index);
			
			if (indexName > indexInicio && indexName < indexFim) {
				int auxIndexInicio = conteudo.indexOf("\"", indexName);
				int auxIndexFim = conteudo.indexOf("\"", auxIndexInicio + 1);
				
				String aux = conteudo.substring(auxIndexInicio, auxIndexFim);

				conteudo.replace(auxIndexInicio, auxIndexFim, aux.toUpperCase());
			}
			
			index = conteudo.indexOf("@Column", index + 1);
		}
		
		index = conteudo.indexOf("@JoinColumn");
		while (index >= 0) {
			int indexInicio = conteudo.indexOf("(", index);
			int indexFim = conteudo.indexOf(")", index);
			
			if (!conteudo.substring(indexInicio, indexFim).contains("@")) {
				int indexName = conteudo.indexOf("name", index);
				
				if (indexName > indexInicio && indexName < indexFim) {
					int auxIndexInicio = conteudo.indexOf("\"", indexName);
					int auxIndexFim = conteudo.indexOf("\"", auxIndexInicio + 1);
					
					String aux = conteudo.substring(auxIndexInicio, auxIndexFim);

					conteudo.replace(auxIndexInicio, auxIndexFim, aux.toUpperCase());
				}
				
				int indexReferencedColumnName = conteudo.indexOf("referencedColumnName", index);
				if (indexReferencedColumnName > indexInicio && indexReferencedColumnName < indexFim) {
					int auxIndexInicio = conteudo.indexOf("\"", indexReferencedColumnName);
					int auxIndexFim = conteudo.indexOf("\"", auxIndexInicio + 1);
					
					String aux = conteudo.substring(auxIndexInicio, auxIndexFim);

					conteudo.replace(auxIndexInicio, auxIndexFim, aux.toUpperCase());
				}
			}
			
			index = conteudo.indexOf("@JoinColumn", index + 1);
		}
		
		index = conteudo.indexOf("@Table");
		if (index >= 0) {
			int indexInicio = conteudo.indexOf("(", index);
			int indexFim = conteudo.indexOf(")", index);
			
			int indexName = conteudo.indexOf("name", index);
			
			if (indexName > indexInicio && indexName < indexFim) {
				int auxIndexInicio = conteudo.indexOf("\"", indexName);
				int auxIndexFim = conteudo.indexOf("\"", auxIndexInicio + 1);
				
				String aux = conteudo.substring(auxIndexInicio, auxIndexFim);

				conteudo.replace(auxIndexInicio, auxIndexFim, aux.toUpperCase());
			}
			
			int indexSchema = conteudo.indexOf("schema", index);
			if (indexSchema > indexInicio && indexSchema < indexFim) {
				int auxIndexInicio = conteudo.indexOf("\"", indexSchema);
				int auxIndexFim = conteudo.indexOf("\"", auxIndexInicio + 1);
				
				String aux = conteudo.substring(auxIndexInicio, auxIndexFim);

				conteudo.replace(auxIndexInicio, auxIndexFim, aux.toUpperCase());
			}
		}

		index = conteudo.indexOf("@SequenceGenerator");
		if (index >= 0) {
			int indexInicio = conteudo.indexOf("(", index);
			int indexFim = conteudo.indexOf(")", index);
			
			int indexSequenceName = conteudo.indexOf("sequenceName", index);
			
			if (indexSequenceName > indexInicio && indexSequenceName < indexFim) {
				int auxIndexInicio = conteudo.indexOf("\"", indexSequenceName);
				int auxIndexFim = conteudo.indexOf("\"", auxIndexInicio + 1);
				
				String aux = conteudo.substring(auxIndexInicio, auxIndexFim);

				conteudo.replace(auxIndexInicio, auxIndexFim, aux.toUpperCase());
			}
		}
		
		FileUtils.writeStringToFile(file, conteudo.toString());
	}

	private class InnerFileFileFilter implements FileFilter {

		@Override
		public boolean accept(File file) {
			return file.isFile();
		}

	}

}
