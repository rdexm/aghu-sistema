package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AelItemSolicExameHist;
import br.gov.mec.aghu.model.AelSismamaHistoResHist;

public class AelSismamaHistoResHistDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelSismamaHistoResHist> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4986208666608043479L;

	public Long obterRespostasSismamaHistoCountPorSoeSeqSeqpHist(Integer soeSeq, Short seqp) {
		String aliasIse = "ise";
		String separador = ".";
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelSismamaHistoResHist.class);
		criteria.createAlias(AelSismamaHistoResHist.Fields.ITEM_SOLICITACAO_EXAME.toString(), aliasIse);
		criteria.createAlias(AelSismamaHistoResHist.Fields.SISMAMA_HISTO_CAD.toString(), "shc");
		criteria.add(Restrictions.eq(aliasIse + separador + AelItemSolicExameHist.Fields.SOE_SEQ.toString(), soeSeq));
		criteria.add(Restrictions.eq(aliasIse + separador + AelItemSolicExameHist.Fields.SEQP.toString(), seqp));

		return executeCriteriaCount(criteria);
	}

	public List<AelSismamaHistoResHist> pesquisarSismamaHistoResPorSoeSeqSeqp(Integer soeSeq, Short seqp) {
		String aliasIse = "ise";
		String aliasShc = "shc";
		String separador = ".";
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelSismamaHistoResHist.class);
		criteria.createAlias(AelSismamaHistoResHist.Fields.ITEM_SOLICITACAO_EXAME.toString(), aliasIse);
		criteria.createAlias(AelSismamaHistoResHist.Fields.SISMAMA_HISTO_CAD.toString(), aliasShc);
		criteria.add(Restrictions.isNotNull(AelSismamaHistoResHist.Fields.RESPOSTA.toString()));
		criteria.add(Restrictions.eq(aliasIse + separador + AelItemSolicExameHist.Fields.SOE_SEQ.toString(), soeSeq));
		criteria.add(Restrictions.eq(aliasIse + separador + AelItemSolicExameHist.Fields.SEQP.toString(), seqp));
		return executeCriteria(criteria);
	}
	
	
	

}
