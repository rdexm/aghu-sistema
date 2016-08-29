package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MpmAlergiaUsual;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class MpmAlergiaUsualDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmAlergiaUsual> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1874779695334957847L;

	private DetachedCriteria obterDetachedCriteriaPesquisarTipoAlergiaUsual(Integer codigo, String descricao, DominioSituacao situacao){
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAlergiaUsual.class);
		
		if(codigo != null){
			criteria.add(Restrictions.eq(MpmAlergiaUsual.Fields.SEQ.toString(), codigo));
		}
		
		if(descricao != null){
			criteria.add(Restrictions.like(MpmAlergiaUsual.Fields.DESCRICAO.toString(), descricao, MatchMode.ANYWHERE));
		}
		
		if(situacao != null){
			criteria.add(Restrictions.eq(MpmAlergiaUsual.Fields.IND_SITUACAO.toString(), situacao.toString()));
		}
		criteria.addOrder(Order.asc(MpmAlergiaUsual.Fields.SEQ.toString()));
		return criteria;
	}
	
	public List<MpmAlergiaUsual> pesquisarAlergiaUsual(	Integer codigo, String descricao, DominioSituacao situacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAlergiaUsual.class);
		criteria = obterDetachedCriteriaPesquisarTipoAlergiaUsual(codigo, descricao, situacao);
		return executeCriteria(criteria);
	}
	
	public List<MpmAlergiaUsual> obterMpmAlergiasUsual(String parametro){
		final DetachedCriteria criteria = montarCriteriaParaPesquisarPorDescricao(parametro, false);
		return executeCriteria(criteria);
	}
	
	public Long obterMpmAlergiasUsualCount(String parametro){
		final DetachedCriteria criteria = montarCriteriaParaPesquisarPorDescricao(parametro, true);
		return executeCriteriaCount(criteria);
	}
	
	private DetachedCriteria montarCriteriaParaPesquisarPorDescricao(String parametro, boolean count) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MpmAlergiaUsual.class);
		
		criteria.add(Restrictions.eq(MpmAlergiaUsual.Fields.IND_SITUACAO.toString(), DominioSituacao.A.toString()));
		
		if(CoreUtil.isNumeroInteger(parametro)){
			criteria.add(Restrictions.or(Restrictions.eq(MpmAlergiaUsual.Fields.SEQ.toString(), Integer.valueOf(parametro)),
					Restrictions.ilike(MpmAlergiaUsual.Fields.DESCRICAO.toString(), parametro, MatchMode.ANYWHERE)));
		}else if(StringUtils.isNotEmpty(parametro)){
			criteria.add(Restrictions.ilike(MpmAlergiaUsual.Fields.DESCRICAO.toString(), parametro, MatchMode.ANYWHERE));
		}

		if (!count){
			criteria.addOrder(Order.asc(MpmAlergiaUsual.Fields.DESCRICAO.toString()));			
		}
		return criteria;
	}
	
	public MpmAlergiaUsual obterMpmAlergiaUsualPorSeq(Integer seq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAlergiaUsual.class);
		criteria.add(Restrictions.eq(MpmAlergiaUsual.Fields.SEQ.toString(), seq));
		return (MpmAlergiaUsual) executeCriteriaUniqueResult(criteria);
	}
	
}
