package br.gov.mec.aghu.ambulatorio.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MamExtratoRegistro;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class MamExtratoRegistroDAO extends BaseDao<MamExtratoRegistro> {

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7810581983323375927L;

	/**
	 * #44179 - F03 - CURSOR cur_exr
	 * @ORADB - MAMP_EMG_ATLZ_REG
	 * @author marcelo.deus
	 */
	public Long obterValorMaximoExtratoRegistro(Long cRgtSeq){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MamExtratoRegistro.class, "MER");
		
		criteria.add(Restrictions.eq("MER." + MamExtratoRegistro.Fields.ID_RGT_SEQ.toString(), cRgtSeq));
		
		criteria.setProjection(Projections.max("MER." + MamExtratoRegistro.Fields.ID_SEQP.toString()));
		
		Long result = (Long) executeCriteriaUniqueResult(criteria);
		if (result == null) {
			result = (long) 0;
		}
		result++;

		return result;
	}
}
