package br.gov.mec.aghu.perinatologia.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.McoIntercorrencia;
import br.gov.mec.aghu.core.commons.CoreUtil;

/**
 * DAO da entidade McoIntercorrencia
 * O
 * @author luismoura
 * 
 */
public class McoIntercorrenciaDAO  extends br.gov.mec.aghu.core.persistence.dao.BaseDao<McoIntercorrencia> {

	private static final long serialVersionUID = 2167745691236967362L;
	
	/**
	 * Monta uma criteria de ativos por Seq Ou Descricao
	 * 
	 * C2 de #37859
	 * 
	 * @param parametro
	 * @return
	 */
	private DetachedCriteria montarCriteriaAtivosPorSeqOuDescricao(final String parametro) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoIntercorrencia.class);
		criteria.createAlias(McoIntercorrencia.Fields.AGH_CID.toString(), "CID", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq(McoIntercorrencia.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		if (StringUtils.isNotBlank(parametro)) {
			if (CoreUtil.isNumeroShort(parametro)) {
				criteria.add(Restrictions.eq(McoIntercorrencia.Fields.SEQ.toString(), Short.valueOf(parametro)));
			} else {
				criteria.add(Restrictions.ilike(McoIntercorrencia.Fields.DESCRICAO.toString(), parametro, MatchMode.ANYWHERE));
			}
		}
		return criteria;
	}

	/**
	 * Pesquisa de ativos por Seq Ou Descricao
	 * 
	 * C2 de #37859
	 * 
	 * @param parametro
	 * @param maxResults
	 * @return
	 */
	public List<McoIntercorrencia> pesquisarAtivosPorSeqOuDescricao(final String parametro, Integer maxResults) {
		DetachedCriteria criteria = this.montarCriteriaAtivosPorSeqOuDescricao(parametro);
		criteria.addOrder(Order.asc(McoIntercorrencia.Fields.DESCRICAO.toString()));
		if (maxResults != null) {
			return super.executeCriteria(criteria, 0, maxResults, null, true);
		}
		return super.executeCriteria(criteria);
	}

	/**
	 * Count de ativos por Seq Ou Descricao
	 * 
	 * C2 de #37859
	 * 
	 * @param parametro
	 * @return
	 */
	public Long pesquisarAtivosPorSeqOuDescricaoCount(final String parametro) {
		DetachedCriteria criteria = this.montarCriteriaAtivosPorSeqOuDescricao(parametro);
		return super.executeCriteriaCount(criteria);
	}
}
