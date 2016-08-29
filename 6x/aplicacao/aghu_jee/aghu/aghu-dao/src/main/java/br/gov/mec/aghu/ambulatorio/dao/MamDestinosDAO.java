package br.gov.mec.aghu.ambulatorio.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MamDestinos;

public class MamDestinosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MamDestinos> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2942915825063728234L;

	/**
	 * #42360
	 * @param numeroConsulta
	 * @return
	 */
	
	public MamDestinos buscarDestinosPorNumeroConsulta(Integer numeroConsulta) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamDestinos.class);

		criteria.add(Restrictions.eq(MamDestinos.Fields.CON_NUMERO.toString(), numeroConsulta));
		List<MamDestinos> list = executeCriteria(criteria);
		if (list != null && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}

}
