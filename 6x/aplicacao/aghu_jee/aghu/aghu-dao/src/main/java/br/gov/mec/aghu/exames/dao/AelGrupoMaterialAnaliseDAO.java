package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AelGrupoMaterialAnalise;

public class AelGrupoMaterialAnaliseDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelGrupoMaterialAnalise> {
	
	private static final long serialVersionUID = 6735036527226049381L;

	public List<AelGrupoMaterialAnalise> pesquisarGrupoMaterialAnalise(Integer firstResults, Integer maxResults, String orderProperty, boolean asc, AelGrupoMaterialAnalise grupoMaterialAnalise) {
		return executeCriteria(this.obterCriteriaGrupoMaterialAnalise(grupoMaterialAnalise), firstResults, maxResults, orderProperty, asc);
	}
	
	public Long pesquisarGrupoMaterialAnaliseCount(AelGrupoMaterialAnalise grupoMaterialAnalise) {
		return executeCriteriaCount(this.obterCriteriaGrupoMaterialAnalise(grupoMaterialAnalise));
	}
	
	private DetachedCriteria obterCriteriaGrupoMaterialAnalise(AelGrupoMaterialAnalise grupoMaterialAnalise) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelGrupoMaterialAnalise.class);
		if(grupoMaterialAnalise.getSeq()!=null) {
			criteria.add(Restrictions.eq(AelGrupoMaterialAnalise.Fields.SEQ.toString(), grupoMaterialAnalise.getSeq()));
		}
		
		if(StringUtils.isNotBlank(grupoMaterialAnalise.getDescricao())) {
			criteria.add(Restrictions.ilike(AelGrupoMaterialAnalise.Fields.DESCRICAO.toString(), grupoMaterialAnalise.getDescricao(),MatchMode.ANYWHERE));
		}
		
		if(grupoMaterialAnalise.getOrdProntOnline()!=null) {
			criteria.add(Restrictions.eq(AelGrupoMaterialAnalise.Fields.ORD_PRONT_ONLINE.toString(), grupoMaterialAnalise.getOrdProntOnline()));
		}
		
		if(grupoMaterialAnalise.getIndSituacao()!=null) {
			criteria.add(Restrictions.eq(AelGrupoMaterialAnalise.Fields.IND_SITUACAO.toString(), grupoMaterialAnalise.getIndSituacao()));
		}
		return criteria;
	}
}
