package br.gov.mec.aghu.controleinfeccao.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.controleinfeccao.vo.CriteriosBacteriaAntimicrobianoVO;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MciAntimicrobianos;
import br.gov.mec.aghu.model.MciCriterioGmr;
import br.gov.mec.aghu.model.MciCriterioGmrId;


public class MciCriterioGmrDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MciCriterioGmr>{

	private static final long serialVersionUID = -1081110401245709754L;

	public List<MciCriterioGmr> pesquisarCriterioGrmPorAmbSeq(Integer ambSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MciCriterioGmr.class);
		
		criteria.add(Restrictions.eq(MciCriterioGmr.Fields.AMB_SEQ.toString(), ambSeq));
		
		return executeCriteria(criteria);	
	}
	
	public List<CriteriosBacteriaAntimicrobianoVO> pesquisarCriterioGrmPorBmrSeq(Integer bmrSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MciCriterioGmr.class, "crt");
		criteria.createAlias("crt." + MciCriterioGmr.Fields.ANTIMICROBIANOS.toString(), "amb", JoinType.INNER_JOIN);
			
		criteria.add(Restrictions.eq(MciCriterioGmr.Fields.BMR_SEQ.toString(), bmrSeq));
		
		criteria.setProjection(Projections.projectionList().add(
				Projections.property("crt." + MciCriterioGmr.Fields.AMB_SEQ.toString()), CriteriosBacteriaAntimicrobianoVO.Fields.AMB_SEQ.toString()).add(
				Projections.property("crt." + MciCriterioGmr.Fields.BMR_SEQ.toString()), CriteriosBacteriaAntimicrobianoVO.Fields.BMR_SEQ.toString()).add(
				Projections.property("crt." + MciCriterioGmr.Fields.SITUACAO.toString()), CriteriosBacteriaAntimicrobianoVO.Fields.SITUACAO.toString()).add(
				Projections.property("amb." + MciAntimicrobianos.Fields.DESCRICAO.toString()), CriteriosBacteriaAntimicrobianoVO.Fields.DESCRICAO.toString()));
		
		criteria.addOrder(Order.asc("amb." + MciAntimicrobianos.Fields.DESCRICAO.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(CriteriosBacteriaAntimicrobianoVO.class));
		
		return executeCriteria(criteria);	
	}
	
	public MciCriterioGmr obterCriterioGmrPeloId(MciCriterioGmrId criterioId) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MciCriterioGmr.class);
		criteria.add(Restrictions.eq(MciCriterioGmr.Fields.AMB_SEQ.toString(), criterioId.getAmbSeq()));
		criteria.add(Restrictions.eq(MciCriterioGmr.Fields.BMR_SEQ.toString(), criterioId.getBmrSeq()));
		return (MciCriterioGmr) executeCriteriaUniqueResult(criteria);
	}
	
	public List<MciCriterioGmr> pesquisarMciCriterioGrmPorBmrSeq(Integer brmSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MciCriterioGmr.class);
		
		criteria.add(Restrictions.eq(MciCriterioGmr.Fields.BMR_SEQ.toString(), brmSeq));
		
		return executeCriteria(criteria);	
	}
	
	public List<MciCriterioGmr> pesquisarMciCriterioGrmAtivoPorBmrSeq(Integer brmSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MciCriterioGmr.class);
		criteria.createAlias(MciCriterioGmr.Fields.ANTIMICROBIANOS.toString(), "AMB", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq(MciCriterioGmr.Fields.BMR_SEQ.toString(), brmSeq));
		criteria.add(Restrictions.eq(MciCriterioGmr.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.addOrder(Order.asc("AMB." + MciAntimicrobianos.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria);
	}
	
	
}