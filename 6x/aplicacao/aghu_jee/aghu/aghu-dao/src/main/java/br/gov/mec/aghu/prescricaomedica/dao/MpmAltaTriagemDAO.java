package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MpmAltaSumarioId;
import br.gov.mec.aghu.model.MpmAltaTriagem;

public class MpmAltaTriagemDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmAltaTriagem> {

	private static final long serialVersionUID = -7714206320716254748L;

	public List<MpmAltaTriagem> pesquisarAltaTriagemPeloMpmAltaSumarioId(MpmAltaSumarioId altaSumarioId) {
		DetachedCriteria criteria = getCriteriaAltaTriagemPeloMpmAltaSumarioId(altaSumarioId);
		return executeCriteria(criteria);
	}

	private DetachedCriteria getCriteriaAltaTriagemPeloMpmAltaSumarioId(
			MpmAltaSumarioId altaSumarioId) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAltaTriagem.class);
		criteria.add(Restrictions.eq(MpmAltaTriagem.Fields.ID_ALTA_SUMARIO.toString(), altaSumarioId));
		return criteria;
	}
	
	public Long pesquisarAltaTriagemPeloMpmAltaSumarioIdCount(MpmAltaSumarioId altaSumarioId) {
		DetachedCriteria criteria = getCriteriaAltaTriagemPeloMpmAltaSumarioId(altaSumarioId);
		return executeCriteriaCount(criteria);
	}
}
