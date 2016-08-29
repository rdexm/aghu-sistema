package br.gov.mec.aghu.blococirurgico.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.PdtAvalPreSedacao;

public class PdtAvalPreSedacaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<PdtAvalPreSedacao> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8931594452276049627L;

	public PdtAvalPreSedacao pesquisarPdtAvalPreSedacaoPorDdtSeq(Integer seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PdtAvalPreSedacao.class,"PDT");
		criteria.createAlias(PdtAvalPreSedacao.Fields.VIAS_AEREAS.toString(), "VIA", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq("PDT."+PdtAvalPreSedacao.Fields.DDT_SEQ.toString(), seq));	
		return (PdtAvalPreSedacao)executeCriteriaUniqueResult(criteria);
	}
	
}
