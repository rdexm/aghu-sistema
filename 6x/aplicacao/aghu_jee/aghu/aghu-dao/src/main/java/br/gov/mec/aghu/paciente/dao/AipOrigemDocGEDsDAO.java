package br.gov.mec.aghu.paciente.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioOrigemDocsDigitalizados;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AipOrigemDocGEDs;

public class AipOrigemDocGEDsDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AipOrigemDocGEDs> {

	private static final long serialVersionUID = -6906044358462258305L;

	public List<AipOrigemDocGEDs> pesquisar(AipOrigemDocGEDs origemFiltro, Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc) {
		DetachedCriteria criteria = createCriteria(origemFiltro.getSeq(), origemFiltro.getOrigem(), origemFiltro.getReferencia(),
				origemFiltro.getIndSituacao());

		criteria.addOrder(Order.asc(AipOrigemDocGEDs.Fields.ORIGEM.toString()));
		criteria.addOrder(Order.asc(AipOrigemDocGEDs.Fields.SEQ.toString()));
		return this.executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}

	private DetachedCriteria createCriteria(Integer seq, DominioOrigemDocsDigitalizados origem, String referencia, DominioSituacao indSituacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipOrigemDocGEDs.class);

		if (seq != null) {
			criteria.add(Restrictions.eq(AipOrigemDocGEDs.Fields.SEQ.toString(), seq));
		}
		if (origem != null) {
			criteria.add(Restrictions.eq(AipOrigemDocGEDs.Fields.ORIGEM.toString(), origem));
		}
		if (StringUtils.isNotBlank(referencia)) {
			criteria.add(Restrictions.like(AipOrigemDocGEDs.Fields.REFERENCIA.toString(), referencia));
		}
		if (indSituacao != null) {
			criteria.add(Restrictions.eq(AipOrigemDocGEDs.Fields.IND_SITUACAO.toString(), indSituacao));
		}
		return criteria;
	}

	public Long pesquisarCount(AipOrigemDocGEDs origemFiltro) {
		DetachedCriteria criteria = createCriteria(origemFiltro.getSeq(), origemFiltro.getOrigem(), origemFiltro.getReferencia(),
				origemFiltro.getIndSituacao());
		return this.executeCriteriaCount(criteria);
	}

}
