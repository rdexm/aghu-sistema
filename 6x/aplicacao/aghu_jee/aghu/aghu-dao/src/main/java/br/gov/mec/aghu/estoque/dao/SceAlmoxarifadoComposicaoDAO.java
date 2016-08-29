package br.gov.mec.aghu.estoque.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.SceAlmoxarifado;
import br.gov.mec.aghu.model.SceAlmoxarifadoComposicao;
import br.gov.mec.aghu.model.SceAlmoxarifadoGrupos;
import br.gov.mec.aghu.model.ScoGrupoMaterial;

public class SceAlmoxarifadoComposicaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SceAlmoxarifadoComposicao> {

	private static final long serialVersionUID = 7133765108239416434L;
	
	public List<SceAlmoxarifadoComposicao> pesquisarPorAlmoxarifado(SceAlmoxarifado almox) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceAlmoxarifadoComposicao.class);

		if (almox != null) {
			criteria.add(Restrictions.eq(SceAlmoxarifadoComposicao.Fields.ALMOXARIFADO.toString(), almox));
		}
		
		return this.executeCriteria(criteria);
	}
	
	public Boolean verificarAlmoxarifadoPermiteGrupo(SceAlmoxarifado almox, ScoGrupoMaterial grupo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceAlmoxarifadoGrupos.class, "ALG");
		criteria.createAlias("ALG."+SceAlmoxarifadoGrupos.Fields.COMPOSICAO.toString(), "ALC");
		
		criteria.setProjection(Projections.property(SceAlmoxarifadoGrupos.Fields.SEQ.toString()));
		if (grupo!= null) {
			criteria.add(Restrictions.eq("ALG."+SceAlmoxarifadoGrupos.Fields.GRUPO_MATERIAL.toString(), grupo));
		}
		
		if (almox != null) {
			criteria.add(Restrictions.eq("ALC."+SceAlmoxarifadoComposicao.Fields.ALMOXARIFADO.toString(), almox));
		}
		
		return this.executeCriteriaCount(criteria) > 0;
	}
}
