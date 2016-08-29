package br.gov.mec.aghu.paciente.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AipPacHcpaXPacUbs;

public class AipPacHcpaXPacUbsDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AipPacHcpaXPacUbs> {

	private static final long serialVersionUID = -5905991757918840444L;

	public List<AipPacHcpaXPacUbs> pesquisarAipPacHcpaXPacUbs(
			Integer pacCodigoHCPA, Integer pacCodigoUBS) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AipPacHcpaXPacUbs.class);
		if (pacCodigoHCPA != null) {
			criteria.add(Restrictions.eq(
					AipPacHcpaXPacUbs.Fields.PAC_CODIGO_HCPA.toString(),
					pacCodigoHCPA));
		}
		if (pacCodigoUBS != null) {
			criteria.add(Restrictions.eq(
					AipPacHcpaXPacUbs.Fields.PAC_CODIGO_UBS.toString(),
					pacCodigoUBS));
		}

		return executeCriteria(criteria);
	}

}
