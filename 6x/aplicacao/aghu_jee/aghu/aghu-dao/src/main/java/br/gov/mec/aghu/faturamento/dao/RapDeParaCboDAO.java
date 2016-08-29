package br.gov.mec.aghu.faturamento.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.RapDeParaCbo;

public class RapDeParaCboDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<RapDeParaCbo> {

	private static final long serialVersionUID = -2935697477444933437L;

	/*
	 * cursor cbo_ant is select * from RAP_DEPARA_CBO where cod_cbo_antigo =
	 * p_cbo;
	 */
	public List<RapDeParaCbo> obterCodigoCboAntigo(final String codigoCbo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(RapDeParaCbo.class);
		criteria.add(Restrictions.eq(RapDeParaCbo.Fields.COD_CBO_ANTIGO.toString(), codigoCbo));
		//criteria.getExecutableCriteria(getSession()).setCacheable(Boolean.TRUE);
		return executeCriteria(criteria, true);
	}

	/*
	 * cursor cbo_novo is select * from RAP_DEPARA_CBO where cod_cbo_novo =
	 * p_cbo;
	 */
	public List<RapDeParaCbo> obterCodigoCboNovo(final String codigoCbo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(RapDeParaCbo.class);
		criteria.add(Restrictions.eq(RapDeParaCbo.Fields.COD_CBO_NOVO.toString(), codigoCbo));
		return executeCriteria(criteria, true);
	}

}
