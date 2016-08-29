package br.gov.mec.aghu.core.merges;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

public class GenereteAlfa1MergeReport extends GenereteBasicMergeReport {
	
	

	public GenereteAlfa1MergeReport(String fileName) {
		super(fileName);
	}

	
	public void execute(String mergeReportFileName, List<MergeItem> mergeItems) throws IOException {
		
		@SuppressWarnings("unchecked")
		Collection<MergeItem> itensFilter = CollectionUtils.select(mergeItems, new Predicate() {
			@Override
			public boolean evaluate(Object obj) {
				if (obj instanceof MergeItem) {
					MergeItem item = (MergeItem) obj;
					for (String moduleTarget : GenereteBasicMergeReport.LIST_MODULES_ALFA1) {
						if (item.hasValidChanges() && item.getModules().contains(moduleTarget)) {
							return true;
						}
					}
				}
				return false;
			}
		});
		
		List<MergeItem> list = new ArrayList<>(itensFilter.size());
		list.addAll(itensFilter);
		Collections.sort(list, new Comparator<MergeItem>() {
			@Override
			public int compare(MergeItem o1, MergeItem o2) {
				return o1.getRevisionAsInteger().compareTo(o2.getRevisionAsInteger());
			}
		});
		
		this.setShowChangeItems(true);
		super.execute(mergeReportFileName, list);
	}// execute

}
