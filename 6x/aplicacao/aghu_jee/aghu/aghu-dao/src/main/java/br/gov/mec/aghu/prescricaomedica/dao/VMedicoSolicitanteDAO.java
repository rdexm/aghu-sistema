package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.model.VMedicoSolicitante;
import br.gov.mec.aghu.core.commons.CoreUtil;


public class VMedicoSolicitanteDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<VMedicoSolicitante> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 350757816053706992L;
	
	/** Obtém criteria para consulta de VMedicoSolicitante para Suggestion Box 06.
	 * #1291
	 * @param parametro {@link Object}
	 * @return {@link List<VMedicoSolicitante>} */
	public List<VMedicoSolicitante> pesquisarVMedicoSolicitanteSB6(Object strPesquisa) {
		DetachedCriteria criteria = DetachedCriteria.forClass(VMedicoSolicitante.class);
		
		if (strPesquisa != null && StringUtils.isNotBlank(strPesquisa.toString())) {
			if (CoreUtil.isNumeroInteger(strPesquisa)) {
				Criterion criterion1 = Restrictions.eq(VMedicoSolicitante.Fields.MATRICULAII.toString(), Integer.valueOf((String) strPesquisa));
				Criterion criterion2 = Restrictions.like(VMedicoSolicitante.Fields.NRO_REG_CONSELHO.toString(), strPesquisa.toString(), MatchMode.ANYWHERE);
				criteria.add(Restrictions.or(criterion1, criterion2));
				
				criteria.add(Restrictions.eq(VMedicoSolicitante.Fields.MATRICULAII.toString(), Integer.valueOf((String) strPesquisa)));
				if(executeCriteria(criteria) != null && executeCriteria(criteria).size() == 1){
					ProjectionList p = Projections.projectionList();
					p.add(Projections.property(VMedicoSolicitante.Fields.MATRICULAII.toString()), VMedicoSolicitante.Fields.MATRICULAII.toString());
					p.add(Projections.property(VMedicoSolicitante.Fields.NOMEII.toString()), VMedicoSolicitante.Fields.NOMEII.toString());
					p.add(Projections.property(VMedicoSolicitante.Fields.NRO_REG_CONSELHO.toString()), VMedicoSolicitante.Fields.NRO_REG_CONSELHO.toString());
					p.add(Projections.property(VMedicoSolicitante.Fields.VIN_CODIGO.toString()), VMedicoSolicitante.Fields.VIN_CODIGO.toString());
					criteria.setProjection(p);
					criteria.setResultTransformer(Transformers.aliasToBean(VMedicoSolicitante.class));
					return this.executeCriteria(criteria, 0, 100, VMedicoSolicitante.Fields.NOMEII.toString(), true);
				}else{
					criteria = null;
					criterion1 = null;
					criterion2 = null;
					criteria = DetachedCriteria.forClass(VMedicoSolicitante.class);
					
					criterion1 = Restrictions.like(VMedicoSolicitante.Fields.NOMEII.toString(), strPesquisa.toString().toUpperCase(), MatchMode.ANYWHERE);
					criterion2 = Restrictions.like(VMedicoSolicitante.Fields.NRO_REG_CONSELHO.toString(), strPesquisa.toString(), MatchMode.ANYWHERE);
					criteria.add(Restrictions.or(criterion1, criterion2));
					
//					criteria.add(Restrictions.ilike(VMedicoSolicitante.Fields.NOMEII.toString(), (String) strPesquisa, MatchMode.ANYWHERE));
				}
			}else{
				criteria = null;
				criteria = DetachedCriteria.forClass(VMedicoSolicitante.class);
				
				Criterion criterion1 = Restrictions.like(VMedicoSolicitante.Fields.NOMEII.toString(), strPesquisa.toString().toUpperCase(), MatchMode.ANYWHERE);
				Criterion criterion2 = Restrictions.like(VMedicoSolicitante.Fields.NRO_REG_CONSELHO.toString(), strPesquisa.toString(), MatchMode.ANYWHERE);
				criteria.add(Restrictions.or(criterion1, criterion2));
				
//				criteria.add(Restrictions.ilike(VMedicoSolicitante.Fields.NOMEII.toString(), (String) strPesquisa, MatchMode.ANYWHERE));
			}
		}
		
		ProjectionList p = Projections.projectionList();
		p.add(Projections.property(VMedicoSolicitante.Fields.MATRICULAII.toString()), VMedicoSolicitante.Fields.MATRICULAII.toString());
		p.add(Projections.property(VMedicoSolicitante.Fields.NOMEII.toString()), VMedicoSolicitante.Fields.NOMEII.toString());
		p.add(Projections.property(VMedicoSolicitante.Fields.NRO_REG_CONSELHO.toString()), VMedicoSolicitante.Fields.NRO_REG_CONSELHO.toString());
		p.add(Projections.property(VMedicoSolicitante.Fields.VIN_CODIGO.toString()), VMedicoSolicitante.Fields.VIN_CODIGO.toString());
		criteria.setProjection(p);
		
		criteria.setResultTransformer(Transformers.aliasToBean(VMedicoSolicitante.class));
		return this.executeCriteria(criteria, 0, 100, VMedicoSolicitante.Fields.NOMEII.toString(), true);
	}
	
	public Long pesquisarVMedicoSolicitanteSB6Count(Object strPesquisa) {
DetachedCriteria criteria = DetachedCriteria.forClass(VMedicoSolicitante.class);
		
		if (strPesquisa != null && StringUtils.isNotBlank(strPesquisa.toString())) {
			if (CoreUtil.isNumeroInteger(strPesquisa)) {
				criteria.add(Restrictions.eq(VMedicoSolicitante.Fields.MATRICULAII.toString(), Integer.valueOf((String) strPesquisa)));
				if(executeCriteria(criteria) != null && executeCriteria(criteria).size() == 1){
					return this.executeCriteriaCount(criteria);
				}else{
					criteria = null;
					criteria = DetachedCriteria.forClass(VMedicoSolicitante.class);
					
					Criterion criterion1 = Restrictions.like(VMedicoSolicitante.Fields.NOMEII.toString(), strPesquisa.toString().toUpperCase(), MatchMode.ANYWHERE);
					Criterion criterion2 = Restrictions.like(VMedicoSolicitante.Fields.NRO_REG_CONSELHO.toString(), strPesquisa.toString(), MatchMode.ANYWHERE);
					criteria.add(Restrictions.or(criterion1, criterion2));
					
//					criteria.add(Restrictions.ilike(VMedicoSolicitante.Fields.NOMEII.toString(), (String) strPesquisa, MatchMode.ANYWHERE));
				}
			}else{
				criteria = null;
				criteria = DetachedCriteria.forClass(VMedicoSolicitante.class);
				
				Criterion criterion1 = Restrictions.like(VMedicoSolicitante.Fields.NOMEII.toString(), strPesquisa.toString().toUpperCase(), MatchMode.ANYWHERE);
				Criterion criterion2 = Restrictions.like(VMedicoSolicitante.Fields.NRO_REG_CONSELHO.toString(), strPesquisa.toString(), MatchMode.ANYWHERE);
				criteria.add(Restrictions.or(criterion1, criterion2));
				
//				criteria.add(Restrictions.ilike(VMedicoSolicitante.Fields.NOMEII.toString(), (String) strPesquisa, MatchMode.ANYWHERE));
			}
		}
		
		return this.executeCriteriaCount(criteria);
	
	}

}
