package br.gov.mec.aghu.compras.contaspagar.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.FcpRetencaoAliquota;
import br.gov.mec.aghu.model.FcpTitulo;
import br.gov.mec.aghu.model.FcpValorTributos;

public class FcpRetencaoAliquotaDAO extends
		br.gov.mec.aghu.core.persistence.dao.BaseDao<FcpTitulo> {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2762548255929895210L;

	public Double obterValorTributosPorNotaRecebimento(Integer nrsSeq, String[] imposto) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FcpRetencaoAliquota.class, "fra");
		criteria.createAlias("fra." + FcpRetencaoAliquota.Fields.FCP_VALOR_TRIBUTOS.toString(), "fvt");
		
		criteria.add(Restrictions.not(Restrictions.in("fra." + FcpRetencaoAliquota.Fields.IMPOSTO.toString(), imposto)));
		criteria.setProjection(Projections.sum("fvt." + FcpValorTributos.Fields.VALOR.toString()));
		
		if (nrsSeq != null) {
			criteria.add(Restrictions.eq("fvt." + FcpValorTributos.Fields.INS_NRS_SEQ.toString(), nrsSeq));
		}
		
		return (Double) executeCriteriaUniqueResult(criteria);
	}
	
	public Double obterTotalTributosPorNrsSeqEImposto(Integer nrsSeq, String[] imposto) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FcpRetencaoAliquota.class, "fra");
		criteria.createAlias("fra." + FcpRetencaoAliquota.Fields.FCP_VALOR_TRIBUTOS.toString(), "fvt");
		criteria.add(Restrictions.not(Restrictions.in("fra." + FcpRetencaoAliquota.Fields.IMPOSTO.toString(), imposto)));
		criteria.setProjection(Projections.sum("fvt." + FcpValorTributos.Fields.VALOR.toString()));
		if (nrsSeq != null) {
			criteria.add(Restrictions.eq("fvt." + FcpValorTributos.Fields.INS_NRS_SEQ.toString(), nrsSeq));
		}
		return (Double) executeCriteriaUniqueResult(criteria);
	}
	
	public Double obterValorTributosPorISSQN(Integer nrsSeq, String[] imposto) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FcpRetencaoAliquota.class, "fra");
		criteria.createAlias("fra." + FcpRetencaoAliquota.Fields.FCP_VALOR_TRIBUTOS.toString(), "fvt");
		
		criteria.add(Restrictions.in("fra." + FcpRetencaoAliquota.Fields.IMPOSTO.toString(), imposto));
		criteria.setProjection(Projections.sum("fvt." + FcpValorTributos.Fields.VALOR.toString()));
		
		if (nrsSeq != null) {
			criteria.add(Restrictions.eq("fvt." + FcpValorTributos.Fields.INS_NRS_SEQ.toString(), nrsSeq));
		}
		
		return (Double) executeCriteriaUniqueResult(criteria);
	}
}
