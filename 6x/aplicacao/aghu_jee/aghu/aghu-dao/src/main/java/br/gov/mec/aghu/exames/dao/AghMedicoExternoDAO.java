package br.gov.mec.aghu.exames.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.AghMedicoExterno;


public class AghMedicoExternoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AghMedicoExterno> {

	
	
	private static final long serialVersionUID = 4351991315399497325L;

	
	public AghMedicoExterno obterMedicoExternoPorPK(final Integer seq){
		AghMedicoExterno medicoExterno = null;
		
		if (seq == null){
			return null;
		}
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AghMedicoExterno.class);
		criteria.createAlias(AghMedicoExterno.Fields.CBO.toString(), "cbo", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq(AghMedicoExterno.Fields.SEQ.toString(), seq));
		
		medicoExterno = (AghMedicoExterno) executeCriteriaUniqueResult(criteria);
		
		return medicoExterno;
	}

	public Long countMedicoExterno(Map<Object, Object> filtersMap) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AghMedicoExterno.class);
		mountCriteria(criteria, filtersMap);
		
		return executeCriteriaCount(criteria);
	}
	
	public List<AghMedicoExterno> pesquisaMedicoExternoPaginado(Map<Object, Object> filtersMap, 
			Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AghMedicoExterno.class);
		mountCriteria(criteria, filtersMap);
		
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}
		
	
	private void mountCriteria(DetachedCriteria criteria, Map<Object, Object> filtersMap) {
		if(filtersMap.get(AghMedicoExterno.Fields.SEQ) != null) {
			final Integer codigo = (Integer) filtersMap.get(AghMedicoExterno.Fields.SEQ);
			criteria.add(Restrictions.eq(AghMedicoExterno.Fields.SEQ.toString(), codigo.intValue()));
		}
		
		if(filtersMap.get(AghMedicoExterno.Fields.CRM) != null) {
			final String crm = (String) filtersMap.get(AghMedicoExterno.Fields.CRM);
			criteria.add(Restrictions.eq(AghMedicoExterno.Fields.CRM.toString(), crm));
		}
		
		if(filtersMap.get(AghMedicoExterno.Fields.NOME) != null 
				&& StringUtils.isNotEmpty((String) filtersMap.get(AghMedicoExterno.Fields.NOME))) {
			final String nome = (String) filtersMap.get(AghMedicoExterno.Fields.NOME);
			criteria.add(Restrictions.ilike(AghMedicoExterno.Fields.NOME.toString(), nome.toUpperCase(), MatchMode.ANYWHERE));
		}
		
		if(filtersMap.get(AghMedicoExterno.Fields.CPF) != null) {
			final Long cpf = (Long) filtersMap.get(AghMedicoExterno.Fields.CPF);
			criteria.add(Restrictions.eq(AghMedicoExterno.Fields.CPF.toString(), cpf.longValue()));
		}
	}
	
	/**
	 * Obtem uma lista de medico externo<br>
	 * a partir de uma busca feita na suggestion.
	 * 
	 * @param parametro
	 * @return
	 */
	public List<AghMedicoExterno> obterMedicoExternoList(String parametro) {
		DetachedCriteria criteria = this.makeCriteriaObterMedicoExternoList(parametro);
		
		criteria.addOrder(Order.asc(AghMedicoExterno.Fields.NOME.toString()));
		
		return executeCriteria(criteria, 0, 400, new HashMap<String, Boolean>());
	}
	
	public Long obterMedicoExternoListCount(String parametro) {
		DetachedCriteria criteria = this.makeCriteriaObterMedicoExternoList(parametro);
		
		return executeCriteriaCount(criteria);
	}
	
	private DetachedCriteria makeCriteriaObterMedicoExternoList(String parametro) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghMedicoExterno.class);
		
		if (StringUtils.isNotBlank(parametro)) {
			criteria.add(Restrictions.or(Restrictions.eq(AghMedicoExterno.Fields.CRM.toString(), parametro), 
					Restrictions.ilike(AghMedicoExterno.Fields.NOME.toString(), parametro, MatchMode.ANYWHERE)));
			}
		
		return criteria;
	}

	/**
	 * Obter o médico externo pelo nome (quando informado) e pelo crm
	 * 
	 * @param nome
	 * @param crm
	 * @return
	 */
	public AghMedicoExterno obterMedicoExternoPeloNomeECrm(String nome, String crm) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AghMedicoExterno.class);

		if (nome != null) {
			criteria.add(Restrictions.eq(
					AghMedicoExterno.Fields.NOME.toString(), nome));
		}
		criteria.add(Restrictions.eq(AghMedicoExterno.Fields.CRM.toString(),
				crm));

		List<AghMedicoExterno> list = executeCriteria(criteria);
		return list != null && !list.isEmpty() ? list.get(0) : null;
	}
	
	public boolean verificarExistenciaCRM(AghMedicoExterno medicoExterno){
		DetachedCriteria criteria = DetachedCriteria.forClass(AghMedicoExterno.class);
		
		criteria.add(Restrictions.eq(AghMedicoExterno.Fields.CRM.toString(),medicoExterno.getCrm()));
		
		if (medicoExterno.getSeq() != null){
			criteria.add(Restrictions.ne(AghMedicoExterno.Fields.SEQ.toString(),medicoExterno.getSeq()));
		}
		
		return executeCriteriaExists(criteria); 
	}

}
