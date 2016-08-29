package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AelSalasExecutorasExames;
import br.gov.mec.aghu.model.AelSalasExecutorasExamesId;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class AelSalasExecutorasExamesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelSalasExecutorasExames> {
	
	private static final long serialVersionUID = -6556810549123166493L;

	/**
	 * .
	 * @return
	 */
	private DetachedCriteria obterCriteria() {
		return DetachedCriteria.forClass(AelSalasExecutorasExames.class);
    }
	
	/**
	 * .
	 * @param salasExecutorasExamesId
	 * @return
	 */
	
	public AelSalasExecutorasExames obterPorId(AelSalasExecutorasExamesId salasExecutorasExamesId) {
		DetachedCriteria criteria = obterCriteria();
		criteria.add(Restrictions.eq(AelSalasExecutorasExames.Fields.ID.toString(), salasExecutorasExamesId));
		criteria.createAlias(AelSalasExecutorasExames.Fields.UNIDADE_FUNCIONAL.toString(), "UNID", JoinType.INNER_JOIN);
		criteria.createAlias(AelSalasExecutorasExames.Fields.SERVIDOR.toString(), "SERV", JoinType.INNER_JOIN);
		
		return (AelSalasExecutorasExames) executeCriteriaUniqueResult(criteria);
	}

	public AelSalasExecutorasExames obterPorUnidadeFuncionalSeqpNumeroSala(AelSalasExecutorasExamesId salasExecutorasExamesId, String numero){
		DetachedCriteria criteria = obterCriteria();
		criteria.add(Restrictions.eq(AelSalasExecutorasExames.Fields.UNF_SEQ.toString(), salasExecutorasExamesId.getUnfSeq()));
		criteria.add(Restrictions.not(Restrictions.eq(AelSalasExecutorasExames.Fields.SEQP.toString(), salasExecutorasExamesId.getSeqp())));
		criteria.add(Restrictions.eq(AelSalasExecutorasExames.Fields.NUMERO.toString(), numero));
		return (AelSalasExecutorasExames) executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * 
	 * @param filtro
	 * @return
	 */
	public Long pesquisaSalasExecutorasExamesCount(AelSalasExecutorasExames filtro){
		return executeCriteriaCount(this.obterSalasExecutorasExames(filtro));
	}
	
	/**
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param filtro
	 * @return
	 */
	public List<AelSalasExecutorasExames> pesquisaSalasExecutorasExames(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, AelSalasExecutorasExames filtro){
		DetachedCriteria criteria = obterSalasExecutorasExames(filtro);
		criteria.createAlias(AelSalasExecutorasExames.Fields.UNIDADE_FUNCIONAL.toString(), "UNF", JoinType.INNER_JOIN);
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}
	
	
	public List<AelSalasExecutorasExames> pesquisarSalasExecutorasExamesPorNumeroEUnidade(String parametro, AghUnidadesFuncionais unidadeExecutora){
		if(unidadeExecutora!=null){
			DetachedCriteria criteria = DetachedCriteria.forClass(AelSalasExecutorasExames.class);
			
			criteria.add(Restrictions.eq(AelSalasExecutorasExames.Fields.UNF_SEQ.toString(), unidadeExecutora.getSeq()));
			
			if (StringUtils.isNotBlank(parametro)) {
				criteria.add(Restrictions.ilike(AelSalasExecutorasExames.Fields.NUMERO.toString(), parametro, MatchMode.ANYWHERE));
			}
			
			criteria.addOrder(Order.asc(AelSalasExecutorasExames.Fields.NUMERO.toString()));			
			List<AelSalasExecutorasExames> listaSalas = executeCriteria(criteria);
			
			return listaSalas;			
		} else {
			return null;
		}
	}
	
	private DetachedCriteria obterCriteriaSalasExecutorasExamesPorSeqOuNumeroEUnidade(String parametro, AghUnidadesFuncionais unidadeExecutora){
		DetachedCriteria criteria = DetachedCriteria.forClass(AelSalasExecutorasExames.class);
		
		criteria.add(Restrictions.eq(AelSalasExecutorasExames.Fields.UNF_SEQ.toString(), unidadeExecutora.getSeq()));
		
		if (StringUtils.isNotBlank(parametro)) {
			Criterion criterionNumeroSeq = null;
			if (CoreUtil.isNumeroShort(parametro)) {
				short seqp = Short.parseShort(parametro);
				criterionNumeroSeq = Restrictions.eq(AelSalasExecutorasExames.Fields.SEQP.toString(), seqp);
			} else {
				criterionNumeroSeq = Restrictions.ilike(AelSalasExecutorasExames.Fields.NUMERO.toString(), parametro, MatchMode.ANYWHERE);
			}		
			criteria.add(criterionNumeroSeq);
		}
		
		criteria.addOrder(Order.asc(AelSalasExecutorasExames.Fields.SEQP.toString()));			

		return criteria;
	}
	
	
	public List<AelSalasExecutorasExames> pesquisarSalasExecutorasExamesPorSeqOuNumeroEUnidade(String parametro, AghUnidadesFuncionais unidadeExecutora){
		if(unidadeExecutora!=null){
			DetachedCriteria criteria = obterCriteriaSalasExecutorasExamesPorSeqOuNumeroEUnidade(parametro, unidadeExecutora);
			
			List<AelSalasExecutorasExames> listaSalas = executeCriteria(criteria);
			
			return listaSalas;			
		} else {
			return null;
		}
	}
		
	public List<AelSalasExecutorasExames> pesquisarSalasExecutorasExamesPorSeqOuNumeroEUnidadeAtivos(String parametro, AghUnidadesFuncionais unidadeExecutora){
		if(unidadeExecutora!=null){
			DetachedCriteria criteria = obterCriteriaSalasExecutorasExamesPorSeqOuNumeroEUnidade(parametro, unidadeExecutora);
			
			criteria.add(Restrictions.eq(AelSalasExecutorasExames.Fields.SITUACAO.toString(), DominioSituacao.A));

			return executeCriteria(criteria);
		} else {
			return null;
		}
	}
	
	private DetachedCriteria obterSalasExecutorasExames(AelSalasExecutorasExames filtro){
		DetachedCriteria criteria = DetachedCriteria.forClass(AelSalasExecutorasExames.class);
		if (filtro!=null){
			if (filtro.getCodigo()!=null){
				criteria.add(Restrictions.eq(AelSalasExecutorasExames.Fields.SEQP.toString(), filtro.getCodigo().shortValue()));
			}
			if (filtro.getUnidadeFuncional()!=null){
				criteria.add(Restrictions.eq(AelSalasExecutorasExames.Fields.UNF_SEQ.toString(), filtro.getUnidadeFuncional().getSeq()));
			}
			if (filtro.getNumero()!=null && !filtro.getNumero().isEmpty()){
				criteria.add(Restrictions.ilike(AelSalasExecutorasExames.Fields.NUMERO.toString(), filtro.getNumero(), MatchMode.ANYWHERE));
			}
			if (filtro.getLocalizacao()!=null && !filtro.getLocalizacao().isEmpty()){
				criteria.add(Restrictions.ilike(AelSalasExecutorasExames.Fields.LOCALIZACAO.toString(), filtro.getLocalizacao(),MatchMode.ANYWHERE));
			}
			if (filtro.getSituacao()!=null){
				criteria.add(Restrictions.eq(AelSalasExecutorasExames.Fields.SITUACAO.toString(), filtro.getSituacao()));
			}
			
		}
		return criteria;
	}
	
	public Short obterMaxSeqpSalaExecutoraPorUnidadeFuncional(Short unfSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelSalasExecutorasExames.class);
		
		criteria.add(Restrictions.eq(AelSalasExecutorasExames.Fields.UNF_SEQ.toString(), unfSeq));
		ProjectionList pList = Projections.projectionList();
		pList.add(Projections.max(AelSalasExecutorasExames.Fields.SEQP.toString()));
		criteria.setProjection(pList);

		return (Short) executeCriteriaUniqueResult(criteria);
	}
	
	public List<AelSalasExecutorasExames> obterSalaExecutoraExamesPorUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional, Object param) {
		DetachedCriteria criteria = obterCriteria();
		
		String strPesquisa = (String) param;
		
		if(StringUtils.isNotBlank(strPesquisa)) {
			criteria.add(Restrictions.ilike(AelSalasExecutorasExames.Fields.NUMERO.toString(), strPesquisa, MatchMode.ANYWHERE));
		}
		criteria.add(Restrictions.eq(AelSalasExecutorasExames.Fields.UNF_SEQ.toString(), unidadeFuncional.getSeq()));
		criteria.add(Restrictions.eq(AelSalasExecutorasExames.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		criteria.setFetchMode(AelSalasExecutorasExames.Fields.UNIDADE_FUNCIONAL.toString(), FetchMode.JOIN);
		
		criteria.addOrder(Order.asc(AelSalasExecutorasExames.Fields.SEQP.toString()));
		return executeCriteria(criteria);
	}	
}
