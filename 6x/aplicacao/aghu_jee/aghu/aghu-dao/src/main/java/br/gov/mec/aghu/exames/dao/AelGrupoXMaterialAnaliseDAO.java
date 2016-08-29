package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.AelGrupoXMaterialAnalise;

public class AelGrupoXMaterialAnaliseDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelGrupoXMaterialAnalise>{

	private static final long serialVersionUID = -301767562809842616L;

	/**
	 * Método que retorna lista com relacionamento entre grupo material análise e material
	 * análise
	 * @param gmaSeq
	 * @return
	 */
	public List<AelGrupoXMaterialAnalise> pesquisarGrupoXMateriaisAnalisesPorGrupo(Integer gmaSeq) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelGrupoXMaterialAnalise.class, "GXM");
		criteria.createAlias("GXM."+AelGrupoXMaterialAnalise.Fields.MATERIAL_ANALISE.toString(), "MAT", JoinType.INNER_JOIN);
		
		if(gmaSeq!=null) {
			criteria.add(Restrictions.eq("GXM."+AelGrupoXMaterialAnalise.Fields.GMA_SEQ.toString(), gmaSeq));
		}
		return executeCriteria(criteria);
	}
	
}
