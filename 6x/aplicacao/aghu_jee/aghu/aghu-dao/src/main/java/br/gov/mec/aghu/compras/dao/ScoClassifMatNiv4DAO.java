package br.gov.mec.aghu.compras.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.ScoClassifMatNiv4;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

/**
 * DAO de {@link ScoClassifMatNiv4}
 * 
 * @author luismoura
 * 
 */
public class ScoClassifMatNiv4DAO extends BaseDao<ScoClassifMatNiv4> {
	private static final long serialVersionUID = 6210839273003512978L;

	/**
	 * C9 de #5758
	 * 
	 * Obter máximo código da classificação nível 4
	 */
	@Override
	protected void obterValorSequencialId(ScoClassifMatNiv4 elemento) {
		if (elemento == null || elemento.getId() == null || elemento.getId().getCn3Cn2Cn1GmtCodigo() == null || elemento.getId().getCn3Cn2Cn1Codigo() == null
				|| elemento.getId().getCn3Cn2Codigo() == null || elemento.getId().getCn3Codigo() == null) {
			throw new IllegalStateException("Elemento não pode ser null");
		}
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoClassifMatNiv4.class);
		criteria.add(Restrictions.eq(ScoClassifMatNiv4.Fields.CN3_CN2_CN1_GMT_CODIGO.toString(), elemento.getId().getCn3Cn2Cn1GmtCodigo()));
		criteria.add(Restrictions.eq(ScoClassifMatNiv4.Fields.CN3_CN2_CN1_CODIGO.toString(), elemento.getId().getCn3Cn2Cn1Codigo()));
		criteria.add(Restrictions.eq(ScoClassifMatNiv4.Fields.CN3_CN2_CODIGO.toString(), elemento.getId().getCn3Cn2Codigo()));
		criteria.add(Restrictions.eq(ScoClassifMatNiv4.Fields.CN3_CODIGO.toString(), elemento.getId().getCn3Codigo()));
		criteria.setProjection(Projections.max(ScoClassifMatNiv4.Fields.CODIGO.toString()));
		Integer codigo = (Integer) super.executeCriteriaUniqueResult(criteria);
		if (codigo == null) {
			codigo = 0;
		} else {
			codigo++;
		}
		elemento.getId().setCodigo(codigo);
	}
}