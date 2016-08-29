package br.gov.mec.aghu.sig.dao;

import org.hibernate.FetchMode;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.SigAtividadeCentroCustos;
import br.gov.mec.aghu.model.SigAtividades;

public class SigAtividadeCentroCustosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SigAtividadeCentroCustos> {

	private static final long serialVersionUID = -879234892345926L;

	public SigAtividadeCentroCustos obterCentroCustoPorAtividade(Integer seqAtividade) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SigAtividadeCentroCustos.class);
		criteria.setFetchMode(SigAtividadeCentroCustos.Fields.CENTRO_CUSTO.toString(), FetchMode.JOIN);
		criteria.add(Restrictions.eq(SigAtividadeCentroCustos.Fields.ATIVIDADE+"."+SigAtividades.Fields.SEQ, seqAtividade));
		return (SigAtividadeCentroCustos) this.executeCriteriaUniqueResult(criteria);	
	}
}
