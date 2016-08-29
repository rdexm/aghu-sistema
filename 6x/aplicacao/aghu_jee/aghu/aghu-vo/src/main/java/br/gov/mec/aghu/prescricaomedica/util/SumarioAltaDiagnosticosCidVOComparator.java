package br.gov.mec.aghu.prescricaomedica.util;

import java.io.Serializable;
import java.util.Comparator;

import br.gov.mec.aghu.prescricaomedica.vo.SumarioAltaDiagnosticosCidVO;

public class SumarioAltaDiagnosticosCidVOComparator implements Comparator<SumarioAltaDiagnosticosCidVO>, Serializable {

	

	/**
	 * 
	 */
	private static final long serialVersionUID = -1678242157309133335L;

	@Override
	public int compare(SumarioAltaDiagnosticosCidVO o1,	SumarioAltaDiagnosticosCidVO o2) {
		return o1.getDescricao().compareTo(o2.getDescricao());
	}
	
}