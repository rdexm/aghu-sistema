package br.gov.mec.aghu.blococirurgico.dao;


import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.PdtDescTecnica;

public class PdtDescTecnicaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<PdtDescTecnica> {


	private static final long serialVersionUID = -468003374323087623L;

	public PdtDescTecnica obterDescricaoTecnicaPorDdtSeq(Integer ddtSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PdtDescTecnica.class);
		criteria.add(Restrictions.eq(PdtDescTecnica.Fields.DDT_SEQ.toString(), ddtSeq));
		return (PdtDescTecnica) this.executeCriteriaUniqueResult(criteria);
	}
	
	public Long obterCountDescricaoNaoNulaPorDdtSeq(Integer ddtSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PdtDescTecnica.class);
	
		criteria.add(Restrictions.eq(PdtDescTecnica.Fields.DDT_SEQ.toString(), ddtSeq));
		criteria.add(Restrictions.isNotNull(PdtDescTecnica.Fields.DESCRICAO.toString())); 
	
		return executeCriteriaCount(criteria);
	}

	public List<PdtDescTecnica> pesquisarPdtDescTecnicaPorDdtSeq(Integer ddtSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(PdtDescTecnica.class);
		
		criteria.add(Restrictions.eq(PdtDescTecnica.Fields.DDT_SEQ.toString(), ddtSeq));
		
		return executeCriteria(criteria);
	}
	
}
