package br.gov.mec.aghu.paciente.prontuarioonline.vo;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.core.utils.DateUtil;

public class ItemSolicitacaoExamePolVOComparator implements Comparator<ItemSolicitacaoExamePolVO>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5221084289982756961L;

	@Override
	public int compare(ItemSolicitacaoExamePolVO o1, ItemSolicitacaoExamePolVO o2) {
		
		int result = 0;
		
		if (o1 != null && o1.getDthrLiberada() != null && o2 != null && o2.getDthrLiberada() != null) {
			Date dthrLiberada1 =DateUtil.truncaData(o1.getDthrLiberada());
			Date dthrLiberada2 = DateUtil.truncaData(o2.getDthrLiberada());
			result = dthrLiberada1.compareTo(dthrLiberada2)*(-1);
		} else if (o1 != null && o1.getDthrLiberada() == null && o2 != null && o2.getDthrLiberada() == null) {
			result = 0;
		}
		
		if (result == 0) {
		    String descricao1 = o1 != null ? StringUtils.trimToEmpty(o1.getExaDescricaoUsual()) : "";
		    String descricao2 = o2 != null ? StringUtils.trimToEmpty(o2.getExaDescricaoUsual()) : "";
			result = descricao1.compareTo(descricao2);
		}
		
		return result;
	
	}

}
