package br.gov.mec.aghu.paciente.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioOrigemDocsDigitalizados;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AipOrigemDocGEDsJn;

public class AipOrigemDocGEDsJnDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AipOrigemDocGEDsJn> {

	private static final long serialVersionUID = -6906044358462258305L;

	public List<AipOrigemDocGEDsJn> pesquisarAipOrigemDocGEDsJn(Integer seq, DominioOrigemDocsDigitalizados origem, String referencia, DominioSituacao indSituacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipOrigemDocGEDsJn.class);

		if (seq != null) {
			criteria.add(Restrictions.eq(AipOrigemDocGEDsJn.Fields.SEQ.toString(), seq));
		}
		if (origem != null) {
			criteria.add(Restrictions.like(AipOrigemDocGEDsJn.Fields.ORIGEM.toString(), origem));
		}
		if (StringUtils.isNotBlank(referencia)) {
			criteria.add(Restrictions.like(AipOrigemDocGEDsJn.Fields.REFERENCIA.toString(), referencia));
		}
		if (indSituacao != null) {
			criteria.add(Restrictions.like(AipOrigemDocGEDsJn.Fields.IND_SITUACAO.toString(), indSituacao));
		}

		return executeCriteria(criteria);
	}

}
