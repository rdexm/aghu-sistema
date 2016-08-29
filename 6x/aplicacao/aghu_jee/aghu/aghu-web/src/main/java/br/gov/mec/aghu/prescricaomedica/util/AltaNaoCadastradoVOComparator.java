package br.gov.mec.aghu.prescricaomedica.util;

import java.io.Serializable;
import java.util.Comparator;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.prescricaomedica.vo.AltaRecomendacaoVO;

public class AltaNaoCadastradoVOComparator implements Comparator<AltaRecomendacaoVO>, Serializable {

	

	/**
	 * 
	 */
	private static final long serialVersionUID = -1579510752164435231L;

	@Override
	public int compare(AltaRecomendacaoVO o1, AltaRecomendacaoVO o2) {
	    String descricao1 = StringUtils.trimToEmpty(o1.getDescricao());
	    String descricao2 = StringUtils.trimToEmpty(o2.getDescricao());
	    return descricao1.compareTo(descricao2);
	}
	
}