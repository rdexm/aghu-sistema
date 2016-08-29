package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AelAmostraItemExamesHist;

public class AelAmostraItemExamesHistDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelAmostraItemExamesHist> {
	
	private static final long serialVersionUID = -7056114584493244964L;
	/*private static final String AIE = "AIE." ;
	private static final String ISE = "ISE." ;
	private static final String MOC = "MOC." ;
	private static final String UNF = "UNF." ;*/
	
	public List<AelAmostraItemExamesHist> buscarItensAmostraExame(Integer iseSoeSeq, Short iseSeqp,Integer amoSeqp){
		DetachedCriteria criteria = DetachedCriteria.forClass(AelAmostraItemExamesHist.class);
		
		criteria.add(Restrictions.eq(AelAmostraItemExamesHist.Fields.SOE_SEQ.toString(), iseSoeSeq));
		//criteria.add(Restrictions.eq(AelAmostraItemExames.Fields.SEQP.toString(), iseSeqp));
		criteria.add(Restrictions.eq(AelAmostraItemExamesHist.Fields.AMO_SEQP.toString(), amoSeqp));
		
		return executeCriteria(criteria);
	}

	
}
