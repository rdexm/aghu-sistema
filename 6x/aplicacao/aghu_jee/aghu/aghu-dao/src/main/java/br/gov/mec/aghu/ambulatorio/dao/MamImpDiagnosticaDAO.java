package br.gov.mec.aghu.ambulatorio.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MamImpDiagnostica;

public class MamImpDiagnosticaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MamImpDiagnostica> {

	private static final long serialVersionUID = 5446170910509257228L;

	public List<MamImpDiagnostica> pesquisarMamImpDiagnosticaPorSeqRegistro(Long rgtSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamImpDiagnostica.class);
		criteria.add(Restrictions.eq(MamImpDiagnostica.Fields.RGT_SEQ.toString(), rgtSeq));

		return executeCriteria(criteria);
	}
	
	/**
	 * #42360
	 * @param numeroConsulta
	 * @return
	 */
	
	public MamImpDiagnostica buscarMamImpDiagnosticaPorNumeroConsulta(Integer numeroConsulta) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamImpDiagnostica.class);

		criteria.add(Restrictions.eq(MamImpDiagnostica.Fields.CON_NUMERO.toString(), numeroConsulta));
		List<MamImpDiagnostica> list = executeCriteria(criteria);
		if (list != null && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}

}
