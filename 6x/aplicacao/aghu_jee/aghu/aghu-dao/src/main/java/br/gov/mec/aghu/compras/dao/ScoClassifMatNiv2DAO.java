package br.gov.mec.aghu.compras.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.ScoClassifMatNiv2;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

/**
 * DAO de {@link ScoClassifMatNiv2}
 * 
 * @author luismoura
 * 
 */
public class ScoClassifMatNiv2DAO extends BaseDao<ScoClassifMatNiv2> {
	private static final long serialVersionUID = 4685826797151404151L;

	/**
	 * C7 de #5758
	 * 
	 * Obter máximo código da classificação nível 2
	 */
	@Override
	protected void obterValorSequencialId(ScoClassifMatNiv2 elemento) {
		if (elemento == null || elemento.getId() == null || elemento.getId().getCn1GmtCodigo() == null || elemento.getId().getCn1Codigo() == null) {
			throw new IllegalStateException("Elemento não pode ser null");
		}
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoClassifMatNiv2.class);
		criteria.add(Restrictions.eq(ScoClassifMatNiv2.Fields.CN1_GMT_CODIGO.toString(), elemento.getId().getCn1GmtCodigo()));
		criteria.add(Restrictions.eq(ScoClassifMatNiv2.Fields.CN1_CODIGO.toString(), elemento.getId().getCn1Codigo()));
		criteria.setProjection(Projections.max(ScoClassifMatNiv2.Fields.CODIGO.toString()));
		Integer codigo = (Integer) super.executeCriteriaUniqueResult(criteria);
		if (codigo == null) {
			codigo = 0;
		} else {
			codigo++;
		}
		elemento.getId().setCodigo(codigo);
	}
}