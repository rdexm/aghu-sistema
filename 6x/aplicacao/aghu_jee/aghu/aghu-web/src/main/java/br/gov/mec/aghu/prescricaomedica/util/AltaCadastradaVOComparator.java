package br.gov.mec.aghu.prescricaomedica.util;

import java.io.Serializable;
import java.util.Comparator;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.prescricaomedica.vo.AltaCadastradaVO;

public class AltaCadastradaVOComparator implements Comparator<AltaCadastradaVO>, Serializable {

	

	/**
	 * 
	 */
	private static final long serialVersionUID = 2572792200199611767L;

	@Override
	public int compare(AltaCadastradaVO o1, AltaCadastradaVO o2) {
	    String descricao1 = StringUtils.trimToEmpty(o1.getDescricao());
	    String descricao2 = StringUtils.trimToEmpty(o2.getDescricao());
	    return descricao1.compareTo(descricao2);
	}
	
}