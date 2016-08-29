package br.gov.mec.aghu.core.merges;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.io.FileUtils;

public class GenerateRevisionsPorModuloMergeReport extends GenereteBasicMergeReport {
	
	public GenerateRevisionsPorModuloMergeReport(String fileName) {
		super(fileName);
	}

	@Override
	public void execute(String mergeReportFileName, Collection<MergeItem> mergeItems) throws IOException {
		
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
		
		Map<String, List<MergeItem>> mapRevionsPorModulos = new HashMap<>();
		
		for (MergeItem mergeItem : itensFilter) {
			SortedSet<String> modulos = mergeItem.getModules();
			for (String modulo : modulos) {
				
				List<MergeItem> itens = null;
				if (mapRevionsPorModulos.containsKey(modulo)) {
					itens = mapRevionsPorModulos.get(modulo);
				} else {
					itens = new LinkedList<>();
					mapRevionsPorModulos.put(modulo, itens);
				}
				itens.add(mergeItem);
			}//FOR modulos
		}//FOR mergeItems
		
		
		Set<String> nomeModulos = mapRevionsPorModulos.keySet();
		List<String> listaModulos = new ArrayList<>(nomeModulos.size());
		listaModulos.addAll(nomeModulos);
		Collections.sort(listaModulos);
		
		StringBuilder strModulos = new StringBuilder();
		strModulos.append("Modulo").append(MergeItem.SEPARADOR_INFO);
		strModulos.append("Qt Revisions").append(MergeItem.SEPARADOR_INFO);
		strModulos.append("Qt Mudancas").append(SEPARATOR_LINHA);
		for (String moduleName : nomeModulos) {
			List<MergeItem> itens = mapRevionsPorModulos.get(moduleName);
			
			strModulos.append(moduleName).append(MergeItem.SEPARADOR_INFO);
			strModulos.append(itens.size()).append(MergeItem.SEPARADOR_INFO);
			strModulos.append(countChangeItems(itens)).append(SEPARATOR_LINHA);
		}
		
		//super.execute(mergeReportFileName, mergeItems);
		File arquivoMergeRelatorio = new File(mergeReportFileName);
		FileUtils.writeStringToFile(arquivoMergeRelatorio, strModulos.toString());
	}

	private Object countChangeItems(List<MergeItem> itens) {
		int returnValue = 0;
		
		for (MergeItem mergeItem : itens) {
			returnValue = returnValue + mergeItem.getValidChangeItems().size();
		}
		
		return returnValue;
	}


}
