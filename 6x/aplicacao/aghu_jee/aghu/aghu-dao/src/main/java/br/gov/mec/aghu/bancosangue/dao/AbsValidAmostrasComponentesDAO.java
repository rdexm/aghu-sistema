package br.gov.mec.aghu.bancosangue.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AbsValidAmostrasComponentes;

public class AbsValidAmostrasComponentesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AbsValidAmostrasComponentes> {

	private static final long serialVersionUID = -1611236074996709748L;

	public List<AbsValidAmostrasComponentes> pesquisarAbsValidAmostrasComponentesPorCsaCodigoPeriodo(String csaCodigo, Integer vMeses) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AbsValidAmostrasComponentes.class);
		criteria.add(Restrictions.eq(AbsValidAmostrasComponentes.Fields.CSA_CODIGO.toString(), csaCodigo));
		criteria.add(Restrictions.le(AbsValidAmostrasComponentes.Fields.IDADE_MES_INICIAL.toString(), vMeses));
		criteria.add(Restrictions.ge(AbsValidAmostrasComponentes.Fields.IDADE_MES_FINAL.toString(), vMeses));
		return this.executeCriteria(criteria);
	}

}
