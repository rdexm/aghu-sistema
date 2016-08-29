package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MpmAltaAtendimento;
import br.gov.mec.aghu.model.MpmAltaSumarioId;

public class MpmAltaAtendimentoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmAltaAtendimento> {
	
	 /**
	 * 
	 */
	private static final long serialVersionUID = 583394186772421535L;

	/**
	 * 
	 * @param {MpmAltaSumarioId} altaSumarioId
	 * 
	 * @return MpmAltaAtendimento
	 */
	public List<MpmAltaAtendimento> pesquisarAltaAtendimentoPeloMpmAltaSumarioId(MpmAltaSumarioId altaSumarioId) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmAltaAtendimento.class);
		criteria.add(Restrictions.eq(
				MpmAltaAtendimento.Fields.ID_ALTA_SUMARIO.toString(), altaSumarioId));
		criteria.addOrder(Order.asc(MpmAltaAtendimento.Fields.SEQP.toString()));

		return executeCriteria(criteria);
	}
}
