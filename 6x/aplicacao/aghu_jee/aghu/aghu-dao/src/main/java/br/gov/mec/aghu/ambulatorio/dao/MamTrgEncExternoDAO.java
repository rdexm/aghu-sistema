package br.gov.mec.aghu.ambulatorio.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MamTrgEncExternos;

public class MamTrgEncExternoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MamTrgEncExternos> {

	private static final long serialVersionUID = 230497968640860097L;

	public MamTrgEncExternos pesquisarEncaminhamentoExternoPorSeqTriagem(Long trgSeq) {
		DetachedCriteria dc = DetachedCriteria.forClass(getClazz(), "TEE");
		
		dc.add(Restrictions.eq("TEE.".concat(MamTrgEncExternos.Fields.TRG_SEQ.toString()), trgSeq));
		dc.add(Restrictions.isNull("TEE.".concat(MamTrgEncExternos.Fields.DTHR_ESTORNO.toString())));
		
		Object obj = executeCriteriaUniqueResult(dc);
		
		if (obj != null) {
			return (MamTrgEncExternos) obj;
		}
		return null;
	}
	
	/**
	 * C8
	 * 
	 * @param seqTriagem
	 * @return
	 */
	public Short obterMaxSeqPTriagem(Long seqTriagem) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(MamTrgEncExternos.class, "MamTriagemEncExterno");

		criteria.add(Restrictions.eq("MamTriagemEncExterno."+ MamTrgEncExternos.Fields.TRG_SEQ.toString(), seqTriagem));

		criteria.setProjection(Projections.max("MamTriagemEncExterno."+ MamTrgEncExternos.Fields.SEQP.toString()));

		Short maxSeqP = (Short) this.executeCriteriaUniqueResult(criteria);
		if (maxSeqP != null) {
			return Short.valueOf(String.valueOf(maxSeqP + 1));
		}
		return 1;
	}
	
}
