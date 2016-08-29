package br.gov.mec.aghu.core.merges;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.io.FileUtils;

/**
 * Gera a lista de Mudancas entre os branches 5 e 6.
 * O arquivo gerado sera no formato estabelicido em ShowChangeItemBuilder.
 * 
 * @author rcorvalao
 * @see ShowChangeItemBuilder
 */
public class GenerateMudancaMergeReport extends GenereteBasicMergeReport {
	
	
	public GenerateMudancaMergeReport(String fileName) {
		super(fileName);
	}

	@Override
	public void execute(String mergeReportFileName, Collection<MergeItem> mergeItems) throws IOException {
		List<ChangeItem> mudancasList = getMudancas(mergeItems);
		
		StringBuilder strModulos = new StringBuilder();
		ShowChangeItemBuilder builder = new ShowChangeItemBuilder();
		
		strModulos.append(builder.getHeader()).append(SEPARATOR_LINHA);
		for (ChangeItem change : mudancasList) {
			strModulos.append(builder.build(change)).append(SEPARATOR_LINHA);
		}
		
		Map<String, Integer> mapRevisionMaiorMenor = getMapRevisionMaiorMenor(mudancasList);
		
		strModulos.append(MergeItem.SEPARADOR_INFO).append(MergeItem.SEPARADOR_INFO).append(RODAPE).append(SEPARATOR_LINHA);
		strModulos.append(MergeItem.SEPARADOR_INFO).append(MergeItem.SEPARADOR_INFO).append("ChangeLog Origem: ").append(this.getBaseFileName()).append(SEPARATOR_LINHA);
		strModulos.append(MergeItem.SEPARADOR_INFO).append(MergeItem.SEPARADOR_INFO).append("Range de Revisions: ").append(mapRevisionMaiorMenor.get("MIN")).append(" <-> ").append(mapRevisionMaiorMenor.get("MAX")).append(SEPARATOR_LINHA);
		strModulos.append(MergeItem.SEPARADOR_INFO).append(MergeItem.SEPARADOR_INFO).append("Total Revisions Validas: ").append(this.getMerteItemsValids(mergeItems).size()).append(SEPARATOR_LINHA);
		strModulos.append(MergeItem.SEPARADOR_INFO).append(MergeItem.SEPARADOR_INFO).append("Total Mudancas Validas: ").append(mudancasList.size()).append(SEPARATOR_LINHA);
		
		
		File arquivoMergeRelatorio = new File(getFileNameCsv(mergeReportFileName));
		FileUtils.writeStringToFile(arquivoMergeRelatorio, strModulos.toString());
	}
	
	protected Map<String, Integer> getMapRevisionMaiorMenor(List<ChangeItem> mudancasList) {
		int max = Integer.MIN_VALUE;
		int min = Integer.MAX_VALUE;
		
		for (ChangeItem changeItem : mudancasList) {
			if (changeItem.getMergeItem().getRevisionAsInteger() > max) {
				max = changeItem.getMergeItem().getRevisionAsInteger();
			}
			if (changeItem.getMergeItem().getRevisionAsInteger() < min) {
				min = changeItem.getMergeItem().getRevisionAsInteger();
			}
		}
		
		Map<String, Integer> map = new HashMap<>();
		map.put("MAX", max);
		map.put("MIN", min);
		
		return map;
	}

	protected String getFileNameCsv(String name) {
		return name + ".csv";
	}
	
	
	protected List<ChangeItem> getMudancas(Collection<MergeItem> mergeItems) {
		Collection<MergeItem> itensFilter = getMerteItemsValids(mergeItems);
		
		List<ChangeItem> mudancasList = new LinkedList<>();
		for (MergeItem mergeItem : itensFilter) {
			List<ChangeItem> changeItems = mergeItem.getValidChangeItems();
			
			mudancasList.addAll(changeItems);
		}//FOR itensFilter
		
		ordenarListaMudancas(mudancasList);
		
		return mudancasList;
	}

	protected void ordenarListaMudancas(List<ChangeItem> mudancasList) {
		//Ordernacao
		Collections.sort(mudancasList, new Comparator<ChangeItem>() {
			@Override
			public int compare(ChangeItem o1, ChangeItem o2) {
				return o1.getMergeItem().getRevisionAsInteger().compareTo(o2.getMergeItem().getRevisionAsInteger());
			}
		});
	}
	
	protected Collection<MergeItem> getMerteItemsValids(Collection<MergeItem> mergeItems) {
		/*
		@SuppressWarnings("unchecked")
		Collection<MergeItem> itensFilter = CollectionUtils.select(mergeItems, new Predicate() {
			@Override
			public boolean evaluate(Object obj) {
				if (obj instanceof MergeItem) {
					MergeItem item = (MergeItem) obj;
					for (String moduleTarget : GenereteAlfa1MergeReport.LIST_MODULES_ALFA1) {
						if (item.hasValidChanges() && item.getModules().contains(moduleTarget)) {
							return true;
						}
					}
				}
				return false;
			}
		});
		*/
		@SuppressWarnings("unchecked")
		Collection<MergeItem> itensFilter = CollectionUtils.select(mergeItems, new Predicate() {
			@Override
			public boolean evaluate(Object obj) {
				if (obj instanceof MergeItem) {
					MergeItem item = (MergeItem) obj;
					return item.hasValidChanges();
				}
				return false;
			}
		});
		
		
		return itensFilter;
	}


}
