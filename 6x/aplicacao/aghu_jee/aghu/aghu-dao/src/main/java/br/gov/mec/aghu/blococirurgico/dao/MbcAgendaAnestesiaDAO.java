package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MbcAgendaAnestesia;

public class MbcAgendaAnestesiaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcAgendaAnestesia> {

	private static final long serialVersionUID = 3191623341939539319L;
	
	public List<MbcAgendaAnestesia> listarAgendaAnestesiaPorAgdSeq(Integer agdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcAgendaAnestesia.class, "aga");
		criteria.createAlias("aga." + MbcAgendaAnestesia.Fields.MBC_TIPO_ANESTESIAS.toString(), "tpa", Criteria.INNER_JOIN);
		criteria.createAlias("aga." + MbcAgendaAnestesia.Fields.MBC_AGENDAS.toString(), "agd", Criteria.INNER_JOIN);

		criteria.add(Restrictions.eq(MbcAgendaAnestesia.Fields.ID_AGD_SEQ.toString(), agdSeq));
		
		return executeCriteria(criteria);
	}
	
}