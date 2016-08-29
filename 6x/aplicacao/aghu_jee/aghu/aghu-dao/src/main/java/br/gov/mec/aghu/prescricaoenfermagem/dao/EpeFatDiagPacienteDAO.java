package br.gov.mec.aghu.prescricaoenfermagem.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.EpeFatDiagPaciente;

public class EpeFatDiagPacienteDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<EpeFatDiagPaciente> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7378470645592337913L;

	public DetachedCriteria montarCriteriaDiagnosticos(Short dgnSnbGnbSeq, Short dgnSnbSequencia, Short dgnSequencia, Short freSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(EpeFatDiagPaciente.class, "fdp");

		if (dgnSnbGnbSeq != null) {
			criteria.add(Restrictions.eq(EpeFatDiagPaciente.Fields.ID_FDG_DGN_SNB_GNB_SEQ.toString(), dgnSnbGnbSeq));
		}
		if (dgnSnbSequencia != null) {
			criteria.add(Restrictions.eq(EpeFatDiagPaciente.Fields.ID_FDG_DGN_SNB_SEQUENCIA.toString(), dgnSnbSequencia));
		}
		if (dgnSequencia != null) {
			criteria.add(Restrictions.eq(EpeFatDiagPaciente.Fields.ID_FDG_DGN_SEQUENCIA.toString(), dgnSequencia));
		}
		if (freSeq != null) {
			criteria.add(Restrictions.eq(EpeFatDiagPaciente.Fields.ID_FDG_FRE_SEQ.toString(), freSeq));
		}

		return criteria;
	}

	public List<EpeFatDiagPaciente> obterEpeCuidadoDiagnostico(Short dgnSnbGnbSeq, Short dgnSnbSequencia, Short dgnSequencia, Short freSeq) {
		
		DetachedCriteria criteria = montarCriteriaDiagnosticos(dgnSnbGnbSeq, dgnSnbSequencia, dgnSequencia, freSeq);

		return executeCriteria(criteria);
		
	}
	
}