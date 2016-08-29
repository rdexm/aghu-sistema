package br.gov.mec.aghu.compras.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.VScoClasMaterial;
import br.gov.mec.aghu.core.commons.CoreUtil;

/**
 * 
 * @modulo compras
 *
 */
public class VScoClasMaterialDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<VScoClasMaterial>{
	
	private static final long serialVersionUID = -8023953838539110155L;

	/**
	 * Obtem VScoClasMaterial por número
	 * @param numero
	 * @return
	 */
	public VScoClasMaterial obterVScoClasMaterialPorNumero(Long numero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(VScoClasMaterial.class, "VCA");
		criteria.add(Restrictions.eq(VScoClasMaterial.Fields.NUMERO.toString(), numero));
		return (VScoClasMaterial)executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * Pesquisa uma lista de VScoClasMaterial
	 * @param codigoGrupo
	 * @return
	 */
	public List<VScoClasMaterial> pesquisarClassificacaoMaterialTransferenciaAutoAlmoxarifados(Object param, Short gmtCodigo){

		DetachedCriteria criteria = DetachedCriteria.forClass(VScoClasMaterial.class, "VCA");
		
		String strPesquisa = (String) param;
		if (StringUtils.isNotBlank(strPesquisa)) {

			if (CoreUtil.isNumeroLong(strPesquisa)) {
				if (!isOracle()) {
					criteria.add(Restrictions.sqlRestriction("cast(numero as varchar) like '"+strPesquisa+"%'"));
				} else {
					criteria.add(Restrictions.sqlRestriction("to_char(numero) like '"+strPesquisa+"%'"));
				}
			} else {
				criteria.add(Restrictions.ilike(VScoClasMaterial.Fields.DESCRICAO.toString(), StringUtils.trim(strPesquisa), MatchMode.ANYWHERE));
			}
		}
		if (gmtCodigo != null) {
			criteria.add(Restrictions.eq(VScoClasMaterial.Fields.GRUPO.toString(), gmtCodigo));
		}
		
		DetachedCriteria subQuery = DetachedCriteria.forClass(SceEstoqueAlmoxarifado.class,"EAL1");
		subQuery.setProjection(Projections.distinct(Projections.property("GMT."+ScoGrupoMaterial.Fields.CODIGO.toString())));
		subQuery.createAlias("EAL1."+SceEstoqueAlmoxarifado.Fields.MATERIAL.toString(), "MAT",JoinType.INNER_JOIN);
		subQuery.createAlias("MAT."+ScoMaterial.Fields.GRUPO_MATERIAL.toString(), "GMT",JoinType.INNER_JOIN);

		criteria.add(Subqueries.propertyIn("VCA."+VScoClasMaterial.Fields.GRUPO.toString(), subQuery));
		
		return executeCriteria(criteria);
		
	}
	
}