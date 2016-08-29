package br.gov.mec.aghu.faturamento.dao;

import br.gov.mec.aghu.model.FatProcedHospInternosJn;

public class FatProcedHospInternosJnDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FatProcedHospInternosJn> {

	private static final long serialVersionUID = -7264979939898652126L;

	@Override
	protected void obterValorSequencialId(FatProcedHospInternosJn elemento) {
		if (elemento == null) {
			throw new IllegalArgumentException("Parâmetro obrigatório");
		}

		//elemento.setSeqJn(this.getNextVal(SequenceID.FAT_PHI_JN_SEQ).longValue());
		
	}

}
