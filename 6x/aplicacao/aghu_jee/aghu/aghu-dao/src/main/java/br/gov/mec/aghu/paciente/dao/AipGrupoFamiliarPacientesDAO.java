package br.gov.mec.aghu.paciente.dao;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.AipGrupoFamiliarPacientes;
import br.gov.mec.aghu.model.AipPacientes;

public class AipGrupoFamiliarPacientesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AipGrupoFamiliarPacientes> {

	private static final long serialVersionUID = 8871699857621445919L;

	public AipGrupoFamiliarPacientes obterDadosGrupoFamiliarPaciente(AipPacientes paciente) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipGrupoFamiliarPacientes.class);
		criteria.createAlias(AipGrupoFamiliarPacientes.Fields.GRUPO_FAMILIAR.toString(), AipGrupoFamiliarPacientes.Fields.GRUPO_FAMILIAR.toString(), JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq(AipGrupoFamiliarPacientes.Fields.PAC_CODIGO.toString(), paciente.getCodigo()));
	
		return (AipGrupoFamiliarPacientes) executeCriteriaUniqueResult(criteria);
	}
}
