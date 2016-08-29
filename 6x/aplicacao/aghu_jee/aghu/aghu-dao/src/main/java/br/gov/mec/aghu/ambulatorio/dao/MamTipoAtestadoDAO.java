package br.gov.mec.aghu.ambulatorio.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MamTipoAtestado;

public class MamTipoAtestadoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MamTipoAtestado> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9124143966904842391L;

	public List<MamTipoAtestado> listarTodos(MamTipoAtestado tipo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamTipoAtestado.class);
		criteria.add(Restrictions.eq(MamTipoAtestado.Fields.DESCRICAO.toString(), tipo));
		return executeCriteria(criteria);
	}
	
}
