package br.gov.mec.aghu.blococirurgico.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MbcImagemDescricoes;

public class MbcImagemDescricoesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcImagemDescricoes> {
	
	private static final long serialVersionUID = 4560857059258442051L;

	/**
	 * Efetua busca de MbcImagemDescricoes
	 * Consulta C11 #18527
	 * @param fdcDcgCrgSeq
	 * @param fdcDcgSeqp
	 * @param fdcSeqp
	 * @return
	 */
	public MbcImagemDescricoes buscarImagemDescricoes(Integer fdcDcgCrgSeq, Short fdcDcgSeqp, Integer fdcSeqp){
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcImagemDescricoes.class, "imc");
		criteria.add(Restrictions.eq("imc."+MbcImagemDescricoes.Fields.FDC_DCG_CRG_SEQ.toString(), fdcDcgCrgSeq));
		criteria.add(Restrictions.eq("imc."+MbcImagemDescricoes.Fields.FDC_DCG_SEQP.toString(), fdcDcgSeqp));
		criteria.add(Restrictions.eq("imc."+MbcImagemDescricoes.Fields.FDC_SEQP.toString(), fdcSeqp));
		
		return (MbcImagemDescricoes) executeCriteriaUniqueResult(criteria);
	}
}
