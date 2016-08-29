package br.gov.mec.aghu.exames.dao;

import br.gov.mec.aghu.dao.SequenceID;
import br.gov.mec.aghu.model.AelExamesJn;

public class AelExamesJnDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelExamesJn> {

	private static final long serialVersionUID = -1161808396562293288L;

	@Override
	protected void obterValorSequencialId(AelExamesJn elemento) {
		if (elemento == null) {
			throw new IllegalArgumentException("Parâmetro obrigatório");
		}

		elemento.getId().setSeqJn(this.getNextVal(SequenceID.AEL_EXA_JN_SEQ));
	}

}
