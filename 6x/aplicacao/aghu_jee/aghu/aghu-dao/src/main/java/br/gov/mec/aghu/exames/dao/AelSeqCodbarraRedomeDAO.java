package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AelAmostras;
import br.gov.mec.aghu.model.AelSeqCodbarraRedome;

public class AelSeqCodbarraRedomeDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelSeqCodbarraRedome> {

	private static final long serialVersionUID = 8892321923642284490L;

	public List<AelSeqCodbarraRedome> obterSeqCodBarraRedome(final AelAmostras amostra) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelSeqCodbarraRedome.class);
		criteria.add(Restrictions.eq(AelSeqCodbarraRedome.Fields.AMOSTRA.toString(), amostra));
		return executeCriteria(criteria);
	}
}
