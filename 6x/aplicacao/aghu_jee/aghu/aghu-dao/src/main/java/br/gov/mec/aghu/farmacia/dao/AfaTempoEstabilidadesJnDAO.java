package br.gov.mec.aghu.farmacia.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AfaTempoEstabilidadesJn;
import br.gov.mec.aghu.model.MpmUnidadeTempo;

public class AfaTempoEstabilidadesJnDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AfaTempoEstabilidadesJn>{
	
	private static final long serialVersionUID = -200688541345292790L;

	/**
	 * Executa a seguinte consulta para verificar se existe dependentes em AfaTempoEstabilidadesJN.
	 * @param Object MpmUnidadeTempo
	 * @return Boolean
	 */
	public Boolean verificaExclusao(MpmUnidadeTempo unidadeTempo) {
		
		List<AfaTempoEstabilidadesJn> result = null;
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaTempoEstabilidadesJn.class);
		criteria.add(Restrictions.eq(AfaTempoEstabilidadesJn.Fields.UTPSEQ.toString(), unidadeTempo.getSeq()));
		result =  executeCriteria(criteria);
		
		if(result.size()>0){
			return true;
		} else {
			return false;
		}
	}
}
