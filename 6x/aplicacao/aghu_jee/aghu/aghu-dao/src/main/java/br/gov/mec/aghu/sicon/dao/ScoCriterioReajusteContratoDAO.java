package br.gov.mec.aghu.sicon.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.ScoCriterioReajusteContrato;
import br.gov.mec.aghu.core.commons.CoreUtil;

//import br.gov.mec.aghu.core.commons.CoreUtil;
/**
 * @modulo sicon.cadastrosbasicos
 * @author cvagheti
 *
 */
public class ScoCriterioReajusteContratoDAO extends
		br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoCriterioReajusteContrato> {

	private static final long serialVersionUID = -3670045752998766149L;

	public long validaDescricaoUnica(Integer seq, String descricao) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(ScoCriterioReajusteContrato.class);

		if (seq != null) {
			criteria.add(Restrictions.ne(
					ScoCriterioReajusteContrato.Fields.SEQ.toString(), seq));
		}

		if (descricao != null) {
			criteria.add(Restrictions.eq(
					ScoCriterioReajusteContrato.Fields.DESCRICAO.toString(),
					descricao));
		}

		return this.executeCriteriaCount(criteria);
	}

	public long pesquisarCriterioReajusteContratoCount(Integer seq,
			String descricao, DominioSituacao situacao) {

		DetachedCriteria criteria = this.pesquisarCriteria(seq, descricao,
				situacao);
		return this.executeCriteriaCount(criteria);
	}

	private DetachedCriteria pesquisarCriteria(Integer seq, String descricao,
			DominioSituacao situacao) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(ScoCriterioReajusteContrato.class);

		if (seq != null) {
			criteria.add(Restrictions.eq(
					ScoCriterioReajusteContrato.Fields.SEQ.toString(), seq));
		}

		if (StringUtils.isNotBlank(descricao)) {
			criteria.add(Restrictions.ilike(
					ScoCriterioReajusteContrato.Fields.DESCRICAO.toString(),
					descricao, MatchMode.ANYWHERE));
		}

		if (situacao != null) {
			criteria.add(Restrictions.eq(
					ScoCriterioReajusteContrato.Fields.SITUACAO.toString(),
					situacao));
		}

		return criteria;
	}

	public List<ScoCriterioReajusteContrato> pesquisar(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc, Integer seq,
			String descricao, DominioSituacao situacao) {

		DetachedCriteria criteria = this.pesquisarCriteria(seq, descricao,
				situacao);

		criteria.addOrder(Order.asc(ScoCriterioReajusteContrato.Fields.SEQ
				.toString()));

		return this.executeCriteria(criteria, firstResult, maxResult,
				orderProperty, asc);
	}
	
	public List<ScoCriterioReajusteContrato> listarCriteriosReajusteContratoAtivos(Object pesquisa) {

		DetachedCriteria criteria = DetachedCriteria.forClass(ScoCriterioReajusteContrato.class);
		
		String strParametro = (String) pesquisa;
		Integer seq = null;		
		if (CoreUtil.isNumeroInteger(strParametro)){
			seq = Integer.valueOf(strParametro);
		}
		if (seq != null) {
			criteria.add(Restrictions.eq(ScoCriterioReajusteContrato.Fields.SEQ.toString(), seq));

		} else {
			if (StringUtils.isNotBlank(strParametro)) {
				criteria.add(Restrictions.ilike(
						ScoCriterioReajusteContrato.Fields.DESCRICAO.toString(),
						strParametro, MatchMode.ANYWHERE));
			}
		}
		
		criteria.add(Restrictions.eq(ScoCriterioReajusteContrato.Fields.SITUACAO.toString(),DominioSituacao.A));
		
		criteria.addOrder(Order.asc(ScoCriterioReajusteContrato.Fields.DESCRICAO.toString()));
		
		return this.executeCriteria(criteria);
	}

}
