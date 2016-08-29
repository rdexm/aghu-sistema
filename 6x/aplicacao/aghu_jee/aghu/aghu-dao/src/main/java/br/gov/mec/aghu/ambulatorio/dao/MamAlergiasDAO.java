package br.gov.mec.aghu.ambulatorio.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MamAlergias;

public class MamAlergiasDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MamAlergias> {

	private static final long serialVersionUID = -4360881001462849420L;

	public List<MamAlergias> pesquisarMamAlergiasPorPaciente(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamAlergias.class);
		
		criteria.add(Restrictions.eq(MamAlergias.Fields.PAC_CODIGO.toString(), pacCodigo));

		return executeCriteria(criteria);
	}

	public List<MamAlergias> pesquisarMamAlergiasPorSeqRegistro(Long rgtSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamAlergias.class);
		
		criteria.add(Restrictions.eq(MamAlergias.Fields.RGT_SEQ.toString(), rgtSeq));

		return executeCriteria(criteria);
	}
	
	/**
	 * #42360
	 * @param numeroConsulta
	 * @return
	 */
	
	public MamAlergias buscarAlergiasPorNumeroConsulta(Integer numeroConsulta) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamAlergias.class);

		criteria.add(Restrictions.eq(MamAlergias.Fields.CON_NUMERO.toString(), numeroConsulta));
		List<MamAlergias> list = executeCriteria(criteria);
		if (list != null && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}

}
