package br.gov.mec.aghu.faturamento.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.FatAutorizaApac;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class FatAutorizaApacsDAO extends BaseDao<FatAutorizaApac> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6476510739315663603L;
	
	public FatAutorizaApac obterFatAutorizaApacPorCpf(Long cpf) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatAutorizaApac.class);
		
		criteria.add(Restrictions.eq(FatAutorizaApac.Fields.CPF.toString(), cpf));
		
		return (FatAutorizaApac) executeCriteriaUniqueResult(criteria); 
	}

}
