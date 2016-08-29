package br.gov.mec.aghu.procedimentoterapeutico.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MptProfCredAssinatLaudo;

public class MptProfCredAssinatLaudoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MptProfCredAssinatLaudo> {

	private static final long serialVersionUID = -6892966453260010391L;

	public Long listarProfCredAssinatLaudoCount(Integer matricula, Integer vinculo, Integer esp, Short convenio) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MptProfCredAssinatLaudo.class);

		if (matricula != null) {
			criteria.add(Restrictions.eq(MptProfCredAssinatLaudo.Fields.MATRICULA.toString(), matricula));
		}

		if (vinculo != null) {
			criteria.add(Restrictions.eq(MptProfCredAssinatLaudo.Fields.VINCULO.toString(), vinculo.shortValue()));
		}

		if (esp != null) {
			criteria.add(Restrictions.eq(MptProfCredAssinatLaudo.Fields.ESP_SEQ.toString(), esp.shortValue()));
		}

		if (convenio != null) {
			criteria.add(Restrictions.eq(MptProfCredAssinatLaudo.Fields.CNV_SEQ.toString(), convenio));
		}

		return executeCriteriaCount(criteria);
	}

}
