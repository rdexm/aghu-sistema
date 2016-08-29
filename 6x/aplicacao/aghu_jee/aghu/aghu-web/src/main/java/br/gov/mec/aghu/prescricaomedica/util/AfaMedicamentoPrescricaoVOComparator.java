package br.gov.mec.aghu.prescricaomedica.util;

import java.io.Serializable;
import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.prescricaomedica.vo.AfaMedicamentoPrescricaoVO;

public class AfaMedicamentoPrescricaoVOComparator implements Comparator<AfaMedicamentoPrescricaoVO>, Serializable {

	

	/**
	 * 
	 */
	private static final long serialVersionUID = -6601669876207887058L;

	@Override
	public int compare(AfaMedicamentoPrescricaoVO o1, AfaMedicamentoPrescricaoVO o2) {
		final Collator collator = Collator.getInstance(new Locale("pt", "BR"));
		collator.setStrength(Collator.PRIMARY);
	   
		String descricao1 = StringUtils.trimToEmpty(o1.getDescricao());
	    String descricao2 = StringUtils.trimToEmpty(o2.getDescricao());

	    return collator.compare(descricao1, descricao2);
	}
	
}