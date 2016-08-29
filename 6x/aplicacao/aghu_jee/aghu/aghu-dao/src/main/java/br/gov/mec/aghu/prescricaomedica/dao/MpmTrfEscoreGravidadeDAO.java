package br.gov.mec.aghu.prescricaomedica.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MpmTrfEscoreGravidade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * 
 * @author heliz
 *
 */
public class MpmTrfEscoreGravidadeDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmTrfEscoreGravidade> {
    
	
	private static final long serialVersionUID = 187477391949770853L;

	public String obterTransferenciaDiagSecundario(Integer altanAtdSeq, Integer altanApaSeq, Short altanAsuSeqp) throws ApplicationBusinessException {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmTrfEscoreGravidade.class);
		criteria.setProjection(Projections.property(MpmTrfEscoreGravidade.Fields.DESCRICAO.toString()));
		criteria.add(Restrictions.eq(MpmTrfEscoreGravidade.Fields.ASU_APA_ATD_SEQ.toString(), altanAtdSeq));
		criteria.add(Restrictions.eq(MpmTrfEscoreGravidade.Fields.ASU_APA_SEQ.toString(), altanApaSeq));
		criteria.add(Restrictions.eq(MpmTrfEscoreGravidade.Fields.ASU_SEQP.toString(), altanAsuSeqp));
		
		String diagnosticoSecundario = (String) this.executeCriteriaUniqueResult(criteria);

		return diagnosticoSecundario;
	}
		
}
