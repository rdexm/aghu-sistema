package br.gov.mec.aghu.blococirurgico.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.MbcAvalPreSedacao;
import br.gov.mec.aghu.model.MbcAvalPreSedacaoId;


public class MbcAvalPreSedacaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcAvalPreSedacao> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8198449972164736525L;


	public MbcAvalPreSedacao pesquisarMbcAvalPreSedacaoPorDdtSeq(MbcAvalPreSedacaoId id) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcAvalPreSedacao.class,"APS");
		criteria.createAlias(MbcAvalPreSedacao.Fields.RAP_SERVIDORES.toString(), "SER", JoinType.INNER_JOIN);
		criteria.createAlias(MbcAvalPreSedacao.Fields.VIAS_AEREAS.toString(), "VIA", JoinType.LEFT_OUTER_JOIN);
//		criteria.add(Restrictions.eq("APS."+MbcAvalPreSedacao.Fields.CRG_SEQ.toString(),id.getDcgCrgSeq() ));	
//		criteria.add(Restrictions.eq("APS."+MbcAvalPreSedacao.Fields.DGC_SEQP.toString(),id.getDcgSeqp() ));
		criteria.add(Restrictions.eq("APS."+MbcAvalPreSedacao.Fields.ID.toString(),id ));
		return (MbcAvalPreSedacao)executeCriteriaUniqueResult(criteria);
	}
	
}
