package br.gov.mec.aghu.prescricaomedica.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MamTipoEstadoPaciente;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class MamTipoEstadoPacienteDAO extends BaseDao<MamTipoEstadoPaciente> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2900907426966262419L;

	
	/**
	 * #44179 - Consulta para retornar o tipo do estado do paciente, parte da melhoria 49372
	 * @author marcelo.deus
	 * @param descricao
	 */
	public MamTipoEstadoPaciente buscarTipoEstadoPacientePorDescricao(String descricao){
		DetachedCriteria criteria = DetachedCriteria.forClass(MamTipoEstadoPaciente.class);
		criteria.add(Restrictions.eq(MamTipoEstadoPaciente.Fields.DESCRICAO.toString(), descricao));
		return (MamTipoEstadoPaciente) executeCriteriaUniqueResult(criteria);
	}
	
	
}
