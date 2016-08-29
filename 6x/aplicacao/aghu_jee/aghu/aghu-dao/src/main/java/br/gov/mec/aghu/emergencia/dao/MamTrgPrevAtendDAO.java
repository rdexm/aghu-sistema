package br.gov.mec.aghu.emergencia.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MamTrgPrevAtend;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;


public class MamTrgPrevAtendDAO extends BaseDao<MamTrgPrevAtend> {
	
	private static final long serialVersionUID = 972902462491037809L;

	/***
	 * C16 Obtém a data/hora prevista de atendimento SELECT tpv.dthr_prev_atend
	 * FROM mam_trg_prev_atends tpv WHERE tpv.trg_seq = <SEQ do acolhimento>;
	 */
    public MamTrgPrevAtend obterDataPrevistaAtend(Long seqTriagem){
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(MamTrgPrevAtend.class, "MamTrgPrevAtend");
		criteria.add(Restrictions.eq("MamTrgPrevAtend." + MamTrgPrevAtend.Fields.TRG_SEQ.toString(), seqTriagem));
				
		return (MamTrgPrevAtend) this.executeCriteriaUniqueResult(criteria);
	}

}