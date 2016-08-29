package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.MbcAgendaDiagnostico;
import br.gov.mec.aghu.model.MbcAgendas;

public class MbcAgendaDiagnosticoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcAgendaDiagnostico> {

	private static final long	serialVersionUID	= 7424083884099273173L;

	/**
	 * 
	 * @param seq
	 * @return
	 */
	public MbcAgendaDiagnostico obterAgendaDiagnosticoEscalaCirurgicaPorAgenda(Integer seq) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcAgendaDiagnostico.class);
		
		criteria.createAlias(MbcAgendaDiagnostico.Fields.MBC_AGENDAS.toString(), "AGD", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(MbcAgendaDiagnostico.Fields.AGH_CID.toString(), "CID", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq("AGD.".concat(MbcAgendas.Fields.SEQ.toString()), seq));
		
		return (MbcAgendaDiagnostico) executeCriteriaUniqueResult(criteria);
	}

	public List<MbcAgendaDiagnostico> pesquisarAgendaDiagnosticoEscalaCirurgicaPorAgenda(Integer seq) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcAgendaDiagnostico.class);
		
		criteria.createAlias(MbcAgendaDiagnostico.Fields.MBC_AGENDAS.toString(), "AGD", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(MbcAgendaDiagnostico.Fields.AGH_CID.toString(), "CID", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq("AGD.".concat(MbcAgendas.Fields.SEQ.toString()), seq));
		
		return executeCriteria(criteria);
	}
}
