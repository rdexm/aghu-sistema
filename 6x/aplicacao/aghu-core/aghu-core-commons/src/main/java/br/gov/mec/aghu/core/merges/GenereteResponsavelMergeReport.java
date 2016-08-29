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

public class GenereteResponsavelMergeReport extends GenereteBasicMergeReport {
	
	
	public GenereteResponsavelMergeReport(String fileName) {
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
		
		Map<String, List<MergeItem>> mapResponsaveis = new HashMap<>();
		
		for (MergeItem mergeItem : itensFilter) {
			String user = mergeItem.getUserOriginal();
			
			if (user == null || "".equals(user)) {
				System.out.println(mergeItem); //NOPMD
			}
			
			List<MergeItem> itens = null;
			if (mapResponsaveis.containsKey(user)) {
				itens = mapResponsaveis.get(user);
			} else {
				itens = new LinkedList<>();
				mapResponsaveis.put(user, itens);
			}
			itens.add(mergeItem);
		}//FOR mergeItems
		
		
		Set<String> responsaveis = mapResponsaveis.keySet();
		StringBuilder strResponsaveis = new StringBuilder();
		for (String resp : responsaveis) {
			
			List<MergeItem> itens = mapResponsaveis.get(resp);
			
			strResponsaveis.append(resp).append(MergeItem.SEPARADOR_INFO);
			strResponsaveis.append(itens.size()).append(SEPARATOR_LINHA);
		}
		
		//super.execute(mergeReportFileName, mergeItems);
		File arquivoMergeRelatorio = new File(mergeReportFileName);
		FileUtils.writeStringToFile(arquivoMergeRelatorio, strResponsaveis.toString());
	}

}
