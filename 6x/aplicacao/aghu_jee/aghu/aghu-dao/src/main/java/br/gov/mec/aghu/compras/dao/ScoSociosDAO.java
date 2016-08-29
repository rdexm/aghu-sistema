package br.gov.mec.aghu.compras.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.ScoSocios;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class ScoSociosDAO extends BaseDao<ScoSocios>{
	private static final long serialVersionUID = 193929243899889823L;

	public List<ScoSocios> listarSociosFornecedores(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, 
			Integer codigo, String nome, String rg, Long cpf){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoSocios.class);
		
		if (StringUtils.isNotBlank(orderProperty)) {
			if (asc) {
				criteria.addOrder(Order.asc(orderProperty));
			} else {
				criteria.addOrder(Order.desc(orderProperty));
			}
		}
		
		if(codigo != null) {
			criteria.add(Restrictions.eq(ScoSocios.Fields.SEQ.toString(), codigo));
		}
		
		if(StringUtils.isNotBlank(nome)) {
			criteria.add(Restrictions.ilike(ScoSocios.Fields.NOME.toString(), nome, MatchMode.ANYWHERE));
		}
		
		if(StringUtils.isNotBlank(rg)) {
			criteria.add(Restrictions.eq(ScoSocios.Fields.RG.toString(), rg));
		}
		
		if(cpf != null) {
			criteria.add(Restrictions.eq(ScoSocios.Fields.CPF.toString(), cpf));
		}
	
		if(firstResult == null && maxResult == null){
			return executeCriteria(criteria);
		}
		
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
		
	}
	
	public Long listarSociosFornecedoresCount(Integer codigo, String nome, String rg, Long cpf){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoSocios.class);
		
		if(codigo != null) {
			criteria.add(Restrictions.eq(ScoSocios.Fields.SEQ.toString(), codigo));
		}
		
		if(StringUtils.isNotBlank(nome)) {
			criteria.add(Restrictions.ilike(ScoSocios.Fields.NOME.toString(), nome, MatchMode.ANYWHERE));
		}
		
		if(StringUtils.isNotBlank(rg)) {
			criteria.add(Restrictions.eq(ScoSocios.Fields.RG.toString(), rg));
		}
		
		if(cpf != null) {
			criteria.add(Restrictions.eq(ScoSocios.Fields.CPF.toString(), cpf));
		}
	
		return executeCriteriaCount(criteria);
		
	}
	
	public Boolean verificarSocioExistentePorCPF(Long numCpf){
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoSocios.class);
		
		criteria.add(Restrictions.eq(ScoSocios.Fields.CPF.toString(), numCpf));
		
		return executeCriteriaExists(criteria);
	}
	
	public Boolean verificarSocioExistentePorRG(String numRg){
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoSocios.class);
		
		criteria.add(Restrictions.eq(ScoSocios.Fields.RG.toString(), numRg));
		
		return executeCriteriaExists(criteria);
	}
}
