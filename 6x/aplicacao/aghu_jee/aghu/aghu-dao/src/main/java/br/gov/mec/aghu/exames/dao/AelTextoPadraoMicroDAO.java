package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AelTextoPadraoMicro;

public class AelTextoPadraoMicroDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelTextoPadraoMicro>  {
	
	private static final long serialVersionUID = -3804009440288457022L;

	public List<AelTextoPadraoMicro> pesquisarTextoPadraoMicroPorAelGrpTxtPadraoMicro(final Short seqAelGrpTxtPadraoMicro) {
		
		final DetachedCriteria criteria =  DetachedCriteria.forClass(AelTextoPadraoMicro.class);
		
		if (seqAelGrpTxtPadraoMicro != null) {
			criteria.add(Restrictions.eq(AelTextoPadraoMicro.Fields.AEL_GRP_TXT_PADRAO_MICROS_SEQ.toString(), seqAelGrpTxtPadraoMicro));
		} 
		
		criteria.addOrder(Order.asc(AelTextoPadraoMicro.Fields.DESCRICAO.toString()));
		
		return executeCriteria(criteria);
	}	
	
	public Short obterProximaSequence(Short luuSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelTextoPadraoMicro.class);
		
		criteria.add(Restrictions.eq(AelTextoPadraoMicro.Fields.LUU_SEQ.toString(), luuSeq));
		criteria.setProjection(Projections.max(AelTextoPadraoMicro.Fields.SEQP.toString()));
		Short seq = (Short) executeCriteriaUniqueResult(criteria);
		
		if (seq == null) {
			return 1;
		}
		return ++seq;
	}
	
}
