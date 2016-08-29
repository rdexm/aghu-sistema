package br.gov.mec.aghu.procedimentoterapeutico.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.controleinfeccao.vo.HistoricoSalaJnVO;
import br.gov.mec.aghu.model.MptSalasJn;

public class MptSalasJnDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MptSalasJn> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5533682217462222660L;
	
	public List<HistoricoSalaJnVO> obterHistoricoSalaJn(Integer firstResult, Integer maxResults, String orderProperty, 
			boolean asc, Short salaSeq) {
		DetachedCriteria criteria = this.obterCriteriaHistoricoSalaJn(salaSeq);
		criteria.addOrder(Order.desc("SAL." + MptSalasJn.Fields.JN_DATE_TIME.toString()));
		return executeCriteria(criteria, firstResult, maxResults, orderProperty, asc);
	}
	
	public Long obterHistoricoSalaJnCount(Short salaSeq) {
		DetachedCriteria criteria = this.obterCriteriaHistoricoSalaJn(salaSeq);
		return executeCriteriaCount(criteria);
	}
	
	private DetachedCriteria obterCriteriaHistoricoSalaJn(Short salaSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MptSalasJn.class, "SAL");		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("SAL." + MptSalasJn.Fields.SEQ_JN.toString()), HistoricoSalaJnVO.Fields.SEQ_JN.toString())
				.add(Projections.property("SAL." + MptSalasJn.Fields.JN_DATE_TIME.toString()), HistoricoSalaJnVO.Fields.DATA_ALT.toString())
				.add(Projections.property("SAL." + MptSalasJn.Fields.JN_USER.toString()), HistoricoSalaJnVO.Fields.USUARIO.toString())
				.add(Projections.property("SAL." + MptSalasJn.Fields.DESCRICAO.toString()), HistoricoSalaJnVO.Fields.DESC_SALA_JN.toString())
				.add(Projections.property("SAL." + MptSalasJn.Fields.ESP_SEQ.toString()), HistoricoSalaJnVO.Fields.ESP_SEQ.toString())
				.add(Projections.property("SAL." + MptSalasJn.Fields.TPS_SEQ.toString()), HistoricoSalaJnVO.Fields.TP_SEQ.toString())
				.add(Projections.property("SAL." + MptSalasJn.Fields.IND_SITUACAO.toString()), HistoricoSalaJnVO.Fields.SITUACAO.toString()));
		
		criteria.add(Restrictions.eq("SAL." + MptSalasJn.Fields.SEQ.toString(), salaSeq));				
		criteria.setResultTransformer(Transformers.aliasToBean(HistoricoSalaJnVO.class));
		return criteria;
	}

}