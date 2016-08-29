package br.gov.mec.aghu.estoque.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.SceAlmoxarifado;
import br.gov.mec.aghu.model.SceAlmoxarifadoComposicao;
import br.gov.mec.aghu.model.SceAlmoxarifadoGrupos;
import br.gov.mec.aghu.model.ScoGrupoMaterial;

public class SceAlmoxarifadoGruposDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SceAlmoxarifadoGrupos> {

	private static final long serialVersionUID = 7933765108239476434L;
	
	public List<SceAlmoxarifadoGrupos> pesquisarGruposPorComposicao(SceAlmoxarifadoComposicao comp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceAlmoxarifadoGrupos.class);

		if (comp != null) {
			criteria.add(Restrictions.eq(SceAlmoxarifadoGrupos.Fields.COMPOSICAO.toString(), comp));
		}
		
		return this.executeCriteria(criteria);
	}
	
	public Boolean verificarGrupoOutraComposicao(ScoGrupoMaterial grupo, Integer seqComposicao, SceAlmoxarifado almox) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceAlmoxarifadoGrupos.class, "ALG");
		criteria.createAlias("ALG."+SceAlmoxarifadoGrupos.Fields.COMPOSICAO.toString(), "ALC");
		criteria.setProjection(Projections.property("ALG."+SceAlmoxarifadoGrupos.Fields.SEQ.toString()));
		
		if (grupo != null) {
			criteria.add(Restrictions.eq("ALG."+SceAlmoxarifadoGrupos.Fields.GRUPO_MATERIAL.toString(), grupo));
		}
		
		if (seqComposicao != null) {
			criteria.add(Restrictions.ne("ALG."+SceAlmoxarifadoGrupos.Fields.SEQ_COMPOSICAO.toString(), seqComposicao));
		}
		
		if (almox != null) {
			criteria.add(Restrictions.eq("ALC."+SceAlmoxarifadoComposicao.Fields.ALMOXARIFADO.toString(), almox));
		}
		
		return executeCriteriaCount(criteria) > 0;
	}
	
	public List<SceAlmoxarifadoGrupos> pesquisarGruposPorAlmoxarifado(SceAlmoxarifado almox) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceAlmoxarifadoGrupos.class, "ALG");
		criteria.createAlias("ALG."+SceAlmoxarifadoGrupos.Fields.COMPOSICAO.toString(), "ALC");
        criteria.createAlias("ALG."+SceAlmoxarifadoGrupos.Fields.GRUPO_MATERIAL.toString(), "GRPMAT");

		if (almox != null) {
			criteria.add(Restrictions.eq("ALC."+SceAlmoxarifadoComposicao.Fields.ALMOXARIFADO.toString(), almox));
		}
		
		return this.executeCriteria(criteria);
	}
}