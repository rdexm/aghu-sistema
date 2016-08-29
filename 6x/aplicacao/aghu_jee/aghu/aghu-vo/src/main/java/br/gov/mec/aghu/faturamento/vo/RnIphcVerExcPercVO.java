package br.gov.mec.aghu.faturamento.vo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.gov.mec.aghu.model.FatExcecaoPercentual;

public class RnIphcVerExcPercVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1584401636069789273L;
	public static final Short DEFAULT_PERCENT = Short.valueOf((short) 100);
	private Map<Byte, Short> seqParaPercentual = new HashMap<Byte, Short>();
	
	public RnIphcVerExcPercVO(List<FatExcecaoPercentual> percentual) {

		super();
		
		Byte seq = null;
		Short percent = null;

		for (FatExcecaoPercentual p : percentual) {
			seq = p.getId().getSeqp();
			percent = p.getPercentual();
			this.seqParaPercentual.put(seq, percent);
		}
	}
	
	public Short getPercentual(Byte seq) {
		
		Short result = null;
		
		result = this.seqParaPercentual.get(seq);
	
		return (result != null ? result : DEFAULT_PERCENT);
	}
}
