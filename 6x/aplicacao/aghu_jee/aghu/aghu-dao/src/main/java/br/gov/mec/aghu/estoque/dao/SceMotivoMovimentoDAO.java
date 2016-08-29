package br.gov.mec.aghu.estoque.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.SceMotivoMovimento;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class SceMotivoMovimentoDAO extends BaseDao<SceMotivoMovimento> {

	private static final long serialVersionUID = 7328141442477534289L;

	@Override
	protected void obterValorSequencialId(SceMotivoMovimento elemento) {
		if (elemento == null || elemento.getId() == null || elemento.getId().getTmvComplemento() == null
				|| elemento.getId().getTmvSeq() == null) {
			throw new IllegalArgumentException("Parametro Invalido!!!");
		}

		DetachedCriteria criteria = DetachedCriteria.forClass(SceMotivoMovimento.class);
		criteria.add(Restrictions.eq(SceMotivoMovimento.Fields.TMV_SEQ.toString(), elemento.getId().getTmvSeq()));
		criteria.add(Restrictions.eq(SceMotivoMovimento.Fields.TMV_COMP.toString(), elemento.getId().getTmvComplemento()));
		criteria.setProjection(Projections.max(SceMotivoMovimento.Fields.NUMERO.toString()));
		Short numero = (Short) executeCriteriaUniqueResult(criteria);
		if (numero == null) {
			numero = 0;
		}
		numero++;
		elemento.getId().setNumero(numero);
	}

	public List<SceMotivoMovimento> obterMotivoMovimentoPorSeqDescricaoETMV(Short tmvSeq, Byte tmvComplemento, Object param) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceMotivoMovimento.class);
		criteria.add(Restrictions.eq(SceMotivoMovimento.Fields.TMV_SEQ.toString(), tmvSeq));
		criteria.add(Restrictions.eq(SceMotivoMovimento.Fields.TMV_COMP.toString(), tmvComplemento));

		String strPesquisa = (String) param;

		if (StringUtils.isNotBlank(strPesquisa)) {
			if (CoreUtil.isNumeroShort(strPesquisa)) {
				criteria.add(Restrictions.eq(SceMotivoMovimento.Fields.NUMERO.toString(), Short.parseShort(strPesquisa)));
			} else {
				criteria.add(Restrictions.ilike(SceMotivoMovimento.Fields.DESCRICAO.toString(), StringUtils.trim(strPesquisa),
						MatchMode.ANYWHERE));
			}
		}
		return executeCriteria(criteria);
	}

	public List<SceMotivoMovimento> listarMotivoMovimento(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			Short seq, Byte complemento) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceMotivoMovimento.class);
		criteria.add(Restrictions.eq(SceMotivoMovimento.Fields.TMV_SEQ.toString(), seq));
		criteria.add(Restrictions.eq(SceMotivoMovimento.Fields.TMV_COMP.toString(), complemento));

		if (StringUtils.isNotBlank(orderProperty) && orderProperty.startsWith(SceMotivoMovimento.Fields.ID.toString() + ".")) {
			if (asc) {
				criteria.addOrder(Order.asc(orderProperty));
			} else {
				criteria.addOrder(Order.desc(orderProperty));
			}
			return executeCriteria(criteria, firstResult, maxResult, null, false);
		}

		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}

	public Long listarMotivoMovimentoCount(Short seq, Byte complemento) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceMotivoMovimento.class);
		criteria.add(Restrictions.eq(SceMotivoMovimento.Fields.TMV_SEQ.toString(), seq));
		criteria.add(Restrictions.eq(SceMotivoMovimento.Fields.TMV_COMP.toString(), complemento));
		return executeCriteriaCount(criteria);
	}

}