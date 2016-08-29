package br.gov.mec.aghu.patrimonio.dao;

import br.gov.mec.aghu.model.PtmDescMotivoMovimentos;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

public class PtmDescMotivoMovimentosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<PtmDescMotivoMovimentos>{

	private static final long serialVersionUID = 9134202982572164069L;
	
	public List<PtmDescMotivoMovimentos> pesquisarDescricoesSituacaoMovimento(Integer situacaoSeq, Boolean ativo, Boolean obrigatorio, String descricao, 
																			  Integer firstResult, Integer maxResult, String orderProperty, boolean asc){
		
		DetachedCriteria criteria = obterCriteria(situacaoSeq, ativo, obrigatorio, descricao);
		criteria.createAlias("DMM."+PtmDescMotivoMovimentos.Fields.PTM_SITUACAO_MOTIVO_MOVIMENTO.toString(), "SMM", JoinType.INNER_JOIN);
		
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}
	
	public PtmDescMotivoMovimentos obterPtmDescMotivoMovimentos(PtmDescMotivoMovimentos ptmDescMotivoMovimentos){
		DetachedCriteria criteria = DetachedCriteria.forClass(PtmDescMotivoMovimentos.class, "DMM");
		criteria.createAlias("DMM."+PtmDescMotivoMovimentos.Fields.PTM_SITUACAO_MOTIVO_MOVIMENTO.toString(), "SMM", JoinType.INNER_JOIN);
		
		criteria.add(Restrictions.eq(PtmDescMotivoMovimentos.Fields.SEQ.toString(), ptmDescMotivoMovimentos.getSeq()));
		
		return (PtmDescMotivoMovimentos) executeCriteriaUniqueResult(criteria);
	}
	
	public Long pesquisarDescricoesSituacaoMovimentoCount(Integer situacaoSeq, Boolean ativo, Boolean obrigatorio, String descricao){
		DetachedCriteria criteria = obterCriteria(situacaoSeq, ativo, obrigatorio, descricao);
		return executeCriteriaCount(criteria);
	} 
	
	private DetachedCriteria obterCriteria(Integer situacaoSeq, Boolean ativo, Boolean obrigatorio, String descricao){
		DetachedCriteria criteria = DetachedCriteria.forClass(PtmDescMotivoMovimentos.class, "DMM");
		
		if (situacaoSeq != null) {
			criteria.add(Restrictions.eq(PtmDescMotivoMovimentos.Fields.PTM_SITUACAO_MOTIVO_MOVIMENTO_SEQ.toString(), situacaoSeq));
		}
		
		if(ativo != null){
			criteria.add(Restrictions.eq(PtmDescMotivoMovimentos.Fields.ATIVO.toString(), ativo));
		}
		
		if(obrigatorio != null){
			criteria.add(Restrictions.eq(PtmDescMotivoMovimentos.Fields.JUSTIFICATIVA_OBRIG.toString(), obrigatorio));
		}
		
		if(StringUtils.isNotBlank(descricao)){
			criteria.add(Restrictions.ilike(PtmDescMotivoMovimentos.Fields.DESCRICAO .toString(), descricao, MatchMode.ANYWHERE));
		}

		return criteria;
	}

}
