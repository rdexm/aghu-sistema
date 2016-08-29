package br.gov.mec.aghu.procedimentoterapeutico.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.controleinfeccao.vo.HistoricoAcomodacaoJnVO;
import br.gov.mec.aghu.model.MptLocalAtendimentoJn;

public class MptLocalAtendimentoJnDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MptLocalAtendimentoJn> {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4025886587196212551L;

	public List<HistoricoAcomodacaoJnVO> obterHistoricoAcomodacaoJn(Integer firstResult, Integer maxResults, String orderProperty, 
			boolean asc, Short localSeq) {
		DetachedCriteria criteria = this.obterCriteriaHistoricoAcomodacaoJn(localSeq);
		criteria.addOrder(Order.desc("LOC." + MptLocalAtendimentoJn.Fields.JN_DATE_TIME.toString()));
		return executeCriteria(criteria, firstResult, maxResults, orderProperty, asc);
	}
	
	public Long obterHistoricoAcomodacaoJnCount(Short localSeq) {
		DetachedCriteria criteria = this.obterCriteriaHistoricoAcomodacaoJn(localSeq);
		return executeCriteriaCount(criteria);
	}
	
	private DetachedCriteria obterCriteriaHistoricoAcomodacaoJn(Short localSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MptLocalAtendimentoJn.class, "LOC");
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("LOC." + MptLocalAtendimentoJn.Fields.SEQ_JN.toString()), HistoricoAcomodacaoJnVO.Fields.SEQ_JN.toString())
				.add(Projections.property("LOC." + MptLocalAtendimentoJn.Fields.JN_DATE_TIME.toString()), HistoricoAcomodacaoJnVO.Fields.DATA_ALT.toString())
				.add(Projections.property("LOC." + MptLocalAtendimentoJn.Fields.JN_USER.toString()), HistoricoAcomodacaoJnVO.Fields.USUARIO.toString())
				.add(Projections.property("LOC." + MptLocalAtendimentoJn.Fields.DESCRICAO.toString()), HistoricoAcomodacaoJnVO.Fields.DESC_ACOMODACAO_JN.toString())
				.add(Projections.property("LOC." + MptLocalAtendimentoJn.Fields.IND_RESERVA.toString()), HistoricoAcomodacaoJnVO.Fields.RESERVA.toString())
				.add(Projections.property("LOC." + MptLocalAtendimentoJn.Fields.TIPO_LOCAL.toString()), HistoricoAcomodacaoJnVO.Fields.TIPO_LOCAL.toString())
				.add(Projections.property("LOC." + MptLocalAtendimentoJn.Fields.IND_SITUACAO.toString()), HistoricoAcomodacaoJnVO.Fields.SITUACAO.toString()));
		
		criteria.add(Restrictions.eq("LOC." + MptLocalAtendimentoJn.Fields.SEQ.toString(), localSeq));
		criteria.setResultTransformer(Transformers.aliasToBean(HistoricoAcomodacaoJnVO.class));
		return criteria;
	}
}