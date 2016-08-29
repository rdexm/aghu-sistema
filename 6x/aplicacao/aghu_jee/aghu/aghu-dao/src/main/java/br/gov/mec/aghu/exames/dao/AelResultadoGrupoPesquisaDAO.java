package br.gov.mec.aghu.exames.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AelResultadoGrupoPesquisa;

public class AelResultadoGrupoPesquisaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelResultadoGrupoPesquisa> {


	private static final long serialVersionUID = 5624593312827542319L;

	public boolean existeDependenciaResultadoCodificado(Integer resultadoCodificado, Integer grupo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelResultadoGrupoPesquisa.class);
		criteria.add(Restrictions.eq(AelResultadoGrupoPesquisa.Fields.SEQ.toString(), resultadoCodificado));
		criteria.add(Restrictions.eq(AelResultadoGrupoPesquisa.Fields.GTC_SEQ.toString(), grupo));
		return (executeCriteriaCount(criteria) > 0);
	}
	
}
