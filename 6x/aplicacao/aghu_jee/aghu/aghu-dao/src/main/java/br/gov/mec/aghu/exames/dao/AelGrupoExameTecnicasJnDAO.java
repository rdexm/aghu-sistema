package br.gov.mec.aghu.exames.dao;

import br.gov.mec.aghu.model.AelGrupoExameTecnicasJn;

public class AelGrupoExameTecnicasJnDAO  extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelGrupoExameTecnicasJn> {
	
	private static final long serialVersionUID = 7024232784912086596L;

	@Override
	protected void obterValorSequencialId(AelGrupoExameTecnicasJn elemento) {
		
		if (elemento == null) {
			throw new IllegalArgumentException("Parâmetro obrigatório");
		}

		//elemento.setSeqJn(this.getNextVal(SequenceID.AEL_GRT_JN_SEQ).longValue());
	}

}
