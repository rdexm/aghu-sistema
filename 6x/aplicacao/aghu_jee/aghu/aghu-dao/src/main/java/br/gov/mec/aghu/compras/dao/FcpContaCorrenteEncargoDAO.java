package br.gov.mec.aghu.compras.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.FcpContaCorrenteEncargo;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class FcpContaCorrenteEncargoDAO extends BaseDao<FcpContaCorrenteEncargo> {

	private static final long serialVersionUID = 5386043856979758403L;
	private static final String ALIAS_FCP_CONTA_CORRENTE_ENCARGO = "CCE";
	private static final String PONTO = "."; 
	
	public Long countPorAgenciaBanco(Short bcoCodigo, Integer codigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FcpContaCorrenteEncargo.class, ALIAS_FCP_CONTA_CORRENTE_ENCARGO);
		
		criteria.add(Restrictions.eq(ALIAS_FCP_CONTA_CORRENTE_ENCARGO + PONTO + FcpContaCorrenteEncargo.Fields.AGB_BCO_CODIGO.toString(), bcoCodigo));
		criteria.add(Restrictions.eq(ALIAS_FCP_CONTA_CORRENTE_ENCARGO + PONTO + FcpContaCorrenteEncargo.Fields.AGB_CODIGO.toString(), codigo));
		
		return executeCriteriaCount(criteria);
	}
}