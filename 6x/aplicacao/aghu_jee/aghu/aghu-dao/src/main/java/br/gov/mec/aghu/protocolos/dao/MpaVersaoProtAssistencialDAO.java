package br.gov.mec.aghu.protocolos.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MpaProtocoloAssistencial;
import br.gov.mec.aghu.model.MpaVersaoProtAssistencial;



public class MpaVersaoProtAssistencialDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpaVersaoProtAssistencial> {

	private static final long serialVersionUID = 4222208497029013214L;

	public List<MpaVersaoProtAssistencial> listarProtocolosAssistenciais(String param) {
		DetachedCriteria criteria = obterCriteriaListarProtocolosAssistenciais(param);
		criteria.addOrder(Order.asc("PTA." + MpaProtocoloAssistencial.Fields.TITULO.toString()));
		
		return executeCriteria(criteria, 0, 100, null, true);
	}

	public Long listarProtocolosAssistenciaisCount(String param) {
		DetachedCriteria criteria = obterCriteriaListarProtocolosAssistenciais(param);
		return executeCriteriaCount(criteria);
	}
	
	private DetachedCriteria obterCriteriaListarProtocolosAssistenciais(String param) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpaVersaoProtAssistencial.class, "VPA");
		criteria.createAlias("VPA." + MpaVersaoProtAssistencial.Fields.MPA_PROTOCOLO_ASSISTENCIAIS.toString(), "PTA");
		
		criteria.add(Restrictions.eq("VPA." + MpaVersaoProtAssistencial.Fields.IND_SITUACAO.toString(), "L"));
		if (StringUtils.isNumeric(param)) {
			criteria.add(Restrictions.eq("VPA." + MpaVersaoProtAssistencial.Fields.SEQP.toString(), Short.valueOf(param)));
		} else if (!StringUtils.isEmpty(param)) {
			criteria.add(Restrictions.ilike("PTA." + MpaProtocoloAssistencial.Fields.TITULO.toString(), param, MatchMode.ANYWHERE));
		}
		return criteria;
	}
}
	
		
	