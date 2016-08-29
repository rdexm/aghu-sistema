package br.gov.mec.aghu.compras.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.ScoClassifMatNiv1;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

/**
 * DAO de {@link ScoClassifMatNiv1}
 * 
 * @author luismoura
 * 
 */
public class ScoClassifMatNiv1DAO extends BaseDao<ScoClassifMatNiv1> {
	private static final long serialVersionUID = -6983597117828549925L;

	/**
	 * C6 de #5758
	 * 
	 * Obter máximo código da classificação nível 1
	 */
	@Override
	public void obterValorSequencialId(ScoClassifMatNiv1 elemento) {
		if (elemento == null || elemento.getId() == null || elemento.getId().getGmtCodigo() == null) {
			throw new IllegalStateException("Elemento não pode ser null");
		}
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoClassifMatNiv1.class);
		criteria.add(Restrictions.eq(ScoClassifMatNiv1.Fields.GMT_CODIGO.toString(), elemento.getId().getGmtCodigo()));
		criteria.setProjection(Projections.max(ScoClassifMatNiv1.Fields.CODIGO.toString()));
		Integer codigo = (Integer) this.executeCriteriaUniqueResult(criteria);
		if (codigo == null) {
			codigo = 0;
		} else {
			codigo++;
		}
		elemento.getId().setCodigo(codigo);
	}

	public List<ScoClassifMatNiv1> listarScoClassifMatNiv1PorGrupoMaterial(
			Integer codigoGrupoMaterial) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoClassifMatNiv1.class);
		criteria.add(Restrictions.eq(ScoClassifMatNiv1.Fields.GMT_CODIGO.toString(), codigoGrupoMaterial));
		return this.executeCriteria(criteria);
		
	}
}