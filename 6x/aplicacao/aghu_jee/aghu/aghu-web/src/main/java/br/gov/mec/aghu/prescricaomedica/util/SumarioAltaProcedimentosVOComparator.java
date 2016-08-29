package br.gov.mec.aghu.prescricaomedica.util;

import java.io.Serializable;
import java.util.Comparator;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.prescricaomedica.vo.SumarioAltaProcedimentosVO;

public class SumarioAltaProcedimentosVOComparator implements Comparator<SumarioAltaProcedimentosVO>, Serializable {

	

	/**
	 * 
	 */
	private static final long serialVersionUID = 9025006526108547478L;

	@Override
	public int compare(SumarioAltaProcedimentosVO o1, SumarioAltaProcedimentosVO o2) {
		int result = 1;

		if (o1 != null && o1.getData() != null && o2 != null && o2.getData() != null) {
		
			result = o1.getData().compareTo(o2.getData());
		
		} else if (o1 != null && o1.getData() == null && o2 != null && o2.getData() == null) {
			
			result = 0;
			
		}
		
		if (result == 0) {
		    String descricao1 = o1 != null ? StringUtils.trimToEmpty(o1.getDescricao()) : "";
		    String descricao2 = o2 != null ? StringUtils.trimToEmpty(o2.getDescricao()) : "";
			result = descricao1.compareTo(descricao2);
		}
		
		return result;
	
	}
	
}