package br.gov.mec.aghu.configuracao.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AghOrigemEventos;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class AghOrigemEventosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AghOrigemEventos> {

	private static final long serialVersionUID = -1832217555244859307L;

	public List<AghOrigemEventos> pesquisarOrigemEventoPorCodigoEDescricao(Object objPesquisa) {
		String strPesquisa = (String) objPesquisa;

		if (StringUtils.isNotBlank(strPesquisa) && CoreUtil.isNumeroShort(strPesquisa)) {
			DetachedCriteria _criteria = DetachedCriteria.forClass(AghOrigemEventos.class);

			_criteria.add(Restrictions.eq(AghOrigemEventos.Fields.SEQ.toString(), Short.valueOf(strPesquisa)));

			List<AghOrigemEventos> list = executeCriteria(_criteria);

			if (list.size() > 0) {
				return list;
			}
		}

		DetachedCriteria criteria = DetachedCriteria.forClass(AghOrigemEventos.class);

		if (StringUtils.isNotBlank(strPesquisa)) {
			criteria.add(Restrictions.ilike(AghOrigemEventos.Fields.DESCRICAO.toString(), strPesquisa, MatchMode.ANYWHERE));
		}

		criteria.addOrder(Order.asc(AghOrigemEventos.Fields.DESCRICAO.toString()));

		return executeCriteria(criteria);
	}
	
	public AghOrigemEventos obterOrigemEventos(Short seq) {
		AghOrigemEventos retorno = findByPK(
				AghOrigemEventos.class, seq);
		return retorno;
	}
	
	public Long pesquisarOrigemEventosCount(Short seq,
			String descricao) {
		return executeCriteriaCount(createPesquisarOrigemEventosCriteria(
				seq, descricao));
	}
	
	public List<AghOrigemEventos> pesquisarOrigemEventos(
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, Short seq, String descricao) {
		DetachedCriteria criteria = createPesquisarOrigemEventosCriteria(
				seq, descricao);
		
		criteria.addOrder(Order.asc(AghOrigemEventos.Fields.SEQ.toString()));

		return executeCriteria(criteria, firstResult, maxResults,
				orderProperty, asc);
	}

	private DetachedCriteria createPesquisarOrigemEventosCriteria(
			Short seq, String descricao) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AghOrigemEventos.class);

		if (seq != null) {
			criteria.add(Restrictions.eq(
					AghOrigemEventos.Fields.SEQ.toString(), seq));
		}

		if (StringUtils.isNotBlank(descricao)) {
			criteria.add(Restrictions.ilike(
					AghOrigemEventos.Fields.DESCRICAO.toString(),
					descricao, MatchMode.ANYWHERE));
		}

		return criteria;
	}

}