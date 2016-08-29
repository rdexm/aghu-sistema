package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AelTopografiaGrupoCidOs;

public class AelTopografiaGruposCidOsDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelTopografiaGrupoCidOs> {

	private static final long serialVersionUID = 3846161295734091849L;

	public List<AelTopografiaGrupoCidOs> listarTopografiaGrupoCidOs() {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelTopografiaGrupoCidOs.class, "GRUPO_CIDO");
		criteria.addOrder(Order.desc("GRUPO_CIDO." + AelTopografiaGrupoCidOs.Fields.SEQ.toString()));
		return executeCriteria(criteria);
	}

	public List<AelTopografiaGrupoCidOs> listarTopografiaGrupoCidOsNodosRaiz() {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelTopografiaGrupoCidOs.class, "GRUPO_CIDO");
		criteria.add(Restrictions.isNull(AelTopografiaGrupoCidOs.Fields.GRUPO.toString()));
		criteria.addOrder(Order.desc("GRUPO_CIDO." + AelTopografiaGrupoCidOs.Fields.SEQ.toString()));
		return executeCriteria(criteria);
	}

	public List<AelTopografiaGrupoCidOs> listarTopografiaGrupoCidOsPorGrupo(Long seqGrupo) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelTopografiaGrupoCidOs.class, "GRUPO_CIDO");

		criteria.add(Restrictions.isNotNull(AelTopografiaGrupoCidOs.Fields.GRUPO.toString()));
		criteria.add(Restrictions.eq(AelTopografiaGrupoCidOs.Fields.GRUPO_SEQ.toString(), seqGrupo));

		criteria.addOrder(Order.desc("GRUPO_CIDO." + AelTopografiaGrupoCidOs.Fields.SEQ.toString()));
		return executeCriteria(criteria);
	}
}
