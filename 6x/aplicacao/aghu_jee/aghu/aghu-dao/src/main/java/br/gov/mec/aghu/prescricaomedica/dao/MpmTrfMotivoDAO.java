package br.gov.mec.aghu.prescricaomedica.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MpmTrfMotivo;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * 
 * @author heliz
 *
 */
public class MpmTrfMotivoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmTrfMotivo> {
    
	
	private static final long serialVersionUID = -5483474416717646718L;

	public String obterMotivoTransferencia(Integer altanAtdSeq, Integer altanApaSeq, Short altanAsuSeqp) throws ApplicationBusinessException {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmTrfMotivo.class);
		criteria.setProjection(Projections.property(MpmTrfMotivo.Fields.DESCRICAO.toString()));
		criteria.add(Restrictions.eq(MpmTrfMotivo.Fields.ASU_APA_ATD_SEQ.toString(), altanAtdSeq));
		criteria.add(Restrictions.eq(MpmTrfMotivo.Fields.ASU_APA_SEQ.toString(), altanApaSeq));
		criteria.add(Restrictions.eq(MpmTrfMotivo.Fields.ASU_SEQP.toString(), altanAsuSeqp));
		
		String motivoTransferencia = (String) this.executeCriteriaUniqueResult(criteria);

		return motivoTransferencia;
	}
		
}
