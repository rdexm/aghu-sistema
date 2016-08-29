package br.gov.mec.aghu.core.merges;

public class ShowShortChangeItemBuilder {
	
	public ShowShortChangeItemBuilder() {
		super();
	}

	public String getHeader() {
		return  "Responsible" + MergeItem.SEPARADOR_INFO
				+ "Status" + MergeItem.SEPARADOR_INFO
				+ "Revision" + MergeItem.SEPARADOR_INFO 
				+ "Change File" + MergeItem.SEPARADOR_INFO 
				+ "Change Type"+ MergeItem.SEPARADOR_INFO 
				+ "Rev. User Orig" + MergeItem.SEPARADOR_INFO
				;//NOPMD
	}
	
	public String build(ChangeItem change) {
		StringBuilder strChange = new StringBuilder(100);
		
		strChange.append(MergeItem.SEPARADOR_INFO);
		strChange.append(MergeItem.SEPARADOR_INFO);
		strChange.append(change.getMergeItem().getRevison()).append(MergeItem.SEPARADOR_INFO);
		strChange.append(change.getArquivo()).append(MergeItem.SEPARADOR_INFO);
		strChange.append(change.getTipo()).append(MergeItem.SEPARADOR_INFO);
		strChange.append(change.getMergeItem().getUserOriginal()).append(MergeItem.SEPARADOR_INFO);
		
		return strChange.toString();
	}
	

}
