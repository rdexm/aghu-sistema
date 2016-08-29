package br.gov.mec.aghu.ambulatorio.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MamTrgAlergias;

public class MamTrgAlergiasDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MamTrgAlergias> {

	private static final long serialVersionUID = 8721436505708157873L;

	public List<MamTrgAlergias> listarTrgAlergiasPorTrgSeq(Long trgSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamTrgAlergias.class);
		criteria.add(Restrictions.eq(MamTrgAlergias.Fields.TRG_SEQ.toString(), trgSeq));
		
		return executeCriteria(criteria);
	}

	/**
	 * #42360
	 * @param numeroConsulta
	 * @return
	 */
	
	public MamTrgAlergias buscarTrgAlergiasPorPacCodigoSeq(Integer pacCodigo, Long trgSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamTrgAlergias.class);

		criteria.add(Restrictions.ne(MamTrgAlergias.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.eq(MamTrgAlergias.Fields.TRG_SEQ.toString(), trgSeq));
		List<MamTrgAlergias> list = executeCriteria(criteria);
		if (list != null && !list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}
	
	public Short obterMaxSeqPTrgAlergias(Long trgSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MamTrgAlergias.class, "MamTrgAlergias");
		criteria.add(Restrictions.eq("MamTrgAlergias." + MamTrgAlergias.Fields.TRG_SEQ.toString(), trgSeq));
		criteria.setProjection(Projections.max("MamTrgAlergias." + MamTrgAlergias.Fields.SEQP.toString()));
		Short maxSeqP = (Short) this.executeCriteriaUniqueResult(criteria);
		
		if (maxSeqP != null) {
			return Short.valueOf(String.valueOf(maxSeqP + 1));
		}
		return 1;
	}

}
