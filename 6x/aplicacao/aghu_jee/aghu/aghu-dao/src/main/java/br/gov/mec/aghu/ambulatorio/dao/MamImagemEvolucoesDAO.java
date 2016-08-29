package br.gov.mec.aghu.ambulatorio.dao;

import java.util.List;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import br.gov.mec.aghu.model.MamImagemEvolucao;


public class MamImagemEvolucoesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MamImagemEvolucao> {

	private static final long serialVersionUID = -2851691163867570484L;

	public List<MamImagemEvolucao> pesquisarItemEvolucoesPorEvolucao(Long seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamImagemEvolucao.class);
		criteria.add(Restrictions.eq(MamImagemEvolucao.Fields.FIE_SEQ.toString(),seq));
		return executeCriteria(criteria);
	}

}