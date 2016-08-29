package br.gov.mec.aghu.paciente.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.AipPacientesDadosCns;

public class AipPacientesDadosCnsDAO extends
		br.gov.mec.aghu.core.persistence.dao.BaseDao<AipPacientesDadosCns> {

	private static final long serialVersionUID = -5643980248887871405L;

	public AipPacientesDadosCns obterPacientesDadosCns(Integer codPaciente) {

		final DetachedCriteria criteria = DetachedCriteria
				.forClass(AipPacientesDadosCns.class);
		criteria.add(Restrictions.eq(
				AipPacientesDadosCns.Fields.CODIGO_PACIENTE.toString(), codPaciente));
		
		criteria.createAlias(AipPacientesDadosCns.Fields.ORGAO_EMISSOR.toString(), AipPacientesDadosCns.Fields.ORGAO_EMISSOR.toString(), JoinType.LEFT_OUTER_JOIN);
		
		criteria.createAlias(AipPacientesDadosCns.Fields.UF.toString(), AipPacientesDadosCns.Fields.UF.toString(), JoinType.LEFT_OUTER_JOIN);

		return (AipPacientesDadosCns) this
				.executeCriteriaUniqueResult(criteria);

	}

}
