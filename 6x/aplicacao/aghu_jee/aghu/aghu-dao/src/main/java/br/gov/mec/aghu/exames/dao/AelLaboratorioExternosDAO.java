package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.model.AelLaboratorioExternos;

public class AelLaboratorioExternosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelLaboratorioExternos> {

	private static final long serialVersionUID = -2482300470517982654L;
	
	public Long countLaboratorioHemocentro(final AelLaboratorioExternos filtros) {
		return executeCriteriaCount(mountCriteria(filtros));
	}
	
	public List<AelLaboratorioExternos> pesquisaLaboratorioHemocentroPaginado(final AelLaboratorioExternos filtros, 
			Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		
		final DetachedCriteria criteria = mountCriteria(filtros);
		criteria.createAlias(AelLaboratorioExternos.Fields.CONVENIO_SAUDE_PLANO.toString(), "CSP", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AelLaboratorioExternos.Fields.CONVENIO_SAUDE.toString(), "CS", JoinType.LEFT_OUTER_JOIN);
		
		if(orderProperty == null){
			orderProperty = AelLaboratorioExternos.Fields.NOME.toString();
		}
		
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}
		
	
	private DetachedCriteria mountCriteria(final AelLaboratorioExternos filtros) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(AelLaboratorioExternos.class);
		
		if(filtros != null){
			if(StringUtils.isNotEmpty(filtros.getNome())){
				criteria.add(Restrictions.ilike(AelLaboratorioExternos.Fields.NOME.toString(), filtros.getNome(), MatchMode.ANYWHERE));
			}
			
			if(StringUtils.isNotEmpty(filtros.getEndereco())){
				criteria.add(Restrictions.ilike(AelLaboratorioExternos.Fields.ENDERECO.toString(), filtros.getEndereco(), MatchMode.ANYWHERE));
			}
			
			if(StringUtils.isNotEmpty(filtros.getCidade())){
				criteria.add(Restrictions.ilike(AelLaboratorioExternos.Fields.CIDADE.toString(), filtros.getCidade(), MatchMode.ANYWHERE));
			}
			
			if(filtros.getConvenio() != null){
				criteria.add(Restrictions.eq(AelLaboratorioExternos.Fields.CONVENIO_SAUDE_PLANO.toString(), filtros.getConvenio()));
			}
			
			if(StringUtils.isNotEmpty(filtros.getCidade())){
				criteria.add(Restrictions.ilike(AelLaboratorioExternos.Fields.CIDADE.toString(), filtros.getCidade(), MatchMode.ANYWHERE));
			}
		}
		
		return criteria;
	}
	
	/**
	 * Obtem uma lista de laboratorio hemocentro<br>
	 * a partir de uma consulta na suggestion.
	 * 
	 * @param parametro
	 * @return
	 */
	public List<AelLaboratorioExternos> obterLaboratorioExternoList(String parametro) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelLaboratorioExternos.class);
		
		if (StringUtils.isNotBlank(parametro)) {
			if(CoreUtil.isNumeroInteger(parametro)) {
				criteria.add(Restrictions.eq(AelLaboratorioExternos.Fields.SEQ.toString(), Integer.valueOf(parametro)));
			} else {
				criteria.add(Restrictions.ilike(AelLaboratorioExternos.Fields.NOME.toString(), parametro, MatchMode.ANYWHERE));
			}
		}	
		criteria.addOrder(Order.asc(AelLaboratorioExternos.Fields.NOME.toString()));
		
		return executeCriteria(criteria);
	}
	
	public AelLaboratorioExternos obterLaboratorioExternoPorSeqComConvenioSaude(Integer laeSeq) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AelLaboratorioExternos.class);
		criteria.createAlias(AelLaboratorioExternos.Fields.CONVENIO_SAUDE.toString(), AelLaboratorioExternos.Fields.CONVENIO_SAUDE.toString());
		criteria.add(Restrictions.eq(AelLaboratorioExternos.Fields.SEQ.toString(), laeSeq));
		Object obj = executeCriteriaUniqueResult(criteria);
		if (obj == null) {
			return null;
		}
		else {
			return (AelLaboratorioExternos) obj;
		}
	}

	public Long obterLaboratorioExternoListCount(String parametro) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AelLaboratorioExternos.class);

		if (StringUtils.isNotBlank(parametro)) {
			if (CoreUtil.isNumeroInteger(parametro)) {
				criteria.add(Restrictions.eq(
						AelLaboratorioExternos.Fields.SEQ.toString(),
						Integer.valueOf(parametro)));
			} else {
				criteria.add(Restrictions.ilike(
						AelLaboratorioExternos.Fields.NOME.toString(),
						parametro, MatchMode.ANYWHERE));
			}
		}

		return executeCriteriaCount(criteria);
	}
}
