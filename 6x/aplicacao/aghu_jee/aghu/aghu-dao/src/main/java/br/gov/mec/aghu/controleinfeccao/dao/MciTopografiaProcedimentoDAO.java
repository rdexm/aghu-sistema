package br.gov.mec.aghu.controleinfeccao.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.controleinfeccao.vo.TopografiaProcedimentoVO;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MciTopografiaInfeccao;
import br.gov.mec.aghu.model.MciTopografiaProcedimento;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;
@SuppressWarnings({"PMD.AvoidDuplicateLiterals","PMD.ConsecutiveLiteralAppends","PMD.ConsecutiveAppendsShouldReuse"})
public class MciTopografiaProcedimentoDAO extends BaseDao<MciTopografiaProcedimento> {

	private static final long serialVersionUID = -1918544001527282883L;

	public List<MciTopografiaProcedimento> listarPorToiSeq(Short id) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MciTopografiaProcedimento.class);
		criteria.add(Restrictions.eq(MciTopografiaProcedimento.Fields.TOPOGRAFIA_INFECCAO_SEQ.toString(), id));
		return executeCriteria(criteria);
	}
	
	public List<TopografiaProcedimentoVO> listarMciTopografiaProcedimentoPorSeqDescSitSeqTop(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, Short seq, String descricao, DominioSituacao situacao, Short toiSeq) {
		
		MciTopografiaProcedimentoPorSeqDescSitSeqTopQueryBuilder queryBuilder = new MciTopografiaProcedimentoPorSeqDescSitSeqTopQueryBuilder();
		return executeCriteria(queryBuilder.build(Boolean.FALSE, seq, descricao, situacao, toiSeq), firstResult, maxResult, orderProperty, asc);
	}

	public Long listarMciTopografiaProcedimentoPorSeqDescSitSeqTopCount(
			Short seq, String descricao, DominioSituacao situacao, Short toiSeq) {
		MciTopografiaProcedimentoPorSeqDescSitSeqTopQueryBuilder queryBuilder = new MciTopografiaProcedimentoPorSeqDescSitSeqTopQueryBuilder();
		return executeCriteriaCount(queryBuilder.build(Boolean.TRUE, seq, descricao, situacao, toiSeq));
	}
	
	public List<TopografiaProcedimentoVO> suggestionBoxTopografiaProcedimentoPorSeqOuDescricaoSituacao(String strPesquisa) {
		MciTopografiaProcedimentoPorSeqDescSitSeqTopQueryBuilder queryBuilder = new MciTopografiaProcedimentoPorSeqDescSitSeqTopQueryBuilder();
		return executeCriteria(queryBuilder.build(Boolean.FALSE, null, strPesquisa, DominioSituacao.A, null), 0, 100, MciTopografiaProcedimento.Fields.DESCRICAO.toString(), true);
	}
	
	//#1297 - C3
	public List<TopografiaProcedimentoVO> listarTopografiasAtivas(String param) {		
		DetachedCriteria criteria = montarCriteriaTopografiasAtivas(param);
		criteria.addOrder(Order.asc("TOI." + MciTopografiaInfeccao.Fields.DESCRICAO.toString()));
		criteria.addOrder(Order.asc("TOP." + MciTopografiaProcedimento.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria, 0, 100, null, true);
	}
	
	//#1297 - C3
	public Long listarTopografiasAtivasCount(String param) {		
		DetachedCriteria criteria = montarCriteriaTopografiasAtivas(param);
		return executeCriteriaCount(criteria);
	}
	
	public DetachedCriteria montarCriteriaTopografiasAtivas(String param) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MciTopografiaInfeccao.class, "TOI");
		criteria.createAlias("TOI." + MciTopografiaInfeccao.Fields.PATOLOGIA_INFECCOES.toString(), "PAI", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("TOI." + MciTopografiaInfeccao.Fields.TOPOGRAFIAS_PROCEDIMENTO.toString(), "TOP", JoinType.LEFT_OUTER_JOIN);

		criteria.add(Restrictions.eq("TOP." + MciTopografiaProcedimento.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		if(StringUtils.isNotBlank(param)) {
			if(CoreUtil.isNumeroInteger(param)){
			Integer seq = Integer.getInteger(param);
			Criterion c1 = Restrictions.eq("TOI." + MciTopografiaInfeccao.Fields.SEQ.toString(), seq);
			Criterion c2 = Restrictions.eq("TOP." + MciTopografiaProcedimento.Fields.SEQ.toString(), seq);
			criteria.add(Restrictions.or(c1, c2));
			} else {
			Criterion c1 =  Restrictions.ilike("TOI." + MciTopografiaInfeccao.Fields.DESCRICAO.toString(), param, MatchMode.ANYWHERE);
			Criterion c2 =  Restrictions.ilike("TOP." + MciTopografiaProcedimento.Fields.DESCRICAO.toString(), param, MatchMode.ANYWHERE);
			criteria.add(Restrictions.or(c1, c2));
			}
		}
		
		ProjectionList projection =	Projections.projectionList()
				.add(Projections.property("TOI." + MciTopografiaInfeccao.Fields.SEQ.toString()), TopografiaProcedimentoVO.Fields.SEQ_TOPOGRAFIA_INFECCAO.toString())
				.add(Projections.property("TOI." + MciTopografiaInfeccao.Fields.DESCRICAO.toString()), TopografiaProcedimentoVO.Fields.DESCRICAO_TOPOGRA_FIAINFECCAO.toString())
				.add(Projections.property("TOP." + MciTopografiaProcedimento.Fields.SEQ.toString()), TopografiaProcedimentoVO.Fields.SEQ.toString())
				.add(Projections.property("TOP." + MciTopografiaProcedimento.Fields.DESCRICAO.toString()), TopografiaProcedimentoVO.Fields.DESCRICAO.toString());
		criteria.setProjection(projection);
		
		criteria.setResultTransformer(Transformers.aliasToBean(TopografiaProcedimentoVO.class));
		return criteria;
	}
	
	public List<TopografiaProcedimentoVO> listarTopografiaProcedimentoAtivas(String param){
		DetachedCriteria criteria = montarCriteriaTopografiasAtivas(param);
		criteria.addOrder(Order.asc("TOP." + MciTopografiaProcedimento.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria, 0, 100, null, true);
	}
	
	public Long listarTopografiaProcedimentoAtivasCount(Object param){
		return executeCriteriaCount(montarCriteriaTopografiasAtivas(param.toString()));
	}
}
