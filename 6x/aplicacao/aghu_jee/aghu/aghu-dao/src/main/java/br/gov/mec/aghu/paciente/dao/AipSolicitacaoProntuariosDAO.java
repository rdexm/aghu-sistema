package br.gov.mec.aghu.paciente.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.FetchMode;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AipSolicitacaoProntuarios;

public class AipSolicitacaoProntuariosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AipSolicitacaoProntuarios> {
	
	private static final long serialVersionUID = 5424427327117665049L;

	public AipSolicitacaoProntuarios obterSolicitacaoProntuarioAnterior(Integer codigo) {
		DetachedCriteria cri = DetachedCriteria.forClass(AipSolicitacaoProntuarios.class);
		cri.add(Restrictions.idEq(codigo));

		return (AipSolicitacaoProntuarios) executeCriteriaUniqueResult(cri);
	}

	public List<AipSolicitacaoProntuarios> pesquisarSolicitacaoProntuario(Integer firstResult,
			Integer maxResults, String solicitante, String responsavel, String observacao) {
		DetachedCriteria criteria = createPesquisarSolicitacaoProntuarioCriteria(solicitante,
				responsavel, observacao);

		return executeCriteria(criteria, firstResult, maxResults, null, false);
	}

	public Long pesquisarSolicitacaoProntuarioCount(String solicitante, String responsavel,
			String observacao) {
		return executeCriteriaCount(createPesquisarSolicitacaoProntuarioCriteria(solicitante,
				responsavel, observacao));
	}

	private DetachedCriteria createPesquisarSolicitacaoProntuarioCriteria(String solicitante,
			String responsavel, String observacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipSolicitacaoProntuarios.class);

		criteria.setFetchMode(AipSolicitacaoProntuarios.Fields.ESPECIALIDADE.toString(), FetchMode.JOIN);
		criteria.setFetchMode(AipSolicitacaoProntuarios.Fields.PACIENTES.toString(), FetchMode.SELECT);
		
		if (StringUtils.isNotBlank(solicitante)) {
			criteria.add(Restrictions.ilike(
					AipSolicitacaoProntuarios.Fields.SOLICITANTE.toString(), solicitante,
					MatchMode.ANYWHERE));
		}

		if (StringUtils.isNotBlank(responsavel)) {
			criteria.add(Restrictions.ilike(
					AipSolicitacaoProntuarios.Fields.RESPONSAVEL.toString(), responsavel,
					MatchMode.ANYWHERE));
		}

		if (StringUtils.isNotBlank(observacao)) {
			criteria.add(Restrictions.ilike(AipSolicitacaoProntuarios.Fields.OBSERVACAO.toString(),
					observacao, MatchMode.ANYWHERE));
		}

		return criteria;
	}

	public AipSolicitacaoProntuarios obterSolicitacaoProntuario(Integer codigo) {
		DetachedCriteria cri = DetachedCriteria.forClass(AipSolicitacaoProntuarios.class);
		cri.add(Restrictions.idEq(codigo));

		return (AipSolicitacaoProntuarios) executeCriteriaUniqueResult(cri);
	}
}
