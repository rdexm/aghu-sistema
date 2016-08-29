package br.gov.mec.aghu.core.merges;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FileUtils;


/**
 * Le o arquivo de change log gerado pelo eclipse e gera os pojos com o dados de merge.
 * 
 * @author rcorvalao
 *
 */
public class GenerateMergeItems {
	
	public static final String SEPARATOR_REVISIONS = "----------------------------------------------------------------------------\n";
	
	private List<MergeItem> mergeItems = new LinkedList<>();
	
	
	public void execute(String inputFileName) throws IOException {
		File arquivoRevisions = new File(inputFileName);
		
		String strContent = FileUtils.readFileToString(arquivoRevisions);
		
		int contentEmptyCount = 0;
		int countRevision = 0;
		int index = 0;
		int indexFim = 0;
		int fim = strContent.length() - SEPARATOR_REVISIONS.length() - 1;
		while (indexFim < fim) {
			indexFim = strContent.indexOf(SEPARATOR_REVISIONS, (indexFim+10));
			
			String itemContent = strContent.substring(index, indexFim);
			if (itemContent != null && !"".equals(itemContent.trim())) {
				if (itemContent.length() > SEPARATOR_REVISIONS.length() && itemContent.contains(MergeItem.CHANGE_PATHS)) {
					MergeItem itemMerge = new MergeItem(itemContent);
					mergeItems.add(itemMerge);
				} else {
					contentEmptyCount = contentEmptyCount + 1;
				}
			}
			
			countRevision = countRevision + 1;
			index = indexFim;// + SEPARATOR_REVISIONS.length();
		}
		
		System.out.println("countRevision: " + countRevision);//NOPMD
		System.out.println("list Revision: " + mergeItems.size());//NOPMD
		System.out.println("contentEmptyCount: " + contentEmptyCount);//NOPMD

	}
	
	
	public List<MergeItem> getMergeItems() {
		return this.mergeItems;
	}

}
