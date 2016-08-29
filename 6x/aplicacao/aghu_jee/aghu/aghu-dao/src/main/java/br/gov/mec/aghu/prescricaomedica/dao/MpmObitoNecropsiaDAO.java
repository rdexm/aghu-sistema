package br.gov.mec.aghu.prescricaomedica.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MpmObitoNecropsia;
import br.gov.mec.aghu.model.MpmObitoNecropsiaId;

/**
 * 
 * @author lalegre
 *
 */
public class MpmObitoNecropsiaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmObitoNecropsia> {

	private static final long serialVersionUID = -8554163131470483114L;

	@Override
	protected void obterValorSequencialId(MpmObitoNecropsia elemento) {
		
		if (elemento.getAltaSumario() == null) {
			
			throw new IllegalArgumentException("MpmObitoNecropsia nao esta associado corretamente a MpmAltaSumario.");
		
		}
		
		MpmObitoNecropsiaId id = new MpmObitoNecropsiaId();
		id.setAsuApaAtdSeq(elemento.getAltaSumario().getId().getApaAtdSeq());
		id.setAsuApaSeq(elemento.getAltaSumario().getId().getApaSeq());
		id.setAsuSeqp(elemento.getAltaSumario().getId().getSeqp());
		
		elemento.setId(id);
		
	}
	
	/**
	 * Retorna MpmObitoNecropsia
	 * @param altanAtdSeq
	 * @param altanApaSeq
	 * @param altanAsuSeqp
	 * @return
	 */
	public MpmObitoNecropsia obterMpmObitoNecropsia(Integer altanAtdSeq, Integer altanApaSeq, Short altanAsuSeqp) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmObitoNecropsia.class);
		criteria.add(Restrictions.eq(MpmObitoNecropsia.Fields.ASU_APA_ATD_SEQ.toString(), altanAtdSeq));
		criteria.add(Restrictions.eq(MpmObitoNecropsia.Fields.ASU_APA_SEQ.toString(), altanApaSeq));
		criteria.add(Restrictions.eq(MpmObitoNecropsia.Fields.ASU_SEQP.toString(), altanAsuSeqp));
		return (MpmObitoNecropsia) executeCriteriaUniqueResult(criteria);
		
	}

}
