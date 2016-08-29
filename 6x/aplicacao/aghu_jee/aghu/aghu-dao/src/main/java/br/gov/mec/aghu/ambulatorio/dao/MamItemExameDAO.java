package br.gov.mec.aghu.ambulatorio.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MamItemExame;
import br.gov.mec.aghu.core.commons.CoreUtil;


public class MamItemExameDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MamItemExame> {

	private static final long serialVersionUID = 5324613220072804847L;

	public List<MamItemExame> pesquisarItemExamePorDescricaoOuSeq(Object param,
			String ordem, Integer maxResults) {
		
		DetachedCriteria criteria = obterCriteria(param);
		return executeCriteria(criteria, 0, maxResults, ordem, true);
	}
	
	public Long pesquisarItemExamePorDescricaoOuSeq(Object param) {
		DetachedCriteria criteria = obterCriteria(param);
		return executeCriteriaCount(criteria);
	}
	
	private DetachedCriteria obterCriteria(Object param) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamItemExame.class);
		
		if (CoreUtil.isNumeroInteger(param)){
			criteria.add(Restrictions.eq(MamItemExame.Fields.SEQ.toString(), Integer.valueOf(param.toString())));
		} else if (!param.toString().equals("")){
			criteria.add(Restrictions.ilike(MamItemExame.Fields.DESCRICAO.toString(), param.toString(), MatchMode.ANYWHERE));
		}
		return criteria;
	}
	
	/**
	 * Monta criteria para consulta de ativos por seq ou descrição
	 * 
	 * C3 de #32658
	 * 
	 * @param param
	 * @return
	 */
	private DetachedCriteria montarCriteriaAtivosPorSeqOuDescricao(String param) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamItemExame.class);
		if (StringUtils.isNotBlank(param)) {
			if (CoreUtil.isNumeroInteger(param)) {
				criteria.add(Restrictions.eq(MamItemExame.Fields.SEQ.toString(), Integer.valueOf(param)));
			} else {
				criteria.add(Restrictions.ilike(MamItemExame.Fields.DESCRICAO.toString(), param, MatchMode.ANYWHERE));
			}
		}
		criteria.add(Restrictions.eq(MamItemExame.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		return criteria;
	}

	/**
	 * Lista ativos por seq ou descrição
	 * 
	 * C3 de #32658
	 * 
	 * @param param
	 * @param maxResults
	 * @return
	 */
	public List<MamItemExame> pesquisarAtivosPorSeqOuDescricao(String param, Integer maxResults) {
		DetachedCriteria criteria = this.montarCriteriaAtivosPorSeqOuDescricao(param);
		criteria.addOrder(Order.asc(MamItemExame.Fields.DESCRICAO.toString()));
		if (maxResults != null) {
			return super.executeCriteria(criteria, 0, maxResults, null, true);
		}
		return super.executeCriteria(criteria);
	}

	/**
	 * Lista ativos por seq ou descrição
	 * 
	 * C3 de #32658
	 * 
	 * @param param
	 * @return
	 */
	public List<MamItemExame> pesquisarAtivosPorSeqOuDescricao(String param) {
		return this.pesquisarAtivosPorSeqOuDescricao(param, null);
	}

	/**
	 * Conta ativos por seq ou descrição
	 * 
	 * C3 de #32658
	 * 
	 * @param param
	 * @return
	 */
	public Long pesquisarAtivosPorSeqOuDescricaoCount(String param) {
		DetachedCriteria criteria = this.montarCriteriaAtivosPorSeqOuDescricao(param);
		return super.executeCriteriaCount(criteria);
	}
}
