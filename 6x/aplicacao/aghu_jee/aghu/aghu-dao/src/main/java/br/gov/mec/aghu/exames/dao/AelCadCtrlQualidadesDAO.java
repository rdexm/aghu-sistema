package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.model.AelCadCtrlQualidades;


public class AelCadCtrlQualidadesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelCadCtrlQualidades> {

	
	private static final long serialVersionUID = -7375455679960179135L;

	public AelCadCtrlQualidades obterCadCtrlQualidadesPorSeqComConvenioSaude(Integer ccqSeq) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AelCadCtrlQualidades.class);
		criteria.createAlias(AelCadCtrlQualidades.Fields.CONVENIO_SAUDE.toString(), AelCadCtrlQualidades.Fields.CONVENIO_SAUDE.toString());
		criteria.add(Restrictions.eq(AelCadCtrlQualidades.Fields.SEQ.toString(), ccqSeq));
		Object obj = executeCriteriaUniqueResult(criteria);
		if (obj == null) {
			return null;
		}
		else {
			return (AelCadCtrlQualidades) obj;
		}
	}
	
	public List<AelCadCtrlQualidades> obterCadCtrlQualidadesList(String parametro) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelCadCtrlQualidades.class);
		
		if (StringUtils.isNotBlank(parametro)) {
			if(CoreUtil.isNumeroInteger(parametro)) {
				criteria.add(Restrictions.eq(AelCadCtrlQualidades.Fields.SEQ.toString(), Integer.valueOf(parametro)));
			} else {
				criteria.add(Restrictions.ilike(AelCadCtrlQualidades.Fields.MATERIAL.toString(), parametro, MatchMode.ANYWHERE));
			}
		}
		criteria.addOrder(Order.asc(AelCadCtrlQualidades.Fields.MATERIAL.toString()));
		
		return executeCriteria(criteria);
	}
	
	private DetachedCriteria obterCriteria(final AelCadCtrlQualidades filtros){
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelCadCtrlQualidades.class);
		criteria.createAlias(AelCadCtrlQualidades.Fields.CONVENIO_SAUDE.toString(), "CS", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AelCadCtrlQualidades.Fields.CONVENIO_SAUDE_PLANO.toString(), "CSP", JoinType.LEFT_OUTER_JOIN);
		
		if(filtros != null){
			if(filtros.getSeq() != null){
				criteria.add(Restrictions.eq(AelCadCtrlQualidades.Fields.SEQ.toString(), filtros.getSeq()));
			}
			
			if (StringUtils.isNotBlank(filtros.getMaterial())) {
				criteria.add(Restrictions.ilike(AelCadCtrlQualidades.Fields.MATERIAL.toString(), filtros.getMaterial(), MatchMode.ANYWHERE));
			}
			
			if(filtros.getConvenioSaudePlano() != null){
				criteria.add(Restrictions.eq(AelCadCtrlQualidades.Fields.CONVENIO_SAUDE_PLANO.toString(), filtros.getConvenioSaudePlano()));
			}
		}
		
		return criteria;
	}

	public Long obterCadCtrlQualidadesListCount(final AelCadCtrlQualidades filtros) {
		return executeCriteriaCount(obterCriteria(filtros));
	}

	public AelCadCtrlQualidades obterAelCadCtrlQualidadesPorId(Integer seq){

		final DetachedCriteria criteria = DetachedCriteria.forClass(AelCadCtrlQualidades.class);
		criteria.createAlias(AelCadCtrlQualidades.Fields.CONVENIO_SAUDE.toString(), "CS", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AelCadCtrlQualidades.Fields.CONVENIO_SAUDE_PLANO.toString(), "CSP", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AelCadCtrlQualidades.Fields.SERVIDOR.toString(), "SERV", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq(AelCadCtrlQualidades.Fields.SEQ.toString(), seq));
		
		return (AelCadCtrlQualidades) executeCriteriaUniqueResult(criteria);
	}
	
	public List<AelCadCtrlQualidades> obterCadCtrlQualidadesList(final AelCadCtrlQualidades filtros,
			final Integer firstResult, final Integer maxResults, String orderProperty, boolean asc) {
		
		final DetachedCriteria criteria = obterCriteria(filtros);
		
		if(!StringUtils.isNotEmpty(orderProperty)){
			orderProperty = AelCadCtrlQualidades.Fields.MATERIAL.toString();
			asc = true;
		}
		
		return executeCriteria(criteria, firstResult, maxResults, orderProperty, asc);
	}

	public Long obterCadCtrlQualidadesListCount(String parametro) {
			DetachedCriteria criteria = DetachedCriteria.forClass(AelCadCtrlQualidades.class);
			
			if (StringUtils.isNotBlank(parametro)) {
				if(CoreUtil.isNumeroInteger(parametro)) {
					criteria.add(Restrictions.eq(AelCadCtrlQualidades.Fields.SEQ.toString(), Integer.valueOf(parametro)));
				} else {
					criteria.add(Restrictions.ilike(AelCadCtrlQualidades.Fields.MATERIAL.toString(), parametro, MatchMode.ANYWHERE));
				}
			}
			
			return executeCriteriaCount(criteria);
	}
}
