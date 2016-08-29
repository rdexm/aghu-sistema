package br.gov.mec.aghu.core.merges;

import java.text.SimpleDateFormat;

public class ShowChangeItemBuilder {
	
	
	private static final SimpleDateFormat DATA_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public ShowChangeItemBuilder() {
		super();
	}

	public String getHeader() {
		return  "Responsible" + MergeItem.SEPARADOR_INFO
				+ "Status" + MergeItem.SEPARADOR_INFO
				+ "Revision" + MergeItem.SEPARADOR_INFO 
				+ "Change File" + MergeItem.SEPARADOR_INFO 
				+ "Change Type"+ MergeItem.SEPARADOR_INFO 
				+ "Change Module" + MergeItem.SEPARADOR_INFO 
				+ "Rev. User Orig" + MergeItem.SEPARADOR_INFO
				+ "Rev. Qt" + MergeItem.SEPARADOR_INFO
				+ "Rev. User" + MergeItem.SEPARADOR_INFO
				+ "Rev. Date"
				;//NOPMD
	}
	
	public String build(ChangeItem change) {
		StringBuilder strChange = new StringBuilder(100);
		
		strChange.append(MergeItem.SEPARADOR_INFO);
		strChange.append(MergeItem.SEPARADOR_INFO);
		strChange.append(change.getMergeItem().getRevison()).append(MergeItem.SEPARADOR_INFO);
		strChange.append(change.getArquivo()).append(MergeItem.SEPARADOR_INFO);
		strChange.append(change.getTipo()).append(MergeItem.SEPARADOR_INFO);
		strChange.append(change.getModule()).append(MergeItem.SEPARADOR_INFO);
		strChange.append(change.getMergeItem().getUserOriginal()).append(MergeItem.SEPARADOR_INFO);
		strChange.append(change.getMergeItem().getValidChangeItems().size()).append(MergeItem.SEPARADOR_INFO);
		strChange.append(change.getMergeItem().getUser()).append(MergeItem.SEPARADOR_INFO);
		strChange.append(DATA_FORMAT.format(change.getMergeItem().getDate())).append(MergeItem.SEPARADOR_INFO);
		
		return strChange.toString();
	}
	

}
