package br.gov.mec.aghu.ambulatorio.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MamEmgLocais;

public class MamEmgLocaisDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MamEmgLocais> {

	private static final long serialVersionUID = 8704231726878561439L;

	public String getPrimeiraDescricaoLocal(Short seq) {

		DetachedCriteria criteria = DetachedCriteria.forClass(MamEmgLocais.class);
		if (seq != null) {
			criteria.add(Restrictions.eq(MamEmgLocais.Fields.UNF_SEQ.toString(), seq));
		}
		criteria.setProjection(Projections.property(MamEmgLocais.Fields.DESCRICAO.toString()));

		List<String> res = executeCriteria(criteria, 0, 1, null, true);

		if (res != null && !res.isEmpty()) {
			return (String) res.get(0);
		}

		return null;
	}
}
