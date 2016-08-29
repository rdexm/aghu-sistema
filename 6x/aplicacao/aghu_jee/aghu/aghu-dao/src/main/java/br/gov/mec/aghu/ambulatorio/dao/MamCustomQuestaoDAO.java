package br.gov.mec.aghu.ambulatorio.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MamCustomQuestao;

public class MamCustomQuestaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MamCustomQuestao>{

	private static final long serialVersionUID = -4658286496011971987L;

	public List<MamCustomQuestao> pesquisarCustomQuestao(Integer qusQutSeq, Short qusSeqp){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MamCustomQuestao.class,"MAM");
		
		criteria.add(Restrictions.eq("MAM." + MamCustomQuestao.Fields.QUS_QUT_SEQ.toString(), qusQutSeq));
		criteria.add(Restrictions.eq("MAM." + MamCustomQuestao.Fields.QUS_SEQP.toString(), qusSeqp));
		
		return executeCriteria(criteria);
	}
	
	/**
	 * #50937
	 * Cursor: cur_cqu
	 * @param qusQutSeq
	 * @param qusSeqp
	 * @return
	 */
	public Boolean temCustomizacaoQuestao(Integer qusQutSeq, Short qusSeqp){

		List<MamCustomQuestao> mamCustomQuestaoList = this.pesquisarCustomQuestao(qusQutSeq, qusSeqp);
	
		if(mamCustomQuestaoList != null && !mamCustomQuestaoList.isEmpty()){
			return Boolean.TRUE;
		}
		return Boolean.FALSE;		
	}
	
}
