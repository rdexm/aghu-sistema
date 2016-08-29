package br.gov.mec.aghu.paciente.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AipGrupoFamiliarJn;

public class AipGrupoFamiliarJnDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AipGrupoFamiliarJn>{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 9139819270239506894L;

	public List<AipGrupoFamiliarJn> pesquisarGrupoFamiliarJn(Integer seq){
		DetachedCriteria criteria = DetachedCriteria.forClass(AipGrupoFamiliarJn.class, "JN");
		criteria.add(Restrictions.eq("JN."+AipGrupoFamiliarJn.Fields.SEQ.toString(), seq));
		return executeCriteria(criteria);
	}
}
