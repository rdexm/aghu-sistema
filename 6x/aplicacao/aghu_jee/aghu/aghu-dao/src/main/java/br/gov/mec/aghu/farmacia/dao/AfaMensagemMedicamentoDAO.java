package br.gov.mec.aghu.farmacia.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AfaMensagemMedicamento;

public class AfaMensagemMedicamentoDAO extends
		br.gov.mec.aghu.core.persistence.dao.BaseDao<AfaMensagemMedicamento> {

	private static final long serialVersionUID = -2312048444890186653L;

	public Long pesquisaMensagemMedicamentoCount(Integer filtroSeq,
			String filtroDescricao, Boolean filtroCoexistente,
			DominioSituacao filtroSituacao) {
		return executeCriteriaCount(createCriteriaPesquisaMensagemMedicamento(
				filtroSeq, filtroDescricao, filtroCoexistente, filtroSituacao));
	}

	public List<AfaMensagemMedicamento> pesquisaMensagemMedicamento(
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, Integer filtroSeq, String filtroDescricao,
			Boolean filtroCoexistente, DominioSituacao filtroSituacao) {
		DetachedCriteria criteria = createCriteriaPesquisaMensagemMedicamento(
				filtroSeq, filtroDescricao, filtroCoexistente, filtroSituacao);
		
		return executeCriteria(criteria, firstResult, maxResults, null, false);
	}

	private DetachedCriteria createCriteriaPesquisaMensagemMedicamento(
			Integer filtroSeq, String filtroDescricao, Boolean filtroCoexistente,
			DominioSituacao filtroSituacao) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AfaMensagemMedicamento.class);

		if (filtroSeq != null) {
			criteria.add(Restrictions.eq(AfaMensagemMedicamento.Fields.SEQ
					.toString(), filtroSeq));
		}

		if (StringUtils.isNotBlank(filtroDescricao)) {
			criteria.add(Restrictions.ilike(
					AfaMensagemMedicamento.Fields.DESCRICAO.toString(),
					filtroDescricao, MatchMode.ANYWHERE));
		}

		if (filtroCoexistente != null) {
			criteria.add(Restrictions.eq(
					AfaMensagemMedicamento.Fields.COEXISTENTE.toString(),
					filtroCoexistente));
		}

		if (filtroSituacao != null) {
			criteria.add(Restrictions.eq(AfaMensagemMedicamento.Fields.SITUACAO
					.toString(), filtroSituacao));
		}

		return criteria;
	}

}
