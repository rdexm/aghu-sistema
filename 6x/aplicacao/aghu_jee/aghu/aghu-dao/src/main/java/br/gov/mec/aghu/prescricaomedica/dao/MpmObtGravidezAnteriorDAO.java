package br.gov.mec.aghu.prescricaomedica.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MpmObtGravidezAnterior;
import br.gov.mec.aghu.model.MpmObtGravidezAnteriorId;

/**
 * 
 * @author lalegre
 *
 */
public class MpmObtGravidezAnteriorDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmObtGravidezAnterior> {

	private static final long serialVersionUID = 2387531762562444859L;

	@Override
	protected void obterValorSequencialId(MpmObtGravidezAnterior elemento) {
		
		if (elemento.getMpmAltaSumarios() == null) {
			
			
			throw new IllegalArgumentException("MpmObtGravidezAnterior nao esta associado corretamente a MpmAltaSumario.");
		
		}
		
		MpmObtGravidezAnteriorId id = new MpmObtGravidezAnteriorId();
		id.setAsuApaAtdSeq(elemento.getMpmAltaSumarios().getId().getApaAtdSeq());
		id.setAsuApaSeq(elemento.getMpmAltaSumarios().getId().getApaSeq());
		id.setAsuSeqp(elemento.getMpmAltaSumarios().getId().getSeqp());
		
		elemento.setId(id);
		
	}
	
	/**
	 * Retorna MpmObtGravidezAnterior
	 * @param altanAtdSeq
	 * @param altanApaSeq
	 * @param altanAsuSeqp
	 * @return
	 */
	public MpmObtGravidezAnterior obterMpmObtGravidezAnterior(Integer altanAtdSeq, Integer altanApaSeq, Short altanAsuSeqp) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmObtGravidezAnterior.class);
		criteria.add(Restrictions.eq(MpmObtGravidezAnterior.Fields.ASU_APA_ATD_SEQ.toString(), altanAtdSeq));
		criteria.add(Restrictions.eq(MpmObtGravidezAnterior.Fields.ASU_APA_SEQ.toString(), altanApaSeq));
		criteria.add(Restrictions.eq(MpmObtGravidezAnterior.Fields.ASU_SEQP.toString(), altanAsuSeqp));
		return (MpmObtGravidezAnterior) executeCriteriaUniqueResult(criteria);
		
	}
		

}
