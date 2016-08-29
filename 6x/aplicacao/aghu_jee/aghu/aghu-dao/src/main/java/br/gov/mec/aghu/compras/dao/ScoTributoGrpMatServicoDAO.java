package br.gov.mec.aghu.compras.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoTributoGrpMatServico;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

/**
 * DAO de {@link ScoTributoGrpMatServico}
 * 
 * @author luismoura
 * 
 */
public class ScoTributoGrpMatServicoDAO extends BaseDao<ScoTributoGrpMatServico> {
	private static final long serialVersionUID = -6287704594641892397L;

	/**
	 * Verifica se existe uma ScoTributoGrpMatServico pelo código do ScoGrupoMaterial
	 * 
	 * C9 de #31584
	 * 
	 * @param gmtCodigo
	 * @return
	 */
	public boolean verificarExistenciaScoTributoGrpMatServicoPorScoGrupoMaterial(Integer gmtCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoTributoGrpMatServico.class);
		criteria.createAlias(ScoTributoGrpMatServico.Fields.SCO_GRUPO_MATERIAL.toString(), "GMT");
		criteria.add(Restrictions.eq("GMT." + ScoGrupoMaterial.Fields.CODIGO.toString(), gmtCodigo));
		return super.executeCriteriaExists(criteria);
	}
}
