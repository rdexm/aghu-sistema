package br.gov.mec.aghu.exames.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AelProjetoIntercorrenciaInternacao;

public class AelProjetoIntercorrenciaInternacaoDAO extends
		br.gov.mec.aghu.core.persistence.dao.BaseDao<AelProjetoIntercorrenciaInternacao> {

	
	
	private static final long serialVersionUID = -7387002912052299419L;

	public AelProjetoIntercorrenciaInternacao obterProjetoIntercorrenciaInternacao(
			Integer codigoPaciente, Integer seqProjetoPesquisa, Short seq) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(AelProjetoIntercorrenciaInternacao.class);
		criteria.add(Restrictions.eq(
				AelProjetoIntercorrenciaInternacao.Fields.ID_PPJPACCODIGO
						.toString(), codigoPaciente));
		criteria.add(Restrictions.eq(
				AelProjetoIntercorrenciaInternacao.Fields.ID_PPJPJQSEQ
						.toString(), seqProjetoPesquisa));

		if (seq != null) {
			criteria.add(Restrictions.eq(
					AelProjetoIntercorrenciaInternacao.Fields.ID_SEQP
							.toString(), seq));
		}

		return (AelProjetoIntercorrenciaInternacao) executeCriteriaUniqueResult(criteria);
	}

}
