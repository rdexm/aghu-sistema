package br.gov.mec.aghu.core.merges;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.io.FileUtils;


/**
 * Gera uma lista do pojo ChangeItem agrupadas por Modulos.
 * 
 * 
 * @author rcorvalao
 *
 */
public class GenerateModificacoesPorModuloMergeReport extends GenereteBasicMergeReport {

	
	public GenerateModificacoesPorModuloMergeReport(String fileName) {
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
		
		Map<String, List<ChangeItem>> mapModificacoesPorModulos = new HashMap<>();
		
		for (MergeItem mergeItem : itensFilter) {
			List<ChangeItem> modificacoes = mergeItem.getValidChangeItems();
			for (ChangeItem changeItem : modificacoes) {
				String modulo = changeItem.getModule();
				
				
				List<ChangeItem> itens = null;
				if (mapModificacoesPorModulos.containsKey(modulo)) {
					itens = mapModificacoesPorModulos.get(modulo);
				} else {
					itens = new LinkedList<>();
					mapModificacoesPorModulos.put(modulo, itens);
				}
				itens.add(changeItem);
				
			}//FOR ChangeItem
		}//FOR mergeItems
		
		
		Set<String> nomeModulos = mapModificacoesPorModulos.keySet();
		StringBuilder strModulos = new StringBuilder();
		for (String moduleName : nomeModulos) {
			
			List<ChangeItem> itens = mapModificacoesPorModulos.get(moduleName);
			
			strModulos.append(moduleName).append(MergeItem.SEPARADOR_INFO);
			strModulos.append(itens.size()).append(SEPARATOR_LINHA);
		}
		
		//super.execute(mergeReportFileName, mergeItems);
		File arquivoMergeRelatorio = new File(mergeReportFileName);
		FileUtils.writeStringToFile(arquivoMergeRelatorio, strModulos.toString());
	}

	
}
