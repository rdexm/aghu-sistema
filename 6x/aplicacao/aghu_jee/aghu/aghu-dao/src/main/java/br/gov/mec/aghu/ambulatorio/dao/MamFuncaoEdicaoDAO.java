package br.gov.mec.aghu.ambulatorio.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MamFuncaoEdicao;

public class MamFuncaoEdicaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MamFuncaoEdicao> {

	private static final long serialVersionUID = 1839024499592626397L;

	/**
	 * #52025 - Consulta utilizada em FUNCTION MAMC_EXEC_FNC_EDICAO
	 * @param codigo
	 * @return
	 */
	public String obterCurFuePorSeq(Short seq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MamFuncaoEdicao.class);
		criteria.add(Restrictions.eq(MamFuncaoEdicao.Fields.SEQ.toString(), seq));
		criteria.setProjection(Projections.property(MamFuncaoEdicao.Fields.COMANDO.toString()));
		return (String) executeCriteriaUniqueResult(criteria);
	}
}
