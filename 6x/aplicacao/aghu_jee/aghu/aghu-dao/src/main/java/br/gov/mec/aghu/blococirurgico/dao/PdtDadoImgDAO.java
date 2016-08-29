package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.PdtDadoImg;

public class PdtDadoImgDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<PdtDadoImg> {
	

	private static final long serialVersionUID = -6744592352460287448L;

	public List<PdtDadoImg> pesquisarPdtDadoImgPorDdtSeq(Integer ddtSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(PdtDadoImg.class);
		
		criteria.add(Restrictions.eq(PdtDadoImg.Fields.DDT_SEQ.toString(), ddtSeq));
		
		return executeCriteria(criteria);
	}
	
	

}
