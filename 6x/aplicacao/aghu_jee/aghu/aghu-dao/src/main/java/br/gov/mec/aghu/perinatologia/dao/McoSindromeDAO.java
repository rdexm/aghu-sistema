package br.gov.mec.aghu.perinatologia.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.McoSindrome;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class McoSindromeDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<McoSindrome> {

	private static final long serialVersionUID = -7411328006986170095L;
	
	/**
	 * #27482 - C8
	 * @param gsoPacCodigo
	 * @param gsoSeqp
	 * @param seqp
	 * @return
	 */
	public List<McoSindrome> listarMcoSindromeAtiva(Object param) {
		DetachedCriteria criteria = mcoSidromeAtivaCriteria(param);
		return executeCriteria(criteria, 0, 100, McoSindrome.Fields.DESCRICAO.toString(), true);
	}

	private DetachedCriteria mcoSidromeAtivaCriteria(Object param) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoSindrome.class);
		String strPesquisa = (String) param;
		if (CoreUtil.isNumeroShort(strPesquisa)) {
			criteria.add(Restrictions.eq(McoSindrome.Fields.SEQ.toString(), Integer.valueOf(strPesquisa)));
		} else if (StringUtils.isNotBlank(strPesquisa)) {
			criteria.add(Restrictions.ilike(McoSindrome.Fields.DESCRICAO.toString(),  String.valueOf(strPesquisa), MatchMode.ANYWHERE));
		}
		criteria.add(Restrictions.eq(McoSindrome.Fields.IND_SITUACAO.toString(), "A"));
		
		return criteria;
	}
	
	
	public Long listaMcoSindromeAtivaCount(Object param) {
		DetachedCriteria criteria = mcoSidromeAtivaCriteria(param);
		return executeCriteriaCount(criteria);
	}
	
	public List<McoSindrome> listarSindrome(Integer seq, String descricao, String situacao, Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoSindrome.class);
		
		if(StringUtils.isNotBlank(situacao)){
			criteria.add(Restrictions.eq(McoSindrome.Fields.IND_SITUACAO.toString(), situacao));
		}
		if (seq != null) {
			criteria.add(Restrictions.eq(McoSindrome.Fields.SEQ.toString(), seq));
		} 
		if (StringUtils.isNotBlank(descricao)) {
			criteria.add(Restrictions.ilike(McoSindrome.Fields.DESCRICAO.toString(), descricao, MatchMode.ANYWHERE));
		}
		criteria.addOrder(Order.desc(McoSindrome.Fields.DESCRICAO.toString()));
		
		return  executeCriteria(criteria,firstResult,maxResult,orderProperty,asc);
	}
	
	public Long listarSindromeCount(Integer seq, String descricao, String situacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoSindrome.class);
		
		if(StringUtils.isNotBlank(situacao)){
			criteria.add(Restrictions.eq(McoSindrome.Fields.IND_SITUACAO.toString(), situacao));
		}
		if (seq != null) {
			criteria.add(Restrictions.eq(McoSindrome.Fields.SEQ.toString(), seq));
		} 
		if (StringUtils.isNotBlank(descricao)) {
			criteria.add(Restrictions.ilike(McoSindrome.Fields.DESCRICAO.toString(), descricao, MatchMode.ANYWHERE));
		}
		//criteria.addOrder(Order.desc(McoSindrome.Fields.DESCRICAO.toString()));
		
		return  executeCriteriaCount(criteria);
	}

	public McoSindrome obterSindromePorDescricao(String descricao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoSindrome.class);
		

		if (StringUtils.isNotBlank(descricao)) {
			criteria.add(Restrictions.eq(McoSindrome.Fields.DESCRICAO.toString(), descricao));
		}
		//criteria.addOrder(Order.desc(McoSindrome.Fields.DESCRICAO.toString()));
		
		return  (McoSindrome) executeCriteriaUniqueResult(criteria);
	}


}