package br.gov.mec.aghu.prescricaomedica.util;

import java.io.Serializable;
import java.util.Comparator;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.prescricaomedica.vo.AltaSumarioInfoComplVO;

public class AltaSumarioInfoComplVOComparator implements Comparator<AltaSumarioInfoComplVO>, Serializable {

	

	/**
	 * 
	 */
	private static final long serialVersionUID = -7021358902103589710L;

	@Override
	public int compare(AltaSumarioInfoComplVO o1, AltaSumarioInfoComplVO o2) {
		String descricao1 = StringUtils.trimToEmpty(o1.getDescricaoMedicamento()); 
		String descricao2 = StringUtils.trimToEmpty(o2.getDescricaoMedicamento()); 
		return descricao1.compareTo(descricao2);
	}
	
}