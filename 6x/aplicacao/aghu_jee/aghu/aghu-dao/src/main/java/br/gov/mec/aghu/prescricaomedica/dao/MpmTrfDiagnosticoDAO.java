package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MpmTrfDiagnostico;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * 
 * @author heliz
 *
 */
public class MpmTrfDiagnosticoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmTrfDiagnostico> {
    
	
	private static final long serialVersionUID = -3028735907634883730L;

	/**
	 * Busca MpmObtOutraCausa do sumário.<br>
	 * 
	 * @param asuAtdSeq
	 * @param asuApaSeq
	 * @param asuSeqp
	 * @return
	 */
	public List<MpmTrfDiagnostico> obterTransferenciaDiagPrincipais(Integer asuAtdSeq, Integer asuApaSeq, Short asuSeqp) throws ApplicationBusinessException {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmTrfDiagnostico.class);
		
		criteria.add(Restrictions.eq(MpmTrfDiagnostico.Fields.ASU_APA_ATD_SEQ.toString(), asuAtdSeq));
		criteria.add(Restrictions.eq(MpmTrfDiagnostico.Fields.ASU_APA_SEQ.toString(), asuApaSeq));
		criteria.add(Restrictions.eq(MpmTrfDiagnostico.Fields.ASU_SEQP.toString(), asuSeqp));
		criteria.add(Restrictions.eq(MpmTrfDiagnostico.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
			
		return executeCriteria(criteria);
	}
		
}
