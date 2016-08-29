package br.gov.mec.aghu.prescricaoenfermagem.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.EpeCuidados;
import br.gov.mec.aghu.model.EpeItemCuidadoSumario;

public class EpeItemCuidadoSumarioDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<EpeItemCuidadoSumario>{

	private static final long serialVersionUID = 5398116245783792990L;
	
	/**
	 * 
	 * @param seq
	 * @return
	 */
	public List<EpeItemCuidadoSumario> obterEpeItemCuidadoSumarioPorEpeCuidadoSeq(Short seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(EpeItemCuidadoSumario.class);
		criteria.add(Restrictions.eq(EpeItemCuidadoSumario.Fields.CUIDADO.toString() + "." +  EpeCuidados.Fields.SEQ.toString(), seq));
		return executeCriteria(criteria);
	}

}
