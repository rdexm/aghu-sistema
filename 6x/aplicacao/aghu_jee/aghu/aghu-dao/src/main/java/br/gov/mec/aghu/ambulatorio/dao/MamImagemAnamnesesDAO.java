package br.gov.mec.aghu.ambulatorio.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MamImagemAnamnese;

public class MamImagemAnamnesesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MamImagemAnamnese> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4906347581669239248L;

	public List<MamImagemAnamnese> obterListaImagemAnamnesePorFanSeq(Long fanSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamImagemAnamnese.class);
		criteria.add(Restrictions.eq(MamImagemAnamnese.Fields.FAN_SEQ.toString(), fanSeq));
		return executeCriteria(criteria);
	}
}