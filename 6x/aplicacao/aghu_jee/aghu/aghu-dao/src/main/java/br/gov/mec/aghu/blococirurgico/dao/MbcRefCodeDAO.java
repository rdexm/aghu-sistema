package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MbcRefCode;


public class MbcRefCodeDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcRefCode> {

	private static final long serialVersionUID = 5118229737129021551L;

	public List<MbcRefCode> buscarRefCodPorDominioEAbbreviation(String dominio, String abbreviation) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcRefCode.class);

		criteria.add(Restrictions.eq(MbcRefCode.Fields.DOMINIO.toString(),dominio));
		criteria.add(Restrictions.eq(MbcRefCode.Fields.ABBREVIATION.toString(),abbreviation));
	
		return executeCriteria(criteria);
	}
	
	public MbcRefCode buscarRefCodePorDominioEJustificativa(String dominio, String dominioTipoAgendaJustificativa) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcRefCode.class);
		
		criteria.createAlias(MbcRefCode.Fields.DOMINIO.toString(), dominio);
		criteria.createAlias(MbcRefCode.Fields.RV_LOW_VALUE.toString(), dominioTipoAgendaJustificativa);

		return (MbcRefCode) executeCriteriaUniqueResult(criteria);	
	}
}