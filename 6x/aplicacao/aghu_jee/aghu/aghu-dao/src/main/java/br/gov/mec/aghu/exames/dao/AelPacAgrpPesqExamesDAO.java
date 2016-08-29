package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AelPacAgrpPesqExames;

public class AelPacAgrpPesqExamesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelPacAgrpPesqExames> {

	private static final long serialVersionUID = 4401358371856614423L;

	public List<AelPacAgrpPesqExames> listarPacAgrpPesqExamesPorCodigoPaciente(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelPacAgrpPesqExames.class);

		criteria.add(Restrictions.eq(AelPacAgrpPesqExames.Fields.PAC_CODIGO.toString(), pacCodigo));

		return executeCriteria(criteria);
	}

	public List<AelPacAgrpPesqExames> listarPacAgrpPesqExames(Integer pacCodigo, Integer[] axeSeqs) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelPacAgrpPesqExames.class);

		criteria.add(Restrictions.eq(AelPacAgrpPesqExames.Fields.PAC_CODIGO.toString(), pacCodigo));
		if (axeSeqs != null && axeSeqs.length > 0) {
			criteria.add(Restrictions.not(Restrictions.in(AelPacAgrpPesqExames.Fields.AXE_SEQ.toString(), axeSeqs)));
		}

		return executeCriteria(criteria);
	}

	public List<Integer> listarAxeSeqsPacAgrpPesq(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelPacAgrpPesqExames.class);
		
		criteria.add(Restrictions.eq(AelPacAgrpPesqExames.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.setProjection(Projections.property(AelPacAgrpPesqExames.Fields.AXE_SEQ.toString()));

		return executeCriteria(criteria);
	}

}
