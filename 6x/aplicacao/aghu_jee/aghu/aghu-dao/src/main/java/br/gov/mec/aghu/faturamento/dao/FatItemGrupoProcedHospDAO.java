package br.gov.mec.aghu.faturamento.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.FatItemGrupoProcedHosp;

public class FatItemGrupoProcedHospDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FatItemGrupoProcedHosp> {

	private static final long serialVersionUID = -3875310408161597355L;

	public List<FatItemGrupoProcedHosp> listarItensGrupoProcedimentoHospitalar(Short iphPhoSeq, Integer iphSeq, Integer firstResult, Integer maxResults) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatItemGrupoProcedHosp.class);

		criteria.add(Restrictions.eq(FatItemGrupoProcedHosp.Fields.IPH_PHO_SEQ.toString(), iphPhoSeq));

		criteria.add(Restrictions.eq(FatItemGrupoProcedHosp.Fields.IPH_SEQ.toString(), iphSeq));

		if (firstResult != null && maxResults != null) {
			return executeCriteria(criteria, firstResult, maxResults, null, false);
		} else {
			return executeCriteria(criteria);
		}
	}

}
