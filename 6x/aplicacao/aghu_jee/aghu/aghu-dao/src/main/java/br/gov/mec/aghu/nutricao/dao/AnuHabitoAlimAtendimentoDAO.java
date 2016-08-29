package br.gov.mec.aghu.nutricao.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AnuHabitoAlimAtendimento;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;

public class AnuHabitoAlimAtendimentoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmPrescricaoMedica> {


	private static final long serialVersionUID = -2184330730737063865L;

	/**
	 * ORADB CURSOR c_haa
	 * 
	 * @param atdSeq
	 * @return
	 */
	public List<Date> executarCursorHaa(Integer atdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AnuHabitoAlimAtendimento.class);
		criteria.add(Restrictions.eq(AnuHabitoAlimAtendimento.Fields.ATD_SEQ.toString(), atdSeq));

		ProjectionList p = Projections.projectionList();
		p.add(Projections.property(AnuHabitoAlimAtendimento.Fields.DTHR_INICIO.toString()));
		criteria.setProjection(p);

		criteria.addOrder(Order.asc(AnuHabitoAlimAtendimento.Fields.DTHR_INICIO.toString()));

		return this.executeCriteria(criteria);
	}

}
