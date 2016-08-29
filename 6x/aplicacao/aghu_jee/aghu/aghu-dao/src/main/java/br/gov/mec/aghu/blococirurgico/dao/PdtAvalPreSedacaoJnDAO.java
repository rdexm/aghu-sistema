package br.gov.mec.aghu.blococirurgico.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.PdtAvalPreSedacaoJn;

public class PdtAvalPreSedacaoJnDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<PdtAvalPreSedacaoJn> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8931594452276049627L;

	public PdtAvalPreSedacaoJn pesquisarPdtAvalPreSedacaoPorDdtSeq(Integer seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PdtAvalPreSedacaoJn.class,"PDT");
		criteria.add(Restrictions.eq("PDT."+PdtAvalPreSedacaoJn.Fields.DDT_SEQ.toString(), seq));	
		return (PdtAvalPreSedacaoJn)executeCriteriaUniqueResult(criteria);
	}
	
	
}
