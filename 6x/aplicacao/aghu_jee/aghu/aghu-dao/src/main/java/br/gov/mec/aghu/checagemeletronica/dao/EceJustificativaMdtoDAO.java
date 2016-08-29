package br.gov.mec.aghu.checagemeletronica.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.EceJustificativaMdto;


public class EceJustificativaMdtoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<EceJustificativaMdto> {

	private static final long serialVersionUID = 4674340738534846937L;

	public List<EceJustificativaMdto> pesquisarJustificativasPorSeqDescricaoSituacao(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			Short seq, String descricao) {
		
		DetachedCriteria criteria = criarCriteriaPesquisarJustificativasPorSeqDescricaoSituacao(seq, descricao);
		criteria.addOrder(Order.asc(EceJustificativaMdto.Fields.SEQ.toString()));
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}

	public Long pesquisarJustificativasPorSeqDescricaoSituacaoCount(Short seq, String descricao) {
		
		DetachedCriteria criteria = criarCriteriaPesquisarJustificativasPorSeqDescricaoSituacao(seq, descricao);
		return this.executeCriteriaCount(criteria);
	}

	private DetachedCriteria criarCriteriaPesquisarJustificativasPorSeqDescricaoSituacao(Short seq, String descricao) {

		DetachedCriteria criteria = DetachedCriteria.forClass(EceJustificativaMdto.class);
		if (seq != null){
			criteria.add(Restrictions.eq(EceJustificativaMdto.Fields.SEQ.toString(), seq));
		}
		if (StringUtils.isNotBlank(descricao)){
			criteria.add(Restrictions.ilike(EceJustificativaMdto.Fields.DESCRICAO.toString(), descricao, MatchMode.ANYWHERE));
		}
		return criteria;
	}


}
