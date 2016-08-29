package br.gov.mec.aghu.faturamento.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.BackupCntaConv;

public class BackupCntaConvDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<BackupCntaConv> {

	private static final long serialVersionUID = -4922004576710431783L;

	public boolean temBackupContaConv(Integer nroConta) {
		DetachedCriteria criteria = DetachedCriteria.forClass(BackupCntaConv.class);
		criteria.add(Restrictions.eq(BackupCntaConv.Fields.NRO.toString(), nroConta));
		criteria.setProjection(Projections.projectionList().add(Projections.property(BackupCntaConv.Fields.NRO.toString())));
		Integer nroContaExistente = (Integer) this.executeCriteriaUniqueResult(criteria);
		if (nroContaExistente != null) {
			return true;
		}
		return false;
	}

}
