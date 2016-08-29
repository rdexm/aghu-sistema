package br.gov.mec.aghu.compras.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.ScoClassifMatNiv3;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

/**
 * DAO de {@link ScoClassifMatNiv3}
 * 
 * @author luismoura
 * 
 */
public class ScoClassifMatNiv3DAO extends BaseDao<ScoClassifMatNiv3> {
	private static final long serialVersionUID = 6210839273003512978L;

	/**
	 * C8 de #5758
	 * 
	 * Obter máximo código da classificação nível 3
	 */
	@Override
	protected void obterValorSequencialId(ScoClassifMatNiv3 elemento) {
		if (elemento == null || elemento.getId() == null || elemento.getId().getCn2Cn1GmtCodigo() == null || elemento.getId().getCn2Cn1Codigo() == null
				|| elemento.getId().getCn2Codigo() == null) {
			throw new IllegalStateException("Elemento não pode ser null");
		}
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoClassifMatNiv3.class);
		criteria.add(Restrictions.eq(ScoClassifMatNiv3.Fields.CN2_CN1_GMT_CODIGO.toString(), elemento.getId().getCn2Cn1GmtCodigo()));
		criteria.add(Restrictions.eq(ScoClassifMatNiv3.Fields.CN2_CN1_CODIGO.toString(), elemento.getId().getCn2Cn1Codigo()));
		criteria.add(Restrictions.eq(ScoClassifMatNiv3.Fields.CN2_CODIGO.toString(), elemento.getId().getCn2Codigo()));
		criteria.setProjection(Projections.max(ScoClassifMatNiv3.Fields.CODIGO.toString()));
		Integer codigo = (Integer) super.executeCriteriaUniqueResult(criteria);
		if (codigo == null) {
			codigo = 0;
		} else {
			codigo++;
		}
		elemento.getId().setCodigo(codigo);
	}
}