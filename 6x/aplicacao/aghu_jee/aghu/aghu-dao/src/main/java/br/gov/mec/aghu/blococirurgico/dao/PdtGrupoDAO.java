package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.PdtGrupo;

public class PdtGrupoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<PdtGrupo> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4143055022907072177L;

	public List<PdtGrupo> pesquisarPdtGrupoPorIdDescricaoSituacao(Integer dptSeq, Short seqp, String descricao, DominioSituacao indSituacao,
			Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		
		DetachedCriteria criteria = this.montarCriteriaParaPesquisarPdtGrupoPorIdDescricaoSituacao(dptSeq, seqp, descricao, indSituacao);
		criteria.addOrder(Order.asc(PdtGrupo.Fields.ID_DPT_SEQ.toString()));
		criteria.addOrder(Order.asc(PdtGrupo.Fields.ID_SEQP.toString()));
		criteria.addOrder(Order.asc(PdtGrupo.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);	
	}

	private DetachedCriteria montarCriteriaParaPesquisarPdtGrupoPorIdDescricaoSituacao(Integer dptSeq, Short seqp, String descricao,
			DominioSituacao indSituacao) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(PdtGrupo.class);
		
		if(dptSeq != null) {
			criteria.add(Restrictions.eq(PdtGrupo.Fields.ID_DPT_SEQ.toString(), dptSeq));
		}	
		
		if(seqp != null) {
			criteria.add(Restrictions.eq(PdtGrupo.Fields.ID_SEQP.toString(), seqp));
		}	
		
		if(StringUtils.isNotBlank(descricao)) {
			criteria.add(Restrictions.ilike(PdtGrupo.Fields.DESCRICAO.toString(), descricao, MatchMode.ANYWHERE));
		}		
			
		if(indSituacao != null) {
			criteria.add(Restrictions.eq(PdtGrupo.Fields.IND_SITUACAO.toString(), indSituacao));
		}	
		return criteria;
	}

	public Long pesquisarPdtGrupoPorIdDescricaoSituacaoCount(Integer dptSeq, Short seqp, String descricao, DominioSituacao indSituacao) {
		DetachedCriteria criteria = this.montarCriteriaParaPesquisarPdtGrupoPorIdDescricaoSituacao(dptSeq, seqp, descricao, indSituacao);
		return executeCriteriaCount(criteria);	
	}

	public PdtGrupo obterPdtGrupoPorDescricao(String descricao, Integer dptSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PdtGrupo.class);
		criteria.add(Restrictions.eq(PdtGrupo.Fields.DESCRICAO.toString(), descricao));
		criteria.add(Restrictions.eq(PdtGrupo.Fields.ID_DPT_SEQ.toString(), dptSeq));
		return (PdtGrupo)executeCriteriaUniqueResult(criteria);		
	}

	public Short nextSeqpPdtGrupo(Integer dptSeq) {
		Short maxSeqp = this.obterSeqpMax(dptSeq);
		
		if (maxSeqp != null) {
			maxSeqp++;
		}else{
			maxSeqp = Short.valueOf("1");
		}
		return maxSeqp;
	}

	private Short obterSeqpMax(Integer dptSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PdtGrupo.class);
		
		criteria.setProjection(Projections.max(PdtGrupo.Fields.ID_SEQP.toString()));  
		criteria.add(Restrictions.eq(PdtGrupo.Fields.ID_DPT_SEQ.toString(), dptSeq));
		
		return (Short)this.executeCriteriaUniqueResult(criteria);
	}

}
