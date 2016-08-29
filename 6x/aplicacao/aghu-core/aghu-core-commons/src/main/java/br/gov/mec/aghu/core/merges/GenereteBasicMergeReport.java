package br.gov.mec.aghu.core.merges;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;


/**
 * Formata um arquivos com todos os MergeItems encontrados.
 * Utiliza o toString do pojos.
 * 
 * @author rcorvalao
 *
 */
public class GenereteBasicMergeReport {
	
	public static final String SEPARATOR_LINHA = "\n";
	
	public static final String RODAPE = "Rodape";
	
	public static final List<String> LIST_MODULES_ALFA1 = Arrays.asList("paciente", "controlepaciente", "ambulatorio", "registrocolaborador", "cups");
	
	/**
	 * por default nao apresenta os changeItens.
	 */
	private boolean showChangeItems = false;

	/**
	 * Nome do arquivo de origem, ou seja, a fonte dos dados.
	 * ChangeLog de origem.
	 */
	private String baseFileName;
	
	
	/**
	 * Nome do arquivo de origem, ou seja, a fonte dos dados.
	 * ChangeLog de origem.
	 * 
	 * @param fileName
	 */
	public GenereteBasicMergeReport(String fileName) {
		super();
		this.baseFileName = fileName;
	}

	
	protected String getBaseFileName() {
		return baseFileName;
	}
	
	public void execute(String mergeReportFileName, Collection<MergeItem> mergeItems) throws IOException {
		File arquivoMergeRelatorio = new File(mergeReportFileName);
		StringBuilder resultMerge = new StringBuilder();
		resultMerge.append(MergeItem.header()).append(SEPARATOR_LINHA);
		if (isShowChangeItems()) {
			resultMerge.append(ChangeItem.header()).append(SEPARATOR_LINHA);
		}
		
		int validCount = 0;
		int invalid = 0;
		int max = 0;
		int min = Integer.MAX_VALUE;
		for (MergeItem mergeItem : mergeItems) {
			if (mergeItem.getRevisionAsInteger() > max) {
				max = mergeItem.getRevisionAsInteger();
			}
			if (mergeItem.getRevisionAsInteger() < min) {
				min = mergeItem.getRevisionAsInteger();
			}
			resultMerge.append(mergeItem).append(SEPARATOR_LINHA);
			if (isShowChangeItems()) {
				showChangeItems(mergeItem, resultMerge);
			}
			
			if (mergeItem.hasValidChanges()) {
				validCount = validCount + 1;
			} else {
				invalid = invalid + 1;
			}
		}
		
		resultMerge.append(RODAPE).append(SEPARATOR_LINHA);
		resultMerge.append("Revision SVN - Menor: ").append(min).append(" Maior: ").append(max).append(SEPARATOR_LINHA);
		resultMerge.append("Total de Itens: ").append(mergeItems.size()).append(SEPARATOR_LINHA);
		resultMerge.append("Items validos  : ").append(validCount).append(SEPARATOR_LINHA);
		resultMerge.append("Items NAO validos: ").append(invalid).append(SEPARATOR_LINHA);
		resultMerge.append("ChangeLog Origem: ").append(this.baseFileName).append(SEPARATOR_LINHA);
		
		FileUtils.writeStringToFile(arquivoMergeRelatorio, resultMerge.toString());

	}// execute

	private void showChangeItems(MergeItem mergeItem, StringBuilder file) {
		for (ChangeItem item : mergeItem.getValidChangeItems()) {
			file.append(item).append(SEPARATOR_LINHA);
		}
	}

	public boolean isShowChangeItems() {
		return showChangeItems;
	}

	public void setShowChangeItems(boolean showChangeItems) {
		this.showChangeItems = showChangeItems;
	}

}
