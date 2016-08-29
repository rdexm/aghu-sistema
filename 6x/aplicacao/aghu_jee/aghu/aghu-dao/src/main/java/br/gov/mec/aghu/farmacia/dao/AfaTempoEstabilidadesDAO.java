package br.gov.mec.aghu.farmacia.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AfaTempoEstabilidades;
import br.gov.mec.aghu.model.MpmUnidadeTempo;

public class AfaTempoEstabilidadesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AfaTempoEstabilidades>{
	
	private static final long serialVersionUID = 831419407065664550L;

	/**
	 * Executa a seguinte consulta para verificar se existe dependentes em AfaTempoEstabilidades.
	 * @param Object MpmUnidadeTempo
	 * @return Boolean
	 */
	public Boolean verificaExclusao(MpmUnidadeTempo unidadeTempo) {
		
		List<AfaTempoEstabilidades> result = null;
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaTempoEstabilidades.class);
		criteria.add(Restrictions.eq(AfaTempoEstabilidades.Fields.UTPSEQ.toString(), unidadeTempo.getSeq()));
		result =  executeCriteria(criteria);
		
		if(result.size()>0){
			return true;
		} else {
			return false;
		}
	}
}
