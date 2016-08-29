package br.gov.mec.aghu.exames.dao;

import br.gov.mec.aghu.model.AelGrpTecnicaUnfExamesJn;

public class AelGrpTecnicaUnfExamesJnDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelGrpTecnicaUnfExamesJn> {
	
	private static final long serialVersionUID = 3450524360135272907L;

	@Override
	protected void obterValorSequencialId(AelGrpTecnicaUnfExamesJn elemento) {
		
		if (elemento == null) {
			throw new IllegalArgumentException("Parâmetro obrigatório");
		}

		//elemento.setSeqJn(this.getNextVal(SequenceID.AEL_TCE_JN_SEQ).longValue());
	}

}
