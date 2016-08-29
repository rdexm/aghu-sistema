package br.gov.mec.aghu.transplante.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MtxResultadoExames;


public class MtxResultadoExamesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MtxResultadoExames> {

	private static final long serialVersionUID = -4422442395096441754L;
	
	/**
	 * Consulta para obter dados do paciente 
	 * #47146 - C5
	 */
	public List<MtxResultadoExames> obterResultadosExames(Integer seqTransplante){
		DetachedCriteria criteria = DetachedCriteria.forClass(MtxResultadoExames.class, "REX");
		
		criteria.add(Restrictions.eq("REX." + MtxResultadoExames.Fields.TRP_SEQ.toString(), seqTransplante));
		
		return executeCriteria (criteria);
	}

}
