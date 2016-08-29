package br.gov.mec.aghu.farmacia.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.AfaGrupoMedicamentoMensagem;

public class AfaGrupoMedicamentoMensagemDAO extends
		br.gov.mec.aghu.core.persistence.dao.BaseDao<AfaGrupoMedicamentoMensagem> {

	

	private static final long serialVersionUID = 4635414675276640512L;

	public List<AfaGrupoMedicamentoMensagem> pesquisarPorSeqMensagemMedicamento(
			Integer memSeq) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AfaGrupoMedicamentoMensagem.class);
		criteria.createAlias(AfaGrupoMedicamentoMensagem.Fields.GRUPO_MEDICAMENTO.toString(), "grp_med", JoinType.LEFT_OUTER_JOIN);
		
		if (memSeq != null) {
			criteria.add(Restrictions.eq(
					AfaGrupoMedicamentoMensagem.Fields.MEM_SEQ.toString(),
					memSeq));
		}

		return executeCriteria(criteria);
	}

}
